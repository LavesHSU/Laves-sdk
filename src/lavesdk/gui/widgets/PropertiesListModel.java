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
 * Class:		PropertiesListModel
 * Task:		The model of a properties list
 * Created:		02.12.13
 * LastChanges:	28.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.language.LanguageFile;

/**
 * Represents the model of a {@link PropertiesList}.
 * <br><br>
 * Use {@link #add(Property)} to add a property to the list and {@link #getPropertyCount()}/{@link #getProperty(int)} to iterate over
 * the existing properties. With {@link #getProperty(String)} you can get a property by its name or you can use a more specific one like
 * {@link #getTextProperty(String)}/{@link #getListProperty(String)}/{@link #getBooleanProperty(String)}/...
 * 
 * @author jdornseifer
 * @since 1.0
 * @since 1.0
 */
public class PropertiesListModel {
	
	/** the list of properties */
	private final List<Property> properties;
	/** the properties (key = name, value = property) */
	private final Map<String, Property> propertiesMap;
	/** the corresponding properties list */
	private PropertiesList list;
	/** the caption of the property name column */
	private final String nameCaption;
	/** the caption of the property value column */
	private final String valueCaption;
	
	/**
	 * Creates a new model.
	 * 
	 * @since 1.0
	 */
	public PropertiesListModel() {
		this(null, null);
	}
	
	/**
	 * Creates a new model.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * captions in the properties list. The following language labels are available:
	 * <ul>
	 * 		<li><i>PROPERTIESLIST_NAME_COLUMN</i>: the caption of the property name column</li>
	 * 		<li><i>PROPERTIESLIST_VALUE_COLUMN</i>: the caption of the property value column</li>
	 * </ul>
	 * 
	 * @param langFile the language file or <code>null</code> if the properties list should not use language dependent column captions
	 * @param langID the language id
	 * @since 1.0
	 */
	public PropertiesListModel(final LanguageFile langFile, final String langID) {
		properties = new ArrayList<Property>(5);
		propertiesMap = new HashMap<String, Property>();
		list = null;
		nameCaption = LanguageFile.getLabel(langFile, "PROPERTIESLIST_NAME_COLUMN", langID, "Property Name");
		valueCaption = LanguageFile.getLabel(langFile, "PROPERTIESLIST_VALUE_COLUMN", langID, "Value");
	}
	
