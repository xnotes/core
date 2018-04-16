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
import org.xnotes.core.utils.JsonSchema.Required;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
@JsonPropertyOrder({"token", "params"})
public class Candidate extends JSON.JsonObject {

	public byte[] token;
	public final Parameters params;

	public Candidate() {
		this(null, null);
	}

	@JsonCreator
	public Candidate(
			@JsonProperty("token") @Required byte[] token,
			@JsonProperty("params") @Required Parameters params
	) {
		this.token = token;
		this.params = params != null ? params : new Parameters();
	}

	@JsonPropertyOrder({"nonce", "salt", "cost", "blockSize", "parallelization"})
	public static class Parameters extends JSON.JsonObject {

		public byte[] nonce;
		public byte[] salt;
		public int cost;
		public int blockSize;
		public int parallelization;

		public Parameters() {
			this(null, null, 0, 0, 0);
		}

		@JsonCreator
		public Parameters(
				@JsonProperty("nonce") @Required byte[] nonce,
				@JsonProperty("salt") @Required byte[] salt,
				@JsonProperty("cost") @Required int cost,
				@JsonProperty("blockSize") @Required int blockSize,
				@JsonProperty("parallelization") @Required int parallelization
		) {
			this.nonce = nonce;
			this.salt = salt;
			this.cost = cost;
			this.blockSize = blockSize;
			this.parallelization = parallelization;
		}

	}
}
