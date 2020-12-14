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
 * Class:		AlgorithmExercise
 * Task:		Exercise of an algorithm step
 * Created:		27.02.14
 * LastChanges:	09.10.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm;

import lavesdk.algorithm.exceptions.IllegalInvocationException;
import lavesdk.algorithm.plugin.views.View;
import lavesdk.algorithm.text.AlgorithmStep;
import lavesdk.algorithm.text.Annotation;
import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.dialogs.SolveExercisePane;
import lavesdk.language.LanguageFile;

/**
 * Represents an abstract exercise that could be linked with an {@link AlgorithmStep}.
 * <br><br>
 * Each exercise has a text which is a description of what should be done to solve the exercise (for example a question). Furthermore all concrete
 * exercises have to implement the {@link #requestSolution()} method and the {@link #examine(Object[], AlgorithmState)} method.<br>
 * Hint: you can override {@link #beforeRequestSolution(AlgorithmState)} and {@link #afterRequestSolution(boolean)} to be notified before and after a solution
 * is/was requested.
 * <br><br>
 * <b>Applying a solution to the algorithm</b>:<br>
 * Override {@link #getApplySolutionToAlgorithm()} and {@link #applySolutionToAlgorithm(AlgorithmState, Object[])} to apply a correct solution
 * the user has given to the algorithm.<br>
 * This is useful when a step can be executed in several ways so applying the solution to the algorithm lets the algorithm continue with the
 * way the user has chosen.<br>
 * By default this mechanism is turned off.<br>
 * <u>Example</u>: The user has to select a path in a graph from a specific vertex to another one which means in general that their
 * are more than one correct paths. The algorithm can choose any path too, that is the path must not be equal to the user's choice.
 * Applying the solution of the user to the algorithm let the algorithm take the same path as the user has specified and it ensures
 * a synchronous execution.
 * <br><br>
 * <b>Custom input hint</b>:<br>
 * By default a general input hint is shown if the {@link #requestSolution()} method returns <code>null</code> and the exercise has related views.
 * The message explains that the user has to solve the exercise in the corresponding view(s) and afterwards click on the solve-button.<br>
 * Override {@link #hasInputHint()} and {@link #getInputHintMessage(LanguageFile, String)} to create a custom input hint.
 * <br><br>
 * <b>Last failed hints</b>:<br>
 * Override {@link #getLastFailedHintMessage()} to return descriptions that inform the user about why the exercise is failed currently.
 * <br><br>
 * <b>Tip</b>:<br>
 * Use {@link SolveExercisePane#showDialog(lavesdk.algorithm.plugin.PluginHost, AlgorithmExercise, lavesdk.gui.dialogs.SolveExerciseDialog.SolutionEntry[], lavesdk.language.LanguageFile, String, String)}
 * to display a customizable input dialog where the user can enter solutions for an exercise.
 * 
 * @author jdornseifer
 * @version 1.2
 * @since 1.0
 * @param <T> the type of the result(s)
 */
public abstract class AlgorithmExercise<T> {
	
	/** the text of the exercise */
	private final String text;
	/** the credits of the exercise */
	private final float credits;
	/** the related views of the exercise */
	private final View[] views;
	/** the current solutions of the exercise of <code>null</code> if the exercise is exited or given up */
	private T[] currSolutions;
	/** the current exercise controller */
	private AlgorithmExerciseController controller;
	/** flag that indicates whether the process of the exercise is enabled */
	private boolean processing;
	/** flag that indicates whether the exercise is currently omitted */
	private boolean givenUp;
	/** the last solution the user has entered */
	private String lastSolution;
	
