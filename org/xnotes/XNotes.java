/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.xnotes.core.CoreSchemaLib.X509Name;
import org.xnotes.core.db.XNotesManager;
import org.xnotes.core.net.Node;
import org.xnotes.core.net.Node.Support;
import org.xnotes.core.net.XNet;
import org.xnotes.core.security.SecurityToolSet;
import org.xnotes.core.security.ots.xnmss.XNMSS;
import org.xnotes.core.security.ots.xnmss.XNMSSDefaultPrivateKeyManager;
import org.xnotes.core.security.ots.xnmss.XNMSSDefaultPublicKeyManager;
import org.xnotes.core.utils.DatatypeConverter;
import org.xnotes.core.utils.FileUtil;
import org.xnotes.core.utils.JSON;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
@SpringBootApplication
public class XNotes {

	public static final String DEFAULT_CONFIG_FILEPATH = "~/xnet.io/etc/xnotes/config.json";
	public static String CONFIG_FILEPATH = DEFAULT_CONFIG_FILEPATH;

	public static final String VERSION = "2.0";
	public static final String[] SUPPORTED_VERSIONS = new String[]{"2.0"};
	public static final String[] SUPPORTED_IDENTITY_ALGORITHMS = new String[]{"EC:{256,384,521}:{SHA192withECDSA,SHA256withECDSA,SHA384withECDSA,SHA512withECDSA}", "RSA:{1024,2048,4096}:{SHA192withRSA,SHA256withRSA,SHA384withRSA,SHA512withRSA}"};
	public static final String[] SUPPORTED_KEYWRAP_ALGORITHMS = new String[]{"RSA:{1024,2048,4096}:{SHA-192,SHA-256,SHA-384,SHA-512}"};
	public static final String[] SUPPORTED_KEY_ALGORITHMS = new String[]{"AES/{CBC}/{PKCS5Padding}:{128,192,256}", "Blowfish/{CBC}/{PKCS5Padding}:{128,192,256}"};
	public static final String[] SUPPORTED_CERTIFICATE_TYPES = new String[]{"X.509"};

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(XNotes.class, args);
	}

	public final Config config;
	public final SecurityToolSet securityToolSet;
	public final XNotesManager manager;
	public final XNet xnet;

	protected XNotes() throws ConfigurationException {

		try {
			config = JSON.parseFile(CONFIG_FILEPATH, Config.class, new Config(), true);
		} catch (IOException ex) {
			throw new InternalError(ex);
		}
		securityToolSet = new SecurityToolSet(this, true);

		XNMSS.registerPublicKeyManager(new XNMSSDefaultPublicKeyManager(securityToolSet, config.general.dbPath));
		XNMSS.registerPrivateKeyManager(new XNMSSDefaultPrivateKeyManager(
				securityToolSet,
				config.general.dbPath,
				config.general.dbPath + "keyPasses" + File.separator,
				XNMSS.getPublicKeyManager()
		));

		manager = new XNotesManager(this);

		Node node = null;
		if (config.identity.id != null) {
			try {
				node = JSON.parseFile(FileUtil.getPath(config.general.nodesDataPath) + config.identity.id, Node.class, null);
				if (node != null) {
					if (node.cert != null && node.cert.length > 0) {
						try {
							CertificateFactory cf = CertificateFactory.getInstance(config.security.certificate.type, config.security.certificate.provider != null ? config.security.certificate.provider : securityToolSet.getProviderForCertificateType(config.security.certificate.type));
							Certificate cert = cf.generateCertificate(new ByteArrayInputStream(node.cert));
							PublicKey pub = cert.getPublicKey();
							if (SecurityToolSet.areAlgorithmsEqual(pub, config.security.identity.algorithm, config.security.identity.keySize)) {
								String id = DatatypeConverter.printBase58Binary(securityToolSet.keyHash(pub.getEncoded()));
								X509Name x509Name = new X509Name(securityToolSet.getCertificateX509NameMap(cert));
								if (!config.identity.id.equals(node.id)
										|| node.name == null
										|| (config.identity.name != null && config.identity.name.CN != null && !node.name.CN.trim().equals(config.identity.name.CN.trim()))
										|| (x509Name.CN != null && !node.name.CN.trim().equals(x509Name.CN.trim()))
										|| !id.equals(node.id)) {
									node = null;
								}
							} else {
								node = null;
							}
						} catch (CertificateException | NoSuchProviderException ex) {
							node = null;
						}
					} else {
						node = null;
					}
				}
			} catch (IOException ex) {
			}
		}

		if (node != null) {
			String alias = DatatypeConverter.printBase58ToHexBinary(node.id);
			try {
				if (securityToolSet.keyStore.contains(alias)) {
					if (config.security.keyStore.keyPass == null) {
						throw new ConfigurationException("Identity key pass at 'config.security.keyStore.keyPass' missing in Configuration file.");
					} else {
						try {
							securityToolSet.keyStore.getPrivateKey(alias, config.security.keyStore.keyPass);
						} catch (KeyStoreException ex) {
							throw new ConfigurationException("Identity key pass at 'config.security.keyStore.keyPass' is not valid.");
						}
					}
				} else {
					node = null;
				}
			} catch (KeyStoreException ex) {
				throw new ConfigurationException(ex);
			}
		}

		if (node == null) {
			if (config.security.keyStore.keyPass == null) {
				config.security.keyStore.keyPass = securityToolSet.randomPassword();
			}
			try {
				node = Node.createSelfSignedNode(securityToolSet, config.identity.name, config.security.keyStore.keyPass, config.identity.description, config.identity.location, new Support());
				node.saveAs(config.general.nodesDataPath + node.id);
				config.identity.id = node.id;
			} catch (KeyStoreException | IOException ex) {
				throw new ConfigurationException(ex);
			}
		}

		xnet = new XNet(this, node);

		try {
			config.save();
		} catch (IOException ex) {
			throw new ConfigurationException("Could not save Configuration file.");
		}

	}

}
