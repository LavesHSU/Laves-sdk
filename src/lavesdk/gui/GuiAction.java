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
 * Class:		GuiAction
 * Task:		Perform an action in the EDT of a graphical user interface
 * Created:		10.02.14
 * LastChanges:	14.02.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui;

import java.awt.AWTError;
import java.awt.AWTException;

/**
 * Represents an action in the graphical user interface.
 * <br><br>
 * An action is always performed in the event dispatch thread (EDT) of swing and can have a result.
 * <br><br>
 * <b>Exception Handling</b>:<br>
 * All actions that are performed <b>synchronously</b> in the EDT rethrow a caught exception so that the exception
 * can be handled by the invoker.
 * <br><br>
 * <b>Attention</b>:<br>
 * This class may only be used by classes of the LAVESDK or more precisely from the inside of this package!<br>
 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS CLASS</i>!
 * 
 * @author Administrator
 * @version 1.0
 * @since 1.0
 * @param <T> the type of the action result or {@link Void} if an action does not have a result
 */
abstract class GuiAction<T> implements Runnable {
	
	/** the name of the action or <code>null</code> */
	protected final String name;
	/** flag that indicates whether this action has a result meaning that the execution must wait until the action is done */
	protected final boolean hasResult;
	/** the result of the action */
	private T result;
	/** the {@link RuntimeException} that was caught during the performance of the action or <code>null</code> */
	private RuntimeException rtException;
	
	/**
	 * Creates a new GUI action.
	 * 
	 * @param name the name of the action or <code>null</code> (the name is used to annotate exceptions that occur during the performance so it makes it a bit easier to allocate the point of failure)
	 * @param hasResult <code>true</code> if the action has a result meaning that the execution must be performed <b>synchronously</b> in the EDT so that the action waits for completion otherwise <code>false</code>
	 * @since 1.0
	 */
	protected GuiAction(final String name, final boolean hasResult) {
		this.name = name;
		this.hasResult = hasResult;
		this.result = null;
		this.rtException = null;
	}
	
	/**
	 * Performs the action.
	 * 
	 * @since 1.0
	 */
	@Override
	public final void run() {
		try {
			result = perform();
		}
		catch(RuntimeException e) {
			if(hasResult)
				rtException = e;
			else
				handleCaughtThrowable(e);
		}
		catch(Throwable t) {
			handleCaughtThrowable(t);
		}
	}
	
	/**
	 * Performs the action.
	 * <br><br>
	 * The action is performed thread safe in the event dispatch thread (EDT) of the graphical user interface.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The action to perform may not consume much time otherwise the EDT can freeze.
	 * 
	 * @return the result of the action
	 * @throws Throwable
	 * <ul>
	 * 		<li>can throw exceptions (like {@link RuntimeException}, {@link AWTException}, ...) and errors (like {@link AWTError}, ...) that are occurred during the performance</li>
	 * </ul>
	 * @since 1.0
	 */
	protected abstract T perform() throws Throwable;
	
	/**
	 * Indicates whether this action expect a result.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return <code>true</code> if the action has a result otherwise <code>false</code>
	 * @since 1.0
	 */
	final boolean hasResult() {
		return hasResult;
	}
	
	/**
	 * Gets the result of the action.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the result or <code>null</code>
	 * @since 1.0
	 */
	final T getResult() {
		return result;
	}
	
	/**
	 * Clears the result of the action and a potentially caught {@link RuntimeException} to <code>null</code>.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @since 1.0
	 */
	final void reset() {
		result = null;
		rtException = null;
	}
	
	/**
	 * Gets the potentially caught {@link RuntimeException} which indicates that it has occurred a runtime exception during
	 * the performance of the action.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the caught runtime exception or <code>null</code> if there is no exception occurred
	 * @since 1.0
	 */
	final RuntimeException getCaughtRuntimeException() {
		return rtException;
	}
	
	/**
	 * Handles the specified {@link Throwable} meaning that an error message is displayed in the console.
	 * 
	 * @param t the throwable
	 * @since 1.0
	 */
	private void handleCaughtThrowable(final Throwable t) {
		System.err.println("it is occurred an exception for the gui action \"" + ((name != null) ? name : "null") + "\" (" + t.getClass().getSimpleName() + ", \"" + t.getMessage() + "\")");
	}

}
