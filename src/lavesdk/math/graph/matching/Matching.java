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

package lavesdk.math.graph.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lavesdk.math.Set;
import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Graph;
import lavesdk.math.graph.Vertex;
import lavesdk.utils.GraphUtils;

/**
 * Represents a matching.
 * <br><br>
 * <b>Definition</b>:<br>
 * Given a graph <code>G = (V, E)</code>, a matching <code>M</code> is a subset of <code>E</code> that is, no two edges share a common vertex.<br>
 * A vertex of <code>V</code> is matched (or saturated) if it is an endpoint of one of the edges in the matching otherwise the vertex is unmatched.
 * <br><br>
 * Use {@link #cast()} to convert this matching using concrete {@link Edge}s in a {@link MatchingByID} using edge indentifiers.
 * <br><br>
 * <b>Augmenting path</b>:<br>
 * Use {@link GraphUtils#isAugmentingPath(lavesdk.math.graph.Path, Matching)} to check whether a path is an augmenting path on a given matching.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <E> the type of the edges
 */
public class Matching<E extends Edge> extends Set<E> {
	
	private static final long serialVersionUID = 1L;
	
	/** the associated graph */
	private final Graph<? extends Vertex, E> graph;
	/** the saturated vertices */
	private final List<Vertex> vertices;
	
	/**
	 * Creates an empty matching.
	 * 
	 * @param graph the graph its edges can be part of the matching
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Matching(final Graph<? extends Vertex, E> graph) throws IllegalArgumentException {
		super();
		
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.graph = graph;
		this.vertices = new ArrayList<Vertex>();
	}
	
	/**
	 * Creates a matching based on another matching meaning the elements of the base matching are added to this matching.
	 * 
	 * @param m the matching as the base
	 * @since 1.0
	 */
	public Matching(final Matching<E> m) {
		super();
		
		this.graph = m.graph;
		this.vertices = new ArrayList<Vertex>();
		
		// add all elements of the base matching
		for(E e : m)
			add(e);
	}
	
	/**
	 * Adds an edge to the matching.
	 * 
	 * @param edge the edge
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if edge is null</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the associated graph does not contain the given edge</li>
	 * 		<li>if the predecessor of the edge is already matched meaning an endpoint of another edge in the matching</li>
	 * 		<li>if the successor of the edge is already matched meaning an endpoint of another edge in the matching</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean add(final E edge) throws NullPointerException, IllegalArgumentException {
		if(!graph.contains(edge))
			throw new IllegalArgumentException("No valid argument");
		else if(vertices.contains(edge.getPredecessor()))
			throw new IllegalArgumentException("the predecessor of the edge is already matched meaning an endpoint of another edge in the matching");
		else if(vertices.contains(edge.getSuccessor()))
			throw new IllegalArgumentException("the successor of the edge is already matched meaning an endpoint of another edge in the matching");
		
		if(super.add(edge)) {
			vertices.add(edge.getPredecessor());
			vertices.add(edge.getSuccessor());
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Adds the edge between the vertex 1 and 2 to the matching.
	 * 
	 * @param v1 the vertex 1
	 * @param v2 the vertex 2
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if edge is null meaning there is no edge between v1 and v2</li>
	 * 		<li>if the predecessor of the edge is already matched meaning an endpoint of another edge in the matching</li>
	 * 		<li>if the successor of the edge is already matched meaning an endpoint of another edge in the matching</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final Vertex v1, final Vertex v2) throws IllegalArgumentException {
		add(graph.getEdge(v1.getID(), v2.getID()));
	}
	
	@Override
	public boolean remove(Object o) {
		if(super.remove(o) && o instanceof Edge) {
			final Edge e = (Edge)o;
			
			// if an edge is removed from the matching the matched vertices have to be removed too
			vertices.remove(e.getPredecessor());
			vertices.remove(e.getSuccessor());
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		if(c == null)
			return false;
		
		final List<Vertex> vertexIntersection = new ArrayList<Vertex>();
		
		// define the intersection of the matched vertex set meaning get all vertices that retain in the set
		// which are the vertices of the edges that retain in the matching
		for(Object o : c) {
			if(o instanceof Edge) {
				final Edge e = (Edge)o;
				vertexIntersection.add(e.getPredecessor());
				vertexIntersection.add(e.getSuccessor());
			}
		}
		
		return super.retainAll(c) && vertices.removeAll(vertexIntersection);
	}
	
	/**
	 * Gets the number of matched (saturated) vertices.
	 * 
	 * @return the number of matched vertices
	 * @since 1.0
	 */
	public int getVertexCount() {
		return vertices.size();
	}
	
	/**
	 * Gets a matched vertex.
	 * 
	 * @param index the index
	 * @return the vertex
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getVertexCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public Vertex getVertex(final int index) throws IndexOutOfBoundsException {
		return vertices.get(index);
	}
	
	/**
	 * Indicates whether the specified vertex is matched (saturated).
	 * 
	 * @param v the vertex
	 * @return <code>true</code> if the given vertex is an endpoint of an edge in the matching otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if v is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean isMatched(final Vertex v) throws IllegalArgumentException {
		if(v == null)
			throw new IllegalArgumentException("No valid argument!");
		
		return vertices.contains(v);
	}
	
	/**
	 * Converts this matching using concrete {@link Edge}s in a {@link MatchingByID} using edge identifiers.
	 * 
	 * @return the matching
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the associated graph was modified meaning edges of the matching were removed</li>
	 * </ul>
	 * @since 1.0
	 */
	public MatchingByID<E> cast() {
		final MatchingByID<E> m = new MatchingByID<E>(graph);
		
		for(E e : this)
			m.add(e.getID());
		
		return m;
	}
	
	/**
	 * Gets a shallow copy of this matching (the elements in the matching are not cloned).
	 * 
	 * @return a clone of this matching
	 * @since 1.0
	 */
	@Override
	public Matching<E> clone() {
		return new Matching<E>(this);
	}
	
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		boolean delimiter = false;
		s.append("{");
		
		for(E e : this) {
			if(delimiter)
				s.append(", ");
			
			s.append("(" + e.getPredecessor().getCaption() + ", " + e.getSuccessor().getCaption() + ")");
			delimiter = true;
		}
		s.append("}");
		
		return s.toString();	
	}

}
