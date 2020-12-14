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
 * Class:		VertexOnlyTransferProtocol
 * Task:		An implementation of a GTP that only transfers the vertex data
 * Created:		12.05.14
 * LastChanges:	20.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Vertex;

/**
 * A {@link GraphTransferProtocol} that only transfers the vertices of a {@link GraphView} to another graph view or the same.
 * 
 * @see DefaultTransferProtocol
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
public class VertexOnlyTransferProtocol<V extends Vertex, E extends Edge> extends GraphTransferProtocol<V, E> {
	
	/** flag that indicates whether only the vertex positions should be transferred */
	private boolean onlyPositions;
	
	/**
	 * Creates a new vertex only transfer protocol.
	 * 
	 * @param graphView the graph view its vertices should be transfered
	 * @param clearExistingGraph <code>true</code> if the graph of the view the data is transferred to should be cleared otherwise <code>false</code>
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public VertexOnlyTransferProtocol(final GraphView<V, E> graphView, final boolean clearExistingGraph) throws NullPointerException {
		this(graphView, clearExistingGraph, false);
	}
	
	/**
	 * Creates a new vertex only transfer protocol.
	 * 
	 * @param graphView the graph view its vertices should be transfered
	 * @param clearExistingGraph <code>true</code> if the graph of the view the data is transferred to should be cleared otherwise <code>false</code>
	 * @param onlyPositions <code>true</code> if only the vertex positions should be transferred otherwise <code>false</code>
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public VertexOnlyTransferProtocol(final GraphView<V, E> graphView, final boolean clearExistingGraph, final boolean onlyPositions) throws NullPointerException {
		super(graphView, clearExistingGraph);
		
		this.onlyPositions = onlyPositions;
	}

	@Override
	protected TransferData getTransferData(V vertex) {
		final TransferData td = new TransferData();
		graphView.getVisualVertex(vertex).serialize(td);
		
		if(onlyPositions) {
			final String[] keys = td.keys();
			
			// remove all visual data that is not related to a position
			for(String key : keys)
				if(key.equals("background") || key.equals("foreground") || key.equals("edgeWidth") || key.equals("scale"))
					td.removeTransferData(key);
		}
		
		return td;
	}

	@Override
	protected TransferData getTransferData(E edge) {
		return null;
	}

}