	/**
	 * Creates a new exercise.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The exercise does not have credits. Use {@link #AlgorithmExercise(String, float)} to specify credits the user can reach if
	 * the exercise is succeeded.
	 * 
	 * @param text the text of the exercise (<b>use html tags to format the text</b>)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if text is null</li>
	 * 		<li>if text is empty</li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmExercise(final String text) throws IllegalArgumentException {
		this(text, 0.0f);
	}
	
	/**
	 * Creates a new exercise.
	 * 
	 * @param text the text of the exercise (<b>use html tags to format the text</b>)
	 * @param credits the credits of the exercise the user can reach or <code>0.0f</code> if the exercise does not have credits
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if text is null</li>
	 * 		<li>if text is empty</li>
	 * 		<li>if credits is <code>< 0.0f</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmExercise(final String text, final float credits) throws IllegalArgumentException {
		this(text, credits, (View)null);
	}
	
	/**
	 * Creates a new exercise.
	 * 
	 * @param text the text of the exercise (<b>use html tags to format the text</b>)
	 * @param credits the credits of the exercise the user can reach or <code>0.0f</code> if the exercise does not have credits
	 * @param view the view in which the exercise has to be solved/the solution has to be entered or <code>null</code> if there is no related view (a valid view will be displayed (if the view is invisible) and the view will be highlighted with an enclosing border to show the user where to enter the solution)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if text is null</li>
	 * 		<li>if text is empty</li>
	 * 		<li>if credits is <code>< 0.0f</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmExercise(final String text, final float credits, final View view) throws IllegalArgumentException {
		this(text, credits, (view != null) ? new View[] { view } : null);
	}
	
	/**
	 * Creates a new exercise.
	 * 
	 * @param text the text of the exercise (<b>use html tags to format the text</b>)
	 * @param credits the credits of the exercise the user can reach or <code>0.0f</code> if the exercise does not have credits
	 * @param views the views in which the exercise has to be solved/the solution has to be entered or <code>null</code> if there are no related views (<b>valid views will be displayed (if a view is invisible) and the views will be highlighted with an enclosing border to show the user where to enter the solution</b>)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if text is null</li>
	 * 		<li>if text is empty</li>
	 * 		<li>if credits is <code>< 0.0f</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmExercise(final String text, final float credits, final View[] views) throws IllegalArgumentException {
		if(text == null || text.isEmpty() || credits < 0.0f)
			throw new IllegalArgumentException("No valid argument!");
		
		this.text = text;
		this.credits = credits;
		this.views = views;
		this.currSolutions = null;
		this.controller = null;
		this.processing = false;
		this.givenUp = false;
	}
	
	/**
	 * Gets the text of the exercise.
	 * 
	 * @return the text (<b>can contain html tags to format the text</b>)
	 * @since 1.0
	 */
	public final String getText() {
		return text;
	}
	
	/**
	 * The credits of the exercise.
	 * 
	 * @return the credits of the exercise the user can reach or <code>0.0f</code> if the exercise does not have credits
	 * @since 1.0
	 */
	public final float getCredits() {
		return credits;
	}
	
	/**
	 * Solves the exercise meaning that the solution of the exercise is requested from the user by using {@link #requestSolution()}.
	 * After that the controller of the exercise is notified to continue with working if the request is no canceled.
	 * 
	 * @return <code>true</code> if the user entered a solution or <code>false</code> if the user has canceled the request
	 * @since 1.0
	 */
	public final boolean solve() {
		final T[] solutions = requestSolution();
		boolean notifyController = false;
		
		// no results entered?
		if(solutions == null)
			return false;
		
		synchronized(this) {
			if(!givenUp) {
				currSolutions = solutions;
				notifyController = true;
			}
		}
		
		if(notifyController)
			controller.wakeUp();
		
		return true;
	}
	
	/**
	 * Gives up the exercise which indicates that the user cannot solve the exercise. The controller of the exercise
	 * is notified to continue with working.
	 * 
	 * @since 1.0
	 */
	public final void giveUp() {
		boolean notifyController = false;
		
		synchronized(this) {
			if(!givenUp) {
				currSolutions = null;
				givenUp = true;
				notifyController = true;
			}
		}
		
		if(notifyController)
			controller.wakeUp();
	}
	
	/**
	 * Gets the input hint message that is displayed to the user if {@link #requestSolution()} returns <code>null</code>.
	 * <br><br>
	 * By default this hint explains the user in a general way, what to to in a related view. Therefore the language file should contain the
	 * following labels:
	 * <ul>
	 * 		<li><i>EXERCISE_INPUTHINT_DEFMSG</i>: the default message of the input hint of an exercise</li>
	 * 		<li><i>EXERCISE_INPUTHINT_TITLE</i>: the title of the input hint message of an exercise</li>
	 * </ul>
	 * <br>
	 * <b>Notice</b>:<br>
	 * Override this method to display a custom message as input hint.
	 * 
	 * @see #hasInputHint()
	 * @see Annotation
	 * @param langFile the language file of the <b>host system</b>
	 * @param langID the language id that is selected in the host system
	 * @return the input hint message as an annotation
	 * @since 1.1
	 */
	public Annotation getInputHintMessage(final LanguageFile langFile, final String langID) {
		final String msg = LanguageFile.getLabel(langFile, "EXERCISE_INPUTHINT_DEFMSG", langID, "Enter your solution of the task in the highlighted view(s) \"&views&\".<br>Click on the \"Solve Exercise\"-button only after you have made the input.");
		final StringBuilder viewsString = new StringBuilder();
		if(views != null) {
			boolean delimiter = false;
			for(View v : views) {
				if(v == null)
					continue;
				
				if(delimiter)
					viewsString.append(", ");
				viewsString.append(v.getTitle());
				delimiter = true;
			}
		}
		
		return new Annotation(msg.replaceAll("&views&", viewsString.toString()));
	}
	
