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

package lavesdk.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;

/**
 * Represents an abstract option dialog.
 * <br><br>
 * An option dialog has a north panel that might be used to display a description, icon, ... ({@link #northPanel}). The
 * center panel displays the options of the dialog ({@link #centerPanel}) and in the south panel there are an ok button and
 * a cancel button.<br>
 * The north panel is separated from the center panel and has a white background to visualize e.g. a description.
 * <br><br>
 * It is recommended that you invoke {@link #pack()} at the end of creating the user interface of the dialog so that the dialog
 * can take on the preferred size.
 * <br><br>
 * Use {@link #setVisible(boolean)} to show the dialog. The dialog is modal meaning {@link #setVisible(boolean)} returns when the
 * dialog is closed. With {@link #isCanceled()} you can check whether the user cancels the dialog.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #OptionDialog(PluginHost, String, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public abstract class OptionDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	/** main north panel that integrates the {@link #northPanel} */
	private final JPanel mainNorthPanel;
	/** the panel that might be used to display a description, icon, ... */
	protected final JPanel northPanel;
	/** the panel that displays the options of the dialog */
	protected final JPanel centerPanel;
	/** the plugin host */
	private final PluginHost host;
	/** flag that indicates whether the dialog is canceled */
	private boolean canceled;
	
	/**
	 * Creates a new option dialog.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the dialog. The following language labels are available:
	 * <ul>
	 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
	 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
	 * </ul>
	 * 
	 * @param host the host that is used to center the dialog in the application or <code>null</code> (centers the dialog in the screen)
	 * @param title the title of the dialog
	 * @param langFile the language file or <code>null</code> if the dialog should not use language dependent labels for the ok button and cancel button (in this case the predefined labels are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public OptionDialog(final PluginHost host, final String title, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		this(host, title, langFile, langID, false);
	}
	
	/**
	 * Creates a new option dialog.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the dialog. The following language labels are available:
	 * <ul>
	 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
	 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
	 * </ul>
	 * 
	 * @param host the host that is used to center the dialog in the application or <code>null</code> (centers the dialog in the screen)
	 * @param title the title of the dialog
	 * @param langFile the language file or <code>null</code> if the dialog should not use language dependent labels for the ok button and cancel button (in this case the predefined labels are shown)
	 * @param langID the language id
	 * @param asUtitlity <code>true</code> if the dialog should be displayed as a utility window
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public OptionDialog(final PluginHost host, final String title, final LanguageFile langFile, final String langID, final boolean asUtitlity) throws IllegalArgumentException {
		if(title == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.host = host;
		this.canceled = true;	// initially the dialog is canceled (only the ok button can change the flag to true)
		
		super.setTitle(title);
		if(asUtitlity)
			super.setType(Type.UTILITY);
		super.setModal(true);
		super.setResizable(true);
		super.setIconImage(Resources.getInstance().EMPTY_IMAGE);
		super.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		final Container contentPane = getContentPane();
		final JPanel content = new JPanel(new BorderLayout(0, 0));
		
		contentPane.setLayout(new BorderLayout());
		contentPane.add(content, BorderLayout.CENTER);
		
		// create the panel in the north
		northPanel = new JPanel() {
			
			private static final long serialVersionUID = 1L;
			private boolean hasSeparator = false;
			
			@Override
			protected void addImpl(Component comp, Object constraints, int index) {
				super.addImpl(comp, constraints, index);
				
				if(!hasSeparator) {
					hasSeparator = true;
					
					// the separator may only be added to the main panel when the north panel has at least one component
					mainNorthPanel.add(new JSeparator(), BorderLayout.SOUTH);
					// change background to white to highlight the description area
					mainNorthPanel.setBackground(Color.white);
					setBackground(Color.white);
					// set a border for insets
					setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				}
			}
		};
		mainNorthPanel = new JPanel(new BorderLayout());
		mainNorthPanel.add(northPanel, BorderLayout.CENTER);
		
		// create the panel in the center
		centerPanel = new JPanel();
		centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// create the panel in the south with the ok and cancel button
		final JPanel southPanel = new JPanel();
		final JButton btnOk = new JButton(LanguageFile.getLabel(langFile, "DLG_BTN_OK", langID, "Ok"));
		final JButton btnCancel = new JButton(LanguageFile.getLabel(langFile, "DLG_BTN_CANCEL", langID, "Cancel"));
		southPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
		southPanel.add(Box.createHorizontalGlue());
		southPanel.add(btnOk);
		southPanel.add(btnCancel);
		
		// let the ok button be the default button
		getRootPane().setDefaultButton(btnOk);
		
		// add the content panels
		content.add(mainNorthPanel, BorderLayout.NORTH);
		content.add(centerPanel, BorderLayout.CENTER);
		content.add(southPanel, BorderLayout.SOUTH);
		
		pack();
		
		btnOk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				OptionDialog.this.canceled = false;
				OptionDialog.this.doOk();
				OptionDialog.this.dispose();
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				OptionDialog.this.canceled = true;
				OptionDialog.this.dispose();
			}
		});
	}
	
	/**
	 * Packs the dialog and centers the dialog in the host application if possible.
	 * 
	 * @since 1.0
	 */
	@Override
	public final void pack() {
		super.pack();
		
		if(host == null)
			super.setLocationRelativeTo(null);
		else
			host.adaptDialog(this);
	}
	
	/**
	 * It is not allowed to change the modal state of the dialog meaning this method does nothing.
	 * 
	 * @param modal the modal state
	 * @since 1.0
	 */
	@Override
	public final void setModal(boolean modal) {
	}
	
	/**
	 * Indicates whether the dialog is canceled by the user.
	 * 
	 * @return <code>true</code> if the dialog was closed or the cancel button was pressed otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean isCanceled() {
		return canceled;
	}
	
	/**
	 * This method is invoked when the user clicks the ok button.
	 * 
	 * @since 1.0
	 */
	protected abstract void doOk();

}
