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
 * Class:		AlgorithmState
 * Task:		Store the state of an algorithm after a specific step
 * Created:		10.11.13
 * LastChanges:	21.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lavesdk.algorithm.exceptions.IllegalInvocationException;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.algorithm.plugin.views.GraphScene;
import lavesdk.math.Matrix;
import lavesdk.math.Set;
import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Graph;
import lavesdk.math.graph.PathByID;
import lavesdk.math.graph.TrailByID;
import lavesdk.math.graph.Vertex;
import lavesdk.math.graph.WalkByID;
import lavesdk.math.graph.matching.MatchingByID;
import lavesdk.serialization.Serializer;

/**
 * Stores the state of an algorithm after (executing) a specific step of the algorithm.
 * <br><br>
 * Use the add methods ({@link #addInt(String, int)}/{@link #addSet(String, Set)}/...) to store the variables
 * that are used in the algorithm.<br>
 * The state has predefined methods to add or request objects of all important types like {@link Integer}, {@link Float},
 * {@link Set}s, {@link Matrix}s, {@link WalkByID}s and so on.
 * <br><br>
 * To assign a state to an algorithm use the get methods ({@link #getInt(String, int)}/{@link #getSet(String, Set)}/...) and
 * assign the data to the algorithm variables.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class AlgorithmState extends Serializer implements AlgorithmStateAttachment {
	
	/** the corresponding plugin of the state */
	private final AlgorithmPlugin plugin;
	/** the step which state is stored */
	private final int stepID;
	/** the byte array that holds the state data */
	private byte[] state;
	/** the map of attachment objects of an algorithm state  */
	private final Map<String, Object> attachment;
	
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new state.
	 * 
	 * @param stepID the id of the step whose state is stored
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if plugin is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmState(final AlgorithmPlugin plugin, final int stepID) throws IllegalArgumentException {
		super(1, "AlgorithmState");
		
		if(plugin == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.plugin = plugin;
		this.stepID = stepID;
		this.state = null;
		this.attachment = new HashMap<String, Object>();
	}
	
	/**
	 * Gets the id of the step whose state is stored.
	 * 
	 * @return the id of the step
	 * @since 1.0
	 */
	public final int getStepID() {
		return stepID;
	}
	
	/**
	 * Adds a (mathematical) set to the state.
	 * 
	 * @param key the data key
	 * @param set the set
	 * @return the set
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <E extends Serializable> Set<E> addSet(final String key, final Set<E> set) throws IllegalArgumentException {
		return addData(key, set);
	}
	
	/**
	 * Gets a (mathematical) set for a given data key.
	 * 
	 * @param key the data key
	 * @return the set or <code>null</code> if the state does not contain a set with the given key and type
	 * @since 1.0
	 */
	public final <E extends Serializable> Set<E> getSet(final String key) {
		return getSet(key, null);
	}
	
	/**
	 * Gets a (mathematical) set for a given data key.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the set or defValue if the state does not contain a set with the given key and type
	 * @since 1.0
	 */
	public final <E extends Serializable> Set<E> getSet(final String key, final Set<E> defValue) {
		final Object o = data.get(key);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final Set<E> s = (Set<E>)o;
			return s;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Adds a matrix to the state.
	 * 
	 * @param key the data key
	 * @param matrix the matrix
	 * @return the matrix
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <E extends Serializable> Matrix<E> addMatrix(final String key, final Matrix<E> matrix) throws IllegalArgumentException {
		return addData(key, matrix);
	}
	
	/**
	 * Gets a matrix for a given data key.
	 * 
	 * @param key the data key
	 * @return the matrix or <code>null</code> if the state does not contain a matrix with the given key and type
	 * @since 1.0
	 */
	public final <E extends Serializable> Matrix<E> getMatrix(final String key) {
		return getMatrix(key, null);
	}
	
	/**
	 * Gets a matrix for a given data key.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the matrix or defValue if the state does not contain a matrix with the given key and type
	 * @since 1.0
	 */
	public final <E extends Serializable> Matrix<E> getMatrix(final String key, final Matrix<E> defValue) {
		final Object o = data.get(key);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final Matrix<E> m = (Matrix<E>)o;
			return m;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Adds a map to the state.
	 * 
	 * @param key the data key
	 * @param map the map
	 * @return the map
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <K extends Serializable, V extends Serializable> Map<K, V> addMap(final String key, final Map<K, V> map) throws IllegalArgumentException {
		return addData(key, map);
	}
	
	/**
	 * Gets a map for a given data key.
	 * 
	 * @param key the data key
	 * @return the map or <code>null</code> if the state does not contain a map with the given key and type
	 * @since 1.0
	 */
	public final <K, V> Map<K, V> getMap(final String key) {
		return getMap(key, null);
	}
	
	/**
	 * Gets a map for a given data key.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the map or defValue if the state does not contain a map with the given key and type
	 * @since 1.0
	 */
	public final <K, V> Map<K, V> getMap(final String key, final Map<K, V> defValue) {
		final Object o = data.get(key);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final Map<K, V> m = (Map<K, V>)o;
			return m;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Adds a matching to the state.
	 * 
	 * @param key the data key
	 * @param m the matching
	 * @return the matching
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <E extends Edge> MatchingByID<E> addMatching(final String key, final MatchingByID<E> m) throws IllegalArgumentException {
		return addData(key, m);
	}
	
	/**
	 * Gets a matching for a given data key.
	 * 
	 * @param key the data key
	 * @param graph the graph its edges can be part of the matching (<b>ensure that this is the one the matching uses when it was added to the state</b>)
	 * @return the matching or <code>null</code> if the state does not contain a matching with the given key and type
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <V extends Vertex, E extends Edge> MatchingByID<E> getMatching(final String key, final Graph<V, E> graph) throws IllegalArgumentException {
		return getMatching(key, graph, null);
	}
	
	/**
	 * Gets a matching for a given data key.
	 * 
	 * @param key the data key
	 * @param graph the graph its edges can be part of the matching (<b>ensure that this is the one the matching uses when it was added to the state</b>)
	 * @param defValue the default return value
	 * @return the matching or defValue if the state does not contain a matching with the given key and type
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <V extends Vertex, E extends Edge> MatchingByID<E> getMatching(final String key, final Graph<V, E> graph, final MatchingByID<E> defValue) throws IllegalArgumentException {
		final Object o = data.get(key);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final MatchingByID<E> m = (MatchingByID<E>)o;
			m.setGraph(graph);
			return m;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Adds a walk to the state.
	 * 
	 * @param key the data key
	 * @param w the walk
	 * @return the walk
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <V extends Vertex> WalkByID<V> addWalk(final String key, final WalkByID<V> w) throws IllegalArgumentException {
		return addData(key, w);
	}
	
	/**
	 * Gets a walk for a given data key.
	 * 
	 * @param key the data key
	 * @param graph the graph its edges can be part of the walk (<b>ensure that this is the one the walk uses when it was added to the state</b>)
	 * @return the walk or <code>null</code> if the state does not contain a walk with the given key and type
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <V extends Vertex, E extends Edge> WalkByID<V> getWalk(final String key, final Graph<V, E> graph) throws IllegalArgumentException {
		return getWalk(key, graph, null);
	}
	
	/**
	 * Gets a walk for a given data key.
	 * 
	 * @param key the data key
	 * @param graph the graph its edges can be part of the walk (<b>ensure that this is the one the walk uses when it was added to the state</b>)
	 * @param defValue the default return value
	 * @return the walk or defValue if the state does not contain a walk with the given key and type
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <V extends Vertex, E extends Edge> WalkByID<V> getWalk(final String key, final Graph<V, E> graph, final WalkByID<V> defValue) throws IllegalArgumentException {
		final Object o = data.get(key);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final WalkByID<V> w = (WalkByID<V>)o;
			w.setGraph(graph);
			return w;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Adds a path to the state.
	 * 
	 * @param key the data key
	 * @param p the path
	 * @return the path
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <V extends Vertex> PathByID<V> addPath(final String key, final PathByID<V> p) throws IllegalArgumentException {
		return addData(key, p);
	}
	
	/**
	 * Gets a path for a given data key.
	 * 
	 * @param key the data key
	 * @param graph the graph its edges can be part of the path (<b>ensure that this is the one the path uses when it was added to the state</b>)
	 * @return the path or <code>null</code> if the state does not contain a path with the given key and type
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <V extends Vertex, E extends Edge> PathByID<V> getPath(final String key, final Graph<V, E> graph) throws IllegalArgumentException {
		return getPath(key, graph, null);
	}
	
	/**
	 * Gets a path for a given data key.
	 * 
	 * @param key the data key
	 * @param graph the graph its edges can be part of the path (<b>ensure that this is the one the path uses when it was added to the state</b>)
	 * @param defValue the default return value
	 * @return the path or defValue if the state does not contain a path with the given key and type
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <V extends Vertex, E extends Edge> PathByID<V> getPath(final String key, final Graph<V, E> graph, final PathByID<V> defValue) throws IllegalArgumentException {
		final Object o = data.get(key);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final PathByID<V> p = (PathByID<V>)o;
			p.setGraph(graph);
			return p;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Adds a trail to the state.
	 * 
	 * @param key the data key
	 * @param t the trail
	 * @return the trail
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <V extends Vertex> TrailByID<V> addTrail(final String key, final TrailByID<V> t) throws IllegalArgumentException {
		return addData(key, t);
	}
	
	/**
	 * Gets a trail for a given data key.
	 * 
	 * @param key the data key
	 * @param graph the graph its edges can be part of the trail (<b>ensure that this is the one the trail uses when it was added to the state</b>)
	 * @return the trail or <code>null</code> if the state does not contain a trail with the given key and type
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <V extends Vertex, E extends Edge> TrailByID<V> getTrail(final String key, final Graph<V, E> graph) throws IllegalArgumentException {
		return getTrail(key, graph, null);
	}
	
	/**
	 * Gets a trail for a given data key.
	 * 
	 * @param key the data key
	 * @param graph the graph its edges can be part of the trail (<b>ensure that this is the one the trail uses when it was added to the state</b>)
	 * @param defValue the default return value
	 * @return the trail or defValue if the state does not contain a trail with the given key and type
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final <V extends Vertex, E extends Edge> TrailByID<V> getTrail(final String key, final Graph<V, E> graph, final TrailByID<V> defValue) throws IllegalArgumentException {
		final Object o = data.get(key);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final TrailByID<V> t = (TrailByID<V>)o;
			t.setGraph(graph);
			return t;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T addAttachment(String key, T attachment) throws IllegalArgumentException {
		if(key == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.attachment.put(key, attachment);
		return attachment;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T getAttachment(String key) {
		return getAttachment(key, null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T getAttachment(String key, T defValue) {
		final Object o = attachment.get(key);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final T attachment = (T)o;
			return attachment;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	/**
	 * Gets a scene for a given data key.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use {@link GraphScene#reverse()} to reverse a graph scene meaning to restore the visual appearance of a graph before the scene was recorded.
	 * 
	 * @param key the data key
	 * @param defValue the default return value
	 * @return the scene or defValue if the state does not contain a scene with the given key and type
	 * @since 1.0
	 */
	public final <V extends Vertex, E extends Edge> GraphScene<V, E> getScene(final String key, final GraphScene<V, E> defValue) {
		final Object o = attachment.get(key);
		
		if(o == null)
			return defValue;
		
		try {
			@SuppressWarnings("unchecked")
			final GraphScene<V, E> scene = (GraphScene<V, E>)o;
			return scene;
		}
		catch(ClassCastException e) {
			return defValue;
		}
	}
	
	@Override
	public boolean equals(Serializer s) {
		if(s instanceof AlgorithmState)
			return equals((AlgorithmState)s);
		else
			return false;
	}
	
	/**
	 * Compares the specified state with this state and returns <code>true</code> if the specified state
	 * has the same step id and the same data (mapping of data key <-> data value) as this state.
	 * 
	 * @param s the state that should be compared with this one
	 * @return <code>true</code> if both are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean equals(final AlgorithmState s) {
		if(s == null)
			return false;
		else
			return this.stepID == s.stepID && super.equals(s);
	}
	
	/**
	 * Freezes the current data of the state.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This must be done after the state is recorded because the state can contain mutable objects like lists, sets, ... whose content
	 * can change!
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @see #unfreeze()
	 * @since 1.0
	 */
	final void freeze() {
		/*
		 * the step id must not be frozen because it is final which means immutable.
		 */
		final StringBuilder err = new StringBuilder();
		state = freezeData();
		
		if(state == null)
			System.err.println("Algorithm state could not be frozen!\n" + err.toString());
	}
	
	/**
	 * Unfreezes the data of the state that means the data is restored from the time of freezing.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This must be done before the state is used for restoration!
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @throws IllegalInvocationException
	 * <ul>
	 * 		<li>if <code>freeze()</code> was not yet successfully invoked</li>
	 * </ul>
	 * @since 1.0
	 */
	final void unfreeze() throws IllegalInvocationException {
		if(state == null)
			throw new IllegalInvocationException("State is unfrozen! Please invoke freeze() first.");
		
		final StringBuilder err = new StringBuilder();
		
		if(!unfreezeData(state, err))
			System.err.println("Algorithm state could not be unfrozen!\n" + err.toString());
	}
	
	@Override
	protected ObjectInputStream createObjectInputStream(ByteArrayInputStream bais) throws IOException {
		return new StateInputStream(plugin.getClass().getClassLoader(), bais);
	}
	
	/**
	 * A custom input stream to load state data.
	 * <br><br>
	 * {@link AlgorithmPlugin}s can store custom objects that can only be created using the class loader of the plugin. It must not be that
	 * the state and the plugin are created by the same class loader and therefore it cannot be ensured that all objects are restorable.
	 * To avoid this problem it is used the class loader of a plugin first to create an object of the input stream and only if this does not
	 * work the resolving is passed on the parent input stream.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class StateInputStream extends ObjectInputStream {
		
		/** the custom class loader that should be used to load classes from the stream */
		private final ClassLoader clsl;
		
		/**
		 * Creates a new state input stream.
		 * 
		 * @param clsl a custom class loader that should be used to load classes from the stream
		 * @param is the input stream
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if clsl is null</li>
		 * </ul>
		 * @throws IOException
		 * <ul>
		 * 		<li>if an I/O error occurs</li>
		 * </ul>
		 * @since 1.0
		 */
		public StateInputStream(final ClassLoader clsl, final InputStream is) throws IllegalArgumentException, IOException {
			super(is);
			
			if(clsl == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.clsl = clsl;
		}
		
		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			try {
				// try to load the class using the custom class loader otherwise use the default resolver
				return Class.forName(desc.getName(), false, clsl);
			}
			catch(ClassNotFoundException e) {
				return super.resolveClass(desc);
			}
		}
		
	}

}
