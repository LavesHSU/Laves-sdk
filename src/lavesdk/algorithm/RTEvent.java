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
 * Class:		RTEvent
 * Task:		Event of an algorithm runtime environment
 * Created:		14.11.13
 * LastChanges:	24.02.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm;

/**
 * Represents the data of a runtime event which is provided by a {@link RTEListener}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class RTEvent {
	
	/** the step id of the executing step */
	public final int executingStepID;
	/** indicates if the event should be done or canceled */
	public boolean doit;
	
	/**
	 * Creates a new event.
	 * 
	 * @param executingStepID the step id of the executing step
	 * @since 1.0
	 */
	public RTEvent(final int executingStepID) {
		this.executingStepID = executingStepID;
		this.doit = true;
	}

}
