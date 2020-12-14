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
 * Interface:	EdgeRenderer
 * Task:		Render edges
 * Created:		14.09.13
 * LastChanges:	09.12.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views.renderers;

import lavesdk.algorithm.plugin.views.GraphView;
import lavesdk.graphics.CatmullRomSpline;
import lavesdk.math.graph.Edge;

/**
 * Interface to render edges in a {@link GraphView}.
 * <br><br>
 * Build your own renderer by implementing {@link EdgeRenderer}.
 * 
 * @see DefaultEdgeRenderer
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <T> the type of the edge to render
 */
public interface EdgeRenderer<T extends Edge> extends Renderer<T> {
	
	/**
	 * Sets the position of the first point of the edge.
	 * 
	 * @param x1 the x position
	 * @param y1 the y position
	 * @since 1.0
	 */
	public void setFirstPosition(final int x1, final int y1);
	
	/**
	 * Sets the position of the second point of the edge.
	 * 
	 * @param x2 the x position
	 * @param y2 the y position
	 * @since 1.0
	 */
	public void setSecondPosition(final int x2, final int y2);
	
	/**
	 * Sets the position of the control point of the edge.
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @since 1.0
	 */
	public void setControlPosition(final int x, final int y);
	
	/**
	 * Sets a flag that indicates if the arrow should be drawn are not.
	 * 
	 * @param drawArrow <code>true</code> if the arrow of the edge should be drawn otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setDrawArrow(final boolean drawArrow);
	
	/**
	 * Sets the line width of the edge.
	 * 
	 * @param width the line width
	 * @since 1.0
	 */
	public void setLineWidth(final int width);
	
	/**
	 * Sets the spline that represents the edge visually.
	 * 
	 * @see CatmullRomSpline
	 * @param s the spline
	 */
	public void setSpline(final CatmullRomSpline s);
	
	/**
	 * Sets the length of the arrow peak.
	 * 
	 * @param length the length
	 * @since 1.0
	 */
	public void setArrowLength(final int length);
	
	/**
	 * Sets the position of the edge label.
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @since 1.0
	 */
	public void setLabelPosition(final int x, final int y);

}
