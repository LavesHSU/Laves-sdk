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
 * Class:		CustomVisualText
 * Task:		Paint a custom string
 * Created:		11.05.14
 * LastChanges:	11.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views.custom;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import lavesdk.algorithm.plugin.views.GraphView;

/**
 * Represents a text that can be displayed in a {@link GraphView}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class CustomVisualText extends CustomVisualObject {
	
	/** the text to paint */
	private String text;
	
	/**
	 * Creates a new custom visual text.
	 * 
	 * @param text the text
	 * @param x the x position of the string in the graph
	 * @param y the y position of the string in the graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if text is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public CustomVisualText(final String text, final int x, final int y) throws IllegalArgumentException {
		super(x, y, 0, 0, Color.white, Color.black);
		
		if(text == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.text = text;
	}
	
	/**
	 * Gets the text that is painted.
	 * 
	 * @return the text
	 * @since 1.0
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Sets the text that should be painted.
	 * 
	 * @param text the text
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if text is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setText(final String text) throws IllegalArgumentException {
		if(text == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.text = text;
	}

	@Override
	public void draw(Graphics2D g, Font f) {
		final FontMetrics fm = g.getFontMetrics(f);
		
		g.setColor(foreground);
		g.setFont(f);
		g.drawString(text, x, y + fm.getAscent());
	}
	
}
