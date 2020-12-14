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
 * Class:		MatrixView
 * Task:		View to display a matrix
 * Created:		29.01.14
 * LastChanges:	08.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Map;

import lavesdk.configuration.Configuration;
import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.widgets.Mask;
import lavesdk.gui.widgets.MatrixEditor;
import lavesdk.gui.widgets.MatrixElementFormat;
import lavesdk.gui.widgets.MatrixEditor.FloatElementFormat;
import lavesdk.gui.widgets.MatrixEditor.IntegerElementFormat;
import lavesdk.gui.widgets.MatrixEditor.NumericElementFormat;
import lavesdk.gui.widgets.MatrixEditor.Strikeout;
import lavesdk.gui.widgets.MatrixEditor.StringElementFormat;
import lavesdk.language.LanguageFile;
import lavesdk.math.Matrix;
import lavesdk.resources.Resources;

/**
 * A matrix view to display and modify matrices.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #MatrixView(String, MatrixElementFormat, boolean, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * <br><br>
 * To change the matrix that is displayed use {@link #setMatrix(Matrix)}. If the matrix view is in edit mode ({@link #setEditable(boolean)})
 * the user can select matrix elements and he can modify them using the keyboard. You can retrieve a modified matrix with {@link #getMatrix()}.
 * <br><br>
 * Furthermore you can change the background and the foreground color of each element by using {@link #setElementBackground(int, int, Color)}/{@link #setElementForeground(int, int, Color)}.
 * <br><br>
 * <b>Element format</b>:<br>
 * The {@link MatrixElementFormat} is responsible for how an object is formatted in the matrix. Additionally it is used to parse a user input string
 * into a concrete element object if the user modifies an element. The matrix editor provides implementations for several types like
 * {@link StringElementFormat}, {@link NumericElementFormat}, {@link IntegerElementFormat}, {@link FloatElementFormat}, ...
 * <br><br>
 * <b>Masks</b>:<br>
 * You can define masks for objects in the matrix. This is useful if you want to display another object or an icon instead of a specific object or value.
 * <u>Example</u>:<br> Your integer matrix contains <code>Integer.MAX_VALUE</code> to identify elements as infinity.<br>
 * To give the matrix a better look and feel you can specify a mask that replaces all of the <code>Integer.MAX_VALUE</code> objects with an infinity symbol:
 * <pre>
 * // set the matrix ...
 * 
 * // create the mask
 * final Mask m = new Mask(Integer.MAX_VALUE, Symbol.getPredefinedSymbol(PredefinedSymbol.INFINITY));
 * // add the mask to the matrix editor
 * matrixView.addMask(m);
 * </pre>
 * <b>Labels</b>:<br>
 * You can define labels for rows and columns of the matrix. These labels are displayed at the left side (row labels) or at the top side (column labels) of the matrix.
 * You can use these labels to annotate each row or column. With {@link #setRowLabels(Map)} you set a row label map and with {@link #setColumnLabels(Map)}
 * you set a column label map. Ensure that you activate {@link #setPaintLabels(boolean)} to display the labels.
 * <br><br>
 * <b>Strikeouts</b>:<br>
 * It is possible to strike out rows and columns as a visual effect. This is done by defining a strikeout that consists of an index which describes
 * the index of the row or column that should be striked out, a color and a line width. Use {@link #addRowStrikeout(MatrixEditor.Strikeout)} and {@link #addColumnStrikeout(MatrixEditor.Strikeout)}
 * to add new strikeouts or to change a strikeout for a specific index. You can remove them by using either {@link #removeRowStrikeout(int)}/{@link #removeColumnStrikeout(int)}
 * or {@link #removeLastRowStrikeout()}/{@link #removeLastColumnStrikeout()}.
 * <br><br>
 * <b>Save and load the configuration</b>:<br>
 * You can save and load a configuration of the matrix view by using {@link #saveConfiguration(Configuration)} and {@link #loadConfiguration(Configuration)}.
 * It is saved or restored the visibility of the view. This makes it possible that you can store the state of the view persistent.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <T> the type of a matrix element
 */
public class MatrixView<T> extends View {
	
	private static final long serialVersionUID = 1L;
	
