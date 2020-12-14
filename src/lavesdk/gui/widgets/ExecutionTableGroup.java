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

/**
 * Represents a table group.
 * <br><br>
 * A table group can consist of columns or items. You can add the groups by either invoking {@link ExecutionTable#addColumnGroup(ExecutionTableGroup)}
 * or {@link ExecutionTable#addItemGroup(ExecutionTableGroup)}.
 * <br><br>
 * You can specify the border of the group as well as the start index of the first group object (column or item) and the amount of objects
 * that the group should cover. The border of the group is always displayed at the last object (column or item) of the group.<br>
 * Furthermore you can determine that the group is repeatable meaning that the group repeats itself after <code>start index + amount</code> and so on.
 * 
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class ExecutionTableGroup {
	
	/** the border of the group */
	private final ExecutionTableBorder border;
	/** the index at which this group should start */
	private final int start;
	/** the number of objects that the group should cover */
	private final int amount;
	/** flag that indicates whether the group should repeat itself */
	private final boolean repeatable;
	
	/**
	 * Creates a new group with the amount of one object.
	 * 
	 * @param border the border of the group (ensure that you create correct borders for vertical groups (e.g. columns) and horizontal groups (e.g. rows)
	 * @param start the index at which the group should start (e.g. related to the items (rows) or columns)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if border is null</li>
	 * 		<li>if itemStart is <code>< 0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableGroup(final ExecutionTableBorder border, final int start) throws IllegalArgumentException {
		this(border, start, 1);
	}
	
	/**
	 * Creates a new group.
	 * 
	 * @param border the border of the group (ensure that you create correct borders for vertical groups (e.g. columns) and horizontal groups (e.g. rows)
	 * @param start the index at which the group should start (e.g. related to the items (rows) or columns)
	 * @param amount the number of objects (e.g. items (rows) or columns) that the group should cover
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if border is null</li>
	 * 		<li>if start is <code>< 0</code></li>
	 * 		<li>if amount is <code>< 1</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableGroup(final ExecutionTableBorder border, final int start, final int amount) throws IllegalArgumentException {
		this(border, start, amount, false);
	}
	
	/**
	 * Creates a new group.
	 * 
	 * @param border the border of the group (ensure that you create correct borders for vertical groups (e.g. columns) and horizontal groups (e.g. rows)
	 * @param start the index at which the group should start (e.g. related to the items (rows) or columns)
	 * @param amount the number of objects (e.g. items (rows) or columns) that the group should cover
	 * @param repeatable <code>true</code> if the group should repeat itself after the object amount otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if border is null</li>
	 * 		<li>if start is <code>< 0</code></li>
	 * 		<li>if amount is <code>< 1</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableGroup(final ExecutionTableBorder border, final int start, final int amount, final boolean repeatable) throws IllegalArgumentException {
		if(border == null || start < 0 || amount < 1)
			throw new IllegalArgumentException("No valid argument!");
		
		this.border = border;
		this.start = start;
		this.amount = amount;
		this.repeatable = repeatable;
	}
	
	/**
	 * Gets the border of the group.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the border
	 * @since 1.0
	 */
	public final ExecutionTableBorder getBorder() {
		return border;
	}
	
	/**
	 * Gets the index at which the group should start.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the start index (e.g. related to the items (rows) or columns)
	 * @since 1.0
	 */
	public final int getStart() {
		return start;
	}
	
	/**
	 * Gets the number of objects that the group should cover.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of objects (e.g. items (rows) or columns)
	 * @since 1.0
	 */
	public final int getAmount() {
		return amount;
	}
	
	/**
	 * Indicates if the group should repeat itself after the amount of objects that the group covers.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the group should repeat itself after the object amount otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean isRepeatable() {
		return repeatable;
	}

}
