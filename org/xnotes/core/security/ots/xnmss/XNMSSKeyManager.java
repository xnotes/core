/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss;

import java.util.Date;
import org.xnotes.core.security.ots.KeyList;
import org.xnotes.core.security.ots.KeyManager;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.security.ots.KeyProvider;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKey;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSOTSKey;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <M>
 * @param <O>
 * @param <P>
 * @param <I>
 */
public abstract class XNMSSKeyManager<M extends XNMSSKey<M, O>, O extends XNMSSOTSKey, P extends KeyProvider<M, O>, I extends XNMSSKeyInfoProvider<M, O>> implements KeyManager<M, O, P, I> {

	private final P _keyProvider;
	private final I _metaKeyInfoProvider;

	protected XNMSSKeyManager(P keyProvider, I metaKeyInfoProvider) {
		_keyProvider = keyProvider;
		_metaKeyInfoProvider = metaKeyInfoProvider;
	}

	protected P getKeyProvider() {
		return _keyProvider;
	}

	protected I getMetaKeyInfoProvider() {
		return _metaKeyInfoProvider;
	}

	@Override
	public boolean isManaged(String metaKeyReference) {
		return _metaKeyInfoProvider.isManaged(metaKeyReference);
	}

	@Override
	public void manage(M metaKey) throws KeyManagerException {
		_keyProvider.setMetaKey(metaKey);
		_metaKeyInfoProvider.manage(metaKey);
	}

	@Override
	public void unmanage(String metaKeyReference) throws KeyManagerException {
		_keyProvider.delete(metaKeyReference);
		_metaKeyInfoProvider.unmanage(metaKeyReference);
	}

	@Override
	public M getMetaKey(String metaKeyReference) throws KeyManagerException {
		try {
			return (M) this.getKeyProvider().getMetaKey(metaKeyReference);
		} catch (ClassCastException ex) {
			throw new KeyManagerException(ex);
		}
	}

	@Override
	public void setMetaKey(M metaKey) throws KeyManagerException {
		this.getKeyProvider().setMetaKey(metaKey);
	}

	@Override
	public KeyList<O> getOTSKeys(String metaKeyReference) throws KeyManagerException {
		return this.getKeyProvider().getOTSKeys(metaKeyReference);
	}

	@Override
	public O getOTSKey(String metaKeyReference, int index) throws KeyManagerException {
		return this.getKeyProvider().getOTSKey(metaKeyReference, index);
	}

	@Override
	public int getOTSKeyCount(String metaKeyReference) throws KeyManagerException {
		return this.getMetaKeyInfoProvider().getOTSKeyCount(metaKeyReference);
	}

	@Override
	public O getCurrentOTSKey(String metaKeyReference) throws KeyManagerException {
		while (this.getMetaKeyInfoProvider().getChildMetaKeyReference(metaKeyReference) != null) {
			metaKeyReference = this.getMetaKeyInfoProvider().getChildMetaKeyReference(metaKeyReference);
			if (!this.getMetaKeyInfoProvider().isManaged(metaKeyReference)) {
				this.getMetaKeyInfoProvider().manage(this.getKeyProvider().getMetaKey(metaKeyReference));
			}
		}
		int i = this.getMetaKeyInfoProvider().getCurrentOTSKeyIndex(metaKeyReference);
		if (i >= this.getOTSKeyCount(metaKeyReference)) {
			return null;
		} else if (i < 0) {
			i = 0;
			this.getMetaKeyInfoProvider().setCurrentOTSKeyIndex(metaKeyReference, i);
		}
		return this.getKeyProvider().getOTSKey(metaKeyReference, i);
	}

	@Override
	public boolean isUsed(String metaKeyReference, int index) throws KeyManagerException {
		return (index > -1 && index < this.getMetaKeyInfoProvider().getUsedOTSKeyInfos(metaKeyReference).size() - 1);
	}

	@Override
	public Date getUsedTime(String metaKeyReference, int index) throws KeyManagerException {
		if (this.isUsed(metaKeyReference, index)) {
			return this.getMetaKeyInfoProvider().getUsedOTSKeyInfos(metaKeyReference).get(index).getTime();
		} else {
			return null;
		}
	}

	@Override
	public byte[] getUsedHash(String metaKeyReference, int index) throws KeyManagerException {
		if (this.isUsed(metaKeyReference, index)) {
			return this.getMetaKeyInfoProvider().getUsedOTSKeyInfos(metaKeyReference).get(index).getHash();
		} else {
			return null;
		}
	}

	public M getChildMetaKey(String metaKeyReference) throws KeyManagerException {
		return this.getMetaKey(this.getMetaKeyInfoProvider().getChildMetaKeyReference(metaKeyReference));
	}

	public void setChildMetaKey(String metaKeyReference, String childMetaKeyReference) throws KeyManagerException {
		this.getMetaKeyInfoProvider().setChildMetaKeyReference(metaKeyReference, childMetaKeyReference);
	}

	public abstract M getRootKey(String metaKeyReference) throws KeyManagerException;

}
