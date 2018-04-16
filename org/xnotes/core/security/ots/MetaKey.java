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
 * @param <O>
 */
public interface MetaKey<O extends OTSKey> extends Key {

	public String getReference();

	public int getOTSKeyCount();

	public KeyList<O> getOTSKeys() throws KeyManagerException;

	public KeyList<O> getUsedOTSKeys() throws KeyManagerException;

	public O getCurrentOTSKey() throws KeyManagerException;

}