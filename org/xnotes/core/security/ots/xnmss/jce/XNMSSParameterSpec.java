/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss.jce;

import java.security.spec.AlgorithmParameterSpec;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSParameterSpec implements AlgorithmParameterSpec {
	
	protected final int height;
	protected final int digestIterations;
	protected final int otsKeySize;
	protected final int otsKeyCount;
	protected final XNMSSOTSPrivateKey parentOTSPrivateKey;

	public XNMSSParameterSpec(int height, int digestIterations, int otsKeySize) {
		this(height, digestIterations, otsKeySize, null);
	}

	public XNMSSParameterSpec(int height, int digestIterations, int otsKeySize, XNMSSOTSPrivateKey parentOTSPrivateKey) {
		this.height = height;
		this.digestIterations = digestIterations;
		this.otsKeySize = otsKeySize;
		this.otsKeyCount = (int) Math.pow(2, height);
		this.parentOTSPrivateKey = parentOTSPrivateKey;
	}

	public int getHeight() {
		return height;
	}

	public int getDigestIterations() {
		return digestIterations;
	}

	public int getOTSKeySize() {
		return otsKeySize;
	}

	public int getOTSKeyCount() {
		return otsKeyCount;
	}

}
