/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.xnotes.core.net.protocol.SignatureObject;
import java.util.List;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 * @param <T>
 */
public interface ChainedRecord<T> {

	@JsonIgnore
	public String getId();

	@JsonIgnore
	public Data getData();

	@JsonIgnore
	public T getObject();

	@JsonIgnore
	public List<SignatureObject> getSignatures();

	public interface Data {

		@JsonIgnore
		public String getPreviousId();

		@JsonIgnore
		public String getPulseId();

		@JsonIgnore
		public int getOrder();

		@JsonIgnore
		public byte[] getObjectHash();

	}

}
