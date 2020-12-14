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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a walk in a {@link Graph} using the identifiers of the vertices.
 * <br><br>
 * <b>Definition</b>:<br>
 * A walk is any route through a graph from vertex to vertex along edges. A walk can end on the same vertex on which it began or on
 * a different vertex. A walk can travel over any edge and any vertex any number of times.
 * <br><br>
 * <b>Serialize a walk</b>:<br>
 * Use {@link WalkByID} to serialize a {@link Walk}. If you deserialize a {@link WalkByID} keep in mind to set the associated graph with
 * {@link #setGraph(Graph)} otherwise the walk is not functioning any more.
 * <br><br>
 * Use {@link #cast()} to convert this walk using vertex identifiers in a {@link Walk} using concrete {@link Vertex}s.
 * 
 * @see PathByID
 * @see TrailByID
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <V> the type of the vertices
 */
public class WalkByID<V extends Vertex> implements Cloneable, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** the list of identifiers of the vertices of the walk */
	protected final List<Integer> vertices;
	/** the associated graph */
	protected transient Graph<V, ? extends Edge> graph;
	/** the weight of the walk */
	private float weight;
	
	/**
	 * Creates an empty walk.
	 * 
	 * @param graph the graph its vertices can be part of this walk
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public WalkByID(final Graph<V, ? extends Edge> graph) throws IllegalArgumentException {
		this(graph, (List<Integer>)null);
	}
	
	/**
	 * Creates a walk based on a predefined walk.
	 * 
	 * @param graph the graph its vertices can be part of this walk
	 * @param walk the predefined walk or <code>null</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if the predefined walk is not a correct one</li>
	 * </ul>
	 * @since 1.0
	 */
	public WalkByID(final Graph<V, ? extends Edge> graph, final Integer[] walk) throws IllegalArgumentException {
		this(graph, toList(walk));
	}
	
	/**
	 * Creates a walk based on a predefined walk.
	 * 
	 * @param graph the graph its vertices can be part of this walk
	 * @param walk the predefined walk or <code>null</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if the predefined walk is not a correct one</li>
	 * </ul>
	 * @since 1.0
	 */
	public WalkByID(final Graph<V, ? extends Edge> graph, final List<Integer> walk) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.vertices = new ArrayList<Integer>();
		this.graph = graph;
		this.weight = 0.0f;
		
		// add the vertices to the walk
		if(walk != null)
			for(Integer v : walk)
				add(v);
	}
	
	/**
	 * Sets the associated graph of the walk.
	 * <br><br>
	 * This has to be done every time a serialized {@link WalkByID} is deserialized from a byte stream because the associated graph
	 * can not be serialized. If you do not set the associated graph after deserialization the walk is not functioning any more.
	 * 
	 * @param graph the graph its vertices can be part of the walk (<b>ensure that this is the one you have used to create the walk</b>)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setGraph(final Graph<V, ? extends Edge> graph) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.graph = graph;
	}
	
	/**
	 * Adds a new vertex to the walk.
	 * 
	 * @param vertexID the id of the vertex
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the walk does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if there is no vertex in the associated graph with the given id</li>
	 * 		<li>if there is no edge between the last added vertex and the new one (look at the definition of a {@link WalkByID})</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final int vertexID) throws UnsupportedOperationException, IllegalArgumentException {
		if(graph == null)
			throw new UnsupportedOperationException("the walk does not have an associated graph");
		else if(graph.getVertexByID(vertexID) == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final Integer lastVertex = (vertices.size() > 0) ? vertices.get(vertices.size() - 1) : null;
		
		if(lastVertex != null && graph.getEdge(lastVertex.intValue(), vertexID) == null)
			throw new IllegalArgumentException("there is no edge between the given vertex and the last added vertex");
		
		vertices.add(vertexID);
		
		// the walk is modified so update the weight
		updateWeight();
	}
	
	/**
	 * Adds a new vertex to the walk.
	 * 
	 * @param vertex the vertex
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the walk does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if vertex is null</li>
	 * 		<li>if there is no vertex in the associated graph with the given id</li>
	 * 		<li>if there is no edge between the last added vertex and the new one (look at the definition of a {@link WalkByID})</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final V vertex) throws UnsupportedOperationException, IllegalArgumentException {
		if(vertex == null)
			throw new IllegalArgumentException("No valid argument!");
		
		add(vertex.getID());
	}
	
	/**
	 * Removes the last vertex of the walk.
	 * 
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the walk does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @since 1.0
	 */
	public void removeLast() throws UnsupportedOperationException {
		if(graph == null)
			throw new UnsupportedOperationException("the walk does not have an associated graph");
		
		if(vertices.size() > 0)
			vertices.remove(vertices.size() - 1);
		
		// the walk is modified so update the weight
		updateWeight();
	}
	
	/**
	 * The length of the walk meaning the number of <b>edges</b>.
	 * 
	 * @return the length of the walk
	 * @since 1.0
	 */
	public int length() {
		return (vertices.size() > 0) ? vertices.size() - 1 : 0;
	}
	
	/**
	 * Gets a vertex identifier at the specified index.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * {@link #length()} returns the number of <b>edges</b> meaning that <code>get(length())</code> is the last vertex of the walk.
	 * 
	 * @param index the index
	 * @return the vertex id
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the walk does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index > length()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public Integer get(final int index) throws UnsupportedOperationException, IndexOutOfBoundsException {
		if(graph == null)
			throw new UnsupportedOperationException("the walk does not have an associated graph");
		
		return vertices.get(index);
	}
	
	/**
	 * Indicates whether the specified vertex is on this walk.
	 * 
	 * @param vertexID the vertex id
	 * @return <code>true</code> if the vertex is on this walk otherwise <code>false</code>
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the walk does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean contains(final int vertexID) throws UnsupportedOperationException {
		if(graph == null)
			throw new UnsupportedOperationException("the walk does not have an associated graph");
		
		return vertices.contains(vertexID);
	}
	
	/**
	 * Indicates whether the specified vertex is on this walk.
	 * 
	 * @param vertex the vertex
	 * @return <code>true</code> if the vertex is on this walk otherwise <code>false</code>
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the walk does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean contains(final V vertex) throws UnsupportedOperationException {
		return (vertex != null && contains(vertex.getID()));
	}
	
	/**
	 * Indicates whether the walk contains the specified edge or more precisely whether the walk contains
	 * the predecessor and the successor of the edge regarding the direction of the edge.
	 * 
	 * @param edge the edge
	 * @return <code>true</code> if the walk contains the specified edge otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean contains(final Edge edge) {
		if(edge == null || edge.getGraph() != graph)
			return false;
		else {
			int v;
			int u;
			
			// look whether the walk contains the predecessor and successor of the specified edge
			for(int i = 0; i < vertices.size() - 1; i++) {
				v = vertices.get(i);
				u = vertices.get(i + 1);
				
				// if the edge is directed then the predecessor and successor must be one after another and if the edge is undirected
				// it may be that v is the successor and u the predecessor or vice versa
				if(edge.isDirected() && v == edge.getPredecessor().getID() && u == edge.getSuccessor().getID())
					return true;
				else if(!edge.isDirected() && (v == edge.getPredecessor().getID() && u == edge.getSuccessor().getID() || v == edge.getSuccessor().getID() && u == edge.getPredecessor().getID()))
					return true;
			}
			
			return false;
		}
	}
	
	/**
	 * Indicates whether the walk is closed meaning the walk ends on the same vertex on which it began.
	 * 
	 * @return <code>true</code> if the walk is closed otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isClosed() {
		return (vertices.size() > 1) ? vertices.get(0) == vertices.get(vertices.size() - 1) : false;
	}
	
	/**
	 * Gets the weight (or cost) of the walk which is the sum of the weights of the traversed edges.
	 * 
	 * @return the weight of the walk
	 * @since 1.0
	 */
	public float getWeight() {
		return weight;
	}
	
	/**
	 * Converts this walk using vertex identifiers in a {@link Walk} with concrete {@link Vertex}s.
	 * 
	 * @return the walk
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the walk does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the walk cannot be cast because the associated graph was modified</li>
	 * </ul>
	 * @since 1.0
	 */
	public Walk<V> cast() throws UnsupportedOperationException, IllegalArgumentException {
		if(graph == null)
			throw new UnsupportedOperationException("the walk does not have an associated graph");
		
		final Walk<V> w = new Walk<V>(graph);
		
		for(Integer id : vertices)
			w.add(graph.getVertexByID(id.intValue()));
		
		return w;
	}
	
	/**
	 * Gets the walk as a list.
	 * 
	 * @return the walk as a list of its vertex identifiers
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> asList() {
		return (List<Integer>)((ArrayList<Integer>)vertices).clone();
	}
	
	/**
	 * Indicates whether this walk equals the specified one.
	 * 
	 * @param o another walk of the same vertex type that should be compared with this walk
	 * @return <code>true</code> if the walks are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if(o instanceof WalkByID) {
			try {
				return equals((WalkByID<V>)o);
			}
			catch(ClassCastException e) {
				return false;
			}
		}
		else
			return false;
	}
	
	/**
	 * Indicates whether this walk equals the specified one.
	 * 
	 * @param w another walk that should be compared with this walk
	 * @return <code>true</code> if the walks are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean equals(final WalkByID<V> w) {
		return (w != null) ? this.vertices.equals(w.vertices) : false;
	}
	
	/**
	 * Gets a shallow copy of this walk (the identifiers of the vertices in the walk are not cloned).
	 * 
	 * @return a clone of this walk
	 * @since 1.0
	 */
	@Override
	public WalkByID<V> clone() {
		return new WalkByID<V>(graph, vertices);
	}
	
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		boolean delimiter = false;
		Vertex v;
		s.append("(");
		
		for(Integer id : vertices) {
			if(delimiter)
				s.append(", ");
			
			v = graph.getVertexByID(id);
			if(v != null)
				s.append(v.getCaption());
			delimiter = true;
		}
		s.append(")");
		
		return s.toString();
	}
	
	/**
	 * Converts the given array to a list.
	 * 
	 * @param array the array
	 * @return the list
	 * @since 1.0
	 */
	protected static List<Integer> toList(final Integer[] array) {
		if(array == null)
			return null;
		
		final List<Integer> list = new ArrayList<Integer>(array.length);
		
		for(Integer i : array)
			list.add(i);
		
		return list;
			
	}
	
	/**
	 * Updates the weight of the walk.
	 * <br><br>
	 * This has to be done each time the walk becomes modified.
	 * 
	 * @since 1.0
	 */
	protected void updateWeight() {
		Edge e;
		
		weight = 0.0f;
		
		for(int i = 1; i < vertices.size(); i++) {
			e = graph.getEdge(vertices.get(i - 1), vertices.get(i));
			if(e != null)
				weight += e.getWeight();
		}
	}

}
