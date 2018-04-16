/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.db;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.xnotes.XNotes;
import org.xnotes.core.utils.JSON;
import org.xnotes.core.utils.JSON.JsonParseException;
import org.xnotes.core.utils.JSON.JsonValidationException;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNotesManager {

	private final WeakReference<XNotes> _xnotesRef;
	private final Map<String, Map<String, XNotesSchema>> _cachedSchemas = new HashMap<>();

	public XNotesManager(XNotes xnotes) {
		_xnotesRef = new WeakReference<>(xnotes);
	}

	public final XNotes xnotes() {
		return _xnotesRef.get();
	}

	public XNotesSchema newSchema(String schemaDescriptor, boolean createIfNotExists) throws JsonParseException, JsonValidationException {
		XNotesSchema schema = JSON.parse(schemaDescriptor, XNotesSchema.class);
		return schema;
	}

	public XNotesSchema getSchema(String schema) throws IOException {
		return this.getSchema(schema, null);
	}

	public XNotesSchema getSchema(String schema, String version) throws IOException {
		if (!_cachedSchemas.containsKey(schema)) {
			XNotesSchema os = JSON.parse(new URL(schema), XNotesSchema.class);
			if (os != null) {
				if (version != null && !version.equals(os.version)) {
					return null;
				}
				Map<String, XNotesSchema> schemaVersions = new HashMap<>();
				schemaVersions.put(os.version, os);
				schemaVersions.put(null, os);
				_cachedSchemas.put(schema, schemaVersions);
			} else {
				throw new IOException();
			}
		} else if (version != null) {
			Map<String, XNotesSchema> schemaVersions = _cachedSchemas.get(schema);
			if (!schemaVersions.containsKey(version)) {
				XNotesSchema os = JSON.parse(new URL(schema), XNotesSchema.class);
				if (os != null) {
					if (!version.equals(os.version)) {
						return null;
					} else {
						schemaVersions.put(os.version, os);
						if (!schemaVersions.containsKey(null) || schemaVersions.get(null).version.compareTo(os.version) < 0) {
							schemaVersions.put(null, os);
						}
					}
				} else {
					throw new IOException();
				}
			}
		}
		return _cachedSchemas.get(schema).get(version);
	}

}
