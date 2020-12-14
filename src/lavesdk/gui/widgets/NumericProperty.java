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

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;

/**
 * Represents a numeric property in a {@link PropertiesList}.
 * <br><br>
 * A numeric property can display numbers and edit them either with a numeric field (only number specific
 * characters are allowed) or a slider.<br>
 * If the editor type is a slider you can specify the minimum and maximum of the slider with {@link #setMinimum(int)}/{@link #setMaximum(int)}.
 * 
 * @see Property
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class NumericProperty extends Property {
	
	/**
	 * Creates a new numeric property.
	 * <br><br>
	 * As editor it is used a numeric field.
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
	public NumericProperty(final String name, final String description) throws IllegalArgumentException {
		this(name, description, 0);
	}
	
	/**
	 * Creates a new numeric property.
	 * <br><br>
	 * As editor it is used a numeric field. If you want to have a slider editor please use another
	 * constructor like {@link #NumericProperty(String, String, boolean)}.
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
	public NumericProperty(final String name, final String description, final Number initialValue) throws IllegalArgumentException {
		this(name, description, initialValue, false);
	}
	
	/**
	 * Creates a new numeric property.
	 * 
	 * @param name the name of the property
	 * @param description the description of the property (<b>the description can contain html tags to format the text</b>)
	 * @param sliderEditor <code>true</code> if the property should use a slider as editor or <code>false</code> if a numeric field should be used as editor
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * 		<li>if name is empty</li>
	 * 		<li>if description is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public NumericProperty(final String name, final String description, final boolean sliderEditor) throws IllegalArgumentException {
		this(name, description, 0, sliderEditor);
	}
	
	/**
	 * Creates a new numeric property.
	 * 
	 * @param name the name of the property
	 * @param description the description of the property (<b>the description can contain html tags to format the text</b>)
	 * @param initialValue the initial value of the property
	 * @param sliderEditor <code>true</code> if the property should use a slider as editor or <code>false</code> if a numeric field should be used as editor
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * 		<li>if name is empty</li>
	 * 		<li>if description is null</li>
	 * 		<li>if initialValue is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public NumericProperty(final String name, final String description, final Number initialValue, final boolean sliderEditor) throws IllegalArgumentException {
		super(name, description, initialValue, new Object[] {sliderEditor});
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.0
	 */
	@Override
	public Number getValue() {
		if(EDT.isExecutedInEDT())
			return (Number)value;
		else
			return EDT.execute(new GuiRequest<Number>() {
				@Override
				protected Number execute() throws Throwable {
					return (Number)value;
				}
			});
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if value is null</li>
	 * 		<li>if value is not of type {@link Number}</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void setValue(Object value) throws IllegalArgumentException {
		if(!(value instanceof Number))
			throw new IllegalArgumentException("No valid argument!");
		
		setValue((Number)value);
	}
	
	/**
	 * Sets the value of the numeric property.
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
	public void setValue(final Number value) throws IllegalArgumentException {
		if(value == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			this.value = value;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setValue") {
				@Override
				protected void execute() throws Throwable {
					NumericProperty.this.value = value;
				}
			});
	}
	
	/**
	 * Gets the minimum of the slider.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This has only an effect if the editor type is a slider.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the minimum
	 * @since 1.0
	 */
	public int getMinimum() {
		if(EDT.isExecutedInEDT())
			return getSliderRange(true);
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return getSliderRange(true);
				}
			});
	}
	
	/**
	 * Sets the minimum of the slider.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This has only an effect if the editor type is a slider.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param minimum the minimum
	 * @since 1.0
	 */
	public void setMinimum(final int minimum) {
		if(EDT.isExecutedInEDT())
			setSliderRange(minimum, true);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setMinimum") {
				@Override
				protected void execute() throws Throwable {
					setSliderRange(minimum, true);
				}
			});
	}
	
	/**
	 * Gets the maximum of the slider.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This has only an effect if the editor type is a slider.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the maximum
	 * @since 1.0
	 */
	public int getMaximum() {
		if(EDT.isExecutedInEDT())
			return getSliderRange(false);
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return getSliderRange(false);
				}
			});
	}
	
	/**
	 * Sets the maximum of the slider.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This has only an effect if the editor type is a slider.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param maximum the maximum
	 * @since 1.0
	 */
	public void setMaximum(final int maximum) {
		if(EDT.isExecutedInEDT())
			setSliderRange(maximum, false);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setMaximum") {
				@Override
				protected void execute() throws Throwable {
					setSliderRange(maximum, false);
				}
			});
	}

	@Override
	protected TableCellEditor createEditor(Object[] arguments) {
		if((boolean)arguments[0] == false)
			return new DefaultCellEditor(new NumericTextField());
		else
			return new SliderCellEditor();
	}
	
	@Override
	protected TableCellRenderer createRenderer(Object[] arguments) {
		return new NumericCellRenderer();
	}
	
	@Override
	protected Class<?> getValueClass() {
		return Number.class;
	}

	@Override
	protected Object onEditStop(Object value) {
		try {
			return NumberFormat.getInstance().parse(value.toString());
		}
		catch (ParseException ex) {
			return 0;
		}
	}
	
	/**
	 * Gets the range of the numeric property but only if the editor type is a slider.
	 * 
	 * @param minimum <code>true</code> to get the minimum otherwise <code>false</code> to get the maximum
	 * @return the range value
	 * @since 1.0
	 */
	private int getSliderRange(final boolean minimum) {
		if(editor instanceof SliderCellEditor)
			return minimum ? ((SliderCellEditor)editor).getMinimum() : ((SliderCellEditor)editor).getMaximum();
		else
			return 0;
	}
	
	/**
	 * Sets the range of the numeric property but only if the editor type is a slider.
	 * 
	 * @param value the range value
	 * @param minimum minimum <code>true</code> to get the minimum otherwise <code>false</code> to get the maximum
	 * @since 1.0
	 */
	private void setSliderRange(final int value, final boolean minimum) {
		if(editor instanceof SliderCellEditor) {
			if(minimum)
				((SliderCellEditor)editor).setMinimum(value);
			else
				((SliderCellEditor)editor).setMaximum(value);
		}
	}

}
