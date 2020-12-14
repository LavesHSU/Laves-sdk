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

package lavesdk.algorithm.plugin.views.renderers;

import lavesdk.algorithm.plugin.views.GraphView;
import lavesdk.math.graph.Vertex;

/**
 * Interface to render vertices in a {@link GraphView}.
 * <br><br>
 * Build your own renderer by implementing {@link VertexRenderer}.
 * 
 * @see DefaultVertexRenderer
 * @author jdornseifer
 * @since 1.0
 * @since 1.0
 * @param <T> the type of the vertex to render
 */
public interface VertexRenderer<T extends Vertex> extends Renderer<T> {

	/**
	 * Sets the left top position of the vertex.
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @since 1.0
	 */
	public void setPositionLeftTop(final int x, final int y);
	
	/**
	 * Sets the center position of the vertex.
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @since 1.0
	 */
	public void setPositionCenter(final int x, final int y);
	
	/**
	 * Sets the diameter of the vertex.
	 * 
	 * @param d the diameter
	 * @since 1.0
	 */
	public void setDiameter(final int d);
	/**
	 * Sets the line width of the edge of the vertex.
	 * 
	 * @param w the line width
	 * @since 1.0
	 */
	public void setEdgeWidth(final int w);
	
	/**
	 * Sets the attachment point of the vertex.
	 * <br><br>
	 * The attachment point is a point at a vertex circle that is optimal to attach objects (like text etc.) to the vertex without
	 * impair the look and feel meaning the attachment point is the point that lies in the center of the largest arc between two edge
	 * endpoints of the vertex.
	 * 
	 * @param x the x position of the attachment point
	 * @param y the y position of the attachment point
	 * @since 1.0
	 */
	public void setAttachmentPoint(final int x, final int y);

}
