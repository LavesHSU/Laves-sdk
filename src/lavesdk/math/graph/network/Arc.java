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
 * Class:		Arc
 * Task:		Representation of an edge in a network
 * Created:		22.10.13
 * LastChanges:	21.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph.network;

import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Vertex;
import lavesdk.serialization.Serializer;
import lavesdk.utils.MathUtils;

/**
 * Represents an edge in a {@link Network}.
 * <br><br>
 * The edge/arc has a weight (or capacity) and a flow value. The flow f(e) of the edge e with a weight of w(e) has the property that
 * <code>0.0f <= f(e) <= w(e)</code>.
 * 
 * @see Network
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class Arc extends Edge {
	
	/** the flow value */
	private float flow;

	/**
	 * Creates a new directed edge with a weight and a flow of <code>0.0f</code>.
	 * 
	 * @param predecessor the predecessor (the node at which this is an outgoing edge)
	 * @param successor the successor (the node at which this is an incoming edge)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Arc(Node predecessor, Node successor) throws IllegalArgumentException {
		this(predecessor, successor, 0.0f, 0.0f);
	}

	/**
	 * Creates a new directed edge with a flow of <code>0.0f</code>.
	 * 
	 * @param predecessor the predecessor (the node at which this is an outgoing edge)
	 * @param successor the successor (the node at which this is an incoming edge)
	 * @param weight the weight of the flow edge
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Arc(Node predecessor, Node successor, float weight) throws IllegalArgumentException {
		this(predecessor, successor, weight, 0.0f);
	}
	
	/**
	 * Creates a new directed edge.
	 * 
	 * @param predecessor the predecessor (the node at which this is an outgoing edge)
	 * @param successor the successor (the node at which this is an incoming edge)
	 * @param weight the weight of the flow edge
	 * @param flow the flow value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * 		<li>if flow is greater than the weight</li>
	 * 		<li>if flow is <code>< 0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public Arc(Node predecessor, Node successor, float weight, final float flow) throws IllegalArgumentException {
		super(predecessor, successor, true, weight);
		
		setFlow(flow);
	}
	
	@Override
	public Node getPredecessor() {
		return (Node)super.getPredecessor();
	}
	
	@Override
	public Node getPredecessor(Vertex origin) {
		return (Node)super.getPredecessor(origin);
	}
	
	@Override
	public Node getSuccessor() {
		return (Node)super.getSuccessor();
	}
	
	@Override
	public Node getSuccessor(Vertex origin) {
		return (Node)super.getSuccessor(origin);
	}
	
	/**
	 * Sets the weight of the edge.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If the weight is less than the flow value then the flow value is automatically set to the new weight.
	 * 
	 * @param weight the weight
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if weight is <code>< 0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void setWeight(float weight) throws IllegalArgumentException {
		if(weight < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		super.setWeight(weight);
		
		// check to flow capacity condition of the flow conservation conditions
		if(weight < flow)
			flow = weight;
	}
	
	/**
	 * Gets the flow value.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The flow value has to be less or equal than the weight of the edge.
	 * 
	 * @return the flow value
	 * @since 1.0
	 */
	public float getFlow() {
		return flow;
	}
	
	/**
	 * Sets the flow value.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The flow value has to be less or equal than the weight of the edge (this is said by the flow capacity condition of
	 * the flow conservation conditions).
	 * 
	 * @param flow the value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if flow is greater than the weight</li>
	 * 		<li>if flow is <code>< 0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public void setFlow(final float flow) throws IllegalArgumentException {
		if(flow > getWeight())
			throw new IllegalArgumentException("flow value is greater then the weight -> break of the flow preservation condition");
		else if(flow < 0.0f)
			throw new IllegalArgumentException("flow value is less then the zero -> break of the flow preservation condition");
		
		this.flow = flow;
	}
	
	/**
	 * Indicates whether the given arc equals this arc.
	 * 
	 * @param arc the arc
	 * @return <code>true</code> if arc equals this arc otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean equals(final Arc arc) {
		return super.equals(arc) && this.flow == arc.flow;
	}

	@Override
	public void serialize(Serializer s) {
		super.serialize(s);
		
		s.addFloat("flow", flow);
	}

	@Override
	public void deserialize(Serializer s) {
		super.deserialize(s);
		
		flow = s.getFloat("flow");
	}
	
	@Override
	public String toString() {
		return MathUtils.formatFloat(flow) + "/" + super.toString();
	}

}
