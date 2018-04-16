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
 * @param <OPUB>
 */
public interface OTSPrivateKey<OPUB extends OTSPublicKey> extends OTSKey, PrivateKey, Entry {
	
	public OPUB getPublicKey();

}