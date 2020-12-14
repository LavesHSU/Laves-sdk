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
 * Class:		ExecutionTableItem
 * Task:		Representation of an item of an execution table
 * Created:		17.12.13
 * LastChanges:	16.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import java.awt.Color;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.gui.widgets.ExecutionTable.ExecutionTableModel;
import lavesdk.utils.MathUtils;

/**
 * Represents an item of an {@link ExecutionTable}.
 * <br><br>
 * <b>Cell content</b>:<br>
 * An item consists of cells for each column of the table. You can specify the cell object (the object that is displayed in the specific
 * cell) by using {@link #setCellObject(int, Object)} or use the <i>cell object by id</i> method ({@link #setCellObjectByID(int, Object)})
 * to set the cell objects by column identifiers instead of column indices.<br>
 * You can set multiple cell objects with {@link #setCellData(Map)} or {@link #setCellDataByID(Map)}.
 * <br><br>
 * <b>Cell color</b>:<br>
 * Change the style of a cell with {@link #setCellBackground(int, Color)} and {@link #setCellForeground(int, Color)}. Use {@link #setCellBackgroundByID(int, Color)}
 * or {@link #setCellForegroundByID(int, Color)} if you only have the identifier of the column where you want to change the background or foreground.
 * <br><br>
 * <b>Borders</b>:<br>
 * You can highlight an item by setting a highlight border using {@link #setBorder(ExecutionTableBorder)}. With {@link #setCellBorder(int, Color)} or
 * {@link #setCellBorderByID(int, Color)} you can set a border color for a specific cell. If a cell has a border color then it is painted a 1px solid
 * border around the cell in the specified color.
 * <br><br>
 * Add a new item to the table by calling {@link ExecutionTable#add(ExecutionTableItem)}.
 * <br><br>
 * <b>Edit cells</b>:<br>
 * By default no cells can be edited by the user. A cell is only editable when the related item <b>and</b> the related column are editable.<br>
 * You should specify a default input parser ({@link #setDefaultInputParser(InputParser)}) and/or cell input parser ({@link #setCellInputParser(int, InputParser)})
 * that the user input can be converted in correct cell objects.<br>
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
public class ExecutionTableItem {

	/** the identifier of the column */
	private final int id;
	/** the corresponding table or <code>null</code> if the item is not added to a table yet */
	private ExecutionTable table;
	/** the model of the execution table or <code>null</code> if the item is not added to a table yet */
	private ExecutionTableModel model;
	/** the background color of the item */
	private Color background;
	/** the foreground color of the item */
	private Color foreground;
	/** caches the parameter data that is dependent on {@link #table} but no table reference is available (because the table will be first set when the item is added to the table) */
	private final Map<String, List<ParameterCache>> parameterDataCache;
	/** the border of the item */
	private ExecutionTableBorder border;
	/** the index that describes the normal order position of the item */
	private final int unsortedOrderIndex;
	/** the index of the item in the list of items */
	private int index;
	/** flag that indicates whether the item is editable */
	private boolean editable;
	/** the default input parser */
	private InputParser<?> defInputParser;
	/** flag that indicates whether the item is displayed or not */
	private boolean visible;
	/** the user data of the item */
	private Object userData;
	
	// cell data (important: please see removeCell(...) if a new cell data list is added)
	/** the objects of the cells */
	private final List<Object> cellObjects;
	/** the background colors of the cells */
	private final List<Color> cellBackgrounds;
	/** the foreground colors of the cells */
	private final List<Color> cellForegrounds;
	/** the border colors of the cells */
	private final List<Border> cellBorders;
	/** the input parsers of the cells */
	private final List<InputParser<?>> cellInputParsers;
	
	/** the default background of the cells */
	private static final Color DEF_BACKGROUND = Color.white;
	/** the default foreground of the cells */
	private static final Color DEF_FOREGROUND = Color.black;
	
	// the method keys (important: update invokeMethod(...) if a new method key is added)
	/** the method key for the {@link #setCellObjectByID(int, Object)} method */
	private static final String MK_SETCELLOBJECTBYID = "setCellObjectByID";
	/** the method key for the {@link #setCellDataByID(Map)} method */
	private static final String MK_SETCELLDATABYID = "setCellDataByID";
	/** the method key for the {@link #setCellBackgroundByID(int, Color)} method */
	private static final String MK_SETCELLBACKGROUNDBYID = "setCellBackgroundByID";
	/** the method key for the {@link #setCellForegroundByID(int, Color)} method */
	private static final String MK_SETCELLFOREGROUNDBYID = "setCellForegroundByID";
	/** the method key for the {@link #setCellBorderByID(int, Color)} method */
	private static final String MK_SETCELLBORDERBYID = "setCellBorderByID";
	
	/** the next unsorted order index */
	private static int nextUnsortedOrderIndex = 1;
	
	/**
	 * Creates a new item.
	 * 
	 * @since 1.0
	 */
	public ExecutionTableItem() {
		this(null);
	}
	
	/**
	 * Creates a new item.
	 * 
	 * @param id the identifier or <code>-1</code> if the item has no identifier
	 * @since 1.0
	 */
	public ExecutionTableItem(final int id) {
		this(null, id);
	}
	
	/**
	 * Creates a new item.
	 * 
	 * @param cellObjects the cell objects or <code>null</code> if the item has no initial cell objects
	 * @since 1.0
	 */
	public ExecutionTableItem(final Object[] cellObjects) {
		this(cellObjects, -1);
	}
	
	/**
	 * Creates a new item.
	 * 
	 * @param cellObjects the cell objects or <code>null</code> if the item has no initial cell objects
	 * @param id the identifier or <code>-1</code> if the item has no identifier
	 * @since 1.0
	 */
	public ExecutionTableItem(final Object[] cellObjects, final int id) {
		this.id = id;
		this.table = null;
		this.model = null;
		this.background = DEF_BACKGROUND;
		this.foreground = DEF_FOREGROUND;
		this.parameterDataCache = new HashMap<String, List<ParameterCache>>(3);
		this.cellObjects = new ArrayList<Object>();
		this.cellBackgrounds = new ArrayList<Color>();
		this.cellForegrounds = new ArrayList<Color>();
		this.cellBorders = new ArrayList<Border>();
		this.border = null;
		this.unsortedOrderIndex = nextUnsortedOrderIndex++;
		this.index = -1;
		this.editable = false;
		this.defInputParser = null;
		this.cellInputParsers = new ArrayList<InputParser<?>>();
		this.visible = true;
		this.userData = null;
		
		// add the cell objects (array index means column index)
		if(cellObjects != null)
			for(Object o : cellObjects)
				this.cellObjects.add(o);
	}
	
	/**
	 * Creates a new item.
	 * 
	 * @param cellData the data of the cells with the column index or the column identifier as the key and the cell's object as the associated value
	 * @param dataByID <code>true</code> if the column identifier is used as the data key or <code>false</code> if the column index is used as the data key
	 * @since 1.0
	 */
	public ExecutionTableItem(final Map<Integer, ? extends Object> cellData, final boolean dataByID) {
		this(cellData, dataByID, -1);
	}
	
	/**
	 * Creates a new item.
	 * 
	 * @param cellData the data of the cells with the column index or the column identifier as the key and the cell's object as the associated value
	 * @param dataByID <code>true</code> if the column identifier is used as the data key or <code>false</code> if the column index is used as the data key
	 * @param id the identifier or <code>-1</code> if the item has no identifier
	 * @since 1.0
	 */
	public ExecutionTableItem(final Map<Integer, ? extends Object> cellData, final boolean dataByID, final int id) {
		this.id = id;
		this.table = null;
		this.model = null;
		this.background = DEF_BACKGROUND;
		this.foreground = DEF_FOREGROUND;
		this.parameterDataCache = new HashMap<String, List<ParameterCache>>(3);
		this.cellObjects = new ArrayList<Object>();
		this.cellBackgrounds = new ArrayList<Color>();
		this.cellForegrounds = new ArrayList<Color>();
		this.cellBorders = new ArrayList<Border>();
		this.border = null;
		this.unsortedOrderIndex = nextUnsortedOrderIndex++;
		this.index = -1;
		this.editable = false;
		this.defInputParser = null;
		this.cellInputParsers = new ArrayList<InputParser<?>>();
		this.visible = true;
		this.userData = null;
		
		// add the cell objects (array index means column index)
		if(cellData != null) {
			if(dataByID)
				setCellDataByID(cellData);
			else
				setCellData(cellData);
		}
	}
	
	/**
	 * Gets the identifier of the item.
	 * 
	 * @return the identifier or <code>-1</code> if there is not specified an identifier for the item
	 * @since 1.0
	 */
	public final int getID() {
		return id;
	}
	
	/**
	 * Gets the items's background color.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the background color of the item
	 * @since 1.0
	 */
	public Color getBackground() {
		if(EDT.isExecutedInEDT())
			return background;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return background;
				}
			});
	}
	
	/**
	 * Sets the item's background color.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param color the background color
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if color is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setBackground(final Color color) throws IllegalArgumentException {
		if(color == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			background = color;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setBackground") {
				@Override
				protected void execute() throws Throwable {
					background = color;
				}
			});
		
		if(table != null && visible)
			table.autoRepaint();
	}
	
	/**
	 * Gets the items's foreground color.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the foreground color of the item
	 * @since 1.0
	 */
	public Color getForeground() {
		if(EDT.isExecutedInEDT())
			return foreground;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return foreground;
				}
			});
	}
	
	/**
	 * Sets the item's foreground color.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param color the foreground color
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if color is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setForeground(final Color color) throws IllegalArgumentException {
		if(color == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			foreground = color;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setForeground") {
				@Override
				protected void execute() throws Throwable {
					foreground = color;
				}
			});
		
		if(table != null && visible)
			table.autoRepaint();
	}
	
	/**
	 * Gets the cell's object of the specified column.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param colIndex the column index
	 * @return the object or <code>null</code> if the cell has no object
	 * @since 1.0
	 */
	public Object getCellObject(final int colIndex) {
		// firstly close the opened editors so that the value of the cell is correct
		if(table != null)
			table.closeEditors();
		
		if(EDT.isExecutedInEDT())
			return getListEntry(cellObjects, colIndex);
		else
			return EDT.execute(new GuiRequest<Object>() {
				@Override
				protected Object execute() throws Throwable {
					return getListEntry(cellObjects, colIndex);
				}
			});
	}
	
	/**
	 * Sets the cell's object at the specified column.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param colIndex the column index
	 * @param object the object
	 * @since 1.0
	 */
	public <T> void setCellObject(final int colIndex, final T object) {
		if(EDT.isExecutedInEDT())
			setListEntry(cellObjects, colIndex, object);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setCellObject") {
				@Override
				protected void execute() throws Throwable {
					setListEntry(cellObjects, colIndex, object);
				}
			});
		
		if(table != null && visible)
			table.autoRepaint();
	}
	
	/**
	 * Sets the cell's object at the specified column.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param colID the identifier of the column
	 * @param object the object
	 * @since 1.0
	 */
	public <T> void setCellObjectByID(final int colID, final T object) {
		EDT.execute(new GuiJob(getClass().getSimpleName() + ".setCellObjectByID") {
			@Override
			protected void execute() throws Throwable {
				if(table == null) {
					// add method data to cache
					final ParameterCache dco = new ParameterCache();
					dco.set("colID", colID);
					dco.set("object", object);
					updateDataCache(MK_SETCELLOBJECTBYID, dco);
				}
				else {
					final ExecutionTableColumn column = table.getColumnByID(colID);
					if(column != null)
						setCellObject(column.getIndex(), object);
				}
			}
		});
	}
	
	/**
	 * Sets the cell data of the item.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param data the data of the cells with the column index as the key and the cell's object as the associated value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if data is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public <T> void setCellData(final Map<Integer, T> data) throws IllegalArgumentException {
		if(data == null)
			throw new IllegalArgumentException("No valid argument!");
		
		EDT.execute(new GuiJob(getClass().getSimpleName() + ".setCellData") {
			@Override
			protected void execute() throws Throwable {
				final Iterator<Integer> it = data.keySet().iterator();
				int index;
				
				while(it.hasNext()) {
					index = it.next();
					setCellObject(index, data.get(index));
				}
			}
		});
		
		if(table != null && visible)
			table.autoRepaint();
	}
	
	/**
	 * Sets the cell data of the item by column identifiers.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param data the data of the cells with the column identifier as the key and the cell's object as the associated value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if data is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public <T> void setCellDataByID(final Map<Integer, T> data) throws IllegalArgumentException {
		if(data == null)
			throw new IllegalArgumentException("No valid argument!");
		
		EDT.execute(new GuiJob(getClass().getSimpleName() + ".setCellDataByID") {
			@Override
			protected void execute() throws Throwable {
				if(table == null) {
					// add method data to cache
					final ParameterCache dco = new ParameterCache();
					dco.set("data", data);
					updateDataCache(MK_SETCELLDATABYID, dco);
				}
				else {
					final Iterator<Integer> it = data.keySet().iterator();
					int id;
					
					while(it.hasNext()) {
						id = it.next();
						setCellObjectByID(id, data.get(id));
					}
				}
			}
		});
	}
	
	/**
	 * Gets the cell's background color.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param colIndex the column index
	 * @return the background color or the default background if no background is set for the specified cell
	 * @since 1.0
	 */
	public Color getCellBackground(final int colIndex) {
		if(EDT.isExecutedInEDT()) {
			final Color c = getListEntry(cellBackgrounds, colIndex);
			return (c != null) ? c : background;
		}
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					final Color c = getListEntry(cellBackgrounds, colIndex);
					return (c != null) ? c : background;
				}
			});
	}
	
	/**
	 * Sets the cell's background color at the specified column.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param colIndex the column index
	 * @param color the background color
	 * @since 1.0
	 */
	public void setCellBackground(final int colIndex, final Color color) {
		if(EDT.isExecutedInEDT())
			setListEntry(cellBackgrounds, colIndex, color);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setCellBackground") {
				@Override
				protected void execute() throws Throwable {
					setListEntry(cellBackgrounds, colIndex, color);
				}
			});
		
		if(table != null && visible)
			table.autoRepaint();
	}
	
	/**
	 * Sets the cell's background color at the specified column.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param colID the identifier of the column
	 * @param color the background color
	 * @since 1.0
	 */
	public void setCellBackgroundByID(final int colID, final Color color) {
		EDT.execute(new GuiJob(getClass().getSimpleName() + ".setCellBackgroundByID") {
			@Override
			protected void execute() throws Throwable {
				if(table == null) {
					// add method data to cache
					final ParameterCache dco = new ParameterCache();
					dco.set("colID", colID);
					dco.set("color", color);
					updateDataCache(MK_SETCELLBACKGROUNDBYID, dco);
				}
				else {
					final ExecutionTableColumn column = table.getColumnByID(colID);
					if(column != null)
						setCellBackground(column.getIndex(), color);
				}
			}
		});
	}
	
	/**
	 * Gets the cell's foreground color.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param colIndex the column index
	 * @return the foreground color or the default foreground if no foreground is set for the specified cell
	 * @since 1.0
	 */
	public Color getCellForeground(final int colIndex) {
		if(EDT.isExecutedInEDT()) {
			final Color c = getListEntry(cellForegrounds, colIndex);
			return (c != null) ? c : foreground;
		}
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					final Color c = getListEntry(cellForegrounds, colIndex);
					return (c != null) ? c : foreground;
				}
			});
	}
	
	/**
	 * Sets the cell's foreground color.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param colIndex the column index
	 * @param color the foreground color
	 * @since 1.0
	 */
	public void setCellForeground(final int colIndex, final Color color) {
		if(EDT.isExecutedInEDT())
			setListEntry(cellForegrounds, colIndex, color);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setCellForeground") {
				@Override
				protected void execute() throws Throwable {
					setListEntry(cellForegrounds, colIndex, color);
				}
			});
		
		if(table != null && visible)
			table.autoRepaint();
	}
	
	/**
	 * Sets the cell's foreground color at the specified column.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param colID the identifier of the column
	 * @param color the foreground color
	 * @since 1.0
	 */
	public void setCellForegroundByID(final int colID, final Color color) {
		EDT.execute(new GuiJob(getClass().getSimpleName() + ".setCellForegroundByID") {
			@Override
			protected void execute() throws Throwable {
				if(table == null) {
					// add method data to cache
					final ParameterCache dco = new ParameterCache();
					dco.set("colID", colID);
					dco.set("color", color);
					updateDataCache(MK_SETCELLFOREGROUNDBYID, dco);
				}
				else {
					final ExecutionTableColumn column = table.getColumnByID(colID);
					if(column != null)
						setCellForeground(column.getIndex(), color);
				}
			}
		});
	}
	
	/**
	 * Gets the cell's border.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param colIndex the column index
	 * @return the border or <code>null</code> if the cell does not have a border
	 * @since 1.0
	 */
	public Border getCellBorder(final int colIndex) {
		if(EDT.isExecutedInEDT())
			return getListEntry(cellBorders, colIndex);
		else
			return EDT.execute(new GuiRequest<Border>() {
				@Override
				protected Border execute() throws Throwable {
					return getListEntry(cellBorders, colIndex);
				}
			});
	}
	
	/**
	 * Sets the cell's border.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param colIndex the column index
	 * @param border the border or <code>null</code> if the cell should not have a border
	 * @since 1.0
	 */
	public void setCellBorder(final int colIndex, final Border border) {
		if(EDT.isExecutedInEDT())
			setListEntry(cellBorders, colIndex, border);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setCellBorder") {
				@Override
				protected void execute() throws Throwable {
					setListEntry(cellBorders, colIndex, border);
				}
			});
		
		if(table != null && visible)
			table.autoRepaint();
	}
	
	/**
	 * Sets the cell's border.
	 * <br><br>
	 * This creates a 1px solid border in the specified color or removes the border from the cell if the color is <code>null</code>.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param colIndex the column index
	 * @param color the border color or <code>null</code> if the cell should not have a border
	 * @since 1.0
	 */
	public void setCellBorder(final int colIndex, final Color color) throws IllegalArgumentException {
		setCellBorder(colIndex, color, 1);
	}
	
	/**
	 * Sets the cell's border.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param colIndex the column index
	 * @param color the border color or <code>null</code> if the cell should not have a border
	 * @param width the line width of the border
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if width is <code>< 1</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public void setCellBorder(final int colIndex, final Color color, final int width) throws IllegalArgumentException {
		if(width < 1)
			throw new IllegalArgumentException("No valid argument!");
		
		setCellBorder(colIndex, (color != null) ? BorderFactory.createMatteBorder(width, width, width, width, color) : null);
	}
	
	/**
	 * Sets the cell's border at the specified column.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param colID the identifier of the column
	 * @param border the border or <code>null</code> if the cell should not have a border
	 * @since 1.0
	 */
	public void setCellBorderByID(final int colID, final Border border) {
		EDT.execute(new GuiJob(getClass().getSimpleName() + ".setCellBorderByID") {
			@Override
			protected void execute() throws Throwable {
				if(table == null) {
					// add method data to cache
					final ParameterCache dco = new ParameterCache();
					dco.set("colID", colID);
					dco.set("border", border);
					updateDataCache(MK_SETCELLBORDERBYID, dco);
				}
				else {
					final ExecutionTableColumn column = table.getColumnByID(colID);
					if(column != null)
						setCellBorder(column.getIndex(), border);
				}
			}
		});
	}
	
	/**
	 * Sets the cell's border at the specified column.
	 * <br><br>
	 * This creates a 1px solid border in the specified color or removes the border from the cell if the color is <code>null</code>.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param colID the identifier of the column
	 * @param color the border color or <code>null</code> if the cell should not have a border
	 * @since 1.0
	 */
	public void setCellBorderByID(final int colID, final Color color) {
		setCellBorderByID(colID, color, 1);
	}
	
	/**
	 * Sets the cell's border at the specified column.
	 * <br><br>
	 * This creates a 1px solid border in the specified color or removes the border from the cell if the color is <code>null</code>.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param colID the identifier of the column
	 * @param color the border color or <code>null</code> if the cell should not have a border
	 * @param width the line width of the border
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if width is <code>< 1</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public void setCellBorderByID(final int colID, final Color color, final int width) throws IllegalArgumentException {
		if(width < 1)
			throw new IllegalArgumentException("No valid argument!");
		
		setCellBorderByID(colID, (color != null) ? BorderFactory.createMatteBorder(width, width, width, width, color) : null);
	}
	
	/**
	 * Gets the item's custom border.
	 * <br><br>
	 * This could be used to highlight an item.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the border or <code>null</code> if the item has no custom border
	 * @since 1.0
	 */
	public ExecutionTableBorder getBorder() {
		if(EDT.isExecutedInEDT())
			return border;
		else
			return EDT.execute(new GuiRequest<ExecutionTableBorder>() {
				@Override
				protected ExecutionTableBorder execute() throws Throwable {
					return border;
				}
			});
	}
	
	/**
	 * Sets the item's custom border.
	 * <br><br>
	 * This could be used to highlight an item.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link ExecutionTable#setAutoRepaint(boolean)})!</b>
	 * 
	 * @param border the border or <code>null</code> if the item should not have a custom border (default)
	 * @since 1.0
	 */
	public void setBorder(final ExecutionTableBorder border) {
		if(EDT.isExecutedInEDT())
			this.border = border;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setBorder") {
				@Override
				protected void execute() throws Throwable {
					ExecutionTableItem.this.border = border;
				}
			});
		
		if(table != null && visible)
			table.autoRepaint();
	}
	
	/**
	 * Gets the index of the item in the list of all items of the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the index of the item in the list of all items or <code>-1</code> if the item is not even added to the table
	 * @since 1.0
	 */
	public final int getIndex() {
		if(EDT.isExecutedInEDT())
			return index;
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return index;
				}
			});
	}
	
	/**
	 * Indicates whether the item is editable.
	 * <br><br>
	 * By default no cells can be edited by the user. A cell is only editable when the related item <b>and</b> the related column are editable.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the item is editable otherwise <code>false</code>
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
	 * Sets whether the item is editable.
	 * <br><br>
	 * By default no cells can be edited by the user. A cell is only editable when the related item <b>and</b> the related column are editable.<br>
	 * You should specify a default input parser ({@link #setDefaultInputParser(InputParser)}) and/or cell input parsers ({@link #setCellInputParser(int, InputParser)})
	 * that the user input can be converted in correct cell objects.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #setDefaultInputParser(InputParser)
	 * @see #setCellInputParser(int, InputParser)
	 * @param editable <code>true</code> if the item should be editable otherwise <code>false</code>
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
					ExecutionTableItem.this.editable = editable;
					if(table != null) table.closeEditors();
				}
			});
	}
	
	/**
	 * Sets the default input parser for the cells.
	 * <br><br>
	 * The {@link InputParser} is used to convert user input into cell objects. If a cell does not have an {@link InputParser}
	 * the default one is used.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #setEditable(boolean)
	 * @see StringInputParser
	 * @see NumericInputParser
	 * @param parser the input parser
	 * @since 1.0
	 */
	public void setDefaultInputParser(final InputParser<?> parser) {
		if(EDT.isExecutedInEDT())
			defInputParser = parser;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setDefaultInputParser") {
				@Override
				protected void execute() throws Throwable {
					defInputParser = parser;
				}
			});
	}
	
	/**
	 * Sets the cell's input parser.
	 * <br><br>
	 * The {@link InputParser} is used to convert user input into cell objects. If a cell does not have an {@link InputParser}
	 * the default one is used.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #setEditable(boolean)
	 * @see #setDefaultInputParser(InputParser)
	 * @see StringInputParser
	 * @see NumericInputParser
	 * @param colIndex the column index
	 * @param parser the input parser
	 * @since 1.0
	 */
	public void setCellInputParser(final int colIndex, final InputParser<?> parser) {
		if(EDT.isExecutedInEDT())
			setListEntry(cellInputParsers, colIndex, parser);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setCellInputParser") {
				@Override
				protected void execute() throws Throwable {
					setListEntry(cellInputParsers, colIndex, parser);
				}
			});
	}
	
	/**
	 * Gets the cell's input parser.
	 * <br><br>
	 * The {@link InputParser} is used to convert user input into cell objects. If a cell does not have an {@link InputParser}
	 * the default one is used.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param colIndex the column index
	 * @return the input parser or the default one (which could be <code>null</code> if the cell does not have an input parser
	 * @since 1.0
	 */
	public InputParser<?> getCellInputParser(final int colIndex) {
		if(EDT.isExecutedInEDT()) {
			final InputParser<?> parser = getListEntry(cellInputParsers, colIndex);
			return (parser != null) ? parser : defInputParser;
		}
		else
			return EDT.execute(new GuiRequest<InputParser<?>>() {
				@Override
				protected InputParser<?> execute() throws Throwable {
					final InputParser<?> parser = getListEntry(cellInputParsers, colIndex);
					return (parser != null) ? parser : defInputParser;
				}
			});
	}
	
	/**
	 * Indicates whether the item is visible or not.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the item is displayed in the table otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isVisible() {
		if(EDT.isExecutedInEDT())
			return visible;
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return visible;
				}
			});
	}
	
	/**
	 * Sets whether whether the item is visible or not.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param visible <code>true</code> if the item should be displayed in the table otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setVisible(final boolean visible) {
		if(EDT.isExecutedInEDT())
			internalSetVisible(visible);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setVisible") {
				@Override
				protected void execute() throws Throwable {
					internalSetVisible(visible);
				}
			});
	}
	
	/**
	 * Gets the user data of the item.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the custom data
	 * @since 1.0
	 */
	public Object getUserData() {
		// we must not shift it to the EDT because this is only additional data that is not used by the item to display something
		return userData;
	}
	
	/**
	 * Sets the user data of the item.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param data the custom data that should be stored at the item
	 * @since 1.0
	 */
	public void setUserData(final Object data) {
		// we must not shift it to the EDT because this is only additional data that is not used by the item to display something
		userData = data;
	}
	
	/**
	 * Sets the index of the item in the list of all items of the table.
	 * <br><br>
	 * Furthermore the height of the item is updated by using {@link ExecutionTable#setItemHeight(int, int)}.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param index the index of the item in the list of all items
	 * @since 1.0
	 */
	final void setIndex(final int index) {
		this.index = index;
	}
	
	/**
	 * Gets the index that describes the normal order position (order position before sorting) of the item.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the unsorted order index
	 * @since 1.0
	 */
	final int getUnsortedOrderIndex() {
		return unsortedOrderIndex;
	}
	
	/**
	 * Gets the corresponding table.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the table or <code>null</code> if there is no table reference set now
	 * @since 1.0
	 */
	ExecutionTable getTable() {
		return table;
	}
	
	/**
	 * Gets the corresponding model.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the model or <code>null</code> if there is no model reference set now
	 * @since 1.0
	 */
	ExecutionTableModel getModel() {
		return model;
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
	void setTableAndModel(final ExecutionTable table, final ExecutionTableModel model) {
		this.table = table;
		this.model = model;
		
		if(table != null) {
			// load the cached data that could not be set because of no valid table reference
			final Iterator<String> it = parameterDataCache.keySet().iterator();
			String methodKey;
			
			while(it.hasNext()) {
				methodKey = it.next();
				invokeMethod(methodKey, parameterDataCache.get(methodKey));
			}
			
			// release parameter caches
			parameterDataCache.clear();
		}
	}
	
	/**
	 * Removes a cell from the item.
	 * <br><br>
	 * This method has to be invoked when a column is removed to delete the cell data.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param colIndex the column index
	 * @since 1.0
	 */
	final void removeCell(final int colIndex) {
		removeCell(cellBackgrounds, colIndex);
		removeCell(cellBorders, colIndex);
		removeCell(cellForegrounds, colIndex);
		removeCell(cellInputParsers, colIndex);
		removeCell(cellObjects, colIndex);
	}
	
	/**
	 * Removes a cell from a specfied list.
	 * 
	 * @param list the list
	 * @param colIndex the index of the cell
	 * @since 1.0
	 */
	private <T> void removeCell(final List<T> list, final int colIndex) {
		if(colIndex >= 0 && colIndex < list.size())
			list.remove(colIndex);
	}
	
	/**
	 * Gets the list entry at the specified index.
	 * 
	 * @param list the list
	 * @param index the index
	 * @return the entry or <code>null</code>
	 * @since 1.0
	 */
	private <T> T getListEntry(final List<T> list, final int index) {
		if(index < 0 || index >= list.size())
			return null;
		
		return list.get(index);
	}
	
	/**
	 * Sets the list entry at the specified index.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The list is automatically expanded if the index is greater then the list size.
	 * 
	 * @param list the list
	 * @param index the index
	 * @param entry the entry
	 * @since 1.0
	 */
	private <T> void setListEntry(final List<T> list, final int index, final T entry) {
		if(index < 0)
			return;
		
		for(int i = list.size(); i <= index; i++)
			list.add(null);
		
		list.set(index, entry);
	}
	
	/**
	 * Updates the parameter cache for the given method.
	 * 
	 * @param methodKey the method key like {@link #MK_SETCELLOBJECTBYID}, {@link #MK_SETCELLDATABYID}, ...
	 * @param pc the parameter cache
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if methodKey is null</li>
	 * 		<li>if pc is null</li>
	 * </ul>
	 * @since 1.0
	 */
	private void updateDataCache(final String methodKey, final ParameterCache pc) throws IllegalArgumentException {
		if(methodKey == null || pc == null)
			throw new IllegalArgumentException("No valid argument!");
		
		List<ParameterCache> pcs = parameterDataCache.get(methodKey);
		if(pcs == null) {
			pcs = new ArrayList<ParameterCache>(3);
			parameterDataCache.put(methodKey, pcs);
		}
		
		pcs.add(pc);
	}
	
	/**
	 * Invokes the specified method with the given list of cached parameters.
	 * 
	 * @param methodKey the method key like {@link #MK_SETCELLOBJECTBYID}, {@link #MK_SETCELLDATABYID}, ...
	 * @param pcs the list of cached parameters for the method
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	private void invokeMethod(final String methodKey, final List<ParameterCache> pcs) {
		if(pcs == null)
			return;
		
		for(ParameterCache pc : pcs) {
			switch(methodKey) {
				case MK_SETCELLOBJECTBYID:
					setCellObjectByID((int)pc.get("colID"), pc.get("object"));
					break;
				case MK_SETCELLDATABYID:
					setCellDataByID((Map<Integer, Object>)pc.get("data"));
					break;
				case MK_SETCELLBACKGROUNDBYID:
					setCellBackgroundByID((int)pc.get("colID"), (Color)pc.get("color"));
					break;
				case MK_SETCELLFOREGROUNDBYID:
					setCellForegroundByID((int)pc.get("colID"), (Color)pc.get("color"));
					break;
				case MK_SETCELLBORDERBYID:
					setCellBorderByID((int)pc.get("colID"), (Border)pc.get("border"));
					break;
			}
		}
	}
	
	/**
	 * Sets whether whether the item is visible or not.
	 * <br><br>
	 * <i>This method is only for internal use of the model!</i>.
	 * 
	 * @param visible <code>true</code> if the item should be displayed in the table otherwise <code>false</code>
	 * @since 1.0
	 */
	private void internalSetVisible(final boolean visible) {
		// the visibility state does not change? then do nothing
		if(this.visible == visible)
			return;
		
		this.visible = visible;
		
		if(model != null)
			model.updateVisibility(this);
	}
	
	/**
	 * Represents cached parameter data.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class ParameterCache {
		
		/** the data of the parameters */
		private final Map<String, Object> data;
		
		/**
		 * Creates a new parameter cache.
		 */
		public ParameterCache() {
			data = new HashMap<String, Object>(2);
		}
		
		/**
		 * Gets the parameter object of the given parameter key.
		 * 
		 * @param key the parameter key
		 * @return the parameter object
		 * @since 1.0
		 */
		public Object get(final String key) {
			return data.get(key);
		}
		
		/**
		 * Sets the parameter object of the given parameter key.
		 * 
		 * @param key the parameter key
		 * @param o the parameter object
		 * @since 1.0
		 */
		public void set(final String key, final Object o) {
			data.put(key, o);
		}
		
	}
	
	/**
	 * Represents a parser for user input.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 * @param <T> the type of object the parser parses
	 */
	public interface InputParser<T> {
		
		/**
		 * Prepares the editor of the input parser.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * This method can be used to mask objects that should be displayed in a different way in the editor of the input parser.
		 * 
		 * @param o the object that should be displayed in the editor of the input parser or <code>null</code> if there is currently no object that could be displayed in the editor
		 * @return the object that has to be displayed
		 * @since 1.0
		 */
		public Object prepareEditor(final Object o);
		
		/**
		 * Parses an input string into a concrete object.
		 * 
		 * @param input the input string
		 * @return the object or <code>null</code> if the input could not be parsed
		 * @since 1.0
		 */
		public T parse(final String input);

	}
	
	/**
	 * A default parser for string output.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public static class StringInputParser implements InputParser<String> {
		
		@Override
		public Object prepareEditor(Object o) {
			return o;
		}

		@Override
		public String parse(String input) {
			return input;
		}
		
	}
	
	/**
	 * A default parser for numeric output.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public static class NumericInputParser implements InputParser<Number> {
		
		/** the character (sequence) describing infinity */
		private final String infinityChar;
		/** the value describing infinity */
		private final Number infinityValue;
		
		/**
		 * Creates a new numeric input parser with <code>-</code> as the infinity character and {@link Float#POSITIVE_INFINITY}
		 * as the infinity value.
		 * 
		 * @since 1.0
		 */
		public NumericInputParser() throws IllegalArgumentException {
			this("-", Float.POSITIVE_INFINITY);
		}
		
		/**
		 * Creates a new numeric input parser with <code>-</code> as the infinity character.
		 * 
		 * @param infinityValue the value describing infinity
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if infinityValue is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public NumericInputParser(final Number infinityValue) throws IllegalArgumentException {
			this("-", infinityValue);
		}
		
		/**
		 * Creates a new numeric input parser.
		 * 
		 * @param infinityChar the character (sequence) describing infinity
		 * @param infinityValue the value describing infinity
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if infinityChar is null</li>
		 * 		<li>if infinityChar is empty</li>
		 * 		<li>if infinityValue is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public NumericInputParser(final String infinityChar, final Number infinityValue) throws IllegalArgumentException {
			if(infinityChar == null || infinityChar.isEmpty() || infinityValue == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.infinityChar = infinityChar;
			this.infinityValue = infinityValue;
		}
		
		@Override
		public Object prepareEditor(Object o) {
			if(o != null && o.equals(infinityValue))
				return infinityChar;
			else if(o!= null && o instanceof Number)
				return MathUtils.formatFloat(((Number)o).floatValue());
			else
				return o;
		}
		
		@Override
		public Number parse(String input) {
			if(input == null)
				return null;
			else if(input.equals(infinityChar))
				return infinityValue;
			else
				try {
					return NumberFormat.getInstance().parse(input);
				} catch (ParseException e) {
					return null;
				}
		}
		
	}

}
