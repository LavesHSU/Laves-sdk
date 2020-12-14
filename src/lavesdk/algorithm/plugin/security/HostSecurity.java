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

package lavesdk.algorithm.plugin.security;

import java.util.ArrayList;
import java.util.List;

import lavesdk.algorithm.AlgorithmExerciseProvider;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.algorithm.plugin.enums.MessageIcon;
import lavesdk.logging.enums.LogType;

/**
 * A security object for the use of a secured host.
 * <br><br>
 * If a class <code>A</code> makes use of a {@link PluginHost} it cannot be guaranteed that this is the real
 * host application because a plugin developer can easily create a {@link PluginHost} himself and set this
 * host instance in <code>A</code>.
 * <br><br>
 * During the runtime there is only one real host application and to prevent an object from using a wrong host this object should
 * inherit from {@link HostSecurity}. To get a functioning object the host must be registered with
 * {@link #registerHost(PluginHost)}. If there is registered an invalid host the object is not functioning and furthermore
 * the plugin is not usable because there are only two cases:
 * <ol>
 * 		<li>all registered hosts accept one of them completely which means this is the secured host</li>
 * 		<li>one registered host do not accept another host which means that there is no secured host</li>
 * </ol>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class HostSecurity {
	
	/** the list of registered hosts */
	private final List<PluginHost> registeredHosts;
	/** the secured host or <code>null</code> */
	private PluginHost host;
	
	/**
	 * Creates a new security manager.
	 * 
	 * @since 1.0
	 */
	public HostSecurity() {
		registeredHosts = new ArrayList<PluginHost>(3);
		host = null;
	}
	
	/**
	 * Registers a new host.
	 * 
	 * @param host the host
	 * @since 1.0
	 */
	public final void registerHost(final PluginHost host) {
		if(host == null || registeredHosts.contains(host))
			return;
		
		// register host
		registeredHosts.add(host);
		// reset current secured host because we look for a new one
		this.host = null;
		
		boolean completelyAccepted;
		
		// for each registered host look if the host is completely accepted by all other hosts
		for(PluginHost host1 : registeredHosts) {
			completelyAccepted = true;
			
			for(PluginHost host2 : registeredHosts) {
				completelyAccepted = completelyAccepted && host2.checkPermission(host1);
				if(!completelyAccepted)
					break;
			}
			
			// we found a completely accepted host? then set the secured host and break up!
			if(completelyAccepted) {
				this.host = host1;
				hostAccepted();
				break;
			}
		}
	}
	
	/**
	 * Is invoked when the secured host is set meaning that {@link #getHost()} returns a valid host.
	 * 
	 * @since 1.0
	 */
	protected void hostAccepted() {
	}
	
	/**
	 * Gets the secured host.
	 * <br><br>
	 * A secured host is a host that is signed off on every other registered hosts.
	 * 
	 * @see #isActivePlugin(AlgorithmPlugin)
	 * @return the secured host or <code>null</code> if there is no secured host
	 * @since 1.0
	 */
	protected final PluginHost getHost() {
		return host;
	}
	
	/**
	 * Indicates if the given plugin is the active one in the host application.
	 * <br><br>
	 * This is a wrapper for using <code>if(getHost() != null) active = getHost().isActivePlugin(...);</code>.
	 * 
	 * @see #getHost()
	 * @param plugin the plugin
	 * @return <code>ture</code> if the given plugin is the active one otherwise <code>false</code>
	 * @since 1.0
	 */
	protected final boolean isActivePlugin(final AlgorithmPlugin plugin) {
		return host != null && host.isActivePlugin(plugin);
	}
	
	/**
	 * Writes a log message.
	 * <br><br>
	 * This is a wrapper for using <code>if(getHost() != null) getHost().writeLogMessage(...);</code>.
	 * 
	 * @param plugin the plugin that wants to log a message
	 * @param msg the message
	 * @param type the message type
	 * @since 1.0
	 */
	protected final void writeLogMessage(final AlgorithmPlugin plugin, final String msg, final LogType type) {
		if(host != null)
			host.writeLogMessage(plugin, msg, type);
	}
	
	/**
	 * Writes a log message.
	 * <br><br>
	 * This is a wrapper for using <code>if(getHost() != null) getHost().writeLogMessage(...);</code>.
	 * 
	 * @param plugin the plugin that wants to log a message
	 * @param msg the message
	 * @param e the exception that occurred
	 * @param type the message type
	 * @since 1.0
	 */
	protected final void writeLogMessage(final AlgorithmPlugin plugin, final String msg, final Exception e, final LogType type) {
		if(host != null)
			host.writeLogMessage(plugin, msg, e, type);
	}
	
	/**
	 * Displays a message box.
	 * <br><br>
	 * This is a wrapper for using <code>if(getHost() != null) getHost().showMessage(...);</code>.
	 * 
	 * @param plugin the plugin that wants to show a message
	 * @param msg the message text
	 * @param title the title of the message
	 * @param icon the icon type of the message
	 * @since 1.0
	 */
	protected final void showMessage(final AlgorithmPlugin plugin, final String msg, final String title, final MessageIcon icon) {
		if(host != null)
			host.showMessage(plugin, msg, title, icon);
	}
	
	/**
	 * Gets the default exercise provider of the host system.
	 * <br><br>
	 * This is a wrapper for using <code>if(getHost() != null) getHost().getDefaultExerciseProvider();</code>.
	 * 
	 * @return the exercise provider or <code>null</code> if there is no secured host
	 * @since 1.0
	 */
	protected final AlgorithmExerciseProvider getDefaultExerciseProvider() {
		if(host != null)
			return host.getDefaultExerciseProvider();
		else
			return null;
	}

}
