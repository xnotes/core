/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.jce;

import java.security.KeyStoreException;
import java.util.Iterator;
import java.util.List;
import org.xnotes.core.security.SecurityToolSet.XNKeyStore;
import org.xnotes.core.security.ots.OTSPublicKey;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <O>
 */
public class XNKeyStorePublicKeyList<O extends OTSPublicKey> extends XNKeyStoreList<O> {

	public XNKeyStorePublicKeyList(XNKeyStore keyStore, String metaKeyReference, int size) {
		super(keyStore, metaKeyReference, size);
	}

	public XNKeyStorePublicKeyList(XNKeyStore keyStore, String metaKeyReference, List<Integer> index) {
		super(keyStore, metaKeyReference, index);
	}

	@Override
	public O get(int index) {
		if (index < 0 || index >= this.size()) {
			throw new IndexOutOfBoundsException("index: " + index + ", size: " + this.size());
		}
		try {
			return (O) this.getKeyStore().getPublicKey(this.getMetaKeyReference() + "_" + index);
		} catch (KeyStoreException | ClassCastException ex) {
			return null;
		}
	}

	@Override
	public int indexOf(OTSPublicKey key) {
		for (int i=0;i<this.size();i++) {
			if (this.get(i).equals(key)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Iterator<O> iterator() {
		return new Iterator<O>() {
			private int _i = -1;

			@Override
			public boolean hasNext() {
				return _i < size() - 1;
			}

			@Override
			public O next() {
				_i++;
				if (_i >= size()) {
					throw new IndexOutOfBoundsException("index: " + _i + ", size: " + size());
				}
				try {
					int idx = getIndexList() != null ? getIndexList().get(_i) : _i;
					return (O) getKeyStore().getPublicKey(getMetaKeyReference() + "_" + idx);
				} catch (KeyStoreException | ClassCastException ex) {
					return null;
				}
			}
		};
	}
}
