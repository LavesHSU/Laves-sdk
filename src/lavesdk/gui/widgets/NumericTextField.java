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

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import lavesdk.utils.MathUtils;

/**
 * A numeric text field component which only allows a digit, a point, a comma and a minus
 * as characters.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class NumericTextField extends JTextField {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new numeric text field.
	 * 
	 * @since 1.0
	 */
	public NumericTextField() {
		super();
	}
	
	/**
	 * Creates a new numeric text field.
	 * 
	 * @param columns the number of columns to use to calculate the preferred width; if columns is set to zero, the preferred width will be whatever naturally results from the component implementation
	 * @since 1.0
	 */
	public NumericTextField(final int columns) {
		super(columns);
	}
	
	/**
	 * Creates a new numeric text field.
	 * 
	 * @param number the number to be displayed
	 * @since 1.0
	 */
	public NumericTextField(final Number number) {
		super(number.toString());
	}
	
	/**
	 * Sets the number (as a string).
	 * 
	 * @param number the number as a string
	 * @since 1.0
	 */
	@Override
	public void setText(String number) {
		// change the format of the number that should be displayed
		if(number != null) {
			Number n;
			
			try {
				n = new Double(number);
			}
			catch(NumberFormatException e) {
				n = null;
			}
			
			if(n != null)
				number = MathUtils.formatDouble(n.doubleValue());
		}
		
		super.setText(number);
	}
	
	@Override
	protected Document createDefaultModel() {
		return new NumericDocument();
	}
	
	/**
	 * The document of the numeric text field.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private static class NumericDocument extends PlainDocument {
		
		private static final long serialVersionUID = 1L;

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if(str == null)
				return;
			
			// accept only digits, points, commas and minuses
			final char[] characters = str.toCharArray();
			for(char c : characters)
				if(!Character.isDigit(c) && c != '.' && c != ',' && c != '-')
					return;
			
			super.insertString(offs, new String(characters), a);
		}
		
	}

}
