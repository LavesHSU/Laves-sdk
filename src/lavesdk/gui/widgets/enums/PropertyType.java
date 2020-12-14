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

package lavesdk.gui.widgets.enums;

import lavesdk.gui.widgets.Property;

/**
 * The type of a {@link Property}.
 * <br><br>
 * Available types are:
 * <ul>
 * 		<li>{@link #TEXT}</li>
 * 		<li>{@link #NUMERIC}</li>
 * 		<li>{@link #LIST}</li>
 * 		<li>{@link #BOOLEAN}</li>
 * 		<li>{@link #COLOR}</li>
 * </ul>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public enum PropertyType {
	
	/** the property is a text property that means the result is a string */
	TEXT,
	
	/** the property is numeric that means the result is a number and the user can only input a numeric value */
	NUMERIC,
	
	/** the property can take on multiple values and the values are represented in a combobox */
	LIST,
	
	/** the property is a boolean value that means the value is represented as a checkbox */
	BOOLEAN,
	
	/** the property is a color value that means the value is represented as a color box and a choose button */
	COLOR
	
}