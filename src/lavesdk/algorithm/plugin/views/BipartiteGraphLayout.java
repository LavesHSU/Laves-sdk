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
 * Class:		BipartiteGraphLayout
 * Task:		Layouts a graph in two partitions/bipartite subsets
 * Created:		04.11.13
 * LastChanges:	12.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import java.util.List;

import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Graph;
import lavesdk.math.graph.Vertex;
import lavesdk.utils.GraphUtils;

/**
 * Layouts the vertices of a graph in two partitions/bipartite subsets.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class BipartiteGraphLayout extends GraphLayout {
	
	/** flag that indicates whether non-incident vertices should be added to subset 1 or 2 */
	private final boolean nonIncidentVerticesToSubset1;
	
	/** the padding between two vertices in a subset */
	private static final int DEF_VERTEXPADDING = 25;
	/** the padding between two subsets */
	private static final int DEF_SUBSETPADDING = 150;
	/** the offset of the first subset from the left side of the view */
	private static final int DEF_OFFSETLEFT = 50;
	/** the offset of the greatest subset from the top side of the view */
	private static final int DEF_OFFSETTOP = 30;
	
	/**
	 * Creates a new bipartite graph layout.
	 * 
	 * @since 1.0
	 */
	public BipartiteGraphLayout() {
		this(true);
	}
	
	/**
	 * Creates a new bipartite graph layout.
	 * 
	 * @param nonIncidentVerticesToSubset1 <code>true</code> if non-incident vertices should be added to subset 1 or <code>false</code> to add non-incident vertices to subset 2
	 * @since 1.0
	 */
	public BipartiteGraphLayout(final boolean nonIncidentVerticesToSubset1) {
		this.nonIncidentVerticesToSubset1 = nonIncidentVerticesToSubset1;
	}

	@Override
	public <V extends Vertex, E extends Edge> void layout(Graph<V, E> graph, GraphView<V, E> graphView) {
		final List<List<V>> subsets = GraphUtils.getBipartiteVertexSets(graph, nonIncidentVerticesToSubset1);
		
		// graph is not bipartite? then break up!
		if(subsets == null)
			return;
		
		final int maxSubsetSize = Math.max(subsets.get(0).size(), subsets.get(1).size());
		final int proportionalVertexPadding = (graph.getOrder() > 5) ? DEF_VERTEXPADDING + 5 * (maxSubsetSize / 5) : DEF_VERTEXPADDING;
		
		// the default values are defined for a none zoom which means a zoom of 100%
		final int vertexPadding = (int)(((float)proportionalVertexPadding / 100.0f) * graphView.getZoom());
		final int subsetPadding = (int)(((float)DEF_SUBSETPADDING / 100.0f) * graphView.getZoom());
		final int offsetLeft = (int)(((float)DEF_OFFSETLEFT / 100.0f) * graphView.getZoom());
		final int offsetTop = (int)(((float)DEF_OFFSETTOP / 100.0f) * graphView.getZoom());
		
		int currRadius;
		int greatestRadius = 0;
		
		// find the greates radius of a vertex
		for(int i = 0; i < graph.getOrder(); i++) {
			currRadius = graphView.getScaledVertexRadius(graphView.getVisualVertex(graph.getVertex(i)));
			if(currRadius > greatestRadius)
				greatestRadius = currRadius;
		}
		
		final int greatestDiameter = greatestRadius * 2;
		List<V> greatestSubset = null;
		
		// find subset with the highest amount of vertices
		for(List<V> subset : subsets) {
			if(greatestSubset == null)
				greatestSubset = subset;
			else if(subset.size() > greatestSubset.size())
				greatestSubset = subset;
		}
		
		// calculate the center of all subsets based on the greatest subset
		final int mainSubsetCenterY = offsetTop + ((greatestSubset != null) ? calculateSubsetCenter(greatestSubset, greatestDiameter, vertexPadding) : 0);
		int subsetX = offsetLeft + greatestRadius;
		int currVertexY;
		GraphView<V, E>.VisualVertex vv;
		
		// go through all subsets and position the vertices
		for(List<V> subset : subsets) {
			// calculate the y position of the first vertex in the subset
			currVertexY = mainSubsetCenterY - calculateSubsetCenter(subset, greatestDiameter, vertexPadding) + greatestRadius;
			
			// go through all vertices of the subset and position them successively
			for(V v : subset) {
				vv = graphView.getVisualVertex(v);
				if(vv != null)
					vv.setPosition(subsetX, currVertexY);
				
				// update the y position of the next vertex in the subset
				currVertexY += greatestDiameter + vertexPadding;
			}
			
			subsetX += greatestDiameter + subsetPadding;
		}
	}
	
	/**
	 * Calculates the center of the subset on the y-axis.
	 * 
	 * @param subset the subset
	 * @param greatestDiameter the greatest diameter
	 * @param vertexPadding the padding between two vertices of the subset
	 * @return the center
	 * @since 1.0
	 */
	private <V extends Vertex> int calculateSubsetCenter(final List<V> subset, final int greatestDiameter, final int vertexPadding) {
		return (subset.size() * greatestDiameter + (subset.size() - 1) * vertexPadding) / 2;
	}

}
