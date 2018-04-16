/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
package org.xnotes.core.security;

import org.xnotes.core.security.hash.HashEngine;
import org.xnotes.core.security.jce.XNProvider;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStoreException;
import org.xnotes.ConfigurationException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.xnotes.Config;
import org.xnotes.XNotes;
import org.xnotes.core.net.protocol.SignatureObject;
import org.xnotes.core.security.hash.BCSCryptHashEngine;
import org.xnotes.core.security.hash.JCEHashEngine;
import org.xnotes.core.security.ots.xnmss.jce.XNMSSKey;
import org.xnotes.core.utils.ASN1;
import org.xnotes.core.utils.DatatypeConverter;
import org.xnotes.core.utils.FileUtil;

/**
 *
 * @author sopheap
 */
public final class SecurityToolSet {

	private static final Provider[] _PROVIDERS;
	private static final Map<String, List<String>> _PROVIDERS_BY_SECURERANDOM_ALGORITHM = new HashMap<>();
	private static final Map<String, List<String>> _PROVIDERS_BY_MESSAGEDIGEST_ALGORITHM = new HashMap<>();
	private static final Map<String, List<String>> _PROVIDERS_BY_KEYPAIRGENERATOR_ALGORITHM = new HashMap<>();
	private static final Map<String, List<String>> _PROVIDERS_BY_KEYFACTORY_ALGORITHM = new HashMap<>();
	private static final Map<String, List<String>> _PROVIDERS_BY_SECRETKEYFACTORY_ALGORITHM = new HashMap<>();
	private static final Map<String, List<String>> _PROVIDERS_BY_SIGNATURE_ALGORITHM = new HashMap<>();
	private static final Map<String, List<String>> _PROVIDERS_BY_KEYGENERATOR_ALGORITHM = new HashMap<>();
	private static final Map<String, List<String>> _PROVIDERS_BY_CIPHER_ALGORITHM = new HashMap<>();
	private static final Map<String, List<String>> _PROVIDERS_BY_CERTIFICATEFACTORY_TYPE = new HashMap<>();

	static {
		if (java.security.Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			java.security.Security.addProvider(new BouncyCastleProvider());
		}
		if (java.security.Security.getProvider(XNProvider.PROVIDER_NAME) == null) {
			java.security.Security.addProvider(new XNProvider());
		}
		_PROVIDERS = java.security.Security.getProviders();
	}

	public static List<String> getProvidersForSecureRandomAlgorithm(String algorithm) {
		if (_PROVIDERS_BY_SECURERANDOM_ALGORITHM.isEmpty()) {
			_populateProviderMap(_PROVIDERS_BY_SECURERANDOM_ALGORITHM, "SecureRandom");
		}
		return _PROVIDERS_BY_SECURERANDOM_ALGORITHM.get(algorithm);
	}

	public static List<String> getProvidersForMessageDigestAlgorithm(String algorithm) {
		if (_PROVIDERS_BY_MESSAGEDIGEST_ALGORITHM.isEmpty()) {
			_populateProviderMap(_PROVIDERS_BY_MESSAGEDIGEST_ALGORITHM, "MessageDigest");
		}
		return _PROVIDERS_BY_MESSAGEDIGEST_ALGORITHM.get(algorithm);
	}

	public static List<String> getProvidersForKeyPairGeneratorAlgorithm(String algorithm) {
		if (_PROVIDERS_BY_KEYPAIRGENERATOR_ALGORITHM.isEmpty()) {
			_populateProviderMap(_PROVIDERS_BY_KEYPAIRGENERATOR_ALGORITHM, "KeyPairGenerator");
		}
		return _PROVIDERS_BY_KEYPAIRGENERATOR_ALGORITHM.get(algorithm);
	}

	public static List<String> getProvidersForKeyFactoryAlgorithm(String algorithm) {
		if (_PROVIDERS_BY_KEYFACTORY_ALGORITHM.isEmpty()) {
			_populateProviderMap(_PROVIDERS_BY_KEYFACTORY_ALGORITHM, "KeyFactory");
		}
		return _PROVIDERS_BY_KEYFACTORY_ALGORITHM.get(algorithm);
	}

	public static List<String> getProvidersForSecretKeyFactoryAlgorithm(String algorithm) {
		if (_PROVIDERS_BY_SECRETKEYFACTORY_ALGORITHM.isEmpty()) {
			_populateProviderMap(_PROVIDERS_BY_SECRETKEYFACTORY_ALGORITHM, "SecretKeyFactory");
		}
		return _PROVIDERS_BY_SECRETKEYFACTORY_ALGORITHM.get(algorithm);
	}

	public static List<String> getProvidersForSignatureAlgorithm(String algorithm) {
		if (_PROVIDERS_BY_SIGNATURE_ALGORITHM.isEmpty()) {
			_populateProviderMap(_PROVIDERS_BY_SIGNATURE_ALGORITHM, "Signature");
		}
		return _PROVIDERS_BY_SIGNATURE_ALGORITHM.get(algorithm);
	}

	public static List<String> getProvidersForKeyGeneratorAlgorithm(String algorithm) {
		if (_PROVIDERS_BY_KEYGENERATOR_ALGORITHM.isEmpty()) {
			_populateProviderMap(_PROVIDERS_BY_KEYGENERATOR_ALGORITHM, "KeyGenerator");
		}
		return _PROVIDERS_BY_KEYGENERATOR_ALGORITHM.get(algorithm);
	}

	public static List<String> getProvidersForCipherAlgorithm(String algorithm) {
		if (_PROVIDERS_BY_CIPHER_ALGORITHM.isEmpty()) {
			_populateProviderMap(_PROVIDERS_BY_CIPHER_ALGORITHM, "Cipher");
		}
		return _PROVIDERS_BY_CIPHER_ALGORITHM.get(algorithm);
	}

	public static List<String> getProvidersForCertificateFactoryType(String type) {
		if (_PROVIDERS_BY_CERTIFICATEFACTORY_TYPE.isEmpty()) {
			_populateProviderMap(_PROVIDERS_BY_CERTIFICATEFACTORY_TYPE, "CertificateFactory");
		}
		return _PROVIDERS_BY_CERTIFICATEFACTORY_TYPE.get(type);
	}

	private static void _populateProviderMap(Map<String, List<String>> map, String serviceType) {
		for (Provider provider : _PROVIDERS) {
			provider.getServices().stream().filter((service) -> (service.getType().equals(serviceType))).map((service) -> {
				if (!map.containsKey(service.getAlgorithm())) {
					map.put(service.getAlgorithm(), new ArrayList<>());
				}
				return service;
			}).map((service) -> map.get(service.getAlgorithm())).forEachOrdered((prList) -> {
				prList.add(provider.getName());
			});
		}
	}

	public static String getProviderForKeyFactoryAlgorithm(String KeyAlg) throws ConfigurationException {
		KeyAlg = KeyAlg.trim();
		List<String> kfProviders = getProvidersForKeyFactoryAlgorithm(KeyAlg);
		if (kfProviders == null) {
			throw new ConfigurationException("Unsupported key factory algorithm '" + KeyAlg + "'.");
		}
		return kfProviders.get(0);
	}

