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
 * Interface:	AlgorithmTextListener
 * Task:		Inform listeners about changes in an algorithm text
 * Created:		18.11.13
 * LastChanges:	18.11.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.text;

/**
 * Listener to listen to changes inside an {@link AlgorithmText}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public interface AlgorithmTextListener {
	
	/**
	 * Indicates that the structure of the text has changed which means that
	 * a new paragraph or step is added.
	 * 
	 * @since 1.0
	 */
	public void structureChanged();
	
	/**
	 * Indicates that the current active step of the text has changed.
	 * 
	 * @since 1.0
	 */
	public void executingStepChanged();

}
