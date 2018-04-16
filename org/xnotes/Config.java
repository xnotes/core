/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.xnotes.core.CoreSchemaLib.X509Name;
import org.xnotes.core.net.protocol.Message;
import org.xnotes.core.security.SecurityToolSet;
import org.xnotes.core.utils.ASN1;
import org.xnotes.core.utils.FileUtil;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JsonSchema;
import org.xnotes.core.utils.JsonSchema.Definition;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
@JsonPropertyOrder({"general", "identity", "network", "security"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Config extends JSON.JsonObject {

	public static final String DEFAULT_WORK_PATH = "~/xnet.io/var/xnotes/temp/";
	public static final String DEFAULT_NODESDATA_PATH = "~/xnet.io/var/xnotes/data/nodes/";
	public static final String DEFAULT_DB_PATH = "~/xnet.io/var/xnotes/db/";
	public static final String DEFAULT_SECDATA_PATH = "~/xnet.io/var/xnotes/data/secure/";
	public static final String DEFAULT_SSL_KEYSTORE_FILEPATH = "~/xnet.io/var/xnotes/ssl.jks";
	public static final String DEFAULT_SSL_KEYSTORE_KEYALIAS = "ssl";
	public static final String DEFAULT_KEYSTORE_FILEPATH = "~/xnet.io/var/xnotes/xnotes.jks";

	public static final String DEFAULT_SCRYPT_ENGINE_CLASS = "org.xnotes.core.security.hash.BCSCryptHashEngine";
	public static final String DEFAULT_KEY_HASH_ENGINE_CLASS = "org.xnotes.core.security.hash.JCEHashEngine";
	public static final String DEFAULT_OBJECT_HASH_ENGINE_CLASS = "org.xnotes.core.security.hash.JCEHashEngine";

	public static final int DEFAULT_XNMSS_KEYSIZE = 8;
	public static final int DEFAULT_EC_KEYSIZE = 521;
	public static final String DEFAULT_EC_SIGNATURE_ALGORITHM = "SHA3-512withECDSA";
	public static final int DEFAULT_RSA_KEYSIZE = 2048;
	public static final String DEFAULT_RSA_SIGNATURE_ALGORITHM = "SHA3-512withRSA";
	public static final int DEFAULT_DSA_KEYSIZE = 2048;
	public static final String DEFAULT_DSA_SIGNATURE_ALGORITHM = "SHA3-512withDSA";

	public General general;
	public Identity identity;
	public Network network;
	public Security security;

	public Config() {
		this(null, null, null, null);
	}

	@JsonCreator
	public Config(
			@JsonProperty("general") General general,
			@JsonProperty("identity") Identity identity,
			@JsonProperty("network") Network network,
			@JsonProperty("security") Security security
	) {
		this.general = general != null ? general : new General();
		this.identity = identity != null ? identity : new Identity();
		this.network = network != null ? network : new Network();
		this.security = security != null ? security : new Security();
	}

	@JsonPropertyOrder({"workPath", "nodesDataPath", "dbPath", "secDataPath"})
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class General extends JSON.JsonObject {

		public String workPath;
		public String nodesDataPath;
		public String dbPath;
		public String secDataPath;

		public General() {
			this(null, null, null, null);
		}

		@JsonCreator
		public General(
				@JsonProperty("workPath") String workPath,
				@JsonProperty("nodesDataPath") String nodesDataPath,
				@JsonProperty("dbPath") String dbPath,
				@JsonProperty("secDataPath") String secDataPath
		) {
			this.workPath = workPath != null ? workPath : DEFAULT_WORK_PATH;
			FileUtil.createPathIfNotExist(this.workPath);
			this.nodesDataPath = nodesDataPath != null ? nodesDataPath : DEFAULT_NODESDATA_PATH;
			FileUtil.createPathIfNotExist(this.nodesDataPath);
			this.dbPath = dbPath != null ? dbPath : DEFAULT_DB_PATH;
			FileUtil.createPathIfNotExist(this.dbPath);
			this.secDataPath = secDataPath != null ? secDataPath : DEFAULT_SECDATA_PATH;
			FileUtil.createPathIfNotExist(this.secDataPath);
		}

	}

	@JsonPropertyOrder({"id", "name", "description", "location"})
	public static class Identity extends JSON.JsonObject {

		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public X509Name name;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/multilingual")
		public Map<String, String> description;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@JsonSchema.Array.MinItems(2)
		@JsonSchema.Array.MaxItems(2)
		public double[] location;

		public Identity() {
			this(null, null, null, null);
		}

		@JsonCreator
		public Identity(
				@JsonProperty("id") String id,
				@JsonProperty("name") X509Name name,
				@JsonProperty("description") Map<String, String> description,
				@JsonProperty("location") double[] location
		) {
			this.id = id;
			this.name = name != null ? name : new X509Name();
			this.description = description != null ? description : new LinkedHashMap();
			this.location = location;
		}
	}

	@JsonPropertyOrder({"rendezvous", "uplink", "pulse", "security"})
	public static class Network extends JSON.JsonObject {

		public Rendezvous rendezvous;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public Uplink uplink;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public Pulse pulse;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public NetworkSecurity security;

		public Network() {
			this(null, null, null, null);
		}

		@JsonCreator
		public Network(
				@JsonProperty("rendezvous") Rendezvous rendezvous,
				@JsonProperty("uplink") Uplink uplink,
				@JsonProperty("pulse") Pulse pulse,
				@JsonProperty("security") NetworkSecurity security
		) {
			this.rendezvous = rendezvous != null ? rendezvous : new Rendezvous();
			this.uplink = uplink != null ? uplink : new Uplink();
			this.pulse = pulse != null ? pulse : new Pulse();
			this.security = security != null ? security : new NetworkSecurity();
		}

		@JsonPropertyOrder({"enabled", "secure", "keyStore", "host", "port", "context", "uri", "allowedOrigins", "blackListedOrigins"})
		public static class Rendezvous extends JSON.JsonObject {

			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public boolean enabled;
			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public boolean secure;
			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public Security.KeyStore keyStore;
			public String host;
			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public int port;
			public String context;
			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public String uri;
			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public String allowedOrigins;
			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public List<String> blackListedOrigins;

			public Rendezvous() {
				this(true, false, null, null, 0, null, null, null, null);
			}

			@JsonCreator
			public Rendezvous(
					@JsonProperty(value = "enabled", defaultValue = "true") boolean enabled,
					@JsonProperty(value = "secure") boolean secure,
					@JsonProperty(value = "keyStore") Security.KeyStore keyStore,
					@JsonProperty(value = "host", defaultValue = "localhost") String host,
					@JsonProperty(value = "port", defaultValue = "8888") int port,
					@JsonProperty(value = "context", defaultValue = "/xnotes") String context,
					@JsonProperty("uri") String uri,
					@JsonProperty(value = "allowedOrigins", defaultValue = "*") String allowedOrigins,
					@JsonProperty("blackListedOrigins") List<String> blackListedOrigins
			) {
				this.enabled = enabled;
				this.secure = secure;
				this.keyStore = keyStore != null ? keyStore : new Security.KeyStore();
				this.host = host != null ? host : "localhost";
				this.port = port != 0 ? port : 8888;
				this.context = context != null ? context : "/xnotes";
				if (uri != null) {
					this.uri = uri;
				} else {
					try {
						this.uri = (this.secure ? "wss" : "ws") + "://" + InetAddress.getLocalHost().getHostName() + ":" + this.port + this.context;
					} catch (UnknownHostException ex) {
						this.uri = (this.secure ? "wss" : "ws") + "://" + this.host + ":" + this.port + this.context;
					}
				}
				this.allowedOrigins = allowedOrigins != null ? allowedOrigins : "*";
				this.blackListedOrigins = blackListedOrigins != null ? blackListedOrigins : new ArrayList<>();
			}
		}

		@JsonPropertyOrder({"recent"})
		public static class Uplink extends JSON.JsonObject {

			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public List<ConnectionInfo> recent;

			public Uplink() {
				this(null);
			}

			@JsonCreator
			public Uplink(
					@JsonProperty("recent") List<ConnectionInfo> recent
			) {
				this.recent = recent != null ? recent : new ArrayList<>();
			}

			@JsonPropertyOrder({"uri", "time", "connect"})
			public static class ConnectionInfo extends JSON.JsonObject {

				public String uri;
				@JsonSerialize(using = JSON.DateSerializer.class)
				@JsonDeserialize(using = JSON.DateDeserializer.class)
				public Date time;
				public Message.Connect connect;

				public ConnectionInfo() {
					this(null, null, null);
				}

				@JsonCreator
				public ConnectionInfo(
						@JsonProperty("uri") String uri,
						@JsonProperty("time") Date time,
						@JsonProperty("connect") Message.Connect connect
				) {
					this.uri = uri;
					this.time = time != null ? time : new Date(0);
					this.connect = connect;
				}
			}

		}

		@JsonPropertyOrder({"participate", "broadcastStage", "maxParticipants", "idleTimeout", "startTimeout", "candidatingTimeout", "voteTimeout", "commitTimeout", "parameters"})
		public static class Pulse extends JSON.JsonObject {

			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public boolean participate;
			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public boolean broadcastStage;
			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public int maxParticipants;
			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public long idleTimeout;
			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public long startTimeout;
			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public long candidatingTimeout;
			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public long voteTimeout;
			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public long commitTimeout;
			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public Parameters parameters;

			public Pulse() {
				this(true, false, 0, 0, 0, 0, 0, 0, null);
			}

			@JsonCreator
			public Pulse(
					@JsonProperty(value = "participate", defaultValue = "true") boolean participate,
					@JsonProperty("broadcastStage") boolean broadcastStage,
					@JsonProperty("maxParticipants") int maxParticipants,
					@JsonProperty("idleTimeout") long idleTimeout,
					@JsonProperty("startTimeout") long startTimeout,
					@JsonProperty("candidatingTimeout") long candidatingTimeout,
					@JsonProperty("voteTimeout") long voteTimeout,
					@JsonProperty("commitTimeout") long commitTimeout,
					@JsonProperty("parameters") Parameters parameters
			) {
				this.participate = participate;
				this.broadcastStage = broadcastStage;
				this.idleTimeout = idleTimeout > 0 ? idleTimeout : 32;
				this.startTimeout = startTimeout > 0 ? startTimeout : 30;
				this.candidatingTimeout = candidatingTimeout > 0 ? candidatingTimeout : 5;
				this.voteTimeout = voteTimeout > 0 ? voteTimeout : 5;
				this.commitTimeout = commitTimeout > 0 ? commitTimeout : 5;
				this.parameters = parameters != null ? parameters : new Parameters();
			}

			@JsonPropertyOrder({"hashEngineClass", "iterations", "size", "saltSize", "maxSaltSize", "cost", "maxCost", "blockSize", "maxBlockSize", "parallelization", "maxParallelization"})
			public static class Parameters extends JSON.JsonObject {

				@JsonInclude(JsonInclude.Include.NON_EMPTY)
				public String hashEngineClass;
				public int iterations;
				public int size = 64;
				public int saltSize = 64;
				public int maxSaltSize = 64;
				public int cost = 256;
				public int maxCost = 16384;
				public int blockSize = 8;
				public int maxBlockSize = 8;
				public int parallelization = 1;
				public int maxParallelization = 1;

				public Parameters() {
					this(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
				}

				@JsonCreator
				public Parameters(
						@JsonProperty(value = "hashEngineClass", defaultValue = DEFAULT_SCRYPT_ENGINE_CLASS) String hashEngineClass,
						@JsonProperty(value = "iterations", defaultValue = "1") int iterations,
						@JsonProperty("size") int size,
						@JsonProperty("saltSize") int saltSize,
						@JsonProperty("maxSaltSize") int maxSaltSize,
						@JsonProperty("cost") int cost,
						@JsonProperty("maxCost") int maxCost,
						@JsonProperty("blockSize") int blockSize,
						@JsonProperty("maxBlockSize") int maxBlockSize,
						@JsonProperty("parallelization") int parallelization,
						@JsonProperty("maxParallelization") int maxParallelization
				) {
					this.hashEngineClass = hashEngineClass != null ? hashEngineClass : DEFAULT_SCRYPT_ENGINE_CLASS;
					this.iterations = iterations > 0 ? iterations : 1;
					this.size = size > 0 ? size : 64;
					this.saltSize = saltSize > 0 ? saltSize : 64;
					this.maxSaltSize = maxSaltSize > 0 ? maxSaltSize : 64;
					this.cost = cost > 0 ? cost : 256;
					this.maxCost = maxCost > 0 ? maxCost : 16384;
					this.blockSize = blockSize > 0 ? blockSize : 8;
					this.maxBlockSize = maxBlockSize > 0 ? maxBlockSize : 8;
					this.parallelization = parallelization > 0 ? parallelization : 1;
					this.maxParallelization = maxParallelization > 0 ? maxParallelization : 1;
				}

			}
		}

		@JsonPropertyOrder({"messageMaxSize"})
		public static class NetworkSecurity extends JSON.JsonObject {

			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public int messageMaxSize;

			public NetworkSecurity() {
				this(0);
			}

			@JsonCreator
			public NetworkSecurity(
					@JsonProperty(value = "messageMaxSize", defaultValue = "8192") int messageMaxSize
			) {
				this.messageMaxSize = messageMaxSize > 0 ? messageMaxSize : 8192;
			}
		}
	}

	@JsonPropertyOrder({"random", "keyHash", "objectHash", "identity", "cipher", "certificate", "password", "keyStore"})
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class Security extends JSON.JsonObject {

		public Random random;
		public Hash keyHash;
		public Hash objectHash;
		public Identity identity;
		public Cipher cipher;
		public Certificate certificate;
		public Password password;
		public KeyStore keyStore;

		public Security() {
			this(null, null, null, null, null, null, null, null);
		}

		@JsonCreator
		public Security(
				@JsonProperty("random") Random random,
				@JsonProperty("keyHash") Hash keyHash,
				@JsonProperty("objectHash") Hash objectHash,
				@JsonProperty("identity") Identity identity,
				@JsonProperty("cipher") Cipher cipher,
				@JsonProperty("certificate") Certificate certificate,
				@JsonProperty("password") Password password,
				@JsonProperty("keyStore") KeyStore keyStore
		) {
			this.random = random != null ? random : new Random();
			this.keyHash = keyHash != null ? keyHash : new Hash();
			if (keyHash == null) {
				this.keyHash.hashEngineClass = DEFAULT_KEY_HASH_ENGINE_CLASS;
			}
			this.objectHash = objectHash != null ? objectHash : new Hash();
			if (objectHash == null) {
				this.keyHash.hashEngineClass = DEFAULT_OBJECT_HASH_ENGINE_CLASS;
			}
			this.identity = identity != null ? identity : new Identity();
			this.cipher = cipher != null ? cipher : new Cipher();
			this.certificate = certificate != null ? certificate : new Certificate();
			this.password = password != null ? password : new Password();
			this.keyStore = keyStore != null ? keyStore : new KeyStore();
		}

		@JsonPropertyOrder({"provider", "algorithm", "randomIdSize"})
		public static class Random extends JSON.JsonObject {

			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public String provider;
			public String algorithm;
			public int randomIdSize;

			public Random() {
				this(null, null, 0);
			}

			@JsonCreator
			public Random(
					@JsonProperty("provider") String provider,
					@JsonProperty("algorithm") String algorithm,
					@JsonProperty(value = "randomIdSize", defaultValue = "64") int randomIdSize
			) {
				this.provider = provider;
				this.algorithm = algorithm != null ? algorithm : SecurityToolSet.getDefaultSecureRandom().getAlgorithm();
				this.randomIdSize = randomIdSize > 0 ? randomIdSize : 64;
			}
		}

		@JsonPropertyOrder({"provider", "algorithm", "iterations", "hashEngineClass"})
		public static class Hash extends JSON.JsonObject {

			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public String provider;
			public String algorithm;
			public int iterations;
			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public String hashEngineClass;

			public Hash() {
				this(null, null, 0, null);
			}

			@JsonCreator
			public Hash(
					@JsonProperty("provider") String provider,
					@JsonProperty(value = "algorithm", defaultValue = "SHA3-512") String algorithm,
					@JsonProperty(value = "iterations", defaultValue = "2") int iterations,
					@JsonProperty("hashEngineClass") String hashEngineClass
			) {
				this.provider = provider;
				this.algorithm = algorithm != null ? algorithm : "SHA3-512";
				this.iterations = iterations > 0 ? iterations : 2;
				this.hashEngineClass = hashEngineClass;
			}
		}

		@JsonPropertyOrder({"provider", "algorithm", "keySize", "signAlg"})
		@Definition("securityIdentity")
		public static class Identity extends JSON.JsonObject {

			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public String provider;
			public String algorithm;
			public int keySize;
			public String signAlg;

			public Identity() {
				this(null, null, 0, null);
			}

			@JsonCreator
			public Identity(
					@JsonProperty("provider") String provider,
					@JsonProperty("algorithm") String algorithm,
					@JsonProperty("keySize") int keySize,
					@JsonProperty("signAlg") String signAlg
			) {
				this.provider = provider;
				this.algorithm = algorithm != null ? algorithm : "XNMSSwithSHA3-512AndEC";
				if (keySize > 0) {
					this.keySize = keySize;
				} else if (this.algorithm.startsWith("XNMSS")) {
					this.keySize = DEFAULT_XNMSS_KEYSIZE;
				} else if (this.algorithm.equals("EC")) {
					this.keySize = DEFAULT_EC_KEYSIZE;
				} else if (this.algorithm.equals("RSA")) {
					this.keySize = DEFAULT_RSA_KEYSIZE;
				} else if (this.algorithm.equals("DSA")) {
					this.keySize = DEFAULT_DSA_KEYSIZE;
				} else {
					this.keySize = 0;
				}
				if (signAlg != null) {
					this.signAlg = signAlg;
				} else if (this.algorithm.startsWith("XNMSS")) {
					this.signAlg = "XNMSS";
				} else if (this.algorithm.equals("EC")) {
					this.signAlg = DEFAULT_EC_SIGNATURE_ALGORITHM;
				} else if (this.algorithm.equals("RSA")) {
					this.signAlg = DEFAULT_RSA_SIGNATURE_ALGORITHM;
				} else if (this.algorithm.equals("DSA")) {
					this.signAlg = DEFAULT_DSA_SIGNATURE_ALGORITHM;
				} else {
					this.signAlg = null;
				}
			}
		}

		@JsonPropertyOrder({"keyWrap", "key"})
		public static class Cipher extends JSON.JsonObject {

			public KeyWrap keyWrap;
			public Key key;

			public Cipher() {
				this(null, null);
			}

			@JsonCreator
			public Cipher(
					@JsonProperty("keyWrap") KeyWrap keyWrap,
					@JsonProperty("key") Key key
			) {
				this.keyWrap = keyWrap != null ? keyWrap : new KeyWrap();
				this.key = key != null ? key : new Key();
			}

			@JsonPropertyOrder({"provider", "algorithm", "keySize", "digest"})
			public static class KeyWrap extends JSON.JsonObject {

				@JsonInclude(JsonInclude.Include.NON_EMPTY)
				public String provider;
				public String algorithm;
				public int keySize;
				public String digest;

				public KeyWrap() {
					this(null, null, 0, null);
				}

				@JsonCreator
				public KeyWrap(
						@JsonProperty("provider") String provider,
						@JsonProperty(value = "algorithm") String algorithm,
						@JsonProperty(value = "keySize") int keySize,
						@JsonProperty(value = "digest") String digest
				) {
					this.provider = provider;
					this.algorithm = algorithm != null ? algorithm : "RSA";
					if (keySize > 0) {
						this.keySize = keySize;
					} else if (this.algorithm.equals("EC")) {
						this.keySize = DEFAULT_EC_KEYSIZE;
					} else if (this.algorithm.equals("RSA")) {
						this.keySize = DEFAULT_RSA_KEYSIZE;
					} else if (this.algorithm.equals("DSA")) {
						this.keySize = DEFAULT_DSA_KEYSIZE;
					} else {
						this.keySize = 0;
					}
					if (digest != null) {
						this.digest = digest;
					} else if (this.algorithm.equals("EC")) {
						this.digest = ASN1.getDigestAlgorithmForSignatureAlgorithm(DEFAULT_EC_SIGNATURE_ALGORITHM);
					} else if (this.algorithm.equals("RSA")) {
						this.digest = ASN1.getDigestAlgorithmForSignatureAlgorithm(DEFAULT_RSA_SIGNATURE_ALGORITHM);
					} else if (this.algorithm.equals("DSA")) {
						this.digest = ASN1.getDigestAlgorithmForSignatureAlgorithm(DEFAULT_DSA_SIGNATURE_ALGORITHM);
					}
				}
			}

			@JsonPropertyOrder({"provider", "algorithm", "keySize", "digest", "parameters", "validity"})
			public static class Key extends JSON.JsonObject {

				@JsonInclude(JsonInclude.Include.NON_EMPTY)
				public String provider;
				public String algorithm;
				public int keySize;
				public String digest;
				public Map<String, Object> parameters;
				public int validity;

				public Key() {
					this(null, null, 0, null, null, 0);
				}

				@JsonCreator
				public Key(
						@JsonProperty("provider") String provider,
						@JsonProperty(value = "algorithm", defaultValue = "AES/CBC/PKCS5Padding") String algorithm,
						@JsonProperty(value = "keySize", defaultValue = "256") int keySize,
						@JsonProperty(value = "digest", defaultValue = "SHA3-512") String digest,
						@JsonProperty("parameters") Map<String, Object> parameters,
						@JsonProperty(value = "validity", defaultValue = "3600") int validity
				) {
					this.provider = provider;
					this.algorithm = algorithm != null ? algorithm : "AES/CBC/PKCS5Padding";
					this.keySize = keySize > 0 ? keySize : 256;
					this.digest = digest != null ? digest : "SHA3-512";
					this.parameters = parameters != null ? parameters : new LinkedHashMap<>();
					this.validity = validity > 0 ? validity : 3600;
				}
			}
		}

		@JsonPropertyOrder({"provider", "type"})
		public static class Certificate extends JSON.JsonObject {

			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public String provider;
			public String type;
			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			@JsonSchema.String.Enum({"MILLISECOND", "Millisecond", "millisecond", "ms", "SECOND", "Second", "second", "s", "MINUTE", "Minute", "minute", "mn", "HOUR", "Hour", "hour", "DAY", "Day", "day", "MONTH", "Month", "month", "YEAR", "Year", "year"})
			public String validityUnit;
			@JsonInclude(JsonInclude.Include.NON_DEFAULT)
			public int validity;

			public Certificate() {
				this(null, null, null, 0);
			}

			@JsonCreator
			public Certificate(
					@JsonProperty("provider") String provider,
					@JsonProperty(value = "type", defaultValue = "X.509") String type,
					@JsonProperty(value = "validityUnit", defaultValue = "YEAR") String validityUnit,
					@JsonProperty(value = "validity", defaultValue = "1") int validity
			) {
				this.provider = provider;
				this.type = type != null ? type : "X.509";
				this.validityUnit = validityUnit != null ? validityUnit.trim().toUpperCase() : "YEAR";
				this.validity = validity > 0 ? validity : 1;
			}
		}

		@JsonPropertyOrder({"provider", "algorithm", "parameters", "length"})
		public static class Password extends JSON.JsonObject {

			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public String provider;
			public String algorithm;
			@JsonInclude(JsonInclude.Include.NON_EMPTY)
			public Map<String, Object> parameters;
			public int length;

			public Password() {
				this(null, null, null, 0);
			}

			@JsonCreator
			public Password(
					@JsonProperty("provider") String provider,
					@JsonProperty(value = "algorithm", defaultValue = "PBKDF2WithHmacSHA256") String algorithm,
					@JsonProperty("parameters") Map<String, Object> parameters,
					@JsonProperty(value = "length", defaultValue = "16") int length
			) {
				this.provider = provider;
				this.algorithm = algorithm != null ? algorithm : "PBKDF2WithHmacSHA256";
				this.parameters = parameters != null ? parameters : new LinkedHashMap<>();
				this.length = length > 0 ? length : 16;
			}
		}

		@JsonPropertyOrder({"provider", "type", "filePath", "password", "keyPass"})
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public static class KeyStore extends JSON.JsonObject {

			public String provider;
			public String type;
			public String filePath;
			public String password;
			public String keyPass;

			public KeyStore() {
				this(null, null, null, null, null);
			}

			@JsonCreator
			public KeyStore(
					@JsonProperty("provider") String provider,
					@JsonProperty(value = "type", defaultValue = "PKCS12") String type,
					@JsonProperty("filePath") String filePath,
					@JsonProperty("password") String password,
					@JsonProperty("keyPass") String keyPass
			) {
				this.provider = provider;
				this.type = type != null ? type : "PKCS12";
				this.filePath = filePath;
				this.password = password;
				this.keyPass = keyPass;
			}
		}
	}
}
