/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss;

import org.xnotes.core.security.SecurityToolSet;
import org.xnotes.core.security.ots.JsonAutoKeyPassProvider;
import org.xnotes.core.security.ots.jce.XNKeyStorePrivateKeyProvider;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSOTSPrivateKey;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSOTSPublicKey;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSPrivateKey;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSPublicKey;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSDefaultPrivateKeyManager extends XNMSSPrivateKeyManager {

	public XNMSSDefaultPrivateKeyManager(SecurityToolSet securityToolSet, String dbPath, String keyPassDBPath, XNMSSPublicKeyManager publicKeyManager) {
		super(new XNKeyStorePrivateKeyProvider<XNMSSPrivateKey, XNMSSPublicKey, XNMSSOTSPrivateKey, XNMSSOTSPublicKey>(
				securityToolSet.keyStore,
				new JsonAutoKeyPassProvider(securityToolSet, keyPassDBPath),
				publicKeyManager.getKeyProvider()),
				new XNMSSJsonKeyInfoProvider(dbPath, XNMSSJsonKeyInfoProvider.EXT_PRIVATE));
	}

}
