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
 * Class:		DefaultGraphFactory
 * Task:		Methods to create vertices and edges
 * Created:		31.10.13
 * LastChanges:	10.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math.graph;

/**
 * Default implementation of a {@link GraphFactory} to create {@link Vertex}s and {@link Edge}s for a {@link Graph} or
 * a sub type.
 * 
 * @see GraphFactory
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class DefaultGraphFactory extends GraphFactory<Vertex, Edge> {

	@Override
	public Vertex createVertex(String caption) throws IllegalArgumentException {
		return new Vertex(caption);
	}

	@Override
	public Edge createEdge(Vertex predecessor, Vertex successor) throws IllegalArgumentException {
		return createEdge(predecessor, successor, 0.0f);
	}

	@Override
	public Edge createEdge(Vertex predecessor, Vertex successor, boolean directed) throws IllegalArgumentException {
		return createEdge(predecessor, successor, directed, 0.0f);
	}

	@Override
	public Edge createEdge(Vertex predecessor, Vertex successor, float weight) throws IllegalArgumentException {
		return createEdge(predecessor, successor, false, weight);
	}

	@Override
	public Edge createEdge(Vertex predecessor, Vertex successor, boolean directed, float weight) throws IllegalArgumentException {
		return new Edge(predecessor, successor, directed, weight);
	}

}
