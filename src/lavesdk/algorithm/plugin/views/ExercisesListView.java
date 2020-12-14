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
 * Class:		ExercisesList
 * Task:		Represent exercises in a list and display the evaluation
 * Created:		07.03.14
 * LastChanges:	09.10.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;

import lavesdk.algorithm.AlgorithmExercise;
import lavesdk.algorithm.AlgorithmExerciseHandler;
import lavesdk.algorithm.AlgorithmExercise.ExamResult;
import lavesdk.algorithm.AlgorithmExerciseProvider;
import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.algorithm.text.AlgorithmStep;
import lavesdk.algorithm.text.Annotation;
import lavesdk.gui.dialogs.AnnotationDialog;
import lavesdk.gui.widgets.AnnotationViewKit;
import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;
import lavesdk.utils.FileUtils;
import lavesdk.utils.MathUtils;

/**
 * Displays a list of {@link AlgorithmExercise}s and operates as an {@link AlgorithmExerciseProvider}.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #ExercisesListView(LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>EXERCISESLIST_TITLE</i>: the title of the exercises list</li>
 * 		<li><i>EXERCISESLIST_ITEM_EXERCISE</i>: the title of an exercise item</li>
 * 		<li><i>EXERCISESLIST_ITEM_CREDIT</i>: the description for a credit</li>
 * 		<li><i>EXERCISESLIST_ITEM_CREDITS</i>: the description for credits</li>
 * 		<li><i>EXERCISESLIST_ITEM_RESULT</i>: the description of the result label in an exercise item</li>
 * 		<li><i>EXERCISESLIST_ITEM_ATTEMPTS</i>: the description of the attempts label in an exercise item</li>
 * 		<li><i>EXERCISESLIST_ITEM_LAST_SOLUTION</i>: the description of the last solution label in an exercise item</li>
 * 		<li><i>EXERCISESLIST_ITEM_BTN_SOLVE</i>: the tooltip of the solve button in an exercise item</li>
 * 		<li><i>EXERCISESLIST_ITEM_BTN_GIVEUP</i>: the tooltip of the give up button in an exercise item</li>
 * 		<li><i>EXERCISESLIST_ITEM_BTN_INPUTHINT</i>: the tooltip of the input hint button in an exercise item</li>
 * 		<li><i>EXERCISESLIST_ITEM_BTN_ANNOTATION</i>: the tooltip of the annotation button in an exercise item</li>
 * 		<li><i>EXERCISESLIST_ITEM_STATE_SUCCEEDED</i>: the description of the state succeeded</li>
 * 		<li><i>EXERCISESLIST_ITEM_STATE_FAILED</i>: the description of the state failed</li>
 * 		<li><i>EXERCISESLIST_ITEM_STATE_FAILEDHINT</i>: the tooltip of the state failed if a hint message is available</li>
 * 		<li><i>EXERCISESLIST_EVALUATION</i>: the text of the header label of the evaluation panel</li>
 * 		<li><i>EXERCISESLIST_EVALUATION_CREDITSACHIEVED</i>: the text of the credits label of the evaluation panel</li>
 * 		<li><i>EXERCISESLIST_EVALUATION_INPERCENT</i>: the text of the credits in percent label of the evaluation panel</li>
 * 		<li><i>EXERCISESLIST_EVALUATION_GRADING</i>: the text of the grading label of the evaluation panel</li>
 * 		<li><i>EXERCISESLIST_EVALUATION_GRADING_SCALE</i>: the title of the grading scale dialog of the evaluation panel that is also used as the tooltip of the grading value button</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * 
 * @author jdornseifer
 * @version 1.2
 * @since 1.0
 */
public class ExercisesListView extends View implements AlgorithmExerciseProvider {

	private static final long serialVersionUID = 1L;
	
	/** the language file */
	private final LanguageFile langFile;
	/** the language id */
	private final String langID;
	/** the handler of the exercise mode */
	private AlgorithmExerciseHandler exerciseModeHandler;
	/** the scroll pane */
	private final JScrollPane scrollPane;
	/** the panel that displays the evaluation */
	private final EvaluationPanel evalPanel;
	/** the active item of the exercises list or <code>null</code> if there is not active item */
	private Item activeItem;
	/** the panel that contains the exercise items */
	private final JPanel itemList;
	/** flag that indicates whether an exam is currently performed */
	private boolean performingExam;
	/** language dependent label for the header of an exercise item */
	private final String labelExercise;
	/** language dependent label for the credit display in an exercise item */
	private final String labelCredit;
	/** language dependent label for the credits display in an exercise item */
	private final String labelCredits;
	/** language dependent label for the result display in an exercise item */
	private final String labelResult;
	/** language dependent label for the attempts display in an exercise item */
	private final String labelAttempts;
	/** language dependent label for the last solution label in an exercise item */
	private final String labelLastSolution;
	/** language dependent label for the tooltip of the solve button in an exercise item */
	private final String labelSolve;
	/** language dependent label for the tooltip of the give up button in an exercise item */
	private final String labelGiveUp;
	/** language dependent label for the tooltip of the input hint button in an exercise item */
	private final String labelInputHint;
	/** language dependent label for the tooltip of the annotation button in an exercise item */
	private final String labelAnnotation;
	/** language dependent label for the tooltip of the result value label in an exercise item */
	private final String labelSucceeded;
	/** language dependent label for the tooltip of the result value label in an exercise item */
	private final String labelFailed;
	/** language dependent label for the tooltip of the result value label in an exercise item */
	private final String labelFailedHint;
	/** the info message to switch off the exercise mode */
	private final String msgInfoExerciseMode;
	/** the title of the info message */
	private final String msgTitleExerciseMode;
	/** the title of the input hint dialog */
	private final String inputHintDlgTitle;
	
	/** the background color of an item */
	private static final Color ITEM_BACKGROUND = Color.white;
	/** the color of an item separator */
	private static final Color ITEM_SEPARATOR = new Color(170, 200, 220);
	/** the background color of the active item */
	private static final Color ACTIVE_ITEM_BACKGROUND = new Color(245, 251, 255);
	/** the border color of the active item */
	private static final Color ACTIVE_ITEM_BORDER = new Color(230, 236, 240);
	
