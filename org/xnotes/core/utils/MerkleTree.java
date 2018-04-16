/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xnotes.core.utils;

import org.bouncycastle.util.Arrays;
import org.xnotes.core.security.hash.HashEngine;

/**
 *
 * @author XNotes Alliance Limited dev@xnotes.com
 */
public class MerkleTree {

	private final HashEngine _hashEngine;
	private final int _digestIterations;
	private final byte[][][] _treeHashes;

	public MerkleTree(int height, HashEngine hashEngine, int digestIterations) {
		_hashEngine = hashEngine;
		_digestIterations = digestIterations;
		_treeHashes = new byte[height + 1][][];
		reset();
	}

	public final void reset() {
		for (int i = 0; i < _treeHashes.length; i++) {
			_treeHashes[i] = new byte[_layerSize(i)][];
		}
	}

	protected HashEngine getHashEngine() {
		return _hashEngine;
	}

	public int getDigestLength() {
		return _hashEngine.getDigestLength();
	}

	public int getHeight() {
		return _treeHashes.length - 1;
	}

	public byte[][][] getTreeHashes() {
		return _treeHashes;
	}

	public byte[] getRoot() {
		return _treeHashes[0][0];
	}

	public byte[] getHash(int layer, int index) {
		return _treeHashes[layer][index];
	}

	public byte[][] getLeaves() {
		return _treeHashes[_treeHashes.length - 1];
	}

	public byte[] getLeaf(int index) {
		return _treeHashes[_treeHashes.length - 1][index];
	}

	public int getLeafCount() {
		return _treeHashes[_treeHashes.length - 1].length;
	}

	public byte[] computeWithLeafHashes(byte[][] hashes) {
		int layer = _treeHashes.length - 1;
		int layerSize = _layerSize(layer);
		for (int i = 0; i < layerSize; i++) {
			if (i < layerSize) {
				_treeHashes[layer][i] = hashes[i];
			} else {
				_treeHashes[layer][i] = new byte[this.getDigestLength()];
			}
		}
		for (int i = _treeHashes.length - 2; i >= 0; i--) {
			for (int j = 0; j < _layerSize(i); j++) {
				_treeHashes[i][j] = _hashEngine.hash(Arrays.concatenate(_treeHashes[i + 1][j * 2], _treeHashes[i + 1][j * 2 + 1]), _digestIterations, null);
			}
		}
		return _treeHashes[0][0];
	}

	public byte[] computeWithLeafHashAndPath(int index, byte[] hash, Hash[] path) throws IllegalArgumentException {
		if (path != null) {
			if (index < this.getLeafCount()) {
				if (hash != null && hash.length == _hashEngine.getDigestLength()) {
					_treeHashes[_treeHashes.length - 1][index] = hash;
					for (int layer = _treeHashes.length - 1; layer > 0; layer--) {
						int idx;
						if (index % 2 == 0) {
							idx = index + 1;
						} else {
							idx = index - 1;
						}
						Hash h = _getHash(layer, idx, path);
						if (h != null) {
							if (h.hash != null && h.hash.length == _hashEngine.getDigestLength()) {
								_treeHashes[layer][idx] = h.hash;
								idx = Math.min(index, idx);
								index = (int) Math.floor(index / 2);
								_treeHashes[layer - 1][index] = _hashEngine.hash(Arrays.concatenate(_treeHashes[layer][idx], _treeHashes[layer][idx + 1]), _digestIterations, null);
							} else {
								throw new IllegalArgumentException("Invalid hash size at {layer:" + layer + ",index:" + idx + "}");
							}
						} else {
							throw new IllegalArgumentException("Required hash missing in path at {layer:" + layer + ",index:" + idx + "}");
						}
					}
					return _treeHashes[0][0];
				} else {
					throw new IllegalArgumentException("Leaf value cannot be null.");
				}
			} else {
				throw new IllegalArgumentException("Index " + index + " is out of range.");
			}
		} else {
			throw new IllegalArgumentException("Cannot generate Tree Hash with null path.");
		}
	}

	private static Hash _getHash(int layer, int index, Hash[] path) {
		for (Hash h : path) {
			if (h.layer == layer && h.index == index) {
				return h;
			}
		}
		return null;
	}

	private int _layerSize(int layer) {
		return layer >= 0 && layer < _treeHashes.length ? (int) Math.pow(2, layer) : 0;
	}

	public Hash[] getPathForLeaf(int index) {
		Hash[] hashPath = new Hash[_treeHashes.length - 1];
		int pathIdx = 0;
		for (int layer = _treeHashes.length - 1; layer > 0; layer--) {
			int idx;
			if (index % 2 == 0) {
				idx = index + 1;
			} else {
				idx = index - 1;
			}
			hashPath[pathIdx] = new Hash(layer, idx, _treeHashes[layer][idx]);
			pathIdx++;
			index = (int) Math.floor(index / 2);
		}
		return hashPath;
	}

	public static class Hash {

		public final int layer;
		public final int index;
		public final byte[] hash;

		public Hash(int layer, int index, byte[] hash) {
			this.layer = layer;
			this.index = index;
			this.hash = hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Hash other = (Hash) obj;
			return (this.layer == other.layer
					|| this.index != other.index
					|| java.util.Arrays.equals(this.hash, other.hash));
		}

		@Override
		public int hashCode() {
			int h = 3;
			h = 47 * h + this.layer;
			h = 47 * h + this.index;
			h = 47 * h + java.util.Arrays.hashCode(this.hash);
			return h;
		}

	}

}
