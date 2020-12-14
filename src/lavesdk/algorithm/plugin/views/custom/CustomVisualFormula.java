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
 * Class:		CustomVisualFormula
 * Task:		Paint a custom formula
 * Created:		11.05.14
 * LastChanges:	20.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views.custom;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import lavesdk.algorithm.plugin.views.GraphView;
import lavesdk.gui.widgets.Symbol;

/**
 * Represents a formula (latex expression) that can be displayed in a {@link GraphView}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class CustomVisualFormula extends CustomVisualObject {
	
	/** the expression of the formula */
	protected String expression;
	/** the icon representing the formula */
	protected Symbol formula;
	/** the last font size of the formula */
	private float lastFontSize;
	
	/**
	 * Creates a new custom visual formula.
	 * 
	 * @param expression the latex expression describing the formula
	 * @param x the x position of the string in the graph
	 * @param y the y position of the string in the graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if expression is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public CustomVisualFormula(final String expression, final int x, final int y) throws IllegalArgumentException {
		super(x, y, 0, 0, Color.white, Color.black);
		
		if(expression == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.expression = expression;
		this.lastFontSize = 8.0f;
		this.formula = Symbol.createLaTeXSymbol(expression, lastFontSize);
	}
	
	/**
	 * Gets the latex expression of the formula that is painted.
	 * 
	 * @return the latex expression
	 * @since 1.0
	 */
	public String getExpression() {
		return expression;
	}
	
	/**
	 * Sets the latex expression of the formula that should be painted.
	 * 
	 * @param expression the latex expression
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if expression is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setExpression(final String expression) throws IllegalArgumentException {
		if(expression == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.expression = expression;
		this.formula = Symbol.createLaTeXSymbol(expression, lastFontSize);
	}
	
	@Override
	public int getWidth() {
		return formula.getIconWidth();
	}
	
	@Override
	public int getHeight() {
		return formula.getIconHeight();
	}

	@Override
	public void draw(Graphics2D g, Font f) {
		// if the font size changed (e.g. because the view is zoomed) then adjust the formula
		if(f.getSize2D() != lastFontSize) {
			lastFontSize = f.getSize2D();
			formula = Symbol.createLaTeXSymbol(expression, lastFontSize);
		}
		
		g.setColor(foreground);
		formula.paintIcon(null, g, x, y);
	}

}
