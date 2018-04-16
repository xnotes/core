/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.xnotes.core.security.hash.HashEngine;
import org.xnotes.core.security.hash.JCEHashEngine;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSS {

	public static class AlgorithmIdentifiers {

		private static final ASN1ObjectIdentifier XNotes = new ASN1ObjectIdentifier("2.16.344.1.888");
		private static final ASN1ObjectIdentifier OID_XNMSS = XNotes.branch("1");

		public static final AlgorithmIdentifier XNMSS = new AlgorithmIdentifier(OID_XNMSS, null);
		public static final AlgorithmIdentifier XNMSSwithSHA1AndEC = new AlgorithmIdentifier(OID_XNMSS.branch("101"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA224AndEC = new AlgorithmIdentifier(OID_XNMSS.branch("201"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA256AndEC = new AlgorithmIdentifier(OID_XNMSS.branch("211"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA384AndEC = new AlgorithmIdentifier(OID_XNMSS.branch("221"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA512AndEC = new AlgorithmIdentifier(OID_XNMSS.branch("231"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA3_224AndEC = new AlgorithmIdentifier(OID_XNMSS.branch("301"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA3_256AndEC = new AlgorithmIdentifier(OID_XNMSS.branch("311"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA3_384AndEC = new AlgorithmIdentifier(OID_XNMSS.branch("321"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA3_512AndEC = new AlgorithmIdentifier(OID_XNMSS.branch("331"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA1AndRSA = new AlgorithmIdentifier(OID_XNMSS.branch("102"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA224AndRSA = new AlgorithmIdentifier(OID_XNMSS.branch("202"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA256AndRSA = new AlgorithmIdentifier(OID_XNMSS.branch("212"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA384AndRSA = new AlgorithmIdentifier(OID_XNMSS.branch("222"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA512AndRSA = new AlgorithmIdentifier(OID_XNMSS.branch("232"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA3_224AndRSA = new AlgorithmIdentifier(OID_XNMSS.branch("302"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA3_256AndRSA = new AlgorithmIdentifier(OID_XNMSS.branch("312"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA3_384AndRSA = new AlgorithmIdentifier(OID_XNMSS.branch("322"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA3_512AndRSA = new AlgorithmIdentifier(OID_XNMSS.branch("332"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA1AndDSA = new AlgorithmIdentifier(OID_XNMSS.branch("103"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA224AndDSA = new AlgorithmIdentifier(OID_XNMSS.branch("203"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA256AndDSA = new AlgorithmIdentifier(OID_XNMSS.branch("213"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA384AndDSA = new AlgorithmIdentifier(OID_XNMSS.branch("223"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA512AndDSA = new AlgorithmIdentifier(OID_XNMSS.branch("233"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA3_224AndDSA = new AlgorithmIdentifier(OID_XNMSS.branch("303"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA3_256AndDSA = new AlgorithmIdentifier(OID_XNMSS.branch("313"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA3_384AndDSA = new AlgorithmIdentifier(OID_XNMSS.branch("323"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSwithSHA3_512AndDSA = new AlgorithmIdentifier(OID_XNMSS.branch("333"), DERNull.INSTANCE);

		public static final AlgorithmIdentifier XNMSSOTSwithSHA1 = new AlgorithmIdentifier(OID_XNMSS.branch("100"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSOTSwithSHA224 = new AlgorithmIdentifier(OID_XNMSS.branch("200"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSOTSwithSHA256 = new AlgorithmIdentifier(OID_XNMSS.branch("210"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSOTSwithSHA384 = new AlgorithmIdentifier(OID_XNMSS.branch("220"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSOTSwithSHA512 = new AlgorithmIdentifier(OID_XNMSS.branch("230"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSOTSwithSHA3_224 = new AlgorithmIdentifier(OID_XNMSS.branch("300"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSOTSwithSHA3_256 = new AlgorithmIdentifier(OID_XNMSS.branch("310"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSOTSwithSHA3_384 = new AlgorithmIdentifier(OID_XNMSS.branch("320"), DERNull.INSTANCE);
		public static final AlgorithmIdentifier XNMSSOTSwithSHA3_512 = new AlgorithmIdentifier(OID_XNMSS.branch("330"), DERNull.INSTANCE);
	}

	protected static final Map<String, HashEngine> hashEngines = new HashMap<>();

	public static void registerHashEngine(HashEngine hashEngine) throws IllegalArgumentException {
		String algorithm = hashEngine.getAlgorithm();
		if (algorithm.equalsIgnoreCase("SHA224")) {
			hashEngines.put("SHA-224", hashEngine);
		} else if (algorithm.equalsIgnoreCase("SHA256")) {
			hashEngines.put("SHA-256", hashEngine);
		} else if (algorithm.equalsIgnoreCase("SHA384")) {
			hashEngines.put("SHA-384", hashEngine);
		} else if (algorithm.equalsIgnoreCase("SHA512")) {
			hashEngines.put("SHA-512", hashEngine);
		} else if (algorithm.equalsIgnoreCase("SHA1")
				|| algorithm.equalsIgnoreCase("SHA-224")
				|| algorithm.equalsIgnoreCase("SHA-256")
				|| algorithm.equalsIgnoreCase("SHA-384")
				|| algorithm.equalsIgnoreCase("SHA-512")
				|| algorithm.equalsIgnoreCase("SHA3-224")
				|| algorithm.equalsIgnoreCase("SHA3-256")
				|| algorithm.equalsIgnoreCase("SHA3-384")
				|| algorithm.equalsIgnoreCase("SHA3-512")) {
			hashEngines.put(algorithm.toUpperCase(), hashEngine);
		} else {
			throw new IllegalArgumentException("Unsupported digest algorithm '" + algorithm + "' specified in HashEngine of class '" + hashEngine.getClass().getName() + "'.");
		}
	}

	public static HashEngine getHashEngineForDigestAlgorithm(String digestAlgorithm) throws IllegalArgumentException {
		HashEngine hashEngine = hashEngines.get(digestAlgorithm);
		if (hashEngine == null) {
			try {
				hashEngine = new JCEHashEngine();
			} catch (Throwable ex) {
				throw new IllegalArgumentException("Exception instantiating HashEngine of class '" + JCEHashEngine.class.getName() + "'.");
			}
			hashEngine.setAlgorithm(digestAlgorithm);
			XNMSS.registerHashEngine(hashEngine);
		}
		return hashEngine;
	}

	private static XNMSSPrivateKeyManager _privateKeysManager;

	public static void registerPrivateKeyManager(XNMSSPrivateKeyManager privateKeysManager) {
		_privateKeysManager = privateKeysManager;
	}

	public static XNMSSPrivateKeyManager getPrivateKeyManager() {
		return _privateKeysManager;
	}

	private static XNMSSPublicKeyManager _publicKeysManager;

	public static void registerPublicKeyManager(XNMSSPublicKeyManager publicKeysManager) {
		_publicKeysManager = publicKeysManager;
	}

	public static XNMSSPublicKeyManager getPublicKeyManager() {
		return _publicKeysManager;
	}

	public static AlgorithmIdentifier getAlgorithmIdentifierForKeyAlgorithm(String algorithm) throws IllegalArgumentException {
		switch (algorithm.toUpperCase()) {
			case "XNMSSWITHSHA1ANDEC":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA1AndEC;
			case "XNMSSWITHSHA224ANDEC":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA224AndEC;
			case "XNMSSWITHSHA256ANDEC":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA256AndEC;
			case "XNMSSWITHSHA384ANDEC":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA384AndEC;
			case "XNMSSWITHSHA512ANDEC":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA512AndEC;
			case "XNMSSWITHSHA3-224ANDEC":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_224AndEC;
			case "XNMSSWITHSHA3-256ANDEC":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_256AndEC;
			case "XNMSSWITHSHA3-384ANDEC":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_384AndEC;
			case "XNMSSWITHSHA3-512ANDEC":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_512AndEC;
			case "XNMSSWITHSHA1ANDRSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA1AndRSA;
			case "XNMSSWITHSHA224ANDRSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA224AndRSA;
			case "XNMSSWITHSHA256ANDRSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA256AndRSA;
			case "XNMSSWITHSHA384ANDRSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA384AndRSA;
			case "XNMSSWITHSHA512ANDRSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA512AndRSA;
			case "XNMSSWITHSHA3-224ANDRSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_224AndRSA;
			case "XNMSSWITHSHA3-256ANDRSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_256AndRSA;
			case "XNMSSWITHSHA3-384ANDRSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_384AndRSA;
			case "XNMSSWITHSHA3-512ANDRSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_512AndRSA;
			case "XNMSSWITHSHA1ANDDSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA1AndDSA;
			case "XNMSSWITHSHA224ANDDSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA224AndDSA;
			case "XNMSSWITHSHA256ANDDSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA256AndDSA;
			case "XNMSSWITHSHA384ANDDSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA384AndDSA;
			case "XNMSSWITHSHA512ANDDSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA512AndDSA;
			case "XNMSSWITHSHA3-224ANDDSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_224AndDSA;
			case "XNMSSWITHSHA3-256ANDDSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_256AndDSA;
			case "XNMSSWITHSHA3-384ANDDSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_384AndDSA;
			case "XNMSSWITHSHA3-512ANDDSA":
				return XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_512AndDSA;
			case "XNMSSOTSWITHSHA1":
				return XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA1;
			case "XNMSSOTSWITHSHA224":
				return XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA224;
			case "XNMSSOTSWITHSHA256":
				return XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA256;
			case "XNMSSOTSWITHSHA384":
				return XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA384;
			case "XNMSSOTSWITHSHA512":
				return XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA512;
			case "XNMSSOTSWITHSHA3-224":
				return XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA3_224;
			case "XNMSSOTSWITHSHA3-256":
				return XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA3_256;
			case "XNMSSOTSWITHSHA3-384":
				return XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA3_384;
			case "XNMSSOTSWITHSHA3-512":
				return XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA3_512;
			default:
				throw new IllegalArgumentException("Unsupported Key Algorithm '" + algorithm + "'.");
		}
	}

	public static String getDigestAlgorithmForKeyAlgorithm(String keyAlgorithm) throws IllegalArgumentException {
		String alg = keyAlgorithm.toUpperCase();
		String algStart = "XNMSSOTSWITHSHA";
		if (!alg.startsWith(algStart)) {
			algStart = "XNMSSWITHSHA";
			if (!alg.startsWith(algStart)) {
				throw new IllegalArgumentException("Unsupported XNMSS Key Algorithm '" + keyAlgorithm + "'.");
			}
		}
		int n1 = algStart.length();
		int n2 = alg.indexOf("AND", n1);
		String digestAlg = keyAlgorithm.substring(n1 - 3, n2 > n1 ? n2 : alg.length());
		if (digestAlg.equalsIgnoreCase("SHA224")) {
			digestAlg = "SHA-224";
		} else if (digestAlg.equalsIgnoreCase("SHA256")) {
			digestAlg = "SHA-256";
		} else if (digestAlg.equalsIgnoreCase("SHA384")) {
			digestAlg = "SHA-384";
		} else if (digestAlg.equalsIgnoreCase("SHA512")) {
			digestAlg = "SHA-512";
		}
		if (digestAlg.equalsIgnoreCase("SHA1")
				|| digestAlg.equalsIgnoreCase("SHA-224")
				|| digestAlg.equalsIgnoreCase("SHA-256")
				|| digestAlg.equalsIgnoreCase("SHA-384")
				|| digestAlg.equalsIgnoreCase("SHA-512")
				|| digestAlg.equalsIgnoreCase("SHA3-224")
				|| digestAlg.equalsIgnoreCase("SHA3-256")
				|| digestAlg.equalsIgnoreCase("SHA3-384")
				|| digestAlg.equalsIgnoreCase("SHA3-512")) {
			return digestAlg.toUpperCase();
		} else {
			throw new IllegalArgumentException("Digest Algorithm '" + digestAlg + "' in specified MSS Algorithm '" + keyAlgorithm + "' is unsupported.");
		}
	}

	public static String getSigningKeyAlgorithmForKeyAlgorithm(String keyAlgorithm) throws IllegalArgumentException {
		String alg = keyAlgorithm.toUpperCase();
		String algStart = "XNMSSWITHSHA";
		if (!alg.startsWith(algStart)) {
			throw new IllegalArgumentException("Unsupported XNMSS Key Algorithm '" + keyAlgorithm + "'.");
		}
		int n1 = algStart.length();
		int n2 = alg.indexOf("AND", n1);
		if (n2 > n1) {
			return alg.substring(n2 + 3);
		} else {
			throw new IllegalArgumentException("Unsupported XNMSS Key Algorithm '" + keyAlgorithm + "'.");
		}
	}

	public static String getOTSKeyAlgorithmForKeyAlgorithm(String keyAlgorithm) throws IllegalArgumentException {
		String alg = keyAlgorithm.toUpperCase();
		String algStart = "XNMSSWITHSHA";
		if (!alg.startsWith(algStart)) {
			throw new IllegalArgumentException("Unsupported XNMSS Key Algorithm '" + keyAlgorithm + "'.");
		}
		int n1 = algStart.length();
		int n2 = alg.indexOf("AND", n1);
		if (n2 > n1) {
			return keyAlgorithm.substring(0, "XNMSS".length()) + "OTS" + keyAlgorithm.substring("XNMSS".length(), n2);
		} else {
			throw new IllegalArgumentException("Unsupported XNMSS Key Algorithm '" + keyAlgorithm + "'.");
		}
	}

	public static String getSignatureKeyAlgorithmForKeyAlgorithm(String keyAlgorithm) throws IllegalArgumentException {
		String alg = keyAlgorithm.toUpperCase();
		String algStart = "XNMSSWITHSHA";
		if (!alg.startsWith(algStart)) {
			throw new IllegalArgumentException("Unsupported XNMSS Key Algorithm '" + keyAlgorithm + "'.");
		}
		int n1 = algStart.length();
		int n2 = alg.indexOf("AND", n1);
		if (n2 > n1) {
			return keyAlgorithm.substring(n2 + "AND".length());
		} else {
			throw new IllegalArgumentException("Unsupported XNMSS Key Algorithm '" + keyAlgorithm + "'.");
		}
	}

	public static String getSignatureAlgorithmForKeyAlgorithm(String keyAlgorithm) {
		return getSignatureAlgorithmForDigestAlgorithmAndSigningKeyAlgorithm(getDigestAlgorithmForKeyAlgorithm(keyAlgorithm), getSigningKeyAlgorithmForKeyAlgorithm(keyAlgorithm));
	}

	public static String getSignatureAlgorithmForDigestAlgorithmAndSigningKeyAlgorithm(String digestAlg, String signingKeyAlg) {
		if (digestAlg.equalsIgnoreCase("SHA-224")) {
			digestAlg = "SHA224";
		} else if (digestAlg.equalsIgnoreCase("SHA-256")) {
			digestAlg = "SHA256";
		} else if (digestAlg.equalsIgnoreCase("SHA-384")) {
			digestAlg = "SHA384";
		} else if (digestAlg.equalsIgnoreCase("SHA-512")) {
			digestAlg = "SHA512";
		}
		if (signingKeyAlg.equals("EC")) {
			return digestAlg + "withECDSA";
		} else {
			return digestAlg + "with" + signingKeyAlg;
		}
	}
	
}
