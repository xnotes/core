/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.net.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.LinkedHashMap;
import java.util.Map;
import org.xnotes.XNotes;
import org.xnotes.core.CoreSchemaLib.X509Name;
import org.xnotes.core.CoreSchemaLib.NodeInfo;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JsonSchema;
import org.xnotes.core.utils.JsonSchema.OneOf;
import org.xnotes.core.utils.JsonSchema.Ref;
import org.xnotes.core.utils.JsonSchema.Required;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
@JsonPropertyOrder({
	"connect",
	"connected",
	"failed",
	"ping",
	"pong",
	"pub",
	"sub",
	"unsub",
	"nosub",
	"add",
	"added",
	"update",
	"updated",
	"revoke",
	"revoked",
	"method",
	"result"
})
@JsonSchema(
		id = JsonSchema.Schema.XNOTES_SCHEMAS_BASE_URL + "xnotes-ddp-1.0",
		description = "XNotes Distributed Data Protocol Schema v1.0",
		title = "XNotes DDP v1.0"
)
@JsonSchema.OneOf()
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message extends JSON.JsonObject {

	public static enum Type {
		connect,
		connected,
		failed,
		ping,
		pong,
		pub,
		sub,
		unsub,
		nosub,
		add,
		added,
		update,
		updated,
		revoke,
		revoked,
		method,
		result

	}

	public Connect connect;
	public Connected connected;
	public Failed failed;
	public Ping ping;
	public Pong pong;
	public Pub pub;
	public Sub sub;
	public Unsub unsub;
	public Nosub nosub;
	public Add add;
	public Added added;
	public Update update;
	public Updated updated;
	public Revoke revoke;
	public Revoked revoked;
	public Method method;
	public Result result;

	public Message(Type messageType) {
		this(
				messageType == Type.connect ? new Connect() : null,
				messageType == Type.connected ? new Connected() : null,
				messageType == Type.failed ? new Failed() : null,
				messageType == Type.ping ? new Ping() : null,
				messageType == Type.pong ? new Pong() : null,
				messageType == Type.pub ? new Pub() : null,
				messageType == Type.sub ? new Sub() : null,
				messageType == Type.unsub ? new Unsub() : null,
				messageType == Type.nosub ? new Nosub() : null,
				messageType == Type.add ? new Add() : null,
				messageType == Type.added ? new Added() : null,
				messageType == Type.update ? new Update() : null,
				messageType == Type.updated ? new Updated() : null,
				messageType == Type.revoke ? new Revoke() : null,
				messageType == Type.revoked ? new Revoked() : null,
				messageType == Type.method ? new Method() : null,
				messageType == Type.result ? new Result() : null
		);
	}

	@JsonCreator
	public Message(
			@JsonProperty("connect") @Required Connect connect,
			@JsonProperty("connected") @Required Connected connected,
			@JsonProperty("failed") @Required Failed failed,
			@JsonProperty("ping") @Required Ping ping,
			@JsonProperty("pong") @Required Pong pong,
			@JsonProperty("pub") @Required Pub pub,
			@JsonProperty("sub") @Required Sub sub,
			@JsonProperty("unsub") @Required Unsub unsub,
			@JsonProperty("nosub") @Required Nosub nosub,
			@JsonProperty("add") @Required Add add,
			@JsonProperty("added") @Required Added added,
			@JsonProperty("update") @Required Update update,
			@JsonProperty("updated") @Required Updated updated,
			@JsonProperty("revoke") @Required Revoke revoke,
			@JsonProperty("revoked") @Required Revoked revoked,
			@JsonProperty("method") @Required Method method,
			@JsonProperty("result") @Required Result result
	) {
		this.connect = connect;
		this.connected = connected;
		this.failed = failed;
		this.ping = ping;
		this.pong = pong;
		this.pub = pub;
		this.sub = sub;
		this.unsub = unsub;
		this.nosub = nosub;
		this.add = add;
		this.added = added;
		this.update = update;
		this.updated = updated;
		this.revoke = revoke;
		this.revoked = revoked;
		this.method = method;
		this.result = result;
	}

	@JsonPropertyOrder({
		"version",
		"support",
		"session",
		"cert",
		"id",
		"name",
		"description",
		"location",
		"passcode"
	})
	@JsonSchema.OneOf({"version,support,session", "version,support,cert", "id,passcode", "name,description,location,passcode"})
	public static final class Connect extends JSON.JsonObject {

		@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/version")
		public String version;
		@JsonSchema.Array.Items(ref = JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/versionPattern")
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public String[] support;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public String session;
		public byte[] cert;
		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public X509Name name;
		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/multilingual")
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public Map<String, String> description;
		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/location")
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public double[] location;
		@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/password")
		public String passcode;

		public Connect() {
			this(null, null, null, null, null, null, null, null, null);
		}

		@JsonCreator
		public Connect(
				@JsonProperty("version") @Required String version,
				@JsonProperty("support") String[] support,
				@JsonProperty("session") String session,
				@JsonProperty("cert") @Required byte[] cert,
				@JsonProperty("id") @Required String id,
				@JsonProperty("name") X509Name name,
				@JsonProperty("description") Map<String, String> description,
				@JsonProperty(value = "location", defaultValue = "[0.0,0.0]") double[] location,
				@JsonProperty("passcode") @Required String passcode
		) {
			this.version = version;
			this.support = support;
			this.session = session;
			this.cert = cert;
			this.id = id;
			this.name = name != null ? name : new X509Name();
			this.description = description != null ? description : new LinkedHashMap<>();
			this.location = location != null ? location : new double[2];
			this.passcode = passcode;
		}

	}

	@JsonPropertyOrder({"version", "session", "remote", "local"})
	public static final class Connected extends JSON.JsonObject {

		@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/version")
		public String version;
		public String session;
		public NodeInfo remote;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public NodeInfo local;

		public Connected() {
			this(null, null, null, null);
		}

		@JsonCreator
		public Connected(
				@JsonProperty("version") @Required String version,
				@JsonProperty("session") @Required String session,
				@JsonProperty("remote") @Required NodeInfo remote,
				@JsonProperty("local") NodeInfo local
		) {
			this.version = version != null ? version : XNotes.VERSION;
			this.session = session;
			this.remote = remote != null ? remote : new NodeInfo();
			this.local = local;
		}

	}

	@JsonPropertyOrder({"error", "info"})
	public static final class Failed extends JSON.JsonObject {

		public Error error;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		public Object info;

		public Failed() {
			this(null, null);
		}

		@JsonCreator
		public Failed(
				@JsonProperty("error") @Required Error error,
				@JsonProperty("info") Object info
		) {
			this.error = error != null ? error : new Error();
			this.info = info;
		}

	}

	public static final class Ping extends JSON.JsonObject {

		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;

		public Ping() {
			this(null);
		}

		@JsonCreator
		public Ping(@JsonProperty("id") @Required String id) {
			this.id = id;
		}
	}

	public static final class Pong extends JSON.JsonObject {

		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;

		public Pong() {
			this(null);
		}

		@JsonCreator
		public Pong(@JsonProperty("id") @Required String id) {
			this.id = id;
		}
	}

	@JsonPropertyOrder({"topic", "id", "object"})
	public static final class Pub extends JSON.JsonObject {

		public String topic;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;
		@OneOf({
			"$ref:" + JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/nodeInfo",
			"$ref:" + JsonSchema.Schema.XNOTES_SCHEMA_URL
		})
		public Object object;

		public Pub() {
			this(null, null, null);
		}

		@JsonCreator
		public Pub(
				@JsonProperty("topic") @Required String topic,
				@JsonProperty("id") @Required String id,
				@JsonProperty("object") @Required Object object
		) {
			this.topic = topic;
			this.id = id;
			this.object = object;
		}
	}

	@JsonPropertyOrder({"topic", "id", "params"})
	public static final class Sub extends JSON.JsonObject {

		public String topic;
		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public Map<String, Object> params;

		public Sub() {
			this(null, null, null);
		}

		@JsonCreator
		public Sub(
				@JsonProperty("topic") @Required String topic,
				@JsonProperty("id") @Required String id,
				@JsonProperty("params") Map<String, Object> params
		) {
			this.topic = topic;
			this.id = id;
			this.params = params != null ? params : new LinkedHashMap<>();
		}
	}

	@JsonPropertyOrder({"topic", "id"})
	public static final class Unsub extends JSON.JsonObject {

		public String topic;
		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;

		public Unsub() {
			this(null, null);
		}

		@JsonCreator
		public Unsub(
				@JsonProperty("topic") @Required String topic,
				@JsonProperty("id") @Required String id
		) {
			this.topic = topic;
			this.id = id;
		}
	}

	@JsonPropertyOrder({"id", "error"})
	public static final class Nosub extends JSON.JsonObject {

		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;
		public Error error;

		public Nosub() {
			this(null, null);
		}

		@JsonCreator
		public Nosub(
				@JsonProperty("id") @Required String id,
				@JsonProperty("error") @Required Error error
		) {
			this.id = id;
			this.error = error != null ? error : new Error();
		}
	}

	@JsonPropertyOrder({"schema", "object"})
	public static final class Add extends JSON.JsonObject {

		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/uri")
		public String schema;
		public Object object;

		public Add() {
			this(null, null);
		}

		@JsonCreator
		public Add(
				@JsonProperty("schema") @Required String schema,
				@JsonProperty("object") @Required Object object
		) {
			this.schema = schema;
			this.object = object;
		}
	}

	@JsonPropertyOrder({"schema", "object"})
	public static final class Added extends JSON.JsonObject {

		public String schema;
		public Object object;

		public Added() {
			this(null, null);
		}

		@JsonCreator
		public Added(
				@JsonProperty("schema") @Required String schema,
				@JsonProperty("object") @Required Object object
		) {
			this.schema = schema;
			this.object = object;
		}
	}

	@JsonPropertyOrder({"schema", "id", "object"})
	public static final class Update extends JSON.JsonObject {

		public String schema;
		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;
		public Object object;

		public Update() {
			this(null, null, null);
		}

		@JsonCreator
		public Update(
				@JsonProperty("schema") @Required String schema,
				@JsonProperty("id") @Required String id,
				@JsonProperty("object") @Required Object object
		) {
			this.schema = schema;
			this.id = id;
			this.object = object;
		}
	}

	@JsonPropertyOrder({"schema", "object"})
	public static final class Updated extends JSON.JsonObject {

		public String schema;
		public Object object;

		public Updated() {
			this(null, null);
		}

		@JsonCreator
		public Updated(
				@JsonProperty("schema") @Required String schema,
				@JsonProperty("object") @Required Object object
		) {
			this.schema = schema;
			this.object = object;
		}
	}

	@JsonPropertyOrder({"schema", "id"})
	public static final class Revoke extends JSON.JsonObject {

		public String schema;
		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;

		public Revoke() {
			this(null, null);
		}

		@JsonCreator
		public Revoke(
				@JsonProperty("schema") @Required String schema,
				@JsonProperty("id") @Required String id
		) {
			this.schema = schema;
			this.id = id;
		}
	}

	@JsonPropertyOrder({"schema", "id"})
	public static final class Revoked extends JSON.JsonObject {

		public String schema;
		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;

		public Revoked() {
			this(null, null);
		}

		@JsonCreator
		public Revoked(
				@JsonProperty("schema") @Required String schema,
				@JsonProperty("id") @Required String id
		) {
			this.schema = schema;
			this.id = id;
		}
	}

	@JsonPropertyOrder({"id", "method", "params"})
	public static final class Method extends JSON.JsonObject {

		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;
		public String method;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public Map<String, Object> params;

		public Method() {
			this(null, null, null);
		}

		@JsonCreator
		public Method(
				@JsonProperty("id") @Required String id,
				@JsonProperty("method") @Required String method,
				@JsonProperty("params") Map<String, Object> params
		) {
			this.id = id;
			this.method = method;
			this.params = params != null ? params : new LinkedHashMap<>();
		}
	}

	@JsonPropertyOrder({"id", "error", "result"})
	@JsonSchema.OneOf({"id,error", "id,result"})
	public static final class Result extends JSON.JsonObject {

		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String id;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		public Error error;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public Object result;

		public Result() {
			this(null, null, null);
		}

		@JsonCreator
		public Result(
				@JsonProperty("id") @Required String id,
				@JsonProperty("error") @Required Error error,
				@JsonProperty("result") @Required Object result
		) {
			this.id = id;
			this.error = error != null ? error : result == null ? new Error() : null;
			this.result = result;
		}
	}

	@JsonPropertyOrder({"code", "type", "reason", "message"})
	public static final class Error extends JSON.JsonObject {

		public static final String ERROR_TYPE_LOGIN = "LOGIN";
		public static final String ERROR_TYPE_REGISTRATION = "REGISTRATION";

		public static final String ERROR_REASON_UNSUPPORTED_VERSION = "UNSUPPORTED_VERSION";
		public static final String ERROR_REASON_INCORRECT_PASSCODE = "INCORRECT_PASSCODE";
		public static final String ERROR_REASON_UNKNOWN_NODE = "UNKNOWN_NODE";
		public static final String ERROR_REASON_SERVER_ERROR = "SERVER_ERROR";

		public int code;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public String type;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public String reason;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public String message;

		public Error() {
			this(0, null, null, null);
		}

		@JsonCreator
		public Error(
				@JsonProperty("code") @Required int code,
				@JsonProperty("type") String type,
				@JsonProperty("reason") String reason,
				@JsonProperty("message") String message
		) {
			this.code = code;
			this.type = type;
			this.reason = reason;
			this.message = message;
		}
	}
}