	/**
	 * Indicates whether the input hint should be displayed if {@link #requestSolution()} returns <code>null</code>.
	 * <br><br>
	 * By default the hint is shown if the exercise has to be solved in a related view instead of a dialog
	 * (see {@link #AlgorithmExercise(String, float, View[])}).
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Override this method to define a custom condition when to show an input hint.
	 * 
	 * @see #getInputHintMessage(LanguageFile, String)
	 * @return <code>true</code> if an input hint should be shown otherwise <code>false</code>
	 * @since 1.1
	 */
	public boolean hasInputHint() {
		return views != null && views.length > 0;
	}
	
	/**
	 * Gets the description of the last failure of the exercise.
	 * <br><br>
	 * In this description the user should be informed why the exercise is failed.
	 * <br><br>
	 * <b>Example</b>:<br>
	 * During the examination of the exercise there might be occur some failures. To inform the user about what he made wrong
	 * you should separate the individual failures in the examination. The hint message should be stored in a private variable:
	 * <pre>
	 * private String lastFailHintMsg = "";
	 * 
	 * //...
	 * 
	 * public Annotation getLastFailHintMessage() {
	 *     return lastFailHintMsg.isEmpty() ? null : new Annotation(lastFailHintMsg);
	 * }
	 * 
	 * //...
	 * 
	 * protected boolean examine(...[] results, AlgorithmState state) {
	 *     // some initializations...
	 *     
	 *     // clear the last message
	 *     lastFailHintMsg = "";
	 *     
	 *     // separate the individual failures where Failure1 might be results[0] == null or something like this
	 *     if(Failure1)
	 *         lastFailHintMsg = "You have selected the wrong ...";
	 *     else if(Failure2)
	 *         lastFailHintMsg = "You have entered the wrong ...";
	 *     // else if ...
	 *     
	 *     return ...;
	 * }
	 * </pre>
	 * 
	 * @return the message as an annotation or <code>null</code> if there is no description available
	 * @since 1.2
	 */
	public Annotation getLastFailedHintMessage() {
		return null;
	}
	
	/**
	 * Indicates whether the exercise has a description of the last failure.
	 * 
	 * @return <code>true</code> if the exercise has a hint (this is the case if the exercise is not given up currently and {@link #getLastFailedHintMessage()} is not <code>null</code>) otherwise <code>false</code>
	 * @since 1.2
	 */
	public final boolean hasLastFailedHint() {
		return !givenUp && getLastFailedHintMessage() != null;
	}
	
	/**
	 * Gets the current solution of the exercise as a string representation.
	 * 
	 * @see #getResultAsString(Object, int)
	 * @return the solution of the exercise as a string
	 * @since 1.0
	 */
	protected final String getSolutionAsString() {
		final StringBuilder solutionString = new StringBuilder();
		
		synchronized(this) {
			if(currSolutions == null)
				solutionString.append("");
			else {
				for(int i = 0; i < currSolutions.length; i++) {
					if(i > 0)
						solutionString.append(", ");
					solutionString.append(getResultAsString(currSolutions[i], i));
				}
			}
		}
		
		return solutionString.toString();
	}
	
	/**
	 * Gets the specified result as a string representation.
	 * <br><br>
	 * If the result is <code>null</code> the string representation is <code>"-"</code> otherwise it is
	 * <code>result.toString()</code>.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Override this method if you want to build a custom string representation of the results.
	 * 
	 * @param result the result or <code>null</code>
	 * @param index the index of the result in the array of results returned by {@link #requestSolution()}
	 * @return the string representation of the result
	 * @since 1.0
	 */
	protected String getResultAsString(final T result, final int index) {
		if(result == null)
			return "";
		else
			return result.toString();
	}
	
