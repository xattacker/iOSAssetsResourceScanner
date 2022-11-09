package com.xattacker.convert.ios;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IOSSourceCodeLoader
{
	private LinkedHashMap<String, String> _sources = null;
	
	public LinkedHashMap<String, String> loadUsedResourceStrings(File aDir)
	{
		_sources = new LinkedHashMap<String, String>();
		
		loadSourceCode(aDir);
		
		return _sources;
	}
	
	private void loadSourceCode(File aFile)
	{
		if (aFile.isDirectory())
		{
			File[] files = aFile.listFiles();
			for (File file : files)
			{
				loadSourceCode(file);
			}
		}
		else if (aFile.isFile())
		{
			String file_name = aFile.getName().toLowerCase();
			String regex_body = "[\\w\\s-]";
			
		   if (file_name.endsWith(".swift"))
			{
				try
				{
					System.out.println("load swift file: " + file_name);
					
					String code = new String(Files.readAllBytes(Paths.get(aFile.toURI())));
					//System.out.println(code);
					
					 Pattern p = Pattern.compile("\"{1}" + regex_body + "{2,}\"");
				    Matcher matcher = p.matcher(code);
				    while (matcher.find()) 
				    {
				        String value = matcher.group(0);
				        //System.out.println(value);
				        
				        int index = value.lastIndexOf("\"");
				        value = value.substring(1, index);
				        //System.out.println("["+ value + "]");
				        
				        if (!_sources.containsKey(value))
				        {
				      	  _sources.put(value, file_name);
				        }
				    }
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		   else if (file_name.endsWith(".xib"))
		   {
				try
				{
					System.out.println("load xib file: " + file_name);
					
					String code = new String(Files.readAllBytes(Paths.get(aFile.toURI())));
					//System.out.println(code);
					
					 Pattern p = Pattern.compile("name=\"{1}" + regex_body + "{2,}\" ");
				    Matcher matcher = p.matcher(code);
				    while (matcher.find()) 
				    {
				        String value = matcher.group(0);
				        //System.out.println(value);
				        
				        int index = value.lastIndexOf("\"");
				        value = value.substring(6, index);
				        //System.out.println("["+ value + "]");
				        
				        if (!_sources.containsKey(value))
				        {
				      	  _sources.put(value, file_name);
				        }
				    }
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		   }
		}
	}
}
