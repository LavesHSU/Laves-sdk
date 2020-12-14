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

package lavesdk.algorithm;

import lavesdk.algorithm.AlgorithmExercise.ExamResult;
import lavesdk.algorithm.text.AlgorithmStep;

/**
 * A provider that handles (primary displays) the exercises of an algorithm.
 * <br><br>
 * The provider is notified when an exam is started and ended and when an exercise is achieved during the execution of the algorithm
 * to present the exercise to the user.<br>
 * Furthermore each provider must have a visibility flag that indicates whether the provider (component) is visible to the user.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public interface AlgorithmExerciseProvider {
	
	/**
	 * Sets the handler of the exercise mode.
	 * 
	 * @param handler the handler
	 * @since 1.0
	 */
	public void setHandler(final AlgorithmExerciseHandler handler);
	
	/**
	 * Is invoked when the exam is started meaning more precisely when the algorithm is started in exercise mode.
	 * 
	 * @since 1.0
	 */
	public void beginExam();
	
	/**
	 * Is invoked when the exam is completed meaning more precisely when the algorithm is stopped or its execution is completed.
	 * 
	 * @param canceled <code>true</code> if the user stops the algorithm during the execution otherwise <code>false</code>
	 * @since 1.0
	 */
	public void endExam(final boolean canceled);
	
	/**
	 * Is invoked when the exercise of the current step in the algorithm is loaded and should be presented to the user.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This event is executed in the event dispatch thread (EDT).
	 * 
	 * @param exercise the exercise
	 * @param step the related step or <code>null</code> if there is no related step
	 * @since 1.0
	 */
	public void beforeProcessingExercise(final AlgorithmExercise<?> exercise, final AlgorithmStep step);
	
	/**
	 * Is invoked when the exercise is processed.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This event is executed in the event dispatch thread (EDT).
	 * 
	 * @param exercise the exercise
	 * @param result the final result of the exercise
	 * @param lastSolution the last solution the user has given
	 * @since 1.0
	 */
	public void afterProcessingExercise(final AlgorithmExercise<?> exercise, final ExamResult result, final String lastSolution);
	
	/**
	 * Is invoked every time the user has entered a new solution of the exercise.
	 * <br><br>
	 * If the exercise failed the user has the chance to correct his answer meaning he can repeat the exercise until such time as
	 * he gives up which means that he omits the exercise.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This event is executed in the event dispatch thread (EDT).
	 * 
	 * @param exercise the exercise
	 * @param succeeded <code>true</code> if the user has entered the right solution otherwise <code>false</code>
	 * @param solution the string representation of the solution the user has made
	 * @since 1.0
	 */
	public void afterSolvingExercise(final AlgorithmExercise<?> exercise, final boolean succeeded, final String solution);
	
	/**
	 * Indicates whether the provider is visible to the user (for example in the graphical user interface).
	 * <br><br>
	 * If the provider is a visual component this method should return the visibility state of the component.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If an exercise is processed it must be guaranteed that the provider is visible otherwise the user cannot solve the exercises. If a
	 * provider is not visible he cannot retrieve events about the process of the exercises.<br>
	 * This event is executed in the event dispatch thread (EDT).
	 * 
	 * @return <code>true</code> if the provider is visible to the user otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isVisible();
	
	/**
	 * Sets whether the provider should be visible to the user (for example in the graphical user interface).
	 * <br><br>
	 * If the provider is a visual component this method should set the visibility state of the component.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If an exercise is processed it must be guaranteed that the provider is visible otherwise the user cannot solve the exercises. If a
	 * provider is not visible he cannot retrieve events about the process of the exercises.<br>
	 * This event is executed in the event dispatch thread (EDT).
	 * 
	 * @param visible <code>true</code> if the provider should be visible to the user otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setVisible(final boolean visible);

}
