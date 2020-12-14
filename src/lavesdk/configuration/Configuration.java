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

package lavesdk.configuration;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lavesdk.serialization.Serializer;
import lavesdk.utils.FileUtils;

/**
 * The configuration data of a plugin.
 * <br><br>
 * You can add new configuration data by using the <i>add</i> methods and request configuration data of a specific
 * type by using the associated <i>get</i> method.
 * <br><br>
 * Each configuration data is associated with a <i>data key</i> which is a string and unique based on the configuration. This means
 * that you cannot store different configuration data by using the same data keys which would result in an overwrite.
 * <br><br>
 * You can save or load a configuration by using the static methods {@link #save(String, Configuration)} and {@link #load(String)}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class Configuration extends Serializer {
	
	private static final long serialVersionUID = 1L;
	
	/** the next id of a configuration */
	private static int nextID = 1;

	/**
	 * Creates a new configuration.
	 * 
	 * @since 1.0
	 */
	public Configuration() {
		super(nextID++, "configuration");
	}
	
	/**
	 * Adds a color to the configuration.
	 * 
	 * @param key the data key
	 * @param c the color
	 * @return the color
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists</li>
	 * 		<li>if c is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final Color addColor(final String key, final Color c) throws IllegalArgumentException {
		return addData(key, c);
	}
	
	/**
	 * Gets a color.
	 * 
	 * @param key the data key
	 * @return the color or <code>null</code> if the configuration does not contain a color with the given key
	 * @since 1.0
	 */
	public final Color getColor(final String key) {
		return getColor(key, null);
	}
	
	/**
	 * Gets a color.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the color or defValue if the configuration does not contain a color with the given key
	 * @since 1.0
	 */
	public final Color getColor(final String key, final Color defValue) {
		return getData(key, Color.class, defValue);
	}
	
	/**
	 * Adds a point to the configuration.
	 * 
	 * @param key the data key
	 * @param p the point
	 * @return the point
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists</li>
	 * 		<li>if p is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final Point addPoint(final String key, final Point p) throws IllegalArgumentException {
		return addData(key, p);
	}
	
	/**
	 * Gets a point.
	 * 
	 * @param key the data key
	 * @return the point or <code>null</code> if the configuration does not contain a point with the given key
	 * @since 1.0
	 */
	public final Point getPoint(final String key) {
		return getPoint(key, null);
	}
	
	/**
	 * Gets a point.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the point or defValue if the configuration does not contain a point with the given key
	 * @since 1.0
	 */
	public final Point getPoint(final String key, final Point defValue) {
		return getData(key, Point.class, defValue);
	}
	
	/**
	 * Adds a rectangle to the configuration.
	 * 
	 * @param key the data key
	 * @param r the rectangle
	 * @return the rectangle
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists</li>
	 * 		<li>if r is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final Rectangle addRectangle(final String key, final Rectangle r) throws IllegalArgumentException {
		return addData(key, r);
	}
	
	/**
	 * Gets a rectangle.
	 * 
	 * @param key the data key
	 * @return the rectangle or <code>null</code> if the configuration does not contain a rectangle with the given key
	 * @since 1.0
	 */
	public final Rectangle getRectangle(final String key) {
		return getRectangle(key, null);
	}
	
	/**
	 * Gets a rectangle.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the rectangle or defValue if the configuration does not contain a rectangle with the given key
	 * @since 1.0
	 */
	public final Rectangle getRectangle(final String key, final Rectangle defValue) {
		return getData(key, Rectangle.class, defValue);
	}
	
	/**
	 * Adds a (sub) configuration to this configuration.
	 * 
	 * @param key the data key
	 * @param cfg the configuration
	 * @return the configuration
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists</li>
	 * 		<li>if cfg is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final Configuration addConfiguration(final String key, final Configuration cfg) throws IllegalArgumentException {
		return addData(key, cfg);
	}
	
	/**
	 * Gets a (sub) configuration for a given data key.
	 * 
	 * @param key the data key
	 * @return the configuration or <code>null</code> if there is no configuration for the given key
	 * @since 1.0
	 */
	public final Configuration getConfiguration(final String key) {
		return getData(key, Configuration.class);
	}
	
	/**
	 * A configuration can override existing keys.
	 * 
	 * @return returns <code>true</code>
	 * @since 1.0
	 */
	@Override
	public boolean areKeysOverrideable() {
		return true;
	}
	
	/**
	 * Loads a configuration file.
	 * 
	 * @param file the configuration file (the file must have the extension ".cfg" otherwise it is added automatically)
	 * @return the configuration
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if file is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static Configuration load(final File file) throws NullPointerException {
		return load(file.getAbsolutePath());
	}
	
	/**
	 * Loads a configuration file.
	 * 
	 * @param filename the file name of the configuration file (the file name must have the extension ".cfg" otherwise it is added automatically)
	 * @return the configuration or a default (empty configuration) if there is no configuration file
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if filename is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static Configuration load(String filename) throws IllegalArgumentException {
		if(filename == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(!filename.endsWith(".cfg"))
			filename += ".cfg";
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(filename);
			ois = new ObjectInputStream(fis);
			
			return (Configuration)ois.readObject();
		} catch (IOException | ClassNotFoundException | ClassCastException e) {
			System.out.println("INFO: No configuration file! Empty configuration is loaded for " + filename + ".");
		}
		finally {
			// release system resources
			if(ois != null) try { ois.close(); } catch(IOException e) {}
			if(fis != null) try { fis.close(); } catch(IOException e) {}
		}
		
		return new Configuration();
	}
	
	/**
	 * Saves a given configuration.
	 * 
	 * @param file the configuration file (the file must have the extension ".cfg" otherwise it is added automatically)
	 * @param cfg the configuration
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if file is null</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if cfg is null</li>
	 * </ul>
	 * @throws IOException
	 * <ul>
	 * 		<li>if configuration could not be saved</li>
	 * </ul>
	 * @since 1.0
	 */
	public static void save(final File file, final Configuration cfg) throws NullPointerException, IllegalArgumentException, IOException {
		save(file.getAbsolutePath(), cfg);
	}
	
	/**
	 * Saves a given configuration.
	 * 
	 * @param filename the file name of the configuration file (the file name must have the extension ".cfg" otherwise it is added automatically)
	 * @param cfg the configuration
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if filename is null</li>
	 * 		<li>if cfg is null</li>
	 * </ul>
	 * @throws IOException
	 * <ul>
	 * 		<li>if configuration could not be saved</li>
	 * </ul>
	 * @since 1.0
	 */
	public static void save(String filename, final Configuration cfg) throws IllegalArgumentException, IOException {
		if(filename == null || cfg == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(!filename.endsWith(".cfg"))
			filename += ".cfg";
		
		// create file if it does not exist
		final File file = FileUtils.createFilePath(filename);
		
		final FileOutputStream fos = new FileOutputStream(file);
		final ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		// write data to stream
		oos.writeObject(cfg);
		oos.flush();
		
		// release system resources
		oos.close();
		fos.close();
	}

}
