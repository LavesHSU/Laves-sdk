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
 * Interface:	GraphViewListener
 * Task:		Listen to events from the graph view
 * Created:		04.12.13
 * LastChanges:	04.12.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Vertex;

/**
 * Listener to listen to events of a {@link GraphView}.
 * <br><br>
 * Available events:
 * <ul>
 * 		<li>{@link #vertexAdded(lavesdk.algorithm.plugin.views.GraphView.VisualVertex)}</li>
 * 		<li>{@link #vertexRemoved(lavesdk.algorithm.plugin.views.GraphView.VisualVertex)}</li>
 * 		<li>{@link #edgeAdded(lavesdk.algorithm.plugin.views.GraphView.VisualEdge)}</li>
 * 		<li>{@link #edgeRemoved(lavesdk.algorithm.plugin.views.GraphView.VisualEdge)}</li>
 * </ul>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public interface GraphViewListener<V extends Vertex, E extends Edge> {
	
	/**
	 * A new vertex is added to the graph view.
	 * 
	 * @param vertex the vertex that is added
	 * @since 1.0
	 */
	public void vertexAdded(final GraphView<V, E>.VisualVertex vertex);
	
	/**
	 * A vertex is removed from the graph view.
	 * 
	 * @param vertex the vertex that is removed
	 * @since 1.0
	 */
	public void vertexRemoved(final GraphView<V, E>.VisualVertex vertex);
	
	/**
	 * A vertex is selected in the graph view.
	 * 
	 * @param vertex the vertex
	 * @since 1.0
	 */
	public void vertexSelected(final GraphView<V, E>.VisualVertex vertex);
	
	/**
	 * A new edge is added to the graph.
	 * 
	 * @param edge the edge that is added
	 * @since 1.0
	 */
	public void edgeAdded(final GraphView<V, E>.VisualEdge edge);
	
	/**
	 * An edge is removed from the graph view.
	 * 
	 * @param edge the edge that is removed
	 * @since 1.0
	 */
	public void edgeRemoved(final GraphView<V, E>.VisualEdge edge);
	
	/**
	 * An edge is selected in the graph view.
	 * 
	 * @param edge the edge
	 * @since 1.0
	 */
	public void edgeSelected(final GraphView<V, E>.VisualEdge edge);

}