	public static String getDigestForSignatureAlgorithm(String signAlg) {
		int i = signAlg.indexOf("with");
		if (i > -1) {
			return signAlg.substring(0, i);
		} else {
			return null;
		}
	}

	public static Map<String, String> getCipherParametersForCipherAlgorihtmName(String cipherAlgorithmName) {
		Map<String, String> params = new HashMap<>();
		if (cipherAlgorithmName.startsWith("PBE")) {
			int i = cipherAlgorithmName.indexOf("With");
			if (i > -1) {
				params.put("type", cipherAlgorithmName.substring(0, i));
				int j = cipherAlgorithmName.indexOf("And", i + 4);
				if (j > -1) {
					params.put("digest", cipherAlgorithmName.substring(i + 4, j));
					params.put("encryption", cipherAlgorithmName.substring(j + 3));
				} else {
					params.put("digest", cipherAlgorithmName.substring(i + 4));
				}
			} else {
				params.put("type", cipherAlgorithmName);
			}
		} else if (cipherAlgorithmName.startsWith("AES") || cipherAlgorithmName.startsWith("Blowfish")) {
			int i = cipherAlgorithmName.indexOf("/");
			if (i > -1) {
				params.put("type", cipherAlgorithmName.substring(0, i));
				int j = cipherAlgorithmName.indexOf("/", i + 1);
				if (j > -1) {
					params.put("mode", cipherAlgorithmName.substring(i + 1, j));
					params.put("padding", cipherAlgorithmName.substring(j + 1));
				} else {
					params.put("mode", cipherAlgorithmName.substring(i + 1));
				}
			} else {
				params.put("type", cipherAlgorithmName);
			}
		} else {
			params.put("type", cipherAlgorithmName);
		}
		return params;
	}

	public static String getCurveNameForECKey(Key key) {
		if (ECKey.class.isInstance(key)) {
			String s = ((ECKey) key).getParams().toString();
			int i = s.indexOf(" ");
			if (i > -1) {
				s = s.substring(0, i);
			}
			return s;
		} else {
			return null;
		}
	}

	public static boolean areAlgorithmsEqual(Key key, String keyAlg) {
		String alg = key.getAlgorithm();
		if (alg.equals(PQCObjectIdentifiers.gmss.toString())) {
			return keyAlg != null && (keyAlg.equals(alg) || keyAlg.equals("GMSS"));
		} else if (keyAlg != null && keyAlg.equals(PQCObjectIdentifiers.gmss.toString())) {
			return alg.equals(keyAlg) || alg.equals("GMSS");
		} else if (alg.equals(PQCObjectIdentifiers.gmssWithSha1.toString())) {
			return keyAlg != null && (keyAlg.equals(alg) || keyAlg.equals("GMSSwithSHA1"));
		} else if (keyAlg != null && keyAlg.equals(PQCObjectIdentifiers.gmssWithSha1.toString())) {
			return alg.equals(keyAlg) || alg.equals("GMSSwithSHA1");
		} else if (alg.equals(PQCObjectIdentifiers.gmssWithSha224.toString())) {
			return keyAlg != null && (keyAlg.equals(alg) || keyAlg.equals("GMSSwithSHA224"));
		} else if (keyAlg != null && keyAlg.equals(PQCObjectIdentifiers.gmssWithSha224.toString())) {
			return alg.equals(keyAlg) || alg.equals("GMSSwithSHA224");
		} else if (alg.equals(PQCObjectIdentifiers.gmssWithSha256.toString())) {
			return keyAlg != null && (keyAlg.equals(alg) || keyAlg.equals("GMSSwithSHA256"));
		} else if (keyAlg != null && keyAlg.equals(PQCObjectIdentifiers.gmssWithSha256.toString())) {
			return alg.equals(keyAlg) || alg.equals("GMSSwithSHA256");
		} else if (alg.equals(PQCObjectIdentifiers.gmssWithSha384.toString())) {
			return keyAlg != null && (keyAlg.equals(alg) || keyAlg.equals("GMSSwithSHA384"));
		} else if (keyAlg != null && keyAlg.equals(PQCObjectIdentifiers.gmssWithSha384.toString())) {
			return alg.equals(keyAlg) || alg.equals("GMSSwithSHA384");
		} else if (alg.equals(PQCObjectIdentifiers.gmssWithSha512.toString())) {
			return keyAlg != null && (keyAlg.equals(alg) || keyAlg.equals("GMSSwithSHA512"));
		} else if (keyAlg != null && keyAlg.equals(PQCObjectIdentifiers.gmssWithSha512.toString())) {
			return alg.equals(keyAlg) || alg.equals("GMSSwithSHA512");
		} else {
			return alg.equals(keyAlg);
		}
	}

	public static boolean areAlgorithmsEqual(Key key, String keyAlg, int keySize) {
		boolean eq = areAlgorithmsEqual(key, keyAlg);
		if (eq) {
			return keySize == getKeySize(key);
		}
		return false;
	}

