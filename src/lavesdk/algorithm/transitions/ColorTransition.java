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
 * Class:		ColorTransition
 * Task:		Performs color transitions in an algorithm visualization
 * Created:		15.07.14
 * LastChanges:	15.07.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.transitions;

import java.awt.Color;

import lavesdk.algorithm.AlgorithmRTE;
import lavesdk.algorithm.RTTransition;
import lavesdk.algorithm.plugin.views.GraphView;

/**
 * A class to create a linear color transition between a source color and a destination color.
 * <br><br>
 * Use a color transition as follows (here we change the background of a vertex in a {@link GraphView}):
 * <pre>
 * // get the visual vertex its background should be changed
 * final VisualVertex vv = graph.getVisualVertexByID(...);
 * 
 * // create the color transition and use the actual runtime environment
 * new ColorTransition(this, vv.getBackground(), Color.blue) {
 *     protected void apply(Color o) {
 *         // apply the transition object to the vertex
 *         vv.setBackground(o);
 *         // repaint the graph view
 *         graphView.repaint();
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
public abstract class ColorTransition extends RTTransition<Color> {
	
	/** the source color */
	protected final Color from;
	/** the destination color */
	protected final Color to;
	/** auxiliary variable for the red value */
	private final float rDiv;
	/** auxiliary variable for the green value */
	private final float gDiv;
	/** auxiliary variable for the blue value */
	private final float bDiv;

	/**
	 * Creates a new color transition.
	 * <br><br>
	 * It is used the default duration {@link RTTransition#DEF_DURATION}  and the default step count {@link RTTransition#DEF_STEPS}.
	 * 
	 * @param rte the runtime environment of the related algorithm
	 * @param from the source color
	 * @param to the destination color
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if rte is null</li>
	 * 		<li>if from is null</li>
	 * 		<li>if to is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ColorTransition(final AlgorithmRTE rte, final Color from, final Color to) throws IllegalArgumentException {
		this(rte, DEF_DURATION, DEF_STEPS, from, to);
	}
	
	/**
	 * Creates a new color transition.
	 * 
	 * @param rte the runtime environment of the related algorithm
	 * @param duration the duration of the transition in <b>milliseconds</b>
	 * @param steps the step count of the transition (an animation frame is <code>duration / steps</code> milliseconds long)
	 * @param from the source color
	 * @param to the destination color
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if rte is null</li>
	 * 		<li>if duration is smaller steps</li>
	 * 		<li>if steps is <code>< 2</code></li>
	 * 		<li>if from is null</li>
	 * 		<li>if to is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ColorTransition(final AlgorithmRTE rte, final long duration, final int steps, final Color from, final Color to) throws IllegalArgumentException {
		super(rte, duration, steps);
		
		if(from == null || to == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final float denominator = (float)steps - 1.0f;
		
		this.from = from;
		this.to = to;
		this.rDiv = (to.getRed() - from.getRed()) / denominator;
		this.gDiv = (to.getGreen() - from.getGreen()) / denominator;
		this.bDiv = (to.getBlue() - from.getBlue()) / denominator;
	}

	@Override
	protected final Color runStep(int step) {
		final int i = step - 1;
		
		return new Color((int)(from.getRed() + i * rDiv), (int)(from.getGreen() + i * gDiv), (int)(from.getBlue() + i * bDiv));
	}

}
