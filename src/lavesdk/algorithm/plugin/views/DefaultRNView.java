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
 * Class:		DefaultRNView
 * Task:		Default implementation of a residual network view
 * Created:		15.10.14
 * LastChanges:	08.04.15
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import lavesdk.language.LanguageFile;
import lavesdk.math.graph.Graph;
import lavesdk.math.graph.GraphFactory;
import lavesdk.math.graph.Vertex;
import lavesdk.math.graph.network.Arc;
import lavesdk.math.graph.network.DefaultRNFactory;
import lavesdk.math.graph.network.Node;
import lavesdk.math.graph.network.RNEdge;
import lavesdk.math.graph.network.ResidualNetwork;

/**
 * The default implementation of a residual network view.
 * <br><br>
 * The default residual network view can handle with {@link Vertex} and {@link RNEdge} objects using a custom {@link GraphFactory}.
 * <br><br>
 * The residual network view aims at supporting a clear
 * 
 * @see DefaultNetworkView
 * @author jdornseifer
 * @version 1.0
 * @since 1.2
 */
public class DefaultRNView extends GraphView<Vertex, RNEdge> {

	private static final long serialVersionUID = 1L;
	
	/** the related network view */
	private final DefaultNetworkView networkView;
	
	/** the tolerance value for the graphical adoption of a node caption */
	private static final double CAPTIONADOPTION_TOLERANCE = 50.0;

	/**
	 * Creates a new default residual network view.
	 * 
	 * @param title the title
	 * @param langFile the language file
	 * @param langID the language id
	 * @param networkView the related network view that displays the network of the residual network
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if networkView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public DefaultRNView(String title, LanguageFile langFile, String langID, final DefaultNetworkView networkView) throws IllegalArgumentException, NullPointerException {
		this(title, true, langFile, langID, networkView);
	}
	
	/**
	 * Creates a new default residual network view.
	 * 
	 * @param title the title
	 * @param closable <code>true</code> if the residual network view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a residual network view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langFile the language file
	 * @param langID the language id
	 * @param networkView the related network view that displays the network of the residual network
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @throws NullPointerException
	 * <ul>
	 * 		<li>if networkView is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public DefaultRNView(String title, boolean closable, LanguageFile langFile, String langID, final DefaultNetworkView networkView) throws IllegalArgumentException, NullPointerException {
		super(title, new ResidualNetwork(networkView.getGraph()), new DefaultRNFactory(), null, closable, langFile, langID);
		
		this.networkView = networkView;
	}
	
	/**
	 * Gets the residual network of the residual network view.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Be aware that the changes you make directly on the graph are not transferred to the visual layer.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the residual network
	 * @since 1.0
	 */
	@Override
	public ResidualNetwork getGraph() {
		return (ResidualNetwork)super.getGraph();
	}
	
	/**
	 * Sets the residual network of the residual network view.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * For each vertex and residual network edge of the residual network a visual component will be created. To position the vertices you should
	 * call {@link #layoutGraph(GraphLayout)} otherwise existing vertices will be positioned at (0,0).
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param rn the residual network
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if rn is null</li>
	 * 		<li>if rn is no instance of {@link ResidualNetwork}</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void setGraph(Graph<Vertex, RNEdge> rn) throws IllegalArgumentException {
		if(!(rn instanceof ResidualNetwork))
			throw new IllegalArgumentException("No valid argument!");
		
		super.setGraph(rn);
	}
	
	@Override
	protected void afterVisualVertexCreated(VisualVertex vv) {
		GraphView<Node, Arc>.VisualVertex currNode;
		GraphView<Node, Arc>.VisualVertex currNodeWithSmallestDist = null;
		double currDist;
		double currSmallestDist = Double.MAX_VALUE;
		
		for(int i = 0; i < networkView.getVisualVertexCount(); i++) {
			currNode = networkView.getVisualVertex(i);
			currDist = Math.sqrt((currNode.getX() - vv.getX()) * (currNode.getX() - vv.getX()) + (currNode.getY() - vv.getY()) * (currNode.getY() - vv.getY()));
			if(currDist < currSmallestDist) {
				currNodeWithSmallestDist = currNode;
				currSmallestDist = currDist;
			}
		}
		
		if(currNodeWithSmallestDist != null && currSmallestDist <= CAPTIONADOPTION_TOLERANCE)
			vv.getVertex().setCaption(currNodeWithSmallestDist.getVertex().getCaption());
	}
	
	@Override
	protected boolean beforeVisualEdgeCreated(VisualVertex predecessor, VisualVertex successor, boolean directed) {
		final boolean allowed = networkView.getVisualVertexByCaption(predecessor.getVertex().getCaption()) != null && networkView.getVisualVertexByCaption(successor.getVertex().getCaption()) != null;
		
		if(!allowed) {
			final String text = LanguageFile.getLabel(langFile, "MSG_ERR_DEFAULTRNVIEW_CREATEEDGE", langID, "The vertices \"&predecessor&\" and \"&successor&\" do not exist in the network.\nYou can only create edges between vertices that exist in the related network!\nTip: Check the captions of the vertices.");
			JOptionPane.showMessageDialog(this, text.replace("&predecessor&", predecessor.getVertex().getCaption()).replace("&successor&", successor.getVertex().getCaption()), LanguageFile.getLabel(langFile, "MSG_ERR_TITLE_DEFAULTRNVIEW_CREATEEDGE", langID, "Create edge"), JOptionPane.ERROR_MESSAGE);
		}
		
		return allowed;
	}
	
	@Override
	protected List<EdgeOffset> requestOffsetEdges(VisualVertex v, VisualVertex u) {
		/*
		 * info: between two vertices in the default residual network view may only be two edges at a max
		 */
		
