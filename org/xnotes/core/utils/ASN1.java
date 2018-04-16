/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.utils;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.xnotes.core.security.ots.xnmss.XNMSS;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class ASN1 {

	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA1 = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA224 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA384 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA512 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_224 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_224, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_256, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_384 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_384, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_512 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_512, null);

	public static final ASN1ObjectIdentifier OBJECT_IDENTIFIER_EC = X9ObjectIdentifiers.id_ecPublicKey;
	public static final ASN1ObjectIdentifier OBJECT_IDENTIFIER_RSA = PKCSObjectIdentifiers.rsaEncryption;
	public static final ASN1ObjectIdentifier OBJECT_IDENTIFIER_DSA = new ASN1ObjectIdentifier("1.2.840.10040.4.1");

	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP112R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp112r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP112R2 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp112r2);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT113R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect113r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT113R2 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect113r2);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP128R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp128r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP128R2 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp128r2);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT131R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect131r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT131R2 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect131r2);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP160K1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp160k1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP160r1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp160r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP160R2 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp160r2);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2PNB163V1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2pnb163v1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2PNB163V2 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2pnb163v2);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2PNB163V3 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2pnb163v3);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT163K1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect163k1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT163R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect163r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT163R2 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect163r2);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2PNB176W1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2pnb176w1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2TNB191V1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2tnb191v1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2TNB191V2 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2tnb191v2);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2TNB191V3 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2tnb191v3);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2ONB191V4 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2onb191v4);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2ONB191V5 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2onb191v5);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP192K1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp192k1);
    public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_PRIME192V2 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.prime192v2);
    public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_PRIME192V3 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.prime192v3);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT193R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect193r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT193R2 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect193r2);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2PNB208W1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2pnb208w1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP224K1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp224k1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP224R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp224r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT233K1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect233k1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT233R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect233r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2TNB239V1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2tnb239v1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2TNB239V2 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2tnb239v2);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2TNB239V3 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2tnb239v3);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2ONB239V4 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2onb239v4);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2ONB239V5 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2onb239v5);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT239K1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect239k1);
    public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_PRIME239V1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.prime239v1);
    public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_PRIME239V2 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.prime239v2);
    public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_PRIME239V3 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.prime239v3);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP256K1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp256k1);
    public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_PRIME256V1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.prime256v1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2PNB272W1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2pnb272w1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT283K1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect283k1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT283R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect283r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2PNB304W1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2pnb304w1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2TNB359V1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2tnb359v1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2PNB368W1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2pnb368w1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP384R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp384r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT409K1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect409k1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT409R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect409r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_C2TNB431R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, X9ObjectIdentifiers.c2tnb431r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECP521R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.secp521r1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT571K1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect571k1);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_EC_SECT571R1 = new AlgorithmIdentifier(OBJECT_IDENTIFIER_EC, SECObjectIdentifiers.sect571r1);

	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA1WITHECDSA = new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA1, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA224WITHECDSA = new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA224, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA256WITHECDSA = new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA256, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA384WITHECDSA = new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA384, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA512WITHECDSA = new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA512, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_224WITHECDSA = new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_224, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_256WITHECDSA = new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_256, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_384WITHECDSA = new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_384, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_512WITHECDSA = new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_512, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA1WITHRSA = new AlgorithmIdentifier(OIWObjectIdentifiers.sha1WithRSA, null); // or PKCSObjectIdentifiers.sha1WithRSAEncryption
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA224WITHRSA = new AlgorithmIdentifier(PKCSObjectIdentifiers.sha224WithRSAEncryption, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA256WITHRSA = new AlgorithmIdentifier(PKCSObjectIdentifiers.sha256WithRSAEncryption, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA384WITHRSA = new AlgorithmIdentifier(PKCSObjectIdentifiers.sha384WithRSAEncryption, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA512WITHRSA = new AlgorithmIdentifier(PKCSObjectIdentifiers.sha512WithRSAEncryption, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_224WITHRSA = new AlgorithmIdentifier(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_256WITHRSA = new AlgorithmIdentifier(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_384WITHRSA = new AlgorithmIdentifier(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_512WITHRSA = new AlgorithmIdentifier(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA1WITHDSA = new AlgorithmIdentifier(OIWObjectIdentifiers.dsaWithSHA1, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA224WITHDSA = new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha224, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA256WITHDSA = new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha256, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA384WITHDSA = new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha384, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA512WITHDSA = new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha512, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_224WITHDSA = new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_224, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_256WITHDSA = new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_256, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_384WITHDSA = new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_384, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_SHA3_512WITHDSA = new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_512, null);
	public static final AlgorithmIdentifier ALGORITHM_IDENTIFIER_XNMSS = XNMSS.AlgorithmIdentifiers.XNMSS;
	
	public static String detectKeyAlgorithmForKeyBytes(byte[] keyBytes) throws InvalidKeySpecException {
		try {
			ASN1Primitive asn1 = ASN1Primitive.fromByteArray(keyBytes);
			if (ASN1Sequence.class.isInstance(asn1)) {
				if (((ASN1Sequence)asn1).size()>0) {
					AlgorithmIdentifier algId = null;
					if (ASN1Sequence.class.isInstance(((ASN1Sequence)asn1).getObjectAt(0))
							&& ((ASN1Sequence)((ASN1Sequence)asn1).getObjectAt(0)).size()>0) {
						// X.509 (Public Key)
						if (ASN1ObjectIdentifier.class.isInstance(((ASN1Sequence)((ASN1Sequence)asn1).getObjectAt(0)).getObjectAt(0))) {
							algId = new AlgorithmIdentifier(
									(ASN1ObjectIdentifier)((ASN1Sequence)((ASN1Sequence)asn1).getObjectAt(0)).getObjectAt(0),
									((ASN1Sequence)((ASN1Sequence)asn1).getObjectAt(0)).size() > 1 ? ((ASN1Sequence)((ASN1Sequence)asn1).getObjectAt(0)).getObjectAt(1) : null
							);
						}
					} else if (ASN1Integer.class.isInstance(((ASN1Sequence)asn1).getObjectAt(0))
							&& ASN1Sequence.class.isInstance(((ASN1Sequence)asn1).getObjectAt(1))
							&& ((ASN1Sequence)((ASN1Sequence)asn1).getObjectAt(1)).size()>0) {
						// PKCS#8 (Private Key)
						if (ASN1ObjectIdentifier.class.isInstance(((ASN1Sequence)((ASN1Sequence)asn1).getObjectAt(1)).getObjectAt(0))) {
							algId = new AlgorithmIdentifier(
									(ASN1ObjectIdentifier)((ASN1Sequence)((ASN1Sequence)asn1).getObjectAt(1)).getObjectAt(0),
									((ASN1Sequence)((ASN1Sequence)asn1).getObjectAt(1)).size() > 1 ? ((ASN1Sequence)((ASN1Sequence)asn1).getObjectAt(1)).getObjectAt(1) : null
							);
						}
					}
					if (algId != null) {
						try {
							return getKeyAlgorithmForAlgorithmIdentifier(algId);
						} catch (IllegalArgumentException ex) {
							throw new InvalidKeySpecException(ex);
						}
					}
				}
			}
		} catch (IOException ex) {
			throw new InvalidKeySpecException(ex);
		}
		throw new InvalidKeySpecException("Key Format Exception.");
	}

	public static String getKeyAlgorithmForAlgorithmIdentifier(AlgorithmIdentifier algId) throws IllegalArgumentException {
		if (algId!= null && OBJECT_IDENTIFIER_EC.equals(algId.getAlgorithm())) {
			return "EC";
		} else if (algId!= null && OBJECT_IDENTIFIER_RSA.equals(algId.getAlgorithm())) {
			return "RSA";
		} else if (algId!= null && OBJECT_IDENTIFIER_DSA.equals(algId.getAlgorithm())) {
			return "DSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA1AndEC.equals(algId)) {
			return "XNMSSwithSHA1AndEC";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA224AndEC.equals(algId)) {
			return "XNMSSwithSHA224AndEC";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA256AndEC.equals(algId)) {
			return "XNMSSwithSHA256AndEC";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA384AndEC.equals(algId)) {
			return "XNMSSwithSHA384AndEC";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA512AndEC.equals(algId)) {
			return "XNMSSwithSHA512AndEC";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_224AndEC.equals(algId)) {
			return "XNMSSwithSHA3-224AndEC";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_256AndEC.equals(algId)) {
			return "XNMSSwithSHA3-256AndEC";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_384AndEC.equals(algId)) {
			return "XNMSSwithSHA3-384AndEC";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_512AndEC.equals(algId)) {
			return "XNMSSwithSHA3-512AndEC";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA1AndRSA.equals(algId)) {
			return "XNMSSwithSHA1AndRSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA224AndRSA.equals(algId)) {
			return "XNMSSwithSHA224AndRSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA256AndRSA.equals(algId)) {
			return "XNMSSwithSHA256AndRSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA384AndRSA.equals(algId)) {
			return "XNMSSwithSHA384AndRSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA512AndRSA.equals(algId)) {
			return "XNMSSwithSHA512AndRSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_224AndRSA.equals(algId)) {
			return "XNMSSwithSHA3-224AndRSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_256AndRSA.equals(algId)) {
			return "XNMSSwithSHA3-256AndRSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_384AndRSA.equals(algId)) {
			return "XNMSSwithSHA3-384AndRSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_512AndRSA.equals(algId)) {
			return "XNMSSwithSHA3-512AndRSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA1AndDSA.equals(algId)) {
			return "XNMSSwithSHA1AndDSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA224AndDSA.equals(algId)) {
			return "XNMSSwithSHA224AndDSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA256AndDSA.equals(algId)) {
			return "XNMSSwithSHA256AndDSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA384AndDSA.equals(algId)) {
			return "XNMSSwithSHA384AndDSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA512AndDSA.equals(algId)) {
			return "XNMSSwithSHA512AndDSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_224AndDSA.equals(algId)) {
			return "XNMSSwithSHA3-224AndDSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_256AndDSA.equals(algId)) {
			return "XNMSSwithSHA3-256AndDSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_384AndDSA.equals(algId)) {
			return "XNMSSwithSHA3-384AndDSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSwithSHA3_512AndDSA.equals(algId)) {
			return "XNMSSwithSHA3-512AndDSA";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA1.equals(algId)) {
			return "XNMSSOTSwithSHA1";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA224.equals(algId)) {
			return "XNMSSOTSwithSHA224";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA256.equals(algId)) {
			return "XNMSSOTSwithSHA256";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA384.equals(algId)) {
			return "XNMSSOTSwithSHA384";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA512.equals(algId)) {
			return "XNMSSOTSwithSHA512";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA3_224.equals(algId)) {
			return "XNMSSOTSwithSHA3-224";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA3_256.equals(algId)) {
			return "XNMSSOTSwithSHA3-256";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA3_384.equals(algId)) {
			return "XNMSSOTSwithSHA3-384";
		} else if (XNMSS.AlgorithmIdentifiers.XNMSSOTSwithSHA3_512.equals(algId)) {
			return "XNMSSOTSwithSHA3-512";
		} else {
			throw new IllegalArgumentException("Unsupported Key Algorithm Identifier '" + algId != null ? algId.getAlgorithm().toString() : "null" + "'.");
		}
	}

	public static String getDigestAlgorithmForAlgorithmIdentifier(AlgorithmIdentifier algId) throws IllegalArgumentException {
		if (ALGORITHM_IDENTIFIER_SHA1.equals(algId)) {
			return "SHA1";
		} else if (ALGORITHM_IDENTIFIER_SHA224.equals(algId)) {
			return "SHA-224";
		} else if (ALGORITHM_IDENTIFIER_SHA256.equals(algId)) {
			return "SHA-256";
		} else if (ALGORITHM_IDENTIFIER_SHA384.equals(algId)) {
			return "SHA-384";
		} else if (ALGORITHM_IDENTIFIER_SHA512.equals(algId)) {
			return "SHA-512";
		} else if (ALGORITHM_IDENTIFIER_SHA3_224.equals(algId)) {
			return "SHA3-224";
		} else if (ALGORITHM_IDENTIFIER_SHA3_256.equals(algId)) {
			return "SHA3-256";
		} else if (ALGORITHM_IDENTIFIER_SHA3_384.equals(algId)) {
			return "SHA3-384";
		} else if (ALGORITHM_IDENTIFIER_SHA3_512.equals(algId)) {
			return "SHA3-512";
		} else {
			throw new IllegalArgumentException("Unsupported Digest Algorithm Identifier '" + algId != null ? algId.getAlgorithm().toString() : "null" + "'.");
		}
	}

	public static String getDigestAlgorithmForSignatureAlgorithm(String algorithm) throws IllegalArgumentException {
		switch (algorithm.toUpperCase()) {
			case "SHA1WITHECDSA":
			case "SHA1WITHRSA":
			case "SHA1WITHDSA":
				return "SHA1";
			case "SHA224WITHECDSA":
			case "SHA224WITHRSA":
			case "SHA224WITHDSA":
				return "SHA-224";
			case "SHA256WITHECDSA":
			case "SHA256WITHRSA":
			case "SHA256WITHDSA":
				return "SHA-256";
			case "SHA384WITHECDSA":
			case "SHA384WITHRSA":
			case "SHA384WITHDSA":
				return "SHA-384";
			case "SHA512WITHECDSA":
			case "SHA512WITHRSA":
			case "SHA512WITHDSA":
				return "SHA-512";
			case "SHA3-224WITHECDSA":
			case "SHA3-224WITHRSA":
			case "SHA3-224WITHDSA":
				return "SHA3-224";
			case "SHA3-256WITHECDSA":
			case "SHA3-256WITHRSA":
			case "SHA3-256WITHDSA":
				return "SHA3-256";
			case "SHA3-384WITHECDSA":
			case "SHA3-384WITHRSA":
			case "SHA3-384WITHDSA":
				return "SHA3-384";
			case "SHA3-512WITHECDSA":
			case "SHA3-512WITHRSA":
			case "SHA3-512WITHDSA":
				return "SHA3-512";
			default:
				throw new IllegalArgumentException("Unsupported Signature Algorithm '" + algorithm + "'.");

		}
	}

	public static AlgorithmIdentifier getAlgorithmIdentifierForSignatureAlgorithm(String algorithm) throws IllegalArgumentException {
		switch (algorithm.toUpperCase()) {
			case "XNMSS":
				return ALGORITHM_IDENTIFIER_XNMSS;
			case "SHA1WITHECDSA":
				return ALGORITHM_IDENTIFIER_SHA1WITHECDSA;
			case "SHA224WITHECDSA":
				return ALGORITHM_IDENTIFIER_SHA224WITHECDSA;
			case "SHA256WITHECDSA":
				return ALGORITHM_IDENTIFIER_SHA256WITHECDSA;
			case "SHA384WITHECDSA":
				return ALGORITHM_IDENTIFIER_SHA384WITHECDSA;
			case "SHA512WITHECDSA":
				return ALGORITHM_IDENTIFIER_SHA512WITHECDSA;
			case "SHA3-224WITHECDSA":
				return ALGORITHM_IDENTIFIER_SHA3_224WITHECDSA;
			case "SHA3-256WITHECDSA":
				return ALGORITHM_IDENTIFIER_SHA3_256WITHECDSA;
			case "SHA3-384WITHECDSA":
				return ALGORITHM_IDENTIFIER_SHA3_384WITHECDSA;
			case "SHA3-512WITHECDSA":
				return ALGORITHM_IDENTIFIER_SHA3_512WITHECDSA;
			case "SHA1WITHRSA":
				return ALGORITHM_IDENTIFIER_SHA1WITHRSA;
			case "SHA224WITHRSA":
				return ALGORITHM_IDENTIFIER_SHA224WITHRSA;
			case "SHA256WITHRSA":
				return ALGORITHM_IDENTIFIER_SHA256WITHRSA;
			case "SHA384WITHRSA":
				return ALGORITHM_IDENTIFIER_SHA384WITHRSA;
			case "SHA512WITHRSA":
				return ALGORITHM_IDENTIFIER_SHA512WITHRSA;
			case "SHA3-224WITHRSA":
				return ALGORITHM_IDENTIFIER_SHA3_224WITHRSA;
			case "SHA3-256WITHRSA":
				return ALGORITHM_IDENTIFIER_SHA3_256WITHRSA;
			case "SHA3-384WITHRSA":
				return ALGORITHM_IDENTIFIER_SHA3_384WITHRSA;
			case "SHA3-512WITHRSA":
				return ALGORITHM_IDENTIFIER_SHA3_512WITHRSA;
			case "SHA1WITHDSA":
				return ALGORITHM_IDENTIFIER_SHA1WITHDSA;
			case "SHA224WITHDSA":
				return ALGORITHM_IDENTIFIER_SHA224WITHDSA;
			case "SHA256WITHDSA":
				return ALGORITHM_IDENTIFIER_SHA256WITHDSA;
			case "SHA384WITHDSA":
				return ALGORITHM_IDENTIFIER_SHA384WITHDSA;
			case "SHA512WITHDSA":
				return ALGORITHM_IDENTIFIER_SHA512WITHDSA;
			case "SHA3-224WITHDSA":
				return ALGORITHM_IDENTIFIER_SHA3_224WITHDSA;
			case "SHA3-256WITHDSA":
				return ALGORITHM_IDENTIFIER_SHA3_256WITHDSA;
			case "SHA3-384WITHDSA":
				return ALGORITHM_IDENTIFIER_SHA3_384WITHDSA;
			case "SHA3-512WITHDSA":
				return ALGORITHM_IDENTIFIER_SHA3_512WITHDSA;
			default:
				throw new IllegalArgumentException("Unsupported Signature Algorithm '" + algorithm + "'.");

		}
	}

	public static String getSignatureAlgorithmForAlgorithmIdentifier(AlgorithmIdentifier algId) throws IllegalArgumentException {
		if (ALGORITHM_IDENTIFIER_XNMSS.equals(algId)) {
			return "XNMSS";
		} else if (ALGORITHM_IDENTIFIER_SHA1WITHECDSA.equals(algId)) {
			return "SHA1withEC";
		} else if (ALGORITHM_IDENTIFIER_SHA224WITHECDSA.equals(algId)) {
			return "SHA224withEC";
		} else if (ALGORITHM_IDENTIFIER_SHA256WITHECDSA.equals(algId)) {
			return "SHA256withEC";
		} else if (ALGORITHM_IDENTIFIER_SHA384WITHECDSA.equals(algId)) {
			return "SHA384withEC";
		} else if (ALGORITHM_IDENTIFIER_SHA512WITHECDSA.equals(algId)) {
			return "SHA512withEC";
		} else if (ALGORITHM_IDENTIFIER_SHA3_224WITHECDSA.equals(algId)) {
			return "SHA3-224withEC";
		} else if (ALGORITHM_IDENTIFIER_SHA3_256WITHECDSA.equals(algId)) {
			return "SHA3-256withEC";
		} else if (ALGORITHM_IDENTIFIER_SHA3_384WITHECDSA.equals(algId)) {
			return "SHA3-384withEC";
		} else if (ALGORITHM_IDENTIFIER_SHA3_512WITHECDSA.equals(algId)) {
			return "SHA3-512withEC";
		} else if (ALGORITHM_IDENTIFIER_SHA1WITHRSA.equals(algId)) {
			return "SHA1withRSA";
		} else if (ALGORITHM_IDENTIFIER_SHA224WITHRSA.equals(algId)) {
			return "SHA224withRSA";
		} else if (ALGORITHM_IDENTIFIER_SHA256WITHRSA.equals(algId)) {
			return "SHA256withRSA";
		} else if (ALGORITHM_IDENTIFIER_SHA384WITHRSA.equals(algId)) {
			return "SHA384withRSA";
		} else if (ALGORITHM_IDENTIFIER_SHA512WITHRSA.equals(algId)) {
			return "SHA512withRSA";
		} else if (ALGORITHM_IDENTIFIER_SHA3_224WITHRSA.equals(algId)) {
			return "SHA3-224withRSA";
		} else if (ALGORITHM_IDENTIFIER_SHA3_256WITHRSA.equals(algId)) {
			return "SHA3-256withRSA";
		} else if (ALGORITHM_IDENTIFIER_SHA3_384WITHRSA.equals(algId)) {
			return "SHA3-384withRSA";
		} else if (ALGORITHM_IDENTIFIER_SHA3_512WITHRSA.equals(algId)) {
			return "SHA3-512withRSA";
		} else if (ALGORITHM_IDENTIFIER_SHA1WITHDSA.equals(algId)) {
			return "SHA1withDSA";
		} else if (ALGORITHM_IDENTIFIER_SHA224WITHDSA.equals(algId)) {
			return "SHA224withDSA";
		} else if (ALGORITHM_IDENTIFIER_SHA256WITHDSA.equals(algId)) {
			return "SHA256withDSA";
		} else if (ALGORITHM_IDENTIFIER_SHA384WITHDSA.equals(algId)) {
			return "SHA384withDSA";
		} else if (ALGORITHM_IDENTIFIER_SHA512WITHDSA.equals(algId)) {
			return "SHA512withDSA";
		} else if (ALGORITHM_IDENTIFIER_SHA3_224WITHDSA.equals(algId)) {
			return "SHA3-224withDSA";
		} else if (ALGORITHM_IDENTIFIER_SHA3_256WITHDSA.equals(algId)) {
			return "SHA3-256withDSA";
		} else if (ALGORITHM_IDENTIFIER_SHA3_384WITHDSA.equals(algId)) {
			return "SHA3-384withDSA";
		} else if (ALGORITHM_IDENTIFIER_SHA3_512WITHDSA.equals(algId)) {
			return "SHA3-512withDSA";
		} else {
			throw new IllegalArgumentException("Unsupported Signature Algorithm Identifier '" + algId != null ? algId.getAlgorithm().toString() : "null" + "'.");
		}
	}

}
