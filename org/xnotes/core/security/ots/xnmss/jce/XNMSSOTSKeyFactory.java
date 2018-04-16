/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss.jce;

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
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.xnotes.core.utils.ASN1;
import org.xnotes.core.utils.MerkleTree.Hash;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSOTSKeyFactory extends KeyFactorySpi {

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
			Object[] keyParams = _decodeKey(ks.getEncoded(), false);
			return new XNMSSOTSPublicKey((String) keyParams[0], (int) keyParams[1], (int) keyParams[2], (byte[]) keyParams[3], (int) keyParams[4], (int) keyParams[5], (PublicKey) keyParams[6], (Hash[]) keyParams[7]);
		} catch (IOException | IllegalArgumentException | NoSuchAlgorithmException ex) {
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
			Object[] keyParams = _decodeKey(ks.getEncoded(), true);
			return new XNMSSOTSPrivateKey((String) keyParams[1], (int) keyParams[2], (int) keyParams[3], (byte[]) keyParams[4], (int) keyParams[5], (int) keyParams[6], (PublicKey) keyParams[7], (PrivateKey) keyParams[8], (Hash[]) keyParams[9]);
		} catch (IOException | IllegalArgumentException | NoSuchAlgorithmException ex) {
			throw new InvalidKeySpecException(ex);
		} catch (InvalidKeySpecException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new InvalidKeySpecException("Private Key Parse Exception: " + ex.getMessage());
		}
	}

	private Object[] _decodeKey(byte[] keyBytes, boolean priv) throws IOException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
		ASN1Sequence encoded = (ASN1Sequence) ASN1Primitive.fromByteArray(keyBytes);
		int pkcs8Version = priv ? ((ASN1Integer) encoded.getObjectAt(0)).getValue().intValue() : 0;
		String algorithm = ASN1.detectKeyAlgorithmForKeyBytes(keyBytes);
		if (!algorithm.toUpperCase().startsWith("XNMSSOTSWITHSHA")) {
			throw new InvalidKeySpecException("Invalid XNMSS Onte-Time-Signature Key Algorithm '" + algorithm + "'.");
		}
		ASN1Sequence asn1Key = (ASN1Sequence) ASN1Primitive.fromByteArray(priv ? ((ASN1OctetString) encoded.getObjectAt(2)).getOctets() : ((ASN1BitString) encoded.getObjectAt(1)).getBytes());
		int height = ((ASN1Integer) asn1Key.getObjectAt(0)).getValue().intValue();
		int digestIterations = ((ASN1Integer) asn1Key.getObjectAt(1)).getValue().intValue();
		byte[] root = ((ASN1OctetString) asn1Key.getObjectAt(2)).getOctets();
		int indexOffset = ((ASN1Integer) asn1Key.getObjectAt(3)).getValue().intValue();
		int index = ((ASN1Integer) asn1Key.getObjectAt(4)).getValue().intValue();
		byte[] signPubKeyBytes = ((ASN1OctetString) asn1Key.getObjectAt(5)).getOctets();
		String pubAlg = ASN1.detectKeyAlgorithmForKeyBytes(signPubKeyBytes);
		byte[] signPrivKeyBytes = null;
		String privAlg;
		if (priv) {
			signPrivKeyBytes = ((ASN1OctetString) asn1Key.getObjectAt(6)).getOctets();
			privAlg = ASN1.detectKeyAlgorithmForKeyBytes(signPrivKeyBytes);
			if (!pubAlg.equals(privAlg)) {
				throw new InvalidKeySpecException("OTS Public Key and Private Key are of different Algorithm.");
			}
		}
		KeyFactory kf = KeyFactory.getInstance(pubAlg);
		PublicKey signPublicKey = kf.generatePublic(new X509EncodedKeySpec(signPubKeyBytes));
		PrivateKey signPrivateKey = signPrivKeyBytes != null ? kf.generatePrivate(new PKCS8EncodedKeySpec(signPrivKeyBytes)) : null;
		ASN1Sequence hashPathSeq = (ASN1Sequence) asn1Key.getObjectAt(priv ? 7 : 6);
		Hash[] hashPath = new Hash[hashPathSeq.size()];
		for (int i = 0; i < hashPathSeq.size(); i++) {
			hashPath[i] = new Hash(
					((ASN1Integer) ((ASN1Sequence) hashPathSeq.getObjectAt(i)).getObjectAt(0)).getValue().intValue(),
					((ASN1Integer) ((ASN1Sequence) hashPathSeq.getObjectAt(i)).getObjectAt(1)).getValue().intValue(),
					((ASN1OctetString) ((ASN1Sequence) hashPathSeq.getObjectAt(i)).getObjectAt(2)).getOctets());
		}
		return priv ? new Object[]{pkcs8Version, algorithm, height, digestIterations, root, indexOffset, index, signPublicKey, signPrivateKey, hashPath} : new Object[]{algorithm, height, digestIterations, root, indexOffset, index, signPublicKey, hashPath};
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
