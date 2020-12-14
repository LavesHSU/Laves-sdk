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
 * Class:		LogFile
 * Task:		File for logging actions
 * Created:		10.09.13
 * LastChanges:	22.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.logging.enums.LogType;
import lavesdk.utils.FileUtils;

/**
 * Represents a file for logging information.
 * <br><br>
 * Use {@link #writeToLog(String, LogType)} or one of its equivalents to write a message to the log file.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class LogFile {
	
	/** the logging api */
	private final Logger logger;
	
	/**
	 * Creates a new log file.
	 * 
	 * @param filename the path and name of the file
	 * @throws IOException
	 * <ul>
	 * 		<li>if filename is no valid file path</li>
	 * </ul>
	 * @since 1.0
	 */
	public LogFile(final String filename) throws IOException {
		this.logger = Logger.getLogger(LogFile.class.getName());
		
		FileUtils.createFilePath(filename);
		
		final FileHandler handler = new FileHandler(filename);
		handler.setFormatter(new LogFormat());
		this.logger.addHandler(handler);
		this.logger.setLevel(Level.INFO);
	}
	
	/**
	 * Writes a message to the log file.
	 * 
	 * @param msg the message
	 * @param type the type of the message
	 * @since 1.0
	 */
	public void writeToLog(final String msg, final LogType type) {
		writeToLog(null, msg, null, type);
	}
	
	/**
	 * Writes a message to the log file.
	 * 
	 * @param plugin a {@link AlgorithmPlugin} that want to log a message
	 * @param msg the message
	 * @param type the type of the message
	 * @since 1.0
	 */
	public void writeToLog(final AlgorithmPlugin plugin, final String msg, final LogType type) {
		writeToLog(plugin, msg, null, type);
	}
	
	/**
	 * Writes a message to the log file.
	 * 
	 * @param msg the message
	 * @param e an exception that occurred
	 * @param type the type of the message
	 * @since 1.0
	 */
	public void writeToLog(final String msg, final Exception e, final LogType type) {
		writeToLog(null, msg, e, type);
	}
	
	/**
	 * Writes a message to the log file.
	 * 
	 * @param plugin a {@link AlgorithmPlugin} that want to log a message
	 * @param msg the message
	 * @param e an exception that occurred
	 * @param type the type of the message
	 * @since 1.0
	 */
	public void writeToLog(final AlgorithmPlugin plugin, final String msg, final Exception e, final LogType type) {
		final String pluginText = (plugin != null) ? " (plugin: " + plugin.getName() + ", plugin-version: " + plugin.getVersion() + ")" : "";
		final String exceptionText = (e != null) ? FileUtils.LINESEPARATOR + e.toString() : "";
		
		logger.log(getLogLevel(type), msg + pluginText + exceptionText);
	}
	
	/**
	 * Gets the level for a given log type.
	 * 
	 * @param type {@link LogType}
	 * @return the equivalent level
	 * @since 1.0
	 */
	private Level getLogLevel(final LogType type) {
		switch(type) {
			case ERROR: 	return Level.SEVERE;
			case WARNING:	return Level.WARNING;
			case INFO:
			default:		return Level.INFO;
		}
	}

}
