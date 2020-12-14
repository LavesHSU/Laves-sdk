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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * A custom cell editor to edit color properties in a {@link PropertiesList}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class ColorCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1L;
	
	/** the current color */
	private Color color;
	/** the editor */
	private final JPanel editorPanel;
	/** the color panel which is part of the editor */
	private final JPanel colorPanel;
	/** the choose button which is part of the editor */
	private final JButton button;
	/** the color chooser */
	private final JColorChooser colorChooser;
	/** the dialog for the color chooser */
	private final JDialog colorDialog;
	/** the event controller */
	private final EventController eventController;
	
	/**
	 * Creates a new color cell editor.
	 * 
	 * @param name the name of the editor (the name is displayed as the title of the color chooser)
	 * @since 1.0
	 */
	public ColorCellEditor(final String name) {
		color = Color.white;
		eventController = new EventController();
		editorPanel = new JPanel();
		colorPanel = new JPanel();
		button = new JButton("...");
		colorChooser = new JColorChooser();
		colorDialog = JColorChooser.createDialog(button, name, true, colorChooser, eventController, null);
		
		// listener for button click
		button.addActionListener(eventController);
		
		// layout the editor panel
		editorPanel.setLayout(new BorderLayout());
		editorPanel.add(colorPanel, BorderLayout.CENTER);
		editorPanel.add(button, BorderLayout.EAST);
	}

	@Override
	public Object getCellEditorValue() {
		return color;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		try {
			color = (Color)value;
			colorPanel.setBackground(color);
			editorPanel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
		}
		catch(ClassCastException e) {
		}
		
		return editorPanel;
	}
	
	/**
	 * Handles the events of the cell editor.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class EventController implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == ColorCellEditor.this.button) {
				ColorCellEditor.this.colorPanel.setBackground(color);
				ColorCellEditor.this.colorChooser.setColor(color);
				ColorCellEditor.this.colorDialog.setVisible(true);
				
				// make the renderer reappear
				ColorCellEditor.this.fireEditingStopped();
			}
			else {
				// user pressed "ok" in the color chooser dialog
				ColorCellEditor.this.color = ColorCellEditor.this.colorChooser.getColor();
			}
		}
		
	}

}
