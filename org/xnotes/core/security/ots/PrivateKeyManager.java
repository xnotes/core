/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <MPRV>
 * @param <MPUB>
 * @param <OPRV>
 * @param <OPUB>
 * @param <P>
 * @param <I>
 */
public interface PrivateKeyManager<MPRV extends MetaPrivateKey<MPUB, OPRV, OPUB>, MPUB extends MetaPublicKey<OPUB>, OPRV extends OTSPrivateKey<OPUB>, OPUB extends OTSPublicKey, P extends PrivateKeyProvider<MPRV, MPUB, OPRV, OPUB>, I extends MetaKeyInfoProvider<MPRV, OPRV>> extends KeyManager<MPRV, OPRV, P, I> {

	public void setOTSKeys(OPRV[] privateKeys) throws KeyManagerException;

	public boolean hasNextOTSKey(String metaKeyReference) throws KeyManagerException;

	public OPRV nextOTSKey(String metaKeyReference, byte[] usedHash) throws KeyManagerException;

}
