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

package lavesdk.algorithm.text;

import lavesdk.algorithm.text.exceptions.InvalidLaTeXFormulaException;

import org.scilab.forge.jlatexmath.ParseException;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXFormula.TeXIconBuilder;
import org.scilab.forge.jlatexmath.TeXIcon;

/**
 * Represents a formula given by a latex expression.
 * <br><br>
 * Each formula has a normal state and a highlighted state. Use {@link #getNormal()} to get the normal
 * state of the formula and {@link #getHighlighted()} to get the highlighted state.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class LaTeXFormula {
	
	/** the corresponding step */
	private final AlgorithmStep step;
	/** the latex expression */
	private final String expression;
	/** the normal latex formula */
	private TeXIcon normal;
	/** the highlighted latex formula */
	private TeXIcon highlighted;
	/** the parameters of the formula */
	private final String parameters;
	/** the parameter values */
	private final String[] paramValues;
	
	/** the delimiter of parameters */
	private static final String PARAM_DELIMITER = ",";
	
	/**
	 * Creates a new formula.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS CONSTRUCTOR</i>!
	 * 
	 * @param step the corresponding step
	 * @param expression the expression in latex
	 * @param parameters the parameter string of the formula
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if step is null</li>
	 * 		<li>if expression is null</li>
	 * 		<li>if position <code>< 0</code></li>
	 * 		<li>if parameters is null</li>
	 * </ul>
	 * @throws InvalidLaTeXFormulaException
	 * <ul>
	 * 		<li>if the latex expression has an incorrect format</li>
	 * </ul>
	 * @since 1.0
	 */
	LaTeXFormula(final AlgorithmStep step, final String expression, final String parameters) throws IllegalArgumentException, InvalidLaTeXFormulaException {
		if(step == null || expression == null || parameters == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.step = step;
		this.expression = expression;
		this.parameters = parameters;
		if(parameters.length() > 0)
			this.paramValues = parameters.split(PARAM_DELIMITER);
		else
			this.paramValues = new String[0];
		
		updateFormula();
	}
	
	/**
	 * Gets the corresponding step.
	 * 
	 * @return the corresponding step
	 * @since 1.0
	 */
	public final AlgorithmStep getStep() {
		return step;
	}
	
	/**
	 * Gets the latex expression.
	 * 
	 * @return the expression
	 * @since 1.0
	 */
	public final String getExpression() {
		return expression;
	}
	
	/**
	 * Gets the parameter string of the formula.
	 * 
	 * @return the parameter string
	 * @since 1.0
	 */
	public final String getParameters() {
		return parameters;
	}
	
	/**
	 * Gets the number of parameters that the formula has.
	 * 
	 * @return the number of parameters
	 * @since 1.0
	 */
	public final int getParameterCount() {
		return paramValues.length;
	}
	
	/**
	 * Gets a parameter as a string.
	 * 
	 * @param index the index of the parameter
	 * @return the parameter value
	 * @throws ArrayIndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getParameterCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final String getStringParameter(final int index) throws ArrayIndexOutOfBoundsException {
		return paramValues[index];
	}
	
	/**
	 * Gets a parameter as a integer.
	 * 
	 * @param index the index of the parameter
	 * @return the parameter value
	 * @throws ArrayIndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getParameterCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final int getIntParameter(final int index) throws ArrayIndexOutOfBoundsException {
		try {
			return new Integer(paramValues[index]);
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}
	
	/**
	 * Gets a parameter as a float.
	 * 
	 * @param index the index of the parameter
	 * @return the parameter value
	 * @throws ArrayIndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getParameterCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final float getFloatParameter(final int index) throws ArrayIndexOutOfBoundsException {
		try {
			return new Float(paramValues[index]);
		}
		catch(NumberFormatException e) {
			return 0.0f;
		}
	}
	
	/**
	 * Gets a parameter as a boolean.
	 * 
	 * @param index the index of the parameter
	 * @return the parameter value
	 * @throws ArrayIndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getParameterCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final boolean getBooleanParameter(final int index) throws ArrayIndexOutOfBoundsException {
		try {
			return new Boolean(paramValues[index]);
		}
		catch(NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Gets the normal formula.
	 * 
	 * @return the normal formula
	 * @since 1.0
	 */
	public final TeXIcon getNormal() {
		return normal;
	}
	
	/**
	 * Gets the highlighted formula.
	 * 
	 * @return the formula in the highlight color
	 * @since 1.0
	 */
	public final TeXIcon getHighlighted() {
		return highlighted;
	}
	
	@Override
	public String toString() {
		return expression;
	}
	
	/**
	 * Updates the formula meaning that the icons of the states normal and highlighted
	 * are recreated using the current font size of the associated {@link AlgorithmText}.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @throws InvalidLaTeXFormulaException
	 * <ul>
	 * 		<li>if the latex expression has an incorrect format</li>
	 * </ul>
	 * @since 1.0
	 */
	void updateFormula() throws InvalidLaTeXFormulaException {
		try {
			final AlgorithmText text = step.getParagraph().getParent();
			final TeXIconBuilder builder = new TeXFormula(expression).new TeXIconBuilder();
			
			normal = builder.setStyle(TeXConstants.STYLE_DISPLAY).setSize(text.getFontSize()).build();
			highlighted = builder.setType(TeXFormula.BOLD).build();
		}
		catch(ParseException e) {
			throw new InvalidLaTeXFormulaException(e.getMessage() + " (" + expression + ")");
		}
	}

}
