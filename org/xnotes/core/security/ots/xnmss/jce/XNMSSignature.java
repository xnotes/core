/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss.jce;

import org.xnotes.core.security.ots.xnmss.XNMSSPrivateKeyManager;
import org.xnotes.core.security.ots.xnmss.XNMSSPublicKeyManager;
import org.xnotes.core.security.ots.xnmss.XNMSS;
import org.xnotes.core.utils.MerkleTree;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.xnotes.core.security.hash.HashEngine;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSignature extends Signature {

	private XNMSSPublicKey _publicKey;
	private ByteArrayOutputStream _bytesToBeHashed;
	private DataOutputStream _dataToBeHashed;
	private XNMSSOTSPrivateKey _otsPrivateKey;
	private Signature _OTSignature;

	public XNMSSignature() {
		super("XNMSS");
	}

	@Override
	protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
		if (!XNMSSPublicKey.class.isInstance(publicKey)) {
			throw new InvalidKeyException("Invalid Public Key class '" + publicKey.getClass().getName() + "' for XNMSS Signature: Public Key of class '" + XNMSSPublicKey.class.getName() + "' expected.");
		}
		_publicKey = (XNMSSPublicKey) publicKey;
		_bytesToBeHashed = new ByteArrayOutputStream();
		_dataToBeHashed = new DataOutputStream(_bytesToBeHashed);
		_otsPrivateKey = null;
		try {
			_OTSignature = Signature.getInstance(_publicKey.getSignatureAlgorithm());
		} catch (NoSuchAlgorithmException ex) {
			throw new InvalidKeyException(ex);
		}
	}

	@Override
	protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
		if (!XNMSSPrivateKey.class.isInstance(privateKey) && !XNMSSOTSPrivateKey.class.isInstance(privateKey)) {
			throw new InvalidKeyException("Invalid Private Key class '" + privateKey.getClass().getName() + "' for XNMSS Signature: Private Key of class '" + XNMSSPrivateKey.class.getName() + "' or '" + XNMSSOTSPrivateKey.class.getName() + "' expected.");
		}
		_publicKey = null;
		if (XNMSSPrivateKey.class.isInstance(privateKey)) {
			_otsPrivateKey = ((XNMSSPrivateKey) privateKey).getCurrentOTSKey();
		} else {
			_otsPrivateKey = (XNMSSOTSPrivateKey) privateKey;
		}
		if (XNMSS.getPrivateKeyManager().isUsed(_otsPrivateKey.getMetaKeyReference(), _otsPrivateKey.getIndex())) {
			throw new InvalidKeyException("OTS Private Key #" + _otsPrivateKey.getIndex() + " for Meta Key reference '" + _otsPrivateKey.getMetaKeyReference() + "' has already been used for signing.");
		}
		_bytesToBeHashed = new ByteArrayOutputStream();
		_dataToBeHashed = new DataOutputStream(_bytesToBeHashed);
		try {
			_OTSignature = Signature.getInstance(XNMSS.getSignatureAlgorithmForDigestAlgorithmAndSigningKeyAlgorithm(XNMSS.getDigestAlgorithmForKeyAlgorithm(_otsPrivateKey.getAlgorithm()),
					_otsPrivateKey.getSignaturePrivateKey().getAlgorithm()));
			_OTSignature.initSign(_otsPrivateKey.getSignaturePrivateKey());
		} catch (InvalidKeyException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new InvalidKeyException(ex);
		}
	}

	@Override
	protected void engineUpdate(byte b) throws SignatureException {
		try {
			_dataToBeHashed.write(b);
		} catch (Throwable ex) {
			throw new SignatureException(ex);
		}
	}

	@Override
	protected void engineUpdate(byte[] b, int off, int len) throws SignatureException {
		try {
			_dataToBeHashed.write(Arrays.copyOfRange(b, off, off + len));
		} catch (Throwable ex) {
			throw new SignatureException(ex);
		}
	}

	@Override
	protected byte[] engineSign() throws SignatureException {
		if (_otsPrivateKey == null) {
			throw new SignatureException("Not initialized for signature.");
		}
		try {
			_dataToBeHashed.flush();
			byte[] bytesToBeHashed = _bytesToBeHashed.toByteArray();
			byte[] hash = MessageDigest.getInstance(_otsPrivateKey.getDigestAlgorithm()).digest(bytesToBeHashed);
			if (_otsPrivateKey.isUsed() && !Arrays.equals(_otsPrivateKey.getUsedHash(), hash)) {
				throw new SignatureException("Signing failed: Private Key referenced '" + _otsPrivateKey.getMetaKeyReference() + ":" + _otsPrivateKey.getIndex() + "' has already been used for signing different data on " + _otsPrivateKey.getUsedTime().toString() + ".");
			}
			_OTSignature.update(bytesToBeHashed);
			ASN1EncodableVector signWrap = new ASN1EncodableVector();
			signWrap.add(XNMSS.AlgorithmIdentifiers.XNMSS);
			ASN1EncodableVector sign = new ASN1EncodableVector();
			sign.add(new DEROctetString(_otsPrivateKey.getPublicKey().getEncoded()));
			sign.add(new DEROctetString(_OTSignature.sign()));
			signWrap.add(new DEROctetString(new DERSequence(sign).getEncoded()));
			byte[] signature = new DERSequence(signWrap).getEncoded();
			XNMSSPrivateKeyManager km = XNMSS.getPrivateKeyManager();
			if (km != null) {
				km.nextOTSKey(_otsPrivateKey.getMetaKeyReference(), hash);
			}
			return signature;
		} catch (Throwable ex) {
			throw new SignatureException(ex);
		}
	}

	@Override
	protected boolean engineVerify(byte[] sigBytes) throws SignatureException {
		if (_publicKey == null) {
			throw new SignatureException("Not initialized for verification.");
		}
		try {
			ASN1Sequence signWrap = (ASN1Sequence) ASN1Primitive.fromByteArray(sigBytes);
			AlgorithmIdentifier algId = new AlgorithmIdentifier(
					(ASN1ObjectIdentifier) ((ASN1Sequence) signWrap.getObjectAt(0)).getObjectAt(0),
					((ASN1Sequence) signWrap.getObjectAt(0)).size() > 1 ? ((ASN1Sequence) signWrap.getObjectAt(0)).getObjectAt(1) : null);
			if (!algId.equals(XNMSS.AlgorithmIdentifiers.XNMSS)) {
				throw new SignatureException("Unsupported Signature Algorithm Identifier '" + algId.toString() + "'.");
			}
			ASN1Sequence sigSeq = (ASN1Sequence) ASN1Primitive.fromByteArray(((ASN1OctetString) signWrap.getObjectAt(1)).getOctets());
			KeyFactory kf = KeyFactory.getInstance(XNMSS.getOTSKeyAlgorithmForKeyAlgorithm(_publicKey.getAlgorithm()));
			XNMSSOTSPublicKey otsPublicKey = (XNMSSOTSPublicKey) kf.generatePublic(new X509EncodedKeySpec(((ASN1OctetString) sigSeq.getObjectAt(0)).getOctets()));
			_OTSignature.initVerify(otsPublicKey.getSignaturePublicKey());
			_dataToBeHashed.flush();
			byte[] bytesToBeHashed = _bytesToBeHashed.toByteArray();
			byte[] hash = MessageDigest.getInstance(_publicKey.getDigestAlgorithm()).digest(bytesToBeHashed);
			_OTSignature.update(bytesToBeHashed);
			byte[] otSign = ((ASN1OctetString) sigSeq.getObjectAt(1)).getOctets();
			if (!_OTSignature.verify(otSign)) {
				return false;
			}
			XNMSSPublicKeyManager km = XNMSS.getPublicKeyManager();
			XNMSSOTSPublicKey otsPubKey = otsPublicKey;
			if (!Arrays.equals(otsPublicKey.getRoot(), _publicKey.getRoot())) {
				if (km != null && km.isManaged(_publicKey.getReference())) {
					XNMSSPublicKey metaPubKey = km.getMetaKey(otsPublicKey.getMetaKeyReference());
					while (metaPubKey.isSubKey() && !Arrays.equals(otsPubKey.getRoot(), _publicKey.getRoot())) {
						XNMSSPublicKey parentMetaKey = km.getMetaKey(metaPubKey.getParentOTSPublicKey().getMetaKeyReference());
						Signature signer = Signature.getInstance("XNMSS");
						signer.initVerify(parentMetaKey);
						signer.update(metaPubKey.getRoot());
						if (!signer.verify(metaPubKey.getParentSignature())) {
							return false;
						}
						km.setChildMetaKey(parentMetaKey.getReference(), metaPubKey.getReference());
						otsPubKey = metaPubKey.getParentOTSPublicKey();
						metaPubKey = parentMetaKey;
					}
					if (!Arrays.equals(otsPubKey.getRoot(), _publicKey.getRoot())) {
						return false;
					}
				} else {
					return false;
				}
			}
			HashEngine he = XNMSS.getHashEngineForDigestAlgorithm(XNMSS.getDigestAlgorithmForKeyAlgorithm(_publicKey.getAlgorithm()));
			MerkleTree mt = new MerkleTree(_publicKey.getHeight(), he, _publicKey.getDigestIterations());
			byte[] root = mt.computeWithLeafHashAndPath(otsPubKey.getIndex(), he.hash(otsPubKey.getSignaturePublicKey().getEncoded(), _publicKey.getDigestIterations(), null), otsPubKey.getHashPath());
			boolean verif = Arrays.equals(root, _publicKey.getRoot());
			if (verif && km != null && km.isManaged(otsPublicKey.getMetaKeyReference()) && !km.getUsedOTSKeys(otsPublicKey.getMetaKeyReference()).contains(otsPublicKey)) {
				km.markUsed(otsPublicKey.getMetaKeyReference(), otsPublicKey.getIndex(), new Date(), hash);
			}
			return verif;
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException ex) {
			throw new SignatureException(ex);
		} catch (SignatureException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new SignatureException("Signature Parse Exception: " + ex.getMessage());
		}
	}

	@Override
	protected void engineSetParameter(String param, Object value) throws InvalidParameterException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected Object engineGetParameter(String param) throws InvalidParameterException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
