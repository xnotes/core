/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.jce;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.security.KeyStoreException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.xnotes.core.security.SecurityToolSet.XNKeyStore;
import org.xnotes.core.security.ots.KeyList;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.security.ots.MetaPublicKey;
import org.xnotes.core.security.ots.OTSPublicKey;
import org.xnotes.core.security.ots.PublicKeyProvider;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <MPUB>
 * @param <OPUB>
 */
public class XNKeyStorePublicKeyProvider<MPUB extends MetaPublicKey<OPUB>, OPUB extends OTSPublicKey> extends XNKeyStoreKeyProvider<MPUB, OPUB> implements PublicKeyProvider<MPUB, OPUB> {

	private final XNKeyStore _ks;
	private final LoadingCache<String, MPUB> _metaKeyCache = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new CacheLoader<String, MPUB>() {
				@Override
				public MPUB load(String metaKeyReference) throws KeyStoreException {
					return (MPUB) _ks.getPublicKey(metaKeyReference);
				}
			});
	private final LoadingCache<String, XNKeyStoreList<OPUB>> _otsKeyListCache = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new CacheLoader<String, XNKeyStoreList<OPUB>>() {
				@Override
				public XNKeyStoreList<OPUB> load(String metaKeyReference) throws ExecutionException {
					return new XNKeyStorePublicKeyList<>(
							_ks,
							metaKeyReference,
							_metaKeyCache.get(metaKeyReference).getOTSKeyCount());
				}
			});

	public XNKeyStorePublicKeyProvider(XNKeyStore keyStore) {
		_ks = keyStore;
	}

	@Override
	public XNKeyStore getKeyStore() {
		return _ks;
	}

	@Override
	protected LoadingCache<String, MPUB> getMetaKeyCache() {
		return _metaKeyCache;
	}

	@Override
	protected LoadingCache<String, XNKeyStoreList<OPUB>> getOTSKeyListCache() {
		return _otsKeyListCache;
	}

	@Override
	public MPUB getMetaKey(String metaKeyReference) throws KeyManagerException {
		try {
			return this.getMetaKeyCache().get(metaKeyReference);
		} catch (KeyManagerException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new KeyManagerException(ex);
		}
	}

	@Override
	public void setMetaKey(MPUB metaKey) throws KeyManagerException {
		try {
			this.getKeyStore().setPublicKey(metaKey.getReference(), metaKey);
			this.getMetaKeyCache().put(metaKey.getReference(), metaKey);
		} catch (KeyStoreException ex) {
			throw new KeyManagerException(ex);
		}
	}

	@Override
	public KeyList<OPUB> getOTSKeys(String metaKeyReference) throws KeyManagerException {
		try {
			return this.getOTSKeyListCache().get(metaKeyReference);
		} catch (KeyManagerException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new KeyManagerException(ex);
		}
	}

	@Override
	public void addOTSKey(OPUB publicKey) throws KeyManagerException {
		try {
			this.getKeyStore().setPublicKey(publicKey.getMetaKeyReference() + "_" + publicKey.getIndex(), publicKey);
			List<Integer> indexList = this.getOTSKeyListCache().get(publicKey.getMetaKeyReference()).getIndexList();
			if (!indexList.contains(publicKey.getIndex())) {
				indexList.add(publicKey.getIndex());
			}
		} catch (KeyStoreException | ExecutionException ex) {
			throw new KeyManagerException(ex);
		}
	}

}
