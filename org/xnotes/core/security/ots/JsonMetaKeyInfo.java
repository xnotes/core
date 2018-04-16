/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.util.Arrays;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JsonSchema.Required;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
@JsonPropertyOrder({"keyCount", "usedKeys", "currentKey", "subKeyRef", "params"})
public class JsonMetaKeyInfo extends JSON.JsonObject {

	@JsonInclude(JsonInclude.Include.ALWAYS)
	public final int keyCount;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public final List<JsonOTSUsedKeyInfo> usedKeys;
	@JsonInclude(JsonInclude.Include.ALWAYS)
	public int currentKey;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public String childKeyRef;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public final Map<String, Object> params = new HashMap<>();

	public JsonMetaKeyInfo(int keyCount) {
		this(keyCount, null, -1, null);
	}

	@JsonCreator
	public JsonMetaKeyInfo(
			@JsonProperty("keyCount") @Required int keyCount,
			@JsonProperty("usedKeys") List<JsonOTSUsedKeyInfo> usedKeys,
			@JsonProperty("currentKey") @Required int currentKey,
			@JsonProperty("subKeyReference") String subKeyReference) {
		this.keyCount = keyCount;
		if (usedKeys != null) {
			this.usedKeys = usedKeys;
		} else {
			this.usedKeys = new ArrayList<>();
		}
		this.currentKey = currentKey;
		this.childKeyRef = subKeyReference;
	}

	public JsonMetaKeyInfo(JsonMetaKeyInfo info) {
		this.keyCount = info.keyCount;
		if (info.usedKeys != null) {
			this.usedKeys = new ArrayList<>();
			info.usedKeys.forEach((uki) -> {
				this.usedKeys.add(new JsonOTSUsedKeyInfo(uki));
			});
		} else {
			this.usedKeys = new ArrayList<>();
		}
		this.currentKey = info.currentKey;
		this.childKeyRef = info.childKeyRef;
		this.setFilePath(info.getFilePath());
	}

	@JsonPropertyOrder({"index", "time", "hash"})
	public static class JsonOTSUsedKeyInfo extends JSON.JsonObject implements OTSUsedKeyInfo {

		@JsonInclude(JsonInclude.Include.ALWAYS)
		public final int index;
		@JsonInclude(JsonInclude.Include.ALWAYS)
		public final long time;
		@JsonInclude(JsonInclude.Include.ALWAYS)
		public final byte[] hash;

		@JsonCreator
		public JsonOTSUsedKeyInfo(
				@JsonProperty("index") @Required int index,
				@JsonProperty("time") @Required long time,
				@JsonProperty("hash") @Required byte[] hash) {
			this.index = index;
			this.time = time;
			this.hash = hash;
		}

		public JsonOTSUsedKeyInfo(OTSUsedKeyInfo info) {
			this.index = info.getIndex();
			this.time = info.getTime().getTime();
			this.hash = Arrays.clone(info.getHash());
		}

		@Override
		@JsonIgnore
		public int getIndex() {
			return index;
		}

		@Override
		@JsonIgnore
		public Date getTime() {
			return new Date(time);
		}

		@Override
		@JsonIgnore
		public byte[] getHash() {
			return hash;
		}
	}

}
