package org.xnotes.core.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ConcurrentCacheMap<K, V> extends ConcurrentHashMap<K, V> {

	private final Map<K, Date> _lastAccess = new HashMap<>();
	private int _timeToLive;
	private TimeUnit _timeUnit;
	private int _maxSize;

	public ConcurrentCacheMap() {
		super();
	}

	public ConcurrentCacheMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ConcurrentCacheMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	public ConcurrentCacheMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public ConcurrentCacheMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
		super(initialCapacity, loadFactor, concurrencyLevel);
	}

	public void setTimeToLive(int amount, TimeUnit unit) {
		_timeToLive = amount;
		_timeUnit = unit;
	}

	public void setMaxSize(int maxSize) {
		_maxSize = maxSize;
	}

	@Override
	public V get(Object key) {
		V v = super.get(key);
		_lastAccess.put((K) key, new Date());
		return v;
	}

	@Override
	public V put(K key, V value) {
		if (_maxSize <= 0 || this.size() < _maxSize) {
			V v = super.put(key, value);
			_lastAccess.put((K) key, new Date());
			return v;
		} else {
			return null;
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		super.putAll(m);
		m.keySet().forEach((key) -> {
			_lastAccess.put((K) key, new Date());
		});
	}

	@Override
	public V remove(Object key) {
		V v = super.remove(key);
		_lastAccess.remove(key);
		return v;
	}

	@Override
	public void clear() {
		super.clear();
		_lastAccess.clear();
	}

//	@Override
//	public V putIfAbsent(K key, V value) {
//		return super.putIfAbsent(key, value);
//	}
	@Override
	public boolean remove(Object key, Object value) {
		if (super.remove(key, value)) {
			_lastAccess.remove((K) key);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		boolean b = super.replace(key, oldValue, newValue);
		_lastAccess.put(key, new Date());
		return b;
	}

	@Override
	public V replace(K key, V value) {
		V v = super.replace(key, value);
		_lastAccess.put(key, new Date());
		return v;
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		V v = super.getOrDefault(key, defaultValue);
		_lastAccess.put((K) key, new Date());
		return v;
	}

//	@Override
//	public void forEach(BiConsumer<? super K, ? super V> action) {
//		super.forEach(action);
//	}
//
//	@Override
//	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
//		super.replaceAll(function);
//	}
//
//	@Override
//	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
//		return super.computeIfAbsent(key, mappingFunction);
//	}
//
//	@Override
//	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
//		return super.computeIfPresent(key, remappingFunction);
//	}
//
//	@Override
//	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
//		return super.compute(key, remappingFunction);
//	}
//
//	@Override
//	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
//		return super.merge(key, value, remappingFunction);
//	}

//	@Override
//	public boolean contains(Object value) {
//		return super.contains(value);
//	}

//	@Override
//	public Enumeration<K> keys() {
//		return super.keys();
//	}

//	@Override
//	public Enumeration<V> elements() {
//		return super.elements();
//	}

}
