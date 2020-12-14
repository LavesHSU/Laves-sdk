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

import javax.swing.JDialog;

import lavesdk.algorithm.AlgorithmExerciseProvider;
import lavesdk.algorithm.plugin.enums.MessageIcon;
import lavesdk.algorithm.plugin.security.HostSecurity;
import lavesdk.algorithm.plugin.views.ExercisesListView;
import lavesdk.algorithm.plugin.views.GraphView;
import lavesdk.algorithm.text.AlgorithmText;
import lavesdk.language.LanguageFile;
import lavesdk.logging.LogFile;
import lavesdk.logging.enums.LogType;
import lavesdk.resources.Resources;

/**
 * Represents the host of a collection of {@link AlgorithmPlugin}s.
 * <br><br>
 * For a plugin this is the interface to the host application.<br>
 * At each time there can only be one active plugin, all other plugins are disabled.
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public interface PluginHost {
	
	/**
	 * Gets the language id that is used and set in the host application.
	 * 
	 * @see LanguageFile
	 * @see #getLanguageFile()
	 * @return the language id which is specified
	 * @since 1.0
	 */
	public String getLanguageID();
	
	/**
	 * Gets the language file of the host application.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This language file also includes the labels of the LAVESDK language file (found in \resources\files or {@link Resources#LANGUAGE_FILE})
	 * that means all the language dependent labels of graphical views (like {@link GraphView} and so on) which are part of the LAVESDK
	 * can be requested by using this file.
	 * 
	 * @see LanguageFile
	 * @see #getLanguageID()
	 * @return the language file
	 * @since 1.0
	 */
	public LanguageFile getLanguageFile();
	
	/**
	 * Indicates if the given plugin is currently enabled in the host application.
	 * 
	 * @param plugin {@link AlgorithmPlugin}
	 * @return <code>true</code> if plugin is the active one otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isActivePlugin(final AlgorithmPlugin plugin);
	
	/**
	 * Displays a message box in the host application.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Only the active plugin has the permission to show a message in the host application!
	 * 
	 * @param plugin {@link AlgorithmPlugin} that wants to show a message
	 * @param msg the message text
	 * @param title the title of the message
	 * @param icon the icon type of the message
	 * @since 1.0
	 */
	public void showMessage(final AlgorithmPlugin plugin, final String msg, final String title, final MessageIcon icon);
	
	/**
	 * Writes a message to the log file.
	 * 
	 * @see LogFile
	 * @param plugin the {@link AlgorithmPlugin} that wants to log a message
	 * @param msg the message
	 * @param type the message type
	 * @since 1.0
	 */
	public void writeLogMessage(final AlgorithmPlugin plugin, final String msg, final LogType type);
	
	/**
	 * Writes a message to the log file.
	 * 
	 * @see LogFile
	 * @param plugin the {@link AlgorithmPlugin} that wants to log a message
	 * @param msg the message
	 * @param e the exception that occurred
	 * @param type the message type
	 * @since 1.0
	 */
	public void writeLogMessage(final AlgorithmPlugin plugin, final String msg, final Exception e, final LogType type);
	
	/**
	 * Checks if the given host is accepted by this host.
	 * <br><br>
	 * This is necessary so that plugin developers do not write their own host applications to bypass security-critical areas.
	 * 
	 * @see HostSecurity
	 * @param host the host
	 * @return <code>true</code> if the host is accepted by this one otherwise <code>false</code> (in general it is done by returning <code>host == this</code>)
	 * @since 1.0
	 */
	public boolean checkPermission(final PluginHost host);
	
	/**
	 * Gets the default exercise provider that is implemented by the host system.
	 * 
	 * @see ExercisesListView
	 * @return the default exercise provider
	 * @since 1.0
	 */
	public AlgorithmExerciseProvider getDefaultExerciseProvider();
	
	/**
	 * Indicates that the mode of the runtime environment of the active plugin changed from normal mode to exercise
	 * mode or the other way around.
	 * 
	 * @since 1.0
	 */
	public void rteModeChanged();
	
	/**
	 * Adapts the dialog to the host application meaning that the dialog is centered in the host
	 * and the application icon is set to the dialog.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method should be implemented as follows:
	 * <pre>
	 * public void adaptDialog(final JDialog dialog) {
	 *     dialog.setLocationRelativeTo(this);
	 *     dialog.setIconImage(this.getIconImage());
	 * }
	 * </pre>
	 * @param dialog the dialog that should be adapted
	 * @since 1.0
	 */
	public void adaptDialog(final JDialog dialog);
	
	/**
	 * Gets the number of the currently installed plugins in the host application.
	 * 
	 * @return number of plugins
	 * @since 1.1
	 */
	public int getPluginCount();
	
	/**
	 * Gets the name of a plugin.
	 * 
	 * @see AlgorithmPlugin#getName()
	 * @param index the index of the plugin
	 * @return the plugin name
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getPluginCount()</code>)</li>
	 * </ul>
	 * @since 1.1
	 */
	public String getPluginName(final int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the description of a plugin.
	 * 
	 * @see AlgorithmPlugin#getDescription()
	 * @param index the index of the plugin
	 * @return the plugin description
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getPluginCount()</code>)</li>
	 * </ul>
	 * @since 1.1
	 */
	public String getPluginDescription(final int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the type of a plugin.
	 * 
	 * @see AlgorithmPlugin#getType()
	 * @param index the index of the plugin
	 * @return the plugin type
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getPluginCount()</code>)</li>
	 * </ul>
	 * @since 1.1
	 */
	public String getPluginType(final int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the author of a plugin.
	 * 
	 * @see AlgorithmPlugin#getAuthor()
	 * @param index the index of the plugin
	 * @return the plugin author
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getPluginCount()</code>)</li>
	 * </ul>
	 * @since 1.1
	 */
	public String getPluginAuthor(final int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the author contact details of a plugin.
	 * 
	 * @see AlgorithmPlugin#getAuthorContact()
	 * @param index the index of the plugin
	 * @return the plugin author contact details
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getPluginCount()</code>)</li>
	 * </ul>
	 * @since 1.1
	 */
	public String getPluginAuthorContact(final int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the assumptions of a plugin.
	 * 
	 * @see AlgorithmPlugin#getAssumptions()
	 * @param index the index of the plugin
	 * @return the plugin assumptions
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getPluginCount()</code>)</li>
	 * </ul>
	 * @since 1.1
	 */
	public String getPluginAssumptions(final int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the problem affiliation of a plugin.
	 * 
	 * @see AlgorithmPlugin#getProblemAffiliation()
	 * @param index the index of the plugin
	 * @return the plugin problem affiliation
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getPluginCount()</code>)</li>
	 * </ul>
	 * @since 1.1
	 */
	public String getPluginProblemAffiliation(final int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the subject of a plugin.
	 * 
	 * @see AlgorithmPlugin#getSubject()
	 * @param index the index of the plugin
	 * @return the plugin subject
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getPluginCount()</code>)</li>
	 * </ul>
	 * @since 1.1
	 */
	public String getPluginSubject(final int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the instructions of a plugin.
	 * 
	 * @see AlgorithmPlugin#getInstructions()
	 * @param index the index of the plugin
	 * @return the plugin instructions
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getPluginCount()</code>)</li>
	 * </ul>
	 * @since 1.1
	 */
	public String getPluginInstructions(final int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the version of a plugin.
	 * 
	 * @see AlgorithmPlugin#getVersion()
	 * @param index the index of the plugin
	 * @return the plugin version
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getPluginCount()</code>)</li>
	 * </ul>
	 * @since 1.1
	 */
	public String getPluginVersion(final int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the algorithm text of a plugin.
	 * 
	 * @see AlgorithmPlugin#getText()
	 * @param index the index of the plugin
	 * @return the plugin algorithm text
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getPluginCount()</code>)</li>
	 * </ul>
	 * @since 1.1
	 */
	public AlgorithmText getPluginText(final int index) throws IndexOutOfBoundsException;

}
