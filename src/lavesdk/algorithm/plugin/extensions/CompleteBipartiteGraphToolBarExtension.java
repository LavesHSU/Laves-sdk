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
 * Class:		CompleteBipartiteGraphToolBarExtension
 * Task:		Extend toolbar by complete bipartite graph functionality
 * Created:		28.03.14
 * LastChanges:	11.11.15
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
import javax.swing.JLabel;
import javax.swing.JTextField;

import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.algorithm.plugin.views.BipartiteGraphLayout;
import lavesdk.algorithm.plugin.views.GraphView;
import lavesdk.gui.dialogs.OptionDialog;
import lavesdk.language.LanguageFile;
import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Vertex;
import lavesdk.resources.Resources;
import lavesdk.utils.GraphUtils;

/**
 * Extends the toolbar of a host application by graph functionality to check whether a graph is complete bipartite and to create complete bipartite graphs.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #CompleteBipartiteGraphToolBarExtension(PluginHost, GraphView, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>GRAPHTOOLBAR_IS_COMPLETE_BIPARTITE</i>: the tooltip text of the graph toolbar button which checks if the graph is complete bipartite</li>
 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE</i>: the tooltip text of the graph toolbar button which creates a complete bipartite graph</li>
 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_TITLE</i>: the title of the dialog where the user has to enter the number of vertices in subset 1 and 2 when he wants to create a complete bipartite graph</li>
 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_SUBSET1</i>: the label of the field in the dialog where the user has to enter the number of vertices in subset 1 when he wants to create a complete bipartite graph</li>
 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_SUBSET2</i>: the label of the field in the dialog where the user has to enter the number of vertices in subset 2 when he wants to create a complete bipartite graph</li>
 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_MAXWEIGHT</i>: the label of the field the user has to enter the max. weight of edges</li>
 * 		<li><i>GRAPHTOOLBAR_STATE_YES</i>: the word yes to show the state of an statement (like: Is it a complete graph?)</li>
 * 		<li><i>GRAPHTOOLBAR_STATE_NO</i>: the word no to show the state of an statement (like: Is it a complete graph?)</li>
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
public class CompleteBipartiteGraphToolBarExtension<V extends Vertex, E extends Edge> extends ToolBarExtension {
	
	/** the host */
	private final PluginHost host;
	/** the graph view with which this extension is working */
	private final GraphView<V, E> graphView;
	/** the language file */
	private final LanguageFile langFile;
	/** the language id */
	private final String langID;
	/** toolbar button for checking if graph is complete bipartite */
	private final JButton isCompleteBipartiteGraphBtn;
	/** toolbar button for creating a complete bipartite graph*/
	private final JButton createCompleteBipartiteGraphBtn;
	/** language dependent label of the expression "yes" */
	private final String stateYes;
	/** language dependent label of the expression "no" */
	private final String stateNo;
	/** language dependent title for the dialog */
	private final String dlgTitle;
	/** language dependent label for the number of vertices in subset 1 */
	private final String labelSubset1;
	/** language dependent label for the number of vertices in subset 2 */
	private final String labelSubset2;
	/** language dependent label for the max. weight of the edges */
	private final String labelMaxWeight;
	
	/**
	 * Creates a new complete bipartite graph toolbar extension.
	 * 
	 * @param host the host that is used to center the dialog to create complete bipartite graphs in the application or <code>null</code> (centers the dialog in the screen)
	 * @param graphView the graph view on which the extension is applied
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public CompleteBipartiteGraphToolBarExtension(final PluginHost host, final GraphView<V, E> graphView) throws IllegalArgumentException {
		this(host, graphView, null, "");
	}
	
	/**
	 * Creates a new complete bipartite graph toolbar extension.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages or tooltips. The following language labels are available:
	 * <ul>
	 * 		<li><i>GRAPHTOOLBAR_IS_COMPLETE_BIPARTITE</i>: the tooltip text of the graph toolbar button which checks if the graph is complete bipartite</li>
	 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE</i>: the tooltip text of the graph toolbar button which creates a complete bipartite graph</li>
	 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_TITLE</i>: the title of the dialog where the user has to enter the number of vertices in subset 1 and 2 when he wants to create a complete bipartite graph</li>
	 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_SUBSET1</i>: the label of the field in the dialog where the user has to enter the number of vertices in subset 1 when he wants to create a complete bipartite graph</li>
	 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_SUBSET2</i>: the label of the field in the dialog where the user has to enter the number of vertices in subset 2 when he wants to create a complete bipartite graph</li>
	 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_MAXWEIGHT</i>: the label of the field the user has to enter the max. weight of edges</li>
	 * 		<li><i>GRAPHTOOLBAR_STATE_YES</i>: the word yes to show the state of an statement (like: Is it a complete graph?)</li>
	 * 		<li><i>GRAPHTOOLBAR_STATE_NO</i>: the word no to show the state of an statement (like: Is it a complete graph?)</li>
	 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
	 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
	 * </ul>
	 * 
	 * @param host the host that is used to center the dialog to create complete bipartite graphs in the application or <code>null</code> (centers the dialog in the screen)
	 * @param graphView the graph view on which the extension is applied
	 * @param langFile the language file with labels for the tooltips of the toolbar buttons
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public CompleteBipartiteGraphToolBarExtension(final PluginHost host, final GraphView<V, E> graphView, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		this(host, graphView, langFile, langID, true);
	}
	
	/**
	 * Creates a new complete bipartite graph toolbar extension.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages or tooltips. The following language labels are available:
	 * <ul>
	 * 		<li><i>GRAPHTOOLBAR_IS_COMPLETE_BIPARTITE</i>: the tooltip text of the graph toolbar button which checks if the graph is complete bipartite</li>
	 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE</i>: the tooltip text of the graph toolbar button which creates a complete bipartite graph</li>
	 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_TITLE</i>: the title of the dialog where the user has to enter the number of vertices in subset 1 and 2 when he wants to create a complete bipartite graph</li>
	 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_SUBSET1</i>: the label of the field in the dialog where the user has to enter the number of vertices in subset 1 when he wants to create a complete bipartite graph</li>
	 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_SUBSET2</i>: the label of the field in the dialog where the user has to enter the number of vertices in subset 2 when he wants to create a complete bipartite graph</li>
	 * 		<li><i>GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_MAXWEIGHT</i>: the label of the field the user has to enter the max. weight of edges</li>
	 * 		<li><i>GRAPHTOOLBAR_STATE_YES</i>: the word yes to show the state of an statement (like: Is it a complete graph?)</li>
	 * 		<li><i>GRAPHTOOLBAR_STATE_NO</i>: the word no to show the state of an statement (like: Is it a complete graph?)</li>
	 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
	 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
	 * </ul>
	 * 
	 * @param host the host that is used to center the dialog to create complete bipartite graphs in the application or <code>null</code> (centers the dialog in the screen)
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
	public CompleteBipartiteGraphToolBarExtension(final PluginHost host, final GraphView<V, E> graphView, final LanguageFile langFile, final String langID, final boolean startsWithSeparator) throws IllegalArgumentException {
		super(startsWithSeparator);
		
		if(graphView == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.host = host;
		this.graphView = graphView;
		this.langFile = langFile;
		this.langID = langID;
		
		// create toolbar buttons
		isCompleteBipartiteGraphBtn = new JButton(Resources.getInstance().COMPLETE_BIPARTITE_GRAPH_ICON);
		createCompleteBipartiteGraphBtn = new JButton(Resources.getInstance().CREATE_COMPLETE_BIPARTITE_GRAPH_ICON);
		
		// set button tooltips
		isCompleteBipartiteGraphBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_IS_COMPLETE_BIPARTITE", langID, "Is Graph complete bipartite?") + "</html>");
		createCompleteBipartiteGraphBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE", langID, "Create complete bipartite Graph") + "</html>");
		
		// load expression labels
		stateYes = LanguageFile.getLabel(langFile, "STATE_YES", langID, "Yes");
		stateNo = LanguageFile.getLabel(langFile, "STATE_NO", langID, "No");
		dlgTitle = LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_TITLE", langID, "Create complete bipartite Graph");
		labelSubset1 = LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_SUBSET1", langID, "Number of vertices in subset 1:");
		labelSubset2 = LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_SUBSET2", langID, "Number of vertices in subset 2:");
		labelMaxWeight = LanguageFile.getLabel(langFile, "GRAPHTOOLBAR_CREATE_COMPLETE_BIPARTITE_DLG_MAXWEIGHT", langID, "Max. weight of edges:");
		
		// create toolbar extension look
		addButton(isCompleteBipartiteGraphBtn);
		addButton(createCompleteBipartiteGraphBtn);
		
		// add listeners
		isCompleteBipartiteGraphBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final boolean state = GraphUtils.isCompleteBipartite(CompleteBipartiteGraphToolBarExtension.this.graphView.getGraph());
				StatePopup.showState(state, CompleteBipartiteGraphToolBarExtension.this.isCompleteBipartiteGraphBtn, state ? CompleteBipartiteGraphToolBarExtension.this.stateYes : CompleteBipartiteGraphToolBarExtension.this.stateNo);
			}
		});
		createCompleteBipartiteGraphBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CompleteBipartiteGraphToolBarExtension.this.showCreateDialog();
			}
		});
	}
	
	@Override
	public boolean getShowInMenu() {
		return true;
	}
	
	@Override
	public String getMenuOptionText() {
		return createCompleteBipartiteGraphBtn.getToolTipText();
	}
	
	@Override
	public Icon getMenuOptionIcon() {
		return createCompleteBipartiteGraphBtn.getIcon();
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
		
		if(!dlg.isCanceled() && dlg.getN() > 0 && dlg.getM() > 0) {
			graphView.setGraph(GraphUtils.createCompleteBipartiteGraph(dlg.getN(), dlg.getM(), graphView.getGraphFactory(), dlg.getMaxEdgeWeight()));
			graphView.layoutGraph(new BipartiteGraphLayout());
			graphView.repaint();
		}
	}
	
	/**
	 * The Dialog to create a complete bipartite graph.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 */
	private class Dialog extends OptionDialog {

		private static final long serialVersionUID = 1L;
		
		/** the text field to enter the number of vertices in subset 1 */
		private final JTextField txtSubset1;
		/** the text field to enter the number of vertices in subset 2 */
		private final JTextField txtSubset2;
		/** the text field to enter the max. weight */
		private final JTextField txtMaxEdgeWeight;
		/** number of vertices in subset 1 */
		private int n;
		/** number of vertices in subset 2 */
		private int m;
		/** max. weight of edges */
		private float maxEdgeWeight;

		/**
		 * Creates a new dialog.
		 * 
		 * @since 1.0
		 */
		public Dialog() {
			super(CompleteBipartiteGraphToolBarExtension.this.host, "", CompleteBipartiteGraphToolBarExtension.this.langFile, CompleteBipartiteGraphToolBarExtension.this.langID, true);
			
			northPanel.setLayout(new BorderLayout());
			northPanel.add(new JLabel(CompleteBipartiteGraphToolBarExtension.this.dlgTitle), BorderLayout.CENTER);
			
			txtSubset1 = new JTextField();
			txtSubset2 = new JTextField();
			txtMaxEdgeWeight = new JTextField();
			n = -1;
			m = -1;
			maxEdgeWeight = 0.0f;
			
			centerPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(new JLabel(CompleteBipartiteGraphToolBarExtension.this.labelSubset1), gbc);
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(txtSubset1, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(new JLabel(CompleteBipartiteGraphToolBarExtension.this.labelSubset2), gbc);
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(txtSubset2, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(new JLabel(CompleteBipartiteGraphToolBarExtension.this.labelMaxWeight), gbc);
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2, 2, 2, 2);
			centerPanel.add(txtMaxEdgeWeight, gbc);
			
			pack();
			setSize(300, getHeight());
		}
		
		/**
		 * Gets the number of vertices in subset 1.
		 * 
		 * @return the number of vertices in subset 1 or <code>-1</code> if the user enters an invalid value
		 * @since 1.0
		 */
		public int getN() {
			return n;
		}
		
		/**
		 * Gets the number of vertices in subset 2.
		 * 
		 * @return the number of vertices in subset 2 or <code>-1</code> if the user enters an invalid value
		 * @since 1.0
		 */
		public int getM() {
			return m;
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

		@Override
		protected void doOk() {
			try {
				n = new Integer(txtSubset1.getText());
			}
			catch(NumberFormatException ex) {
				n = -1;
			}
			
			try {
				m = new Integer(txtSubset2.getText());
			}
			catch(NumberFormatException ex) {
				m = -1;
			}
			
			try {
				maxEdgeWeight = new Float(txtMaxEdgeWeight.getText());
			}
			catch(NumberFormatException ex) {
				maxEdgeWeight = 0.0f;
			}
		}
		
	}

}
