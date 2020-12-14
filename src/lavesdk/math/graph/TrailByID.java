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
 * Class:		TrailByID
 * Task:		Representation of a trail of vertex identifiers in a graph
 * Created:		10.04.14
 * LastChanges:	20.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a trail in a {@link Graph} using the identifiers of the vertices.
 * <br><br>
 * <b>Definition</b>:<br>
 * A trail is a {@link WalkByID} that does not pass over the same edge twice. A trail might visit the same vertex twice, but only if it
 * comes and goes from a different edge each time.
 * <br><br>
 * <b>Serialize a trail</b>:<br>
 * Use {@link TrailByID} to serialize a {@link Trail}. If you deserialize a {@link TrailByID} keep in mind to set the associated graph with
 * {@link #setGraph(Graph)} otherwise the trail is not functioning any more.
 * <br><br>
 * Use {@link #cast()} to convert this trail using vertex identifiers in a {@link Trail} using concrete {@link Vertex}s.
 * 
 * @see WalkByID
 * @see PathByID
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <V> the type of the vertices
 */
public class TrailByID<V extends Vertex> extends WalkByID<V> {
	
	private static final long serialVersionUID = 1L;
	
	/** list of edge identifiers to detect already existing edges */
	private final List<Integer> edges;
	
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
	public TrailByID(final Graph<V, ? extends Edge> graph) throws IllegalArgumentException {
		this(graph, (List<Integer>)null);
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
	public TrailByID(final Graph<V, ? extends Edge> graph, final Integer[] trail) throws IllegalArgumentException {
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
	public TrailByID(final Graph<V, ? extends Edge> graph, final List<Integer> trail) throws IllegalArgumentException {
		super(graph, (List<Integer>)null);
		
		edges = new ArrayList<Integer>();
		
		// add the vertices to the trail
		// (important: this has to be done after the edges list is initialized)
		if(trail != null)
			for(Integer v : trail)
				add(v);
	}
	
	/**
	 * Adds a new vertex to the path.
	 * 
	 * @param vertexID the id of the vertex
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the trail does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if there is no vertex in the associated graph with the given id</li>
	 * 		<li>if there is no edge between the last added vertex and the new one (look at the definition of a {@link TrailByID})</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void add(final int vertexID) throws UnsupportedOperationException, IllegalArgumentException {
		add(vertexID, 0);
	}
	
	/**
	 * Adds a new vertex to the path.
	 * <br><br>
	 * This is useful if you want to create a trail in a multi graph that should take a specific edge between two vertices.
	 * 
	 * @param vertexID the id of the vertex
	 * @param edgeID the id of the edge the trail should take when traversing from the last vertex of the trail to the new one or <code>< 1</code> to take an edge that is currently not visited
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the trail does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if there is no vertex in the associated graph with the given id</li>
	 * 		<li>if the edge of id <code>edgeID</code> is not incident to the vertex</li>
	 * 		<li>if there is no edge between the last added vertex and the new one (look at the definition of a {@link TrailByID})</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final int vertexID, final int edgeID) throws UnsupportedOperationException, IllegalArgumentException {
		if(graph == null)
			throw new UnsupportedOperationException("the walk does not have an associated graph");
		
		final Integer lastVertex = (vertices.size() > 0) ? vertices.get(vertices.size() - 1) : null;
		final List<? extends Edge> possEdges = (lastVertex != null) ? graph.getEdges(lastVertex.intValue(), vertexID) : null;
		final List<Integer> possEdgeIDs = (possEdges != null) ? new ArrayList<Integer>() : null;
		
		if(possEdges != null)
			for(Edge e : possEdges)
				possEdgeIDs.add(e.getID());
		
		if(edgeID > 0 && !possEdgeIDs.contains(edgeID))
			throw new IllegalArgumentException("the specified edge is not incident to the specified vertex");
		
		if(possEdgeIDs != null && edges.containsAll(possEdgeIDs))
			throw new IllegalArgumentException("the trail already contains all edges between the vertices with the identifiers " + lastVertex.intValue() + " and " + vertexID);
		
		super.add(vertexID);
		if(possEdgeIDs != null) {
			for(Integer id : possEdgeIDs) {
				if(!edges.contains(id) && (edgeID < 1 || edgeID == id.intValue())) {
					edges.add(id);
					break;
				}
			}
		}
	}
	
	/**
	 * Removes the last vertex of the trail.
	 * 
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the trail does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void removeLast() throws UnsupportedOperationException {
		super.removeLast();
		
		if(edges.size() > 0)
			edges.remove(edges.size() - 1);
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
	 * Converts this trail using vertex identifiers in a {@link Trail} with concrete {@link Vertex}s.
	 * 
	 * @return the trail
	 * @throws UnsupportedOperationException
	 * <ul>
	 * 		<li>if the walk does not have an associated graph (see {@link #setGraph(Graph)})</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the trail cannot be cast because the associated graph was modified</li>
	 * </ul>
	 * @since 1.0
	 */
	public Trail<V> cast() throws UnsupportedOperationException, IllegalArgumentException {
		if(graph == null)
			throw new UnsupportedOperationException("the walk does not have an associated graph");
		
		final Trail<V> t = new Trail<V>(graph);
		
		for(int i = 0; i < vertices.size(); i++)
			t.add(graph.getVertexByID(vertices.get(i).intValue()), (i > 0) ? graph.getEdgeByID(edges.get(i - 1)) : null);
		
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
		if(o instanceof TrailByID) {
			try {
				return equals((TrailByID<V>)o);
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
	public boolean equals(final TrailByID<V> t) {
		return (t != null) ? this.vertices.equals(t.vertices) && this.edges.equals(t.edges) : false;
	}
	
	/**
	 * Gets a shallow copy of this trail (the identifiers of the vertices in the trail are not cloned).
	 * 
	 * @return a clone of this trail
	 * @since 1.0
	 */
	@Override
	public TrailByID<V> clone() {
		return new TrailByID<V>(graph, vertices);
	}

}
