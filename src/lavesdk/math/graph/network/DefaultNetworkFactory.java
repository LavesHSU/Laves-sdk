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

import lavesdk.math.graph.GraphFactory;

/**
 * Default implementation of a {@link GraphFactory} to create {@link Node}s and {@link Arc}s for a {@link Network}.
 * 
 * @see GraphFactory
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class DefaultNetworkFactory extends GraphFactory<Node, Arc> {

	@Override
	public Node createVertex(String caption) throws IllegalArgumentException {
		return new Node(caption);
	}

	@Override
	public Arc createEdge(Node predecessor, Node successor) throws IllegalArgumentException {
		return createEdge(predecessor, successor, 0.0f);
	}

	@Override
	public Arc createEdge(Node predecessor, Node successor, boolean directed) throws IllegalArgumentException {
		return createEdge(predecessor, successor, directed, 0.0f);
	}

	@Override
	public Arc createEdge(Node predecessor, Node successor, float weight) throws IllegalArgumentException {
		return createEdge(predecessor, successor, true, weight);
	}

	@Override
	public Arc createEdge(Node predecessor, Node successor, boolean directed, float weight) throws IllegalArgumentException {
		return new Arc(predecessor, successor, weight, 0.0f);
	}

}
