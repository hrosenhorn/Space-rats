package com.spacerats.cache;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Håkan Larsson
 */
public class CacheImpl<K, V> implements Cache<K, V>
{
	private ConcurrentMap<K, Entry<V>> storage = new ConcurrentHashMap<K, Entry<V>>();

	public void addEntry(K key, V value)
	{
		storage.put(key, new Entry<V>(value, this));
	}

	public Entry<V> getEntry(K key)
	{
		synchronized (storage)
		{
			Entry<V> vEntry = storage.get(key);

			if (vEntry == null)
			{
				return null;
			}
			
			return storage.get(key);
		}
	}


	public V getValue(K key)
	{
		return storage.get(key).getData();
	}

	public void purge()
	{
		storage.clear();
	}
}