	/** the matrix view */
	private final MatrixEditor<T> matrixEditor;

	/**
	 * Creates a new matrix view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param elementFormat the format of the matrix elements (like {@link StringElementFormat}, {@link NumericElementFormat} and so on of the {@link MatrixEditor})
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public MatrixView(final String title, final MatrixElementFormat<T> elementFormat) throws IllegalArgumentException {
		this(title, elementFormat, true);
	}

	/**
	 * Creates a new matrix view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param elementFormat the format of the matrix elements (like {@link StringElementFormat}, {@link NumericElementFormat} and so on of the {@link MatrixEditor})
	 * @param closable <code>true</code> if the matrix view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a matrix view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public MatrixView(final String title, final MatrixElementFormat<T> elementFormat, final boolean closable) throws IllegalArgumentException {
		this(title, elementFormat, closable, null, null);
	}
	
	/**
	 * Creates a new matrix view.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the matrix view. The following language labels are available:
	 * <ul>
	 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
	 * </ul>
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param elementFormat the format of the matrix elements (like {@link StringElementFormat}, {@link NumericElementFormat} and so on of the {@link MatrixEditor})
	 * @param closable <code>true</code> if the matrix view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a matrix view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langFile the language file or <code>null</code> if the matrix view should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public MatrixView(final String title, final MatrixElementFormat<T> elementFormat, final boolean closable, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		super(title, closable, langFile, langID);
		
		matrixEditor = new MatrixEditor<T>(elementFormat);
		matrixEditor.setEditable(false);
		
		// create a header bar extension for increasing/decreasing/normalizing the font of the matrix
		new FontHeaderBarExtension(this, matrixEditor.getFont().getSize2D(), closable, langFile, langID);
		
		content.setLayout(new BorderLayout());
		content.add(matrixEditor, BorderLayout.CENTER);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getAutoRepaint() {
		return matrixEditor.getAutoRepaint();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAutoRepaint(boolean autoRepaint) {
		matrixEditor.setAutoRepaint(autoRepaint);
	}
	
	/**
	 * Resets the matrix view meaning the matrix is set to <code>null</code>, the labels are cleared and the paint labels flag is reset
	 * to <code>false</code> and all strikeouts (of rows and columns) are removed.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #setMatrix(Matrix)
	 * @since 1.0
	 */
	@Override
	public void reset() {
		matrixEditor.setMatrix(null);
		matrixEditor.setColumnLabels(null);
		matrixEditor.setRowLabels(null);
		matrixEditor.setPaintLabels(false);
		matrixEditor.removeAllColumnStrikeouts();
		matrixEditor.removeAllRowStrikeouts();
	}
	
	/**
	 * Indicates whether the matrix is editable or not.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the view is in editable mode otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isEditable() {
		return matrixEditor.isEditable();
	}
	
	/**
	 * Sets whether the matrix is editable or not.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param editable <code>true</code> if the view should be set in editable mode otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setEditable(final boolean editable) {
		matrixEditor.setEditable(editable);
	}
	
	/**
	 * Clears the selection in the matrix view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void clearSelection() {
		matrixEditor.clearSelection();
	}
	
	/**
	 * Gets the matrix that is displayed.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the matrix or <code>null</code> if currently no matrix is displayed
	 * @since 1.0
	 */
	public final Matrix<T> getMatrix() {
		return matrixEditor.getMatrix();
	}
	
	/**
	 * Sets the matrix that should be displayed.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The matrix is cloned meaning that modifications to the specified matrix do not have an effect on the display of
	 * the matrix. Therefore you have to set the modified matrix to visualize the changes.
	 * <br><br>
	 * The matrix view is automatically repainted using {@link #repaint()}.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param matrix the matrix to display or <code>null</code>
	 * @since 1.0
	 */
	public final void setMatrix(final Matrix<T> matrix) {
		matrixEditor.setMatrix(matrix);
	}
	
	/**
	 * Gets the number of masks that are defined for the matrix view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of masks
	 * @since 1.0
	 */
	public int getMaskCount() {
		return matrixEditor.getMaskCount();
	}
	
