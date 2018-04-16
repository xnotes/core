/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss;

import org.xnotes.core.security.SecurityToolSet;
import org.xnotes.core.security.ots.jce.XNKeyStorePublicKeyProvider;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSOTSPublicKey;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSPublicKey;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSDefaultPublicKeyManager extends XNMSSPublicKeyManager {

	public XNMSSDefaultPublicKeyManager(SecurityToolSet sts, String dbPath) {
		super(new XNKeyStorePublicKeyProvider<XNMSSPublicKey, XNMSSOTSPublicKey>(sts.keyStore),
				new XNMSSJsonKeyInfoProvider(dbPath, XNMSSJsonKeyInfoProvider.EXT_PUBLIC));
	}

}
