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

package lavesdk.gui.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

/**
 * Represents a border in a table.
 * <br><br>
 * A border can have a width (meaning the line width of the border) and a color.
 * <br><br>
 * To create a swing {@link Border} use the static method {@link #createBorder(ExecutionTableBorder, ExecutionTableBorder, ExecutionTableBorder, ExecutionTableBorder)}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class ExecutionTableBorder {
	
	/** the border width */
	private final int width;
	/** the border color */
	private final Color color;
	/** the stroke of the border */
	private final Stroke stroke;
	
	/** contains all created borders */
	private static final Map<String, Border> borders;
	
	static {
		borders = new HashMap<String, Border>();
	}
	
	/**
	 * Creates a new border.
	 * 
	 * @param width the width of the border
	 * @param color the color of the border
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if width is <code>< 0</code></li>
	 * 		<li>if color is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableBorder(final int width, final Color color) throws IllegalArgumentException {
		if(width < 0 || color == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.width = width;
		this.color = color;
		this.stroke = new BasicStroke(width);
	}
	
	/**
	 * Gets the width of the border.
	 * 
	 * @return the line width
	 * @since 1.0
	 */
	public final int getWidth() {
		return width;
	}
	
	/**
	 * Gets the color of the border.
	 * 
	 * @return the border color
	 * @since 1.0
	 */
	public final Color getColor() {
		return color;
	}
	
	@Override
	public String toString() {
		return ExecutionTableBorder.class.getSimpleName() + "[" + width + "," + color + "]"; 
	}
	
	/**
	 * Creates a swing {@link Border} using four borders.
	 * 
	 * @param left the left border
	 * @param right the right border
	 * @param top the top border
	 * @param bottom the bottom border
	 * @return the compound border
	 * @since 1.0
	 */
	public static Border createBorder(final ExecutionTableBorder left, final ExecutionTableBorder right, final ExecutionTableBorder top, final ExecutionTableBorder bottom) {
		final String key = createBorderKey(left, right, top, bottom);
		Border border = borders.get(key);
		
		if(border == null) {
			final Insets insets = new Insets((top != null) ? top.width : 0, (left != null) ? left.width : 0, (bottom != null) ? bottom.width : 0, (right != null) ? right.width : 0);
			border = new AbstractBorder() {

				private static final long serialVersionUID = 1L;
				
				@Override
				public Insets getBorderInsets(Component c) {
					return insets;
				}
				
				@Override
				public Insets getBorderInsets(Component c, Insets insets) {
					return insets;
				}
				
				@Override
				public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
					final Graphics2D g2d = (Graphics2D)g;
					final Stroke oldStroke = g2d.getStroke();
					int linePos;
					
					// the left and the right border are at the bottom (draw at first)
					if(left != null) {
						linePos = x + left.width / 2;
						g2d.setColor(left.color);
						g2d.setStroke(left.stroke);
						g2d.drawLine(linePos, y, linePos, y + height);
					}
					if(right != null) {
						linePos = x + width - (int)((float)right.width / 2 + 0.5f);
						g2d.setColor(right.color);
						g2d.setStroke(right.stroke);
						g2d.drawLine(linePos, y, linePos, y + height);
					}
					
					// the top and the bottom border are at the top (draw at last)
					if(top != null) {
						linePos = y + top.width / 2;
						g2d.setColor(top.color);
						g2d.setStroke(top.stroke);
						g2d.drawLine(x, linePos, x + width, linePos);
					}
					if(bottom != null) {
						linePos = y + height - (int)((float)bottom.width / 2 + 0.5f);
						g2d.setColor(bottom.color);
						g2d.setStroke(bottom.stroke);
						g2d.drawLine(x, linePos, x + width, linePos);
					}
					
					g2d.setStroke(oldStroke);
				}
			};
			
			borders.put(key, border);
		}
		
		return border;
	}
	
	/**
	 * Creates a key for the given borders.
	 * 
	 * @param left the left border
	 * @param right the right border
	 * @param top the top border
	 * @param bottom the bottom border
	 * @return the border key
	 * @since 1.0
	 */
	private static String createBorderKey(final ExecutionTableBorder left, final ExecutionTableBorder right, final ExecutionTableBorder top, final ExecutionTableBorder bottom) {
		final StringBuilder key = new StringBuilder(75);
		
		appendBorderKey(key, left);
		appendBorderKey(key, right);
		appendBorderKey(key, top);
		appendBorderKey(key, bottom);
		
		return key.toString();
	}
	
	/**
	 * Appends the key of the border to the border key string.
	 * 
	 * @param bks the border key string
	 * @param border the border
	 * @since 1.0
	 */
	private static void appendBorderKey(final StringBuilder bks, final ExecutionTableBorder border) {
		if(border != null)
			bks.append(border.width);
		bks.append(',');
		if(border != null)
			bks.append(border.color);
		bks.append(';');
	}

}
