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
 * Class:		MatrixEditor
 * Task:		Display and modify a matrix
 * Created:		30.01.14
 * LastChanges:	08.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.Transient;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import lavesdk.graphics.CatmullRomSpline;
import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.gui.widgets.Mask.Type;
import lavesdk.math.Matrix;
import lavesdk.math.ObjectMatrix;
import lavesdk.utils.MathUtils;

/**
 * A matrix editor to display and modify matrices.
 * <br><br>
 * To change the matrix that is displayed use {@link #setMatrix(Matrix)}. If the matrix editor is in edit mode ({@link #setEditable(boolean)})
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
 * matrixEditor.addMask(m);
 * </pre>
 * <b>Labels</b>:<br>
 * You can define labels for rows and columns of the matrix. These labels are displayed at the left side (row labels) or at the top side (column labels) of the matrix.
 * You can use these labels to annotate each row or column. With {@link #setRowLabels(Map)} you set a row label map and with {@link #setColumnLabels(Map)}
 * you set a column label map. Ensure that you activate {@link #setPaintLabels(boolean)} to display the labels.
 * <br><br>
 * <b>Strikeouts</b>:<br>
 * It is possible to strike out rows and columns as a visual effect. This is done by defining a strikeout that consists of an index which describes
 * the index of the row or column that should be striked out, a color and a line width. Use {@link #addRowStrikeout(Strikeout)} and {@link #addColumnStrikeout(Strikeout)}
 * to add new strikeouts or to change a strikeout for a specific index. You can remove them by using either {@link #removeRowStrikeout(int)}/{@link #removeColumnStrikeout(int)}
 * or {@link #removeLastRowStrikeout()}/{@link #removeLastColumnStrikeout()}.
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 * @param <T> the type of a matrix element
 */
public class MatrixEditor<T> extends BaseComponent {

	private static final long serialVersionUID = 1L;
	
	/** the matrix that should be displayed or <code>null</code> if there is no matrix to display */
	private Matrix<T> matrix;
	/** the matrix mask or <code>null</code> if {@link #matrix} is <code>null</code> (an element is either a {@link Mask} or <code>null</code> if the element should not be masked) */
	private ObjectMatrix<Mask> matrixMask;
	/** the element format */
	private final MatrixElementFormat<T> elementFormat;
	/** the masks of the matrix editor */
	private final List<Mask> masks;
	/** the event controller */
	private final EventController eventController;
	/** a matrix that contains the background colors of the matrix elements or <code>null</code> if {@link #matrix} is <code>null</code> */
	private ObjectMatrix<Color> elementBackgrounds;
	/** a matrix that contains the foreground colors of the matrix elements or <code>null</code> if {@link #matrix} is <code>null</code> */
	private ObjectMatrix<Color> elementForegrounds;
	/** the spacing between two columns */
	private int columnSpacing;
	/** the spacing between two rows */
	private int rowSpacing;
	/** flag that indicates whether the matrix in the editor is editable or not */
	private boolean editable;
	/** the panel that displays the matrix */
	private final MatrixDrawingPanel matrixPanel;
	/** the scroll pane that encloses the table */
	private final JScrollPane scrollPane;
	/** flag that indicates whether the matrix editor is initialized */
	private boolean initialized;
	/** the mapping between a row index (key) and a row label (value) or <code>null</code> if there is no row label map */
	private Map<Integer, String> rowLabels;
	/** the mapping between a column index (key) and a column label (value) or <code>null</code> if there is no column label map */
	private Map<Integer, String> columnLabels;
	/** flag that indicates whether the editor should display the specified row and column labels or not */
	private boolean paintLabels;
	/** the width for each column of the matrix */
	private int[] columnWidths;
	/** the height of each row of the matrix (including the row spacing) */
	private int rowHeight;
	/** the width of the row labels area */
	private int rowLabelsWidth;
	/** the aggregate width of all matrix columns */
	private int columnsWidth;
	/** the height of the font */
	private int rowFontHeight;
	/** the relative distance from the top of a character to its display point that represents the font baseline */
	private int rowFontBaseline;
	/** the dimension of the largest mask icon */
	private final Dimension largestMaskIcon;
	/** the spline of the left matrix parenthesis */
	private CatmullRomSpline leftParenthesisSpline;
	/** the spline of the right matrix parenthesis */
	private CatmullRomSpline rightParenthesisSpline;
	/** flag that indicates whether the row height has to be recalculated */
	private boolean calcRowHeight;
	/** flag that indicates whether the column widths have to be recalculated */
	private boolean calcColumnWidths;
	/** flag that indicates whether the row labels area width has to be recalculated */
	private boolean calcRowLabelsWidth;
	/** the font of the matrix labels */
	private Font labelFont;
	/** the stroke for the parenthesis splines */
	private final Stroke parenthesisSplineStroke;
	/** the selection background color of an element */
	private final Color selBackground;
	/** the selection foreground color of an element */
	private final Color selForeground;
	/** the selected element or <code>null</code> if no element is selected */
	private ElementPosition selElement;
	/** the last time a key typed event occurred */
	private long lastKeyTyped;
	/** the aggregated text that is typed by the user ({@link #keyTyped(KeyEvent)}) */
	private String keyTypedString;
	/** the last selected element that was changed using the key board or <code>null</code> */
	private ElementPosition keyTypedSelElem;
	/** the strikeouts of the rows */
	private final List<Strikeout> rowStrikeouts;
	/** the strikeouts of the columns */
	private final List<Strikeout> columnStrikeouts;
	/** flag that indicates whether auto repaint is enabled for the matrix editor */ 
	private boolean autoRepaint;
	
	/** the default background color of matrix elements */
	private static final Color DEF_BACKGROUND = Color.white;
	/** the default foreground color of matrix elements */
	private static final Color DEF_FOREGROUND = Color.black;
	/** the default parenthesis width */
	private static final int PARENTHESISWIDTH = 2;
	/** the width of the area that displays the parenthesis */
	private static final int PARENTHESISAREA_WIDTH = 10;
	/** the padding around the matrix meaning the distance to the border of the editor */
	private static final int PADDING = 4;
	/** the default spacing between to columns */
	private static final int DEF_COLUMN_SPACING = 4;
	/** the default spacing between to rows */
	private static final int DEF_ROW_SPACING = 2;
	
