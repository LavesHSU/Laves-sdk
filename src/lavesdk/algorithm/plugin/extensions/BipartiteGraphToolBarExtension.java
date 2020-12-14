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
 * Class:		BipartiteGraphToolBarExtension
 * Task:		Extend toolbar by bipartite graph functionality
 * Created:		28.03.14
 * LastChanges:	17.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.extensions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import lavesdk.algorithm.plugin.views.GraphView;
import lavesdk.language.LanguageFile;
import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Vertex;
import lavesdk.resources.Resources;
import lavesdk.utils.GraphUtils;

/**
 * Extends the toolbar of a host application by graph functionality to check whether a graph is bipartite.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #BipartiteGraphToolBarExtension(GraphView, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>GRAPHTOOLBAR_IS_BIPARTITE</i>: the tooltip text of the graph toolbar button which checks if the graph is bipartite</li>
 * 		<li><i>GRAPHTOOLBAR_STATE_YES</i>: the word yes to show the state of an statement (like: Is it a complete graph?)</li>
 * 		<li><i>GRAPHTOOLBAR_STATE_NO</i>: the word no to show the state of an statement (like: Is it a complete graph?)</li>
 * </ul>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <V> the type of vertices
 * @param <E> the type of edges
 */
public class BipartiteGraphToolBarExtension<V extends Vertex, E extends Edge> extends ToolBarExtension {
	
	/** the graph view with which this extension is working */
	private final GraphView<V, E> graphView;
	/** toolbar button for checking if graph is bipartite */
	private final JButton isBipartiteGraphBtn;
	/** language dependent label of the expression "yes" */
	private final String stateYes;
	/** language dependent label of the expression "no" */
	private final String stateNo;
	
	/**
	 * Creates a new bipartite graph toolbar extension.
	 * 
	 * @param graphView the graph view on which the extension is applied
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public BipartiteGraphToolBarExtension(final GraphView<V, E> graphView) throws IllegalArgumentException {
		this(graphView, null, "");
	}
	
	/**
	 * Creates a new bipartite graph toolbar extension.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages or tooltips. The following language labels are available:
	 * <ul>
	 * 		<li><i>GRAPHTOOLBAR_IS_BIPARTITE</i>: the tooltip text of the graph toolbar button which checks if the graph is bipartite</li>
	 * 		<li><i>GRAPHTOOLBAR_STATE_YES</i>: the word yes to show the state of an statement (like: Is it a complete graph?)</li>
	 * 		<li><i>GRAPHTOOLBAR_STATE_NO</i>: the word no to show the state of an statement (like: Is it a complete graph?)</li>
	 * </ul>
	 * 
	 * @param graphView the graph view on which the extension is applied
	 * @param langFile the language file with labels for the tooltips of the toolbar buttons
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public BipartiteGraphToolBarExtension(final GraphView<V, E> graphView, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		this(graphView, langFile, langID, true);
	}
	
	/**
	 * Creates a new bipartite graph toolbar extension.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages or tooltips. The following language labels are available:
	 * <ul>
	 * 		<li><i>GRAPHTOOLBAR_IS_BIPARTITE</i>: the tooltip text of the graph toolbar button which checks if the graph is bipartite</li>
	 * 		<li><i>GRAPHTOOLBAR_STATE_YES</i>: the word yes to show the state of an statement (like: Is it a complete graph?)</li>
	 * 		<li><i>GRAPHTOOLBAR_STATE_NO</i>: the word no to show the state of an statement (like: Is it a complete graph?)</li>
	 * </ul>
	 * 
	 * @param graphView the graph view on which the extension is applied
	 * @param langFile the language file with labels for the tooltips of the toolbar buttons
	 * @param langID the language id
	 * @param startsWithSeparator <code>true</code> if the extension should be separated from the other elements in the toolbar otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public BipartiteGraphToolBarExtension(final GraphView<V, E> graphView, final LanguageFile langFile, final String langID, final boolean startsWithSeparator) throws IllegalArgumentException {
		super(startsWithSeparator);
		
		if(graphView == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.graphView = graphView;
		
		// create toolbar buttons
		isBipartiteGraphBtn = new JButton(Resources.getInstance().BIPARTITE_GRAPH_ICON);
		
		// set button tooltips
		isBipartiteGraphBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_IS_BIPARTITE", langID, "Is Graph bipartite?") + "</html>");
		
		// load expression labels
		stateYes = LanguageFile.getLabel(langFile, "STATE_YES", langID, "Yes");
		stateNo = LanguageFile.getLabel(langFile, "STATE_NO", langID, "No");
		
		// create toolbar extension look
		addButton(isBipartiteGraphBtn);
		
		// add listeners
		isBipartiteGraphBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final boolean state = GraphUtils.isBipartite(BipartiteGraphToolBarExtension.this.graphView.getGraph());
				StatePopup.showState(state, BipartiteGraphToolBarExtension.this.isBipartiteGraphBtn, state ? BipartiteGraphToolBarExtension.this.stateYes : BipartiteGraphToolBarExtension.this.stateNo);
			}
		});
	}

}
