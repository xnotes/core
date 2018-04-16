/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots;

import java.security.Key;
import java.util.Date;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public interface OTSKey extends Key {

	public String getMetaKeyReference();

	public int getOTSKeyCount();

	public int getIndex();

	public boolean isUsed() throws KeyManagerException;

	public Date getUsedTime() throws KeyManagerException;

	public byte[] getUsedHash() throws KeyManagerException;

}