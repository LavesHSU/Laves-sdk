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
 * Class:		DefaultVertexRenderer
 * Task:		Default renderer of vertices
 * Created:		14.09.13
 * LastChanges:	12.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import lavesdk.math.graph.Vertex;

/**
 * The default renderer of vertices.
 * <br><br>
 * Vertices are rendered as a circle and the caption is rendered in the center
 * of the vertex.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <T> the type of the vertex to render
 */
public class DefaultVertexRenderer<T extends Vertex> implements VertexRenderer<T> {

	/** the circle */
	protected final Ellipse2D.Float circle;
	/** the background color */
	protected Color background;
	/** the foreground color */
	protected Color foreground;
	/** the x position of the center of the vertex */
	protected int xCenter;
	/** the y position of the center of the vertex */
	protected int yCenter;
	/** the font */
	protected Font font;
	/** the line width of the edge */
	protected int edgeWidth;
	/** the stroke for the vertex edge */
	protected Stroke edgeStroke;
	
	/**
	 * Creates a new default vertex renderer.
	 * 
	 * @since 1.0
	 */
	public DefaultVertexRenderer() {
		circle = new Ellipse2D.Float();
		background = Color.white;
		foreground = Color.black;
		xCenter = 0;
		yCenter = 0;
		font = null;
		edgeWidth = 1;
		edgeStroke = new BasicStroke(edgeWidth);
	}
	
	@Override
	public void setBackground(Color c) {
		background = c;
	}
	
	@Override
	public void setForeground(Color c) {
		foreground = c;
	}

	@Override
	public void draw(Graphics2D g, T o) {
		// draw the background circle of the vertex
		g.setColor(background);
		g.fill(circle);
		
		// draw the border circle of the vertex
		final Stroke oldStroke = g.getStroke();
		g.setColor(foreground);
		g.setStroke(edgeStroke);
		g.draw(circle);
		g.setStroke(oldStroke);
		
		// draw the caption of the vertex
		if(font != null)
			g.setFont(font);
		final FontMetrics fm = g.getFontMetrics();
		g.drawString(o.getCaption(), xCenter - (fm.stringWidth(o.getCaption()) / 2) + 1, yCenter + (fm.getAscent() / 2) + fm.getLeading());
	}

	@Override
	public void setPositionLeftTop(int x, int y) {
		circle.x = x;
		circle.y = y;
	}

	@Override
	public void setPositionCenter(int x, int y) {
		this.xCenter = x;
		this.yCenter = y;
	}

	@Override
	public void setDiameter(int d) {
		circle.width = circle.height = d;
	}

	@Override
	public void setFont(Font f) {
		font = f;
	}

	@Override
	public void setEdgeWidth(int w) {
		if(w != edgeWidth) {
			edgeWidth = w;
			edgeStroke = new BasicStroke(edgeWidth);
		}
	}
	
	@Override
	public void setAttachmentPoint(int x, int y) {
	}

}
