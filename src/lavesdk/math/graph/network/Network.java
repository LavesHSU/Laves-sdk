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

import lavesdk.math.graph.SimpleGraph;
import lavesdk.math.graph.Vertex;
import lavesdk.math.graph.network.enums.FlowType;

/**
 * Represents a network.
 * <br><br>
 * A network is based on a simple directed graph with at least a source node and a sink node. The {@link Arc}s have a weight
 * and a flow value.
 * <br><br>
 * To check if the flow conservation condition of the current state of the network is true use {@link #checkFlowConservationCondition()}.
 * 
 * @see SimpleGraph
 * @author jdornseifer
 * @version 1.2
 * @since 1.0
 */
public class Network<V extends Node, E extends Arc> extends SimpleGraph<V, E> {
	
	private final FlowType flowType;
	/** the source node of the network */
	private final V source;
	/** the sink nod of the network */
	private final V sink;
	
	/**
	 * Creates a new network.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The source and the sink nodes are added automatically to the network by calling {@link #add(Vertex)}.
	 * 
	 * @param type the type of the flow in the network
	 * @param source the source node
	 * @param sink the sink node
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if type is null</li>
	 * 		<li>if source is null</li>
	 * 		<li>if sink is null</li>
	 * 		<li>if source is equal to sink</li>
	 * </ul>
	 * @since 1.0
	 */
	public Network(final FlowType type, final V source, final V sink) throws IllegalArgumentException {
		super(true);
		
		if(type == null || source == null || sink == null || source == sink)
			throw new IllegalArgumentException("No valid argument!");
		
		this.flowType = type;
		this.source = source;
		this.sink = sink;
		
		// add the source and sink node to the network
		add(source);
		add(sink);
		
		// the source and the sink do not have an excess
		source.setHasExcess(false);
		sink.setHasExcess(false);
	}
	
	/**
	 * Gets the type of the flow in the network.
	 * 
	 * @return the flow type
	 * @since 1.0
	 */
	public final FlowType getFlowType() {
		return flowType;
	}
	
	/**
	 * Gets the source node of the network.
	 * 
	 * @return the source node
	 * @since 1.0
	 */
	public final V getSource() {
		return source;
	}
	
	/**
	 * Gets the sink node of the network.
	 * 
	 * @return the sink node
	 * @since 1.0
	 */
	public final V getSink() {
		return sink;
	}
	
	/**
	 * Determines the excess of all nodes of the network.
	 * 
	 * @see Node#determineExcess(Node)
	 * @since 1.2
	 */
	public void determineExcesses() {
		for(int i = 0; i < getOrder(); i++)
			Node.determineExcess(getVertex(i));
	}
	
	/**
	 * Checks if the flow conservation condition of the current state of the network is fulfilled.
	 * <br><br>
	 * This means if the {@link FlowType} is {@link FlowType#FLOW}:<br>
	 * <i>For all nodes except the source and the sink: the amount of the flow into the node is <b>equal</b> to the amount of the flow out of it.</i>
	 * <br><br>
	 * If the {@link FlowType} is {@link FlowType#PREFLOW} it means:<br>
	 * <i>For all nodes except the source and the sink: the amount of the flow into the node is <b>greater or equal</b> to the amount of the flow out of it</i>
	 * 
	 * @return <code>true</code> if the flow conservation condition is fulfilled otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean checkFlowConservationCondition() {
		V n;
		float sumIncomingFlow;
		float sumOutgoingFlow;
		
		for(int i = 0; i < getOrder(); i++) {
			n = getVertex(i);
			
			// the flow conservation condition has only be checked for vertices except the source and the sink
			if(n == source || n == sink)
				continue;
			
			sumIncomingFlow = 0;
			sumOutgoingFlow = 0;
			
			for(int j = 0; j < n.getIncomingEdgeCount(); j++)
				sumIncomingFlow += n.getIncomingEdge(j).getFlow();
			
			for(int j = 0; j < n.getOutgoingEdgeCount(); j++)
				sumOutgoingFlow += n.getOutgoingEdge(j).getFlow();
			
			// check the flow conservation
			switch(flowType) {
				case FLOW:
					if(sumIncomingFlow != sumOutgoingFlow)
						return false;
					break;
				case PREFLOW:
					if(sumIncomingFlow < sumOutgoingFlow)
						return false;
					break;
			}
		}
		
		return true;
	}
	
	/**
	 * Gets the residual network of the current state of the network.
	 * 
	 * @return a new instance representing the residual network
	 * @since 1.1
	 */
	public ResidualNetwork getResidualNetwork() {
		V n;
		E e;
		Vertex pred;
		Vertex succ;
		
		final ResidualNetwork residualNetwork = new ResidualNetwork(this);
		
		// add the source and the sink
		residualNetwork.add(new Vertex(source.getCaption()));
		residualNetwork.add(new Vertex(sink.getCaption()));
		
		// go through all nodes except source and sink and add it to the residual network
		for(int i = 0; i < getOrder(); i++) {
			n = getVertex(i);
			
			// source and sink node are already added
			if(n == source || n == sink)
				continue;
			
			residualNetwork.add(new Vertex(n.getCaption()));
		}
		
		// go through all arcs and add edges if necessary
		for(int i = 0; i < getSize(); i++) {
			e = getEdge(i);
			pred = residualNetwork.getVertexByCaption(e.getPredecessor().getCaption());
			succ = residualNetwork.getVertexByCaption(e.getSuccessor().getCaption());
			
			// if there is enough capacity to increase the flow from the predecessor to the successor
			// then create an edge between the vertices
			if(e.getWeight() != e.getFlow())
				residualNetwork.add(new RNEdge(pred, succ, e.getWeight() - e.getFlow(), e.getID()));
			// if there is a valid flow between the predecessor and the successor then create an inverted edge
			// that represents the reflow
			if(e.getFlow() > 0)
				residualNetwork.add(new RNEdge(succ, pred, e.getFlow(), e.getID()));
		}
		
		return residualNetwork;
	}
	
	/**
	 * Gets the strength of the flow.
	 * 
	 * @return the strength of the flow in the network
	 * @since 1.0
	 */
	public float getFlowStrength() {
		float sumIncomingFlow = 0;
		float sumOutgoingFlow = 0;
		
		for(int j = 0; j < source.getIncomingEdgeCount(); j++)
			sumIncomingFlow += source.getIncomingEdge(j).getFlow();
		
		for(int j = 0; j < source.getOutgoingEdgeCount(); j++)
			sumOutgoingFlow += source.getOutgoingEdge(j).getFlow();
		
		return sumOutgoingFlow - sumIncomingFlow;
	}
	
	@Override
	protected boolean isRemovable(V v) {
		return (v != source) && (v != sink);
	}

}
