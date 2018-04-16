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
import java.util.concurrent.TimeUnit;
import org.xnotes.ConfigurationException;
import org.xnotes.core.security.SecurityToolSet.XNKeyStore;
import org.xnotes.core.security.ots.KeyList;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.security.ots.KeyPassProvider;
import org.xnotes.core.security.ots.MetaPrivateKey;
import org.xnotes.core.security.ots.MetaPublicKey;
import org.xnotes.core.security.ots.OTSPrivateKey;
import org.xnotes.core.security.ots.OTSPublicKey;
import org.xnotes.core.security.ots.PrivateKeyProvider;
import org.xnotes.core.security.ots.PublicKeyProvider;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <MPRV>
 * @param <MPUB>
 * @param <OPRV>
 * @param <OPUB>
 */
public class XNKeyStorePrivateKeyProvider<MPRV extends MetaPrivateKey<MPUB, OPRV, OPUB>, MPUB extends MetaPublicKey<OPUB>, OPRV extends OTSPrivateKey<OPUB>, OPUB extends OTSPublicKey> extends XNKeyStoreKeyProvider<MPRV, OPRV> implements PrivateKeyProvider<MPRV, MPUB, OPRV, OPUB> {

	private final XNKeyStore _ks;
	private final KeyPassProvider _kpds;
	private final PublicKeyProvider<MPUB, OPUB> _pubKeyProvider;
	private final LoadingCache<String, MPRV> _metaKeyCache = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new CacheLoader<String, MPRV>() {
				@Override
				public MPRV load(String metaKeyReference) throws KeyStoreException {
					return (MPRV) _ks.getPrivateKey(metaKeyReference, _kpds.getMetaKeyPass(metaKeyReference));
				}
			});
	private final LoadingCache<String, XNKeyStoreList<OPRV>> _otsKeyListCache = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new CacheLoader<String, XNKeyStoreList<OPRV>>() {
				@Override
				public XNKeyStoreList<OPRV> load(String metaKeyReference) {
					return new XNKeyStorePrivateKeyList<>(
							_ks,
							metaKeyReference,
							_pubKeyProvider.getOTSKey(metaKeyReference, 0).getOTSKeyCount(),
							_kpds);
				}
			});

	public XNKeyStorePrivateKeyProvider(XNKeyStore keyStore, KeyPassProvider keyPassProvider, PublicKeyProvider<MPUB, OPUB> publicKeyProvider) {
		_ks = keyStore;
		_kpds = keyPassProvider;
		_pubKeyProvider = publicKeyProvider;
	}

	@Override
	public XNKeyStore getKeyStore() {
		return _ks;
	}

	@Override
	public final KeyPassProvider getKeyPassProvider() {
		return _kpds;
	}

	@Override
	public PublicKeyProvider<MPUB, OPUB> getPublicKeyProvider() {
		return _pubKeyProvider;
	}

	@Override
	protected LoadingCache<String, MPRV> getMetaKeyCache() {
		return _metaKeyCache;
	}

	@Override
	protected LoadingCache<String, XNKeyStoreList<OPRV>> getOTSKeyListCache() {
		return _otsKeyListCache;
	}

	@Override
	public MPRV getMetaKey(String metaKeyReference) throws KeyManagerException {
		try {
			return this.getMetaKeyCache().get(metaKeyReference);
		} catch (KeyManagerException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new KeyManagerException(ex);
		}
	}

	@Override
	public void setMetaKey(MPRV metaKey) throws KeyManagerException {
		try {
			this.getKeyStore().setPrivateKey(metaKey.getReference(), metaKey, metaKey.getPublicKey(), this.getKeyPassProvider().getMetaKeyPass(metaKey.getReference()));
			this.getMetaKeyCache().put(metaKey.getReference(), metaKey);
		} catch (KeyStoreException ex) {
			throw new KeyManagerException(ex);
		}
	}

	@Override
	public KeyList<OPRV> getOTSKeys(String metaKeyReference) throws KeyManagerException {
		try {
			return this.getOTSKeyListCache().get(metaKeyReference);
		} catch (KeyManagerException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new KeyManagerException(ex);
		}
	}

	@Override
	public void setOTSKeys(OPRV[] privateKeys) throws KeyManagerException {
		for (OPRV privateKey : privateKeys) {
			try {
				this.getKeyStore().setPrivateKey(
						privateKey.getMetaKeyReference() + "_" + privateKey.getIndex(),
						privateKey,
						privateKey.getPublicKey(),
						this.getKeyPassProvider().getOTSKeyPass(privateKey.getMetaKeyReference(), privateKey.getIndex()),
						false);
			} catch (KeyStoreException ex) {
				throw new KeyManagerException(ex);
			}
		}
		try {
			this.getKeyStore().save();
			if (privateKeys.length > 0) {
				this.getOTSKeyListCache().put(privateKeys[0].getMetaKeyReference(), new XNKeyStorePrivateKeyList<>(this.getKeyStore(), privateKeys[0].getMetaKeyReference(), privateKeys[0].getOTSKeyCount(), _kpds));
			}
		} catch (ConfigurationException ex) {
			throw new KeyManagerException(ex);
		}
	}

}
