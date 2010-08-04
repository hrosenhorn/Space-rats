package com.spacerats.cache;

/**
 * @author Håkan Larsson
 */
public interface Cache<K, V>
{
	void addEntry(K key, V value);
	Entry<V> getEntry(K key);
	
	V getValue(K key);
	void purge ();
}
