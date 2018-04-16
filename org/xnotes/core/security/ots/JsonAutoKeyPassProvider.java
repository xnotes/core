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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.xnotes.core.security.SecurityToolSet;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JsonSchema.Required;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class JsonAutoKeyPassProvider implements KeyPassProvider {

	private final SecurityToolSet _sts;
	private final String _path;
	private final LoadingCache<String, JsonKeyPass> _cache = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new CacheLoader<String, JsonKeyPass>() {
				@Override
				public JsonKeyPass load(String metaKeyReference) throws KeyManagerException {
					try {
						JsonKeyPass kp = JSON.parseFile(_path + metaKeyReference, JsonKeyPass.class, new JsonKeyPass(metaKeyReference, _sts), true);
						kp.setSecurityToolSet(_sts);
						return kp;
					} catch (IOException ex) {
						throw new KeyManagerException(ex);
					}
				}
			});

	public JsonAutoKeyPassProvider(SecurityToolSet sts, String path) {
		_sts = sts;
		_path = path;
	}

	private JsonKeyPass _getJsonKeyPass(String metaKeyReference) throws KeyManagerException {
		try {
			return _cache.get(metaKeyReference);
		} catch (KeyManagerException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new KeyManagerException(ex);
		}
	}

	@Override
	public String getMetaKeyPass(String metaKeyReference) throws KeyManagerException {
		return _getJsonKeyPass(metaKeyReference).keyPass;
	}

	@Override
	public String getOTSKeyPass(String metaKeyReference, int index) throws KeyManagerException {
		return index > -1 ? _getJsonKeyPass(metaKeyReference).get(index) : null;
	}

	@JsonPropertyOrder({"metaKeyRef", "keyPass", "otsKeyPass"})
	public static class JsonKeyPass extends JSON.JsonObject {

		@JsonInclude(JsonInclude.Include.ALWAYS)
		public final String metaKeyRef;
		@JsonInclude(JsonInclude.Include.ALWAYS)
		public String keyPass;
		@JsonInclude(JsonInclude.Include.NON_EMPTY)
		public final Map<String, String> otsKeyPass = new LinkedHashMap<>();

		private SecurityToolSet _sts;

		public JsonKeyPass(String metaKeyRef, SecurityToolSet sts) {
			this.metaKeyRef = metaKeyRef;
			_sts = sts;
			this.keyPass = _sts.randomPassword();
		}

		@JsonCreator
		public JsonKeyPass(
				@JsonProperty("metaKeyRef") @Required String metaKeyRef,
				@JsonProperty("keyPass") @Required String keyPass,
				@JsonProperty("otsKeyPass") Map<String, String> otsKeyPass) {
			this.metaKeyRef = metaKeyRef;
			this.keyPass = keyPass;
			if (otsKeyPass != null) {
				this.otsKeyPass.putAll(otsKeyPass);
			}
		}

		public JsonKeyPass(SecurityToolSet sts, String metaKeyRef, String keyPass) {
			_sts = sts;
			this.metaKeyRef = metaKeyRef;
			this.keyPass = keyPass != null ? keyPass : _sts.randomPassword();
		}

		public void setSecurityToolSet(SecurityToolSet sts) {
			_sts = sts;
		}

		@JsonIgnore
		public String get(int index) {
			if (!this.otsKeyPass.containsKey("" + index)) {
				this.otsKeyPass.put("" + index, _sts.randomPassword());
				this.toString();
				try {
					this.save();
				} catch (IOException ex) {
				}
			}
			return this.otsKeyPass.get("" + index);
		}

	}

}
