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
 * Class:		AdjacencyMatrixDialog
 * Task:		Represents a dialog to create graphs with an adjacency matrix
 * Created:		01.04.14
 * LastChanges:	06.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.gui.dialogs.enums.AllowedGraphType;
import lavesdk.gui.widgets.Mask;
import lavesdk.gui.widgets.MatrixEditor;
import lavesdk.gui.widgets.Symbol;
import lavesdk.gui.widgets.MatrixEditor.FloatElementFormat;
import lavesdk.gui.widgets.Symbol.PredefinedSymbol;
import lavesdk.language.LanguageFile;
import lavesdk.math.Matrix;
import lavesdk.math.NumericMatrix;
import lavesdk.resources.Resources;

/**
 * Represents a dialog to create an adjacency matrix of a graph.
 * <br><br>
 * The user can enter the number of vertices and the adjacency matrix. Furthermore it is possible to determine whether the graph should be directed
 * or undirected and whether zero-weight edges are allowed or not.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #AdjacencyMatrixDialog(PluginHost, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>ADJACENCYMATRIXDLG_TITLE</i>: the title of the adjacency matrix dialog</li>
 * 		<li><i>ADJACENCYMATRIXDLG_DESCRIPTION</i>: the description of the adjacency matrix dialog</li>
 * 		<li><i>ADJACENCYMATRIXDLG_NOV</i>: the label of the field where the user has to enter the number of vertices</li>
 * 		<li><i>ADJACENCYMATRIXDLG_ALLOWZEROWEIGHTS</i>: the label of the checkbox to specify whether the adjacency matrix may contain zero-weight edges or not</li>
 * 		<li><i>ADJACENCYMATRIXDLG_ALLOWZEROWEIGHTS_DESC</i>: the label of the description of the zero-weight edge checkbox</li>
 * 		<li><i>DLG_CB_CREATEDIRECTEDGRAPH</i>: the label of the checkbox where the user can determine whether to create either a directed or an undirected graph</li>
 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * 
 * @see OptionDialog
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class AdjacencyMatrixDialog extends OptionDialog {
	
	private static final long serialVersionUID = 1L;
	
	/** the matrix editor */
	private final MatrixEditor<Float> matrixEditor;
	/** the checkbox to allow zero weights as input */
	private final JCheckBox cbAllowZeroWeights;
	/** the checkbox for changing the graph type */
	private final JCheckBox cbDirectedGraph;
	/** the matrix the user has entered */
	private Matrix<Float> adjacencyMatrix;
	
	/** the default size of the adjacency matrix */
	private static final int DEF_MATRIXSIZE = 4;
	
	/**
	 * Creates a new adjacency matrix dialog.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the dialog. The following language labels are available:
	 * <ul>
	 * 		<li><i>ADJACENCYMATRIXDLG_TITLE</i>: the title of the adjacency matrix dialog</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_DESCRIPTION</i>: the description of the adjacency matrix dialog</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_NOV</i>: the label of the field where the user has to enter the number of vertices</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_ALLOWZEROWEIGHTS</i>: the label of the checkbox to specify whether the adjacency matrix may contain zero-weight edges or not</li>
	 * 		<li><i>ADJACENCYMATRIXDLG_ALLOWZEROWEIGHTS_DESC</i>: the label of the description of the zero-weight edge checkbox</li>
	 * 		<li><i>DLG_CB_CREATEDIRECTEDGRAPH</i>: the label of the checkbox where the user can determine whether to create either a directed or an undirected graph</li>
	 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
	 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
	 * </ul>
	 * <br>
	 * <b>Notice</b>:<br>
	 * The user is allowed to determine the type of the graph meaning it is used {@link AllowedGraphType#BOTH}
	 * 
	 * @param host the host that is used to center the dialog in the application or <code>null</code> (centers the dialog in the screen)
	 * @param langFile the language file or <code>null</code> if the dialog should not use language dependent labels for the title, ok button and cancel button (in this case the predefined labels are shown)
	 * @param langID the language id
	 * @since 1.0
	 */
	public AdjacencyMatrixDialog(final PluginHost host, final LanguageFile langFile, final String langID) {
		this(host, langFile, langID, AllowedGraphType.BOTH);
	}
	
	/**
	 * Creates a new adjacency matrix dialog.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the dialog. The following language labels are available:
	 * <ul>
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
	 * @param host the host that is used to center the dialog in the application or <code>null</code> (centers the dialog in the screen)
	 * @param langFile the language file or <code>null</code> if the dialog should not use language dependent labels for the title, ok button and cancel button (in this case the predefined labels are shown)
	 * @param langID the language id
	 * @param type the type that determines whether the user is allowed to set or change the value of the checkbox that allows the user to decide whether the graph should be directed or undirected
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if type is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public AdjacencyMatrixDialog(final PluginHost host, final LanguageFile langFile, final String langID, final AllowedGraphType type) throws IllegalArgumentException {
		super(host, LanguageFile.getLabel(langFile, "ADJACENCYMATRIXDLG_TITLE", langID, "Adjacency Matrix"), langFile, langID, true);
		
		if(type == null)
			throw new IllegalArgumentException("No valid argument!");
		
		matrixEditor = new MatrixEditor<Float>(new FloatElementFormat());
		matrixEditor.addMask(new Mask(Float.POSITIVE_INFINITY, Symbol.getPredefinedSymbol(PredefinedSymbol.INFINITY)));
		cbAllowZeroWeights = new JCheckBox(LanguageFile.getLabel(langFile, "ADJACENCYMATRIXDLG_ALLOWZEROWEIGHTS", langID, "Allow zero-weight edges?"));
		cbDirectedGraph = new JCheckBox(LanguageFile.getLabel(langFile, "DLG_CB_CREATEDIRECTEDGRAPH", langID, "Create a directed graph?"));
		adjacencyMatrix = null;
		
		final JTextField txtNOV = new JTextField("" + DEF_MATRIXSIZE);
		final JButton btnUpdate = new JButton(Resources.getInstance().REFRESH_ICON);
		
		cbDirectedGraph.setSelected(type == AllowedGraphType.DIRECTED_ONLY);
		cbDirectedGraph.setEnabled(type == AllowedGraphType.BOTH);
		
		northPanel.setLayout(new BorderLayout());
		centerPanel.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		
		northPanel.add(new JLabel("<html>" + LanguageFile.getLabel(langFile, "ADJACENCYMATRIXDLG_DESCRIPTION", langID, "Enter the number of vertices and create the adjacency matrix by selecting a field and enter a weight for the edge using the keyboard.") + "</html>"), BorderLayout.CENTER);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.insets = new Insets(0, 0, 2, 2);
		centerPanel.add(new JLabel(LanguageFile.getLabel(langFile, "ADJACENCYMATRIXDLG_NOV", langID, "Number of vertices:")), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.insets = new Insets(0, 2, 2, 2);
		centerPanel.add(txtNOV, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.insets = new Insets(0, 2, 2, 0);
		centerPanel.add(btnUpdate, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.insets = new Insets(0, 0, 2, 0);
		centerPanel.add(cbAllowZeroWeights, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.insets = new Insets(0, 10, 2, 0);
		centerPanel.add(new JLabel("<html><i>" + LanguageFile.getLabel(langFile, "ADJACENCYMATRIXDLG_ALLOWZEROWEIGHTS_DESC", langID, "Enable this option to allow zero-weight edges too.<br>In that case use \"-\" as the entry for \"no edge\" in the adjacency matrix.") + "</i></html>"), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new Insets(4, 0, 2, 0);
		centerPanel.add(matrixEditor, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.insets = new Insets(0, 0, 0, 0);
		centerPanel.add(cbDirectedGraph, gbc);
		
		// initialize the adjacency matrix
		updateAdjacencyMatrix(txtNOV.getText());
		
		pack();
		
		btnUpdate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AdjacencyMatrixDialog.this.updateAdjacencyMatrix(txtNOV.getText());
			}
		});
		txtNOV.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
					AdjacencyMatrixDialog.this.updateAdjacencyMatrix(txtNOV.getText());
			}
		});
	}
	
	/**
	 * Gets the adjacency matrix the user has entered.
	 * 
	 * @return the adjacency matrix or <code>null</code> if the user cancels the dialog
	 * @since 1.0
	 */
	public Matrix<Float> getAdjacencyMatrix() {
		return adjacencyMatrix;
	}
	
	/**
	 * Gets the value of the checkbox where the user can decide whether zero-weight edges are allowed.
	 * 
	 * @return <code>true</code> if zero-weight edges are allowed otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getAllowZeroWeights() {
		return cbAllowZeroWeights.isSelected();
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
		adjacencyMatrix = matrixEditor.getMatrix();
	}
	
	/**
	 * Updates the adjacency matrix with a new number of vertices.
	 * 
	 * @param nov the number of vertices as a string
	 * @since 1.0
	 */
	private void updateAdjacencyMatrix(final String nov) {
		Integer size;
		
		try {
			size = new Integer(nov);
		}
		catch(NumberFormatException e) {
			size = null;
		}
		
		// no valid number of vertices? then quit because the matrix cannot be updated
		if(size == null)
			return;
		
		final Map<Integer, String> labels = new HashMap<Integer, String>(size.intValue());
		for(int i = 1; i <= size.intValue(); i++)
			labels.put(i - 1, "" + i);
		
		// display labels that the user knows which row and column belong to which vertex
		matrixEditor.setRowLabels(labels);
		matrixEditor.setColumnLabels(labels);
		
		// set the new matrix but copy the old content
		final Matrix<Float> oldMatrix = matrixEditor.getMatrix();
		Matrix<Float> newMatrix = new NumericMatrix<Float>(size.intValue(), size.intValue(), 0.0f);
		if(oldMatrix != null)
			Matrix.copy(oldMatrix, newMatrix);
		matrixEditor.setMatrix(newMatrix);
		
		pack();
	}

}
