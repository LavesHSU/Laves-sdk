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
 * Class:		GraphUtils
 * Task:		Utility functions in dealing with graphs
 * Created:		31.10.13
 * LastChanges:	04.04.16
 * LastAuthor:	jdornseifer
 */

package lavesdk.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import lavesdk.math.Matrix;
import lavesdk.math.NumericMatrix;
import lavesdk.math.Set;
import lavesdk.math.graph.DefaultGraphFactory;
import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Graph;
import lavesdk.math.graph.GraphFactory;
import lavesdk.math.graph.Path;
import lavesdk.math.graph.SimpleGraph;
import lavesdk.math.graph.Trail;
import lavesdk.math.graph.Vertex;
import lavesdk.math.graph.Walk;
import lavesdk.math.graph.enums.Type;
import lavesdk.math.graph.matching.Matching;

/**
 * Utility functions in dealing with graphs.
 * 
 * @author jdornseifer
 * @version 1.4
 * @since 1.0
 */
public class GraphUtils {
	
	/** random value generator */
	private static final Random rand = new Random();
	
	private GraphUtils() {
	}
	
	/**
	 * Indicates whether the given graph is a simple graph that means the graph has no loops and two different
	 * vertices are only connected by one edge.
	 * 
	 * @param graph the graph
	 * @return <code>true</code> if the graph is simple otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> boolean isSimpleGraph(final Graph<V, E> graph) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		// if there are no vertices then the graph is not simple
		if(graph.getOrder() < 1)
			return false;
		
		// if the graph contains loops then it is no simple graph
		for(int i = 0; i < graph.getSize(); i++)
			if(graph.getEdge(i).getPredecessor() == graph.getEdge(i).getSuccessor())
				return false;
		
		// furthermore the graph may not be a multi graph
		return !isMultiGraph(graph);
	}
	
	/**
	 * Indicates whether the given graph is a multi graph that means the graph has vertices
	 * with more than one edge between them.
	 * 
	 * @param graph the graph
	 * @return <code>true</code> if it is a multi graph otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> boolean isMultiGraph(final Graph<V, E> graph) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		V v;
		Vertex u;
		final List<Vertex> adjacentVertices = new ArrayList<Vertex>();
		
		// find a pair of vertices (v,u) which is adjacent by more than one edge
		for(int i = 0; i < graph.getOrder(); i++) {
			v = graph.getVertex(i);
			adjacentVertices.clear();
			
			for(int j = 0; j < v.getOutgoingEdgeCount(); j++) {
				u = v.getOutgoingEdge(j).getSuccessor(v);
				
				// are they already adjacent? then we have at least two edges
				// between v and u which means it is a multi graph!
				if(adjacentVertices.contains(u))
					return true;
				
				adjacentVertices.add(u);
			}
		}
		
		return false;
	}
	
	/**
	 * Indicates whether the given graph is bipartite that means if the graph is simple and the set of vertices can be divided
	 * into two disjoint subsets.
	 * 
	 * @see #getBipartiteVertexSets(Graph)
	 * @param graph the graph
	 * @return <code>true</code> if the graph is bipartite otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> boolean isBipartite(final Graph<V, E> graph) throws IllegalArgumentException {
		return isSimpleGraph(graph) && is2Colorable(graph); 
	}
	
	/**
	 * Gets the vertex subsets <code>V1</code> and <code>V2</code> so that <code>V1 union V2 = V</code> and <code>V1 intersection V2 = empty set</code>.
	 * This is only possible if the graph is bipartite.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * To check whether the given graph is bipartite it is solved a 2 coloring problem that means the complexity is in O(|V| + |E|).
	 * 
	 * @see #isBipartite(Graph)
	 * @param graph the graph
	 * @return the two partitions/vertex sets <code>V1</code> and <code>V2</code> in one list or <code>null</code> if graph is not bipartite
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> List<List<V>> getBipartiteVertexSets(final Graph<V, E> graph) throws IllegalArgumentException {
		return getBipartiteVertexSets(graph, true);
	}
	
	/**
	 * Gets the vertex subsets <code>V1</code> and <code>V2</code> so that <code>V1 union V2 = V</code> and <code>V1 intersection V2 = empty set</code>.
	 * This is only possible if the graph is bipartite.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * To check whether the given graph is bipartite it is solved a 2 coloring problem that means the complexity is in O(|V| + |E|).
	 * 
	 * @see #isBipartite(Graph)
	 * @param graph the graph
	 * @param nonIncidentVerticesToSubset1 <code>true</code> if non-incident vertices should be added to subset 1 or <code>false</code> to add non-incident vertices to subset 2
	 * @return the two partitions/vertex sets <code>V1</code> and <code>V2</code> in one list or <code>null</code> if graph is not bipartite
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> List<List<V>> getBipartiteVertexSets(final Graph<V, E> graph, final boolean nonIncidentVerticesToSubset1) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final Map<Vertex, Integer> colors = new HashMap<Vertex, Integer>();
		
		if(!is2Colorable(graph, colors, nonIncidentVerticesToSubset1))
			return null;
		
		// create two lists for the disjoint subsets
		final List<List<V>> subsets = new ArrayList<List<V>>(2);
		subsets.add(new ArrayList<V>());
		subsets.add(new ArrayList<V>());
		
		int color;
		
		// assign the vertices to subset 1 if they have color 1 and to subset 2 if they have color 2
		for(int i = 0; i < graph.getOrder(); i++) {
			color = colors.get(graph.getVertex(i));
			subsets.get(color - 1).add(graph.getVertex(i));
		}
		
		return subsets;
	}
	
	/**
	 * Indicates whether the given graph is 2-colorable.
	 * 
	 * @param graph the graph
	 * @return <code>true</code> if the graph is 2-colorable otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> boolean is2Colorable(final Graph<V, E> graph) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		// no vertices? then the graph cannot be 2-colorable
		if(graph.getOrder() == 0)
			return false;
		else
			return is2Colorable(graph, new HashMap<Vertex, Integer>(), true);
	}
	
	/**
	 * Indicates whether the given graph is complete that means if the graph is simple and each vertex of the graph is connected with
	 * each other.
	 * 
	 * @param graph the graph
	 * @return <code>true</code> if the graph is complete otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> boolean isComplete(final Graph<V, E> graph) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		// graph is not simple? then he cannot be complete by definition
		if(!isSimpleGraph(graph))
			return false;
		
		final int degOfVertex = (graph.getType() == Type.DIRECTED) ? (graph.getOrder() - 1) * 2 :  graph.getOrder() - 1;
		
		// each vertex must have a degree of order - 1
		for(int i = 0; i < graph.getOrder(); i++)
			if(graph.getVertex(i).getDegree() != degOfVertex)
				return false;
		
		return true;
	}
	
	/**
	 * Indicates whether the given graph is complete bipartite that means the graph is simple and each vertex of subset one
	 * is connected with each vertex of subset two and vice versa.
	 * 
	 * @param graph the graph
	 * @return <code>true</code> if the graph is complete bipartite otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> boolean isCompleteBipartite(final Graph<V, E> graph) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final List<List<V>> subsets = getBipartiteVertexSets(graph);
		
		// graph is not bipartite? then he isn't complete bipartite too!
		if(subsets == null)
			return false;
		if(!isSimpleGraph(graph))
			return false;
		
		// each vertex of subset one must have a degree of the amount of vertices in subset two
		// and each vertex of subset two must have a degree of the amount of vertices in subset one
		final int subsetOneVertexDeg = subsets.get(1).size();
		final int subsetTwoVertexDeg = subsets.get(0).size();
		
		for(int i = 0; i < subsets.get(0).size(); i++)
			if(subsets.get(0).get(i).getDegree() != subsetOneVertexDeg)
				return false;
		for(int i = 0; i < subsets.get(1).size(); i++)
			if(subsets.get(1).get(i).getDegree() != subsetTwoVertexDeg)
				return false;
		
		return true;
	}
	
	/**
	 * Gets all connected components from the given graph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method uses the <i>tarjan's strongly connected components algorithm</i> to compute the components!
	 * 
	 * @param graph the graph
	 * @return a list of the connected components (each connected component is a set of vertices)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> List<List<V>> getConnectedComponents(final Graph<V, E> graph) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final List<List<V>> components = new ArrayList<List<V>>();
		final MutableNumber<Integer> dfsIndex = new MutableNumber<Integer>(0);
		final Stack<Integer> stack = new Stack<Integer>();
		final int[] indices = new int[graph.getOrder()];
		final int[] lowlinks = new int[indices.length];
		
		/*
		 * INFO:
		 * look at http://en.wikipedia.org/wiki/Tarjan's_strongly_connected_components_algorithm for more information
		 */
		
