/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.utils;

import java.util.Iterator;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <T>
 */
public class ArrayIterator<T> implements Iterator<T> {

	private final T[] _array;
	private int _i;

	public ArrayIterator(T[] array) {
		_array = array;
		_i = -1;
	}

	@Override
	public boolean hasNext() {
		return _i + 1 < _array.length;
	}

	@Override
	public T next() {
		if (this.hasNext()) {
			_i++;
			return _array[_i];
		} else {
			return null;
		}
	}

}
