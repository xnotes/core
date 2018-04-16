/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.db;

import org.xnotes.core.net.protocol.SignatureObject;
import java.util.ArrayList;
import java.util.List;
import org.xnotes.core.utils.JSON;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class Block extends JSON.JsonObject {

	public String id;
	public Data data;
	public SignatureObject signature;

	public class Data {

		public String previousId;
		public String pulse;
		public byte[] merkleRoot;
		public List<ChainedRecord> records = new ArrayList<>();
	}
}
