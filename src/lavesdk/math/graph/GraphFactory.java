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


/**
 * Factory class to create specific vertices and edges for a graph.
 * 
 * @see DefaultGraphFactory
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public abstract class GraphFactory<V extends Vertex, E extends Edge> {
	
	/**
	 * Creates a new vertex.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The method may not return an invalid object that means in general <code>null</code>. Otherwise
	 * no vertices can be created!
	 * 
	 * @param caption the default caption (that is an unique index value for the current graph)
	 * @return the vertex
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if caption is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public abstract V createVertex(final String caption) throws IllegalArgumentException;
	
	/**
	 * Creates a new edge with a weight of zero.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The method may not return an invalid object that means in general <code>null</code>. Otherwise
	 * no edges can be created!
	 * 
	 * @param predecessor the predecessor of the edge
	 * @param successor the successor of the edge
	 * @return the edge
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public abstract E createEdge(final V predecessor, final V successor) throws IllegalArgumentException;
	
	/**
	 * Creates a new edge.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The method may not return an invalid object that means in general <code>null</code>. Otherwise
	 * no edges can be created!
	 * 
	 * @param predecessor the predecessor of the edge
	 * @param successor the successor of the edge
	 * @param directed flag that indicates whether the edge should be directed (<code>true</code>) or undirected (<code>false</code>) (<b>has only an effect in mixed graphs otherwise the type of the edge is predefined</b>)
	 * @return the edge
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public abstract E createEdge(final V predecessor, final V successor, final boolean directed) throws IllegalArgumentException;
	
	/**
	 * Creates a new edge.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The method may not return an invalid object that means in general <code>null</code>. Otherwise
	 * no edges can be created!
	 * 
	 * @param predecessor the predecessor of the edge
	 * @param successor the successor of the edge
	 * @param weight the weight of the edge
	 * @return the edge
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public abstract E createEdge(final V predecessor, final V successor, final float weight) throws IllegalArgumentException;
	
	/**
	 * Creates a new edge.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The method may not return an invalid object that means in general <code>null</code>. Otherwise
	 * no edges can be created!
	 * 
	 * @param predecessor the predecessor of the edge
	 * @param successor the successor of the edge
	 * @param weight the weight of the edge
	 * @param directed flag that indicates whether the edge should be directed (<code>true</code>) or undirected (<code>false</code>) (<b>has only an effect in mixed graphs otherwise the type of the edge is predefined</b>)
	 * @return the edge
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public abstract E createEdge(final V predecessor, final V successor, final boolean directed, final float weight) throws IllegalArgumentException;

}
