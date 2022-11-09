package com.xattacker;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.xattacker.convert.ios.IOSSourceCodeLoader;
import com.xattacker.convert.ios.IOSAssetsResourceImporter;


public final class MainPanel extends Frame
{
	private HashMap<String, String> _resourceKeys;
	
	private LinkedHashMap<String, String> _sources;
	private File _sourceCodeFolder;
	
	public MainPanel()
	{
		super("iOSAssetsResourceScanner");
		
		setBounds(0, 0, 360, 250);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension s = getSize();
		this.setLayout(null);
		setLocation((d.width - s.width) / 2, (d.height - s.height) / 2);
		setResizable(false);
		setVisible(true);
		
		Button button = new Button("Swift source code Folder");
		add(button);
		button.setBounds(30, 60, 300, 25);
		button.setEnabled(true);
		button.addActionListener(
		new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				chooseFolder(
					new FileSelectedListener()
					{
						@Override
						public void onFileSelected(File aFile)
						{
							IOSSourceCodeLoader loader = new IOSSourceCodeLoader();
							_sources = loader.loadUsedResourceStrings(aFile);
							_sourceCodeFolder = aFile;
						}
					}
				);
			}
		});
		
		button = new Button("xcassets Path");
		add(button);
		button.setBounds(30, 100, 300, 25);
		button.setEnabled(true);
		button.addActionListener(
		new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				chooseFolder(
					_sourceCodeFolder,
					new FileSelectedListener()
					{
						@Override
						public void onFileSelected(File aFile)
						{
							
							IOSAssetsResourceImporter importer = new IOSAssetsResourceImporter();
							
							try
							{
								_resourceKeys = importer.loadResource(aFile.getAbsolutePath(), null);
								System.out.println("assets count: " + _resourceKeys.size());
							}
							catch (Exception e)
							{
								e.printStackTrace();
								
								showDialog(" ERROR ", "import failed", DialogType.ERROR);
							}
						}
					}
				);
			}
		});
		
		
		button = new Button("Scan");
		add(button);
		button.setBounds(30, 180, 300, 25);
		button.setEnabled(true);
		button.addActionListener(
		new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (_resourceKeys == null)
				{
					showDialog("Input Error", "Localizable.strings not set", DialogType.ERROR);
					
					return;
				}
				
				if (_sources == null)
				{
					showDialog("Input Error", "Swift source code Folder not set", DialogType.ERROR);
					
					return;
				}
				
//				_resourceKeys.forEach(
//					(key, value) -> {
//				   System.out.println("key: [" + key + "]");
//				});
				
				scan(_sources, _resourceKeys);
			}
		});
		
		addWindowListener(
		new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt)
			{
				dispose();
				System.exit(0);
			}
		});
	}
	
	private void scan(HashMap<String, String> sourceKeys, HashMap<String, String> assets)
	{
		LinkedHashMap<String, String> unused = new LinkedHashMap<String, String>(assets);
		
		sourceKeys.forEach(
			(key, value) -> {
		   if (unused.containsKey(key))
		   {
		   	unused.remove(key);
		   }
		});
		
		if (unused.isEmpty())
		{
			showDialog("Scan Completed", "There is no any non unused assets resource key", DialogType.INFORMATION);
		}
		else
		{
			StringBuilder builder = new StringBuilder();
			unused.forEach(
					(key, value) -> {
						builder.append(key);
						builder.append("\n");
				});
			
			System.out.println("unused assets: " + unused.size());
			System.out.println(builder.toString());
			
			showDialog("Scan Completed", "There are non existed resource key: (" + unused.size() + ") \n" + builder.toString(), DialogType.WARNING);
		}
	}
	
	private void chooseFile(File aDefautDir, FileSelectedListener aListener, String desc, String... exts)
	{
		try
		{
			JFileChooser jfc = new JFileChooser(aDefautDir);
			MyFileFilter filter = new MyFileFilter(desc);
			
			for (String ext : exts)
			{
				filter.addExt(ext);
			}

			jfc.setFileFilter(filter);

			int result = jfc.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				File selected = jfc.getSelectedFile();
				aListener.onFileSelected(selected);
			}
		}
		catch (Exception e)
		{
		}
	}
	
	private void chooseFolder(File aDefautDir, FileSelectedListener aListener)
	{
		try
		{
			JFileChooser chooser = new JFileChooser(aDefautDir);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
		   chooser.setAcceptAllFileFilterUsed(false); // disable the "All files" option.
		    
			int result = chooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				File selected = chooser.getSelectedFile();
				aListener.onFileSelected(selected);
			}
		}
		catch (Exception e)
		{
		}
	}
	
	private void chooseFolder(FileSelectedListener aListener)
	{
		chooseFolder(null, aListener);
	}
	
	public enum DialogType
	{
		INFORMATION(JOptionPane.INFORMATION_MESSAGE),
		WARNING(JOptionPane.WARNING_MESSAGE),
		ERROR(JOptionPane.ERROR_MESSAGE);
		
		int _type;
		
		private DialogType(int aType)
		{
			_type = aType;
		}
	};
	
	private void showDialog(String title, String message, DialogType type)
	{
		JOptionPane.showMessageDialog(new JFrame(), message, title, type._type);
	}
}
