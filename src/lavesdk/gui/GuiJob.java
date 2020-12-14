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
 * Class:		GuiJob
 * Task:		Perform a job in the EDT of a graphical user interface
 * Created:		10.02.14
 * LastChanges:	14.02.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui;

/**
 * Performs a job in the event dispatch thread (EDT) of a graphical user interface (GUI).
 * <br><br>
 * A job that modifies a graphical user interface has to be executed in the EDT of the swing client.
 * Because Swing is <b>not thread-safe</b> it is not possible to update the GUI from a worker thread (thread that does background work or
 * time-consuming tasks) without hazard the consequences of unpredictable behavior in the GUI.<br>
 * Therefore you should use a {@link GuiJob} or a {@link GuiRequest} (if you need to get data from a GUI component) to perform <b>thread-safe</b>
 * tasks in the graphical user interface (in a safe way from a worker thread).<br>
 * In a normal case the job is executed <b>asynchronously</b> in the EDT but if you need to perform a synchronous job use {@link #GuiJob(boolean)}
 * and set the flag to <code>true</code> which means that the job waits for its completion.
 * <br><br>
 * <b>Example</b>:<br>
 * <pre>
 * EDT.execute(new GuiJob("setText") {
 *     protected void execute() throws Throwable {
 *         guiComponent.setText("Hello World!");
 *     }
 * });
 * </pre>
 * 
 * @see EDT
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public abstract class GuiJob extends GuiAction<Void> {

	/**
	 * Creates a new GUI job that is performed <b>asynchronously</b> in the EDT.
	 * 
	 * @since 1.0
	 */
	public GuiJob() {
		this(null);
	}
	
	/**
	 * Creates a new GUI job that is performed <b>asynchronously</b> in the EDT.
	 * 
	 * @param name the name of the job or <code>null</code> (the name is used to annotate exceptions that occur during the performance so it makes it a bit easier to allocate the point of failure)
	 * @since 1.0
	 */
	public GuiJob(final String name) {
		this(name, false);
	}
	
	/**
	 * Creates a new GUI job.
	 * 
	 * @param waitForCompletion <code>true</code> if the job should be performed <b>synchronously</b> in the EDT (meaning the job waits for its completion until all pending events in the EDT are processed) otherwise <code>false</code>
	 * @since 1.0
	 */
	public GuiJob(final boolean waitForCompletion) {
		this(null, waitForCompletion);
	}
	
	/**
	 * Creates a new GUI job.
	 * 
	 * @param name the name of the job or <code>null</code> (the name is used to annotate exceptions that occur during the performance so it makes it a bit easier to allocate the point of failure)
	 * @param waitForCompletion <code>true</code> if the job should be performed <b>synchronously</b> in the EDT (meaning the job waits for its completion until all pending events in the EDT are processed) otherwise <code>false</code>
	 * @since 1.0
	 */
	public GuiJob(final String name, final boolean waitForCompletion) {
		super(name, waitForCompletion);
	}

	@Override
	protected final Void perform() throws Throwable {
		execute();
		return null;
	}
	
	/**
	 * Executes the job.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The job should not be a time-consuming task otherwise the graphical user interface can freeze and is not responsive to user interactions.
	 * 
	 * @throws Throwable
	 * <ul>
	 * 		<li>any exception or error that occurs during the execution is forwarded to the job handler</li>
	 * </ul>
	 * @since 1.0
	 */
	protected abstract void execute() throws Throwable;

}
