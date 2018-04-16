/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss.jce;

import org.xnotes.core.security.ots.xnmss.XNMSSPrivateKeyManager;
import org.xnotes.core.security.ots.xnmss.XNMSSPublicKeyManager;
import org.xnotes.core.security.ots.xnmss.XNMSS;
import org.xnotes.core.utils.MerkleTree;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.xnotes.Config;
import org.xnotes.ConfigurationException;
import org.xnotes.core.security.SecurityToolSet;
import org.xnotes.core.security.hash.HashEngine;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public abstract class XNMSSKeyPairGenerator extends KeyPairGenerator {

	public static int DEFAULT_DIGEST_ITERATIONS = 1;

	private XNMSSParameterSpec _params;
	private SecureRandom _random;

	protected XNMSSKeyPairGenerator(String algorithm) {
		super(algorithm);
	}

	@Override
	public void initialize(int keySize) {
		this.initialize(keySize, null);
	}

	@Override
	public void initialize(int keySize, SecureRandom secureRandom) {
		try {
			String sigKeyAlgorithm = XNMSS.getSignatureKeyAlgorithmForKeyAlgorithm(this.getAlgorithm());
			this.initialize(new XNMSSParameterSpec(
					keySize,
					DEFAULT_DIGEST_ITERATIONS, sigKeyAlgorithm.equalsIgnoreCase("EC") ? Config.DEFAULT_EC_KEYSIZE
					: sigKeyAlgorithm.equalsIgnoreCase("RSA") ? Config.DEFAULT_RSA_KEYSIZE
					: Config.DEFAULT_DSA_KEYSIZE),
					secureRandom);
		} catch (InvalidAlgorithmParameterException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	@Override
	public void initialize(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
		initialize(params, null);
	}

	@Override
	public void initialize(AlgorithmParameterSpec paramSpec, SecureRandom random) throws InvalidAlgorithmParameterException {
		if (!XNMSSParameterSpec.class.isInstance(paramSpec)) {
			throw new InvalidAlgorithmParameterException("Invalid AlgorithmParameterSpec Class '" + paramSpec.getClass().getName() + "'.");
		}
		_params = (XNMSSParameterSpec) paramSpec;
		_random = random != null ? random : SecurityToolSet.getDefaultSecureRandom();
	}

	@Override
	public KeyPair generateKeyPair() {
		if (_params != null) {
			try {
				String otsKeyAlgorithm = XNMSS.getSigningKeyAlgorithmForKeyAlgorithm(this.getAlgorithm());
				String otsDigestAlgorithm = XNMSS.getDigestAlgorithmForKeyAlgorithm(this.getAlgorithm());
				int n = _params.otsKeyCount;
				byte[][] hashes = new byte[n][];
				KeyPairGenerator kpg = KeyPairGenerator.getInstance(otsKeyAlgorithm);
				HashEngine hashEngine = XNMSS.getHashEngineForDigestAlgorithm(otsDigestAlgorithm);
				PublicKey[] signPublicKeys = new PublicKey[n];
				PrivateKey[] signPrivateKeys = new PrivateKey[n];
				for (int i = 0; i < n; i++) {
					kpg.initialize(_params.otsKeySize > 0 ? _params.otsKeySize
							: otsKeyAlgorithm.equalsIgnoreCase("EC") ? Config.DEFAULT_EC_KEYSIZE
							: otsKeyAlgorithm.equalsIgnoreCase("RSA") ? Config.DEFAULT_RSA_KEYSIZE
							: Config.DEFAULT_DSA_KEYSIZE,
							_random);
					KeyPair kp = kpg.generateKeyPair();
					signPublicKeys[i] = kp.getPublic();
					signPrivateKeys[i] = kp.getPrivate();
					hashes[i] = hashEngine.hash(signPublicKeys[i].getEncoded(), _params.getDigestIterations(), null);
				}
				MerkleTree mt = new MerkleTree(_params.getHeight(), hashEngine, _params.getDigestIterations());
				mt.computeWithLeafHashes(hashes);
				XNMSSPublicKey publicKey = new XNMSSPublicKey(this.getAlgorithm(), _params.height, _params.digestIterations, mt.getRoot(), _params.parentOTSPrivateKey);
				publicKey.setSecureRandom(_random);
				XNMSSOTSPrivateKey[] otsPrivateKeys = new XNMSSOTSPrivateKey[n];
				for (int i = 0; i < n; i++) {
					otsPrivateKeys[i] = new XNMSSOTSPrivateKey(XNMSS.getOTSKeyAlgorithmForKeyAlgorithm(this.getAlgorithm()), _params.height, _params.digestIterations, mt.getRoot(), _params.parentOTSPrivateKey != null ? _params.parentOTSPrivateKey.getIndexOffset() + _params.parentOTSPrivateKey.getIndex() + 1 : 0, i, signPublicKeys[i], signPrivateKeys[i], mt.getPathForLeaf(i));
					otsPrivateKeys[i].setSecureRandom(_random);
					otsPrivateKeys[i].getPublicKey().setSecureRandom(_random);
				}
				XNMSSPrivateKey privateKey = new XNMSSPrivateKey(this.getAlgorithm(), _params.height, _params.digestIterations, publicKey, _params.parentOTSPrivateKey, mt, otsPrivateKeys);
				privateKey.setSecureRandom(_random);
				XNMSSPublicKeyManager pubKeyManager = XNMSS.getPublicKeyManager();
				if (pubKeyManager != null) {
					pubKeyManager.manage(publicKey);
				}
				XNMSSPrivateKeyManager privKeyManager = XNMSS.getPrivateKeyManager();
				if (privKeyManager != null) {
					privKeyManager.manage(privateKey);
				}
				return new KeyPair(publicKey, privateKey);
			} catch (IllegalArgumentException | NoSuchAlgorithmException ex) {
				throw new ConfigurationException(ex);
			}
		}
		return null;
	}

	public static class XNMSSwithSHA1AndECKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA1AndECKeyPairGenerator() {
			super("XNMSSwithSHA1AndEC");
		}

	}

	public static class XNMSSwithSHA224AndECKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA224AndECKeyPairGenerator() {
			super("XNMSSwithSHA224AndEC");
		}
	}

	public static class XNMSSwithSHA256AndECKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA256AndECKeyPairGenerator() {
			super("XNMSSwithSHA256AndEC");
		}
	}

	public static class XNMSSwithSHA384AndECKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA384AndECKeyPairGenerator() {
			super("XNMSSwithSHA384AndEC");
		}
	}

	public static class XNMSSwithSHA512AndECKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA512AndECKeyPairGenerator() {
			super("XNMSSwithSHA512AndEC");
		}
	}

	public static class XNMSSwithSHA3_224AndECKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA3_224AndECKeyPairGenerator() {
			super("XNMSSwithSHA3-224AndEC");
		}
	}

	public static class XNMSSwithSHA3_256AndECKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA3_256AndECKeyPairGenerator() {
			super("XNMSSwithSHA3-256AndEC");
		}
	}

	public static class XNMSSwithSHA3_384AndECKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA3_384AndECKeyPairGenerator() {
			super("XNMSSwithSHA3-384AndEC");
		}
	}

	public static class XNMSSwithSHA3_512AndECKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA3_512AndECKeyPairGenerator() {
			super("XNMSSwithSHA3-512AndEC");
		}
	}

	public static class XNMSSwithSHA1AndRSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA1AndRSAKeyPairGenerator() {
			super("XNMSSwithSHA1AndRSA");
		}
	}

	public static class XNMSSwithSHA224AndRSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA224AndRSAKeyPairGenerator() {
			super("XNMSSwithSHA224AndRSA");
		}
	}

	public static class XNMSSwithSHA256AndRSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA256AndRSAKeyPairGenerator() {
			super("XNMSSwithSHA256AndRSA");
		}
	}

	public static class XNMSSwithSHA384AndRSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA384AndRSAKeyPairGenerator() {
			super("XNMSSwithSHA384AndRSA");
		}
	}

	public static class XNMSSwithSHA512AndRSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA512AndRSAKeyPairGenerator() {
			super("XNMSSwithSHA512AndRSA");
		}
	}

	public static class XNMSSwithSHA3_224AndRSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA3_224AndRSAKeyPairGenerator() {
			super("XNMSSwithSHA3-224AndRSA");
		}
	}

	public static class XNMSSwithSHA3_256AndRSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA3_256AndRSAKeyPairGenerator() {
			super("XNMSSwithSHA3-256AndRSA");
		}
	}

	public static class XNMSSwithSHA3_384AndRSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA3_384AndRSAKeyPairGenerator() {
			super("XNMSSwithSHA3-384AndRSA");
		}
	}

	public static class XNMSSwithSHA3_512AndRSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA3_512AndRSAKeyPairGenerator() {
			super("XNMSSwithSHA3-512AndRSA");
		}
	}

	public static class XNMSSwithSHA1AndDSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA1AndDSAKeyPairGenerator() {
			super("XNMSSwithSHA1AndDSA");
		}
	}

	public static class XNMSSwithSHA224AndDSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA224AndDSAKeyPairGenerator() {
			super("XNMSSwithSHA224AndDSA");
		}
	}

	public static class XNMSSwithSHA256AndDSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA256AndDSAKeyPairGenerator() {
			super("XNMSSwithSHA256AndDSA");
		}
	}

	public static class XNMSSwithSHA384AndDSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA384AndDSAKeyPairGenerator() {
			super("XNMSSwithSHA384AndDSA");
		}
	}

	public static class XNMSSwithSHA512AndDSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA512AndDSAKeyPairGenerator() {
			super("XNMSSwithSHA512AndDSA");
		}
	}

	public static class XNMSSwithSHA3_224AndDSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA3_224AndDSAKeyPairGenerator() {
			super("XNMSSwithSHA3-224AndDSA");
		}
	}

	public static class XNMSSwithSHA3_256AndDSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA3_256AndDSAKeyPairGenerator() {
			super("XNMSSwithSHA3-256AndDSA");
		}
	}

	public static class XNMSSwithSHA3_384AndDSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA3_384AndDSAKeyPairGenerator() {
			super("XNMSSwithSHA3-384AndDSA");
		}
	}

	public static class XNMSSwithSHA3_512AndDSAKeyPairGenerator extends XNMSSKeyPairGenerator {

		public XNMSSwithSHA3_512AndDSAKeyPairGenerator() {
			super("XNMSSwithSHA3-512AndDSA");
		}
	}
}
