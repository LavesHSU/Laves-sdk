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
 * Class:		PropertiesList
 * Task:		Representation of a properties list
 * Created:		01.10.13
 * LastChanges:	28.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;

/**
 * Represents a properties list.
 * <br><br>
 * Use the {@link PropertiesListModel} to add and remove {@link Property}s from the list or to specify
 * the column captions, the row height or of the header should be visible. You can get the model of the
 * properties list with {@link #getModel()}.
 * <br><br>
 * <b>Notice</b>:<br>
 * The properties list is scrollable by itself meaning that you do not need to integrate it in a {@link JScrollPane}.
 * 
 * @see Property
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class PropertiesList extends JPanel {

	private static final long serialVersionUID = 1L;
	
	/** the model of the properties list */
	private final PropertiesListTableModel tableModel;
	/** the model of the properties list */
	private PropertiesListModel model;
	/** the table that represents the properties list */
	private final JTable table;
	/** the label that displays the description of a selected property */
	private final JLabel descriptionLbl;
	/** flag that indicates if components can be added to the properties list or not */
	private boolean addImplDisabled;
	/** the event controller of the properties list */
	private final EventController eventController;
	
	/**
	 * Creates a new properties list.
	 * 
	 * @since 1.0
	 */
	public PropertiesList() {
		this(null);
	}
	
	/**
	 * Creates a new properties list.
	 * 
	 * @param model the model of the properties list or <code>null</code> for the default model
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the specified model is already used by another properties list</li>
	 * </ul>
	 * @since 1.0
	 */
	public PropertiesList(final PropertiesListModel model) throws IllegalArgumentException {
		addImplDisabled = false;
		
		super.setLayout(new BorderLayout());
		
		this.tableModel = new PropertiesListTableModel();
		this.eventController = new EventController();
		this.table = new JTable(tableModel) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public TableCellEditor getCellEditor(int rowIndex, int columnIndex) {
				// get the row (property) specific cell editor
				if(convertColumnIndexToModel(columnIndex) == 1)
					return tableModel.getRow(convertRowIndexToModel(rowIndex)).getEditor();
				else
					return super.getCellEditor(rowIndex, columnIndex);
			}
			
			@Override
			public TableCellRenderer getCellRenderer(int rowIndex, int columnIndex) {
				final Property p = tableModel.getRow(convertRowIndexToModel(rowIndex));
				
				// get the row (property) specific cell renderer
				if(convertColumnIndexToModel(columnIndex) == 1)
					return (p.getRenderer() != null) ? p.getRenderer() : getDefaultRenderer(p.getValueClass());
				else
					return super.getCellRenderer(rowIndex, columnIndex);
			}
			
		};
		
		// set the model (and load further model data) or use a default model
		setModel((model != null) ? model : new PropertiesListModel());
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(eventController);
		
		descriptionLbl = new JLabel();
		descriptionLbl.setVisible(false);
		
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(descriptionLbl, BorderLayout.SOUTH);
		
		// no more components may be added to the properties list
		addImplDisabled = true;
	}
	
	/**
	 * Gets the model of the properties list.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the model
	 * @since 1.0
	 */
	public PropertiesListModel getModel() {
		if(EDT.isExecutedInEDT())
			return model;
		else
			return EDT.execute(new GuiRequest<PropertiesListModel>() {
				@Override
				protected PropertiesListModel execute() throws Throwable {
					return model;
				}
			});
	}
	
	/**
	 * Sets the model of the properties list.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param model the model
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if model is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setModel(final PropertiesListModel model) throws IllegalArgumentException {
		if(model == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			internalSetModel(model);
		else
			EDT.execute(new GuiJob("PropertiesList.setModel") {
				@Override
				protected void execute() throws Throwable {
					internalSetModel(model);
				}
			});
	}
	
	/**
	 * Adds a new property to the list respectively to its model.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see TextProperty
	 * @see NumericProperty
	 * @see BooleanProperty
	 * @see ListProperty
	 * @see ColorProperty
	 * @param p the property
	 * @return <code>true</code> if the property could be added otherwise <code>false</code> (this could be if there is already a property with that name)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if p is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final boolean add(final Property p) throws IllegalArgumentException {
		return model.add(p);
	}
	
	/**
	 * Removes a property from the list respectively from its model.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param name the name of the property that should be removed
	 * @return <code>true</code> if the property could be removed otherwise <code>false</code> (this could be if there is no property with that name)
	 */
	public final boolean remove(final String name) {
		return model.remove(name);
	}
	
	/**
	 * Gets the property with the specified name and class.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see PropertiesListModel#getProperty(String, Class)
	 * @see TextProperty
	 * @see NumericProperty
	 * @see BooleanProperty
	 * @see ListProperty
	 * @see ColorProperty
	 * @param name the name of the property
	 * @param c the class of the property (like <code>TextProperty.class</code>, ...)
	 * @return the property or <code>null</code> if there is either no property with the given name or the property of the name is not assignable from the specified class
	 * @since 1.0
	 */
	public <T extends Property> T getProperty(final String name, final Class<T> c) {
		return model.getProperty(name, c);
	}
	
	/**
	 * The layout of a properties list may not be changed meaning this method does nothing!
	 * 
	 * @param mgr the layout manager
	 * @since 1.0
	 */
	@Override
	public void setLayout(LayoutManager mgr) {
		// This is not allowed!
	}
	
	/**
	 * Removes all properties from the properties list.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	@Override
	public void removeAll() {
		model.removeAll();
	}
	
	@Override
	protected void addImpl(Component c, Object constraints, int index) {
		if(!addImplDisabled)
			super.addImpl(c, constraints, index);
	}
	
	/**
	 * Gets the model of the table that represents the properties list.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the table model
	 * @since 1.0
	 */
	PropertiesListTableModel getTableModel() {
		if(EDT.isExecutedInEDT())
			return tableModel;
		else
			return EDT.execute(new GuiRequest<PropertiesListTableModel>() {
				@Override
				protected PropertiesListTableModel execute() throws Throwable {
					return tableModel;
				}
			});
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
		if(EDT.isExecutedInEDT())
			table.editCellAt(-1, -1);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".closeEditors") {
				@Override
				protected void execute() throws Throwable {
					table.editCellAt(-1, -1);
				}
			});
	}
	
	/**
	 * Sets the model of the properties list.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param model the model
	 * @since 1.0
	 */
	private void internalSetModel(final PropertiesListModel model) {
		this.model = model;
		this.model.setList(this);
		
		// update the table
		table.setAutoCreateRowSorter(this.model.hasAutoRowSorter());
		if(!this.model.isHeaderVisible())
			table.setTableHeader(null);
		table.setRowHeight(this.model.getRowHeight());
		
		// update the table model
		tableModel.setNameColumnCaption(this.model.getNameColumnCaption());
		tableModel.setValueColumnCaption(this.model.getValueColumnCaption());
		tableModel.removeAll();
		for(int i = 0; i < this.model.getPropertyCount(); i++)
			tableModel.addRow(this.model.getProperty(i));
	}
	
	/**
	 * Displays the description of the selected property (if one is selected).
	 * 
	 * @since 1.0
	 */
	private void displayDescription() {
		final int row = table.getSelectedRow();
		
		if(row == -1) {
			descriptionLbl.setVisible(false);
			return;
		}
		
		final int modelRow = table.convertRowIndexToModel(row);
		final Property p = tableModel.getRow(modelRow);
		
		// the description label is only visible when the selected property has a description
		descriptionLbl.setVisible(!p.getDescription().isEmpty());
		descriptionLbl.setText("<html>" + p.getDescription() + "</html>");
		table.scrollRectToVisible(table.getCellRect(row, 0, true));
	}
	
	/**
	 * Represents the model of the properties list.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	class PropertiesListTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		
		/** the column names */
		private final String[] columns;
		/** the rows of the table */
		private final List<Property> rows;
		
		/**
		 * Creates a new model.
		 * 
		 * @since 1.0
		 */
		public PropertiesListTableModel() {
			// the properties list has always two columns, one for the property name and one for the proeprty value
			columns = new String[] { "Name", "Value" };
			rows = new ArrayList<Property>(5);
		}
		
		/**
		 * Adds a new row.
		 * 
		 * @param p the property that represents the row
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if p is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public void addRow(final Property p) throws IllegalArgumentException {
			if(p == null)
				throw new IllegalArgumentException("No valid argument!");
			
			rows.add(p);
			
			fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
		}
		
		/**
		 * Removes the row of the specified property.
		 * 
		 * @param p the property
		 * @since 1.0
		 */
		public void removeRow(final Property p) {
			rows.remove(p);
			
			fireTableStructureChanged();
		}
		
		/**
		 * Removes all rows.
		 * 
		 * @since 1.0
		 */
		public void removeAll() {
			rows.clear();
			
			fireTableStructureChanged();
		}
		
		/**
		 * Gets the row at the given index.
		 * 
		 * @param index the index
		 * @return the property that represents the row
		 * @throws IndexOutOfBoundsException
		 * <ul>
		 * 		<li>if the index is out of range (<code>index < 0 || index >= getRowCount()</code>)</li>
		 * </ul>
		 * @since 1.0
		 */
		public Property getRow(final int index) throws IndexOutOfBoundsException {
			return rows.get(index);
		}
		
		/**
		 * Sets the caption of the first column.
		 * 
		 * @param caption the caption
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if caption is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public void setNameColumnCaption(final String caption) throws IllegalArgumentException {
			if(caption == null)
				throw new IllegalArgumentException("No valid argument!");
			
			columns[0] = caption;
		}
		
		/**
		 * Sets the caption of the second column.
		 * 
		 * @param caption the caption
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if caption is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public void setValueColumnCaption(final String caption) throws IllegalArgumentException {
			if(caption == null)
				throw new IllegalArgumentException("No valid argument!");
			
			columns[1] = caption;
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 1;
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}
		
		@Override
		public String getColumnName(int columnIndex) {
			return columns[columnIndex];
		}

		@Override
		public int getRowCount() {
			return rows.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			// the first column is the property name column and the second the property value column
			if(columnIndex == 0)
				return rows.get(rowIndex).getName();
			else
				return rows.get(rowIndex).getValue();
		}
		
	}
	
	/**
	 * Handles the events of the properties list.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class EventController implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			PropertiesList.this.displayDescription();
		}
		
	}

}
