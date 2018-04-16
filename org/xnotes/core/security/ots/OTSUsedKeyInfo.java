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
 */
public interface OTSUsedKeyInfo {
	
	public int getIndex();
	
	public Date getTime();
	
	public byte[] getHash();
	
}
