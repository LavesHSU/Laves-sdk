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

import lavesdk.algorithm.transitions.ColorTransition;
import lavesdk.algorithm.transitions.LinearPositionTransition;

/**
 * The base class of a transition to animate sequences in an algorithm visualization.
 * <br><br>
 * Use a transition as follows (where <i>MyTransition</i> is a concrete one) to animate a sequence in an algorithm visualization:
 * <pre>
 * new MyTransition(...) {
 *     protected void apply(...) {
 *         // apply the transition object to a visualization object
 *     }
 * }.run();
 * </pre>
 * <b>Attention</b>:<br>
 * Do not forget to invoke the {@link #run()} method on the transition!
 * 
 * @see ColorTransition
 * @see LinearPositionTransition
 * @author jdornseifer
 * @version 1.0
 * @since 1.2
 */
public abstract class RTTransition<T> {
	
	/** the runtime environment of the related algorithm that is used to animate the transition */
	private final AlgorithmRTE rte;
	/** the duration of the transition */
	protected final long duration;
	/** the number of steps the transition has which is always <code>>= 2</code> */
	protected final int steps;
	/** the duration of an animation frame meaning <code>frame = duration / steps;</code> */
	protected final long frame;
	
	/** a default duration */
	public static final long DEF_DURATION = 300;
	/** a default step size */
	public static final int DEF_STEPS = 10;
	
	/**
	 * Creates a new runtime transition to animation sequences.
	 * <br><br>
	 * It is used a default duration of {@value #DEF_DURATION} milliseconds and a default step count of {@value #DEF_STEPS}.
	 * 
	 * @param rte the runtime environment of the related algorithm
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if rte is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public RTTransition(final AlgorithmRTE rte) throws IllegalArgumentException {
		this(rte, DEF_DURATION, DEF_STEPS);
	}
	
	/**
	 * Creates a new runtime transition to animation sequences.
	 * 
	 * @param rte the runtime environment of the related algorithm
	 * @param duration the duration of the transition in <b>milliseconds</b>
	 * @param steps the step count of the transition (an animation frame is <code>duration / steps</code> milliseconds long)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if rte is null</li>
	 * 		<li>if duration is smaller steps</li>
	 * 		<li>if steps is <code>< 2</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public RTTransition(final AlgorithmRTE rte, final long duration, final int steps) throws IllegalArgumentException {
		if(rte == null || duration < steps || steps < 2)
			throw new IllegalArgumentException("No valid argument!");
		
		this.rte = rte;
		this.duration = duration;
		this.steps = steps;
		this.frame = duration / steps;
	}
	
	/**
	 * Runs the transition.
	 * 
	 * @since 1.0
	 */
	public final void run() {
		beforeRun();
		
		for(int i = 1; i < steps; i++) {
			apply(runStep(i));
			rte.sleep(frame);
		}
		
		afterRun();
	}
	
	/**
	 * Runs a step of the transition.
	 * 
	 * @param step the current step (one-based)
	 * @return the transition object of the current step
	 * @since 1.0
	 */
	protected abstract T runStep(final int step);
	
	/**
	 * Applies the current transition object.
	 * 
	 * @param o the transition object
	 * @since 1.0
	 */
	protected abstract void apply(final T o);
	
	/**
	 * Is invoked before the transition is started.
	 * 
	 * @since 1.0
	 */
	protected void beforeRun() {
	}
	
	/**
	 * Is invoked after the transition is ended.
	 * 
	 * @since 1.0
	 */
	protected void afterRun() {
	}

}
