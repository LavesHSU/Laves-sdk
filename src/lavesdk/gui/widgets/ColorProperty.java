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
 * Class:		ColorProperty
 * Task:		Represents a color property of a properties list
 * Created:		29.11.13
 * LastChanges:	14.02.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import java.awt.Color;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;

/**
 * Represents a color property in a {@link PropertiesList}.
 * <br><br>
 * A color property can display color values so that the color is shown in the cell and optional its rgb values.
 * To edit a color value the system color chooser is opened to select a new color.
 * 
 * @see Property
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class ColorProperty extends Property {
	
	/**
	 * Creates a new color property.
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
	public ColorProperty(final String name, final String description) throws IllegalArgumentException {
		this(name, description, Color.white);
	}
	
	/**
	 * Creates a new color property.
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
	public ColorProperty(final String name, final String description, final Color initialValue) throws IllegalArgumentException {
		this(name, description, initialValue, false);
	}
	
	/**
	 * Creates a new color property.
	 * 
	 * @param name the name of the property
	 * @param description the description of the property (<b>the description can contain html tags to format the text</b>)
	 * @param displayRGB <code>true</code> if the rgb value of the color should be displayed otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * 		<li>if name is empty</li>
	 * 		<li>if description is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ColorProperty(final String name, final String description, final boolean displayRGB) throws IllegalArgumentException {
		this(name, description, Color.white, displayRGB);
	}
	
	/**
	 * Creates a new color property.
	 * 
	 * @param name the name of the property
	 * @param description the description of the property (<b>the description can contain html tags to format the text</b>)
	 * @param initialValue the initial value of the property
	 * @param displayRGB <code>true</code> if the rgb value of the color should be displayed otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * 		<li>if name is empty</li>
	 * 		<li>if description is null</li>
	 * 		<li>if initialValue is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ColorProperty(final String name, final String description, final Color initialValue, final boolean displayRGB) throws IllegalArgumentException {
		super(name, description, initialValue, new Object[] {displayRGB});
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.0
	 */
	@Override
	public Color getValue() {
		if(EDT.isExecutedInEDT())
			return (Color)value;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return (Color)value;
				}
			});
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if value is null</li>
	 * 		<li>if value is not of type {@link Color}</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void setValue(final Object value) throws IllegalArgumentException {
		if(!(value instanceof Color))
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			setValue((Color)value);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setValue") {
				@Override
				protected void execute() throws Throwable {
					setValue((Color)value);
				}
			});
	}
	
	/**
	 * Sets the value of the color property.
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
	public void setValue(final Color value) throws IllegalArgumentException {
		if(value == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			this.value = value;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setValue") {
				@Override
				protected void execute() throws Throwable {
					ColorProperty.this.value = value;
				}
			});
	}

	@Override
	protected TableCellEditor createEditor(Object[] arguments) {
		return new ColorCellEditor(getName());
	}
	
	@Override
	protected TableCellRenderer createRenderer(Object[] arguments) {
		return new ColorCellRenderer((boolean)arguments[0]);
	}
	
	@Override
	protected Class<?> getValueClass() {
		return Color.class;
	}

	@Override
	protected Object onEditStop(Object value) {
		return value;
	}

}

