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
import lavesdk.math.graph.Vertex;

/**
 * Default implementation of a {@link GraphFactory} to create {@link Vertex}es and {@link RNEdge}s for a {@link ResidualNetwork}.
 * 
 * @see GraphFactory
 * @author jdornseifer
 * @version 1.0
 * @since 1.2
 */
public class DefaultRNFactory extends GraphFactory<Vertex, RNEdge> {
	
	@Override
	public Vertex createVertex(String caption) throws IllegalArgumentException {
		return new Vertex(caption);
	}

	@Override
	public RNEdge createEdge(Vertex predecessor, Vertex successor) throws IllegalArgumentException {
		return createEdge(predecessor, successor, true);
	}

	@Override
	public RNEdge createEdge(Vertex predecessor, Vertex successor, boolean directed) throws IllegalArgumentException {
		return createEdge(predecessor, successor, directed, 0.0f);
	}

	@Override
	public RNEdge createEdge(Vertex predecessor, Vertex successor, float weight) throws IllegalArgumentException {
		return createEdge(predecessor, successor, true, weight);
	}

	@Override
	public RNEdge createEdge(Vertex predecessor, Vertex successor, boolean directed, float weight) throws IllegalArgumentException {
		return new RNEdge(predecessor, successor, weight, -1);
	}

}