	/**
	 * Gets a mask at a given index.
	 * <br><br>
	 * With masks you can replace objects by other objects or icons. For example you can define that the matrix elements with the number
	 * <code>12345</code> should be masked with the <i>infinity</i> symbol. Therefore you create a new mask and add it to the matrix view like:
	 * <pre>
	 * // create the mask
	 * final Mask infMask = new Mask(12345, Symbol.getPredefinedSymbol(PredefinedSymbol.INFINITY));
	 * // add it to the matrix
	 * matrixTable.addMask(infMask);
	 * </pre>
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the mask
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getMaskCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public Mask getMask(final int index) throws IndexOutOfBoundsException {
		return matrixEditor.getMask(index);
	}
	
	/**
	 * Adds a new mask to the matrix view.
	 * <br><br>
	 * With masks you can replace objects by other objects or icons. For example you can define that the matrix elements with the number
	 * <code>12345</code> should be masked with the <i>infinity</i> symbol. Therefore you create a new mask and add it to the matrix view like:
	 * <pre>
	 * // create the mask
	 * final Mask infMask = new Mask(12345, Symbol.getPredefinedSymbol(PredefinedSymbol.INFINITY));
	 * // add it to the matrix
	 * matrixTable.addMask(infMask);
	 * </pre>
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param mask the mask to be added
	 * @since 1.0
	 */
	public void addMask(final Mask mask) {
		matrixEditor.addMask(mask);
	}
	
	/**
	 * Removes the mask from the matrix view.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param mask the mask that should be removed
	 * @since 1.0
	 */
	public void removeMask(final Mask mask) {
		matrixEditor.removeMask(mask);
	}
	
	/**
	 * Gets the background color of a matrix element.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param i the row index of the element in the matrix
	 * @param j the column index  of the element in the matrix
	 * @return the background color
	 * @since 1.0
	 */
	public Color getElementBackground(final int i, final int j) {
		return matrixEditor.getElementBackground(i, j);
	}
	
	/**
	 * Sets the background color of a matrix element.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It has to be set a valid matrix before you can change an element's background color otherwise this method
	 * has no effect.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param i the row index of the element in the matrix
	 * @param j the column index  of the element in the matrix
	 * @param color the background color
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if i is out of bounds</li>
	 * 		<li>if j is out of bounds</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setElementBackground(final int i, final int j, final Color color) throws IndexOutOfBoundsException {
		matrixEditor.setElementBackground(i, j, color);
	}
	
	/**
	 * Gets the foreground color of a matrix element.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param i the row index of the element in the matrix
	 * @param j the column index of the element in the matrix
	 * @return the foreground color
	 * @since 1.0
	 */
	public Color getElementForeground(final int i, final int j) {
		return matrixEditor.getElementForeground(i, j);
	}
	
	/**
	 * Sets the foreground color of a matrix element.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It has to be set a valid matrix before you can change an element's foreground color otherwise this method
	 * has no effect.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param i the row index of the element in the matrix
	 * @param j the column index  of the element in the matrix
	 * @param color the foreground color
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if i is out of bounds</li>
	 * 		<li>if j is out of bounds</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setElementForeground(final int i, final int j, final Color color) throws IndexOutOfBoundsException {
		matrixEditor.setElementForeground(i, j, color);
	}
	
	/**
	 * Gets the column spacing of the matrix.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the column spacing
	 * @since 1.0
	 */
	public int getColumnSpacing() {
		return matrixEditor.getColumnSpacing();
	}
	
	/**
	 * Sets the column spacing of the matrix.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param spacing the spacing between two columns
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if spacing is <code>< 0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public void setColumnSpacing(final int spacing) throws IllegalArgumentException {
		matrixEditor.setColumnSpacing(spacing);
	}
	
	/**
	 * Gets the row spacing of the matrix.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the row spacing
	 * @since 1.0
	 */
	public int getRowSpacing() {
		return matrixEditor.getRowSpacing();
	}
	
