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

import lavesdk.language.LanguageFile;
import lavesdk.math.graph.DefaultGraphFactory;
import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Graph;
import lavesdk.math.graph.Vertex;
import lavesdk.resources.Resources;

/**
 * The default implementation of a graph view.
 * <br><br>
 * The default graph view can handle with {@link Vertex} and {@link Edge} objects using the {@link DefaultGraphFactory}.
 * 
 * @see GraphView
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class DefaultGraphView extends GraphView<Vertex, Edge> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new default graph view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param graph the graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public DefaultGraphView(String title, Graph<Vertex, Edge> graph) throws IllegalArgumentException {
		this(title, graph, null);
	}
	
	/**
	 * Creates a new default graph view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param graph the graph
	 * @param graphLayout the graph layout to layout the vertices of the graph automatically or <code>null</code> for the default layout
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public DefaultGraphView(String title, Graph<Vertex, Edge> graph, GraphLayout graphLayout) throws IllegalArgumentException {
		this(title, graph, graphLayout, true, "en");
	}

	/**
	 * Creates a new default graph view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param graph the graph
	 * @param graphLayout the graph layout to layout the vertices of the graph automatically or <code>null</code> for the default layout
	 * @param closable <code>true</code> if the graph view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a graph view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public DefaultGraphView(String title, Graph<Vertex, Edge> graph, GraphLayout graphLayout, boolean closable, final String langID) throws IllegalArgumentException {
		this(title, graph, graphLayout, closable, Resources.getInstance().LANGUAGE_FILE, langID);
	}
	
	/**
	 * Creates a new default graph view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param graph the graph
	 * @param graphLayout the graph layout to layout the vertices of the graph automatically or <code>null</code> for the default layout
	 * @param closable <code>true</code> if the graph view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a graph view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langFile the language file or <code>null</code> if the default graph view should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public DefaultGraphView(String title, Graph<Vertex, Edge> graph, GraphLayout graphLayout, boolean closable, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		super(title, graph, new DefaultGraphFactory(), graphLayout, closable, langFile, langID);
	}

}