	/**
	 * Requests the results of the exercise meaning that the user should input the value or values of the solution.
	 * <br><br>
	 * <b>Example</b>:<br>
	 * The task is that the user selects the correct vertex of a graph that has certain properties. The task could be "Select the vertex that
	 * has the smallest distance to vertex u.".<br>
	 * So {@link #requestSolution()} should return the selected vertex of the graph or <code>null</code> if the user does not have selected a
	 * vertex:
	 * <pre>
	 * ...
	 * public VisualVertex[] requestSolution() {
	 *    if(graphView.getSelectedVertexCount() != 1)
	 *        return null;
	 *    else
	 *        return new VisualVertex[] { graphView.getSelectedVertex(0) };
	 * }
	 * ...
	 * </pre>
	 * 
	 * @return the value(s) of the solution as the result(s) of the exercise or <code>null</code> if the user canceled the request
	 * @since 1.0
	 */
	protected abstract T[] requestSolution();
	
	/**
	 * Examines the results the user has entered as the solution of the exercise.
	 * 
	 * @see #doAutoExamine(AlgorithmState, String[], Object[])
	 * @param results the results requested by {@link #requestSolution()}
	 * @param state the current state of the algorithm
	 * @return <code>true</code> if the solution is correct otherwise <code>false</code>
	 * @since 1.0
	 */
	protected abstract boolean examine(final T[] results, final AlgorithmState state);
	
	/**
	 * Does an automatic examination of the results a user has entered as the solution of the exercise.
	 * <br><br>
	 * <b>Example</b>:<br>
	 * The user must enter two sets <code>A</code> and <code>B</code> to solve the exercise. To examine the solution automatically it must be
	 * assumed that these fields (described by the keys) are also available in the algorithm state.<br>
	 * That means the results are compared with the values of the state at the given keys.
	 * <pre>
	 * ...
	 * protected boolean examine(T[] results, AlgorithmState state) {
	 *     return doAutoExamine(state, new String[] { "A", "B" }, results);
	 * }
	 * ...
	 * </pre>
	 * 
	 * @param state the current state of the algorithm
	 * @param keys the keys of the result fields in the state (should fit to the result fields)
	 * @param results the results requested by {@link #requestSolution()}
	 * @return <code>true</code> if the solution is correct otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if state is null</li>
	 * 		<li>if keys is null</li>
	 * 		<li>if results is null</li>
	 * 		<li>if <code>results.length != keys.length</code></li>
	 * </ul>
	 * @since 1.0
	 */
	protected final boolean doAutoExamine(final AlgorithmState state, final String[] keys, final T[] results) throws IllegalArgumentException {
		if(state == null || keys == null || results == null || results.length != keys.length)
			throw new IllegalArgumentException("No valid argument!");
		
		boolean succeeded = false;
		
		// check whether all results are correct otherwise the exercise failed
		for(int i = 0; i < results.length; i++) {
			succeeded = results[i] != null && results[i].equals(state.getObject(keys[i]));
			if(!succeeded)
				break;
		}
		
		return succeeded;
	}
	
	/**
	 * This method is invoked before the user enters a solution.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Override and use this method to prepare the solution if necessary.<br>
	 * <b>This method is executed in the event dispatch thread (EDT)!</b>
	 * 
	 * @param state the state of the algorithm (can be used to present current data to the user)
	 * @see #afterRequestSolution(boolean)
	 * @since 1.0
	 */
	protected void beforeRequestSolution(final AlgorithmState state) {
	}
	
	/**
	 * This method is invoked after the user has entered a solution.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Override and use this method to remove objects you added for preparation.<br>
	 * <b>This method is executed in the event dispatch thread (EDT)!</b>
	 * 
	 * @param omitted <code>true</code> if the exercise is omitted meaning the user has given up to solve the exercise otherwise <code>false</code>
	 * @see #beforeRequestSolution(AlgorithmState)
	 * @since 1.0
	 */
	protected void afterRequestSolution(final boolean omitted) {
	}
	
