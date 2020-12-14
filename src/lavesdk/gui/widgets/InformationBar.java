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
 * Class:		InformationBar
 * Task:		Show an information bar with plugin information
 * Created:		08.04.14
 * LastChanges:	23.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;

/**
 * Represents an information bar that shows the assumption(s) and the instructions of a plugin.
 * <br><br>
 * Use {@link #update(AlgorithmPlugin)} to update the information bar with the data of a plugin. Use {@link #isVisible()} to check whether
 * the bar is currently displayed (if not you can use {@link #setVisible(boolean)} to activate the bar again). The bar can only be displayed if it is
 * activatable meaning there are valid information that could be shown. You can check whether the bar is activatable by using {@link #isActivatable()}.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #InformationBar(Component, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>INFORMATIONBAR_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the information bar</li>
 * 		<li><i>INFORMATIONBAR_ASSUMPTIONS</i>: the label that shows the assumptions of a plugin in the information bar</li>
 * 		<li><i>INFORMATIONBAR_SHOWINSTRUCTIONS</i>: the tooltip text of the button that shows the instructions of a plugin in the information bar</li>
 * 		<li><i>INFORMATIONBAR_SHOWINSTRUCTIONS_TITLE</i>: the title of the dialog that shows the instructions of a plugin in the information bar</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class InformationBar extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/** the parent to center the instructions dialog or <code>null</code> */
	private final Component parent;
	/** flag that indicates whether the bar is initialized */
	private boolean initialized;
	/** the panel with the assumption */
	private final JPanel assumptionPanel;
	/** the label for assumption */
	private final JLabel lblAssumption;
	/** the label for assumption value */
	private final JLabel lblAssumptionValue;
	/** the label that is the button to open the instructions */
	private final JLabel instructionsBtn;
	/** the label that is the button to close the bar */
	private final JLabel closeBtn;
	/** the title of the instructions dialog */
	private final String titleInstructions;
	/** flag that indicates whether the bar is currently activatable */
	private boolean activatable;
	/** the current instructions to show */
	private String currInstructions;

	/**
	 * Creates a new information bar.
	 * 
	 * @param parent the parent component that is used to center the dialog that shows the instructions or <code>null</code>
	 * @since 1.0
	 */
	public InformationBar(final Component parent) {
		this(parent, null, null);
	}
	
	/**
	 * Creates a new information bar.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the bar. The following language labels are available:
	 * <ul>
	 * 		<li><i>INFORMATIONBAR_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the information bar</li>
	 * 		<li><i>INFORMATIONBAR_ASSUMPTIONS</i>: the label that shows the assumptions of a plugin in the information bar</li>
	 * 		<li><i>INFORMATIONBAR_SHOWINSTRUCTIONS</i>: the tooltip text of the button that shows the instructions of a plugin in the information bar</li>
	 * 		<li><i>INFORMATIONBAR_SHOWINSTRUCTIONS_TITLE</i>: the title of the dialog that shows the instructions of a plugin in the information bar</li>
	 * </ul>
	 * 
	 * @param parent the parent component that is used to center the dialog that shows the instructions or <code>null</code>
	 * @param langFile the language file or <code>null</code> if the bar should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @since 1.0
	 */
	public InformationBar(final Component parent, final LanguageFile langFile, final String langID) {
		final Font f = UIManager.getFont("Label.font");

		this.initialized = false;
		this.parent = parent;
		this.activatable = false;
		this.currInstructions = null;
		this.titleInstructions = LanguageFile.getLabel(langFile, "INFORMATIONBAR_SHOWINSTRUCTIONS_TITLE", langID, "Instructions");
		this.assumptionPanel = new JPanel(new BorderLayout(5, 0));
		this.lblAssumption = new JLabel(LanguageFile.getLabel(langFile, "INFORMATIONBAR_ASSUMPTIONS", langID, "Assumption(s):"));
		this.lblAssumptionValue = new JLabel();
		this.instructionsBtn = new JLabel(Resources.getInstance().INSTRUCTIONS_ICON);
		this.closeBtn = new JLabel(Resources.getInstance().CLOSE_ICON);
		
		// the assumption title should be displayed in bold
		lblAssumption.setFont(f.deriveFont(Font.BOLD));
		
		// initialize the close button
		closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		closeBtn.setToolTipText(LanguageFile.getLabel(langFile, "INFORMATIONBAR_CLOSE_TOOLTIP", langID, "Close Information Bar"));
		closeBtn.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				InformationBar.this.closeBtn.setIcon(Resources.getInstance().CLOSE_ICON);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				InformationBar.this.closeBtn.setIcon(Resources.getInstance().CLOSE_HOVER_ICON);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				InformationBar.super.setVisible(false);
			}
		});
		
		// initialize the show instructions button
		instructionsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		instructionsBtn.setToolTipText(LanguageFile.getLabel(langFile, "INFORMATIONBAR_SHOWINSTRUCTIONS", langID, "Show Instructions"));
		instructionsBtn.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				InformationBar.this.showInstructions();
			}
		});
		
		// create the assumption panel
		assumptionPanel.add(lblAssumption, BorderLayout.WEST);
		assumptionPanel.add(lblAssumptionValue, BorderLayout.CENTER);

		// create a border layout to add the components of the bar
		super.setLayout(new BorderLayout(10, 0));
		
		// add all components
		add(closeBtn, BorderLayout.WEST);
		add(assumptionPanel, BorderLayout.CENTER);
		add(instructionsBtn, BorderLayout.EAST);
		
		// create a border to separate the information bar from the rest of a window or component
		super.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		
		// set the initial background of the bar
		setBackground(new Color(240, 230, 200));
		
		// by default the bar is not activatable meaning not visible
		super.setVisible(false);
		
		initialized = true;
	}
	
	/**
	 * Updates the information bar with the specified plugin.
	 * <br><br>
	 * If the plugin has no assumptions and instructions the bar will be deactivated otherwise the information displays the assumptions
	 * and/or the instructions of the plugin.
	 * 
	 * @param plugin the plugin
	 * @since 1.0
	 */
	public void update(final AlgorithmPlugin plugin) {
		final boolean validAssumption = plugin.getAssumptions() != null && !plugin.getAssumptions().isEmpty();
		final boolean validInstructions = plugin.getInstructions() != null && !plugin.getInstructions().isEmpty();
		
		activatable = validAssumption || validInstructions;
		currInstructions = validInstructions ? plugin.getInstructions() : null;
		
		lblAssumption.setVisible(validAssumption);
		lblAssumptionValue.setVisible(validAssumption);
		lblAssumptionValue.setText("<html>" + (validAssumption ? plugin.getAssumptions() : "") + "</html>");
		instructionsBtn.setVisible(validInstructions);
		
		if(!activatable)
			super.setVisible(false);
		else
			super.setVisible(true);
	}
	
	/**
	 * Sets the background color of the bar.
	 * 
	 * @param bg the background color
	 * @since 1.0
	 */
	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if(assumptionPanel != null)
			assumptionPanel.setBackground(bg);
	}
	
	/**
	 * Sets the foreground color of the text in the bar.
	 * 
	 * @param fg the foreground color
	 * @since 1.0
	 */
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if(lblAssumption != null)
			lblAssumption.setForeground(fg);
		if(lblAssumptionValue != null)
			lblAssumptionValue.setForeground(fg);
	}
	
	/**
	 * Sets the visibility state of the information bar but this is only possible if the bar is
	 * activatable meaning that the bar was updated with a plugin that has valid information that can be displayed
	 * in the bar.
	 * 
	 * @see #isActivatable()
	 * @param visible <code>true</code> if the bar should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	@Override
	public void setVisible(boolean visible) {
		if(activatable)
			super.setVisible(visible);
	}
	
	/**
	 * Indicates whether the bar is activatable meaning that the bar was updated with a plugin that has valid information that can be displayed
	 * in the bar.
	 * 
	 * @return <code>true</code> if the bar is activatable otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isActivatable() {
		return activatable;
	}
	
	/**
	 * The layout of the information bar may not be changed meaning this method does nothing!
	 * 
	 * @param mgr the layout manager
	 * @since 1.0
	 */
	@Override
	public void setLayout(LayoutManager mgr) {
	}
	
	/**
	 * The border of the information bar may not be changed meaning this method does nothing!
	 * 
	 * @param border the border
	 * @since 1.0
	 */
	@Override
	public void setBorder(Border border) {
	}
	
	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		if(!initialized)
			super.addImpl(comp, constraints, index);
	}
	
	/**
	 * Opens a dialog to show the instructions.
	 * 
	 * @since 1.0
	 */
	private void showInstructions() {
		final JLabel lblInstructions = new JLabel("<html>" + currInstructions + "</html>");
		JOptionPane.showMessageDialog(parent, lblInstructions, titleInstructions, JOptionPane.INFORMATION_MESSAGE);
	}

}
