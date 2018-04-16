/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss.jce;

import org.xnotes.core.security.ots.xnmss.XNMSSPublicKeyManager;
import org.xnotes.core.security.ots.xnmss.XNMSS;
import org.xnotes.core.utils.MerkleTree;
import java.security.PublicKey;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.security.ots.OTSPublicKey;
import org.xnotes.core.utils.DatatypeConverter;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSOTSPublicKey extends XNMSSOTSKey<XNMSSPublicKey> implements OTSPublicKey {

	public XNMSSOTSPublicKey(String algorithm, int height, int digestIterations, byte[] root, int indexOffset, int index, PublicKey signPublicKey, MerkleTree.Hash[] hashPath) {
		super(algorithm, height, digestIterations, root, indexOffset, index, signPublicKey, hashPath);
	}

	@Override
	public XNMSSPublicKey getMetaKey() throws KeyManagerException {
		XNMSSPublicKeyManager km = XNMSS.getPublicKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Public Key Manager configured for XNMSS Private Keys.");
		}
		return km.getMetaKey(this.getMetaKeyReference());
	}

	@Override
	public final boolean isUsed() throws KeyManagerException {
		XNMSSPublicKeyManager km = XNMSS.getPublicKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Public Key Manager configured for XNMSS Private Keys.");
		}
		return km.isUsed(this.getMetaKeyReference(), this.getIndex());
	}

	@Override
	public final Date getUsedTime() throws KeyManagerException {
		if (this.isUsed()) {
			return XNMSS.getPublicKeyManager().getUsedTime(this.getMetaKeyReference(), this.getIndex());
		} else {
			return null;
		}
	}

	@Override
	public final byte[] getUsedHash() throws KeyManagerException {
		if (this.isUsed()) {
			return XNMSS.getPublicKeyManager().getUsedHash(this.getMetaKeyReference(), this.getIndex());
		} else {
			return null;
		}
	}

	@Override
	public String getFormat() {
		return "X.509";
	}

	@Override
	public final byte[] getEncoded() {
		return KeyUtil.getEncodedSubjectPublicKeyInfo(this.getAlgorithmIdentifier(), this.toASN1Primitive());
	}

	@Override
	public ASN1Primitive toASN1Primitive() {
		return super.toASN1Primitive(null);
	}

	@Override
	public String toString() {
		return "XNMSS OTS Public Key '" + this.getMetaKeyReference() + "_" + this.getIndex() + "'" + (this.isUsed() ? ": used on " + this.getUsedTime().toString() + " with data hash '" + DatatypeConverter.printBase58Binary(this.getUsedHash()) : " (unused).");
	}

}