	/**
	 * Indicates whether the solution of the exercise should be applied to the algorithm before the related step is executed.
	 * <br><br>
	 * This is useful when a step can be executed in several ways so applying the solution to the algorithm lets the
	 * algorithm continue with the way the user has chosen.<br>
	 * By default this mechanism is turned off.
	 * <br><br>
	 * <u>Example</u>: The user has to select a path in a graph from a specific vertex to another one which means in general that their
	 * are more than one correct paths. The algorithm can choose any path too, that is the path must not be equal to the user's choice.
	 * Applying the solution of the user to the algorithm let the algorithm take the same path as the user has specified and it ensures
	 * a synchronous execution.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * <b>Applying solutions to the algorithm assumes that the exercise does not need to execute the related step for examination.</b><br>
	 * If you override this method you have to override {@link #applySolutionToAlgorithm(AlgorithmState, Object[])} too.
	 * 
	 * @return <code>true</code> if the solution should be applied to the algorithm otherwise <code>false</code>
	 * @since 1.0
	 */
	protected boolean getApplySolutionToAlgorithm() {
		return false;
	}
	
	/**
	 * Applies the specified solution to the given {@link AlgorithmState}.
	 * <br><br>
	 * Before a solution is applied to the algorithm it is validated using {@link #examine(Object[], AlgorithmState)} that is, only correct solutions
	 * can be applied to the algorithm.
	 * <br><br>
	 * Use the <i>add</i> methods to map the solution(s) onto the associated key(s) you use to adopt solutions in the algorithm
	 * (with {@link AlgorithmRTE#adoptState(int, AlgorithmState)}).<br>
	 * <br><br>
	 * If you override {@link #getApplySolutionToAlgorithm()} and return <code>true</code> you have to override this method too.
	 * 
	 * @param state the state
	 * @param solutions the solutions of the user by {@link #requestSolution()}
	 * @since 1.0
	 */
	protected void applySolutionToAlgorithm(final AlgorithmState state, final T[] solutions) {
	}
	
	/**
	 * This method is invoked each time the exercise is entered.
	 * 
	 * @since 1.0
	 */
	protected void entered() {
	}
	
	/**
	 * This method is invoked each time the exercise is exited.
	 * 
	 * @since 1.0
	 */
	protected void exited() {
	}
	
