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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * The base interface for all renderers.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <T> the type of the object to render
 */
public interface Renderer<T> {
	
	/**
	 * Sets the background color.
	 * 
	 * @param c the background color
	 * @since 1.0
	 */
	public void setBackground(final Color c);
	
	/**
	 * Sets the foreground color.
	 * 
	 * @param c the foreground color
	 * @since 1.0
	 */
	public void setForeground(final Color c);
	
	/**
	 * Sets the font.
	 * 
	 * @param f the font
	 * @since 1.0
	 */
	public void setFont(final Font f);
	
	/**
	 * Renders the object to the graphics context.
	 * 
	 * @param g the graphics context
	 * @param o the object that should be rendered
	 * @since 1.0
	 */
	public void draw(final Graphics2D g, final T o);

}
