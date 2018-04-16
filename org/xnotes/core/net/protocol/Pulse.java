/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.net.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.xnotes.core.db.ChainedRecord;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JsonSchema;
import org.xnotes.core.utils.JsonSchema.Required;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
@JsonPropertyOrder({"id", "time", "data", "candidate", "signatures"})
public class Pulse extends JSON.JsonObject implements ChainedRecord {

	@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
	public String id;
	@JsonSerialize(using = JSON.DateSerializer.class)
	@JsonDeserialize(using = JSON.DateDeserializer.class)
	public Date time;
	public final Data data;
	public final Candidate candidate;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public final List<SignatureObject> signatures;

	public Pulse() {
		this(null, null, null, null, null);
	}

	@JsonCreator
	public Pulse(
			@JsonProperty("id") @Required String id,
			@JsonProperty("time") @Required Date time,
			@JsonProperty("data") @Required Data data,
			@JsonProperty("candidate") @Required Candidate candidate,
			@JsonProperty("signatures") @Required List<SignatureObject> signatures
	) {
		this.id = id;
		this.time = time;
		this.data = data != null ? data : new Data();
		this.candidate = candidate != null ? candidate : new Candidate();
		this.signatures = signatures != null ? signatures : new ArrayList<>();
	}

	@Override
	@JsonIgnore
	public String getId() {
		return id;
	}

	@Override
	@JsonIgnore
	public ChainedRecord.Data getData() {
		return data;
	}

	@Override
	@JsonIgnore
	public Object getObject() {
		return candidate;
	}

	@Override
	@JsonIgnore
	public List<SignatureObject> getSignatures() {
		return signatures;
	}

	@JsonPropertyOrder({"previousId", "order", "objectHash"})
	public static class Data extends JSON.JsonObject implements ChainedRecord.Data {

		@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
		public String previousId;
		public int order;
		public byte[] objectHash;

		public Data() {
			this(null, 0, null);
		}

		@JsonCreator
		public Data(
				@JsonProperty("previousId") @Required String previousId,
				@JsonProperty("order") @Required int order,
				@JsonProperty("objectHash") @Required byte[] objectHash
		) {
			this.previousId = previousId;
			this.order = order;
			this.objectHash = objectHash;
		}

		@Override
		@JsonIgnore
		public String getPreviousId() {
			return previousId;
		}

		@Override
		@JsonIgnore
		public String getPulseId() {
			return null;
		}

		@Override
		@JsonIgnore
		public int getOrder() {
			return order;
		}

		@Override
		@JsonIgnore
		public byte[] getObjectHash() {
			return objectHash;
		}

	}
}
