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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTextField;

import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;

/**
 * An input dialog where the user can enter a string.
 * <br><br>
 * Use {@link #getInput()} to get the input the user has entered and with {@link #isCanceled()} you can check whether the user has canceled
 * the dialog.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #InputDialog(PluginHost, String, String, String, LanguageFile, String)} to specify a language file from which
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
public class InputDialog extends OptionDialog {
	
	private static final long serialVersionUID = 1L;
	
	/** the input field of the dialog */
	private final JTextField inputField;
	/** the input */
	private String input;
	
	/**
	 * Creates a new input dialog.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the dialog. The following language labels are available:
	 * <ul>
	 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
	 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
	 * </ul>
	 * 
	 * @param host the host
	 * @param title the title of the input dialog
	 * @param description the description of the dialog or <code>null</code> if the dialog should not have a description (<b>can contain html tags to format the text</b>)
	 * @param label the label of the input field (<b>can contain html tags to format the text</b>)
	 * @param langFile the language file or <code>null</code> if the dialog should not use language dependent labels for the ok button and cancel button (in this case the predefined labels are shown)
	 * @param langID the language id
	 * @since 1.0
	 */
	public InputDialog(final PluginHost host, final String title, final String description, final String label, final LanguageFile langFile, final String langID) {
		this(host, title, description, label, "", langFile, langID);
	}

	/**
	 * Creates a new input dialog.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the dialog. The following language labels are available:
	 * <ul>
	 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
	 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
	 * </ul>
	 * 
	 * @param host the host
	 * @param title the title of the input dialog
	 * @param description the description of the dialog or <code>null</code> if the dialog should not have a description  (<b>can contain html tags to format the text</b>)
	 * @param label the label of the input field (<b>can contain html tags to format the text</b>)
	 * @param initialValue the initial input value
	 * @param langFile the language file or <code>null</code> if the dialog should not use language dependent labels for the ok button and cancel button (in this case the predefined labels are shown)
	 * @param langID the language id
	 * @since 1.0
	 */
	public InputDialog(final PluginHost host, final String title, final String description, final String label, final String initialValue, final LanguageFile langFile, final String langID) {
		super(host, title, langFile, langID, true);
		
		inputField = new JTextField((initialValue != null) ? initialValue : "");
		input = "";
		
		if(description != null && !description.isEmpty()) {
			northPanel.setLayout(new BorderLayout());
			northPanel.add(new JLabel("<html>" + description + "</html>"), BorderLayout.CENTER);
		}
		
		centerPanel.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.0;
		gbc.insets = new Insets(2, 2, 2, 0);
		centerPanel.add(new JLabel("<html>" + label + "</html>"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(2, 2, 2, 0);
		centerPanel.add(inputField, gbc);
		
		pack();
	}
	
	/**
	 * Gets the input the user has entered.
	 * 
	 * @return the input
	 * @since 1.0
	 */
	public String getInput() {
		return input;
	}
	
	@Override
	protected void doOk() {
		input = inputField.getText();
	}

}