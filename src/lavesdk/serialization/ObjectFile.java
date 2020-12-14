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
 * Class:		ObjectFile
 * Task:		Save and load object data
 * Created:		30.09.13
 * LastChanges:	21.01.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.serialization;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lavesdk.utils.FileUtils;

/**
 * Represents an object file to serialize and deserialize object data.
 * <br><br>
 * <b>Load object file</b>:<br>
 * Use {@link #load()} to load the object file. To get a serializer for a specific object, invoke
 * {@link #getSerializer(Serializable, String)}. To load all objects for a specific type iterate over the serializers
 * with the use of {@link #getSerializerCount(String)}.<br>
 * <u>Example</u>:
 * <pre>
 * final ObjectFile of = new ObjectFile("myFile.of");
 * // load file
 * try {
 *     of.load();
 * }
 * catch(IOException e) {
 *     System.err.println(e.getMessage());
 * }
 * 
 * // file contains serializers for types "a" and "b"
 * for(int i = 0; i < of.getSerializerCount("a"); i++) {
 *     A a = new A();
 *     Serializer s = of.getSerializer(a, "a");
 *     a.deserialize(s);
 * }
 * for(int i = 0; i < of.getSerializerCount("b"); i++) {
 *     B b = new B();
 *     Serializer s = of.getSerializer(b, "b");
 *     b.deserialize(s);
 * }
 * </pre>
 * <b>Save objects</b>:<br>
 * To serialize objects invoke {@link #getSerializer(Serializable, String)} with the specific object to get the object's serializer.<br>
 * After that invoke {@link Serializable#serialize(Serializer)} with the serializer on the object to serialize its data.
 * Finally you only have to call {@link #save()} and all serializers that are requested will be stored in the file.<br>
 * <u>Example</u>:
 * <pre>
 * final ObjectFile of = new ObjectFile("myFile.of");
 * // serialize a list of objects of class A
 * for(int i = 0; i < list.size(); i++) {
 *     A a = list.get(i);
 *     Serializer s = of.getSerializer(a, "a");
 *     a.serialize(s);
 * }
 * // save file
 * try {
 *     of.save();
 * }
 * catch(IOException e) {
 *     System.err.println(e.getMessage());
 * }
 * </pre>
 * <b>Mapping</b>:<br>
 * If you have an identifier of a serializer then you can request the related object by calling {@link #getObject(int)}.<br>
 * <u>Example</u>: You use serializer identifiers to connect objects in an object file. If you want to know the object behind a serialize id
 * you can call the method above to get it.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class ObjectFile {
	
	/** the file name of the object file */
	private final String filename;
	/** all serializers */
	private final Map<Object, Serializer> serializers;
	/** mapping between object and serialize id */
	private final Map<Integer, Object> objects;
	/** a list that contains the identifiers of all serializers in the insertion order of their objects */
	private final List<Integer> objectsOrder;
	/** all serializers that are loaded from a file but not mapped to an object */
	private final List<Serializer> unallocatedSerializers;
	/** the number of serializers of a specific name in the file */
	private final HashMap<String, Integer> serializerNameCount;
	/** the next id of a new serializer */
	private int nextID;
	
	/**
	 * Creates a new object file.
	 * 
	 * @param file the file
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if file is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ObjectFile(final File file) throws NullPointerException {
		this(file.getAbsolutePath());
	}
	
	/**
	 * Creates a new object file.
	 * 
	 * @param filename the file name
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if filename is null</li>
	 * 		<li>if filename is empty</li>
	 * </ul>
	 * @since 1.0
	 */
	public ObjectFile(final String filename) throws IllegalArgumentException {
		if(filename == null || filename.isEmpty())
			throw new IllegalArgumentException("No valid argument!");
		
		this.filename = filename;
		serializers = new HashMap<Object, Serializer>();
		objectsOrder = new ArrayList<Integer>();
		objects = new HashMap<Integer, Object>();
		unallocatedSerializers = new ArrayList<Serializer>();
		serializerNameCount = new HashMap<String, Integer>();
		nextID = 1;
	}
	
	/**
	 * Loads the objects from the specified location.
	 * 
	 * @throws IOException
	 * <ul>
	 * 		<li>if the file contains invalid data</li>
	 * </ul>
	 * @since 1.0
	 */
	public final void load() throws IOException {
		final FileInputStream fis = new FileInputStream(filename);
		final ObjectInputStream ois = new ObjectInputStream(fis);
		
		// clear mapping because new data is stored
		serializers.clear();
		objectsOrder.clear();
		objects.clear();
		unallocatedSerializers.clear();
		serializerNameCount.clear();
		
		try {
			int i = 0;
			Serializer s;
			
			// read data from stream
			@SuppressWarnings("unchecked")
			final ArrayList<Serializer> list = (ArrayList<Serializer>)ois.readObject();
			// add all loaded serializers to list of unallocated serializers because now not mapping between
			// object and serializer is realized
			for(i = 0; i < list.size(); i++) {
				s = list.get(i);
				if(s != null) {
					unallocatedSerializers.add(s);
					incSerializerCount(s.getName());
				}
			}

			// update the next id
			int maxID = 0;
			for(i = 0; i < unallocatedSerializers.size(); i++) {
				s = unallocatedSerializers.get(i);
				if(s.getID() > maxID)
					maxID = s.getID();
			}
			
			// the next id must be greater then the maximum identifier of the loaded serializers
			if(maxID > nextID)
				nextID = maxID + 1;
		}
		catch(ClassNotFoundException | EOFException e) {
			throw new IOException("File contains invalid data!");
		}
		finally {
			// release system resources
			ois.close();
			fis.close();
		}
	}
	
	/**
	 * Saves the objects to the specified location.
	 * 
	 * @throws IOException
	 * <ul>
	 * 		<li>if file could not be written</li>
	 * </ul>
	 * @since 1.0
	 */
	public final void save() throws IOException {
		// create file if it does not exist
		final File file = FileUtils.createFilePath(filename);
		
		final FileOutputStream fos = new FileOutputStream(file);
		final ObjectOutputStream oos = new ObjectOutputStream(fos);
		final ArrayList<Serializer> data = new ArrayList<Serializer>(serializers.values().size());
		Serializer s;
		
		try {
			// add all serializers to the data array in their insertion order
			for(Integer id : objectsOrder) {
				s = serializers.get(objects.get(id));
				if(s != null)
					data.add(s);
				else
					throw new IOException("Because of inconsistency the object file cannot be saved (illegal serializer (null))!");
			}
			
			// write data to stream
			oos.writeObject(data);
			oos.flush();
		}
		catch(IOException e) {
			throw e;
		}
		finally {
			// release system resources
			oos.close();
			fos.close();
		}
	}
	
	/**
	 * Gets the number of serializers for a user specific type or section.
	 * <br><br>
	 * <b>Example</b>:<br>
	 * You had saved objects of types "a" and "b" to an object file. Now you want to load the file
	 * but you didn't know how many objects of type "a" and how many of type "b" are in the file
	 * to get their serializers. This information you get by invoking {@link #getSerializerCount(String)}
	 * and then get a serializer for each object of a type by calling {@link #getSerializer(Serializable, String)}.
	 * 
	 * @see #getSerializer(Serializable, String)
	 * @param serializerName the name of the serializer
	 * @return the number of serializers
	 * @since 1.0
	 */
	public final int getSerializerCount(final String serializerName) {
		final Integer i = serializerNameCount.get(serializerName);
		if(i == null)
			return 0;
		else
			return i;
	}
	
	/**
	 * Gets a serializer for a given object.
	 * <br><br>
	 * <b>Example</b>:<br>
	 * You have loaded an object file. Now you want to create the objects with the loaded data.
	 * With {@link #getSerializerCount(String)} you can get the number of serializers for a specific type
	 * that are loaded from the file. So you can go through all serializers and get an object specific serializer by
	 * calling {@link #getSerializer(Serializable, String)}.
	 * <pre>
	 * final ObjectFile of = new ObjectFile("myFile.of");
	 * // load file
	 * try {
	 *     of.load();
	 * }
	 * catch(IOException e) {
	 *     System.err.println(e.getMessage());
	 * }
	 * 
	 * // file contains serializers for types "a" and "b"
	 * for(int i = 0; i < of.getSerializerCount("a"); i++) {
	 *     A a = new A();
	 *     Serializer s = of.getSerializer(a, "a");
	 *     a.deserialize(s);
	 * }
	 * for(int i = 0; i < of.getSerializerCount("b"); i++) {
	 *     B b = new B();
	 *     Serializer s = of.getSerializer(b, "b");
	 *     b.deserialize(s);
	 * }
	 * </pre>
	 * If you want to save a file you can request the serializer for each object you want to save by invoking
	 * {@link #getSerializer(Serializable, String)} and then serialize the object with {@link Serializable#serialize(Serializer)}.
	 * After that you can call {@link #save()} to save the serializers of all objects.
	 * 
	 * @see #getObject(int)
	 * @param o the object
	 * @param serializerName the name of the serializer (more generally the user defined type of the object)
	 * @return the serializer
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if o is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <T extends Serializable> Serializer getSerializer(final T o, final String serializerName) throws IllegalArgumentException {
		if(o == null)
			throw new IllegalArgumentException("No valid argument!");
		
		Serializer s = serializers.get(o);
		
		// no valid serializer for object?
		if(s == null) {
			boolean incSerializerCount = false;
			
			// find an unallocated serializer with the given type
			for(int i = 0; i < unallocatedSerializers.size(); i++) {
				Serializer serializer = unallocatedSerializers.get(i);
				if(serializer.getName().equals(serializerName)) {
					s = serializer;
					// remove the serializer because now he is allocated
					unallocatedSerializers.remove(i);
					break;
				}
			}
			
			// if no serializer is found then create a new one for the object 
			if(s == null) {
				s = new Serializer(nextID++, serializerName);
				incSerializerCount = true;
			}
			
			// link the serializer
			serializers.put(o, s);
			objects.put(s.getID(), o);
			objectsOrder.add(s.getID());
			
			// increment the number of this serializer type if necessary
			if(incSerializerCount)
				incSerializerCount(serializerName);
		}
		
		return s;
	}
	
	/**
	 * Gets the related object from a given serialization identifier.
	 * 
	 * @param serializeID the identifier of the serializer
	 * @return the object that is serialized with the serializer with the given id
	 * @since 1.0
	 */
	public final Object getObject(final int serializeID) {
		return objects.get(serializeID);
	}
	
	/**
	 * Updates the related object for a specific serialization identifier.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This should only be done if the object used to get a serializer is invalid an must be replaced.
	 * 
	 * @param serializeID the identifier of the serializer
	 * @param o the object
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if serializeID is not existing</li>
	 * 		<li>if o is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final void updateObject(final int serializeID, final Object o) throws IllegalArgumentException {
		if(!objects.containsKey(serializeID) || o == null)
			throw new IllegalArgumentException("No valid argument!");
		
		objects.put(serializeID, o);
	}
	
	/**
	 * Increments the number of serializers of a specific name.
	 * 
	 * @param serializerName the name
	 * @since 1.0
	 */
	public void incSerializerCount(final String serializerName) {
		Integer i = serializerNameCount.get(serializerName);
		if(i == null)
			i = 1;
		else
			i++;
		
		serializerNameCount.put(serializerName, i);
	}
}
