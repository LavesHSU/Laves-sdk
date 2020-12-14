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
 * Class:		GraphLayout
 * Task:		Layouts a graph
 * Created:		30.10.13
 * LastChanges:	30.10.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Graph;
import lavesdk.math.graph.Vertex;

/**
 * Base class to layout a {@link Graph} in {@link GraphView} that means to position the vertices of a graph automatically.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public abstract class GraphLayout {
	
	/**
	 * Executes the layout algorithm to position the vertices of the given graph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * A vertex has no position itself! You have to use the visual equivalent to set the position of a specific vertex.
	 * Use {@link GraphView#getVisualVertex(Vertex)} of the graph view to get the visual component of a vertex of the graph.
	 * 
	 * @param graph the graph
	 * @param graphView the graph view where the graph is displayed
	 * @since 1.0
	 */
	public abstract <V extends Vertex, E extends Edge> void layout(final Graph<V, E> graph, final GraphView<V, E> graphView);

}
