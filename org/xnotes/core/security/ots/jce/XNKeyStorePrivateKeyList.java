/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.jce;

import java.security.KeyStoreException;
import java.util.Iterator;
import org.xnotes.core.security.SecurityToolSet.XNKeyStore;
import org.xnotes.core.security.ots.KeyPassProvider;
import org.xnotes.core.security.ots.OTSPrivateKey;
import org.xnotes.core.security.ots.OTSPublicKey;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <OPRV>
 * @param <OPUB>
 */
public class XNKeyStorePrivateKeyList<OPRV extends OTSPrivateKey<OPUB>, OPUB extends OTSPublicKey> extends XNKeyStoreList<OPRV> {

	private final KeyPassProvider _kpds;

	public XNKeyStorePrivateKeyList(XNKeyStore keyStore, String metaKeyReference, int size, KeyPassProvider keyPassProvider) {
		super(keyStore, metaKeyReference, size);
		_kpds = keyPassProvider;
	}

	@Override
	public OPRV get(int index) {
		if (index < 0 || index >= this.size()) {
			throw new IndexOutOfBoundsException("index: " + index + ", size: " + this.size());
		}
		try {
			return (OPRV) this.getKeyStore().getPrivateKey(this.getMetaKeyReference() + "_" + index, _kpds.getOTSKeyPass(this.getMetaKeyReference(), index));
		} catch (KeyStoreException | ClassCastException ex) {
			return null;
		}
	}

	@Override
	public int indexOf(OPRV key) {
		return key.getIndex();
	}

	@Override
	public Iterator<OPRV> iterator() {
		return new Iterator<OPRV>() {
			private int _i = -1;

			@Override
			public boolean hasNext() {
				return _i < size() - 1;
			}

			@Override
			public OPRV next() {
				_i++;
				if (_i >= size()) {
					throw new IndexOutOfBoundsException("index: " + _i + ", size: " + size());
				}
				try {
					return (OPRV) getKeyStore().getPrivateKey(getMetaKeyReference() + "_" + _i, _kpds.getOTSKeyPass(getMetaKeyReference(), _i));
				} catch (KeyStoreException | ClassCastException ex) {
					return null;
				}
			}
		};
	}
}
