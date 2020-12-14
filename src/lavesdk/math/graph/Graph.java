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
 * Class:		Graph
 * Task:		Representation of a graph
 * Created:		11.09.13
 * LastChanges:	15.10.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lavesdk.math.Set;
import lavesdk.math.graph.enums.Type;
import lavesdk.utils.GraphUtils;

/**
 * Represents a graph as a set of vertices and edges. Each graph is by default a weighted graph because an edge
 * has a weight from scratch.
 * <br><br>
 * <b>Vertices</b>:<br>
 * To add or remove vertices to/from the graph use {@link #add(Vertex)} or {@link #remove(Vertex)}. The caption of
 * a vertex is unique that means a graph can only contain vertices with different captions. If you try to add
 * a vertex "A" although there is already a vertex with the caption "A" then the vertex to add is ignored.<br>
 * To go through the set of vertices use {@link #getOrder()} and {@link #getVertex(int)}.
 * <br><br>
 * <b>Edges</b>:<br>
 * To add or remove edges to/from the graph use {@link #add(Edge)} or {@link #remove(Edge)}. A graph can contain loops but only has one edge between
 * any two vertices.<br>
 * To go through the set of edges use {@link #getSize()} and {@link #getEdge(int)} or use {@link #getEdge(Vertex, Vertex)} to check
 * if there is an edge between two vertices.
 * <br><br>
 * To add more than one edge between any two vertices use a {@link MultiGraph}.<br>
 * Use a {@link SimpleGraph} to represent a graph that has no loops (edges connected at both ends to the same vertex) and
 * no more than one edge between any two different vertices.
 * <br><br>
 * <b>Iterate over the graph</b>:<br>
 * Use {@link Vertex#getIncomingEdge(int)}/{@link Vertex#getOutgoingEdge(int)} to iterate over the graph.
 * <br><br>
 * <b>Identification of vertices/edges</b>:<br>
 * Each vertex and edge is uniquely identified by an id that is unique based on the associated graph. It is most efficient to
 * use this id to reference vertices/edges which you want to store in another data structure like a list, set or something else instead
 * of using the real objects.
 * <br><br>
 * <b>Walks</b>:<br>
 * You can define walks in graphs using {@link Walk} or its possible variations {@link Path} and {@link Trail}.
 * 
 * @see MultiGraph
 * @see SimpleGraph
 * @see GraphUtils
 * @see Walk
 * @see Path
 * @see Trail
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 * @param <V> the type of vertex that should be used in the graph
 * @param <E> the type of edge that should be used in the graph
 */
public class Graph<V extends Vertex, E extends Edge> {

	/** the adjacency matrix of the graph */
	private final AdjacencyMatrix<V, E> adjacencyMatrix;
	/** the set of vertices */
	private final List<V> vertices;
	/** the set of vertices which are mapped onto their identifiers */
	private final Map<Integer, V> verticesByID;
	/** the set of edges */
	private final List<E> edges;
	/** the set of edges which are mapped onto their identifiers */
	private final Map<Integer, E> edgesByID;
	/** the type of the graph */
	private final Type type;
	/** contains the id of a new vertex that is added to the graph */
	private int nextVertexID;
	/** contains the id of a new edge that is added to the graph */
	private int nextEdgeID;
	/** the list of {@link AccessibleIDObserver}s */
	private final List<AccessibleIDObserver> accessibleIDObservers;
	
	/**
	 * Creates a new graph.
	 * 
	 * @param type the type of the graph
	 * @since 1.0
	 */
	public Graph(final Type type) {
		this.adjacencyMatrix = new AdjacencyMatrix<V, E>();
		this.vertices = new ArrayList<V>();
		this.verticesByID = new HashMap<Integer, V>();
		this.edges = new ArrayList<E>();
		this.edgesByID = new HashMap<Integer, E>();
		this.type = type;
		this.nextVertexID = 1;
		this.nextEdgeID = 1;
		this.accessibleIDObservers = new ArrayList<AccessibleIDObserver>(0);
	}
	
