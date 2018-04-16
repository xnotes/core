/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots;

import java.util.Date;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <M>
 * @param <O>
 * @param <P>
 * @param <I>
 */
public interface KeyManager<M extends MetaKey<O>, O extends OTSKey, P extends KeyProvider<M, O>, I extends MetaKeyInfoProvider<M, O>> {

	public boolean isManaged(String metaKeyReference);

	public void manage(M metaKey) throws KeyManagerException;

	public void unmanage(String metaKeyReference) throws KeyManagerException;

	public M getMetaKey(String metaKeyReference) throws KeyManagerException;
	
	public void setMetaKey(M metaKey) throws KeyManagerException;

	public KeyList<O> getOTSKeys(String metaKeyReference) throws KeyManagerException;

	public O getOTSKey(String metaKeyReference, int index) throws KeyManagerException;

	public int getOTSKeyCount(String metaKeyReference) throws KeyManagerException;

	public O getCurrentOTSKey(String metaKeyReference) throws KeyManagerException;

	public boolean isUsed(String metaKeyReference, int index) throws KeyManagerException;

	public Date getUsedTime(String metaKeyReference, int index) throws KeyManagerException;

	public byte[] getUsedHash(String metaKeyReference, int index) throws KeyManagerException;

	public KeyList<O> getUsedOTSKeys(String metaKeyReference) throws KeyManagerException;
	
}
