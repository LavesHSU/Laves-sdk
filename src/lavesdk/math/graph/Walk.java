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
 * Class:		Walk
 * Task:		Representation of a walk in a graph
 * Created:		08.04.14
 * LastChanges:	21.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a walk in a {@link Graph}.
 * <br><br>
 * <b>Definition</b>:<br>
 * A walk is any route through a graph from vertex to vertex along edges. A walk can end on the same vertex on which it began (<b>closed walk</b>)
 * or on a different vertex (<b>open walk</b>). A walk can travel over any edge and any vertex any number of times.
 * <br><br>
 * Use {@link #cast()} to convert this walk using concrete {@link Vertex}s in a {@link WalkByID} using vertex identifiers.
 * <br><br>
 * If you want to serialize a walk please look at {@link WalkByID}.
 * 
 * @see Path
 * @see Trail
 * @see WalkByID
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <V> the type of the vertices
 */
public class Walk<V extends Vertex> implements Cloneable {
	
	/** the list of vertices of the walk */
	protected final List<V> vertices;
	/** the associated graph */
	protected final Graph<V, ? extends Edge> graph;
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
	public Walk(final Graph<V, ? extends Edge> graph) throws IllegalArgumentException {
		this(graph, (List<V>)null);
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
	public Walk(final Graph<V, ? extends Edge> graph, final V[] walk) throws IllegalArgumentException {
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
	public Walk(final Graph<V, ? extends Edge> graph, final List<V> walk) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.vertices = new ArrayList<V>();
		this.graph = graph;
		this.weight = 0.0f;
		
		// add the vertices to the walk
		if(walk != null)
			for(V v : walk)
				add(v);
	}
	
	/**
	 * Adds a new vertex to the walk.
	 * 
	 * @param vertex the vertex
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if vertex is null</li>
	 * 		<li>if vertex is not contained in the associated graph</li>
	 * 		<li>if there is no edge between the last added vertex and the new one (look at the definition of a {@link Walk})</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final V vertex) throws IllegalArgumentException {
		addImpl(vertices.size(), vertex);
	}
	
	/**
	 * Adds a new vertex to the walk.
	 * 
	 * @param index the index at which the vertex should be inserted
	 * @param vertex the vertex
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if vertex is null</li>
	 * 		<li>if vertex is not contained in the associated graph</li>
	 * 		<li>if there is no edge between the last added vertex and the new one (look at the definition of a {@link Walk})</li>
	 * </ul>
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of bounds</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final int index, final V vertex) throws IllegalArgumentException, IndexOutOfBoundsException {
		addImpl(index, vertex);
	}
	
	/**
	 * Inserts a <b>closed</b> walk.
	 * 
	 * @param w the walk to insert
	 * @param back <code>true</code> if the walk should be inserted at the last occurrence of the starting vertex or <code>false</code> for the first occurrence
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if w is null</li>
	 * 		<li>if w is not closed</li>
	 * 		<li>if w does not have the same associated graph as this walk</li>
	 * 		<li>if the start vertex of w is not contained in this walk</li>
	 * </ul>
	 * @since 1.0
	 */
	public void insert(final Walk<V> w, final boolean back) throws IllegalArgumentException {
		final V start = (w.length() > 0) ? w.get(0) : null;
		
		if(start == null)
			return;
		
		int occurrences = 0;
		
		for(int i = 0; i < vertices.size(); i++)
			if(vertices.get(i) == start)
				occurrences++;
		
		insert(w, back ? occurrences : 1);
	}
	
	/**
	 * Inserts a <b>closed</b> walk.
	 * 
	 * @param w the walk to insert
	 * @param index the index of the starting vertex of w in this walk at which the walk w should be inserted (example this = <code>v1,v2,v3,v2,v4,v1</code>, w = <code>v2,v5,v2</code>, so w can be inserted at occurrence 1 or 2 of v2)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if w is null</li>
	 * 		<li>if w is not closed</li>
	 * 		<li>if w does not have the same associated graph as this walk</li>
	 * 		<li>if the start vertex of w is not contained in this walk</li>
	 * 		<li>if index is invalid meaning that index is <code>< 1</code> or <code>> the number of occurrences of the starting vertex of w</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public void insert(final Walk<V> w, final int index) throws IllegalArgumentException {
		if(w == null)
			throw new IllegalArgumentException("No valid argument!");
		else if(!w.isClosed())
			throw new IllegalArgumentException("walk has to be closed");
		else if(w.graph != this.graph)
			throw new IllegalArgumentException("walk has a different associated graph");
		
		final V start = w.get(0);
		final List<Integer> occurrences = new ArrayList<Integer>();
		
		for(int i = 0; i < vertices.size(); i++)
			if(vertices.get(i) == start)
				occurrences.add(i);
		
		if(occurrences.size() == 0)
			throw new IllegalArgumentException("the start vertex of the walk is not contained in this walk");
		else if(index < 1 || index > occurrences.size())
				throw new IllegalArgumentException("the index is invalid");
		
		insertImpl(occurrences.get(index - 1), w);
	}
	
	/**
	 * Removes the last vertex of the walk.
	 * 
	 * @since 1.0
	 */
	public void removeLast() {
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
	 * Gets a vertex at the specified index.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * {@link #length()} returns the number of <b>edges</b> meaning that <code>get(length())</code> is the last vertex of the walk.
	 * 
	 * @param index the index
	 * @return the vertex
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index > length()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public V get(final int index) throws IndexOutOfBoundsException {
		return vertices.get(index);
	}
	
	/**
	 * Indicates whether the specified vertex is on this walk.
	 * 
	 * @param vertex the vertex
	 * @return <code>true</code> if the vertex is on this walk otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean contains(final V vertex) {
		return (vertex != null && vertex.getGraph() == this.graph && vertices.contains(vertex));
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
			V v;
			V u;
			
			// look whether the walk contains the predecessor and successor of the specified edge
			for(int i = 0; i < vertices.size() - 1; i++) {
				v = vertices.get(i);
				u = vertices.get(i + 1);
				
				// if the edge is directed then the predecessor and successor must be one after another and if the edge is undirected
				// it may be that v is the successor and u the predecessor or vice versa
				if(edge.isDirected() && v == edge.getPredecessor() && u == edge.getSuccessor())
					return true;
				else if(!edge.isDirected() && (v == edge.getPredecessor() && u == edge.getSuccessor() || v == edge.getSuccessor() && u == edge.getPredecessor()))
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
	 * Converts this walk using concrete {@link Vertex}s in a {@link WalkByID} using vertex identifiers.
	 * 
	 * @return the walk
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the associated graph was modified meaning vertices of the walk were removed</li>
	 * </ul>
	 * @since 1.0
	 */
	public WalkByID<V> cast() throws IllegalArgumentException {
		final WalkByID<V> w = new WalkByID<V>(graph);
		
		for(V v : vertices)
			w.add(v.getID());
		
		return w;
	}
	
	/**
	 * Gets the walk as a list.
	 * 
	 * @return the walk as a list of its vertices
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	public List<V> asList() {
		return (List<V>)((ArrayList<V>)vertices).clone();
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
		if(o instanceof Walk) {
			try {
				return equals((Walk<V>)o);
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
	public boolean equals(final Walk<V> w) {
		return (w != null) ? this.vertices.equals(w.vertices) : false;
	}
	
	/**
	 * Gets a shallow copy of this walk (the vertices in the walk are not cloned).
	 * 
	 * @return a clone of this walk
	 * @since 1.0
	 */
	@Override
	public Walk<V> clone() {
		return new Walk<V>(graph, vertices);
	}
	
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		boolean delimiter = false;
		s.append("(");
		
		for(V v : vertices) {
			if(delimiter)
				s.append(", ");
			
			s.append(v);
			delimiter = true;
		}
		s.append(")");
		
		return s.toString();
	}
	
	/**
	 * Adds a new vertex to the walk at a given index.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method updates the weight of the walk automatically using {@link #updateWeight()}.
	 * 
	 * @param index the index at which the vertex should be added
	 * @param vertex the vertex
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if vertex is null</li>
	 * 		<li>if vertex is not contained in the associated graph</li>
	 * 		<li>if there is no edge between the predecessor and the new vertex or the successor and the new vertex (look at the definition of a {@link Walk})</li>
	 * </ul>
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of bounds</li>
	 * </ul>
	 * @since 1.0
	 */
	protected void addImpl(final int index, final V vertex) throws IllegalArgumentException, IndexOutOfBoundsException {
		if(vertex == null || vertex.getGraph() != graph)
			throw new IllegalArgumentException("No valid argument!");
		
		final V predecessor = (index > 0) ? vertices.get(index - 1) : null;
		final V successor = (index < vertices.size()) ? vertices.get(index) : null;
		
		if(predecessor != null && graph.getEdge(predecessor, vertex) == null)
			throw new IllegalArgumentException("there is no edge between the given vertex " + vertex + " and the predecessor " + predecessor);
		else if(successor != null && graph.getEdge(vertex, successor) == null)
			throw new IllegalArgumentException("there is no edge between the given vertex " + vertex + " and the successor " + successor);
		
		vertices.add(index, vertex);
		
		// the walk is modified so update the weight
		updateWeight();
	}
	
	/**
	 * The implementation of insert a walk in this walk.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method updates the weight of the walk automatically using {@link #updateWeight()}.
	 * 
	 * @param index the index at which the walk has to be inserted (<b>rembember that the first vertex is already in this walk</b>)
	 * @param w the walk
	 * @since 1.0
	 */
	protected void insertImpl(final int index, final Walk<V> w) {
		// insert the vertices AFTER the start vertex
		for(int i = 1; i <= w.length(); i++)
			addImpl(index + i, w.get(i));
	}
	
	/**
	 * Converts the given array to a list.
	 * 
	 * @param array the array
	 * @return the list
	 * @since 1.0
	 */
	protected static <V extends Vertex> List<V> toList(final V[] array) {
		if(array == null)
			return null;
		
		final List<V> list = new ArrayList<V>(array.length);
		
		for(V v : array)
			list.add(v);
		
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
