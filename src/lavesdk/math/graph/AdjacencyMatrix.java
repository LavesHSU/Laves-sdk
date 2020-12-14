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
 * Class:		AdjacencyMatrix
 * Task:		Representation of an adjacency matrix of a graph
 * Created:		14.11.13
 * LastChanges:	14.10.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an adjacency matrix of a graph.
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public final class AdjacencyMatrix<V extends Vertex, E extends Edge> {
	
	/** the matrix data structure */
	private final Map<Integer, List<Entry>> matrix;
	
	/**
	 * Creates a new adjacency matrix with an initial row capacity of ten.
	 * 
	 * @since 1.0
	 */
	public AdjacencyMatrix() {
		this(10);
	}
	
	/**
	 * Creates a new adjacency matrix.
	 * 
	 * @param capacity the initial row capacity
	 * @since 1.0
	 */
	public AdjacencyMatrix(final int capacity) {
		matrix = new HashMap<Integer, List<Entry>>(capacity);
	}
	
	/**
	 * Adds an edge to the adjacency matrix.
	 * 
	 * @param edge the edge
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if edge is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final E edge) throws NullPointerException {
		add(edge, false);
		
		// if the edge is undirected then do the same reversed meaning to add the predecessor to the successor row
		if(!edge.isDirected())
			add(edge, true);
	}
	
	/**
	 * Removes an edge from the adjacency matrix.
	 * 
	 * @param edge the edge
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if edge is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void remove(final E edge) throws NullPointerException {
		remove(edge, false);
		
		// if the edge is undirected then do the same reversed meaning to remove the predecessor from the successor row
		if(!edge.isDirected())
			remove(edge, true);
	}
	
	/**
	 * Gets the edge between the two vertices.
	 * <br><br>
	 * Let <code>G = (V, E)</code> be a graph with <code>V</code> as the set of vertices and <code>E</code> as the set of edges and <code>m</code> be the
	 * adjacency matrix. then <code>m(i,k) = edge</code> if <code>(i,k)</code> is an element of <code>E</code> and <code>m(i,k) = null</code> if <code>(i,k)</code> is not
	 * an element of <code>E</code>.
	 * 
	 * @param v1 the id of the first vertex
	 * @param v2 the id of the second vertex
	 * @return the edge or <code>null</code> if there is no edge between v1 and v2 (<code>(v1,v2)</code> is not an element of <code>E</code>)
	 * @since 1.0
	 */
	public E get(final int v1, final int v2) {
		final List<Entry> row = matrix.get(v1);
		
		if(row == null)
			return null;
		
		// go through all entries of v1 and look for v2
		for(Entry e : row)
			if(e.vID == v2)
				return e.edge;
		
		return null;
	}
	
	/**
	 * Gets a list of all edges between the two vertices. The result can only have multiple edges
	 * if the graph is a multi graph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If a graph is of type undirected then <code>getAll(v1,v2) == getAll(v2,v1)</code> otherwise
	 * one has <code>getAll(v1,v2) != getAll(v2,v1)</code>.
	 * 
	 * @see #get(int, int)
	 * @param v1 the id of the first vertex
	 * @param v2 the id of the second vertex
	 * @return a list of all vertices which are between the specified vertices or <code>null</code> if there is no edge between the vertices
	 * @since 1.0
	 */
	public List<E> getAll(final int v1, final int v2) {
		final List<Entry> row = matrix.get(v1);
		
		if(row == null)
			return null;
		
		final List<E> all = new ArrayList<E>(row.size());
		for(Entry e : row)
			if(e.vID == v2)
				all.add(e.edge);
		
		return (all.size() == 0) ? null : all;
	}
	
	/**
	 * Gets the edge between the two vertices.
	 * <br><br>
	 * Let <code>G = (V, E)</code> be a graph with <code>V</code> as the set of vertices and <code>E</code> as the set of edges and <code>m</code> be the
	 * adjacency matrix. then <code>m(i,k) = edge</code> if <code>(i,k)</code> is an element of <code>E</code> and <code>m(i,k) = null</code> if <code>(i,k)</code> is not
	 * an element of <code>E</code>.
	 * 
	 * @param v1 the first vertex
	 * @param v2 the second vertex
	 * @return the edge or <code>null</code> if there is no edge between v1 and v2 (<code>(v1,v2)</code> is not an element of <code>E</code>)
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if v1 is null</li>
	 * 		<li>if v2 is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public E get(final V v1, final V v2) throws NullPointerException {
		return get(v1.getID(), v2.getID());
	}
	
	/**
	 * Gets a list of all edges between the two vertices. The result can only have multiple edges
	 * if the graph is a multi graph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If a graph is of type undirected then <code>getAll(v1,v2) == getAll(v2,v1)</code> otherwise
	 * one has <code>getAll(v1,v2) != getAll(v2,v1)</code>.
	 * 
	 * @see #get(Vertex, Vertex)
	 * @param v1 the first vertex
	 * @param v2 the second vertex
	 * @return a list of all edges which are between the specified vertices or <code>null</code> if there is no edge between the vertices
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if v1 is null</li>
	 * 		<li>if v2 is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public List<E> getAll(final V v1, final V v2) throws NullPointerException {
		return getAll(v1.getID(), v2.getID());
	}
	
	/**
	 * Updates the direction of the edge in the adjacency matrix.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This is only possible if the edge is modifying ({@link Edge#isModifying()}).
	 * 
	 * @param edge the edge its direction changed
	 * @since 1.0
	 */
	void updateDirection(final E edge) {
		if(!edge.isModifying())
			return;
		
		// if the edge becomes directed then remove the (reversed) undirected edge from successor -> predecessor
		// otherwise if the edge becomes undirected then add the reversed undirected edge from successor -> predecessor
		if(edge.isDirected())
			remove(edge, true);
		else
			add(edge, true);
	}
	
	/**
	 * Adds the edge to the adjacency matrix.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * Do not use this method from somewhere else then {@link #add(Edge)}! This is only a help method so to
	 * extend the adjacency matrix please invoke the previously named method.
	 * 
	 * @param edge the edge
	 * @param reverse <code>true</code> if the predecessor should be added to the successors row rather than vice versa
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if edge is null</li>
	 * </ul>
	 * @since 1.0
	 */
	private void add(final E edge, final boolean reverse) throws NullPointerException {
		final Vertex predecessor = reverse ? edge.getSuccessor() : edge.getPredecessor();
		final Vertex successor = reverse ? edge.getPredecessor() : edge.getSuccessor();
		
		// get the row of the predecessor in what the links are connected or create
		// a new row if the predecessor does not have one yet
		List<Entry> row = matrix.get(predecessor.getID());
		if(row == null) {
			row = new ArrayList<Entry>(5);
			matrix.put(predecessor.getID(), row);
		}
		
		// put edge to the row of the adjacency matrix
		row.add(new Entry(successor.getID(), edge));
	}
	
	/**
	 * Removes the edge from the adjacency matrix.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * Do not use this method from somewhere else then {@link #remove(Edge)}! This is only a help method so to
	 * reduce the adjacency matrix please invoke the previously named method.
	 * 
	 * @param edge the edge
	 * @param reverse <code>true</code> if the predecessor should be removed from the successors row rather than vice versa
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if edge is null</li>
	 * </ul>
	 * @since 1.0
	 */
	private void remove(final E edge, final boolean reverse) throws NullPointerException {
		final Vertex predecessor = reverse ? edge.getSuccessor() : edge.getPredecessor();
		final Vertex successor = reverse ? edge.getPredecessor() : edge.getSuccessor();
		
		// get the row of the predecessor in what the links are connected
		final List<Entry> row = matrix.get(predecessor.getID());
		if(row == null)
			return;
		
		// remove all entries with the specified edge from the row
		for(int i = row.size() - 1; i >= 0; i--)
			if(row.get(i).vID == successor.getID() && row.get(i).edge == edge)
				row.remove(i);
	}
	
	/**
	 * An entry in the adjacency matrix.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class Entry {
		
		/** the vertex id */
		public final int vID;
		/** the edge */
		public final E edge;
		
		/**
		 * Creates a new entry.
		 * 
		 * @param vID the vertex id
		 * @param edge the edge
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if edge is null</li>
		 * </ul>
		 */
		public Entry(final int vID, final E edge) throws IllegalArgumentException {
			if(edge == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.vID = vID;
			this.edge = edge;
		}
		
	}

}
