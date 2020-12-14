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
 * Class:		PluginBundle
 * Task:		The bundle of a plugin containing the plugin itself, the resource loader and additional information
 * Created:		21.04.14
 * LastChanges:	29.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin;

import java.io.File;

import lavesdk.utils.FileUtils;

/**
 * The bundle of a plugin containing the plugin itself, the resource loader and additional information.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class PluginBundle {
	
	/** the plugin */
	private final AlgorithmPlugin plugin;
	/** the resource loader of the plugin */
	private final ResourceLoader resLoader;
	/** the file of the plugin */
	private final File file;
	/** the file name of the plugin */
	private final String name;
	/** the simplified file name of the plugin */
	private final String simpleName;
	/** the path to the plugin */
	private final String path;
	
	public PluginBundle(final AlgorithmPlugin plugin, final ResourceLoader resLoader, final File file) throws IllegalArgumentException {
		if(plugin == null || resLoader == null || file == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.plugin = plugin;
		this.resLoader = resLoader;
		this.file = file;
		this.name = file.getName();
		this.simpleName = name.substring(0, name.length() - ".jar".length());
		this.path = FileUtils.getDirFromFilePath(file.getAbsolutePath());
	}
	
	/**
	 * Gets the plugin.
	 * 
	 * @return the plugin
	 * @since 1.0
	 */
	public final AlgorithmPlugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Gets the resource loader of the plugin.
	 * 
	 * @return the resource loader
	 * @since 1.0
	 */
	public final ResourceLoader getResourceLoader() {
		return resLoader;
	}
	
	/**
	 * Gets the file of the plugin.
	 * 
	 * @return the plugin file
	 * @since 1.0
	 */
	public final File getFile() {
		return file;
	}
	
	/**
	 * Gets the name of the plugin file.
	 * 
	 * @return the plugin file name
	 * @since 1.0
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Gets the simplified name of the plugin file.
	 * 
	 * @return the simplified plugin file name without the file extension
	 * @since 1.0
	 */
	public final String getSimpleName() {
		return simpleName;
	}
	
	/**
	 * Gets the path to the plugin file.
	 * 
	 * @return the plugin file path (ending with a file separator)
	 * @since 1.0
	 */
	public final String getPath() {
		return path;
	}

}
