/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss.jce;

import org.xnotes.core.security.ots.xnmss.XNMSS;
import org.xnotes.core.utils.MerkleTree;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Objects;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.utils.MerkleTree.Hash;
import org.xnotes.core.utils.DatatypeConverter;
import org.xnotes.core.security.ots.OTSKey;
import org.xnotes.core.utils.ASN1;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <M>
 */
public abstract class XNMSSOTSKey<M extends XNMSSKey> implements OTSKey, ASN1Encodable {

	private SecureRandom _secureRandom;
	private final String _algorithm;
	private final AlgorithmIdentifier _algorithmIdentifier;
	private final String _metaKeyAlgorithm;
	private final String _digestAlgorithm;
	private final int _height;
	private final int _otsKeyCount;
	private final int _digestIterations;
	private final byte[] _root;
	private final int _indexOffset;
	private final int _index;
	private final PublicKey _signPublicKey;
	private final MerkleTree.Hash[] _hashPath;
	private final String _metaKeyReference;

	protected XNMSSOTSKey(String algorithm, int height, int digestIterations, byte[] root, int indexOffset, int index, PublicKey signPublicKey, Hash[] hashPath) {
		_algorithm = algorithm;
		_algorithmIdentifier = XNMSS.getAlgorithmIdentifierForKeyAlgorithm(_algorithm);
		String metaKeyAlgorithm;
		try {
			metaKeyAlgorithm = _algorithm.substring(0, 5) + _algorithm.substring(8) + "And" + ASN1.detectKeyAlgorithmForKeyBytes(signPublicKey.getEncoded());
		} catch (InvalidKeySpecException ex) {
			metaKeyAlgorithm = null;
		}
		_metaKeyAlgorithm = metaKeyAlgorithm;
		_digestAlgorithm = XNMSS.getDigestAlgorithmForKeyAlgorithm(_algorithm);
		_height = height;
		_otsKeyCount = (int) Math.pow(2, _height);
		_digestIterations = digestIterations;
		_root = root;
		_indexOffset = indexOffset;
		_index = index;
		_signPublicKey = signPublicKey;
		_hashPath = hashPath;
		_metaKeyReference = DatatypeConverter.printHexBinary(_root);
	}

	@Override
	public final String getAlgorithm() {
		return _algorithm;
	}

	public final AlgorithmIdentifier getAlgorithmIdentifier() {
		return _algorithmIdentifier;
	}
	
	public final String getMetaKeyAlgorithm() {
		return _metaKeyAlgorithm;
	}
	
	public final SecureRandom getSecureRandom() {
		return _secureRandom;
	}
	
	public final void setSecureRandom(SecureRandom secureRandom) {
		_secureRandom = secureRandom;
	}

	public final int getHeight() {
		return _height;
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

	@Override
	public final String getMetaKeyReference() {
		return _metaKeyReference;
	}
	
	public abstract M getMetaKey() throws KeyManagerException;

	public final int getIndexOffset() {
		return _indexOffset;
	}

	@Override
	public final int getOTSKeyCount() {
		return _otsKeyCount;
	}

	@Override
	public final int getIndex() {
		return _index;
	}

	public final PublicKey getSignaturePublicKey() {
		return _signPublicKey;
	}

	public final Hash[] getHashPath() {
		return _hashPath;
	}

	protected ASN1Primitive toASN1Primitive(PrivateKey privateKey) {
		ASN1EncodableVector key = new ASN1EncodableVector();
		key.add(new ASN1Integer(_height));
		key.add(new ASN1Integer(_digestIterations));
		key.add(new DEROctetString(this.getRoot()));
		key.add(new ASN1Integer(_indexOffset));
		key.add(new ASN1Integer(_index));
		key.add(new DEROctetString(this.getSignaturePublicKey().getEncoded()));
		if (privateKey != null) {
			key.add(new DEROctetString(privateKey.getEncoded()));
		}
		ASN1EncodableVector pathVector = new ASN1EncodableVector();
		for (Hash h : this.getHashPath()) {
			ASN1EncodableVector hVector = new ASN1EncodableVector();
			hVector.add(new ASN1Integer(h.layer));
			hVector.add(new ASN1Integer(h.index));
			hVector.add(new DEROctetString(h.hash));
			pathVector.add(new DERSequence(hVector));
		}
		key.add(new DERSequence(pathVector));
		return new DERSequence(key);
	}

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
		final XNMSSOTSKey other = (XNMSSOTSKey) obj;
		return (Arrays.equals(_root, other._root)
				&& _index == other._index
				&& _signPublicKey.equals(other._signPublicKey)
				&& Arrays.deepEquals(_hashPath, other._hashPath));
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + Arrays.hashCode(_root);
		hash = 59 * hash + _index;
		hash = 59 * hash + Objects.hashCode(_signPublicKey);
		hash = 59 * hash + Arrays.deepHashCode(_hashPath);
		return hash;
	}

}
