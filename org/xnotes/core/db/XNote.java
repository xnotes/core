/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.xnotes.core.db.XNote.XNoteObject;
import org.xnotes.core.net.protocol.SignatureObject;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JsonSchema;
import org.xnotes.core.utils.JsonSchema.Required;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <T>
 */
public class XNote<T> extends JSON.JsonObject implements ChainedRecord<XNoteObject<T>> {

	@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
	public String id;
	public final XNoteData data;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public final XNoteObject<T> object;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public final List<SignatureObject> signatures;

	public XNote() {
		this(null, null, null, null);
	}

	@JsonCreator
	public XNote(
			@JsonProperty("id") @Required String id,
			@JsonProperty("data") @Required XNoteData data,
			@JsonProperty("object") @Required XNoteObject<T> object,
			@JsonProperty("signatures") @Required List<SignatureObject> signatures
	) {
		this.id = id;
		this.data = data != null ? data : new XNoteData();
		this.object = object != null ? object : new XNoteObject();
		this.signatures = signatures != null ? signatures : new ArrayList<>();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ChainedRecord.Data getData() {
		return data;
	}

	@Override
	public XNoteObject<T> getObject() {
		return object;
	}

	@Override
	public List<SignatureObject> getSignatures() {
		return signatures;
	}

	public static final class XNoteData extends JSON.JsonObject implements ChainedRecord.Data {

		@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String previousId;
		@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String pulseId;
		public int order;
		public byte[] objectHash;

		public XNoteData() {
			this(null, null, 0, null);
		}

		@JsonCreator
		public XNoteData(
				@JsonProperty("previousId") @Required String previousId,
				@JsonProperty("pulseId") @Required String pulseId,
				@JsonProperty("order") @Required int order,
				@JsonProperty("objectHash") @Required byte[] objectHash
		) {
			this.previousId = previousId;
			this.pulseId = pulseId;
			this.order = order;
			this.objectHash = objectHash;
		}

		@Override
		public String getPreviousId() {
			return previousId;
		}

		@Override
		public String getPulseId() {
			return pulseId;
		}

		@Override
		public int getOrder() {
			return order;
		}

		@Override
		public byte[] getObjectHash() {
			return objectHash;
		}

	}

	public static final class XNoteObject<T> extends JSON.JsonObject {

		public String schema;
		public String version;
		public T object;

		public XNoteObject() {
			this(null, null, null);
		}

		@JsonCreator
		public XNoteObject(
				@JsonProperty("schema") @Required String schema,
				@JsonProperty(value = "version", defaultValue = "1.0") @Required String version,
				@JsonProperty("entity") @Required T object
		) {
			this.schema = schema;
			this.version = version != null ? version : "1.0";
			this.object = object;
		}

	}

}
