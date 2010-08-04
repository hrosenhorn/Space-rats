package com.spacerats.cache;

/**
 * @author Håkan Larsson
 */
public class CachePreloadException extends Exception
{
	public CachePreloadException()
	{
	}

	public CachePreloadException(String message)
	{
		super(message);
	}

	public CachePreloadException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CachePreloadException(Throwable cause)
	{
		super(cause);
	}
}
