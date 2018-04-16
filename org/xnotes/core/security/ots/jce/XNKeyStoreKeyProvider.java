/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.jce;

import com.google.common.cache.LoadingCache;
import java.security.KeyStoreException;
import org.xnotes.core.security.SecurityToolSet.XNKeyStore;
import org.xnotes.core.security.ots.KeyList;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.security.ots.MetaKey;
import org.xnotes.core.security.ots.OTSKey;
import org.xnotes.core.security.ots.KeyProvider;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <M>
 * @param <O>
 */
public abstract class XNKeyStoreKeyProvider<M extends MetaKey<O>, O extends OTSKey> implements KeyProvider<M, O> {

//	protected final Map<String, M> _metaKeyCache = new HashMap<>();
//	protected final LoadingCache<String, M> metaKeyCache;
//	private final LoadingCache<String, M> _metaKeyCache = CacheBuilder.newBuilder()
//			.maximumSize(1000)
//			.expireAfterAccess(10, TimeUnit.MINUTES)
//			.build(new CacheLoader<String, M>() {
//				@Override
//				public M load(String metaKeyReference) throws KeyManagerException {
//					try {
//						JsonMetaKeyInfo info = JSON.parseFile(_dbPath + metaKeyReference + (_ext != null ? "." + _ext : ""), JsonMetaKeyInfo.class, null, true);
//						if (info == null) {
//							throw new KeyManagerException("MetaKey referenced '" + metaKeyReference + "' is NOT managed.");
//						}
//						return info;
//					} catch (IOException ex) {
//						throw new KeyManagerException(ex);
//					}
//				}
//			});

	protected abstract LoadingCache<String, M> getMetaKeyCache();

	protected abstract LoadingCache<String, XNKeyStoreList<O>> getOTSKeyListCache();
	
	public abstract XNKeyStore getKeyStore();

	@Override
	public O getOTSKey(String metaKeyReference, int index) throws KeyManagerException {
		KeyList<O> keyList = this.getOTSKeys(metaKeyReference);
		if (keyList != null) {
			return keyList.get(index);
		} else {
			throw new KeyManagerException("Could not get OTS Key List for Meta Key Reference '" + metaKeyReference + "'.");
		}
	}

	@Override
	public void delete(String metaKeyReference) throws KeyManagerException {
		try {
			if (this.getKeyStore().contains(metaKeyReference)) {
				this.getKeyStore().delete(metaKeyReference);
				M metaKey = this.getMetaKey(metaKeyReference);
				for (int i = 0; i < metaKey.getOTSKeyCount(); i++) {
					this.getKeyStore().delete(metaKeyReference + "_" + i);
				}
				this.getOTSKeyListCache().invalidate(metaKeyReference);
			}
		} catch (KeyStoreException ex) {
			throw new KeyManagerException(ex);
		}
	}

}
