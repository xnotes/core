/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss;

import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKey;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSOTSKey;
import org.xnotes.core.security.ots.MetaKeyInfoProvider;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <M>
 * @param <O>
 */
public interface XNMSSKeyInfoProvider<M extends XNMSSKey<M, O>, O extends XNMSSOTSKey> extends MetaKeyInfoProvider<M, O> {

	public boolean beginBatch(String metaKeyReference);
	
	public boolean endBatch(String metaKeyReference);
	
	public void cancelBatch(String metaKeyReference);

	public String getChildMetaKeyReference(String metaKeyReference) throws KeyManagerException;

	public void setChildMetaKeyReference(String metaKeyReference, String childMetaKeyReference) throws KeyManagerException;

}
