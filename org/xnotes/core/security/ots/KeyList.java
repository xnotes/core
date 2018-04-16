/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots;

import java.security.Key;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <K>
 */
public interface KeyList<K extends Key> extends Iterable<K> {

	public int size();

	default public boolean isEmpty() {
		return this.size() == 0;
	}

	public boolean contains(K key);

	public K get(int index);

	public int indexOf(K key);

}
