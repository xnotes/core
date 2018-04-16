/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.google.json.JsonSanitizer;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.util.ObjectUtils;
import org.xnotes.core.CoreSchemaLib;
import org.xnotes.core.db.XNotesSchema;
import org.xnotes.core.net.protocol.Message;
import org.xnotes.core.utils.JsonSchema.JsonSchemaException;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class JSON {

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	public static final JsonValidator VALIDATOR;

	static {
		JsonValidator v;
		try {
			LoadingConfigurationBuilder lcb = LoadingConfiguration.newBuilder();
			v = JsonSchemaFactory.newBuilder().setLoadingConfiguration(
					LoadingConfiguration.newBuilder()
							.preloadSchema(JsonSchema.Schema.forClass(CoreSchemaLib.class))
							.preloadSchema(JsonSchema.Schema.forClass(Message.class))
							.preloadSchema(JsonSchema.Schema.forClass(XNotesSchema.class))
							.setEnableCache(true)
							.freeze())
					.freeze().getValidator();
		} catch (IOException ex) {
			v = null;
		}
		VALIDATOR = v;
	}

	public static final String stringify(Object object) {
		return stringify(object, false);
	}

	public static final String stringify(Object object, ObjectNode schema) throws JsonValidationException {
		return stringify(object, schema, false);
	}

	public static final String stringify(Object object, boolean prettyPrint) {
		try {
			return stringify(object, null, prettyPrint);
		} catch (JsonValidationException ex) {
			return null;
		}
	}

	public static final String stringify(Object object, ObjectNode schema, boolean prettyPrint) throws JsonValidationException {
		String jsonString;
		try {
			jsonString = prettyPrint ? OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object) : OBJECT_MAPPER.writeValueAsString(object);
		} catch (JsonProcessingException ex) {
			throw new JsonValidationException(ex);
		}
//		if (schema != null || JSON.Object.class.isInstance(object)) {
//			try {
//				parse(jsonString, schema != null ? schema : ((JSON.Object) object).getSchema(), object.getClass());
//			} catch (JsonValidationException ex) {
//				if (schema == null) {
//					return jsonString;
//				} else {
//					throw ex;
//				}
//			} catch (JsonParseException ex) {
//				throw new JsonValidationException(ex);
//			}
//		}
		return jsonString;
	}

	public static final JsonNode parse(String jsonString) throws JsonParseException {
		return parse(jsonString, JsonNode.class);
	}

	public static final <T> T parse(String jsonString, Class<T> type) throws JsonParseException {
		try {
			return parse(jsonString, null, type);
		} catch (JsonValidationException ex) {
			throw new JsonParseException(ex);
		}
	}

	public static final JsonNode parse(String jsonString, ObjectNode schema) throws JsonParseException, JsonValidationException {
		return parse(jsonString, schema, JsonNode.class);
	}

	public static final <T> T parse(String jsonString, ObjectNode schema, Class<T> type) throws JsonParseException, JsonValidationException {
		try {
			return _parse(OBJECT_MAPPER.readTree(JsonSanitizer.sanitize(jsonString)), schema, type);
		} catch (JsonParseException | JsonValidationException ex) {
			throw ex;
		} catch (IOException ex) {
			throw new JsonParseException(ex);
		}
	}

	public static final JsonNode parse(URL url) throws JsonParseException {
		return parse(url, JsonNode.class);
	}

	public static final <T> T parse(URL url, Class<T> type) throws JsonParseException {
		try {
			return parse(url, null, type);
		} catch (JsonValidationException ex) {
			return null;
		}
	}

	public static final JsonNode parse(URL url, ObjectNode schema) throws JsonParseException, JsonValidationException {
		return parse(url, schema, JsonNode.class);
	}

	public static final <T> T parse(URL url, ObjectNode schema, Class<T> type) throws JsonParseException, JsonValidationException {
		try {
			return _parse(OBJECT_MAPPER.readTree(url), schema, type);
		} catch (JsonParseException | JsonValidationException ex) {
			throw ex;
		} catch (IOException ex) {
			throw new JsonParseException(ex);
		}
	}

	private static <T> T _parse(JsonNode jn, ObjectNode schema, Class<T> type) throws JsonParseException, JsonValidationException {
		try {
			if (NullNode.class.isInstance(jn) && isJsonObject(type)) {
				jn = OBJECT_MAPPER.createObjectNode();
			}
			T obj = JsonNode.class.isAssignableFrom(type) ? (T) jn : OBJECT_MAPPER.treeToValue(jn, type);
			if (schema != null || JSON.JsonObject.class.isAssignableFrom(type)) {
				validate(schema != null ? schema : ((JSON.JsonObject) obj).getSchema(), jn);
			}
			return obj;
		} catch (JsonProcessingException ex) {
			throw new JsonParseException(ex);
		}
	}

	public static final <T> T parseFile(String filePath, Class<T> type, T defaultObject) throws JsonParseException, IOException {
		return parseFile(filePath, type, defaultObject, false);
	}

	public static final <T> T parseFile(String filePath, Class<T> type, T defaultObject, boolean prettyPrint) throws JsonParseException, IOException {
		return parseFile(filePath, null, type, defaultObject, prettyPrint);
	}

	public static final <T> T parseFile(String filePath, ObjectNode schema, Class<T> type, T defaultObject) throws JsonParseException, IOException {
		return parseFile(filePath, schema, type, defaultObject, false);
	}

	public static final <T> T parseFile(String filePath, ObjectNode schema, Class<T> type, T defaultObject, boolean prettyPrint) throws JsonParseException, IOException {
		String fp = FileUtil.getActualPath(filePath);
		T o;
		if (Files.notExists(Paths.get(fp))) {
			if (defaultObject != null) {
				writeToFile(defaultObject, filePath, prettyPrint);
				o = defaultObject;
			} else {
				return null;
			}
		} else {
			o = parse(new String(Files.readAllBytes(Paths.get(fp))), schema, type);
		}
		if (JSON.JsonObject.class.isAssignableFrom(type)) {
			try {
				((JSON.JsonObject) o)._lastSaveHash = MessageDigest.getInstance(JsonObject.DIGEST_ALGORTHM).digest(o.toString().getBytes());
				((JSON.JsonObject) o).setFilePath(filePath);
			} catch (NoSuchAlgorithmException ex) {
				throw new IOException(ex);
			}
		}
		return o;
	}

	public static void validate(ObjectNode schema, JsonNode jn) throws JsonValidationException {
		if (VALIDATOR != null) {
			try {
				ProcessingReport pr = VALIDATOR.validate(schema, jn);
				if (!pr.isSuccess()) {
					StringBuilder sb = new StringBuilder();
					pr.forEach((processingMessage) -> {
						if (sb.length() > 0) {
							sb.append(", ");
						}
						sb.append(processingMessage.getMessage());
					});
					throw new JsonValidationException("Exceptions while validating '" + schema.toString() + "': " + sb.toString());
				}
			} catch (ProcessingException ex) {
				throw new JsonValidationException(ex);
			}
		}
	}

	public static final String writeToFile(Object object, String filePath) throws IOException {
		return writeToFile(object, filePath, false);
	}

	public static final String writeToFile(Object object, String filePath, boolean prettyPrint) throws IOException {
		String content = stringify(object, prettyPrint);
		if (content != null) {
			FileUtil.writeToFile(filePath, content);
			return content;
		} else {
			throw new IOException("Object of Class '" + object.getClass().getName() + "' could not be stringified.");
		}
	}

	protected static boolean isJsonBoolean(Class<?> type) {
		return boolean.class == type || Boolean.class.isAssignableFrom(type) || BooleanNode.class.isAssignableFrom(type);
	}

	protected static boolean isJsonInteger(Class<?> type) {
		return byte.class == type || Byte.class.isAssignableFrom(type)
				|| short.class == type || Short.class.isAssignableFrom(type)
				|| int.class == type || Integer.class.isAssignableFrom(type)
				|| long.class == type || Long.class.isAssignableFrom(type)
				|| isJsonDate(type)
				|| IntNode.class.isAssignableFrom(type);
	}

	protected static boolean isJsonNumber(Class<?> type) {
		return isJsonInteger(type)
				|| float.class == type || Float.class.isAssignableFrom(type)
				|| double.class == type || Double.class.isAssignableFrom(type)
				|| NumericNode.class.isAssignableFrom(type);
	}

	protected static boolean isJsonDate(Class<?> type) {
		return Date.class.isAssignableFrom(type);
	}

	protected static boolean isJsonBinary(Class<?> type) {
		return byte[].class == type || ByteBuffer.class.isAssignableFrom(type);
	}

	protected static boolean isJsonString(Class<?> type) {
		return CharSequence.class.isAssignableFrom(type)
				|| isJsonBinary(type)
				|| BigInteger.class.isAssignableFrom(type) || BigDecimal.class.isAssignableFrom(type)
				|| TextNode.class.isAssignableFrom(type);
	}

	protected static boolean isJsonArray(Class<?> type) {
		return boolean[].class == type
				|| short[].class == type
				|| int[].class == type
				|| long[].class == type
				|| float[].class == type
				|| double[].class == type
				|| Object[].class.isAssignableFrom(type)
				|| ArrayNode.class.isAssignableFrom(type)
				|| Collection.class.isAssignableFrom(type);
	}

	protected static boolean isJsonObject(Class<?> type) {
		return !isJsonBoolean(type) && !isJsonNumber(type) && !isJsonString(type) && !isJsonArray(type);
	}

	public static abstract class JsonObject extends JsonSerializable.Base {

		protected static final String DIGEST_ALGORTHM = "SHA1";

		private static final Map<Class<?>, ObjectNode> _SCHEMAS = new HashMap<>();

		private static final Map<Class<?>, Map<Field, List<Annotation>>> _FIELD_INFO = new HashMap<>();

		private byte[] _lastSaveHash = null;
		private String _filePath = null;

		public ObjectNode getSchema() {
			Class<?> cls = this.getClass();
			if (!_SCHEMAS.containsKey(cls)) {
				try {
					_SCHEMAS.put(cls, JsonSchema.Schema.forClass(cls));
				} catch (JsonSchema.JsonSchemaException ex) {
				}
			}
			return _SCHEMAS.get(this.getClass());
		}

		@JsonIgnore
		public String toPrettyPrintedString() {
			return stringify(this, true);
		}

		@Override
		public final String toString() {
			return stringify(this);
		}

		@JsonIgnore
		public byte[] toBytes() {
			return stringify(this).getBytes();
		}

		@JsonIgnore
		public ByteBuffer toByteBuffer() {
			return ByteBuffer.wrap(this.toBytes());
		}

		@JsonIgnore
		public ObjectNode toObjectNode() {
			try {
				return (ObjectNode) OBJECT_MAPPER.readTree(this.toString());
			} catch (IOException ex) {
				return null;
			}
		}

		@JsonIgnore
		public final String getFilePath() {
			return _filePath;
		}

		@JsonIgnore
		public final void setFilePath(String filePath) {
			_filePath = filePath;
		}

		@JsonIgnore
		public synchronized void save() throws IOException {
			try {
				byte[] currentHash = MessageDigest.getInstance(DIGEST_ALGORTHM).digest(this.toString().getBytes());
				if (_lastSaveHash == null || !Arrays.equals(_lastSaveHash, currentHash)) {
					JSON.writeToFile(this, _filePath, true);
					_lastSaveHash = currentHash;
				}
			} catch (NoSuchAlgorithmException ex) {
				throw new IOException(ex);
			}
		}

		@JsonIgnore
		public void saveAs(String filePath) throws IOException {
			_filePath = filePath;
			this.save();
		}

		@Override
		public boolean equals(Object other) {
			if (other == null) {
				return false;
			}
			if (this == other) {
				return true;
			}
			if (this.getClass() != other.getClass()) {
				return false;
			}
			Map<Field, List<Annotation>> m = _getAnnotatedFields();
			if (m != null) {
				for (Field f : m.keySet()) {
					try {
						Object v = f.get(this);
						Object ov = f.get(other);
						if (!((v == null && ov == null) || (v != null && v.equals(ov)))) {
							return false;
						}
					} catch (IllegalArgumentException | IllegalAccessException ex) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return this.getClass().hashCode() * 8 + ObjectUtils.nullSafeHashCode(this.toString());
		}

		@Override
		public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
			Map<Field, List<Annotation>> m = _getAnnotatedFields();
			if (m != null) {
				gen.writeStartObject();
				List<Field> fields = new ArrayList<>();
				if (this.getClass().isAnnotationPresent(JsonPropertyOrder.class)) {
					List<Field> fl = new ArrayList<>(m.keySet());
					for (String fStr : this.getClass().getAnnotation(JsonPropertyOrder.class).value()) {
						Field f = null;
						for (Field ff : fl) {
							String name = JsonSchema.Schema.getNameForAnnotatedElement(ff);
							if (name != null && name.equals(fStr.trim())) {
								f = ff;
								fl.remove(f);
								break;
							}
						}
						if (f != null) {
							fields.add(f);
						}
					}
				}
				m.keySet().stream().filter((f) -> (!fields.contains(f))).forEachOrdered((f) -> {
					fields.add(f);
				});
				for (Field f : fields) {
					if (m.containsKey(f)) {
						List<Annotation> al = m.get(f);
						String name = JsonSchema.Schema.getNameForAnnotatedElement(f);
						Object v;
						try {
							v = f.get(this);
							boolean write = true;
							JsonSchema.Required rq = _getAnnotationForAnnotationClass(al, JsonSchema.Required.class);
							if (rq == null || !rq.value()) {
								JsonInclude inc = _getAnnotationForAnnotationClass(al, JsonInclude.class);
								JsonProperty jp = _getAnnotationForAnnotationClass(al, JsonProperty.class);
								String df = jp != null && !jp.defaultValue().isEmpty() ? jp.defaultValue() : null;
								if (inc != null) {
									switch (inc.value()) {
										case NON_ABSENT:
										case NON_NULL:
											write = v != null;
											break;
										case NON_DEFAULT:
											write = !_isDefault(f, v, df);
											break;
										case NON_EMPTY:
											write = !_isEmpty(f, v, df);
											break;
										default:
											break;
									}
								} else {
									write = _isPresent(f, v);
								}
							}
							if (write) {
								JsonSerialize js = _getAnnotationForAnnotationClass(al, JsonSerialize.class);
								if (js != null) {
									gen.writeFieldName(name);
									try {
										js.using().getConstructor().newInstance().serialize(v, gen, serializers);
									} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
										try {
											gen.writeObject(v);
										} catch (IOException ex1) {
											gen.writeNull();
										}
									}
								} else {
									gen.writeObjectField(name, v);
								}
							}
						} catch (IllegalArgumentException | IllegalAccessException ex) {
						}
					}
				}
				gen.writeEndObject();
			} else {
				gen.writeNull();
			}
		}

		@Override
		public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
			this.serialize(gen, serializers);
		}

		@Override
		public boolean isEmpty(SerializerProvider serializers) {
			Map<Field, List<Annotation>> m = _getAnnotatedFields();
			if (m != null) {
				for (Entry<Field, List<Annotation>> e : m.entrySet()) {
					Object v;
					try {
						v = e.getKey().get(this);
						JsonProperty jp = _getAnnotationForAnnotationClass(e.getValue(), JsonProperty.class);
						String df = jp != null && !jp.defaultValue().isEmpty() ? jp.defaultValue() : null;
						if (!_isEmpty(e.getKey(), v, df)) {
							return false;
						}
					} catch (IllegalArgumentException | IllegalAccessException ex) {
					}
				}
			}
			return true;
		}

		@JsonIgnore
		public boolean isEmpty() {
			return this.isEmpty(null);
		}

		private static boolean _isPresent(Field f, Object v) {
			Class<?> type = f.getType();
			return !((type == boolean.class && (v == null || !(boolean) v))
					|| (type == byte.class && (v == null || (byte) v == (byte) 0))
					|| (type == short.class && (v == null || (short) v == (short) 0))
					|| (type == int.class && (v == null || (int) v == 0))
					|| (type == long.class && (v == null || (long) v == 0L))
					|| (type == float.class && (v == null || (float) v == 0.0f))
					|| (type == double.class && (v == null || (double) v == 0.0d))
					|| (type == boolean[].class && (v == null || Arrays.equals((boolean[]) v, new boolean[((boolean[]) v).length])))
					|| (type == byte[].class && (v == null || Arrays.equals((byte[]) v, new byte[((byte[]) v).length])))
					|| (type == short[].class && (v == null || Arrays.equals((short[]) v, new short[((short[]) v).length])))
					|| (type == int[].class && (v == null || Arrays.equals((int[]) v, new int[((int[]) v).length])))
					|| (type == long[].class && (v == null || Arrays.equals((long[]) v, new long[((long[]) v).length])))
					|| (type == float[].class && (v == null || Arrays.equals((float[]) v, new float[((float[]) v).length])))
					|| (type == double[].class && (v == null || Arrays.equals((double[]) v, new double[((double[]) v).length])))
					|| (Object[].class.isAssignableFrom(type) && (v == null || Arrays.equals((Object[]) v, new Object[((Object[]) v).length])))
					|| v == null);
		}

		private static boolean _isDefault(Field f, Object v, String df) {
			Class<?> type = f.getType();
			if (df == null) {
				try {
					return (!_isPresent(f, v)
							|| v.equals(v.getClass().getConstructor().newInstance()));
				} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
					return false;
				}
			} else {
				try {
					Object dv = OBJECT_MAPPER.readValue(isJsonString(type) ? "\"" + df + "\"" : df, type);
					return (type == boolean.class && (v == null || (boolean) dv))
							|| (type == byte.class && (v == null || (byte) v == (byte) dv))
							|| (type == short.class && (v == null || (short) v == (short) dv))
							|| (type == int.class && (v == null || (int) v == (int) dv))
							|| (type == long.class && (v == null || (long) v == (long) dv))
							|| (type == float.class && (v == null || (float) v == (float) dv))
							|| (type == double.class && (v == null || (double) v == (double) dv))
							|| (type == boolean[].class && (v == null || Arrays.equals((boolean[]) v, (boolean[]) dv)))
							|| (type == byte[].class && (v == null || Arrays.equals((byte[]) v, (byte[]) dv)))
							|| (type == short[].class && (v == null || Arrays.equals((short[]) v, (short[]) dv)))
							|| (type == int[].class && (v == null || Arrays.equals((int[]) v, (int[]) dv)))
							|| (type == long[].class && (v == null || Arrays.equals((long[]) v, (long[]) dv)))
							|| (type == float[].class && (v == null || Arrays.equals((float[]) v, (float[]) dv)))
							|| (type == double[].class && (v == null || Arrays.equals((double[]) v, (double[]) dv)))
							|| (Object[].class.isAssignableFrom(type) && (v == null || Arrays.equals((Object[]) v, (Object[]) dv)))
							|| v == null
							|| v.equals(dv);
				} catch (IOException ex) {
					return false;
				}
			}
		}

		private static boolean _isEmpty(Field f, Object v, String df) {
			if (_isDefault(f, v, df)) {
				return true;
			} else {
				return _isEmpty(f, v);
			}
		}

		private static boolean _isEmpty(Field f, Object v) {
			if (v == null) {
				return true;
			} else {
				try {
					Method m = v.getClass().getMethod("isEmpty", SerializerProvider.class);
					if (m.getReturnType() == boolean.class) {
						return (boolean) m.invoke(v, (SerializerProvider) null);
					}
				} catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException | IllegalArgumentException ex) {
					try {
						Method m = v.getClass().getMethod("isEmpty");
						if (m.getReturnType() == boolean.class) {
							return (boolean) m.invoke(v);
						}
					} catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException | IllegalArgumentException ex2) {
					}
				}
				return false;
			}
		}

		private Map<Field, List<Annotation>> _getAnnotatedFields() {
			List<Map<Field, List<Annotation>>> annotatedFieldAlternatives = _getAnnotatedFieldAlternatives();
			while (annotatedFieldAlternatives.size() > 1) {
				List<Map<Field, List<Annotation>>> deselected = new ArrayList<>();
				for (Map<Field, List<Annotation>> map : annotatedFieldAlternatives) {
					if (!deselected.contains(map)) {
						for (Entry<Field, List<Annotation>> e : map.entrySet()) {
							try {
								Field f = e.getKey();
								Object v = f.get(this);
								JsonProperty jp = _getAnnotationForAnnotationClass(e.getValue(), JsonProperty.class);
								String df = jp != null && !jp.defaultValue().isEmpty() ? jp.defaultValue() : null;
								JsonSchema.Required rq = _getAnnotationForAnnotationClass(e.getValue(), JsonSchema.Required.class);
								boolean include = false;
								if (rq != null && rq.value()) {
									if (_isDefault(f, v, df)) {
										if (!deselected.contains(map)) {
											deselected.add(map);
										}
										break;
									} else {
										include = true;
									}
								} else {
									if (!_isDefault(f, v, df)) {
										JsonInclude ji = _getAnnotationForAnnotationClass(e.getValue(), JsonInclude.class);
										if (ji != null) {
											switch (ji.value()) {
												case NON_ABSENT:
												case NON_NULL:
													include = v != null;
													break;
												case NON_EMPTY:
													include = !_isEmpty(f, v);
													break;
												default:
													include = true;
													break;
											}
										} else {
											include = true;
										}
									}
								}
								if (include) {
									annotatedFieldAlternatives.stream().filter((m) -> (!m.containsKey(f) && !deselected.contains(m))).forEachOrdered((m) -> {
										deselected.add(m);
									});
								}
							} catch (IllegalArgumentException | IllegalAccessException ex) {
							}
						}
					}
				}
				deselected.forEach((m) -> {
					annotatedFieldAlternatives.remove(m);
				});
				if (annotatedFieldAlternatives.isEmpty() || annotatedFieldAlternatives.size() == 1) {
					break;
				}
			}
			if (annotatedFieldAlternatives.isEmpty()) {
				return null;
			} else {
				return annotatedFieldAlternatives.get(0);
			}
		}

		private List<Map<Field, List<Annotation>>> _getAnnotatedFieldAlternatives() {
			Class<?> cls = this.getClass();
			List<Map<Field, List<Annotation>>> anFieldAlt = new ArrayList<>();
			String[] fa = null;
			if (cls.isAnnotationPresent(JsonSchema.AllOf.class)) {
				fa = cls.getAnnotation(JsonSchema.AllOf.class).value();
			} else if (cls.isAnnotationPresent(JsonSchema.AnyOf.class)) {
				fa = cls.getAnnotation(JsonSchema.AnyOf.class).value();
			} else if (cls.isAnnotationPresent(JsonSchema.OneOf.class)) {
				fa = cls.getAnnotation(JsonSchema.OneOf.class).value();
			} else if (cls.isAnnotationPresent(JsonSchema.Not.class)) {
				fa = cls.getAnnotation(JsonSchema.Not.class).value();
			} else {
				anFieldAlt.add(getAnnotatedFieldsForClass(cls));
			}
			if (fa != null) {
				if (fa.length == 1 && fa[0].equals("*")) {
					getAnnotatedFieldsForClass(cls).entrySet().stream().map((e) -> {
						Map<Field, List<Annotation>> m = new LinkedHashMap<>();
						m.put(e.getKey(), e.getValue());
						return m;
					}).forEachOrdered((m) -> {
						anFieldAlt.add(m);
					});
				} else {
					for (String fieldListStr : fa) {
						Map<Field, List<Annotation>> m = new LinkedHashMap<>();
						String[] fgStr = fieldListStr.split("\\,");
						for (String fn : fgStr) {
							Entry<Field, List<Annotation>> e = _getAnnotatedFieldForName(cls, fn.trim());
							if (e != null) {
								m.put(e.getKey(), e.getValue());
							}
						}
						anFieldAlt.add(m);
					}
				}
			}
			return anFieldAlt;
		}

		protected static Map<Field, List<Annotation>> getAnnotatedFieldsForClass(Class<?> cls) {
			if (!_FIELD_INFO.containsKey(cls)) {
				Map<Field, List<Annotation>> annotatedFields = new LinkedHashMap<>();
				for (Field f : cls.getFields()) {
					if ((f.getModifiers() & 9) == 1 && !f.isAnnotationPresent(JsonIgnore.class)) {
						List<Annotation> fi = new ArrayList<>(Arrays.asList(f.getAnnotations()));
						try {
							Parameter p = JsonSchema.Schema.getJsonCreatorConstructorParameterForField(cls, f);
							if (p != null) {
								Arrays.asList(p.getAnnotations()).forEach((a) -> {
									Annotation an = _getAnnotationForAnnotationClass(fi, a.annotationType());
									if (an != null) {
										fi.remove(an);
									}
									fi.add(a);
								});
							}
							if (f.getDeclaringClass() != cls) {
								Map<Field, List<Annotation>> am = getAnnotatedFieldsForClass(f.getDeclaringClass());
								am.get(f).forEach((a) -> {
									if (_getAnnotationForAnnotationClass(fi, a.annotationType()) == null) {
										fi.add(a);
									}
								});
							}
							if (cls.isAnnotationPresent(JsonInclude.class) && _getAnnotationForAnnotationClass(fi, JsonInclude.class) == null) {
								fi.add(cls.getAnnotation(JsonInclude.class));
							}
							annotatedFields.put(f, fi);
						} catch (JsonSchemaException ex) {
							break;
						}
					}
				}
				_FIELD_INFO.put(cls, annotatedFields);
			}
			return new LinkedHashMap<>(_FIELD_INFO.get(cls));
		}

		private static Entry<Field, List<Annotation>> _getAnnotatedFieldForName(Class<?> cls, String fieldName) {
			for (Entry<Field, List<Annotation>> e : getAnnotatedFieldsForClass(cls).entrySet()) {
				if (e.getKey().getName().equals(fieldName)) {
					return e;
				}
			}
			return null;
		}

		private static <T extends Annotation> T _getAnnotationForAnnotationClass(List<Annotation> al, Class<T> annotationClass) {
			for (Annotation a : al) {
				if (a.annotationType() == annotationClass) {
					return (T) a;
				}
			}
			return null;
		}

	}

	public static final class Base58Serializer extends StdSerializer<byte[]> {

		public Base58Serializer() {
			this(null);
		}

		public Base58Serializer(Class<byte[]> valueType) {
			super(valueType);
		}

		@Override
		public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			try {
				gen.writeString(DatatypeConverter.printBase58Binary(value));
			} catch (IllegalArgumentException ex) {
				throw new IOException(ex);
			}
		}
	}

	public static final class Base58Deserializer extends StdDeserializer<byte[]> {

		public Base58Deserializer() {
			this(null);
		}

		public Base58Deserializer(Class<byte[]> valueType) {
			super(valueType);
		}

		@Override
		public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			try {
				return DatatypeConverter.parseBase58Binary(p.getText());
			} catch (IllegalArgumentException ex) {
				throw new IOException(ex);
			}
		}

	}

