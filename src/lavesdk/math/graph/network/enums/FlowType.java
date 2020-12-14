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
 * Enum:		FlowType
 * Task:		The type of the flow in a network
 * Created:		23.10.13
 * LastChanges:	23.10.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph.network.enums;

import lavesdk.math.graph.network.Network;

/**
 * The type of the flow in a {@link Network}.
 * <br><br>
 * The available types are:
 * <ul>
 * 		<li>{@link #FLOW}</li>
 * 		<li>{@link #PREFLOW}</li>
 * </ul>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public enum FlowType {
	
	/**
	 * The type of the flow in the network is normal that means a flow with the properties
	 * <ol>
	 * 		<li>the flow f(e) of the edge e with a weight w(e) is <code>0 <= f(e) <= w(e)</code></li>
	 * 		<li>for all nodes except the source and the target: the amount of the flow into the node is <b>equal</b> to the amount of the flow out of it</li>
	 * </ol>
	 */
	FLOW,
	
	/**
	 * The type of the flow in the network is a preflow that means a flow with the properties
	 * <ol>
	 * 		<li>the flow f(e) of the edge e with a weight w(e) is <code>0 <= f(e) <= w(e)</code></li>
	 * 		<li>for all nodes except the source and the target: the amount of the flow into the node is <b>greater or equal</b> to the amount of the flow out of it</li>
	 * </ol>
	 */
	PREFLOW

}
