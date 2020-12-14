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
 * Class:		DefaultEdgeRenderer
 * Task:		Default renderer of edges
 * Created:		14.09.13
 * LastChanges: 12.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import lavesdk.graphics.CatmullRomSpline;
import lavesdk.math.graph.Edge;

/**
 * The default renderer of edges.
 * <br><br>
 * Edges are rendered as {@link CatmullRomSpline}s and the label ({@link Edge#toString()}) is rendered in a way that it is
 * always visible and not overlaid by the spline.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <T> the type of the edge to render
 */
public class DefaultEdgeRenderer<T extends Edge> implements EdgeRenderer<T> {

	/** flag that indicates whether the label of the edge should be displayed */
	protected boolean paintLabels;
	/** the spline */
	protected CatmullRomSpline spline;
	/** the foreground color */
	protected Color foreground;
	/** the font */
	protected Font font;
	/** the x position of the first point */
	protected int x1;
	/** the y position of the first point */
	protected int y1;
	/** the x position of the second point */
	protected int x2;
	/** the y position of the second point */
	protected int y2;
	/** the x position of the edge label */
	protected int labelX;
	/** the y position of the edge label */
	protected int labelY;
	/** a flag that indicates if the arrow should be drawn */
	protected boolean drawArrow;
	/** the line width */
	protected int lineWidth;
	/** the stroke for the edge */
	protected Stroke lineStroke;
	/** the length of the arrow */
	protected int arrowLength;
	
	/**
	 * Creates a new default edge renderer.
	 * 
	 * @since 1.0
	 */
	public DefaultEdgeRenderer() {
		this(true);
	}
	
	/**
	 * Creates a new default edge renderer.
	 * 
	 * @param paintLabels <code>true</code> if the labels of the edges should be display otherwise <code>false</code>
	 * @since 1.0
	 */
	public DefaultEdgeRenderer(final boolean paintLabels) {
		this.paintLabels = paintLabels;
		this.spline = null;
		this.foreground = Color.black;
		this.font = null;
		this.x1 = 0;
		this.y1 = 0;
		this.x2 = 0;
		this.y2 = 0;
		this.labelX = 0;
		this.labelY = 0;
		this.drawArrow = false;
		this.lineWidth = 1;
		this.lineStroke = new BasicStroke(lineWidth);
		this.arrowLength = 5;
	}
	
	@Override
	public void setBackground(Color c) {
	}
	
	@Override
	public void setForeground(Color c) {
		foreground = c;
	}

	@Override
	public void setFont(Font f) {
		font = f;
	}

	@Override
	public void draw(Graphics2D g, T o) {
		g.setColor(foreground);
		
		final Stroke oldStroke = g.getStroke();
		g.setStroke(lineStroke);
		
		// draw the spline
		g.draw(spline.getPath());
		
		// draw an arrow only if necessary
		if(drawArrow) {
			// we adjust the arrow to the line that goes through the last 2 points of the spline
			final Point2D.Float p1 = spline.getPoints()[spline.getPoints().length / 2 - 1];
			final Point2D.Float p2 = spline.getPoints()[spline.getPoints().length - 1];
			
			// calculate the vector
			final float dX = p2.x - p1.x;
			final float dY = p2.y - p1.y;
			final double l = Math.sqrt(dX*dX + dY*dY);
			final int x = (int)((dX / l) * arrowLength);
			final int y = (int)((dY / l) * arrowLength);
			
			// draw both arrow lines
			g.drawLine(x2, y2, x2 - x - y, y2 - y + x);
			g.drawLine(x2, y2, x2 - x + y, y2 - y - x);
		}
		
		if(paintLabels) {
			// draw the edge label
			if(font != null)
				g.setFont(font);
			final FontMetrics fm = g.getFontMetrics();
			// if vertex 1 is below vertex 2 then the edge is concave that means the label must aligned to the left (x-axis)
			// if vertex 1 is left from vertex 2 then the edge is convex that means the label must be aligned to the bottom (y-axis)
			g.drawString(o.toString(), (y1 < y2) ? labelX - fm.stringWidth(o.toString()) : labelX + 2, (x1 < x2) ? labelY + fm.getAscent() + fm.getLeading() : labelY - 2);
		}
		
		// reset old line stroke
		g.setStroke(oldStroke);
	}

	@Override
	public void setFirstPosition(int x1, int y1) {
		this.x1 = x1;
		this.y1 = y1;
	}

	@Override
	public void setSecondPosition(int x2, int y2) {
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	public void setControlPosition(int x, int y) {
	}

	@Override
	public void setDrawArrow(boolean drawArrow) {
		this.drawArrow = drawArrow;
	}

	@Override
	public void setLineWidth(int width) {
		if(width != lineWidth) {
			lineWidth = width;
			lineStroke = new BasicStroke(lineWidth);
		}
	}

	@Override
	public void setSpline(CatmullRomSpline s) {
		spline = s;
	}

	@Override
	public void setArrowLength(int length) {
		arrowLength = length;
	}
	
	@Override
	public void setLabelPosition(int x, int y) {
		labelX = x;
		labelY = y;
	}

}
