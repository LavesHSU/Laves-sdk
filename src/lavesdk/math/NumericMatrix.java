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
 * Represents a numeric matrix.
 * <br><br>
 * A numeric matrix can be only of type {@link Number} which means that it can only contain numeric elements.
 * <br><br>
 * <b>Matrix operations</b>:<br>
 * <ul>
 * 		<li>{@link #add(NumericMatrix, NumericMatrix)}</li>
 * 		<li>{@link #scalarMult(NumericMatrix, Number)}</li>
 * 		<li>{@link #transpose(NumericMatrix, Number)}</li>
 * 		<li>{@link #multiply(NumericMatrix, NumericMatrix)}</li>
 * </ul>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <T> the type of the numeric matrix elements
 */
public class NumericMatrix<T extends Number> extends Matrix<T> {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new numeric matrix of size <code>n</code> x <code>m</code> and a default element value of <code>null</code>.
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
	public NumericMatrix(int n, int m) throws IllegalArgumentException {
		super(n, m);
	}
	
	/**
	 * Creates a new numeric matrix of size <code>n</code> x <code>m</code>.
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
	public NumericMatrix(int n, int m, T defValue) throws IllegalArgumentException {
		super(n, m, defValue);
	}
	
	@Override
	public Matrix<T> clone() {
		final NumericMatrix<T> copy = new NumericMatrix<T>(n, m);
		copy(this, copy);
		
		return copy;
	}
	
	/**
	 * Adds the given matrices.
	 * 
	 * @param a1 matrix 1
	 * @param a2 matrix 2
	 * @return the sum (<code>a1 + a2</code>)
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if a1 is null</li>
	 * 		<li>if a2 is null</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the size of a1 is unequal the size of a2</li>
	 * </ul>
	 * @since 1.0
	 */
	public static NumericMatrix<Number> add(final NumericMatrix<? extends Number> a1, final NumericMatrix<?extends Number> a2) throws NullPointerException, IllegalArgumentException {
		if(a1.getRowCount() != a2.getRowCount() || a1.getColumnCount() != a2.getColumnCount())
			throw new IllegalArgumentException("matrices are not of the same type");
		
		final NumericMatrix<Number> m = new NumericMatrix<Number>(a1.getRowCount(), a1.getColumnCount());
		Number e1;
		Number e2;
		
		for(int i = 0; i < m.getRowCount(); i++) {
			for(int j = 0; j < m.getColumnCount(); j++) {
				e1 = a1.get(i, j);
				e2 = a2.get(i, j);
				
				if(e1 != null && e2 != null)
					m.set(i, j, new Double(e1.doubleValue() + e2.doubleValue()));
			}
		}
		
		return m;
	}
	
	/**
	 * Multiplying the given matrix with a specified scalar <code>c</code>.
	 * 
	 * @param a the matrix
	 * @param c the scalar
	 * @return the scalar multiplication (<code>c * a</code>)
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if a is null</li>
	 * 		<li>if c is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static NumericMatrix<Number> scalarMult(final NumericMatrix<? extends Number> a, final Number c) throws NullPointerException {
		final NumericMatrix<Number> m = new NumericMatrix<Number>(a.getRowCount(), a.getColumnCount());
		Number e;
		
		for(int i = 0; i < m.getRowCount(); i++) {
			for(int j = 0; j < m.getColumnCount(); j++) {
				e = a.get(i, j);
				
				if(e != null)
					m.set(i, j, new Double(e.doubleValue() * c.doubleValue()));
			}
		}
		
		return m;
	}
	
	/**
	 * Transposes the given matrix.
	 * 
	 * @param a the n-by-m matrix
	 * @return the transpose which is the m-by-n matrix of a
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if a is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static NumericMatrix<Number> transpose(final NumericMatrix<? extends Number> a, final Number c) throws NullPointerException {
		final NumericMatrix<Number> m = new NumericMatrix<Number>(a.getColumnCount(), a.getRowCount());
		
		for(int i = 0; i < m.getRowCount(); i++)
			for(int j = 0; j < m.getColumnCount(); j++)
				m.set(i, j, a.get(j, i));
		
		return m;
	}
	
	/**
	 * Multiplies the given matrices.
	 * 
	 * @param a1 matrix 1
	 * @param a2 matrix 2
	 * @return the multiplication (<code>a1 * a2</code>) which is a n-by-p matrix (a1 = n-by-m, a2 = m-by-p)
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if a1 is null</li>
	 * 		<li>if a2 is null</li>
	 * </ul>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the number of columns of a1 does not fit to the number of rows of a2</li>
	 * </ul>
	 * @since 1.0
	 */
	public static NumericMatrix<Number> multiply(final NumericMatrix<? extends Number> a1, final NumericMatrix<?extends Number> a2) throws NullPointerException, IllegalArgumentException {
		if(a1.getColumnCount() != a2.getRowCount())
			throw new IllegalArgumentException("number of columns of a1 does not fit to number of rows of a2");
		
		final NumericMatrix<Number> m = new NumericMatrix<Number>(a1.getRowCount(), a2.getColumnCount());
		Number e1;
		Number e2;
		Double sum;
		
		for(int i = 0; i < m.getRowCount(); i++) {
			for(int j = 0; j < m.getColumnCount(); j++) {
				sum = new Double(0.0);
				
				for(int r = 0; r < a1.getColumnCount(); r++) {
					e1 = a1.get(i, r);
					e2 = a2.get(r, j);
					
					if(e1 == null || e2 == null) {
						sum = null;
						break;
					}
					
					sum += e1.doubleValue()*e2.doubleValue();
				}
				
				m.set(i, j, sum);
			}
		}
		
		return m;
	}

}