	/** the padding to the top of the item */
	private static final int ITEM_PADDING_TOP = 5;
	/** the padding to the left of the item */
	private static final int ITEM_PADDING_LEFT = 2;
	/** the padding to the right of the item */
	private static final int ITEM_PADDING_RIGHT = 2;
	/** the padding to the bottom of the item */
	private static final int ITEM_PADDING_BOTTOM = 2;
	/** the active border of the item */
	private static final Border ITEM_BORDER_ACTIVE = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ACTIVE_ITEM_BORDER), BorderFactory.createEmptyBorder(ITEM_PADDING_TOP - 1, ITEM_PADDING_LEFT - 1, ITEM_PADDING_BOTTOM - 1, ITEM_PADDING_RIGHT - 1));
	/** the normal border of the item */
	private static final Border ITEM_BORDER_NORMAL = BorderFactory.createEmptyBorder(ITEM_PADDING_TOP, ITEM_PADDING_LEFT, ITEM_PADDING_BOTTOM, ITEM_PADDING_RIGHT);
	/** the normal border of the item with a top separator */
	private static final Border ITEM_BORDER_NORMAL_WITHSEP = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ITEM_SEPARATOR), BorderFactory.createEmptyBorder(ITEM_PADDING_TOP - 1, ITEM_PADDING_LEFT, ITEM_PADDING_BOTTOM, ITEM_PADDING_RIGHT));
	
	/** label that is used to compute the preferred height of a html label */
	private static final JLabel prefHeightHTMLLabel = new JLabel();
	
	/**
	 * Creates a new exercises list.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the evaluation panel. The following language labels are available:
	 * <ul>
	 * 		<li><i>EXERCISESLIST_TITLE</i>: the title of the exercises list</li>
	 * 		<li><i>EXERCISESLIST_ITEM_EXERCISE</i>: the title of an exercise item</li>
	 * 		<li><i>EXERCISESLIST_ITEM_CREDIT</i>: the description for a credit</li>
	 * 		<li><i>EXERCISESLIST_ITEM_CREDITS</i>: the description for credits</li>
	 * 		<li><i>EXERCISESLIST_ITEM_RESULT</i>: the description of the result label in an exercise item</li>
	 * 		<li><i>EXERCISESLIST_ITEM_ATTEMPTS</i>: the description of the attempts label in an exercise item</li>
	 * 		<li><i>EXERCISESLIST_ITEM_LAST_SOLUTION</i>: the description of the last solution label in an exercise item</li>
	 * 		<li><i>EXERCISESLIST_ITEM_BTN_SOLVE</i>: the tooltip of the solve button in an exercise item</li>
	 * 		<li><i>EXERCISESLIST_ITEM_BTN_GIVEUP</i>: the tooltip of the give up button in an exercise item</li>
	 * 		<li><i>EXERCISESLIST_ITEM_BTN_INPUTHINT</i>: the tooltip of the input hint button in an exercise item</li>
	 * 		<li><i>EXERCISESLIST_ITEM_BTN_ANNOTATION</i>: the tooltip of the annotation button in an exercise item</li>
	 * 		<li><i>EXERCISESLIST_ITEM_STATE_SUCCEEDED</i>: the tooltip of the state succeeded</li>
	 * 		<li><i>EXERCISESLIST_ITEM_STATE_FAILED</i>: the tooltip of the state failed</li>
	 * 		<li><i>EXERCISESLIST_ITEM_STATE_FAILEDHINT</i>: the tooltip of the state failed if a hint message is available</li>
	 * 		<li><i>EXERCISESLIST_EVALUATION</i>: the text of the header label of the evaluation panel</li>
	 * 		<li><i>EXERCISESLIST_EVALUATION_CREDITS</i>: the text of the credits label of the evaluation panel</li>
	 * 		<li><i>EXERCISESLIST_EVALUATION_INPERCENT</i>: the text of the credits in percent label of the evaluation panel</li>
	 * 		<li><i>EXERCISESLIST_EVALUATION_GRADING</i>: the text of the grading label of the evaluation panel</li>
	 * 		<li><i>EXERCISESLIST_EVALUATION_GRADING_SCALE</i>: the title of the grading scale dialog of the evaluation panel that is also used as the tooltip of the grading value button</li>
	 * 		<li><i>MSG_INFO_EXERCISEMODE</i>: the info message if the user wants to enable the exercise mode but the algorithm is still running</li>
	 * 		<li><i>MSG_INFO_TITLE_EXERCISEMODE</i>: the title of the info message</li>
	 * </ul>
	 * 
	 * @param langFile the language file or <code>null</code> if the list should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @since 1.0
	 */
	public ExercisesListView(final LanguageFile langFile, final String langID) {
		super(LanguageFile.getLabel(langFile, "EXERCISESLIST_TITLE", langID, "Exercises"), true, langFile, langID);
		
		this.langFile = langFile;
		this.langID = langID;
		this.exerciseModeHandler = null;
		this.evalPanel = new EvaluationPanel(langFile, langID);
		this.activeItem = null;
		this.performingExam = false;
		this.labelExercise = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_EXERCISE", langID, "Exercise");
		this.labelCredit = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_CREDIT", langID, "Credit");
		this.labelCredits = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_CREDITS", langID, "Credits");
		this.labelResult = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_RESULT", langID, "Result:");
		this.labelAttempts = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_ATTEMPTS", langID, "Attempts:");
		this.labelLastSolution = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_LAST_SOLUTION", langID, "Last Solution:");
		this.labelSolve = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_BTN_SOLVE", langID, "Solve Exercise");
		this.labelGiveUp = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_BTN_GIVEUP", langID, "Give Up Exercise");
		this.labelInputHint = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_BTN_INPUTHINT", langID, "Show Input Hint");
		this.labelAnnotation = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_BTN_ANNOTATION", langID, "Show Annotation");
		this.labelSucceeded = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_STATE_SUCCEEDED", langID, "Succeeded");
		this.labelFailed = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_STATE_FAILED", langID, "Failed");
		this.labelFailedHint = LanguageFile.getLabel(langFile, "EXERCISESLIST_ITEM_STATE_FAILEDHINT", langID, "Failed - Why?");
		this.msgInfoExerciseMode = LanguageFile.getLabel(langFile, "MSG_INFO_EXERCISEMODE", langID, "The exercise mode can only be activated/deactivated when the algorithm is stopped!");
		this.msgTitleExerciseMode = LanguageFile.getLabel(langFile, "MSG_INFO_TITLE_EXERCISEMODE", langID, "Exercise Mode");
		this.inputHintDlgTitle = LanguageFile.getLabel(langFile, "EXERCISE_INPUTHINT_TITLE", langID, "Input Hint");
		
		// set the layout of the content
		content.setLayout(new BorderLayout());
		
		itemList = new JPanel();
		itemList.setLayout(new GridBagLayout());
		
		scrollPane = new JScrollPane(itemList);
		
		// specify the colors of the components
		itemList.setBackground(Color.white);
		scrollPane.setBackground(Color.white);
		scrollPane.getViewport().setBackground(Color.white);
		evalPanel.setBackground(SystemColor.control);
		
		// add the components of the exercise list
		content.add(scrollPane, BorderLayout.CENTER);
		content.add(evalPanel, BorderLayout.SOUTH);
		
		itemList.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				ExercisesListView.this.scrollPane.getViewport().setViewPosition(new Point(0, ExercisesListView.this.itemList.getHeight()));
			}
		});
		
		scrollPane.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				ExercisesListView.this.itemList.revalidate();
			}
		});
	}
	
	/**
	 * The components of an exercises list may not be removed meaning this method does nothing!
	 * 
	 * @since 1.0
	 */
	@Override
	public void removeAll() {
		// this is not allowed
	}
	
	/**
	 * Sets whether the exercises list should be visible.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param visible <code>true</code> if the exercises list should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
	}
	
	@Override
	public void setHandler(AlgorithmExerciseHandler handler) {
		exerciseModeHandler = handler;
	}

	@Override
	public void beginExam() {
		// a new exam begins so reset the exercises list
		reset();
		
		performingExam = true;
	}

	@Override
	public void endExam(boolean canceled) {
		// evaluate the exercise but only if the user does not cancel the exam
		if(!canceled) {
			final Component[] components = itemList.getComponents();
			Item item;
			float totalCredits = 0.0f;
			float achievedCredits = 0.0f;
			
			for(Component c : components) {
				if(c instanceof Item) {
					item = (Item)c;
					totalCredits += item.getTotalCredits();
					achievedCredits += item.getAchievedCredits();
				}
			}
			
			// update the evaluation panel
			evalPanel.update(totalCredits, achievedCredits);
		}
		
		performingExam = false;
	}

	@Override
	public void beforeProcessingExercise(AlgorithmExercise<?> exercise, AlgorithmStep step) {
		if(activeItem != null)
			activeItem.setBackground(ITEM_BACKGROUND);
		
		// create a new item for the exercise and set it as the active one
		activeItem = new Item(exercise, step, itemList.getComponentCount() > 0);
		activeItem.setBackground(ACTIVE_ITEM_BACKGROUND);
		
		if(itemList.getComponentCount() > 0)
			itemList.remove(itemList.getComponentCount() - 1);
		
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = itemList.getComponentCount();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		
		// add the item to the panel which puts the item at the bottom of the list
		itemList.add(activeItem, gbc);
		
		final GridBagConstraints dummyGBC = new GridBagConstraints();
		dummyGBC.gridx = 0;
		dummyGBC.gridy = itemList.getComponentCount();
		dummyGBC.fill = GridBagConstraints.BOTH;
		dummyGBC.weightx = 1.0;
		dummyGBC.weighty = 1.0;
		itemList.add(new JLabel(), dummyGBC);
		
		itemList.revalidate();
	}

	@Override
	public void afterProcessingExercise(AlgorithmExercise<?> exercise, ExamResult result, String lastSolution) {
		// finish the process of the active item
		if(activeItem != null)
			activeItem.endProcess(result == ExamResult.SUCCEEDED, lastSolution);
	}

	@Override
	public void afterSolvingExercise(AlgorithmExercise<?> exercise, boolean succeeded, String solution) {
		// process the active item
		if(activeItem != null)
			activeItem.doProcess(succeeded, solution);
	}

	@Override
	public void reset() {
		// reset the list
		itemList.removeAll();
		// reset the evaluation area
		evalPanel.reset();
		
		repaint();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void repaintComponent() {
		super.repaintComponent();
		itemList.repaint();
		evalPanel.repaint();
	}
	
	/**
	 * Closes the exercises list view.
	 * 
	 * @since 1.0
	 */
	@Override
	protected void close() {
		// the exercises provider can only be closed when there is currently no exam performed
		if(performingExam) {
			JOptionPane.showMessageDialog(null, msgInfoExerciseMode, msgTitleExerciseMode, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		// note: when the exercise mode is disabled the provider is automatically set to invisible
		exerciseModeHandler.setExerciseModeEnabled(false);
	}
	
	/**
	 * Computes the preferred height of a html text in a label.
	 * 
	 * @param text the html text
	 * @param width the fixed width
	 * @return the preferred height
	 * @since 1.0
	 */
    private static Dimension getPreferredHeight(final String text, final int width) {
        prefHeightHTMLLabel.setText(text);
        
        final javax.swing.text.View view = (javax.swing.text.View)prefHeightHTMLLabel.getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey);
        view.setSize(width, 0);
 
        float w = view.getPreferredSpan(javax.swing.text.View.X_AXIS);
        float h = view.getPreferredSpan(javax.swing.text.View.Y_AXIS);
 
        return new Dimension((int)Math.ceil(w), (int)Math.ceil(h));
    }
	
	/**
	 * Represents an exercise in the list of all exercises.
	 * 
	 * @author jdornseifer
	 * @version 1.1
	 * @since 1.0
	 */
	private class Item extends JPanel {

		private static final long serialVersionUID = 1L;
		
		/** the associated exercise */
		private final AlgorithmExercise<?> exercise;
		/** the related step */
		private final AlgorithmStep step;
		/** flag that indicates whether a separator should be shown */
		private final boolean showSeparator;
		/** the attempts the user needs to solve the exercise */
		private int attempts;
		/** flag that indicates that the item is locked meaning that it is processed */
		private boolean locked;
		/** flag that indicates whether the exercise succeeded */
		private boolean succeeded;
		/** the lable for the header of the item */
		private final JLabel lblHeader;
		/** the lable for the text of the exercise */
		private final JLabel lblExerciseText;
		/** the lable for result description */
		private final JLabel lblResult;
		/** the lable for the result value */
		private final JLabel lblResultValue;
		/** the lable for the attempts description */
		private final JLabel lblAttempts;
		/** the lable for the attempts value */
		private final JLabel lblAttemptsValue;
		/** the button to solve the exercise */
		private final JButton btnSolve;
		/** the button to give up the exercise */
		private final JButton btnGiveUp;
		/** the button to show an input hint for the exercise or <code>null</code> if there is no input hint */
		private final JButton btnInputHint;
		/** the button to show the annotation of a step or <code>null</code> if there is no annotation */
		private final JButton btnAnnotation;
		/** the label for the last solution description */
		private final JLabel lblLastSolution;
		/** the label for the last solution value */
		private final JLabel lblLastSolutionValue;
		/** the separator that separates the exercise text from the information area below */
		private final JSeparator separator;
		/** the separator that separates the exercise buttons from the additional buttons or <code>null</code> if there are no additional buttons */
		private final JSeparator separatorAdditionalButtons;
		/** the minimum width of the item */
		private final int minimumWidth;
		/** the last value of the preferred size */
		private Dimension lastPrefSize;
		/** the preferred size of the header */
		private final Dimension prefSizeHeader;
		/** the preferred size of the result label */
		private final Dimension prefSizeResult;
		/** the preferred size of the attempts label */
		private final Dimension prefSizeAttempts;
		/** the preferred size of the last solution label */
		private final Dimension prefSizeLastSol;
		/** the preferred size of the solve button */
		private final Dimension prefSizeSolveBtn;
		/** the preferred size of the give up button */
		private final Dimension prefSizeGiveUpBtn;
		/** the preferred size of the input hint button */
		private final Dimension prefSizeInputHintBtn;
		/** the preferred size of the annotation button */
		private final Dimension prefSizeAnnotationBtn;
		/** the preferred size of the separator */
		private final Dimension prefSizeSeparator;
		/** the preferred size of the separator that separates the additional buttons from the exercise buttons */
		private final Dimension prefSizeSeparatorAdditionalButtons;
		/** the mouse listener of the result value label */
		private final MouseListener resultValueMouseListener;
		
		/** the inner padding between the results/attempts/last solution column and the buttons */
		private final int INNER_MIN_PADDING = 50;
		/** the interspacing between two buttons */
		private final int BUTTON_INTERSPACING = 5;
		/** the interspacing between the additional buttons and the exercise buttons */
		private final int BUTTONGROUP_INTERSPACING = 10;
		
		/**
		 * Creates a new exercise item and highlights it as the active one.
		 * 
		 * @param exercise the exercise
		 * @param step the related step or <code>null</code>
		 * @param showSeparator <code>true</code> if the item should have a separator to the item above this one otherwise <code>false</code>
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if exercise is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public Item(final AlgorithmExercise<?> exercise, final AlgorithmStep step, final boolean showSeparator) throws IllegalArgumentException {
			if(exercise == null)
				throw new IllegalArgumentException("No valid argument!");

			final Font f = UIManager.getFont("Label.font");
			final String creditText = (exercise.getCredits() == 1.0f) ? ExercisesListView.this.labelCredit : ExercisesListView.this.labelCredits;
			
			this.exercise = exercise;
			this.step = step;
			this.showSeparator = showSeparator;
			this.attempts = 0;
			this.locked = false;
			this.succeeded = false;
			this.lblHeader = new JLabel(ExercisesListView.this.labelExercise + " (" + MathUtils.formatFloat(exercise.getCredits()) + " " + creditText + "):");
			this.lblHeader.setFont(f.deriveFont(Font.BOLD));
			add(lblHeader);
			this.lblExerciseText = new JLabel(getExerciseText(exercise));
			add(lblExerciseText);
			this.lblResult = new JLabel(ExercisesListView.this.labelResult);
			this.lblResult.setForeground(Color.gray);
			add(lblResult);
			this.lblResultValue = new JLabel();
			this.lblResultValue.setForeground(Color.gray);
			add(lblResultValue);
			this.lblAttempts = new JLabel(ExercisesListView.this.labelAttempts);
			this.lblAttempts.setForeground(Color.gray);
			add(lblAttempts);
			this.lblAttemptsValue = new JLabel("" + attempts);
			this.lblAttemptsValue.setForeground(Color.gray);
			add(lblAttemptsValue);
			this.lblLastSolution = new JLabel(ExercisesListView.this.labelLastSolution);
			this.lblLastSolution.setForeground(Color.gray);
			add(lblLastSolution);
			this.lblLastSolutionValue = new JLabel();
			this.lblLastSolutionValue.setForeground(Color.gray);
			add(lblLastSolutionValue);
			this.btnSolve = new JButton(Resources.getInstance().EXERCISE_SOLVE_ICON);
			this.btnSolve.setToolTipText(ExercisesListView.this.labelSolve);
			add(btnSolve);
			this.btnGiveUp = new JButton(Resources.getInstance().EXERCISE_GIVEUP_ICON);
			this.btnGiveUp.setToolTipText(ExercisesListView.this.labelGiveUp);
			add(btnGiveUp);
			this.btnInputHint = (exercise.hasInputHint()) ? new JButton(Resources.getInstance().EXERCISE_INPUTHINT_ICON) : null;
			if(btnInputHint != null) {
				btnInputHint.setToolTipText(ExercisesListView.this.labelInputHint);
				add(btnInputHint);
			}
			this.btnAnnotation = (step != null && step.getAnnotation() != null) ? new JButton(Resources.getInstance().ANNOTATION_ICON) : null;
			if(btnAnnotation != null) {
				btnAnnotation.setToolTipText(ExercisesListView.this.labelAnnotation);
				add(btnAnnotation);
			}
			this.separatorAdditionalButtons = (btnInputHint != null || btnAnnotation != null) ? new JSeparator(JSeparator.VERTICAL) : null;
			if(separatorAdditionalButtons != null)
				add(separatorAdditionalButtons);
			this.separator = new JSeparator();
			add(separator);
			this.lastPrefSize = new Dimension(-1, 0);
			
			// get the preferred sizes of the static components (this has to be done after the components are initialized with
			// their static appearance)
			prefSizeHeader = lblHeader.getPreferredSize();
			prefSizeResult = lblResult.getPreferredSize();
			prefSizeAttempts = lblAttempts.getPreferredSize();
			prefSizeLastSol = lblLastSolution.getPreferredSize();
			prefSizeSolveBtn = btnSolve.getPreferredSize();
			prefSizeGiveUpBtn = btnGiveUp.getPreferredSize();
			prefSizeInputHintBtn = (btnInputHint != null) ? btnInputHint.getPreferredSize() : new Dimension(0, 0);
			prefSizeAnnotationBtn = (btnAnnotation != null) ? btnAnnotation.getPreferredSize() : new Dimension(0, 0);
			prefSizeSeparator = separator.getPreferredSize();
			prefSizeSeparatorAdditionalButtons = (separatorAdditionalButtons != null) ? separatorAdditionalButtons.getPreferredSize() : new Dimension(0, 0);
			
			// create a click listener for the result value label
			resultValueMouseListener = new MouseAdapter() {
				
				@Override
				public void mouseClicked(MouseEvent e) {
					Item.this.showHintDialog(Item.this.exercise.getLastFailedHintMessage(), ExercisesListView.this.labelFailedHint);
				}
			};
			
			// compute the minimum width of the item meaning that the item cannot be smaller than this size
			minimumWidth = computeMinimumWidth();
			
			// highlight the item as the active one
			setBackground(ACTIVE_ITEM_BACKGROUND);
			setBorder(ITEM_BORDER_ACTIVE);
			
			// the layout of the item components is self-computed (see doLayout())
			setLayout(null);
			
			// add action listeners to handle user events
			btnSolve.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Item.this.setLockStatus(true);
					
					final boolean hasSolution = Item.this.exercise.solve();
					
					// if the user has canceled the request for entering the solution then unlock the item and show the input hint
					if(!hasSolution) {
						Item.this.setLockStatus(false);
						Item.this.showHintDialog(Item.this.exercise.getInputHintMessage(ExercisesListView.this.langFile, ExercisesListView.this.langID), ExercisesListView.this.inputHintDlgTitle);
					}
				}
			});
			btnGiveUp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Item.this.setLockStatus(true);
					Item.this.exercise.giveUp();
				}
			});
			if(btnInputHint != null)
				btnInputHint.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						Item.this.showHintDialog(Item.this.exercise.getInputHintMessage(ExercisesListView.this.langFile, ExercisesListView.this.langID), ExercisesListView.this.inputHintDlgTitle);
					}
				});
			if(btnAnnotation != null)
				btnAnnotation.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						final Annotation a = Item.this.step.getAnnotation();
						if(a != null)
							new AnnotationDialog(ExercisesListView.this.exerciseModeHandler.getHost(), a, ExercisesListView.this.langFile, ExercisesListView.this.langID).setVisible(true);
					}
				});
		}
		
		/**
		 * Computes the preferred size of the item using a specified fixed width.
		 * 
		 * @param width the fixed width the item should have
		 * @return the preferred size of the item
		 * @since 1.0
		 */
		public Dimension getPreferredSize() {
			final int itemListWidth = (scrollPane.getViewport().getWidth() >= minimumWidth) ? scrollPane.getViewport().getWidth() : minimumWidth;
			
			// if the fixed width complies with the last preferred width then return the cached size
			// because the height may not changed
			if(itemListWidth == lastPrefSize.width)
				return lastPrefSize;
			
			// clean the width from the padding
			int cleanWidth = itemListWidth - ITEM_PADDING_LEFT - ITEM_PADDING_RIGHT;
			
			lastPrefSize = new Dimension(itemListWidth, 0);
			
			final Dimension exerciseTextDim = ExercisesListView.getPreferredHeight(lblExerciseText.getText(), cleanWidth);
			final int maxInnerHeight = MathUtils.max(prefSizeResult.height + prefSizeAttempts.height, prefSizeSolveBtn.height, prefSizeGiveUpBtn.height);
			
			lastPrefSize.height += prefSizeHeader.height;
			lastPrefSize.height += exerciseTextDim.height;
			lastPrefSize.height += prefSizeSeparator.height;
			lastPrefSize.height += maxInnerHeight;
			lastPrefSize.height += prefSizeLastSol.height;
			
			// finally add the padding to the height
			lastPrefSize.height += ITEM_PADDING_TOP + ITEM_PADDING_BOTTOM;
			
			return lastPrefSize;
		}
		
		@Override
		public void doLayout() {
			/*
			 * Layout:
			 * 
			 * Header (x Credits):
			 * Exercise Text
			 * -----------------------
			 * Result:   x    ===  ===
			 * Attempts: x    ===  ===  ----> Solve and Give Up Button + additional buttons (like input hint button and annotation button)
			 * Last Solution: x
			 */
			
			final int width = getWidth() - ITEM_PADDING_LEFT - ITEM_PADDING_RIGHT;
			int currY = ITEM_PADDING_TOP;
			int currX = ITEM_PADDING_LEFT;
			
			lblHeader.setBounds(currX, currY, prefSizeHeader.width, prefSizeHeader.height);
			currY += prefSizeHeader.height;
			
			final Dimension etDim = ExercisesListView.getPreferredHeight(lblExerciseText.getText(), width);
			lblExerciseText.setBounds(currX, currY, etDim.width, etDim.height);
			currY += etDim.height;
			
			separator.setBounds(currX, currY, width, prefSizeSeparator.height);
			currY += prefSizeSeparator.height;

			final int innerAreaY = currY;
			final int maxInnerHeight = MathUtils.max(prefSizeResult.height + prefSizeAttempts.height, prefSizeSolveBtn.height, prefSizeGiveUpBtn.height);
			final int resultY = innerAreaY + (maxInnerHeight - (prefSizeResult.height + prefSizeAttempts.height)) / 2;
			final int solveBtnY = innerAreaY + (maxInnerHeight - prefSizeSolveBtn.height) / 2;
			final int giveUpBtnY = innerAreaY + (maxInnerHeight - prefSizeGiveUpBtn.height) / 2;
			final int inputHintBtnY = innerAreaY + (maxInnerHeight - prefSizeInputHintBtn.height) / 2;
			final int annotationBtnY = innerAreaY + (maxInnerHeight - prefSizeAnnotationBtn.height) / 2;
			final int colWidth = MathUtils.max(prefSizeResult.width, prefSizeAttempts.width, prefSizeLastSol.width);
			
			lblResult.setBounds(currX, resultY, prefSizeResult.width, prefSizeResult.height);
			final Dimension rvDim = lblResultValue.getPreferredSize();
			lblResultValue.setBounds(currX + colWidth + 3, resultY + (prefSizeResult.height - rvDim.height) / 2, rvDim.width, rvDim.height);
			currY = resultY + prefSizeResult.height;
			
			lblAttempts.setBounds(currX, currY, prefSizeAttempts.width, prefSizeAttempts.height);
			final Dimension avDim = lblAttemptsValue.getPreferredSize();
			lblAttemptsValue.setBounds(currX + colWidth + 3, currY + (prefSizeAttempts.height - avDim.height) / 2, avDim.width, avDim.height);
			
			final int additionalBtnX = width - prefSizeAnnotationBtn.width - prefSizeInputHintBtn.width - ((btnInputHint != null && btnAnnotation != null) ? BUTTON_INTERSPACING : 0) - 1;
			int annotationBtnX = additionalBtnX;
			if(btnInputHint != null) {
				btnInputHint.setBounds(additionalBtnX, inputHintBtnY, prefSizeInputHintBtn.width, prefSizeInputHintBtn.height);
				annotationBtnX += prefSizeInputHintBtn.width + BUTTON_INTERSPACING;
			}
			if(btnAnnotation != null)
				btnAnnotation.setBounds(annotationBtnX, annotationBtnY, prefSizeAnnotationBtn.width, prefSizeAnnotationBtn.height);
			if(separatorAdditionalButtons != null)
				separatorAdditionalButtons.setBounds(additionalBtnX - (BUTTONGROUP_INTERSPACING + prefSizeSeparatorAdditionalButtons.width) / 2, giveUpBtnY, prefSizeSeparatorAdditionalButtons.width, prefSizeGiveUpBtn.height);
			
			final int giveUpBtnX = additionalBtnX - BUTTONGROUP_INTERSPACING - prefSizeGiveUpBtn.width;
			btnGiveUp.setBounds(giveUpBtnX, giveUpBtnY, prefSizeGiveUpBtn.width, prefSizeGiveUpBtn.height);
			btnSolve.setBounds(giveUpBtnX - BUTTON_INTERSPACING - prefSizeSolveBtn.width, solveBtnY, prefSizeSolveBtn.width, prefSizeSolveBtn.height);
			
			currY = innerAreaY + maxInnerHeight;
			lblLastSolution.setBounds(currX, currY, prefSizeLastSol.width, prefSizeLastSol.height);
			final Dimension lsvDim = lblLastSolutionValue.getPreferredSize();
			lblLastSolutionValue.setBounds(currX + colWidth + 3, currY, width - currX - colWidth - 3, lsvDim.height);
		}
		
		/**
		 * Processes the item.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * Do not forget to {@link #lock()} the item, if the processing of the associated exercise is finished!
		 * 
		 * @param succeeded <code>true</code> if the associated exercise is solved correct otherwise <code>false</code>
		 * @param lastSolution the last solution the user has entered
		 * @since 1.0
		 */
		public void doProcess(final boolean succeeded, final String lastSolution) {
			// unlock the item state
			setLockStatus(false);
			
			// increase the attempts
			attempts++;
			
			// update the content
			update(succeeded, lastSolution);
		}
		
		/**
		 * Ends the processing of the item meaning of the associated exercise.
		 * <br><br>
		 * This updates the item with the specified values and invokes {@link #lock()} and deactivates the highlight style of the item.
		 * 
		 * @param succeeded the final result of the exercise
		 * @param lastSolution the last solution the user has entered
		 * @since 1.0
		 */
		public void endProcess(final boolean succeeded, final String lastSolution) {
			// update the content
			update(succeeded, lastSolution);
			
			// lock the item because it is processed
			lock();
		}
		
		/**
		 * Gets the total amount of credits the user can achieve.
		 * 
		 * @return the total amount of credits
		 * @since 1.0
		 */
		public float getTotalCredits() {
			return exercise.getCredits();
		}
		
		/**
		 * Gets the credits the user has achieved.
		 * <br><br>
		 * The credits are calculated from the credits of the exercise divided by the attempts but if the exercise succeeded.
		 * Otherwise the credits the user achieved is zero.
		 * 
		 * @return the credits
		 * @since 1.0
		 */
		public float getAchievedCredits() {
			if(succeeded && attempts > 0)
				return exercise.getCredits() / attempts;
			else
				return 0.0f;
		}
		
		/**
		 * Computes the minimum width of the item.
		 * 
		 * @return the minimum width
		 * @since 1.0
		 */
		private int computeMinimumWidth() {
			final int colWidth = MathUtils.max(prefSizeResult.width, prefSizeAttempts.width, prefSizeLastSol.width);
			
			// calculate the additional space of the input hint button and the annotation button that are both optional
			int additionalBtnSpace = (btnInputHint != null || btnAnnotation != null) ? BUTTONGROUP_INTERSPACING : 0;
			if(btnInputHint != null)
				additionalBtnSpace += prefSizeInputHintBtn.width;
			if(btnAnnotation != null)
				additionalBtnSpace += prefSizeAnnotationBtn.width;
			if(btnInputHint != null && btnAnnotation != null)
				additionalBtnSpace += BUTTON_INTERSPACING;
			
			return Math.max(prefSizeHeader.width, colWidth + INNER_MIN_PADDING + prefSizeSolveBtn.width + BUTTON_INTERSPACING + prefSizeGiveUpBtn.width + additionalBtnSpace);
		}
		
		/**
		 * Updates the item's content.
		 * 
		 * @param succeeded <code>true</code> if the associated exercise is solved correct otherwise <code>false</code>
		 * @param lastSolution the last solution the user has entered
		 * @since 1.0
		 */
		private void update(final boolean succeeded, final String lastSolution) {
			final boolean hasLastFailedHint = exercise.hasLastFailedHint();
			final boolean resultValueAsButton = (!succeeded && hasLastFailedHint);
			Icon icon;
			String tooltip;
			
			this.succeeded = succeeded;
			
			if(!succeeded) {
				icon = hasLastFailedHint ? Resources.getInstance().FAILED_HINT_ICON : Resources.getInstance().FAILED_ICON;
				tooltip = hasLastFailedHint ? ExercisesListView.this.labelFailedHint : ExercisesListView.this.labelFailed;
			}
			else {
				// the exercise succeeded but the user needs more then one attempt then show the sufficient icon
				icon = (attempts > 1) ? Resources.getInstance().SUFFICIENT_ICON : Resources.getInstance().SUCCEEDED_ICON;
				tooltip = ExercisesListView.this.labelSucceeded;
			}
			
			// update the attempts value
			lblAttemptsValue.setText("" + attempts);
			
			// update the result value
			lblResultValue.setIcon(icon);
			lblResultValue.setToolTipText(tooltip);
			lblResultValue.setCursor(resultValueAsButton ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : null);
			lblResultValue.removeMouseListener(resultValueMouseListener);
			if(resultValueAsButton)
				lblResultValue.addMouseListener(resultValueMouseListener);
			
			// display the last solution
			lblLastSolutionValue.setText(lastSolution);
			lblLastSolutionValue.setToolTipText(lblLastSolutionValue.getText());
			
			repaint();
		}
		
		/**
		 * Locks the item meaning that the buttons to solve or give up the exercise are disabled that
		 * the user cannot enter new solutions. This indicates that the exercise is finished.
		 * <br><br>
		 * It is not possible to unlock an item because each item is only available for solving one exercise.
		 * 
		 * @since 1.0
		 */
		private void lock() {
			setLockStatus(true);
			locked = true;
			
			// reset the background
			setBackground(ITEM_BACKGROUND);
			
			// separate the item from the items above?
			setBorder(showSeparator ? ITEM_BORDER_NORMAL_WITHSEP : ITEM_BORDER_NORMAL);
			
			repaint();
		}
		
		/**
		 * Sets the lock status of the item but this is only possible if their was no call to {@link #lock()} previously.
		 * 
		 * @param status the status
		 * @since 1.0
		 */
		private void setLockStatus(final boolean status) {
			if(locked)
				return;
			
			btnSolve.setEnabled(!status);
			btnGiveUp.setEnabled(!status);
			if(btnInputHint != null)
				btnInputHint.setEnabled(!status);
			if(btnAnnotation != null)
				btnAnnotation.setEnabled(!status);
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
		 * Shows a hint dialog.
		 * 
		 * @param annotation the annotation to be shown
		 * @param title the title of the dialog
		 * @since 1.2
		 */
		private void showHintDialog(final Annotation annotation, final String title) {
			if(annotation == null)
				return;

			final PluginHost host = exerciseModeHandler.getHost();
			final JEditorPane editorPane = new JEditorPane();
			final JScrollPane editorScrollPane = new JScrollPane(editorPane);
			editorPane.setEditorKit(new AnnotationViewKit(annotation.getImagesList()));
			editorPane.setText(annotation.getText());
			editorPane.setEditable(false);
			final JOptionPane infoPane = new JOptionPane(editorScrollPane, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
			final JDialog infoDlg = infoPane.createDialog(title);
			host.adaptDialog(infoDlg);
			infoDlg.setVisible(true);
		}
		
	}
	
	/**
	 * Represents an evaluation panel.
	 * <br><br>
	 * <b>Language dependent labels, tooltips and messages</b>:<br>
	 * Use {@link EvaluationPanel#EvaluationPanel(LanguageFile, String)} to specify a language file from which
	 * labels, tooltips and messages are read by the given language id. The following language labels are available:
	 * <ul>
	 * 		<li><i>EXERCISESLIST_EVALUATION</i>: the text of the header label of the evaluation panel</li>
	 * 		<li><i>EXERCISESLIST_EVALUATION_CREDITSACHIEVED</i>: the text of the credits label of the evaluation panel</li>
	 * 		<li><i>EXERCISESLIST_EVALUATION_INPERCENT</i>: the text of the credits in percent label of the evaluation panel</li>
	 * 		<li><i>EXERCISESLIST_EVALUATION_GRADING</i>: the text of the grading label of the evaluation panel</li>
	 * 		<li><i>EXERCISESLIST_EVALUATION_GRADING_SCALE</i>: the title of the grading scale dialog of the evaluation panel that is also used as the tooltip of the grading value button</li>
	 * </ul>
	 * <b>>> Prepared language file</b>:<br>
	 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
	 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class EvaluationPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		
		/** the header label */
		private final JLabel lblEvaluation;
		/** the credits label */
		private final JLabel lblCredits;
		/** the credits achieved label */
		private final JLabel lblCreditsAchieved;
		/** the in percent label */
		private final JLabel lblInPercent;
		/** the credits in percent label */
		private final JLabel lblCreditsInPercent;
		/** the grading label */
		private final JLabel lblGrading;
		/** the grading value button */
		private final JButton btnGradingValue;
		/** the grading scale */
		private final float[][] gradingScale;
		/** the language dependent text of the grading scale dialog */
		private final String gradingScaleDlgTitle;
		
		/** the default value of the credits achieved label */
		private static final String DEF_CREDITS_ACHIEVED = "-/-";
		/** the default value of the credits in percent label */
		private static final String DEF_CREDITS_PERCENT = "-%";
		/** the default value of the grading value button */
		private static final String DEF_GRADING_VALUE = "-";
		
		/**
		 * Creates a new evaluation panel.
		 * <br><br>
		 * <b>Language</b>:<br>
		 * You can specify a {@link LanguageFile} and a language id to display language dependent
		 * messages and tooltips in the evaluation panel. The following language labels are available:
		 * <ul>
		 * 		<li><i>EXERCISESLIST_EVALUATION</i>: the text of the header label of the evaluation panel</li>
		 * 		<li><i>EXERCISESLIST_EVALUATION_CREDITSACHIEVED</i>: the text of the credits label of the evaluation panel</li>
		 * 		<li><i>EXERCISESLIST_EVALUATION_INPERCENT</i>: the text of the credits in percent label of the evaluation panel</li>
		 * 		<li><i>EXERCISESLIST_EVALUATION_GRADING</i>: the text of the grading label of the evaluation panel</li>
		 * 		<li><i>EXERCISESLIST_EVALUATION_GRADING_SCALE</i>: the title of the grading scale dialog of the evaluation panel that is also used as the tooltip of the grading value button</li>
		 * </ul>
		 * 
		 * @param langFile the language file or <code>null</code> if the list should not use language independent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
		 * @param langID the language id
		 * @since 1.0
		 */
		public EvaluationPanel(final LanguageFile langFile, final String langID) {
			super();
			
			lblEvaluation = new JLabel(LanguageFile.getLabel(langFile, "EXERCISESLIST_EVALUATION", langID, "Evaluation"));
			lblCredits = new JLabel(LanguageFile.getLabel(langFile, "EXERCISESLIST_EVALUATION_CREDITSACHIEVED", langID, "Credits:"));
			lblCreditsAchieved = new JLabel(DEF_CREDITS_ACHIEVED);
			lblInPercent = new JLabel(LanguageFile.getLabel(langFile, "EXERCISESLIST_EVALUATION_INPERCENT", langID, "In Percent:"));
			lblCreditsInPercent = new JLabel(DEF_CREDITS_PERCENT);
			lblGrading = new JLabel(LanguageFile.getLabel(langFile, "EXERCISESLIST_EVALUATION_GRADING", langID, "Grading:"));
			btnGradingValue = new JButton(DEF_GRADING_VALUE);
			gradingScaleDlgTitle = LanguageFile.getLabel(langFile, "EXERCISESLIST_EVALUATION_GRADING_SCALE", langID, "Grading Scale");
			btnGradingValue.setToolTipText(gradingScaleDlgTitle);
			gradingScale = createGradingScale();
			
			// change the font of the header label
			final Font f = UIManager.getFont("Label.font");
			lblEvaluation.setFont(f.deriveFont(Font.BOLD));
			
			// set the layout of the panel
			setLayout(new GridBagLayout());
			
			GridBagConstraints gbc = new GridBagConstraints();
			
			// create the layout
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 2;
			gbc.weightx = 0.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			add(lblEvaluation, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			add(lblCredits, gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.0;
			add(lblCreditsAchieved, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			add(lblInPercent, gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.0;
			add(lblCreditsInPercent, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			add(lblGrading, gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 3;
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.0;
			add(btnGradingValue, gbc);
			
			// if the user clicks the grading value button then open a dialog with the grading scale as a table
			btnGradingValue.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// use a text area to display tabulators
					final JTextArea textArea = new JTextArea(getGradingScaleAsTable());
					textArea.setFont(UIManager.getFont("TextPane.font"));
					textArea.setEditable(false);
					
					final JOptionPane pane = new JOptionPane(textArea, JOptionPane.INFORMATION_MESSAGE);
					final JDialog dlg = pane.createDialog(gradingScaleDlgTitle);
					
					if(ExercisesListView.this.exerciseModeHandler.getHost() != null)
						ExercisesListView.this.exerciseModeHandler.getHost().adaptDialog(dlg);
					
					textArea.setBackground(dlg.getBackground());
					dlg.setVisible(true);
				}
			});
		}
		
		/**
		 * Updates the evaluation panel.
		 * 
		 * @param totalCredits the total amount of credits
		 * @param achievedCredits the credits the user has achieved
		 * @since 1.0
		 */
		public void update(final float totalCredits, final float achievedCredits) {
			final float inPercent = (totalCredits > 0.0f) ? achievedCredits / totalCredits : 0.0f;
			
			lblCreditsAchieved.setText(MathUtils.formatFloat(achievedCredits) + "/" + MathUtils.formatFloat(totalCredits));
			lblCreditsInPercent.setText(NumberFormat.getPercentInstance().format(inPercent));
			btnGradingValue.setText(NumberFormat.getInstance().format(getGradingValue(inPercent)));
			
			repaint();
		}
		
		/**
		 * Resets the evaluation panel.
		 * 
		 * @since 1.0
		 */
		public void reset() {
			lblCreditsAchieved.setText(DEF_CREDITS_ACHIEVED);
			lblCreditsInPercent.setText(DEF_CREDITS_PERCENT);
			btnGradingValue.setText(DEF_GRADING_VALUE);
			
			repaint();
		}
		
		/**
		 * Creates a grading scale where a grade <code>i</code> is defined as <code><= gradingScale[i][0] && > gradingScale[i + 1][0]</code>.
		 * 
		 * @return the grading scale
		 * @since 1.0
		 */
		private float[][] createGradingScale() {
			return new float[][] {
					{ 1.0f, 1.0f },
					{ 0.98f, 1.3f },
					{ 0.92f, 1.7f },
					{ 0.86f, 2.0f },
					{ 0.80f, 2.3f },
					{ 0.74f, 2.7f },
					{ 0.68f, 3.0f },
					{ 0.62f, 3.3f },
					{ 0.56f, 3.7f },
					{ 0.52f, 4.0f },
					{ 0.5f, 5.0f }
			};
		}
		
		/**
		 * Gets the grading scale as a table.
		 * 
		 * @return the grading scale in a string representation
		 * @since 1.0
		 */
		private String getGradingScaleAsTable() {
			final StringBuilder string = new StringBuilder();
			float currGrade;
			float currGradeCeiling;
			float currGradeFloor;
			
			for(int i = 0; i < gradingScale.length; i++) {
				if(i == gradingScale.length - 1)
					break;
				
				currGradeCeiling = gradingScale[i][0] * 100;
				currGradeFloor = gradingScale[i + 1][0] * 100;
				currGrade = gradingScale[i][1];
				
				string.append(MathUtils.formatFloat(currGradeCeiling) + "-" + MathUtils.formatFloat(currGradeFloor) + "%\t" + NumberFormat.getInstance().format(currGrade) + "\n");
			}
			
			string.append("< 50%\t" + NumberFormat.getNumberInstance().format(5.0));
			
			return string.toString();
		}
		
		/**
		 * Gets the grade for a given credit value in percent.
		 * 
		 * @param creditsInPercent the credit value in percent meaning between <code>1.0f</code> and <code>0.0f</code>
		 * @return the grade
		 * @since 1.0
		 */
		private float getGradingValue(final float creditsInPercent) {
			for(int i = 0; i < gradingScale.length; i++) {
				// if the last grade is reached then return its value
				if(i == gradingScale.length - 1)
					return gradingScale[i][1];
				else {
					// check whether the credit value in percent is in the current grade
					if(creditsInPercent <= gradingScale[i][0] && creditsInPercent > gradingScale[i + 1][0])
						return gradingScale[i][1];
				}
			}
			
			return 5.0f;
		}
		
	}

}
