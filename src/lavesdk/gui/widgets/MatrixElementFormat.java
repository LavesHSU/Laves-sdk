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
 * Class:		MatrixElementFormat
 * Task:		Format and parse matrix elements
 * Created:		30.01.14
 * LastChanges:	30.01.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

/**
 * The format of a matrix element in a {@link MatrixEditor}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <T> the type of the matrix element
 */
public abstract class MatrixElementFormat<T> {
	
	/**
	 * Formats the given element.
	 * 
	 * @param element the element or <code>null</code> for a <i>null</i> element
	 * @return the text representation of the element or an empty string if the element cannot be formatted
	 * @since 1.0
	 */
	public abstract String format(T element);
	
	/**
	 * Parses the given text representation to a concrete element.
	 * 
	 * @param element the text representation of the element
	 * @return the element or <code>null</code> if the element could not be parsed
	 * @since 1.0
	 */
	public abstract T parse(String element);

}
