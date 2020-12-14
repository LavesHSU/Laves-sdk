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

package lavesdk.algorithm;

/**
 * Listener to get notified by the algorithm runtime environment about several events.
 * <br><br>
 * Available events:
 * <ul>
 * 		<li>{@link #beforeStart(RTEvent)}</li>
 * 		<li>{@link #beforeResume(RTEvent)}</li>
 * 		<li>{@link #beforePause(RTEvent)}</li>
 * 		<li>{@link #onStop()}</li>
 * 		<li>{@link #onRunning()}</li>
 * 		<li>{@link #onPause()}</li>
 * </ul>
 * <b>Notice</b>:<br>
 * All events are executed in the event dispatch thread. That means you should not perform time-consuming tasks but it is ensured
 * that you can make any changes to the graphical user interface (GUI) without producing thread interference.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public interface RTEListener {
	
	/**
	 * Indicates that the runtime environment wants to start from scratch.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use {@link RTEvent#doit} to cancel the action if necessary.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * All events are executed in the event dispatch thread. That means you should not perform time-consuming tasks but it is ensured
	 * that you can make any changes to the graphical user interface (GUI) without producing thread interference.
	 * 
	 * @param e the event data
	 * @since 1.0
	 */
	public void beforeStart(final RTEvent e);
	
	/**
	 * Indicates that the runtime environment wants to resume.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use {@link RTEvent#doit} to cancel the action if necessary.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * All events are executed in the event dispatch thread. That means you should not perform time-consuming tasks but it is ensured
	 * that you can make any changes to the graphical user interface (GUI) without producing thread interference.
	 * 
	 * @param e the event data
	 * @since 1.0
	 */
	public void beforeResume(final RTEvent e);
	
	/**
	 * Indicates that the runtime environment wants to transition into pause state.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use {@link RTEvent#doit} to cancel the action if necessary.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * All events are executed in the event dispatch thread. That means you should not perform time-consuming tasks but it is ensured
	 * that you can make any changes to the graphical user interface (GUI) without producing thread interference.
	 * 
	 * @param e the event data
	 * @since 1.0
	 */
	public void beforePause(final RTEvent e);
	
	/**
	 * Indicates that the runtime environment is terminated which means it has to be restarted.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * All events are executed in the event dispatch thread. That means you should not perform time-consuming tasks but it is ensured
	 * that you can make any changes to the graphical user interface (GUI) without producing thread interference.
	 * 
	 * @since 1.0
	 */
	public void onStop();
	
	/**
	 * Indicates that the runtime environment is started now meaning that it is passed into running mode.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * All events are executed in the event dispatch thread. That means you should not perform time-consuming tasks but it is ensured
	 * that you can make any changes to the graphical user interface (GUI) without producing thread interference.
	 * 
	 * @since 1.0
	 */
	public void onRunning();
	
	/**
	 * Indicates that the runtime environment has passed into pause state.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * All events are executed in the event dispatch thread. That means you should not perform time-consuming tasks but it is ensured
	 * that you can make any changes to the graphical user interface (GUI) without producing thread interference.
	 * 
	 * @since 1.0
	 */
	public void onPause();

}
