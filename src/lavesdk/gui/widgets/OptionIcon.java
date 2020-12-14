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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.SystemColor;

import javax.swing.Icon;

import lavesdk.resources.Resources;

/**
 * Representation of an option icon that consists of the icon defined by the user and a drop down arrow.
 * <br><br>
 * <b>Attention</b>:<br>
 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS CLASS</i>!
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
class OptionIcon implements Icon {
	
	/** the user icon */
	private final Icon userIcon;
	/** flag that indicates whether a rollover effect should be rendered */
	private final boolean rolloverEffect;
	/** the width of the icon */
	private final int width;
	/** the height of the icon */
	private final int height;
	
	/** the arrow icon */
	private static final Icon arrowIcon;
	/** the width of the arrow area */
	private static final int arrowAreaWidth;
	/** the spacing between the user icon and the arrow icon */
	private static final int SPACING = 2;
	/** the highlight color of the divider */
	private static final Color dividerHighlight = SystemColor.controlHighlight;
	/** the shadow color of the divider */
	private static final Color dividerShadow = SystemColor.controlShadow;
	
	static {
		arrowIcon = Resources.getInstance().DROPDOWN_ARROW_ICON;
		arrowAreaWidth = arrowIcon.getIconWidth();
	}
	
	/**
	 * Creates a new option icon.
	 * 
	 * @param userIcon the user icon
	 * @param rolloverEffect <code>true</code> if the icon should display a rollover effect otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if userIcon is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public OptionIcon(final Icon userIcon, final boolean rolloverEffect) throws IllegalArgumentException {
		if(userIcon == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.userIcon = userIcon;
		this.rolloverEffect = rolloverEffect;
		this.width = userIcon.getIconWidth() + arrowIcon.getIconWidth() + SPACING;
		this.height = Math.max(userIcon.getIconHeight(), arrowIcon.getIconHeight());
	}

	/**
	 * Gets the height of the icon.
	 * 
	 * @return the height
	 * @since 1.0
	 */
	@Override
	public int getIconHeight() {
		return height;
	}

	/**
	 * Gets the width of the icon.
	 * 
	 * @return the width
	 * @since 1.0
	 */
	@Override
	public int getIconWidth() {
		return width;
	}
	
	/**
	 * Gets the width of the arrow area that displays the drop down arrow of the icon.
	 * 
	 * @return the arrow area width
	 * @since 1.0
	 */
	public static int getArrowAreaWidth() {
		return arrowAreaWidth;
	}
	
	/**
	 * Gets the arrow icon.
	 * 
	 * @return the arrow icon
	 * @since 1.0
	 */
	public static Icon getArrowIcon() {
		return arrowIcon;
	}

	/**
	 * Paints the option icon.
	 * 
	 * @param component the component or <code>null</code>
	 * @param g the graphics context
	 * @param x the x position
	 * @param y the y position
	 */
	@Override
	public void paintIcon(Component component, Graphics g, int x, int y) {
		userIcon.paintIcon(component, g, x, y + (height - userIcon.getIconHeight()) / 2);
		arrowIcon.paintIcon(component, g, x + userIcon.getIconWidth() + SPACING, y + (height - arrowIcon.getIconHeight()) / 2);
		
		if(rolloverEffect) {
			final Color oldColor = g.getColor();
			
			// draw the separator
			g.setColor(dividerHighlight);
			g.drawLine(x + userIcon.getIconWidth() + 1, y, x + userIcon.getIconWidth() + 1, y + height);
			g.setColor(dividerShadow);
			g.drawLine(x + userIcon.getIconWidth() + 2, y, x + userIcon.getIconWidth() + 2, y + height);
			
			// reset the old color
			g.setColor(oldColor);
		}
	}

}
