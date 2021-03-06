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

package lavesdk.math.graph.enums;

/**
 * The type of a graph.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public enum Type {
	
	/** the type of the graph is directed that means the graph can only contain directed edges */
	DIRECTED,

	/** the type of the graph is undirected that means the graph can only contain undirected edges */
	UNDIRECTED,

	/** the type of the graph is mixed that means the graph can contain directed and undirected edges */
	MIXED

}
