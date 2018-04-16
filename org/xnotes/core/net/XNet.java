/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.net;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.Ssl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.xnotes.Config;
import org.xnotes.ConfigurationException;
import org.xnotes.XNotes;
import org.xnotes.core.CoreSchemaLib.NodeInfo;
import org.xnotes.core.net.protocol.Message;
import org.xnotes.core.net.protocol.Message.Result;
import org.xnotes.core.net.protocol.Message.Sub;
import org.xnotes.core.security.SecurityToolSet;
import org.xnotes.core.security.SecurityToolSet.XNKeyStore;
import org.xnotes.core.utils.DatatypeConverter;
import org.xnotes.core.utils.FileUtil;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JSON.JsonParseException;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNet {

	protected static final String SESSION_UUID_KEY = "uuid";

	private final WeakReference<XNotes> _xnotesRef;
	private final Node _node;
	protected XNKeyStore sslKeyStore;

	private final ExecutorService _executor = Executors.newCachedThreadPool();

	protected final Map<String, WebSocketSessionInfo> sessionsInfo;
	protected final WebSocketTextMessageHandler wsTextMessageHandler;

	private URI _uplinkURI;
	protected WebSocketConnectionManager uplinkConnectionManager;
	protected Callback uplinkConnectionCallback;

	public XNet(XNotes xnotes, Node node) throws ConfigurationException {
		this._xnotesRef = new WeakReference<>(xnotes);
		_node = node;
		sslKeyStore = null;
		this.sessionsInfo = new ConcurrentHashMap<>();
		this.wsTextMessageHandler = new WebSocketTextMessageHandler(this);
	}

	public final XNotes xnotes() {
		return _xnotesRef.get();
	}

	public Node getNode() {
		return _node;
	}

	public void start() {
		if (!xnotes().config.network.rendezvous.enabled) {
			xnotes().config.network.rendezvous.enabled = true;
			try {
				xnotes().config.save();
			} catch (IOException ex) {
			}
		}
	}

	public void stop() {
		if (xnotes().config.network.rendezvous.enabled) {
			xnotes().config.network.rendezvous.enabled = false;
			try {
				xnotes().config.save();
			} catch (IOException ex) {
			}
		}
	}

	public void connect(String uri, Callback callback) {
		if (uplinkConnectionManager == null) {
			try {
				_uplinkURI = new URI(uri);
				WebSocketTextMessageHandler messageHandler = new WebSocketTextMessageHandler(this, this.wsTextMessageHandler);
				uplinkConnectionManager = new WebSocketConnectionManager(
						new StandardWebSocketClient(),
						messageHandler,
						_uplinkURI.toString());
				uplinkConnectionCallback = callback;
				uplinkConnectionManager.start();
			} catch (URISyntaxException ex) {
				uplinkConnectionCallback.function(ex);
			}
		} else {
			uplinkConnectionCallback.function(new RuntimeException("Already connected to '" + _uplinkURI.toString() + "'."));
		}
	}

	public void disconnect() {
		if (uplinkConnectionManager != null) {
			uplinkConnectionManager.stop();
		}
	}

	public void ping(String id, Callback callback) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public Set<WebSocketSession> getAllSessions() {
		Set<WebSocketSession> s = new HashSet<>();
		sessionsInfo.values().forEach((wssi) -> {
			s.add(wssi.session);
		});
		return s;
	}

	public Set<WebSocketSession> getAllOpenSessions() {
		Set<WebSocketSession> s = new HashSet<>();
		sessionsInfo.values().stream().filter((wssi) -> (wssi.session.isOpen())).forEachOrdered((wssi) -> {
			s.add(wssi.session);
		});
		return s;
	}

	public Set<WebSocketSession> getAllOpenSessionsExcept(WebSocketSession session) {
		Set<WebSocketSession> s = new HashSet<>();
		sessionsInfo.values().stream().filter((wssi) -> (wssi.session.isOpen() && !wssi.session.getAttributes().get(SESSION_UUID_KEY).equals(session.getAttributes().get(SESSION_UUID_KEY)))).forEachOrdered((wssi) -> {
			s.add(wssi.session);
		});
		return s;
	}

	public Node getNodeForSessionInfo(WebSocketSessionInfo sessionInfo) {
		for (WebSocketSessionInfo wssi : sessionsInfo.values()) {
			if (wssi.session.getAttributes().get(SESSION_UUID_KEY).equals(sessionInfo.session.getAttributes().get(SESSION_UUID_KEY))) {
				return wssi.node;
			}
		}
		return null;
	}

	public WebSocketSessionInfo getSessionInfoForNodeId(String nodeId) {
		for (WebSocketSessionInfo wssi : sessionsInfo.values()) {
			if (wssi.node != null && wssi.node.id.equals(nodeId)) {
				return wssi;
			}
		}
		return null;
	}

	public void send(WebSocketSession session, Message msg) {
		TextMessage text = new TextMessage(JSON.stringify(msg));
		try {
			session.sendMessage(text);
		} catch (IOException ex) {
		}
	}

	public void broadcast(WebSocketSession session, Message msg) {
		TextMessage text = new TextMessage(JSON.stringify(msg));
		this.getAllOpenSessions().forEach((s) -> {
			try {
				s.sendMessage(text);
			} catch (IOException ex) {
			}
		});
	}

	public void forward(WebSocketSession session, Message msg) {
		TextMessage text = new TextMessage(JSON.stringify(msg));
		this.getAllOpenSessionsExcept(session).forEach((s) -> {
			try {
				s.sendMessage(text);
			} catch (IOException ex) {
			}
		});
	}

	public void subscribe(WebSocketSession session, Sub sub, Callback cb) {

	}
	
	public void publish(String topic, Object object) {
		
	}
	
	public Result call(String methodName, Map<String, Object> params) {
		return new Result();
	}

	public Future<Result> callAsync(String methodName, Map<String, Object> params) {
		return null;
	}

	@Configuration
	@EnableWebSocket
	public static class WebSocketConfig implements WebSocketConfigurer, ApplicationContextAware {

		private ApplicationContext _context;

		@Override
		public void registerWebSocketHandlers(WebSocketHandlerRegistry wshr) {
			XNotes xn = _context.getBean(XNotes.class);
			wshr.addHandler(xn.xnet.wsTextMessageHandler, xn.config.network.rendezvous.context)
					.setAllowedOrigins(xn.config.network.rendezvous.allowedOrigins != null ? xn.config.network.rendezvous.allowedOrigins : "*");
		}

		@Bean
		public EmbeddedServletContainerCustomizer containerCustomizer() throws FileNotFoundException {
			return (ConfigurableEmbeddedServletContainer container) -> {
				XNotes xnotes = _context.getBean(XNotes.class);
				container.setPort(xnotes.config.network.rendezvous.port);
				if (xnotes.config.network.rendezvous.secure) {
					if (xnotes.config.identity.id != null && !xnotes.config.identity.id.trim().isEmpty()
							&& xnotes.config.security.keyStore.password != null && !xnotes.config.security.keyStore.password.isEmpty()) {
						int hc = xnotes.config.hashCode();
						if (xnotes.config.network.rendezvous.keyStore.filePath == null || xnotes.config.network.rendezvous.keyStore.filePath.trim().isEmpty()) {
							xnotes.config.network.rendezvous.keyStore.filePath = Config.DEFAULT_SSL_KEYSTORE_FILEPATH;
						}

						xnotes.xnet.sslKeyStore = SecurityToolSet.XNKeyStore.getInstance(xnotes.securityToolSet, xnotes.config.network.rendezvous.keyStore);
						String keyPass = xnotes.config.network.rendezvous.keyStore.keyPass != null ? xnotes.config.network.rendezvous.keyStore.keyPass : xnotes.config.network.rendezvous.keyStore.password;
						if (xnotes.config.network.rendezvous.uri != null && !xnotes.config.network.rendezvous.uri.trim().isEmpty()) {
							try {
								URI uri = new URI(xnotes.config.network.rendezvous.uri);
								if (!uri.getScheme().equals("wss")) {
									int i = xnotes.config.network.rendezvous.uri.indexOf("://");
									xnotes.config.network.rendezvous.uri = "wss" + xnotes.config.network.rendezvous.uri.substring(i);
								}
							} catch (URISyntaxException ex) {
								throw new ConfigurationException(ex);
							}
						}

						if (hc != xnotes.config.hashCode()) {
							try {
								xnotes.config.save();
							} catch (IOException ex) {
								throw new ConfigurationException(ex);
							}
						}

						String cn;
						try {
							cn = new URI(xnotes.config.network.rendezvous.uri).getHost();
						} catch (URISyntaxException ex1) {
							try {
								cn = InetAddress.getLocalHost().getHostName();
							} catch (UnknownHostException ex2) {
								cn = xnotes.config.network.rendezvous.host;
							}
						}
						try {
							if (xnotes.xnet.sslKeyStore.contains(Config.DEFAULT_SSL_KEYSTORE_KEYALIAS)) {
								Certificate cert = xnotes.xnet.sslKeyStore.getCertificate(Config.DEFAULT_SSL_KEYSTORE_KEYALIAS);
								Map<String, String> x509Name = xnotes.securityToolSet.getCertificateX509NameMap(cert);
								if (!x509Name.containsKey("CN") || !x509Name.get("CN").equals(cn)) {
									xnotes.xnet.sslKeyStore.delete(Config.DEFAULT_SSL_KEYSTORE_KEYALIAS);
								}
							}
							if (!xnotes.xnet.sslKeyStore.contains(Config.DEFAULT_SSL_KEYSTORE_KEYALIAS)) {
								KeyPair keyPair = xnotes.securityToolSet.generateKeyPairForCipher();
								Map<String, String> subjectX509NameMap = new HashMap<>();
								subjectX509NameMap.putAll(xnotes.config.identity.name.toMap());
								subjectX509NameMap.put("CN", cn);
								String idKeyAlias = DatatypeConverter.printBase58ToHexBinary(xnotes.config.identity.id);
								String idKeyPass = xnotes.config.security.keyStore.keyPass != null ? xnotes.config.security.keyStore.keyPass : xnotes.config.security.keyStore.password;
								PrivateKey issuerPrivateKey = xnotes.securityToolSet.keyStore.getPrivateKey(idKeyAlias, idKeyPass);
								Certificate issuerCert = xnotes.securityToolSet.keyStore.getCertificate(idKeyAlias);
								Certificate cert = xnotes.securityToolSet.generateCertificate(
										issuerPrivateKey,
										issuerCert,
										subjectX509NameMap,
										keyPair.getPublic()
								);
								xnotes.xnet.sslKeyStore.setPrivateKey(
										Config.DEFAULT_SSL_KEYSTORE_KEYALIAS,
										keyPair.getPrivate(),
										keyPass,
										new Certificate[]{cert, issuerCert});
							} else if (keyPass == null) {
								throw new ConfigurationException("SSL key pass missing in Configuration file.");
							}
						} catch (KeyStoreException ex) {
							throw new ConfigurationException(ex);
						}

						Ssl ssl = new Ssl();
						ssl.setEnabled(true);
						if (xnotes.config.network.rendezvous.keyStore.provider != null && !xnotes.config.network.rendezvous.keyStore.provider.trim().isEmpty()) {
							ssl.setKeyStoreProvider(xnotes.config.network.rendezvous.keyStore.provider.trim());
						}
						ssl.setKeyStoreType(xnotes.config.network.rendezvous.keyStore.type);
						ssl.setKeyStore(FileUtil.getActualPath(xnotes.config.network.rendezvous.keyStore.filePath));
						ssl.setKeyStorePassword(xnotes.config.network.rendezvous.keyStore.password);
						ssl.setKeyAlias(Config.DEFAULT_SSL_KEYSTORE_KEYALIAS);
						ssl.setKeyPassword(keyPass);
						container.setSsl(ssl);
					} else {
						throw new ConfigurationException("Node identity not defined.");
					}
				}
			};
		}

		@Override
		public void setApplicationContext(ApplicationContext ac) throws BeansException {
			_context = ac;
		}
	}

	public static class WebSocketTextMessageHandler extends TextWebSocketHandler {

		protected final WeakReference<XNet> xnetRef;
		protected final WebSocketTextMessageHandler mainHandler;

		public WebSocketTextMessageHandler(XNet xnet) {
			xnetRef = new WeakReference<>(xnet);
			mainHandler = null;
		}

		protected WebSocketTextMessageHandler(XNet xnet, WebSocketTextMessageHandler mainHandler) {
			xnetRef = new WeakReference<>(xnet);
			this.mainHandler = mainHandler;
		}

		@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			if (xnet().xnotes().config.network.rendezvous.enabled) {
				session.setTextMessageSizeLimit(xnet().xnotes().config.network.security.messageMaxSize);
				String sessionUUID = xnet().xnotes().securityToolSet.randomID(32);
				session.getAttributes().put(SESSION_UUID_KEY, sessionUUID);
				if (mainHandler == null) {
					// Server-side Connection
					List<String> bl = xnet().xnotes().config.network.rendezvous.blackListedOrigins;
					if (!bl.contains(session.getRemoteAddress().getHostName()) && !bl.contains(session.getRemoteAddress().getAddress().getHostAddress())) {
						xnet().sessionsInfo.put(sessionUUID, new WebSocketSessionInfo(xnet(), session, false));
					} else {
						session.close(CloseStatus.POLICY_VIOLATION);
					}
				} else {
					// Client-side Connection
					xnet().sessionsInfo.put(sessionUUID, new WebSocketSessionInfo(xnet(), session, true));
					Message msg = new Message(Message.Type.connect);
					msg.connect.version = XNotes.VERSION;
					msg.connect.support = XNotes.SUPPORTED_VERSIONS;
					msg.connect.cert = xnet().getNode().cert;
					// TO-DO
					xnet().send(session, msg);
				}
			} else {
				session.close(CloseStatus.SERVICE_RESTARTED);
			}
		}

		@Override
		public void handleTextMessage(WebSocketSession session, TextMessage text) throws InterruptedException, IOException {
			if (session.isOpen()) {
				WebSocketSessionInfo wssi = xnet().sessionsInfo.get((String) session.getAttributes().get(SESSION_UUID_KEY));
				try {
					Message msg = JSON.parse(text.getPayload(), Message.class);
					wssi.activate();
					if (wssi.node == null) {
						if (mainHandler == null) {
							// Server-side Connection Message handling
							if (msg.connect != null) {
								Message response;
								CloseStatus closeStatus = null;
								if (msg.connect.version != null) {
									if (checkVersion(msg.connect.version, msg.connect.support)) {
										if (msg.connect.session != null) {
											// Full Client Reconnection
											response = new Message(Message.Type.connected);
										} else {
											// Full Client connection using X509Certificate
											response = new Message(Message.Type.connected);
										}
									} else {
										closeStatus = CloseStatus.NOT_ACCEPTABLE;
										response = new Message(Message.Type.failed);
										response.failed.error.code = closeStatus.getCode();
										response.failed.error.type = Message.Error.ERROR_TYPE_LOGIN;
										response.failed.error.reason = Message.Error.ERROR_REASON_UNSUPPORTED_VERSION;
										response.failed.error.message = "Could not determine a compatible version.";
										response.failed.info = new HashMap<>();
										((Map<String, Object>) msg.failed.info).put("version", XNotes.VERSION);
									}
								} else if (msg.connect.id != null) {
									// Light Client Login with ID + Passcode
									WebSocketSessionInfo oldwss = xnet().getSessionInfoForNodeId(msg.connect.id);
									if (oldwss != null) {
										try {
											wssi = new WebSocketSessionInfo(xnet(), session, oldwss, msg.connect.passcode);
											xnet().sessionsInfo.remove((String) oldwss.session.getAttributes().get(SESSION_UUID_KEY));
											response = new Message(Message.Type.connected);
											response.connected.version = xnet().getNode().support.version;
											response.connected.session = (String) session.getAttributes().get(SESSION_UUID_KEY);
											response.connected.remote = new NodeInfo(xnet().getNode());
											response.connected.local = new NodeInfo(wssi.node);
										} catch (SecurityException ex) {
											closeStatus = CloseStatus.NOT_ACCEPTABLE;
											response = new Message(Message.Type.failed);
											response.failed.error.code = closeStatus.getCode();
											response.failed.error.type = Message.Error.ERROR_TYPE_LOGIN;
											response.failed.error.reason = Message.Error.ERROR_REASON_INCORRECT_PASSCODE;
											response.failed.error.message = "Incorrect Passcode for Node ID '" + msg.connect.id + "'.";
										}
									} else {
										try {
											wssi.node = Node.getNode(xnet().xnotes().securityToolSet, msg.connect.id, msg.connect.passcode);
											if (wssi.node != null) {
												wssi.keyPass = msg.connect.passcode;
												response = new Message(Message.Type.connected);
												response.connected.version = xnet().getNode().support.version;
												response.connected.session = (String) session.getAttributes().get(SESSION_UUID_KEY);
												response.connected.remote = new NodeInfo(xnet().getNode());
												response.connected.local = new NodeInfo(wssi.node);
											} else {
												closeStatus = CloseStatus.NOT_ACCEPTABLE;
												response = new Message(Message.Type.failed);
												response.failed.error.code = closeStatus.getCode();
												response.failed.error.type = Message.Error.ERROR_TYPE_LOGIN;
												response.failed.error.reason = Message.Error.ERROR_REASON_UNKNOWN_NODE;
												response.failed.error.message = "Unknown Node ID '" + msg.connect.id + "'.";
												response.failed.info = null;
											}
										} catch (SecurityException ex) {
											closeStatus = CloseStatus.NOT_ACCEPTABLE;
											response = new Message(Message.Type.failed);
											response.failed.error.code = closeStatus.getCode();
											response.failed.error.type = Message.Error.ERROR_TYPE_LOGIN;
											response.failed.error.reason = Message.Error.ERROR_REASON_INCORRECT_PASSCODE;
											response.failed.error.message = "Incorrect Passcode for Node ID '" + msg.connect.id + "'.";
										}
									}
								} else {
									// Light Client Register new account with X509 Name + Passcode
									try {
										wssi.node = Node.createNode(
												xnet().xnotes().securityToolSet,
												xnet().getNode(),
												xnet().xnotes().config.security.keyStore.keyPass,
												msg.connect.name,
												msg.connect.passcode,
												msg.connect.description,
												msg.connect.location,
												xnet().getNode().support
										);
										wssi.node.saveAs(xnet().xnotes().config.general.nodesDataPath + wssi.node.id);
										wssi.keyPass = msg.connect.passcode;
										response = new Message(Message.Type.connected);
										response.connected.version = xnet().getNode().support.version;
										response.connected.session = (String) session.getAttributes().get(SESSION_UUID_KEY);
										response.connected.remote = new NodeInfo(xnet().getNode());
										response.connected.local = new NodeInfo(wssi.node);
									} catch (KeyStoreException ex) {
										closeStatus = CloseStatus.SERVER_ERROR;
										response = new Message(Message.Type.failed);
										response.failed.error.code = closeStatus.getCode();
										response.failed.error.type = Message.Error.ERROR_TYPE_REGISTRATION;
										response.failed.error.reason = Message.Error.ERROR_REASON_SERVER_ERROR;
										response.failed.error.message = "Unexpected Server error while registering client Node.";
									}
								}
								xnet().send(session, response);
								if (response.connected != null) {
									Message pubMsg = new Message(Message.Type.pub);
									pubMsg.pub.topic = "nodeUp";
									pubMsg.pub.object = response.connected.local;
									xnet().forward(session, pubMsg);
								} else if (closeStatus != null) {
									wssi.close(closeStatus);
								}
							} else {
								wssi.close(CloseStatus.PROTOCOL_ERROR);
							}
						} else if (xnet().uplinkConnectionManager != null) {
							// Client-side Connection Message handling
							if (msg.connected != null) {
								if (xnet().uplinkConnectionCallback != null) {
									xnet().uplinkConnectionCallback.function(msg);
								}
							} else if (msg.failed != null) {
								if (xnet().uplinkConnectionCallback != null) {
									xnet().uplinkConnectionCallback.function(msg);
								}
							} else {
								if (xnet().uplinkConnectionCallback != null) {
									xnet().uplinkConnectionCallback.function(CloseStatus.PROTOCOL_ERROR);
								}
								wssi.close(CloseStatus.PROTOCOL_ERROR);
							}
						} else {
							wssi.close(CloseStatus.PROTOCOL_ERROR);
						}
					} else if (mainHandler == null) {
						if (msg.pub != null) {
							if (msg.pub.topic.equals("usedPrivateKey")) {
							}
						}
					} else {
						mainHandler.handleTextMessage(session, text);
					}
				} catch (JsonParseException ex) {
					wssi.close(CloseStatus.BAD_DATA);
				}
			}
		}

		protected XNet xnet() {
			return xnetRef.get();
		}

		protected boolean checkVersion(String version, String[] supportedVersions) {
			// TO-DO
			return XNotes.VERSION.equals(version);
		}

		@Override
		public void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
			if (mainHandler == null) {
				xnet().sessionsInfo.get((String) session.getAttributes().get(SESSION_UUID_KEY)).activate();
			} else {
				mainHandler.handlePongMessage(session, message);
			}
		}

		@Override
		public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
			if (mainHandler == null) {
			} else {
				mainHandler.handleTransportError(session, exception);
			}
		}

		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
			if (session.isOpen()) {
				if (mainHandler == null) {
					WebSocketSessionInfo wssi = xnet().sessionsInfo.get((String) session.getAttributes().get(SESSION_UUID_KEY));
					if (wssi != null) {
						if (wssi.node != null) {
							wssi.closeStatus = status;
						} else {
							xnet().sessionsInfo.remove((String) session.getAttributes().get(SESSION_UUID_KEY));
						}
					}
				} else {
					mainHandler.afterConnectionClosed(session, status);
				}
			}
		}
	}

	protected static final class WebSocketSessionInfo {

		private final WeakReference<XNet> _xnetRef;

		public final WebSocketSession session;
		public final boolean isClient;
		public final Date startTime;
		public Date lastActivityTime;
		public Node node;
		public String keyPass;
		public CloseStatus closeStatus;

		public WebSocketSessionInfo(XNet xnet, WebSocketSession session, boolean isClient) {
			_xnetRef = new WeakReference<>(xnet);
			this.session = session;
			this.isClient = isClient;
			this.startTime = new Date();
			this.lastActivityTime = startTime;
			this.node = null;
			this.keyPass = null;
			this.closeStatus = null;
		}

		public WebSocketSessionInfo(XNet xnet, WebSocketSession newSession, WebSocketSessionInfo oldSessionInfo, String keyPass) throws SecurityException {
			try {
				xnet.xnotes().securityToolSet.keyStore.getPrivateKey(DatatypeConverter.printBase58ToHexBinary(oldSessionInfo.node.id), keyPass);
			} catch (KeyStoreException ex) {
				throw new SecurityException("Invalid Key Pass for Node ID '" + oldSessionInfo.node.id + "'.");
			}
			_xnetRef = new WeakReference<>(xnet);
			this.session = newSession;
			this.isClient = oldSessionInfo.isClient;
			this.startTime = new Date();
			this.lastActivityTime = startTime;
			this.node = oldSessionInfo.node;
			this.keyPass = keyPass;
			this.closeStatus = null;
		}

		public final XNet xnet() {
			return _xnetRef.get();
		}

		public void activate() {
			lastActivityTime = new Date();
			this.closeStatus = null;
		}

		public void close(CloseStatus status) {
			try {
				this.session.close(status);
			} catch (IOException ex) {
			}
			this.closeStatus = status;
		}

	}

}
