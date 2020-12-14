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
 * Class:		AccessibleID
 * Task:		Make the id of a graph object accessible
 * Created:		15.05.14
 * LastChanges:	15.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph;

/**
 * Represents an accessible id of a {@link Graph} object like a {@link Vertex} or an {@link Edge}.
 * <br><br>
 * <b>Attention</b>:<br>
 * Each graph object has a local identifier meaning that the identifier is unique based on the related graph and context.
 * A {@link Graph} manages and allocates the identifiers to the objects internally. Using an {@link AccessibleID} it is possible
 * to change the identifier of a graph object but this has to be done carefully. So it is not recommended to use this class outside
 * of the LAVESDK although it is public, which has to do with the fact that it must be usable from other packages of the SDK.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class AccessibleID {
	
	/** the vertex as the graph object or <code>null</code> if the edge is used */
	private final Vertex vertex;
	/** the edge as the graph object or <code>null</code> if the vertex is used */
	private final Edge edge;
	/** the related graph */
	private final Graph<?, ?> graph;
	
	/**
	 * Creates a new accessible identifier based on a {@link Vertex}.
	 * 
	 * @param vertex the vertex its id should be modified
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if vertex is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public AccessibleID(final Vertex vertex) throws IllegalArgumentException {
		if(vertex == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.vertex = vertex;
		this.edge = null;
		this.graph = vertex.getGraph();
	}
	
	/**
	 * Creates a new accessible identifier based on an {@link Edge}.
	 * 
	 * @param edge the edge its id should be modified
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if edge is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public AccessibleID(final Edge edge) throws IllegalArgumentException {
		if(edge == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.vertex = null;
		this.edge = edge;
		this.graph = edge.getGraph();
	}
	
	/**
	 * Modifies the identifier of the related graph object.
	 * 
	 * @param newID the new identifier
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if newID is <code>< 1</code></li>
	 * 		<li>if the graph already contains an object (vertex or edge) with newID</li>
	 * </ul>
	 * @since 1.0
	 */
	public final void modify(final int newID) throws IllegalArgumentException {
		if(vertex != null)
			graph.modifyVertexID(vertex.getID(), newID);
		else if(edge != null)
			graph.modifyEdgeID(edge.getID(), newID);
	}

}
