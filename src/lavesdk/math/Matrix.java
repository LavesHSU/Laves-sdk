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
 * Class:		Matrix
 * Task:		Representation of a base matrix
 * Created:		18.11.13
 * LastChanges:	08.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.math;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the base of a matrix of a specific type of objects.
 * <br><br>
 * The indices of a matrix are zero-based meaning that <code>a(0 0)</code> is the top left element 
 * and <code>a(n-1 m-1)</code> is the bottom right element of a matrix <code>a</code>.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <T> the type of a matrix element
 */
public abstract class Matrix<T> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** the matrix data structure */
	private final List<List<T>> a;
	/** the number of rows */
	protected final int n;
	/** the number of columns */
	protected final int m;
	
	/**
	 * Creates a new matrix of size <code>n</code> x <code>m</code> and a default element value of <code>null</code>.
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
	public Matrix(final int n, final int m) throws IllegalArgumentException {
		this(n, m, null);
	}
	
	/**
	 * Creates a new matrix of size <code>n</code> x <code>m</code>.
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
	public Matrix(final int n, final int m, final T defValue) throws IllegalArgumentException {
		if(n < 1 || m < 1)
			throw new IllegalArgumentException("No valid argument!");
		
		this.a = new ArrayList<List<T>>(n);
		this.n = n;
		this.m = m;
		
		for(int i = 0; i < n; i++) {
			this.a.add(new ArrayList<T>(m));
			for(int j = 0; j < m; j++)
				this.a.get(i).add(defValue);
		}
	}
	
	/**
	 * Gets the value at <code>a(i j)</code>.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The matrix is zero-based meaning that <code>a(0 0)</code> is the top left element 
	 * and <code>a(n-1 m-1)</code> is the bottom right element of the matrix <code>a</code>.
	 * 
	 * @param i the index of the row
	 * @param j the index of the column
	 * @return the value
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if i is out of bounds</li>
	 * 		<li>if j is out of bounds</li>
	 * </ul>
	 * @since 1.0
	 */
	public T get(final int i, final int j) throws IndexOutOfBoundsException {
		return a.get(i).get(j);
	}
	
	/**
	 * Sets the element at <code>a(i j)</code> of the matrix.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The matrix is zero-based meaning that <code>a(0 0)</code> is the top left element 
	 * and <code>a(n-1 m-1)</code> is the bottom right element of the matrix <code>a</code>.
	 * 
	 * @see #isInDimension(int, int)
	 * @param i the index of the row
	 * @param j the index of the column
	 * @param value the value
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if i is out of bounds</li>
	 * 		<li>if j is out of bounds</li>
	 * </ul>
	 * @since 1.0
	 */
	public void set(final int i, final int j, final T value) throws IndexOutOfBoundsException {
		a.get(i).set(j, value);
	}
	
	/**
	 * Sets the elements of the matrix.
	 * 
	 * @param elements the elements
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if elements is null</li>
	 * 		<li>if elements does not have the dimension of this matrix</li>
	 * </ul>
	 */
	public void set(final T[][] elements) throws IllegalArgumentException {
		if(elements == null || elements.length != n || elements[0].length != m)
			throw new IllegalArgumentException("No valid argument!");
		
		for(int i = 0; i < n; i++)
			for(int j = 0; j < m; j++)
				set(i, j, elements[i][j]);
	}
	
	/**
	 * Gets the number of rows this matrix has.
	 * 
	 * @return the number of rows
	 * @since 1.0
	 */
	public final int getRowCount() {
		return n;
	}
	
	/**
	 * Gets the number of columns this matrix has.
	 * 
	 * @return the number of columns
	 * @since 1.0
	 */
	public final int getColumnCount() {
		return m;
	}
	
	/**
	 * Indicates whether the row index <code>i</code> an the column index <code>j</code> are in the dimension of the matrix
	 * meaning more formally: <code>(i >= 0 && i < getRowCount()) && (j >= 0 && j < getColumnCount())</code>.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The matrix is zero-based meaning that <code>a(0 0)</code> is the top left element 
	 * and <code>a(n-1 m-1)</code> is the bottom right element of the matrix <code>a</code>.
	 * 
	 * @param i the row index
	 * @param j the column index
	 * @return <code>true</code> if the indices are in the dimension of the matrix otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean isInDimension(final int i, final int j) {
		return (i >= 0 && i < n) && (j >= 0 && j < m);
	}
	
	/**
	 * Indicates whether it is a square matrix.
	 * 
	 * @return <code>true</code> if the matrix is square otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean isSquare() {
		return (n == m);
	}
	
	/**
	 * Clones the matrix meaning that it is returned a shallow copy of <code>this</code> {@link Matrix} instance
	 * (the elements themselves are not copied/cloned).
	 * 
	 * @return a new instance of the matrix with the same elements as <code>this</code> matrix
	 * @since 1.0.0
	 */
	public abstract Matrix<T> clone();
	
	/**
	 * Indicates whether this matrix equals the specified matrix.
	 * 
	 * @param matrix the matrix to be compared with
	 * @return <code>true</code> if both matrices are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	@Override
	public boolean equals(Object matrix) {
		if(!(matrix instanceof Matrix))
			return false;
		else
			return equals((Matrix<?>)matrix);
	}
	
	/**
	 * Indicates whether this matrix equals the specified matrix.
	 * 
	 * @param matrix the matrix to be compared with
	 * @return <code>true</code> if both matrices are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean equals(Matrix<?> matrix) {
		return (matrix != null) ? this.a.equals(matrix.a) : false;
	}
	
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		T o;
		
		s.append("{");
		
		for(int i = 0; i < n; i++) {
			if(i > 0)
				s.append(",");
			s.append("{");
			
			for(int j = 0; j < m; j++) {
				if(j > 0)
					s.append(",");
				
				o = get(i, j);
				if(o != null)
					s.append(o.toString());
			}
			s.append("}");
		}
		
		s.append("}");
		
		return s.toString();
	}
	
	/**
	 * Copies the elements of one matrix (source) to another matrix (destination).
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The dimension of the source matrix must not conform to the dimension of the destination matrix.
	 * 
	 * @param source the source matrix whose elements should be copied
	 * @param dest the destination matrix
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if source is null</li>
	 * 		<li>if dest is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <T> void copy(final Matrix<T> source, final Matrix<T> dest) throws IllegalArgumentException {
		if(source == null || dest == null)
			throw new IllegalArgumentException("No valid argument!");
		
		for(int i = 0; i < dest.getRowCount(); i++) {
			for(int j = 0; j < dest.getColumnCount(); j++) {
				if(source.isInDimension(i, j))
					dest.set(i, j, source.get(i, j));
			}
		}
	}

}
