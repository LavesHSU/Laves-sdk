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
 * Class:		Path
 * Task:		Representation of a path in a graph
 * Created:		08.04.14
 * LastChanges:	20.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph;

import java.util.List;

/**
 * Represents a path in a {@link Graph}.
 * <br><br>
 * <b>Definition</b>:<br>
 * A path is a {@link Walk} that does not include any vertex twice, except that its first vertex might be the same as its last.
 * <br><br>
 * Use {@link #cast()} to convert this path using concrete {@link Vertex}s in a {@link PathByID} using vertex identifiers.
 * <br><br>
 * If you want to serialize a path please look at {@link PathByID}.
 * 
 * @see Walk
 * @see Trail
 * @see PathByID
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <V> the type of the vertices
 */
public class Path<V extends Vertex> extends Walk<V> {
	
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
	public Path(final Graph<V, ? extends Edge> graph) throws IllegalArgumentException {
		this(graph, (List<V>)null);
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
	public Path(final Graph<V, ? extends Edge> graph, final V[] path) throws IllegalArgumentException {
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
	public Path(final Graph<V, ? extends Edge> graph, final List<V> path) throws IllegalArgumentException {
		super(graph, path);
	}
	
	/**
	 * Adds a new vertex to the path.
	 * 
	 * @param vertex the vertex
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if vertex is null</li>
	 * 		<li>if vertex is not contained in the associated graph</li>
	 * 		<li>if there is no edge between the last added vertex and the new one (look at the definition of a {@link Path})</li>
	 * 		<li>if the path is already closed (meaning a circle) (look at the definition of a {@link Path})</li>
	 * 		<li>if the path already contains the vertex (look at the definition of a {@link Path})</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void add(final V vertex) throws IllegalArgumentException {
		super.add(vertex);
	}
	
	/**
	 * Adds a new vertex to the path.
	 * 
	 * @param index the index at which the vertex should be inserted
	 * @param vertex the vertex
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if vertex is null</li>
	 * 		<li>if vertex is not contained in the associated graph</li>
	 * 		<li>if there is no edge between the last added vertex and the new one (look at the definition of a {@link Path})</li>
	 * 		<li>if the path is already closed (meaning a circle) (look at the definition of a {@link Path})</li>
	 * 		<li>if the path already contains the vertex (look at the definition of a {@link Path})</li>
	 * </ul>
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of bounds</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void add(final int index, final V vertex) throws IllegalArgumentException, IndexOutOfBoundsException {
		super.add(index, vertex);
	}
	
	/**
	 * <b>It is not possible to insert another path!</b>
	 * 
	 * @param w the path
	 * @param back <code>true</code> if the path should be inserted at the last occurrence of the starting vertex or <code>false</code> for the first occurrence
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if this method is invoked</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void insert(Walk<V> w, boolean back) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("it is not possible to insert another path in this path");
	}
	
	/**
	 * <b>It is not possible to insert another path!</b>
	 * 
	 * @param w the path
	 * @param index the index of the starting vertex of w in this path at which the path w should be inserted (example this = <code>v1,v2,v3,v2,v4,v1</code>, w = <code>v2,v5,v2</code>, so w can be inserted at occurrence 1 or 2 of v2)
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if this method is invoked</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void insert(Walk<V> w, int index) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("it is not possible to insert another path in this path");
	}
	
	/**
	 * Removes the last vertex of the path.
	 * 
	 * @since 1.0
	 */
	@Override
	public void removeLast() {
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
	 * Converts this path using concrete {@link Vertex}s in a {@link PathByID} using vertex identifiers.
	 * 
	 * @return the path
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the associated graph was modified meaning vertices of the path were removed</li>
	 * </ul>
	 * @since 1.0
	 */
	public PathByID<V> cast() throws IllegalArgumentException {
		final PathByID<V> p = new PathByID<V>(graph);
		
		for(V v : vertices)
			p.add(v.getID());
		
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
		if(o instanceof Path) {
			try {
				return equals((Path<V>)o);
			}
			catch(ClassCastException e) {
				return false;
			}
		}
		else
			return false;
	}
	
	/**
	 * Indicates whether this path equals the specified one.
	 * 
	 * @param p another path that should be compared with this path
	 * @return <code>true</code> if the paths are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean equals(final Path<V> p) {
		return (p != null) ? this.vertices.equals(p.vertices) : false;
	}
	
	/**
	 * Gets a shallow copy of this path (the vertices in the path are not cloned).
	 * 
	 * @return a clone of this path
	 * @since 1.0
	 */
	@Override
	public Path<V> clone() {
		return new Path<V>(graph, vertices);
	}
	
	/**
	 * Adds a new vertex to the path at a specified index.
	 * 
	 * @param index the index at which the vertex should be added
	 * @param vertex the vertex
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if vertex is null</li>
	 * 		<li>if vertex is not contained in the associated graph</li>
	 * 		<li>if there is no edge between the predecessor and the new vertex or the successor and the new vertex (look at the definition of a {@link Path})</li>
	 * 		<li>if the path is already closed (meaning a cycle) and it is tried to add a vertex at the end of the path (look at the definition of a {@link Path})</li>
	 * 		<li>if the path already contains the vertex (look at the definition of a {@link Path})</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	protected void addImpl(int index, V vertex) throws IllegalArgumentException {
		final boolean addToTheEnd = (index == vertices.size());
		final V firstVertex = (vertices.size() > 0) ? vertices.get(0) : null;
		
		// a path can only contain different vertices except the first one meaning the path is a circle
		if(firstVertex == vertex && addToTheEnd && !closed)
			closed = true;
		else if(addToTheEnd && closed)
			throw new IllegalArgumentException("the path is closed (meaning a cycle) so it is not possible to add further vertices at the end of the path");
		else if(vertices.contains(vertex))
			throw new IllegalArgumentException("the path already contains the vertex " + vertex);
		
		super.addImpl(index, vertex);
	}

}
