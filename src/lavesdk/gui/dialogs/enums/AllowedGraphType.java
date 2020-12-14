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

package lavesdk.gui.dialogs.enums;

/**
 * Allowed graph types a dialog can create:
 * <ul>
 * 		<li>{@link #DIRECTED_ONLY}</li>
 * 		<li>{@link #UNDIRECTED_ONLY}</li>
 * 		<li>{@link #BOTH}</li>
 * </ul>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public enum AllowedGraphType {
	
	/** the user cannot change the graph type (graph type is predefined and directed) */
	DIRECTED_ONLY,

	/** the user cannot change the graph type (graph type is predefined and undirected) */
	UNDIRECTED_ONLY,
	
	/** the user can change the graph type (graph type can be directed or undirected) */
	BOTH

}
