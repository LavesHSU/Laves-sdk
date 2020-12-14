/**
 * This is part of the LAVESDK - Logistics Algorithms Visualization and Education Software Development Kit.
 * 
 * Copyright (C) 2013 Jan Dornseifer & Department of Management Information Science, University of Siegen
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * See license/LICENSE.txt for further information.
 * 
 *=========================================================================================================
 * 
 * Class:		PluginLoader
 * Task:		Load Plugin-JARs from a specific location
 * Created:		01.09.13
 * LastChanges:	21.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import lavesdk.algorithm.plugin.exceptions.InvalidPluginException;
import lavesdk.utils.ProgressListener;

/**
 * Loads plugins from a specific location ({@link #loadPlugin(File)}) or from a directory ({@link #loadPlugins(String)}).
 * <br><br>
 * Use {@link #getInstance()} to get an instance of the plugin loader!
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class PluginLoader {
	
	/** the instance of the loader */
	private static PluginLoader instance;
	
	private PluginLoader() {
	}
	
	/**
	 * Gets the instance of the plugin loader.
	 * 
	 * @return {@link PluginLoader}
	 * @since 1.0
	 */
	public static final PluginLoader getInstance() {
		if(instance == null)
			instance = new PluginLoader();
		
		return instance;
	}
	
	/**
	 * Loads all valid plugins in the given directory.
	 * 
	 * @param dir the plugin directory
	 * @return a list of all loaded plugins
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if dir is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final List<PluginBundle> loadPlugins(final String dir) throws IllegalArgumentException {
		return loadPlugins(dir, null);
	}
	
	/**
	 * Loads all valid plugins in the given directory.
	 * 
	 * @param dir the plugin directory
	 * @param listener {@link ProgressListener} to retrieve information about the loading progress
	 * @return a list of all loaded plugins
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if dir is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final List<PluginBundle> loadPlugins(final String dir, final ProgressListener listener) throws IllegalArgumentException {
		if(dir == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final File folder = new File(dir);
		
		if(!folder.isDirectory())
			throw new IllegalArgumentException("The given path is not a directory!");
		
		// get all plugins (that means all jar files in the given folder)
		final File[] plugins = folder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().endsWith(".jar");
			}
		});
		final List<PluginBundle> result = new ArrayList<PluginBundle>(plugins.length);
		int progress = 1;
		
		// notify listener about the total amount of plugins that have to be loaded
		if(listener != null)
			listener.totalProgress(plugins.length);
		
		// load and instantiate all plugins
		for(File plugin : plugins) {
			try {
				result.add(loadPlugin(plugin));
			}
			catch(InvalidPluginException e) {
				// no action because invalid plugins are ignored
			}
			
			// notify listener about current loading progress
			if(listener != null)
				listener.currentProgress(progress++);
		}
		
		return result;
	}
	
	/**
	 * Loads a given plugin file.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * One class in the plugin jar file must implement the {@link AlgorithmPlugin} interface!
	 * 
	 * @param file the plugin jar
	 * @return a plugin bundle
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if file is null</li>
	 * 		<li>if file is not a jar file meaning it does not end with <code>.jar</code></li>
	 * </ul>
	 * @throws InvalidPluginException
	 * <ul>
	 * 		<li>if the plugin could not be loaded</li>
	 * </ul>
	 * @since 1.0
	 */
	public final PluginBundle loadPlugin(final File file) throws IllegalArgumentException, InvalidPluginException {
		if(file == null || !file.getName().toLowerCase().endsWith(".jar"))
			throw new IllegalArgumentException("No valid argument!");
		
		final String CLASS_FILE_EXT = ".class";
		URLClassLoader clsLoader = null;
		FileInputStream fis = null;
		JarInputStream jis = null;
		JarEntry jEntry;
		String entryName;
		String clsName;
		Class<?> cls;
		PluginBundle bundle = null;
		
		try {
			// create class loader for jar file and open an input stream to read it
			clsLoader = new URLClassLoader(new URL[] { file.toURI().toURL() });
			fis = new FileInputStream(file);
			jis = new JarInputStream(fis);
			
			// run through all entries of the jar file (that means all classes, interfaces, ...)
			while((jEntry = jis.getNextJarEntry()) != null) {
				entryName = jEntry.getName();
				
				// check if the entry is a class file
				if(entryName.toLowerCase().endsWith(CLASS_FILE_EXT)) {
					// extract class name
					clsName = entryName.substring(0, entryName.length() - CLASS_FILE_EXT.length()).replace('/', '.');
					// load class with the specified class loader
					cls = clsLoader.loadClass(clsName);
					
					// does the loaded class implements the plugin interface?
					if(AlgorithmPlugin.class.isAssignableFrom(cls)) {
						// create an instance of the plugin and make the package of it
						bundle = new PluginBundle((AlgorithmPlugin)cls.newInstance(), new ResourceLoader(clsLoader), file);
						// the entry point is found so quit the search
						break;
					}
				}
			}
		}
		catch(IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new InvalidPluginException(e.getMessage());
		}
		finally {
			// release system resources
			if(jis != null) try { jis.close(); } catch(IOException e) { jis = null; }
			if(fis != null) try { fis.close(); } catch(IOException e) { fis = null; }
			
			/*
			 * INFO:
			 * the class loader may not be closed otherwise it is not possible to load any resource of the plugin jar!
			 */
		}
		
		if(bundle == null)
			throw new InvalidPluginException("The plugin " + file.getName() + " could not be loaded. Entry point is missing!");
		else
			return bundle;
	}

}
