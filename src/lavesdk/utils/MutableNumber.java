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
 * Class:		MutableNumber
 * Task:		Make a number mutable
 * Created:		21.11.13
 * LastChanges:	21.11.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.utils;

/**
 * Represents a mutable number.
 * <br><br>
 * This number can be used as a <i>call-by-reference</i> parameter of a method to replace a primitive number type
 * which can only be committed as a <i>call-by-value</i> parameter.<br>
 * Meaning that you can change the number in another method than the one that provides the number.
 * <br><br>
 * Use {@link #value()} to get the value of the number and {@link #value(Number)} to set a new value of the number.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class MutableNumber<T extends Number> {
	
	/** the number */
	private T value;
	
	/**
	 * Creates a new mutable number.
	 * 
	 * @param value the initial value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if value is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public MutableNumber(final T value) throws IllegalArgumentException {
		value(value);
	}
	
	/**
	 * Gets the value of the number.
	 * 
	 * @return the value
	 * @since 1.0
	 */
	public T value() {
		return value;
	}
	
	/**
	 * Sets the value of the number.
	 * 
	 * @param value the value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if value is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void value(final T value) throws IllegalArgumentException {
		if(value == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

}
