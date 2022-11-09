package com.xattacker.convert.ios;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;


public final class IOSAssetsResourceImporter
{
	private LinkedHashMap<String, String> _properties = null;
	private ArrayList<String> _duplicateds = null;
	
	public LinkedHashMap<String, String> loadResource(String aFolder, ArrayList<String> aDuplicateds) throws Exception
	{
		try
		{
			File dir = new File(aFolder);
			if (!dir.isDirectory())
			{
				throw new Exception(aFolder + " should be a assets folder");
			}
			
			
			_properties = new LinkedHashMap<String, String>();
			_duplicateds = new ArrayList<String>();
			loadImageset(dir);
		}
		catch (Exception ex)
		{
			throw ex;
		}
		finally
		{
			System.gc();
		}

		return _properties;
	}
	
	private void loadImageset(File aDir)
	{
		File[] files = aDir.listFiles();
		for (File file : files)
		{
			if (!file.isDirectory())
			{
				continue;
			}
			
			String file_name = file.getName();
			if (file_name.endsWith(".imageset"))
			{
				file_name = file_name.substring(0, file_name.indexOf("."));
				System.out.println("got imageset folder name: " + file_name);
				if (_properties.get(file_name) == null)
				{
					_properties.put(file_name, file_name);
				}
				else
				{
					// duplicated name
					_duplicateds.add(file_name);
				}
			}
			else if (file_name.indexOf(".") < 0)
			{
				// a pure folder, try to load sub folder
				loadImageset(file);
			}
		}
	}
}
