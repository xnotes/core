/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <MPUB>
 * @param <OPUB>
 */
public interface PublicKeyProvider<MPUB extends MetaPublicKey<OPUB>, OPUB extends OTSPublicKey> extends KeyProvider<MPUB, OPUB> {
	
	public void addOTSKey(OPUB publicKey) throws KeyManagerException;

}
