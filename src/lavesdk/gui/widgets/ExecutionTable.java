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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.gui.widgets.ExecutionTableItem.InputParser;
import lavesdk.gui.widgets.enums.SelectionType;
import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;

/**
 * Represents an execution table.
 * <br><br>
 * The table consists of {@link ExecutionTableColumn}s and {@link ExecutionTableItem}s. You can modify the content and style of
 * table cells in the specific item.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #ExecutionTable(LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>EXECUTIONTABLE_SORTUP_BTN_TOOLTIP</i>: the tooltip text of a sort up button in a sortable execution table</li>
 * 		<li><i>EXECUTIONTABLE_SORTDOWN_BTN_TOOLTIP</i>: the tooltip text of a sort down button in a sortable execution table</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * <br><br>
 * <b>Renderers for cell contents</b>:<br>
 * The execution table has predefined renderers for cell objects of type {@link Number}, {@link Boolean} and {@link Icon}. All other cell object types
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
 * table.addColumnGroup(colGroup);
 * table.addItemGroup(itemGroup);
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
 * 
 * @author jdornseifer
 * @version 1.2
 * @since 1.0
 */
public class ExecutionTable extends BaseComponent {

	private static final long serialVersionUID = 1L;
	
	/** the scroll pane of the table */
	private final JScrollPane scrollPane;
	/** the table */
	private final JTable table;
	/** the model of the table */
	private final ExecutionTableModel model;
	/** the list of groups of the columns */
	private final List<ExecutionTableGroup> columnGroups;
	/** the list of groups of the items */
	private final List<ExecutionTableGroup> itemGroups;
	/** the grid border representing the grid lines of the table or <code>null</code> if no grid lines should be shown */
	private ExecutionTableBorder gridBorder;
	/** the default grid color */
	private final Color defGridColor;
	/** flag that indicates whether the table is initialized */
	private boolean initialized;
	/** the list of default renderer classes */
	private final List<Class<?>> cellRendererClasses;
	/** the list of default renderers */
	private final List<TableCellRenderer> cellRenderers;
	/** the default renderer if there is no suitable renderer in the list of default renderers */
	private final TableCellRenderer defCellRenderer;
	/** flag that indicates whether the table should be auto scrolled to the item that was added at last */
	private boolean autoScrollEnabled;
	/** flag that indicates whether auto repaint is enabled for the execution table */ 
	private boolean autoRepaint;
	/** the type of the selection in the table */
	private SelectionType selType;
	/** the height of the column header */
	private int columnHeaderHeight;
	/** the language dependent tooltip text for a sort up button */
	private final String toolTipSortUp;
	/** the language dependent tooltip text for a sort down button */
	private final String toolTipSortDown;
	/** flag that indicates whether the editors are currently being closed */
	private boolean closingEditors;
	
	/** the comparator to restore the unsorted order of the items (rows) */
	private static final Comparator<ExecutionTableItem> unsortedOrderComparator;
	/** the cell padding meaning the padding of the cell text to the cell's edge */
	private static final Border cellPaddingBorder;
	/** the icon for the sort up button */
	private static final Icon sortUpIcon;
	/** the icon for the sort down button */
	private static final Icon sortDownIcon;
	/** the width of the sort up icon */
	private static final int sortUpIconWidth;
	/** the width of the sort down icon */
	private static final int sortDownIconWidth;
	/** the default item height */
	private static final int DEF_ITEMHEIGHT = 20;
	
	static {
		unsortedOrderComparator = new Comparator<ExecutionTableItem>() {
			
			@Override
			public int compare(ExecutionTableItem item1, ExecutionTableItem item2) {
				return item1.getUnsortedOrderIndex() - item2.getUnsortedOrderIndex();
			}
		};
		
		cellPaddingBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
		sortUpIcon = Resources.getInstance().SORT_UP_ICON;
		sortUpIconWidth = (sortUpIcon != null) ? sortUpIcon.getIconWidth() : 0;
		sortDownIcon = Resources.getInstance().SORT_DOWN_ICON;
		sortDownIconWidth = (sortDownIcon != null) ? sortDownIcon.getIconWidth() : 0;
	}
	
	/**
	 * Creates a new execution table.
	 * 
	 * @since 1.0
	 */
	public ExecutionTable() {
		this(null, "");
	}
	
