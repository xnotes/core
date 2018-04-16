/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.xnotes.core.CoreSchemaLib;
import org.xnotes.core.utils.JSON.JsonValidationException;
import static org.xnotes.core.utils.JSON.OBJECT_MAPPER;
import static org.xnotes.core.utils.JSON.parse;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonSchema {

	java.lang.String id();

	java.lang.String description() default "";

	java.lang.String title() default "";

	java.lang.String[] definitions() default "";

	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface Definition {

		java.lang.String value();
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface Ref {

		java.lang.String value();
	}

	@Target({ElementType.TYPE, ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface NoDefinition {
	}

	@Target({ElementType.TYPE, ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface AllOf {

		java.lang.String[] value() default "*";
	}

	@Target({ElementType.TYPE, ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface AnyOf {

		java.lang.String[] value() default "*";
	}

	@Target({ElementType.TYPE, ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface OneOf {

		java.lang.String[] value() default "*";
	}

	@Target({ElementType.TYPE, ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface Not {

		java.lang.String[] value();
	}

	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public static @interface Required {

		boolean value() default true;
	}

	public static final class Number {

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface MultipleOf {

			double value();
		}

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface Maximum {

			double value();
		}

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface ExclusiveMaximum {

			boolean value() default true;
		}

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface Minimum {

			double value();
		}

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface ExclusiveMinimum {

			boolean value() default true;
		}

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface Enum {

			double[] value();
		}
	}

	public static final class String {

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface MaxLength {

			int value() default Integer.MAX_VALUE;
		}

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface MinLength {

			int value() default 0;
		}

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface Pattern {

			java.lang.String value();
		}

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface Enum {

			java.lang.String[] value();
		}
	}

	public static final class Array {

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface Items {

			Class<?> type() default Schema.UndefinedValueType.class;

			java.lang.String ref() default "";

			java.lang.String value() default "";
		}

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface MaxItems {

			int value();
		}

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface MinItems {

			int value();
		}

		@Target({ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface UniqueItems {

			boolean value() default true;
		}
	}

	public static final class Object {

		@Target({ElementType.TYPE, ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface PatternProperties {

			java.lang.String key() default "*";

			Class<?> valueType() default Schema.UndefinedValueType.class;

			java.lang.String valueRef() default "";

			java.lang.String value() default "";

		}

		@Target({ElementType.TYPE, ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface MaxProperties {

			int value();
		}

		@Target({ElementType.TYPE, ElementType.FIELD})
		@Retention(RetentionPolicy.RUNTIME)
		@Documented
		public static @interface MinProperties {

			int value();
		}

	}

	public static final class Schema {

		public static final java.lang.String XNOTES_SCHEMAS_BASE_URL = "http://xnotes.org/schemas/";
		public static final java.lang.String XNOTES_CORE_SCHEMA_LIB_URL = XNOTES_SCHEMAS_BASE_URL + "xnotes-core-schema-lib-1.0";
		public static final java.lang.String XNOTES_SCHEMA_URL = XNOTES_SCHEMAS_BASE_URL + "xnotes-schema-1.0";

		public static final java.lang.String JSON_VALIDATION_SCHEMA_ID = "http://json-schema.org/draft-04/schema#";

		private static final java.lang.String _JSON_ANY_TYPE_KEY = "anyType";
		private static ObjectNode _JSON_ANY_TYPE;
		private static final java.lang.String _JSON_BASE64_TYPE_KEY = "base64";
		private static ObjectNode _JSON_BASE64_TYPE;
		private static final java.lang.String _JSON_BASE64MIME_TYPE_KEY = "base64Mime";
		private static ObjectNode _JSON_BASE64MIME_TYPE;
		private static final java.lang.String _JSON_BASE58_TYPE_KEY = "base58";
		private static ObjectNode _JSON_BASE58_TYPE;
		private static final java.lang.String _JSON_NONEMPTYTEXT_REGEX = "^(?!(?:[ \t\r\n]*)$).*$";

		public static final ObjectNode JSON_VALIDATION_SCHEMA;

		static {
			ObjectNode on;
			try {
				on = (ObjectNode) OBJECT_MAPPER.readTree(FileUtil.getResourceAsString(CoreSchemaLib.class, "json-schema-draft-04.json"));
			} catch (IOException ex) {
				on = null;
			}
			JSON_VALIDATION_SCHEMA = on;
			try {
				_JSON_ANY_TYPE = (ObjectNode) OBJECT_MAPPER.readTree("{\"oneOf\":[{\"type\":\"boolean\"},{\"type\":\"integer\"},{\"type\":\"number\"},{\"type\":\"null\"},{\"type\":\"string\"},{\"type\":\"array\"},{\"type\":\"object\"}]}");
				_JSON_BASE64_TYPE = (ObjectNode) OBJECT_MAPPER.readTree("{\"type\":\"string\",\"pattern\":\"^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$\"}");
				_JSON_BASE64MIME_TYPE = (ObjectNode) OBJECT_MAPPER.readTree("{\"type\":\"string\",\"pattern\":\"^?:data:([a-z]+\\/[a-z0-9-+.]+(;[a-z-]+=[a-z0-9-]+)?)?(;base64)?,(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$\"}");
				_JSON_BASE58_TYPE = (ObjectNode) OBJECT_MAPPER.readTree("{\"type\":\"string\",\"pattern\":\"^(?:[A-NP-Za-km-z1-9+/])*$\"}");
			} catch (IOException ex) {
			}
		}

		public static final ObjectNode forClass(Class<?> type) throws JsonSchemaException {
			ObjectNode schema = OBJECT_MAPPER.createObjectNode();
			ObjectNode definitions = OBJECT_MAPPER.createObjectNode();
			try {
				if (type.isAnnotationPresent(JsonSchema.class)) {
					JsonSchema js = type.getAnnotation(JsonSchema.class);
					schema.set("id", OBJECT_MAPPER.readTree("\"" + js.id() + "\""));
					schema.set("$schema", OBJECT_MAPPER.readTree("\"" + JSON_VALIDATION_SCHEMA_ID + "\""));
					if (!js.title().isEmpty()) {
						schema.set("title", OBJECT_MAPPER.readTree("\"" + js.title() + "\""));
					}
					if (!js.description().isEmpty()) {
						schema.set("description", OBJECT_MAPPER.readTree("\"" + js.description() + "\""));
					}
					if (!(js.definitions().length == 1 && js.definitions()[0].isEmpty())) {
						for (java.lang.String d : js.definitions()) {
							java.lang.String[] sd = d.split("\\:", 2);
							if (sd.length != 2) {
								throw new JsonSchemaException("Invalid Definition '" + d + "'.");
							}
							sd[0] = sd[0].trim();
							sd[1] = sd[1].trim();
							ObjectNode definition = OBJECT_MAPPER.createObjectNode();
							if (!(sd[1].startsWith("{") && sd[1].endsWith("}"))) {
								throw new JsonSchemaException("Invalid Definition '" + d + "'.");
							}
							_setDefinitionWithPropertiesString(definition, sd[1].substring(1, sd[1].length() - 1), type, "");
							if (!definitions.has(sd[0])) {
								definitions.set(sd[0], definition);
							} else if (!definition.equals(definitions.get(sd[0]))) {
								throw new JsonSchemaException("Definition '" + sd[0] + "' already exists.");
							}
						}
					}
				} else {
					schema.set("$schema", OBJECT_MAPPER.readTree("\"" + JSON_VALIDATION_SCHEMA_ID + "\""));
				}
				_addDefinitionForInnerClasses(type, type, definitions);
				ObjectNode definition = OBJECT_MAPPER.createObjectNode();
				setFullDefinitionForAnnotatedElement(type, type, definition, type, definitions);
				if (definitions.size() > 0) {
					schema.set("definitions", definitions);
				}
				schema.setAll(definition);
			} catch (JsonSchemaException ex) {
				throw ex;
			} catch (IOException ex) {
				throw new JsonSchemaException(ex);
			}
			try {
				JSON.validate(JSON_VALIDATION_SCHEMA, schema);
			} catch (JsonValidationException ex) {
				throw new JsonSchemaException(ex);
			}
			return schema;
		}

		public static final ObjectNode getSubSchema(ObjectNode schema, java.lang.String definition) {
			JsonNode defNode = schema.get("definitions");
			if (defNode != null && ObjectNode.class.isInstance(defNode)) {
				defNode = ((ObjectNode) defNode).get(definition);
				if (defNode != null && ObjectNode.class.isInstance(defNode)) {
					ObjectNode subSchema = ((ObjectNode) defNode).deepCopy();
					Map<java.lang.String, ObjectNode> defMap = new LinkedHashMap<>();
					_populateDefinitionMap(schema, subSchema, defMap);
					subSchema.set("definitions", OBJECT_MAPPER.createObjectNode());
					ObjectNode on = (ObjectNode) subSchema.get("definitions");
					defMap.entrySet().forEach((e) -> {
						on.set(e.getKey(), e.getValue());
					});
					return subSchema;
				}
			}
			return null;
		}

		private static void _populateDefinitionMap(ObjectNode schema, JsonNode node, Map<java.lang.String, ObjectNode> defMap) {
			if (node.isObject()) {
				Iterator<Entry<java.lang.String, JsonNode>> i = ((ObjectNode) node).fields();
				while (i.hasNext()) {
					Entry<java.lang.String, JsonNode> e = i.next();
					if (e.getKey().equals("$ref") && e.getValue().isTextual()) {
						java.lang.String path = e.getValue().asText();
						int n = path.indexOf("#/definitions/");
						if (n >= 0) {
							path = path.substring(n + 14);
							java.lang.String[] p = path.split("\\/");
							JsonNode cNode = schema.get("definitions");
							java.lang.String fn = null;
							for (java.lang.String f : p) {
								if (cNode.isObject()) {
									cNode = cNode.get(f);
									fn = f;
								} else {
									cNode = null;
									fn = null;
									break;
								}
							}
							if (cNode != null && cNode.isObject()) {
								defMap.put(fn, (ObjectNode) cNode);
								_populateDefinitionMap(schema, cNode, defMap);
							}
						}
					} else if (e.getValue().isObject() || e.getValue().isArray()) {
						_populateDefinitionMap(schema, e.getValue(), defMap);
					}
				}
			} else if (node.isArray()) {
				Iterator<JsonNode> i = ((ArrayNode) node).elements();
				while (i.hasNext()) {
					_populateDefinitionMap(schema, i.next(), defMap);
				}
			}
		}

		private static void _setDefinitionWithPropertiesString(ObjectNode object, java.lang.String propertiesString, Class<?> rootType, java.lang.String parent) throws JsonSchemaException {
			for (java.lang.String f : _getPropertiesList(propertiesString)) {
				try {
					java.lang.String[] sf = f.split("\\:", 2);
					if (sf.length != 2) {
						throw new JsonSchemaException("Invalid Definition Object '" + f + "'.");
					}
					sf[0] = sf[0].trim();
					sf[1] = sf[1].trim();
					switch (sf[0]) {
						case "items":
						case "properties":
						case "patternProperties":
							if (!(sf[1].startsWith("{") && sf[1].endsWith("}"))) {
								throw new JsonSchemaException("Invalid Definition '" + f + "'.");
							}
							ObjectNode subObject = OBJECT_MAPPER.createObjectNode();
							_setDefinitionWithPropertiesString(subObject, sf[1].substring(1, sf[1].length() - 1), rootType, sf[0]);
							object.set(sf[0], subObject);
							break;
						case "required":
						case "enum":
						case "allOf":
						case "anyOf":
						case "oneOf":
						case "not":
							if (!(sf[1].startsWith("[") && sf[1].endsWith("]"))) {
								throw new JsonSchemaException("Invalid Definition '" + f + "'.");
							}
							ArrayNode subArray = OBJECT_MAPPER.createArrayNode();
							_setArrayWithItemsString(subArray, sf[1].substring(1, sf[1].length() - 1), rootType);
							object.set(sf[0], subArray);
							break;
						case "pattern":
							if (!(sf[1].startsWith("'") && sf[1].endsWith("'"))) {
								throw new JsonSchemaException("Invalid Definition '" + f + "'.");
							}
							object.set(sf[0], OBJECT_MAPPER.readTree("\"" + sf[1].substring(1, sf[1].length() - 1) + "\""));
							break;
						case "type":
							object.set(sf[0], OBJECT_MAPPER.readTree("\"" + sf[1] + "\""));
						case "$ref":
							object.set(sf[0], OBJECT_MAPPER.readTree("\"" + _getReferenceForRef(sf[1], rootType) + "\""));
							break;
						default:
							switch (parent) {
								case "patternProperties":
									if (!(sf[0].startsWith("'") && sf[0].endsWith("'") && sf[1].startsWith("{") && sf[1].endsWith("}"))) {
										throw new JsonSchemaException("Invalid Definition '" + f + "'.");
									}
									ObjectNode patternPropertiesChild = OBJECT_MAPPER.createObjectNode();
									_setDefinitionWithPropertiesString(patternPropertiesChild, sf[1].substring(1, sf[1].length() - 1), rootType, "");
									object.set(sf[0].substring(1, sf[0].length() - 1), patternPropertiesChild);
									break;
								case "items":
								case "properties":
									if (!(sf[1].startsWith("{") && sf[1].endsWith("}"))) {
										throw new JsonSchemaException("Invalid Definition '" + f + "'.");
									}
									ObjectNode child = OBJECT_MAPPER.createObjectNode();
									_setDefinitionWithPropertiesString(child, sf[1].substring(1, sf[1].length() - 1), rootType, "");
									object.set(sf[0], child);
									break;
								default:
									object.set(sf[0], OBJECT_MAPPER.readTree(sf[1]));
									break;
							}
							break;
					}
				} catch (JsonSchemaException ex) {
					throw ex;
				} catch (IOException ex) {
					throw new JsonSchemaException(ex);
				}
			}
		}

		private static void _setArrayWithItemsString(ArrayNode array, java.lang.String items, Class<?> rootType) throws JsonSchemaException {
			for (java.lang.String i : _getPropertiesList(items)) {
				try {
					if (i.startsWith("{") && i.endsWith("}")) {
						ObjectNode subObject = OBJECT_MAPPER.createObjectNode();
						_setDefinitionWithPropertiesString(subObject, i.substring(1, i.length() - 1), rootType, "");
						array.add(subObject);
					} else if (i.startsWith("[") && i.endsWith("]")) {
						ArrayNode subArray = OBJECT_MAPPER.createArrayNode();
						_setArrayWithItemsString(subArray, i.substring(1, i.length() - 1), rootType);
						array.add(subArray);
					} else if (i.startsWith("'") && i.endsWith("'")) {
						array.add(OBJECT_MAPPER.readTree("\"" + i.substring(1, i.length() - 1) + "\""));
					} else {
						array.add(OBJECT_MAPPER.readTree(i));
					}
				} catch (JsonSchemaException ex) {
					throw ex;
				} catch (IOException ex) {
					throw new JsonSchemaException(ex);
				}
			}
		}

		private static List<java.lang.String> _getPropertiesList(java.lang.String propertiesString) {
			propertiesString = propertiesString.trim();
			List<java.lang.String> sp = new ArrayList<>();
			int s = 0;
			boolean dq = false;
			boolean sq = false;
			int p = 0;
			int b = 0;
			int c = 0;
			for (int n = 0; n < propertiesString.length(); n++) {
				boolean next = true;
				if (n + 1 < propertiesString.length()) {
					java.lang.String ec = propertiesString.substring(n, n + 2);
					if ((ec.equals("\\'") && sq) || (ec.equals("\\\"") && dq)) {
						n += 2;
						next = false;
					}
				}
				if (next) {
					char a = propertiesString.charAt(n);
					if (a == ',' && !sq && !dq && p == 0 && b == 0 && c == 0) {
						if (s < n && !propertiesString.substring(s, n).trim().isEmpty()) {
							sp.add(propertiesString.substring(s, n).replace("\\'", "'").trim());
						}
						s = n + 1;
					} else if (a == '(' && !sq && !dq) {
						p++;
					} else if (a == ')' && !sq && !dq) {
						p--;
					} else if (a == '[' && !sq && !dq) {
						b++;
					} else if (a == ']' && !sq && !dq) {
						b--;
					} else if (a == '{' && !sq && !dq) {
						c++;
					} else if (a == '}' && !sq && !dq) {
						c--;
					} else if ((a == '\t' || a == '\r' || a == '\n' || a == '\\') && (sq || dq)) {
						propertiesString = propertiesString.substring(0, n) + (a == '\t' ? "\\t" : (a == '\r' ? "\\r" : (a == '\n' ? "\\n" : "\\\\"))) + (n < propertiesString.length() ? propertiesString.substring(n + 1) : "");
						n++;
					} else if (a == '\'') {
						sq = !sq;
					} else if (a == '"') {
						dq = !dq;
					}
				}
			}
			if (s < propertiesString.length() && !propertiesString.substring(s).trim().isEmpty()) {
				sp.add(propertiesString.substring(s).replace("\\'", "'").trim());
			}
			return sp;
		}

		private static void _addDefinitionForInnerClasses(Class<?> type, Class<?> rootType, ObjectNode definitions) throws JsonSchemaException {
			for (Class<?> cls : type.getClasses()) {
				if (!cls.isAnnotationPresent(JsonSchema.class) && !cls.isAnnotationPresent(NoDefinition.class)) {
					java.lang.String defName = getNameForAnnotatedElement(cls);
					ObjectNode def = OBJECT_MAPPER.createObjectNode();
					setFullDefinitionForAnnotatedElement(cls, cls, def, rootType, definitions);
					if (!definitions.has(defName)) {
						definitions.set(defName, def);
						_addDefinitionForInnerClasses(cls, rootType, definitions);
					} else if (!def.equals(definitions.get(defName))) {
						throw new JsonSchemaException("Definition '" + defName + "' already exists: rename class '" + cls.getSimpleName() + "' or annotate it using @Definition with an alternative name.");
					}
				}
			}
		}

		protected static Class<?> getTypeForAnnotatedElement(AnnotatedElement anElt) {
			if (Class.class.isInstance(anElt)) {
				return (Class<?>) anElt;
			} else if (Constructor.class.isInstance(anElt)) {
				return ((Constructor) anElt).getDeclaringClass();
			} else if (Field.class.isInstance(anElt)) {
				return ((Field) anElt).getType();
			} else if (Parameter.class.isInstance(anElt)) {
				return ((Parameter) anElt).getType();
			} else if (Method.class.isInstance(anElt)) {
				return ((Method) anElt).getReturnType();
			} else {
				return null;
			}
		}

		protected static java.lang.String getNameForAnnotatedElement(AnnotatedElement anElt) {
			if (anElt.isAnnotationPresent(JsonProperty.class)) {
				JsonProperty jp = anElt.getAnnotation(JsonProperty.class);
				if (!jp.value().equals(JsonProperty.USE_DEFAULT_NAME)) {
					return jp.value();
				}
			}
			if (Class.class.isInstance(anElt)) {
				if (anElt.isAnnotationPresent(JsonSchema.Definition.class)) {
					JsonSchema.Definition jd = anElt.getAnnotation(JsonSchema.Definition.class);
					if (jd.value().isEmpty()) {
						java.lang.String name = ((Class<?>) anElt).getSimpleName();
						return name.substring(0, 1).toLowerCase() + name.substring(1);
					} else {
						return jd.value();
					}
				} else {
					java.lang.String name = ((Class<?>) anElt).getSimpleName();
					return name.substring(0, 1).toLowerCase() + name.substring(1);
				}
			} else if (Field.class.isInstance(anElt)) {
				return ((Field) anElt).getName();
			} else if (Method.class.isInstance(anElt)) {
				return ((Method) anElt).getName();
			} else if (Parameter.class.isInstance(anElt)) {
				return ((Parameter) anElt).getName();
			} else {
				return null;
			}
		}

		private static Class<?> _getSchemaRootClass(Class<?> type, Class<?> rootType) {
			while (type != null) {
				if (type == rootType) {
					return type;
				} else if (type.isAnnotationPresent(JsonSchema.class) || type.getEnclosingClass() == null) {
					return type;
				} else {
					type = type.getEnclosingClass();
				}
			}
			return type;
		}

		private static java.lang.String _getReferenceForType(Class<?> type, Class<?> rootType) {
			Class<?> otherRootType = _getSchemaRootClass(type, rootType);
			if (otherRootType.isAnnotationPresent(JsonSchema.class)) {
				java.lang.String otherId = otherRootType.getAnnotation(JsonSchema.class).id();
				if (rootType.isAnnotationPresent(JsonSchema.class)) {
					java.lang.String pathFileAndRef[] = getURIPathFileAndRef(rootType.getAnnotation(JsonSchema.class).id());
					java.lang.String otherPathFileAndRef[] = getURIPathFileAndRef(otherId);
					if (pathFileAndRef[0].equals(otherPathFileAndRef[0])) {
						if (pathFileAndRef[1].equals(otherPathFileAndRef[1])) {
							return "#/definitions/" + getNameForAnnotatedElement(type);
						} else {
							return otherPathFileAndRef[1] + "#/definitions/" + getNameForAnnotatedElement(type);
						}
					} else {
						return otherId;
					}
				} else if (otherRootType == type) {
					return otherId;
				} else {
					return otherId + "#/definitions/" + getNameForAnnotatedElement(type);
				}
			} else {
				return "#/definitions/" + getNameForAnnotatedElement(type);
			}
		}

		private static java.lang.String _getReferenceForRef(java.lang.String ref, Class<?> rootType) {
			if (rootType.isAnnotationPresent(JsonSchema.class)) {
				java.lang.String pathFileAndRef[] = getURIPathFileAndRef(rootType.getAnnotation(JsonSchema.class).id());
				java.lang.String otherPathFileAndRef[] = getURIPathFileAndRef(ref);
				if (pathFileAndRef[0].equals(otherPathFileAndRef[0])) {
					if (pathFileAndRef[1].equals(otherPathFileAndRef[1])) {
						return otherPathFileAndRef[2];
					} else {
						return otherPathFileAndRef[1] + otherPathFileAndRef[2];
					}
				}
			}
			return ref;
		}

		public static java.lang.String[] getURIPathFileAndRef(java.lang.String uriFullPath) {
			java.lang.String p;
			java.lang.String ref;
			int i = uriFullPath.lastIndexOf("#");
			if (i > -1) {
				p = uriFullPath.substring(0, i).trim();
				ref = uriFullPath.substring(i).trim();
			} else {
				p = uriFullPath.trim();
				ref = "";
			}
			i = p.lastIndexOf("/");
			if (i > -1) {
				return new java.lang.String[]{p.substring(0, i + 1).trim(), p.substring(i + 1).trim(), ref};
			} else {
				return new java.lang.String[]{"", p.trim(), ref};
			}
		}

		protected static Parameter getJsonCreatorConstructorParameterForField(Class<?> cls, Field f) throws JsonSchemaException {
			java.lang.String name = getNameForAnnotatedElement(f);
			for (Constructor c : cls.getConstructors()) {
				if (c.isAnnotationPresent(JsonCreator.class)) {
					for (Parameter p : c.getParameters()) {
						if (name.equals(getNameForAnnotatedElement(p))) {
							if (f.getType().isAssignableFrom(p.getType())) {
								return p;
							} else {
								throw new JsonSchemaException("Class '" + cls.getName() + "': Type '" + p.getType().getName() + "' of Parameter '" + name + "' in JsonCreator Constructor is not compatible with related Field Type '" + f.getType() + "'.");
							}
						}
					}
				}
			}
			Class<?> dcls = f.getDeclaringClass();
			if (dcls == cls) {
				return null;
			} else {
				return getJsonCreatorConstructorParameterForField(dcls, f);
			}
		}

		private static void _mergeObjectNodes(ObjectNode on1, ObjectNode on2) {
			if (!on1.has("allOf") && !on1.has("anyOf") && !on1.has("oneOf") && !on1.has("not") && !on1.has("$ref")) {
				Iterator<Entry<java.lang.String, JsonNode>> i = on2.fields();
				while (i.hasNext()) {
					Entry<java.lang.String, JsonNode> e = i.next();
					if (!on1.has(e.getKey())) {
						on1.set(e.getKey(), e.getValue());
					} else if (on1.get(e.getKey()).isArray() && on2.get(e.getKey()).isArray()) {
						_mergeArrayNodes((ArrayNode) on1.get(e.getKey()), (ArrayNode) on2.get(e.getKey()));
					} else if (on1.get(e.getKey()).isObject() && on2.get(e.getKey()).isObject()) {
						_mergeObjectNodes((ObjectNode) on1.get(e.getKey()), (ObjectNode) on2.get(e.getKey()));
					}
				}
			}
			if (!on1.has("default") && on2.has("default")) {
				on1.set("default", on2.get("default"));
			}
		}

		private static void _mergeArrayNodes(ArrayNode an1, ArrayNode an2) {
			Iterator<JsonNode> i = an2.elements();
			while (i.hasNext()) {
				JsonNode j = i.next();
				boolean has = false;
				Iterator<JsonNode> ii = an1.elements();
				while (ii.hasNext()) {
					if (ii.next().toString().equals(j.toString())) {
						has = true;
						break;
					}
				}
				if (!has) {
					an1.add(j);
				}
			}
		}

		private static void _setAlternativeForType(AnnotatedElement anElt, Class<?> type, ObjectNode definition, Class<?> rootType, ObjectNode definitions) throws JsonSchemaException {
			java.lang.String key;
			java.lang.String[] value;
			if (anElt.isAnnotationPresent(JsonSchema.AllOf.class)) {
				key = "allOf";
				value = anElt.getAnnotation(JsonSchema.AllOf.class).value();
			} else if (anElt.isAnnotationPresent(JsonSchema.AnyOf.class)) {
				key = "anyf";
				value = anElt.getAnnotation(JsonSchema.AnyOf.class).value();
			} else if (anElt.isAnnotationPresent(JsonSchema.OneOf.class)) {
				key = "oneOf";
				value = anElt.getAnnotation(JsonSchema.OneOf.class).value();
			} else {
				key = "not";
				value = anElt.getAnnotation(JsonSchema.Not.class).value();
			}
			if (Class.class.isAssignableFrom(anElt.getClass())) {
				if (value.length == 1 && (value[0].trim().isEmpty() || value[0].trim().equals("*"))) {
					ArrayNode array = OBJECT_MAPPER.createArrayNode();
					_setAlternativesForType(type, array, rootType, definitions);
					if (array.size() > 0) {
						definition.set(key, array);
					}
				} else {
					ArrayNode array = OBJECT_MAPPER.createArrayNode();
					_setAlternativesForType(type, value, array, rootType, definitions);
					if (array.size() > 0) {
						definition.set(key, array);
					}
				}
			} else if (!(value.length == 1 && (value[0].trim().isEmpty() || value[0].trim().equals("*")))) {
				ArrayNode array = OBJECT_MAPPER.createArrayNode();
				for (java.lang.String strDef : value) {
					ObjectNode def = OBJECT_MAPPER.createObjectNode();
					_setDefinitionWithPropertiesString(def, strDef, rootType, "");
					array.add(def);
				}
				if (array.size() > 0) {
					definition.set(key, array);
				}
			}
		}

		private static void _setAlternativesForType(Class<?> type, ArrayNode alternatives, Class<?> rootType, ObjectNode definitions) throws JsonSchemaException {
			for (Field f : JSON.JsonObject.getAnnotatedFieldsForClass(type).keySet()) {
				ObjectNode alternative = OBJECT_MAPPER.createObjectNode();
				try {
					alternative.set("type", OBJECT_MAPPER.readTree("\"object\""));
					ObjectNode properties = OBJECT_MAPPER.createObjectNode();
					ArrayNode required = OBJECT_MAPPER.createArrayNode();
					ObjectNode fieldDef = OBJECT_MAPPER.createObjectNode();
					_setDefinitionForAnnotatedElement(f, fieldDef, rootType, definitions, required);
					Parameter p = getJsonCreatorConstructorParameterForField(type, f);
					if (p != null) {
						ObjectNode paramDef = OBJECT_MAPPER.createObjectNode();
						_setDefinitionForAnnotatedElement(p, paramDef, rootType, definitions, required);
						_mergeObjectNodes(fieldDef, paramDef);
					}
					properties.set(getNameForAnnotatedElement(f), fieldDef);
					alternative.set("properties", properties);
					if (required.size() > 0) {
						alternative.set("required", required);
					}
					alternative.set("additionalProperties", OBJECT_MAPPER.readTree("false"));
					alternatives.add(alternative);
				} catch (JsonSchemaException ex) {
					throw ex;
				} catch (IOException ex) {
					throw new JsonSchemaException(ex);
				}
			}
		}

		private static void _setAlternativesForType(Class<?> type, java.lang.String[] altGroups, ArrayNode array, Class<?> rootType, ObjectNode definitions) throws JsonSchemaException {
			for (java.lang.String flStr : altGroups) {
				java.lang.String[] fa = flStr.split(",");
				ObjectNode alt = OBJECT_MAPPER.createObjectNode();
				try {
					alt.set("type", OBJECT_MAPPER.readTree("\"object\""));
					ObjectNode properties = OBJECT_MAPPER.createObjectNode();
					ArrayNode required = OBJECT_MAPPER.createArrayNode();
					for (java.lang.String fStr : fa) {
						try {
							Field f = type.getField(fStr.trim());
							if ((f.getModifiers() & 9) == 1 && !f.isAnnotationPresent(JsonIgnore.class)) {
								ObjectNode fieldDef = OBJECT_MAPPER.createObjectNode();
								_setDefinitionForAnnotatedElement(f, fieldDef, rootType, definitions, required);
								Parameter p = getJsonCreatorConstructorParameterForField(type, f);
								if (p != null) {
									ObjectNode paramDef = OBJECT_MAPPER.createObjectNode();
									_setDefinitionForAnnotatedElement(p, paramDef, rootType, definitions, required);
									_mergeObjectNodes(fieldDef, paramDef);
								}
								properties.set(getNameForAnnotatedElement(f), fieldDef);
							}
						} catch (NoSuchFieldException | SecurityException ex) {
							throw new JsonSchemaException(ex);
						}
					}
					if (properties.size() > 0) {
						alt.set("properties", properties);
						if (required.size() > 0) {
							alt.set("required", required);
						}
						alt.set("additionalProperties", OBJECT_MAPPER.readTree("false"));
					}
					array.add(alt);
				} catch (JsonSchemaException ex) {
					throw ex;
				} catch (IOException ex) {
					throw new JsonSchemaException(ex);
				}
			}
		}

		private static void _setDefinitionForAnnotatedElement(AnnotatedElement anElt, ObjectNode definition, Class<?> rootType, ObjectNode definitions, ArrayNode required) throws JsonSchemaException {
			Class<?> type = getTypeForAnnotatedElement(anElt);
			if (type != null) {
				try {
					if (anElt.isAnnotationPresent(JsonSchema.AllOf.class)
							|| anElt.isAnnotationPresent(JsonSchema.AnyOf.class)
							|| anElt.isAnnotationPresent(JsonSchema.OneOf.class)
							|| anElt.isAnnotationPresent(JsonSchema.Not.class)) {
						_setAlternativeForType(anElt, type, definition, rootType, definitions);
					} else if (rootType == type) {
						definition.set("$ref", OBJECT_MAPPER.readTree("\"#\""));
					} else if (type.isAnnotationPresent(JsonSchema.class)) {
						JsonSchema js = type.getAnnotation(JsonSchema.class);
						if (anElt.isAnnotationPresent(JsonSchema.NoDefinition.class)) {
							ObjectNode def = (ObjectNode) parse(new URL(js.id()), JSON_VALIDATION_SCHEMA);
							def.remove("id");
							def.remove("$schema");
							def.remove("title");
							def.remove("description");
							if (def.has("definitions")) {
								definitions.setAll((ObjectNode) def.get("definitions"));
								def.remove("definitions");
							}
							if (def.has("required")) {
								Iterator<JsonNode> i = ((ArrayNode) def.get("required")).iterator();
								List<JsonNode> toBeAdded = new ArrayList<>();
								while (i.hasNext()) {
									Iterator<JsonNode> ir = required.iterator();
									JsonNode jn = i.next();
									java.lang.String f = jn.asText();
									boolean contains = false;
									while (ir.hasNext()) {
										if (ir.next().asText().equals(f)) {
											contains = true;
											break;
										}
									}
									if (!contains) {
										toBeAdded.add(jn);
									}
								}
								required.addAll(toBeAdded);
								def.remove("required");
							}
							definition.setAll(def);
						} else {
							definition.set("$ref", OBJECT_MAPPER.readTree("\"" + js.id() + "\""));
						}
					} else {
						if (type.isAnnotationPresent(JsonSchema.NoDefinition.class) || anElt.isAnnotationPresent(JsonSchema.NoDefinition.class)
								|| Object.class == type
								|| JSON.isJsonBoolean(type)
								|| JSON.isJsonNumber(type)
								|| JSON.isJsonString(type)
								|| JSON.isJsonArray(type)
								|| Map.class.isAssignableFrom(type) || ObjectNode.class.isAssignableFrom(type)) {
							if (anElt.isAnnotationPresent(JsonSchema.Ref.class)) {
								definition.set("$ref", OBJECT_MAPPER.readTree("\"" + _getReferenceForRef(anElt.getAnnotation(JsonSchema.Ref.class).value(), rootType) + "\""));
							} else {
								setFullDefinitionForAnnotatedElement(anElt, type, definition, rootType, definitions);
							}
						} else {
							java.lang.String defName = _getReferenceForType(type, rootType);
							if (defName.startsWith("#/definitions/")) {
								ObjectNode def = OBJECT_MAPPER.createObjectNode();
								setFullDefinitionForAnnotatedElement(anElt, type, def, rootType, definitions);
								if (!definitions.has(defName.substring(14))) {
									definitions.set(defName.substring(14), def);
								} else if (!def.equals(definitions.get(defName.substring(14)))) {
									throw new JsonSchemaException("Definition '" + defName + "' already exists: rename class '" + type.getSimpleName() + "' or annotate it using @Definition with an alternative name.");
								}
							}
							definition.set("$ref", OBJECT_MAPPER.readTree("\"" + defName + "\""));
						}
						if (anElt.isAnnotationPresent(JsonSchema.Required.class) && anElt.getAnnotation(JsonSchema.Required.class).value()) {
							Iterator<JsonNode> i = required.elements();
							java.lang.String reqName = getNameForAnnotatedElement(anElt);
							boolean has = false;
							if (reqName != null) {
								while (i.hasNext()) {
									if (reqName.equals(i.next().asText())) {
										has = true;
										break;
									}
								}
								if (!has) {
									required.add(OBJECT_MAPPER.readTree("\"" + reqName + "\""));
								}
							}
						}
						if (anElt.isAnnotationPresent(JsonProperty.class)) {
							if (!anElt.getAnnotation(JsonProperty.class).defaultValue().isEmpty()) {
								java.lang.String defaultValue;
								if (CharSequence.class.isAssignableFrom(type)) {
									defaultValue = "\"" + anElt.getAnnotation(JsonProperty.class).defaultValue() + "\"";
								} else {
									defaultValue = anElt.getAnnotation(JsonProperty.class).defaultValue();
								}
								definition.set("default", OBJECT_MAPPER.readTree(defaultValue));
							}
						}
					}

				} catch (JsonSchemaException ex) {
					throw ex;
				} catch (IOException ex) {
					throw new JsonSchemaException(ex);
				}
			}
		}

		protected static void setFullDefinitionForAnnotatedElement(AnnotatedElement anElt, Class<?> type, ObjectNode definition, Class<?> rootType, ObjectNode definitions) throws JsonSchemaException {
			try {
				if (type.isAnnotationPresent(JsonSchema.AllOf.class)
						|| type.isAnnotationPresent(JsonSchema.AnyOf.class)
						|| type.isAnnotationPresent(JsonSchema.OneOf.class)
						|| type.isAnnotationPresent(JsonSchema.Not.class)) {
					_setAlternativeForType(type, type, definition, rootType, definitions);
				} else if (anElt.isAnnotationPresent(JsonSchema.AllOf.class)
						|| anElt.isAnnotationPresent(JsonSchema.AnyOf.class)
						|| anElt.isAnnotationPresent(JsonSchema.OneOf.class)
						|| anElt.isAnnotationPresent(JsonSchema.Not.class)) {
					_setAlternativeForType(anElt, type, definition, rootType, definitions);
				} else if (JSON.isJsonBoolean(type)) {
					definition.set("type", OBJECT_MAPPER.readTree("\"boolean\""));
				} else if (JSON.isJsonDate(type)) {
					definition.set("type", OBJECT_MAPPER.readTree("\"integer\""));
					_setNumberDefinition(anElt, definition);
					if (!definition.has("minimum")) {
						definition.set("minimum", OBJECT_MAPPER.readTree("0"));
					}
				} else if (JSON.isJsonInteger(type)) {
					definition.set("type", OBJECT_MAPPER.readTree("\"integer\""));
					_setNumberDefinition(anElt, definition);
				} else if (JSON.isJsonNumber(type)) {
					definition.set("type", OBJECT_MAPPER.readTree("\"number\""));
					_setNumberDefinition(anElt, definition);
				} else if (JSON.isJsonBinary(type)) {
					if (anElt.isAnnotationPresent(JsonSerialize.class)) {
						if (JSON.Base64MIMETypeSerializer.class.isAssignableFrom(anElt.getAnnotation(JsonSerialize.class).using())) {
							if (!definitions.has(_JSON_BASE64MIME_TYPE_KEY)) {
								definitions.set(_JSON_BASE64MIME_TYPE_KEY, _JSON_BASE64MIME_TYPE);
							} else if (!_JSON_BASE64MIME_TYPE.equals(definitions.get(_JSON_BASE64MIME_TYPE_KEY))) {
								throw new JsonSchemaException("Definition '" + _JSON_BASE64MIME_TYPE_KEY + "' already exists.");
							}
							definition.set("$ref", OBJECT_MAPPER.readTree("\"#/definitions/" + _JSON_BASE64MIME_TYPE_KEY + "\""));
							return;
						} else if (JSON.Base58Serializer.class.isAssignableFrom(anElt.getAnnotation(JsonSerialize.class).using())) {
							if (!definitions.has(_JSON_BASE58_TYPE_KEY)) {
								definitions.set(_JSON_BASE58_TYPE_KEY, _JSON_BASE58_TYPE);
							} else if (!_JSON_BASE58_TYPE.equals(definitions.get(_JSON_BASE58_TYPE_KEY))) {
								throw new JsonSchemaException("Definition '" + _JSON_BASE58_TYPE_KEY + "' already exists.");
							}
							definition.set("$ref", OBJECT_MAPPER.readTree("\"#/definitions/" + _JSON_BASE58_TYPE_KEY + "\""));
							return;
						}
					}
					if (!definitions.has(_JSON_BASE64_TYPE_KEY)) {
						definitions.set(_JSON_BASE64_TYPE_KEY, _JSON_BASE64_TYPE);
					} else if (_JSON_BASE64_TYPE != null && !_JSON_BASE64_TYPE.equals(definitions.get(_JSON_BASE64_TYPE_KEY))) {
						throw new JsonSchemaException("Definition '" + _JSON_BASE64_TYPE_KEY + "' already exists.");
					}
					definition.set("$ref", OBJECT_MAPPER.readTree("\"#/definitions/" + _JSON_BASE64_TYPE_KEY + "\""));
				} else if (JSON.isJsonString(type)) {
					_setStringDefinition(anElt, definition);
				} else if (JSON.isJsonArray(type)) {
					_setArrayDefinition(anElt, type, definition, rootType, definitions);
				} else if (JSON.isJsonObject(type)) {
					_setObjectDefinition(anElt, type, definition, rootType, definitions);
				}
			} catch (JsonSchemaException ex) {
				throw ex;
			} catch (IOException ex) {
				throw new JsonSchemaException(ex);
			}
		}

		private static void _setNumberDefinition(AnnotatedElement anElt, ObjectNode definition) throws JsonSchemaException {
			try {
				if (anElt.isAnnotationPresent(JsonSchema.Number.MultipleOf.class)) {
					definition.set("multipleOf", OBJECT_MAPPER.readTree("" + anElt.getAnnotation(JsonSchema.Number.MultipleOf.class).value()));
				}
				if (anElt.isAnnotationPresent(JsonSchema.Number.Maximum.class)) {
					definition.set("maximum", OBJECT_MAPPER.readTree("" + anElt.getAnnotation(JsonSchema.Number.Maximum.class).value()));
				}
				if (anElt.isAnnotationPresent(JsonSchema.Number.ExclusiveMaximum.class)) {
					definition.set("exclusiveMaximum", OBJECT_MAPPER.readTree(anElt.getAnnotation(JsonSchema.Number.ExclusiveMaximum.class).value() ? "true" : "false"));
				}
				if (anElt.isAnnotationPresent(JsonSchema.Number.Minimum.class)) {
					definition.set("minimum", OBJECT_MAPPER.readTree("" + anElt.getAnnotation(JsonSchema.Number.Minimum.class).value()));
				}
				if (anElt.isAnnotationPresent(JsonSchema.Number.ExclusiveMinimum.class)) {
					definition.set("exclusiveMinimum", OBJECT_MAPPER.readTree(anElt.getAnnotation(JsonSchema.Number.ExclusiveMinimum.class).value() ? "true" : "false"));
				}
				if (anElt.isAnnotationPresent(JsonSchema.Number.Enum.class)) {
					double[] e = anElt.getAnnotation(JsonSchema.Number.Enum.class).value();
					StringBuilder sb = new StringBuilder();
					for (double d : e) {
						if (sb.length() > 0) {
							sb.append(",");
						}
						if (Math.floor(d) == d) {
							sb.append(Math.round(d));
						} else {
							sb.append(d);
						}
					}
					definition.set("enum", OBJECT_MAPPER.readTree("[" + sb.toString() + "]"));
				}
			} catch (IOException ex) {
				throw new JsonSchemaException(ex);
			}
		}

		private static void _setStringDefinition(AnnotatedElement anElt, ObjectNode definition) throws JsonSchemaException {
			try {
				definition.set("type", OBJECT_MAPPER.readTree("\"string\""));
				if (anElt.isAnnotationPresent(JsonSchema.String.MaxLength.class)) {
					definition.set("maxLength", OBJECT_MAPPER.readTree("" + anElt.getAnnotation(JsonSchema.String.MaxLength.class).value()));
				}
				if (anElt.isAnnotationPresent(JsonSchema.String.MinLength.class)) {
					definition.set("minLength", OBJECT_MAPPER.readTree("" + anElt.getAnnotation(JsonSchema.String.MinLength.class).value()));
				}
				if (anElt.isAnnotationPresent(JsonSchema.String.Pattern.class)) {
					definition.set("pattern", OBJECT_MAPPER.readTree("\"" + anElt.getAnnotation(JsonSchema.String.Pattern.class).value() + "\""));
				}
				if (anElt.isAnnotationPresent(JsonSchema.String.Enum.class)) {
					java.lang.String[] e = anElt.getAnnotation(JsonSchema.String.Enum.class).value();
					StringBuilder sb = new StringBuilder();
					for (java.lang.String s : e) {
						if (sb.length() > 0) {
							sb.append(",");
						}
						sb.append("\"").append(s).append("\"");
					}
					definition.set("enum", OBJECT_MAPPER.readTree("[" + sb.toString() + "]"));
				}
			} catch (IOException ex) {
				throw new JsonSchemaException(ex);
			}
		}

		private static void _setArrayDefinition(AnnotatedElement anElt, Class<?> type, ObjectNode definition, Class<?> rootType, ObjectNode definitions) throws JsonSchemaException {
			try {
				boolean additionalItems = false;
				definition.set("type", OBJECT_MAPPER.readTree("\"array\""));
				ObjectNode itemsParams = null;
				if (anElt.isAnnotationPresent(JsonSchema.Array.Items.class)) {
					JsonSchema.Array.Items it = anElt.getAnnotation(JsonSchema.Array.Items.class);
					if (it.type() != UndefinedValueType.class) {
						if (Field.class.isAssignableFrom(anElt.getClass()) && Collection.class.isAssignableFrom(type) && ParameterizedType.class.isInstance(((Field) anElt).getGenericType())) {
							_checkType(((ParameterizedType) ((Field) anElt).getGenericType()).getActualTypeArguments()[0], it.type());
						}
						itemsParams = OBJECT_MAPPER.createObjectNode();
						ArrayNode required = OBJECT_MAPPER.createArrayNode();
						_setDefinitionForAnnotatedElement(it.type(), itemsParams, rootType, definitions, required);
						if (required.size() > 0) {
							itemsParams.set("required", required);
						}
					} else if (!it.ref().isEmpty()) {
						itemsParams = OBJECT_MAPPER.createObjectNode();
						itemsParams.set("$ref", OBJECT_MAPPER.readTree("\"" + _getReferenceForRef(it.ref(), rootType) + "\""));
					} else if (!it.value().isEmpty()) {
						itemsParams = OBJECT_MAPPER.createObjectNode();
						_setDefinitionWithPropertiesString(itemsParams, it.value(), rootType, "");
					}
				}
				if (itemsParams == null) {
					itemsParams = OBJECT_MAPPER.createObjectNode();
					ArrayNode required = OBJECT_MAPPER.createArrayNode();
					if (boolean[].class.isAssignableFrom(type) || Boolean[].class.isAssignableFrom(type)) {
						_setDefinitionForAnnotatedElement(boolean.class, itemsParams, rootType, definitions, required);
					} else if (short[].class == type || Short[].class.isAssignableFrom(type)
							|| int[].class == type || Integer[].class.isAssignableFrom(type)
							|| long[].class == type || Long[].class.isAssignableFrom(type)) {
						_setDefinitionForAnnotatedElement(long.class, itemsParams, rootType, definitions, required);
					} else if (Date[].class.isAssignableFrom(type)) {
						_setDefinitionForAnnotatedElement(long.class, itemsParams, rootType, definitions, required);
						if (!itemsParams.has("minimum")) {
							itemsParams.set("minimum", OBJECT_MAPPER.readTree("0"));
						}
					} else if (float[].class == type || Float[].class.isAssignableFrom(type)
							|| double[].class == type || Double[].class.isAssignableFrom(type)) {
						_setDefinitionForAnnotatedElement(double.class, itemsParams, rootType, definitions, required);
					} else if (byte[].class == type || Byte[].class.isAssignableFrom(type)
							|| BigInteger[].class.isAssignableFrom(type) || BigDecimal[].class.isAssignableFrom(type)
							|| CharSequence[].class.isAssignableFrom(type)) {
						_setDefinitionForAnnotatedElement(CharSequence.class, itemsParams, rootType, definitions, required);
					} else if (JsonNode.class.isAssignableFrom(type)) {
						_setDefinitionForAnnotatedElement(Object.class, itemsParams, rootType, definitions, required);
					} else if (Collection.class.isAssignableFrom(type)) {
						if (Field.class.isAssignableFrom(anElt.getClass()) && ParameterizedType.class.isInstance(((Field) anElt).getGenericType())) {
							_setSubtypeDefinition(((ParameterizedType) ((Field) anElt).getGenericType()).getActualTypeArguments()[0], itemsParams, rootType, definitions, required);
						} else {
							_setDefinitionForAnnotatedElement(Object.class, itemsParams, rootType, definitions, required);
						}
					}
					if (required.size() > 0) {
						itemsParams.set("required", required);
					}
				}
				if (itemsParams.size() > 0) {
					definition.set("items", itemsParams);
				} else {
					additionalItems = true;
				}
				if (anElt.isAnnotationPresent(JsonSchema.Array.MaxItems.class)) {
					definition.set("maxItems", OBJECT_MAPPER.readTree("" + anElt.getAnnotation(JsonSchema.Array.MaxItems.class).value()));
				}
				if (anElt.isAnnotationPresent(JsonSchema.Array.MinItems.class)) {
					definition.set("minItems", OBJECT_MAPPER.readTree("" + anElt.getAnnotation(JsonSchema.Array.MinItems.class).value()));
				}
				if ((anElt.isAnnotationPresent(JsonSchema.Array.UniqueItems.class) && anElt.getAnnotation(JsonSchema.Array.UniqueItems.class).value())
						|| (Collection.class.isAssignableFrom(type) && (!anElt.isAnnotationPresent(JsonSchema.Array.UniqueItems.class) || anElt.getAnnotation(JsonSchema.Array.UniqueItems.class).value()))) {
					definition.set("uniqueItems", OBJECT_MAPPER.readTree("true"));
				}
				if (!definition.has("$ref") && !additionalItems) {
					definition.set("additionalItems", OBJECT_MAPPER.readTree("false"));
				}
			} catch (JsonSchemaException ex) {
				throw ex;
			} catch (IOException ex) {
				throw new JsonSchemaException(ex);
			}
		}

		private static void _setObjectDefinition(AnnotatedElement anElt, Class<?> type, ObjectNode definition, Class<?> rootType, ObjectNode definitions) throws JsonSchemaException {
			try {
				boolean additionalProperties = false;
				if (type.isAnnotationPresent(Object.PatternProperties.class) || anElt.isAnnotationPresent(Object.PatternProperties.class)) {
					definition.set("type", OBJECT_MAPPER.readTree("\"object\""));
					ObjectNode patternPropertiesParams = OBJECT_MAPPER.createObjectNode();
					Object.PatternProperties pp = type.isAnnotationPresent(Object.PatternProperties.class) ? type.getAnnotation(Object.PatternProperties.class) : anElt.getAnnotation(Object.PatternProperties.class);
					java.lang.String keyPattern;
					if (pp.key().trim().isEmpty() || pp.key().trim().equals("*")) {
						if (Map.class.isAssignableFrom(type) && Field.class.isAssignableFrom(anElt.getClass()) && ParameterizedType.class.isInstance(((Field) anElt).getGenericType())) {
							_checkMapKeyIsCharSequence(((ParameterizedType) ((Field) anElt).getGenericType()).getActualTypeArguments()[0]);
							keyPattern = _JSON_NONEMPTYTEXT_REGEX;
						} else if (ObjectNode.class.isAssignableFrom(type)) {
							keyPattern = _JSON_NONEMPTYTEXT_REGEX;
						} else {
							StringBuilder sb = new StringBuilder();
							JSON.JsonObject.getAnnotatedFieldsForClass(type).keySet().forEach((f) -> {
								if (sb.length() > 0) {
									sb.append("|");
								}
								sb.append(getNameForAnnotatedElement(f));
							});
							keyPattern = "^(" + sb.toString() + ")$";
						}
					} else {
						keyPattern = pp.key();
					}
					ObjectNode patternPropertiesValuesParams = OBJECT_MAPPER.createObjectNode();
					if (pp.valueType() != UndefinedValueType.class) {
						if (Map.class.isAssignableFrom(type) && Field.class.isAssignableFrom(anElt.getClass()) && ParameterizedType.class.isInstance(((Field) anElt).getGenericType())) {
							_checkType(((ParameterizedType) ((Field) anElt).getGenericType()).getActualTypeArguments()[1], pp.valueType());
						} else if (type != Object.class && !ObjectNode.class.isAssignableFrom(type)) {
							for (Field f : JSON.JsonObject.getAnnotatedFieldsForClass(type).keySet()) {
								if (!f.getType().isAssignableFrom(pp.valueType())) {
									throw new JsonSchemaException("Class '" + type.getName() + "': Type of Field '" + f.getType().getName() + " " + f.getName() + " is not compatible with Value Type defined in @PatternProperties '" + pp.valueType().getName() + "'.");
								}
							}
						}
						ArrayNode required = OBJECT_MAPPER.createArrayNode();
						_setDefinitionForAnnotatedElement(pp.valueType(), patternPropertiesValuesParams, rootType, definitions, required);
						if (required.size() > 0) {
							patternPropertiesValuesParams.set("required", required);
						}
					} else if (!pp.value().isEmpty()) {
						_setDefinitionWithPropertiesString(patternPropertiesValuesParams, pp.value(), rootType, "");
					} else if (!pp.valueRef().isEmpty()) {
						patternPropertiesValuesParams.set("$ref", OBJECT_MAPPER.readTree("\"" + _getReferenceForRef(pp.valueRef(), rootType) + "\""));
					} else if (Map.class.isAssignableFrom(type)) {
						ArrayNode required = OBJECT_MAPPER.createArrayNode();
						if (Field.class.isAssignableFrom(anElt.getClass()) && ParameterizedType.class.isInstance(((Field) anElt).getGenericType())) {
							_checkMapKeyIsCharSequence(((ParameterizedType) ((Field) anElt).getGenericType()).getActualTypeArguments()[0]);
							_setSubtypeDefinition(((ParameterizedType) ((Field) anElt).getGenericType()).getActualTypeArguments()[1], patternPropertiesValuesParams, rootType, definitions, required);
						} else {
							_setDefinitionForAnnotatedElement(Object.class, patternPropertiesValuesParams, rootType, definitions, required);
						}
						if (required.size() > 0) {
							patternPropertiesValuesParams.set("required", required);
						}
					} else if (Object.class == type || ObjectNode.class.isAssignableFrom(type)) {
						ArrayNode required = OBJECT_MAPPER.createArrayNode();
						_setDefinitionForAnnotatedElement(Object.class, patternPropertiesValuesParams, rootType, definitions, required);
						if (required.size() > 0) {
							patternPropertiesValuesParams.set("required", required);
						}
					} else {
						throw new JsonSchemaException("PatternProperties cannot be applied on Object Class '" + type.getName() + "'.");
					}
					patternPropertiesParams.set(keyPattern, patternPropertiesValuesParams);
					definition.set("patternProperties", patternPropertiesParams);
				} else if (Map.class.isAssignableFrom(type)) {
					definition.set("type", OBJECT_MAPPER.readTree("\"object\""));
					ObjectNode patternPropertiesParams = OBJECT_MAPPER.createObjectNode();
					ObjectNode patternPropertiesValuesParams = OBJECT_MAPPER.createObjectNode();
					ArrayNode required = OBJECT_MAPPER.createArrayNode();
					if (Field.class.isAssignableFrom(anElt.getClass()) && ParameterizedType.class.isInstance(((Field) anElt).getGenericType())) {
						_checkMapKeyIsCharSequence(((ParameterizedType) ((Field) anElt).getGenericType()).getActualTypeArguments()[0]);
						_setSubtypeDefinition(((ParameterizedType) ((Field) anElt).getGenericType()).getActualTypeArguments()[1], patternPropertiesValuesParams, rootType, definitions, required);
					} else {
						_setDefinitionForAnnotatedElement(Object.class, patternPropertiesValuesParams, rootType, definitions, required);
					}
					if (required.size() > 0) {
						patternPropertiesValuesParams.set("required", required);
					}
					patternPropertiesParams.set(_JSON_NONEMPTYTEXT_REGEX, patternPropertiesValuesParams);
					definition.set("patternProperties", patternPropertiesParams);
				} else if (Object.class == type || ObjectNode.class.isAssignableFrom(type)) {
					if (!definitions.has(_JSON_ANY_TYPE_KEY)) {
						definitions.set(_JSON_ANY_TYPE_KEY, _JSON_ANY_TYPE);
					} else if (_JSON_ANY_TYPE != null && !_JSON_ANY_TYPE.equals(definitions.get(_JSON_ANY_TYPE_KEY))) {
						throw new JsonSchemaException("Definition '" + _JSON_ANY_TYPE_KEY + "' already exists.");
					}
					definition.set("$ref", OBJECT_MAPPER.readTree("\"#/definitions/" + _JSON_ANY_TYPE_KEY + "\""));
				} else {
					definition.set("type", OBJECT_MAPPER.readTree("\"object\""));
					ObjectNode properties = OBJECT_MAPPER.createObjectNode();
					ArrayNode required = OBJECT_MAPPER.createArrayNode();
					for (Field f : JSON.JsonObject.getAnnotatedFieldsForClass(type).keySet()) {
						ObjectNode fieldDef = OBJECT_MAPPER.createObjectNode();
						_setDefinitionForAnnotatedElement(f, fieldDef, rootType, definitions, required);
						Parameter p = getJsonCreatorConstructorParameterForField(type, f);
						if (p != null) {
							ObjectNode paramDef = OBJECT_MAPPER.createObjectNode();
							_setDefinitionForAnnotatedElement(p, paramDef, rootType, definitions, required);
							_mergeObjectNodes(fieldDef, paramDef);
						}
						properties.set(getNameForAnnotatedElement(f), fieldDef);
					}
					if (properties.size() > 0) {
						definition.set("properties", properties);
						if (required.size() > 0) {
							definition.set("required", required);
						}
					} else {
						additionalProperties = true;
					}
				}
				if (type.isAnnotationPresent(Object.MaxProperties.class) || anElt.isAnnotationPresent(Object.MaxProperties.class)) {
					definition.set("maxProperties", OBJECT_MAPPER.readTree("" + (type.isAnnotationPresent(Object.MaxProperties.class) ? type.getAnnotation(Object.MaxProperties.class).value() : anElt.getAnnotation(Object.MaxProperties.class).value())));
				}
				if (type.isAnnotationPresent(Object.MinProperties.class) || anElt.isAnnotationPresent(Object.MinProperties.class)) {
					definition.set("minProperties", OBJECT_MAPPER.readTree("" + (type.isAnnotationPresent(Object.MinProperties.class) ? type.getAnnotation(Object.MinProperties.class).value() : anElt.getAnnotation(Object.MinProperties.class).value())));
				}
				if (!definition.has("$ref") && !additionalProperties) {
					definition.set("additionalProperties", OBJECT_MAPPER.readTree("false"));
				}
			} catch (JsonSchemaException ex) {
				throw ex;
			} catch (IOException ex) {
				throw new JsonSchemaException(ex);
			}
		}

		private static void _checkType(Type typeToBeChecked, Class<?> parentClass) throws JsonSchemaException {
			Class<?> classToBeChecked;
			if (ParameterizedType.class.isInstance(typeToBeChecked)) {
				classToBeChecked = (Class<?>) ((ParameterizedType) typeToBeChecked).getRawType();
			} else {
				classToBeChecked = (Class<?>) typeToBeChecked;
			}
			if (!classToBeChecked.isAssignableFrom(parentClass)) {
				throw new JsonSchemaException("Type '" + classToBeChecked.getName() + "' is not compatible with '" + parentClass.getName() + "'.");
			}
		}

		private static void _checkMapKeyIsCharSequence(Type type) throws JsonSchemaException {
			if (type.getClass() != Class.class) {
				throw new JsonSchemaException("Map Key Generic Type must be is not compatible with CharSequence Class.");
			}
			if (!CharSequence.class.isAssignableFrom((Class<?>) type)) {
				throw new JsonSchemaException("Map Key Generic Type '" + ((Class<?>) type).getName() + "' is not compatible with CharSequence Class.");
			}
		}

		private static void _setSubtypeDefinition(Type t, ObjectNode definition, Class<?> rootType, ObjectNode definitions, ArrayNode required) throws IOException {
			Class<?> valuePType;
			if (ParameterizedType.class.isInstance(t)) {
				valuePType = (Class<?>) ((ParameterizedType) t).getRawType();
				if (Collection.class.isAssignableFrom(valuePType)) {
					definition.set("type", OBJECT_MAPPER.readTree("\"array\""));
					ObjectNode subValueDefinition = OBJECT_MAPPER.createObjectNode();
					ArrayNode subValueRequired = OBJECT_MAPPER.createArrayNode();
					_setSubtypeDefinition(((ParameterizedType) t).getActualTypeArguments()[0], subValueDefinition, rootType, definitions, subValueRequired);
					if (subValueRequired.size() > 0) {
						subValueDefinition.set("required", subValueRequired);
					}
					definition.set("items", subValueDefinition);
					definition.set("additionalItems", OBJECT_MAPPER.readTree("false"));
					return;
				} else if (Map.class.isAssignableFrom(valuePType)) {
					definition.set("type", OBJECT_MAPPER.readTree("\"object\""));
					ObjectNode subValueDefinition = OBJECT_MAPPER.createObjectNode();
					ArrayNode subValueRequired = OBJECT_MAPPER.createArrayNode();
					_setSubtypeDefinition(((ParameterizedType) t).getActualTypeArguments()[1], subValueDefinition, rootType, definitions, subValueRequired);
					if (subValueRequired.size() > 0) {
						subValueDefinition.set("required", subValueRequired);
					}
					ObjectNode ppDef = OBJECT_MAPPER.createObjectNode();
					ppDef.set(_JSON_NONEMPTYTEXT_REGEX, subValueDefinition);
					definition.set("patternProperties", ppDef);
					definition.set("additionalProperties", OBJECT_MAPPER.readTree("false"));
					return;
				}
			} else {
				valuePType = (Class<?>) t;
			}
			_setDefinitionForAnnotatedElement(valuePType, definition, rootType, definitions, required);
		}

		public static final class UndefinedValueType {
		}

	}

	public static class JsonSchemaException extends IOException {

		public JsonSchemaException(java.lang.String msg, Throwable rootCause) {
			super(msg, rootCause);
		}

		public JsonSchemaException(java.lang.String msg) {
			super(msg);
		}

		public JsonSchemaException(Throwable rootCause) {
			this(null, rootCause);
		}

	}

}
