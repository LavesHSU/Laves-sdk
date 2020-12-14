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

package lavesdk.math;

/**
 * Represents an object matrix.
 * <br><br>
 * An object matrix can contain every object based on a specific type.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <T> the type of a matrix element
 */
public class ObjectMatrix<T> extends Matrix<T> {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new object matrix of size <code>n</code> x <code>m</code> and a default element value of <code>null</code>.
	 * 
	 * @param n the number of rows
	 * @param m the number of columns
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if n <code>< 1</code></li>
	 * 		<li>if m <code>< 1</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public ObjectMatrix(int n, int m) throws IllegalArgumentException {
		super(n, m);
	}
	
	/**
	 * Creates a new object matrix of size <code>n</code> x <code>m</code>.
	 * 
	 * @param n the number of rows
	 * @param m the number of columns
	 * @param defValue the default element value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if n <code>< 1</code></li>
	 * 		<li>if m <code>< 1</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public ObjectMatrix(int n, int m, T defValue) throws IllegalArgumentException {
		super(n, m, defValue);
	}
	
	@Override
	public ObjectMatrix<T> clone() {
		final ObjectMatrix<T> copy = new ObjectMatrix<T>(n, m);
		copy(this, copy);
		
		return copy;
	}

}