	/**
	 * Creates a new execution table.
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the table. The following language labels are available:
	 * <ul>
	 * 		<li><i>EXECUTIONTABLE_SORTUP_BTN_TOOLTIP</i>: the tooltip text of a sort up button in a sortable execution table</li>
	 * 		<li><i>EXECUTIONTABLE_SORTDOWN_BTN_TOOLTIP</i>: the tooltip text of a sort down button in a sortable execution table</li>
	 * </ul>
	 * 
	 * @param langFile the language file or <code>null</code> if the table should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @since 1.0
	 */
	public ExecutionTable(final LanguageFile langFile, final String langID) {
		initialized = false;
		model = new ExecutionTableModel();
		columnHeaderHeight = -1;
		table = new JTable(model) {
			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				return ExecutionTable.this.prepareRenderer(renderer, row, column);
			}
			
			@Override
			public Component prepareEditor(TableCellEditor editor, int row, int column) {
				return ExecutionTable.this.prepareEditor(editor, row, column);
			}
			
			@Override
			public boolean editCellAt(int row, int column, EventObject e) {
				final boolean res = super.editCellAt(row, column, e);

				// if the current editor is a text field then select the content so that the user can directly input new data
				final Component editor = getEditorComponent();
				if(editor != null && editor instanceof JTextComponent) {
					// if the editor is opened using a mouse click (respectively a double click) then the select action has to be appended
					if(e instanceof MouseEvent)
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								((JTextComponent)editor).selectAll();
							}
						});
					else
						((JTextComponent)editor).selectAll();
				}
				
				return res;
			}
			
			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				return ExecutionTable.this.getCellRenderer(row, column);
			}
			
			@Override
			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					private static final long serialVersionUID = 1L;
					
					@Override
					public Dimension getPreferredSize() {
						final Dimension size = super.getPreferredSize();
						if(columnHeaderHeight > 0)
							size.height = columnHeaderHeight;
						
						return size;
					}

					@Override
					public String getToolTipText(MouseEvent event) {
						final Point p = event.getPoint();
						final int colIndex = columnModel.getColumnIndexAtX(p.x);
						final String name = (colIndex >= 0) ? ExecutionTable.this.model.getColumnName(colIndex) : "";
						
						if(name.isEmpty())
							return null;
						else
							return "<html>" + name + "</html>";
					}
				};
			}
			
		};
		scrollPane = new JScrollPane(table);
		columnGroups = new ArrayList<ExecutionTableGroup>(3);
		itemGroups = new ArrayList<ExecutionTableGroup>(3);
		gridBorder = new ExecutionTableBorder(1, table.getGridColor());
		defGridColor = table.getGridColor();
		cellRendererClasses = new ArrayList<Class<?>>();
		cellRenderers = new ArrayList<TableCellRenderer>();
		defCellRenderer = new DefaultTableCellRenderer();
		autoScrollEnabled = true;
		autoRepaint = false;
		selType = SelectionType.NONE;
		closingEditors = false;
		
		// load the language dependent tooltips of the sort buttons
		toolTipSortUp = LanguageFile.getLabel(langFile, "EXECUTIONTABLE_SORTUP_BTN_TOOLTIP", langID, "Up");
		toolTipSortDown = LanguageFile.getLabel(langFile, "EXECUTIONTABLE_SORTDOWN_BTN_TOOLTIP", langID, "Down");
		
		super.setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		
		// columns may not be movable or sortable by column header
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoCreateRowSorter(false);
		table.setRowHeight(DEF_ITEMHEIGHT);
		// column names should be displayed in the center and it should be possible to render html
		final DefaultTableCellRenderer tableHeaderRenderer = (DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer();
		table.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				final JLabel label = (JLabel)tableHeaderRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				// text should be centered and html formatted
				label.setHorizontalAlignment(JLabel.CENTER);
				label.setText("<html>" + label.getText() + "</html>");
				
				return label;
			}
		});
		// the grid lines are custom rendered
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		// by default nothing can be selected
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// add default renderers
		addDefaultCellRenderer(Number.class, new NumericCellRenderer());
		addDefaultCellRenderer(Boolean.class, table.getDefaultRenderer(Boolean.class));
		addDefaultCellRenderer(Icon.class, table.getDefaultRenderer(Icon.class));
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				ExecutionTable.this.mouseReleased(e);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				ExecutionTable.this.mouseExited(e);
			}
		});
		table.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				ExecutionTable.this.mouseMoved(e);
			}
		});
		
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
	 * If you enable this option the execution table invokes {@link #repaint()} automatically if a method is invoked that is marked as
	 * auto repaintable.
	 * 
	 * @param autoRepaint <code>true</code> if auto repaint should be enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setAutoRepaint(final boolean autoRepaint) {
		this.autoRepaint = autoRepaint;
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
		if(c == null || renderer == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			internalAddDefaultCellRenderer(c, renderer);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".addDefaultCellRenderer") {
				@Override
				protected void execute() throws Throwable {
					internalAddDefaultCellRenderer(c, renderer);
				}
			});
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
		if(EDT.isExecutedInEDT())
			return gridBorder;
		else
			return EDT.execute(new GuiRequest<ExecutionTableBorder>() {
				@Override
				protected ExecutionTableBorder execute() throws Throwable {
					return gridBorder;
				}
			});
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
		if(EDT.isExecutedInEDT())
			gridBorder = border;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setGridBorder") {
				@Override
				protected void execute() throws Throwable {
					gridBorder = border;
				}
			});
		
		autoRepaint();
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
		if(EDT.isExecutedInEDT())
			return defGridColor;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return defGridColor;
				}
			});
	}
	
	/**
	 * Gets the height of the items (rows).
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the item height
	 * @since 1.0
	 */
	public int getItemHeight() {
		if(EDT.isExecutedInEDT())
			return table.getRowHeight();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return table.getRowHeight();
				}
			});
	}
	
	/**
	 * Sets the height of the items (rows).
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param height the item height
	 * @since 1.0
	 */
	public void setItemHeight(final int height) {
		if(EDT.isExecutedInEDT())
			table.setRowHeight(height);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setItemHeight") {
				@Override
				protected void execute() throws Throwable {
					table.setRowHeight(height);
				}
			});
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
		if(height < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT()) {
			columnHeaderHeight = height;
			scrollPane.revalidate();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setColumnHeaderHeight") {
				@Override
				protected void execute() throws Throwable {
					columnHeaderHeight = height;
					scrollPane.revalidate();
				}
			});
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
		if(EDT.isExecutedInEDT())
			return table.getAutoResizeMode() == JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return table.getAutoResizeMode() == JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
				}
			});
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
		if(EDT.isExecutedInEDT())
			table.setAutoResizeMode(enabled ? JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS : JTable.AUTO_RESIZE_OFF);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setAutoResizeColumns") {
				@Override
				protected void execute() throws Throwable {
					table.setAutoResizeMode(enabled ? JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS : JTable.AUTO_RESIZE_OFF);
				}
			});
	}
	
	/**
	 * Indicates whether the execution table is sortable meaning that a user can sort the items using buttons at each item
	 * to change the position up/down.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the execution table is sortable otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isSortable() {
		if(EDT.isExecutedInEDT())
			return model.isSortable();
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return model.isSortable();
				}
			});
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
		if(EDT.isExecutedInEDT())
			model.setSortable(sortable);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setSortable") {
				@Override
				protected void execute() throws Throwable {
					model.setSortable(sortable);
				}
			});
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
		if(EDT.isExecutedInEDT())
	    	return autoScrollEnabled;
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
					protected Boolean execute() throws Throwable {
						return autoScrollEnabled;
				}
			});
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
		if(EDT.isExecutedInEDT())
			autoScrollEnabled = autoScroll;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setAutoScrollEnabled") {
				@Override
				protected void execute() throws Throwable {
					autoScrollEnabled = autoScroll;
				}
		});
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
		if(EDT.isExecutedInEDT())
	    	return selType;
		else
			return EDT.execute(new GuiRequest<SelectionType>() {
				@Override
					protected SelectionType execute() throws Throwable {
						return selType;
				}
			});
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
		if(type == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT()) {
			table.setCellSelectionEnabled(type == SelectionType.CELL);
			table.setRowSelectionAllowed(type == SelectionType.ROW || type == SelectionType.ROWS);
			table.getSelectionModel().setSelectionMode((type == SelectionType.ROWS) ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setSelectionType") {
				@Override
				protected void execute() throws Throwable {
					table.setCellSelectionEnabled(type == SelectionType.CELL);
					table.setRowSelectionAllowed(type == SelectionType.ROW || type == SelectionType.ROWS);
					table.getSelectionModel().setSelectionMode((type == SelectionType.ROWS) ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);
				}
		});
		
		// clear the current selection
		clearSelection();
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
		if(EDT.isExecutedInEDT())
			return (table.getSelectedColumn() >= 0) ? getColumn(table.convertColumnIndexToModel(table.getSelectedColumn())) : null;
		else
			return EDT.execute(new GuiRequest<ExecutionTableColumn>() {
				@Override
				protected ExecutionTableColumn execute() throws Throwable {
					return (table.getSelectedColumn() >= 0) ? getColumn(table.convertColumnIndexToModel(table.getSelectedColumn())) : null;
				}
			});
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
		if(EDT.isExecutedInEDT())
			return (table.getSelectedRow() >= 0) ? getItem(table.convertRowIndexToModel(table.getSelectedRow())) : null;
		else
			return EDT.execute(new GuiRequest<ExecutionTableItem>() {
				@Override
				protected ExecutionTableItem execute() throws Throwable {
					return (table.getSelectedRow() >= 0) ? getItem(table.convertRowIndexToModel(table.getSelectedRow())) : null;
				}
			});
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
		if(EDT.isExecutedInEDT())
			return table.getSelectedRowCount();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return table.getSelectedRowCount();
				}
			});
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
		if(EDT.isExecutedInEDT())
			return getItem(table.convertRowIndexToModel(table.getSelectedRows()[index]));
		else
			return EDT.execute(new GuiRequest<ExecutionTableItem>() {
				@Override
				protected ExecutionTableItem execute() throws Throwable {
					return getItem(table.convertRowIndexToModel(table.getSelectedRows()[index]));
				}
			});
	}
	
	/**
	 * Clears the selection of the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void clearSelection() {
		if(EDT.isExecutedInEDT())
			table.clearSelection();
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".clearSelection") {
				@Override
				protected void execute() throws Throwable {
					table.clearSelection();
				}
			});
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
	 * 		<li>if their already exists another column with the same identifier</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final ExecutionTableColumn column) throws IllegalArgumentException {
		if(EDT.isExecutedInEDT())
			model.add(column);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".add") {
				@Override
				protected void execute() throws Throwable {
					model.add(column);
				}
			});
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
	 * 		<li>if there are two different columns with the same identifier</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final ExecutionTableColumn[] columns) throws IllegalArgumentException {
		if(EDT.isExecutedInEDT())
			model.add(columns);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".add") {
				@Override
				protected void execute() throws Throwable {
					model.add(columns);
				}
			});
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
	 * 		<li>if their already exists another item with the same identifier</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final ExecutionTableItem item) throws IllegalArgumentException {
		if(EDT.isExecutedInEDT())
			model.add(-1, item);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".add") {
				@Override
				protected void execute() throws Throwable {
					model.add(-1, item);
				}
			});
	}
	
	/**
	 * Adds a new item to the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index index at which the given item should be inserted or <code>-1</code> to add the item at the end of the rows
	 * @param item the item
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if item is null</li>
	 * 		<li>if item is already added to another table</li>
	 * 		<li>if their already exists another item with the same identifier</li>
	 * </ul>
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the specified index is out of bounds</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final int index, final ExecutionTableItem item) throws IllegalArgumentException, IndexOutOfBoundsException {
		if(EDT.isExecutedInEDT())
			model.add(index, item);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".add") {
				@Override
				protected void execute() throws Throwable {
					model.add(index, item);
				}
			});
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
	 * 		<li>if there are two different items with the same identifier</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final ExecutionTableItem[] items) throws IllegalArgumentException {
		if(EDT.isExecutedInEDT())
			model.add(items);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".add") {
				@Override
				protected void execute() throws Throwable {
					model.add(items);
				}
			});
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
		if(EDT.isExecutedInEDT())
			model.remove(column);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".remove") {
				@Override
				protected void execute() throws Throwable {
					model.remove(column);
				}
			});
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
		if(EDT.isExecutedInEDT())
			model.remove(item);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".remove") {
				@Override
				protected void execute() throws Throwable {
					model.remove(item);
				}
			});
	}
	
	/**
	 * Removes all columns from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllColumns() {
		if(EDT.isExecutedInEDT())
			model.removeAllColumns();
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeAllColumns") {
				@Override
				protected void execute() throws Throwable {
					model.removeAllColumns();
				}
			});
	}
	
	/**
	 * Removes all items from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllItems() {
		if(EDT.isExecutedInEDT())
			model.removeAllItems();
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeAllItems") {
				@Override
				protected void execute() throws Throwable {
					model.removeAllItems();
				}
			});
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
		if(EDT.isExecutedInEDT())
			return model.getRealColumnCount();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return model.getRealColumnCount();
				}
			});
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
		if(EDT.isExecutedInEDT())
			return model.getRealColumn(index, false);
		else
			return EDT.execute(new GuiRequest<ExecutionTableColumn>() {
				@Override
				protected ExecutionTableColumn execute() throws Throwable {
					return model.getRealColumn(index, false);
				}
			});
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
		if(EDT.isExecutedInEDT())
			return model.getRealColumnByID(id);
		else
			return EDT.execute(new GuiRequest<ExecutionTableColumn>() {
				@Override
				protected ExecutionTableColumn execute() throws Throwable {
					return model.getRealColumnByID(id);
				}
			});
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
		if(EDT.isExecutedInEDT())
			return (model.getRealColumnCount() > 0) ? model.getRealColumn(0, false) : null;
		else
			return EDT.execute(new GuiRequest<ExecutionTableColumn>() {
				@Override
				protected ExecutionTableColumn execute() throws Throwable {
					return (model.getRealColumnCount() > 0) ? model.getRealColumn(0, false) : null;
				}
			});
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
		if(EDT.isExecutedInEDT())
			return (model.getRealColumnCount() > 0) ? model.getRealColumn(model.getRealColumnCount() - 1, false) : null;
		else
			return EDT.execute(new GuiRequest<ExecutionTableColumn>() {
				@Override
				protected ExecutionTableColumn execute() throws Throwable {
					return (model.getRealColumnCount() > 0) ? model.getRealColumn(model.getRealColumnCount() - 1, false) : null;
				}
			});
	}
	
	/**
	 * Gets the number of items of the table.
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
		if(EDT.isExecutedInEDT())
			return model.getItemCount();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return model.getItemCount();
				}
			});
	}
	
	/**
	 * Gets the item at the specified index.
	 * <br><br>
	 * <b>Notice</b>:<br>
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
		if(EDT.isExecutedInEDT())
			return model.getItem(index);
		else
			return EDT.execute(new GuiRequest<ExecutionTableItem>() {
				@Override
				protected ExecutionTableItem execute() throws Throwable {
					return model.getItem(index);
				}
			});
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
		if(EDT.isExecutedInEDT())
			return model.getItemByID(id);
		else
			return EDT.execute(new GuiRequest<ExecutionTableItem>() {
				@Override
				protected ExecutionTableItem execute() throws Throwable {
					return model.getItemByID(id);
				}
			});
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
		if(EDT.isExecutedInEDT())
			return (model.getItemCount() > 0) ? model.getItem(0) : null;
		else
			return EDT.execute(new GuiRequest<ExecutionTableItem>() {
				@Override
				protected ExecutionTableItem execute() throws Throwable {
					return (model.getItemCount() > 0) ? model.getItem(0) : null;
				}
			});
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
		if(EDT.isExecutedInEDT())
			return (model.getItemCount() > 0) ? model.getItem(model.getItemCount() - 1) : null;
		else
			return EDT.execute(new GuiRequest<ExecutionTableItem>() {
				@Override
				protected ExecutionTableItem execute() throws Throwable {
					return (model.getItemCount() > 0) ? model.getItem(model.getItemCount() - 1) : null;
				}
			});
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
		if(EDT.isExecutedInEDT())
			return model.getRowCount();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return model.getRowCount();
				}
			});
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
		if(EDT.isExecutedInEDT())
			return model.getVisibleRow(index);
		else
			return EDT.execute(new GuiRequest<ExecutionTableItem>() {
				@Override
				protected ExecutionTableItem execute() throws Throwable {
					return model.getVisibleRow(index);
				}
			});
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
		if(EDT.isExecutedInEDT())
			return model.getVisibleRowByID(id);
		else
			return EDT.execute(new GuiRequest<ExecutionTableItem>() {
				@Override
				protected ExecutionTableItem execute() throws Throwable {
					return model.getVisibleRowByID(id);
				}
			});
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
		if(EDT.isExecutedInEDT())
			return model.convertItemIndexToVisible(index);
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return model.convertItemIndexToVisible(index);
				}
			});
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
		if(EDT.isExecutedInEDT())
			model.sortItems(colIndex, order);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".sortItems") {
				@Override
				protected void execute() throws Throwable {
					model.sortItems(colIndex, order);
				}
			});
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
		if(group == null || columnGroups.contains(group))
			return;

		if(EDT.isExecutedInEDT())
			columnGroups.add(group);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".addColumnGroup") {
				@Override
				protected void execute() throws Throwable {
					columnGroups.add(group);
				}
			});
		
		autoRepaint();
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
		if(!columnGroups.contains(group))
			return;
		
		if(EDT.isExecutedInEDT())
			columnGroups.remove(group);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeColumnGroup") {
				@Override
				protected void execute() throws Throwable {
					columnGroups.remove(group);
				}
			});
		
		autoRepaint();
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
		if(EDT.isExecutedInEDT())
			return columnGroups.size();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return columnGroups.size();
				}
			});
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
		if(EDT.isExecutedInEDT())
			return columnGroups.get(index);
		else
			return EDT.execute(new GuiRequest<ExecutionTableGroup>() {
				@Override
				protected ExecutionTableGroup execute() throws Throwable {
					return columnGroups.get(index);
				}
			});
	}
	
	/**
	 * Removes all column groups from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllColumnGroups() {
		if(EDT.isExecutedInEDT())
			columnGroups.clear();
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeAllColumnGroups") {
				@Override
				protected void execute() throws Throwable {
					columnGroups.clear();
				}
			});
		
		autoRepaint();
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
		if(group == null || itemGroups.contains(group))
			return;
		
		if(EDT.isExecutedInEDT())
			itemGroups.add(group);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".addItemGroup") {
				@Override
				protected void execute() throws Throwable {
					itemGroups.add(group);
				}
			});
		
		autoRepaint();
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
		if(!itemGroups.contains(group))
			return;
		
		if(EDT.isExecutedInEDT())
			itemGroups.remove(group);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeItemGroup") {
				@Override
				protected void execute() throws Throwable {
					itemGroups.remove(group);
				}
			});
		
		autoRepaint();
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
		if(EDT.isExecutedInEDT())
			return itemGroups.size();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return itemGroups.size();
				}
			});
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
		if(EDT.isExecutedInEDT())
			return itemGroups.get(index);
		else
			return EDT.execute(new GuiRequest<ExecutionTableGroup>() {
				@Override
				protected ExecutionTableGroup execute() throws Throwable {
					return itemGroups.get(index);
				}
			});
	}
	
	/**
	 * Removes all item groups from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllItemGroups() {
		if(EDT.isExecutedInEDT())
			itemGroups.clear();
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeAllItemGroups") {
				@Override
				protected void execute() throws Throwable {
					itemGroups.clear();
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * The layout of an execution table may not be changed meaning this method does nothing!
	 * 
	 * @param mgr the layout manager
	 * @since 1.0
	 */
	@Override
	public void setLayout(LayoutManager mgr) {
		// this is not allowed
	}
	
	/**
	 * Removes all columns, items and groups from the table.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	@Override
	public void removeAll() {
		removeAllColumns();
		removeAllItems();
		removeAllColumnGroups();
		removeAllItemGroups();
	}
	
	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		if(!initialized)
			super.addImpl(comp, constraints, index);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void repaintComponent() {
		super.repaintComponent();
		table.repaint();
	}
	
	/**
	 * Repaints the table but only if auto repainting is allowed.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @since 1.0
	 */
	void autoRepaint() {
		if(autoRepaint) {
			if(getParent() != null)
				getParent().repaint();
			else
				table.repaint();
		}
	}
	
	/**
	 * Sets the width of the specified column.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param index the real index of the column
	 * @param width the width
	 * @since 1.0
	 */
	void setColumnWidth(final int index, final int width) {
		final int colIndex = model.convertRealIndexToColumn(index);
		
		if(colIndex < table.getColumnModel().getColumnCount())
			table.getColumnModel().getColumn(colIndex).setPreferredWidth(width);
	}
	
	/**
	 * Closes all editors that are open.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @since 1.0
	 */
	void closeEditors() {
		// the close method may be invoked during a close process that means we have to skip if we are currently in such a process
		if(closingEditors)
			return;
		
		if(EDT.isExecutedInEDT()) {
			closingEditors = true;
			table.editCellAt(-1, -1);
			closingEditors = false;
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".closeEditors") {
				@Override
				protected void execute() throws Throwable {
					closingEditors = true;
					table.editCellAt(-1, -1);
					closingEditors = false;
				}
			});
	}
	
	/**
	 * The mouse was released on the table.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseReleased(final MouseEvent e) {
		// if the sort mode is off then we do not need to check whether a sort up/down button is clicked
		if(!model.isSortable())
			return;
		
		final int rowAtPoint = table.rowAtPoint(e.getPoint());
		final int colAtPoint = table.columnAtPoint(e.getPoint());
		
		// if there is no cell under the current mouse position then a sort button cannot be pressed
		if(rowAtPoint < 0 || colAtPoint < 0)
			return;
		
		final int rowInModel = table.convertRowIndexToModel(rowAtPoint);
		final int colInModel = table.convertColumnIndexToModel(colAtPoint);
		final int realColIndex = model.convertColumnIndexToReal(colInModel);
		
		if(model.isSortUpColumn(realColIndex))
			model.sortUp(rowInModel);
		else if(model.isSortDownColumn(realColIndex))
			model.sortDown(rowInModel);
	}
	
	/**
	 * The mouse was released on the table.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseMoved(final MouseEvent e) {
		// if sort mode is off then we do not need to change the cursor of the table if a sort up/down button is hovered
		if(!model.isSortable())
			return;
		
		final int rowAtPoint = table.rowAtPoint(e.getPoint());
		final int colAtPoint = table.columnAtPoint(e.getPoint());
		
		// if there is no cell under the current mouse position then the cursor cannot be changed
		if(rowAtPoint < 0 || colAtPoint < 0) {
			table.setCursor(Cursor.getDefaultCursor());
			return;
		}
		
		final int colInModel = table.convertColumnIndexToModel(colAtPoint);
		final int realColIndex = model.convertColumnIndexToReal(colInModel);
		
		if(model.isSortColumn(realColIndex))
			table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		else
			table.setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * The mouse was released on the table.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseExited(final MouseEvent e) {
		if(!model.isSortable())
			return;
		
		// in sort mode the cursor is changed if a user hovers a sort up or a sort down button so if the cursor
		// exits the table then reset the default cursor
		table.setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * Gets the renderer for a specific cell.
	 * 
	 * @param row the row
	 * @param column the column
	 * @return the cell renderer
	 * @since 1.0
	 */
	private TableCellRenderer getCellRenderer(final int row, final int column) {
		final int rowInModel = table.convertRowIndexToModel(row);
		final int colInModel = table.convertColumnIndexToModel(column);
		final ExecutionTableColumn col = model.getRealColumn(colInModel, true);
		Object cellObject = model.getValueAt(rowInModel, colInModel);
		final Mask cellMask = (col != null) ? col.getMask(cellObject) : null;
		
		// check if there is a mask for the cell object and if so change the cell object to the mask value
		if(cellMask != null) {
			switch(cellMask.getType()) {
				case OBJECT_MASK:
					cellObject = cellMask.getMaskObject();
					break;
				case ICON_MASK:
					cellObject = cellMask.getMaskIcon();
					break;
			}
		}
		
		// get a specific renderer for each object in the table
		return (cellObject != null) ? getDefaultCellRenderer(cellObject.getClass()) : defCellRenderer;
	}
	
	/**
	 * Prepares the renderer of a table cell.
	 * 
	 * @param renderer the cell renderer
	 * @param row the row index
	 * @param column the column index
	 * @return the modified cell component
	 * @since 1.0
	 */
	private Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
		final int rowInModel = table.convertRowIndexToModel(row);
		final int colInModel = table.convertColumnIndexToModel(column);
		final ExecutionTableItem item = model.getVisibleRow(rowInModel);
		final ExecutionTableColumn col = model.getRealColumn(colInModel, true);
		final boolean cellSelected = (col != null) ? table.isCellSelected(row, column) : false;	// sort columns may not be selected
		final Object cellObject = model.getValueAt(rowInModel, colInModel);
		final Mask cellMask = (col != null) ? col.getMask(cellObject) : null;
		Object cellValue = cellObject;
		
		// if the cell object matches to a mask then get the mask object (mask icons are rendered by the icon cell renderer of the table)
		if(cellMask != null) {
			switch(cellMask.getType()) {
				case OBJECT_MASK:
					cellValue = cellMask.getMaskObject();
					break;
				case ICON_MASK:
					cellValue = cellMask.getMaskIcon();
					break;
			}
		}
		
		// get the component of the cell
		final Component component = renderer.getTableCellRendererComponent(table, cellValue, cellSelected, false, row, column);
		
		// load the cell's borders which is only possible if we have a JComponent
		if(component instanceof JComponent) {
			final JComponent jcomponent = (JComponent)component;
			final ExecutionTableBorder itemBorder = item.getBorder();
			final Border cellInnerBorder = (col != null) ? item.getCellBorder(col.getIndex()) : null;
			ExecutionTableBorder colBorder = (col != null) ? getGroupBorder(columnGroups, col.getIndex()) : null;
			ExecutionTableBorder rowBorder = getGroupBorder(itemGroups, rowInModel);
			Border cellBorder;
			
			// if there is no column or row border then use the defined grid border
			if(colBorder == null)
				colBorder = gridBorder;
			if(rowBorder == null)
				rowBorder = gridBorder;
			
			// the item border has a higher priority than the group borders!
			//(display the item border only in cells that are unequal a cell of a sort column)
			if(itemBorder != null && col != null) {
				// create a complete border if we only have one column, create a right open or left open border if it is
				// the first respectively the last column or mix the item and column border if necessary
				if(model.getRealColumnCount() == 1)
					cellBorder = ExecutionTableBorder.createBorder(itemBorder, itemBorder, itemBorder, itemBorder);
				else if(col.getIndex() == 0)
					cellBorder = ExecutionTableBorder.createBorder(itemBorder, colBorder, itemBorder, itemBorder);
				else if(col.getIndex() == model.getRealColumnCount() - 1)
					cellBorder = ExecutionTableBorder.createBorder(null, itemBorder, itemBorder, itemBorder);
				else
					cellBorder = ExecutionTableBorder.createBorder(null, colBorder, itemBorder, itemBorder);
			}
			else
				cellBorder = ExecutionTableBorder.createBorder(null, colBorder, null, rowBorder);
			
			// the cell should have an inner border then compound the outer and the inner border
			if(cellInnerBorder != null)
				cellBorder = BorderFactory.createCompoundBorder(cellBorder, cellInnerBorder);
			
			// the cell should have a padding so create a compound border
			jcomponent.setBorder(BorderFactory.createCompoundBorder(cellBorder, cellPaddingBorder));
			
			// if the column is a sort column then show a tooltip text that labels the sort button
			if(col == null) {
				if(model.isSortUpColumn(model.convertColumnIndexToReal(colInModel)))
					jcomponent.setToolTipText(toolTipSortUp);
				else if(model.isSortDownColumn(model.convertColumnIndexToReal(colInModel)))
					jcomponent.setToolTipText(toolTipSortDown);
			}
		}
		
		if(col != null) {
			// set the background and foreground only if the cell is not selected otherwise the background and foreground
			// matches the selection colors
			if(!cellSelected) {
				component.setBackground(item.getCellBackground(col.getIndex()));
				component.setForeground(item.getCellForeground(col.getIndex()));
			}
			
			// labels and buttons are the only components that can have a horizontal alignment
			if(component instanceof JLabel)
				((JLabel)component).setHorizontalAlignment(col.getAlignment());
			else if(component instanceof AbstractButton)
				((AbstractButton)component).setHorizontalAlignment(col.getAlignment());
		}
		
		return component;
	}
	
	/**
	 * Prepares the editor of a cell.
	 * 
	 * @param editor the editor
	 * @param row the row
	 * @param column the column
	 * @return the editor component
	 * @since 1.0
	 */
	private Component prepareEditor(TableCellEditor editor, int row, int column) {
		final int rowInModel = table.convertRowIndexToModel(row);
		final int colInModel = table.convertColumnIndexToModel(column);
		final ExecutionTableItem item = model.getVisibleRow(rowInModel);
		final ExecutionTableColumn col = model.getRealColumn(colInModel, true);
		final boolean cellSelected = table.isCellSelected(row, column);
		final Object cellObject = model.getValueAt(rowInModel, colInModel);
		
		/*
		 * INFO:
		 * Sort columns may not be edited so a sort column can never prepare an editor!
		 */
		
		return editor.getTableCellEditorComponent(table, item.getCellInputParser(col.getIndex()).prepareEditor(cellObject), cellSelected, row, column);
	}
	
	/**
	 * Gets a group border for a specific index.
	 * 
	 * @param groups the list of groups
	 * @param index the index of the group object
	 * @return the border or <code>null</code> if the object does not have a group border
	 * @since 1.0
	 */
	private ExecutionTableBorder getGroupBorder(final List<ExecutionTableGroup> groups, final int index) {
		int normIndex;
		
		for(ExecutionTableGroup group : groups) {
			normIndex = index - group.getStart() + 1;
			
			if(normIndex < 1 || (!group.isRepeatable() && normIndex > group.getAmount()))
				continue;
			
			if(normIndex % group.getAmount() == 0)
				return group.getBorder();
		}
		
		return null;
	}
	
	/**
	 * Adds a new default renderer for the specified class.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param c a valid class
	 * @param renderer a valid renderer
	 * @since 1.0
	 */
	private void internalAddDefaultCellRenderer(final Class<?> c, final TableCellRenderer renderer) {
		int index = getDefaultCellRendererIndex(c);
		
		// if there is no renderer for the specific class then add the new renderer to the beginning of the list
		// this makes it possible to use renderers for more specific class (like a Double renderer although therr is a renderer for Number)
		if(index < 0) {
			cellRendererClasses.add(0, c);
			cellRenderers.add(0, renderer);
		}
		else {
			cellRendererClasses.set(0, c);
			cellRenderers.set(0, renderer);
		}
	}
	
	/**
	 * Gets the index of the cell renderer that is associated with the specified class.
	 * 
	 * @param c the class
	 * @return the index or <code>-1</code> if there is no renderer for the specified class
	 * @since 1.0
	 */
	private int getDefaultCellRendererIndex(final Class<?> c) {
		int index = -1;
		
		if(c != null) {
			for(int i = 0; i < cellRendererClasses.size(); i++) {
				if(cellRendererClasses.get(i).isAssignableFrom(c)) {
					index = i;
					break;
				}
			}
		}
		
		return index;
	}
	
	/**
	 * Gets the cell renderer for the specified class.
	 * 
	 * @param c the class
	 * @return the associated renderer or the default cell render if there is no renderer for the specified class
	 * @since 1.0
	 */
	private TableCellRenderer getDefaultCellRenderer(final Class<?> c) {
		final int index = getDefaultCellRendererIndex(c);
		
		return (index >= 0) ? cellRenderers.get(index) : defCellRenderer;
	}
	
	/**
	 * Scrolls the table to the specified item.
	 * 
	 * @param item the item
	 * @since 1.0
	 */
	private void scrollToItem(final ExecutionTableItem item) {
		scrollPane.getViewport().scrollRectToVisible(table.getCellRect(item.getIndex(), table.convertColumnIndexToView(0), true));
	}
	
	/**
	 * The model of the execution table.
	 * 
	 * @author jdornseifer
	 * @version 1.2
	 * @since 1.0
	 */
	class ExecutionTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		
		/** the list of columns */
		private final List<ExecutionTableColumn> columns;
		/** the mapping between the identifiers (key) and their columns (value) */
		private final Map<Integer, ExecutionTableColumn> columnsByID;
		/** the list of rows/items */
		private final List<ExecutionTableItem> items;
		/** the mapping between the identifiers (key) and their items (value) */
		private final Map<Integer, ExecutionTableItem> itemsByID;
		/** the list of visible rows/items */
		private final List<ExecutionTableItem> visibleRows;
		/** flag that indicates whether the items are sortable meaning that a user can sort the items using buttons for position up/down */
		private boolean sortable;
		
		/**
		 * Creates a new model.
		 * 
		 * @since 1.0
		 */
		public ExecutionTableModel() {
			columns = new ArrayList<ExecutionTableColumn>(5);
			columnsByID = new HashMap<Integer, ExecutionTableColumn>();
			items = new ArrayList<ExecutionTableItem>();
			itemsByID = new HashMap<Integer, ExecutionTableItem>();
			visibleRows = new ArrayList<ExecutionTableItem>();
			sortable = false;
		}
		
		/**
		 * Adds a new column to the model.
		 * 
		 * @param column the column
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if column is null</li>
		 * 		<li>if column is already added to another table</li>
		 * 		<li>if their already exists another column with the same identifier</li>
		 * </ul>
		 * @since 1.0
		 */
		public void add(final ExecutionTableColumn column) throws IllegalArgumentException {
			internalAdd(column);
			fireTableStructureChanged();
		}
		
		/**
		 * Adds new columns to the model.
		 * 
		 * @param columns the list of columns to be added
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if a column is null</li>
		 * 		<li>if a column is already added to another table</li>
		 * 		<li>if their two different columns with the same identifier</li>
		 * </ul>
		 * @since 1.0
		 */
		public void add(final ExecutionTableColumn[] columns) throws IllegalArgumentException {
			if(columns == null)
				return;
			
			for(ExecutionTableColumn column : columns)
				internalAdd(column);
			
			fireTableStructureChanged();
		}
		
		/**
		 * Removes the given column from the model.
		 * 
		 * @param column the column
		 * @since 1.0
		 */
		public void remove(final ExecutionTableColumn column) {
			if(column == null || column.getTable() != ExecutionTable.this)
				return;
			
			// firstly close the opened editors so that the values and indices of the items are correct
			ExecutionTable.this.closeEditors();
			
			columns.remove(column.getIndex());
			if(column.getID() != -1)
				columnsByID.remove(column.getID());
			
			// remove the cell of the column from the items
			for(int i = 0; i < items.size(); i++)
				items.get(i).removeCell(column.getIndex());
			
			// update the indices of the columns after the removed one
			for(int i = column.getIndex(); i < columns.size(); i++)
				columns.get(i).setIndex(i);
			
			// afterwards reset the table and index
			column.setTable(null);
			column.setIndex(-1);
			
			fireTableStructureChanged();
		}
		
		/**
		 * Removes all columns from the model.
		 * 
		 * @since 1.0
		 */
		public void removeAllColumns() {
			// firstly close the opened editors so that the values and indices of the items are correct
			ExecutionTable.this.closeEditors();
			
			columns.clear();
			columnsByID.clear();
			fireTableStructureChanged();
		}
		
		/**
		 * Gets the real column count to access {@link #getRealColumn(int, boolean)}.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * If the table is sortable there are two new columns to sort up or sort down items. That means {@link #getColumnCount()}
		 * returns a value that is not the real one consider the columns that were added to the table. So this method returns
		 * the number of columns excluding sort columns.
		 * 
		 * @return the real column count
		 * @since 1.0
		 */
		public int getRealColumnCount() {
			return columns.size();
		}
		
		/**
		 * Gets the column at the given index.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * The given index is automatically converted into a real index using {@link #convertColumnIndexToReal(int)}.
		 * 
		 * @see #getRealColumnCount()
		 * @param index the model index of the column
		 * @param includeSortColumns <code>true</code> if the sort columns should be included that is, when the index is a sort column index the method returns <code>null</code>
		 * @return the column or <code>null</code> if the column is a sort column
		 * @throws IndexOutOfBoundsException
		 * <ul>
		 * 		<li>if includeSortColumns is <code>false</code> and the index is out of range (<code>index < 0 || index >= getRealColumnCount()</code>)</li>
		 * </ul>
		 * @since 1.0
		 */
		public ExecutionTableColumn getRealColumn(final int index, final boolean includeSortColumns) throws IndexOutOfBoundsException {
			final int realColIndex = convertColumnIndexToReal(index);
			
			if(includeSortColumns && isSortColumn(realColIndex))
				return null;
			
			return columns.get(realColIndex);
		}
		
		/**
		 * Gets the column by the specified id.
		 * 
		 * @param id the id
		 * @return the column or <code>null</code> if there is no column with the given id
		 * @since 1.0
		 */
		public ExecutionTableColumn getRealColumnByID(final int id) {
			return columnsByID.get(id);
		}
		
		/**
		 * Adds a new item to the model.
		 * 
		 * @param index index at which the given item should be inserted or <code>-1</code> to add the item at the end of the rows
		 * @param item the item
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if item is null</li>
		 * 		<li>if item is already added to another table</li>
		 * 		<li>if their already exists another item with the same identifier</li>
		 * </ul>
		 * @throws IndexOutOfBoundsException
		 * <ul>
		 * 		<li>if the specified index is out of bounds</li>
		 * </ul>
		 * @since 1.0
		 */
		public void add(final int index, final ExecutionTableItem item) throws IllegalArgumentException, IndexOutOfBoundsException {
			internalAdd(index, item);
			fireTableRowsInserted(item.getIndex(), item.getIndex());
			
			// scroll to the item if necessary
			if(ExecutionTable.this.autoScrollEnabled)
				ExecutionTable.this.scrollToItem(item);
		}
		
		/**
		 * Adds new items to the model.
		 * 
		 * @param items the list of items to be added
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if an item is null</li>
		 * 		<li>if an item is already added to another table</li>
		 * 		<li>if there are two different items with the same identifier</li>
		 * </ul>
		 * @since 1.0
		 */
		public void add(final ExecutionTableItem[] items) throws IllegalArgumentException {
			if(items == null)
				return;
			
			for(ExecutionTableItem item : items)
				internalAdd(-1, item);
			
			if(items.length > 0) {
				fireTableRowsInserted(items[0].getIndex(), items[items.length - 1].getIndex());
				
				// scroll to the item if necessary
				if(ExecutionTable.this.autoScrollEnabled)
					ExecutionTable.this.scrollToItem(items[items.length - 1]);
			}
		}
		
		/**
		 * Removes the given item from the model.
		 * 
		 * @param item the item
		 * @since 1.0
		 */
		public void remove(final ExecutionTableItem item) {
			if(item == null || item.getTable() != ExecutionTable.this)
				return;
			
			// firstly close the opened editors so that the values and indices of the items are correct
			ExecutionTable.this.closeEditors();
			
			final int oldIndex = item.getIndex();
			
			items.remove(item);
			if(item.getID() != -1)
				itemsByID.remove(item.getID());
			visibleRows.remove(convertItemIndexToVisible(oldIndex));
			
			// update the indices of the items after the removed one
			for(int i = item.getIndex(); i < items.size(); i++)
				items.get(i).setIndex(i);
			
			// afterwards reset the table, model and index of the item
			item.setTableAndModel(null, null);
			item.setIndex(-1);
			
			fireTableRowsDeleted(oldIndex, oldIndex);
		}
		
		/**
		 * Removes all items from the model.
		 * 
		 * @since 1.0
		 */
		public void removeAllItems() {
			// firstly close the opened editors so that the values and indices of the items are correct
			ExecutionTable.this.closeEditors();
			
			items.clear();
			itemsByID.clear();
			visibleRows.clear();
			fireTableStructureChanged();
		}
		
		/**
		 * Gets the number of items in the model.
		 * 
		 * @return the number of items
		 * @since 1.0
		 */
		public int getItemCount() {
			return items.size();
		}
		
		/**
		 * Gets the item at the given index.
		 * 
		 * @see #getItemCount()
		 * @param index the index
		 * @return the item
		 * @throws IndexOutOfBoundsException
		 * <ul>
		 * 		<li>if the index is out of range (<code>index < 0 || index >= getItemCount()</code>)</li>
		 * </ul>
		 * @since 1.0
		 */
		public ExecutionTableItem getItem(final int index) throws IndexOutOfBoundsException {
			return items.get(index);
		}
		
		/**
		 * Gets the item by the specified id.
		 * 
		 * @param id the id
		 * @return the item or <code>null</code> if there is no item with the given id
		 * @since 1.0
		 */
		public ExecutionTableItem getItemByID(final int id) {
			return itemsByID.get(id);
		}
		
		/**
		 * Indicates whether the execution table is sortable meaning that a user can sort the items using buttons at each item
		 * to change the position up/down.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return <code>true</code> if the execution table is sortable otherwise <code>false</code>
		 * @since 1.0
		 */
		public boolean isSortable() {
			return sortable;
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
		 * @see #sortUp(int)
		 * @see #sortDown(int)
		 * @param sortable <code> if the table should be sortable otherwise <code>false</code>
		 * @since 1.0
		 */
		public void setSortable(final boolean sortable) {
			// if the sort mode does not change then quit
			if(sortable == this.sortable)
				return;
			
			this.sortable = sortable;
			
			// close possible opened editors
			ExecutionTable.this.closeEditors();
			// reset the cursor to avoid cursor problems (for example if the user hovers a sort up/down button
			// during the sort mode is deactivated)
			ExecutionTable.this.table.setCursor(Cursor.getDefaultCursor());
			
			// the structure of the table changed because the sort columns are added
			fireTableStructureChanged();
		}
		
		/**
		 * Lets the specified row go one position up in the sorting order of the items but only if the sort mode is on
		 * ({@link #isSortable()}).
		 * 
		 * @param rowIndex the index of the <b>visible</b> row that should be sorted up
		 * @since 1.0
		 */
		public void sortUp(final int rowIndex) {
			if(!sortable)
				return;
			
			// if possible then swap the given row with the predecessor row
			if(rowIndex > 0) {
				swapRows(rowIndex - 1, rowIndex);
				fireTableRowsUpdated(rowIndex - 1, rowIndex);
			}
		}
		
		/**
		 * Lets the specified row go one position down in the sorting order of the items but only if the sort mode is on
		 * ({@link #isSortable()}).
		 * 
		 * @param rowIndex the index of the <b>visible</b> row that should be sorted up
		 * @since 1.0
		 */
		public void sortDown(final int rowIndex) {
			if(!sortable)
				return;
			
			// if possible then swap the given row with the successor row
			if(rowIndex < visibleRows.size() - 1) {
				swapRows(rowIndex, rowIndex + 1);
				fireTableRowsUpdated(rowIndex, rowIndex + 1);
			}
		}
		
		/**
		 * Sorts the items by a specified column index and sort order.
		 * 
		 * @param colIndex the index of the column the items should be ordered by using the real index ({@link #convertColumnIndexToReal(int)})
		 * @param order the sort order
		 * @throws IndexOutOfBoundsException
		 * <ul>
		 * 		<li>if the column index is out of range (<code>index < 0 || index >= getRealColumnCount()</code>)</li>
		 * </ul>
		 * @since 1.0
		 */
		public void sortItems(final int colIndex, final SortOrder order) throws IndexOutOfBoundsException {
			if(colIndex < 0 || colIndex >= columns.size())
				throw new IndexOutOfBoundsException("column index out of range");
			
			// firstly close the opened editors so that the values and indices of the items are correct
			ExecutionTable.this.closeEditors();
			
			if(order == null || order == SortOrder.UNSORTED)
				Collections.sort(items, unsortedOrderComparator);
			else {
				Collections.sort(items, new Comparator<ExecutionTableItem>() {
					
					@SuppressWarnings("unchecked")
					@Override
					public int compare(ExecutionTableItem item1, ExecutionTableItem item2) {
						final Object cellObj1 = item1.getCellObject(colIndex);
						final Object cellObj2 = item2.getCellObject(colIndex);
						int compare = 0;
						
						if(cellObj1 == null && cellObj2 == null)
							compare = item1.getUnsortedOrderIndex() - item2.getUnsortedOrderIndex();
						else if(cellObj1 == null)
							compare = -1;
						else if(cellObj2 == null)
							compare = 1;
						else {
							if(cellObj1 instanceof Comparable && cellObj2 instanceof Comparable)
								compare = ((Comparable<Object>)cellObj1).compareTo((Comparable<Object>)cellObj2);
							else
								compare = cellObj1.toString().compareTo(cellObj2.toString());
						}
						
						return (order == SortOrder.ASCENDING) ? compare : -compare;
					}
				});
			}

			// the visible rows have to be recreated because the structure changed
			visibleRows.clear();
			
			// set the new item indices and recreate the visible rows
			for(int i = 0; i < items.size(); i++) {
				items.get(i).setIndex(i);
				// add only the visible items
				if(items.get(i).isVisible())
					visibleRows.add(items.get(i));
			}
			
			fireTableStructureChanged();
		}
		
		/**
		 * Converts the given model index of a column to its real index meaning if the table is sortable ({@link #isSortable()})
		 * the first two columns are sort columns so <code>colIndex</code> <code>0</code> is mapped to <code>-2</code> and
		 * <code>colIndex</code> <code>1</code> is mapped to <code>-1</code>.
		 * 
		 * @param colIndex the model index of the column
		 * @return the real column index
		 * @since 1.0
		 */
		public int convertColumnIndexToReal(final int colIndex) {
			return sortable ? colIndex - 2 : colIndex;
		}
		
		/**
		 * Converts the given real index of a column to the index of the column in the model meaning if the table is sortable
		 * ({@link #isSortable()}) the first two columns are sort columns so <code>colIndex</code> <code>0</code> is mapped to <code>2</code>,
		 * <code>colIndex</code> <code>1</code> is mapped to <code>3</code> and so on.
		 * 
		 * @param colIndex the real index of the column
		 * @return the model index
		 * @since 1.0
		 */
		public int convertRealIndexToColumn(final int colIndex) {
			return sortable ? colIndex + 2 : colIndex;
		}
		
		/**
		 * Indicates whether the given real column index ({@link #convertColumnIndexToReal(int)}) is the index of a sort column.
		 * 
		 * @param realColIndex the real column index
		 * @return <code>true</code> if the related column is a sort column otherwise <code>false</code>
		 * @since 1.0
		 */
		public boolean isSortColumn(final int realColIndex) {
			return isSortUpColumn(realColIndex) || isSortDownColumn(realColIndex);
		}
		
		/**
		 * Indicates whether the given real column index ({@link #convertColumnIndexToReal(int)}) is the index of the sort up column.
		 * 
		 * @param realColIndex the real column index
		 * @return <code>true</code> if the related column is the sort up column otherwise <code>false</code>
		 * @since 1.0
		 */
		public boolean isSortUpColumn(final int realColIndex) {
			return realColIndex == -2;
		}
		
		/**
		 * Indicates whether the given real column index ({@link #convertColumnIndexToReal(int)}) is the index of the sort down column.
		 * 
		 * @param realColIndex the real column index
		 * @return <code>true</code> if the related column is the sort down column otherwise <code>false</code>
		 * @since 1.0
		 */
		public boolean isSortDownColumn(final int realColIndex) {
			return realColIndex == -1;
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
			// item is the last one then it can only have the last index of the visible rows
			if(index == items.size() - 1)
				return visibleRows.size() - 1;
			
			final int lastIndex = (index >= items.size()) ? items.size() - 1 : index - 1;
			int numOfInvisibleItems = 0;
			
			for(int i = 0; i <= lastIndex; i++)
				if(!items.get(i).isVisible())
					numOfInvisibleItems++;
			
			return index - numOfInvisibleItems;
		}

		@Override
		public int getColumnCount() {
			if(sortable)
				return getRealColumnCount() + 2;
			else
				return getRealColumnCount();
		}
		
		@Override
		public String getColumnName(int colIndex) {
			final ExecutionTableColumn col = getRealColumn(colIndex, true);
			
			if(col == null)
				return "";
			else
				return col.getName();
		}

		/**
		 * Gets the number of visible rows in the table.
		 * 
		 * @return number of visible rows
		 * @since 1.0
		 */
		@Override
		public int getRowCount() {
			return visibleRows.size();
		}
		
		/**
		 * Gets the visible row at the specified index.
		 * 
		 * @param index the index
		 * @return the item of the row
		 * @throws IndexOutOfBoundsException
		 * <ul>
		 * 		<li>if the index is out of range (<code>index < 0 || index >= getRowCount()</code>)</li>
		 * </ul>
		 * @since 1.0
		 */
		public ExecutionTableItem getVisibleRow(final int index) throws IndexOutOfBoundsException {
			return visibleRows.get(index);
		}
		
		/**
		 * Gets the visible item by the specified id.
		 * 
		 * @param id the id
		 * @return the item or <code>null</code> if there is no visible item with the given id
		 * @since 1.0
		 */
		public ExecutionTableItem getVisibleRowByID(final int id) {
			for(ExecutionTableItem i : visibleRows)
				if(i.getID() == id)
					return i;
			
			return null;
		}
		
		/**
		 * Indicates whether the specified cell is editable.
		 * 
		 * @param rowIndex the model row index
		 * @param colIndex the model column index
		 * @return <code>true</code> if the cell is editable otherwise <code>false</code>
		 * @since 1.0
		 */
		@Override
		public boolean isCellEditable(int rowIndex, int colIndex) {
			final ExecutionTableColumn col = getRealColumn(colIndex, true);
			return visibleRows.get(rowIndex).isEditable() && (col != null && col.isEditable());
		}
		
		/**
		 * Gets the value at the specified cell.
		 * 
		 * @param rowIndex the model row index
		 * @param colIndex the model column index
		 * @return the cell value (might be <code>null</code>)
		 * @since 1.0
		 */
		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			final int realColIndex = convertColumnIndexToReal(colIndex);
			
			// if the table is sortable return the sort button icons for the related sort columns
			if(isSortUpColumn(realColIndex))
				return ExecutionTable.sortUpIcon;
			else if(isSortDownColumn(realColIndex))
				return ExecutionTable.sortDownIcon;
			else
				return visibleRows.get(rowIndex).getCellObject(realColIndex);
		}
		
		/**
		 * Sets the value at the specified cell.
		 * 
		 * @param o the new cell value
		 * @param rowIndex the model row index
		 * @param colIndex the model column index
		 * @since 1.0
		 */
		@Override
		public void setValueAt(Object o, int rowIndex, int colIndex) {
			final int realColIndex = convertColumnIndexToReal(colIndex);
			
			if(isSortColumn(realColIndex))
				return;
			
			// this method is invoked when a user edits a cell using the cell editor
			
			final ExecutionTableItem item = visibleRows.get(rowIndex);
			final InputParser<?> parser = item.getCellInputParser(realColIndex);
			
			// if the cell is not editable then retain the old cell object otherwise parse the new value
			if(!item.isEditable() || !columns.get(realColIndex).isEditable())
				o = item.getCellObject(realColIndex);
			else if(parser != null)
				o = parser.parse(o.toString());
			
			// set the edit value to the cell
			item.setCellObject(realColIndex, o);
			fireTableCellUpdated(rowIndex, colIndex);
		}
		
		@Override
		public void fireTableStructureChanged() {
			super.fireTableStructureChanged();
			
			// table structure changed so reset the column widths
			for(int i = 0; i < columns.size(); i++) {
				if(columns.get(i).getWidth() >= 0)
					setColumnWidth(i, columns.get(i).getWidth());
			}
			
			// if the table is sortable then restrict the sort columns to the button sizes
			if(sortable) {
				final TableColumn sortUpColumn = ExecutionTable.this.table.getColumnModel().getColumn(0);
				final TableColumn sortDownColumn = ExecutionTable.this.table.getColumnModel().getColumn(1);
				sortUpColumn.setPreferredWidth(ExecutionTable.sortUpIconWidth);
				sortUpColumn.setMaxWidth(ExecutionTable.sortUpIconWidth);
				sortUpColumn.setMinWidth(ExecutionTable.sortUpIconWidth);
				sortDownColumn.setPreferredWidth(ExecutionTable.sortDownIconWidth);
				sortDownColumn.setMaxWidth(ExecutionTable.sortDownIconWidth);
				sortDownColumn.setMinWidth(ExecutionTable.sortDownIconWidth);
			}
		}
		
		/**
		 * Updates the visibility of the given item meaning if the item is invisible it is removed from the list of
		 * visible rows otherwise it is added at the related position.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param item the item
		 * @since 1.0
		 */
		void updateVisibility(final ExecutionTableItem item) {
			final int visibleIndex = convertItemIndexToVisible(item.getIndex());
			final boolean containsItem = visibleRows.contains(item);
			
			// if item is invisible remove the item from the list of invisible rows otherwise add the item
			// at the related position
			if(!item.isVisible() && containsItem) {
				visibleRows.remove(item);
				fireTableRowsDeleted(visibleIndex, visibleIndex);
			}
			else if(item.isVisible() && !containsItem) {
				visibleRows.add(convertItemIndexToVisible(item.getIndex()), item);
				fireTableRowsInserted(visibleIndex, visibleIndex);
			}
		}
		
		/**
		 * Adds the given column.
		 * <br><br>
		 * <i>This method is only for internal use of the model!</i>.
		 * 
		 * @param column the column
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if column is null</li>
		 * 		<li>if column is already added to another table</li>
		 * 		<li>if their already exists another column with the same identifier</li>
		 * </ul>
		 * @since 1.0
		 */
		private void internalAdd(final ExecutionTableColumn column) throws IllegalArgumentException {
			if(column == null)
				return;
			else if(column.getTable() != null)
				throw new IllegalArgumentException("column is already added to another table");
			else if(column.getID() != -1 && columnsByID.containsKey(column.getID()))
				throw new IllegalArgumentException("their already exists a column with the id " + column.getID());
			
			// firstly close the opened editors so that the values and indices of the items are correct
			ExecutionTable.this.closeEditors();

			// first add the column to the list of columns (so that setColumnWidth(...) can update the structure if needed)
			columns.add(column);
			if(column.getID() != -1)
				columnsByID.put(column.getID(), column);
			
			// set the dependency to this table
			column.setTable(ExecutionTable.this);
			column.setIndex(columns.size() - 1);
		}
		
		/**
		 * Adds the given item.
		 * <br><br>
		 * <i>This method is only for internal use of the model!</i>.
		 * 
		 * @param index index at which the given item should be inserted or <code>-1</code> to add the item at the end of the rows
		 * @param item the item
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if item is null</li>
		 * 		<li>if item is already added to another table</li>
		 * 		<li>if their already exists another item with the same identifier</li>
		 * </ul>
		 * @throws IndexOutOfBoundsException
		 * <ul>
		 * 		<li>if the specified index is out of bounds</li>
		 * </ul>
		 * @since 1.0
		 */
		private void internalAdd(int index, final ExecutionTableItem item) throws IllegalArgumentException, IndexOutOfBoundsException {
			if(item == null)
				return;
			else if(item.getTable() != null)
				throw new IllegalArgumentException("item is already added to another table");
			else if(item.getID() != -1 && itemsByID.containsKey(item.getID()))
				throw new IllegalArgumentException("their already exists an item with the id " + item.getID());
			
			// firstly close the opened editors so that the values and indices of the items are correct
			ExecutionTable.this.closeEditors();
			
			final boolean toTheEnd = (index < 0);
			
			if(toTheEnd) {
				index = items.size();
				items.add(item);
			}
			else
				items.add(index, item);
			
			if(item.getID() != -1)
				itemsByID.put(item.getID(), item);
			
			// only add the item to the list of visible rows if the item is visible
			if(item.isVisible())
				visibleRows.add(toTheEnd ? visibleRows.size() : convertItemIndexToVisible(index), item);
			
			item.setTableAndModel(ExecutionTable.this, this);
			item.setIndex(index);
		}
		
		/**
		 * Swaps the specified rows in the list of all rows. This is based on the list of <b>visible rows</b> and it is transferred
		 * into the list of all items meaning it could be that there are other items between the specified visibility indices that are invisible.
		 * 
		 * @param vrIndex1 the visible index of the first row that should be at the index of the second row
		 * @param vrIndex2 the visible index of the second row that should be at the index of the first row
		 * @since 1.0
		 */
		private void swapRows(final int vrIndex1, final int vrIndex2) {
			final ExecutionTableItem vr1 = visibleRows.get(vrIndex1);
			final ExecutionTableItem vr2 = visibleRows.get(vrIndex2);
			final ExecutionTableItem row1 = items.get(vr1.getIndex());
			final ExecutionTableItem row2 = items.get(vr2.getIndex());
			final int itemIndex1 = row1.getIndex();
			final int itemIndex2 = row2.getIndex();
			
			// swap the rows in the list of all items
			items.set(itemIndex1, row2);
			items.set(itemIndex2, row1);
			// swap the rows in the list of visible rows
			visibleRows.set(vrIndex1, row2);
			visibleRows.set(vrIndex2, row1);
			
			// update their indices
			row1.setIndex(itemIndex2);
			row2.setIndex(itemIndex1);
		}
		
	}

}
