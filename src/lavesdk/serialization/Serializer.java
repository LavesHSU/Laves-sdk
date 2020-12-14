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

package lavesdk.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to store object data.
 * <br><br>
 * Invoke {@link ObjectFile#getSerializer(lavesdk.serialization.Serializable, String)} to get a serializer for an object that should be
 * stored together with other objects in a file.
 * <br><br>
 * <b>Attention</b>:<br>
 * If you add mutable objects to the serializer ({@link #addObject(String, Serializable)}/{@link #addCollection(String, Collection)}/...) please
 * ensure that these objects are not changed until the serializer is saved persistent such as with an {@link ObjectFile}.
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class Serializer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** the identifier of the serializer */
	private final int id;
	/** the name of the serializer */
	private final String name;
	/** the data */
	protected final HashMap<String, Object> data;
	
	/**
	 * Creates a new serializer.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package or a sub class!<br>
	 * <i>DO NOT REMOVE THE PROTECTED VISIBILITY OF THIS CONSTRUCTOR</i>!
	 * 
	 * @param id the id of the serializer
	 * @param name the name/type of the serializer
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if id <code>< 1</code></li>
	 * 		<li>if name is null</li>
	 * </ul>
	 * @since 1.0
	 */
	protected Serializer(final int id, final String name) throws IllegalArgumentException {
		if(id < 1 || name == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.id = id;
		this.name = name;
		this.data = new HashMap<String, Object>();
	}
	
	/**
	 * Gets the id of the serializer.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This identifier is unique based on the {@link ObjectFile}.
	 * 
	 * @return the identifier
	 * @since 1.0
	 */
	public final int getID() {
		return id;
	}
	
	/**
	 * Gets the name/type of the serializer.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This name must not be unique, it describes only to which type of objects the serializer relates to.
	 * 
	 * @see ObjectFile#getSerializerCount(String)
	 * @return the name/type
	 * @since 1.0
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Adds an object to the serializer.
	 * 
	 * @param key the data key
	 * @param o the object
	 * @return the object
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <T extends Serializable> T addObject(final String key, final T o) throws IllegalArgumentException {
		return addData(key, o);
	}
	
	/**
	 * Gets an object.
	 * 
	 * @param key the data key
	 * @return the object or <code>null</code> if the serializer does not contain an object with the given key
	 * @since 1.0
	 */
	public final <T extends Serializable> T getObject(final String key) {
		return getObject(key, null);
	}
	
	/**
	 * Gets an object.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the object or defValue if the serializer does not contain an object with the given key
	 * @since 1.0
	 */
	public final <T extends Serializable> T getObject(final String key, final T defValue) {
		final Object o = getData(key, Object.class, defValue);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final T obj = (T)o;
			return obj;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Adds an integer value to the serializer.
	 * 
	 * @param key the data key
	 * @param i the value
	 * @return the value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final int addInt(final String key, final int i) throws IllegalArgumentException {
		return addData(key, i);
	}
	
	/**
	 * Gets an integer value for a given data key.
	 * 
	 * @param key the data key
	 * @return the value or <code>0</code> if the serializer does not contain an integer with the given key
	 * @since 1.0
	 */
	public final int getInt(final String key) {
		return getInt(key, 0);
	}
	
	/**
	 * Gets an integer value for a given data key.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the value or defValue if the serializer does not contain an integer with the given key
	 * @since 1.0
	 */
	public final int getInt(final String key, final int defValue) {
		return getData(key, Integer.class, defValue);
	}
	
	/**
	 * Adds a string value to the serializer.
	 * 
	 * @param key the data key
	 * @param s the value
	 * @return the value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final String addString(final String key, final String s) throws IllegalArgumentException {
		return addData(key, s);
	}
	
	/**
	 * Gets a string value for a given data key.
	 * 
	 * @param key the data key
	 * @return the value or empty string if the serializer does not contain a string with the given key
	 * @since 1.0
	 */
	public final String getString(final String key) {
		return getString(key, "");
	}
	
	/**
	 * Gets a string value for a given data key.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the value or defValue if the serializer does not contain a string with the given key
	 * @since 1.0
	 */
	public final String getString(final String key, final String defValue) {
		final Object o = data.get(key);
		if(o != null)
			return o.toString();
		else
			return defValue;
	}
	
	/**
	 * Adds a float value to the serializer.
	 * 
	 * @param key the data key
	 * @param f the value
	 * @return the value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final float addFloat(final String key, final float f) throws IllegalArgumentException {
		return addData(key, f);
	}
	
	/**
	 * Gets a float value for a given data key.
	 * 
	 * @param key the data key
	 * @return the value or <code>0.0f</code> if the serializer does not contain a float with the given key
	 * @since 1.0
	 */
	public final float getFloat(final String key) {
		return getFloat(key, 0.0f);
	}
	
	/**
	 * Gets a float value for a given data key.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the value or defValue if the serializer does not contain a float with the given key
	 * @since 1.0
	 */
	public final float getFloat(final String key, final float defValue) {
		return getData(key, Float.class, defValue);
	}
	
	/**
	 * Adds a double value to the serializer.
	 * 
	 * @param key the data key
	 * @param d the value
	 * @return the value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final double addDouble(final String key, final double d) throws IllegalArgumentException {
		return addData(key, d);
	}
	
	/**
	 * Gets a double value for a given data key.
	 * 
	 * @param key the data key
	 * @return the value or <code>0.0</code> if the serializer does not contain a double with the given key
	 * @since 1.0
	 */
	public final double getDouble(final String key) {
		return getDouble(key, 0.0);
	}
	
	/**
	 * Gets a double value for a given data key.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the value or defValue if the serializer does not contain a double with the given key
	 * @since 1.0
	 */
	public final double getDouble(final String key, final double defValue) {
		return getData(key, Double.class, defValue);
	}
	
	/**
	 * Adds a boolean value to the serializer.
	 * 
	 * @param key the data key
	 * @param b the value
	 * @return the value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final boolean addBoolean(final String key, final boolean b) throws IllegalArgumentException {
		return addData(key, b);
	}
	
	/**
	 * Gets a boolean value for a given data key.
	 * 
	 * @param key the data key
	 * @return the value or <code>false</code> if the serializer does not contain a boolean with the given key
	 * @since 1.0
	 */
	public final boolean getBoolean(final String key) {
		return getBoolean(key, false);
	}
	
	/**
	 * Gets a boolean value for a given data key.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the value or defValue if the serializer does not contain a boolean with the given key
	 * @since 1.0
	 */
	public final boolean getBoolean(final String key, final boolean defValue) {
		return getData(key, Boolean.class, defValue);
	}
	
	/**
	 * Adds a character value to the serializer.
	 * 
	 * @param key the data key
	 * @param c the value
	 * @return the value
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final char addChar(final String key, final char c) throws IllegalArgumentException {
		return addData(key, c);
	}
	
	/**
	 * Gets a character value for a given data key.
	 * 
	 * @param key the data key
	 * @return the value or <code>' '</code> if the serializer does not contain a character with the given key
	 * @since 1.0
	 */
	public final char getChar(final String key) {
		return getChar(key, ' ');
	}
	
	/**
	 * Gets a character value for a given data key.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the value or defValue if the serializer does not contain a character with the given key
	 * @since 1.0
	 */
	public final char getChar(final String key, final char defValue) {
		return getData(key, Character.class, defValue);
	}
	
	/**
	 * Adds a (sub) serializer to this serializer.
	 * 
	 * @param key the data key
	 * @param s the serializer
	 * @return the serializer
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final Serializer addSerializer(final String key, final Serializer s) throws IllegalArgumentException {
		return addData(key, s);
	}
	
	/**
	 * Gets a (sub) serializer for a given data key.
	 * 
	 * @param key the data key
	 * @return the serializer or <code>null</code> if there is no serializer for the given key
	 * @since 1.0
	 */
	public final Serializer getSerializer(final String key) {
		return getData(key, Serializer.class);
	}
	
	/**
	 * Adds a collection to the serializer.
	 * 
	 * @param key the data key
	 * @param c the collection
	 * @return the collection
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <E extends Serializable> Collection<E> addCollection(final String key, final Collection<E> c) throws IllegalArgumentException {
		return addData(key, c);
	}
	
	/**
	 * Gets a collection for a given data key.
	 * 
	 * @param key the data key
	 * @return the collection or <code>null</code> if the serializer does not contain a collection with the given key and type
	 * @since 1.0
	 */
	public final <E> Collection<E> getCollection(final String key) {
		return getCollection(key, null);
	}
	
	/**
	 * Gets a collection for a given data key.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the collection or defValue if the serializer does not contain a collection with the given key and type
	 * @since 1.0
	 */
	public final <E> Collection<E> getCollection(final String key, final Collection<E> defValue) {
		final Object o = data.get(key);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final Collection<E> c = (Collection<E>)o;
			return c;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Adds a list to the serializer.
	 * 
	 * @param key the data key
	 * @param l the list
	 * @return the list
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <E extends Serializable> List<E> addList(final String key, final List<E> l) throws IllegalArgumentException {
		return addData(key, l);
	}
	
	/**
	 * Gets a list for a given data key.
	 * 
	 * @param key the data key
	 * @return the list or <code>null</code> if the serializer does not contain a list with the given key and type
	 * @since 1.0
	 */
	public final <E> List<E> getList(final String key) {
		return getList(key, null);
	}
	
	/**
	 * Gets a list for a given data key.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the list or defValue if the serializer does not contain a list with the given key and type
	 * @since 1.0
	 */
	public final <E> List<E> getList(final String key, final List<E> defValue) {
		final Object o = data.get(key);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final List<E> l = (List<E>)o;
			return l;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Adds an array for a given data key.
	 * 
	 * @param key the data key
	 * @param value the array
	 * @return the array
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <T extends Serializable> T[] addArray(final String key, final T[] value) throws IllegalArgumentException {
		return addData(key, value);
	}
	
	/**
	 * Gets an array for a given data key.
	 * 
	 * @param key the data key
	 * @return the array or <code>null</code> if the serializer does not contain an array with the given key and type
	 * @since 1.0
	 */
	public final <T> T[] getArray(final String key) {
		return getArray(key, null);
	}
	
	/**
	 * Gets an array for a given data key.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the array or defValue if the serializer does not contain an array with the given key and type
	 * @since 1.0
	 */
	public final <T> T[] getArray(final String key, final T[] defValue) {
		final Object o = data.get(key);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final T[] a = (T[])o;
			return a;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Compares the specified object with this serializer and returns <code>true</code> if the specified object is a {@link Serializer}
	 * and serializes the same data (mapping of data key <-> data value) as this serializer.
	 * 
	 * @param obj the object that should be compared with this serializer
	 * @return <code>true</code> if both are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Serializer)
			return equals((Serializer)obj);
		else
			return false;
	}
	
	/**
	 * Compares the specified serializer with this serializer and returns <code>true</code> if the specified serializer
	 * serializes the same data (mapping of data key <-> data value) as this serializer.
	 * 
	 * @param s the serializer that should be compared with this one
	 * @return <code>true</code> if both are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean equals(final Serializer s) {
		if(s == null)
			return false;
		else
			return data.equals(s.data);
	}
	
	/**
	 * Indicates whether keys are overrideable.
	 * 
	 * @return <code>true</code> if keys are overrideable otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean areKeysOverrideable() {
		return false;
	}
	
	/**
	 * Gets an iterator over the data keys of the serializer.
	 * 
	 * @return iterator over the data keys
	 * @since 1.1
	 */
	public final String[] keys() {
		final Set<String> keySet = data.keySet();
		final Iterator<String> it = keySet.iterator();
		
		final String[] keys = new String[keySet.size()];
		int i = 0;
		while(it.hasNext())
			keys[i++] = it.next();
		
		return keys;
	}
	
	/**
	 * Checks if the given key is valid that means if <code>key != null</code> and the key does not exist
	 * in the data table of the serializer.
	 * 
	 * @param key the key
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	protected void checkKey(final String key) throws IllegalArgumentException {
		if(key == null || (!areKeysOverrideable() && data.containsKey(key)))
			throw new IllegalArgumentException("No valid key!");
	}
	
	/**
	 * Adds a data object to the serializer.
	 * 
	 * @param key the data key
	 * @param d the data object (<b>ensure that this data object is {@link Serializable}</b>)
	 * @return the data object
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not overrideable)</li>
	 * </ul>
	 * @since 1.0
	 */
	protected final <T> T addData(final String key, final T d) {
		checkKey(key);
		
		data.put(key, d);
		return d;
	}
	
	/**
	 * Gets the data of a field described by a specific data key in a general way.
	 * 
	 * @param key the data key
	 * @param c the data class
	 * @return the data object or <code>null</code> if the serializer does not contain a data object with the given key
	 * @since 1.0
	 */
	protected final <T> T getData(final String key, final Class<T> c) {
		return getData(key, c, null);
	}
	
	/**
	 * Gets the data of a field described by a specific data key in a general way.
	 * 
	 * @param key the data key
	 * @param c the data class
	 * @param defValue the default return value
	 * @return the data object or defValue if the serializer does not contain a data object with the given key
	 * @since 1.0
	 */
	protected final <T> T getData(final String key, final Class<T> c, final T defValue) {
		final Object o = data.get(key);
		
		if(o != null && c.isAssignableFrom(o.getClass()))
			return c.cast(o);
		else
			return defValue;
	}
	
	/**
	 * Removes the data of a field.
	 * 
	 * @param key the data key of the field
	 * @since 1.1
	 */
	protected final void removeData(final String key) {
		data.remove(key);
	}
	
	/**
	 * Freezes the current data of the serializer.
	 * <br><br>
	 * This is useful if the serializer contains mutable objects and it should be avoided that modifications at these
	 * objects have an effect on the serialized data.
	 * <br><br>
	 * Use the returned byte array to {@link #unfreezeData(byte[])} and to restore the data of the serializer from
	 * the tie of freezing.
	 * 
	 * @see #unfreezeData(byte[])
	 * @return the byte array that contains the current serializer data
	 * @since 1.0
	 */
	protected byte[] freezeData() {
		return freezeData(null);
	}
	
	/**
	 * Freezes the current data of the serializer.
	 * <br><br>
	 * This is useful if the serializer contains mutable objects and it should be avoided that modifications at these
	 * objects have an effect on the serialized data.
	 * <br><br>
	 * Use the returned byte array to {@link #unfreezeData(byte[])} and to restore the data of the serializer from
	 * the tie of freezing.
	 * 
	 * @param errorMsg a string that contains a possible error message after the method failed or <code>null</code>
	 * @see #unfreezeData(byte[])
	 * @return the byte array that contains the current serializer data
	 * @since 1.0
	 */
	protected byte[] freezeData(final StringBuilder errorMsg) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		
		try {
			oos = createObjectOutputStream(baos);
			oos.writeObject(data);
			
			return baos.toByteArray();
		}
		catch (IOException e) {
			if(errorMsg != null)
				errorMsg.append(e.toString());
			return null;
		}
		finally {
			if(oos != null) try { oos.close(); } catch(IOException e) {}
			if(baos != null) try { baos.close(); } catch(IOException e) {}
		}
	}
	
	/**
	 * Creates the object output stream that is used to freeze the data of the serializer.
	 * <br><br>
	 * Override this method if you need to create a custom {@link ObjectOutputStream}.
	 * 
	 * @param baos the byte array stream of the unfrozen data
	 * @return the object output stream
	 * @throws IOException
	 * <ul>
	 * 		<li>if an I/O error occurs</li>
	 * </ul>
	 * @since 1.0
	 */
	protected ObjectOutputStream createObjectOutputStream(final ByteArrayOutputStream baos) throws IOException {
		return new ObjectOutputStream(baos);
	}
	
	/**
	 * Unfreezes the data of the serializer that means the data is restored from the time of freezing.
	 * <br><br>
	 * This is useful if the serializer contains mutable objects and it should be avoided that modifications at these
	 * objects have an effect on the serialized data.
	 * 
	 * @see #freezeData()
	 * @param data the byte array that contains the data that should be restored
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if data is null</li>
	 * </ul>
	 * @since 1.0
	 */
	protected boolean unfreezeData(final byte[] data) throws IllegalArgumentException {
		return unfreezeData(data, null);
	}
	
	/**
	 * Unfreezes the data of the serializer that means the data is restored from the time of freezing.
	 * <br><br>
	 * This is useful if the serializer contains mutable objects and it should be avoided that modifications at these
	 * objects have an effect on the serialized data.
	 * 
	 * @see #freezeData()
	 * @param data the byte array that contains the data that should be restored
	 * @param errorMsg a string that contains a possible error message after the method failed or <code>null</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if data is null</li>
	 * </ul>
	 * @since 1.0
	 */
	protected boolean unfreezeData(final byte[] data, final StringBuilder errorMsg) throws IllegalArgumentException {
		if(data == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream ois = null;
		
		try {
			ois = createObjectInputStream(bais);
			
			@SuppressWarnings("unchecked")
			final Map<String, Object> dataMap = (Map<String, Object>)ois.readObject();
			
			// we need to copy the serialized data so remove the old stuff
			this.data.clear();
			
			final Iterator<String> it = dataMap.keySet().iterator();
			String key;
			
			// load the data from the deserialized map
			while(it.hasNext()) {
				key = it.next();
				this.data.put(key, dataMap.get(key));
			}
			
			return true;
		}
		catch (IOException | ClassNotFoundException e) {
			if(errorMsg != null)
				errorMsg.append(e.toString());
			return false;
		}
		finally {
			if(ois != null) try { ois.close(); } catch(IOException e) {}
			if(bais != null) try { bais.close(); } catch(IOException e) {}
		}
	}
	
	/**
	 * Creates the object input stream that is used to unfreeze the data of the serializer.
	 * <br><br>
	 * Override this method if you need to create a custom {@link ObjectInputStream}.
	 * 
	 * @param bais the byte array stream of the freezed data
	 * @return the object input stream
	 * @throws IOException
	 * <ul>
	 * 		<li>if an I/O error occurs</li>
	 * </ul>
	 * @since 1.0
	 */
	protected ObjectInputStream createObjectInputStream(final ByteArrayInputStream bais) throws IOException {
		return new ObjectInputStream(bais);
	}

}
