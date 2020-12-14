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
 * Class:		RandomGraphToolBarExtension
 * Task:		Extend toolbar by functionality to create randomly generated graphs
 * Created:		11.11.15
 * LastChanges:	04.04.16
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.extensions;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.algorithm.plugin.views.GraphView;
import lavesdk.gui.dialogs.OptionDialog;
import lavesdk.gui.dialogs.enums.AllowedGraphType;
import lavesdk.language.LanguageFile;
import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Vertex;
import lavesdk.resources.Resources;
import lavesdk.utils.GraphUtils;

/**
 * Extends the toolbar of a host application by functionality to create randomly generated graphs.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #RandomGraphToolBarExtension(PluginHost, GraphView, AllowedGraphType, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH</i>: the tooltip text of the button to create a random graph</li>
 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH_DLG_TITLE</i>: the title of the dialog where the user has to enter the number of vertices when he wants to create a random graph</li>
 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH_DLG_NOV</i>: the label of the field the user has to enter the number of vertices when he wants to create a random graph</li>
 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH_DLG_MINWEIGHT</i>: the label of the field the user has to enter the min. weight of edges</li>
 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH_DLG_MAXWEIGHT</i>: the label of the field the user has to enter the max. weight of edges</li>
 * 		<li><i>DLG_CB_CREATEDIRECTEDGRAPH</i>: the label of the checkbox where the user can determine whether to create either a directed or an undirected graph</li>
 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
 * </ul>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.4
 * @param <V> the type of vertices
 * @param <E> the type of edges
 */
public class RandomGraphToolBarExtension<V extends Vertex, E extends Edge> extends ToolBarExtension {

	/** the host */
	private final PluginHost host;
	/** the graph view with which this extension is working */
	private final GraphView<V, E> graphView;
	/** the language file */
	private final LanguageFile langFile;
	/** the language id */
	private final String langID;
	/** toolbar button for creating a random graph */
	private final JButton randomGraph;
	/** the allowed graph type to create */
	private AllowedGraphType type;
	/** language dependent title for the dialog */
	private final String dlgTitle;
	/** language dependent label for the number of vertices */
	private final String labelNumberOfVertices;
	/** language dependent label for the min. weight of the edges */
	private final String labelMinWeight;
	/** language dependent label for the max. weight of the edges */
	private final String labelMaxWeight;
	
	/**
	 * Creates a new random graph toolbar extension.
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
	public RandomGraphToolBarExtension(final PluginHost host, final GraphView<V, E> graphView, final AllowedGraphType type) throws IllegalArgumentException {
		this(host, graphView, type, null, "");
	}
	
	/**
	 * Creates a new random graph toolbar extension.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages or tooltips. The following language labels are available:
	 * <ul>
	 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH</i>: the tooltip text of the button to create a random graph</li>
	 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH_DLG_TITLE</i>: the title of the dialog where the user has to enter the number of vertices when he wants to create a random graph</li>
	 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH_DLG_NOV</i>: the label of the field the user has to enter the number of vertices when he wants to create a random graph</li>
	 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH_DLG_MINWEIGHT</i>: the label of the field the user has to enter the min. weight of edges</li>
	 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH_DLG_MAXWEIGHT</i>: the label of the field the user has to enter the max. weight of edges</li>
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
	public RandomGraphToolBarExtension(final PluginHost host, final GraphView<V, E> graphView, final AllowedGraphType type, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		this(host, graphView, type, langFile, langID, true);
	}
	
	/**
	 * Creates a new random graph toolbar extension.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages or tooltips. The following language labels are available:
	 * <ul>
	 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH</i>: the tooltip text of the button to create a random graph</li>
	 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH_DLG_TITLE</i>: the title of the dialog where the user has to enter the number of vertices when he wants to create a random graph</li>
	 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH_DLG_NOV</i>: the label of the field the user has to enter the number of vertices when he wants to create a random graph</li>
	 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH_DLG_MINWEIGHT</i>: the label of the field the user has to enter the min. weight of edges</li>
	 * 		<li><i>GRAPHTOOLBAR_RANDOMGRAPH_DLG_MAXWEIGHT</i>: the label of the field the user has to enter the max. weight of edges</li>
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
	public RandomGraphToolBarExtension(final PluginHost host, final GraphView<V, E> graphView, final AllowedGraphType type, final LanguageFile langFile, final String langID, final boolean startsWithSeparator) throws IllegalArgumentException {
		super(startsWithSeparator);
		
		if(graphView == null || type == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.host = host;
		this.graphView = graphView;
		this.langFile = langFile;
		this.langID = langID;
		this.type = type;
		
		// create toolbar button
		randomGraph = new JButton(Resources.getInstance().RANDOM_GRAPH_ICON);
		
		// set button tooltip
		randomGraph.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_RANDOMGRAPH", langID, "Create a random Graph") + "</html>");
		
		// load labels
		dlgTitle = LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_RANDOMGRAPH_DLG_TITLE", langID, "Create a random Graph");
		labelNumberOfVertices = LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_RANDOMGRAPH_DLG_NOV", langID, "Number of vertices:");
		labelMinWeight = LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_RANDOMGRAPH_DLG_MINWEIGHT", langID, "Min. weight of edges:");
		labelMaxWeight = LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_RANDOMGRAPH_DLG_MAXWEIGHT", langID, "Max. weight of edges:");
		
		// create toolbar extension look
		addButton(randomGraph);
		
		// add listeners
		randomGraph.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				RandomGraphToolBarExtension.this.showCreateDialog();
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
		return randomGraph.getToolTipText();
	}
	
	@Override
	public Icon getMenuOptionIcon() {
		return randomGraph.getIcon();
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
		final Dialog dlg = new Dialog();
		dlg.setVisible(true);
		
		if(!dlg.isCanceled() && dlg.getNOV() > 0) {
			graphView.setGraph(GraphUtils.createRandomGraph(dlg.getNOV(), dlg.getDirectedGraphChecked(), graphView.getGraphFactory(), dlg.getMinEdgeWeight(), dlg.getMaxEdgeWeight()));
			graphView.layoutGraph(RandomGraphToolBarExtension.this.graphView.createCircleGraphLayout());
			graphView.repaint();
		}
	}
	
	/**
	 * The Dialog to create a random graph.
	 * 
	 * @author jdornseifer
	 * @version 1.3
	 * @since 1.0
	 */
	private class Dialog extends OptionDialog {

