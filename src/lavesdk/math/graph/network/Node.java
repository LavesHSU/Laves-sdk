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

package lavesdk.math.graph.network;

import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Graph;
import lavesdk.math.graph.Vertex;
import lavesdk.serialization.Serializer;

/**
 * Represents a vertex in a {@link Network}.
 * <br><br>
 * Use {@link #determineExcess(Node)} to determine the excess of a specific node.
 * 
 * @see Vertex
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class Node extends Vertex {
	
	/** the excess */
	private float excess;
	/** flag that indicates wheher a node has an excess (the source and sink of a network do not have an excess) */
	private boolean hasExcess;

	/**
	 * Creates a new node.
	 * 
	 * @param caption the caption of the vertex
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if caption is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Node(String caption) throws IllegalArgumentException {
		super(caption);
		
		excess = 0.0f;
		hasExcess = true;
	}
	
	@Override
	public Arc getIncomingEdge(int index) throws IndexOutOfBoundsException {
		return (Arc)super.getIncomingEdge(index);
	}
	
	@Override
	public Arc getOutgoingEdge(int index) throws IndexOutOfBoundsException {
		return (Arc)super.getOutgoingEdge(index);
	}
	
	/**
	 * Sets the caption of the node.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The caption of the source node or the sink node of a network cannot be changed!
	 * 
	 * @param caption the caption of the node
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if caption is null</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void setCaption(String caption) throws IllegalArgumentException {
		final Graph<? extends Vertex, ? extends Edge> graph = getGraph();
		Network<? extends Vertex, ? extends Edge> network = null;
		
		if(graph instanceof Network)
			network = (Network<? extends Vertex, ? extends Edge>)graph;
		
		// if the node is the source or the sink of the network then it should not be possible to change the caption
		if(network != null && (network.getSource() == this || network.getSink() == this) && !getCaption().isEmpty())
			return;
		
		super.setCaption(caption);
	}
	
	/**
	 * Gets the excess of the node.
	 * <br><br>
	 * The excess is defined as:<br>
	 * <i>excess(v) = sum of the incoming flow of v - sum of the outgoing flow of v</i>
	 * <br><br>
	 * To update the excess of all nodes invoke {@link Network#determineExcesses()}.
	 * 
	 * @return the excess of the node
	 * @since 1.0
	 */
	public float getExcess() {
		return excess;
	}
	
	/**
	 * Sets the excess of the node.
	 * <br><br>
	 * The excess is defined as:<br>
	 * <i>excess(v) = sum of the incoming flow of v - sum of the outgoing flow of v</i>
	 * <br><br>
	 * To update the excess of all nodes invoke {@link Network#determineExcesses()}.
	 * 
	 * @param excess the excess of the node
	 * @since 1.1
	 */
	public void setExcess(final float excess) {
		this.excess = hasExcess ? excess : 0.0f;
	}
	
	/**
	 * Indicates whether the node has an excess.
	 * <br><br>
	 * <b>All nodes except the source and the sink of a network can have an excess!</b>
	 * 
	 * @return <code>true</code> if the node can have an excess otherwise <code>false</code>
	 * @since 1.1
	 */
	public boolean hasExcess() {
		return hasExcess;
	}
	
	/**
	 * Determines the excess of a specified node and saves it using {@link #setExcess(float)}.
	 * <br><br>
	 * The excess of a node v is defined as:<br>
	 * <i>excess(v) = sum of the incoming flow of v - sum of the outgoing flow of v</i>
	 * 
	 * @param n the node
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if n is null</li>
	 * </ul>
	 * @since 1.1
	 */
	public static void determineExcess(final Node n) throws IllegalArgumentException {
		float sumIncomingFlow = 0.0f;
		float sumOutgoingFlow = 0.0f;
		
		for(int j = 0; j < n.getIncomingEdgeCount(); j++)
			sumIncomingFlow += n.getIncomingEdge(j).getFlow();
		
		for(int j = 0; j < n.getOutgoingEdgeCount(); j++)
			sumOutgoingFlow += n.getOutgoingEdge(j).getFlow();
		
		n.setExcess(sumIncomingFlow - sumOutgoingFlow);
	}

	@Override
	public void serialize(Serializer s) {
		super.serialize(s);
		
		s.addFloat("excess", excess);
		s.addBoolean("hasExcess", hasExcess);
	}

	@Override
	public void deserialize(Serializer s) {
		super.deserialize(s);
		
		excess = s.getFloat("excess");
		hasExcess = s.getBoolean("hasExcess", true);
	}
	
	/**
	 * Sets whether the node can have an excess.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param hasExcess <code>true</code> if the node can have an excess otherwise <code>false</code>
	 * @since 1.1
	 */
	void setHasExcess(final boolean hasExcess) {
		this.hasExcess = hasExcess;
	}

}
