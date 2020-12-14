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
 * Class:		ExecutionTableView
 * Task:		View to display an execution table
 * Created:		19.12.13
 * LastChanges:	19.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import lavesdk.configuration.Configuration;
import lavesdk.gui.widgets.ExecutionTable;
import lavesdk.gui.widgets.ExecutionTableBorder;
import lavesdk.gui.widgets.ExecutionTableColumn;
import lavesdk.gui.widgets.ExecutionTableGroup;
import lavesdk.gui.widgets.ExecutionTableItem;
import lavesdk.gui.widgets.ExecutionTableItem.InputParser;
import lavesdk.gui.widgets.enums.SelectionType;
import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;

/**
 * Represents a view that displays an execution table.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #ExecutionTableView(String, boolean, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
 * 		<li><i>EXECUTIONTABLE_SORTUP_BTN_TOOLTIP</i>: the tooltip text of a sort up button in a sortable execution table</li>
 * 		<li><i>EXECUTIONTABLE_SORTDOWN_BTN_TOOLTIP</i>: the tooltip text of a sort down button in a sortable execution table</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * <br><br>
 * <b>The table</b>:<br>
 * An execution table consists of {@link ExecutionTableColumn}s and {@link ExecutionTableItem}s. You can modify the content and style of
 * table cells in the specific item.<br>
 * Use the <i>add</i> methods ({@link #add(ExecutionTableColumn)}/{@link #add(ExecutionTableItem)}/...) to add columns and items to the table view.
 * <br><br>
 * <b>Renderers for cell contents</b>:<br>
 * The execution table has predefined renderers for cell objects of type {@link Number} and {@link Boolean}. All other cell object types
 * use the {@link DefaultTableCellRenderer}.<br>
 * You can specify a new default renderer for a specific type by using {@link #addDefaultCellRenderer(Class, TableCellRenderer)}.
 * <br><br>
 * <b>Edit-Mode</b>:<br>
 * A cell is editable if the related {@link ExecutionTableItem} and the related {@link ExecutionTableColumn} are editable (by default they are not editable).
 * The input the user has entered is a {@link String}. Use {@link InputParser}s to convert the input in the correct object type. You can specify a default parser
 * for all cells using {@link ExecutionTableItem#setDefaultInputParser(InputParser)} or parsers for each cell using {@link ExecutionTableItem#setCellInputParser(int, InputParser)}.<br>
 * <u>Example</u>:
 * <pre>
 * // add columns and items to the table
 * ...
 * 
 * // the first and the last column are editable
 * table.getColumn(0).setEditable(true);
 * table.getColumn(table.getColumnCount() - 1).setEditable(true);
 * 
 * // the cells of the item contain numeric data so set the suitable input parser
 * // because each cell contains numbers we can set a default parser without adding one to each cell
 * item.setDefaultInputParser(new ExecutionTableItem.NumericInputParser());
 * // enable edit mode
 * item.setEditable(true);	// now the user can edit the first and the last cell
 * </pre>
 * <b>Grid lines</b>:<br>
 * Use {@link #setGridBorder(ExecutionTableBorder)} to specify a custom grid style or to <b>disable the grid lines</b> of the table.
 * <br><br>
 * <b>Groups</b>:<br>
 * Use {@link ExecutionTableGroup}s to display objects (columns or items) that are distinct from other objects and add the groups by
 * using either {@link #addColumnGroup(ExecutionTableGroup)} or {@link #addItemGroup(ExecutionTableGroup)}.<br>
 * <u>Example</u>: the first column should be distinct from the other columns and at each second item we want to have a separator
 * <pre>
 * // create a non-repeatable group with a solid black border of 2 pixels
 * // for the first column (start index 0 and 1 column as the amount)
 * final ExecutionTableGroup colGroup = new ExecutionTableGroup(new ExecutionTableBorder(2, Color.black), 0, 1, false);
 * // create a repeatable group with a solid black border of 2 pixels
 * // that starts at the 3 row (start index 2) and includes 2 rows
 * final ExecutionTableGroup itemGroup = new ExecutionTableGroup(new ExecutionTableBorder(2, Color.black), 2, 2, true);
 * 
 * // add the groups to the table
 * tableView.addColumnGroup(colGroup);
 * tableView.addItemGroup(itemGroup);
 * </pre>
 * <b>Sorting</b>:<br>
 * Use {@link #sortItems(int, SortOrder)} to sort the items of the table by a specific column.
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
 * <b>Save and load the configuration</b>:<br>
 * You can save and load a configuration of the execution table view by using {@link #saveConfiguration(Configuration)} and {@link #loadConfiguration(Configuration)}.
 * It is saved or restored the visibility of the view. This makes it possible that you can store the state of the view persistent.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class ExecutionTableView extends View {
	
	private static final long serialVersionUID = 1L;
	
	/** the table of the view */
	private final ExecutionTable table;
	
	/**
	 * Creates a new execution table view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableView(String title) throws IllegalArgumentException {
		this(title, true);
	}
	
	/**
	 * Creates a new execution table view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param closable <code>true</code> if the table view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a table view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableView(String title, boolean closable) throws IllegalArgumentException {
		this(title, closable, null, null);
	}
	
	/**
	 * Creates a new execution table view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param closable <code>true</code> if the table view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a table view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableView(String title, boolean closable, String langID) throws IllegalArgumentException {
		this(title, closable, Resources.getInstance().LANGUAGE_FILE, langID);
	}
	
	/**
	 * Creates a new execution table view.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the table view. The following language labels are available:
	 * <ul>
	 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
	 * 		<li><i>EXECUTIONTABLE_SORTUP_BTN_TOOLTIP</i>: the tooltip text of a sort up button in a sortable execution table</li>
	 * 		<li><i>EXECUTIONTABLE_SORTDOWN_BTN_TOOLTIP</i>: the tooltip text of a sort down button in a sortable execution table</li>
	 * </ul>
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param closable <code>true</code> if the table view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a table view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langFile the language file or <code>null</code> if the table view should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableView(String title, boolean closable, LanguageFile langFile, String langID) throws IllegalArgumentException {
		super(title, closable, langFile, langID);
		
		table = new ExecutionTable(langFile, langID);
		
		content.setLayout(new BorderLayout());
		content.add(table);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getAutoRepaint() {
		return table.getAutoRepaint();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAutoRepaint(boolean autoRepaint) {
		table.setAutoRepaint(autoRepaint);
	}
	
	/**
	 * Adds a new default renderer for the specified class.
	 * <br><br>
	 * If there is already a renderer for the specified class this renderer is overwritten with the new one.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param c the class
	 * @param renderer the renderer
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if c is null</li>
	 * 		<li>if renderer is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void addDefaultCellRenderer(final Class<?> c, final TableCellRenderer renderer) throws IllegalArgumentException {
		table.addDefaultCellRenderer(c, renderer);
	}
	
	/**
	 * Gets the grid border.
	 * <br><br>
	 * The grid border is used to display the table's grid lines.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the grid border or <code>null</code> if the table does not have grid lines
	 * @since 1.0
	 */
	public ExecutionTableBorder getGridBorder() {
		return table.getGridBorder();
	}
	
	/**
	 * Sets the grid border.
	 * <br><br>
	 * The grid border is used to display the table's grid lines.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @see #getDefaultGridColor()
	 * @param border the border or <code>null</code> if the table should not have grid lines
	 * @since 1.0
	 */
	public void setGridBorder(final ExecutionTableBorder border) {
		table.setGridBorder(border);
	}
	
	/**
	 * Gets the system dependent default grid line color.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the default grid line color
	 * @since 1.0
	 */
	public Color getDefaultGridColor() {
		return table.getDefaultGridColor();
	}
	
	/**
	 * Gets the height of the items in the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the height
	 * @since 1.0
	 */
	public int getItemHeight() {
		return table.getItemHeight();
	}
	
	/**
	 * Sets the given value as the height for all items of the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param height the height
	 * @since 1.0
	 */
	public void setItemHeight(final int height) {
		table.setItemHeight(height);
	}
	
	/**
	 * Sets the height of the column header.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param height the height of the column header
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if height is <code>< 0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public void setColumnHeaderHeight(final int height) throws IllegalArgumentException {
		table.setColumnHeaderHeight(height);
	}
	
	/**
	 * Indicates whether the columns are auto resizable.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the columns are auto resizable (default) otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean getAutoResizeColumns() {
		return table.getAutoResizeColumns();
	}
	
	/**
	 * Sets whether the columns are auto resizable. If auto resize is enabled then the columns adjust their widths to fit the table size but
	 * retain the percentage amount of their width on the total width.<br>
	 * When the columns are not auto resizable you should set their widths using {@link ExecutionTableColumn#setWidth(int)}.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param enabled <code>true</code> if auto resize of the columns is enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setAutoResizeColumns(final boolean enabled) {
		table.setAutoResizeColumns(enabled);
	}
	
	/**
	 * Indicates whether the table is sortable meaning that a user can sort the items using buttons at each item
	 * to change the position up/down.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the table is sortable otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isSortable() {
		return table.isSortable();
	}
	
	/**
	 * Sets whether the table is sortable meaning that a user can sort the items using buttons at each item
	 * to change the position up/down.
	 * <br><br>
	 * To sort the table automatically use {@link #sortItems(int, SortOrder)}.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This mode supports manual sorting meaning all items that are invisible are not shown and therefore they are not available
	 * in manual sorting. If you let the user sort the table and afterwards make alle invisible items visible again the presented order must not
	 * be valid because if there are two items <code>i1</code> and <code>i5</code> with<br>
	 * <code>
	 * i1 - visible<br>
	 * i2 - invisible<br>
	 * i3 - invisible<br>
	 * i4 - invisible<br>
	 * i5 - visible
	 * </code><br>
	 * these items are also swapped in the list of all items meaning the result after sort down <code>i1</code> is<br>
	 * <code>
	 * i5 - visible<br>
	 * i2 - invisible<br>
	 * i3 - invisible<br>
	 * i4 - invisible<br>
	 * i1 - visible
	 * </code>
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param sortable <code> if the table should be sortable otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setSortable(final boolean sortable) {
		table.setSortable(sortable);
	}
    
	/**
	 * Indicates whether auto scroll is enabled meaning that the execution table is automatically scrolled to the
	 * last item that was added.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if auto scroll is enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isAutoScrollEnabled() {
		return table.isAutoScrollEnabled();
	}
    
	/**
	 * Sets whether auto scroll should be enabled meaning that the execution table is automatically scrolled to the
	 * last item that was added.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param autoScroll <code>true</code> if auto scroll should be enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setAutoScrollEnabled(final boolean autoScroll) {
		table.setAutoScrollEnabled(autoScroll);
	}
    
	/**
	 * Gets the selection type of the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the selection type
	 * @since 1.0
	 */
	public SelectionType getSelectionType() {
		return table.getSelectionType();
	}
    
	/**
	 * Sets the selection type of the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param type the selection type
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if type is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setSelectionType(final SelectionType type) throws IllegalArgumentException {
		table.setSelectionType(type);
	}
	
	/**
	 * Gets the selected column in the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #clearSelection()
	 * @return the selected column or <code>null</code> if no cell is selected
	 * @since 1.0
	 */
	public ExecutionTableColumn getSelectedColumn() {
		return table.getSelectedColumn();
	}
	
	/**
	 * Gets the selected item in the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #clearSelection()
	 * @return the selected item or <code>null</code> if no cell is selected
	 * @since 1.0
	 */
	public ExecutionTableItem getSelectedItem() {
		return table.getSelectedItem();
	}
	
	/**
	 * Gets the number of selected items in the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of selected items
	 * @since 1.0
	 */
	public int getSelectedItemCount() {
		return table.getSelectedItemCount();
	}
	
	/**
	 * Gets the selected item at the specified index.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The selection of multiple items is only possible if the {@link SelectionType} is set to {@link SelectionType#ROWS}.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #clearSelection()
	 * @return the selected item
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getSelectedItemCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableItem getSelectedItem(final int index) throws IndexOutOfBoundsException {
		return table.getSelectedItem(index);
	}
	
	/**
	 * Clears the selection of the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void clearSelection() {
		table.clearSelection();
	}
	
	/**
	 * Adds a new column to the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param column the column
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if column is null</li>
	 * 		<li>if column is already added to another table</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final ExecutionTableColumn column) throws IllegalArgumentException {
		table.add(column);
	}
	
	/**
	 * Adds new columns to the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param columns the list of column to be added
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if a column is null</li>
	 * 		<li>if a column is already added to another table</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final ExecutionTableColumn[] columns) throws IllegalArgumentException {
		table.add(columns);
	}
	
	/**
	 * Adds a new item to the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param item the item
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if item is null</li>
	 * 		<li>if item is already added to another table</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final ExecutionTableItem item) throws IllegalArgumentException {
		table.add(item);
	}
	
	/**
	 * Adds new items to the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param items the list of items to be added
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if an item is null</li>
	 * 		<li>if an item is already added to another table</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final ExecutionTableItem[] items) throws IllegalArgumentException {
		table.add(items);
	}
	
	/**
	 * Removes the column from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param column the column
	 * @since 1.0
	 */
	public void remove(final ExecutionTableColumn column) {
		table.remove(column);
	}
	
	/**
	 * Removes the item from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param item the item
	 * @since 1.0
	 */
	public void remove(final ExecutionTableItem item) {
		table.remove(item);
	}
	
	/**
	 * Removes all columns from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllColumns() {
		table.removeAllColumns();
	}
	
	/**
	 * Removes all items from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllItems() {
		table.removeAllItems();
	}
	
	/**
	 * Gets the number of columns of the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of columns
	 * @since 1.0
	 */
	public int getColumnCount() {
		return table.getColumnCount();
	}
	
	/**
	 * Gets the column at the given index.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the column
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getColumnCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableColumn getColumn(final int index) throws IndexOutOfBoundsException {
		return table.getColumn(index);
	}
	
	/**
	 * Gets the column by the specified id.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param id the id
	 * @return the column or <code>null</code> if there is no column with the given id
	 * @since 1.0
	 */
	public ExecutionTableColumn getColumnByID(final int id) {
		return table.getColumnByID(id);
	}
	
	/**
	 * Gets the first column of the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the first column or <code>null</code> if the table does not have a column
	 * @since 1.0
	 */
	public ExecutionTableColumn getFirstColumn() {
		return table.getFirstColumn();
	}
	
	/**
	 * Gets the last column of the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the last column or <code>null</code> if the table does not have a column
	 * @since 1.0
	 */
	public ExecutionTableColumn getLastColumn() {
		return table.getLastColumn();
	}
	
	/**
	 * Gets the number of items (rows) of the table.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This number must not comply with the number of visible rows in the table because an {@link ExecutionTableItem} can be made invisible.
	 * Use {@link #getVisibleRowCount()} and {@link #getVisibleRow(int)} to iterate over the visible items of the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of items
	 * @since 1.0
	 */
	public int getItemCount() {
		return table.getItemCount();
	}
	
	/**
	 * Gets the item at the specified index.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the item
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getItemCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableItem getItem(final int index) throws IndexOutOfBoundsException {
		return table.getItem(index);
	}
	
	/**
	 * Gets the item by the specified id.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param id the id
	 * @return the item or <code>null</code> if there is no item with the given id
	 * @since 1.0
	 */
	public ExecutionTableItem getItemByID(final int id) {
		return table.getItemByID(id);
	}
	
	/**
	 * Gets the first item of the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the first item or <code>null</code> if the table does not have an item
	 * @since 1.0
	 */
	public ExecutionTableItem getFirstItem() {
		return table.getFirstItem();
	}
	
	/**
	 * Gets the last item of the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the last item or <code>null</code> if the table does not have an item
	 * @since 1.0
	 */
	public ExecutionTableItem getLastItem() {
		return table.getLastItem();
	}
	
	/**
	 * Gets the number of visible rows in the table.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * {@link #getVisibleRowCount()} is unequal {@link #getItemCount()} if an {@link ExecutionTableItem} is set to invisible using
	 * {@link ExecutionTableItem#setVisible(boolean)}.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of visible rows
	 * @since 1.0
	 */
	public int getVisibleRowCount() {
		return table.getVisibleRowCount();
	}
	
	/**
	 * Gets the visible row at the specified index.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If you make items invisible using {@link ExecutionTableItem#setVisible(boolean)} the index of the item that can be requested using
	 * {@link ExecutionTableItem#getIndex()} is not the index of the item in the list of visible items/rows.<br>
	 * Make use of {@link #convertItemIndexToVisible(int)} to convert an item index in an index of the visible rows list.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the item of the row
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getVisibleRowCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableItem getVisibleRow(final int index) throws IndexOutOfBoundsException {
		return table.getVisibleRow(index);
	}
	
	/**
	 * Gets the visible item by the specified id.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param id the id
	 * @return the item or <code>null</code> if there is no visible item with the given id
	 * @since 1.0
	 */
	public ExecutionTableItem getVisibleRowByID(final int id) {
		return table.getVisibleRowByID(id);
	}
	
	/**
	 * Converts the given item index (<b>of a visible item</b>) in an index of the visible rows list.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If the item is invisible the returned index must not be valid!
	 * 
	 * @param index the item index
	 * @return the item index in the list of visible rows
	 * @since 1.0
	 */
	public int convertItemIndexToVisible(final int index) {
		return table.convertItemIndexToVisible(index);
	}
	
	/**
	 * Sorts the items by a specified column index and sort order.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If you want to restore the unsorted order then use {@link SortOrder#UNSORTED}. The column index
	 * does not matter.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param colIndex the index of the column the items should be ordered by
	 * @param order the sort order
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the column index is out of range (<code>index < 0 || index >= getColumnCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public void sortItems(final int colIndex, final SortOrder order) throws IndexOutOfBoundsException {
		table.sortItems(colIndex, order);
	}
	
	/**
	 * Adds a new column group to the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param group the group to be added as a column group
	 * @since 1.0
	 */
	public void addColumnGroup(final ExecutionTableGroup group) {
		table.addColumnGroup(group);
	}
	
	/**
	 * Removes a column group from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param group the group to be removed
	 * @since 1.0
	 */
	public void removeColumnGroup(final ExecutionTableGroup group) {
		table.removeColumnGroup(group);
	}
	
	/**
	 * Gets the number of column groups in the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of column groups
	 * @since 1.0
	 */
	public int getColumnGroupCount() {
		return table.getColumnGroupCount();
	}
	
	/**
	 * Gets the column group at the given index.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the column group
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getColumnGroupCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableGroup getColumnGroup(final int index) throws IndexOutOfBoundsException {
		return table.getColumnGroup(index);
	}
	
	/**
	 * Removes all column groups from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllColumnGroups() {
		table.removeAllColumnGroups();
	}
	
	/**
	 * Adds a new item (row) group to the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param group the group to be added as an item group
	 * @since 1.0
	 */
	public void addItemGroup(final ExecutionTableGroup group) {
		table.addItemGroup(group);
	}
	
	/**
	 * Removes an item (row) group from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param group the group to be removed
	 * @since 1.0
	 */
	public void removeItemGroup(final ExecutionTableGroup group) {
		table.removeItemGroup(group);
	}
	
	/**
	 * Gets the number of item groups in the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of item groups
	 * @since 1.0
	 */
	public int getItemGroupCount() {
		return table.getItemGroupCount();
	}
	
	/**
	 * Gets the item group at the given index.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the item group
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getItemGroupCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public ExecutionTableGroup getItemGroup(final int index) throws IndexOutOfBoundsException {
		return table.getItemGroup(index);
	}
	
	/**
	 * Removes all item groups from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllItemGroups() {
		table.removeAllItemGroups();
	}
	
	/**
	 * Resets the table view meaning that all columns, items and groups are removed from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	@Override
	public void reset() {
		table.removeAll();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRepaintDisabled(boolean disabled) {
		table.setRepaintDisabled(disabled);
		super.setRepaintDisabled(disabled);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void repaintComponent() {
		super.repaintComponent();
		table.repaint();
	}

}
