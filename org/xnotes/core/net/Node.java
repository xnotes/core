/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.net;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.xnotes.Config.Identity;
import org.xnotes.XNotes;
import org.xnotes.core.CoreSchemaLib.X509Name;
import org.xnotes.core.security.SecurityToolSet;
import org.xnotes.core.utils.DatatypeConverter;
import org.xnotes.core.utils.FileUtil;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JsonSchema;
import org.xnotes.core.utils.JsonSchema.Required;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
@JsonPropertyOrder({"id", "name", "description", "location", "cert", "support"})
public class Node extends Identity {

	public static Node createSelfSignedNode(SecurityToolSet securityToolSet, X509Name name, String keyPass, Map<String, String> description, double[] location, Support support) throws KeyStoreException {
		try {
			KeyPair keyPair = securityToolSet.generateKeyPairForIdentity();
			byte[] id = securityToolSet.keyHash(keyPair.getPublic().getEncoded());
			String nodeId = DatatypeConverter.printBase58Binary(id);
			Map<String, String> x509NameMap = new HashMap<>();
			if (name != null) {
				if (name.CN != null) {
					x509NameMap.put("CN", name.CN);
				} else {
					name.CN = nodeId;
					x509NameMap.put("CN", nodeId);
				}
			} else {
				x509NameMap.put("CN", nodeId);
			}
			Certificate cert = securityToolSet.generateSelfSignedCertificate(x509NameMap, keyPair);
			securityToolSet.keyStore.setPrivateKey(
					DatatypeConverter.printHexBinary(id),
					keyPair.getPrivate(),
					keyPass,
					new Certificate[]{cert});
			return new Node(nodeId, new X509Name(x509NameMap), description, location, cert.getEncoded(), support);
		} catch (CertificateEncodingException ex) {
			throw new KeyStoreException(ex);
		}
	}

	public static Node createNode(SecurityToolSet securityToolSet, Node signingNode, String signingKeyPass, X509Name subjectName, String subjectKeyPass, Map<String, String> nodeDescription, double[] nodeLocation, Support nodeSupport) throws KeyStoreException {
		try {
			KeyPair subjectKeyPair = securityToolSet.generateKeyPairForIdentity();
			byte[] id = securityToolSet.keyHash(subjectKeyPair.getPublic().getEncoded());
			String nodeId = DatatypeConverter.printBase58Binary(id);
			if (subjectName == null) {
				subjectName = new X509Name();
			}
			if (subjectName.CN == null) {
				subjectName.CN = nodeId;
			}
			String signindEntryAlias = DatatypeConverter.printBase58ToHexBinary(signingNode.id);
			PrivateKey signingPrivateKey = securityToolSet.keyStore.getPrivateKey(signindEntryAlias, signingKeyPass);
			Certificate signingCert = securityToolSet.keyStore.getCertificate(signindEntryAlias);
			Certificate subjectCert = securityToolSet.generateCertificate(signingPrivateKey, signingCert, subjectName.toMap(), subjectKeyPair.getPublic());
			securityToolSet.keyStore.setPrivateKey(
					DatatypeConverter.printHexBinary(id),
					subjectKeyPair.getPrivate(),
					subjectKeyPass,
					new Certificate[]{subjectCert, signingCert});
			return new Node(nodeId, subjectName, nodeDescription, nodeLocation, subjectCert.getEncoded(), nodeSupport);
		} catch (CertificateEncodingException ex) {
			throw new KeyStoreException(ex);
		}
	}

