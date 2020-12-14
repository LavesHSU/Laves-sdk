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
 * Class:		RNEdge
 * Task:		Edge in a residual network
 * Created:		16.07.14
 * LastChanges:	16.07.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph.network;

import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Vertex;

/**
 * Represents an edge in a residual network of a {@link Network}.
 * <br><br>
 * This class extends {@link Edge} by a relation id that stores the id of the related arc in the network.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.2
 */
public class RNEdge extends Edge {
	
	/** the relation id of the arc in the network */
	private int relationID;
	
	/**
	 * Creates a new residual network edge.
	 * 
	 * @param predecessor the predecessor
	 * @param successor the successor
	 * @param weight the weight
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public RNEdge(final Vertex predecessor, final Vertex successor, final float weight) throws IllegalArgumentException {
		this(predecessor, successor, weight, -1);
	}

	/**
	 * Creates a new residual network edge.
	 * 
	 * @param predecessor the predecessor
	 * @param successor the successor
	 * @param weight the weight
	 * @param relationID the id of the related arc in the network
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public RNEdge(final Vertex predecessor, final Vertex successor, final float weight, final int relationID) throws IllegalArgumentException {
		super(predecessor, successor, true, weight);
		
		this.relationID = relationID;
	}
	
	/**
	 * Gets the id of the related arc in the network.
	 * 
	 * @return the id of the related arc or <code>< 1</code> if there is not id set
	 * @since 1.0
	 */
	public int getRelationID() {
		return relationID;
	}
	
	/**
	 * Sets the id of the related arc in the network.
	 * 
	 * @param id the id of the related arc
	 * @since 1.0
	 */
	public void setRelationID(final int id) {
		relationID = id;
	}
	
	@Override
	public boolean equals(Edge e) {
		if(e instanceof RNEdge)
			return super.equals(e) && this.relationID == ((RNEdge)e).relationID;
		else
			return false;
	}

}
