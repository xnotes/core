/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.net.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.LinkedHashMap;
import java.util.Map;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JsonSchema.Required;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
@JsonPropertyOrder({"algorithm", "params"})
public class AlgorithmInfo extends JSON.JsonObject {

	public String algorithm;
	public final Map<String, Object> params;

	public AlgorithmInfo() {
		this(null, null);
	}

	@JsonCreator
	public AlgorithmInfo(
			@JsonProperty("algorithm") @Required String algorithm,
			@JsonProperty("params") Map<String, Object> params
	) {
		this.algorithm = algorithm;
		this.params = params != null ? params : new LinkedHashMap<>();
	}
}
