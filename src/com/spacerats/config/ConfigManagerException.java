package com.spacerats.config;

/**
 * @author Håkan Larsson
 */
public class ConfigManagerException extends Exception
{
	private static final long serialVersionUID = 4156782566109332756L;

	public ConfigManagerException(String message)
	{
		super(message);
	}

	public ConfigManagerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ConfigManagerException(Throwable cause)
	{
		super(cause);
	}
}