	/**
	 * Gets the type of the graph.
	 * 
	 * @return the type
	 * @since 1.0
	 */
	public final Type getType() {
		return type;
	}
	
	/**
	 * Gets the number of vertices in the graph.
	 * 
	 * @return the number of vertices
	 * @since 1.0
	 */
	public final int getOrder() {
		return vertices.size();
	}
	
	/**
	 * Gets the vertex at the given index.
	 * 
	 * @param index the index
	 * @return the vertex
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getOrder()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final V getVertex(final int index) throws IndexOutOfBoundsException {
		return vertices.get(index);
	}
	
	/**
	 * Gets the vertex with the given caption.
	 * 
	 * @param caption the caption of the vertex
	 * @return the vertex or <code>null</code> if there is no vertex with the specified caption
	 * @since 1.0
	 */
	public final V getVertexByCaption(final String caption) {
		for(V v : vertices)
			if(v.getCaption().equals(caption))
				return v;
		
		return null;
	}
	
	/**
	 * Gets the vertex with the specified id.
	 * 
	 * @param id the identifier
	 * @return the vertex or <code>null</code> if there is no vertex with the specified id
	 * @since 1.0
	 */
	public final V getVertexByID(final int id) {
		return verticesByID.get(id);
	}
	
	/**
	 * Gets the number of edges in the graph.
	 * 
	 * @return the number of edges
	 * @since 1.0
	 */
	public final int getSize() {
		return edges.size();
	}
	
