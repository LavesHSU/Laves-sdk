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

import lavesdk.math.graph.enums.Type;
import lavesdk.serialization.Serializable;
import lavesdk.serialization.Serializer;
import lavesdk.utils.MathUtils;

/**
 * Represents an edge between two vertices in a graph.
 * <br><br>
 * <b>Identifier</b>:<br>
 * Each edge has an unique identifier based on the associated graph. It is most efficient to use this identifier to reference
 * an edge in a data structure like a list, set, and so on. Use {@link Graph#getEdgeByID(int)} to get the edge of a specific id.
 * <br><br>
 * Use {@link #getPredecessor()} to access the vertex where the edge is an outgoing one and use {@link #getSuccessor()}
 * to access the vertex where the edge is an incoming one.<br>
 * To iterate over the graph use {@link #getPredecessor(Vertex)}/{@link #getSuccessor(Vertex)} to be free of how the edges are
 * created.<br>
 * With {@link #getWeight()} you can get the weight of this edge if the graph is a weighted one.
 * <br><br>
 * <b>Serialize edge data</b>:<br>
 * If you build a new sub type of {@link Edge} keep in mind that you should override {@link #serialize(Serializer)} and {@link #deserialize(Serializer)}
 * so that the additional data of the new type can be stored in a graph file.
 * 
 * @see Graph
 * @see Serializable
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class Edge implements Serializable {
	
	/** the predecessor vertex */
	protected final Vertex predecessor;
	/** the successor vertex */
	protected final Vertex successor;
	/** flag that indicates if the edge is directed or undirected */
	private boolean directed;
	/** the original value of {@link #directed} */
	private boolean orgDirectedFlag;
	/** the weight of the edge */
	private float weight;
	/** the corresponding graph */
	private Graph<? extends Vertex, ? extends Edge> graph;
	/** the id of the edge which is unique based on the associated graph */
	private int id;
	/** the index of the edge in the list of edges of the associated graph */
	private int index;
	/** flag that indicates whether the edge is currently modified */
	private boolean modified;
	
	/**
	 * Creates a new (undirected) edge with a weight of <code>0.0f</code>.
	 * 
	 * @param predecessor the predecessor (the vertex at which this is an outgoing edge)
	 * @param successor the successor (the vertex at which this is an incoming edge)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Edge(final Vertex predecessor, final Vertex successor) throws IllegalArgumentException {
		this(predecessor, successor, false);
	}
	
	/**
	 * Creates a new edge with a weight of <code>0.0f</code>.
	 * 
	 * @param predecessor the predecessor (the vertex at which this is an outgoing edge)
	 * @param successor the successor (the vertex at which this is an incoming edge)
	 * @param directed flag that indicates whether the edge should be directed (<code>true</code>) or undirected (<code>false</code>) (<b>has only an effect in mixed graphs otherwise the type of the edge is predefined</b>)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Edge(final Vertex predecessor, final Vertex successor, final boolean directed) throws IllegalArgumentException {
		this(predecessor, successor, directed, 0.0f);
	}
	
	/**
	 * Creates a new (undirected) edge.
	 * 
	 * @param predecessor the predecessor (the vertex at which this is an outgoing edge)
	 * @param successor the successor (the vertex at which this is an incoming edge)
	 * @param weight the weight of the edge
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Edge(final Vertex predecessor, final Vertex successor, final float weight) throws IllegalArgumentException {
		this(predecessor, successor, false, weight);
	}
	
	/**
	 * Creates a new edge.
	 * <br><br>
	 * <b>Type of the edge</b>:<br>
	 * The type of the edge could be directed or undirected. Please note, if you specify a directed
	 * edge and add it to a {@link Graph} which has an undirected edge type then the type of the edge is automatically changed
	 * to undirected.
	 * 
	 * @param predecessor the predecessor (the vertex at which this is an outgoing edge)
	 * @param successor the successor (the vertex at which this is an incoming edge)
	 * @param directed flag that indicates whether the edge should be directed (<code>true</code>) or undirected (<code>false</code>) (<b>has only an effect in mixed graphs otherwise the type of the edge is predefined</b>)
	 * @param weight the weight of the edge
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Edge(final Vertex predecessor, final Vertex successor, final boolean directed, final float weight) throws IllegalArgumentException {
		if(predecessor == null || successor == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.predecessor = predecessor;
		this.successor = successor;
		this.weight = weight;
		this.orgDirectedFlag = this.directed = directed;
		this.graph = null;
		this.id = -1;
		this.index = -1;
		this.modified = false;
	}
	
	/**
	 * Gets a unique identifier of this edge based on the associated graph. That means there are not two different edges e1, e2 with
	 * <code>e1.getID() == e2.getID()</code>.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It is most efficient to use the identifier to reference an edge in a data structure like a list, set, ...
	 * 
	 * @return the identifier of the edge or <code>-1</code> if this edge is not yet associated with a graph
	 * @since 1.0
	 */
	public final int getID() {
		return id;
	}
	
	/**
	 * Gets the current index of the edge in the list of edges of the associated graph meaning that
	 * <code>graph.getEdge(e.getIndex()) == e</code>.
	 * 
	 * @return the edge's index or <code>-1</code> if the edge is not associated with a graph
	 * @since 1.0
	 */
	public final int getIndex() {
		return index;
	}
	
	/**
	 * Gets the predecessor of the edge that means the vertex at which this is an outgoing edge.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If you want to iterate over an undirected or mixed graph it is recommended to use {@link #getPredecessor(Vertex)}.
	 * 
	 * @see #getPredecessor(Vertex)
	 * @return the predecessor as specified by the constructor
	 * @since 1.0
	 */
	public Vertex getPredecessor() {
		return predecessor;
	}
	
	/**
	 * Gets the successor of the edge that means the vertex at which this is an incoming edge.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If you want to iterate over an undirected or mixed graph it is recommended to use {@link #getSuccessor(Vertex)}.
	 * 
	 * @see #getSuccessor(Vertex)
	 * @return the successor as specified by the constructor
	 * @since 1.0
	 */
	public Vertex getSuccessor() {
		return successor;
	}
	
	/**
	 * Gets the predecessor by an origin. <b>Use this method to iterate over the {@link Graph} regardless of
	 * whether the edge is directed or not.</b>
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If the edge is directed the predecessor is clear otherwise if the edge is undirected the
	 * origin decides which is the predecessor.<br>
	 * <u>Example</u>: The vertices v1 and v2 are connected by an undirected edge and the edge is created as
	 * <code>new Edge(v1, v2, 0, false)</code>. If you want to know the predecessor of v2 it is v1 but if
	 * you want to know the predecessor of v1 by iterating over the incoming edges you cannot invoke
	 * <code>edge.getPredecessor()</code> because this is v1 itself. Therefore you have to invoke <code>edge.getPredecessor(v1)</code>.
	 * <pre>
	 * // iterate over the successors of v1
	 * for(Vertex v1 : vertices)
	 *     v1.getOutgoingEdge(i).getSuccessor(v1)...
	 * 
	 * // iterate over the predecessors of v2
	 * for(Vertex v2 : vertices)
	 *     v2.getIncomingEdge(i).getPredecessor(v2)...
	 * </pre>
	 * 
	 * @param origin the origin from which the predecessor is considered
	 * @return the predecessor of the origin or <code>null</code> if the vertex specified as the origin has nothing to do with this edge
	 * @since 1.0
	 */
	public Vertex getPredecessor(final Vertex origin) {
		// if the edge is directed then the origin has no effect!
		if(directed)
			return predecessor;
		else {
			if(origin == predecessor)
				return successor;
			else if(origin == successor)
				return predecessor;
			else
				return null;
		}
	}
	
	/**
	 * Gets the successor by an origin. <b>Use this method to iterate over the {@link Graph} regardless of
	 * whether the edge is directed or not.</b>
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If the edge is directed the successor is clear otherwise if the edge is undirected the
	 * origin decides which is the successor.<br>
	 * <u>Example</u>: The vertices v1 and v2 are connected by an undirected edge and the edge is created as
	 * <code>new Edge(v1, v2, 0, false)</code>. If you want to know the successor of v1 it is v2 but if
	 * you want to know the successor of v2 by iterating over the outgoing edges you cannot invoke
	 * <code>edge.getSuccessor()</code> because this is v2 itself. Therefore you have to invoke <code>edge.getSuccessor(v2)</code>.
	 * <pre>
	 * // iterate over the successors of v1
	 * for(Vertex v1 : vertices)
	 *     v1.getOutgoingEdge(i).getSuccessor(v1)...
	 * 
	 * // iterate over the predecessors of v1
	 * for(Vertex v1 : vertices)
	 *     v1.getIncomingEdge(i).getPredecessor(v1)...
	 * </pre>
	 * 
	 * @param origin the origin from which the successor is considered
	 * @return the successor of the origin or <code>null</code> if the vertex specified as the origin has nothing to do with this edge
	 * @since 1.0
	 */
	public Vertex getSuccessor(final Vertex origin) {
		// if the edge is directed then the origin has no effect!
		if(directed)
			return successor;
		else {
			if(origin == predecessor)
				return successor;
			else if(origin == successor)
				return predecessor;
			else
				return null;
		}
	}
	
	/**
	 * Gets the weight of the edge.
	 * 
	 * @return the weight
	 * @since 1.0
	 */
	public float getWeight() {
		return weight;
	}
	
	/**
	 * Sets the weight of the edge.
	 * 
	 * @param weight the weight
	 * @since 1.0
	 */
	public void setWeight(final float weight) {
		this.weight = weight;
	}
	
	/**
	 * Indicates if the edge is directed or undirected.
	 * 
	 * @return <code>true</code> if the edge is directed or <code>false</code> if the edge is undirected
	 * @since 1.0
	 */
	public final boolean isDirected() {
		return directed;
	}
	
	/**
	 * Sets if the edge is directed or undirected.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This is only possible if the corresponding graph supports that type of edge
	 * 
	 * @param directed <code>true</code> if the edge is directed or <code>false</code> if the edge is undirected
	 * @since 1.0
	 */
	public final void setDirected(final boolean directed) {
		orgDirectedFlag = directed;
		this.directed = getDirected();
	}
	
	/**
	 * Indicates if this edge is a loop meaning that the predecessor is equal the successor.
	 * 
	 * @return <code>true</code> if the edge is a loop otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean isLoop() {
		return predecessor == successor;
	}
	
	/**
	 * Returns a string representation of the edge more precisely the weight of the vertex.
	 * 
	 * @return a string representation
	 * @since 1.0
	 */
	@Override
	public String toString() {
		return MathUtils.formatFloat(weight);
	}
	
	/**
	 * Indicates whether the given edge equals this edge.
	 * <br>
	 * This is the case if this edge is directed and <code>e.directed == this.directed && e.predecessor == this.predecessor && e.successor == this.successor && e.weight == this.weight</code>
	 * or if this edge is undirected and <code>e.directed == this.directed && ((e.predecessor == this.predecessor && e.successor == this.successor) || (e.predecessor == this.successor && e.successor == this.predecessor)) && e.weight == this.weight</code>.
	 * 
	 * @param e the edge
	 * @return <code>true</code> if e equals this edge otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean equals(final Edge e) {
		if(e == null)
			return false;
		else if(e == this)
			return !modified;	// if the specified edge is this edge and this edge is currently modified then the two edges are not the same (otherwise the modification cannot be validated)
		else
			return equalsIgnoreWeight(e) && e.weight == this.weight;
	}
	
	/**
	 * Indicates whether the given edge equals this edge.
	 * <br>
	 * This is the case if this edge is directed and <code>e.directed == this.directed && e.predecessor.equals(this.predecessor) && e.successor.equals(this.successor)</code>
	 * or if this edge is undirected and <code>e.directed == this.directed && ((e.predecessor.equals(this.predecessor) && e.successor.equals(this.successor)) || (e.predecessor.equals(this.successor) && e.successor.equals(this.predecessor)))</code>.
	 * 
	 * @param e the edge
	 * @return <code>true</code> if e equals this edge otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean equalsIgnoreWeight(final Edge e) {
		/* two edges are equal if:
		 * - both are directed and the predecessor and successor are equal
		 * - both are undirected and the predecessor and successor are equal or the predecessor
		 *   is the successor of the other edge and the successor the predecessor of the other edge
		 */
		if(e == null)
			return false;
		else if(e == this)
			return !modified;	// if the specified edge is this edge and this edge is currently modified then the two edges are not the same (otherwise the modification cannot be validated)
		else if(this.directed)
			return e.directed == this.directed && e.predecessor.equals(this.predecessor) && e.successor.equals(this.successor);
		else
			return e.directed == this.directed && ((e.predecessor.equals(this.predecessor) && e.successor.equals(this.successor)) || (e.predecessor.equals(this.successor) && e.successor.equals(this.predecessor)));
	}
	
	/**
	 * Checks if the given object equals this edge.
	 * 
	 * @param o the object
	 * @return <code>true</code> if o equals this edge otherwise <code>false</code>
	 * @since 1.0
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof Edge)
			return equals((Edge)o);
		
		return false;
	}

	@Override
	public void serialize(Serializer s) {
		s.addFloat("weight", weight);
		s.addBoolean("orgDirectedFlag", orgDirectedFlag);
		s.addBoolean("directed", directed);
	}

	@Override
	public void deserialize(Serializer s) {
		weight = s.getFloat("weight");
		orgDirectedFlag = s.getBoolean("orgDirectedFlag");
		directed = s.getBoolean("directed");
		
		// update the directed flag
		directed = getDirected();
	}
	
	/**
	 * Gets the corresponding graph of the edge.
	 * 
	 * @return the graph
	 * @since 1.0
	 */
	protected final Graph<? extends Vertex, ? extends Edge> getGraph() {
		return graph;
	}
	
	/**
	 * Sets the identifier of the edge.
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
	 * Sets the corresponding graph of the edge and checks the directed flag, that means the edge can only be directed
	 * if the corresponding graph is of type {@link Type#DIRECTED} or {@link Type#MIXED}.
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
		
		// update the directed flag
		directed = getDirected();
	}
	
	/**
	 * Indicates whether the edge is currently modified.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return <code>true</code> if the edge is modifying otherwise <code>false</code>
	 * @since 1.0
	 */
	final boolean isModifying() {
		return modified;
	}
	
	/**
	 * Gets the state of the directed flag.
	 * 
	 * @return the state of the directed flag
	 * @since 1.0
	 */
	private boolean getDirected() {
		if(graph == null)
			return directed;
		
		boolean state;

		// the edge can only be directed if the type of the corresponding graph is not undirected
		// but if the type is directed then the edge must be always directed
		switch(graph.getType()) {
			case DIRECTED:
				state = true;
				break;
			case UNDIRECTED:
				state = false;
				break;
			case MIXED:
			default:
				final boolean oldDirected = directed;
				
				// we change the flag of the edge and then we check whether this modification is allowed but only if the flag changed
				// otherwise it can remain as it is (because elsewhere the edge is duplicated in the graph)
				directed = orgDirectedFlag;
				if(oldDirected != directed) {
					modified = true;
					if(graph.validateEdgeModification(id)) {
						graph.updateEdgeDirection(id);
						state = orgDirectedFlag;
					}
					else
						state = oldDirected;
					directed = oldDirected;
					modified = false;
				}
				else
					state = oldDirected;
		}
		
		return state;
	}

}
