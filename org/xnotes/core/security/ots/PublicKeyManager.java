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
 * @param <MPUB>
 * @param <OPUB>
 * @param <P>
 * @param <I>
 */
public interface PublicKeyManager<MPUB extends MetaPublicKey<OPUB>, OPUB extends OTSPublicKey, P extends PublicKeyProvider<MPUB, OPUB>, I extends MetaKeyInfoProvider<MPUB, OPUB>> extends KeyManager<MPUB, OPUB, P, I> {
	
	public void addOTSKey(OPUB publicKey) throws KeyManagerException;

	public void markUsed(String metaKeyReference, int index, Date time, byte[] dataHash) throws KeyManagerException;

	public void setCurrentOTSKey(String metaKeyReference, int index) throws KeyManagerException;

}