	/**
	 * Enters the process of the exercise.
	 * <br><br>
	 * <b>The {@link AlgorithmExerciseProvider#beforeProcessingExercise(AlgorithmExercise)} message is automatically emitted using the required
	 * provider of the specified controller!</b><br>
	 * Remember that the message is emitted in the event dispatch thread (EDT).
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This must be done before the user can start solving the exercise.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @see #exit(ExamResult)
	 * @see #examine(AlgorithmState)
	 * @param controller the communication controller of the runtime environment
	 * @param step the related step or <code>null</code> if there is no related step
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if controller is null</li>
	 * </ul>
	 * @since 1.0
	 */
	void enter(final AlgorithmExerciseController controller, final AlgorithmStep step) throws IllegalArgumentException {
		if(controller == null)
			throw new IllegalArgumentException("No valid argument!");
		
		// if the exercise is currently in process then we have to exit because nothing should be done here
		if(processing)
			return;
		
		synchronized(this) {
			this.controller = controller;
			this.processing = true;
			this.givenUp = false;
			this.lastSolution = "";
		}
		
		// display and highlight the related views if necessary
		if(views != null) {
			for(View view : views) {
				if(view != null) {
					// if the view is closed then display the view that the user can solve the exercise
					view.setVisible(true);
					view.highlight(true);
				}
			}
		}
		
		entered();
		
		// notify the provider (important: this has to be shifted into the EDT to avoid thread interference because this method
		// is invoked by a runtime thread of an algorithm)
		final AlgorithmExerciseProvider ep = controller.getExerciseProvider();
		if(ep != null)
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".enter") {
				@Override
				protected void execute() throws Throwable {
					if(ep.isVisible())
						ep.beforeProcessingExercise(AlgorithmExercise.this, step);
				}
			});
	}
	
	/**
	 * Exits the process of the exercise.
	 * <br><br>
	 * <b>The {@link AlgorithmExerciseProvider#afterProcessingExercise(AlgorithmExercise, ExamResult, String)} message is automatically emitted
	 * using the required provider of the specified controller in {@link #enter(AlgorithmExerciseController)}!</b><br>
	 * Remember that the message is emitted in the event dispatch thread (EDT).
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param result the final result of the exercise
	 * @since 1.0
	 */
	void exit(final ExamResult result) {
		// if the exercise was not processed then we have to exit because nothing should be done here
		if(!processing)
			return;
		
		// disable the highlight of the related view if necessary
		if(views != null) {
			for(View view : views) {
				if(view != null)
					view.highlight(false);
			}
		}
		
		// store the last solution because after notifying the provider the solution is cleared
		final String lastSolution = this.lastSolution;
		
		// notify the provider (important: this has to be shifted into the EDT to avoid thread interference because this method
		// is invoked by a runtime thread of an algorithm)
		final AlgorithmExerciseProvider ep = controller.getExerciseProvider();
		if(ep != null)
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".exit") {
				@Override
				protected void execute() throws Throwable {
					if(ep.isVisible())
						ep.afterProcessingExercise(AlgorithmExercise.this, result, lastSolution);
				}
			});
		
		// after that reset the process data
		synchronized(this) {
			currSolutions = null;
			controller = null;
			processing = false;
			givenUp = false;
			this.lastSolution = null;
		}
		
		exited();
	}
	
	/**
	 * Examines the results of the current solution.
	 * <br><br>
	 * <b>The {@link AlgorithmExerciseProvider#afterSolvingExercise(AlgorithmExercise, boolean, String)} message is automatically emitted
	 * using the required provider of the specified controller in {@link #enter(AlgorithmExerciseController)}!</b><br>
	 * Remember that the message is emitted in the event dispatch thread (EDT).
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method should only be used during the process of the exercise meaning in the execution scope defined by {@link #enter(AlgorithmExerciseController)}
	 * and {@link #exit(ExamResult)}.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @see #isOmitted()
	 * @param state the current algorithm state that could be used to examine the exercise results
	 * @return the result of the examination
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if state is null</li>
	 * </ul>
	 * @throws IllegalInvocationException
	 * <ul>
	 * 		<li>if exercise was not entered before</li>
	 * 		<li>if given state was not frozen before</li>
	 * </ul>
	 * @since 1.0
	 */
	ExamResult examine(final AlgorithmState state) throws IllegalArgumentException, IllegalInvocationException {
		if(state == null)
			throw new IllegalArgumentException("No valid argument!");
		
		// exercise was not entered?
		if(!processing)
			throw new IllegalInvocationException("exercise was not entered before");
		
		// exercise was not processed?
		if(givenUp)
			return ExamResult.FAILED;
		
		// unfreeze the state to get the real object values
		state.unfreeze();
		
		// check the results of the current processing
		final boolean succeeded = examine(currSolutions, state);
		lastSolution = getSolutionAsString();
		
		// notify the provider (important: this has to be shifted into the EDT to avoid thread interference because this method
		// is invoked by a runtime thread of an algorithm)
		final AlgorithmExerciseProvider ep = controller.getExerciseProvider();
		if(ep != null)
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".examine") {
				@Override
				protected void execute() throws Throwable {
					if(ep.isVisible())
						ep.afterSolvingExercise(AlgorithmExercise.this, succeeded, AlgorithmExercise.this.lastSolution);
				}
			});
		
		// examine the results of the exercise
		return succeeded ? ExamResult.SUCCEEDED : ExamResult.FAILED;
	}
	
	/**
	 * Transfers the solution of the exercise into the specified {@link AlgorithmState}.
	 * <br><br>
	 * This is only possible if previously a solution was requested. You should only transfer the solution if the exercise succeeded.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param state the state
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if state is null</li>
	 * </ul>
	 * @since 1.0
	 */
	void transferSolution(final AlgorithmState state) throws IllegalArgumentException {
		if(state == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(currSolutions != null)
			applySolutionToAlgorithm(state, currSolutions);
	}
	
	/**
	 * Indicates whether the exercise is given up in the current process meaning that the user omits the exercise.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method should only be used during the process of the exercise meaning in the execution scope defined by {@link #enter(AlgorithmExerciseController)}
	 * and {@link #exit(ExamResult)}.<br>
	 * If the exercise is omitted it should be proceeded with the next task.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return <code>true</code> if the exercise is omitted otherwise <code>false</code>
	 * @since 1.0
	 */
	boolean isOmitted() {
		return givenUp;
	}
	
	/**
	 * Indicates whether the exercise is currently in process or in other words the exercise is entered but not exited yet.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return <code>true</code> if the exercise is in process otherwise <code>false</code>
	 * @since 1.0
	 */
	boolean isInProcess() {
		return processing;
	}
	
	/**
	 * The result of an examination.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public enum ExamResult {
		
		/** the exercise is succeeded */
		SUCCEEDED,
		
		/** the exercise is failed */
		FAILED
		
	}

}