		private static final long serialVersionUID = 1L;
		
		/** the text field to enter the number of vertices */
		private final JTextField txtNOV;
		/** the checkbox for changing the graph type */
		private final JCheckBox cbDirectedGraph;
		/** the text field to enter the min. weight */
		private final JTextField txtMinEdgeWeight;
		/** the text field to enter the max. weight */
		private final JTextField txtMaxEdgeWeight;
		/** number of vertices */
		private int nov;
		/** min. weight of edges */
		private float minEdgeWeight;
		/** max. weight of edges */
		private float maxEdgeWeight;

		/**
		 * Creates a new dialog.
		 * 
		 * @since 1.0
		 */
		public Dialog() {
			super(RandomGraphToolBarExtension.this.host, "", RandomGraphToolBarExtension.this.langFile, RandomGraphToolBarExtension.this.langID, true);
			
			northPanel.setLayout(new BorderLayout());
			northPanel.add(new JLabel(RandomGraphToolBarExtension.this.dlgTitle), BorderLayout.CENTER);
			
			txtNOV = new JTextField();
			cbDirectedGraph = new JCheckBox(LanguageFile.getLabel(RandomGraphToolBarExtension.this.langFile, "DLG_CB_CREATEDIRECTEDGRAPH", RandomGraphToolBarExtension.this.langID, "Create a directed graph?"));
			txtMinEdgeWeight = new JTextField();
			txtMaxEdgeWeight = new JTextField();
			nov = -1;
			minEdgeWeight = 1.0f;
			maxEdgeWeight = 0.0f;
			
			cbDirectedGraph.setSelected(type == AllowedGraphType.DIRECTED_ONLY);
			cbDirectedGraph.setEnabled(type == AllowedGraphType.BOTH);
			
			centerPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(new JLabel(RandomGraphToolBarExtension.this.labelNumberOfVertices), gbc);
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(txtNOV, gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(cbDirectedGraph, gbc);gbc.gridx = 0;

			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(new JLabel(RandomGraphToolBarExtension.this.labelMinWeight), gbc);
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(txtMinEdgeWeight, gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(new JLabel(RandomGraphToolBarExtension.this.labelMaxWeight), gbc);
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 3;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(txtMaxEdgeWeight, gbc);
			
			pack();
			setSize(300, getHeight());
		}
		
		/**
		 * Gets the number of vertices.
		 * 
		 * @return the number of vertices or <code>-1</code> if the user enters an invalid value
		 * @since 1.0
		 */
		public int getNOV() {
			return nov;
		}
		
		/**
		 * Gets the min. weight of edges.
		 * 
		 * @return the min. weight of edges
		 * @since 1.3
		 */
		public float getMinEdgeWeight() {
			return minEdgeWeight;
		}
		
		/**
		 * Gets the max. weight of edges.
		 * 
		 * @return the max. weight of edges
		 * @since 1.3
		 */
		public float getMaxEdgeWeight() {
			return maxEdgeWeight;
		}
		
		/**
		 * Gets the value of the checkbox where the user can decide whether the graph should be directed or undirected.
		 * 
		 * @return <code>true</code> if the graph should be directed otherwise <code>false</code>
		 * @since 1.0
		 */
		public boolean getDirectedGraphChecked() {
			return cbDirectedGraph.isSelected();
		}

		@Override
		protected void doOk() {
			try {
				nov = new Integer(txtNOV.getText());
			}
			catch(NumberFormatException ex) {
				nov = -1;
			}
			
			try {
				minEdgeWeight = new Float(txtMinEdgeWeight.getText());
			}
			catch(NumberFormatException ex) {
				minEdgeWeight = 0.0f;
			}
			
			try {
				maxEdgeWeight = new Float(txtMaxEdgeWeight.getText());
			}
			catch(NumberFormatException ex) {
				maxEdgeWeight = 0.0f;
			}
			
			if(maxEdgeWeight < minEdgeWeight)
				maxEdgeWeight = minEdgeWeight;
		}
	}

}
