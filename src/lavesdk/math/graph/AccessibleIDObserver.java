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

package lavesdk.math.graph;

/**
 * Observer to observe modifications of graph object identifiers.
 * 
 * @see AccessibleID
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public abstract class AccessibleIDObserver {
	
	/** the related graph */
	private final Graph<?, ?> graph;
	
	/**
	 * Creates a new identifier observer.
	 * 
	 * @param graph the related graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public AccessibleIDObserver(final Graph<?, ?> graph) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.graph = graph;
		
		// add the observer as a listener to the graph
		graph.addIDObserver(this);
	}
	
	/**
	 * Removes the observer from the related graph.
	 * 
	 * @since 1.0
	 */
	public final void remove() {
		graph.removeIDObserver(this);
	}
	
	/**
	 * Indicates that the vertex with the identifier <code>oldID</code> has changed its id to <code>newID</code>.
	 * 
	 * @param oldID the old id of the vertex
	 * @param newID the new identifier
	 * @since 1.0
	 */
	protected abstract void vertexIDModified(final int oldID, final int newID);
	
	/**
	 * Indicates that the edge with the identifier <code>oldID</code> has changed its id to <code>newID</code>.
	 * 
	 * @param oldID the old id of the edge
	 * @param newID the new identifier
	 * @since 1.0
	 */
	protected abstract void edgeIDModified(final int oldID, final int newID);

}
