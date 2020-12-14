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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;

/**
 * A custom cell editor for radio buttons.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class RadioButtonCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1L;
	
	/** the selection value of the radio button */
	private Boolean selected;
	/** the radio button */
	private final JRadioButton radioButton;
	/** the border of the editor */
	private Border border;
	
	/**
	 * Creates a new radio button cell editor.
	 * 
	 * @since 1.0
	 */
	public RadioButtonCellEditor() {
		selected = false;
		radioButton = new JRadioButton();
		
		radioButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				RadioButtonCellEditor.this.radioButton.setSelected(true);
				RadioButtonCellEditor.this.selected = RadioButtonCellEditor.this.radioButton.isSelected();
				// make the renderer reappear
				RadioButtonCellEditor.this.fireEditingStopped();
			}
		});
	}

	@Override
	public Object getCellEditorValue() {
		return selected;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		try {
			selected = (Boolean)value;
			radioButton.setSelected(selected);
			radioButton.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
			radioButton.setHorizontalAlignment(JRadioButton.CENTER);
			
			if(border == null)
				border = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			radioButton.setBorder(border);
		}
		catch(ClassCastException e) {
		}
		
		return radioButton;
	}

}
