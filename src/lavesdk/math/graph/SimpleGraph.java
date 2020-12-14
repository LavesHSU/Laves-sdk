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
 * Class:		SimpleGraph
 * Task:		Representation of a simple graph
 * Created:		11.09.13
 * LastChanges:	31.10.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph;

import lavesdk.math.graph.enums.Type;
import lavesdk.utils.GraphUtils;

/**
 * Represents a simple graph as a set of vertices and edges.
 * <br><br>
 * A simple graph is a graph that has no loops (edges connected at both ends to the same vertex) and
 * no more than one edge between any two different vertices.
 * 
 * @see Graph
 * @see GraphUtils
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class SimpleGraph<V extends Vertex, E extends Edge> extends Graph<V, E> {

	/**
	 * Creates a new simple graph.
	 * 
	 * @param directed <code>true</code> for a directed and <code>false</code> for an undirected simple graph
	 * @since 1.0
	 */
	public SimpleGraph(boolean directed) {
		super(directed ? Type.DIRECTED : Type.UNDIRECTED);
	}
	
	@Override
	protected final boolean isEdgeAllowed(E edge) {
		return (edge.getPredecessor() != edge.getSuccessor());
	}
	
	@Override
	protected final int containsEdge(E edge) {
		// do not allow that subclass change the simple graph to a multi graph
		return super.containsEdge(edge);
	}

}
