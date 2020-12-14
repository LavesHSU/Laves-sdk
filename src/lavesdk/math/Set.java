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

package lavesdk.math;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a mathematical set of elements which means it is not permissible that the set of elements contains:
 * <ul>
 * 		<li>a null element</li>
 * 		<li>itself</li>
 * 		<li>an element twice</li>
 * </ul>
 * <b>Set operations</b>:
 * <ul>
 * 		<li>{@link #union(Set, Set)}/{@link #union(Collection)}/{@link #union(Set[])}</li>
 * 		<li>{@link #intersection(Set, Set)}/{@link #intersection(Collection)}/{@link #intersection(Set[])}</li>
 * 		<li>{@link #complement(Set, Set)}</li>
 * 		<li>{@link #symDifference(Set, Set)}</li>
 * </ul>
 * The internal data structure of the set is based on an {@link ArrayList}.
 * <br><br>
 * You can use {@link #parse(String, ElementParser, String)} to convert a string representation of a set into a concrete object.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 * @param <E> the type of a set element
 */
public class Set<E> implements java.util.Set<E>, Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	
	/** the set as a list */
	private final List<E> set;
	
	/**
	 * Creates an empty set with an initial capacity of ten.
	 * 
	 * @since 1.0
	 */
	public Set() {
		this(10);
	}
	
	/**
	 * Creates an empty set with the specified initial capacity.
	 * 
	 * @param capacity the initial capacity of the set
	 * @since 1.0
	 */
	public Set(final int capacity) {
		set = new ArrayList<E>(capacity);
	}
	
	/**
	 * Creates a set with the specified elements in the given array.
	 * 
	 * @param elements the elements the set should contain initially
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if elements is null</li>
	 * 		<li>if elements contains a null element</li>
	 * </ul>
	 * @since 1.0
	 */
	public Set(final E[] elements) throws IllegalArgumentException {
		this(toCollection(elements));
	}
	
	/**
	 * Creates a set with the specified elements in the given collection.
	 * 
	 * @param c the collection
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if c is null</li>
	 * 		<li>if c contains a null element</li>
	 * </ul>
	 * @since 1.0
	 */
	public Set(final Collection<? extends E> c) throws NullPointerException {
		set = new ArrayList<E>(c.size());
		addAll(c);
	}

	/**
	 * Adds a new element to the set.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It is not permissible that the set contains a null element, itself or an element twice.
	 * 
	 * @param e the element
	 * @return <code>true</code> if the element could be added otherwise <code>false</code>
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if e is null</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public boolean add(E e) throws NullPointerException {
		// it is not permissible that a set contains a null element, itself or an element twice
		if(e == null)
			throw new NullPointerException("element is null");
		else if(e == this || set.contains(e))
			return false;
		
		return set.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if(c == null)
			throw new NullPointerException("collection is null");
		
		final Iterator<? extends E> it = c.iterator();
		E e;
		
		// transfer collection data to this set
		while(it.hasNext()) {
			e = it.next();
			
			// as claimed by java.util.Set<E> throw a NullPointerException if the collection contains a null element
			// and it is not permissible to add null elements to this set
			if(e == null)
				throw new NullPointerException("collection contains null element");
			
			add(e);
		}
		
		return true;
	}
	
	/**
	 * Adds all of the elements in the specified array to this set if they're not already present (optional operation).
	 * If the specified array is also a set, the addAll operation effectively modifies this set so that its value is the
	 * union of the two sets.<br>
	 * The behavior of this operation is undefined if the specified array is modified while the operation is in progress.
	 * 
	 * @param a array containing elements to be added to this set
	 * @return <code>true</code> if this set changed as a result of the call
	 * @throws
	 * <ul>
	 * 		<li>if the specified array contains a null element and this set does not permit null elements, or if the specified array is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean addAll(final E[] a) {
		return addAll(toCollection(a));
	}

	@Override
	public void clear() {
		set.clear();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return set.containsAll(c);
	}
	
	/**
	 * Returns <code>true</code> if this set contains all of the elements in the specified array.
	 * 
	 * @param a array to be checked for containment in this collection
	 * @return <code>true</code> if this set contains all of the elements in the specified array
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if the specified array contains one or more null elements and this set does not permit null elements, or if the specified array is null.</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean containsAll(final E[] a) throws NullPointerException {
		return containsAll(toCollection(a));
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return set.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return set.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if(c == null)
			return false;
		
		boolean result = true;
		
		for(Object o : c)
			result = result && remove(o);
		
		return result;
	}
	
	/**
	 * Removes all of this set's elements that are also contained in the specified array.
	 * After this call returns, this set will contain no elements in common with the specified array.
	 * 
	 * @param a array containing elements to be removed from this set
	 * @return <code>true</code> if this collection changed as a result of the call
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if the specified array is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean removeAll(final E[] a) {
		return removeAll(toCollection(a));
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return set.retainAll(c);
	}
	
	/**
	 * Retains only the elements in this set that are contained in the specified array (optional operation).
	 * In other words, removes from this set all of its elements that are not contained in the specified array.
	 * If the specified array is also a set, this operation effectively modifies this set so that its value is the
	 * intersection of the two sets.
	 * 
	 * @param a array containing elements to be retained in this set
	 * @return <code>true</code> if this set changed as a result of the call
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if the specified array is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean retainAll(final E[] a) throws NullPointerException {
		return retainAll(toCollection(a));
	}

	@Override
	public int size() {
		return set.size();
	}
	
	/**
	 * Gets a specific element of the set at the given index.
	 * 
	 * @param index the index
	 * @return the element
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= size()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public E get(final int index) throws IndexOutOfBoundsException {
		return set.get(index);
	}

	@Override
	public Object[] toArray() {
		return set.toArray();
	}

	@Override
	public <T> T[] toArray(T[] t) {
		return set.toArray(t);
	}
	
	/**
	 * Gets the set of elements as a list.
	 * 
	 * @return a list of the elements in the set.
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	public List<E> asList() {
		return (List<E>)((ArrayList<E>)set).clone();
	}
	
	/**
	 * Gets a shallow copy of this set (the elements in the set are not cloned).
	 * 
	 * @return a clone of this set
	 * @since 1.0
	 */
	@Override
	public Set<E> clone() {
		return new Set<E>(this);
	}
	
	/**
	 * Indicates whether this set equals the given collection.
	 * <br><br>
	 * This is the case if both sets have the same size and this set contains all elements of the specified set.
	 * 
	 * @param set another set
	 * @return <code>true</code> if both sets are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean equals(final Collection<E> set) {
		if(set == this)
			return true;
		
		return this.size() == set.size() && this.containsAll(set);
	}
	
	/**
	 * Indicates whether this set equals the specified one.
	 * 
	 * @param o another set of the same type that should be compared with this set
	 * @return <code>true</code> if the sets are equal otherwise <code>false</code>
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if(o instanceof Collection) {
			try {
				return equals((Collection<E>)o);
			}
			catch(ClassCastException e) {
				return false;
			}
		}
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return set.hashCode();
	}
	
	@Override
	public String toString() {
		final StringBuilder data = new StringBuilder();
		
		for(int i = 0; i < set.size(); i++) {
			if(i > 0)
				data.append(",");
			data.append(set.get(i).toString());
		}
		
		return "{" + data + "}";
	}
	
	/**
	 * Computes the union of the two specified sets that means a set with all distinct elements of set 1 and set 2.
	 * 
	 * @param set1 the first set
	 * @param set2 the second set
	 * @return the union of the two sets
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if set1 is null</li>
	 * 		<li>if set2 is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <E> Set<E> union(final Set<? extends E> set1, final Set<? extends E> set2) throws NullPointerException {
		final Set<E> union = new Set<E>();
		
		// union the two sets
		union.addAll(set1);
		union.addAll(set2);
		
		return union;
	}
	
	/**
	 * Computes the union of the specified sets in the given collection that means a set with all distinct elements
	 * of the sets.
	 * 
	 * @param sets a collection of sets which should be unified
	 * @return the union of the given sets
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if sets contains a null element</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <E> Set<E> union(final Collection<Set<? extends E>> sets) throws NullPointerException {
		final Set<E> union = new Set<E>();
		final Iterator<Set<? extends E>> it = sets.iterator();
		
		while(it.hasNext())
			union.addAll(it.next());
		
		return union;
	}
	
	/**
	 * Computes the union of the specified sets in the given array that means a set with all distinct elements
	 * of the sets.
	 * 
	 * @param sets an array of sets which should be unified
	 * @return the union of the given sets
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if sets is null</li>
	 * 		<li>if sets contains a null element</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <E> Set<E> union(final Set<? extends E>[] sets) throws NullPointerException {
		return union(toCollection(sets));
	}
	
	/**
	 * Computes the intersection of the two specified sets that means a set which contains only elements that
	 * are in both sets, but no other elements.
	 * 
	 * @param set1 the first set
	 * @param set2 the second set
	 * @return the intersection of the given sets
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if set1 is null</li>
	 * 		<li>if set2 is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <E> Set<E> intersection(final Set<? extends E> set1, final Set<? extends E> set2) throws NullPointerException {
		final Set<E> intersection = new Set<E>(set1);
		
		// retain only elements that are contained in both sets
		intersection.retainAll(set2);
		
		return intersection;
	}
	
	/**
	 * Computes the intersection of the specified sets in the given collection that means a set which contains only elements that
	 * are in all the sets, but no other elements.
	 * 
	 * @param sets a collection of sets which should be intersected
	 * @return the intersection of the given sets
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if sets contains a null element</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <E> Set<E> intersection(final Collection<Set<? extends E>> sets) throws NullPointerException {
		final Set<E> intersection = new Set<E>();
		boolean initialized = false;
		
		// retain only the elements that are contained in all sets
		for(Set<? extends E> set : sets) {
			// initialize the intersection set with the first set of the collection and intersect
			// it with each other
			if(!initialized) {
				intersection.addAll(set);
				initialized = true;
			}
			else
				intersection.retainAll(set);
		}
		
		return intersection;
	}
	
	/**
	 * Computes the intersection of the specified sets in the given collection that means a set which contains only elements that
	 * are in all the sets, but no other elements.
	 * 
	 * @param sets an array of sets which should be intersected
	 * @return the intersection of the given sets
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if sets contains a null element</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <E> Set<E> intersection(final Set<? extends E>[] sets) throws NullPointerException {
		return intersection(toCollection(sets));
	}
	
	/**
	 * Computes the complement of the two specified sets that means a set which contains only elements that
	 * are in the first set but not in the second one.
	 * 
	 * @param set1 the first set
	 * @param set2 the second set
	 * @return the complement of the given sets
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if set1 is null</li>
	 * 		<li>if set2 is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <E> Set<E> complement(final Set<? extends E> set1, final Set<? extends E> set2) throws NullPointerException {
		final Set<E> complement = new Set<E>(set1);
		
		// subtract the elements of the second set from the first one
		complement.removeAll(set2);
		
		return complement;
	}
	
	/**
	 * Computes the symmetric difference of the two specified sets which is defined as:<br>
	 * <i>(set1 complement set2) union (set2 complement set1)</i>
	 * 
	 * @param set1 the first set
	 * @param set2 the second set
	 * @return the symmetric difference of the given sets
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if set1 is null</li>
	 * 		<li>if set2 is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <E> Set<E> symDifference(final Set<? extends E> set1, final Set<? extends E> set2) throws NullPointerException {
		return union(complement(set1, set2), complement(set2, set1));
	}
	
	/**
	 * Parses a string representation of a set into a {@link Set} object.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It is used the comma (",") as the delimiter. Use {@link #parse(String, ElementParser, String)} to specify another
	 * delimiter.
	 * 
	 * @see StringElementParser
	 * @see NumberElementParser
	 * @see IntegerElementParser
	 * @see LongElementParser
	 * @see FloatElementParser
	 * @see DoubleElementParser
	 * @param set the set string
	 * @param parser the parser of the elements
	 * @return the set
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if set is null</li>
	 * 		<li>if parser is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <E> Set<E> parse(final String set, final ElementParser<E> parser) throws IllegalArgumentException {
		return parse(set, parser, ",");
	}
	
	/**
	 * Parses a string representation of a set into a {@link Set} object.
	 * 
	 * @see StringElementParser
	 * @see NumberElementParser
	 * @see IntegerElementParser
	 * @see LongElementParser
	 * @see FloatElementParser
	 * @see DoubleElementParser
	 * @param set the set string
	 * @param parser the parser of the elements
	 * @param delimiter the delimiter of the set elements
	 * @return the set
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if set is null</li>
	 * 		<li>if parser is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <E> Set<E> parse(final String set, final ElementParser<E> parser, final String delimiter) throws IllegalArgumentException {
		if(set == null || parser == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final Set<E> result = new Set<E>();
		
		final boolean enclosingBracktes = set.startsWith("{") && set.endsWith("}");
		
		// remove the enclosing brackets of the set if they exist
		final int elementsStart = enclosingBracktes ? 1 : 0;
		final int elementsEnd = enclosingBracktes ? set.length() - 1 : set.length();
		final String elementsString = set.substring(elementsStart, elementsEnd);
		
		// split the set string into the elements
		final String[] elements = elementsString.split(delimiter);
		String e;
		
		// parse all elements
		for(String element : elements) {
			e = element.trim();
			if(e != null && !e.isEmpty())
				result.add(parser.parse(e));
		}
		
		return result;
	}
	
	/**
	 * Converts the specified array of sets to a collection of sets.
	 * 
	 * @param sets the array of sets
	 * @return a collection of the sets
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if sets is null</li>
	 * </ul>
	 * @since 1.0
	 */
	private static <E> Collection<Set<? extends E>> toCollection(final Set<? extends E>[] sets) throws NullPointerException {
		if(sets == null)
			throw new NullPointerException();
		
		final Collection<Set<? extends E>> c = new ArrayList<Set<? extends E>>(sets.length);
		for(Set<? extends E> set : sets)
			c.add(set);
		
		return c;
	}
	
	/**
	 * Converts the specified array to a collection.
	 * 
	 * @param set the array
	 * @return the collection
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if set is null</li>
	 * </ul>
	 * @since 1.0
	 */
	private static <E> Collection<E> toCollection(final E[] set) throws NullPointerException {
		if(set == null)
			throw new NullPointerException();
		
		final Collection<E> c = new ArrayList<E>(set.length);
		for(E e : set)
			c.add(e);
		
		return c;
	}
	
	/**
	 * A default parser for string elements.
	 * 
	 * @see ElementParser
	 * @author jdornseifer
	 * @version 1.0
	 */
	public static class StringElementParser extends ElementParser<String> {

		@Override
		public String parse(String element) {
			return element;
		}
		
	}
	
	/**
	 * A default parser for number elements.
	 * 
	 * @see ElementParser
	 * @see IntegerElementParser
	 * @see LongElementParser
	 * @see FloatElementParser
	 * @see DoubleElementParser
	 * @author jdornseifer
	 * @version 1.0
	 */
	public static class NumberElementParser extends ElementParser<Number> {

		@Override
		public Number parse(String element) {
			try {
				return NumberFormat.getInstance().parse(element);
			} catch (ParseException e) {
				return 0;
			}
		}
		
	}
	
	/**
	 * A default parser for integer elements.
	 * 
	 * @see ElementParser
	 * @author jdornseifer
	 * @version 1.0
	 */
	public static class IntegerElementParser extends ElementParser<Integer> {

		@Override
		public Integer parse(String element) {
			try {
				return NumberFormat.getInstance().parse(element).intValue();
			} catch (ParseException e) {
				return 0;
			}
		}
		
	}
	
	/**
	 * A default parser for long elements.
	 * 
	 * @see ElementParser
	 * @author jdornseifer
	 * @version 1.0
	 */
	public static class LongElementParser extends ElementParser<Long> {

		@Override
		public Long parse(String element) {
			try {
				return NumberFormat.getInstance().parse(element).longValue();
			} catch (ParseException e) {
				return 0L;
			}
		}
		
	}
	
	/**
	 * A default parser for float elements.
	 * 
	 * @see ElementParser
	 * @author jdornseifer
	 * @version 1.0
	 */
	public static class FloatElementParser extends ElementParser<Float> {

		@Override
		public Float parse(String element) {
			try {
				return NumberFormat.getInstance().parse(element).floatValue();
			} catch (ParseException e) {
				return 0.0f;
			}
		}
		
	}
	
	/**
	 * A default parser for double elements.
	 * 
	 * @see ElementParser
	 * @author jdornseifer
	 * @version 1.0
	 */
	public static class DoubleElementParser extends ElementParser<Double> {

		@Override
		public Double parse(String element) {
			try {
				return NumberFormat.getInstance().parse(element).doubleValue();
			} catch (ParseException e) {
				return 0.0;
			}
		}
		
	}

}