	/**
	 * Sets the row spacing of the matrix.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param spacing the spacing between two rows
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if spacing is <code>< 0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public void setRowSpacing(final int spacing) throws IllegalArgumentException {
		matrixEditor.setRowSpacing(spacing);
	}
	
	/**
	 * Indicates whether the matrix view displays row and column labels.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if row and column labels are displayed otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isPaintLabels() {
		return matrixEditor.isPaintLabels();
	}
	
	/**
	 * Sets whether the matrix view should display row and column labels.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param paint <code>true</code> if row and column labels should be displayed otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setPaintLabels(final boolean paint) {
		matrixEditor.setPaintLabels(paint);
	}
	
	/**
	 * Sets the row labels of the matrix view.
	 * <br><br>
	 * The row labels are displayed at the left side of the matrix next to the left parenthesis.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @see #setPaintLabels(boolean)
	 * @param labels the labels map that maps a row index (key) onto a label text (value)
	 * @since 1.0
	 */
	public void setRowLabels(final Map<Integer, String> labels) {
		matrixEditor.setRowLabels(labels);
	}
	
	/**
	 * Sets the column labels of the matrix view.
	 * <br><br>
	 * The column labels are displayed at the top side of the matrix as a header for each column of the matrix.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @see #setPaintLabels(boolean)
	 * @param labels the labels map that maps a column index (key) onto a label text (value)
	 * @since 1.0
	 */
	public void setColumnLabels(final Map<Integer, String> labels) {
		matrixEditor.setColumnLabels(labels);
	}
	
	/**
	 * Adds a row strikeout.
	 * <br><br>
	 * The strikeout has a color and a line width and its index specifies the <b>index of the row</b> that should be striked out.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param strikeout the strikeout
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if strikeout is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void addRowStrikeout(final Strikeout strikeout) throws IllegalArgumentException {
		matrixEditor.addRowStrikeout(strikeout);
	}
	
	/**
	 * Removes the strikeout for the specified row if there is one.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param rowIndex the index of the row its strikeout should be removed
	 * @since 1.0
	 */
	public void removeRowStrikeout(final int rowIndex) {
		matrixEditor.removeRowStrikeout(rowIndex);
	}
	
	/**
	 * Removes the strikeout of a row that was added as a last.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @since 1.0
	 */
	public void removeLastRowStrikeout() {
		matrixEditor.removeLastRowStrikeout();
	}
	
	/**
	 * Removes all row strikeouts.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllRowStrikeouts() {
		matrixEditor.removeAllRowStrikeouts();
	}
	
	/**
	 * Adds a column strikeout.
	 * <br><br>
	 * The strikeout has a color and a line width and its index specifies the <b>index of the column</b> that should be striked out.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param strikeout the strikeout
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if strikeout is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void addColumnStrikeout(final Strikeout strikeout) throws IllegalArgumentException {
		matrixEditor.addColumnStrikeout(strikeout);
	}
	
	/**
	 * Removes the strikeout for the specified column if there is one.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param columnIndex the index of the column its strikeout should be removed
	 * @since 1.0
	 */
	public void removeColumnStrikeout(final int columnIndex) {
		matrixEditor.removeColumnStrikeout(columnIndex);
	}
	
	/**
	 * Removes the strikeout of a column that was added as a last.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @since 1.0
	 */
	public void removeLastColumnStrikeout() {
		matrixEditor.removeLastColumnStrikeout();
	}
	
	/**
	 * Removes all column strikeouts.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllColumnStrikeouts() {
		matrixEditor.removeAllColumnStrikeouts();
	}
	
	/**
	 * Gets the height of a row in the matrix editor.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The height of the column labels header complies with the row height.
	 * 
	 * @return the height of a row
	 * @since 1.0
	 */
	public int getRowHeight() {
		return matrixEditor.getRowHeight();
	}
	
	/**
	 * Sets the font of the matrix view which is used to render the matrix elements.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param f the font
	 * @since 1.0.0
	 */
	@Override
	public void setFont(final Font f) {
		if(EDT.isExecutedInEDT())
			super.setFont(f);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + "setFont") {
				@Override
				protected void execute() throws Throwable {
					MatrixView.super.setFont(f);
				}
			});
		
		matrixEditor.setFont(f);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRepaintDisabled(boolean disabled) {
		matrixEditor.setRepaintDisabled(disabled);
		super.setRepaintDisabled(disabled);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void repaintComponent() {
		super.repaintComponent();
		matrixEditor.repaint();
	}

}
