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
 */
public interface PrivateKeyProvider<MPRV extends MetaPrivateKey<MPUB, OPRV, OPUB>, MPUB extends MetaPublicKey<OPUB>, OPRV extends OTSPrivateKey<OPUB>, OPUB extends OTSPublicKey> extends KeyProvider<MPRV, OPRV> {
	
	public KeyPassProvider getKeyPassProvider();

	public PublicKeyProvider<MPUB, OPUB> getPublicKeyProvider();

	public void setOTSKeys(OPRV[] privateKeys) throws KeyManagerException;
	
}
