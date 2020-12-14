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
 * Class:		MatrixToGraphToolBarExtension
 * Task:		Extend toolbar by functionality to create a graph using an adjacency matrix
 * Created:		01.04.14
 * LastChanges:	11.11.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.extensions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;

import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.algorithm.plugin.views.GraphView;
import lavesdk.gui.dialogs.AdjacencyMatrixDialog;
import lavesdk.gui.dialogs.enums.AllowedGraphType;
import lavesdk.language.LanguageFile;
import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Vertex;
import lavesdk.resources.Resources;
import lavesdk.utils.GraphUtils;

/**
 * Extends the toolbar of a host application by functionality to create graphs by use of an adjacency matrix.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #MatrixToGraphToolBarExtension(PluginHost, GraphView, AllowedGraphType, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>GRAPHTOOLBAR_MATRIXTOGRAPH</i>: the tooltip text of the button to create a graph using an adjacency matrix</li>
 * 		<li><i>ADJACENCYMATRIXDLG_TITLE</i>: the title of the adjacency matrix dialog</li>
 * 		<li><i>ADJACENCYMATRIXDLG_DESCRIPTION</i>: the description of the adjacency matrix dialog</li>
 * 		<li><i>ADJACENCYMATRIXDLG_NOV</i>: the label of the field where the user has to enter the number of vertices</li>
 * 		<li><i>ADJACENCYMATRIXDLG_ALLOWZEROWEIGHTS</i>: the label of the checkbox to specify whether the adjacency matrix may contain zero-weight edges or not</li>
 * 		<li><i>ADJACENCYMATRIXDLG_ALLOWZEROWEIGHTS_DESC</i>: the label of the description of the zero-weight edge checkbox</li>
 * 		<li><i>DLG_CB_CREATEDIRECTEDGRAPH</i>: the label of the checkbox where the user can determine whether to create either a directed or an undirected graph</li>
 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
 * </ul>
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 * @param <V> the type of vertices
 * @param <E> the type of edges
 */
public class MatrixToGraphToolBarExtension<V extends Vertex, E extends Edge> extends ToolBarExtension {

	/** the host */
	private final PluginHost host;
	/** the language file */
	private final LanguageFile langFile;
	/** the language id */
	private final String langID;
	/** the graph view with which this extension is working */
	private final GraphView<V, E> graphView;
	/** toolbar button for creating a graph using an adjacency matrix */
	private final JButton matrixToGraph;
	/** the allowed graph type to create */
	private AllowedGraphType type;
	
