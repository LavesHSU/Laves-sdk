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
 * Class:		ColorCellRenderer
 * Task:		A custom cell renderer to draw color properties
 * Created:		28.11.13
 * LastChanges:	17.12.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * Renders a color cell.
 * <br><br>
 * Use {@link JTable#setDefaultRenderer(Class, TableCellRenderer)} to specify this renderer as the default for the {@link Color} class.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class ColorCellRenderer extends JLabel implements TableCellRenderer {
	
	private static final long serialVersionUID = 1L;
	
	/** the border of the unselected state */
	private Border unselectedBorder;
	/** the border of the selected state */
	private Border selectedBorder;
	/** the flag that indicates if the rgb values should be displayed */
	private final boolean displayRGB;
	
	/**
	 * Creates a new color cell renderer.
	 * 
	 * @param displayRGB <code>true</code> if the rgb value of the color should be displayed otherwise <code>false</code>
	 * @since 1.0
	 */
	public ColorCellRenderer(final boolean displayRGB) {
		this.displayRGB = displayRGB;
		this.unselectedBorder = null;
		this.selectedBorder = null;
		
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		// set the background color from the given value
		try {
			setBackground((Color)value);
		}
		catch(ClassCastException e) {
		}
		
		// draw selection state of necessary
		if(isSelected) {
			if(selectedBorder == null)
				selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getSelectionBackground());
			setBorder(selectedBorder);
		}
		else {
			if(unselectedBorder == null)
				unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());
			setBorder(unselectedBorder);
		}
		
		// display the rgb value in the center of the cell if necessary
		if(displayRGB) {
			setHorizontalAlignment(JLabel.CENTER);
			setHorizontalTextPosition(JLabel.CENTER);
			setForeground(new Color(255 - getBackground().getRed(), 255 - getBackground().getGreen(), 255 - getBackground().getBlue()));
			setFont(table.getFont());
			setText(getBackground().getRed() + ", " + getBackground().getGreen() + ", " + getBackground().getBlue());
		}
		
		return this;
	}

}
