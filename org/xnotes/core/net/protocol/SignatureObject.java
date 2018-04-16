/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.net.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JsonSchema;
import org.xnotes.core.utils.JsonSchema.Required;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
@JsonPropertyOrder({"signer", "signInfo", "data"})
public final class SignatureObject extends JSON.JsonObject {

	@JsonSchema.Ref(JsonSchema.Schema.XNOTES_CORE_SCHEMA_LIB_URL + "#/definitions/base58")
	public String signer;
	public final SignatureAlgorithmInfo signInfo;
	public byte[] data;

	public SignatureObject() {
		this(null, null, null);
	}

	@JsonCreator
	public SignatureObject(
			@JsonProperty("signer") @Required String signer,
			@JsonProperty("signInfo") @Required SignatureAlgorithmInfo signInfo,
			@JsonProperty("data") @Required byte[] data
	) {
		this.signer = signer;
		this.signInfo = signInfo != null ? signInfo : new SignatureAlgorithmInfo();
		this.data = data;
	}

	@JsonPropertyOrder({"key", "algorithm"})
	public static final class SignatureAlgorithmInfo extends JSON.JsonObject {

		public final AlgorithmInfo key;
		public String algorithm;

		public SignatureAlgorithmInfo() {
			this(null, null);
		}

		@JsonCreator
		public SignatureAlgorithmInfo(
				@JsonProperty("key") @Required AlgorithmInfo key,
				@JsonProperty("algorithm") @Required String algorithm
		) {
			this.key = key != null ? key : new AlgorithmInfo();
			this.algorithm = algorithm;
		}

	}

}
