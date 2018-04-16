/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss.jce;

import org.xnotes.core.security.ots.xnmss.XNMSSPublicKeyManager;
import org.xnotes.core.security.ots.xnmss.XNMSS;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.xnotes.core.security.ots.MetaPublicKey;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.security.ots.KeyList;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSPublicKey extends XNMSSKey<XNMSSPublicKey, XNMSSOTSPublicKey> implements MetaPublicKey<XNMSSOTSPublicKey> {

	protected XNMSSPublicKey(String algorithm, int height, int digestIterations, byte[] root, XNMSSOTSPublicKey parentOTSPublicKey, byte[] parentSign) throws IllegalArgumentException {
		this(algorithm, height, digestIterations, root, parentOTSPublicKey, parentSign, null);
	}

	protected XNMSSPublicKey(String algorithm, int height, int digestIterations, byte[] root, XNMSSOTSPrivateKey parentOTSPrivateKey) throws IllegalArgumentException {
		this(algorithm, height, digestIterations, root, null, null, parentOTSPrivateKey);
	}

	private XNMSSPublicKey(String algorithm, int height, int digestIterations, byte[] root, XNMSSOTSPublicKey parentOTSPublicKey, byte[] parentSign, XNMSSOTSPrivateKey parentOTSPrivateKey) throws IllegalArgumentException {
		super(algorithm, height, digestIterations, root, parentOTSPublicKey, parentSign, parentOTSPrivateKey);
	}

	@Override
	public XNMSSOTSPublicKey getCurrentOTSKey() throws KeyManagerException {
		XNMSSPublicKeyManager km = XNMSS.getPublicKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Key Manager configured for XNMSS Keys.");
		}
		return km.getCurrentOTSKey(this.getReference());
	}

	@Override
	public KeyList<XNMSSOTSPublicKey> getOTSKeys() throws KeyManagerException {
		XNMSSPublicKeyManager km = XNMSS.getPublicKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Key Manager configured for XNMSS Keys.");
		}
		return km.getOTSKeys(this.getReference());
	}

	@Override
	public KeyList<XNMSSOTSPublicKey> getUsedOTSKeys() throws KeyManagerException {
		XNMSSPublicKeyManager km = XNMSS.getPublicKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Key Manager configured for XNMSS Keys.");
		}
		return km.getUsedOTSKeys(this.getReference());
	}

	@Override
	public XNMSSPublicKey getRootKey() throws KeyManagerException {
		XNMSSPublicKeyManager km = XNMSS.getPublicKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Key Manager configured for XNMSS Keys.");
		}
		XNMSSPublicKey key = this;
		while (key.isSubKey()) {
			key = km.getMetaKey(key.getParentOTSPublicKey().getMetaKeyReference());
		}
		return key;
	}

	@Override
	public String getFormat() {
		return "X.509";
	}

	@Override
	public byte[] getEncoded() {
		return KeyUtil.getEncodedSubjectPublicKeyInfo(this.getAlgorithmIdentifier(), this.toASN1Primitive());
	}

	@Override
	public ASN1Primitive toASN1Primitive() {
		ASN1EncodableVector key = new ASN1EncodableVector();
		key.add(this.getASN1Parameters());
		key.add(new DEROctetString(this.getRoot()));
		return new DERSequence(key);
	}

	@Override
	public String toString() {
		return "XNMSS Public Key '" + this.getReference() + "': " + this.getDigestIterations() + " x " + this.getDigestAlgorithm() + " with " + this.getOTSKeyCount() + " " + XNMSS.getOTSKeyAlgorithmForKeyAlgorithm(this.getAlgorithm()) + " OTS Keys.";
	}

}
