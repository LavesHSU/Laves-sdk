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
 * Class:		PathByID
 * Task:		Representation of a path of vertex identifiers in a graph
 * Created:		10.04.14
 * LastChanges:	11.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph;

import java.util.List;

/**
 * Represents a path in a {@link Graph} using the identifiers of the vertices.
 * <br><br>
 * <b>Definition</b>:<br>
 * A path is a {@link WalkByID} that does not include any vertex twice, except that its first vertex might be the same as its last.
 * <br><br>
 * <b>Serialize a path</b>:<br>
 * Use {@link PathByID} to serialize a {@link Path}. If you deserialize a {@link PathByID} keep in mind to set the associated graph with
 * {@link #setGraph(Graph)} otherwise the path is not functioning any more.
 * <br><br>
 * Use {@link #cast()} to convert this path using vertex identifiers in a {@link Path} using concrete {@link Vertex}s.
 * 
 * @see WalkByID
 * @see TrailByID
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <V> the type of the vertices
 */
public class PathByID<V extends Vertex> extends WalkByID<V> {
	
	private static final long serialVersionUID = 1L;
	
	/** indicates whether the path is closed */
	private boolean closed;
	
	/**
	 * Creates an empty path.
	 * 
	 * @param graph the graph its vertices can be part of this path
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public PathByID(final Graph<V, ? extends Edge> graph) throws IllegalArgumentException {
		this(graph, (List<Integer>)null);
	}
	
	/**
	 * Creates a path based on a predefined path.
	 * 
	 * @param graph the graph its vertices can be part of this path
	 * @param path the predefined path or <code>null</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if the predefined path is not a correct one</li>
	 * </ul>
	 * @since 1.0
	 */
	public PathByID(final Graph<V, ? extends Edge> graph, final Integer[] path) throws IllegalArgumentException {
		this(graph, toList(path));
	}
	
	/**
	 * Creates a path based on a predefined path.
	 * 
	 * @param graph the graph its vertices can be part of this path
	 * @param path the predefined path or <code>null</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if the predefined path is not a correct one</li>
	 * </ul>
	 * @since 1.0
	 */
	public PathByID(final Graph<V, ? extends Edge> graph, final List<Integer> path) throws IllegalArgumentException {
		super(graph, path);
	}
	
	/**
	 * Adds a new vertex to the path.
	 * 
	 * @param vertexID the id of the vertex
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the path does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if there is no vertex in the associated graph with the given id</li>
	 * 		<li>if there is no edge between the last added vertex and the new one (look at the definition of a {@link PathByID})</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final int vertexID) throws UnsupportedOperationException, IllegalArgumentException {
		final Integer firstVertex = (vertices.size() > 0) ? vertices.get(0) : null;
		
		// a path can only contain different vertices except the first one meaning the path is a circle
		if(firstVertex != null && firstVertex.intValue() == vertexID && !closed)
			closed = true;
		else if(closed)
			throw new IllegalArgumentException("the path is closed (meaning a circle) so it is not possible to add further vertices");
		else if(vertices.contains(vertexID))
			throw new IllegalArgumentException("the path already contains the vertex with the id " + vertexID);
		
		super.add(vertexID);
	}
	
	/**
	 * Removes the last vertex of the path.
	 * 
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the path does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void removeLast() throws UnsupportedOperationException {
		super.removeLast();
		
		// if the path was previously a circle then now it is no circle anymore
		if(closed)
			closed = false;
	}
	
	/**
	 * Indicates whether the path is a cycle meaning that the path begins and ends on the same vertex.
	 * 
	 * @return <code>true</code> if the path is a cycle otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isClosed() {
		return closed;
	}
	
	/**
	 * Converts this path using vertex identifiers in a {@link Path} with concrete {@link Vertex}s.
	 * 
	 * @return the path
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the walk does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the path cannot be cast because the associated graph was modified</li>
	 * </ul>
	 * @since 1.0
	 */
	public Path<V> cast() throws UnsupportedOperationException, IllegalArgumentException {
		if(graph == null)
			throw new UnsupportedOperationException("the walk does not have an associated graph");
		
		final Path<V> p = new Path<V>(graph);
		
		for(Integer id : vertices)
			p.add(graph.getVertexByID(id.intValue()));
		
		return p;
	}
	
	/**
	 * Indicates whether this path equals the specified one.
	 * 
	 * @param o another path of the same vertex type that should be compared with this path
	 * @return <code>true</code> if the paths are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if(o instanceof PathByID) {
			try {
				return equals((PathByID<V>)o);
			}
			catch(ClassCastException e) {
				return false;
			}
		}
		else
			return false;
	}
	
	/**
	 * Gets a shallow copy of this path (the identifiers of the vertices in the path are not cloned).
	 * 
	 * @return a clone of this path
	 * @since 1.0
	 */
	@Override
	public PathByID<V> clone() {
		return new PathByID<V>(graph, vertices);
	}
	
	/**
	 * Indicates whether this path equals the specified one.
	 * 
	 * @param p another path that should be compared with this path
	 * @return <code>true</code> if the paths are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean equals(final PathByID<V> p) {
		return (p != null) ? this.vertices.equals(p.vertices) : false;
	}

}
