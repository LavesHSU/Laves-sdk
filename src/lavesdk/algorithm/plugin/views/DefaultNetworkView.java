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
 * Class:		DefaultNetworkView
 * Task:		Default implementation of a network view
 * Created:		02.12.13
 * LastChanges:	12.12.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import java.text.NumberFormat;
import java.text.ParseException;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.gui.widgets.NumericProperty;
import lavesdk.gui.widgets.PropertiesListModel;
import lavesdk.language.LanguageFile;
import lavesdk.math.graph.Graph;
import lavesdk.math.graph.network.Arc;
import lavesdk.math.graph.network.DefaultNetworkFactory;
import lavesdk.math.graph.network.Network;
import lavesdk.math.graph.network.Node;
import lavesdk.resources.Resources;

/**
 * The default implementation of a network view.
 * <br><br>
 * The default network view can handle with {@link Node} and {@link Arc} objects using the {@link DefaultNetworkFactory}.
 * 
 * @see GraphView
 * @see DefaultRNView
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class DefaultNetworkView extends GraphView<Node, Arc> {

	private static final long serialVersionUID = 1L;
	
	/** the language dependent property name of the flow of an arc */
	private final String arcPropFlow;
	/** flag that indicates whether a keyboard input should be used to change the flow of an arc */
	private boolean applyInputToFlow;
	/** flag that indicates whether a keyboard input should be used to change the excess of a node */
	private boolean applyInputToExcess;
	
	/**
	 * Creates a new default network view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param network the network
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if network is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public DefaultNetworkView(String title, Network<Node, Arc> network) throws IllegalArgumentException {
		this(title, network, null);
	}
	
	/**
	 * Creates a new default network view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param network the network
	 * @param graphLayout the graph layout to layout the nodes of the network automatically or <code>null</code> for the default layout
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if network is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public DefaultNetworkView(String title, Network<Node, Arc> network, GraphLayout graphLayout) throws IllegalArgumentException {
		this(title, network, graphLayout, true, "en");
	}

	/**
	 * Creates a new default network view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param network the network
	 * @param graphLayout the graph layout to layout the nodes of the network automatically or <code>null</code> for the default layout
	 * @param closable <code>true</code> if the network view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a network view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if network is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public DefaultNetworkView(String title, Network<Node, Arc> network, GraphLayout graphLayout, boolean closable, String langID) throws IllegalArgumentException {
		this(title, network, graphLayout, closable, Resources.getInstance().LANGUAGE_FILE, langID);
	}
	
	/**
	 * Creates a new default network view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param network the network
	 * @param graphLayout the graph layout to layout the nodes of the network automatically or <code>null</code> for the default layout
	 * @param closable <code>true</code> if the network view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a network view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langFile the language file or <code>null</code> if the default network view should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if network is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public DefaultNetworkView(String title, Network<Node, Arc> network, GraphLayout graphLayout, boolean closable, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		super(title, network, new DefaultNetworkFactory(), graphLayout, closable, langFile, langID);
		
		arcPropFlow = LanguageFile.getLabel(langFile, "NETWORKVIEW_ARCPROPS_FLOW", langID, "Flow");
		applyInputToFlow = false;
		applyInputToExcess = false;
	}
	
	/**
	 * Gets the network of the network view.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Be aware that the changes you make directly on the graph are not transferred to the visual layer.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the network
	 * @since 1.0
	 */
	@Override
	public Network<Node, Arc> getGraph() {
		return (Network<Node, Arc>)super.getGraph();
	}
	
	/**
	 * Sets the network of the network view.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * For each node and arc of the network a visual component will be created. To position the vertices you should
	 * call {@link #layoutGraph(GraphLayout)} otherwise existing vertices will be positioned at (0,0).
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param network the network
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if network is null</li>
	 * 		<li>if network is no instance of {@link Network}</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void setGraph(Graph<Node, Arc> network) throws IllegalArgumentException {
		if(!(network instanceof Network))
			throw new IllegalArgumentException("No valid argument!");
		
		super.setGraph(network);
	}
	
	/**
	 * Indicates whether a keyboard input should be applied to the flow of an {@link Arc}.
	 * <br><br>
	 * By default the user can change the weight by using the keyboard.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the keyboard input changes the flow or <code>false</code> if the keyboard input changes the weight
	 * @since 1.0
	 */
	public boolean getApplyInputToFlow() {
		if(EDT.isExecutedInEDT())
			return applyInputToFlow;
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return applyInputToFlow;
				}
			});
	}
	
	/**
	 * Sets whether a keyboard input should be applied to the flow of an {@link Arc}.
	 * <br><br>
	 * By default the user can change the weight by using the keyboard.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param apply <code>true</code> if the keyboard input should change the flow or <code>false</code> if the keyboard input should change the weight
	 * @since 1.0
	 */
	public void setApplyInputToFlow(final boolean apply) {
		if(EDT.isExecutedInEDT())
			applyInputToFlow = apply;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setApplyInputToFlow") {
				@Override
				protected void execute() throws Throwable {
					applyInputToFlow = apply;
				}
			});
	}
	
	/**
	 * Indicates whether a keyboard input should be applied to the excess of a {@link Node}.
	 * <br><br>
	 * By default the user can change the caption by using the keyboard.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the keyboard input changes the excess or <code>false</code> if the keyboard input changes the caption
	 * @since 1.0
	 */
	public boolean getApplyInputToExcess() {
		if(EDT.isExecutedInEDT())
			return applyInputToExcess;
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return applyInputToExcess;
				}
			});
	}
	
	/**
	 * Sets whether a keyboard input should be applied to the excess of a {@link Node}.
	 * <br><br>
	 * By default the user can change the caption by using the keyboard.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param apply <code>true</code> if the keyboard input changes the excess or <code>false</code> if the keyboard input changes the caption
	 * @since 1.0
	 */
	public void setApplyInputToExcess(final boolean apply) {
		if(EDT.isExecutedInEDT())
			applyInputToExcess = apply;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setApplyInputToExcess") {
				@Override
				protected void execute() throws Throwable {
					applyInputToExcess = apply;
				}
			});
	}

	@Override
	protected void loadAdvancedEdgeProperties(PropertiesListModel plm, Arc edge) {
		plm.add(new NumericProperty(arcPropFlow, "", edge.getFlow()));
	}

	@Override
	protected void applyAdvancedEdgeProperties(PropertiesListModel plm, Arc edge) {
		final NumericProperty propFlow = plm.getNumericProperty(arcPropFlow);
		if(propFlow != null && propFlow.getValue().floatValue() >= 0.0f && propFlow.getValue().floatValue() <= edge.getWeight())
			edge.setFlow(propFlow.getValue().floatValue());
	}
	
	@Override
	protected void applyKeyboardInput(Arc edge, String input) {
		if(!applyInputToFlow)
			super.applyKeyboardInput(edge, input);
		else
			try { edge.setFlow(NumberFormat.getInstance().parse(input).floatValue()); } catch(ParseException | IllegalArgumentException ex) {}
	}
	
	@Override
	protected void applyKeyboardInput(Node vertex, String input) {
		if(!applyInputToExcess)
			super.applyKeyboardInput(vertex, input);
		else
			try { vertex.setExcess(NumberFormat.getInstance().parse(input).floatValue()); } catch(ParseException | IllegalArgumentException ex) {}
	}

}
