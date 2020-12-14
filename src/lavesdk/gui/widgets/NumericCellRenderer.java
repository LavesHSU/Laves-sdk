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
 * Class:		NumericCellRenderer
 * Task:		A custom cell renderer to draw numeric properties
 * Created:		28.11.13
 * LastChanges:	29.01.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import lavesdk.utils.MathUtils;

/**
 * Renders a numeric cell.
 * <br><br>
 * Use {@link JTable#setDefaultRenderer(Class, TableCellRenderer)} to specify this renderer as the default for the {@link Number} class.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class NumericCellRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	/** the border to show a padding */
	private Border paddingBorder;
	
	/**
	 * Creates a new numeric cell renderer.
	 * 
	 * @since 1.0
	 */
	public NumericCellRenderer() {
		paddingBorder = null;
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if(paddingBorder == null)
			paddingBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
		
		setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
		setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
		setBorder(paddingBorder);
		setFont(table.getFont());
		
		if(value instanceof Number)
			setText(MathUtils.formatDouble(((Number)value).doubleValue()));
		else
			setText("");
		
		return this;
	}

}
