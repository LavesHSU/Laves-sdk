/**
 * This is part of the LAVESDK - Logistics Algorithms Visualization and Education Software Development Kit.
 * 
 * Copyright (C) 2020 Jan Dornseifer & Department of Management Information Science, University of Siegen &
 *                    Department for Management Science and Operations Research, Helmut Schmidt University
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
 */

package lavesdk.algorithm.plugin;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * The resource loader is responsible for loading resources from an {@link AlgorithmPlugin} more precisely from the
 * plugin JAR.
 * <br><br>
 * Use {@link #getResource(String)} to get an URL of the resource file or use {@link #getResourceAsStream(String)}
 * to get an input stream that could be used to read the file directly.
 * <br><br>
 * Resource files can only be integrated in a plugin JAR if they are added to the source folder of your plugin project.<br>
 * <u>Example project structure</u>:
 * <pre>
 * MyPluginProject
 *   - src
 *       - main
 *           MyPlugin.java
 *           - resources
 *               langFile.txt
 * </pre>
 * To read the resource file "langFile.txt" you have to specify the entire path meaning <code>main/resources/langFile.txt</code>.
 * You can derive the path from the package structure of the source folder.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class ResourceLoader {
	
	/** the class loader of the plugin */
	private final ClassLoader clsl;
	/** the url class loader of the plugin or <code>null</code> if the plugin is not loaded from an url class loader */
	private final URLClassLoader urlClsl;
	
	/*
	 * INFO:
	 * this is only a wrapper for using MyPluginClass.class.getResource(...) to simplify the load of resources
	 * and to explain the developer the use of resource files
	 */
	
	/**
	 * Creates a new resource loader.
	 * 
	 * @param clsl the class loader of the plugin
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if clsl is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ResourceLoader(final ClassLoader clsl) throws IllegalArgumentException {
		if(clsl == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.clsl = clsl;
		this.urlClsl = (clsl instanceof URLClassLoader) ? (URLClassLoader)clsl : null;
	}
	
	/**
	 * Gets the URL of a resource file.
	 * <br><br>
	 * <b>Example project structure</b>:
	 * <pre>
	 * MyPluginProject
	 *   - src
	 *       - main
	 *           MyPlugin.java
	 *           - resources
	 *               langFile.txt
	 * </pre>
	 * To read the resource file "langFile.txt" you have to specify the entire path meaning <code>main/resources/langFile.txt</code>.
	 * You can derive the path from the package structure of the source folder.
	 * 
	 * @param name the resource file name as well as the entire path like <code>main/resources/myFile.res</code>
	 * @return the URL of the resource or <code>null</code> if there is no resource with the specified name
	 * @since 1.0
	 */
	public URL getResource(final String name) {
		return clsl.getResource(name);
	}
	
	/**
	 * Gets an input stream of a resource file.
	 * <br><br>
	 * <b>Example project structure</b>:
	 * <pre>
	 * MyPluginProject
	 *   - src
	 *       - main
	 *           MyPlugin.java
	 *           - resources
	 *               langFile.txt
	 * </pre>
	 * To read the resource file "langFile.txt" you have to specify the entire path meaning <code>main/resources/langFile.txt</code>.
	 * You can derive the path from the package structure of the source folder.
	 * 
	 * @param name the resource file name as well as the entire path like <code>main/resources/myFile.res</code>
	 * @return the input stream or <code>null</code> if there is no resource with the specified name
	 * @since 1.0
	 */
	public InputStream getResourceAsStream(final String name) {
		return clsl.getResourceAsStream(name);
	}
	
	/**
	 * Finds a resource.
	 * 
	 * @param name the name of the resource
	 * @return the url to the resource or <code>null</code> if the resource could not be found
	 * @since 1.0
	 */
	public URL findResource(final String name) {
		return (urlClsl != null) ? urlClsl.findResource(name) : null;
	}
	
	/**
	 * Gets a resource as an icon.
	 * <br><br>
	 * <b>Example project structure</b>:
	 * <pre>
	 * MyPluginProject
	 *   - src
	 *       - main
	 *           MyPlugin.java
	 *           - resources
	 *               myIcon.png
	 * </pre>
	 * To read the icon "myIcon.png" you have to specify the entire path meaning <code>main/resources/myIcon.png</code>.
	 * You can derive the path from the package structure of the source folder.
	 * 
	 * @param name the name of the resource
	 * @return the icon or <code>null</code> if the resource could not be loaded
	 * @since 1.0
	 */
	public Icon getResourceAsIcon(final String name) {
		try {
			return new ImageIcon(getResource(name));
		}
		catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * Gets a resource as an image.
	 * <br><br>
	 * <b>Example project structure</b>:
	 * <pre>
	 * MyPluginProject
	 *   - src
	 *       - main
	 *           MyPlugin.java
	 *           - resources
	 *               myImage.png
	 * </pre>
	 * To read the image "myImage.png" you have to specify the entire path meaning <code>main/resources/myImage.png</code>.
	 * You can derive the path from the package structure of the source folder.
	 * 
	 * @param name the name of the resource
	 * @return the image or <code>null</code> if the resource could not be loaded
	 * @since 1.0
	 */
	public Image getResourceAsImage(final String name) {
		try {
			return Toolkit.getDefaultToolkit().getImage(getResource(name));
		}
		catch(Exception e) {
			return null;
		}
	}

}
