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

package lavesdk.math.graph;

import lavesdk.math.graph.enums.Type;
import lavesdk.utils.GraphUtils;

/**
 * Represents a multi graph as a set of vertices and edges.
 * <br><br>
 * In a multi graph it is allowed to add more than one edge between any two vertices.
 * 
 * @see Graph
 * @see GraphUtils
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class MultiGraph<V extends Vertex, E extends Edge> extends Graph<V, E> {

	/**
	 * Creates a new multi graph.
	 * 
	 * @param directed <code>true</code> for a directed and <code>false</code> for an undirected multi graph
	 * @since 1.0
	 */
	public MultiGraph(boolean directed) {
		super(directed ? Type.DIRECTED : Type.UNDIRECTED);
	}
	
	@Override
	protected final int containsEdge(E edge) {
		// check for reference because you can have multiple edges between two vertices that have
		// equal attributes
		for(int i = 0; i < getSize(); i++)
			if(getEdge(i) == edge)
				return i;
		
		return -1;
	}

}
