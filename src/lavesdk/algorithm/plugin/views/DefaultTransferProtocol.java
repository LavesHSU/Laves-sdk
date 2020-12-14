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

package lavesdk.algorithm.plugin.views;

import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Vertex;

/**
 * A default implementation of a {@link GraphTransferProtocol} that makes it possible to transfer a graph of one graph view to another
 * or to the same.
 * <br><br>
 * The default protocol transfers all data from all vertices and edges. Define your own protocol if you do not want that certain objects or
 * properties are transfered.
 * <br><br>
 * <b>Transfer a graph</b>:<br>
 * In the example below we want to transfer a current graph (e.g. a simple, undirected graph) to a mixed graph in the <b>same</b> graph view:
 * <pre>
 * // create the GTP and prepare it
 * final DefaultGraphTransferProtocol&lt;Vertex, Edge&gt; gtp = new DefaultGraphTransferProtocol&lt;Vertex, Edge&gt;(myGraphView);
 * gtp.prepare();
 * 
 * // change the type of the graph view to a mixed graph
 * myGraphView.setGraph(new Graph(Type.MIXED));
 * 
 * // afterwards transfer the data of the old graph to the mixed graph
 * myGraphView.transferGraph(gtp);
 * </pre>
 * 
 * @see VertexOnlyTransferProtocol
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
public class DefaultTransferProtocol<V extends Vertex, E extends Edge> extends GraphTransferProtocol<V, E> {
	
	/**
	 * Creates a new default transfer protocol.
	 * 
	 * @param graphView the graph view its graph should be transfered
	 * @param clearExistingGraph <code>true</code> if the graph of the view the data is transferred to should be cleared otherwise <code>false</code>
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public DefaultTransferProtocol(final GraphView<V, E> graphView, final boolean clearExistingGraph) throws NullPointerException {
		super(graphView, clearExistingGraph);
	}

	@Override
	protected TransferData getTransferData(V vertex) {
		final TransferData td = new TransferData();
		graphView.getVisualVertex(vertex).serialize(td);
		return td;
	}

	@Override
	protected TransferData getTransferData(E edge) {
		final TransferData td = new TransferData();
		graphView.getVisualEdge(edge).serialize(td);
		return td;
	}

}
