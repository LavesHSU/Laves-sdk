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
 * Interface:	AlgorithmExerciseController
 * Task:		The controller of an exercise
 * Created:		27.02.14
 * LastChanges:	26.03.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm;

/**
 * Controller of an {@link AlgorithmExercise}.
 * <br><br>
 * <b>Attention</b>:<br>
 * This interface may only be used by classes or interfaces of the LAVESDK or more precisely from the inside of this package!<br>
 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS INTERFACE</i>!
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
interface AlgorithmExerciseController extends AlgorithmExerciseHandler {
	
	/**
	 * Wakes up the controller to continue with processing.
	 * 
	 * @since 1.0
	 */
	public void wakeUp();
	
	/**
	 * Gets the provider of the exercises.
	 * 
	 * @return the exercise provider
	 * @since 1.0
	 */
	public AlgorithmExerciseProvider getExerciseProvider();

}
