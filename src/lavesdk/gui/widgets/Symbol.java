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
 * Class:		Symbol
 * Task:		Representation of a Symbol
 * Created:		20.12.13
 * LastChanges:	07.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

/**
 * Represents a symbol that can be used to mask objects ({@link Mask}).
 * <br><br>
 * <b>Predefined symbols</b>:<br>
 * Use the static method {@link #getPredefinedSymbol(PredefinedSymbol)} to get a predefined symbol.
 * <br><br>
 * <b>Create a custom symbol</b>:<br>
 * If you want to create a custom symbol meaning that you paint the symbol on you own inherit from {@link Symbol}
 * and override {@link #getIconHeight()}, {@link #getIconWidth()} and {@link #paintIcon(Component, Graphics, int, int)}.
 * <br><br>
 * <b>Use predefined icon</b>:<br>
 * If you want to use a predefined icon as a symbol then you can create the symbol with {@link #Symbol(Icon)}.
 * In this case you do not need to create custom painting or something else.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class Symbol implements Icon {
	
	/** the symbol as an icon */
	private final Icon symbol;
	
	/** the alpha symbol (Greek letter) */
	private static Symbol ALPHA;
	/** the beta symbol (Greek letter) */
	private static Symbol BETA;
	/** the gamma symbol (Greek letter) */
	private static Symbol GAMMA;
	/** the delta symbol (Greek letter) */
	private static Symbol DELTA;
	/** the delta symbol as capital (Greek letter) */
	private static Symbol DELTA_BIG;
	/** the theta symbol (Greek letter) */
	private static Symbol THETA;
	/** the lambda symbol (Greek letter) */
	private static Symbol LAMBDA;
	/** the pi symbol (Greek letter) */
	private static Symbol PI;
	/** the sigma symbol (Greek letter) */
	private static Symbol SIGMA;
	/** the sigma symbol as capital (Greek letter) */
	private static Symbol SIGMA_BIG;
	/** the rho symbol (Greek letter) */
	private static Symbol RHO;
	/** the tau symbol (Greek letter) */
	private static Symbol TAU;
	/** the phi symbol (Greek letter) */
	private static Symbol PHI;
	/** the infinity symbol */
	private static Symbol INFINITY;
	/** the sum symbol */
	private static Symbol SUM;
	
	/**
	 * Creates a new custom symbol.
	 * <br><br>
	 * In this case you have to override {@link #getIconHeight()}, {@link #getIconWidth()} and {@link #paintIcon(Component, Graphics, int, int)}.
	 * 
	 * @since 1.0
	 */
	public Symbol() {
		this(null);
	}
	
	/**
	 * Creates a new symbol based on a loaded icon.
	 * 
	 * @param symbol the icon that should be used as the symbol
	 * @since 1.0
	 */
	public Symbol(final Icon symbol) {
		this.symbol = symbol;
	}
	
	/**
	 * Gets a predefined symbol.
	 * 
	 * @param symbol the predefined symbol type
	 * @return the symbol or <code>null</code> if the symbol is not defined (all predefined symbols are defined)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if symbol is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static Symbol getPredefinedSymbol(final PredefinedSymbol symbol) throws IllegalArgumentException {
		if(symbol == null)
			throw new IllegalArgumentException("No valid argument!");
		
		switch(symbol) {
			case ALPHA:
				if(ALPHA == null)
					ALPHA = createLaTeXSymbol("\\alpha");
				return ALPHA;
			case BETA:
				if(BETA == null)
					BETA = createLaTeXSymbol("\\beta");
				return BETA;
			case GAMMA:
				if(GAMMA == null)
					GAMMA = createLaTeXSymbol("\\gamma");
				return GAMMA;
			case DELTA:
				if(DELTA == null)
					DELTA = createLaTeXSymbol("\\delta");
				return DELTA;
			case DELTA_BIG:
				if(DELTA_BIG == null)
					DELTA_BIG = createLaTeXSymbol("\\Delta");
				return DELTA_BIG;
			case THETA:
				if(THETA == null)
					THETA = createLaTeXSymbol("\\theta");
				return THETA;
			case LAMBDA:
				if(LAMBDA == null)
					LAMBDA = createLaTeXSymbol("\\lambda");
				return LAMBDA;
			case PI:
				if(PI == null)
					PI = createLaTeXSymbol("\\pi");
				return PI;
			case SIGMA:
				if(SIGMA == null)
					SIGMA = createLaTeXSymbol("\\sigma");
				return SIGMA;
			case SIGMA_BIG:
				if(SIGMA_BIG == null)
					SIGMA_BIG = createLaTeXSymbol("\\Sigma");
				return SIGMA_BIG;
			case RHO:
				if(RHO == null)
					RHO = createLaTeXSymbol("\\rho");
				return RHO;
			case TAU:
				if(TAU == null)
					TAU = createLaTeXSymbol("\\tau");
				return TAU;
			case PHI:
				if(PHI == null)
					PHI = createLaTeXSymbol("\\phi");
				return PHI;
			case INFINITY:
				if(INFINITY == null)
					INFINITY = createLaTeXSymbol("\\infty");
				return INFINITY;
			case SUM:
				if(SUM == null)
					SUM = createLaTeXSymbol("\\sum");
				return SUM;
		}
		
		return null;
	}
	
	/**
	 * Creates a latex symbol using a font size of <code>10.0f</code>.
	 * 
	 * @param latexExpr the latex expression of the symbol
	 * @return the symbol
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if latexExpr is null</li>
	 * 		<li>if latexExpr is empty</li>
	 * </ul>
	 * @since 1.0
	 */
	public static Symbol createLaTeXSymbol(final String latexExpr) throws IllegalArgumentException {
		return createLaTeXSymbol(latexExpr, 10.0f);
	}
	
	/**
	 * Creates a latex symbol.
	 * 
	 * @param latexExpr the latex expression of the symbol
	 * @param fontSize the font size of the latex symbol
	 * @return the symbol
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if latexExpr is null</li>
	 * 		<li>if latexExpr is empty</li>
	 * 		<li>if fontSize is <code><= 0.0f</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public static Symbol createLaTeXSymbol(final String latexExpr, final float fontSize) throws IllegalArgumentException {
		if(latexExpr == null || latexExpr.isEmpty() || fontSize <= 0.0f)
			throw new IllegalArgumentException("No valid argument!");
		
		return new Symbol(new TeXFormula(latexExpr).new TeXIconBuilder().setStyle(TeXConstants.STYLE_TEXT).setSize(fontSize).build());
	}
	
	/**
	 * Gets the symbol height.
	 * 
	 * @return the height
	 * @since 1.0
	 */
	@Override
	public int getIconHeight() {
		return (symbol != null) ? symbol.getIconHeight() : 0;
	}
	
	/**
	 * Gets the symbol width.
	 * 
	 * @return the width
	 * @since 1.0
	 */
	@Override
	public int getIconWidth() {
		return (symbol != null) ? symbol.getIconWidth() : 0;
	}

	/**
	 * Paints the symbol.
	 * 
	 * @param comp the component that invokes painting or <code>null</code>
	 * @param g the graphics context
	 * @param x the x position
	 * @param y the y position
	 * @since 1.0
	 */
	@Override
	public void paintIcon(Component comp, Graphics g, int x, int y) {
		if(symbol != null) {
			// latex symbols can take on the foreground color of the component
			if(symbol instanceof TeXIcon && comp != null)
				((TeXIcon)symbol).setForeground(comp.getForeground());
			
			symbol.paintIcon(comp, g, x, y);
		}
	}
	
	/**
	 * Predefined symbols.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public enum PredefinedSymbol {
		
		/** the alpha symbol (Greek letter) */
		ALPHA,
		
		/** the beta symbol (Greek letter) */
		BETA,
		
		/** the gamma symbol (Greek letter) */
		GAMMA,
		
		/** the delta symbol (Greek letter) */
		DELTA,
		
		/** the delta symbol as capital (Greek letter) */
		DELTA_BIG,
		
		/** the theta symbol (Greek letter) */
		THETA,
		
		/** the lambda symbol (Greek letter) */
		LAMBDA,
		
		/** the pi symbol (Greek letter) */
		PI,
		
		/** the sigma symbol (Greek letter) */
		SIGMA,
		
		/** the sigma symbol as capital (Greek letter) */
		SIGMA_BIG,
		
		/** the rho symbol (Greek letter) */
		RHO,
		
		/** the tau symbol (Greek letter) */
		TAU,
		
		/** the phi symbol (Greek letter) */
		PHI,
		
		/** the infinity symbol */
		INFINITY,
		
		/** the sum symbol */
		SUM
		
	}

}
