/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss.jce;

import org.xnotes.core.security.ots.xnmss.XNMSSPrivateKeyManager;
import org.xnotes.core.security.ots.xnmss.XNMSS;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Objects;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.security.ots.OTSPrivateKey;
import org.xnotes.core.utils.DatatypeConverter;
import org.xnotes.core.utils.MerkleTree.Hash;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSOTSPrivateKey extends XNMSSOTSKey<XNMSSPrivateKey> implements OTSPrivateKey<XNMSSOTSPublicKey> {

	private final XNMSSOTSPublicKey _otsPublicKey;
	private final PrivateKey _signPrivateKey;

	protected XNMSSOTSPrivateKey(String algorithm, int height, int digestIterations, byte[] root, int indexOffset, int index, PublicKey signPublicKey, PrivateKey signPrivateKey, Hash[] hashPath) {
		super(algorithm, height, digestIterations, root, indexOffset, index, signPublicKey, hashPath);
		_otsPublicKey = new XNMSSOTSPublicKey(algorithm, height, digestIterations, root, indexOffset, index, signPublicKey, hashPath);
		_signPrivateKey = signPrivateKey;
	}

	public PrivateKey getSignaturePrivateKey() {
		return _signPrivateKey;
	}

	@Override
	public XNMSSOTSPublicKey getPublicKey() {
		return _otsPublicKey;
	}

	@Override
	public XNMSSPrivateKey getMetaKey() throws KeyManagerException {
		XNMSSPrivateKeyManager km = XNMSS.getPrivateKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Private Key Manager configured for XNMSS Private Keys.");
		}
		return km.getMetaKey(this.getMetaKeyReference());
	}

	@Override
	public final boolean isUsed() throws KeyManagerException {
		XNMSSPrivateKeyManager km = XNMSS.getPrivateKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Private Key Manager configured for XNMSS Private Keys.");
		}
		return km.isUsed(this.getMetaKeyReference(), this.getIndex());
	}

	@Override
	public final Date getUsedTime() throws KeyManagerException {
		if (this.isUsed()) {
			return XNMSS.getPrivateKeyManager().getUsedTime(this.getMetaKeyReference(), this.getIndex());
		} else {
			return null;
		}
	}

	@Override
	public final byte[] getUsedHash() throws KeyManagerException {
		if (this.isUsed()) {
			return XNMSS.getPrivateKeyManager().getUsedHash(this.getMetaKeyReference(), this.getIndex());
		} else {
			return null;
		}
	}

	@Override
	public String getFormat() {
		return "PKCS#8";
	}

	@Override
	public byte[] getEncoded() {
		return KeyUtil.getEncodedPrivateKeyInfo(this.getAlgorithmIdentifier(), this.toASN1Primitive());
	}

	@Override
	public ASN1Primitive toASN1Primitive() {
		return super.toASN1Primitive(_signPrivateKey);
	}

	@Override
	public String toString() {
		return "XNMSS OTS Private Key '" + this.getMetaKeyReference() + "_" + this.getIndex() + "'" + (this.isUsed() ? ": used on " + this.getUsedTime().toString() + " with data hash '" + DatatypeConverter.printBase58Binary(this.getUsedHash()) : " (unused).");
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj)
				&& _otsPublicKey.equals(obj)
				&& _signPrivateKey.equals(obj);
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = 59 * hash + Objects.hashCode(_otsPublicKey);
		hash = 59 * hash + Objects.hashCode(_signPrivateKey);
		return hash;
	}

}
