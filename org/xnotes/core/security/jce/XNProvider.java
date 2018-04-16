/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.jce;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import org.xnotes.XNotes;
import org.xnotes.core.security.ots.xnmss.XNMSS;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyFactory;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA1AndDSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA1AndECKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA1AndRSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA224AndDSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA224AndECKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA224AndRSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA256AndDSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA256AndECKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA256AndRSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA384AndDSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA384AndECKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA384AndRSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA3_224AndDSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA3_224AndECKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA3_224AndRSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA3_256AndDSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA3_256AndECKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA3_256AndRSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA3_384AndDSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA3_384AndECKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA3_384AndRSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA3_512AndDSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA3_512AndECKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA3_512AndRSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA512AndDSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA512AndECKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKeyPairGenerator.XNMSSwithSHA512AndRSAKeyPairGenerator;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSOTSKeyFactory;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSignature;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public final class XNProvider extends Provider {

	private static final double _version = 1.0;

	private static final String _info = "XNotes Security Provider v" + XNotes.VERSION;

	public static final String PROVIDER_NAME = "XN";

	public XNProvider() {
		super(PROVIDER_NAME, _version, _info);
		AccessController.doPrivileged((PrivilegedAction) () -> {
			_setup();
			return null;
		});
	}

	private void _setup() {
		this.put("KeyPairGenerator.XNMSSwithSHA1AndEC", XNMSSwithSHA1AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA224AndEC", XNMSSwithSHA224AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA256AndEC", XNMSSwithSHA256AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA384AndEC", XNMSSwithSHA384AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA512AndEC", XNMSSwithSHA512AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA3-224AndEC", XNMSSwithSHA3_224AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA3-256AndEC", XNMSSwithSHA3_256AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA3-384AndEC", XNMSSwithSHA3_384AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA3-512AndEC", XNMSSwithSHA3_512AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA1AndRSA", XNMSSwithSHA1AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA224AndRSA", XNMSSwithSHA224AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA256AndRSA", XNMSSwithSHA256AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA384AndRSA", XNMSSwithSHA384AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA512AndRSA", XNMSSwithSHA512AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA3-224AndRSA", XNMSSwithSHA3_224AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA3-256AndRSA", XNMSSwithSHA3_256AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA3-384AndRSA", XNMSSwithSHA3_384AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA3-512AndRSA", XNMSSwithSHA3_512AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA1AndDSA", XNMSSwithSHA1AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA224AndDSA", XNMSSwithSHA224AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA256AndDSA", XNMSSwithSHA256AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA384AndDSA", XNMSSwithSHA384AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA512AndDSA", XNMSSwithSHA512AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA3-224AndDSA", XNMSSwithSHA3_224AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA3-256AndDSA", XNMSSwithSHA3_256AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA3-384AndDSA", XNMSSwithSHA3_384AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator.XNMSSwithSHA3-512AndDSA", XNMSSwithSHA3_512AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA1AndEC.getAlgorithm().toString(), XNMSSwithSHA1AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA224AndEC.getAlgorithm().toString(), XNMSSwithSHA224AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA256AndEC.getAlgorithm().toString(), XNMSSwithSHA256AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA384AndEC.getAlgorithm().toString(), XNMSSwithSHA384AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA512AndEC.getAlgorithm().toString(), XNMSSwithSHA512AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_224AndEC.getAlgorithm().toString(), XNMSSwithSHA3_224AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_256AndEC.getAlgorithm().toString(), XNMSSwithSHA3_256AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_384AndEC.getAlgorithm().toString(), XNMSSwithSHA3_384AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_512AndEC.getAlgorithm().toString(), XNMSSwithSHA3_512AndECKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA1AndRSA.getAlgorithm().toString(), XNMSSwithSHA1AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA224AndRSA.getAlgorithm().toString(), XNMSSwithSHA224AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA256AndRSA.getAlgorithm().toString(), XNMSSwithSHA256AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA384AndRSA.getAlgorithm().toString(), XNMSSwithSHA384AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA512AndRSA.getAlgorithm().toString(), XNMSSwithSHA512AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_224AndRSA.getAlgorithm().toString(), XNMSSwithSHA3_224AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_256AndRSA.getAlgorithm().toString(), XNMSSwithSHA3_256AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_384AndRSA.getAlgorithm().toString(), XNMSSwithSHA3_384AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_512AndRSA.getAlgorithm().toString(), XNMSSwithSHA3_512AndRSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA1AndDSA.getAlgorithm().toString(), XNMSSwithSHA1AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA224AndDSA.getAlgorithm().toString(), XNMSSwithSHA224AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA256AndDSA.getAlgorithm().toString(), XNMSSwithSHA256AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA384AndDSA.getAlgorithm().toString(), XNMSSwithSHA384AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA512AndDSA.getAlgorithm().toString(), XNMSSwithSHA512AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_224AndDSA.getAlgorithm().toString(), XNMSSwithSHA3_224AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_256AndDSA.getAlgorithm().toString(), XNMSSwithSHA3_256AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_384AndDSA.getAlgorithm().toString(), XNMSSwithSHA3_384AndDSAKeyPairGenerator.class.getName());
		this.put("KeyPairGenerator." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_512AndDSA.getAlgorithm().toString(), XNMSSwithSHA3_512AndDSAKeyPairGenerator.class.getName());

		this.put("KeyFactory.XNMSSwithSHA1AndEC", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA224AndEC", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA256AndEC", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA384AndEC", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA512AndEC", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA3-224AndEC", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA3-256AndEC", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA3-384AndEC", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA3-512AndEC", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA1AndRSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA224AndRSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA256AndRSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA384AndRSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA512AndRSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA3-224AndRSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA3-256AndRSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA3-384AndRSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA3-512AndRSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA1AndDSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA224AndDSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA256AndDSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA384AndDSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA512AndDSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA3-224AndDSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA3-256AndDSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA3-384AndDSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSwithSHA3-512AndDSA", XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA1AndEC.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA224AndEC.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA256AndEC.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA384AndEC.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA512AndEC.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_224AndEC.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_256AndEC.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_384AndEC.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_512AndEC.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA1AndRSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA224AndRSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA256AndRSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA384AndRSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA512AndRSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_224AndRSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_256AndRSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_384AndRSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_512AndRSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA1AndDSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA224AndDSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA256AndDSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA384AndDSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA512AndDSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_224AndDSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_256AndDSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_384AndDSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_512AndDSA.getAlgorithm().toString(), XNMSSKeyFactory.class.getName());

		this.put("KeyFactory.XNMSSOTSwithSHA1", XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSOTSwithSHA224", XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSOTSwithSHA256", XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSOTSwithSHA384", XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSOTSwithSHA512", XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSOTSwithSHA3-224", XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSOTSwithSHA3-256", XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSOTSwithSHA3-384", XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory.XNMSSOTSwithSHA3-512", XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA1.getAlgorithm().toString(), XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA224.getAlgorithm().toString(), XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA256.getAlgorithm().toString(), XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA384.getAlgorithm().toString(), XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA512.getAlgorithm().toString(), XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA3_224.getAlgorithm().toString(), XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA3_256.getAlgorithm().toString(), XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA3_384.getAlgorithm().toString(), XNMSSOTSKeyFactory.class.getName());
		this.put("KeyFactory." + XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA3_512.getAlgorithm().toString(), XNMSSOTSKeyFactory.class.getName());

		this.put("Signature.XNMSS", XNMSSignature.class.getName());
		this.put("Signature." + XNMSS.AlgorithmIdentifiers.XNMSS.getAlgorithm().toString(), XNMSSignature.class.getName());
	}

}
