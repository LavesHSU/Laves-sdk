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

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * Renders a radio button cell.
 * <br><br>
 * Use {@link JTable#setDefaultRenderer(Class, TableCellRenderer)} to specify this renderer as the default for the {@link Boolean} class.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class RadioButtonCellRenderer extends JRadioButton implements TableCellRenderer {
	
	private static final long serialVersionUID = 1L;
	
	/** the border of the cell */
	private Border border;
	
	/**
	 * Creates a new radio button cell renderer.
	 * 
	 * @since 1.0
	 */
	public RadioButtonCellRenderer() {
		this.border = null;
		
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		// set the background color from the given value
		try {
			setSelected((Boolean)value);
		}
		catch(ClassCastException e) {
			setSelected(false);
		}
		
		if(border == null)
			border = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		
		setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
		setHorizontalAlignment(JRadioButton.CENTER);
		setBorder(hasFocus ? UIManager.getBorder("Table.focusCellHighlightBorder") : border);
		
		return this;
	}

}