	/**
	 * Creates a new matrix to graph toolbar extension.
	 * 
	 * @param host the host that is used to center the dialog to create graphs in the application or <code>null</code> (centers the dialog in the screen)
	 * @param graphView the graph view on which the extension is applied
	 * @param type the type that determines whether the user is allowed to set or change the value of the checkbox in the dialog that allows the user to decide whether the graph should be directed or undirected
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * 		<li>if type is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public MatrixToGraphToolBarExtension(final PluginHost host, final GraphView<V, E> graphView, final AllowedGraphType type) throws IllegalArgumentException {
		this(host, graphView, type, null, "");
	}
	
	/**
	 * Creates a new matrix to graph toolbar extension.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages or tooltips. The following language labels are available:
	 * <ul>
	 * 		<li><i>GRAPHTOOLBAR_MATRIXTOGRAPH</i>: the tooltip text of the button to create a graph using an adjacency matrix</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_TITLE</i>: the title of the adjacency matrix dialog</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_DESCRIPTION</i>: the description of the adjacency matrix dialog</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_NOV</i>: the label of the field where the user has to enter the number of vertices</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_ALLOWZEROWEIGHTS</i>: the label of the checkbox to specify whether the adjacency matrix may contain zero-weight edges or not</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_ALLOWZEROWEIGHTS_DESC</i>: the label of the description of the zero-weight edge checkbox</li>
	 * 		<li><i>DLG_CB_CREATEDIRECTEDGRAPH</i>: the label of the checkbox where the user can determine whether to create either a directed or an undirected graph</li>
	 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
	 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
	 * </ul>
	 * 
	 * @param host the host that is used to center the dialog to create graphs in the application or <code>null</code> (centers the dialog in the screen)
	 * @param graphView the graph view on which the extension is applied
	 * @param type the type that determines whether the user is allowed to set or change the value of the checkbox in the dialog that allows the user to decide whether the graph should be directed or undirected
	 * @param langFile the language file with labels for the tooltips of the toolbar buttons
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * 		<li>if type is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public MatrixToGraphToolBarExtension(final PluginHost host, final GraphView<V, E> graphView, final AllowedGraphType type, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		this(host, graphView, type, langFile, langID, true);
	}
	
	/**
	 * Creates a new matrix to graph toolbar extension.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages or tooltips. The following language labels are available:
	 * <ul>
	 * 		<li><i>GRAPHTOOLBAR_MATRIXTOGRAPH</i>: the tooltip text of the button to create a graph using an adjacency matrix</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_TITLE</i>: the title of the adjacency matrix dialog</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_DESCRIPTION</i>: the description of the adjacency matrix dialog</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_NOV</i>: the label of the field where the user has to enter the number of vertices</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_ALLOWZEROWEIGHTS</i>: the label of the checkbox to specify whether the adjacency matrix may contain zero-weight edges or not</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_ALLOWZEROWEIGHTS_DESC</i>: the label of the description of the zero-weight edge checkbox</li>
	 * 		<li><i>DLG_CB_CREATEDIRECTEDGRAPH</i>: the label of the checkbox where the user can determine whether to create either a directed or an undirected graph</li>
	 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
	 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
	 * </ul>
	 * 
	 * @param host the host that is used to center the dialog to create graphs in the application or <code>null</code> (centers the dialog in the screen)
	 * @param graphView the graph view on which the extension is applied
	 * @param type the type that determines whether the user is allowed to set or change the value of the checkbox in the dialog that allows the user to decide whether the graph should be directed or undirected
	 * @param langFile the language file with labels for the tooltips of the toolbar buttons
	 * @param langID the language id
	 * @param startsWithSeparator <code>true</code> if the extension should be separated from the other elements in the toolbar otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * 		<li>if type is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public MatrixToGraphToolBarExtension(final PluginHost host, final GraphView<V, E> graphView, final AllowedGraphType type, final LanguageFile langFile, final String langID, final boolean startsWithSeparator) throws IllegalArgumentException {
		super(startsWithSeparator);
		
		if(graphView == null || type == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.host = host;
		this.langFile = langFile;
		this.langID = langID;
		this.graphView = graphView;
		this.type = type;
		
		// create toolbar button
		matrixToGraph = new JButton(Resources.getInstance().MATRIX_TO_GRAPH_ICON);
		
		// set button tooltip
		matrixToGraph.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_MATRIXTOGRAPH", langID, "Create a Graph by use of an Adjacency Matrix") + "</html>");
		
		// create toolbar extension look
		addButton(matrixToGraph);
		
		// add listeners
		matrixToGraph.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MatrixToGraphToolBarExtension.this.showCreateDialog();
			}
		});
	}
	
	/**
	 * Gets the allowed graph type.
	 * <br><br>
	 * With this type you can decide which type of a graph a user can create.
	 * 
	 * @return the allowed graph type
	 * @since 1.0
	 */
	public AllowedGraphType getAllowedGraphType() {
		return type;
	}
	
	/**
	 * Sets the allowed graph type.
	 * <br><br>
	 * With this type you can decide which type of a graph a user can create.
	 * 
	 * @param type the allowed graph type
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if type is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setAllowedGraphType(final AllowedGraphType type) throws IllegalArgumentException {
		if(type == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.type = type;
	}
	
	@Override
	public boolean getShowInMenu() {
		return true;
	}
	
	@Override
	public String getMenuOptionText() {
		return matrixToGraph.getToolTipText();
	}
	
	@Override
	public Icon getMenuOptionIcon() {
		return matrixToGraph.getIcon();
	}
	
	@Override
	public void showMenuOption() {
		showCreateDialog();
	}
	
	/**
	 * Displays the dialog to create the graph.
	 * 
	 * @since 1.1
	 */
	private void showCreateDialog() {
		final AdjacencyMatrixDialog dlg = new AdjacencyMatrixDialog(host, langFile, langID, type);
		dlg.setVisible(true);
		
		if(dlg.getAdjacencyMatrix() != null) {
			graphView.setGraph(GraphUtils.createGraph(dlg.getAdjacencyMatrix(), graphView.getGraphFactory(), dlg.getDirectedGraphChecked(), dlg.getAllowZeroWeights()));
			graphView.layoutGraph(graphView.createCircleGraphLayout());
			graphView.repaint();
		}
	}

}
