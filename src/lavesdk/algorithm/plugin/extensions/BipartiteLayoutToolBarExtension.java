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

package lavesdk.algorithm.plugin.extensions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import lavesdk.algorithm.plugin.views.BipartiteGraphLayout;
import lavesdk.algorithm.plugin.views.GraphView;
import lavesdk.language.LanguageFile;
import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Vertex;
import lavesdk.resources.Resources;

/**
 * Extends the toolbar of a host application by graph functionality to layout the vertices of a graph bipartite.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #BipartiteLayoutToolBarExtension(GraphView, boolean, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>GRAPHTOOLBAR_BIPARTITE_LAYOUT</i>: the tooltip text of the button to apply a bipartite layout to the graph</li>
 * </ul>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <V> the type of vertices
 * @param <E> the type of edges
 */
public class BipartiteLayoutToolBarExtension<V extends Vertex, E extends Edge> extends ToolBarExtension {
	
	/** the graph view with which this extension is working */
	private final GraphView<V, E> graphView;
	/** toolbar button for bipartite layout */
	private final JButton bipartiteLayoutBtn;
	
	/**
	 * Creates a new bipartite layout toolbar extension.
	 * 
	 * @param graphView the graph view on which the extension is applied
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public BipartiteLayoutToolBarExtension(final GraphView<V, E> graphView) throws IllegalArgumentException {
		this(graphView, true, null, "");
	}
	
	/**
	 * Creates a new bipartite layout toolbar extension.
	 * 
	 * @param graphView the graph view on which the extension is applied
	 * @param nonIncidentVerticesToSubset1 <code>true</code> if non-incident vertices should be added to subset 1 or <code>false</code> to add non-incident vertices to subset 2
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public BipartiteLayoutToolBarExtension(final GraphView<V, E> graphView, final boolean nonIncidentVerticesToSubset1) throws IllegalArgumentException {
		this(graphView, nonIncidentVerticesToSubset1, null, "");
	}
	
	/**
	 * Creates a new bipartite layout toolbar extension.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages or tooltips. The following language labels are available:
	 * <ul>
	 * 		<li><i>GRAPHTOOLBAR_BIPARTITE_LAYOUT</i>: the tooltip text of the button to apply a bipartite layout to the graph</li>
	 * </ul>
	 * 
	 * @param graphView the graph view on which the extension is applied
	 * @param nonIncidentVerticesToSubset1 <code>true</code> if non-incident vertices should be added to subset 1 or <code>false</code> to add non-incident vertices to subset 2
	 * @param langFile the language file with labels for the tooltips of the toolbar buttons
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public BipartiteLayoutToolBarExtension(final GraphView<V, E> graphView, final boolean nonIncidentVerticesToSubset1, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		this(graphView, nonIncidentVerticesToSubset1, langFile, langID, true);
	}
	
	/**
	 * Creates a new bipartite layout toolbar extension.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages or tooltips. The following language labels are available:
	 * <ul>
	 * 		<li><i>GRAPHTOOLBAR_BIPARTITE_LAYOUT</i>: the tooltip text of the button to apply a bipartite layout to the graph</li>
	 * </ul>
	 * 
	 * @param graphView the graph view on which the extension is applied
	 * @param nonIncidentVerticesToSubset1 <code>true</code> if non-incident vertices should be added to subset 1 or <code>false</code> to add non-incident vertices to subset 2
	 * @param langFile the language file with labels for the tooltips of the toolbar buttons
	 * @param langID the language id
	 * @param startsWithSeparator <code>true</code> if the extension should be separated from the other elements in the toolbar otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public BipartiteLayoutToolBarExtension(final GraphView<V, E> graphView, final boolean nonIncidentVerticesToSubset1, final LanguageFile langFile, final String langID, final boolean startsWithSeparator) throws IllegalArgumentException {
		super(startsWithSeparator);
		
		if(graphView == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.graphView = graphView;
		
		// create toolbar button
		bipartiteLayoutBtn = new JButton(Resources.getInstance().BIPARTITE_LAYOUT_ICON);
		
		// set button tooltip
		bipartiteLayoutBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_BIPARTITE_LAYOUT", langID, "Apply Bipartite Layout to Graph (<b>only possible if graph is bipartite</b>)") + "</html>");
		
		// create toolbar extension look
		addButton(bipartiteLayoutBtn);
		
		// add listener
		bipartiteLayoutBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				BipartiteLayoutToolBarExtension.this.graphView.layoutGraph(new BipartiteGraphLayout(nonIncidentVerticesToSubset1));
				BipartiteLayoutToolBarExtension.this.graphView.repaint();
			}
		});
	}

}
