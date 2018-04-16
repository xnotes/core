/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss.jce;

import org.xnotes.core.security.ots.xnmss.XNMSS;
import org.xnotes.core.utils.MerkleTree;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyFactorySpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.Arrays;
import org.xnotes.core.security.hash.HashEngine;
import org.xnotes.core.utils.ASN1;
import org.xnotes.core.utils.DatatypeConverter;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSKeyFactory extends KeyFactorySpi {

	@Override
	protected PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
		if (!X509EncodedKeySpec.class.isInstance(keySpec)) {
			throw new InvalidKeySpecException("Unknown XNMSS Public KeySpec Class '" + keySpec.getClass().getName() + "'.");
		}
		X509EncodedKeySpec ks = (X509EncodedKeySpec) keySpec;
		if (!ks.getFormat().equals("X.509")) {
			throw new InvalidKeySpecException("Unknown XNMSS Public Key Format '" + ks.getFormat() + "'.");
		}
		try {
			byte[] keyBytes = ks.getEncoded();
			String algorithm = ASN1.detectKeyAlgorithmForKeyBytes(keyBytes);
			if (!algorithm.toUpperCase().startsWith("XNMSSWITHSHA")) {
				throw new InvalidKeySpecException("Invalid XNMSS Key Algorithm '" + algorithm + "'.");
			}
			ASN1Sequence asn1Encoded = (ASN1Sequence) ASN1Primitive.fromByteArray(keyBytes);
			ASN1Sequence asn1Key = (ASN1Sequence) ASN1Primitive.fromByteArray(((ASN1BitString) asn1Encoded.getObjectAt(1)).getBytes());
			Object[] params = _decodeKeyParams((ASN1Sequence) asn1Key.getObjectAt(0));
			return new XNMSSPublicKey(algorithm, (int) params[0], (int) params[1], ((ASN1OctetString) asn1Key.getObjectAt(1)).getOctets(), (XNMSSOTSPublicKey) params[2], (byte[]) params[3]);
		} catch (IOException | NoSuchAlgorithmException ex) {
			throw new InvalidKeySpecException(ex);
		} catch (InvalidKeySpecException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new InvalidKeySpecException("Public Key Parse Exception: " + ex.getMessage());
		}
	}

	@Override
	protected PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
		if (!PKCS8EncodedKeySpec.class.isInstance(keySpec)) {
			throw new InvalidKeySpecException("Unknown XNMSS Private KeySpec Class '" + keySpec.getClass().getName() + "'.");
		}
		PKCS8EncodedKeySpec ks = (PKCS8EncodedKeySpec) keySpec;
		if (!ks.getFormat().equals("PKCS#8")) {
			throw new InvalidKeySpecException("Unknown XNMSS Private Key Format '" + ks.getFormat() + "'.");
		}
		try {
			byte[] keyBytes = ks.getEncoded();
			ASN1Sequence asn1Encoded = (ASN1Sequence) ASN1Primitive.fromByteArray(keyBytes);
			int pkcs8Version = ((ASN1Integer) asn1Encoded.getObjectAt(0)).getValue().intValue();
			String algorithm = ASN1.detectKeyAlgorithmForKeyBytes(keyBytes);
			if (!algorithm.toUpperCase().startsWith("XNMSSWITHSHA")) {
				throw new InvalidKeySpecException("Invalid XNMSS Key Algorithm '" + algorithm + "'.");
			}
			ASN1Sequence asn1Key = (ASN1Sequence) ASN1Primitive.fromByteArray(((ASN1OctetString) asn1Encoded.getObjectAt(2)).getOctets());
			Object[] params = _decodeKeyParams((ASN1Sequence) asn1Key.getObjectAt(0));
			int height = (int) params[0];
			int digestIterations = (int) params[1];
			XNMSSOTSPublicKey parentOTSPublicKey = (XNMSSOTSPublicKey) params[2];
			byte[] parentSign = (byte[]) params[3];
			byte[] root = ((ASN1OctetString) asn1Key.getObjectAt(1)).getOctets();
			byte[] otsPrivateKeysBytes = ((ASN1OctetString) asn1Key.getObjectAt(2)).getOctets();
			Inflater unzip = new Inflater();
			unzip.setInput(otsPrivateKeysBytes);
			ByteArrayOutputStream os = new ByteArrayOutputStream(otsPrivateKeysBytes.length);
			byte[] buf = new byte[1024];
			while (!unzip.finished()) {
				os.write(buf, 0, unzip.inflate(buf));
			}
			os.close();
			ASN1Sequence asn1OTSPrivateKeys = (ASN1Sequence) ASN1Primitive.fromByteArray(os.toByteArray());
			int n = (int) Math.pow(2, height);
			if (asn1OTSPrivateKeys.size() != n) {
				throw new InvalidKeySpecException("Incorrect number of OTS Key Pairs for height " + height + ": expected " + n + " key pairs.");
			}
			String digestAlgorithm = XNMSS.getDigestAlgorithmForKeyAlgorithm(algorithm);
			String otsPrivateKeyAlgorithm = XNMSS.getOTSKeyAlgorithmForKeyAlgorithm(algorithm);
			XNMSSOTSPrivateKey[] otsPrivateKeys = new XNMSSOTSPrivateKey[n];
			byte[][] hashes = new byte[n][];
			HashEngine hashEngine = XNMSS.getHashEngineForDigestAlgorithm(digestAlgorithm);
			for (int i = 0; i < n; i++) {
				try {
					KeyFactory kf = KeyFactory.getInstance(otsPrivateKeyAlgorithm);
					otsPrivateKeys[i] = (XNMSSOTSPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(((ASN1OctetString) asn1OTSPrivateKeys.getObjectAt(i)).getOctets()));
					hashes[i] = hashEngine.hash(otsPrivateKeys[i].getSignaturePublicKey().getEncoded(), digestIterations, null);
				} catch (NoSuchAlgorithmException ex) {
					throw new InvalidKeySpecException(ex);
				}
			}
			MerkleTree mt = new MerkleTree(height, hashEngine, digestIterations);
			mt.computeWithLeafHashes(hashes);
			if (!Arrays.areEqual(root, mt.getRoot())) {
				throw new InvalidKeySpecException("Root value defined in MSS Key Parameters '" + DatatypeConverter.printHexBinary(root) + "' does not match the calculated root value for the key pairs '" + DatatypeConverter.printHexBinary(mt.getRoot()) + "'.");
			}
			return new XNMSSPrivateKey(algorithm, height, digestIterations, parentOTSPublicKey, parentSign, mt, otsPrivateKeys);
		} catch (IOException | NoSuchAlgorithmException | DataFormatException ex) {
			throw new InvalidKeySpecException(ex);
		} catch (InvalidKeySpecException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new InvalidKeySpecException("Private Key Parse Exception: " + ex.getMessage());
		}
	}

	private Object[] _decodeKeyParams(ASN1Sequence params) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		int height = ((ASN1Integer) params.getObjectAt(0)).getValue().intValue();
		int digestIterations = ((ASN1Integer) params.getObjectAt(1)).getValue().intValue();
		if (params.size() > 2) {
			ASN1Sequence subKeyParams = (ASN1Sequence) params.getObjectAt(2);
			byte[] pubKeyBytes = ((ASN1OctetString) subKeyParams.getObjectAt(0)).getOctets();
			String algorithm = ASN1.detectKeyAlgorithmForKeyBytes(pubKeyBytes);
			KeyFactory kf = KeyFactory.getInstance(algorithm);
			XNMSSOTSPublicKey parentOTSPublicKey = (XNMSSOTSPublicKey) kf.generatePublic(new X509EncodedKeySpec(pubKeyBytes));
			byte[] parentSign = ((ASN1OctetString) subKeyParams.getObjectAt(1)).getOctets();
			return new Object[]{height, digestIterations, parentOTSPublicKey, parentSign};
		} else {
			return new Object[]{height, digestIterations, null, null};
		}
	}

	@Override
	protected <T extends KeySpec> T engineGetKeySpec(Key key, Class<T> keySpec) throws InvalidKeySpecException {
		switch (key.getFormat()) {
			case "X.509":
				return (T) new X509EncodedKeySpec(key.getEncoded());
			case "PKCS#8":
				return (T) new PKCS8EncodedKeySpec(key.getEncoded());
			default:
				throw new InvalidKeySpecException("Invalid Key Spec.");
		}
	}

	@Override
	protected Key engineTranslateKey(Key key) throws InvalidKeyException {
		throw new InvalidKeyException("Not supported.");
	}

}
