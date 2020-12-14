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
 * Class:		ResidualNetwork
 * Task:		Representation of a residual network based on a network
 * Created:		15.10.14
 * LastChanges:	15.10.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph.network;

import java.util.List;

import lavesdk.math.graph.MultiGraph;
import lavesdk.math.graph.Vertex;

/**
 * Represents a residual network of a {@link Network}.
 * <br><br>
 * Use {@link #getNetwork()} to get the related network of the residual network meaning the graph the residual network
 * is based on.
 * 
 * @see Network
 * @author jdornseifer
 * @version 1.0
 * @since 1.2
 */
public class ResidualNetwork extends MultiGraph<Vertex, RNEdge> {
	
	/** the related network */
	private final Network<? extends Node, ? extends Arc> network;
	
	/**
	 * Creates a new residual network.
	 * 
	 * @param network the related network
	 * @since 1.0
	 */
	public ResidualNetwork(final Network<? extends Node, ? extends Arc> network) throws IllegalArgumentException {
		super(true);
		
		if(network == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.network = network;
	}
	
	/**
	 * Gets the related network.
	 * 
	 * @return the network
	 * @since 1.0
	 */
	public Network<? extends Node, ? extends Arc> getNetwork() {
		return network;
	}
	
	@Override
	protected boolean isEdgeAllowed(RNEdge edge) {
		final List<RNEdge> edges = getEdges(edge.getPredecessor(), edge.getSuccessor());
		
		return (edges == null || edges.size() < 2);
	}
	
	@Override
	protected void beforeEdgeAdded(RNEdge edge) {
		super.beforeEdgeAdded(edge);
		
		// if the edge does not have a related arc then find one in the related network
		if(edge.getRelationID() < 1)
			edge.setRelationID(findRelationIDOfNewEdge(edge.getPredecessor(), edge.getSuccessor()));
	}
	
	@Override
	protected void afterEdgeRemoved(RNEdge edge) {
		super.afterEdgeRemoved(edge);
		
		// check whether there is an edge between the two vertices of the removed edge that may not have 
		// a valid relation id (try to update the invalid relation identifiers)
		final List<RNEdge> edges = getEdges(edge.getPredecessor(), edge.getSuccessor());
		if(edges != null)
			for(RNEdge e : edges)
				if(e.getRelationID() < 0)
					e.setRelationID(findRelationIDOfNewEdge(e.getPredecessor(), e.getSuccessor()));
	}

	/**
	 * Finds the relation id of a new residual network edge in the residual network.
	 * 
	 * @param predecessor the predecessor
	 * @param successor the successor
	 * @return the relation id or <code>< 0</code> if there is no free id
	 * @since 1.0
	 */
	private int findRelationIDOfNewEdge(final Vertex predecessor, final Vertex successor) {
		final Node p = network.getVertexByCaption(predecessor.getCaption());
		final Node s = network.getVertexByCaption(successor.getCaption());
		
		if(p == null || s == null)
			return -1;
		
		final Arc a = network.getEdge(p.getID(), s.getID());
		final Arc ra = network.getEdge(s.getID(), p.getID());
		final int aID = (a == null) ? -1 : a.getID();
		final int raID = (ra == null) ? -1 : ra.getID();
		final List<RNEdge> lre = getEdges(predecessor, successor);
		
		// if there is no edge between the predecessor and the successor currently then the new residual network
		// edge represents the original arc in the network
		if(lre == null)
			return (aID >= 0) ? aID : raID;	// if arc is not existing in original direction then return the reverse arc (might be < 0 too) (this could be the case if the user adds the reverse rn edge of a single arc between two nodes firstly)
		else {
			// otherwise if there is already one edge then the new rn edge should represent the reverse arc in the network
			if(lre.size() == 1) {
				final RNEdge re = lre.get(0);
				if(re.getRelationID() == aID)
					return raID;
				else if(re.getRelationID() == raID)
					return aID;
				else
					return (raID >= 0) ? raID : aID;
			}
			else
				return -1;	// all correct edges are added meaning a new one is an wrong edge
		}
	}

}