//	public static final class Base64Serializer extends StdSerializer<byte[]> {
//
//		public Base64Serializer() {
//			this(null);
//		}
//
//		public Base64Serializer(Class<byte[]> valueType) {
//			super(valueType);
//		}
//
//		@Override
//		public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
//			try {
//				gen.writeString(DatatypeConverter.printBase64Binary(value));
//			} catch (IllegalArgumentException ex) {
//				throw new IOException(ex);
//			}
//		}
//	}
//
//	public static final class Base64Deserializer extends StdDeserializer<byte[]> {
//
//		public Base64Deserializer() {
//			this(null);
//		}
//
//		public Base64Deserializer(Class<byte[]> valueType) {
//			super(valueType);
//		}
//
//		@Override
//		public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//			try {
//				return DatatypeConverter.parseBase64Binary(p.getText());
//			} catch (IllegalArgumentException ex) {
//				throw new IOException(ex);
//			}
//		}
//
//	}
	public static final class Base64MIMETypeSerializer extends StdSerializer<byte[]> {

		public Base64MIMETypeSerializer() {
			this(null);
		}

		public Base64MIMETypeSerializer(Class<byte[]> valueType) {
			super(valueType);
		}

		@Override
		public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			try {
				gen.writeString(DatatypeConverter.printBase64MIMETypeBinary(value));
			} catch (IllegalArgumentException ex) {
				throw new IOException(ex);
			}
		}
	}

	public static final class Base64MIMETypeDeserializer extends StdDeserializer<byte[]> {

		public Base64MIMETypeDeserializer() {
			this(null);
		}

		public Base64MIMETypeDeserializer(Class<byte[]> valueType) {
			super(valueType);
		}

		@Override
		public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			try {
				return DatatypeConverter.parseBase64MIMETypeBinary(p.getText());
			} catch (IllegalArgumentException ex) {
				throw new IOException(ex);
			}
		}

	}

	public static final class Base64ByteBufferSerializer extends StdSerializer<ByteBuffer> {

		public Base64ByteBufferSerializer() {
			this(null);
		}

		public Base64ByteBufferSerializer(Class<ByteBuffer> valueType) {
			super(valueType);
		}

		@Override
		public void serialize(ByteBuffer value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			try {
				gen.writeString(DatatypeConverter.printBase64Binary(value.array()));
			} catch (IllegalArgumentException ex) {
				throw new IOException(ex);
			}
		}
	}

	public static final class Base64ByteBufferDeserializer extends StdDeserializer<ByteBuffer> {

		public Base64ByteBufferDeserializer() {
			this(null);
		}

		public Base64ByteBufferDeserializer(Class<ByteBuffer> valueType) {
			super(valueType);
		}

		@Override
		public ByteBuffer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			try {
				return ByteBuffer.wrap(DatatypeConverter.parseBase64Binary(p.getText()));
			} catch (IllegalArgumentException ex) {
				throw new IOException(ex);
			}
		}

	}

	public static final class DateSerializer extends StdSerializer<Date> {

		public DateSerializer() {
			this(null);
		}

		public DateSerializer(Class<Date> valueType) {
			super(valueType);
		}

		@Override
		public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			gen.writeNumber(value.getTime());
		}
	}

	public static final class DateDeserializer extends StdDeserializer<Date> {

		public DateDeserializer() {
			this(null);
		}

		public DateDeserializer(Class<Date> valueType) {
			super(valueType);
		}

		@Override
		public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			return new Date(p.getLongValue());
		}

	}

	public static final class BigIntegerSerializer extends StdSerializer<BigInteger> {

		public BigIntegerSerializer() {
			this(null);
		}

		public BigIntegerSerializer(Class<BigInteger> valueType) {
			super(valueType);
		}

		@Override
		public void serialize(BigInteger value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			gen.writeNumber(value.toString());
		}
	}

	public static final class BigIntegerDeserializer extends StdDeserializer<BigInteger> {

		public BigIntegerDeserializer() {
			this(null);
		}

		public BigIntegerDeserializer(Class<BigInteger> valueType) {
			super(valueType);
		}

		@Override
		public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			return new BigInteger(p.getText());
		}

	}

	public static final class BigDecimalSerializer extends StdSerializer<BigDecimal> {

		public BigDecimalSerializer() {
			this(null);
		}

		public BigDecimalSerializer(Class<BigDecimal> valueType) {
			super(valueType);
		}

		@Override
		public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			gen.writeNumber(value.toString());
		}
	}

	public static final class BigDecimalDeserializer extends StdDeserializer<BigDecimal> {

		public BigDecimalDeserializer() {
			this(null);
		}

		public BigDecimalDeserializer(Class<BigDecimal> valueType) {
			super(valueType);
		}

		@Override
		public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			return new BigDecimal(p.getText());
		}

	}

	public static class JsonParseException extends IOException {

		public JsonParseException(String msg, Throwable rootCause) {
			super(msg, rootCause);
		}

		public JsonParseException(String msg) {
			super(msg);
		}

		public JsonParseException(Throwable rootCause) {
			this(null, rootCause);
		}

	}

	public static class JsonValidationException extends IOException {

		protected JsonValidationException(String msg, Throwable rootCause) {
			super(msg, rootCause);
		}

		protected JsonValidationException(String msg) {
			super(msg);
		}

		protected JsonValidationException(Throwable rootCause) {
			this(null, rootCause);
		}

	}

}
