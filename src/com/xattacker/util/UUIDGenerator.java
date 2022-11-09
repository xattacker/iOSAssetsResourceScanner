package com.xattacker.util;

import java.util.UUID;


public final class UUIDGenerator
{
	private UUIDGenerator()
	{
	}
	
   public static String generateUUID()
   {
  	 return UUID.randomUUID().toString();
   }
}
