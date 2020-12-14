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
 * Class:		FileUtils
 * Task:		Utility functions and system independent constants in dealing with files
 * Created:		01.09.13
 * LastChanges:	29.01.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.utils;

import java.io.File;
import java.io.IOException;

/**
 * Utility functions and system independent constants in dealing with files.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class FileUtils {
	
	/** platform independent file path separator like "\" on windows or "/" on unix */
	public final static String FILESEPARATOR = System.getProperty("file.separator");
	/** platform independent home directory of the current user (<b>notice</b>: the path ends with a file separator) */
	public final static String USERHOMEDIR = System.getProperty("user.home") + FILESEPARATOR;
	/** platform independent line separator (line feed/line break) for files/strings */
	public final static String LINESEPARATOR = System.getProperty("line.separator");
	
	private FileUtils() {
	}
	
	/**
	 * Creates the file and corresponding directories if it/they don't exist.
	 * 
	 * @param filename the name and path of the file
	 * @return {@link File}
	 * @throws IOException
	 * <ul>
	 * 		<li>if file could not be created</li>
	 * </ul>
	 * @since 1.0
	 */
	public static File createFilePath(final String filename) throws IOException {
		final File file = new File(filename);
		
		if(!file.exists()) {
			// first create directories if they do not exist
			final String dirname = getDirFromFilePath(filename);
			final File dir = new File(dirname);
			
			// if directories are successfully created -> create file
			if(!dir.exists() && !dir.mkdirs())
				throw new IOException("could not create folders");
			
			file.createNewFile();
		}
		
		return file;
	}
	
	/**
	 * Gets the path to the last directory of the given filename.
	 * 
	 * @param filename the name and path of the file
	 * @return path to last directory of the file (ending with the file separator)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if filename is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static String getDirFromFilePath(final String filename) throws IllegalArgumentException {
		if(filename == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final int lastSep = filename.lastIndexOf(FILESEPARATOR);
		
		String dir = (lastSep > 0) ? filename.substring(0, lastSep) : "";
		if(!dir.endsWith(FILESEPARATOR))
			dir += FILESEPARATOR;
		
		return dir;
	}
	
	/**
	 * Indicates if the given file has a valid file extension.
	 * 
	 * @param file the file
	 * @return <code>true</code> if the file has an extension otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if file is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static boolean hasExtension(final File file) throws IllegalArgumentException {
		if(file == null)
			throw new IllegalArgumentException("No valid argument!");
		
		return hasExtension(file.getAbsolutePath());
	}
	
	/**
	 * Indicates if the given file name has a valid file extension.
	 * 
	 * @param filename the name and path of the file
	 * @return <code>true</code> if the file name has an extension otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if filename is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static boolean hasExtension(final String filename) throws IllegalArgumentException {
		if(filename == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final int lastDot = filename.lastIndexOf(".");
		
		return (lastDot >= 0 && lastDot > filename.lastIndexOf(FILESEPARATOR));
	}
	
	/**
	 * Validates a file meaning that the file is checked for a valid extension and if this is not the case then the
	 * file is expanded by the extension that is specified.
	 * 
	 * @param file the file
	 * @param extension the extension
	 * @return the validated file
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if file is null</li>
	 * 		<li>if extension is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static File validateFile(final File file, String extension) throws IllegalArgumentException {
		if(file == null || extension == null)
			throw new IllegalArgumentException("No valid argument!");
		
		// file has an extension? then it is valid!
		if(hasExtension(file))
			return file;
		
		// correct the extension if it does not start with a dot
		if(!extension.startsWith("."))
			extension = "." + extension;
		
		return new File(file.getAbsolutePath() + extension);
	}

}
