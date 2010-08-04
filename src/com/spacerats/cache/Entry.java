package com.spacerats.cache;

/**
 * @author Håkan Larsson
 */
public class Entry<V>
{
	private V data;
	private Cache<?, V> cache;

	public Entry(V data, Cache<?, V> cache)
	{
		this.data = data;
		this.cache = cache;
	}
	
	public V getData()
	{
		return data;
	}
}
