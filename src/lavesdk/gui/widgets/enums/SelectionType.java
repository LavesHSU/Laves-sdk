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

import lavesdk.gui.widgets.ExecutionTable;

/**
 * The type of the selection in an {@link ExecutionTable}.
 * <br><br>
 * Available types are:
 * <ul>
 * 		<li>{@link #NONE}</li>
 * 		<li>{@link #CELL}</li>
 * 		<li>{@link #ROW}</li>
 * 		<li>{@link #ROWS}</li>
 * </ul>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public enum SelectionType {
	
	/** no selection enabled */
	NONE,
	
	/** only one cell is selectable */
	CELL,
	
	/** single full row selection is enabled */
	ROW,
	
	/** multiple full row selection is enabled */
	ROWS

}