	/**
	 * Gets the edge at the given index.
	 * 
	 * @param index the index
	 * @return the edge
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getSize()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final E getEdge(final int index) throws IndexOutOfBoundsException {
		return edges.get(index);
	}
	
	/**
	 * Gets the edge with the specified id.
	 * 
	 * @param id the identifier
	 * @return the edge or <code>null</code> if there is no edge with the specified id
	 * @since 1.0
	 */
	public final E getEdgeByID(final int id) {
		return edgesByID.get(id);
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
	public final E getEdge(final int v1, final int v2) {
		return adjacencyMatrix.get(v1, v2);
	}
	
	/**
	 * Gets the edge between the two vertices.
	 * <br><br>
	 * Let <code>G = (V, E)</code> be a graph with <code>V</code> as the set of vertices and <code>E</code> as the set of edges and <code>m</code> be the
	 * adjacency matrix. then <code>m(i,k) = edge</code> if <code>(i,k)</code> is an element of <code>E</code> and <code>m(i,k) = null</code> if <code>(i,k)</code> is not
	 * an element of <code>E</code>.
	 * 
	 * @see #getEdge(int, int)
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
	public final E getEdge(final V v1, final V v2) throws NullPointerException {
		if(v1.getGraph() != this || v2.getGraph() != this)
			return null;
		
		return adjacencyMatrix.get(v1, v2);
	}
	
	/**
	 * Gets a list of all edges between the two vertices. The result can only have multiple edges
	 * if the graph is a multi graph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If a graph is of type undirected then <code>getAll(v1,v2) == getAll(v2,v1)</code> otherwise
	 * one has <code>getAll(v1,v2) != getAll(v2,v1)</code>.
	 * 
	 * @see #getEdge(int, int)
	 * @param v1 the id of the first vertex
	 * @param v2 the id of the second vertex
	 * @return a list of all vertices which are between the specified vertices or <code>null</code> if there is no edge between the vertices
	 * @since 1.0
	 */
	public final List<E> getEdges(final int v1, final int v2) {
		return adjacencyMatrix.getAll(v1, v2);
	}
	
	/**
	 * Gets a list of all edges between the two vertices. The result can only have multiple edges
	 * if the graph is a multi graph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If a graph is of type undirected then <code>getAll(v1,v2) == getAll(v2,v1)</code> otherwise
	 * one has <code>getAll(v1,v2) != getAll(v2,v1)</code>.
	 * 
	 * @see #getEdge(Vertex, Vertex)
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
	public final List<E> getEdges(final V v1, final V v2) {
		return adjacencyMatrix.getAll(v1, v2);
	}
	
	/**
	 * Adds a new vertex to the graph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If their exists already a vertex with the same caption as the specified vertex then the vertex to add is ignored!
	 * 
	 * @param vertex the vertex
	 * @return <code>true</code> if the vertex could be added otherwise <code>false</code> (this could be if the vertex caption does already exist or is added to another graph before)
	 * @since 1.0
	 */
	public final boolean add(final V vertex) {
		// a vertex can only be added to one graph!
		if(vertex == null || vertex.getGraph() != null)
			return false;
		
		beforeVertexAdded(vertex);
		
		// vertex is not allowed or already existing? then break up!
		if(!isVertexAllowed(vertex) || containsVertex(vertex))
			return false;
		
		// set the associated graph and the id of the vertex
		vertex.setGraph(this);
		vertex.setID(nextVertexID++);
		
		// the index of the vertex is the current size of the vertex list
		vertex.setIndex(vertices.size());
		
		// put vertex mapping
		verticesByID.put(vertex.getID(), vertex);
		
		if(vertices.add(vertex)) {
			afterVertexAdded(vertex);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Removes a vertex from the graph.
	 * 
	 * @param vertex the vertex
	 * @return <code>true</code> if the vertex could be removed otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean remove(final V vertex) {
		if(vertex == null || !containsVertex(vertex) || !isRemovable(vertex))
			return false;
		
		// remove all edges that containing the vertex or break up if there is an edge
		// that may not be removed (meaning that the vertex may not be removed too)
		if(!removeEdgesByVertex(vertex))
			return false;
		
		// clear graph reference
		vertex.setGraph(null);
		
		// remove vertex mapping
		verticesByID.remove(vertex.getID());
		
		if(vertices.remove(vertex)) {
			// adjust the indices of the vertices (but only the indices behind the removed vertex because of performance issues)
			for(int i = vertex.getIndex(); i < vertices.size(); i++)
				vertices.get(i).setIndex(i);
			afterVertexRemoved(vertex);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Adds a new edge to the graph.
	 * 
	 * @param edge the edge
	 * @return <code>true</code> if the edge could be added otherwise <code>false</code> (this could be if the edge is added to another graph before)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the predecessor and/or the successor of the edge do not exist in the graph</li>
	 * </ul>
	 * @since 1.0
	 */
	public final boolean add(final E edge) throws IllegalArgumentException {
		// edge can only be added to one graph
		if(edge == null || edge.getGraph() != null)
			return false;
		
		// set the associated graph (important: this has to be done before it is checked whether the edge already exists
		// because the edge needs the correct value for directed that is used in equals() to compare edges) 
		edge.setGraph(this);
		
		beforeEdgeAdded(edge);
		
		// check if the edge is allowed to be added to the graph, the edge is not still available in the graph
		// and of the vertices of the edge exist in this graph
		if(!isEdgeAllowed(edge) || containsEdge(edge) >= 0)
			return false;
		else if(!existVertices(edge))
			throw new IllegalArgumentException("Predecessor and/or successor of the edge do not exist!");
		
		// set identifier of the edge
		edge.setID(nextEdgeID++);
		
		// set linking
		edge.getPredecessor().addOutgoingEdge(edge);
		edge.getSuccessor().addIncomingEdge(edge);
		
		// edge is undirected then link the edge additionally against the successor
		if(!edge.isDirected()) {
			edge.getSuccessor().addOutgoingEdge(edge);
			edge.getPredecessor().addIncomingEdge(edge);
		}
		
		// the index of the edge is the current size of the edge list
		edge.setIndex(edges.size());
		
		// extend the adjacency matrix by the edge
		adjacencyMatrix.add(edge);
		
		// put edge mapping
		edgesByID.put(edge.getID(), edge);
		
		if(edges.add(edge)) {
			afterEdgeAdded(edge);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Removes an edge from the graph.
	 * 
	 * @param edge the edge
	 * @return <code>true</code> if the edge could be removed otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean remove(final E edge) {
		if(edge == null || containsEdge(edge) < 0 || !isRemovable(edge))
			return false;
		
		// remove linking
		edge.getPredecessor().removeOutgoingEdge(edge);
		edge.getSuccessor().removeIncomingEdge(edge);
		
		// edge is undirected then remove linking from the successor
		if(!edge.isDirected()) {
			edge.getSuccessor().removeOutgoingEdge(edge);
			edge.getPredecessor().removeIncomingEdge(edge);
		}
		
		// reduce the adjacency matrix
		adjacencyMatrix.remove(edge);
		
		// clear graph reference
		edge.setGraph(null);
		
		// remove edge mapping
		edgesByID.remove(edge.getID());
		
		if(edges.remove(edge)) {
			// adjust the indices of the edges (but only the indices behind the removed edge because of performance issues)
			for(int i = edge.getIndex(); i < edges.size(); i++)
				edges.get(i).setIndex(i);
			afterEdgeRemoved(edge);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Removes all vertices and edges from the graph that are allowed to be removed.
	 * 
	 * @since 1.0
	 */
	public final void removeAll() {
		// remove all edges (first) and vertices (second) that are allowed to be removed from the graph
		// (do not use remove()/clear() methods on the lists)
		for(int i = edges.size() - 1; i >= 0; i--)
			remove(edges.get(i));
		for(int i = vertices.size() - 1; i >= 0; i--)
			remove(vertices.get(i));
	}
	
	/**
	 * Gets the vertex set containing the identifiers of all vertices the graph consists of currently.
	 * 
	 * @see #getVertexByID(int)
	 * @return the vertex set with the id's of the vertices
	 * @since 1.0
	 */
	public Set<Integer> getVertexByIDSet() {
		final Set<Integer> s = new Set<Integer>(vertices.size());
		
		for(V v : vertices)
			s.add(v.getID());
		
		return s;
	}
	
	/**
	 * Gets the vertex set of all vertices the graph consists of currently.
	 * 
	 * @return the vertex set
	 * @since 1.0
	 */
	public Set<V> getVertexSet() {
		final Set<V> s = new Set<V>(vertices.size());
		
		for(V v : vertices)
			s.add(v);
		
		return s;
	}
	
	/**
	 * Gets the edge set containing the identifiers of all edges the graph consists of currently.
	 * 
	 * @see #getEdgeByID(int)
	 * @return the edge set with the id's of the edges
	 * @since 1.0
	 */
	public Set<Integer> getEdgeByIDSet() {
		final Set<Integer> s = new Set<Integer>(edges.size());
		
		for(E e : edges)
			s.add(e.getID());
		
		return s;
	}
	
	/**
	 * Gets the edge set of all edges the graph consists of currently.
	 * 
	 * @return the edge set
	 * @since 1.0
	 */
	public Set<E> getEdgeSet() {
		final Set<E> s = new Set<E>(edges.size());
		
		for(E e : edges)
			s.add(e);
		
		return s;
	}
	
	/**
	 * Indicates if the graph contains the given vertex.
	 * 
	 * @param vertex the vertex
	 * @return <code>true</code> if the graph contains the vertex otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean contains(final V vertex) {
		if(vertex == null || vertex.getGraph() != this)
			return false;
		else
			return containsVertex(vertex);
	}
	
	/**
	 * Indicates if the graph contains the given edge.
	 * 
	 * @param edge the edge
	 * @return <code>true</code> if the graph contains the edge otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean contains(final E edge) {
		if(edge == null || edge.getGraph() != this)
			return false;
		else
			return containsEdge(edge) >= 0;
	}
	
	/**
	 * Indicates whether the specified graph equals this graph.
	 * <br><br>
	 * This is the case if the vertex set and the edge set of both graphs are equal.
	 * 
	 * @param graph another graph
	 * @return <code>true</code> if both graphs are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean equals(final Graph<V, E> graph) {
		if(graph == null)
			return false;
		else
			return graph.getVertexSet().equals(this.getVertexSet()) && graph.getEdgeSet().equals(this.getEdgeSet());
	}
	
	/**
	 * Indicates whether the specified graph equals this graph.
	 * <br><br>
	 * This is the case if the vertex set and the edge set of both graphs are equal.
	 * 
	 * @param obj another graph of the same type
	 * @return <code>true</code> if both graphs are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Graph) {
			try {
				return equals((Graph<V, E>)obj);
			}
			catch(ClassCastException e) {
				return false;
			}
		}
		else
			return false;
	}
	
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		boolean delimiter = false;
		
		s.append("G=(");
		
		s.append("{");
		for(V v : vertices) {
			if(delimiter)
				s.append(",");
			
			s.append(v.getCaption());
			delimiter = true;
		}
		s.append("}, ");
		
		delimiter = false;
		s.append("{");
		for(E e : edges) {
			if(delimiter)
				s.append(",");
			
			s.append("(" + e.getPredecessor().getCaption() + ", " + e.getSuccessor().getCaption() + ")");
			delimiter = true;
		}
		s.append("})");
		
		return s.toString();
	}
	
	/**
	 * Checks if the given caption is already used by a vertex in the graph.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param caption the caption
	 * @return <code>true</code> if the caption already exists otherwise <code>false</code>
	 * @since 1.0
	 */
	boolean existVertexCaption(final String caption) {
		for(V v : vertices)
			if(v.getCaption().equals(caption))
				return true;
		
		return false;
	}
	
	/**
	 * Validates an edge modification.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param id the id of the modified edge
	 * @return <code>true</code> if the edge modification is allowed otherwise <code>false</code>
	 * @since 1.0
	 */
	boolean validateEdgeModification(final int id) {
		final E e = edgesByID.get(id);
		
		if(e == null)
			return false;
		else
			return isEdgeAllowed(e) && containsEdge(e) < 0;
	}
	
	/**
	 * Adds an observer that can listener to modification events of object identifiers.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param observer the observer
	 * @since 1.0
	 */
	void addIDObserver(final AccessibleIDObserver observer) {
		if(observer == null || accessibleIDObservers.contains(observer))
			return;
		
		accessibleIDObservers.add(observer);
	}
	
	/**
	 * Removes an observer that can listener to modification events of object identifiers.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param observer the observer to be removed
	 * @since 1.0
	 */
	void removeIDObserver(final AccessibleIDObserver observer) {
		accessibleIDObservers.remove(observer);
	}
	
	/**
	 * Updates the direction of the edge meaning the adjacency matrix is updated as well as
	 * the outgoing and incoming edges of the related vertices.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This is only possible if the edge is modifying ({@link Edge#isModifying()}).
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param id the id of the edge that becomes modified
	 * @since 1.0
	 */
	void updateEdgeDirection(final int id) {
		final E e = edgesByID.get(id);
		
		if(!e.isModifying())
			return;
		
		adjacencyMatrix.updateDirection(e);
		
		// if the direction of the edge changes from undirected to directed then the reversed edge has to be removed
		// otherwise if the direction changes from directed to undirected then the reversed edge has to be added
		if(e.isDirected()) {
			e.getSuccessor().removeOutgoingEdge(e);
			e.getPredecessor().removeIncomingEdge(e);
		}
		else {
			e.getSuccessor().addOutgoingEdge(e);
			e.getPredecessor().addIncomingEdge(e);
		}
	}
	
	/**
	 * Modifies the id of the specified vertex.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @see AccessibleID
	 * @param oldID the id of the vertex its id should be modified
	 * @param newID the new identifier
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if there is no vertex with the specified oldID</li>
	 * 		<li>if newID is <code>< 1</code></li>
	 * 		<li>if the graph already contains a vertex with newID</li>
	 * </ul>
	 * @since 1.0
	 */
	void modifyVertexID(final int oldID, final int newID) throws IllegalArgumentException {
		if(oldID == newID)
			return;
		
		final V vertex = verticesByID.get(oldID);
		
		if(vertex == null || newID < 1 || verticesByID.containsKey(newID))
			throw new IllegalArgumentException("No valid argument!");
		
		verticesByID.remove(oldID);
		verticesByID.put(newID, vertex);
		vertex.setID(newID);
		
		// notify the observers
		for(AccessibleIDObserver o : accessibleIDObservers)
			o.vertexIDModified(oldID, newID);
	}
	
	/**
	 * Modifies the id of the specified edge.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @see AccessibleID
	 * @param oldID the id of the edge its id should be modified
	 * @param newID the new identifier
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if there is no edge with the specified oldID</li>
	 * 		<li>if newID is <code>< 1</code></li>
	 * 		<li>if the graph already contains an edge with newID</li>
	 * </ul>
	 * @since 1.0
	 */
	void modifyEdgeID(final int oldID, final int newID) throws IllegalArgumentException {
		if(oldID == newID)
			return;
		
		final E edge = edgesByID.get(oldID);
		
		if(edge == null || newID < 1 || edgesByID.containsKey(newID))
			throw new IllegalArgumentException("No valid argument!");
		
		edgesByID.remove(oldID);
		edgesByID.put(newID, edge);
		edge.setID(newID);
		
		// notify the observers
		for(AccessibleIDObserver o : accessibleIDObservers)
			o.edgeIDModified(oldID, newID);
	}
	
	/**
	 * Checks if the graph already contains the given vertex, that means if there is already a vertex in the graph
	 * with the caption of the specified vertex.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Override this method if you want to decide yourself which vertices are allowed and which not.
	 * 
	 * @param vertex the vertex
	 * @return <code>true</code> if the vertex already exists otherwise <code>false</code>
	 * @since 1.0
	 */
	protected boolean containsVertex(final V vertex) {
		return existVertexCaption(vertex.getCaption());
	}
	
	/**
	 * Checks if the graph already contains the given edge, that means if <code>getEdge(i).equalsIgnoreWeight(edge)</code>
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Override this method if you want to handle that multiple edges are allowed, etc.
	 * 
	 * @param edge the edge
	 * @return <code>-1</code> if the edge does not exist otherwise the index of the edge in the list of edges
	 * @since 1.0
	 */
	protected int containsEdge(final E edge) {
		// edges can be compared with equalsIgnoreWeight(...) because this method is final so it cannot be modified
		for(int i = 0; i < edges.size(); i++)
			if(edges.get(i).equalsIgnoreWeight(edge))
				return i;
		
		return -1;
	}
	
	/**
	 * Checks if the given vertex is allowed. By default any vertex is allowed.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Override this method if you want to restrict the graph to specific vertices.
	 * 
	 * @param vertex the vertex
	 * @return <code>true</code> if the vertex is allowed otherwise <code>false</code>
	 * @since 1.0
	 */
	protected boolean isVertexAllowed(final V vertex) {
		return true;
	}
	
	/**
	 * Checks if the given edge is allowed. By default any edge is allowed.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Override this method if you want to restrict the graph to specific edges.
	 * 
	 * @param edge the edge
	 * @return <code>true</code> if the edge is allowed otherwise <code>false</code>
	 * @since 1.0
	 */
	protected boolean isEdgeAllowed(final E edge) {
		return true;
	}
	
	/**
	 * Indicates if the given vertex may be removed. By default any vertex can be removed.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Override this method if you want to handle whether specific vertices can be removed.
	 * 
	 * @param vertex the vertex
	 * @return <code>true</code> if the vertex can be removed otherwise <code>false</code>
	 * @since 1.0
	 */
	protected boolean isRemovable(final V vertex) {
		return true;
	}
	
	/**
	 * Indicates if the given edge may be removed. By default any vertex can be removed.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Override this method if you want to handle whether specific edges can be removed.
	 * 
	 * @param edge the edge
	 * @return <code>true</code> if the edge can be removed otherwise <code>false</code>
	 * @since 1.0
	 */
	protected boolean isRemovable(final E edge) {
		return true;
	}
	
	/**
	 * Is invoked before a new vertex is added to the graph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method is invoked before anything is done with the vertex.
	 * 
	 * @param vertex the vertex that is added
	 * @since 1.1
	 */
	protected void beforeVertexAdded(final V vertex) {
	}
	
	/**
	 * Is invoked before a new edge is added to the graph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method is invoked before anything is done with the edge.
	 * 
	 * @param edge the edge that is added
	 * @since 1.1
	 */
	protected void beforeEdgeAdded(final E edge) {
	}
	
	/**
	 * Is invoked after a new vertex is added to the graph.
	 * 
	 * @param vertex the vertex that is added
	 * @since 1.1
	 */
	protected void afterVertexAdded(final V vertex) {
	}
	
	/**
	 * Is invoked after a new edge is added to the graph.
	 * 
	 * @param edge the edge that is added
	 * @since 1.1
	 */
	protected void afterEdgeAdded(final E edge) {
	}
	
	/**
	 * Is invoked after a vertex is removed from the graph.
	 * 
	 * @param vertex the vertex that is removed
	 * @since 1.1
	 */
	protected void afterVertexRemoved(final V vertex) {
	}
	
	/**
	 * Is invoked after an edge is removed from the graph.
	 * 
	 * @param edge the edge that is removed
	 * @since 1.1
	 */
	protected void afterEdgeRemoved(final E edge) {
	}
	
	/**
	 * Removes all edges that contain the given vertex as predecessor or successor.
	 * 
	 * @param vertex the vertex
	 * @return <code>true</code> if all edges could be removed and <code>false</code> that there are edges which are not allowed to be removed (<b>meaning that the vertex is not allowed to be removed too</b>)
	 * @since 1.0
	 */
	private boolean removeEdgesByVertex(final V vertex) {
		final List<E> removeEdges = new ArrayList<E>();
		boolean result = true;
		E e;
		
		// add all edges to the list that should be removed
		for(int i = edges.size() - 1; i >= 0; i--) {
			e = edges.get(i);
			
			// vertex is predecessor or successor of this edge then remove the edge
			if(e.getPredecessor() == vertex || e.getSuccessor() == vertex) {
				removeEdges.add(e);
				result = isRemovable(e);
			}
			
			if(!result)
				break;
		}
		
		// only remove the edges if all edges may be removed otherwise
		// the vertex may not be removed too
		if(result) {
			for(int i = removeEdges.size() - 1; i >= 0; i--)
				remove(removeEdges.get(i));
		}
		
		return result;
	}
	
	/**
	 * Checks if the predecessor and successor of the given edge exist in the graph.
	 * 
	 * @param edge the edge
	 * @return <code>true</code> if the graph contains the predecessor and successor otherwise <code>false</code>
	 * @since 1.0
	 */
	private boolean existVertices(final E edge) {
		boolean predecessorExists = false;
		boolean successorExists = false;
		
		for(V v : vertices) {
			// check if graph has a reference of the predecessor and successor vertex
			if(!predecessorExists && edge.getPredecessor() == v)
				predecessorExists = true;
			// ATTENTION: no "else if" because a loop has predecessor == successor!
			if(!successorExists && edge.getSuccessor() == v)
				successorExists = true;
			
			if(predecessorExists && successorExists)
				break;
		}
		
		return predecessorExists && successorExists;
	}

}
