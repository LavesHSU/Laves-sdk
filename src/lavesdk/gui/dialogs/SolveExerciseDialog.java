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
 * Class:		SolveExerciseDialog
 * Task:		Represents a dialog to solve algorithm exercises
 * Created:		26.03.14
 * LastChanges:	09.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import lavesdk.algorithm.AlgorithmExercise;
import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;
import lavesdk.utils.FileUtils;

/**
 * Represents a dialog to solve {@link AlgorithmExercise}s where the user has to input a solution or select values from a list or
 * something like that.
 * <br><br>
 * The dialog displays the specified {@link SolutionEntry}s one below the other like:
 * <pre>
 * Label Entry 1 | Component Entry 1
 * ---------------------------------
 * Label Entry 2 | Component Entry 2
 * ---------------------------------
 * Label Entry 3 | Component Entry 3
 * ---------------------------------
 * ...
 * </pre>
 * Use {@link #setVisible(boolean)} to show the dialog. The dialog is modal meaning {@link #setVisible(boolean)} returns when the
 * dialog is closed. With {@link #isCanceled()} you can check whether the user cancels the dialog.
 * <br><br>
 * To get the input the user has entered use {@link SolutionEntry#getComponent()}.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #SolveExerciseDialog(PluginHost, AlgorithmExercise, SolutionEntry[], String, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
	 * 		<li><i>SOLVEEXERCISEDLG_TITLE</i>: the title of the solve exercise dialog</li>
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
public class SolveExerciseDialog extends OptionDialog {
	
	private static final long serialVersionUID = 1L;
	
	/** the maximum width of the dialog if the dialog is opened */
	private static final int MAX_OPEN_WIDTH = 600;
	/** the maximum height of the dialog if the dialog is opened */
	private static final int MAX_OPEN_HEIGHT = 500;
	
	/**
	 * Creates a new solve exercise dialog.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the dialog. The following language labels are available:
	 * <ul>
	 * 		<li><i>SOLVEEXERCISEDLG_TITLE</i>: the title of the solve exercise dialog</li>
	 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the dialog</li>
	 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the dialog</li>
	 * </ul>
	 * 
	 * @param host the host that is used to center the dialog in the application or <code>null</code> (centers the dialog in the screen)
	 * @param exercise the exercise to display
	 * @param entries the entries where the user has to enter or select the solutions
	 * @param inputHint a hint for the user how to format the input
	 * @param langFile the language file or <code>null</code> if the dialog should not use language dependent labels for the title, ok button and cancel button (in this case the predefined labels are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if exercise is null</li>
	 * 		<li>if entries is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public SolveExerciseDialog(final PluginHost host, final AlgorithmExercise<?> exercise, final SolutionEntry<?>[] entries, final String inputHint, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		super(host, LanguageFile.getLabel(langFile, "SOLVEEXERCISEDLG_TITLE", langID, "Solve Exercise"), langFile, langID);
		
		if(exercise == null || entries == null)
			throw new IllegalArgumentException("No valid arguments!");
		
		// change the icon of the dialog
		super.setIconImage((Resources.getInstance().EXERCISE_SOLVE_ICON != null) ? Resources.getInstance().EXERCISE_SOLVE_ICON.getImage() : null);
		
		// create the panel in the north with the icon and the exercise text
		northPanel.setLayout(new BorderLayout(5, 5));
		northPanel.add(new JLabel(Resources.getInstance().QUESTION_ICON), BorderLayout.WEST);
		northPanel.add(new JLabel(getExerciseText(exercise)), BorderLayout.CENTER);
		
		// create the panel in the center with the solution entries
		final JPanel centerPanel = new JPanel(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		int gridy = 0;
		
		if(inputHint != null && !inputHint.isEmpty()) {
			gbc.gridx = 0;
			gbc.gridy = gridy++;
			gbc.gridwidth = 2;
			gbc.weightx = 0.0;
			gbc.insets = new Insets(0, 0, 2, 0);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			centerPanel.add(new JLabel("<html>" + inputHint.replaceAll(FileUtils.LINESEPARATOR, "<br>") + "</html>"), gbc);
		}
		
		for(SolutionEntry<?> e : entries) {
			if(e == null)
				continue;
			
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.gridwidth = 1;
			gbc.weightx = 0.0;
			gbc.insets = new Insets(2, 0, 2, 2);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			centerPanel.add(new JLabel("<html>" + e.getLabel() + "</html>"), gbc);
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.gridwidth = 1;
			gbc.weightx = 1.0;
			gbc.insets = new Insets(2, 2, 2, 0);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			centerPanel.add(e.getComponent(), gbc);
			
			gridy++;
		}
		
		final JScrollPane scrollPane = new JScrollPane(centerPanel);
		scrollPane.setBorder(null);
		
		super.centerPanel.setLayout(new BorderLayout());
		super.centerPanel.add(scrollPane, BorderLayout.CENTER);
		
		pack();
		
		// if the dialog is opened the dialog should not exceed a specific size
		final Dimension size = getSize();
		setSize(new Dimension(Math.min(size.width, MAX_OPEN_WIDTH), Math.min(size.height, MAX_OPEN_HEIGHT)));
	}

	@Override
	protected void doOk() {
	}
	
	/**
	 * Gets the text of the exercise in an HTML format so that the text is automatically wrapped.
	 * 
	 * @param exercise the exercise
	 * @return the text of the exercise as a formatted string
	 * @since 1.0
	 */
	private String getExerciseText(final AlgorithmExercise<?> exercise) {
		return "<html>" + exercise.getText().replaceAll(FileUtils.LINESEPARATOR, "<br>") + "</html>";
	}
	
	/**
	 * Represents a solution entry in the {@link SolveExerciseDialog}.
	 * <br><br>
	 * An entry consists of a label and a component where the user can enter or select a solution.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 * @param <C> the type of the component
	 */
	public static class SolutionEntry<C extends Component> {
		
		/** the description of the entry */
		private final String label;
		/** the component of the entry */
		private final C component;
		
		/**
		 * Creates a new solution entry.
		 * 
		 * @param label the label of the entry (<b>can contain html tags to format the text</b>)
		 * @param component the component of the entry
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if component is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public SolutionEntry(final String label, final C component) throws IllegalArgumentException {
			if(component == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.label = (label != null) ? label : "";
			this.component = component;
		}
		
		/**
		 * Gets the label of the entry.
		 * 
		 * @return the label or an empty string if the entry does not have a label (<b>can contain html tags to format the text</b>)
		 * @since 1.0
		 */
		public final String getLabel() {
			return label;
		}
		
		/**
		 * Gets the component of the entry.
		 * 
		 * @return the component of the entry
		 * @since 1.0
		 */
		public final C getComponent() {
			return component;
		}
		
	}

}
