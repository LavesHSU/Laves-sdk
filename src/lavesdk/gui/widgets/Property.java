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
 * Class:		Property
 * Task:		Represents a property of a properties list
 * Created:		01.10.13
 * LastChanges:	14.02.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;

/**
 * Represents a property of a {@link PropertiesList}.
 * 
 * @see PropertiesListModel
 * @see TextProperty
 * @see NumericProperty
 * @see BooleanProperty
 * @see ListProperty
 * @see ColorProperty
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public abstract class Property {
	
	/** the name of the property */
	private final String name;
	/** the description of the property */
	private final String description;
	/** the value of the property */
	protected Object value;
	/** the property's editor */
	protected final TableCellEditor editor;
	/** the property's renderer */
	protected final TableCellRenderer renderer;
	
	/**
	 * Creates a new property.
	 * 
	 * @param name the name of the property
	 * @param description the description of the property (<b>the description can contain html tags to format the text</b>)
	 * @param initialValue the initial value of the property
	 * @param arguments arguments for the creation of the editor in {@link #createEditor(Object[])} and the renderer in {@link #createRenderer(Object[])} or <code>null</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * 		<li>if name is empty</li>
	 * 		<li>if description is null</li>
	 * 		<li>if initialValue is null</li>
	 * </ul>
	 * @throws IllegalStateException
	 * <ul>
	 * 		<li>if the created editor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Property(final String name, final String description, final Object initialValue, final Object[] arguments) throws IllegalArgumentException, IllegalStateException {
		if(name == null || name.isEmpty() || description == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.name = name;
		this.description = description;
		this.editor = createEditor(arguments);
		this.renderer = createRenderer(arguments);
		setValue(initialValue);
		
		if(editor == null)
			throw new IllegalStateException("No valid editor!");
		
		editor.addCellEditorListener(new CellEditorListener() {
			
			@Override
			public void editingStopped(ChangeEvent e) {
				// the user edits a property? then apply the data to the property value
				if(e.getSource() instanceof AbstractCellEditor)
					Property.this.setValue(Property.this.onEditStop(((AbstractCellEditor)e.getSource()).getCellEditorValue()));
			}
			
			@Override
			public void editingCanceled(ChangeEvent e) {
			}
		});
	}
	
	/**
	 * Gets the name of the property.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the name
	 * @since 1.0
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Gets the description of the property.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the description
	 * @since 1.0
	 */
	public final String getDescription() {
		return description;
	}
	
	/**
	 * Gets the value of the property.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the value of the property
	 * @since 1.0
	 */
	public Object getValue() {
		if(EDT.isExecutedInEDT())
			return value;
		else
			return EDT.execute(new GuiRequest<Object>() {
				@Override
				protected Object execute() throws Throwable {
					return value;
				}
			});
	}
	
	/**
	 * Sets the value of the property.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param value the value
	 * @since 1.0
	 */
	public void setValue(final Object value) {
		if(EDT.isExecutedInEDT())
			this.value = value;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setValue") {
				@Override
				protected void execute() throws Throwable {
					Property.this.value = value;
				}
			});
	}
	
	@Override
	public String toString() {
		return name + "[value=" + value + "]";
	}
	
	/**
	 * Creates the editor of the property.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method must return a valid editor otherwise an {@link IllegalStateException} is thrown.
	 * 
	 * @see DefaultCellEditor
	 * @see ColorCellEditor
	 * @see SliderCellEditor
	 * @param arguments the arguments list which is committed by the constructor or <code>null</code>
	 * @return the table cell editor
	 * @since 1.0
	 */
	protected abstract TableCellEditor createEditor(final Object[] arguments);
	
	/**
	 * Creates the renderer of the property.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If you do not specify a custom cell renderer (meaning that this method returns <code>null</code>) the value class
	 * ({@link #getValueClass()}) is used to find a default cell renderer for this property (see {@link DefaultTableCellRenderer}) .
	 * 
	 * @param arguments the arguments list which is committed by the constructor or <code>null</code>
	 * @return the table cell renderer of this property or <code>null</code> if the default renderer should be used
	 * @since 1.0
	 */
	protected abstract TableCellRenderer createRenderer(final Object[] arguments);
	
	/**
	 * Gets the class of the value.
	 * <br><br>
	 * <b>Example</b>:<br>
	 * If you have a text property this method should return <code>String.class</code> or if you have
	 * a numeric property the return value should be <code>Number.class</code> and so on.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If you do not have specified a custom cell renderer for the property (meaning that {@link #createRenderer(Object[])}
	 * returns <code>null</code>) the value class is used to set the default cell renderer for this property.
	 * 
	 * @return the class of the property value
	 * @since 1.0
	 */
	protected abstract Class<?> getValueClass();
	
	/**
	 * Checks if the committed value is valid.
	 * <br><br>
	 * This method is invoked when the user has stopped the editing. Use this method to verify the edit value.
	 * 
	 * @param value the edit value
	 * @return the verified edit value which is passed in {@link #setValue(Object)}
	 * @since 1.0
	 */
	protected abstract Object onEditStop(final Object value);
	
	/**
	 * Gets the editor of the property.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the editor
	 * @since 1.0
	 */
	final TableCellEditor getEditor() {
		return editor;
	}
	
	/**
	 * Gets the renderer of the property.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the renderer or <code>null</code> if the default renderer should be used
	 * @since 1.0
	 */
	final TableCellRenderer getRenderer() {
		return renderer;
	}
	
}
