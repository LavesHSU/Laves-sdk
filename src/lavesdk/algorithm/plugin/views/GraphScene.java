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
 * Class:		GraphScene
 * Task:		Record modifications on a graph view
 * Created:		03.12.13
 * LastChanges:	15.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lavesdk.algorithm.plugin.views.GraphView.VisualEdge;
import lavesdk.algorithm.plugin.views.GraphView.VisualVertex;
import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.math.graph.AccessibleID;
import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Vertex;
import lavesdk.serialization.Serializable;
import lavesdk.serialization.Serializer;

/**
 * Represents a scene in a {@link GraphView}.
 * <br><br>
 * A graph scene has a start and an end point which is settled by {@link #begin()} and {@link #end()}/{@link #end(boolean)}. All actions
 * like adding new vertices/edges, removing existing vertices/edges or modify vertices/edges that were made at the graph in this interval
 * can be reversed. That means you can restore the state of the graph view before the scene.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class GraphScene<V extends Vertex, E extends Edge> {
	
	/** the corresponding graph view */
	private final GraphView<V, E> graphView;
	/** the mapping between the object id and the associated vertex scene object */
	private final Map<Integer, VisualVertexSceneObject> sceneVertices;
	/** the insertion order if the vertex scene objects (stores the object identifiers) */
	private final List<Integer> sceneVerticesOrder;
	/** the mapping between the object id and the associated edge scene object */
	private final Map<Integer, VisualEdgeSceneObject> sceneEdges;
	/** the insertion order if the edge scene objects (stores the object identifiers) */
	private final List<Integer> sceneEdgesOrder;
	/** the id of the next scene object state */
	private int nextStateID;
	/** the scene recorder */
	private SceneRecorder sceneRecorder;
	/** flag that indicates that {@link #begin()} was invoked */
	private boolean sceneBegin;
	/** flag that indicates that {@link #end(boolean)} was invoked */
	private boolean sceneEnd;
	/** the data of the scene */
	private SceneObjectState sceneData;
	
	/**
	 * Creates a new graph scene.
	 * 
	 * @param graphView the corresponding graph view
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public GraphScene(final GraphView<V, E> graphView) throws IllegalArgumentException {
		if(graphView == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.graphView = graphView;
		this.sceneVertices = new HashMap<Integer, VisualVertexSceneObject>();
		this.sceneVerticesOrder = new ArrayList<Integer>();
		this.sceneEdges = new HashMap<Integer, VisualEdgeSceneObject>();
		this.sceneEdgesOrder = new ArrayList<Integer>();
		this.nextStateID = 1;
		this.sceneRecorder = null;
		this.sceneBegin = false;
		this.sceneEnd = false;
		this.sceneData = null;
	}
	
	/**
	 * Gets the corresponding graph view.
	 * 
	 * @return the graph view
	 * @since 1.0
	 */
	public final GraphView<V, E> getGraphView() {
		return graphView;
	}
	
	/**
	 * Starts the scene meaning that from now on all actions at the graph view like adding new objects, removing
	 * existing objects or modify objects are recorded and can be reversed after ending the scene.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #end(boolean)
	 * @see #reverse()
	 * @return this graph scene
	 * @throws IllegalStateException
	 * <ul>
	 * 		<li>if the scene is already ended</li>
	 * </ul>
	 * @since 1.0
	 */
	public synchronized final GraphScene<V, E> begin() throws IllegalStateException {
		if(sceneEnd)
			throw new IllegalStateException("scene has already ended");
		
		// store the scene data
		sceneData = new SceneObjectState();
		graphView.serialize(sceneData);
		sceneData.freeze();
		
		// load all scene objects and their initial states
		for(int i = 0; i < graphView.getVisualVertexCount(); i++)
			addVertexSceneObject(graphView.getVisualVertex(i));
		for(int i = 0; i < graphView.getVisualEdgeCount(); i++)
			addEdgeSceneObject(graphView.getVisualEdge(i));
		
		// add a scene recorder to the graph view to listen to scene events
		sceneRecorder = new SceneRecorder();
		graphView.addGraphViewListener(sceneRecorder);
		
		sceneBegin = true;
		
		return this;
	}
	
	/**
	 * Ends the scene meaning that the recording of actions is stopped.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * All objects that are not modified in this scene are released to reduce the memory effort.
	 * If you record one scene on each other you should ensure that you use a scene history (like a stack)
	 * to reverse one scene after another. Otherwise it can be that the scenes are tangled which can have a bad
	 * effect on the restoration of the scenes.<br>
	 * Meaning that if you record <i>scene1</i>, <i>scene2</i>, <i>scene3</i> you must reverse them in reversed order:
	 * <code>scene3.reverse()</code>, <code>scene2.reverse()</code>, <code>scene1.reverse()</code>.
	 * <br><br>
	 * <b>Recommendation</b>:<br>
	 * Use {@link #end()} and a stack data structure to store the scene history if you record multiple scenes
	 * step by step and use <code>end(false)</code> (no release of unmodified objects) if you want to record
	 * only one scene that can restore a specific state of the graph view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return this graph scene
	 * @throws IllegalStateException
	 * <ul>
	 * 		<li>if the scene was not started till now</li>
	 * </ul>
	 * @since 1.0
	 */
	public synchronized final GraphScene<V, E> end() throws IllegalStateException {
		return end(true);
	}
	
	/**
	 * Ends the scene meaning that the recording of actions is stopped.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If you do not release unmodified objects you have a higher memory effort and a slower restoration of
	 * the scene. The advantage is that you must not use a scene history if you want to record one scene after
	 * another.
	 * <br><br>
	 * <b>Recommendation</b>:<br>
	 * Use {@link #end()} and a stack data structure to store the scene history if you record multiple scenes
	 * step by step and use <code>end(false)</code> (no release of unmodified objects) if you want to record
	 * only one scene that can restore a specific state of the graph view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #end()
	 * @param releaseUnmodifiedObjects <code>true</code> if objects that were not modified should be released otherwise <code>false</code>
	 * @return this graph scene
	 * @throws IllegalStateException
	 * <ul>
	 * 		<li>if the scene was not started till now</li>
	 * </ul>
	 * @since 1.0
	 */
	public synchronized final GraphScene<V, E> end(final boolean releaseUnmodifiedObjects) throws IllegalStateException {
		if(!sceneBegin)
			throw new IllegalStateException("scene has not yet begun");
		else if(sceneEnd)
			return this;
		
		// release the objects whose state do not changed if necessary
		if(releaseUnmodifiedObjects) {
			for(int i = sceneVerticesOrder.size() - 1; i >= 0; i--) {
				if(!sceneVertices.get(sceneVerticesOrder.get(i)).isModified()) {
					sceneVertices.remove(sceneVerticesOrder.get(i));
					sceneVerticesOrder.remove(i);
				}
			}
			for(int i = sceneEdgesOrder.size() - 1; i >= 0; i--) {
				if(!sceneEdges.get(sceneEdgesOrder.get(i)).isModified()) {
					sceneEdges.remove(sceneEdgesOrder.get(i));
					sceneEdgesOrder.remove(i);
				}
			}
		}
		
		// remove the scene recorder because no further scene events may be recorded
		graphView.removeGraphViewListener(sceneRecorder);
		
		sceneEnd = true;
		
		return this;
	}
	
	/**
	 * Reverses the scene meaning that all actions at the graph view (in this scene) like adding new objects, removing
	 * existing objects or modify objects are reversed so that the state of the graph view is the one before
	 * this scene.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @throws IllegalStateException
	 * <ul>
	 * 		<li>if the scene is not ended yet</li>
	 * </ul>
	 * @since 1.0
	 */
	public synchronized final void reverse() throws IllegalStateException {
		if(!sceneEnd)
			throw new IllegalStateException("scene has to be ended before it can be reversed");
		
		/*
		 * info:
		 * the scene object lists have to be iterated in reversed order otherwise it might be that
		 * a removed object cannot be restored because their already exists an object that was added in the scene
		 */
		
		// firstly: reverse the vertices of the scene
		for(int i = sceneVerticesOrder.size() - 1; i >= 0; i--)
			reverseVertexSceneObject(sceneVertices.get(sceneVerticesOrder.get(i)));
		// secondly: reverse the edges of the scene (because edges based on vertices this step
		// must be done after reversing the vertices)
		for(int i = sceneEdgesOrder.size() - 1; i >= 0; i--)
			reverseEdgeSceneObject(sceneEdges.get(sceneEdgesOrder.get(i)));
		
		// reverse the scene data
		if(sceneData != null) {
			sceneData.unfreeze();
			graphView.deserialize(sceneData);
			// use this hack to prevent graph view from painting the vertices at the wrong position because
			// the reversed vertex scene objects have their old positions but deserializing the scene data sets
			// the old zoom value which indicates an adjustment of the vertex positions; overwriting the zoom reverses
			// this adjustment and the vertices are painted at their real positions
			graphView.setZoom(graphView.getZoom());
		}
		
		// scene is reversed so release all resources
		sceneVertices.clear();
		sceneVerticesOrder.clear();
		sceneEdges.clear();
		sceneEdgesOrder.clear();
		
		// repaint the graph view
		graphView.repaint();
	}
	
	/**
	 * Indicates whether the scene is recorded meaning that {@link #end(boolean)} was invoked.
	 * 
	 * @return <code>true</code> if the scene recording is finished otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean isRecorded() {
		return sceneEnd;
	}
	
	/**
	 * Adds a vertex as a scene object to the scene.
	 * 
	 * @param vv the visual vertex
	 * @return the scene object of the vertex
	 * @since 1.0
	 */
	private SceneObject addVertexSceneObject(final GraphView<V, E>.VisualVertex vv) {
		final VisualVertexSceneObject vertex = new VisualVertexSceneObject(vv);
		sceneVertices.put(vertex.getObjectID(), vertex);
		sceneVerticesOrder.add(vertex.getObjectID());
		vertex.requestInitialState();
		
		return vertex;
	}
	
	/**
	 * Adds an edge as a scene object to the scene.
	 * 
	 * @param ve the visual edge
	 * @return the scene object of the edge
	 * @since 1.0
	 */
	private SceneObject addEdgeSceneObject(final GraphView<V, E>.VisualEdge ve) {
		final VisualEdgeSceneObject edge = new VisualEdgeSceneObject(ve);
		sceneEdges.put(edge.getObjectID(), edge);
		sceneEdgesOrder.add(edge.getObjectID());
		edge.requestInitialState();
		
		return edge;
	}
	
	/**
	 * Reverses a vertex scene object meaning that the previous state of the vertex is restored.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param vertex the vertex scene object
	 * @since 1.0
	 */
	private void reverseVertexSceneObject(final VisualVertexSceneObject vertex) {
		// reverse the object meaning if the scene object was added to the scene then
		// remove it, if it was removed from the scene then restore it and otherwise
		// restore its state
		switch(vertex.getAction()) {
			case ADDED:
				if(EDT.isExecutedInEDT())
					graphView.removeVisualVertex(vertex.getVisualVertex());
				else
					EDT.execute(new GuiJob() {
						@Override
						protected void execute() throws Throwable {
							graphView.removeVisualVertex(vertex.getVisualVertex());
						}
					});
				break;
			case REMOVED:
				GraphView<V, E>.VisualVertex newVV;
				
				if(EDT.isExecutedInEDT())
					newVV = graphView.createVisualVertex(0, 0);
				else
					newVV = EDT.execute(new GuiRequest<GraphView<V, E>.VisualVertex>() {
						@Override
						protected GraphView<V, E>.VisualVertex execute() throws Throwable {
							return graphView.createVisualVertex(0, 0);
						}
					});
				
				// restore the old identifier of the vertex
				if(newVV != null) {
					final AccessibleID aid = new AccessibleID(newVV.getVertex());
					aid.modify(vertex.getObjectID());
				}
				
				vertex.restoreInitialState(newVV);
				break;
			case NONE:
			default:
				final GraphView<V, E>.VisualVertex vv = graphView.getVisualVertexByID(vertex.getObjectID());
				vertex.restoreInitialState(vv);
		}
	}
	
	/**
	 * Reverses an edge scene object meaning that the previous state of the edge is restored.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param edge the edge scene object
	 * @since 1.0
	 */
	private void reverseEdgeSceneObject(final VisualEdgeSceneObject edge) {
		// reverse the object meaning if the scene object was added to the scene then
		// remove it, if it was removed from the scene then restore it and otherwise
		// restore its state
		switch(edge.getAction()) {
			case ADDED:
				if(EDT.isExecutedInEDT())
					graphView.removeVisualEdge(edge.getVisualEdge());
				else
					EDT.execute(new GuiJob() {
						@Override
						protected void execute() throws Throwable {
							graphView.removeVisualEdge(edge.getVisualEdge());
						}
					});
				break;
			case REMOVED:
				final VisualVertexSceneObject predecessor = sceneVertices.get(edge.getPredecessorID());
				final VisualVertexSceneObject successor = sceneVertices.get(edge.getSuccessorID());
				
				if(predecessor != null && successor != null) {
					GraphView<V, E>.VisualEdge newVE;
					
					if(EDT.isExecutedInEDT())
						newVE = graphView.createVisualEdge(predecessor.getVisualVertex(), successor.getVisualVertex(), edge.isDirected());
					else
						newVE = EDT.execute(new GuiRequest<GraphView<V, E>.VisualEdge>() {
							@Override
							protected GraphView<V, E>.VisualEdge execute() throws Throwable {
								return graphView.createVisualEdge(predecessor.getVisualVertex(), successor.getVisualVertex(), edge.isDirected());
							}
						});
					
					// restore the old identifier of the edge
					if(newVE != null) {
						final AccessibleID aid = new AccessibleID(newVE.getEdge());
						aid.modify(edge.getObjectID());
					}
					
					edge.restoreInitialState(newVE);
				}
				break;
			case NONE:
			default:
				final GraphView<V, E>.VisualEdge ve = graphView.getVisualEdgeByID(edge.getObjectID());
				edge.restoreInitialState(ve);
		}
	}
	
	/**
	 * Represents an abstract scene object.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private abstract class SceneObject {
		
		/** the scene object */
		protected final Serializable object;
		/** the object's id */
		protected final int objectID;
		/** the action that is applied to this object */
		private SceneObjectAction action;
		/** the initial state of the object */
		private SceneObjectState initialState;
		
		/**
		 * Creates a new scene object.
		 * 
		 * @param object the serializable scene object
		 * @param objectID the object's id
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if object is null</li>
		 * </ul>
		 */
		public SceneObject(final Serializable object, final int objectID) throws IllegalArgumentException {
			if(object == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.object = object;
			this.objectID = objectID;
			this.action = SceneObjectAction.NONE;
			this.initialState = null;
		}
		
		/**
		 * Gets the object's id.
		 * 
		 * @return the object's id
		 * @since 1.0
		 */
		public final int getObjectID() {
			return objectID;
		}
		
		/**
		 * Requests the initial state if the object.
		 * 
		 * @throws IllegalStateException
		 * <ul>
		 * 		<li>if the initial state is already requested</li>
		 * </ul>
		 * @since 1.0
		 */
		public void requestInitialState() throws IllegalStateException {
			if(initialState != null)
				throw new IllegalStateException("initial state is already requested");
			
			initialState = new SceneObjectState();
			object.serialize(initialState);
			
			// freeze the initial data to make the data unmodifiable
			initialState.freeze();
		}
		
		/**
		 * Restores the initial state of this scene object to a component object.
		 * 
		 * @param object the component object
		 * @since 1.0
		 */
		public void restoreInitialState(final Serializable object) {
			if(initialState == null || object == null)
				return;
			
			initialState.unfreeze();
			object.deserialize(initialState);
			initialState = null;
		}
		
		/**
		 * Indicates whether the scene object is modified or not.
		 * <br><br>
		 * A scene object is modified if a {@link SceneObjectAction} unlike {@link SceneObjectAction#NONE} is applied
		 * to the scene object or the current state differs from the initial state.
		 * 
		 * @return <code>true</code> if the scene object is modified otherwise <code>false</code>
		 * @since 1.0
		 */
		public boolean isModified() {
			if(initialState == null)
				return false;
			else {
				// there is an action applied on the scene object? then it is definitely modified
				// otherwise check if the current state differs from the initial state
				if(action != SceneObjectAction.NONE)
					return true;
				else {
					final SceneObjectState modifiedState = new SceneObjectState();
					object.serialize(modifiedState);
					return !initialState.equals(modifiedState);
				}
			}
		}
		
		/**
		 * Applies an action to the scene object.
		 * 
		 * @param action the action
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if action is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public void applyAction(final SceneObjectAction action) throws IllegalArgumentException {
			this.action = this.action.apply(action);
		}
		
		/**
		 * Gets the applied action of the scene object.
		 * 
		 * @return the applied action
		 * @since 1.0
		 */
		public SceneObjectAction getAction() {
			return action;
		}
		
	}
	
	/**
	 * Represents a scene object for a {@link VisualVertex}.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class VisualVertexSceneObject extends SceneObject {
		
		/**
		 * Creates a new visual vertex scene object.
		 * 
		 * @param vv the visual vertex its scene object should be created
		 * @since 1.0
		 */
		public VisualVertexSceneObject(final GraphView<V, E>.VisualVertex vv) {
			super(vv, vv.getVertex().getID());
		}
		
		/**
		 * Gets the visual component of the scene object in the graph using the object id.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * Because the inversion of a graph scene creates new visual vertex instances but with the corresponding identifiers
		 * it is necessary to request the visual vertex directly from the graph view using the object id.
		 * 
		 * @return the visual vertex
		 * @since 1.0
		 */
		public GraphView<V, E>.VisualVertex getVisualVertex() {
			return GraphScene.this.graphView.getVisualVertexByID(objectID);
		}
		
	}
	
	/**
	 * Represents a scene object for a {@link VisualEdge}.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class VisualEdgeSceneObject extends SceneObject {
		
		/** flag that indicates whether the edge of the scene object is directed */
		private final boolean directed;
		/** the id of the predecessor vertex */
		private final int predecessorID;
		/** the id of the successor vertex */
		private final int successorID;
		
		/**
		 * Creates a new visual edge scene object.
		 * 
		 * @param ve the visual edge its scene object should be created
		 * @since 1.0
		 */
		public VisualEdgeSceneObject(final GraphView<V, E>.VisualEdge ve) {
			super(ve, ve.getEdge().getID());
			
			directed = ve.getEdge().isDirected();
			predecessorID = ve.getPredecessor().getVertex().getID();
			successorID = ve.getSuccessor().getVertex().getID();
		}
		
		/**
		 * Gets the visual component of the scene object in the graph using the object id.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * Because the inversion of a graph scene creates new visual edge instances but with the corresponding identifiers
		 * it is necessary to request the visual edge directly from the graph view using the object id.
		 * 
		 * @return the visual vertex
		 * @since 1.0
		 */
		public GraphView<V, E>.VisualEdge getVisualEdge() {
			return GraphScene.this.graphView.getVisualEdgeByID(objectID);
		}
		
		/**
		 * Indicates whether the corresponding visual edge is directed or not.
		 * 
		 * @return <code>true</code> if the edge is directed or <code>false</code> if it is undirected
		 * @since 1.0
		 */
		public boolean isDirected() {
			return directed;
		}
		
		/**
		 * Gets the identifier of the predecessor vertex.
		 * 
		 * @return the id of the predecessor
		 * @since 1.0
		 */
		public int getPredecessorID() {
			return predecessorID;
		}
		
		/**
		 * Gets the identifier of the successor vertex.
		 * 
		 * @return the id of the successor
		 * @since 1.0
		 */
		public int getSuccessorID() {
			return successorID;
		}
		
	}
	
	/**
	 * Represents an action of a scene object.
	 * 
	 * @author jdornseifer
	 * @since 1.0
	 * @since 1.0
	 */
	private enum SceneObjectAction {
		
		/** no action */
		NONE,
		
		/** scene object is added */
		ADDED,
		
		/** scene object is removed */
		REMOVED;
		
		/**
		 * Applies the specified action to this one and generates a new action as the result of combining the
		 * two actions.
		 * 
		 * @param soa the action that should be applied to this one
		 * @return the combined action
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if soa is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public SceneObjectAction apply(final SceneObjectAction soa) throws IllegalArgumentException {
			if(soa == null)
				throw new IllegalArgumentException("No valid argument!");
			
			switch(this) {
				case NONE:		return soa;									// NONE can be combined with any action
				case ADDED:		return (soa == ADDED) ? ADDED : NONE;		// ADDED can not be combined with anything meaning the combination results in NONE
				case REMOVED:	return (soa == REMOVED) ? REMOVED : NONE;	// REMOVED can not be combined with anything meaning the combination results in NONE
				default:		return NONE;
			}
			
		}
		
	}
	
	/**
	 * Represents the state of a {@link SceneObject}.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class SceneObjectState extends Serializer {
		
		private static final long serialVersionUID = 1L;
		
		/** the freezed data of the state */
		private byte[] state;
		
		/**
		 * Creates a new scene object state.
		 * 
		 * @param objectName the objects name
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if objectName is null</li>
		 * </ul>
		 */
		public SceneObjectState() throws IllegalArgumentException {
			super(GraphScene.this.nextStateID++, "SceneObject");
		}
		
		/**
		 * Freezes the state's data.
		 * 
		 * @since 1.0
		 */
		public void freeze() {
			state = freezeData();
		}
		
		/**
		 * Unfreezes the state's data.
		 * 
		 * @since 1.0
		 */
		public void unfreeze() {
			if(state != null)
				unfreezeData(state);
		}
		
	}
	
	/**
	 * The scene recorder which records <i>add</i> and <i>remove</i> events.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class SceneRecorder implements GraphViewListener<V, E> {

		@Override
		public void vertexAdded(GraphView<V, E>.VisualVertex vertex) {
			GraphScene.this.addVertexSceneObject(vertex).applyAction(SceneObjectAction.ADDED);
		}

		@Override
		public void vertexRemoved(GraphView<V, E>.VisualVertex vertex) {
			final SceneObject so = GraphScene.this.sceneVertices.get(vertex.getVertex().getID());
			if(so != null)
				so.applyAction(SceneObjectAction.REMOVED);
		}

		@Override
		public void vertexSelected(GraphView<V, E>.VisualVertex vertex) {
		}

		@Override
		public void edgeAdded(GraphView<V, E>.VisualEdge edge) {
			GraphScene.this.addEdgeSceneObject(edge).applyAction(SceneObjectAction.ADDED);
		}

		@Override
		public void edgeRemoved(GraphView<V, E>.VisualEdge edge) {
			final SceneObject so = GraphScene.this.sceneEdges.get(edge.getEdge().getID());
			if(so != null)
				so.applyAction(SceneObjectAction.REMOVED);
		}

		@Override
		public void edgeSelected(GraphView<V, E>.VisualEdge edge) {
		}
		
	}

}