	/**
	 * Creates a new matrix editor.
	 * 
	 * @param format the format of the matrix elements
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if format is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public MatrixEditor(final MatrixElementFormat<T> format) throws IllegalArgumentException {
		this(format, null);
	}
	
	/**
	 * Creates a new matrix editor.
	 * 
	 * @param format the format of the matrix elements
	 * @param matrix the matrix to display or <code>null</code> if no matrix should be displayed initially
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if format is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public MatrixEditor(final MatrixElementFormat<T> format, final Matrix<T> matrix) throws IllegalArgumentException {
		if(format == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.initialized = false;
		this.elementFormat = format;
		this.matrixPanel = new MatrixDrawingPanel();
		this.scrollPane = new JScrollPane(matrixPanel);
		this.masks = new ArrayList<Mask>();
		this.eventController = new EventController();
		this.rowLabels = null;
		this.columnLabels = null;
		this.paintLabels = false;
		this.columnSpacing = DEF_COLUMN_SPACING;
		this.rowSpacing = DEF_ROW_SPACING;
		this.editable = true;
		this.columnWidths = null;
		this.rowHeight = 0;
		this.rowLabelsWidth = 0;
		this.columnsWidth = 0;
		this.rowFontHeight = 0;
		this.rowFontBaseline = 0;
		this.largestMaskIcon = new Dimension();
		this.leftParenthesisSpline = null;
		this.rightParenthesisSpline = null;
		this.calcColumnWidths = true;
		this.calcRowHeight = true;
		this.calcRowLabelsWidth = true;
		this.parenthesisSplineStroke = new BasicStroke(PARENTHESISWIDTH);
		this.selBackground = SystemColor.textHighlight;
		this.selForeground = SystemColor.textHighlightText;
		this.selElement = null;
		this.lastKeyTyped = 0;
		this.keyTypedString = "";
		this.keyTypedSelElem = null;
		this.rowStrikeouts = new ArrayList<Strikeout>();
		this.columnStrikeouts = new ArrayList<Strikeout>();
		this.autoRepaint = false;
		
		// set the matrix and initialize the dependent objects
		setMatrix(matrix);
		
		// initialize the fonts (important: otherwise the label font is not valid)
		internalSetFont(UIManager.getFont("Label.font"));
		
		// the editor is focusable to handle keyboard input
		setFocusable(true);
		
		super.setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		
		// the scroll pane should not display a border
		scrollPane.setBackground(DEF_BACKGROUND);
		matrixPanel.setBackground(DEF_BACKGROUND);
		
		// add the listeners
		addKeyListener(eventController);
		matrixPanel.addMouseListener(eventController);
		matrixPanel.addMouseMotionListener(eventController);
		
		initialized = true;
	}
	
	/**
	 * Indicates whether auto repaint is enabled.
	 * 
	 * @return <code>true</code> if auto repaint is enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getAutoRepaint() {
		return autoRepaint;
	}
	
	/**
	 * Sets whether auto repaint is enabled.
	 * <br><br>
	 * If you enable this option the matrix editor invokes {@link #repaint()} automatically if a method is invoked that is marked as
	 * auto repaintable.
	 * 
	 * @param autoRepaint <code>true</code> if auto repaint should be enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setAutoRepaint(final boolean autoRepaint) {
		this.autoRepaint = autoRepaint;
	}
	
	/**
	 * Indicates whether the matrix is editable or not.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the editor is in editable mode otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isEditable() {
		if(EDT.isExecutedInEDT())
			return editable;
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return editable;
				}
			});
	}
	
	/**
	 * Sets whether the matrix is editable or not.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param editable <code>true</code> if the editor should be set in editable mode otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setEditable(final boolean editable) {
		if(EDT.isExecutedInEDT())
			this.editable = editable;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setEditable") {
				@Override
				protected void execute() throws Throwable {
					MatrixEditor.this.editable = editable;
				}
			});
		
		// release the selected element if the editor is not editable any more
		if(!editable)
			clearSelection();
	}
	
	/**
	 * Clears the selection in the matrix editor.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void clearSelection() {
		if(EDT.isExecutedInEDT())
			selElement = null;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".clearSelection") {
				@Override
				protected void execute() throws Throwable {
					selElement = null;
				}
			});
		
		// repaint() is thread-safe
		matrixPanel.repaint();
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
		if(EDT.isExecutedInEDT())
			return matrix;
		else
			return EDT.execute(new GuiRequest<Matrix<T>>() {
				@Override
				protected Matrix<T> execute() throws Throwable {
					return matrix;
				}
			});
	}
	
	/**
	 * Sets the matrix that should be displayed.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The matrix is cloned meaning that modifications to the specified matrix do not have an effect on the display of
	 * the matrix. Therefore you have to set the modified matrix to visualize the changes.
	 * <br><br>
	 * The matrix editor is automatically repainted using {@link #repaint()}.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param matrix the matrix to display or <code>null</code>
	 * @since 1.0
	 */
	public final void setMatrix(final Matrix<T> matrix) {
		if(EDT.isExecutedInEDT())
			internalSetMatrix(matrix);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setMatrix") {
				@Override
				protected void execute() throws Throwable {
					internalSetMatrix(matrix);
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
	}
	
	/**
	 * Gets the number of masks that are defined for the matrix editor.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of masks
	 * @since 1.0
	 */
	public int getMaskCount() {
		if(EDT.isExecutedInEDT())
			return masks.size();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return masks.size();
				}
			});
	}
	
	/**
	 * Gets a mask at a given index.
	 * <br><br>
	 * With masks you can replace objects by other objects or icons. For example you can define that the matrix elements with the number
	 * <code>12345</code> should be masked with the <i>infinity</i> symbol. Therefore you create a new mask and add it to the matrix editor like:
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
		if(EDT.isExecutedInEDT())
			return masks.get(index);
		else
			return EDT.execute(new GuiRequest<Mask>() {
				@Override
				protected Mask execute() throws Throwable {
					return masks.get(index);
				}
			});
	}
	
	/**
	 * Adds a new mask to the matrix editor.
	 * <br><br>
	 * With masks you can replace objects by other objects or icons. For example you can define that the matrix elements with the number
	 * <code>12345</code> should be masked with the <i>infinity</i> symbol. Therefore you create a new mask and add it to the matrix editor like:
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
		if(mask == null || masks.contains(mask))
			return;
		
		if(EDT.isExecutedInEDT()) {
			masks.add(mask);
			updateMatrixMask();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".addMask") {
				@Override
				protected void execute() throws Throwable {
					masks.add(mask);
					updateMatrixMask();
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
	}
	
	/**
	 * Removes the mask from the matrix editor.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param mask the mask that should be removed
	 * @since 1.0
	 */
	public void removeMask(final Mask mask) {
		if(mask == null || !masks.contains(mask))
			return;
		
		if(EDT.isExecutedInEDT())
			masks.remove(mask);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeMask") {
				@Override
				protected void execute() throws Throwable {
					masks.remove(mask);
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT())
			return (elementBackgrounds != null && elementBackgrounds.isInDimension(i, j)) ? elementBackgrounds.get(i, j) : DEF_BACKGROUND;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return (elementBackgrounds != null && elementBackgrounds.isInDimension(i, j)) ? elementBackgrounds.get(i, j) : DEF_BACKGROUND;
				}
			});
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
		if(EDT.isExecutedInEDT()) {
			if(elementBackgrounds != null) elementBackgrounds.set(i, j, color);
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setElementBackground") {
				@Override
				protected void execute() throws Throwable {
					if(elementBackgrounds != null) elementBackgrounds.set(i, j, color);
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT())
			return (elementForegrounds != null && elementForegrounds.isInDimension(i, j)) ? elementForegrounds.get(i, j) : DEF_FOREGROUND;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return (elementForegrounds != null && elementForegrounds.isInDimension(i, j)) ? elementForegrounds.get(i, j) : DEF_FOREGROUND;
				}
			});
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
		if(EDT.isExecutedInEDT()) {
			if(elementForegrounds != null) elementForegrounds.set(i, j, color);
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setElementForeground") {
				@Override
				protected void execute() throws Throwable {
					if(elementForegrounds != null) elementForegrounds.set(i, j, color);
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT())
			return columnSpacing;
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return columnSpacing;
				}
			});
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
		if(spacing < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT()) {
			columnSpacing = spacing;
			calcColumnWidths = true;
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setColumnSpacing") {
				@Override
				protected void execute() throws Throwable {
					columnSpacing = spacing;
					calcColumnWidths = true;
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT())
			return rowSpacing;
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return rowSpacing;
				}
			});
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
		if(spacing < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT()) {
			rowSpacing = spacing;
			calcRowHeight = true;
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setRowSpacing") {
				@Override
				protected void execute() throws Throwable {
					rowSpacing = spacing;
					calcRowHeight = true;
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
	}
	
	/**
	 * Indicates whether the matrix editor displays row and column labels.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @return <code>true</code> if row and column labels are displayed otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isPaintLabels() {
		if(EDT.isExecutedInEDT())
			return paintLabels;
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return paintLabels;
				}
			});
	}
	
	/**
	 * Sets whether the matrix editor should display row and column labels.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param paint <code>true</code> if row and column labels should be displayed otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setPaintLabels(final boolean paint) {
		if(EDT.isExecutedInEDT())
			paintLabels = paint;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setPaintLabels") {
				@Override
				protected void execute() throws Throwable {
					paintLabels = paint;
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
	}
	
	/**
	 * Sets the row labels of the matrix editor.
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
		if(EDT.isExecutedInEDT()) {
			rowLabels = labels;
			calcRowLabelsWidth = true;
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setRowLabels") {
				@Override
				protected void execute() throws Throwable {
					rowLabels = labels;
					calcRowLabelsWidth = true;
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
	}
	
	/**
	 * Sets the column labels of the matrix editor.
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
		if(EDT.isExecutedInEDT()) {
			columnLabels = labels;
			calcColumnWidths = true;
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setColumnLabels") {
				@Override
				protected void execute() throws Throwable {
					columnLabels = labels;
					calcColumnWidths = true;
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT())
			addStrikeout(rowStrikeouts, strikeout);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".addRowStrikeout") {
				@Override
				protected void execute() throws Throwable {
					addStrikeout(rowStrikeouts, strikeout);
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT())
			removeStrikeout(rowStrikeouts, rowIndex);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeRowStrikeout") {
				@Override
				protected void execute() throws Throwable {
					removeStrikeout(rowStrikeouts, rowIndex);
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT())
			removeLastStrikeout(rowStrikeouts);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeLastRowStrikeout") {
				@Override
				protected void execute() throws Throwable {
					removeLastStrikeout(rowStrikeouts);
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT())
			rowStrikeouts.clear();
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeAllRowStrikeouts") {
				@Override
				protected void execute() throws Throwable {
					rowStrikeouts.clear();
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT())
			addStrikeout(columnStrikeouts, strikeout);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".addColumnStrikeout") {
				@Override
				protected void execute() throws Throwable {
					addStrikeout(columnStrikeouts, strikeout);
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT())
			removeStrikeout(columnStrikeouts, columnIndex);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeColumnStrikeout") {
				@Override
				protected void execute() throws Throwable {
					removeStrikeout(columnStrikeouts, columnIndex);
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT())
			removeLastStrikeout(columnStrikeouts);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeLastColumnStrikeout") {
				@Override
				protected void execute() throws Throwable {
					removeLastStrikeout(columnStrikeouts);
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT())
			columnStrikeouts.clear();
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeAllColumnStrikeouts") {
				@Override
				protected void execute() throws Throwable {
					columnStrikeouts.clear();
				}
			});
		
		// repaint() is thread-safe
		if(autoRepaint)
			repaint();
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
		if(EDT.isExecutedInEDT()) {
			recomputeSize();
			return rowHeight;
		}
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					recomputeSize();
					return rowHeight;
				}
			});
	}
	
	/**
	 * Sets the font of the matrix editor which is used to render the matrix elements.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param f the font
	 * @since 1.0.0
	 */
	@Override
	public void setFont(final Font f) {
		if(EDT.isExecutedInEDT())
			internalSetFont(f);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setFont") {
				@Override
				protected void execute() throws Throwable {
					internalSetFont(f);
				}
			});
	}
	
	/**
	 * The layout of a matrix editor may not be changed meaning this method does nothing!
	 * 
	 * @param mgr the layout manager
	 * @since 1.0
	 */
	@Override
	public void setLayout(LayoutManager mgr) {
		// this is not allowed
	}
	
	/**
	 * The components of the matrix editor cannot be removed.
	 * 
	 * @since 1.0
	 */
	@Override
	public void removeAll() {
		// this is not allowed
	}
	
	/**
	 * Gets the preferred size of the matrix editor.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the preferred size
	 * @since 1.0
	 */
	@Override
	@Transient
	public Dimension getPreferredSize() {
		if(EDT.isExecutedInEDT()) {
			recomputeSize();
			return scrollPane.getPreferredSize();
		}
		else
			return EDT.execute(new GuiRequest<Dimension>() {
				@Override
				protected Dimension execute() throws Throwable {
					recomputeSize();
					return scrollPane.getPreferredSize();
				}
			});
	}
	
	@Override
	protected void addImpl(Component component, Object constraints, int index) {
		if(!initialized)
			super.addImpl(component, constraints, index);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void repaintComponent() {
		super.repaintComponent();
		matrixPanel.repaint();
	}
	
	/**
	 * Key released event when user releases a key on the keyboard.
	 * 
	 * @param e the key event
	 * @since 1.0
	 */
	private void keyReleased(final KeyEvent e) {
		if(editable == false)
			return;
		
		// if currently there is no element selected or no matrix displayed then the user cannot use the
		// arrow keys to navigate
		if(selElement == null || matrix == null)
			return;
		
		if(e.getKeyCode() == KeyEvent.VK_UP)
			selElement = new ElementPosition(Math.max(selElement.rowIndex - 1, 0), selElement.colIndex);
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
			selElement = new ElementPosition(Math.min(selElement.rowIndex + 1, matrix.getRowCount() - 1), selElement.colIndex);
		else if(e.getKeyCode() == KeyEvent.VK_LEFT)
			selElement = new ElementPosition(selElement.rowIndex, Math.max(selElement.colIndex - 1, 0));
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			selElement = new ElementPosition(selElement.rowIndex, Math.min(selElement.colIndex + 1, matrix.getColumnCount() - 1));
		else if(e.getKeyCode() == KeyEvent.VK_DELETE && e.getModifiers() == 0) {
			// delete the selected element meaning that its value is null
			if(matrix.isInDimension(selElement.rowIndex, selElement.colIndex)) {
				matrix.set(selElement.rowIndex, selElement.colIndex, null);
				// a matrix element changed so update the mask of the matrix
				updateMatrixMask();
				// set the recalculation flag
				calcColumnWidths = true;
			}
		}
		
		// selection changed so repaint the matrix
		matrixPanel.repaint();
	}
	
	/**
	 * Key typed event when user types something on the keyboard.
	 * 
	 * @param e the key event
	 * @since 1.0
	 */
	private void keyTyped(final KeyEvent e) {
		if(editable == false)
			return;
		
		// if there is no element selected that could be modified or if there is not valid matrix then break up!
		if(selElement == null || matrix == null)
			return;
		
		// accept only writable keys
		final int keyChar = e.getKeyChar();
		if(keyChar < 32 || keyChar == 126)
			return;
		
		// extend the string or start a new one?
		// if the user waits to long a new string should start just as the selection changed
		keyTypedString = (e.getWhen() - lastKeyTyped <= 500 && selElement == keyTypedSelElem) ? keyTypedString + e.getKeyChar() : "" + e.getKeyChar();
		
		// set the element if it is valid
		final T modElement = elementFormat.parse(keyTypedString);
		if(modElement != null && matrix.isInDimension(selElement.rowIndex, selElement.colIndex)) {
			matrix.set(selElement.rowIndex, selElement.colIndex, modElement);
			// a matrix element changed so update the mask of the matrix
			updateMatrixMask();
		}
		
		keyTypedSelElem = selElement;
		lastKeyTyped = e.getWhen();
		
		// set the recalculation flag
		calcColumnWidths = true;
		
		// repaint the matrix if necessary
		matrixPanel.repaint();
	}
	
	/**
	 * Mouse down event when user presses the mouse on the drawing panel.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseDown(final MouseEvent e) {
		// mouse is pressed onto the drawing panel then set the focus to the editor to activate keyboard input
		requestFocus();
		
		if(editable == false)
			return;
		
		// select the element that is under the mouse cursor
		selElement = getElementFromPosition(e.getX(), e.getY());
		
		matrixPanel.repaint();
	}
	
	/**
	 * Mouse move event when user moves the mouse on the drawing panel.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseMove(final MouseEvent e) {
		// if matrix is not editable then do not change the cursor
		if(editable == false)
			return;
		
		// change the cursor to the hand of the mouse is over an element
		if(getElementFromPosition(e.getX(), e.getY()) != null)
			matrixPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		else
			matrixPanel.setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * Mouse exit event when user moves the mouse out of the drawing panel.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseExited(final MouseEvent e) {
		matrixPanel.setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * Gets the element from the given position.
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @return the position of the element in the matrix at the specified coordinate
	 * @since 1.0
	 */
	private ElementPosition getElementFromPosition(final int x, final int y) {
		if(x < getMatrixX() || x > getMatrixX() + getMatrixWidth() || matrix == null || rowHeight < 1)
			return null;
		else {
			final int i = (y - getMatrixY()) / rowHeight;
			
			// no valid row?
			if(i < 0 && i >= matrix.getRowCount())
				return null;
			
			int x1 = getMatrixX();
			int x2;
			
			for(int j = 0; j < columnWidths.length; j++) {
				x2 = x1 + columnWidths[j];
				
				if(x >= x1 && x < x2)
					return new ElementPosition(i, j);
				
				x1 = x2;
			}
		}
		
		return null;
	}
	
	/**
	 * Adds the strikeout to the specified list and removes an old strikeout with an equal index.
	 * 
	 * @param list the list
	 * @param strikeout the strikeout
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if strikeout is null</li>
	 * </ul>
	 * @since 1.0
	 */
	private void addStrikeout(final List<Strikeout> list, final Strikeout strikeout) throws IllegalArgumentException {
		if(strikeout == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final int listIndex = getListIndexOfStrikeout(list, strikeout.getIndex());
		
		// already existing? then remove it first
		if(listIndex >= 0)
			list.remove(listIndex);
		
		// add it to the end of the list
		list.add(strikeout);
	}
	
	/**
	 * Removes the strikeout with the given index.
	 * 
	 * @param list the list of the strikeouts
	 * @param index the index of the strikeout that should be removed
	 * @since 1.0
	 */
	private void removeStrikeout(final List<Strikeout> list, final int index) {
		for(int i = list.size(); i >= 0; i--) {
			if(list.get(i).getIndex() == index) {
				list.remove(i);
				break;
			}
		}
	}
	
	/**
	 * Removes the strikeout that was added as a last.
	 * 
	 * @param list the list
	 * @since 1.0
	 */
	private void removeLastStrikeout(final List<Strikeout> list) {
		if(list.size() > 0)
			list.remove(list.size() - 1);
	}
	
	/**
	 * Gets the list index of the strikeout with the specified index.
	 * 
	 * @param list the list with the strokeouts
	 * @param index the index of the strikeout that is searched
	 * @return the list index of the strikeout with the specified index or <code>-1</code> if this strikeout does not exist
	 * @since 1.0
	 */
	private int getListIndexOfStrikeout(final List<Strikeout> list, final int index) {
		for(int i = 0; i < list.size(); i++)
			if(list.get(i).getIndex() == index)
				return i;
		
		return -1;
	}
	
	/**
	 * Sets the matrix.
	 * <br><br>
	 * This is for internal purposes only.
	 * 
	 * @param matrix the matrix or <code>null</code>
	 * @since 1.0
	 */
	private void internalSetMatrix(final Matrix<T> matrix) {
		if(matrix != null) {
			final boolean changedDim = (this.matrix == null) || (this.matrix.getRowCount() != matrix.getRowCount() || this.matrix.getColumnCount() != matrix.getColumnCount());
			this.matrix = matrix.clone();
			
			// if the dimension of the matrix has changed then we need to recreate the color matrices
			if(changedDim) {
				final ObjectMatrix<Color> oldBGColors = elementBackgrounds;
				final ObjectMatrix<Color> oldFGColors = elementForegrounds;
				elementBackgrounds = new ObjectMatrix<Color>(matrix.getRowCount(), matrix.getColumnCount(), DEF_BACKGROUND);
				elementForegrounds = new ObjectMatrix<Color>(matrix.getRowCount(), matrix.getColumnCount(), DEF_FOREGROUND);
				
				// copy the old colors
				if(oldBGColors != null)
					Matrix.copy(oldBGColors, elementBackgrounds);
				if(oldFGColors != null)
					Matrix.copy(oldFGColors, elementForegrounds);
			}
			
			updateMatrixMask();
		}
		else {
			this.matrix = null;
			matrixMask = null;
			elementBackgrounds = null;
			elementForegrounds = null;
		}
		
		// the selection is reversed
		selElement = null;
		
		// set recalculation flags
		calcColumnWidths = true;
	}
	
	/**
	 * Sets the font of the matrix and the related flags for recalculation.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param f the font
	 * @since 1.0
	 */
	private void internalSetFont(final Font f) {
		if(f == null)
			return;
		
		super.setFont(f);
		
		labelFont = f.deriveFont(Font.BOLD);
		
		// set recalculation flags
		calcRowHeight = true;
		calcRowLabelsWidth = true;
		calcColumnWidths = true;
	}
	
	/**
	 * Recomputes the size of the matrix and of all related values but only if the size changed ({@link #hasSizeChanged()}).
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It is created a temporary graphics context to calculate the values.
	 * 
	 * @since 1.0
	 */
	private void recomputeSize() {
		// recalculate the size if necessary
		if(hasSizeChanged()) {
			final BufferedImage tmpImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
			final Graphics2D tmpG = tmpImg.createGraphics();
			computeValues(tmpG);
		}
	}
	
	/**
	 * Gets the x position of the matrix.
	 * 
	 * @return the x position of the matrix's top left corner
	 * @since 1.0
	 */
	private int getMatrixX() {
		return rowLabelsWidth + PARENTHESISAREA_WIDTH + PADDING;
	}
	
	/**
	 * Gets the y position of the matrix.
	 * 
	 * @return the y position of the matrix's top left corner
	 * @since 1.0
	 */
	private int getMatrixY() {
		return (validLabelMap(columnLabels) ? rowFontHeight : 0) + PADDING;
	}
	
	/**
	 * Gets the width of the matrix.
	 * 
	 * @return the matrix width
	 * @since 1.0
	 */
	private int getMatrixWidth() {
		return columnsWidth;
	}
	
	/**
	 * Gets the complete width of the matrix including the left labels, the matrix width, the parentheses and the padding.
	 * 
	 * @return the complete width
	 * @since 1.0
	 */
	private int getMatrixWidthComplete() {
		return rowLabelsWidth + columnsWidth + 2 * PARENTHESISAREA_WIDTH + 2 * PADDING;
	}
	
	/**
	 * Gets the height of the matrix.
	 * 
	 * @return the matrix height
	 * @since 1.0
	 */
	private int getMatrixHeight() {
		return (matrix != null) ? matrix.getRowCount() * rowHeight : 0;
	}
	
	/**
	 * Gets the complete height of the matrix including the top labels, the matrix height and the padding.
	 * 
	 * @return the complete height
	 * @since 1.0
	 */
	private int getMatrixHeightComplete() {
		return getMatrixHeight() + (validLabelMap(columnLabels) ? rowFontHeight : 0) + 2 * PADDING;
	}
	
	/**
	 * Gets the mask the matches to the given object.
	 * 
	 * @param o the object whose mask is searched
	 * @return the mask that matches to the object or <code>null</code> if no mask matches
	 * @since 1.0
	 */
	private Mask getMask(final Object o) {
		for(Mask m : masks)
			if(m.matches(o))
				return m;
		
		return null;
	}
	
	/**
	 * Gets the label of a specified index.
	 * 
	 * @param labelMap the label map ({@link #rowLabels}, {@link #columnLabels})
	 * @param index the index (row or column index)
	 * @return the label or an empty string if there is no label for the specified index
	 * @since 1.0
	 */
	private String getLabel(final Map<Integer, String> labelMap, final int index) {
		final String label = (labelMap != null) ? labelMap.get(index) : null;
		return (label != null) ? label : "";
	}
	
	/**
	 * Indicates whether the specified label map has labels or not.
	 * 
	 * @param labelMap the label map
	 * @return <code>true</code> if the specified map has labels meaning the map is not <code>null</code> otherwise <code>false</code>
	 * @since 1.0.0
	 */
	private boolean validLabelMap(final Map<Integer, String> labelMap) {
		return labelMap != null;
	}
	
	/**
	 * Updates the mask of the matrix meaning it is checked for each element if there is a {@link Mask} that matches to the element.
	 * <br><br>
	 * Furthermore the dimension of the largest mask icon ({@link #largestMaskIcon}) is determined.
	 * 
	 * @since 1.0
	 */
	private void updateMatrixMask() {
		if(matrix == null)
			return;
		
		Mask mask;
		
		// create a new matrix to store the masks of elements if necessary
		if(matrixMask == null || matrixMask.getRowCount() != matrix.getRowCount() || matrixMask.getColumnCount() != matrix.getColumnCount())
			matrixMask = new ObjectMatrix<Mask>(matrix.getRowCount(), matrix.getColumnCount());
		
		largestMaskIcon.width = largestMaskIcon.height = 0;
		
		// look if there is a mask for each element of the matrix
		for(int i = 0; i < matrix.getRowCount(); i++) {
			for(int j = 0; j < matrix.getColumnCount(); j++) {
				mask = getMask(matrix.get(i, j));
				matrixMask.set(i, j, mask);
				
				if(mask != null && mask.getType() == Type.ICON_MASK) {
					largestMaskIcon.width = Math.max(mask.getMaskIcon().getIconWidth(), largestMaskIcon.width);
					largestMaskIcon.height = Math.max(mask.getMaskIcon().getIconHeight(), largestMaskIcon.height);
				}
			}
		}
	}
	
	/**
	 * Indicates whether the size of the matrix has changed meaning that the matrix editor has to be recalculated
	 * using {@link #computeValues(Graphics2D)}.
	 * 
	 * @return <code>true</code> if the size changed otherwise <code>false</code>
	 * @since 1.0
	 */
	private boolean hasSizeChanged() {
		return calcRowHeight || calcRowLabelsWidth || calcColumnWidths;
	}
	
	/**
	 * Computes the {@link #rowHeight}, {@link #rowLabelsWidth} and {@link #columnWidths} of the matrix editor and
	 * resets the corresponding flags ({@link #calcRowHeight}, {@link #calcRowLabelsWidth}, ...).
	 * 
	 * @param g the graphics context
	 * @since 1.0
	 */
	private void computeValues(final Graphics2D g) {
		// cancel the computations if there is no valid matrix because there is nothing to display
		if(matrix == null)
			return;
		
		// set the font of the matrix editor for string measurement
		g.setFont(getFont());
		
		final FontMetrics elemFM = g.getFontMetrics();
		final FontMetrics labelFM = g.getFontMetrics(labelFont);
		final boolean matrixSizeChanged = hasSizeChanged();
		
		if(calcRowHeight) {
			rowFontBaseline = elemFM.getAscent();
			rowFontHeight = elemFM.getHeight();
			rowHeight = ((rowFontHeight >= largestMaskIcon.height) ? rowFontHeight : largestMaskIcon.height) + rowSpacing;
		}
		
		if(calcRowLabelsWidth) {
			int maxWidth = 0;
			int currWidth;
			
			if(validLabelMap(rowLabels)) {
				// find the broadest row label
				for(int i = 0; i < matrix.getRowCount(); i++) {
					currWidth = labelFM.stringWidth(getLabel(rowLabels, i));
					if(currWidth > maxWidth)
						maxWidth = currWidth;
				}
			}
			
			// update the current row labels width
			rowLabelsWidth = maxWidth + 2;
		}
		
		if(calcColumnWidths) {
			int maxWidth;
			int currWidth;
			
			columnWidths = new int[matrix.getColumnCount()];
			columnsWidth = 0;
			
			// if there are column labels then add the widths of the labels as the initial column width
			if(validLabelMap(columnLabels)) {
				for(int j = 0; j < columnWidths.length; j++)
					columnWidths[j] = labelFM.stringWidth(getLabel(columnLabels, j));
			}
			
			// check all rows for every column and compute their broadest values
			for(int j = 0; j < matrix.getColumnCount(); j++) {
				maxWidth = columnWidths[j];
				
				for(int i = 0; i < matrix.getRowCount(); i++) {
					currWidth = elemFM.stringWidth(elementFormat.format(matrix.get(i, j)));
					if(currWidth > maxWidth)
						maxWidth = currWidth;
				}
				
				if(maxWidth < largestMaskIcon.width)
					maxWidth = largestMaskIcon.width;
				
				columnWidths[j] = maxWidth + columnSpacing;
				columnsWidth += columnWidths[j];
			}
		}
		
		// recalculate the parenthesis splines if necessary
		if(matrixSizeChanged) {
			final int parenthesesY = getMatrixY();
			final int matrixHeight = getMatrixHeight();
			final int lbsX = getMatrixX() - PARENTHESISAREA_WIDTH;
			final int rbsX = getMatrixX() + getMatrixWidth();
			
			leftParenthesisSpline = new CatmullRomSpline(new Point[] {
					new Point(lbsX + PARENTHESISAREA_WIDTH - PARENTHESISWIDTH, parenthesesY),
					new Point(lbsX + PARENTHESISWIDTH, parenthesesY + matrixHeight * 1/5),
					new Point(lbsX + PARENTHESISWIDTH, parenthesesY + matrixHeight * 4/5),
					new Point(lbsX + PARENTHESISAREA_WIDTH - PARENTHESISWIDTH, parenthesesY + matrixHeight)
			});
			rightParenthesisSpline = new CatmullRomSpline(new Point[] {
					new Point(rbsX + PARENTHESISWIDTH, parenthesesY),
					new Point(rbsX + PARENTHESISAREA_WIDTH - PARENTHESISWIDTH, parenthesesY + matrixHeight * 1/5),
					new Point(rbsX + PARENTHESISAREA_WIDTH - PARENTHESISWIDTH, parenthesesY + matrixHeight * 4/5),
					new Point(rbsX + PARENTHESISWIDTH, parenthesesY + matrixHeight)
			});
		}
		
		// reset flags
		calcRowHeight = false;
		calcRowLabelsWidth = false;
		calcColumnWidths = false;
		
		// adjust the drawing panel of the matrix if the size has changed
		if(matrixSizeChanged) {
			matrixPanel.setPreferredSize(new Dimension(getMatrixWidthComplete(), getMatrixHeightComplete()));
			matrixPanel.revalidate();
		}
	}
	
	/**
	 * Paints the matrix.
	 * 
	 * @param g the graphics context
	 * @since 1.0
	 */
	private void paint(Graphics2D g) {
		// there is not matrix to display? then cancel painting
		if(matrix == null)
			return;
		
		// use the current font of the matrix editor as the default font
		g.setFont(getFont());
		
		// recalculate values if necessary
		computeValues(g);
		
		// enable antialiasing to draw the parentheses (interpolation: nearest neighbor (fast), bilinear (slower), bicubic (slowest, but the best))
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		// draw the parentheses
		final Stroke oldStroke = g.getStroke();
		g.setStroke(parenthesisSplineStroke);
		g.draw(leftParenthesisSpline.getPath());
		g.draw(rightParenthesisSpline.getPath());
		g.setStroke(oldStroke);
		
		// disable antialiasing to render the elements
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		
		final FontMetrics elemFM = g.getFontMetrics();
		final FontMetrics labelFM = g.getFontMetrics(labelFont);
		final int boundsX = PADDING;
		final int boundsY = PADDING;
		final int matrixX = boundsX + rowLabelsWidth + PARENTHESISAREA_WIDTH;
		final int firstRowY = boundsY + (validLabelMap(columnLabels) ? rowFontHeight : 0);
		final int[] columnXs = new int[columnWidths.length];
		int currX = matrixX;
		int currY = firstRowY;
		String label;
		Mask mask;
		String elemString;
		Icon elemIcon;
		Color elemBackground;
		Color elemForeground;
		
		for(int i = 0; i < matrix.getRowCount(); i++) {
			// draw the row label if necessary
			label = getLabel(rowLabels, i);
			if(!label.isEmpty()) {
				g.setFont(labelFont);
				g.setColor(DEF_FOREGROUND);
				g.drawString(label, boundsX, currY + rowFontBaseline);
			}
			
			for(int j = 0; j < matrix.getColumnCount(); j++) {
				// if we iterate over the first row then we draw the column labels
				if(i == 0) {
					label = getLabel(columnLabels, j);
					if(!label.isEmpty()) {
						g.setFont(labelFont);
						g.setColor(DEF_FOREGROUND);
						g.drawString(label, currX + (columnWidths[j] - labelFM.stringWidth(label)) / 2, boundsY + rowFontBaseline);
					}
					
					columnXs[j] = currX;
				}
				
				mask = matrixMask.get(i, j);
				elemString = elementFormat.format(matrix.get(i, j));
				elemBackground = elementBackgrounds.get(i, j);
				elemForeground = elementForegrounds.get(i, j);
				
				// if the current element is selected then change the background and foreground color
				if(selElement != null && i == selElement.rowIndex && j == selElement.colIndex) {
					elemBackground = selBackground;
					elemForeground = selForeground;
				}
				
				// draw the background of the element but only when it differs from the matrix background
				if(!elemBackground.equals(DEF_BACKGROUND)) {
					g.setColor(elemBackground);
					g.fillRect(currX, currY, columnWidths[j], rowHeight);
				}
				
				// set the foreground color and the of the element
				g.setColor(elemForeground);
				g.setFont(getFont());
				
				if(mask != null) {
					// draw the mask instead of the element
					switch(mask.getType()) {
						case OBJECT_MASK:
							elemString = mask.getMaskObject().toString();
							break;
						case ICON_MASK:
							// the element string may not be painted
							elemString = "";
							elemIcon = mask.getMaskIcon();
							
							// paint the mask icon in the center of the element's cell
							elemIcon.paintIcon(matrixPanel, g, currX + (columnWidths[j] - elemIcon.getIconWidth()) / 2, currY + (rowHeight - elemIcon.getIconHeight()) / 2);
							break;
					}
				}
				
				// draw the element string (centered) if necessary
				if(!elemString.isEmpty())
					g.drawString(elemString, currX + (columnWidths[j] - elemFM.stringWidth(elemString)) / 2, currY + (rowHeight - rowFontHeight) / 2 + rowFontBaseline);
				
				// set the x position of the next element
				currX += columnWidths[j];
			}
			
			currY += rowHeight;
			currX = matrixX;
		}
		
		final int rowRightX = matrixX + columnsWidth + PARENTHESISAREA_WIDTH;
		int currRowY = firstRowY + rowHeight / 2;
		int currColX;
		
		// draw the strikeouts for the rows and columns
		for(Strikeout s : rowStrikeouts) {
			// row is not in the range
			if(s.getIndex() >= matrix.getRowCount())
				continue;
			
			g.setColor(s.getColor());
			g.setStroke(s.getStroke());
			g.drawLine(boundsX, currRowY + (rowHeight * s.getIndex()) - s.getLineWidth() / 2, rowRightX, currRowY + (rowHeight * s.getIndex()) - s.getLineWidth() / 2);
		}
		for(Strikeout s : columnStrikeouts) {
			// row is not in the range
			if(s.getIndex() >= matrix.getColumnCount())
				continue;
			
			currColX = columnXs[s.getIndex()] + (columnWidths[s.getIndex()] - s.getLineWidth()) / 2;
			
			g.setColor(s.getColor());
			g.setStroke(s.getStroke());
			g.drawLine(currColX, boundsY, currColX, firstRowY + matrix.getRowCount() * rowHeight);
		}
		
		// set the initial stroke
		g.setStroke(oldStroke);
	}
	
	/**
	 * Represents the position of a matrix element.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class ElementPosition {
		
		/** the index of the row of the element */
		public int rowIndex;
		/** the index of the column of the element */
		public int colIndex;
		
		/**
		 * Creates a new element position.
		 * 
		 * @param rowIndex the row index that describes the matrix element
		 * @param colIndex the column index that describes the matrix element
	 * @since 1.0
		 */
		public ElementPosition(final int rowIndex, final int colIndex) {
			this.rowIndex = rowIndex;
			this.colIndex = colIndex;
		}
	}
	
	/**
	 * Panel that displays the matrix.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class MatrixDrawingPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			MatrixEditor.this.paint((Graphics2D)g);
		}
		
	}
	
	/**
	 * Handles the events of the matrix editor.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class EventController implements MouseListener, MouseMotionListener, KeyListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
			MatrixEditor.this.mouseExited(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			MatrixEditor.this.mouseDown(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			MatrixEditor.this.mouseMove(e);
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			MatrixEditor.this.keyReleased(e);
		}

		@Override
		public void keyTyped(KeyEvent e) {
			MatrixEditor.this.keyTyped(e);
		}
	}
	
	/**
	 * Represents a strikeout in the matrix editor.
	 * <br><br>
	 * Strikeouts can be defined for rows and columns of a matrix. You can determine which row or column should be striked out
	 * by specify the <i>index</i> and the <i>color</i> in which the strikeout should be displayed.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public static class Strikeout {
		
		/** the index of the row or column */
		private final int index;
		/** the color */
		private final Color color;
		/** the line width of the strikeout */
		private final int lineWidth;
		/** the stroke that represents the strikeout */
		private final Stroke stroke;
		
		/**
		 * Creates a new strikeout.
		 * 
		 * @param index the index of the row or column
		 * @param color the color in which the strikeout should be displayed
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if index is <code>< 0</code></li>
		 * 		<li>if color is null</li>
		 * </ul>
	 * @since 1.0
		 */
		public Strikeout(final int index, final Color color) throws IllegalArgumentException {
			this(index, color, 1);
		}
		
		/**
		 * Creates a new strikeout.
		 * 
		 * @param index the index of the row or column
		 * @param color the color in which the strikeout should be displayed
		 * @param lineWidth the line width of the strikeout
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if index is <code>< 0</code></li>
		 * 		<li>if color is null</li>
		 * 		<li>if lineWidth is <code>< 1</code></li>
		 * </ul>
	 * @since 1.0
		 */
		public Strikeout(final int index, final Color color, final int lineWidth) throws IllegalArgumentException {
			if(index < 0 || color == null || lineWidth < 1)
				throw new IllegalArgumentException("No valid argument!");
			
			this.index = index;
			this.color = color;
			this.lineWidth = lineWidth;
			this.stroke = new BasicStroke(lineWidth);
		}
		
		/**
		 * Gets the index of the row or column that should be striked out.
		 * 
		 * @return the index of the row or column
		 * @since 1.0
		 */
		public final int getIndex() {
			return index;
		}
		
		/**
		 * The color in which the strikeout should be displayed.
		 * 
		 * @return the color of the strikeout
		 * @since 1.0
		 */
		public final Color getColor() {
			return color;
		}
		
		/**
		 * Gets the line width of the stroke that represents the strikeout.
		 * 
		 * @return the line width
		 * @since 1.0
		 */
		public final int getLineWidth() {
			return lineWidth;
		}
		
		/**
		 * Gets the stroke that represents the strikeout.
		 * 
		 * @return the stroke
		 * @since 1.0
		 */
		Stroke getStroke() {
			return stroke;
		}
		
	}
	
	/**
	 * The element format for {@link String} objects in a matrix.
	 * 
	 * @see NumericElementFormat
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public static class StringElementFormat extends MatrixElementFormat<String> {

		@Override
		public String format(String element) {
			if(element == null)
				return "";
			else
				return element;
		}

		@Override
		public String parse(String element) {
			return element;
		}
		
	}
	
	/**
	 * The element format for {@link Number} objects in a matrix.
	 * 
	 * @see IntegerElementFormat
	 * @see LongElementFormat
	 * @see FloatElementFormat
	 * @see DoubleElementFormat
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 * @param <T> the concrete type
	 */
	public static abstract class NumericElementFormat<T extends Number> extends MatrixElementFormat<T> {
		
		/** the infinity character */
		private final String infinityChar;
		
		/**
		 * Creates a new numeric element format.
		 * <br><br>
		 * It is used "-" as the infinityChar.
		 * 
		 * @since 1.0
		 */
		public NumericElementFormat() {
			this("-");
		}
		
		/**
		 * Creates a new numeric element format.
		 * 
		 * @param infinityChar the character that represents infinity
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if infinityChar is null</li>
		 * 		<li>if infinityChar is empty</li>
		 * </ul>
		 * @since 1.0
		 */
		public NumericElementFormat(final String infinityChar) throws IllegalArgumentException {
			if(infinityChar == null || infinityChar.isEmpty())
				throw new IllegalArgumentException("No valid argument!");
			
			this.infinityChar = infinityChar;
		}

		@Override
		public String format(T element) {
			if(element != null)
				return MathUtils.formatDouble(element.doubleValue());
			else
				return "";
		}
		
		@Override
		public T parse(String element) {
			if(element.equals(infinityChar))
				return getInfinity();
			
			try {
				return parseElement(element);
			} catch (ParseException e) {
				return null;
			}
		}
		
		/**
		 * Gets the value of infinity.
		 * 
		 * @return the value of infinity
		 * @since 1.0
		 */
		protected abstract T getInfinity();
		
		/**
		 * Parses the given element.
		 * 
		 * @param element the text representation of the element
		 * @return the element
		 * @throws ParseException
		 * <ul>
		 * 		<li>if the element could not be parsed</li>
		 * </ul>
		 * @since 1.0
		 */
		protected abstract T parseElement(final String element) throws ParseException;
		
	}
	
	/**
	 * The element format for {@link Integer} objects in a matrix.
	 * <br><br>
	 * It is used {@link Integer#MAX_VALUE} as the infinity value.
	 * 
	 * @see NumericElementFormat
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public static class IntegerElementFormat extends NumericElementFormat<Integer> {
		
		/**
		 * Creates a new integer element format.
		 * <br><br>
		 * It is used "-" as the infinityChar.
		 * 
		 * @since 1.0
		 */
		public IntegerElementFormat() {
			super();
		}
		
		/**
		 * Creates a new integer element format.
		 * 
		 * @param infinityChar the character that represents infinity
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if infinityChar is null</li>
		 * 		<li>if infinityChar is empty</li>
		 * </ul>
		 * @since 1.0
		 */
		public IntegerElementFormat(final String infinityChar) throws IllegalArgumentException {
			super(infinityChar);
		}

		@Override
		protected Integer getInfinity() {
			return Integer.MAX_VALUE;
		}

		@Override
		protected Integer parseElement(String element) throws ParseException {
			return NumberFormat.getInstance().parse(element).intValue();
		}
		
	}
	
	/**
	 * The element format for {@link Long} objects in a matrix.
	 * <br><br>
	 * It is used {@link Long#MAX_VALUE} as the infinity value.
	 * 
	 * @see NumericElementFormat
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public static class LongElementFormat extends NumericElementFormat<Long> {
		
		/**
		 * Creates a new long element format.
		 * <br><br>
		 * It is used "-" as the infinityChar.
		 * 
		 * @since 1.0
		 */
		public LongElementFormat() {
			super();
		}
		
		/**
		 * Creates a new long element format.
		 * 
		 * @param infinityChar the character that represents infinity
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if infinityChar is null</li>
		 * 		<li>if infinityChar is empty</li>
		 * </ul>
		 * @since 1.0
		 */
		public LongElementFormat(final String infinityChar) throws IllegalArgumentException {
			super(infinityChar);
		}

		@Override
		protected Long getInfinity() {
			return Long.MAX_VALUE;
		}

		@Override
		protected Long parseElement(String element) throws ParseException {
			return NumberFormat.getInstance().parse(element).longValue();
		}
		
	}
	
	/**
	 * The element format for {@link Float} objects in a matrix.
	 * <br><br>
	 * It is used {@link Float#POSITIVE_INFINITY} as the infinity value.
	 * 
	 * @see NumericElementFormat
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public static class FloatElementFormat extends NumericElementFormat<Float> {
		
		/**
		 * Creates a new float element format.
		 * <br><br>
		 * It is used "-" as the infinityChar.
		 * 
		 * @since 1.0
		 */
		public FloatElementFormat() {
			super();
		}
		
		/**
		 * Creates a new float element format.
		 * 
		 * @param infinityChar the character that represents infinity
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if infinityChar is null</li>
		 * 		<li>if infinityChar is empty</li>
		 * </ul>
		 * @since 1.0
		 */
		public FloatElementFormat(final String infinityChar) throws IllegalArgumentException {
			super(infinityChar);
		}

		@Override
		protected Float getInfinity() {
			return Float.POSITIVE_INFINITY;
		}

		@Override
		protected Float parseElement(String element) throws ParseException {
			return NumberFormat.getInstance().parse(element).floatValue();
		}
		
	}
	
	/**
	 * The element format for {@link Double} objects in a matrix.
	 * <br><br>
	 * It is used {@link Double#POSITIVE_INFINITY} as the infinity value.
	 * 
	 * @see NumericElementFormat
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public static class DoubleElementFormat extends NumericElementFormat<Double> {
		
		/**
		 * Creates a new double element format.
		 * <br><br>
		 * It is used "-" as the infinityChar.
		 * 
		 * @since 1.0
		 */
		public DoubleElementFormat() {
			super();
		}
		
		/**
		 * Creates a new double element format.
		 * 
		 * @param infinityChar the character that represents infinity
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if infinityChar is null</li>
		 * 		<li>if infinityChar is empty</li>
		 * </ul>
		 * @since 1.0
		 */
		public DoubleElementFormat(final String infinityChar) throws IllegalArgumentException {
			super(infinityChar);
		}

		@Override
		protected Double getInfinity() {
			return Double.POSITIVE_INFINITY;
		}

		@Override
		protected Double parseElement(String element) throws ParseException {
			return NumberFormat.getInstance().parse(element).doubleValue();
		}
		
	}

}
