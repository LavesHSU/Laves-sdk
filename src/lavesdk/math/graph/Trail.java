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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a trail in a {@link Graph}.
 * <br><br>
 * <b>Definition</b>:<br>
 * A trail is a {@link Walk} that does not pass over the same edge twice. A trail might visit the same vertex twice, but only if it
 * comes and goes from a different edge each time.
 * <br><br>
 * Use {@link #cast()} to convert this trail using concrete {@link Vertex}s in a {@link TrailByID} using vertex identifiers.
 * <br><br>
 * If you want to serialize a trail please look at {@link TrailByID}.
 * 
 * @see Walk
 * @see Path
 * @see TrailByID
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <V> the type of the vertices
 */
public class Trail<V extends Vertex> extends Walk<V> {
	
	/** list of edge identifiers to detect already existing edges */
	private final List<Integer> edges;
	/** the edge that should be used to traverse from the last vertex to the new vertex or <code>null</code> */
	private Edge useEdge;
	
	/**
	 * Creates an empty trail.
	 * 
	 * @param graph the graph its vertices can be part of this trail
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Trail(final Graph<V, ? extends Edge> graph) throws IllegalArgumentException {
		this(graph, (List<V>)null);
	}
	
	/**
	 * Creates a trail based on a predefined trail.
	 * 
	 * @param graph the graph its vertices can be part of this trail
	 * @param trail the predefined trail or <code>null</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if the predefined trail is not a correct one</li>
	 * </ul>
	 * @since 1.0
	 */
	public Trail(final Graph<V, ? extends Edge> graph, final V[] trail) throws IllegalArgumentException {
		this(graph, toList(trail));
	}
	
	/**
	 * Creates a trail based on a predefined trail.
	 * 
	 * @param graph the graph its vertices can be part of this trail
	 * @param trail the predefined trail or <code>null</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if the predefined trail is not a correct one</li>
	 * </ul>
	 * @since 1.0
	 */
	public Trail(final Graph<V, ? extends Edge> graph, final List<V> trail) throws IllegalArgumentException {
		super(graph, (List<V>)null);
		
		edges = new ArrayList<Integer>();
		useEdge = null;
		
		// add the vertices to the trail
		// (important: this has to be done after the edges list is initialized)
		if(trail != null)
			for(V v : trail)
				add(v);
	}
	
	/**
	 * Adds a new vertex to the trail.
	 * 
	 * @param vertex the vertex
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if vertex is null</li>
	 * 		<li>if vertex is not contained in the associated graph</li>
	 * 		<li>if there is no edge between the last added vertex and the new one (look at the definition of a {@link Trail})</li>
	 * 		<li>if the trail already contains an edge between the last added vertex and the new one (look at the definition of a {@link Trail})</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void add(final V vertex) throws IllegalArgumentException {
		super.add(vertex);
	}
	
	/**
	 * Adds a new vertex to the trail.
	 * 
	 * @param index the index at which the vertex should be inserted
	 * @param vertex the vertex
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if vertex is null</li>
	 * 		<li>if vertex is not contained in the associated graph</li>
	 * 		<li>if there is no edge between the last added vertex and the new one (look at the definition of a {@link Trail})</li>
	 * 		<li>if the trail already contains an edge between the last added vertex and the new one (look at the definition of a {@link Trail})</li>
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
	 * Adds a new vertex to the trail using the specified edge.
	 * <br><br>
	 * This is useful if you want to create a trail in a multi graph that should take a specific edge between two vertices.
	 * 
	 * @param vertex the vertex
	 * @param edge the edge the trail should take when traversing from the last vertex of the trail to the new one or <code>null</code> to take an edge that is currently not visited
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if vertex is null</li>
	 * 		<li>if vertex is not contained in the associated graph</li>
	 * 		<li>if edge is not contained in the associated graph</li>
	 * 		<li>if edge is not incident to vertex or if the edge is already existing in the trail</li>
	 * 		<li>if there is no edge between the last added vertex and the new one (look at the definition of a {@link Trail})</li>
	 * 		<li>if the trail already contains an edge between the last added vertex and the new one (look at the definition of a {@link Trail})</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final V vertex, final Edge edge) throws IllegalArgumentException {
		if(edge != null && edge.getGraph() != graph)
			throw new IllegalArgumentException("No valid argument!");
		
		useEdge = edge;
		super.add(vertex);
	}
	
	/**
	 * Inserts a <b>closed</b> trail at the first occurrence of the starting vertex.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Ensure that the specified walk is a {@link Trail} instance, is closed and does not contain edges that already exist in this trail.
	 * 
	 * @param w a trail
	 * @param back <code>true</code> if the trail should be inserted at the last occurrence of the starting vertex or <code>false</code> for the first occurrence
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if w is null</li>
	 * 		<li>if w is not closed</li>
	 * 		<li>if w is not a {@link Trail} instance</li>
	 * 		<li>if the start vertex of w is not contained in this trail</li>
	 * 		<li>if the specified walk contains an edge that already exists in this trail</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void insert(final Walk<V> w, final boolean back) throws IllegalArgumentException {
		super.insert(w, back);
	}
	
	/**
	 * Inserts a <b>closed</b> trail at the first occurrence of the starting vertex.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Ensure that the specified walk is a {@link Trail} instance, is closed and does not contain edges that already exist in this trail.
	 * 
	 * @param w a trail
	 * @param index the index of the starting vertex of w in this trail at which the trail w should be inserted (example this = <code>v1,v2,v3,v2,v4,v1</code>, w = <code>v2,v5,v2</code>, so w can be inserted at occurrence 1 or 2 of v2)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if w is null</li>
	 * 		<li>if w is not closed</li>
	 * 		<li>if w is not a {@link Trail} instance</li>
	 * 		<li>if the start vertex of w is not contained in this trail</li>
	 * 		<li>if index is invalid meaning that index is <code>< 1</code> or <code>> the number of occurrences of the starting vertex of w</code></li>
	 * 		<li>if the specified walk contains an edge that already exists in this trail</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void insert(final Walk<V> w, final int index) throws IllegalArgumentException {
		super.insert(w, index);
	}
	
	/**
	 * Removes the last vertex of the trail.
	 * 
	 * @since 1.0
	 */
	@Override
	public void removeLast() {
		super.removeLast();
		if(edges.size() > 0)
			edges.remove(edges.size() - 1);
	}
	
	/**
	 * Gets the edge of the trail at the specified index.
	 * 
	 * @see #length()
	 * @param index the index
	 * @return the edge or <code>null</code> if there is no edge (a reason for that might be that the graph is modified)
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index > length()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public Edge getEdge(final int index) throws IndexOutOfBoundsException {
		return graph.getEdgeByID(edges.get(index));
	}
	
	/**
	 * Indicates whether the trail contains the specified edge.
	 * 
	 * @param e the edge
	 * @return <code>true</code> if the trail contains the edge otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean contains(final Edge e) {
		return (e != null && e.getGraph() == graph && edges.contains(e.getID()));
	}
	
	/**
	 * Indicates whether this trail is a circuit meaning that the trail begins and ends on the same vertex.
	 * 
	 * @return <code>true</code> if the trail is a circuit otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isClosed() {
		return super.isClosed();
	}
	
	/**
	 * Converts this trail using concrete {@link Vertex}s in a {@link TrailByID} using vertex identifiers.
	 * 
	 * @return the trail
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the associated graph was modified meaning vertices of the trail were removed</li>
	 * </ul>
	 * @since 1.0
	 */
	public TrailByID<V> cast() throws IllegalArgumentException {
		final TrailByID<V> t = new TrailByID<V>(graph);
		
		for(int i = 0; i < vertices.size(); i++)
			t.add(vertices.get(i).getID(), (i > 0) ? edges.get(i - 1) : 0);
		
		return t;
	}
	
	/**
	 * Indicates whether this trail equals the specified one.
	 * 
	 * @param o another trail of the same vertex type that should be compared with this trail
	 * @return <code>true</code> if the trails are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if(o instanceof Trail) {
			try {
				return equals((Trail<V>)o);
			}
			catch(ClassCastException e) {
				return false;
			}
		}
		else
			return false;
	}
	
	/**
	 * Indicates whether this trail equals the specified one.
	 * 
	 * @param t another trail that should be compared with this trail
	 * @return <code>true</code> if the trails are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean equals(final Trail<V> t) {
		return (t != null) ? this.vertices.equals(t.vertices) && this.edges.equals(t.edges) : false;
	}
	
	/**
	 * Gets a shallow copy of this trail (the vertices in the trail are not cloned).
	 * 
	 * @return a clone of this trail
	 * @since 1.0
	 */
	@Override
	public Trail<V> clone() {
		return new Trail<V>(graph, vertices);
	}
	
	/**
	 * Adds a new vertex to the trail at a specified index.
	 * 
	 * @param index the index at which the vertex should be added
	 * @param vertex the vertex
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if vertex is null</li>
	 * 		<li>if vertex is not contained in the associated graph</li>
	 * 		<li>if the current edge to use is not incident to the vertex or already existing</li>
	 * 		<li>if there is no edge between the predecessor and the new vertex or the successor and the new vertex (look at the definition of a {@link Trail})</li>
	 * 		<li>if the trail already contains an edge between the predecessor and the new vertex or the successor and the new vertex (look at the definition of a {@link Trail})</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	protected void addImpl(int index, V vertex) throws IllegalArgumentException {
		final V predecessor = (index > 0) ? vertices.get(index - 1) : null;
		final V successor = (index < vertices.size()) ? vertices.get(index) : null;
		final List<? extends Edge> edges1 = (predecessor != null && vertex != null) ? graph.getEdges(predecessor, vertex) : null;
		final List<? extends Edge> edges2 = (successor != null && vertex != null) ? graph.getEdges(vertex, successor) : null;
		final List<Integer> edges1IDs = (edges1 != null) ? new ArrayList<Integer>() : null;
		final List<Integer> edges2IDs = (edges2 != null) ? new ArrayList<Integer>() : null;
		final int edgeIndex = (index - 1 < 0) ? 0 : index - 1;
		
		if(edges1 != null)
			for(Edge e : edges1)
				edges1IDs.add(e.getID());
		if(edges2 != null)
			for(Edge e : edges2)
				edges2IDs.add(e.getID());
		
		if(useEdge != null && !edges1IDs.contains(useEdge.getID()))
			throw new IllegalArgumentException("the specified edge is not incident to the specified vertex");
		else if(useEdge != null && edges.contains(useEdge.getID()))
			throw new IllegalArgumentException("the specified edge is already contained in the trail");
		
		if(edges1IDs != null && edges.containsAll(edges1IDs))
			throw new IllegalArgumentException("the trail already contains all edges between " + predecessor + " and " + vertex);
		else if(edges2IDs != null && edges.containsAll(edges2IDs))
			throw new IllegalArgumentException("the trail already contains all edges between " + vertex + " and " + successor);
		
		super.addImpl(index, vertex);
		if(edges1IDs != null) {
			for(Integer id : edges1IDs) {
				if(!edges.contains(id) && (useEdge == null || useEdge.getID() == id.intValue())) {
					edges.add(edgeIndex, id);
					break;
				}
			}
		}
		if(edges2IDs != null) {
			for(Integer id : edges2IDs) {
				if(!edges.contains(id)) {
					edges.add(edgeIndex + 1, id);
					break;
				}
			}
		}
		
		useEdge = null;
	}
	
	@Override
	protected void insertImpl(int index, Walk<V> w) throws IllegalArgumentException {
		if(!(w instanceof Trail))
			throw new IllegalArgumentException("No valid argument!");
		
		final Trail<V> t = (Trail<V>)w;
		
		// check whether the specified walk contains an edge that already exists in this trail
		for(int i = 0; i < t.length(); i++)
			if(edges.contains(t.get(i)))
				throw new IllegalArgumentException("the walk contains an edge that already exists in this trail");
		
		// insert the walk at the specified position
		// (example: 1,2,4,5 -> insert 2,3,6,2 -> the edge between 2 and 4 has to be obtained so it must not be removed
		//  and the edges (2,3),(3,6) and (6,2) have to be added to the list of edges, furthermore 3,6,2 have to be
		//  added after 2 (index=1) in the list of vertices; that is, the list of vertices is 1,2,3,6,2,4,5)
		for(int i = 1; i <= t.length(); i++) {
			vertices.add(index + i, t.get(i));
			edges.add(index + i - 1, t.edges.get(i - 1));
		}
		
		// the trail changed so update the weight
		updateWeight();
	}

}
