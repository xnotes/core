/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss.jce;

import org.xnotes.core.security.ots.xnmss.XNMSS;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.utils.DatatypeConverter;
import org.xnotes.core.security.ots.MetaKey;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <M>
 * @param <O>
 */
public abstract class XNMSSKey<M extends XNMSSKey, O extends XNMSSOTSKey> implements MetaKey<O>, ASN1Encodable {

	private final String _algorithm;
	private final AlgorithmIdentifier _algorithmIdentifier;
	private final String _digestAlgorithm;
	private final int _digestLength;
	private final String _signatureKeyAlgorithm;
	private final String _signAlgorithm;
	private final int _height;
	private final int _otsKeyCount;
	private final int _digestIterations;
	private final byte[] _root;
	private final XNMSSOTSPublicKey _parentOTSPublicKey;
	private final byte[] _parentSign;
	private final ASN1Sequence _asn1Parameters;
	private final String _reference;
	private SecureRandom _secureRandom;
	
	protected XNMSSKey(String algorithm, int height, int digestIterations, byte[] root, XNMSSOTSPublicKey parentOTSPublicKey, byte[] parentSign, XNMSSOTSPrivateKey parentOTSPrivateKey) throws IllegalArgumentException {
		_algorithm = algorithm;
		_algorithmIdentifier = XNMSS.getAlgorithmIdentifierForKeyAlgorithm(_algorithm);
		_digestAlgorithm = XNMSS.getDigestAlgorithmForKeyAlgorithm(_algorithm);
		switch (_digestAlgorithm) {
			case "SHA-224":
			case "SHA3-224":
				_digestLength = 28;
				break;
			case "SHA-256":
			case "SHA3-256":
				_digestLength = 32;
				break;
			case "SHA-384":
			case "SHA3-384":
				_digestLength = 48;
				break;
			case "SHA-512":
			case "SHA3-512":
				_digestLength = 64;
				break;
			default:
				_digestLength = 20;
				break;
		}
		_signatureKeyAlgorithm = XNMSS.getSigningKeyAlgorithmForKeyAlgorithm(_algorithm);
		_signAlgorithm = XNMSS.getSignatureAlgorithmForDigestAlgorithmAndSigningKeyAlgorithm(_digestAlgorithm, _signatureKeyAlgorithm);
		_height = height;
		_otsKeyCount = (int) Math.pow(2, _height);
		_digestIterations = digestIterations;
		_root = root;
		if (parentOTSPrivateKey != null) {
			_parentOTSPublicKey = parentOTSPrivateKey.getPublicKey();
			try {
				Signature sign = Signature.getInstance("XNMSS");
				sign.initSign(parentOTSPrivateKey);
				sign.update(_root);
				_parentSign = sign.sign();
			} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
				throw new IllegalArgumentException(ex);
			}
		} else {
			_parentOTSPublicKey = parentOTSPublicKey;
			_parentSign = parentSign;
		}
		ASN1EncodableVector params = new ASN1EncodableVector();
		params.add(new ASN1Integer(_height));
		params.add(new ASN1Integer(_digestIterations));
		if (_parentOTSPublicKey != null) {
			ASN1EncodableVector subKeyParams = new ASN1EncodableVector();
			subKeyParams.add(new DEROctetString(_parentOTSPublicKey.getEncoded()));
			subKeyParams.add(new DEROctetString(_parentSign));
			params.add(new DERSequence(subKeyParams));
		}
		_asn1Parameters = new DERSequence(params);
		_reference = DatatypeConverter.printHexBinary(_root);
	}
	
	@Override
	public final String getReference() {
		return _reference;
	}

	@Override
	public final String getAlgorithm() {
		return _algorithm;
	}

	protected final AlgorithmIdentifier getAlgorithmIdentifier() {
		return _algorithmIdentifier;
	}

	public SecureRandom getSecureRandom() {
		return _secureRandom;
	}
	
	public void setSecureRandom(SecureRandom secureRandom) {
		_secureRandom = secureRandom;
	}

	public final int getHeight() {
		return _height;
	}

	@Override
	public final int getOTSKeyCount() {
		return _otsKeyCount;
	}

	public final int getDigestIterations() {
		return _digestIterations;
	}

	public final byte[] getRoot() {
		return _root;
	}

	public final String getDigestAlgorithm() {
		return _digestAlgorithm;
	}

	public final int getDigestLength() {
		return _digestLength;
	}

	public final String getSignatureKeyAlgorithm() {
		return _signatureKeyAlgorithm;
	}

	public final String getSignatureAlgorithm() {
		return _signAlgorithm;
	}

	protected final ASN1Encodable getASN1Parameters() {
		return _asn1Parameters;
	}

	public final boolean isSubKey() {
		return _parentOTSPublicKey != null;
	}

	public final XNMSSOTSPublicKey getParentOTSPublicKey() {
		return _parentOTSPublicKey;
	}
	
	public final byte[] getParentSignature() {
		return _parentSign;
	}
	
	public abstract M getRootKey() throws KeyManagerException;
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final XNMSSKey other = (XNMSSKey) obj;
		return Arrays.equals(this.getRoot(), other.getRoot());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + Arrays.hashCode(this.getRoot());
		return hash;
	}

}
