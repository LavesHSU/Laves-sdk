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
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;

/**
 * A custom cell editor to change numeric values with a slider.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class SliderCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1L;
	
	/** the number that is changed by the slider */
	private Number number;
	/** the editor */
	private final JPanel editorPanel;
	/** the label that shows the current number */
	private final JLabel numberLabel;
	/** the slider */
	private final JSlider slider;
	
	/**
	 * Creates a new slider cell editor.
	 * 
	 * @since 1.0
	 */
	public SliderCellEditor() {
		number = 0;
		editorPanel = new JPanel();
		numberLabel = new JLabel();
		slider = new JSlider(0, 100);
		
		numberLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		numberLabel.setPreferredSize(new Dimension(30, 0));
		
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				SliderCellEditor.this.number = SliderCellEditor.this.slider.getValue();
				SliderCellEditor.this.numberLabel.setText(SliderCellEditor.this.number.toString());
			}
		});
		
		editorPanel.setLayout(new BorderLayout());
		editorPanel.add(numberLabel, BorderLayout.WEST);
		editorPanel.add(slider, BorderLayout.CENTER);
	}
	
	/**
	 * Gets the minimum of the slider.
	 * 
	 * @return the minimum
	 * @since 1.0
	 */
	public int getMinimum() {
		return slider.getMinimum();
	}
	
	/**
	 * Sets the minimum of the slider.
	 * 
	 * @param minimum the minimum
	 * @since 1.0
	 */
	public void setMinimum(final int minimum) {
		slider.setMinimum(minimum);
	}
	
	/**
	 * Gets the maximum of the slider.
	 * 
	 * @return the maximum
	 * @since 1.0
	 */
	public int getMaximum() {
		return slider.getMaximum();
	}
	
	/**
	 * Sets the maximum of the slider.
	 * 
	 * @param maximum the maximum
	 * @since 1.0
	 */
	public void setMaximum(final int maximum) {
		slider.setMaximum(maximum);
	}

	@Override
	public Object getCellEditorValue() {
		return number;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		try {
			number = (Number)value;
			editorPanel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
			numberLabel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
			numberLabel.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
			slider.setValue(number.intValue());
			slider.setBackground(table.getBackground());
		}
		catch(ClassCastException e) {
		}
		
		return editorPanel;
	}

}
