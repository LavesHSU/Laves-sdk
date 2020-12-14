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

package lavesdk.algorithm.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lavesdk.algorithm.AlgorithmExercise;
import lavesdk.algorithm.AlgorithmRTE;
import lavesdk.algorithm.text.exceptions.InvalidIdentifierException;

/**
 * Represents the structure of an algorithm text.
 * <br><br>
 * <b>Structure</b>:<br>
 * A text consists of paragraphs and paragraphs consist of steps. A paragraph is a container of steps and the steps
 * represent the actual instructions, that should be executed in the corresponding paragraph.<br>
 * Each paragraph and each step has an identifier. The step identifiers are used in the {@link AlgorithmRTE} to execute a specific algorithm step.
 * <br><br>
 * To get information about which step is currently in execution call {@link #getExecutingStepID()}. Use {@link #getStepByID(int)} or {@link #getParagraphByID(int)}
 * to get a step or paragraph with the aid of an identifier.
 * <br><br>
 * <b>Final exercise</b>:<br>
 * Use {@link #setFinalExercise(AlgorithmExercise)} to set an exercise that has to be solved when the algorithm is terminated. If you want to add
 * exercises to steps then use {@link AlgorithmStep#setExercise(AlgorithmExercise)}.
 * 
 * @see AlgorithmParagraph
 * @see AlgorithmStep
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class AlgorithmText {
	
	/** the list of all paragraphs */
	private final List<AlgorithmParagraph> paragraphs;
	/** the mapping between id <-> paragraph */
	private final Map<Integer, AlgorithmParagraph> paragraphsByID;
	/** the list of all steps */
	private final List<AlgorithmStep> steps;
	/** the mapping between id <-> step */
	private final Map<Integer, AlgorithmStep> stepsByID;
	/** list of listeners */
	private final List<AlgorithmTextListener> listeners;
	/** the step id of the currently active step meaning of the currently executed step in the algorithm runtime environment */
	private int executingStepID;
	/** the font size of latex formulas */
	private float fontSize;
	/** the final exercise of the text */
	private AlgorithmExercise<?> finalExercise;
	
	/** the default font size of the text and the latex formulas which is {@value #FONTSIZE} */
	public static final float FONTSIZE = 12.0f;
	
	/**
	 * Creates a new empty algorithm text.
	 * 
	 * @since 1.0
	 */
	public AlgorithmText() {
		this(FONTSIZE);
	}
	
	/**
	 * Creates a new empty algorithm text.
	 * 
	 * @param fontSize the font size of the text
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if fontSize <code><= 0.0f</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmText(final float fontSize) throws IllegalArgumentException {
		if(fontSize <= 0.0f)
			throw new IllegalArgumentException("No valid argument!");
		
		this.paragraphs = new ArrayList<AlgorithmParagraph>(5);
		this.paragraphsByID = new HashMap<Integer, AlgorithmParagraph>(5);
		this.steps = new ArrayList<AlgorithmStep>();
		this.stepsByID = new HashMap<Integer, AlgorithmStep>();
		this.listeners = new ArrayList<AlgorithmTextListener>(3);
		this.executingStepID = -1;
		this.fontSize = fontSize;
		this.finalExercise = null;
	}
	
	/**
	 * Adds a listener to listen to changes inside the text that means if new steps or
	 * paragraphs were added or if the current step changed.
	 * 
	 * @param listener the listener
	 * @since 1.0
	 */
	public void addTextListener(final AlgorithmTextListener listener) {
		if(listener == null || listeners.contains(listener))
			return;
		
		listeners.add(listener);
	}
	
	/**
	 * Removes an existing listener.
	 * 
	 * @param listener the listener
	 * @since 1.0
	 */
	public void removeTextListener(final AlgorithmTextListener listener) {
		if(listener == null || !listeners.contains(listener))
			return;
		
		listeners.remove(listener);
	}
	
	/**
	 * Gets the number of paragraphs.
	 * 
	 * @return the amount of paragraphs
	 * @since 1.0
	 */
	public int getParagraphCount() {
		return paragraphs.size();
	}
	
	/**
	 * Gets the paragraph at the given index.
	 * 
	 * @param index the index
	 * @return the paragraph
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getParagraphCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmParagraph getParagraph(final int index) throws IndexOutOfBoundsException {
		return paragraphs.get(index);
	}
	
	/**
	 * Gets the paragraph that has the given id.
	 * 
	 * @param id the identifier of the paragraph
	 * @return the paragraph or <code>null</code>, if there is no paragraph with the specified id
	 * @since 1.0
	 */
	public AlgorithmParagraph getParagraphByID(final int id) {
		return paragraphsByID.get(id);
	}
	
	/**
	 * Gets the number of steps that are available in the text.
	 * 
	 * @return the amount of steps
	 * @since 1.0
	 */
	public int getStepCount() {
		return steps.size();
	}
	
	/**
	 * Gets the step at the given index.
	 * 
	 * @param index the index
	 * @return the step
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getStepCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmStep getStep(final int index) throws IndexOutOfBoundsException {
		return steps.get(index);
	}
	
	/**
	 * Gets the step that has the given id.
	 * 
	 * @param id the identifier of the step
	 * @return the step or <code>null</code>, if there is no step with the specified id
	 * @since 1.0
	 */
	public AlgorithmStep getStepByID(final int id) {
		return stepsByID.get(id);
	}
	
	/**
	 * Gets the identifier of the step that is currently executed in the runtime environment of
	 * the algorithm.
	 * 
	 * @see AlgorithmRTE
	 * @return the id of the currently executed step or <code>-1</code> if there is no step in execution
	 * @since 1.0
	 */
	public int getExecutingStepID() {
		return executingStepID;
	}
	
	/**
	 * Sets the identifier of the step that is currently executed in the runtime environment of
	 * the algorithm.
	 * 
	 * @see AlgorithmRTE
	 * @param id the id of the currently executed step or <code>-1</code> if there is no step in execution
	 * @since 1.0
	 */
	public void setExecutingStepID(final int id) {
		executingStepID = id;
		
		fireExecutingStepChanged();
	}
	
	/**
	 * Gets the id of the first step in the first paragraph of the algorithm text.
	 * 
	 * @return the id of the first step or <code>-1</code> if the text does not have steps
	 * @since 1.0
	 */
	public int getFirstStepID() {
		return (steps.size() > 0) ? steps.get(0).getID() : -1;
	}
	
	/**
	 * Gets the font size of the text
	 * 
	 * @return the font size in which the text and the formulas are displayed
	 * @since 1.0
	 */
	public float getFontSize() {
		return fontSize;
	}
	
	/**
	 * Sets the font size of the text.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Because of all formulas have to be recreated this method can consume some time!
	 * 
	 * @param size the font size in which the text and the formulas are displayed
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if size <code><= 0.0f</code>
	 * </ul>
	 * @since 1.0
	 */
	public void setFontSize(final float size) throws IllegalArgumentException {
		if(size <= 0.0f)
			throw new IllegalArgumentException("No valid argument!");
		
		fontSize = size;
		
		// the font size changed which means that all formulas have to be updated
		for(AlgorithmStep s : steps)
			for(int i = 0; i < s.getFormulaCount(); i++)
				s.getFormula(i).updateFormula();
	}
	
	/**
	 * Gets the final exercise of the algorithm.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The final exercise is presented to the user if the algorithm is terminated.
	 * 
	 * @return the final exercise or <code>null</code> if the algorithm has no final exercise
	 * @since 1.0
	 */
	public AlgorithmExercise<?> getFinalExercise() {
		return finalExercise;
	}
	
	/**
	 * Sets the final exercise of the algorithm.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The final exercise is presented to the user if the algorithm is terminated.
	 * 
	 * @param exercise the final exercise or <code>null</code> if the algorithm should not have a final exercise
	 * @since 1.0
	 */
	public void setFinalExercise(final AlgorithmExercise<?> exercise) {
		finalExercise = exercise;
	}
	
	/**
	 * Returns a deep base copy of the algorithm text meaning that the returned text contains the
	 * same structure as this text but some properties (like breakpoints, exercises, etc.) are not included.
	 * 
	 * @return a base copy of this text
	 * @since 1.1
	 */
	public AlgorithmText getBaseCopy() {
		final AlgorithmText text = new AlgorithmText();
		
		for(AlgorithmParagraph p : paragraphs)
			new AlgorithmParagraph(text, p.getName(), p.getID());
		for(AlgorithmStep s : steps)
			new AlgorithmStep(text.getParagraphByID(s.getParagraph().getID()), s.getText(), s.getID(), s.getIndent());
		
		return text;
	}
	
	/**
	 * Adds a new paragraph to the text.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The paragraph must have a valid id! Furthermore the listeners are notified on the changed structure.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param paragraph the paragraph to add
	 * @throws InvalidIdentifierException
	 * <ul>
	 * 		<li>if the id of the paragraph is already existing</li>
	 * </ul>
	 * @since 1.0
	 */
	void addParagraph(final AlgorithmParagraph paragraph) throws InvalidIdentifierException {
		if(paragraph == null)
			return;
		else if(paragraphsByID.containsKey(paragraph.getID()))
			throw new InvalidIdentifierException("id of paragraph is already existing"); 
		
		paragraphs.add(paragraph);
		paragraphsByID.put(paragraph.getID(), paragraph);
		fireStructureChanged();
	}
	
	/**
	 * Adds a new step to the text.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The step must have a valid id!
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param step the step to add
	 * @throws InvalidIdentifierException
	 * <ul>
	 * 		<li>if the id of the step is already existing</li>
	 * </ul>
	 * @since 1.0
	 */
	void addStep(final AlgorithmStep step) throws InvalidIdentifierException {
		if(step == null)
			return;
		else if(stepsByID.containsKey(step.getID()))
			throw new InvalidIdentifierException("id of step is already existing"); 
		
		steps.add(step);
		stepsByID.put(step.getID(), step);
	}
	
	/**
	 * Fires the event for a changed structure.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @since 1.0
	 */
	void fireStructureChanged() {
		for(AlgorithmTextListener l : listeners)
			l.structureChanged();
	}
	
	/**
	 * Fires the event for a changed execution step.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @since 1.0
	 */
	void fireExecutingStepChanged() {
		for(AlgorithmTextListener l : listeners)
			l.executingStepChanged();
	}

}
