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
 * Enum:		AlgorithmStartOption
 * Task:		Start options of an algorithm
 * Created:		30.04.14
 * LastChanges:	30.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.enums;

/**
 * The start options of an algorithm.
 * <br><br>
 * <b>Available options</b>:
 * <ul>
 * 		<li>{@link #NORMAL}</li>
 * 		<li>{@link #START_TO_FINISH}</li>
 * 		<li>{@link #PLAY_AND_PAUSE}</li>
 * </ul>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public enum AlgorithmStartOption {
	
	/** the default start option meaning the algorithm is executed using the current execution speed factor */
	NORMAL,
	
	/** the algorithm runs to its end in the fastest possible execution speed */
	START_TO_FINISH,
	
	/** the algorithm plays the current step and pauses after the step is executed */
	PLAY_AND_PAUSE

}
