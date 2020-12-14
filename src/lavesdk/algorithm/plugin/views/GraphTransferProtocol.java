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

import java.util.ArrayList;
import java.util.List;

import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Graph;
import lavesdk.math.graph.Vertex;
import lavesdk.serialization.Serializer;

/**
 * The graph transfer protocol specifies how a graph is transfered from one graph view to another.
 * <br><br>
 * You implement a protocol by inherit from {@link GraphTransferProtocol} and override {@link #getTransferData(Vertex)} and {@link #getTransferData(Edge)}.
 * In this methods you decide what data and which parts of the data should be transferred.
 * <br><br>
 * <b>TransferData</b>:<br>
 * Storage for properties of an object or multiple objects that should be transfered from one graph view to another.
 * 
 * @see DefaultTransferProtocol
 * @see VertexOnlyTransferProtocol
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public abstract class GraphTransferProtocol<V extends Vertex, E extends Edge> {
    
	/** the graph view its graph should be transferred */
	protected final GraphView<V, E> graphView;
	/** the related graph */
	protected final Graph<V, E> graph;
	/** flag that indicates whether an existing graph should be cleared before the data is transferred */
	protected final boolean clearExistingGraph;
    /** the last id of a transfer data */
	private int lastTransferID;
	/** the prepared vertex transfer data */
	private final List<TransferData> vertexTransferData;
	/** the prepared edge transfer data */
	private final List<TransferData> edgeTransferData;
	/** flag that indicates whether the protocol was prepared */
	private boolean isPrepared;
	/** the zoom value of the graph view at the time the protocol is prepared */
	private int zoom;
    
	/**
	 * Creates a new graph transfer protocol.
	 * 
	 * @param graphView the graph view its graph should be transferred
	 * @param clearExistingGraph <code>true</code> if the graph of the view the data is transferred to should be cleared otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
    public GraphTransferProtocol(final GraphView<V, E> graphView, final boolean clearExistingGraph) throws IllegalArgumentException {
        if(graphView == null)
            throw new IllegalArgumentException("No valid argument!");
        
        this.graphView = graphView;
        this.graph = graphView.getGraph();
        this.clearExistingGraph = clearExistingGraph;
        this.lastTransferID = 0;
        this.vertexTransferData = new ArrayList<TransferData>();
        this.edgeTransferData = new ArrayList<TransferData>();
        this.isPrepared = false;
        this.zoom = 0;
    }
    
    /**
     * Gets the graph view its graph should be transferred.
     * 
     * @return the graph view
     * @since 1.0
     */
    public final GraphView<V, E> getGraphView() {
    	return graphView;
    }
    
    /**
     * Gets the graph that should be transferred.
     * 
     * @return the graph
     * @since 1.0
     */
    public final Graph<V, E> getGraph() {
    	return graph;
    }
    
    /**
     * Prepares the protocol.
     * <br><br>
     * <b>Notice</b>:<br>
     * It is recommended that this method is invoked directly after creating the graph transfer protocol.<br>
     * In any case it has to be called before an associated {@link GraphView} changes its graph data structure using {@link GraphView#setGraph(Graph)}.
     * 
     * @return <code>true</code> if the protocol could be prepared otherwise <code>false</code> (a reason might be that you have already prepared the protocol before)
     * @since 1.0
     */
    public final boolean prepare() {
    	if(isPrepared)
    		return false;
    	
    	// prepare the transfer data
    	for(int i = 0; i < graph.getOrder(); i++)
			vertexTransferData.add(getTransferData(graph.getVertex(i)));
		for(int i = 0; i < graph.getSize(); i++)
			edgeTransferData.add(getTransferData(graph.getEdge(i)));
		
		// store zoom value
		zoom = graphView.getZoom();
		
		isPrepared = true;
		return isPrepared;
    }
    
    /**
     * Gets the transfer data for a given vertex.
     * 
     * @param vertex a vertex of the graph
     * @return the transfer data or <code>null</code> if the object should not be transfered
     * @since 1.0
     */
    protected abstract TransferData getTransferData(final V vertex);
    
    /**
     * Gets the transfer data for a given edge.
     * 
     * @param edge an edge of the graph
     * @return the transfer data or <code>null</code> if the object should not be transfered
     * @since 1.0
     */
    protected abstract TransferData getTransferData(final E edge);
    
    /**
     * Gets the vertex data that should be transferred.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
     * 
     * @return the vertex data to transfer (the number of vertices complies with the order of the associated graph)
     * @since 1.0
     */
    List<TransferData> getVertexTransferData() {
    	return vertexTransferData;
    }
    
    /**
     * Gets the edge data that should be transferred.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
     * 
     * @return the edge data to transfer (the number of edges complies with the size of the associated graph)
     * @since 1.0
     */
    List<TransferData> getEdgeTransferData() {
    	return edgeTransferData;
    }
    
    /**
     * Indicates whether the protocol is prepared.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
     * 
     * @return <code>true</code> if the protocol is prepared otherwise <code>false</code>
     * @since 1.0
     */
    boolean isPrepared() {
    	return isPrepared;
    }
    
    /**
     * Gets the zoom at the time the protocol was prepared because the vertex positions are based on that zoom value.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
     * 
     * @return the zoom value that has to be restored
     * @since 1.0
     */
    int getZoom() {
    	return zoom;
    }
    
    /**
     * Indicates whether an existing graph in the view the data is transferred to should be cleared.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
     * 
     * @return <ocde>true</code> if existing data should be removed otherwise <code>false</code>
     * @since 1.0
     */
    boolean getClearExistingGraph() {
    	return clearExistingGraph;
    }
    
    /**
     * Represents a storage for the properties of an object that should be transfered
     * from one graph view to another.
     * 
     * @author jdornseifer
     * @version 1.1
     * @since 1.0
     */
    public class TransferData extends Serializer {
        
		private static final long serialVersionUID = 1L;

		/**
		 * Creates a new transfer data.
		 * 
		 * @since 1.0
		 */
        public TransferData() {
            super(++lastTransferID, "" + lastTransferID);
        }
        
        /**
         * Removes a data field from the storage.
         * 
         * @param key the data key
         * @since 1.1
         */
        protected void removeTransferData(final String key) {
        	super.removeData(key);
        }
        
    }

}
