/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.xnotes.core.security.ots.KeyList;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.security.ots.PublicKeyManager;
import org.xnotes.core.security.ots.PublicKeyProvider;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSOTSPublicKey;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSPublicKey;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSPublicKeyManager extends XNMSSKeyManager<XNMSSPublicKey, XNMSSOTSPublicKey, PublicKeyProvider<XNMSSPublicKey, XNMSSOTSPublicKey>, XNMSSKeyInfoProvider<XNMSSPublicKey, XNMSSOTSPublicKey>> implements PublicKeyManager<XNMSSPublicKey, XNMSSOTSPublicKey, PublicKeyProvider<XNMSSPublicKey, XNMSSOTSPublicKey>, XNMSSKeyInfoProvider<XNMSSPublicKey, XNMSSOTSPublicKey>> {
	
	public XNMSSPublicKeyManager(
			PublicKeyProvider<XNMSSPublicKey, XNMSSOTSPublicKey> keyProvider,
			XNMSSKeyInfoProvider<XNMSSPublicKey, XNMSSOTSPublicKey> metaKeyInfoProvider) {
		super(keyProvider, metaKeyInfoProvider);
	}

	@Override
	public void addOTSKey(XNMSSOTSPublicKey publicKey) throws KeyManagerException {
		this.getKeyProvider().addOTSKey(publicKey);
	}

	@Override
	public void markUsed(String metaKeyReference, int index, Date time, byte[] dataHash) throws KeyManagerException {
		this.getMetaKeyInfoProvider().markUsed(metaKeyReference, index, time, dataHash);
		this.getMetaKeyInfoProvider().setCurrentOTSKeyIndex(metaKeyReference, index);
	}

	@Override
	public KeyList<XNMSSOTSPublicKey> getUsedOTSKeys(String metaKeyReference) throws KeyManagerException {
		
		List<Integer> indexList = new ArrayList<>();
		getMetaKeyInfoProvider().getUsedOTSKeyInfos(metaKeyReference).forEach((oki) -> {
			indexList.add(oki.getIndex());
		});
		
		return new KeyList<XNMSSOTSPublicKey>() {
			@Override
			public int size() {
				return indexList.size();
			}

			@Override
			public XNMSSOTSPublicKey get(int index) {
				if (index < 0 || index >= this.size()) {
					throw new IndexOutOfBoundsException("index: " + index + ", size: " + this.size());
				}
				XNMSSOTSPublicKey key = getKeyProvider().getOTSKey(metaKeyReference, indexList.get(index));
				if (this.contains(key)) {
					return key;
				} else {
					return null;
				}
			}

			@Override
			public boolean contains(XNMSSOTSPublicKey key) {
				return indexList.stream().anyMatch((i) -> (i == key.getIndex()));
			}

			@Override
			public int indexOf(XNMSSOTSPublicKey key) {
				for (int i=0;i<this.size();i++) {
					if (this.get(i).equals(key)) {
						return i;
					}
				}
				return -1;
			}

			@Override
			public Iterator<XNMSSOTSPublicKey> iterator() {
				return new Iterator<XNMSSOTSPublicKey>() {
					private int _i = -1;

					@Override
					public boolean hasNext() {
						return _i < size() - 1;
					}

					@Override
					public XNMSSOTSPublicKey next() {
						if (_i < size() - 1) {
							_i++;
							if (_i >= size()) {
								throw new IndexOutOfBoundsException("index: " + _i + ", size: " + size());
							}
							return getKeyProvider().getOTSKey(metaKeyReference, indexList.get(_i));
						} else {
							return null;
						}
					}
				};
			}

		};
	}

	@Override
	public void setCurrentOTSKey(String metaKeyReference, int index) throws KeyManagerException {
		this.getMetaKeyInfoProvider().setCurrentOTSKeyIndex(metaKeyReference, index);
	}

	@Override
	public XNMSSPublicKey getRootKey(String metaKeyReference) throws KeyManagerException {
		XNMSSPublicKey key = this.getKeyProvider().getMetaKey(metaKeyReference);
		while (key.isSubKey()) {
			key = this.getMetaKey(key.getParentOTSPublicKey().getMetaKeyReference());
		}
		return key;
	}
}