	public static int getKeySize(Key key) {
		try {
			String alg = key.getAlgorithm().toUpperCase();
			AlgorithmIdentifier algId = null;
			String fmt = key.getFormat();
			if (fmt.equalsIgnoreCase("PKCS#8")) {
				algId = new AlgorithmIdentifier(
						(ASN1ObjectIdentifier) ((ASN1Sequence) ((ASN1Sequence) ASN1Primitive.fromByteArray(key.getEncoded())).getObjectAt(1)).getObjectAt(0),
						((ASN1Sequence) ((ASN1Sequence) ASN1Primitive.fromByteArray(key.getEncoded())).getObjectAt(1)).size() > 1 ? ((ASN1Sequence) ((ASN1Sequence) ASN1Primitive.fromByteArray(key.getEncoded())).getObjectAt(1)).getObjectAt(1) : null
				);
			} else if (fmt.equalsIgnoreCase("X.509")) {
				algId = new AlgorithmIdentifier(
						(ASN1ObjectIdentifier) ((ASN1Sequence) ((ASN1Sequence) ASN1Primitive.fromByteArray(key.getEncoded())).getObjectAt(0)).getObjectAt(0),
						((ASN1Sequence) ((ASN1Sequence) ASN1Primitive.fromByteArray(key.getEncoded())).getObjectAt(0)).size() > 1 ? ((ASN1Sequence) ((ASN1Sequence) ASN1Primitive.fromByteArray(key.getEncoded())).getObjectAt(0)).getObjectAt(1) : null
				);
			}
			if (algId != null) {
				if (alg.startsWith("XNMSSWITHSHA")) {
					return ((XNMSSKey) key).getHeight();
				} else if (alg.equals("EC")) {
					if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP112R1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP112R2)) {
						return 112;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT113R1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT113R2)) {
						return 113;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP128R1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP128R2)) {
						return 128;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT131R1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT131R2)) {
						return 131;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP160K1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP160r1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP160R2)) {
						return 160;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2PNB163V1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2PNB163V2)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2PNB163V3)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT163K1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT163R1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT163R2)) {
						return 163;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2PNB176W1)) {
						return 176;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2TNB191V1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2TNB191V2)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2TNB191V3)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2ONB191V4)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2ONB191V5)) {
						return 191;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP192K1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_PRIME192V2)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_PRIME192V3)) {
						return 192;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT193R1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT193R2)) {
						return 193;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2PNB208W1)) {
						return 208;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP224K1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP224R1)) {
						return 224;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT233K1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT233R1)) {
						return 233;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2TNB239V1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2TNB239V2)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2TNB239V3)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2ONB239V4)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2ONB239V5)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT239K1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_PRIME239V1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_PRIME239V2)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_PRIME239V3)) {
						return 239;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP256K1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_PRIME256V1)) {
						return 256;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2PNB272W1)) {
						return 272;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT283K1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT283R1)) {
						return 283;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2PNB304W1)) {
						return 304;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2TNB359V1)) {
						return 359;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2PNB368W1)) {
						return 368;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP384R1)) {
						return 384;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT409K1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT409R1)) {
						return 409;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_C2TNB431R1)) {
						return 431;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECP521R1)) {
						return 521;
					} else if (algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT571K1)
							|| algId.equals(ASN1.ALGORITHM_IDENTIFIER_EC_SECT571R1)) {
						return 571;
					} else {
						return -1;
					}
				} else if (alg.equals("RSA")) {
					ASN1Sequence param = (ASN1Sequence) algId.getParameters();
					byte[] version = ((ASN1Integer) param.getObjectAt(0)).getValue().toByteArray();
					byte[] modulus = ((ASN1Integer) param.getObjectAt(1)).getValue().toByteArray();
					byte[] publicExponent = ((ASN1Integer) param.getObjectAt(2)).getValue().toByteArray();
					byte[] privateExponent = ((ASN1Integer) param.getObjectAt(3)).getValue().toByteArray();
					byte[] prime1 = ((ASN1Integer) param.getObjectAt(4)).getValue().toByteArray();
					byte[] prime2 = ((ASN1Integer) param.getObjectAt(5)).getValue().toByteArray();
					byte[] exponent1 = ((ASN1Integer) param.getObjectAt(6)).getValue().toByteArray();
					byte[] exponent2 = ((ASN1Integer) param.getObjectAt(7)).getValue().toByteArray();
					byte[] coefficient = ((ASN1Integer) param.getObjectAt(8)).getValue().toByteArray();
					ASN1Primitive otherPrimeInfos;
					if (param.size() > 9) {
						otherPrimeInfos = param.getObjectAt(9).toASN1Primitive();
					}
					return ((int) Math.floor(modulus.length / 8)) * 64;
				} else if (alg.equals("DSA")) {
					ASN1Sequence param = (ASN1Sequence) algId.getParameters();
					byte[] p = ((ASN1Integer) param.getObjectAt(0)).getValue().toByteArray();
					byte[] q = ((ASN1Integer) param.getObjectAt(1)).getValue().toByteArray();
					byte[] g = ((ASN1Integer) param.getObjectAt(2)).getValue().toByteArray();
					return g.length * 8;
				}
			}
		} catch (IOException ex) {
		}
		return (key.getEncoded().length * 8);
	}

	public static final SecureRandom getDefaultSecureRandom() {
		try {
			return SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException ex) {
			return null;
		}
	}

	private final WeakReference<XNotes> _xnotesRef;

	public final XNKeyStore keyStore;

	private final Config.Security _security;
	private final SecureRandom _secureRandom;
	private final CertificateFactory _certificateFactory;
	private final SecretKeyFactory _passwordDerivationKeyFactory;
	private final HashEngine _keyHashEngine;
	private final HashEngine _objectHashEngine;
	private final HashEngine _sCryptHashEngine;

	public SecurityToolSet(XNotes xnotes, boolean createKeyStoreIfNotExist) throws ConfigurationException {
		_xnotesRef = new WeakReference<>(xnotes);

		_security = xnotes().config.security;

		String rngAlg = _security.random.algorithm != null && !_security.random.algorithm.trim().isEmpty() ? _security.random.algorithm : SecurityToolSet.getDefaultSecureRandom().getAlgorithm();
		String provider = getProviderForRandomAlgorithm(rngAlg);
		try {
			_secureRandom = SecureRandom.getInstance(rngAlg, provider);
		} catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
			throw new ConfigurationException(ex);
		}

		try {
			_certificateFactory = CertificateFactory.getInstance(_security.certificate.type, _security.certificate.provider != null ? _security.certificate.provider : this.getProviderForCertificateType(_security.certificate.type));
		} catch (CertificateException | NoSuchProviderException ex) {
			throw new ConfigurationException(ex);
		}

		try {
			_passwordDerivationKeyFactory = SecretKeyFactory.getInstance(_security.password.algorithm, _security.password.provider != null ? _security.password.provider : this.getProviderForSecretKeyAlgorithm(_security.password.algorithm));
		} catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
			throw new ConfigurationException(ex);
		}

		HashMap<String, Object> params = new HashMap<>();
		params.put("algorithm", _security.keyHash.algorithm);
		params.put("provider", _security.keyHash.provider != null ? _security.keyHash.provider : getProviderForKeyHashAlgorithm(_security.keyHash.algorithm));
		params.put("iterations", _security.keyHash.iterations);
		_keyHashEngine = _getConfiguredHashEngine(_security.keyHash.hashEngineClass, params, JCEHashEngine.class);

		params = new HashMap<>();
		params.put("algorithm", _security.objectHash.algorithm);
		params.put("provider", _security.objectHash.provider != null ? _security.objectHash.provider : getProviderForKeyHashAlgorithm(_security.objectHash.algorithm));
		params.put("iterations", _security.objectHash.iterations);
		_objectHashEngine = _getConfiguredHashEngine(_security.objectHash.hashEngineClass, params, JCEHashEngine.class);

		params = new HashMap<>();
		params.put("size", xnotes().config.network.pulse.parameters.size);
		params.put("saltSize", xnotes().config.network.pulse.parameters.saltSize);
		params.put("cost", xnotes().config.network.pulse.parameters.cost);
		params.put("blockSize", xnotes().config.network.pulse.parameters.blockSize);
		params.put("parallelization", xnotes().config.network.pulse.parameters.parallelization);
		_sCryptHashEngine = _getConfiguredHashEngine(xnotes().config.network.pulse.parameters.hashEngineClass, params, BCSCryptHashEngine.class);

		if (_security.keyStore.filePath == null || _security.keyStore.filePath.trim().isEmpty()) {
			_security.keyStore.filePath = Config.DEFAULT_KEYSTORE_FILEPATH;
		}

		keyStore = XNKeyStore.getInstance(this, _security.keyStore);

	}

	public final XNotes xnotes() {
		return _xnotesRef.get();
	}

	private HashEngine _getConfiguredHashEngine(String hashEngineClassName, Map<String, Object> parameters, Class<? extends HashEngine> defaultHashEngineClass) throws ConfigurationException {
		HashEngine he;
		if (hashEngineClassName != null) {
			try {
				Class<?> cls = Class.forName(hashEngineClassName);
				if (HashEngine.class.isAssignableFrom(cls)) {
					try {
						he = (HashEngine) cls.getConstructor().newInstance();
					} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
						throw new ConfigurationException("HashEngine Class '" + hashEngineClassName + "' cannot be instantiated: " + ex.getMessage());
					}
				} else {
					throw new ConfigurationException("Class '" + hashEngineClassName + "' must implement HashEngine interface.");
				}
			} catch (ClassNotFoundException ex) {
				throw new ConfigurationException("Class '" + hashEngineClassName + "' not found.");
			}
		} else {
			try {
				he = defaultHashEngineClass.getConstructor().newInstance();
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				throw new ConfigurationException(ex);
			}
		}
		he.setParameters(parameters);
		return he;
	}

	public String getProviderForRandomAlgorithm(String randomAlg) throws ConfigurationException {
		randomAlg = randomAlg.trim();
		String provider = _secureRandom != null ? _secureRandom.getProvider().getName() : _security.random.provider;
		if (provider == null || provider.trim().isEmpty()) {
			List<String> randomProviders = getProvidersForSecureRandomAlgorithm(randomAlg);
			if (randomProviders == null) {
				throw new ConfigurationException("Unsupported cipher algorithm '" + randomAlg + "'.");
			}
			provider = randomProviders.get(0);
		} else {
			if (Security.getProvider(provider) == null) {
				throw new ConfigurationException("Provider specified at 'security.random.provider' is not available.");
			}
		}
		return provider;
	}

	public String getProviderForKeyHashAlgorithm(String hashAlg) throws ConfigurationException {
		hashAlg = hashAlg.trim();
		String provider = _security.keyHash.provider;
		if (provider == null || provider.trim().isEmpty()) {
			List<String> digestProviders = getProvidersForMessageDigestAlgorithm(hashAlg);
			if (digestProviders == null) {
				throw new ConfigurationException("Unsupported key hash algorithm '" + hashAlg + "'.");
			}
			provider = digestProviders.get(0);
		} else {
			if (Security.getProvider(provider) == null) {
				throw new ConfigurationException("Provider specified at 'security.keyHash.provider' is not available.");
			}
		}
		return provider;
	}

	public String getProviderForIdentityAlgorithms(String keyAlg, String signAlg) throws ConfigurationException {
		keyAlg = keyAlg.trim();
		signAlg = signAlg.trim();
		String provider = _security.identity.provider;
		if (provider == null || provider.trim().isEmpty()) {
			List<String> kfProviders = getProvidersForKeyFactoryAlgorithm(keyAlg);
			if (kfProviders == null) {
				throw new ConfigurationException("Unsupported key factory algorithm '" + keyAlg + "'.");
			}
			List<String> signProviders = getProvidersForSignatureAlgorithm(signAlg);
			if (signProviders == null) {
				throw new ConfigurationException("Unsupported signature algorithm '" + signAlg + "'.");
			}
			provider = null;
			for (String cp : kfProviders) {
				if (signProviders.contains(cp)) {
					provider = cp;
					break;
				}
			}
			if (provider == null) {
				throw new ConfigurationException("Unsupported identity key algorithm '" + keyAlg + "' combined with signature algorithm '" + signAlg + "'.");
			}
		} else {
			if (Security.getProvider(provider) == null) {
				throw new ConfigurationException("Provider specified at 'security.identity.provider' is not available.");
			}
		}
		return provider;
	}

	public String getProviderForCipherKeyWrapAlgorithms(String keyAlg, String digest) throws ConfigurationException {
		keyAlg = keyAlg.trim();
		digest = digest.trim();
		String provider = _security.cipher.keyWrap.provider;
		if (provider == null || provider.trim().isEmpty()) {
			List<String> kfProviders = getProvidersForKeyFactoryAlgorithm(keyAlg);
			if (kfProviders == null) {
				throw new ConfigurationException("Unsupported key factory algorithm '" + keyAlg + "'.");
			}
			List<String> digestProviders = getProvidersForMessageDigestAlgorithm(digest);
			if (digestProviders == null) {
				throw new ConfigurationException("Unsupported digest key wrap algorithm '" + digest + "'.");
			}
			provider = null;
			for (String cp : kfProviders) {
				if (digestProviders.contains(cp)) {
					provider = cp;
					break;
				}
			}
			if (provider == null) {
				throw new ConfigurationException("Unsupported cipher key algorithm '" + keyAlg + "' combined with digest algorithm '" + digest + "'.");
			}
		} else {
			if (Security.getProvider(provider) == null) {
				throw new ConfigurationException("Provider specified at 'security.cipher.keyWrap.provider' is not available.");
			}
		}
		return provider;
	}

	public String getProviderForCipherKeyAlgorithm(String cipherAlg) throws ConfigurationException {
		cipherAlg = cipherAlg.trim();
		String provider = _security.cipher.key.provider;
		if (provider == null || provider.trim().isEmpty()) {
			int i = cipherAlg.indexOf("/");
			if (i == -1) {
				i = cipherAlg.length();
			}
			String keyAlg = cipherAlg.substring(0, i);
			List<String> keyGenProviders = getProvidersForKeyGeneratorAlgorithm(keyAlg);
			if (keyGenProviders == null) {
				throw new ConfigurationException("Unsupported cipher key algorithm '" + keyAlg + "'.");
			}
			List<String> cipherProviders = getProvidersForCipherAlgorithm(cipherAlg);
			if (cipherProviders == null) {
				throw new ConfigurationException("Unsupported cipher algorithm '" + cipherAlg + "'.");
			}
			provider = null;
			for (String kp : keyGenProviders) {
				if (cipherProviders.contains(kp)) {
					provider = kp;
					break;
				}
			}
			if (provider == null) {
				throw new ConfigurationException("Unsupported cipher key algorithm '" + keyAlg + "' combined with cipher algorithm '" + cipherAlg + "'.");
			}
		} else {
			if (Security.getProvider(provider) == null) {
				throw new ConfigurationException("Provider specified at 'security.cipher.key.provider' is not available.");
			}
		}
		return provider;
	}

	public String getProviderForSecretKeyAlgorithm(String algorithm) throws ConfigurationException {
		algorithm = algorithm.trim();
		String provider = _security.password.provider;
		if (provider == null || provider.trim().isEmpty()) {
			List<String> certProviders = getProvidersForSecretKeyFactoryAlgorithm(algorithm);
			if (certProviders == null) {
				throw new ConfigurationException("Unsupported Secret Key algorithm '" + algorithm + "'.");
			}
			provider = certProviders.get(0);
		} else {
			if (Security.getProvider(provider) == null) {
				throw new ConfigurationException("Provider specified at 'security.certificate.provider' is not available.");
			}
		}
		return provider;
	}

	public String getProviderForCertificateType(String type) throws ConfigurationException {
		type = type.trim();
		String provider = _security.certificate.provider;
		if (provider == null || provider.trim().isEmpty()) {
			List<String> certProviders = getProvidersForCertificateFactoryType(type);
			if (certProviders == null) {
				throw new ConfigurationException("Unsupported certificate type '" + type + "'.");
			}
			provider = certProviders.get(0);
		} else {
			if (Security.getProvider(provider) == null) {
				throw new ConfigurationException("Provider specified at 'security.certificate.provider' is not available.");
			}
		}
		return provider;
	}

	public SecureRandom getSecureRandom() {
		return _secureRandom;
	}

	public int randomInt(int low, int hi) {
		try {
			return (low + _secureRandom.nextInt(hi - low + 1));
		} catch (ConfigurationException ex) {
			return low - 1;
		}
	}

	public byte[] randomBytes() {
		return randomBytes(_security.random.randomIdSize);
	}

	public byte[] randomBytes(int numberOfBytes) {
		return randomBytes(numberOfBytes, _secureRandom);
	}

	public static byte[] randomBytes(int numberOfBytes, SecureRandom random) {
		try {
			byte[] bytes = new byte[numberOfBytes];
			(random != null ? random : SecurityToolSet.getDefaultSecureRandom()).nextBytes(bytes);
			return bytes;
		} catch (ConfigurationException ex) {
			return null;
		}
	}

	public String randomHexString() {
		return randomHexString(_security.random.randomIdSize);
	}

	public String randomHexString(int numberOfBytes) {
		return DatatypeConverter.printHexBinary(randomBytes(numberOfBytes));
	}

	public String randomID() {
		return randomID(_security.random.randomIdSize);
	}

	public String randomID(int numberOfBytes) {
		return DatatypeConverter.printBase58Binary(randomBytes(numberOfBytes));
	}

	public String randomPassword() {
		return randomPassword(_security.password.length);
	}

	private static final String _PASSWORD_CHARS = "01234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%&*-_=+?/abcdefghijklmnopqrstuvwxyz";

	public static String randomPassword(int length, SecureRandom random) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(_PASSWORD_CHARS.charAt((random != null ? random : SecurityToolSet.getDefaultSecureRandom()).nextInt(_PASSWORD_CHARS.length())));
		}
		return sb.toString();
	}

	public String randomPassword(int length) {
		return randomPassword(length, _secureRandom);
	}

	public byte[] keyHash(byte[] bytes) {
		return _keyHashEngine.hash(bytes, _security.keyHash.iterations, null);
	}

	public byte[] objectHash(byte[] bytes) throws ConfigurationException {
		return _objectHashEngine.hash(bytes, _security.objectHash.iterations, null);
	}

	public byte[][] sCrypt(byte[] bytes) {
		byte[] salt = randomBytes((int) _sCryptHashEngine.getParameter("saltSize"));
		return new byte[][]{_sCryptHashEngine.hash(bytes, 1, salt), salt};
	}

	public KeyPair generateKeyPairForIdentity() throws ConfigurationException {
		String keyAlg = _security.identity.algorithm;
		String signAlg = _security.identity.signAlg;
		String provider = getProviderForIdentityAlgorithms(keyAlg, signAlg);
		int keySize = _security.identity.keySize;
		return _generateKeyPair(keyAlg, provider, keySize);
	}

	public KeyPair generateKeyPairForCipher() throws ConfigurationException {
		String keyAlg = _security.cipher.keyWrap.algorithm;
		String digestAlg = _security.cipher.keyWrap.digest;
		String provider = getProviderForCipherKeyWrapAlgorithms(keyAlg, digestAlg);
		int keySize = _security.cipher.keyWrap.keySize;
		return _generateKeyPair(keyAlg, provider, keySize);
	}

	private KeyPair _generateKeyPair(String alg, String provider, int keySize) throws ConfigurationException {
		try {
			KeyPairGenerator keyPairGenerator = provider != null ? KeyPairGenerator.getInstance(alg, provider) : KeyPairGenerator.getInstance(alg);
			keyPairGenerator.initialize(keySize, _secureRandom);
			return keyPairGenerator.generateKeyPair();
		} catch (ConfigurationException | NoSuchAlgorithmException | NoSuchProviderException ex) {
			throw new ConfigurationException(ex);
		}
	}

	public Certificate generateSelfSignedCertificate(Map<String, String> nameMap, KeyPair issuerKeyPair) throws ConfigurationException {
		return _generateCertificate(nameMap, issuerKeyPair.getPrivate(), issuerKeyPair.getPublic(), nameMap, issuerKeyPair.getPublic(), true);
	}

	public Certificate generateCertificate(PrivateKey issuerPrivateKey, Certificate issuerCertificate, Map<String, String> subjectNameMap, PublicKey subjectPublicKey) throws ConfigurationException {
		return _generateCertificate(this.getCertificateX509NameMap(issuerCertificate), issuerPrivateKey, issuerCertificate.getPublicKey(), subjectNameMap, subjectPublicKey, false);
	}

	private Certificate _generateCertificate(Map<String, String> issuerNameMap, PrivateKey issuerPrivateKey, PublicKey issuerPublicKey, Map<String, String> subjectNameMap, PublicKey subjectPublicKey, boolean isCA) throws ConfigurationException {
		if (areAlgorithmsEqual(issuerPrivateKey, _security.identity.algorithm)) {
			if (_security.certificate.type == null || _security.certificate.type.trim().equalsIgnoreCase("X.509")) {
				X500Name issuerX500Name = _getX500NameFromMap(issuerNameMap);
				X500Name subjectX500Name = _getX500NameFromMap(subjectNameMap);
				Date notBefore = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(notBefore);
				int f;
				if (_security.certificate.validityUnit.equalsIgnoreCase("MILLISECOND")) {
					f = Calendar.MILLISECOND;
				} else if (_security.certificate.validityUnit.equalsIgnoreCase("SECOND")) {
					f = Calendar.SECOND;
				} else if (_security.certificate.validityUnit.equalsIgnoreCase("MINUTE")) {
					f = Calendar.MINUTE;
				} else if (_security.certificate.validityUnit.equalsIgnoreCase("HOUR")) {
					f = Calendar.HOUR;
				} else if (_security.certificate.validityUnit.equalsIgnoreCase("DAY")) {
					f = Calendar.DAY_OF_MONTH;
				} else if (_security.certificate.validityUnit.equalsIgnoreCase("MONTH")) {
					f = Calendar.MONTH;
				} else {
					f = Calendar.YEAR;
				}
				c.add(f, _security.certificate.validity);
				Date notAfter = c.getTime();
				BigInteger serial = new BigInteger(randomBytes(64));

				X509v3CertificateBuilder certGen;
				try {
					certGen = new JcaX509v3CertificateBuilder(
							issuerX500Name,
							serial,
							notBefore, notAfter,
							subjectX500Name,
							subjectPublicKey);
					int keyUsage = 0;
					if (isCA) {
						certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
						keyUsage |= KeyUsage.keyCertSign | KeyUsage.cRLSign;
					}
					if (areAlgorithmsEqual(subjectPublicKey, _security.cipher.keyWrap.algorithm)) {
						keyUsage |= KeyUsage.dataEncipherment | KeyUsage.keyEncipherment | KeyUsage.keyAgreement;
					}
					if (areAlgorithmsEqual(subjectPublicKey, _security.identity.algorithm)) {
						keyUsage |= KeyUsage.nonRepudiation | KeyUsage.digitalSignature;
					}
					certGen.addExtension(Extension.keyUsage, true, new KeyUsage(keyUsage));
					certGen.addExtension(Extension.authorityKeyIdentifier, false, new AuthorityKeyIdentifier(this.keyHash(issuerPublicKey.getEncoded())));
					certGen.addExtension(Extension.issuerAlternativeName, false, new GeneralNames(new GeneralName(issuerX500Name)));
					certGen.addExtension(Extension.subjectKeyIdentifier, false, new SubjectKeyIdentifier(this.keyHash(subjectPublicKey.getEncoded())));
					certGen.addExtension(Extension.subjectAlternativeName, false, new GeneralNames(new GeneralName(subjectX500Name)));
				} catch (CertIOException ex) {
					throw new ConfigurationException(ex);
				}
				try {
					String signProvider = getProviderForIdentityAlgorithms(_security.identity.algorithm, _security.identity.signAlg);
					String certProvider = getProviderForCertificateType(_security.certificate.type);
					X509Certificate cert;
					ContentSigner sigGen;
					try {
						Signature sig = Signature.getInstance(_security.identity.signAlg, signProvider);
						synchronized (sig) {
							sig.initSign(issuerPrivateKey);
							sigGen = new ContentSigner() {
								@Override
								public AlgorithmIdentifier getAlgorithmIdentifier() {
									return ASN1.getAlgorithmIdentifierForSignatureAlgorithm(_security.identity.signAlg);
								}

								@Override
								public OutputStream getOutputStream() {
									return new OutputStream() {
										@Override
										public void write(byte[] bytes, int off, int len) throws IOException {
											try {
												sig.update(bytes, off, len);
											} catch (SignatureException e) {
												throw new IOException("Exception in content signer: " + e.getMessage(), e);
											}
										}

										@Override
										public void write(byte[] bytes) throws IOException {
											try {
												sig.update(bytes);
											} catch (SignatureException e) {
												throw new IOException("Exception in content signer: " + e.getMessage(), e);
											}
										}

										@Override
										public void write(int b) throws IOException {
											try {
												sig.update((byte) b);
											} catch (SignatureException e) {
												throw new IOException("Exception in content signer: " + e.getMessage(), e);
											}
										}

										byte[] getSignature() throws SignatureException {
											return sig.sign();
										}
									};
								}

								@Override
								public byte[] getSignature() {
									try {
										return sig.sign();
									} catch (SignatureException ex) {
										return null;
									}
								}
							};
						}
					} catch (IllegalArgumentException ex) {
						sigGen = new JcaContentSignerBuilder(_security.identity.signAlg).setProvider(signProvider).build(issuerPrivateKey);
					}
					cert = new JcaX509CertificateConverter().setProvider(certProvider).getCertificate(certGen.build(sigGen));
					cert.checkValidity(new Date());
					cert.verify(issuerPublicKey);
					return cert;
				} catch (OperatorCreationException | CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException | IllegalArgumentException | ConfigurationException ex) {
					throw new ConfigurationException(ex);
				}
			} else {
				throw new ConfigurationException("Unsupported Certificate Type '" + _security.certificate.type + "'.");
			}
		} else {
			throw new ConfigurationException("The algorithm of the provided Issuer Private Key '" + issuerPrivateKey.getAlgorithm() + "' is different from the configured Identity Algorithm '" + _security.identity.algorithm + "'.");
		}
	}

	private static X500Name _getX500NameFromMap(Map<String, String> nameMap) {
		X500NameBuilder x500NameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
		for (Entry<String, String> e : nameMap.entrySet()) {
			if (e.getValue() != null && !e.getValue().trim().isEmpty()) {
				switch (e.getKey()) {
					case "CN":
						x500NameBuilder.addRDN(BCStyle.CN, e.getValue().trim());
						break;
					case "UID":
						x500NameBuilder.addRDN(BCStyle.UID, e.getValue().trim());
						break;
					case "DC":
						x500NameBuilder.addRDN(BCStyle.DC, e.getValue().trim());
						break;
					case "O":
						x500NameBuilder.addRDN(BCStyle.O, e.getValue().trim());
						break;
					case "OU":
						x500NameBuilder.addRDN(BCStyle.OU, e.getValue().trim());
						break;
					case "STREET":
						x500NameBuilder.addRDN(BCStyle.STREET, e.getValue().trim());
						break;
					case "L":
						x500NameBuilder.addRDN(BCStyle.L, e.getValue().trim());
						break;
					case "ST":
						x500NameBuilder.addRDN(BCStyle.ST, e.getValue().trim());
						break;
					case "C":
						x500NameBuilder.addRDN(BCStyle.C, e.getValue().trim());
						break;
					default:
						break;
				}
			}
		}
		return x500NameBuilder.build();
	}

	public Certificate certificateFromBase64String(String certBase64) throws SecurityException {
		return certificateFromBytes(DatatypeConverter.parseBase64Binary(certBase64));
	}

	public Certificate certificateFromBytes(byte[] certBytes) throws SecurityException {
		return _certificateFromInputStream(new ByteArrayInputStream(certBytes));
	}

	private Certificate _certificateFromInputStream(InputStream is) throws SecurityException {
		try {
			return _certificateFactory.generateCertificate(is);
		} catch (CertificateException ex) {
			throw new SecurityException(ex);
		}
	}

	public Map<String, String> getCertificateX509NameMap(Certificate cert) throws ConfigurationException {
		Map<String, String> map = new HashMap<>();
		if ((_security.certificate.type == null || _security.certificate.type.trim().equalsIgnoreCase("X.509"))) {
			if (X509Certificate.class.isInstance(cert)) {
				X500Principal p = ((X509Certificate) cert).getSubjectX500Principal();
				String[] c = p.getName().split("\\,");
				for (String a : c) {
					String[] b = a.split("\\=", 2);
					map.put(b[0].trim(), b.length > 1 ? b[1].trim() : "");
				}
				return map;
			} else {
				throw new ConfigurationException("Unsupported Certificate Class '" + cert.getClass().getName() + "'.");
			}
		} else {
			throw new ConfigurationException("Unsupported Certificate Type '" + _security.certificate.type + "'.");
		}
	}

	public byte[] sign(PrivateKey privateKey, String signatureAlg, byte[] bytesToBeSigned) throws SecurityException {
		if (privateKey != null) {
			try {
				Signature signature = Signature.getInstance(signatureAlg, this.getProviderForIdentityAlgorithms(privateKey.getAlgorithm(), signatureAlg));
				synchronized (signature) {
					signature.initSign(privateKey);
					signature.update(bytesToBeSigned);
					return signature.sign();
				}
			} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException | IllegalStateException ex) {
				throw new SecurityException(ex);
			}
		} else {
			throw new SecurityException("Cannot sign with null private key.");
		}
	}

	public boolean verify(Certificate cert, String signatureAlg, byte[] bytesToBeVerified, byte[] signatureToBeVerified) throws SecurityException {
		return verify(cert.getPublicKey(), signatureAlg, bytesToBeVerified, signatureToBeVerified);
	}

	public boolean verify(PublicKey publicKey, String signatureAlg, byte[] bytesToBeVerified, byte[] signatureToBeVerified) throws SecurityException {
		if (publicKey != null) {
			try {
				Signature signature = Signature.getInstance(signatureAlg, this.getProviderForIdentityAlgorithms(publicKey.getAlgorithm(), signatureAlg));
				synchronized (signature) {
					signature.initVerify(publicKey);
					signature.update(bytesToBeVerified);
					return signature.verify(signatureToBeVerified);
				}
			} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException ex) {
				throw new SecurityException(ex);
			}
		} else {
			throw new SecurityException("Cannot verify with null public key.");
		}
	}

	public boolean verify(byte[] certBytes, byte[] bytesToBeVerified, SignatureObject sign) throws SecurityException {
		return _verify(new ByteArrayInputStream(certBytes), bytesToBeVerified, sign);
	}

	public boolean verify(byte[] bytesToBeVerified, SignatureObject sign) throws SecurityException {
		if (bytesToBeVerified != null && sign.signer != null) {
			try {
				Certificate cert = this.keyStore.getCertificate(DatatypeConverter.printBase58ToHexBinary(sign.signer));
				return verify(cert.getEncoded(), bytesToBeVerified, sign);
			} catch (KeyStoreException ex) {
				throw new SecurityException("Could not find Certificate for Signer '" + sign.signer + "'.");
			} catch (CertificateEncodingException ex) {
				throw new SecurityException("Certificate for Signer '" + sign.signer + "' is corrupted.");
			} catch (IllegalArgumentException ex) {
				throw new SecurityException("Invalid Signer ID '" + sign.signer + "'.");
			}
		} else {
			return false;
		}
	}

	private boolean _verify(InputStream is, byte[] bytesToBeVerified, SignatureObject sign) throws SecurityException {
		Certificate cert = _certificateFromInputStream(is);
		if (areAlgorithmsEqual(cert.getPublicKey(), sign.signInfo.key.algorithm)) {
			return verify(cert, sign.signInfo.algorithm, bytesToBeVerified, sign.data);
		} else {
			throw new SecurityException("Inconsitency between algorithm in signature information (" + sign.signInfo.key + ") and certificate algorithm (" + cert.getPublicKey().getAlgorithm() + ")");
		}
	}

	public byte[][] derivedKeyForPassword(String password) throws ConfigurationException {
		return this.derivedKeyForPassword(password, null);
	}

	public byte[][] derivedKeyForPassword(String password, byte[] salt) throws ConfigurationException {
		if (_passwordDerivationKeyFactory.getAlgorithm().toUpperCase().startsWith("PBKDF")) {
			int iterations;
			if (_security.password.parameters == null || !_security.password.parameters.containsKey("iterations")) {
				iterations = 8;
			} else {
				Object itParam = _security.password.parameters.get("iterations");
				iterations = String.class.isInstance(itParam) ? Integer.parseInt((String) itParam) : (int) itParam;
			}
			int length;
			if (_security.password.parameters == null || !_security.password.parameters.containsKey("length")) {
				length = 8;
			} else {
				Object lParam = _security.password.parameters.get("length");
				length = String.class.isInstance(lParam) ? Integer.parseInt((String) lParam) : (int) lParam;
			}
			if (salt == null || salt.length < 1) {
				int saltLength;
				if (_security.password.parameters == null || !_security.password.parameters.containsKey("saltLength")) {
					saltLength = length;
				} else {
					Object slParam = _security.password.parameters.get("saltLength");
					saltLength = String.class.isInstance(slParam) ? Integer.parseInt((String) slParam) : (int) slParam;
				}
				salt = new byte[saltLength];
				_secureRandom.nextBytes(salt);
			}
			try {
				return new byte[][]{salt, _passwordDerivationKeyFactory.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, length)).getEncoded()};
			} catch (InvalidKeySpecException ex) {
				throw new ConfigurationException(ex);
			}
		} else {
			throw new ConfigurationException("Unsupported password derivation algorithm '" + _passwordDerivationKeyFactory.getAlgorithm() + "'.");
		}
	}

	public static final class XNKeyStore {

		public static XNKeyStore getInstance(SecurityToolSet sts, Config.Security.KeyStore keyStoreConfig) throws ConfigurationException {
			FileUtil.PathFileComponents ksfc = FileUtil.getPathFileComponents(keyStoreConfig.filePath, null);
			String keyStoreActualFilePath = FileUtil.getActualPath(ksfc.filePath);
			if (Files.notExists(Paths.get(keyStoreActualFilePath))) {
				if (keyStoreConfig.password == null) {
					keyStoreConfig.password = sts.randomPassword();
				}
				if (!ksfc.path.equals(FileUtil.getPath("~"))) {
					FileUtil.createPathIfNotExist(ksfc.path);
				}
				try {
					char[] cp = keyStoreConfig.password.toCharArray();
					KeyStore ks = getKeyStoreInstance(keyStoreConfig.type, keyStoreConfig.provider);
					ks.load(null, cp);
					saveKeyStore(ks, keyStoreActualFilePath, cp);
				} catch (IOException | NoSuchAlgorithmException | CertificateException ex) {
					throw new ConfigurationException(ex);
				}
			}
			return new XNKeyStore(keyStoreConfig, keyStoreActualFilePath);
		}

		protected static KeyStore getKeyStoreInstance(String type, String provider) throws ConfigurationException {
			String ksType = type != null ? type : KeyStore.getDefaultType();
			try {
				return provider != null ? KeyStore.getInstance(ksType, provider) : KeyStore.getInstance(ksType);
			} catch (KeyStoreException | NoSuchProviderException ex) {
				throw new ConfigurationException(ex);
			}
		}

		private static void _load(KeyStore keyStore, String actualFilePath, char[] password) throws ConfigurationException {
			try {
				try (InputStream is = new FileInputStream(actualFilePath)) {
					keyStore.load(is, password);
				}
			} catch (IOException | NoSuchAlgorithmException | CertificateException ex) {
				throw new ConfigurationException(ex);
			}
		}

		protected synchronized static void saveKeyStore(KeyStore keyStore, String actualFilePath, char[] password) throws ConfigurationException {
			_saveKeyStore(keyStore, actualFilePath, password);
			_load(keyStore, actualFilePath, password);
		}

		private static void _saveKeyStore(KeyStore keyStore, String actualFilePath, char[] password) throws ConfigurationException {
			try {
				try (OutputStream os = new FileOutputStream(actualFilePath)) {
					keyStore.store(os, password);
				} catch (KeyStoreException ex) {
					throw new ConfigurationException(ex);
				}
			} catch (IOException | NoSuchAlgorithmException | CertificateException ex) {
				throw new ConfigurationException(ex);
			}
		}

		private final Config.Security.KeyStore _keyStoreConfig;
		private final KeyStore _jks;
		private final String _actualFilePath;

		private XNKeyStore(Config.Security.KeyStore keyStoreConfig, String actualFilePath) throws ConfigurationException {
			_keyStoreConfig = keyStoreConfig;
			_actualFilePath = actualFilePath;
			_jks = getKeyStoreInstance(_keyStoreConfig.type, _keyStoreConfig.provider);
			if (_keyStoreConfig.password != null) {
				_load(_jks, _actualFilePath, _keyStoreConfig.password.toCharArray());
			} else {
				throw new ConfigurationException("Cannot load KeyStore: No password set in Configuration file at security.keyStore.password.");
			}
		}

		public String getFilePath() {
			return _actualFilePath;
		}

		public final KeyStore jks() throws ConfigurationException {
			return _jks;
		}

		public void changePassword(String alias, String oldPassword, String newKPassword) throws KeyStoreException {
			this.changePassword(alias, oldPassword, newKPassword, true);
		}

		public synchronized void changePassword(String alias, String oldPassword, String newKPassword, boolean save) throws KeyStoreException {
			try {
				KeyStore.Entry entry = jks().getEntry(alias, new PasswordProtection(oldPassword.toCharArray()));
				if (entry != null) {
					jks().deleteEntry(alias);
					jks().setEntry(alias, entry, new PasswordProtection(newKPassword.toCharArray()));
					if (save) {
						this.save();
					}
				}
			} catch (NoSuchAlgorithmException | UnrecoverableEntryException ex) {
				throw new KeyStoreException(ex);
			}
		}

		public boolean contains(String alias) throws KeyStoreException {
			try {
				return jks().containsAlias(alias);
			} catch (ConfigurationException ex) {
				throw new KeyStoreException(ex);
			}
		}

		public Certificate getCertificate(String alias) throws KeyStoreException {
			try {
				return jks().getCertificate(alias);
			} catch (ConfigurationException ex) {
				throw new KeyStoreException(ex);
			}
		}

		public void setCertificate(String alias, Certificate cert) throws KeyStoreException {
			this.setCertificate(alias, cert, true);
		}

		public synchronized void setCertificate(String alias, Certificate cert, boolean save) throws KeyStoreException {
			if (!jks().containsAlias(alias)) {
				try {
					jks().setCertificateEntry(alias, cert);
					if (save) {
						this.save();
					}
				} catch (ConfigurationException ex) {
					throw new KeyStoreException(ex);
				}
			}
		}

		public PublicKey getPublicKey(String alias) throws KeyStoreException {
			try {
				Certificate cert = jks().getCertificate(alias);
				if (cert != null) {
					return cert.getPublicKey();
				} else {
					return null;
				}
			} catch (ConfigurationException ex) {
				throw new KeyStoreException(ex);
			}
		}

		public void setPublicKey(String alias, PublicKey publicKey) throws KeyStoreException {
			this.setPublicKey(alias, publicKey, true);
		}

		public synchronized void setPublicKey(String alias, PublicKey publicKey, boolean save) throws KeyStoreException {
			try {
				this.setCertificate(alias, _dummyCertificateForPublicKey(publicKey), save);
			} catch (OperatorCreationException ex) {
				throw new KeyStoreException(ex);
			}
		}

		private Certificate _dummyCertificateForPublicKey(PublicKey publicKey) throws OperatorCreationException {
			try {
				ASN1EncodableVector tbsCert = new ASN1EncodableVector();
				tbsCert.add(new DERTaggedObject(true, 0, new ASN1Integer(2)));
				tbsCert.add(new ASN1Integer(0));
				ASN1Sequence sigAlg = (ASN1Sequence) new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.10040.4.3"), null).toASN1Primitive();
				tbsCert.add(sigAlg);
				ASN1EncodableVector cn = new ASN1EncodableVector();
				cn.add(new ASN1ObjectIdentifier("2.5.4.3"));
				cn.add(new DERUTF8String(""));
				ASN1EncodableVector name = new ASN1EncodableVector();
				name.add(new DERSequence(cn));
				ASN1EncodableVector info = new ASN1EncodableVector();
				info.add(new DERSet(name));
				tbsCert.add(new DERSequence(info));
				ASN1EncodableVector validity = new ASN1EncodableVector();
				validity.add(new ASN1GeneralizedTime("00000101000001Z"));
				validity.add(new ASN1GeneralizedTime("99991231235959Z"));
				tbsCert.add(new DERSequence(validity));
				tbsCert.add(new DERSequence(info));
				tbsCert.add((ASN1Sequence) ASN1Primitive.fromByteArray(publicKey.getEncoded()));
				ASN1EncodableVector cert = new ASN1EncodableVector();
				cert.add(new DERSequence(tbsCert));
				cert.add(new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.10040.4.3"), null).toASN1Primitive());
				cert.add(new DERBitString(new byte[0]));
				return CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(new DERSequence(cert).getEncoded()));
			} catch (IOException | CertificateException ex) {
				throw new OperatorCreationException(ex.getMessage());
			}
		}

		public PrivateKey getPrivateKey(String alias, String password) throws KeyStoreException {
			try {
				return (PrivateKey) jks().getKey(alias, password.toCharArray());
			} catch (ConfigurationException | NoSuchAlgorithmException | UnrecoverableKeyException | ClassCastException ex) {
				throw new KeyStoreException(ex);
			}
		}

		public void setPrivateKey(String alias, PrivateKey privateKey, PublicKey publicKey, String keyPass) throws KeyStoreException {
			this.setPrivateKey(alias, privateKey, publicKey, keyPass, true);
		}

		public void setPrivateKey(String alias, PrivateKey privateKey, PublicKey publicKey, String keyPass, boolean save) throws KeyStoreException {
			try {
				this.setPrivateKey(alias, privateKey, keyPass, new Certificate[]{_dummyCertificateForPublicKey(publicKey)}, save);
			} catch (OperatorCreationException ex) {
				throw new KeyStoreException(ex);
			}
		}

		public void setPrivateKey(String alias, PrivateKey privateKey, String keyPass, Certificate[] chain) throws KeyStoreException {
			this.setPrivateKey(alias, privateKey, keyPass, chain, true);
		}

		public synchronized void setPrivateKey(String alias, PrivateKey privateKey, String keyPass, Certificate[] chain, boolean save) throws KeyStoreException {
			try {
				jks().setKeyEntry(alias, privateKey, keyPass.toCharArray(), chain);
				if (save) {
					this.save();
				}
			} catch (ConfigurationException ex) {
				throw new KeyStoreException(ex);
			}
		}

		public SecretKey getSecretKey(String alias, String password) throws KeyStoreException {
			try {
				return (SecretKey) jks().getKey(alias, password.toCharArray());
			} catch (ConfigurationException | NoSuchAlgorithmException | UnrecoverableKeyException | ClassCastException ex) {
				throw new KeyStoreException(ex);
			}
		}

		public void setSecretKey(String alias, SecretKey key, String password) throws KeyStoreException {
			this.setSecretKey(alias, key, password, true);
		}

		public synchronized void setSecretKey(String alias, SecretKey key, String password, boolean save) throws KeyStoreException {
			try {
				jks().setKeyEntry(alias, key, password.toCharArray(), null);
				if (save) {
					this.save();
				}
			} catch (ConfigurationException ex) {
				throw new KeyStoreException(ex);
			}
		}

		public void delete(String alias) throws KeyStoreException {
			this.delete(alias, true);
		}

		public synchronized void delete(String alias, boolean save) throws KeyStoreException {
			try {
				jks().deleteEntry(alias);
				if (save) {
					this.save();
				}
			} catch (ConfigurationException ex) {
				throw new KeyStoreException(ex);
			}
		}

		public void save() throws ConfigurationException {
			if (_keyStoreConfig.password != null) {
				saveKeyStore(jks(), _actualFilePath, _keyStoreConfig.password.toCharArray());
			} else {
				throw new ConfigurationException("Cannot save KeyStore: No password set in Configuration file at security.keyStore.password.");
			}
		}

		@Override
		public void finalize() throws Throwable {
			super.finalize();
			_saveKeyStore(_jks, _actualFilePath, _keyStoreConfig.password.toCharArray());
		}

	}

}
