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
 * Class:		ListProperty
 * Task:		Represents a list property of a properties list
 * Created:		29.11.13
 * LastChanges:	14.02.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;

/**
 * Represents a list property in a {@link PropertiesList}.
 * <br><br>
 * A list property can contain multiple string values in a combobox.
 * 
 * @see Property
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class ListProperty extends Property {
	
	/** the list that represents the items */
	private JComboBox<String> list;
	
	/**
	 * Creates a new list property.
	 * 
	 * @param name the name of the property
	 * @param description the description of the property (<b>the description can contain html tags to format the text</b>)
	 * @param items the list items of the property
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * 		<li>if name is empty</li>
	 * 		<li>if description is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ListProperty(final String name, final String description, final String[] items) throws IllegalArgumentException {
		super(name, description, "", items);
	}
	
	/**
	 * Creates a new list property.
	 * 
	 * @param name the name of the property
	 * @param description the description of the property (<b>the description can contain html tags to format the text</b>)
	 * @param initialValue the initial value of the property
	 * @param items the list items of the property
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * 		<li>if name is empty</li>
	 * 		<li>if description is null</li>
	 * 		<li>if initialValue is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ListProperty(final String name, final String description, final String initialValue, final String[] items) throws IllegalArgumentException {
		super(name, description, initialValue, null);
		
		// add all items
		for(String item : items)
			list.addItem(item);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.0
	 */
	@Override
	public String getValue() {
		if(EDT.isExecutedInEDT())
			return (String)value;
		else
			return EDT.execute(new GuiRequest<String>() {
				@Override
				protected String execute() throws Throwable {
					return (String)value;
				}
			});
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if value is null</li>
	 * 		<li>if value is not of type {@link String}</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void setValue(Object value) throws IllegalArgumentException {
		if(!(value instanceof String))
			throw new IllegalArgumentException("No valid argument!");
		
		setValue((String)value);
	}
	
	/**
	 * Sets the value of the list property.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param value the value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if value is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setValue(final String value) throws IllegalArgumentException {
		if(value == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			this.value = value;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setValue") {
				@Override
				protected void execute() throws Throwable {
					ListProperty.this.value = value;
				}
			});
	}

	@Override
	protected TableCellEditor createEditor(Object[] arguments) {
		list = new JComboBox<String>();
		return new DefaultCellEditor(list);
	}
	
	@Override
	protected TableCellRenderer createRenderer(Object[] arguments) {
		return null;
	}
	
	@Override
	protected Class<?> getValueClass() {
		return String.class;
	}

	@Override
	protected Object onEditStop(Object value) {
		return value;
	}

}
