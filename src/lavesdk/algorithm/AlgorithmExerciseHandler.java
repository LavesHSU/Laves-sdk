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
 * Interface:	AlgorithmExerciseHandler
 * Task:		The handler of an exercise mode
 * Created:		26.03.14
 * LastChanges:	24.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm;

import lavesdk.algorithm.plugin.PluginHost;

/**
 * Handler of an exercise mode that processes {@link AlgorithmExercise}s.
 * 
 * @see AlgorithmExerciseController
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public interface AlgorithmExerciseHandler {
	
	/**
	 * Indicates whether the exercise mode is enabled.
	 * 
	 * @return <code>true</code> if the exercise mode is enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isExerciseModeEnabled();
	
	/**
	 * Sets whether the exercise mode should be enabled.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This switches the {@link AlgorithmExerciseProvider} of the exercise mode to invisible.
	 * 
	 * @param enabled <code>true</code> if the exercise mode should be enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setExerciseModeEnabled(final boolean enabled);
	
	/**
	 * Gets the host of the exercise handler.
	 * 
	 * @return the host or <code>null</code> if no host is available
	 * @since 1.0
	 */
	public PluginHost getHost();

}
