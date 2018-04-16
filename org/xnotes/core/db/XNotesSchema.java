/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JSON.JsonValidationException;
import org.xnotes.core.utils.JsonSchema;
import org.xnotes.core.utils.JsonSchema.JsonSchemaException;
import org.xnotes.core.utils.JsonSchema.OneOf;
import org.xnotes.core.utils.JsonSchema.Ref;
import org.xnotes.core.utils.JsonSchema.Required;
import static org.xnotes.core.utils.JsonSchema.Schema.XNOTES_SCHEMAS_BASE_URL;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
@JsonSchema(
		id = JsonSchema.Schema.XNOTES_SCHEMAS_BASE_URL + "xnotes-schema-1.0",
		description = "XNotes Schema v1.0",
		title = "XNotes Schema v1.0",
		definitions = {
			"idList:{type:array,items:{$ref:" + JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58},additionalItems:false}",
			"nonEmptyTextList:{type:array,items:{$ref:" + JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/nonEmptyText},additionalItems:false}",
			"fieldName:{$ref:" + JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/nonEmptyText}",
			"fieldNameList:{type:array,items:{$ref:#/definitions/fieldName},additionalItems:false}",
			"fieldGroupfName:{$ref:" + JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/nonEmptyText}",
			"roleName:{$ref:" + JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/nonEmptyText}"
		}
)
@JsonPropertyOrder({"id", "version", "name", "description", "imports", "exports", "definition"})
public final class XNotesSchema extends JSON.JsonObject {

	public static final java.lang.String XNOTES_SCHEMA_URL = XNOTES_SCHEMAS_BASE_URL + "xnotes-schema-1.0";

	@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/uri")
	public String id;
	@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/uri")
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public String $schema;
	public String version;
	@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/multilingual")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public final Map<String, String> name;
	@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/multilingual")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public final Map<String, String> description;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public final List<Import> imports;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public final Exports exports;
	public final Definition definition;

	public XNotesSchema() throws JsonSchemaException {
		this(null, null, null, null, null, null, null, null);
	}

	@JsonCreator
	public XNotesSchema(
			@JsonProperty("id") @Required String id,
			@JsonProperty(value = "$schema", defaultValue = XNOTES_SCHEMA_URL + "#") String $schema,
			@JsonProperty(value = "version", defaultValue = "1.0") String version,
			@JsonProperty("name") Map<String, String> name,
			@JsonProperty("description") Map<String, String> description,
			@JsonProperty("imports") List<Import> imports,
			@JsonProperty("exports") Exports exports,
			@JsonProperty("definition") @Required Definition definition
	) throws JsonSchemaException {
		this.id = id;
		this.$schema = $schema != null ? $schema : XNOTES_SCHEMA_URL + "#";
		this.version = version != null ? version : "1.0";
		this.name = name != null ? name : new LinkedHashMap<>();
		this.description = description != null ? description : new LinkedHashMap<>();
		this.imports = imports != null ? imports : new ArrayList<>();
		this.exports = exports != null ? exports : new Exports();
		this.definition = definition != null ? definition : new Definition();
		this.definition.xnotesSchemaRef = new WeakReference<>(this);
	}

	@JsonPropertyOrder({"validationSchema", "fieldsMap", "fieldGroupsMap", "rolesMap"})
	public static final class Import extends JSON.JsonObject {

		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/uri")
		public String validationSchema;
		@JsonSchema.Object.PatternProperties(valueRef = "#/definitions/fieldName")
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final Map<String, String> fieldsMap;
		@JsonSchema.Object.PatternProperties(valueRef = "#/definitions/fieldGroupName")
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final Map<String, String> fieldGroupsMap;
		@JsonSchema.Object.PatternProperties(valueRef = "#/definitions/roleName")
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final Map<String, String> rolesMap;

		public Import() {
			this(null, null, null, null);
		}

		@JsonCreator
		public Import(
				@JsonProperty("validationSchema") @Required String validationSchema,
				@JsonProperty("fieldsMap") Map<String, String> fieldsMap,
				@JsonProperty("fieldGroupsMap") Map<String, String> fieldGroupsMap,
				@JsonProperty("rolesMap") Map<String, String> rolesMap
		) {
			this.validationSchema = validationSchema;
			this.fieldsMap = fieldsMap != null ? fieldsMap : new LinkedHashMap<>();
			this.fieldGroupsMap = fieldGroupsMap != null ? fieldGroupsMap : new LinkedHashMap<>();
			this.rolesMap = rolesMap != null ? rolesMap : new LinkedHashMap<>();
		}

	}

