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

import lavesdk.math.Set;
import lavesdk.math.graph.enums.Type;
import lavesdk.serialization.Serializable;
import lavesdk.serialization.Serializer;

/**
 * Represents a vertex of a {@link Graph}.
 * <br><br>
 * <b>Identifier</b>:<br>
 * Each vertex has an unique identifier based on the associated graph. It is most efficient to use this identifier to reference
 * a vertex in a data structure like a list, set, and so on. Use {@link Graph#getVertexByID(int)} to get the vertex of a specific id.
 * <br><br>
 * <b>Caption</b>:<br>
 * The caption of a vertex is a unique based on the associated graph, that means you should take care of individual captions for
 * each vertex.
 * <br><br>
 * <b>Incoming and outgoing edges</b>:<br>
 * To find out which edges are incoming ones use {@link #getIncomingEdgeCount()} and {@link #getIncomingEdge(int)}. Use
 * {@link #getOutgoingEdgeCount()} and {@link #getOutgoingEdge(int)} to find out which edges are outgoing ones.
 * <br><br>
 * <b>Serialize vertex data</b>:<br>
 * If you build a new sub type of {@link Vertex} keep in mind that you should override {@link #serialize(Serializer)} and {@link #deserialize(Serializer)}
 * so that the additional data of the new type can be stored in a graph file.
 * 
 * @see Graph
 * @see Serializable
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class Vertex implements Serializable {
	
	/** the caption of the vertex */
	private String caption;
	/** the list of incoming edges */
	private final List<Edge> incomingEdges;
	/** the list of outgoing edges */
	private final List<Edge> outgoingEdges;
	/** the set of incident edges */
	private final Set<Edge> incidentEdges;
	/** the associated graph */
	private Graph<? extends Vertex, ? extends Edge> graph;
	/** the id of the vertex which is unique based on the associated graph */
	private int id;
	/** the index of the vertex in the list of vertices of the associated graph */
	private int index;
	
	/**
	 * Creates a new vertex.
	 * 
	 * @param caption the caption of the vertex
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if caption is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Vertex(final String caption) throws IllegalArgumentException {
		if(caption == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.incomingEdges = new ArrayList<Edge>();
		this.outgoingEdges = new ArrayList<Edge>();
		this.incidentEdges = new Set<Edge>();
		this.caption = caption;
		this.graph = null;
		this.id = -1;
		this.index = -1;
	}
	
	/**
	 * Gets a unique identifier of this vertex based on the associated graph. That means there are not two different vertices v1, v2 with
	 * <code>v1.getID() == v2.getID()</code>.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It is most efficient to use the identifier to reference a vertex in a data structure like a list, set, ...
	 * 
	 * @return the identifier of the vertex or <code>-1</code> if this vertex is not yet associated with a graph
	 * @since 1.0
	 */
	public final int getID() {
		return id;
	}
	
	/**
	 * Gets the current index of the vertex in the list of vertices of the associated graph meaning that
	 * <code>graph.getVertex(v.getIndex()) == v</code>.
	 * 
	 * @return the vertex's index or <code>-1</code> if the vertex is not associated with a graph
	 * @since 1.0
	 */
	public final int getIndex() {
		return index;
	}
	
	/**
	 * Gets the caption of the vertex.
	 * 
	 * @return the caption
	 * @since 1.0
	 */
	public String getCaption() {
		return caption;
	}
	
	/**
	 * Sets the caption of the vertex.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * In a graph the caption of a vertex is unique that means there aren't two different vertices with the same vertex so
	 * if the new caption already exists in the graph then it is ignored.
	 * 
	 * @param caption the caption
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if caption is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setCaption(final String caption) throws IllegalArgumentException {
		if(caption == null)
			throw new IllegalArgumentException("No valid argument!");
		
		// check if the caption already exists in the graph,
		// if this is the case then break up!
		if(graph != null && graph.existVertexCaption(caption))
			return;
		
		this.caption = caption;
	}
	
	/**
	 * Gets the number of incoming edges.
	 * 
	 * @return the number of incoming edges
	 * @since 1.0
	 */
	public int getIncomingEdgeCount() {
		return incomingEdges.size();
	}
	
	/**
	 * Gets an incoming edge at the given index.
	 * 
	 * @see Edge#getPredecessor(Vertex)
	 * @see Edge#getSuccessor(Vertex)
	 * @param index the index
	 * @return the edge
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getIncomingEdgesCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public Edge getIncomingEdge(final int index) throws IndexOutOfBoundsException {
		return incomingEdges.get(index);
	}
	
	/**
	 * Gets the number of outgoing edges.
	 * 
	 * @return the number of outgoing edges
	 * @since 1.0
	 */
	public int getOutgoingEdgeCount() {
		return outgoingEdges.size();
	}
	
	/**
	 * Gets an outgoing edge at the given index.
	 * 
	 * @see Edge#getPredecessor(Vertex)
	 * @see Edge#getSuccessor(Vertex)
	 * @param index the index
	 * @return the edge
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getOutgoingEdgesCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public Edge getOutgoingEdge(final int index) throws IndexOutOfBoundsException {
		return outgoingEdges.get(index);
	}
	
	/**
	 * Gets the number of incident edges.
	 * <br><br>
	 * An edge and a vertex are called incident if the vertex is on the edge meaning that the vertex is an endpoint of the edge.
	 * 
	 * @return the number of incident edges
	 * @since 1.0
	 */
	public int getIncidentEdgeCount() {
		return incidentEdges.size();
	}
	
	/**
	 * Gets an incident edge at the given index.
	 * 
	 * @see Edge#getPredecessor(Vertex)
	 * @see Edge#getSuccessor(Vertex)
	 * @param index the index
	 * @return the edge
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getIncidentEdgeCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public Edge getIncidentEdge(final int index) throws IndexOutOfBoundsException {
		return incidentEdges.get(index);
	}
	
	/**
	 * Gets the degree of the vertex.
	 * <br><br>
	 * In an undirected graph the degree of a vertex is defined as: <i>deg(v) = number of edges which are connected with v</i> (loops are counted twice).<br>
	 * In a directed graph there are teh outdegree <i>deg+(v) = number of outgoing edges of v</i>, the indegree <i>deg-(v) = number of incoming edges of v</i>
	 * and the degree which is defined as: <i>deg(v) = deg+(v) + deg-(v)</i>.
	 * 
	 * @return the degree of the vertex
	 * @since 1.0
	 */
	public int getDegree() {
		if(graph.getType() == Type.DIRECTED)
			return getOutdegree() + getIndegree();
		else
			return outgoingEdges.size() + getLoopCount();
	}
	
	/**
	 * Gets the indegree of the vertex which is defined as: <i>deg-(v) = number of incoming edges of v</i>.
	 * 
	 * @return the indegree of the vertex
	 * @since 1.0
	 */
	public int getIndegree() {
		return incomingEdges.size();
	}
	
	/**
	 * Gets the outdegree of the vertex which is defined as: <i>deg+(v) = number of outgoing edges of v</i>.
	 * 
	 * @return the outdegree of the vertex
	 * @since 1.0
	 */
	public int getOutdegree() {
		return outgoingEdges.size();
	}
	
	/**
	 * Returns a string representation of the vertex more precisely the caption of the vertex.
	 * 
	 * @return a string representation
	 * @since 1.0
	 */
	@Override
	public String toString() {
		return caption;
	}
	
	/**
	 * Indicates whether the given vertex equals this vertex.
	 * <br>
	 * This is the case if <code>v != null && v.caption.equals(this.caption)</code>.
	 * 
	 * @param v the vertex
	 * @return <code>true</code> if v equals this vertex otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean equals(final Vertex v) {
		return v != null && v.caption.equals(this.caption);
	}
	
	/**
	 * Indicates whether the given object equals this vertex.
	 * 
	 * @param o the object
	 * @return <code>true</code> if o equals this vertex otherwise <code>false</code>
	 * @since 1.0
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof Vertex)
			return equals((Vertex)o);
		
		return false;
	}

	@Override
	public void serialize(Serializer s) {
		s.addString("caption", caption);
	}

	@Override
	public void deserialize(Serializer s) {
		final String caption = s.getString("caption");
		
		// only change the caption if the deserialized caption is valid otherwise retain the old one
		// a caption is valid if the vertex is not added to a graph yet or if there is no other vertex
		// in the related graph with this caption
		if(graph == null || !graph.existVertexCaption(caption))
			this.caption = caption;
	}
	
	/**
	 * Gets the associated graph of the vertex.
	 * 
	 * @return the graph
	 * @since 1.0
	 */
	protected final Graph<? extends Vertex, ? extends Edge> getGraph() {
		return graph;
	}
	
	/**
	 * Sets the identifier of the vertex.
	 * <br><br>
	 * It must be guaranteed that this id is unique based on the associated graph.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param id the identifier
	 * @since 1.0
	 */
	final void setID(final int id) {
		this.id = id;
	}
	
	/**
	 * Sets the index of the vertex in the list of vertices of the associated graph.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param index the index
	 * @since 1.0
	 */
	final void setIndex(final int index) {
		this.index = index;
	}
	
	/**
	 * Adds a new edge to the list of incoming edges.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param edge the edge
	 * @since 1.0
	 */
	void addIncomingEdge(final Edge edge) {
		if(edge != null) {
			incomingEdges.add(edge);
			incidentEdges.add(edge);
		}
	}
	
	/**
	 * Removes an edge from the list of incoming edges.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param edge the edge
	 * @since 1.0
	 */
	void removeIncomingEdge(final Edge edge) {
		incomingEdges.remove(edge);
		incidentEdges.remove(edge);
	}
	
	/**
	 * Adds a new edge to the list of outgoing edges.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param edge the edge
	 * @since 1.0
	 */
	void addOutgoingEdge(final Edge edge) {
		if(edge != null) {
			outgoingEdges.add(edge);
			incidentEdges.add(edge);
		}
	}
	
	/**
	 * Removes an edge from the list of outgoing edges.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param edge the edge
	 * @since 1.0
	 */
	void removeOutgoingEdge(final Edge edge) {
		outgoingEdges.remove(edge);
		incidentEdges.remove(edge);
	}
	
	/**
	 * Sets the associated graph of the vertex.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param graph the graph
	 * @since 1.0
	 */
	final void setGraph(final Graph<? extends Vertex, ? extends Edge> graph) {
		this.graph = graph;
	}
	
	/**
	 * Gets the number of loops.
	 * 
	 * @return number of loops of this vertex
	 * @since 1.0
	 */
	private int getLoopCount() {
		int loops = 0;
		
		for(Edge e : outgoingEdges)
			if(e.getSuccessor(this) == this)
				loops++;
		
		return loops;
	}

}
