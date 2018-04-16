/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.jce;

import java.util.List;
import org.xnotes.core.security.SecurityToolSet.XNKeyStore;
import org.xnotes.core.security.ots.KeyList;
import org.xnotes.core.security.ots.OTSKey;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <O>
 */
public abstract class XNKeyStoreList<O extends OTSKey> implements KeyList<O> {

	private final XNKeyStore _ks;
	private final String _metaKeyReference;
	private final int _size;
	private final List<Integer> _indexList;

	public XNKeyStoreList(XNKeyStore keyStore, String metaKeyReference, int size) {
		_ks = keyStore;
		_metaKeyReference = metaKeyReference;
		_size = size;
		_indexList = null;
	}

	public XNKeyStoreList(XNKeyStore keyStore, String metaKeyReference, List<Integer> index) {
		_ks = keyStore;
		_metaKeyReference = metaKeyReference;
		_indexList = index;
		_size = _indexList.size();
	}

	public XNKeyStore getKeyStore() {
		return _ks;
	}

	public String getMetaKeyReference() {
		return _metaKeyReference;
	}
	
	public List<Integer> getIndexList() {
		return _indexList;
	}

	@Override
	public int size() {
		return _size;
	}

	@Override
	public boolean isEmpty() {
		return _size == 0;
	}

	@Override
	public boolean contains(O key) {
		if (_indexList == null) {
			return key.getIndex() > -1 && key.getIndex() < _size;
		} else {
			return _indexList.stream().anyMatch((i) -> (i == key.getIndex()));
		}
	}

}
