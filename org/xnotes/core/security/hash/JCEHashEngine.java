/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;
import org.xnotes.ConfigurationException;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class JCEHashEngine implements HashEngine {

	private String _algorithm;
	private String _provider;

	@Override
	public void setProvider(String provider) {
		_provider = provider;
	}

	@Override
	public String getProvider() {
		return _provider;
	}

	@Override
	public void setAlgorithm(String algorithm) {
		_algorithm = algorithm;
	}

	@Override
	public String getAlgorithm() {
		return _algorithm;
	}

	@Override
	public int getDigestLength() {
		try {
			return (_provider != null ? MessageDigest.getInstance(_algorithm, _provider) : MessageDigest.getInstance(_algorithm)).getDigestLength();
		} catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
			throw new ConfigurationException(ex.getMessage(), ex);
		}
	}

	@Override
	public Map<String, Object> getParameters() {
		Map<String, Object> params = new HashMap<>();
		params.put("algorithm", _algorithm);
		params.put("provider", _provider);
		return params;
	}

	@Override
	public final void setParameters(Map<String, Object> parameters) throws ConfigurationException {
		try {
			if (parameters.containsKey("algorithm")) {
				_algorithm = (String) parameters.get("algorithm");
			}
			if (parameters.containsKey("provider")) {
				_provider = (String) parameters.get("provider");
			}
		} catch (Throwable ex) {
			throw new ConfigurationException(ex);
		}
	}

	@Override
	public Object getParameter(String param) {
		if (param != null) {
			switch (param) {
				case "algorithm":
					return _algorithm;
				case "provider":
					return _provider;
				default:
					return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public byte[] hash(byte[] bytesToBeHashed, int iterations, byte[] salt) {
		try {
			MessageDigest md = _provider != null ? MessageDigest.getInstance(_algorithm, _provider) : MessageDigest.getInstance(_algorithm);
			byte[] h = bytesToBeHashed;
			for (int i = 0; i < (iterations > 0 ? iterations : 0); i++) {
				h = md.digest(h);
			}
			return h;
		} catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
			throw new ConfigurationException(ex.getMessage(), ex);
		}
	}

}
