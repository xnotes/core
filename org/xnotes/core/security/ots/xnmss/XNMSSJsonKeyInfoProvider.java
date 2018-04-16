/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.security.ots.xnmss;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.xnotes.core.security.ots.JsonMetaKeyInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.bouncycastle.util.Arrays;
import org.xnotes.core.security.ots.KeyManagerException;
import org.xnotes.core.security.ots.MetaKey;
import org.xnotes.core.security.ots.JsonMetaKeyInfo.JsonOTSUsedKeyInfo;
import org.xnotes.core.utils.FileUtil;
import org.xnotes.core.utils.JSON;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class XNMSSJsonKeyInfoProvider implements XNMSSKeyInfoProvider {

	public static final String EXT_PUBLIC = "pub";
	public static final String EXT_PRIVATE = "priv";

	private final String _dbPath;
	private final String _ext;
	private final LoadingCache<String, JsonMetaKeyInfo> _cache = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterAccess(1, TimeUnit.MINUTES)
			.build(new CacheLoader<String, JsonMetaKeyInfo>() {
				@Override
				public JsonMetaKeyInfo load(String metaKeyReference) throws KeyManagerException {
					try {
						JsonMetaKeyInfo info = JSON.parseFile(_dbPath + metaKeyReference + (_ext != null ? "." + _ext : ""), JsonMetaKeyInfo.class, null, true);
						if (info == null) {
							throw new KeyManagerException("MetaKey referenced '" + metaKeyReference + "' is NOT managed.");
						}
						return info;
					} catch (IOException ex) {
						throw new KeyManagerException(ex);
					}
				}
			});
	private final Map<String, JsonMetaKeyInfo> _temp = new ConcurrentHashMap<>();

	public XNMSSJsonKeyInfoProvider(String dbPath, String extension) {
		_dbPath = FileUtil.getActualPath(dbPath);
		_ext = extension;
	}

	protected JsonMetaKeyInfo getKeyInfo(String metaKeyReference) {
		try {
			if (_temp.containsKey(metaKeyReference)) {
				JsonMetaKeyInfo info = _temp.get(metaKeyReference);
				if (info == null) {
					throw new KeyManagerException("MetaKey referenced '" + metaKeyReference + "' is NOT managed.");
				}
				return info;
			}
			return _cache.get(metaKeyReference);
		} catch (ExecutionException ex) {
			throw new KeyManagerException(ex);
		}
	}

	@Override
	public boolean beginBatch(String metaKeyReference) {
		if (!_temp.containsKey(metaKeyReference)) {
			try {
				_temp.put(metaKeyReference, new JsonMetaKeyInfo(_cache.get(metaKeyReference)));
				return true;
			} catch (ExecutionException ex) {
				throw new KeyManagerException(ex);
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean endBatch(String metaKeyReference) throws KeyManagerException {
		if (_temp.containsKey(metaKeyReference)) {
			try {
				JsonMetaKeyInfo info = _temp.get(metaKeyReference);
				info.save();
				_cache.put(metaKeyReference, info);
				_temp.remove(metaKeyReference);
				return true;
			} catch (IOException ex) {
				throw new KeyManagerException(ex);
			}
		} else {
			return false;
		}
	}

	@Override
	public void cancelBatch(String metaKeyReference) throws KeyManagerException {
		if (_temp.containsKey(metaKeyReference)) {
			_temp.remove(metaKeyReference);
		}
	}

	@Override
	public boolean isManaged(String metaKeyReference) {
		try {
			return (this.getKeyInfo(metaKeyReference) != null);
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public void manage(MetaKey metaKey) throws KeyManagerException {
		if (!this.isManaged(metaKey.getReference())) {
			JsonMetaKeyInfo info;
			try {
				info = JSON.parseFile(_dbPath + metaKey.getReference() + (_ext != null ? "." + _ext : ""), JsonMetaKeyInfo.class, new JsonMetaKeyInfo(metaKey.getOTSKeyCount()), true);
			} catch (IOException ex) {
				throw new KeyManagerException(ex);
			}
			if (_temp.containsKey(metaKey.getReference())) {
				_temp.put(metaKey.getReference(), info);
			} else {
				try {
					info.save();
					_cache.put(metaKey.getReference(), info);
				} catch (IOException ex) {
					throw new KeyManagerException(ex);
				}
			}
		}
	}

	@Override
	public void unmanage(String metaKeyReference) throws KeyManagerException {
		if (this.isManaged(metaKeyReference)) {
			try {
				Files.delete(Paths.get(_dbPath + metaKeyReference + (_ext != null ? "." + _ext : "")));
				if (_temp.containsKey(metaKeyReference)) {
					_temp.remove(metaKeyReference);
				}
				_cache.invalidate(metaKeyReference);
			} catch (IOException ex) {
				throw new KeyManagerException(ex);
			}
		}
	}

	@Override
	public int getOTSKeyCount(String metaKeyReference) throws KeyManagerException {
		return this.getKeyInfo(metaKeyReference).keyCount;
	}

	@Override
	public int getCurrentOTSKeyIndex(String metaKeyReference) throws KeyManagerException {
		return this.getKeyInfo(metaKeyReference).currentKey;
	}

	@Override
	public void setCurrentOTSKeyIndex(String metaKeyReference, int index) throws KeyManagerException {
		JsonMetaKeyInfo info = this.getKeyInfo(metaKeyReference);
		info.currentKey = index;
		if (!_temp.containsKey(metaKeyReference)) {
			try {
				info.save();
			} catch (IOException ex) {
				throw new KeyManagerException(ex);
			}
		}
	}

	@Override
	public boolean isUsed(String metaKeyReference, int index) throws KeyManagerException {
		if (index < 0 || index >= this.getKeyInfo(metaKeyReference).keyCount) {
			throw new KeyManagerException("Index " + index + " is out of range: index must be between 0 and " + this.getKeyInfo(metaKeyReference).keyCount + ".");
		}
		return index < this.getKeyInfo(metaKeyReference).usedKeys.size();
	}

	@Override
	public void markUsed(String metaKeyReference, int index, Date time, byte[] dataHash) throws KeyManagerException {
		JsonMetaKeyInfo info = this.getKeyInfo(metaKeyReference);
		if (index > info.usedKeys.size() && _ext != null && _ext.equals(XNMSSJsonKeyInfoProvider.EXT_PRIVATE)) {
			throw new KeyManagerException("Illegal mark-used operation: XNMSS OTS Private Keys only support sequential used-marking.");
		} else if (index < -1) {
			throw new KeyManagerException("Illegal mark-used operation: cannot mark with negative index.");
		} else if (index < info.usedKeys.size() && info.usedKeys.get(index) != null && dataHash != null && !Arrays.areEqual(info.usedKeys.get(index).getHash(), dataHash)) {
			throw new KeyManagerException("XNMSS OTS Key #" + index + " for XNMSS Key reference '" + metaKeyReference + "' has already been marked with a different data hash.");
		}
		info.usedKeys.add(new JsonOTSUsedKeyInfo(index, time.getTime(), dataHash));
		if (!_temp.containsKey(metaKeyReference)) {
			try {
				info.save();
			} catch (IOException ex) {
				throw new KeyManagerException(ex);
			}
		}
	}

	@Override
	public List<JsonOTSUsedKeyInfo> getUsedOTSKeyInfos(String metaKeyReference) throws KeyManagerException {
		return this.getKeyInfo(metaKeyReference).usedKeys;
	}

	@Override
	public String getChildMetaKeyReference(String metaKeyReference) throws KeyManagerException {
		return this.getKeyInfo(metaKeyReference).childKeyRef;
	}

	@Override
	public void setChildMetaKeyReference(String metaKeyReference, String childMetaKeyReference) throws KeyManagerException {
		JsonMetaKeyInfo info = this.getKeyInfo(metaKeyReference);
		info.childKeyRef = childMetaKeyReference;
		if (!_temp.containsKey(metaKeyReference)) {
			try {
				info.save();
			} catch (IOException ex) {
				throw new KeyManagerException(ex);
			}
		}
	}

}
