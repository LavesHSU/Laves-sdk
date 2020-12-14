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

package lavesdk.gui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 * Provides methods to execute {@link GuiJob}s or {@link GuiRequest}s (<b>thread-safe</b>) in the event dispatch thread (EDT) of a graphical
 * user interface (GUI) from any other thread or the EDT itself.
 * <br><br>
 * Use {@link #execute(GuiJob)} to perform a {@link GuiJob} or use {@link #execute(GuiRequest)} to perform a {@link GuiRequest}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class EDT {
	
	private EDT() {
	}
	
	/**
	 * Indicates whether an action is executed in the event dispatch thread (EDT).
	 * 
	 * @return <code>true</code> if it is executed in the EDT otherwise <code>false</code> if it is executed in another thread than the EDT
	 * @since 1.0
	 */
	public static boolean isExecutedInEDT() {
		return SwingUtilities.isEventDispatchThread();
	}
	
	/**
	 * Executes the specified job in the event dispatch thread of a graphical user interface.
	 * 
	 * @param job the job
	 * @throws RuntimeException
	 * <ul>
	 * 		<li>if job is null</li>
	 * 		<li>if a runtime exception occurred during the performance of the job (<b>this is only possible if you execute a job that waits for its completion</b>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public static void execute(final GuiJob job) throws RuntimeException {
		perform(job);
	}
	
	/**
	 * Executes the specified request in the event dispatch thread of a graphical user interface.
	 * 
	 * @param request the request
	 * @return the result of the request or <code>null</code>
	 * @throws RuntimeException
	 * <ul>
	 * 		<li>if request is null</li>
	 * 		<li>if a runtime exception occurred during the performance of the request</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <T> T execute(final GuiRequest<T> request) throws RuntimeException {
		return perform(request);
	}
	
	/**
	 * Performs the execution of the specified action in the event dispatch thread.
	 *  
	 * @param action the action
	 * @return the result of the action
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if action is null</li>
	 * </ul>
	 * @throws RuntimeException
	 * <ul>
	 * 		<li>if a runtime exception occurred during the performance of an action (<b>this is only possible if the action is performed synchronously in the EDT</b>)</li>
	 * </ul>
	 * @since 1.0
	 */
	private static <T> T perform(final GuiAction<T> action) throws RuntimeException {
		if(action == null)
			throw new IllegalArgumentException("Invalid action (action == null)! Cannot be executed in the EDT.");
		
		// the thread that executes the action is the event dispatch thread (EDT)?
		if(isExecutedInEDT()) {
			// yes, then we can directly run the action
			action.run();
		}
		else {
			// no, then shift the GUI action to the event dispatch thread (EDT)
			if(action.hasResult()) {
				// if the action has a result then we have to wait until the action is done by the EDT
				try {
					// this should only be done by threads unlike the EDT otherwise this results in a dead lock
					// (is avoided because of the first execution condition that checks whether the action is performed
					// in the EDT)
					SwingUtilities.invokeAndWait(action);
				} catch (InvocationTargetException | InterruptedException e) {
					action.reset();
				}
			}
			else
				SwingUtilities.invokeLater(action);
		}
		
		// it has occurred an exception during the performance of the action? then rethrow it to show the point of failure
		// (only possible if the action has a result otherwise it cannot be determined when the action is performed 
		// because of invokeLater(...))
		if(action.hasResult() && action.getCaughtRuntimeException() != null)
			throw action.getCaughtRuntimeException();
		
		// get the result, clear it for a further execution and return it
		final T result = action.getResult();
		action.reset();
		
		return result;
	}

}
