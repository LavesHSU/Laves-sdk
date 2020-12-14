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

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;

/**
 * Represents a column in an {@link ExecutionTable}.
 * <br><br>
 * A column has a name that is displayed in the column header of the table and you can specify an identifier of the
 * column that can be used to set data in {@link ExecutionTableItem}s.
 * <br><br>
 * Add a new column to the table by calling {@link ExecutionTable#add(ExecutionTableColumn)}.
 * <br><br>
 * <b>Masks</b>:<br>
 * With masks you can replace objects by other objects or icons. For example you can define that the cells with the content
 * "pi" (or {@link Math#PI}) should be masked with the <i>pi</i> symbol. Therefore you create a new mask and add it to the column like:
 * <pre>
 * // create the mask
 * final Mask piMask = new Mask("pi", Symbol.getPredefinedSymbol(PredefinedSymbol.PI));
 * // add it to the column
 * column.addMask(piMask);
 * </pre>
 * <b>Edit cells</b>:<br>
 * By default no cells can be edited by the user. A cell is only editable when the related item <b>and</b> the related column are editable.<br>
 * <u>Example</u>:
 * <pre>
 * // create the columns
 * ...
 * // create the items
 * ...
 * 
 * // the user can edit the first and the third cell of the item second item
 * table.getColumn(0).setEditable(true);
 * table.getColumn(2).setEditable(true);	// after that it is not possible yet to edit the cells
 * table.getItem(1).setEditable(true);		// now the user can input values for the cells 1 and 3
 * </pre>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class ExecutionTableColumn {

	/** the corresponding table */
	private ExecutionTable table;
	/** the name of the column */
	private String name;
	/** the identifier of the column */
	private final int id;
	/** the index of the column in the list of all table columns */ 
	private int index;
	/** the alignment of the column content */
	private int alignment;
	/** the masks that are defined for the column */
	private final List<Mask> masks;
	/** flag that indicates whether the column is editable */
	private boolean editable;
	/** the width the user has specified but cannot be set because the column is not added to a table yet */
	private int userWidth;
	
	/** content is aligned at the left side of the column */
	public static final int LEFT = SwingConstants.LEFT;
	/** content is aligned at the right side of the column */
	public static final int RIGHT = SwingConstants.RIGHT;
	/** content is aligned in the center of the column */
	public static final int CENTER = SwingConstants.CENTER;
	
	/**
	 * Creates a new column.
	 * 
	 * @param name the name of the column that is displayed in the column header (<b>can contain html tags to format the column text</b>)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableColumn(final String name) throws IllegalArgumentException {
		this(name, -1);
	}
	
	/**
	 * Creates a new column.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It is not checked whether the identifier is unique meaning if there is another column that has the identifier too.
	 * You have to check this on your own.
	 * 
	 * @param name the name of the column that is displayed in the column header (<b>can contain html tags to format the column text</b>)
	 * @param id the identifier or <code>-1</code> if the column has no identifier
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableColumn(final String name, final int id) throws IllegalArgumentException {
		if(name == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.table = null;
		this.name = name;
		this.id = id;
		this.index = -1;
		this.alignment = CENTER;
		this.masks = new ArrayList<Mask>(3);
		this.editable = false;
		this.userWidth = -1;
	}
	
	/**
	 * Gets the name of the column.
	 * <br><br>
	 * The name is displayed as the column header.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the name of the column (<b>can contain html tags to format the column text</b>)
	 * @since 1.0
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Gets the identifier of the column.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the identifier or <code>-1</code> if there is not specified an identifier for the column
	 * @since 1.0
	 */
	public final int getID() {
		return id;
	}
	
	/**
	 * Gets the index of the column in the list of all table columns.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the index in the list of all columns or <code>-1</code> if the column is not even added to a table
	 * @since 1.0
	 */
	public final int getIndex() {
		return index;
	}
	
	/**
	 * Gets the alignment of the column content.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the alignment which is one of the following constants: {@link #LEFT}, {@link #CENTER}, {@link #RIGHT}
	 * @since 1.0
	 */
	public int getAlignment() {
		if(EDT.isExecutedInEDT())
			return alignment;
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return alignment;
				}
			});
	}
	
	/**
	 * Sets the alignment of the column content.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param alignment the alignment which has to be one of the following constants: {@link #LEFT}, {@link #CENTER}, {@link #RIGHT}
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<lI>if alignment is not one of the constants from above</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setAlignment(final int alignment) throws IllegalArgumentException {
		if(alignment != LEFT && alignment != CENTER && alignment != RIGHT)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			this.alignment = alignment;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setAlignment") {
				@Override
				protected void execute() throws Throwable {
					ExecutionTableColumn.this.alignment = alignment;
				}
			});
	}
	
	/**
	 * Gets the number of masks that are defined for the column.
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
	 * With masks you can replace objects by other objects or icons. For example you can define that the cells with the string
	 * "pi" should be masked with the <i>pi</i> symbol. Therefore you create a new mask and add it to the column like:
	 * <pre>
	 * // create the mask
	 * final Mask piMask = new Mask("pi", Symbol.getPredefinedSymbol(PredefinedSymbol.PI));
	 * // add it to the column
	 * column.addMask(piMask);
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
	 * Adds a new mask to the column.
	 * <br><br>
	 * With masks you can replace objects by other objects or icons. For example you can define that the cells with the string
	 * "pi" should be masked with the <i>pi</i> symbol. Therefore you create a new mask and add it to the column like:
	 * <pre>
	 * // create the mask
	 * final Mask piMask = new Mask("pi", Symbol.getPredefinedSymbol(PredefinedSymbol.PI));
	 * // add it to the column
	 * column.addMask(piMask);
	 * </pre>
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param mask the mask to be added
	 * @since 1.0
	 */
	public void addMask(final Mask mask) {
		if(mask == null || masks.contains(mask))
			return;
		
		if(EDT.isExecutedInEDT())
			masks.add(mask);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".addMask") {
				@Override
				protected void execute() throws Throwable {
					masks.add(mask);
				}
			});
	}
	
	/**
	 * Removes the mask from the column.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
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
	}
	
	/**
	 * Indicates whether the column is editable.
	 * <br><br>
	 * By default no cells can be edited by the user. A cell is only editable when the related item <b>and</b> the related column are editable.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the column is editable otherwise <code>false</code>
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
	 * Sets whether the column is editable.
	 * <br><br>
	 * By default no cells can be edited by the user. A cell is only editable when the related item <b>and</b> the related column are editable.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param editable <code>true</code> if the column should be editable otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setEditable(final boolean editable) throws IllegalArgumentException {
		if(EDT.isExecutedInEDT()) {
			this.editable = editable;
			if(table != null) table.closeEditors();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setEditable") {
				@Override
				protected void execute() throws Throwable {
					ExecutionTableColumn.this.editable = editable;
					if(table != null) table.closeEditors();
				}
			});
	}
	
	/**
	 * Gets the width of the column.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the width of the column or <code>-1</code> if there is not specified a width (uses default width)
	 * @since 1.0
	 */
	public int getWidth() {
		if(EDT.isExecutedInEDT())
			return userWidth;
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return userWidth;
				}
			});
	}
	
	/**
	 * Sets the width of the column.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see ExecutionTable#setAutoResizeColumns(boolean)
	 * @param width the width of the column
	 * @since 1.0
	 */
	public void setWidth(final int width) {
		if(EDT.isExecutedInEDT()) {
			userWidth = width;
			if(table != null) table.setColumnWidth(index, userWidth);
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setWidth") {
				@Override
				protected void execute() throws Throwable {
					userWidth = width;
					if(table != null) table.setColumnWidth(index, userWidth);
				}
			});
	}
	
	@Override
	public String toString() {
		return ExecutionTableColumn.class.getSimpleName() + "[" + name + "]";
	}
	
	/**
	 * Gets the corresponding table.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the table or <code>null</code> if there is no table reference set
	 * @since 1.0
	 */
	final ExecutionTable getTable() {
		return table;
	}
	
	/**
	 * Sets the corresponding table.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param table the table
	 * @since 1.0
	 */
	final void setTable(final ExecutionTable table) {
		this.table = table;
		
		// set the width the user has specified if necessary
		if(table != null && index >= 0 && userWidth >= 0)
			table.setColumnWidth(index, userWidth);
	}
	
	/**
	 * Sets the index of the column in the list of all table columns.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param index the index
	 * @since 1.0
	 */
	final void setIndex(final int index) {
		this.index = index;
		
		// set the width the user has specified if necessary
		if(table != null && index >= 0 && userWidth >= 0)
			table.setColumnWidth(index, userWidth);
	}
	
	/**
	 * Gets the mask that matches to the given object.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param o the object whose mask is searched
	 * @return the mask that matches to the object or <code>null</code> if no mask matches
	 * @since 1.0
	 */
	final Mask getMask(final Object o) {
		for(Mask m : masks)
			if(m.matches(o))
				return m;
		
		return null;
	}

}