	@JsonPropertyOrder({"fields", "fieldGroups", "roles"})
	public static final class Exports extends JSON.JsonObject {

		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@JsonSchema.Array.Items(ref = "#/definitions/fieldName")
		public final List<String> fields;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@JsonSchema.Array.Items(ref = "#/definitions/fieldGroupName")
		public final List<String> fieldGroups;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@JsonSchema.Array.Items(ref = "#/definitions/roleName")
		public final List<String> roles;

		public Exports() {
			this(null, null, null);
		}

		@JsonCreator
		public Exports(
				@JsonProperty("fields") List<String> fields,
				@JsonProperty("fieldGroups") List<String> fieldGroups,
				@JsonProperty("roles") List<String> roles
		) {
			this.fields = fields != null ? fields : new ArrayList<>();
			this.fieldGroups = fieldGroups != null ? fieldGroups : new ArrayList<>();
			this.roles = roles != null ? roles : new ArrayList<>();
		}

	}

	@JsonPropertyOrder({"schema", "fieldGroups", "roles", "access", "validation"})
	public static final class Definition extends JSON.JsonObject {

		protected WeakReference<XNotesSchema> xnotesSchemaRef;
		private ObjectNode _jsonSchema = null;

		@OneOf({"$ref:" + JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/uri", "$ref:" + JsonSchema.Schema.JSON_VALIDATION_SCHEMA_ID})
		public Object schema;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@JsonSchema.Object.PatternProperties(valueRef = "#/definitions/fieldNameList")
		public final Map<String/*FieldGroupName*/, List<String/*FieldName*/>> fieldGroups;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final Map<String/*RoleName*/, Role> roles;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final Map<String/*RoleName*/, Access> access;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final Validation validation;

		public Definition() throws JsonSchemaException {
			this(null, null, null, null, null);
		}

		@JsonCreator
		public Definition(
				@JsonProperty("schema") @Required Object schema,
				@JsonProperty("fieldGroups") Map<String, List<String>> fieldGroups,
				@JsonProperty("roles") Map<String, Role> roles,
				@JsonProperty("access") Map<String, Access> access,
				@JsonProperty("validation") Validation validation
		) throws JsonSchemaException {
			if (schema != null) {
				this.getJsonValidationSchema();
			} else {
				this.schema = null;
			}
			this.fieldGroups = fieldGroups != null ? fieldGroups : new LinkedHashMap<>();
			this.roles = roles != null ? roles : new LinkedHashMap<>();
			this.access = access != null ? access : new LinkedHashMap<>();
			this.validation = validation != null ? validation : new Validation();
		}

		@JsonIgnore
		public final ObjectNode getJsonValidationSchema() throws JsonSchemaException {
			if (_jsonSchema == null
					|| (String.class.isInstance(schema) && !_jsonSchema.get("id").equals(schema))
					|| (Map.class.isInstance(schema) && !_jsonSchema.equals(JSON.OBJECT_MAPPER.valueToTree(schema)))
					|| (ObjectNode.class.isInstance(schema) && !_jsonSchema.equals(schema))) {
				_jsonSchema = null;
				JsonNode jn;
				if (Map.class.isInstance(schema)) {
					jn = JSON.OBJECT_MAPPER.valueToTree(schema);
				} else if (ObjectNode.class.isInstance(schema)) {
					jn = (JsonNode) schema;
				} else if (String.class.isInstance(schema)) {
					String[] uc = JsonSchema.Schema.getURIPathFileAndRef((String) schema);
					String url = uc[0] + uc[1];
					try {
						jn = JSON.OBJECT_MAPPER.readTree(new URL(url));
					} catch (IOException ex) {
						throw new JsonSchemaException("Invalid Json Validation Schema '" + url + "' defined in XNotes Schema '" + xnotesSchemaRef.get().id + "': " + ex.getMessage());
					}
					if (ObjectNode.class.isInstance(jn)) {
						if (!uc[2].isEmpty()) {
							int i = uc[2].indexOf("definitions/");
							if (i > -1) {
								jn = JsonSchema.Schema.getSubSchema((ObjectNode) jn, uc[3].substring(i + 12));
							}
						}
					}
				} else {
					throw new JsonSchemaException("Unsupported Schema Object Class '" + schema.getClass().getName() + "' defined in XNotes Schema '" + xnotesSchemaRef.get().id + "'.");
				}
				try {
					JSON.validate(JsonSchema.Schema.JSON_VALIDATION_SCHEMA, jn);
					_jsonSchema = (ObjectNode) jn;
					if (!String.class.isInstance(schema)) {
						schema = jn;
					}
				} catch (JsonValidationException ex) {
					throw new JsonSchemaException("Invalid Json Validation Schema '" + schema.toString() + "' defined in XNotes Schema '" + xnotesSchemaRef.get().id + "': " + ex.getMessage());
				}
			}
			return _jsonSchema;
		}

	}

	@JsonPropertyOrder({
		"members",
		"autoAddAll",
		"autoAddCreator",
		"autoAddModifier",
		"autoAddReader",
		"autoAddRevoker",
		"autoAddMembersFromFields",
		"allowJoinRequests",
		"grant",
		"grantNotification",
		"joinInvite",
		"allowLeaveRequests",
		"revoke",
		"revokeNotification",
		"leaveInvite"
	})
	public static final class Role extends JSON.JsonObject {

		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@Ref("#/definitions/idList")
		public final List<String> members;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public boolean autoAddAll;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public boolean autoAddCreator;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public boolean autoAddModifier;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public boolean autoAddReader;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public boolean autoAddRevoker;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@JsonSchema.Array.Items(ref = JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/nonEmptyText")
		public final List<String> autoAddMembersFromFields;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public boolean allowJoinRequests;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final Validation grant;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public boolean grantNotification;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public boolean joinInvite;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public boolean allowLeaveRequests;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final Validation revoke;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public boolean revokeNotification;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public boolean leaveInvite;

		public Role() {
			this(null, false, false, false, false, false, null, false, null, false, false, false, null, false, false);
		}

		@JsonCreator
		public Role(
				@JsonProperty("members") List<String> members,
				@JsonProperty("autoAddAll") boolean autoAddAll,
				@JsonProperty("autoAddCreator") boolean autoAddCreator,
				@JsonProperty("autoAddModifier") boolean autoAddModifier,
				@JsonProperty("autoAddReader") boolean autoAddReader,
				@JsonProperty("autoAddRevoker") boolean autoAddRevoker,
				@JsonProperty("autoAddFieldContents") List<String> autoAddMembersFromFields,
				@JsonProperty("allowJoinRequests") boolean allowJoinRequests,
				@JsonProperty("grant") Validation grant,
				@JsonProperty("grantNotification") boolean grantNotification,
				@JsonProperty("joinInvite") boolean joinInvite,
				@JsonProperty("allowLeaveRequests") boolean allowLeaveRequests,
				@JsonProperty("revoke") Validation revoke,
				@JsonProperty("revokeNotification") boolean revokeNotification,
				@JsonProperty("leaveInvite") boolean leaveInvite
		) {
			this.members = members != null ? members : new ArrayList<>();
			this.autoAddAll = autoAddAll;
			this.autoAddCreator = autoAddCreator;
			this.autoAddModifier = autoAddModifier;
			this.autoAddReader = autoAddReader;
			this.autoAddRevoker = autoAddRevoker;
			this.autoAddMembersFromFields = autoAddMembersFromFields != null ? autoAddMembersFromFields : new ArrayList<>();
			this.allowJoinRequests = allowJoinRequests;
			this.grant = grant != null ? grant : new Validation();
			this.grantNotification = grantNotification;
			this.joinInvite = joinInvite;
			this.allowLeaveRequests = allowLeaveRequests;
			this.revoke = revoke != null ? revoke : new Validation();
			this.revokeNotification = revokeNotification;
			this.leaveInvite = leaveInvite;
		}
	}

	@JsonPropertyOrder({"rule", "schema", "role", "memberCount", "percentage", "exclusiveMinimum", "timeout"})
	public static class ValidationRule extends JSON.JsonObject {

		@JsonSchema.String.Enum({"none", "anyone", "everyone", "atLeast", "quorum"})
		public String rule;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/uri")
		public String schema;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@Ref("#/definitions/roleName")
		public String role;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public int memberCount;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public double percentage;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public boolean exclusiveMinimum;
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public int timeout;

		public ValidationRule() {
			this(null, null, null, 0, 0, false, 1);
		}

		@JsonCreator
		public ValidationRule(
				@JsonProperty(value = "rule", defaultValue = "anyone") @Required String rule,
				@JsonProperty("schema") String schema,
				@JsonProperty("role") String role,
				@JsonProperty("memberCount") int memberCount,
				@JsonProperty("percentage") double percentage,
				@JsonProperty("exclusiveMinimum") boolean exclusiveMinimum,
				@JsonProperty(value = "timeout", defaultValue = "1") int timeout
		) {
			this.rule = rule != null ? rule : "anyone";
			this.schema = schema;
			this.role = role;
			this.memberCount = memberCount;
			this.percentage = percentage;
			this.exclusiveMinimum = exclusiveMinimum;
			this.timeout = timeout != 0 ? timeout : 1;
		}
	}

	@JsonPropertyOrder({"allOf", "anyOf", "oneOf", "not", "rule", "schema", "role", "memberCount", "percentage", "exclusiveMinimum", "timeout"})
	@JsonSchema.OneOf({"allOf", "anyOf", "oneOf", "not", "rule,schema,role,memberCount,percentage,exclusiveMinimum,timeout"})
	public static final class Validation extends ValidationRule {

		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@JsonSchema.Array.MinItems(1)
		public final List<ValidationRule> allOf;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@JsonSchema.Array.MinItems(1)
		public final List<ValidationRule> anyOf;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@JsonSchema.Array.MinItems(1)
		public final List<ValidationRule> oneOf;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		@JsonSchema.Array.MinItems(1)
		public final List<ValidationRule> not;

		public Validation() {
			this(null, null, null, 0, 0, false, 1, null, null, null, null);
		}

		@JsonCreator
		public Validation(
				@JsonProperty(value = "rule", defaultValue = "anyone") @Required String rule,
				@JsonProperty("schema") String schema,
				@JsonProperty("role") String role,
				@JsonProperty("memberCount") int memberCount,
				@JsonProperty("percentage") double percentage,
				@JsonProperty("exclusiveMinimum") boolean exclusiveMinimum,
				@JsonProperty(value = "timeout", defaultValue = "1") int timeout,
				@JsonProperty("allOf") @Required List<ValidationRule> allOf,
				@JsonProperty("anyOf") @Required List<ValidationRule> anyOf,
				@JsonProperty("oneOf") @Required List<ValidationRule> oneOf,
				@JsonProperty("not") @Required List<ValidationRule> not
		) {
			super(rule, schema, role, memberCount, percentage, exclusiveMinimum, timeout);
			if (allOf != null || anyOf != null || oneOf != null || not != null) {
				if (allOf != null) {
					this.allOf = allOf;
					this.anyOf = null;
					this.oneOf = null;
					this.not = null;
				} else if (anyOf != null) {
					this.allOf = null;
					this.anyOf = anyOf;
					this.oneOf = null;
					this.not = null;
				} else if (oneOf != null) {
					this.allOf = null;
					this.anyOf = null;
					this.oneOf = oneOf;
					this.not = null;
				} else {
					this.allOf = null;
					this.anyOf = null;
					this.oneOf = null;
					this.not = not;
				}
				this.rule = "anyone";
				this.schema = null;
				this.role = null;
				this.memberCount = 0;
				this.percentage = 0;
				this.exclusiveMinimum = false;
				this.timeout = 1;
			} else {
				this.allOf = null;
				this.anyOf = null;
				this.oneOf = null;
				this.not = null;
			}
		}

	}

	@JsonPropertyOrder({"instance", "fieldGroups", "privileges", "otherRolesPrivileges"})
	public static final class Access extends JSON.JsonObject {

		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final InstanceAccess instance;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final Map<String/*FieldGroupName*/, InstanceAccess> fieldGroups;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final Privileges privileges;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final Map<String/*RoleName*/, Privileges> otherRolesPrivileges;

		public Access() {
			this(null, null, null, null);
		}

		@JsonCreator
		public Access(
				@JsonProperty("object") InstanceAccess instance,
				@JsonProperty("fieldGroups") Map<String, InstanceAccess> fieldGroups,
				@JsonProperty("privileges") Privileges privileges,
				@JsonProperty("otherRolesPrivileges") Map<String, Privileges> otherRolesPrivileges
		) {
			this.instance = instance != null ? instance : new InstanceAccess();
			this.fieldGroups = fieldGroups != null ? fieldGroups : new LinkedHashMap<>();
			this.privileges = privileges != null ? privileges : new Privileges();
			this.otherRolesPrivileges = otherRolesPrivileges != null ? otherRolesPrivileges : new LinkedHashMap<>();
		}

		@JsonPropertyOrder({"create", "modify", "read", "revoke"})
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public static final class InstanceAccess extends JSON.JsonObject {

			public boolean create;
			public boolean modify;
			public boolean read = true;
			public boolean revoke;

			public InstanceAccess() {
				this(false, false, true, false);
			}

			@JsonCreator
			public InstanceAccess(
					@JsonProperty("create") boolean create,
					@JsonProperty("modify") boolean modify,
					@JsonProperty(value = "read", defaultValue = "true") boolean read,
					@JsonProperty("revoke") boolean revoke
			) {
				this.create = create;
				this.modify = modify;
				this.read = read != false ? read : true;
				this.revoke = revoke;
			}
		}

		@JsonPropertyOrder({"grant", "revoke"})
		@JsonInclude(JsonInclude.Include.NON_DEFAULT)
		public static final class Privileges extends JSON.JsonObject {

			public boolean grant;
			public boolean revoke;

			public Privileges() {
				this(false, false);
			}

			@JsonCreator
			public Privileges(
					@JsonProperty("grant") boolean grant,
					@JsonProperty("revoke") boolean revoke
			) {
				this.grant = grant;
				this.revoke = revoke;
			}

		}
	}

}
