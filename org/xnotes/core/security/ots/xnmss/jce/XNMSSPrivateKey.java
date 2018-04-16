/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss.jce;

import org.xnotes.core.security.ots.xnmss.XNMSSPrivateKeyManager;
import org.xnotes.core.security.ots.xnmss.XNMSS;
import org.xnotes.core.utils.MerkleTree;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.security.ots.MetaPrivateKey;
import org.xnotes.core.security.ots.KeyArrayList;
import org.xnotes.core.security.ots.KeyList;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSPrivateKey extends XNMSSKey<XNMSSPrivateKey, XNMSSOTSPrivateKey> implements MetaPrivateKey<XNMSSPublicKey, XNMSSOTSPrivateKey, XNMSSOTSPublicKey> {

	private final MerkleTree _merkleTree;
	private final XNMSSPublicKey _publicKey;
	private final KeyList<XNMSSOTSPrivateKey> _otsPrivateKeys;

	protected XNMSSPrivateKey(String algorithm, int height, int digestIterations, XNMSSOTSPublicKey parentOTSPublicKey, byte[] parentSign, MerkleTree merkleTree, XNMSSOTSPrivateKey[] otsPrivateKeys) throws IllegalArgumentException {
		this(algorithm, height, digestIterations, null, parentOTSPublicKey, parentSign, null, merkleTree, otsPrivateKeys);
	}

	protected XNMSSPrivateKey(String algorithm, int height, int digestIterations, XNMSSPublicKey publicKey, XNMSSOTSPrivateKey parentOTSPrivateKey, MerkleTree merkleTree, XNMSSOTSPrivateKey[] otsPrivateKeys) throws IllegalArgumentException {
		this(algorithm, height, digestIterations, publicKey, null, null, parentOTSPrivateKey, merkleTree, otsPrivateKeys);
	}

	private XNMSSPrivateKey(String algorithm, int height, int digestIterations, XNMSSPublicKey publicKey, XNMSSOTSPublicKey parentOTSPublicKey, byte[] parentSign, XNMSSOTSPrivateKey parentOTSPrivateKey, MerkleTree merkleTree, XNMSSOTSPrivateKey[] otsPrivateKeys) throws IllegalArgumentException {
		super(algorithm, height, digestIterations, merkleTree.getRoot(), parentOTSPublicKey, parentSign, parentOTSPrivateKey);
		if (this.getOTSKeyCount() != otsPrivateKeys.length) {
			throw new IllegalArgumentException("Height of " + this.getHeight() + " requires " + this.getOTSKeyCount() + " key pairs: " + otsPrivateKeys.length + " key pairs found.");
		}
		_merkleTree = merkleTree;
		_publicKey = publicKey != null ? publicKey : new XNMSSPublicKey(algorithm, height, digestIterations, merkleTree.getRoot(), this.getParentOTSPublicKey(), this.getParentSignature());
		XNMSSPrivateKeyManager km = XNMSS.getPrivateKeyManager();
		if (km != null) {
			km.setOTSKeys(otsPrivateKeys);
			_otsPrivateKeys = km.getOTSKeys(this.getReference());
		} else {
			_otsPrivateKeys = new KeyArrayList<>(otsPrivateKeys);
		}
	}

	protected MerkleTree getMerkleTree() {
		return _merkleTree;
	}

	@Override
	public XNMSSPublicKey getPublicKey() {
		return _publicKey;
	}

	@Override
	public XNMSSOTSPrivateKey getCurrentOTSKey() throws KeyManagerException {
		XNMSSPrivateKeyManager km = XNMSS.getPrivateKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Private Key Manager configured for XNMSS Private Keys.");
		}
		return km.getCurrentOTSKey(this.getReference());
	}

	@Override
	public KeyList<XNMSSOTSPrivateKey> getOTSKeys() throws KeyManagerException {
		return _otsPrivateKeys;
	}

	@Override
	public KeyList<XNMSSOTSPrivateKey> getUsedOTSKeys() throws KeyManagerException {
		XNMSSPrivateKeyManager km = XNMSS.getPrivateKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Private Key Manager configured for XNMSS Private Keys.");
		}
		return km.getUsedOTSKeys(this.getReference());
	}

	@Override
	public boolean hasNextOTSKey() throws KeyManagerException {
		XNMSSPrivateKeyManager km = XNMSS.getPrivateKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Private Key Manager configured for XNMSS Private Keys.");
		}
		return true;
	}

	@Override
	public XNMSSOTSPrivateKey nextOTSKey(byte[] signedBytes) throws KeyManagerException {
		XNMSSPrivateKeyManager km = XNMSS.getPrivateKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Private Key Manager configured for XNMSS Private Keys.");
		}
		return km.nextOTSKey(this.getReference(), signedBytes);
	}

	@Override
	public XNMSSPrivateKey getRootKey() throws KeyManagerException {
		XNMSSPrivateKeyManager km = XNMSS.getPrivateKeyManager();
		if (km == null) {
			throw new KeyManagerException("No OTS Private Key Manager configured for XNMSS Private Keys.");
		}
		XNMSSPrivateKey key = this;
		while (key.isSubKey()) {
			key = km.getMetaKey(key.getParentOTSPublicKey().getMetaKeyReference());
		}
		return key;
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
		ASN1EncodableVector key = new ASN1EncodableVector();
		key.add(this.getASN1Parameters());
		key.add(new DEROctetString(this.getRoot()));
		ASN1EncodableVector otsPrivateKeys = new ASN1EncodableVector();
		_otsPrivateKeys.forEach((k) -> {
			otsPrivateKeys.add(new DEROctetString(k != null ? k.getEncoded() : new byte[0]));
		});
		byte[] otsPrivateKeysBytes;
		try {
			otsPrivateKeysBytes = new DERSequence(otsPrivateKeys).getEncoded();
			Deflater zip = new Deflater(9, false);
			zip.setInput(otsPrivateKeysBytes);
			zip.finish();
			ByteArrayOutputStream os = new ByteArrayOutputStream(otsPrivateKeysBytes.length);
			byte[] buf = new byte[1024];
			while (!zip.finished()) {
				os.write(buf, 0, zip.deflate(buf));
			}
			os.close();
			otsPrivateKeysBytes = os.toByteArray();
			key.add(new DEROctetString(otsPrivateKeysBytes));
		} catch (IOException ex) {
		}
		return new DERSequence(key);
	}

	@Override
	public String toString() {
		return "XNMSS Private Key '" + this.getReference() + "': " + this.getDigestIterations() + " x " + this.getDigestAlgorithm() + " with " + this.getOTSKeyCount() + " " + XNMSS.getOTSKeyAlgorithmForKeyAlgorithm(this.getAlgorithm()) + " OTS Keys.";
	}

}
