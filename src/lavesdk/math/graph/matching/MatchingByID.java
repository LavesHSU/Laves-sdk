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
 * Class:		MatchingByID
 * Task:		Representation of a matching in a graph
 * Created:		10.04.14
 * LastChanges:	11.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph.matching;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lavesdk.math.Set;
import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Graph;
import lavesdk.math.graph.Vertex;

/**
 * Represents a matching using the identifiers of {@link Edge}s.
 * <br><br>
 * <b>Definition</b>:<br>
 * Given a graph <code>G = (V, E)</code>, a matching <code>M</code> is a subset of <code>E</code> that is, no two edges share a common vertex.<br>
 * A vertex of <code>V</code> is matched (or saturated) if it is an endpoint of one of the edges in the matching otherwise the vertex is unmatched.
 * <br><br>
 * <b>Serialize a matching</b>:<br>
 * Use {@link MatchingByID} to serialize a {@link Matching}. If you deserialize a {@link MatchingByID} keep in mind to set the associated graph with
 * {@link #setGraph(Graph)} otherwise the matching is not functioning any more.
 * <br><br>
 * Use {@link #cast()} to convert this matching using edge identifiers in a {@link Matching} using concrete {@link Edge}s.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <E> the type of the edges
 */
public class MatchingByID<E extends Edge> extends Set<Integer> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** the saturated vertices */
	private final List<Integer> vertices;
	/** the associated graph (may not be serialized) */
	private transient Graph<? extends Vertex, E> graph;
	
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
	public MatchingByID(final Graph<? extends Vertex, E> graph) throws IllegalArgumentException {
		super();
		
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.vertices = new ArrayList<Integer>();
		this.graph = graph;
	}
	
	/**
	 * Creates a matching based on another matching meaning the elements of the base matching are added to this matching.
	 * 
	 * @param m the matching as the base
	 * @since 1.0
	 */
	public MatchingByID(final MatchingByID<E> m) {
		this.vertices = new ArrayList<Integer>();
		this.graph = m.graph;
		
		for(Integer id : m)
			add(id);
	}
	
	/**
	 * Sets the associated graph of the matching.
	 * <br><br>
	 * This has to be done every time a serialized {@link MatchingByID} is deserialized from a byte stream because the associated graph
	 * can not be serialized. If you do not set the associated graph after deserialization the matching is not functioning any more.
	 * 
	 * @param graph the graph its edges can be part of the matching (<b>ensure that this is the one you have used to create the matching</b>)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setGraph(final Graph<? extends Vertex, E> graph) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.graph = graph;
	}
	
	/**
	 * Adds an edge to the matching.
	 * 
	 * @param edgeID the edge
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the matching does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if edgeID is null</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the associated graph does not contain the given edge</li>
	 * 		<li>if the predecessor of the edge is already matched meaning an endpoint of another edge in the matching</li>
	 * 		<li>if the successor of the edge is already matched meaning an endpoint of another edge in the matching</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean add(final Integer edgeID) throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {
		return add(edgeID.intValue());
	}
	
	/**
	 * Adds an edge to the matching.
	 * 
	 * @param edgeID the edge
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the matching does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the associated graph does not contain the given edge</li>
	 * 		<li>if the predecessor of the edge is already matched meaning an endpoint of another edge in the matching</li>
	 * 		<li>if the successor of the edge is already matched meaning an endpoint of another edge in the matching</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean add(final int edgeID) throws UnsupportedOperationException, IllegalArgumentException {
		if(graph == null)
			throw new UnsupportedOperationException("matching does not have an associated graph");
		
		final Edge e = graph.getEdgeByID(edgeID);
		
		if(e == null)
			throw new IllegalArgumentException("No valid argument");
		else if(vertices.contains(e.getPredecessor().getID()))
			throw new IllegalArgumentException("the predecessor of the edge is already matched meaning an endpoint of another edge in the matching");
		else if(vertices.contains(e.getSuccessor().getID()))
			throw new IllegalArgumentException("the successor of the edge is already matched meaning an endpoint of another edge in the matching");
		
		if(super.add(edgeID)) {
			vertices.add(e.getPredecessor().getID());
			vertices.add(e.getSuccessor().getID());
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Adds an edge to the matching.
	 * 
	 * @param edge the edge
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the matching does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
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
	public boolean add(final E edge) throws UnsupportedOperationException, NullPointerException, IllegalArgumentException {
		return add(edge.getID());
	}
	
	/**
	 * Adds the edge between the vertex 1 and 2 to the matching.
	 * 
	 * @param v1 the vertex 1
	 * @param v2 the vertex 2
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the matching does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if edge is null meaning there is no edge between v1 and v2</li>
	 * 		<li>if the predecessor of the edge is already matched meaning an endpoint of another edge in the matching</li>
	 * 		<li>if the successor of the edge is already matched meaning an endpoint of another edge in the matching</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final Vertex v1, final Vertex v2) throws UnsupportedOperationException, IllegalArgumentException {
		if(graph == null)
			throw new UnsupportedOperationException("matching does not have an associated graph");
		
		add(graph.getEdge(v1.getID(), v2.getID()));
	}
	
	/**
	 * Removes an edge from the matching.
	 * 
	 * @param o the id of the edge that should be removed from the matching
	 * @return <code>true</code> if the edge could be removed otherwise <code>false</code>
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the matching does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public boolean remove(Object o) throws UnsupportedOperationException {
		if(graph == null)
			throw new UnsupportedOperationException("matching does not have an associated graph");
		
		if(super.remove(o) && o instanceof Integer) {
			final Edge e = graph.getEdgeByID(((Integer)o).intValue());
			
			if(e == null)
				return false;
			
			// if an edge is removed from the matching the matched vertices have to be removed too
			vertices.remove(e.getPredecessor().getID());
			vertices.remove(e.getSuccessor().getID());
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Retains only the edges that are part of the given collection.
	 * 
	 * @param c the collection with the identifiers of the edges that should be retained
	 * @return <code>true</code> if the edges could be retained otherwise <code>false</code>
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the matching does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public boolean retainAll(Collection<?> c) throws UnsupportedOperationException {
		if(graph == null)
			throw new UnsupportedOperationException("matching does not have an associated graph");
		
		if(c == null)
			return false;
		
		final List<Integer> vertexIntersection = new ArrayList<Integer>();
		
		// define the intersection of the matched vertex set meaning get all vertices that retain in the set
		// which are the vertices of the edges that retain in the matching
		for(Object o : c) {
			if(o instanceof Integer) {
				final Edge e = graph.getEdgeByID(((Integer)o).intValue());
				if(e != null) {
					vertexIntersection.add(e.getPredecessor().getID());
					vertexIntersection.add(e.getSuccessor().getID());
				}
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
	 * Gets a matched vertex id.
	 * 
	 * @param index the index
	 * @return the vertex id
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the matching does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getVertexCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public int getVertexID(final int index) throws UnsupportedOperationException, IndexOutOfBoundsException {
		if(graph == null)
			throw new UnsupportedOperationException("matching does not have an associated graph");
		
		return vertices.get(index);
	}
	
	/**
	 * Indicates whether the specified vertex is matched (saturated).
	 * 
	 * @param v the vertex
	 * @return <code>true</code> if the given vertex is an endpoint of an edge in the matching otherwise <code>false</code>
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the matching does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if v is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean isMatched(final Vertex v) throws UnsupportedOperationException, IllegalArgumentException {
		if(v == null)
			throw new IllegalArgumentException("No valid argument!");
		
		return isMatched(v.getID());
	}
	
	/**
	 * Indicates whether the specified vertex is matched (saturated).
	 * 
	 * @param vertexID the vertex id
	 * @return <code>true</code> if the given vertex is an endpoint of an edge in the matching otherwise <code>false</code>
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the matching does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean isMatched(final int vertexID) throws UnsupportedOperationException, IllegalArgumentException {
		if(graph == null)
			throw new UnsupportedOperationException("matching does not have an associated graph");
		
		return vertices.contains(vertexID);
	}
	
	/**
	 * Translates this matching that uses edge identifiers into a {@link Matching} with concrete {@link Edge}s.
	 * 
	 * @return the matching
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the matching does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the matching cannot be cast because the associated graph was modified</li>
	 * </ul>
	 * @since 1.0
	 */
	public Matching<E> cast() throws UnsupportedOperationException, IllegalArgumentException {
		if(graph == null)
			throw new UnsupportedOperationException("matching does not have an associated graph");
		
		final Matching<E> m = new Matching<E>(graph);
		
		for(Integer id : this)
			m.add(graph.getEdgeByID(id.intValue()));
		
		return m;
	}
	
	/**
	 * Gets a shallow copy of this matching (the elements in the matching are not cloned).
	 * 
	 * @return a clone of this matching
	 * @since 1.0
	 */
	@Override
	public MatchingByID<E> clone() {
		return new MatchingByID<E>(this);
	}
	
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		boolean delimiter = false;
		Edge e;
		s.append("{");
		
		for(Integer i : this) {
			if(delimiter)
				s.append(", ");
			
			e = (graph != null) ? graph.getEdgeByID(i.intValue()) : null;
			
			if(e != null)
				s.append("(" + e.getPredecessor().getCaption() + ", " + e.getSuccessor().getCaption() + ")");
			
			delimiter = true;
		}
		s.append("}");
		
		return s.toString();	
	}

}
