/**
 * This is part of the LAVESDK - Logistics Algorithms Visualization and Education Software Development Kit.
 * 
 * Copyright (C) 2020 Jan Dornseifer & Department of Management Information Science, University of Siegen &
 *                    Department for Management Science and Operations Research, Helmut Schmidt University
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
 */

package lavesdk.math;

/**
 * A parser for the elements of a set in a string representation.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <E> the type of the elements
 */
public abstract class ElementParser<E> {
	
	/**
	 * Parses the given element into the concrete type.
	 * 
	 * @param element the element as a string
	 * @return the concrete element of the specified type
	 * @since 1.0
	 */
	public abstract E parse(final String element);

}
