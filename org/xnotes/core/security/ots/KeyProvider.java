/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <M>
 * @param <O>
 */
public interface KeyProvider<M extends MetaKey<O>, O extends OTSKey> {
	
	public M getMetaKey(String metaKeyReference) throws KeyManagerException;
	
	public void setMetaKey(M metaKey) throws KeyManagerException;

	public void delete(String metaKeyReference) throws KeyManagerException;

	public KeyList<O> getOTSKeys(String metaKeyReference) throws KeyManagerException;

	public O getOTSKey(String metaKeyReference, int index) throws KeyManagerException;
	
}
