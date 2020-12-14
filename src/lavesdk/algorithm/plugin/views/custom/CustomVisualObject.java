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

package lavesdk.algorithm.plugin.views.custom;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import lavesdk.algorithm.plugin.views.GraphView;

/**
 * Represents a custom visual object that can be painted in a {@link GraphView}.
 * <br><br>
 * Create your custom objects by inherit from this class.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public abstract class CustomVisualObject {
	
	/** the x position of the object */
	protected int x;
	/** the y position of the object */
	protected int y;
	/** the width of the object */
	protected int width;
	/** the height of the object */
	protected int height;
	/** the background color of the object */
	protected Color background;
	/** the foreground color of the object */
	protected Color foreground;
	
	/**
	 * Creates a new custom visual object at the position <code>(0,0)</code> and with a width and height of <code>0</code>.
	 * 
	 * @since 1.0
	 */
	public CustomVisualObject() {
		this(0, 0, 0, 0, Color.white, Color.black);
	}
	
	/**
	 * Creates a new custom visual object.
	 * 
	 * @param x the x position of the object
	 * @param y the y position of the object
	 * @param width the width of the object
	 * @param height the height of the object
	 * @param background the background of the object
	 * @param foreground the foreground of the object
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if background is null</li>
	 * 		<li>if foreground is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public CustomVisualObject(final int x, final int y, final int width, final int height, final Color background, final Color foreground) throws IllegalArgumentException {
		if(background == null || foreground == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.background = background;
		this.foreground = foreground;
	}
	
	/**
	 * Gets the x position of the object.
	 * 
	 * @return the x position of the object
	 * @since 1.0
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Sets the x position of the object.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The x position of each custom object is adjusted using this method if the graph view is zoomed. Therefore it is recommended
	 * to store the x position and return it in {@link #getX()}.
	 * 
	 * @param x the x position of the object
	 * @since 1.0
	 */
	public void setX(final int x) {
		this.x = x;
	}
	
	/**
	 * Gets the y position of the object.
	 * 
	 * @return the y position of the object
	 * @since 1.0
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Sets the y position of the object.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The y position of each custom object is adjusted using this method if the graph view is zoomed. Therefore it is recommended
	 * to store the y position and return it in {@link #getY()}.
	 * 
	 * @param y the y position of the object
	 * @since 1.0
	 */
	public void setY(final int y) {
		this.y = y;
	}
	
	/**
	 * Gets the width of the object.
	 * 
	 * @return the width of the object
	 * @since 1.0
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Sets the width of the object.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The width of each custom object is adjusted using this method if the graph view is zoomed. Therefore it is recommended
	 * to store the width and return it in {@link #getWidth()}.
	 * 
	 * @param width the width of the object
	 * @since 1.0
	 */
	public void setWidth(final int width) {
		this.width = width;
	}
	
	/**
	 * Gets the height of the object.
	 * 
	 * @return the height of the object
	 * @since 1.0
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Sets the height of the object.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The height of each custom object is adjusted using this method if the graph view is zoomed. Therefore it is recommended
	 * to store the height and return it in {@link #getHeight()}.
	 * 
	 * @param height the height of the object
	 * @since 1.0
	 */
	public void setHeight(final int height) {
		this.height = height;
	}
	
	/**
	 * Gets the background color of the object.
	 * 
	 * @return the background color of the object
	 * @since 1.0
	 */
	public Color getBackground() {
		return background;
	}
	
	/**
	 * Sets the background color of the object.
	 * 
	 * @param c the background color of the object
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if c is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setBackground(final Color c) throws IllegalArgumentException {
		if(c == null)
			throw new IllegalArgumentException("No valid argument!");
		
		background = c;
	}
	
	/**
	 * Gets the foreground color of the object.
	 * 
	 * @return the foreground color of the object
	 * @since 1.0
	 */
	public Color getForeground() {
		return foreground;
	}
	
	/**
	 * Sets the foreground color of the object.
	 * 
	 * @param c the foreground color of the object
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if c is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setForeground(final Color c) throws IllegalArgumentException {
		if(c == null)
			throw new IllegalArgumentException("No valid argument!");
		
		background = c;
	}
	
	/**
	 * Draws the object.
	 * 
	 * @param g the graphics context
	 * @param f the font of the graph view that is used to paint text
	 * @since 1.0
	 */
	public abstract void draw(final Graphics2D g, final Font f);
	
}
