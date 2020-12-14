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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.algorithm.text.Annotation;
import lavesdk.gui.widgets.AnnotationViewKit;
import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;

/**
 * Represents a dialog to display {@link Annotation}s.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #AnnotationDialog(PluginHost, Annotation, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>ANNOTATIONDLG_TITLE</i>: the title of the annotation dialog</li>
 * 		<li><i>DLG_BTN_QUIT</i>: the caption of the quit button of a dialog</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class AnnotationDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new annotation dialog.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the dialog. The following language labels are available:
	 * <ul>
	 * 		<li><i>ANNOTATIONDLG_TITLE</i>: the title of the annotation dialog</li>
	 * 		<li><i>DLG_BTN_QUIT</i>: the caption of the quit button of a dialog</li>
	 * </ul>
	 * 
	 * @param host the host that is used to center the dialog in the application or <code>null</code> (centers the dialog in the screen)
	 * @param annotation the annotation to display
	 * @param langFile the language file or <code>null</code> if the dialog should not use language dependent labels for the quit button and the title (in this case the predefined labels are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if annotation is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public AnnotationDialog(final PluginHost host, final Annotation annotation, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		if(annotation == null)
			throw new IllegalArgumentException("No valid argument!");
		
		setTitle(LanguageFile.getLabel(langFile, "ANNOTATIONDLG_TITLE", langID, "Annotation"));
		setResizable(true);
		setIconImage(Resources.getInstance().EMPTY_IMAGE);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		final Container contentPane = getContentPane();
		final JPanel contentPanel = new JPanel(new BorderLayout());
		contentPane.setLayout(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// create the view of the annotation
		final JEditorPane editorPane = new JEditorPane();
		final JScrollPane scrollPane = new JScrollPane(editorPane);
		editorPane.setEditorKit(new AnnotationViewKit(annotation.getImagesList()));
		editorPane.setText(annotation.getText());
		editorPane.setEditable(false);
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		
		// create the panel in the south with the ok and cancel button
		final JPanel southPanel = new JPanel();
		final JButton btnQuit = new JButton(LanguageFile.getLabel(langFile, "DLG_BTN_QUIT", langID, "Quit"));
		southPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
		southPanel.add(Box.createHorizontalGlue());
		southPanel.add(btnQuit);
		
		contentPane.add(contentPanel, BorderLayout.CENTER);
		contentPane.add(southPanel, BorderLayout.SOUTH);
		
		// let the ok button be the default button
		getRootPane().setDefaultButton(btnQuit);
		
		pack();
		
		if(host == null)
			setLocationRelativeTo(null);
		else
			host.adaptDialog(this);
		
		btnQuit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AnnotationDialog.this.dispose();
			}
		});
	}

}
