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
 * Class:		LinearPositionTransition
 * Task:		Performs linear position transitions in an algorithm visualization
 * Created:		15.07.14
 * LastChanges:	15.07.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.transitions;

import java.awt.Point;

import lavesdk.algorithm.AlgorithmRTE;
import lavesdk.algorithm.RTTransition;

/**
 * A class to create a linear position transition between a source position and a target position.
 * <br><br>
 * Use a linear position transition as follows (here we change the position of a visual object in a custom view:
 * <pre>
 * // get the visual object its position should be changed
 * final VisualObject vo = customView.getVisualObject(...);
 * 
 * // create the linear position transition and use the actual runtime environment
 * new LinearPositionTransition(this, new Point(vo.getX(), vo.getY()), new Point(100, 100)) {
 *     protected void apply(Point o) {
 *         // apply the transition object to the visual object
 *         vo.setX(o.getX());
 *         vo.setY(o.getY());
 *         // repaint the custom view
 *         customView.repaint();
 *     }
 * }.run();
 * </pre>
 * <b>Attention</b>:<br>
 * Do not forget to invoke the {@link #run()} method on the transition!
 * 
 * @see RTTransition
 * @author jdornseifer
 * @version 1.0
 * @since 1.2
 */
public abstract class LinearPositionTransition extends RTTransition<Point> {
	
	/** the source position */
	protected final Point source;
	/** the target position */
	protected final Point target;
	/** auxiliary variable to calculate the intermediate x position */
	private final float xDiv;
	/** auxiliary variable to calculate the intermediate y position */
	private final float yDiv;

	/**
	 * Creates a new linear position transition.
	 * <br><br>
	 * It is used the default duration {@link RTTransition#DEF_DURATION}  and the default step count {@link RTTransition#DEF_STEPS}.
	 * 
	 * @param rte the runtime environment of the related algorithm
	 * @param source the source position
	 * @param target the target position
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if rte is null</li>
	 * 		<li>if source is null</li>
	 * 		<li>if target is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public LinearPositionTransition(final AlgorithmRTE rte, final Point source, final Point target) throws IllegalArgumentException {
		this(rte, DEF_DURATION, DEF_STEPS, source, target);
	}
	
	/**
	 * Creates a new linear position transition.
	 * 
	 * @param rte the runtime environment of the related algorithm
	 * @param duration the duration of the transition in <b>milliseconds</b>
	 * @param steps the step count of the transition (an animation frame is <code>duration / steps</code> milliseconds long)
	 * @param source the source position
	 * @param target the target position
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if rte is null</li>
	 * 		<li>if duration is smaller steps</li>
	 * 		<li>if steps is <code>< 2</code></li>
	 * 		<li>if source is null</li>
	 * 		<li>if target is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public LinearPositionTransition(final AlgorithmRTE rte, final long duration, final int steps, final Point source, final Point target) throws IllegalArgumentException {
		super(rte, duration, steps);
		
		if(source == null || target == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final float denominator = (float)steps;
		
		this.source = source;
		this.target = target;
		this.xDiv = (target.x - source.x) / denominator;
		this.yDiv = (target.y - source.y) / denominator;
	}

	@Override
	protected Point runStep(int step) {
		return new Point(source.x + (int)(step * xDiv), source.y + (int)(step * yDiv));
	}

}
