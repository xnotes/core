/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots;

import java.security.KeyStore.Entry;
import java.security.PrivateKey;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <MPUB>
 * @param <OPUB>
 * @param <OPRV>
 */
public interface MetaPrivateKey<MPUB extends MetaPublicKey<OPUB>, OPRV extends OTSPrivateKey<OPUB>, OPUB extends OTSPublicKey> extends MetaKey<OPRV>, PrivateKey, Entry {
	
	public MPUB getPublicKey();

	public boolean hasNextOTSKey() throws KeyManagerException;

	public OPRV nextOTSKey(byte[] usedHash) throws KeyManagerException;

}