	/**
	 * Adds a new property to the model.
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
		if(p == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			return internalAdd(p);
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return internalAdd(p);
				}
			});
	}
	
	/**
	 * Removes a property from the list.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param name the name of the property that should be removed
	 * @return <code>true</code> if the property could be removed otherwise <code>false</code> (this could be if there is no property with that name)
	 */
	public final boolean remove(final String name) {
		if(EDT.isExecutedInEDT())
			return internalRemove(name);
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return internalRemove(name);
				}
			});
	}
	
	/**
	 * Removes all properties from the list.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public final void removeAll() {
		if(EDT.isExecutedInEDT())
			internalRemoveAll();
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeAll") {
				@Override
				protected void execute() throws Throwable {
					internalRemoveAll();
				}
			});
	}
	
	/**
	 * Gets the number of properties.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of properties
	 * @since 1.0
	 */
	public int getPropertyCount() {
		if(EDT.isExecutedInEDT())
			return properties.size();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return properties.size();
				}
			});
	}
	
	/**
	 * Gets a property at a given index.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the property
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getPropertyCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public Property getProperty(final int index) throws IndexOutOfBoundsException {
		if(EDT.isExecutedInEDT())
			return properties.get(index);
		else
			return EDT.execute(new GuiRequest<Property>() {
				@Override
				protected Property execute() throws Throwable {
					return properties.get(index);
				}
			});
	}
	
	/**
	 * Gets the property with the specified name.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param name the name of the property
	 * @return the property or <code>null</code> if there is no property with the given name
	 * @since 1.0
	 */
	public Property getProperty(final String name) {
		return getProperty(name, Property.class);
	}
	
	/**
	 * Gets the property with the specified name and class.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
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
		if(EDT.isExecutedInEDT())
			return internalGetProperty(name, c);
		else
			return EDT.execute(new GuiRequest<T>() {
				@Override
				protected T execute() throws Throwable {
					return internalGetProperty(name, c);
				}
			});
	}
	
	/**
	 * Gets the text property with the specified name.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param name the name of the text property
	 * @return the text property or <code>null</code> if there is no text property with the given name
	 * @since 1.0
	 */
	public TextProperty getTextProperty(final String name) {
		return getProperty(name, TextProperty.class);
	}
	
	/**
	 * Gets the numeric property with the specified name.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param name the name of the numeric property
	 * @return the numeric property or <code>null</code> if there is no numeric property with the given name
	 * @since 1.0
	 */
	public NumericProperty getNumericProperty(final String name) {
		return getProperty(name, NumericProperty.class);
	}
	
	/**
	 * Gets the boolean property with the specified name.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param name the name of the boolean property
	 * @return the boolean property or <code>null</code> if there is no boolean property with the given name
	 * @since 1.0
	 */
	public BooleanProperty getBooleanProperty(final String name) {
		return getProperty(name, BooleanProperty.class);
	}
	
	/**
	 * Gets the list property with the specified name.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param name the name of the list property
	 * @return the list property or <code>null</code> if there is no list property with the given name
	 * @since 1.0
	 */
	public ListProperty getListProperty(final String name) {
		return getProperty(name, ListProperty.class);
	}
	
	/**
	 * Gets the color property with the specified name.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param name the name of the color property
	 * @return the color property or <code>null</code> if there is no color property with the given name
	 * @since 1.0
	 */
	public ColorProperty getColorProperty(final String name) {
		return getProperty(name, ColorProperty.class);
	}
	
	/**
	 * Gets the caption of the property name column.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the caption
	 * @since 1.0
	 */
	public String getNameColumnCaption() {
		return nameCaption;
	}
	
	/**
	 * Gets the caption of the property value column.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the caption
	 * @since 1.0
	 */
	public String getValueColumnCaption() {
		return valueCaption;
	}
	
	/**
	 * Indicates whether the header of the properties list should be visible.
	 * 
	 * @return <code>true</code> if the header with the column captions should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isHeaderVisible() {
		return false;
	}
	
	/**
	 * Gets the row height of the properties list.
	 * 
	 * @return the row height
	 * @since 1.0
	 */
	public int getRowHeight() {
		return 20;
	}
	
	/**
	 * Indicates if the properties list should use the default row sorter.
	 * 
	 * @return <code>true</code> if the properties list should auto create a row sorter otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean hasAutoRowSorter() {
		return false;
	}
	
	/**
	 * Sets the list of the model.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param list the properties list
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if list is null</li>
	 * 		<li>if this model is already used by another properties list</li>
	 * </ul>
	 * @since 1.0
	 */
	void setList(final PropertiesList list) throws IllegalArgumentException {
		if(this.list != null || list == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.list = list;
	}
	
	/**
	 * Gets the list of the model.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the properties list or <code>null</code> if there is currently no properties list that is associated with the model
	 * @since 1.0
	 */
	PropertiesList getList() {
		return list;
	}
	
	/**
	 * Adds a new property to the model.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param p a <b>valid</b> property
	 * @return <code>true</code> if the property could be added otherwise <code>false</code> (this could be if there is already a property with that name)
	 * @since 1.0
	 */
	private final boolean internalAdd(final Property p) throws IllegalArgumentException {
		if(p == null)
			throw new IllegalArgumentException("No valid argument!");
		
		// map already contains a property with that name then break up
		if(propertiesMap.containsKey(p.getName()))
			return false;
		
		// add the property
		properties.add(p);
		propertiesMap.put(p.getName(), p);
		// add property to table model of the list
		if(list != null)
			list.getTableModel().addRow(p);
		
		return true;
	}
	
	/**
	 * Removes a property from the list.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param name the name of the property that should be removed
	 * @return <code>true</code> if the property could be removed otherwise <code>false</code> (this could be if there is no property with that name)
	 */
	private final boolean internalRemove(final String name) {
		final Property p = propertiesMap.get(name);
		
		// no such property? then break up!
		if(p == null)
			return false;
		
		// remove the property
		properties.remove(p);
		propertiesMap.remove(name);
		// remove property from table model of the list
		if(list != null)
			list.getTableModel().removeRow(p);
		
		return true;
	}
	
	/**
	 * Removes all properties from the list.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @since 1.0
	 */
	private final void internalRemoveAll() {
		properties.clear();
		propertiesMap.clear();
		if(list != null)
			list.getTableModel().removeAll();
	}
	
	/**
	 * Gets the property with the specified name and class.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param name the name of the property
	 * @param c the class of the property (like <code>TextProperty.class</code>, ...)
	 * @return the property or <code>null</code> if there is either no property with the given name or the property of the name is not assignable from the specified class
	 * @since 1.0
	 */
	private <T extends Property> T internalGetProperty(final String name, final Class<T> c) {
		// close editors that all data is valid
		if(list != null)
			list.closeEditors();
		
		final Property p = propertiesMap.get(name);
		
		if(p.getClass().isAssignableFrom(c))
			return c.cast(p);
		else
			return null;
	}

}
