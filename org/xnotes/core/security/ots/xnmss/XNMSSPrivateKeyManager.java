/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Date;
import java.util.Iterator;
import org.xnotes.core.security.SecurityToolSet;
import org.xnotes.core.security.ots.KeyList;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.security.ots.PrivateKeyManager;
import org.xnotes.core.security.ots.PrivateKeyProvider;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSOTSPrivateKey;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSOTSPublicKey;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSParameterSpec;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSPrivateKey;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSPublicKey;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSPrivateKeyManager extends XNMSSKeyManager<XNMSSPrivateKey, XNMSSOTSPrivateKey, PrivateKeyProvider<XNMSSPrivateKey, XNMSSPublicKey, XNMSSOTSPrivateKey, XNMSSOTSPublicKey>, XNMSSKeyInfoProvider<XNMSSPrivateKey, XNMSSOTSPrivateKey>> implements PrivateKeyManager<XNMSSPrivateKey, XNMSSPublicKey, XNMSSOTSPrivateKey, XNMSSOTSPublicKey, PrivateKeyProvider<XNMSSPrivateKey, XNMSSPublicKey, XNMSSOTSPrivateKey, XNMSSOTSPublicKey>, XNMSSKeyInfoProvider<XNMSSPrivateKey, XNMSSOTSPrivateKey>> {
	
	protected XNMSSPrivateKeyManager(
			PrivateKeyProvider<XNMSSPrivateKey, XNMSSPublicKey, XNMSSOTSPrivateKey, XNMSSOTSPublicKey> keyProvider,
			XNMSSKeyInfoProvider<XNMSSPrivateKey, XNMSSOTSPrivateKey> metaKeyInfoProvider) {
		super(keyProvider, metaKeyInfoProvider);
	}
	
	@Override
	public void setOTSKeys(XNMSSOTSPrivateKey[] privateKeys) throws KeyManagerException {
		this.getKeyProvider().setOTSKeys(privateKeys);
	}

	@Override
	public KeyList<XNMSSOTSPrivateKey> getUsedOTSKeys(String metaKeyReference) throws KeyManagerException {
		return new KeyList<XNMSSOTSPrivateKey>() {
			@Override
			public int size() {
				return getMetaKeyInfoProvider().getUsedOTSKeyInfos(metaKeyReference).size();
			}

			@Override
			public XNMSSOTSPrivateKey get(int index) {
				if (index < 0 || index >= this.size()) {
					throw new IndexOutOfBoundsException("index: " + index + ", size: " + this.size());
				}
				XNMSSOTSPrivateKey key = getKeyProvider().getOTSKey(metaKeyReference, index);
				if (this.contains(key)) {
					return key;
				} else {
					return null;
				}
			}

			@Override
			public boolean contains(XNMSSOTSPrivateKey key) {
				return key.getIndex() >= 0 && key.getIndex() < this.size();
			}

			@Override
			public int indexOf(XNMSSOTSPrivateKey key) {
				if (this.contains(key)) {
					return key.getIndex();
				} else {
					return -1;
				}
			}

			@Override
			public Iterator<XNMSSOTSPrivateKey> iterator() {
				return new Iterator<XNMSSOTSPrivateKey>() {
					private int _i = -1;

					@Override
					public boolean hasNext() {
						return _i < size() - 1;
					}

					@Override
					public XNMSSOTSPrivateKey next() {
						if (_i < size() - 1) {
							_i++;
							if (_i >= size()) {
								throw new IndexOutOfBoundsException("index: " + _i + ", size: " + size());
							}
							return getKeyProvider().getOTSKey(metaKeyReference, _i);
						} else {
							return null;
						}
					}
				};
			}

		};
	}

	@Override
	public boolean hasNextOTSKey(String metaKeyReference) throws KeyManagerException {
		return true;
	}

	@Override
	public XNMSSOTSPrivateKey nextOTSKey(String metaKeyReference, byte[] usedHash) throws KeyManagerException {
		while (this.getMetaKeyInfoProvider().getChildMetaKeyReference(metaKeyReference) != null) {
			metaKeyReference = this.getMetaKeyInfoProvider().getChildMetaKeyReference(metaKeyReference);
			if (!this.getMetaKeyInfoProvider().isManaged(metaKeyReference)) {
				this.getMetaKeyInfoProvider().manage(this.getKeyProvider().getMetaKey(metaKeyReference));
			}
		}
		int i = this.getMetaKeyInfoProvider().getCurrentOTSKeyIndex(metaKeyReference);
		if (i == -1) {
			this.getMetaKeyInfoProvider().setCurrentOTSKeyIndex(metaKeyReference, 0);
			return this.getCurrentOTSKey(metaKeyReference);
		} else if (i < this.getOTSKeyCount(metaKeyReference)) {
			if (this.getUsedOTSKeys(metaKeyReference).size() != i) {
				throw new KeyManagerException("Private Key Manager is inconsistent.");
			}
			boolean endBatch = this.getMetaKeyInfoProvider().beginBatch(metaKeyReference);
			this.getMetaKeyInfoProvider().markUsed(metaKeyReference, i, new Date(), usedHash);
			i++;
			if (i < this.getOTSKeyCount(metaKeyReference)) {
				this.getMetaKeyInfoProvider().setCurrentOTSKeyIndex(metaKeyReference, i);
				if (i < this.getOTSKeyCount(metaKeyReference) - 1) {
					if (endBatch) {
						this.getMetaKeyInfoProvider().endBatch(metaKeyReference);
					}
					return this.getCurrentOTSKey(metaKeyReference);
				} else {
					try {
						XNMSSOTSPrivateKey currentOTSPrivateKey = super.getCurrentOTSKey(metaKeyReference);
						KeyPairGenerator kgen = KeyPairGenerator.getInstance(currentOTSPrivateKey.getMetaKeyAlgorithm());
						kgen.initialize(
								new XNMSSParameterSpec(
										currentOTSPrivateKey.getHeight(),
										currentOTSPrivateKey.getDigestIterations(),
										SecurityToolSet.getKeySize(currentOTSPrivateKey.getSignaturePublicKey()),
										currentOTSPrivateKey),
								currentOTSPrivateKey.getSecureRandom());
						KeyPair kp = kgen.generateKeyPair();
						XNMSSPrivateKey newManagedPrivateKey = (XNMSSPrivateKey) kp.getPrivate();
						this.manage(newManagedPrivateKey);
						this.getMetaKeyInfoProvider().setChildMetaKeyReference(metaKeyReference, newManagedPrivateKey.getReference());
						if (endBatch) {
							this.getMetaKeyInfoProvider().endBatch(metaKeyReference);
						}
						return newManagedPrivateKey.getCurrentOTSKey();
					} catch (Throwable ex) {
						if (endBatch) {
							this.getMetaKeyInfoProvider().cancelBatch(metaKeyReference);
						}
						throw new KeyManagerException(ex);
					}
				}
			} else {
				this.getMetaKeyInfoProvider().setCurrentOTSKeyIndex(metaKeyReference, i);
				if (endBatch) {
					this.getMetaKeyInfoProvider().endBatch(metaKeyReference);
				}
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public XNMSSPrivateKey getRootKey(String metaKeyReference) throws KeyManagerException {
		XNMSSPrivateKey key = this.getKeyProvider().getMetaKey(metaKeyReference);
		while (key.isSubKey()) {
			key = this.getMetaKey(key.getParentOTSPublicKey().getMetaKeyReference());
		}
		return key;
	}
}
