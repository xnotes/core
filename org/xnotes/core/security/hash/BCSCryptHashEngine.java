/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.hash;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.generators.SCrypt;
import org.xnotes.ConfigurationException;
import org.xnotes.core.security.SecurityToolSet;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class BCSCryptHashEngine implements HashEngine {

	private int _size;
	private int _saltSize;
	private int _cost;
	private int _blockSize;
	private int _parallelization;

	@Override
	public void setProvider(String provider) {
	}

	@Override
	public String getProvider() {
		return "BC";
	}

	@Override
	public void setAlgorithm(String provider) {
	}

	@Override
	public String getAlgorithm() {
		return "SCrypt";
	}

	@Override
	public int getDigestLength() {
		return _size;
	}

	@Override
	public Map<String, Object> getParameters() {
		Map<String, Object> params = new HashMap<>();
		params.put("size", _size);
		params.put("saltSize", _saltSize);
		params.put("cost", _cost);
		params.put("blockSize", _blockSize);
		params.put("parallelization", _parallelization);
		return params;
	}

	@Override
	public Object getParameter(String param) {
		if (param != null) {
			switch (param) {
				case "size":
					return _size;
				case "saltSize":
					return _saltSize;
				case "cost":
					return _cost;
				case "blockSize":
					return _blockSize;
				case "parallelization":
					return _parallelization;
				default:
					return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public void setParameters(Map<String, Object> parameters) throws ConfigurationException {
		try {
			if (parameters.containsKey("size")) {
				_size = (int) parameters.get("size");
			}
			if (parameters.containsKey("saltSize")) {
				_saltSize = (int) parameters.get("saltSize");
			}
			if (parameters.containsKey("cost")) {
				_cost = (int) parameters.get("cost");
			}
			if (parameters.containsKey("blockSize")) {
				_blockSize = (int) parameters.get("blockSize");
			}
			if (parameters.containsKey("parallelization")) {
				_parallelization = (int) parameters.get("parallelization");
			}
		} catch (Throwable ex) {
			throw new ConfigurationException(ex);
		}
	}

	@Override
	public byte[] hash(byte[] bytesToBeHashed, int iterations, byte[] salt) {
		if (salt == null || salt.length == 0) {
			salt = new byte[_saltSize];
			SecurityToolSet.getDefaultSecureRandom().nextBytes(salt);
		}
		byte[] h = bytesToBeHashed;
		for (int i = 0; i < (iterations > 0 ? iterations : 0); i++) {
			h = SCrypt.generate(h, salt, _cost, _blockSize, _parallelization, _size);
		}
		return h;
	}
}
