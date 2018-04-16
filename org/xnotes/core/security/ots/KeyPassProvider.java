/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public interface KeyPassProvider {
	
	public String getMetaKeyPass(String metaKeyReference) throws KeyManagerException;

	public String getOTSKeyPass(String metaKeyReference, int index) throws KeyManagerException;

}
