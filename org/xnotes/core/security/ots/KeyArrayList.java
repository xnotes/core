/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <O>
 */
public class KeyArrayList<O extends OTSKey> implements KeyList<O> {
	
	private final ArrayList<O> _a = new ArrayList<>();
	
	public KeyArrayList(Collection<O> c) {
		_a.addAll(c);
	}

	public KeyArrayList(O[] a) {
		_a.addAll(Arrays.asList(a));
	}

	@Override
	public int size() {
		return _a.size();
	}

	@Override
	public boolean isEmpty() {
		return _a.isEmpty();
	}

	@Override
	public boolean contains(O key) {
		return _a.contains(key);
	}

	@Override
	public O get(int index) {
		return _a.get(index);
	}

	@Override
	public int indexOf(O key) {
		return _a.indexOf(key);
	}

	@Override
	public Iterator<O> iterator() {
		return _a.iterator();
	}

}