		final List<EdgeOffset> offsets = new ArrayList<EdgeOffset>();
		final Vertex predecessor = v.getVertex();
		final Vertex successor = u.getVertex();
		final List<RNEdge> edges = getGraph().getEdges(predecessor, successor);
		final List<RNEdge> redges = getGraph().getEdges(successor, predecessor);
		int invalidEdgesCount = 0;
		int invalidREdgesCount = 0;
		
		if(edges == null)
			return null;
		
		for(RNEdge e : edges)
			if(e.getRelationID() < 0)
				invalidEdgesCount++;
		if(redges != null)
			for(RNEdge re : redges)
				if(re.getRelationID() < 0)
					invalidREdgesCount++;
		
		// if there is an invalid edge from v -> u or from u -> v then the network can only have one edge
		// between v and u so we can use the standard offset request
		if(invalidEdgesCount > 0 || invalidREdgesCount > 0)
			return super.requestOffsetEdges(v, u);
		
		// otherwise there are two edges between v and u that is, we have to order the edges that the user can identify
		// them meaning which residual network edge belongs to which network edge
		if(edges.size() == 1) {
			final RNEdge edge = edges.get(0);
			final GraphView<Node, Arc>.VisualEdge nedge = networkView.getVisualEdgeByID(edge.getRelationID());
			
			if(redges == null)
				offsets.add(new EdgeOffset(edge, 0));
			else if(redges.size() == 1)
				offsets.add(new EdgeOffset(edge, 1));
			else {
				if(nedge == null)
					offsets.add(new EdgeOffset(edge, -2));
				else {
					final boolean reverse = !nedge.getEdge().getPredecessor().getCaption().equals(predecessor.getCaption()) || !nedge.getEdge().getSuccessor().getCaption().equals(successor.getCaption());
					
					boolean redgeExist = false;
					for(RNEdge re : redges) {
						if(re.getRelationID() == edge.getRelationID()) {
							redgeExist = true;
							break;
						}
					}
					
					final int offset = (reverse && redgeExist && redges.size() == 2) ? 2 : 1;
					offsets.add(new EdgeOffset(edge, reverse ? -offset : offset));
				}
			}
		}
		else if(edges.size() == 2) {
			final RNEdge edge1 = edges.get(0);
			final RNEdge edge2 = edges.get(1);
			final GraphView<Node, Arc>.VisualEdge nedge1 = networkView.getVisualEdgeByID(edge1.getRelationID());
			final GraphView<Node, Arc>.VisualEdge nedge2 = networkView.getVisualEdgeByID(edge2.getRelationID());
			final GraphView<Node, Arc>.VisualEdge arc;
			final RNEdge arcEdge;
			final RNEdge reverseArcEdge;
			
			if(nedge1 == null || nedge2 == null)
				return null;
			
			if(nedge1.getEdge().getPredecessor().getCaption().equals(predecessor.getCaption()) && nedge1.getEdge().getSuccessor().getCaption().equals(successor.getCaption())) {
				arc = nedge1;
				arcEdge = edge1;
				reverseArcEdge = edge2;
			}
			else {
				arc = nedge2;
				arcEdge = edge2;
				reverseArcEdge = edge1;
			}
			
			offsets.add(new EdgeOffset(arcEdge, arc.getOffsetIndex()));
			
			boolean redge2Exist = false;
			if(redges != null) {
				for(RNEdge re : redges) {
					if(re.getRelationID() == reverseArcEdge.getRelationID()) {
						redge2Exist = true;
						break;
					}
				}
			}
			offsets.add(new EdgeOffset(reverseArcEdge, -(redge2Exist ? 2 : 1)));
		}
		
		return offsets;
	}

}