	public static Node getNode(SecurityToolSet securityToolSet, String nodeId, String keyPass) throws SecurityException {
		String entryAlias = DatatypeConverter.printBase58ToHexBinary(nodeId);
		try {
			if (securityToolSet.keyStore.contains(entryAlias)) {
				try {
					securityToolSet.keyStore.getPrivateKey(entryAlias, keyPass);
				} catch (KeyStoreException ex) {
					throw new SecurityException("Invalid Key Pass for Node ID '" + nodeId + "'.");
				}
				String nodeFilePath = FileUtil.getActualPath(FileUtil.getPath(securityToolSet.xnotes().config.general.nodesDataPath)) + nodeId;
				if (Files.exists(Paths.get(nodeFilePath))) {
					try {
						return JSON.parseFile(nodeFilePath, Node.class, null);
					} catch (IOException ex) {
					}
				}
				Certificate cert = securityToolSet.keyStore.getCertificate(entryAlias);
				try {
					Node node = new Node(nodeId, null, null, null, cert.getEncoded(), null);
					node.saveAs(nodeFilePath);
					return node;
				} catch (CertificateEncodingException | IOException ex) {
				}
			}
		} catch (KeyStoreException ex) {
		}
		return null;
	}

	public byte[] cert;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public Support support;

	private WeakReference<XNet> _xnetRef;

	public Node() {
		this(null, null, null, null, null, null);
	}

	public Node(XNet xnet, String id) throws KeyStoreException {
		_xnetRef = new WeakReference<>(xnet);
		try {
			this.id = id;
			Certificate certificate = xnet().xnotes().securityToolSet.keyStore.getCertificate(DatatypeConverter.printBase58ToHexBinary(id));
			if (cert != null) {
				this.cert = certificate.getEncoded();
			}
			this.support = new Support(
					XNotes.VERSION,
					XNotes.SUPPORTED_VERSIONS,
					XNotes.SUPPORTED_IDENTITY_ALGORITHMS,
					XNotes.SUPPORTED_KEYWRAP_ALGORITHMS,
					XNotes.SUPPORTED_KEY_ALGORITHMS,
					XNotes.SUPPORTED_CERTIFICATE_TYPES
			);
		} catch (CertificateEncodingException ex) {
			throw new KeyStoreException(ex);
		}
	}

	@JsonCreator
	public Node(
			@JsonProperty("id") @Required String id,
			@JsonProperty("name") X509Name name,
			@JsonProperty("description") Map<String, String> description,
			@JsonProperty("location") double[] location,
			@JsonProperty("cert") @Required byte[] cert,
			@JsonProperty("support") Support support
	) {
		super(id, name, description, location);
		this.cert = cert;
		this.support = support != null ? support : new Support();
	}

	public final XNet xnet() {
		return _xnetRef.get();
	}

	@JsonIgnore
	public PrivateKey getPrivateKey(String keyPass) throws KeyStoreException {
		return xnet().xnotes().securityToolSet.keyStore.getPrivateKey(DatatypeConverter.printBase58ToHexBinary(id), keyPass);
	}

	@JsonIgnore
	public Map<String, String> getCertificateName() {
		return xnet().xnotes().securityToolSet.getCertificateX509NameMap(xnet().xnotes().securityToolSet.certificateFromBytes(cert));
	}

	@JsonPropertyOrder({"version", "versions", "identity", "keyWrap", "key", "certificate"})
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static final class Support extends JSON.JsonObject {

		@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/version")
		public String version;
		@JsonSchema.Array.Items(ref = JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/versionPattern")
		public String[] versions;
		public final String[] identity;
		public final String[] keyWrap;
		public final String[] key;
		public final String[] certificate;

		public Support() {
			this(XNotes.VERSION, XNotes.SUPPORTED_VERSIONS, null, null, null, null);
		}

		@JsonCreator
		public Support(
				@JsonProperty("version") String version,
				@JsonProperty("versions") String[] versions,
				@JsonProperty("identity") String[] identity,
				@JsonProperty("keyWrap") String[] keyWrap,
				@JsonProperty("key") String[] key,
				@JsonProperty("certificate") String[] certificate
		) {
			this.version = version;
			this.versions = versions;
			this.identity = identity;
			this.keyWrap = keyWrap;
			this.key = key;
			this.certificate = certificate;
		}

	}

}