		// mark indices as undefined
		for(int i = 0; i < indices.length; i++)
			indices[i] = -1;
		
		// for each vertex compute connectivity if its index is undefined
		for(int i = 0; i < graph.getOrder(); i++)
			if(indices[i] == -1)
				computeConnectedComponents(graph, components, graph.getVertex(i), stack, dfsIndex, indices, lowlinks);
		
		return components;
	}
	
	/**
	 * Indicates whether the given graph is (strongly) connected.
	 * <br><br>
	 * In case of an <b>undirected graph</b> this method indicates whether the specified <b>graph is connected</b> and in the case
	 * of a <b>directed graph</b> this method indicates whether the specified graph is <b>strongly connected</b>.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * To check whether a directed graph is only connected (not strongly connected) use {@link #invertGraph(Graph, GraphFactory)} to transfer
	 * the directed graph in an undirected one and then check whether the undirected equivalent is connected.
	 * 
	 * @param graph the graph
	 * @return <code>true</code> if the given graph is (strongly) connected otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> boolean isConnected(final Graph<V, E> graph) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		return (getConnectedComponents(graph).size() == 1);
	}
	
	/**
	 * Creates a complete graph K<sub>n</sub> of n vertices.
	 * 
	 * @param n the number of vertices
	 * @param directed <code>true</code> if the graph should be directed otherwise <code>false</code>
	 * @return a complete graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if n is <code>< 1</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public static SimpleGraph<Vertex, Edge> createCompleteGraph(final int n, final boolean directed) throws IllegalArgumentException {
		return createCompleteGraph(n, directed, new DefaultGraphFactory());
	}
	
	/**
	 * Creates a complete graph K<sub>n</sub> of n vertices.
	 * 
	 * @param n the number of vertices
	 * @param directed <code>true</code> if the graph should be directed otherwise <code>false</code>
	 * @param factory the factory to create vertices and edges for the graph
	 * @return a complete graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if n is <code>< 1</code></li>
	 * 		<li>if factory is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> SimpleGraph<V, E> createCompleteGraph(final int n, final boolean directed, final GraphFactory<V, E> factory) throws IllegalArgumentException {
		return createCompleteGraph(n, directed, factory, 0.0f);
	}
	
	/**
	 * Creates a complete graph K<sub>n</sub> of n vertices.
	 * 
	 * @param n the number of vertices
	 * @param directed <code>true</code> if the graph should be directed otherwise <code>false</code>
	 * @param factory the factory to create vertices and edges for the graph
	 * @param maxEdgeWeight the max. weight of edges (the weights are generated randomly up to that value)
	 * @return a complete graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if n is <code>< 1</code></li>
	 * 		<li>if factory is null</li>
	 * </ul>
	 * @since 1.3
	 */
	public static <V extends Vertex, E extends Edge> SimpleGraph<V, E> createCompleteGraph(final int n, final boolean directed, final GraphFactory<V, E> factory, float maxEdgeWeight) throws IllegalArgumentException {
		if(n < 1 || factory == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final SimpleGraph<V, E> graph = new SimpleGraph<V, E>(directed);
		
		// create all vertices
		for(int i = 1; i <= n; i++)
			graph.add(factory.createVertex("" + i));
		
		V v;
		V u;
		E e;
		
		// connect each vertex with each other
		for(int i = 0; i < graph.getOrder(); i++) {
			v = graph.getVertex(i);
			
			for(int j = 0; j < graph.getOrder(); j++) {
				u = graph.getVertex(j);
				// same vertices? then continue with the next because loops are not allowed
				if(u == v)
					continue;
				
				e = factory.createEdge(v, u);
				if(maxEdgeWeight > 0.0f)
					e.setWeight(1 + rand.nextInt((int)maxEdgeWeight));
				graph.add(e);
			}
		}
		
		return graph;
	}
	
	/**
	 * Creates a complete bipartite graph K<sub>n,m</sub> of n vertices in the first and m vertices in the second subset.
	 * 
	 * @param n the number of vertices in the first subset
	 * @param m the number of vertices in the first subset
	 * @return a complete graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if n is <code>< 1</code></li>
	 * 		<li>if m is <code>< 1</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public static SimpleGraph<Vertex, Edge> createCompleteBipartiteGraph(final int n, final int m) throws IllegalArgumentException {
		return createCompleteBipartiteGraph(n, m, new DefaultGraphFactory());
	}
	
	/**
	 * Creates a complete bipartite graph K<sub>n,m</sub> of n vertices in the first and m vertices in the second subset.
	 * 
	 * @param n the number of vertices in the first subset
	 * @param m the number of vertices in the first subset
	 * @param factory the factory to create vertices and edges for the graph
	 * @return a complete graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if n is <code>< 1</code></li>
	 * 		<li>if m is <code>< 1</code></li>
	 * 		<li>if factory is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> SimpleGraph<V, E> createCompleteBipartiteGraph(final int n, final int m, final GraphFactory<V, E> factory) throws IllegalArgumentException {
		return createCompleteBipartiteGraph(n, m, factory, 0.0f);
	}
	
	/**
	 * Creates a complete bipartite graph K<sub>n,m</sub> of n vertices in the first and m vertices in the second subset.
	 * 
	 * @param n the number of vertices in the first subset
	 * @param m the number of vertices in the first subset
	 * @param factory the factory to create vertices and edges for the graph
	 * @param maxEdgeWeight the max. weight of edges (the weights are generated randomly up to that value)
	 * @return a complete graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if n is <code>< 1</code></li>
	 * 		<li>if m is <code>< 1</code></li>
	 * 		<li>if factory is null</li>
	 * </ul>
	 * @since 1.3
	 */
	public static <V extends Vertex, E extends Edge> SimpleGraph<V, E> createCompleteBipartiteGraph(final int n, final int m, final GraphFactory<V, E> factory, float maxEdgeWeight) throws IllegalArgumentException {
		if(n < 1 || m < 1 || factory == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final SimpleGraph<V, E> graph = new SimpleGraph<V, E>(false);
		
		// create all vertices for both subsets
		for(int i = 1; i <= n + m; i++)
			graph.add(factory.createVertex("" + i));
		
		E e;
		
		// connect each vertex of subset one (n vertices) with each other of subset two (m vertices)
		for(int i = 0; i < n; i++) {
			for(int j = n; j < m + n; j++) {
				e = factory.createEdge(graph.getVertex(i), graph.getVertex(j));
				if(maxEdgeWeight > 0.0f)
					e.setWeight(1 + rand.nextInt((int)maxEdgeWeight));
				graph.add(e);
			}
		}
		
		return graph;
	}
	
	/**
	 * Creates a graph based on an adjacency matrix where a zero-weight edge indicates that there is no edge between two vertices.
	 * 
	 * @param adjacencyMatrix the adjacency matrix
	 * @param factory the graph factory
	 * @param directed <code>true</code> if a directed graph should be created otherwise <code>false</code> for an undirected one
	 * @return the graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if adjacencyMatrix is null</li>
	 * 		<li>if adjacencyMatrix is not square</li>
	 * 		<li>if factory is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> SimpleGraph<V, E> createGraph(final Matrix<? extends Number> adjacencyMatrix, final GraphFactory<V, E> factory, final boolean directed) {
		return createGraph(adjacencyMatrix, factory, directed, false);
	}
	
	/**
	 * Creates a graph based on an adjacency matrix.
	 * 
	 * @param adjacencyMatrix the adjacency matrix
	 * @param factory the graph factory
	 * @param directed <code>true</code> if a directed graph should be created otherwise <code>false</code> for an undirected one
	 * @param zeroWeightsAllowed <code>true</code> if zero-weight edges are allowed (in that case use {@link Float#POSITIVE_INFINITY} to define that there is no edge between two vertices) otherwise <code>false</code> meaning a zero-weight edge indicates that there is no edge between the two vertices
	 * @return the graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if adjacencyMatrix is null</li>
	 * 		<li>if adjacencyMatrix is not square</li>
	 * 		<li>if factory is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> SimpleGraph<V, E> createGraph(final Matrix<? extends Number> adjacencyMatrix, final GraphFactory<V, E> factory, final boolean directed, final boolean zeroWeightsAllowed) {
		// the adjacency matrix has to be square!
		if(adjacencyMatrix == null || !adjacencyMatrix.isSquare() || factory == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final SimpleGraph<V, E> graph = new SimpleGraph<V, E>(directed);
		Number weight;
		
		// create all vertices of the matrix
		for(int v = 0; v < adjacencyMatrix.getRowCount(); v++)
			graph.add(factory.createVertex("" + (v + 1)));
		
		// create the edges
		for(int i = 0; i < adjacencyMatrix.getRowCount(); i++) {
			for(int j = 0; j < adjacencyMatrix.getColumnCount(); j++) {
				weight = adjacencyMatrix.get(i, j);
				// if there is no valid weight or it is a weight that describes that there is no edge then continue with the next element
				if(weight == null || (!zeroWeightsAllowed && weight.floatValue() == 0.0f) || (zeroWeightsAllowed && weight.floatValue() == Float.POSITIVE_INFINITY))
					continue;
				
				// create the edge between the two vertices
				graph.add(factory.createEdge(graph.getVertex(i), graph.getVertex(j), weight.floatValue()));
			}
		}
		
		return graph;
	}
	
	/**
	 * Creates a random graph.
	 * 
	 * @param n number of vertices
	 * @param directed <code>true</code> if a directed graph should be created otherwise <code>false</code> for an undirected one
	 * @return the graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if n is <code>< 1</code></li>
	 * </ul>
	 * @since 1.4
	 */
	public static SimpleGraph<Vertex, Edge> createRandomGraph(final int n, final boolean directed) {
		return createRandomGraph(n, directed, new DefaultGraphFactory());
	}
	
	/**
	 * Creates a random graph.
	 * 
	 * @param n number of vertices
	 * @param directed <code>true</code> if a directed graph should be created otherwise <code>false</code> for an undirected one
	 * @param factory the graph factory
	 * @return the graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if n is <code>< 1</code></li>
	 * 		<li>if factory is null</li>
	 * </ul>
	 * @since 1.4
	 */
	public static <V extends Vertex, E extends Edge> SimpleGraph<V, E> createRandomGraph(final int n, final boolean directed, final GraphFactory<V, E> factory) {
		return createRandomGraph(n, directed, factory, 0.0f, 0.0f);
	}
	
	/**
	 * Creates a random graph.
	 * 
	 * @param n number of vertices
	 * @param directed <code>true</code> if a directed graph should be created otherwise <code>false</code> for an undirected one
	 * @param factory the graph factory
	 * @param minEdgeWeight the min. weight of edges
	 * @param maxEdgeWeight the max. weight of edges
	 * @return the graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if n is <code>< 1</code></li>
	 * 		<li>if factory is null</li>
	 * </ul>
	 * @since 1.4
	 */
	public static <V extends Vertex, E extends Edge> SimpleGraph<V, E> createRandomGraph(final int n, final boolean directed, final GraphFactory<V, E> factory, float minEdgeWeight, float maxEdgeWeight) {
		if(n < 1 || factory == null)
			throw new IllegalArgumentException("No valid argument!");

		final SimpleGraph<V, E> graph = new SimpleGraph<V, E>(directed);
		
		if(minEdgeWeight < 0.0f)
			minEdgeWeight = 0.0f;
		if(maxEdgeWeight < minEdgeWeight)
			maxEdgeWeight = minEdgeWeight;

		// create all vertices
		for(int i = 1; i <= n; i++)
			graph.add(factory.createVertex("" + i));
		
		V v;
		V u;
		E e;
		int tieBreaker;
		final int tieBreakerBase = directed ? 35 : 50;
		
		// connect each vertex with each other
		for(int i = 0; i < graph.getOrder(); i++) {
			v = graph.getVertex(i);
			
			for(int j = 0; j < graph.getOrder(); j++) {
				u = graph.getVertex(j);
				// same vertices? then continue with the next because loops are not allowed
				if(u == v)
					continue;
				
				// randomly decide if we want to add an edge or not by using a number between 0 and 100
				tieBreaker = rand.nextInt(100);
				if(tieBreaker < tieBreakerBase)
					continue;
				
				e = factory.createEdge(v, u);
				if(maxEdgeWeight > 0.0f)
					e.setWeight(rand.nextInt((int)(maxEdgeWeight - minEdgeWeight) + 1) + (int)minEdgeWeight);
				graph.add(e);
			}
		}
		
		return graph;
	}
	
	/**
	 * Inverts the specified graph meaning a directed graph is transferred in an undirected one and an undirected
	 * graph is transferred in a directed one.
	 * 
	 * @param graph the graph
	 * @return the inverted graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if type of graph is mixed</li>
	 * </ul>
	 * @since 1.0
	 */
	public static Graph<Vertex, Edge> invertGraph(final Graph<Vertex, Edge> graph) throws IllegalArgumentException {
		return invertGraph(graph, new DefaultGraphFactory());
	}
	
	/**
	 * Inverts the specified graph meaning a directed graph is transferred in an undirected one and an undirected
	 * graph is transferred in a directed one.
	 * 
	 * @param graph the graph
	 * @param factory the graph factory
	 * @return the inverted graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if type of graph is mixed</li>
	 * 		<li>if factory is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> Graph<V, E> invertGraph(final Graph<V, E> graph, final GraphFactory<V, E> factory) throws IllegalArgumentException {
		if(graph == null || graph.getType() == Type.MIXED || factory == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final Graph<V, E> invGraph = new Graph<V, E>((graph.getType() == Type.UNDIRECTED) ? Type.DIRECTED : Type.UNDIRECTED);
		final boolean directed = (invGraph.getType() == Type.DIRECTED);
		
		// add the vertices
		for(int i = 0; i < graph.getOrder(); i++)
			invGraph.add(factory.createVertex(graph.getVertex(i).getCaption()));
		
		// add all adges
		E e;
		for(int i = 0; i < graph.getSize(); i++) {
			e = graph.getEdge(i);
			invGraph.add(factory.createEdge(invGraph.getVertexByCaption(e.getPredecessor().getCaption()), invGraph.getVertexByCaption(e.getSuccessor().getCaption()), directed, e.getWeight()));
		}
		
		return invGraph;
	}
	
	/**
	 * Creates an adjacency matrix of a specified graph.
	 * 
	 * @param graph the graph its adjacency matrix should be created
	 * @return the adjacency matrix of the graph where <code>null</code> indicates that there is no edge between two vertices
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> NumericMatrix<Float> createAdjacencyMatrix(final Graph<V, E> graph) throws IllegalArgumentException {
		return createAdjacencyMatrix(graph, false);
	}
	
	/**
	 * Creates an adjacency matrix of a specified graph.
	 * 
	 * @param graph the graph its adjacency matrix should be created
	 * @param asUpperTriangleMatrix <code>true</code> if the form of the adjacency matrix should be an upper triangle matrix (only possible if the graph is undirected and complete or complete bipartite) otherwise <code>false</code>
	 * @return the adjacency matrix of the graph where <code>null</code> indicates that there is no edge between two vertices
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> NumericMatrix<Float> createAdjacencyMatrix(final Graph<V, E> graph, final boolean asUpperTriangleMatrix) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final NumericMatrix<Float> a = new NumericMatrix<Float>(graph.getOrder(), graph.getOrder(), null);
		final boolean utm = asUpperTriangleMatrix && graph.getType() == Type.UNDIRECTED && (isComplete(graph) || isCompleteBipartite(graph));
		V v;
		V u;
		Edge e;
		int from;
		
		for(int i = 0; i < graph.getOrder(); i++) {
			v = graph.getVertex(i);
			from = utm ? i : 0;
			for(int j = from; j < graph.getOrder(); j++) {
				if(i == j)
					continue;
				
				u = graph.getVertex(j);
				e = graph.getEdge(v, u);
				if(e != null)
					a.set(i, j, e.getWeight());
			}
		}
		
		return a;
	}
	
	/**
	 * Finds the shortest path from a start vertex to all other vertices of a graph.
	 * 
	 * @param graph the graph
	 * @param from the start vertex
	 * @param distance the output matrix of the distance (<code>distance.get(v)</code> is the distance from the start vertex to v where {@link Float#POSITIVE_INFINITY} means that there is no path from the start vertex to this vertex)
	 * @param path the output matrix of the path (<code>path.get(v)</code> is the predecessor of v on the shortest path from the start vertex to v or <code>null</code> if there is no path from the start vertex to v)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if from is null</li>
	 * 		<li>if the graph does not contain the specified start vertex</li>
	 * 		<li>if distance is null</li>
	 * 		<li>if path is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> void findShortestPathFrom(final Graph<V, E> graph, final V from, final Map<V, Float> distance, final Map<V, V> path) throws IllegalArgumentException {
		if(graph == null || from == null || !graph.contains(from) || distance == null || path == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final Set<V> Q = graph.getVertexSet();
		V v;
		V u;
		Edge e;
		float minWeight;
		float dist;
		
		// initialize the matrices
		for(int i = 0; i < graph.getOrder(); i++) {
			v = graph.getVertex(i);
			distance.put(v, Float.POSITIVE_INFINITY);
			path.put(v, null);
		}
		distance.put(from, 0.0f);
		
		while(!Q.isEmpty()) {
			// find vertex u with the smallest value in the distance matrix
			minWeight = Float.POSITIVE_INFINITY;
			u = Q.get(0);
			for(V w : Q) {
				if(distance.get(w) < minWeight) {
					minWeight = distance.get(w);
					u = w;
				}
			}
			
			// remove u from Q
			Q.remove(u);
			
			// update the distance for each neighbor
			for(int i = 0; i < u.getOutgoingEdgeCount(); i++) {
				e = u.getOutgoingEdge(i);
				v = graph.getVertex(e.getSuccessor(u).getIndex());
				
				if(Q.contains(v)) {
					dist = minWeight + e.getWeight();
					if(dist < distance.get(v)) {
						distance.put(v, dist);
						path.put(v, u);
					}
				}
			}
		}
	}
	
	/**
	 * Finds the shortest path from a start vertex to all other vertices of a graph.
	 * 
	 * @param graph the graph
	 * @param from the start vertex
	 * @param to the end vertex
	 * @return the path from the start vertex to the end vertex or an empty path if there is no path between the specified vertices
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if from is null</li>
	 * 		<li>if to is null</li>
	 * 		<li>if from equals to</li>
	 * 		<li>if the graph does not contain the specified start vertex</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> Path<V> findShortestPathFromTo(final Graph<V, E> graph, final V from, final V to) throws IllegalArgumentException {
		if(to == null || from == to)
			throw new IllegalArgumentException("No valid argument!");
		
		final Map<V, V> p = new HashMap<V, V>();
		final Path<V> path = new Path<V>(graph);
		
		findShortestPathFrom(graph, from, new HashMap<V, Float>(), p);
		
		V v = to;
		V u;
		
		// create the path in reverse order
		while(v != null) {
			u = v;
			v = p.get(v);
			// if there is no predecessor then only add u to the path if the path already has at least one edge
			if(v != null || path.length() > 0)
				path.add(0, u);
		}
		
		return path;
	}
	
	/**
	 * Finds the shortest paths from every vertex to all other vertices of a graph.
	 * 
	 * @param graph the graph
	 * @param distMatrix the distance matrix (output parameter) that has to be an n-by-n matrix where n is the order of the graph
	 * @param predMatrix the predecessor matrix (output parameter) that has to be an n-by-n matrix where n is the order of the graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if distMatrix is null</li>
	 * 		<li>if predMatrix is null</li>
	 * 		<li>if distMatrix is not a valid n-by-n matrix</li>
	 * 		<li>if predMatrix is not a valid n-by-n matrix</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> void findShortestPaths(final Graph<V, E> graph, final Matrix<Float> distMatrix, final Matrix<V> predMatrix) throws IllegalArgumentException {
		findShortestPaths(graph, distMatrix, predMatrix, false);
	}
	
	/**
	 * Finds the shortest paths from every vertex to all other vertices of a graph.
	 * 
	 * @param graph the graph
	 * @param distMatrix the distance matrix (output parameter) that has to be an n-by-n matrix where n is the order of the graph
	 * @param predMatrix the predecessor matrix (output parameter) that has to be an n-by-n matrix where n is the order of the graph
	 * @param considerAllVertices <code>true</code> if all vertices should be considered (to check against negative cycles) otherwise <code>false</code> (default)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if distMatrix is null</li>
	 * 		<li>if predMatrix is null</li>
	 * 		<li>if distMatrix is not a valid n-by-n matrix</li>
	 * 		<li>if predMatrix is not a valid n-by-n matrix</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> void findShortestPaths(final Graph<V, E> graph, final Matrix<Float> distMatrix, final Matrix<V> predMatrix, final boolean considerAllVertices) throws IllegalArgumentException {
		if(graph == null || distMatrix == null || predMatrix == null || (distMatrix.getRowCount() != graph.getOrder() || !distMatrix.isSquare()) || (predMatrix.getRowCount() != graph.getOrder() || !predMatrix.isSquare()))
			throw new IllegalArgumentException("No valid argument!");
		
		for(int i = 0; i < graph.getOrder(); i++) {
			distMatrix.set(i, i, 0.0f);
			predMatrix.set(i, i, graph.getVertex(i));
		}
		
		for(int i = 0; i < graph.getOrder(); i++) {
			for(int j = 0; j < graph.getOrder(); j++) {
				if(j == i)
					continue;
				
				E e;
				if((e = graph.getEdge(graph.getVertex(i), graph.getVertex(j))) != null) {
					distMatrix.set(i, j, e.getWeight());
					predMatrix.set(i, j, graph.getVertex(i));
				}
				else {
					distMatrix.set(i, j, Float.MAX_VALUE);
					predMatrix.set(i, j, null);
				}
			}
		}
		
		float dist;
		
		for(int t = 0; t < graph.getOrder(); t++) {
			for(int s = 0; s < graph.getOrder(); s++) {
				if(!considerAllVertices && s == t)
					continue;
				
				for(int d = 0; d < graph.getOrder(); d++) {
					if(!considerAllVertices && (d == t || d == s))
						continue;
					
					dist = distMatrix.get(s, t) + distMatrix.get(t, d);
					if(dist < distMatrix.get(s, d)) {
						distMatrix.set(s, d, dist);
						predMatrix.set(s, d, predMatrix.get(t, d));
					}
				}
			}
		}
	}
	
	/**
	 * Performs a breadth first search (BFS) in the specified graph starting with the first vertex.
	 * 
	 * @param graph the graph
	 * @return the BFS tree
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if there is no valid start vertex (start is null)</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> SimpleGraph<Vertex, Edge> breadthFirstSearch(final Graph<V, E> graph) {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		return breadthFirstSearch(graph, (graph.getOrder() > 0) ? graph.getVertex(0) : null);
	}
	
	/**
	 * Performs a breadth first search (BFS) in the specified graph starting at a given vertex.
	 * 
	 * @param graph the graph
	 * @param start the vertex to start with
	 * @return the BFS tree
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if there is no valid start vertex (start is null)</li>
	 * 		<li>if the specified graph does not contain the given start vertex</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> SimpleGraph<Vertex, Edge> breadthFirstSearch(final Graph<V, E> graph, final V start) throws IllegalArgumentException {
		if(graph == null || start == null || !graph.contains(start))
			throw new IllegalArgumentException("No valid argument!");
		
		final SimpleGraph<Vertex, Edge> tree = new SimpleGraph<Vertex, Edge>(false);
		final boolean[] marked = new boolean[graph.getOrder()];
		final List<Vertex> queue = new ArrayList<Vertex>();
		Vertex vInGraph;
		Vertex wInGraph;
		Vertex vInTree;
		Vertex wInTree;
		
		// add the start vertex to the tree, mark him and add him to the queue
		tree.add(new Vertex(start.getCaption()));
		marked[start.getIndex()] = true;
		queue.add(start);
		
		while(!queue.isEmpty()) {
			// dequeue
			vInGraph = queue.get(0);
			queue.remove(0);
			// get the equivalent in the tree
			vInTree = tree.getVertexByCaption(vInGraph.getCaption());
			
			// for all neighbors
			for(int i = 0; i < vInGraph.getOutgoingEdgeCount(); i++) {
				wInGraph = vInGraph.getOutgoingEdge(i).getSuccessor(vInGraph);
				
				// vertex is not marked/visited?
				if(!marked[wInGraph.getIndex()]) {
					// create a new vertex in the tree
					wInTree = new Vertex(wInGraph.getCaption());
					tree.add(wInTree);
					// now the vertex is marked/visited
					marked[wInGraph.getIndex()] = true;
					// add the marked vertex to the queue so that its neighbors are processed too
					queue.add(wInGraph);
					// add an edge between the current vertices in the tree for traversal
					tree.add(new Edge(vInTree, wInTree));
				}
			}
		}
		
		return tree;
	}
	
	/**
	 * Performs a depth first search (DFS) in the specified graph starting with the first vertex.
	 * 
	 * @param graph the graph
	 * @return the DFS tree
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if there is no valid start vertex (start is null)</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> SimpleGraph<Vertex, Edge> depthFirstSearch(final Graph<V, E> graph) {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		return depthFirstSearch(graph, (graph.getOrder() > 0) ? graph.getVertex(0) : null);
	}
	
	/**
	 * Performs a depth first search (DFS) in the specified graph starting at a given vertex.
	 * 
	 * @param graph the graph
	 * @param start the vertex to start with
	 * @return the DFS tree
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if there is no valid start vertex (start is null)</li>
	 * 		<li>if the specified graph does not contain the given start vertex</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> SimpleGraph<Vertex, Edge> depthFirstSearch(final Graph<V, E> graph, final V start) throws IllegalArgumentException {
		if(graph == null || start == null || !graph.contains(start))
			throw new IllegalArgumentException("No valid argument!");
		
		final SimpleGraph<Vertex, Edge> tree = new SimpleGraph<Vertex, Edge>(false);
		final boolean[] visited = new boolean[graph.getOrder()];
		
		depthFirstSearch(graph, start, tree, visited);
		
		return tree;
	}
	
	/**
	 * Indicates whether the specified path is an augmenting path on the given matching.
	 * <br><br>
	 * Given a graph <code>G = (V, E)</code>, a path <code>(v_1,...v_k)</code> on a matching <code>M</code> is an augmenting path, if:
	 * <ul>
	 * 		<li>the path is no cycle (meaning each vertex exists only once)</li>
	 * 		<li>the first (<code>v_1</code>) and the last vertex (<code>v_k</code>) of the path are not matched meaning they are not endpoints of an edge of <code>M</code></li>
	 * 		<li>the path is an alternating path that starts from and ends on free (unmatched) vertices</li>
	 * </ul>
	 * <br>
	 * <b>Notice</b>:<br>
	 * Ensure that both, the path and the matching are associated with the same graph.
	 * 
	 * @param path the path
	 * @param matching the matching
	 * @return <code>true</code> if it is an augmenting path otherwise <code>false</code> 
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if path is null</li>
	 * 		<li>if matching is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> boolean isAugmentingPath(final Path<V> path, final Matching<E> matching) throws IllegalArgumentException {
		if(path == null || matching == null)
			throw new IllegalArgumentException("No valid argument!");
		
		/*
		 * a path is an augmenting path if:
		 * 1. the path is no cycle (meaning each vertex exists only one time)
		 * 2. the first and the last vertex of the path are not matched meaning they are not endpoints of an edge of the matching
		 * 3. the path is an alternating path that starts from and ends on free (unmatched) vertices
		 */
		if(path.isClosed() || path.length() < 1)
			return false;
		else if(matching.isMatched(path.get(0)) || matching.isMatched(path.get(path.length())))
			return false;
		
		// check if vertices v_2 up to v_k-1 of the path (v_1,v_2,...,v_k-1,v_k) are matched vertices
		for(int i = 1; i < path.length(); i++)
			if(!matching.isMatched(path.get(i)))
				return false;
		
		return true;
	}
	
	/**
	 * Finds an augmenting path beginning with a start vertex in a specified graph based on a given matching.
	 * 
	 * @param graph the graph
	 * @param start the start vertex
	 * @param m the matching (<b>ensure that this matching is associated with the given graph</b>)
	 * @return an augmenting path or <code>null</code> if there is no augmenting path starting with the specified vertex in the graph based on the given matching
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if there is no valid start vertex (start is null)</li>
	 * 		<li>if the specified graph does not contain the given start vertex</li>
	 * </ul>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> Path<V> findAugmentingPath(final Graph<V, E> graph, final V start, final Matching<E> m) throws IllegalArgumentException {
		// create the APT
		final SimpleGraph<Vertex, Edge> apt = createAugmentingPathTree(graph, start, m);
		
		final Vertex root = apt.getVertexByCaption(start.getCaption());
		final Path<V> ap = new Path<V>(graph);
		final boolean[] visited = new boolean[apt.getOrder()];
		
		// find a path
		if(findUmatchedLeaf(graph, root, m, ap, visited))
			return ap;
		else
			return null;
	}
	
	/**
	 * Indicates whether the specified graph is an Eulerian graph.
	 * <br><br>
	 * In case of an <b>undirected</b>, <b>connected</b> graph <code>G = (V,E)</code>, <code>G</code> is eulerian
	 * if and only if the degree of each vertex is even.<br>
	 * In case of a <b>directed</b>, <b>connected</b> graph <code>G = (V,E)</code>, <code>G</code> is eulerian if
	 * and only if the outdegree is euqal to the indegree of each vertex.
	 * 
	 * @param graph the graph
	 * @return <code>true</code> if the graph is an Eulerian graph otherwise <code>false</code>
	 * @since 1.0
	 */
	public static <V extends Vertex, E extends Edge> boolean isEulerian(final Graph<V, E> graph) {
		if(!isConnected(graph))
			return false;
		
		switch(graph.getType()) {
			case UNDIRECTED:
				for(int i = 0; i < graph.getOrder(); i++)
					if(graph.getVertex(i).getDegree() % 2 != 0)
						return false;
				
				return true;
			case DIRECTED:
				for(int i = 0; i < graph.getOrder(); i++)
					if(graph.getVertex(i).getIndegree() != graph.getVertex(i).getOutdegree())
						return false;
				
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Converts a specified walk as a string in a concrete {@link Walk}.
	 * 
	 * @param walk the walk with the captions of the vertices
	 * @param graph the related graph
	 * @return the concrete walk or <code>null</code> if the input walk contains invalid vertex captions
	 * @since 1.1
	 */
	public static <V extends Vertex> Walk<V> toWalk(String walk, final Graph<V, ?> graph) {
		final Walk<V> w = new Walk<V>(graph);
		
		if(!convertWalk(walk, w, graph))
			return null;
		else
			return w;
	}
	
	/**
	 * Converts a specified path as a string in a concrete {@link Path}.
	 * 
	 * @param path the path with the captions of the vertices
	 * @param graph the related graph
	 * @return the concrete path or <code>null</code> if the input path contains invalid vertex captions
	 * @since 1.1
	 */
	public static <V extends Vertex> Path<V> toPath(String path, final Graph<V, ?> graph) {
		final Path<V> p = new Path<V>(graph);
		
		if(!convertWalk(path, p, graph))
			return null;
		else
			return p;
	}
	
	/**
	 * Converts a specified trail as a string in a concrete {@link Trail}.
	 * 
	 * @param trail the trail with the captions of the vertices
	 * @param graph the related graph
	 * @param base the base trail its edges may not contained in the returned trail or <code>null</code> if there is no base trail
	 * @return the concrete trail or <code>null</code> if the input trail contains invalid vertex captions
	 * @since 1.1
	 */
	public static <V extends Vertex> Trail<V> toTrail(String trail, final Graph<V, ? extends Edge> graph, final Trail<Vertex> base) {
		final Trail<V> t = new Trail<V>(graph);
		V v = null;
		V u;
		
		// remove leading and trailing whitespaces
		trail = trail.trim();
		
		// if the user enters the trail like "(v1,v2,...,vN)" then remove the brackets
		trail = trail.substring(trail.startsWith("(") ? 1 : 0, trail.endsWith(")") ? trail.length() - 1 : trail.length());
		
		// split the trail input into its vertices (respectively their captions)
		final String[] vertices = trail.split(",");
		
		for(int i = 0; i < vertices.length; i++) {
			u = graph.getVertexByCaption(vertices[i].trim());
			
			if(u == null)
				return null;
			
			try {
				if(base == null || v == null)
					t.add(u);
				else {
					// if there is a base trail then we need to find an edge between v and u that is not contained
					// in the base trail or the currently created trail otherwise the trail cannot be added to a base trail
					final List<? extends Edge> edges = graph.getEdges(v, u);
					for(Edge e : edges) {
						if(!base.contains(e) && !t.contains(e)) {
							t.add(u, e);
							break;
						}
					}
				}
			}
			catch(IllegalArgumentException e) {
				return null;
			}
			
			v = u;
		}
		
		return t;
	}
	
	/**
	 * Finds an unmatched leaf in an APT.
	 * 
	 * @param graph the graph
	 * @param vInTree the current vertex in the tree
	 * @param m the matching
	 * @param path the path
	 * @param visited the visited states of the vertices
	 * @return <code>true</code> if there is an unmatched leaf in the subtree where vInTree is the root otherwise <code>false</code>
	 * @since 1.0
	 */
	private static <V extends Vertex, E extends Edge> boolean findUmatchedLeaf(final Graph<V, E> graph, final Vertex vInTree, final Matching<E> m, final Path<V> path, final boolean[] visited) {
		final V vInGraph = graph.getVertexByCaption(vInTree.getCaption());
		Vertex wInTree;
		V wInGraph;
		boolean found = false;
		
		// add current vertex to the path and mark him as visited
		path.add(vInGraph);
		visited[vInTree.getIndex()] = true;
		
		// go through all edges of the vertex in the tree
		for(int i = 0; i < vInTree.getOutgoingEdgeCount(); i++) {
			wInTree = vInTree.getOutgoingEdge(i).getPredecessor(vInTree);
			
			// if vertex is already visited we do not need to check him again
			if(visited[wInTree.getIndex()])
				continue;
			
			// convert the tree vertex to a vertex of the graph
			wInGraph = graph.getVertexByCaption(wInTree.getCaption());
			
			// if we have found an unvisited and unmatched vertex then we have found an augmenting path to
			if(!m.isMatched(wInGraph)) {
				// add this last vertex, mark him as visited (!) and finish
				path.add(wInGraph);
				visited[wInTree.getIndex()] = true;
				found = true;
				break;
			}
			
			// find an unmatched leaf (vertex) in the next subtree
			found = findUmatchedLeaf(graph, wInTree, m, path, visited);
			// no unmatched leaf found? then remove the last vertex from the path so that we can search in another subtree
			if(!found)
				path.removeLast();
			else
				break;
		}
		
		return found;
	}
	
	/**
	 * Creates an augmenting path tree (APT) meaning a BFS tree that contains only alternating paths.
	 * <br><br>
	 * Every path from the root to a leaf is in the following pattern: <code>unmatched edge, matched edge, unmatched edge, ...</code>.
	 * 
	 * @param graph the graph
	 * @param start the vertex to start with
	 * @param m the matching associated with the given graph
	 * @return the augmenting path tree (APT)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * 		<li>if there is no valid start vertex (start is null)</li>
	 * 		<li>if the specified graph does not contain the given start vertex</li>
	 * </ul>
	 * @since 1.0
	 */
	private static <V extends Vertex, E extends Edge> SimpleGraph<Vertex, Edge> createAugmentingPathTree(final Graph<V, E> graph, final V start, final Matching<E> m) throws IllegalArgumentException {
		if(graph == null || start == null || !graph.contains(start))
			throw new IllegalArgumentException("No valid argument!");
		
		final SimpleGraph<Vertex, Edge> tree = new SimpleGraph<Vertex, Edge>(false);
		final boolean[] marked = new boolean[graph.getOrder()];
		final List<Vertex> queue = new ArrayList<Vertex>();
		final List<Boolean> queueMatchedState = new ArrayList<Boolean>();
		Vertex vInGraph;
		Vertex wInGraph;
		Vertex vInTree;
		Vertex wInTree;
		E eInGraph;
		boolean currMatchedState;
		
		/*
		 * The creation of an APT is a modification of a BFS:
		 * ===================================================
		 * Instead of visiting all neighbors of a vertex we only visit the ones with an edge that complies with the current
		 * matched state.
		 * The matched state is alternating that is, if we have a path p = (v_1, v_2, v_3, v_4, ...) the matched states of the edges
		 * are (v_1,v_2)=unmatched, (v_2,v_3)=matched, (v_3,v_4)=unmatched etc.
		 * Hence it is easy to find an augmenting path in an APT because one must only find an unmatched leaf in the APT.
		 */
		
		tree.add(new Vertex(start.getCaption()));
		marked[start.getIndex()] = true;
		queue.add(start);
		queueMatchedState.add(false);
		
		while(!queue.isEmpty()) {
			// dequeue
			vInGraph = queue.get(0);
			queue.remove(0);
			currMatchedState = queueMatchedState.get(0);
			queueMatchedState.remove(0);
			// get the equivalent in the tree
			vInTree = tree.getVertexByCaption(vInGraph.getCaption());
			
			// for all neighbors
			for(int i = 0; i < vInGraph.getOutgoingEdgeCount(); i++) {
				eInGraph = graph.getEdgeByID(vInGraph.getOutgoingEdge(i).getID());
				wInGraph = eInGraph.getSuccessor(vInGraph);
				
				// if the current edge is not conform with the current matched state then this edge cannot be visited
				if(m.contains(eInGraph) != currMatchedState)
					continue;
				
				// vertex is not marked/visited?
				if(!marked[wInGraph.getIndex()]) {
					// create a new vertex in the tree
					wInTree = new Vertex(wInGraph.getCaption());
					tree.add(wInTree);
					// now the vertex is marked/visited
					marked[wInGraph.getIndex()] = true;
					// add the marked vertex to the queue so that its neighbors are processed too
					queue.add(wInGraph);
					// add the reversed matched state the vertex has to handle with
					queueMatchedState.add(!currMatchedState);
					// add an edge between the current vertices in the tree for traversal
					tree.add(new Edge(vInTree, wInTree));
				}
			}
		}
		
		return tree;
	}
	
	/**
	 * Performs a depth first search (DFS).
	 * 
	 * @param graph the graph
	 * @param v the current unvisited vertex
	 * @param tree the DFS tree
	 * @param visited the visited state of the vertices
	 * @since 1.0
	 */
	private static <V extends Vertex, E extends Edge> void depthFirstSearch(final Graph<V, E> graph, final Vertex v, final SimpleGraph<Vertex, Edge> tree, final boolean[] visited) {
		Vertex w;
		
		visited[v.getIndex()] = true;
		tree.add(new Vertex(v.getCaption()));
		
		for(int i = 0; i < v.getOutgoingEdgeCount(); i++) {
			w = v.getOutgoingEdge(i).getSuccessor(v);
			
			if(!visited[w.getIndex()]) {
				depthFirstSearch(graph, w, tree, visited);
				tree.add(new Edge(tree.getVertexByCaption(v.getCaption()), tree.getVertexByCaption(w.getCaption())));
			}
		}
	}
	
	/**
	 * Indicates whether the given graph is 2-colorable.
	 * 
	 * @param graph the graph
	 * @param colors the mapping between vertex and color which is the result of the graph coloring
	 * @param nonIncidentVerticesToSubset1 <code>true</code> if non-incident vertices should be added to subset 1 or <code>false</code> to add non-incident vertices to subset 2
	 * @return <code>true</code> if the graph is 2-colorable otherwise <code>false</code>
	 * @since 1.0
	 */
	private static <V extends Vertex, E extends Edge> boolean is2Colorable(final Graph<V, E> graph, final Map<Vertex, Integer> colors, final boolean nonIncidentVerticesToSubset1) {
		// set each color to unknown
		for(int i = 0; i < graph.getOrder(); i++)
			colors.put(graph.getVertex(i), -1);
		
		// color each vertex by using dfs (depth-first search)s
		for(int i = 0; i < graph.getOrder(); i++)
				if(colors.get(graph.getVertex(i)) == -1 && !colorVertex(graph.getVertex(i), colors, 1, 2, nonIncidentVerticesToSubset1 ? 1 : 2))
					return false;
		
		return true;
	}
	
	/**
	 * Colors a vertex (aid for solving 2 coloring problem).
	 * 
	 * @param v the vertex
	 * @param colors the color mapping
	 * @param color1 the first color
	 * @param color2 the second color
	 * @param nonIncidentColor the color of non-incident vertices (that is, color1 to add non-incident vertices to subset 1 otherwise color2)
	 * @return <code>true</code> if vertex can be colored otherwise <code>false</code>
	 * @since 1.0
	 */
	private static boolean colorVertex(final Vertex v, final Map<Vertex, Integer> colors, final int color1, final int color2, final int nonIncidentColor) {
		Vertex u;
		int colorU;
		boolean colorable = true;
		
		// non-incident vertices should be added to a specific subset (1 or 2)
		if(v.getIncomingEdgeCount() < 1 && v.getOutgoingEdgeCount() < 1)
			colors.put(v, nonIncidentColor);
		else
			colors.put(v, color1);
		
		for(int i = 0; i < v.getOutgoingEdgeCount(); i++) {
			u = v.getOutgoingEdge(i).getSuccessor(v);
			colorU = colors.get(u);
			
			// two adjacent vertices have the same color? then graph is not 2-colorable
			if(colorU == color1)
				return false;
			else if(colorU == -1)
				colorable = colorVertex(u, colors, color2, color1, nonIncidentColor);
			
			// coloring not possible? then break up
			if(!colorable)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Computes the connected components of the given graph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method is part of the <i>tarjan algorithm</i> to compute strongly connected components!
	 * 
	 * @param graph the graph
	 * @param components the components list
	 * @param v the current vertex
	 * @param stack the stack of vertices
	 * @param dfsIndex the current depth-first search index
	 * @param indices the indices of the vertices
	 * @param lowlinks the lowlinks of the vertices
	 * @since 1.0
	 */
	private static <V extends Vertex, E extends Edge> void computeConnectedComponents(final Graph<V, E> graph, final List<List<V>> components, final Vertex v, final Stack<Integer> stack, MutableNumber<Integer> dfsIndex, final int[] indices, final int[] lowlinks) {
		Vertex u;
		
		// set the depth index for v to the smallest unused index
		indices[v.getIndex()] = dfsIndex.value();
		lowlinks[v.getIndex()] = dfsIndex.value();
		dfsIndex.value(dfsIndex.value() + 1);
		stack.push(v.getID());
		
		// consider the successors of v
		for(int i = 0; i < v.getOutgoingEdgeCount(); i++) {
			u = v.getOutgoingEdge(i).getSuccessor(v);
			
			if(indices[u.getIndex()] == -1) {
				// the successor u has not yet been visited; recurse on it
				computeConnectedComponents(graph, components, u, stack, dfsIndex, indices, lowlinks);
				lowlinks[v.getIndex()] = Math.min(lowlinks[v.getIndex()], lowlinks[u.getIndex()]);
			}
			else if(stack.contains(u.getID())) {
				// the successor u is in the stack and hence in the current SCC (strongly connected component)
				lowlinks[v.getIndex()] = Math.min(lowlinks[v.getIndex()], lowlinks[u.getIndex()]);
			}
		}
		
		// if v is a root node, pop the stack and generate an SCC
		if(lowlinks[v.getIndex()] == indices[v.getIndex()]) {
			// start a new strongly connected component
			final List<V> scc = new ArrayList<V>();
			V w;
			
			// add w to current strongly connected component
			do {
				w = graph.getVertexByID(stack.pop());
				if(w != null)
					scc.add(w);
			} while(w != v);
			
			// add the SCC to the list of components
			components.add(scc);
		}
	}
	
	/**
	 * Converts a specified walk as a string in a concrete {@link Walk} or a subtype.
	 * 
	 * @param walk the walk with the captions of the vertices
	 * @return the concrete walk or <code>null</code> if the input walk contains invalid vertex captions
	 * @param graph the related graph
	 * @return <code>true</code> if the string could be converted successfully into a concrete walk otherwise <code>false</code>
	 * @since 1.1
	 */
	private static <V extends Vertex> boolean convertWalk(String walk, final Walk<V> w, final Graph<V, ?> graph) {
		V v;
		
		// remove leading and trailing whitespaces
		walk = walk.trim();
		
		// if the user enters the walk like "(v1,v2,...,vN)" then remove the brackets
		walk = walk.substring(walk.startsWith("(") ? 1 : 0, walk.endsWith(")") ? walk.length() - 1 : walk.length());
		
		// split the walk input into its vertices (respectively their captions)
		final String[] vertices = walk.split(",");
		
		for(String vertexCaption : vertices) {
			vertexCaption = vertexCaption.trim();
			v = graph.getVertexByCaption(vertexCaption);
			
			if(v == null)
				return false;
			
			try {
				w.add(v);
			}
			catch(IllegalArgumentException e) {
				return false;
			}
		}
		
		return true;
	}

}
