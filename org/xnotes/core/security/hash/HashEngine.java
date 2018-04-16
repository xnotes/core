/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
package org.xnotes.core.security.hash;

import java.util.Map;
import org.xnotes.ConfigurationException;

public interface HashEngine {

	public void setProvider(String provider);

	public String getProvider();

	public void setAlgorithm(String algorithm);

	public String getAlgorithm();

	public int getDigestLength();

	public Map<String, Object> getParameters();

	public Object getParameter(String param);

	public void setParameters(Map<String, Object> parameters) throws ConfigurationException;

	public byte[] hash(byte[] bytesToBeHashed, int iterations, byte[] salt);

}
