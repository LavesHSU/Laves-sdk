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
import java.util.List;

import lavesdk.algorithm.text.exceptions.InvalidIdentifierException;

/**
 * Represents a paragraph of an {@link AlgorithmText}.
 * <br><br>
 * A paragraph consists of {@link AlgorithmStep}s which represent the individual text passages of the paragraph.
 * 
 * @see AlgorithmText
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class AlgorithmParagraph {
	
	/** the list of steps that are associated with this paragraph */
	private final List<AlgorithmStep> steps;
	/** the parent of the paragraph */
	private final AlgorithmText parent;
	/** the (display) name of the paragraph */
	private final String name;
	/** a {@link AlgorithmText}-wide identifier for the paragraph */
	private final int id;
	
	/**
	 * Creates a new paragraph.
	 * 
	 * @param parent the associated {@link AlgorithmText}
	 * @param name the name if the paragraph, like <code>1. Initialization</code> (<b>important</b>: the name is right-trimmed that means whitespace after the name have no effect)
	 * @param id the identifier of the paragraph which has to be unique based on the associated algorithm text
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if parent is null</li>
	 * 		<li>if name is null</li>
	 * 		<li>if id is <code>< 1</code></li>
	 * </ul>
	 * @throws InvalidIdentifierException
	 * <ul>
	 * 		<li>if the id is already existing</li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmParagraph(final AlgorithmText parent, final String name, final int id) throws IllegalArgumentException, InvalidIdentifierException {
		if(parent == null || name == null || id < 1)
			throw new IllegalArgumentException("No valid argument!");
		
		this.steps = new ArrayList<AlgorithmStep>(5);
		this.parent = parent;
		this.name = name;
		this.id = id;
		
		parent.addParagraph(this);
	}
	
	/**
	 * Gets the parent of the paragraph.
	 * 
	 * @return {@link AlgorithmText}
	 * @since 1.0
	 */
	public final AlgorithmText getParent() {
		return parent;
	}
	
	/**
	 * Gets the name of the paragraph.
	 * 
	 * @return the name
	 * @since 1.0
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Gets the identifier of the paragraph.
	 * 
	 * @return the identifier
	 * @since 1.0
	 */
	public final int getID() {
		return id;
	}
	
	/**
	 * Gets the number of steps of this paragraph.
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
	
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Adds a new step to this paragraph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The step must have a valid id! Furthermore the listeners are notified on the changed structure.
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
		if(step == null || steps.contains(step))
			return;
		
		// firstly add the step to the text (this could trigger an InvalidIdentifierException)
		parent.addStep(step);
		// secondly add the step to the list
		steps.add(step);
		// at last notify the listeners
		parent.fireStructureChanged();
	}

}
