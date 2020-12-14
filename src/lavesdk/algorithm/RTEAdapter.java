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
 * Class:		RTEAdapter
 * Task:		Provides a default implementation of the listener
 * Created:		14.11.13
 * LastChanges:	24.02.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm;

/**
 * Provides a default implementation of {@link RTEListener}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class RTEAdapter implements RTEListener {

	@Override
	public void beforeStart(RTEvent e) {
	}

	@Override
	public void beforeResume(RTEvent e) {
	}

	@Override
	public void beforePause(RTEvent e) {
	}

	@Override
	public void onStop() {
	}

	@Override
	public void onRunning() {
	}

	@Override
	public void onPause() {
	}

}
