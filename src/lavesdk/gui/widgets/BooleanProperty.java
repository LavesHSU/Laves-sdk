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
 * Class:		BooleanProperty
 * Task:		Represents a boolean property of a properties list
 * Created:		29.11.13
 * LastChanges:	14.02.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;

/**
 * Represents a boolean property in a {@link PropertiesList}.
 * <br><br>
 * A boolean property can display its boolean value and edit them with a checkbox or if you specify a {@link BooleanPropertyGroup}
 * then the value is displayed with a radio button.
 * 
 * @see Property
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class BooleanProperty extends Property {
	
	/** the associated group or <code>null</code> */
	private final BooleanPropertyGroup group;
	
	/**
	 * Creates a new boolean property.
	 * 
	 * @param name the name of the property
	 * @param description the description of the property (<b>the description can contain html tags to format the text</b>)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * 		<li>if name is empty</li>
	 * 		<li>if description is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public BooleanProperty(final String name, final String description) throws IllegalArgumentException {
		this(name, description, null);
	}
	
	/**
	 * Creates a new boolean property.
	 * 
	 * @param name the name of the property
	 * @param description the description of the property (<b>the description can contain html tags to format the text</b>)
	 * @param group the group to which this property belongs to or <code>null</code> (in a boolean group there is only one property selected meaning that only one property has a value of <code>true</code>)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * 		<li>if name is empty</li>
	 * 		<li>if description is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public BooleanProperty(final String name, final String description, final BooleanPropertyGroup group) throws IllegalArgumentException {
		this(name, description, false, group);
	}
	
	/**
	 * Creates a new boolean property.
	 * 
	 * @param name the name of the property
	 * @param description the description of the property (<b>the description can contain html tags to format the text</b>)
	 * @param initialValue the initial value of the property
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * 		<li>if name is empty</li>
	 * 		<li>if description is null</li>
	 * 		<li>if initialValue is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public BooleanProperty(final String name, final String description, final boolean initialValue) {
		this(name, description, initialValue, null);
	}
	
	/**
	 * Creates a new boolean property.
	 * 
	 * @param name the name of the property
	 * @param description the description of the property (<b>the description can contain html tags to format the text</b>)
	 * @param initialValue the initial value of the property
	 * @param group the group to which this property belongs to or <code>null</code> (in a boolean group there is only one property selected meaning that only one property has a value of <code>true</code>)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * 		<li>if name is empty</li>
	 * 		<li>if description is null</li>
	 * 		<li>if initialValue is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public BooleanProperty(final String name, final String description, final boolean initialValue, final BooleanPropertyGroup group) throws IllegalArgumentException {
		super(name, description, initialValue, new Object[] {group != null});
		
		this.group = group;
		
		if(group != null)
			group.add(this);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.0
	 */
	@Override
	public Boolean getValue() {
		if(EDT.isExecutedInEDT())
			return (Boolean)value;
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return (Boolean)value;
				}
			});
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if value is null</li>
	 * 		<li>if value is not of type {@link Boolean}</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void setValue(final Object value) throws IllegalArgumentException {
		if(!(value instanceof Boolean) || value == null)
			throw new IllegalArgumentException("No valid argument!");
		
		setValue(((Boolean)value).booleanValue());
	}
	
	/**
	 * Sets the value of the boolean property.
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
	public void setValue(final boolean value) throws IllegalArgumentException {
		if(EDT.isExecutedInEDT())
			this.value = value;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setValue") {
				@Override
				protected void execute() throws Throwable {
					BooleanProperty.this.value = value;
				}
			});
	}

	@Override
	protected TableCellEditor createEditor(Object[] arguments) {
		if((boolean)arguments[0] == false) {
			final JCheckBox cb = new JCheckBox();
			cb.setHorizontalAlignment(JCheckBox.CENTER);
			return new DefaultCellEditor(cb);
		}
		else
			return new RadioButtonCellEditor();
	}
	
	@Override
	protected TableCellRenderer createRenderer(Object[] arguments) {
		if((boolean)arguments[0] == false)
			return null;
		else
			return new RadioButtonCellRenderer();
	}
	
	@Override
	protected Class<?> getValueClass() {
		return Boolean.class;
	}

	@Override
	protected Object onEditStop(Object value) {
		// the property has a group? then ensure that only one property is selected!
		if(group != null)
			group.update(this);
			
		return value;
	}

}
