/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots;

import java.util.Date;
import java.util.List;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <M>
 * @param <O>
 */
public interface MetaKeyInfoProvider<M extends MetaKey<O>, O extends OTSKey> {
	
	public boolean isManaged(String metaKeyReference);

	public void manage(M metaKey) throws KeyManagerException;

	public void unmanage(String metaKeyReference) throws KeyManagerException;

	public int getOTSKeyCount(String metaKeyReference) throws KeyManagerException;

	public int getCurrentOTSKeyIndex(String metaKeyReference) throws KeyManagerException;

	public void setCurrentOTSKeyIndex(String metaKeyReference, int index) throws KeyManagerException;

	public boolean isUsed(String metaKeyReference, int index) throws KeyManagerException;

	public void markUsed(String metaKeyReference, int index, Date time, byte[] dataHash) throws KeyManagerException;

	public List<OTSUsedKeyInfo> getUsedOTSKeyInfos(String metaKeyReference) throws KeyManagerException;

}
