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
 * Class:		GuiRequest
 * Task:		Perform a request in the EDT of a graphical user interface
 * Created:		10.02.14
 * LastChanges:	14.02.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui;

/**
 * Performs a request in the event dispatch thread (EDT) of a graphical user interface (GUI).
 * <br><br>
 * A request that queries data from a component of a graphical user interface has to be executed in the EDT of the swing client.
 * Because Swing is <b>not thread-safe</b> it is not possible to query GUI data from a worker thread (thread that does background work or
 * time-consuming tasks) without hazard the consequences of unpredictable behavior.<br>
 * Therefore you should use a {@link GuiRequest} or a {@link GuiJob} (if you do not need to request data) to perform <b>thread-safe</b>
 * tasks in the graphical user interface (in a safe way from a worker thread).<br>
 * A request is always executed <b>synchronously</b> in the EDT meaning that the request waits for completion (until all pending
 * events in the EDT are processed).
 * <br><br>
 * <b>Example</b>:<br>
 * <pre>
 * final String text = EDT.execute(new GuiRequest<String>("getText") {
 *     protected String execute() throws Throwable {
 *         return guiComponent.getText();
 *     }
 * });
 * </pre>
 * 
 * @see EDT
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <T> the type of the result of the request
 */
public abstract class GuiRequest<T> extends GuiAction<T> {

	/**
	 * Creates a new GUI request.
	 * 
	 * @since 1.0
	 */
	public GuiRequest() {
		this(null);
	}
	
	/**
	 * Creates a new GUI request.
	 * 
	 * @param name the name of the request or <code>null</code> (the name is used to annotate exceptions that occur during the performance so it makes it a bit easier to allocate the point of failure)
	 * @since 1.0
	 */
	public GuiRequest(String name) {
		super(name, true);
	}

	@Override
	protected final T perform() throws Throwable {
		return execute();
	}
	
	/**
	 * Executes the request.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The request should not be a time-consuming task otherwise the graphical user interface can freeze and is not responsive to user interactions.
	 * 
	 * @return the result of the request or <code>null</code>
	 * @throws Throwable
	 * <ul>
	 * 		<li>any exception or error that occurs during the execution is forwarded to the request handler</li>
	 * </ul>
	 * @since 1.0
	 */
	protected abstract T execute() throws Throwable;

}
