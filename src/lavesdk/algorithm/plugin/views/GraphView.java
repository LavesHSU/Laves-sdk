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

package lavesdk.algorithm.plugin.views;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import lavesdk.algorithm.plugin.views.custom.CustomVisualObject;
import lavesdk.algorithm.plugin.views.custom.CustomVisualText;
import lavesdk.algorithm.plugin.views.renderers.DefaultEdgeRenderer;
import lavesdk.algorithm.plugin.views.renderers.DefaultVertexRenderer;
import lavesdk.algorithm.plugin.views.renderers.EdgeRenderer;
import lavesdk.algorithm.plugin.views.renderers.VertexRenderer;
import lavesdk.configuration.Configuration;
import lavesdk.graphics.CatmullRomSpline;
import lavesdk.graphics.SplineIntersectionPoint;
import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.gui.widgets.BooleanProperty;
import lavesdk.gui.widgets.NumericProperty;
import lavesdk.gui.widgets.PropertiesList;
import lavesdk.gui.widgets.PropertiesListModel;
import lavesdk.gui.widgets.Property;
import lavesdk.gui.widgets.TextProperty;
import lavesdk.language.LanguageFile;
import lavesdk.math.graph.AccessibleIDObserver;
import lavesdk.math.graph.Edge;
import lavesdk.math.graph.Graph;
import lavesdk.math.graph.GraphFactory;
import lavesdk.math.graph.Vertex;
import lavesdk.resources.Resources;
import lavesdk.serialization.ObjectFile;
import lavesdk.serialization.Serializable;
import lavesdk.serialization.Serializer;
import lavesdk.utils.FileUtils;
import lavesdk.utils.MathUtils;
import lavesdk.utils.PopupWindow;

/**
 * Represents the view of a graph.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #GraphView(String, Graph, GraphFactory, boolean, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
 * 		<li><i>GRAPHVIEW_MOUSECURSOR_BTN_TOOLTIP</i>: the tooltip text of the mouse cursor button in the toolbar of the view</li>
 * 		<li><i>GRAPHVIEW_ADDVERTEX_BTN_TOOLTIP</i>: the tooltip text of the add new vertex button in the toolbar of the view</li>
 * 		<li><i>GRAPHVIEW_ADDEDGE_BTN_TOOLTIP</i>: the tooltip text of the add new edge button in the toolbar of the view</li>
 * 		<li><i>GRAPHVIEW_DELETEOBJ_BTN_TOOLTIP</i>: the tooltip text of the delete selected objects button in the toolbar of the view</li>
 * 		<li><i>GRAPHVIEW_SHOWPROPS_BTN_TOOLTIP</i>: the tooltip text of the show properties button in the toolbar of the view</li>
 * 		<li><i>GRAPHVIEW_ZOOMIN_BTN_TOOLTIP</i>: the tooltip text of the zoom in button in the toolbar of the view</li>
 * 		<li><i>GRAPHVIEW_ZOOMOUT_BTN_TOOLTIP</i>: the tooltip text of the zoom out button in the toolbar of the view</li>
 * 		<li><i>GRAPHVIEW_PROPERTIESDLG_TITLE</i>: the title of the properties dialog in which the properties of a vertex or edge are shown</li>
 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the properties dialog in which the properties of a vertex or edge are shown</li>
 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the properties dialog in which the properties of a vertex or edge are shown</li>
 * 		<li><i>GRAPHVIEW_VERTEXPROPS_CAPTION</i>: the name of the caption property in the properties dialog of a vertex</li>
 * 		<li><i>GRAPHVIEW_EDGEPROPS_WEIGHT</i>: the name of the weight property in the properties dialog of an edge</li>
 * 		<li><i>GRAPHVIEW_EDGEPROPS_DIRECTED</i>: the name of the directed property in the properties dialog of an edge</li>
 * 		<li><i>GRAPHVIEW_EDGEPROPS_DIRECTED_CHANGEINFO_DG</i>: the description of the directed property if it is a directed graph</li>
 * 		<li><i>GRAPHVIEW_EDGEPROPS_DIRECTED_CHANGEINFO_UDG</i>: the description of the directed property if it is an undirected graph</li>
 * 		<li><i>MSG_WARN_GRAPHVIEW_DELETEOBJ</i>: the warning message that is displayed when the user wants to delete objects</li>
 * 		<li><i>MSG_WARN_TITLE_GRAPHVIEW_DELETEOBJ</i>: the title of the warning message that is displayed when the user wants to delete objects</li>
 * 		<li><i>MSG_INFO_GRAPHVIEW_SHOWPROPS</i>: the info message that informs the user that he has to select <b>one</b> object to show his properties</li>
 * 		<li><i>MSG_INFO_TITLE_GRAPHVIEW_SHOWPROPS</i>: the title of the info message that informs the user that he has to select <b>one</b> object to show his properties</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * <br><br>
 * <b>Visual appearance</b>:<br>
 * Each vertex is represented by a {@link VisualVertex} and each edge by a {@link VisualEdge}. Use this objects to change the color or line width
 * of the vertices/edges. To get access to the visual objects use one of the {@link #getVisualVertex(int)}/{@link #getVisualEdge(int)} methods.<br>
 * Invoke {@link #setFont(Font)} to set a specific font for the display of text, like vertex captions or edge labels.
 * <br><br>
 * <b>Custom objects</b>:<br>
 * It is possible to extends the graph view using {@link CustomVisualObject}s. With {@link #addVisualObject(CustomVisualObject)} you add new
 * objects and with {@link #removeVisualObject(CustomVisualObject)} you can remove the added objects. there are default implementations
 * for {@link CustomVisualText}
 * <br><br>
 * <b>Tools</b>:<br>
 * <ul>
 * 		<li><i>cursor/mouse</i>: the user can use the cursor to select or deselect vertices/edges, move a vertex by dragging the mouse or click in the graph and
 * 		move the mouse to stretch a selection area</li>
 * 		<li><i>vertex</i>: the user can use the cursor and click inside the graph to position a vertex</li>
 * 		<li><i>edge</i>: the user can select two vertices (<i>cursor tool</i>) and click the "edge" button to create an edge between the vertices or the user
 * 		can use the left mouse button to start/end a connection path and the right mouse button to add intermediate points (after that the vertices that lie on the path are connected)</li>
 * </ul>
 * Use {@link #setShowCursorToolAlways(boolean)}, {@link #setShowZoomToolsAlways(boolean)} and {@link #setHideGraphToolsAlways(boolean)} to enable specific tools even if the graph is (not)
 * editable.
 * <br><br>
 * <b>Edit vertices/edges</b>:<br>
 * The user can use the "show properties" button in the toolbar or double click on a vertex/edge to show the properties or to modify them.
 * Furthermore it is possible to change the caption or weight of a selected vertex/edge on the fly using the keyboard (see keyboard input).<br>
 * Override {@link #loadAdvancedVertexProperties(PropertiesListModel, Vertex)}/{@link #loadAdvancedEdgeProperties(PropertiesListModel, Edge)} and
 * {@link #applyAdvancedVertexProperties(PropertiesListModel, Vertex)}/{@link #applyAdvancedEdgeProperties(PropertiesListModel, Edge)} to load and apply
 * vertex/edge data. The graph view only covers the default/general properties of {@link Vertex} and {@link Edge} by default.
 * <br><br>
 * <b>Graph scene</b>:<br>
 * Use {@link GraphScene}s to visualize a sequence of visual effects on the graph that can be made undone.
 * <br><br>
 * <b>Keyboard input</b>:<br>
 * Keyboard input is used to change the caption or the weight of a selected vertex/edge on the fly. You can prevent this by overriding
 * {@link #ignoreKeyboardInput()}.
 * <br><br>
 * <b>Vertex Position Adjustment System (VPAS)</b>:<br>
 * The graph view has a VPAS by default. The VPAS is responsible for adjusting the position of a moving vertex to the horizontal or vertical axis
 * of its neighbors. That means if the user moves a vertex by mouse the neighborhood is under investigation and if there is a vertex which
 * x or y position is inside a tolerance of a few pixels to the x or y position of the moving vertex then he is adjusted to the corresponding axis
 * of the neighbor. This is to simplify the arrangement of vertices for an esthetic graph.<br>
 * Use {@link #setVPASTolerance(int)} to set this tolerance yourself.
 * <br><br>
 * <b>Dynamic edge labeling</b>:<br>
 * The graph view supports dynamic edge labeling by default.<br>
 * The dynamic edge labeling tries to find a good position for each label of the edges so that the label does not
 * collide with another edge or label and furthermore tries to prevent that multiple labels overly.<br>
 * This is less performant than setting the label to the center of an edge but brings a more esthetic image
 * of the graph. So you have the option to disable this dynamic calculation if the graphical performance of the
 * graph view suffers by using {@link #setDynamicEdgeLabeling(boolean)}.
 * <br><br>
 * <b>Renderers</b>:<br>
 * Initial the graph view uses default renderers for vertices and edges. Implement your own renderer using the interfaces
 * {@link VertexRenderer} and {@link EdgeRenderer}. The default implementations are {@link DefaultVertexRenderer} and {@link DefaultEdgeRenderer}.
 * <br><br>
 * <b>Zooming</b>:<br>
 * You can zoom the graph display manually by using {@link #setZoom(int)} or {@link #zoomIn()}/{@link #zoomOut()}.
 * <br><br>
 * <b>Save/Load visual graphs</b>:<br>
 * Use {@link #save(String)} to save a visual graph in the .vgf format and call {@link #load(String)} to load one. The .vgf format is
 * independent from the objects used in the graph view. That means you can load a visual graph that is generated by another graph view
 * with types of {@link Vertex} and {@link Edge} that differ from the ones in this graph view.<br>
 * You can also export the graph to a png image by calling {@link #saveAsPNG(String)}.
 * <br><br>
 * <b>Transfer graphs</b>:<br>
 * With {@link #transferGraph(GraphTransferProtocol)} you can transfer a graph structure from another graph view into this one.
 * <br><br>
 * <b>Save and load the configuration</b>:<br>
 * You can save and load a configuration of the graph view by using {@link #saveConfiguration(Configuration)} and {@link #loadConfiguration(Configuration)}.
 * It is saved or restored the visibility of the view, the toolbar orientation, the zoom value, the selection color and the edge tool color
 * of the graph view. This makes it possible that you can store the state of the view persistent.
 * 
 * @see GraphScene
 * @see GraphLayout
 * @see GraphTransferProtocol
 * @see DefaultGraphView
 * @see DefaultNetworkView
 * @author jdornseifer
 * @version 1.3
 * @since 1.0
 * @param <V> the type of vertex that should be used in the graph view
 * @param <E> the type of edge that should be used in the graph view
 */
public class GraphView<V extends Vertex, E extends Edge> extends View implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** the graph data structure */
	private Graph<V, E> graph;
	/** the graph factory to create vertices and edges for the {@link #graph} */
	private final GraphFactory<V, E> graphFactory;
	/** the scene of the graph view when the view is not editable */
	private GraphScene<V, E> nonEditableScene;
	/** flag that indicates if the previous edit mode of the view is restorable */
	private boolean restorableEditMode;
	/** the toolbar of the view that displays the graph view options */
	private final JToolBar toolBar;
	/** the scroll pane in which the graph is displayed to be scrollable */
	private final JScrollPane scrollPane;
	/** the panel in which the graph is rendered */
	private final GraphDrawingPanel graphPanel;
	/** the mouse cursor button in the toolbar */
	private final JToggleButton cursorBtn;
	/** the add new vertex button in the toolbar */
	private final JToggleButton addVertexBtn;
	/** the add new edge button in the toolbar */
	private final JToggleButton addEdgeBtn;
	/** the delete selected object button in the toolbar */
	private final JButton deleteObjectBtn;
	/** the zoom in button in the toolbar */
	private final JButton zoomInBtn;
	/** the zoom out button in the toolbar */
	private final JButton zoomOutBtn;
	/** the show properties button in the toolbar */
	private final JButton showPropertiesBtn;
	/** the separators of the toolbar delimit functional areas */
	private final JToolBar.Separator[] toolBarSeparators;
	/** the layout of the content area of the graph view */
	private final BorderLayout contentLayout;
	/** the event controller */
	private final EventController eventController;
	/** flag that indicates if the view is currently editable or not */
	private boolean editable;
	/** flag that indicates whether the zoom buttons (in/out) in the toolbar should be shown always even if the view is not editable */
	private boolean showZoomToolsAlways;
	/** flag that indicates whether the cursor button in the toolbar should be shown always even if the view is not editable */
	private boolean showCursorToolAlways;
	/** flag that indicates whether the buttons in the toolbar for adding vertices, adding edges or removing an object should be hidden always even if the view is editable */
	private boolean hideGraphToolsAlways;
	/** the selection type */
	private SelectionType selectionType;
	/** the currently selected tool */
	private Tool selTool;
	/** the tolerance value of the VPAS */
	private int vpasTolerance;
	/** flag that indicates if dynamic edge labeling is enabled */
	private boolean dynamicEdgeLabeling;
	/** the list of visual vertices of the graph */
	private final List<VisualVertex> visualVertices;
	/** a mapping between the id of a {@link Vertex} and its visual component ({@link VisualVertex}) */
	private final Map<Integer, VisualVertex> visualVerticesByID;
	/** the list of visual edges of the graph */
	private final List<VisualEdge> visualEdges;
	/** a mapping between the id of an {@link Edge} and its visual component ({@link VisualEdge}) */
	private final Map<Integer, VisualEdge> visualEdgesByID;
	/** the dimensions of the drawing area of the graph */
	private final Dimension drawingArea;
	/** the current zoom value in percent (e.g. 5%, 50%, 100%, 120%, ...) */
	private int zoom;
	/** the last value of {@link #zoom} (<b>this value may only be changed in {@link #setZoom(int)}</b>) */
	private int lastZoom;
	/** the list of currently selected visual vertices */
	private final List<VisualVertex> selVertices;
	/** the list of currently selected visual edges */
	private final List<VisualEdge> selEdges;
	/** the current mouse position onto the {@link #graphPanel} and {@link #scrollPane} */
	private Point mousePos;
	/** the vertex that is currently moved by user/mouse */
	private VisualVertex vertexToMove;
	/** flag that indicates whether the {@link #vertexToMove} is actually moved */
	private boolean vertexMoved;
	/** the list of points that describe the current path of the edge tool (that means connecting vertices) */
	private final List<Point> edgeToolCtrlPoints;
	/** the rectangle of a selection area or <code>null</code> if currently no selection area is visible */
	private Rectangle selAreaRect;
	/** the stroke of the selection area */
	private final Stroke selAreaStroke;
	/** the dialog to modify vertex properties */
	private final VertexPropertiesDialog vertexPropsDlg;
	/** the dialog to modify edge properties */
	private final EdgePropertiesDialog edgePropsDlg;
	/** the last time a key typed event occurred */
	private long lastKeyTyped;
	/** the aggregated text that is typed by the user ({@link #keyTyped(KeyEvent)}) */
	private String keyTypedString;
	/** the list of listeners */
	private final List<GraphViewListener<V, E>> listeners;
	/** the font of the graph view */
	private Font font;
	/** the list of the custom visual objects of the graph view */
	private final List<CustomVisualObject> customVisualObjects;
	/** flag that indicates whether the graph has changed meaning whether {@link #setGraph(Graph)} or {@link #transferGraph(GraphTransferProtocol)} was invoked during the edit mode was disabled */
	private boolean graphDataStructureChanged;
	/** the offset distance of an edge */
	private int edgeOffsetDistance;
	/** the observer for graph object id modifications or <code>null</code> if there is no observer */
	private AccessibleIDObserver idObserver;
	/** flag that indicates whether a graph file is currently loaded */
	private boolean loadingVGF;
	
	// language dependent messages
	/** a language dependent message to warn the user if he wants to delete selected objects */
	private String deleteObjectsWarningMsg;
	/** a language dependent title of the warning message if the user wants to delete selected objects */
	private String deleteObjectsWarningTitle;
	/** a language dependent message to inform the user if he wants to display the properties of an object but no object is selected */
	private String showPropsInfoMsg;
	/** a language dependent title of the info message if the user wants to display the properties of an object but no object is selected */
	private String showPropsInfoTitle;
	/** a language dependent label of the caption property of a vertex */
	private final String vertexPropCaption;
	/** a language dependent label of the weight property of an edge */
	private final String edgePropWeight;
	/** a language dependent label of the directed property of an edge */
	private final String edgePropDirected;
	/** a language dependent description of the directed property if it is a directed graph */
	private final String edgePropDirectedDescDG;
	/** a language dependent description of the directed property if it is an undirected graph */
	private final String edgePropDirectedDescUDG;
	
	// colors
	/** the color of selected vertices/edges */
	private Color selectionColor;
	/** the color of the connection path of the edge tool */
	private Color edgeToolColor;
	
	// renderer
	/** the renderer of a vertex */
	private VertexRenderer<V> vertexRenderer;
	/** the renderer of an edge */
	private EdgeRenderer<E> edgeRenderer;
	
	// pre-calculated variables and flags
	// NOTICE: variables that should be zoomable must have at least a float type, furthermore they could be represented
	//		   as an integer value for rendering operations (see setZoom(...) for zoom calculations)
	/** the current integer radius size of the vertices (use {@link #internalGetScaledVertexRadius(VisualVertex)} to get the radius of a specific vertex) */
	private int radiusOfVertex;
	/** the current floating-point radius size of the vertices (<b>use this to calculate zooming</b>) */
	private float radiusOfVertexF;
	/** the current offset distance of edges that means the offset factor if there are multiple edges between two vertices */
	private int currEdgeOffsetDistance;
	/** the current offset distance of edges (<b>use this to calculate zooming</b>) */
	private float currEdgeOffsetDistanceF;
	/** the horizontal and vertical offset distance of a loop that means the offset from the support point */
	private int loopOffsetDistance;
	/** the current horizontal and vertical offset distance of a loop that means the offset from the support point (<b>use this to calculate zooming</b>) */
	private float loopOffsetDistanceF;
	/** the current length of the arrow of a directed edge */
	private int edgeArrowLength;
	/** the current length of the arrow peak of a directed (<b>use this to calculate zooming</b>) */
	private float edgeArrowLengthF;
	/** the current font size of the graph view (<b>use this to calculate zooming</b>) */
	private float fontSizeF;
	/** flag that indicates if the positions of the vertices and edges must be recalculated because of a changed zoom factor */
	private boolean adjustPositionsToZoom;
	/** flag that indicates if the mouse is currently pressed */
	private boolean mouseDown;
	
	// constants
	/** the default radius of vertices */
	private static final int DEF_RADIUS = 10;
	/** the padding of the drawing area that means the padding between the farthest vertex and the edge of the drawing area */
	private static final int DRAWINGAREA_PADDING = 100;
	/** the default length of the arrow */
	private static final int DEF_ARROWLENGTH = 5;
	/** the default offset distance */
	private static final int DEF_OFFSETDISTANCE = 20;
	/** the default horizontal offset distance for <i>loops</i> */
	private static final int DEF_LOOPOFFSETDISTANCE = 5;
	/** the tolerance value if the user wants to select an edge */
	private static final int SELECT_EDGE_TOLARANCE = 3;
	/** the radius of the circles that describing the path points of the edge tool */
	private static final int EDGETOOL_CIRCLE_RADIUS = 3;
	/** the tolerance to adjust a vertex position to the axis of another vertex position */
	private static final int DEF_VPAS_TOLERANCE = 5;
	/** a range that is admit as an angle of 180° */
	private static final double MAX_ARC_ANGLE_EPSILONNEG = 180.0 - 0.000001;
	/** a range that is admit as an angle of 180° */
	private static final double MAX_ARC_ANGLE_EPSILONPOS = 180.0 + 0.000001;
	
	/** the extension of a <b>v</b>isual <b>g</b>raph <b>f</b>ile (.vgf) */
	public static final String VISUALGRAPHFILE_EXT = ".vgf";
	/** the default background of a vertex */
	public static final Color DEF_VERTEXBACKGROUND = Color.white;
	/** the default foreground of a vertex */
	public static final Color DEF_VERTEXFOREGROUND = Color.black;
	/** the default line width of a the vertex edge */
	public static final int DEF_VERTEXEDGEWIDTH = 1;
	/** the default color of an edge */
	public static final Color DEF_EDGECOLOR = Color.black;
	/** the default line width of an edge */
	public static final int DEF_EDGELINEWIDTH = 1;
	/** the default selection color */
	private static final Color DEF_SELECTIONCOLOR = SystemColor.textHighlight;
	/** the default edge tool color */
	private static final Color DEF_EDGETOOLCOLOR = Color.black;
	
	/**
	 * Creates a new view of a graph.
	 * <br><br>
	 * <b>Graph layout</b>:<br>
	 * If you create a graph view with a previously filled graph then all vertices of the graph are positioned by a default {@link GraphLayout}.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param graph the graph
	 * @param graphFactory the factory of the graph that creates the vertices and edges
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if graph is null</li>
	 * 		<li>if graphFactory is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public GraphView(final String title, final Graph<V, E> graph, final GraphFactory<V, E> graphFactory) throws IllegalArgumentException {
		this(title, graph, graphFactory, null);
	}
	
	/**
	 * Creates a new view of a graph.
	 * <br><br>
	 * <b>Graph layout</b>:<br>
	 * If you create a graph view with a previously filled graph then all vertices of the graph are positioned by a default {@link GraphLayout}.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param graph the graph
	 * @param graphFactory the factory of the graph that creates the vertices and edges
	 * @param closable <code>true</code> if the graph view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a graph view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if graph is null</li>
	 * 		<li>if graphFactory is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public GraphView(final String title, final Graph<V, E> graph, final GraphFactory<V, E> graphFactory, final boolean closable) throws IllegalArgumentException {
		this(title, graph, graphFactory, null, closable);
	}
	
	/**
	 * Creates a new view of a graph.
	 * <br><br>
	 * <b>Graph layout</b>:<br>
	 * If you create a graph view with a previously filled graph then all vertices of the graph are positioned at (0,0)
	 * in the view. To define vertex positions or to automatically layout the graph use a {@link GraphLayout}.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param graph the graph
	 * @param graphLayout the graph layout to layout the vertices of the graph automatically or <code>null</code> for the default layout
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if graph is null</li>
	 * 		<li>if graphFactory is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public GraphView(final String title, final Graph<V, E> graph, final GraphFactory<V, E> graphFactory, final GraphLayout graphLayout) throws IllegalArgumentException {
		this(title, graph, graphFactory, graphLayout, true, null, null);
	}
	
	/**
	 * Creates a new view of a graph.
	 * <br><br>
	 * <b>Graph layout</b>:<br>
	 * If you create a graph view with a previously filled graph then all vertices of the graph are positioned at (0,0)
	 * in the view. To define vertex positions or to automatically layout the graph use a {@link GraphLayout}.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param graph the graph
	 * @param graphFactory the factory of the graph that creates the vertices and edges
	 * @param graphLayout the graph layout to layout the vertices of the graph automatically or <code>null</code> for the default layout
	 * @param closable <code>true</code> if the graph view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a graph view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if graph is null</li>
	 * 		<li>if graphFactory is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public GraphView(final String title, final Graph<V, E> graph, final GraphFactory<V, E> graphFactory, final GraphLayout graphLayout, final boolean closable) throws IllegalArgumentException {
		this(title, graph, graphFactory, graphLayout, closable, null, null);
	}
	
	/**
	 * Creates a new view of a graph.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages or tooltips in the graph view. The following language labels are available:
	 * <ul>
	 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
	 * 		<li><i>GRAPHVIEW_MOUSECURSOR_BTN_TOOLTIP</i>: the tooltip text of the mouse cursor button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_ADDVERTEX_BTN_TOOLTIP</i>: the tooltip text of the add new vertex button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_ADDEDGE_BTN_TOOLTIP</i>: the tooltip text of the add new edge button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_DELETEOBJ_BTN_TOOLTIP</i>: the tooltip text of the delete selected objects button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_SHOWPROPS_BTN_TOOLTIP</i>: the tooltip text of the show properties button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_ZOOMIN_BTN_TOOLTIP</i>: the tooltip text of the zoom in button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_ZOOMOUT_BTN_TOOLTIP</i>: the tooltip text of the zoom out button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_PROPERTIESDLG_TITLE</i>: the title of the properties dialog in which the properties of a vertex or edge are shown</li>
	 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the properties dialog in which the properties of a vertex or edge are shown</li>
	 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the properties dialog in which the properties of a vertex or edge are shown</li>
	 * 		<li><i>GRAPHVIEW_VERTEXPROPS_CAPTION</i>: the name of the caption property in the properties dialog of a vertex</li>
	 * 		<li><i>GRAPHVIEW_EDGEPROPS_WEIGHT</i>: the name of the weight property in the properties dialog of an edge</li>
	 * 		<li><i>GRAPHVIEW_EDGEPROPS_DIRECTED</i>: the name of the directed property in the properties dialog of an edge</li>
	 * 		<li><i>GRAPHVIEW_EDGEPROPS_DIRECTED_CHANGEINFO_DG</i>: the description of the directed property if it is a directed graph</li>
	 * 		<li><i>GRAPHVIEW_EDGEPROPS_DIRECTED_CHANGEINFO_UDG</i>: the description of the directed property if it is an undirected graph</li>
	 * 		<li><i>MSG_WARN_GRAPHVIEW_DELETEOBJ</i>: the warning message that is displayed when the user wants to delete objects</li>
	 * 		<li><i>MSG_WARN_TITLE_GRAPHVIEW_DELETEOBJ</i>: the title of the warning message that is displayed when the user wants to delete objects</li>
	 * 		<li><i>MSG_INFO_GRAPHVIEW_SHOWPROPS</i>: the info message that informs the user that he has to select <b>one</b> object to show his properties</li>
	 * 		<li><i>MSG_INFO_TITLE_GRAPHVIEW_SHOWPROPS</i>: the title of the info message that informs the user that he has to select <b>one</b> object to show his properties</li>
	 * </ul>
	 * <br><br>
	 * <b>Graph layout</b>:<br>
	 * If you create a graph view with a previously filled graph then all vertices of the graph are positioned by a default {@link GraphLayout}.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param graph the graph
	 * @param graphFactory the factory of the graph that creates the vertices and edges
	 * @param closable <code>true</code> if the graph view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a graph view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langFile the language file or <code>null</code> if the graph view should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if graph is null</li>
	 * 		<li>if graphFactory is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public GraphView(final String title, final Graph<V, E> graph, final GraphFactory<V, E> graphFactory, final boolean closable, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		this(title, graph, graphFactory, null, closable, langFile, langID);
	}
	
	/**
	 * Creates a new view of a graph.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages or tooltips in the graph view. The following language labels are available:
	 * <ul>
	 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
	 * 		<li><i>GRAPHVIEW_MOUSECURSOR_BTN_TOOLTIP</i>: the tooltip text of the mouse cursor button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_ADDVERTEX_BTN_TOOLTIP</i>: the tooltip text of the add new vertex button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_ADDEDGE_BTN_TOOLTIP</i>: the tooltip text of the add new edge button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_DELETEOBJ_BTN_TOOLTIP</i>: the tooltip text of the delete selected objects button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_SHOWPROPS_BTN_TOOLTIP</i>: the tooltip text of the show properties button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_ZOOMIN_BTN_TOOLTIP</i>: the tooltip text of the zoom in button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_ZOOMOUT_BTN_TOOLTIP</i>: the tooltip text of the zoom out button in the toolbar of the view</li>
	 * 		<li><i>GRAPHVIEW_PROPERTIESDLG_TITLE</i>: the title of the properties dialog in which the properties of a vertex or edge are shown</li>
	 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the properties dialog in which the properties of a vertex or edge are shown</li>
	 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the properties dialog in which the properties of a vertex or edge are shown</li>
	 * 		<li><i>GRAPHVIEW_VERTEXPROPS_CAPTION</i>: the name of the caption property in the properties dialog of a vertex</li>
	 * 		<li><i>GRAPHVIEW_EDGEPROPS_WEIGHT</i>: the name of the weight property in the properties dialog of an edge</li>
	 * 		<li><i>GRAPHVIEW_EDGEPROPS_DIRECTED</i>: the name of the directed property in the properties dialog of an edge</li>
	 * 		<li><i>GRAPHVIEW_EDGEPROPS_DIRECTED_CHANGEINFO_DG</i>: the description of the directed property if it is a directed graph</li>
	 * 		<li><i>GRAPHVIEW_EDGEPROPS_DIRECTED_CHANGEINFO_UDG</i>: the description of the directed property if it is an undirected graph</li>
	 * 		<li><i>MSG_WARN_GRAPHVIEW_DELETEOBJ</i>: the warning message that is displayed when the user wants to delete objects</li>
	 * 		<li><i>MSG_WARN_TITLE_GRAPHVIEW_DELETEOBJ</i>: the title of the warning message that is displayed when the user wants to delete objects</li>
	 * 		<li><i>MSG_INFO_GRAPHVIEW_SHOWPROPS</i>: the info message that informs the user that he has to select <b>one</b> object to show his properties</li>
	 * 		<li><i>MSG_INFO_TITLE_GRAPHVIEW_SHOWPROPS</i>: the title of the info message that informs the user that he has to select <b>one</b> object to show his properties</li>
	 * </ul>
	 * <br><br>
	 * <b>Graph layout</b>:<br>
	 * If you create a graph view with a previously filled graph then all vertices of the graph are positioned at (0,0)
	 * in the view. To define vertex positions or to automatically layout the graph use a {@link GraphLayout}.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param graph the graph
	 * @param graphFactory the factory of the graph that creates the vertices and edges
	 * @param graphLayout the graph layout to layout the vertices of the graph automatically or <code>null</code> for the default layout
	 * @param closable <code>true</code> if the graph view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a graph view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langFile the language file or <code>null</code> if the graph view should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * 		<li>if graph is null</li>
	 * 		<li>if graphFactory is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public GraphView(final String title, final Graph<V, E> graph, final GraphFactory<V, E> graphFactory, final GraphLayout graphLayout, final boolean closable, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		super(title, closable, langFile, langID);
		
		if(graph == null || graphFactory == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.graphFactory = graphFactory;
		this.nonEditableScene = null;
		this.restorableEditMode = true;
		this.toolBar = new JToolBar(JToolBar.VERTICAL);
		this.graphPanel = new GraphDrawingPanel();
		this.scrollPane = new JScrollPane(graphPanel);
		this.cursorBtn = new JToggleButton(Resources.getInstance().MOUSECURSOR_ICON);
		this.addVertexBtn = new JToggleButton(Resources.getInstance().VERTEX_ADD_ICON);
		this.addEdgeBtn = new JToggleButton(Resources.getInstance().EDGE_ADD_ICON);
		this.deleteObjectBtn = new JButton(Resources.getInstance().DELETE_ICON);
		this.zoomInBtn = new JButton(Resources.getInstance().ZOOM_IN_ICON);
		this.zoomOutBtn = new JButton(Resources.getInstance().ZOOM_OUT_ICON);
		this.showPropertiesBtn = new JButton(Resources.getInstance().PROPERTIES_ICON);
		this.contentLayout = new BorderLayout();
		this.eventController = new EventController();
		this.editable = true;
		this.showZoomToolsAlways = true;
		this.showCursorToolAlways = false;
		this.hideGraphToolsAlways = false;
		this.selectionType = SelectionType.BOTH;
		this.vpasTolerance = DEF_VPAS_TOLERANCE;
		this.dynamicEdgeLabeling = true;
		this.visualVertices = new ArrayList<VisualVertex>();
		this.visualVerticesByID = new HashMap<Integer, VisualVertex>();
		this.visualEdges = new ArrayList<VisualEdge>();
		this.visualEdgesByID = new HashMap<Integer, VisualEdge>();
		this.drawingArea = new Dimension();
		this.zoom = this.lastZoom = 100;
		this.mouseDown = false;
		this.vertexToMove = null;
		this.vertexMoved = false;
		this.mousePos = new Point();
		this.selVertices = new ArrayList<VisualVertex>();
		this.selEdges = new ArrayList<VisualEdge>();
		this.edgeToolCtrlPoints = new ArrayList<Point>();
		this.selAreaRect = null;
		this.selAreaStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, new float[] { 5.0f }, 0.0f);
		this.vertexPropsDlg = new VertexPropertiesDialog(langFile, langID);
		this.edgePropsDlg = new EdgePropertiesDialog(langFile, langID);
		this.lastKeyTyped = 0;
		this.keyTypedString = "";
		this.listeners = new ArrayList<GraphViewListener<V, E>>(3);
		this.font = UIManager.getFont("Label.font");
		this.customVisualObjects = new ArrayList<CustomVisualObject>();
		this.graphDataStructureChanged = false;
		this.edgeOffsetDistance = DEF_OFFSETDISTANCE;
		this.loadingVGF = false;
		
		// initialize colors
		this.selectionColor = DEF_SELECTIONCOLOR;
		this.edgeToolColor = DEF_EDGETOOLCOLOR;
		
		// initialize renderers
		this.vertexRenderer = new DefaultVertexRenderer<V>();
		this.edgeRenderer = new DefaultEdgeRenderer<E>();
		
		// initialize flags
		this.radiusOfVertexF = this.radiusOfVertex = DEF_RADIUS;
		this.currEdgeOffsetDistanceF = this.currEdgeOffsetDistance = DEF_OFFSETDISTANCE;
		this.loopOffsetDistanceF = this.loopOffsetDistance = DEF_LOOPOFFSETDISTANCE;
		this.edgeArrowLengthF = this.edgeArrowLength = DEF_ARROWLENGTH;
		this.fontSizeF = font.getSize();
		this.adjustPositionsToZoom = false;
		
		content.setLayout(contentLayout);
		content.add(toolBar, BorderLayout.WEST);
		content.add(scrollPane, BorderLayout.CENTER);
		
		// load language dependent labels, tooltips and messages
		cursorBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHVIEW_MOUSECURSOR_BTN_TOOLTIP", langID, "<b>Mouse cursor</b><br>Use the cursor to select/deselect vertices and edges,<br>move a vertex by dragging him with the mouse or click in the graph<br>and move the mouse to stretch a selection area to select multiple objects.") + "</html>");
		addVertexBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHVIEW_ADDVERTEX_BTN_TOOLTIP", langID, "<b>Add a new vertex</b><br>Use the mouse and click inside the graph area to position a new vertex.") + "</html>");
		addEdgeBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHVIEW_ADDEDGE_BTN_TOOLTIP", langID, "<b>Add a new edge</b><br>Use your left mouse button to start/end a connection path and the right mouse button<br>to add intermediate points if necessary (after you click the left mouse button again<br>the vertices that lie on the path are connected).") + "</html>");
		deleteObjectBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHVIEW_DELETEOBJ_BTN_TOOLTIP", langID, "<b>Delete objects</b><br>Select the vertices and edges you want to remove from the graph and<br>press this button or use the delete key on your keyboard.") + "</html>");
		showPropertiesBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHVIEW_SHOWPROPS_BTN_TOOLTIP", langID, "<b>Show properties</b><br>Select a vertex or edge and press this button to show the properties window.<br>Their you can view and modify the properties of the object.<br><b>Tip 1</b>: You can double click on an object to open the properties window too.<br><b>Tip 2</b>: It is also possible to select a vertex/edge and use the keyboard to change<br>the vertex's caption or edge's weight.") + "</html>");
		zoomInBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHVIEW_ZOOMIN_BTN_TOOLTIP", langID, "<b>Zoom in</b>") + "</html>");
		zoomOutBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "GRAPHVIEW_ZOOMOUT_BTN_TOOLTIP", langID, "<b>Zoom out</b>") + "</html>");
		deleteObjectsWarningMsg = LanguageFile.getLabel(langFile, "MSG_WARN_GRAPHVIEW_DELETEOBJ", langID, "Are you sure you want to delete the selected objects from the graph?");
		deleteObjectsWarningTitle = LanguageFile.getLabel(langFile, "MSG_WARN_TITLE_GRAPHVIEW_DELETEOBJ", langID, "Delete selected objects");
		showPropsInfoMsg = LanguageFile.getLabel(langFile, "MSG_INFO_GRAPHVIEW_SHOWPROPS", langID, "Select an object to show or modify its properties!");
		showPropsInfoTitle = LanguageFile.getLabel(langFile, "MSG_INFO_TITLE_GRAPHVIEW_SHOWPROPS", langID, "Show properties");
		vertexPropCaption = LanguageFile.getLabel(langFile, "GRAPHVIEW_VERTEXPROPS_CAPTION", langID, "caption");
		edgePropWeight = LanguageFile.getLabel(langFile, "GRAPHVIEW_EDGEPROPS_WEIGHT", langID, "weight");
		edgePropDirected = LanguageFile.getLabel(langFile, "GRAPHVIEW_EDGEPROPS_DIRECTED", langID, "directed");
		edgePropDirectedDescDG = LanguageFile.getLabel(langFile, "GRAPHVIEW_EDGEPROPS_DIRECTED_CHANGEINFO_DG", langID, "This is a directed graph so changing this value does not have any effect.");
		edgePropDirectedDescUDG = LanguageFile.getLabel(langFile, "GRAPHVIEW_EDGEPROPS_DIRECTED_CHANGEINFO_UDG", langID, "This is an undirected graph so changing this value does not have any effect.");
		
		// create four separators to delimit functional areas of the toolbar
		toolBarSeparators = new JToolBar.Separator[4];
		for(int i = 0; i < toolBarSeparators.length; i++)
			toolBarSeparators[i] = new JToolBar.Separator();
		
		// add buttons to toolbar
		toolBar.add(cursorBtn);
		toolBar.add(toolBarSeparators[0]);
		//toolBar.addSeparator();
		toolBar.add(addVertexBtn);
		toolBar.add(addEdgeBtn);
		toolBar.add(toolBarSeparators[1]);
		toolBar.add(deleteObjectBtn);
		toolBar.add(toolBarSeparators[2]);
		toolBar.add(showPropertiesBtn);
		toolBar.add(toolBarSeparators[3]);
		toolBar.add(zoomInBtn);
		toolBar.add(zoomOutBtn);
		
		// select the default tool
		selectTool(Tool.CURSOR);
		
		// the drawing area should have a white background
		graphPanel.setBackground(Color.white);
		scrollPane.setBackground(Color.white);
		
		// add keyboard listener directly to the view
		addKeyListener(eventController);
		
		// add listeners to graph view and scroll pane
		graphPanel.addMouseListener(eventController);
		graphPanel.addMouseMotionListener(eventController);
		scrollPane.addMouseListener(eventController);
		
		// add listeners to toolbar buttons
		cursorBtn.addActionListener(eventController);
		addVertexBtn.addActionListener(eventController);
		addEdgeBtn.addActionListener(eventController);
		deleteObjectBtn.addActionListener(eventController);
		showPropertiesBtn.addActionListener(eventController);
		zoomInBtn.addActionListener(eventController);
		zoomOutBtn.addActionListener(eventController);
		
		// finally set the graph and layout its components
		setGraph(graph);
		layoutGraph((graphLayout != null) ? graphLayout : new CircleGraphLayout());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getAutoRepaint() {
		return super.getAutoRepaint();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAutoRepaint(boolean autoRepaint) {
		super.setAutoRepaint(autoRepaint);
	}
	
	/**
	 * Adds a listener to listen to graph view events.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param listener the listener
	 * @since 1.0
	 */
	public final void addGraphViewListener(final GraphViewListener<V, E> listener) {
		if(listener == null || listeners.contains(listener))
			return;
		
		if(EDT.isExecutedInEDT())
			listeners.add(listener);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".addGraphViewListener") {
				@Override
				protected void execute() throws Throwable {
					listeners.add(listener);
				}
			});
	}
	
	/**
	 * Removes a listener from the graph view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param listener the listener
	 * @since 1.0
	 */
	public final void removeGraphViewListener(final GraphViewListener<V, E> listener) {
		if(listener == null)
			return;

		
		if(EDT.isExecutedInEDT())
			listeners.remove(listener);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeGraphViewListener") {
				@Override
				protected void execute() throws Throwable {
					listeners.remove(listener);
				}
			});
	}
	
	/**
	 * Indicates whether the graph view is editable (that means the toolbar is visible and the user can modify the graph)
	 * or not.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if user may modify the graph otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean isEditable() {
		if(EDT.isExecutedInEDT())
			return editable;
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return editable;
				}
			});
	}
	
	/**
	 * Sets the editable state of the graph view meaning whether the user can edit the graph that is displayed in the view
	 * or not.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The cursor button and the zoom in/out buttons remain unaffected by the editable state. If you want to disable/enable this buttons use
	 * {@link #setShowCursorToolAlways(boolean)}/{@link #setShowZoomToolsAlways(boolean)}.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #hasRestorableEditMode()
	 * @param editable <code>true</code> if user may modify the graph otherwise <code>false</code>
	 * @since 1.0
	 */
	public final void setEditable(final boolean editable) {
		if(EDT.isExecutedInEDT())
			internalSetEditable(editable);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setEditable") {
				@Override
				protected void execute() throws Throwable {
					internalSetEditable(editable);
				}
			});
	}
	
	/**
	 * Indicates whether the old edit state is restorable.
	 * <br><br>
	 * A restorable edit state means that if the graph view is set from <i>non edit mode</i> to <i>edit mode</i> the state
	 * of the previous <i>edit mode</i> is restored meaning that the changes to the graph objects (vertices/edges) that were made
	 * in the <i>non edit mode</i> are reversed.
	 * <br><br>
	 * By way of example it is possible that objects which were removed from the graph in <i>non edit mode</i> are restored with their
	 * state of the previous <i>edit mode</i>.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the previous edit state is restoreable otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean hasRestorableEditMode() {
		if(EDT.isExecutedInEDT())
			return restorableEditMode;
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return restorableEditMode;
				}
			});
	}
	
	/**
	 * Sets whether the old edit state is restorable.
	 * <br><br>
	 * A restorable edit state means that if the graph view is set from <i>non edit mode</i> to <i>edit mode</i> the state
	 * of the previous <i>edit mode</i> is restored meaning that the changes to the graph objects (vertices/edges) that were made
	 * in the <i>non edit mode</i> are reversed.
	 * <br><br>
	 * By way of example it is possible that objects which were removed from the graph in <i>non edit mode</i> are restored with their
	 * state of the previous <i>edit mode</i>.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param restorable <code>true</code> if it should be possible that the previous edit mode is restorable otherwise <code>false</code>
	 * @since 1.0
	 */
	public final void setRestorableEditMode(final boolean restorable) {
		if(EDT.isExecutedInEDT())
			restorableEditMode = restorable;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setRestorableEditMode") {
				@Override
				protected void execute() throws Throwable {
					restorableEditMode = restorable;
				}
			});
	}
	
	/**
	 * Sets whether the zoom tools (zoom in/out buttons) in the toolbar should always be shown even if the graph view
	 * is not editable.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param show <code>true</code> if the zoom buttons should be visible even if the graph view is not editable otherwise <code>false</code>
	 * @since 1.0
	 */
	public final void setShowZoomToolsAlways(final boolean show) {
		if(EDT.isExecutedInEDT()) {
			showZoomToolsAlways = show;
			updateToolBarButtonVisibility();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setShowZoomToolsAlways") {
				@Override
				protected void execute() throws Throwable {
					showZoomToolsAlways = show;
					updateToolBarButtonVisibility();
				}
			});
	}
	
	/**
	 * Sets whether the cursor tool (cursor/mouse button) in the toolbar should always be shown even if the graph view
	 * is not editable.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param show <code>true</code> if the cursor button should be visible even if the graph view is not editable otherwise <code>false</code>
	 * @since 1.0
	 */
	public final void setShowCursorToolAlways(final boolean show) {
		if(EDT.isExecutedInEDT()) {
			showCursorToolAlways = show;
			updateToolBarButtonVisibility();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setShowCursorToolAlways") {
				@Override
				protected void execute() throws Throwable {
					showCursorToolAlways = show;
					updateToolBarButtonVisibility();
				}
			});
	}
	
	/**
	 * Sets whether the graph tools (buttons for adding vertices, adding edges or removing objects) in the toolbar should always be hidden even if the graph view
	 * is editable.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param hide <code>true</code> if the graph buttons should be invisible even if the graph view is editable otherwise <code>false</code>
	 * @since 1.0
	 */
	public final void setHideGraphToolsAlways(final boolean hide) {
		if(EDT.isExecutedInEDT()) {
			hideGraphToolsAlways = hide;
			updateToolBarButtonVisibility();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setHideGraphToolsAlways") {
				@Override
				protected void execute() throws Throwable {
					hideGraphToolsAlways = hide;
					updateToolBarButtonVisibility();
				}
			});
	}
	
	/**
	 * Gets the selection type of the graph indicating which objects (vertices and edges) a user can select.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the selection type
	 * @since 1.0
	 */
	public SelectionType getSelectionType() {
		if(EDT.isExecutedInEDT())
			return selectionType;
		else
			return EDT.execute(new GuiRequest<SelectionType>() {
				@Override
				protected SelectionType execute() throws Throwable {
					return selectionType;
				}
			});
	}
	
	/**
	 * Sets the selection type of the graph indicating which objects (vertices and edges) a user can select.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param type the selection type
	 * @since 1.0
	 */
	public void setSelectionType(final SelectionType type) {
		if(EDT.isExecutedInEDT()) {
			selectionType = type;
			deselectAll();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setSelectionType") {
				@Override
				protected void execute() throws Throwable {
					selectionType = type;
					deselectAll();
				}
			});
	}
	
	/**
	 * Gets the last zoom value in percent (integral values).
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If you want to calculate a zoomed value <code>v</code> you have to apply <code>(v / lastZoom) * zoom</code>.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the last zoom
	 * @since 1.0
	 */
	public int getLastZoom() {
		if(EDT.isExecutedInEDT())
			return lastZoom;
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return lastZoom;
				}
			});
	}
	
	/**
	 * Gets the current zoom value in percent (integral values).
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the zoom, like 100%, 50%, 150%, ...
	 * @since 1.0
	 */
	public int getZoom() {
		if(EDT.isExecutedInEDT())
			return zoom;
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return zoom;
				}
			});
	}
	
	/**
	 * Sets the current zoom value in percent.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param z the zoom, like 100%, 50%, 150%, ...
	 * @since 1.0
	 */
	public void setZoom(final int z) {
		if(EDT.isExecutedInEDT())
			internalSetZoom(z);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setZoom") {
				@Override
				protected void execute() throws Throwable {
					internalSetZoom(z);
				}
			});
		
		// redraw the graph
		graphPanel.repaint();
	}
	
	/**
	 * Zooms in the graph view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void zoomIn() {
		setZoom(getZoom() + 10);
	}
	
	/**
	 * Zooms out of the graph view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void zoomOut() {
		setZoom(getZoom() - 10);
	}
	
	/**
	 * Gets the offset distance of edges. This is the space between two edges that go from the same vertex to the same vertex.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the offset distance of edges
	 * @since 1.0
	 */
	public int getEdgeOffsetDistance() {
		if(EDT.isExecutedInEDT())
			return edgeOffsetDistance;
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return edgeOffsetDistance;
				}
			});
	}
	
	/**
	 * Sets the offset distance of edges that go in the same direction from the same vertex.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param distance the distance
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if distance is <code>< 0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public void setEdgeOffsetDistance(final int distance) throws IllegalArgumentException {
		if(distance < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT()) {
			edgeOffsetDistance = distance;
			currEdgeOffsetDistanceF = ((float)edgeOffsetDistance / 100.0f) * zoom;
			currEdgeOffsetDistance = (int)currEdgeOffsetDistanceF;
			computeEdgePositions();
			computeDynamicEdgeLabeling();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setEdgeOffsetDistance") {
				@Override
				protected void execute() throws Throwable {
					edgeOffsetDistance = distance;
					currEdgeOffsetDistanceF = ((float)edgeOffsetDistance / 100.0f) * zoom;
					currEdgeOffsetDistance = (int)currEdgeOffsetDistanceF;
					computeEdgePositions();
					computeDynamicEdgeLabeling();
				}
			});
	}
	
	/**
	 * Gets the tolerance value of the Vertex Position Adjustment System (VPAS).
	 * <br><br>
	 * The VPAS is responsible for adjusting the position of a moving vertex to the horizontal or vertical axis
	 * of its neighbors. That means if the user moves a vertex by mouse the neighborhood is under investigation and if there is a vertex which
	 * x or y position is inside a tolerance of a few pixels to the x or y position of the moving vertex then he is adjusted to the corresponding axis
	 * of the neighbor. This is to simplify the arrangement of vertices for an esthetic graph.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the tolerance of the VPAS (by default: 5 pixels)
	 * @since 1.0
	 */
	public int getVPASTolerance() {
		if(EDT.isExecutedInEDT())
			return vpasTolerance;
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return vpasTolerance;
				}
			});
	}
	
	/**
	 * Sets the tolerance value of the Vertex Position Adjustment System (VPAS).
	 * <br><br>
	 * The VPAS is responsible for adjusting the position of a moving vertex to the horizontal or vertical axis
	 * of its neighbors. That means if the user moves a vertex by mouse the neighborhood is under investigation and if there is a vertex which
	 * x or y position is inside a tolerance of a few pixels to the x or y position of the moving vertex then he is adjusted to the corresponding axis
	 * of the neighbor. This is to simplify the arrangement of vertices for an esthetic graph.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param tolerance the tolerance of the VPAS (by default: 5 pixels)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if tolerance is <code>< 0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public void setVPASTolerance(final int tolerance) throws IllegalArgumentException {
		if(tolerance < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			vpasTolerance = tolerance;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setVPASTolerance") {
				@Override
				protected void execute() throws Throwable {
					vpasTolerance = tolerance;
				}
			});
	}
	
	/**
	 * Indicates wether the graph view supports dynamic edge labeling.
	 * <br><br>
	 * <b>Dynamic edge labeling</b>:<br>
	 * The dynamic edge labeling tries to find a good position for each label of the edges so that the label does not
	 * collide with another edge or label and furthermore tries to prevent that multiple labels overly.<br>
	 * This is less performant than setting the label to the center of an edge but brings a more esthetic image
	 * of the graph. So you have the option to disable this dynamic calculation if the graphical performance of the
	 * graph view suffers.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if dynamic edge labeling is enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean hasDynamicEdgeLabeling() {
		if(EDT.isExecutedInEDT())
			return dynamicEdgeLabeling;
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return dynamicEdgeLabeling;
				}
			});
	}
	
	/**
	 * Sets wether the graph view supports dynamic edge labeling.
	 * <br><br>
	 * <b>Dynamic edge labeling</b>:<br>
	 * The dynamic edge labeling tries to find a good position for each label of the edges so that the label does not
	 * collide with another edge or label and furthermore tries to prevent that multiple labels overly.<br>
	 * This is less performant than setting the label to the center of an edge but brings a more esthetic image
	 * of the graph. So you have the option to disable this dynamic calculation if the graphical performance of the
	 * graph view suffers.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param enable <code>true</code> if dynamic edge labeling is enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setDynamicEdgeLabeling(final boolean enable) {
		if(EDT.isExecutedInEDT()) {
			dynamicEdgeLabeling = enable;
			computeEdgePositions();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setDynamicEdgeLabeling") {
				@Override
				protected void execute() throws Throwable {
					dynamicEdgeLabeling = enable;
					computeEdgePositions();
				}
			});
		
		// repaint() is thread-safe
		graphPanel.repaint();
	}
	
	/**
	 * Sets an individual renderer for the vertices of the graph.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param renderer the renderer
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if renderer is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setVertexRenderer(final VertexRenderer<V> renderer) throws IllegalArgumentException {
		if(renderer == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			vertexRenderer = renderer;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setVertexRenderer") {
				@Override
				protected void execute() throws Throwable {
					vertexRenderer = renderer;
				}
			});
	}
	
	/**
	 * Sets an individual renderer for the edges of the graph.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param renderer the renderer
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if renderer is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setEdgeRenderer(final EdgeRenderer<E> renderer) throws IllegalArgumentException {
		if(renderer == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			edgeRenderer = renderer;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setEdgeRenderer") {
				@Override
				protected void execute() throws Throwable {
					edgeRenderer = renderer;
				}
			});
	}
	
	/**
	 * Gets the color of selected vertices or edges.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the selection color
	 */
	public Color getSelectionColor() {
		if(EDT.isExecutedInEDT())
			return selectionColor;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return selectionColor;
				}
			});
	}
	
	/**
	 * Sets the color of selected vertices or edges.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param c the selection color
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if c is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setSelectionColor(final Color c) throws IllegalArgumentException {
		if(c == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			selectionColor = c;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setSelectionColor") {
				@Override
				protected void execute() throws Throwable {
					selectionColor = c;
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * Gets the color of the edge tool (the path that is be drawn when user uses left and right mouse button
	 * to connect vertices).
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the color of the edge tool path
	 * @since 1.0
	 */
	public Color getEdgeToolColor() {
		if(EDT.isExecutedInEDT())
			return edgeToolColor;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return edgeToolColor;
				}
			});
	}
	
	/**
	 * Sets the color of the edge tool (the path that is be drawn when user uses left and right mouse button
	 * to connect vertices).
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param c the color of the edge tool path
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if c is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setEdgeToolColor(final Color c) throws IllegalArgumentException {
		if(c == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			edgeToolColor = c;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setEdgeToolColor") {
				@Override
				protected void execute() throws Throwable {
					edgeToolColor = c;
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * Gets the number of vertices that are shown in the graph view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of vertices
	 * @since 1.0
	 */
	public final int getVisualVertexCount() {
		if(EDT.isExecutedInEDT())
			return visualVertices.size();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return visualVertices.size();
				}
			});
	}
	
	/**
	 * Gets the visual vertex at the given index.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the vertex
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getVisualVertexCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final VisualVertex getVisualVertex(final int index) throws IndexOutOfBoundsException {
		if(EDT.isExecutedInEDT())
			return visualVertices.get(index);
		else
			return EDT.execute(new GuiRequest<VisualVertex>() {
				@Override
				protected VisualVertex execute() throws Throwable {
					return visualVertices.get(index);
				}
			});
	}
	
	/**
	 * Gets the visual vertex of a given vertex.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param v the vertex its visual component is searched
	 * @return the visual vertex or <code>null</code> if the graph view has no visual vertex for the specified one
	 * @since 1.0
	 */
	public final VisualVertex getVisualVertex(final Vertex v) {
		if(EDT.isExecutedInEDT())
			return (v != null) ? getVisualVertexByID(v.getID()) : null;
		else
			return EDT.execute(new GuiRequest<VisualVertex>() {
				@Override
				protected VisualVertex execute() throws Throwable {
					return (v != null) ? getVisualVertexByID(v.getID()) : null;
				}
			});
	}
	
	/**
	 * Gets the visual vertex of a given vertex id.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param id the identifier of the vertex its visual component is searched
	 * @return the visual vertex or <code>null</code> if the graph view has no visual vertex for the specified vertex id
	 * @since 1.0
	 */
	public final VisualVertex getVisualVertexByID(final int id) {
		if(EDT.isExecutedInEDT())
			return visualVerticesByID.get(id);
		else
			return EDT.execute(new GuiRequest<VisualVertex>() {
				@Override
				protected VisualVertex execute() throws Throwable {
					return visualVerticesByID.get(id);
				}
			});
	}
	
	/**
	 * Gets the visual vertex with the given caption.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param caption the caption of the vertex its visual component is searched
	 * @return the visual vertex or <code>null</code> if the graph view has no visual vertex with the specified caption
	 * @since 1.0
	 */
	public final VisualVertex getVisualVertexByCaption(final String caption) {
		if(EDT.isExecutedInEDT())
			return internalGetVisualVertexByCaption(caption);
		else
			return EDT.execute(new GuiRequest<VisualVertex>() {
				@Override
				protected VisualVertex execute() throws Throwable {
					return internalGetVisualVertexByCaption(caption);
				}
			});
	}
	
	/**
	 * Gets the amount of vertices which are currently selected.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the amount of selected vertices
	 * @since 1.0
	 */
	public final int getSelectedVertexCount() {
		if(EDT.isExecutedInEDT())
			return selVertices.size();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return selVertices.size();
				}
			});
	}
	
	/**
	 * Gets the selected vertex at the given index.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the selected vertex
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getSelectedVertexCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final VisualVertex getSelectedVertex(final int index) throws IndexOutOfBoundsException {
		if(EDT.isExecutedInEDT())
			return selVertices.get(index);
		else
			return EDT.execute(new GuiRequest<VisualVertex>() {
				@Override
				protected VisualVertex execute() throws Throwable {
					return selVertices.get(index);
				}
			});
	}
	
	/**
	 * Gets the number of edges that are shown in the graph view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of edges
	 * @since 1.0
	 */
	public final int getVisualEdgeCount() {
		if(EDT.isExecutedInEDT())
			return visualEdges.size();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return visualEdges.size();
				}
			});
	}
	
	/**
	 * Gets the visual edge at the given index.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the edge
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getVisualEdgeCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final VisualEdge getVisualEdge(final int index) throws IndexOutOfBoundsException {
		if(EDT.isExecutedInEDT())
			return visualEdges.get(index);
		else
			return EDT.execute(new GuiRequest<VisualEdge>() {
				@Override
				protected VisualEdge execute() throws Throwable {
					return visualEdges.get(index);
				}
			});
	}
	
	/**
	 * Gets the visual edge of a given edge.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param e the edge its visual component is searched
	 * @return the visual edge or <code>null</code> if the graph view has no visual edge for the specified one
	 * @since 1.0
	 */
	public final VisualEdge getVisualEdge(final Edge e) {
		if(EDT.isExecutedInEDT())
			return (e != null) ? getVisualEdgeByID(e.getID()) : null;
		else
			return EDT.execute(new GuiRequest<VisualEdge>() {
				@Override
				protected VisualEdge execute() throws Throwable {
					return (e != null) ? getVisualEdgeByID(e.getID()) : null;
				}
			});
	}
	
	/**
	 * Gets the visual edge of a given edge id.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param id the identifier of the edge its visual component is searched
	 * @return the visual edge or <code>null</code> if the graph view has no visual edge for the specified edge id
	 * @since 1.0
	 */
	public final VisualEdge getVisualEdgeByID(final int id) {
		if(EDT.isExecutedInEDT())
			return visualEdgesByID.get(id);
		else
			return EDT.execute(new GuiRequest<VisualEdge>() {
				@Override
				protected VisualEdge execute() throws Throwable {
					return visualEdgesByID.get(id);
				}
			});
	}
	
	/**
	 * Gets the amount of edges which are currently selected.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the amount of selected edges
	 * @since 1.0
	 */
	public final int getSelectedEdgeCount() {
		if(EDT.isExecutedInEDT())
			return selEdges.size();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return selEdges.size();
				}
			});
	}
	
	/**
	 * Gets the selected edge at the given index.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the selected edge
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getSelectedEdgeCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final VisualEdge getSelectedEdge(final int index) throws IndexOutOfBoundsException {
		if(EDT.isExecutedInEDT())
			return selEdges.get(index);
		else
			return EDT.execute(new GuiRequest<VisualEdge>() {
				@Override
				protected VisualEdge execute() throws Throwable {
					return selEdges.get(index);
				}
			});
	}
	
	/**
	 * Selects a visual vertex.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param v the vertex
	 * @param multiSelect <code>true</code> if multi select should be used (that means more than one vertex or edge can be selected) otherwise <code>false</code>
	 * @since 1.0
	 */
	public void selectVertex(final VisualVertex v, final boolean multiSelect) {
		if(EDT.isExecutedInEDT())
			internalSelectVertex(v, multiSelect, true);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".selectVertex") {
				@Override
				protected void execute() throws Throwable {
					internalSelectVertex(v, multiSelect, true);
				}
			});
		
		// repaint() is thread-safe
		graphPanel.repaint();
	}
	
	/**
	 * Selects all visual vertices in the list.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param vertices the list of vertices that should be selected
	 * @since 1.0
	 */
	public void selectVertices(final List<VisualVertex> vertices) {
		if(EDT.isExecutedInEDT()) {
			for(VisualVertex v : vertices)
				internalSelectVertex(v, true, false);
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".selectVertices") {
				@Override
				protected void execute() throws Throwable {
					for(VisualVertex v : vertices)
						internalSelectVertex(v, true, false);
				}
			});
		
		// repaint() is thread-safe
		graphPanel.repaint();
	}
	
	/**
	 * Selects a visual edge.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param e the edge
	 * @param multiSelect <code>true</code> if multi select should be used (that means more than one edge or vertex can be selected) otherwise <code>false</code>
	 * @since 1.0
	 */
	public void selectEdge(final VisualEdge e, final boolean multiSelect) {
		if(EDT.isExecutedInEDT())
			internalSelectEdge(e, multiSelect, true);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".selectEdge") {
				@Override
				protected void execute() throws Throwable {
					internalSelectEdge(e, multiSelect, true);
				}
			});
		
		// repaint() is thread-safe
		graphPanel.repaint();
	}
	
	/**
	 * Selects all visual edges in the list.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param edges the list of edges that should be selected
	 * @since 1.0
	 */
	public void selectEdges(final List<VisualEdge> edges) {
		if(EDT.isExecutedInEDT()) {
			for(VisualEdge e : edges)
				internalSelectEdge(e, true, false);
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".selectEdges") {
				@Override
				protected void execute() throws Throwable {
					for(VisualEdge e : edges)
						internalSelectEdge(e, true, false);
				}
			});
		
		// repaint() is thread-safe
		graphPanel.repaint();
	}
	
	/**
	 * Deselects all selected vertices and edges, that means no object is selected anymore.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void deselectAll() {
		if(EDT.isExecutedInEDT())
			internalDeselectAll();
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".deselectAll") {
				@Override
				protected void execute() throws Throwable {
					internalDeselectAll();
				}
			});
		
		// repaint() is thread-safe
		graphPanel.repaint();
	}
	
	/**
	 * Removes all the vertices from the visual graph and its graph data structure.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It will removed only the vertices which are allowed to be removed.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllVertices() {
		if(EDT.isExecutedInEDT()) {
			for(int i = visualVertices.size() - 1; i >= 0; i--)
				removeVisualVertex(visualVertices.get(i));
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeAllVertices") {
				@Override
				protected void execute() throws Throwable {
					for(int i = visualVertices.size() - 1; i >= 0; i--)
						removeVisualVertex(visualVertices.get(i));
				}
			});
		
		// repaint() is thread-safe
		graphPanel.repaint();
	}
	
	/**
	 * Removes all the edges from the visual graph and its graph data structure.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It will removed only the edges which are allowed to be removed.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllEdges() {
		if(EDT.isExecutedInEDT()) {
			for(int i = visualEdges.size() - 1; i >= 0; i--)
				removeVisualEdge(visualEdges.get(i));
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeAllEdges") {
				@Override
				protected void execute() throws Throwable {
					for(int i = visualEdges.size() - 1; i >= 0; i--)
						removeVisualEdge(visualEdges.get(i));
				}
			});
		
		// repaint() is thread-safe
		graphPanel.repaint();
	}
	
	/**
	 * Gets the graph factory which creates the vertices and edges in the graph view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the graph factory
	 * @since 1.0
	 */
	public GraphFactory<V, E> getGraphFactory() {
		if(EDT.isExecutedInEDT())
			return graphFactory;
		else
			return EDT.execute(new GuiRequest<GraphFactory<V, E>>() {
				@Override
				protected GraphFactory<V, E> execute() throws Throwable {
					return graphFactory;
				}
			});
	}
	
	/**
	 * Gets the graph of the graph view.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Be aware that the changes you make directly on the graph are not transferred to the visual layer.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the graph
	 * @since 1.0
	 */
	public Graph<V, E> getGraph() {
		if(EDT.isExecutedInEDT())
			return graph;
		else
			return EDT.execute(new GuiRequest<Graph<V, E>>() {
				@Override
				protected Graph<V, E> execute() throws Throwable {
					return graph;
				}
			});
	}
	
	/**
	 * Sets the graph of the graph view.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * For each vertex and edge of the graph a visual component will be created. To position the vertices you should
	 * call {@link #layoutGraph(GraphLayout)} otherwise existing vertices will be positioned at (0,0).
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #layoutGraph(GraphLayout)
	 * @param graph the graph
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graph is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setGraph(final Graph<V, E> graph) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			internalSetGraph(graph);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setGraph") {
				@Override
				protected void execute() throws Throwable {
					internalSetGraph(graph);
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * Layouts the graph that means all vertices of the graph are automatically positioned by the
	 * layout algorithm specified in the {@link GraphLayout}.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #createCircleGraphLayout()
	 * @param graphLayout the graph layout to layout the vertices of the graph automatically
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if graphLayout is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void layoutGraph(final GraphLayout graphLayout) throws IllegalArgumentException {
		if(graphLayout == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			internalLayoutGraph(graphLayout);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".layoutGraph") {
				@Override
				protected void execute() throws Throwable {
					internalLayoutGraph(graphLayout);
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * Transfers a graph of another graph view into this one.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * By doing that the current graph will be emptied.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param gtp the graph transfer protocol
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if gtp is null</li>
	 * 		<li>if gtp is not prepared</li>
	 * </ul>
	 * @since 1.0
	 */
	public <N extends Vertex, L extends Edge> void transferGraph(final GraphTransferProtocol<N, L> gtp) throws IllegalArgumentException {
		if(gtp == null || !gtp.isPrepared())
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			internalTransferGraph(gtp);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".transferGraph") {
				@Override
				protected void execute() throws Throwable {
					internalTransferGraph(gtp);
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * Creates a new instance of a {@link CircleGraphLayout}.
	 * 
	 * @see #layoutGraph(GraphLayout)
	 * @return the circle graph layout
	 * @since 1.0
	 */
	public CircleGraphLayout createCircleGraphLayout() {
		return new CircleGraphLayout();
	}
	
	/**
	 * Gets the font of the graph view.
	 * <br><br>
	 * The font is used to render the vertex captions, edge labels and so on.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the font
	 * @since 1.0
	 */
	public Font getFont() {
		if(EDT.isExecutedInEDT())
			return font;
		else
			return EDT.execute(new GuiRequest<Font>() {
				@Override
				protected Font execute() throws Throwable {
					return font;
				}
			});
	}
	
	/**
	 * Sets the font of the graph view.
	 * <br><br>
	 * The font is used to render the vertex captions, edge labels and so on.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param f the font
	 * @since 1.0
	 */
	@Override
	public void setFont(final Font f) {
		if(f == null)
			return;
		
		if(EDT.isExecutedInEDT()) {
			font = f;
			// save font size for zoom calculations
			fontSizeF = f.getSize();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setFont") {
				@Override
				protected void execute() throws Throwable {
					font = f;
					// save font size for zoom calculations
					fontSizeF = f.getSize();
				}
			});
	}
	
	/**
	 * Loads a visual graph from a file.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param file the file (if the file does not end with {@link #VISUALGRAPHFILE_EXT} then the extension is added)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if file is null</li>
	 * </ul>
	 * @throws IOException
	 * <ul>
	 * 		<li>if graph file could not be loaded</li>
	 * </ul>
	 * @since 1.0
	 */
	public void load(final File file) throws IllegalArgumentException, IOException {
		if(file == null)
			throw new IllegalArgumentException("No valid argument!");
		
		load(file.getAbsolutePath());
	}
	
	/**
	 * Loads a visual graph from a file.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param filename the file name (if the file name does not end with {@link #VISUALGRAPHFILE_EXT} then the extension is added)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if filename is null</li>
	 * </ul>
	 * @throws IOException
	 * <ul>
	 * 		<li>if graph file could not be loaded</li>
	 * </ul>
	 * @since 1.0
	 */
	public void load(final String filename) throws IllegalArgumentException, IOException {
		if(filename == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			internalLoad(filename);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".load", true) {
				@Override
				protected void execute() throws Throwable {
					internalLoad(filename);
				}
			});
		
		// repaint() is thread-safe
		graphPanel.repaint();
	}
	
	/**
	 * Saves the visual graph to a file.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #resetVisualAppearance()
	 * @param file the file (if the file does not end with {@link #VISUALGRAPHFILE_EXT} then the extension is added)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if file is null</li>
	 * </ul>
	 * @throws IOException
	 * <ul>
	 * 		<li>if graph file could not be saved</li>
	 * </ul>
	 * @since 1.0
	 */
	public void save(final File file) throws IllegalArgumentException, IOException {
		if(file == null)
			throw new IllegalArgumentException("No valid argument!");
		
		save(file.getAbsolutePath());
	}
	
	/**
	 * Saves the visual graph to a file.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #resetVisualAppearance()
	 * @param filename the file name (if the file name does not end with {@link #VISUALGRAPHFILE_EXT} then the extension is added)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if filename is null</li>
	 * </ul>
	 * @throws IOException
	 * <ul>
	 * 		<li>if graph file could not be saved</li>
	 * </ul>
	 * @since 1.0
	 */
	public void save(final String filename) throws IllegalArgumentException, IOException {
		if(filename == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			internalSave(filename);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".save", true) {
				@Override
				protected void execute() throws Throwable {
					internalSave(filename);
				}
			});
	}
	
	/**
	 * Saves the graph as an image in the PNG format.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param file the file (if the file does not end with ".png" then the extension is added)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if file is null</li>
	 * </ul>
	 * @throws IOException
	 * <ul>
	 * 		<li>if graph image could not be saved</li>
	 * </ul>
	 * @since 1.0
	 */
	public void saveAsPNG(final File file) throws IllegalArgumentException, IOException {
		if(file == null)
			throw new IllegalArgumentException("No valid argument!");
		
		saveAsPNG(file.getAbsolutePath());
	}
	
	/**
	 * Saves the graph as an image in the PNG format.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param filename the filename (if the file name does not end with ".png" then the extension is added)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if filename is null</li>
	 * </ul>
	 * @throws IOException
	 * <ul>
	 * 		<li>if graph image could not be saved</li>
	 * </ul>
	 * @since 1.0
	 */
	public void saveAsPNG(final String filename) throws IllegalArgumentException, IOException {
		if(filename == null)
			throw new IllegalArgumentException("No valid argument!");

		if(EDT.isExecutedInEDT())
			internalSaveAsPNG(filename);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".saveAsPNG", true) {
				@Override
				protected void execute() throws Throwable {
					internalSaveAsPNG(filename);
				}
			});
	}
	
	/**
	 * Resets the graph view meaning that all vertices, edges and custom visual objects are removed from the graph if procurable
	 * (it could be that some vertices/edges are not removable).
	 * <br><br>
	 * Additionally the default visual appearance of the remaining vertices/edges is restored.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #removeAllVertices()
	 * @see #removeAllEdges()
	 * @see #resetVisualAppearance()
	 * @since 1.0
	 */
	@Override
	public void reset() {
		// remove all the edges and vertices which are allowed to be removed
		removeAllEdges();
		removeAllVertices();
		removeAllVisualObjects();
		
		if(EDT.isExecutedInEDT()) {
			internalResetVisualAppearance();
			selectTool(Tool.CURSOR);
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".reset") {
				@Override
				protected void execute() throws Throwable {
					internalResetVisualAppearance();
					selectTool(Tool.CURSOR);
				}
			});
	}
	
	/**
	 * Sets the default visual appearance for all vertices and edges.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #DEF_VERTEXBACKGROUND
	 * @see #DEF_VERTEXFOREGROUND
	 * @see #DEF_VERTEXEDGEWIDTH
	 * @see #DEF_EDGECOLOR
	 * @see #DEF_EDGELINEWIDTH
	 * @since 1.0
	 */
	public void resetVisualAppearance() {
		if(EDT.isExecutedInEDT())
			internalResetVisualAppearance();
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".resetVisualAppearance") {
				@Override
				protected void execute() throws Throwable {
					internalResetVisualAppearance();
				}
			});
	}
	
	/**
	 * Gets a vertex from the given position.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @return the vertex under the position or <code>null</code> if there is no vertex
	 * @since 1.0
	 */
	public VisualVertex getVertexFromPosition(final int x, final int y) {
		if(EDT.isExecutedInEDT())
			return internalGetVertexFromPosition(x, y);
		else
			return EDT.execute(new GuiRequest<VisualVertex>() {
				@Override
				protected VisualVertex execute() throws Throwable {
					return internalGetVertexFromPosition(x, y);
				}
			});
	}
	
	/**
	 * Gets an edge from the given position.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @return the edge under the position or <code>null</code> if there is no edge
	 * @since 1.0
	 */
	public VisualEdge getEdgeFromPosition(final int x, final int y) {
		if(EDT.isExecutedInEDT())
			return internalGetEdgeFromPosition(x, y);
		else
			return EDT.execute(new GuiRequest<VisualEdge>() {
				@Override
				protected VisualEdge execute() throws Throwable {
					return internalGetEdgeFromPosition(x, y);
				}
			});
	}
	
	/**
	 * Gets the scaled radius of the given vertex.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param v the vertex
	 * @return the radius of the vertex
	 * @since 1.0
	 */
	public int getScaledVertexRadius(final VisualVertex v) {
		if(EDT.isExecutedInEDT())
			return internalGetScaledVertexRadius(v);
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return internalGetScaledVertexRadius(v);
				}
			});
	}
	
	/**
	 * Adds a new custom visual object to the graph view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param cvo the visual object
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if cvo is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void addVisualObject(final CustomVisualObject cvo) throws IllegalArgumentException {
		if(cvo == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT()) {
			if(!customVisualObjects.contains(cvo))
				customVisualObjects.add(cvo);
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".addVisualObject") {
				@Override
				protected void execute() throws Throwable {
					if(!customVisualObjects.contains(cvo))
						customVisualObjects.add(cvo);
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * Removes a custom visal object from the graph view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param cvo the visual object that should be removed
	 * @since 1.0
	 */
	public void removeVisualObject(final CustomVisualObject cvo) {
		if(EDT.isExecutedInEDT())
			customVisualObjects.remove(cvo);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeVisualObject") {
				@Override
				protected void execute() throws Throwable {
					customVisualObjects.remove(cvo);
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * Removes all the visual objects from the graph view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	public void removeAllVisualObjects() {
		if(EDT.isExecutedInEDT()) {
			for(int i = customVisualObjects.size() - 1; i >= 0; i--)
				removeVisualObject(customVisualObjects.get(i));
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".removeAllVisualObjects") {
				@Override
				protected void execute() throws Throwable {
					for(int i = customVisualObjects.size() - 1; i >= 0; i--)
						removeVisualObject(customVisualObjects.get(i));
				}
			});
	}
	
	/**
	 * Gets the number of <b>custom</b> visual objects.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of visual objects
	 * @since 1.0
	 */
	public int getVisualObjectCount() {
		if(EDT.isExecutedInEDT())
			return customVisualObjects.size();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				protected Integer execute() throws Throwable {
					return customVisualObjects.size();
				}
			});
	}
	
	/**
	 * Gets the custom visual object at the given index.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the visual object
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getVisualObjectCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public CustomVisualObject getVisualObject(final int index) throws IndexOutOfBoundsException {
		if(EDT.isExecutedInEDT())
			return customVisualObjects.get(index);
		else
			return EDT.execute(new GuiRequest<CustomVisualObject>() {
				@Override
				protected CustomVisualObject execute() throws Throwable {
					return customVisualObjects.get(index);
				}
			});
	}
	
	@Override
	public void serialize(Serializer s) {
		s.addInt("zoom", zoom);
	}
	
	@Override
	public void deserialize(Serializer s) {
		setZoom(s.getInt("zoom", 100));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void repaintComponent() {
		super.repaintComponent();
		
		if(graphPanel != null)
			graphPanel.repaint();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void autoRepaint() {
		if(getAutoRepaint())
			graphPanel.repaint();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Furthermore it is read the toolbar orientation (key "toolBarOrientation"), the zoom value (key "zoom"), the selection
	 * color (key "selColor") and the edge tool color (key "edgeToolColor") of the graph view.
	 */
	@Override
	protected void readConfigurationData(Configuration cd) {
		super.readConfigurationData(cd);
		
		changeToolBarOrientation(cd.getString("toolBarOrientation", BorderLayout.WEST));
		setZoom(cd.getInt("zoom", zoom));
		setSelectionColor(cd.getColor("selColor", DEF_SELECTIONCOLOR));
		setEdgeToolColor(cd.getColor("edgeToolColor", DEF_EDGETOOLCOLOR));
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Furthermore it is written the toolbar orientation (key "toolBarOrientation"), the zoom value (key "zoom"), the selection
	 * color (key "selColor") and the edge tool color (key "edgeToolColor") of the graph view.
	 */
	@Override
	protected void writeConfigurationData(Configuration cd) {
		super.writeConfigurationData(cd);
		
		final String tbOrientation = contentLayout.getConstraints(toolBar).toString();
		
		cd.addString("toolBarOrientation", !tbOrientation.isEmpty() ? tbOrientation : BorderLayout.WEST);
		cd.addInt("zoom", getZoom());
		cd.addColor("selColor", getSelectionColor());
		cd.addColor("edgeToolColor", getEdgeToolColor());
	}
	
	/**
	 * Creates a new visual vertex at the specified position.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method invokes {@link #fireVertexAdded(VisualVertex)}.
	 * 
	 * @param x the x position of the vertex
	 * @param y the y position of the vertex
	 * @return the visual vertex or <code>null</code> if vertex could not be added
	 * @since 1.0
	 */
	protected final VisualVertex createVisualVertex(final int x, final int y) {
		if(!beforeVisualVertexCreated(x, y))
			return null;
		
		final V v = graphFactory.createVertex(getFreeVertexName());
		
		if(graph.add(v)) {
			final VisualVertex vv = new VisualVertex(v);
			addVisualVertex(vv, x, y);
			
			// only recalculate the vertex data if no visual graph file is loaded (otherwise see loadVGFData(...))
			if(!loadingVGF) {
				// a new vertex that means recalculate the drawing area dimensions
				adjustDrawingAreaToVertices();
				// initialize the attachment point
				computeVertexAttachmentPoint(vv);
			}
			
			afterVisualVertexCreated(vv);
			
			// notify listeners
			fireVertexAdded(vv);
			
			return vv;
		}
		
		return null;
	}
	
	/**
	 * Removes a visual vertex of the graph and all its edges.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method invokes {@link #fireVertexRemoved(VisualVertex)}.
	 * 
	 * @param v the vertex that should be removed
	 * @since 1.0
	 */
	protected final void removeVisualVertex(final VisualVertex v) {
		if(v == null)
			return;
		
		if(graph.remove(v.getVertex()) || !graph.contains(v.getVertex())) {
			visualVertices.remove(v);
			visualVerticesByID.remove(v.getVertex().getID());
			
			// adjust the indices of all vertices in the list behind the one that is removed
			for(int i = v.getIndex(); i < visualVertices.size(); i++)
				visualVertices.get(i).setIndex(i);
			
			// vertex was selected then remove it from the selection list
			if(v.isSelected())
				selVertices.remove(v);
			
			// remove all visual edges that have the vertex as predecessor or successor
			for(int i = visualEdges.size() - 1; i >= 0; i--)
				if(visualEdges.get(i).dockTo(v))
					removeVisualEdge(visualEdges.get(i));
			
			// notify listeners
			fireVertexRemoved(v);
		}
	}
	
	/**
	 * Adds a new (undirected) visual edge to the graph between two specified vertices.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method invokes {@link #fireEdgeAdded(VisualEdge)}.
	 * 
	 * @param predecessor the predecessor of the edge
	 * @param successor the successor of the edge
	 * @return the visual edge or <code>null</code> if edge could not be added
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	protected final VisualEdge createVisualEdge(final VisualVertex predecessor, final VisualVertex successor) throws IllegalArgumentException {
		return createVisualEdge(predecessor, successor, false);
	}
	
	/**
	 * Adds a new visual edge to the graph between two specified vertices.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method invokes {@link #fireEdgeAdded(VisualEdge)}.
	 * 
	 * @param predecessor the predecessor of the edge
	 * @param successor the successor of the edge
	 * @param directed <code>true</code> if the edge should be directed or <code>false</code> if the edge should be undirected
	 * @return the visual edge or <code>null</code> if edge could not be added
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if predecessor is null</li>
	 * 		<li>if successor is null</li>
	 * </ul>
	 * @since 1.0
	 */
	protected final VisualEdge createVisualEdge(final VisualVertex predecessor, final VisualVertex successor, final boolean directed) throws IllegalArgumentException {
		if(!beforeVisualEdgeCreated(predecessor, successor, directed))
			return null;
		
		final E e = graphFactory.createEdge(predecessor.getVertex(), successor.getVertex(), directed);
		
		if(graph.add(e)) {
			final VisualEdge ve = new VisualEdge(e, predecessor, successor);
			addVisualEdge(ve);

			// only recalculate the edge data if no visual graph file is loaded (otherwise see loadVGFData(...))
			if(!loadingVGF) {
				// first update the indices
				updateEdgeOffsetIndices();
				// second recalculate the edge position (need the valid offset index)
				computeEdgePosition(ve);
				computeDynamicEdgeLabeling();
				// third update the attachment points of the edge endpoints
				computeVertexAttachmentPoint(ve.getPredecessor());
				computeVertexAttachmentPoint(ve.getSuccessor());
			}
			
			afterVisualEdgeCreated(ve);
			
			// notify listeners
			fireEdgeAdded(ve);
			
			return ve;
		}
		
		return null;
	}
	
	/**
	 * Removes a visual edge of the graph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method invokes {@link #fireEdgeRemoved(VisualEdge)}.
	 * 
	 * @param e the edge that should be removed
	 * @since 1.0
	 */
	protected final void removeVisualEdge(final VisualEdge e) {
		if(e == null)
			return;
		
		// it can be that an edge in the graph is removed before its visual component is removed
		// (see removeVisualVertex) so remove the visual edge if there is not corresponding edge
		// in the graph anymore
		if(graph.remove(e.getEdge()) || !graph.contains(e.getEdge())) {
			visualEdges.remove(e);
			visualEdgesByID.remove(e.getEdge().getID());
			
			// adjust the indices of all edges in the list behind the one that is removed
			for(int i = e.getIndex(); i < visualEdges.size(); i++)
				visualEdges.get(i).setIndex(i);
			
			// edge was selected then remove it from the selection list
			if(e.isSelected())
				selEdges.remove(e);
			
			updateEdgeOffsetIndices();
			computeDynamicEdgeLabeling();
			
			// notify listeners
			fireEdgeRemoved(e);
		}
	}
	
	/**
	 * Adds a new button to the toolbar.
	 * 
	 * @param button the button
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if button is null</li>
	 * </ul>
	 * @since 1.2
	 */
	protected final void addToolbarButton(final JButton button) throws IllegalArgumentException {
		if(button == null)
			throw new IllegalArgumentException("No valid argument!");
		
		toolBar.add(button);
	}
	
	/**
	 * Selects a tool of the toolbar.
	 * 
	 * @param tool the tool
	 * @since 1.2
	 */
	protected void selectTool(final Tool tool) {
		cursorBtn.setSelected(tool == Tool.CURSOR);
		addVertexBtn.setSelected(tool == Tool.VERTEX);
		addEdgeBtn.setSelected(tool == Tool.EDGE);
		
		selTool = tool;
		
		deselectAll();
		
		// clear path of edge tool
		edgeToolCtrlPoints.clear();
		
		// repaint graph
		graphPanel.repaint();
	}
	
	/**
	 * This method is invoked before the graphical representation of the graph view is painted.
	 * <br><br>
	 * You can use this method to draw additional visual objects in the back of the view.
	 * 
	 * @param g the graphics context
	 * @since 1.2
	 */
	protected void beforePaint(final Graphics2D g) {
	}
	
	/**
	 * This method is invoked after the graphical representation of the graph view is painted.
	 * <br><br>
	 * You can use this method to draw additional visual objects in the front of the view.
	 * 
	 * @param g the graphics context
	 * @since 1.2
	 */
	protected void afterPaint(final Graphics2D g) {
	}
	
	/**
	 * This method is invoked before a new visual vertex is created using graphical input by the user.
	 * 
	 * @param x the x position of the vertex center
	 * @param y the y position of the vertex center
	 * @return <code>true</code> if vertex may be created otherwise <code>false</code>
	 * @since 1.2
	 */
	protected boolean beforeVisualVertexCreated(final int x, final int y) {
		return true;
	}
	
	/**
	 * This method is invoked before a new visual edge is created using graphical input by the user.
	 * 
	 * @param predecessor the predecessor of the edge
	 * @param successor the successor of the edge
	 * @param directed <code>true</code> if the edge should be directed or <code>false</code> if the edge should be undirected
	 * @return <code>true</code> if edge may be created otherwise <code>false</code>
	 * @since 1.2
	 */
	protected boolean beforeVisualEdgeCreated(final VisualVertex predecessor, final VisualVertex successor, final boolean directed) {
		return true;
	}
	
	/**
	 * This method is invoked after a new visual vertex is created using graphical input by the user.
	 * 
	 * @param vv visual vertex that was created
	 * @since 1.2
	 */
	protected void afterVisualVertexCreated(final VisualVertex vv) {
	}
	
	/**
	 * This method is invoked after a new visual edge is created using graphical input by the user.
	 * 
	 * @param ve visual edge that was created
	 * @since 1.2
	 */
	protected void afterVisualEdgeCreated(final VisualEdge ve) {
	}
	
	/**
	 * Loads the advanced properties of a vertex that aren't elements of the super class {@link Vertex} and adds them to
	 * the properties list model.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * <b>Override this method to load the advanced properties!</b><br>
	 * Don't forget to set the property values! The default/general properties of {@link Vertex} are covered by the graph view.
	 * 
	 * @param plm the properties list model
	 * @param vertex the vertex which properties should be loaded
	 * @since 1.0
	 */
	protected void loadAdvancedVertexProperties(final PropertiesListModel plm, final V vertex) {
	}
	
	/**
	 * Applies the advanced properties of a vertex to the given vertex.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * <b>Override this method to apply the advanced properties!</b><br>
	 * The default/general properties of {@link Vertex} are covered by the graph view.
	 * 
	 * @param plm the properties list model
	 * @param vertex the vertex which properties changed
	 * @since 1.0
	 */
	protected void applyAdvancedVertexProperties(final PropertiesListModel plm, final V vertex) {
	}
	
	/**
	 * Loads the advanced properties of an edge that aren't elements of the super class {@link Edge} and adds them to
	 * the properties list model.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * <b>Override this method to load the advanced properties!</b><br>
	 * Don't forget to set the property values! The default/general properties of {@link Edge} are covered by the graph view.
	 * 
	 * @param plm the properties list model
	 * @param edge the edge which properties should be loaded
	 * @since 1.0
	 */
	protected void loadAdvancedEdgeProperties(final PropertiesListModel plm, final E edge) {
	}
	
	/**
	 * Applies the advanced properties of an edge to the given edge.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * <b>Override this method to load the advanced properties!</b><br>
	 * The default/general properties of {@link Edge} are covered by the graph view.
	 * 
	 * @param plm the properties list model
	 * @param edge the edge which properties changed
	 * @since 1.0
	 */
	protected void applyAdvancedEdgeProperties(final PropertiesListModel plm, final E edge) {
	}
	
	/**
	 * Indicates if the keyboard input should be ignored meaning that the weight of a selected edge or the
	 * caption of a selected vertex cannot be modified by using the keyboard input any longer.
	 * 
	 * @return <code>true</code> if keyboard input for modification of object data should be ignored otherwise <code>false</code>
	 * @since 1.0
	 */
	protected boolean ignoreKeyboardInput() {
		return false;
	}
	
	/**
	 * Applies the keyboard input the user has entered to a vertex.
	 * <br><br>
	 * By default the input is used to change the caption of a vertex.
	 * 
	 * @param vertex the vertex
	 * @param input the input string
	 * @since 1.0
	 */
	protected void applyKeyboardInput(final V vertex, final String input) {
		vertex.setCaption(input);
	}
	
	/**
	 * Applies the keyboard input the user has entered to an edge.
	 * <br><br>
	 * By default the input is used to change the weight of an edge.
	 * 
	 * @param edge the edge
	 * @param input the input string
	 * @since 1.0
	 */
	protected void applyKeyboardInput(final E edge, final String input) {
		try { edge.setWeight(NumberFormat.getInstance().parse(input).floatValue()); } catch(ParseException ex) {}
	}
	
	/**
	 * Requests the edges between two vertices its offset have to be updated.
	 * <br><br>
	 * The offset of an edge describs the bending of the edge meaning an offset of <code>0</code> denotes no bending, an offset of <code>1</code> or
	 * <code>-1</code> is the weakest bending in two different directions, and so on.
	 * 
	 * @param v the visual vertex v
	 * @param u the visual vertex u
	 * @return the list of edges that are mapped to their offset indices or <code>null</code> if their are no edges between the two vertices that have to be updated
	 * @since 1.0
	 */
	protected List<EdgeOffset> requestOffsetEdges(final VisualVertex v, final VisualVertex u) {
		final List<EdgeOffset> res = new ArrayList<EdgeOffset>();
		List<E> edgesV;
		List<E> edgesU;
		int offset;
		int opposingEdgesCount;
		int currentEdgeCount;
		boolean edgesVUndirected;
		boolean edgesUDirected;
		
		// get all edges between v and u (v -> u)
		edgesV = graph.getEdges(v.getVertex(), u.getVertex());
		
		// if there is no edge between v and u (v -> u) then continue with the next
		if(edgesV == null)
			return null;
		
		// determine the number of edges that really go from v -> u
		currentEdgeCount = 0;
		edgesVUndirected = false;
		for(E e : edgesV) {
			if(e.getSuccessor() == u.getVertex()) {
				currentEdgeCount++;
				edgesVUndirected = edgesVUndirected || !e.isDirected();
			}
		}
		
		// get the opposing edges
		edgesU = graph.getEdges(u.getVertex(), v.getVertex());
		opposingEdgesCount = 0;
		edgesUDirected = false;
		
		// look for opposing edges which are drawn from u -> v
		if(edgesU != null) {
			for(E e : edgesU) {
				if(e.getSuccessor() == v.getVertex()) {
					opposingEdgesCount++;
					edgesUDirected = edgesUDirected || e.isDirected();
				}
			}
		}
		
		// special case: if there is only one undirected edge from v -> u and a directed one from u -> v (count 2 because of the undirected edge v -> u)
		// then the undirected edge should be painted in the center in every other case the opposing edges count and current edges count
		// should decide whether to paint the edges
		if(edgesV.size() == 1 && edgesU != null && edgesU.size() == 2 && edgesVUndirected && edgesUDirected)
			offset = 0;
		else {
			// if there are more edges from v -> u instead rather than from u -> v then we start at offset index 0 so that there is an edge
			// in the middle
			offset = (opposingEdgesCount < currentEdgeCount) ? 0 : 1;
		}
		
		// remove all vertices from the list that are not drawn from v -> u
		// and add all valid edges to the result list
		for(int i = 0; i < edgesV.size(); i++) {
			if(edgesV.get(i).getSuccessor() != u.getVertex()) {
				edgesV.remove(i);
				i--;
			}
			else
				res.add(new EdgeOffset(edgesV.get(i), offset++));
		}
		
		return res;
	}
	
	/**
	 * Key released event when user releases a key on the keyboard.
	 * 
	 * @param e the key event
	 * @since 1.0
	 */
	private void keyReleased(final KeyEvent e) {
		if(editable == false)
			return;
		
		// user types the delete key? then delete the selected objects
		if(e.getKeyCode() == KeyEvent.VK_DELETE && e.getModifiers() == 0)
			deleteSelectedObjects();
	}
	
	/**
	 * Key typed event when user types something on the keyboard.
	 * 
	 * @param e the key event
	 * @since 1.0
	 */
	private void keyTyped(final KeyEvent e) {
		// graph may not be modified or keyboard input should be ignored? then exit because nothing may be done here!
		if(editable == false || ignoreKeyboardInput())
			return;
		
		// if there is more than one object selected then break up!
		if(getSelectedEdgeCount() + getSelectedVertexCount() != 1)
			return;
		
		// accept only writable keys (127 is the delete key)
		final int keyChar = e.getKeyChar();
		if(keyChar < 32 || keyChar == 127)
			return;
		
		final V vertex = (getSelectedVertexCount() > 0) ? getSelectedVertex(0).getVertex() : null;
		final E edge = (getSelectedEdgeCount() > 0) ? getSelectedEdge(0).getEdge() : null;
		
		// extend the string or start a new one?
		keyTypedString = (e.getWhen() - lastKeyTyped <= 600) ? keyTypedString + e.getKeyChar() : "" + e.getKeyChar();
		
		if(vertex != null)
			applyKeyboardInput(vertex, keyTypedString);
		else if(edge != null)
			applyKeyboardInput(edge, keyTypedString);
		
		lastKeyTyped = e.getWhen();
		
		// repaint graph if necessary
		if(vertex != null || edge != null)
			graphPanel.repaint();
	}
	
	/**
	 * Mouse down event when user presses the mouse on the drawing area.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseDown(final MouseEvent e) {
		// mouse is pressed onto the view then set the focus to the graph view
		requestFocus();
		
		// if the view is not in edit mode and the cursor tool is not "visible" then break up because nothing is to do here
		if(editable == false && !showCursorToolAlways)
			return;
		
		if(selTool == Tool.CURSOR) {
			// moving vertices is only possible if the graph view is in edit mode
			if(editable)
				vertexToMove = SwingUtilities.isLeftMouseButton(e) ? internalGetVertexFromPosition(e.getX(), e.getY()) : null;
			
			// clear actual selection range
			selAreaRect = null;
			// check whether the user has clicked an object in the graph
			final VisualVertex v = internalGetVertexFromPosition(e.getX(), e.getY());
			if(v == null) {
				final VisualEdge edge = internalGetEdgeFromPosition(e.getX(), e.getY());
				if(edge == null) {
					// user does not click onto an object then deselect all selected ones
					deselectAll();
					// prepare rectangle that user can hold the mouse and stretch a selection area
					selAreaRect = new Rectangle(e.getX(), e.getY(), 0, 0);
				}
			}
		}
		
		mouseDown = true;
		vertexMoved = false;

		mousePos.x = e.getX();
		mousePos.y = e.getY();
	}
	
	/**
	 * Mouse up event when user releases the mouse on the drawing area.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseUp(final MouseEvent e) {
		// graph may not be modified? then exit because nothing may be done here!
		if(!mouseDown)
			return;
		
		if(selTool == Tool.VERTEX && editable) {
			createVisualVertex(e.getX(), e.getY());
			graphPanel.repaint();
		}
		else if(selTool == Tool.EDGE && editable) {
			// start edge path only if the first control point is onto a vertex
			if(edgeToolCtrlPoints.size() > 0 || internalGetVertexFromPosition(e.getX(), e.getY()) != null) {
				if(edgeToolCtrlPoints.size() < 1) {
					mousePos.x = e.getX();
					mousePos.y = e.getY();
				}
				
				edgeToolCtrlPoints.add(new Point(e.getX(), e.getY()));
				
				// if the left mouse button is used and we have more than 1 controk point then create
				// the edge(s) by the path
				if(SwingUtilities.isLeftMouseButton(e) && edgeToolCtrlPoints.size() > 1) {
					createEdgesByPath(edgeToolCtrlPoints);
					// edges are created so release the path
					edgeToolCtrlPoints.clear();
				}
				
				// repaint the graph to show path
				graphPanel.repaint();
			}
		}
		else if(selTool == Tool.CURSOR && selectionType != SelectionType.NONE && !vertexMoved) {
			// in edit mode it is only allowed to select object if the cursor tool is always shown
			if(!editable && !showCursorToolAlways)
				return;
			
			final VisualVertex v = internalGetVertexFromPosition(e.getX(), e.getY());
			
			if(v != null && (selectionType == SelectionType.VERTICES_ONLY || selectionType == SelectionType.BOTH))
				selectVertex(v, (e.getModifiers() & InputEvent.CTRL_MASK) != 0);
			else {
				final VisualEdge edge = internalGetEdgeFromPosition(e.getX(), e.getY());
				
				if(edge != null && (selectionType == SelectionType.EDGES_ONLY || selectionType == SelectionType.BOTH))
					selectEdge(edge, (e.getModifiers() & InputEvent.CTRL_MASK) != 0);
			}
			
			// select vertices and edges in the selection area
			if(selAreaRect != null && selAreaRect.width > 0 && selAreaRect.height > 0) {
				// select all vertices and edges inside of the rectangle (redraw is done by the methods) if possible
				if(selectionType == SelectionType.VERTICES_ONLY || selectionType == SelectionType.BOTH)
					selectVertices(getVerticesFromRect(selAreaRect));
				if(selectionType == SelectionType.EDGES_ONLY || selectionType == SelectionType.BOTH)
					selectEdges(getEdgesFromRect(selAreaRect));
			}
			
			graphPanel.repaint();
		}
		// release selection area
		selAreaRect = null;
		
		mouseDown = false;
		vertexToMove = null;
		vertexMoved = false;
	}
	
	/**
	 * Mouse move event when user moves the mouse on the drawing area.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseMove(final MouseEvent e) {
		// graph may not be modified? then exit because nothing may be done here!
		if(editable == false)
			return;
		
		// a vertex or a group of selected vertices may only be moved if the user does not press the CTRL key
		// (this should prevent the user from deselecting the vertices because of moving the cursor)
		if(mouseDown && vertexToMove != null && (e.getModifiers() & InputEvent.CTRL_MASK) == 0) {
			final int diffX = e.getX() - mousePos.x;
			final int diffY = e.getY() - mousePos.y;
			boolean vertexAdjustedToAxisX = false;
			boolean vertexAdjustedToAxisY = false;
			
			// create the list of movable vertices which are the selected ones and if the current vertex at the mouse position
			// is not selected then add this vertex to the list too
			final List<VisualVertex> movableVertices = new ArrayList<VisualVertex>();
			if(!vertexToMove.isSelected()) {
				movableVertices.add(0, vertexToMove);
				deselectAll();
			}
			else
				movableVertices.addAll(selVertices);
			
			for(VisualVertex vv : movableVertices) {
				// compute the new x and y position of the vertex
				int x = vv.getX() + diffX;
				int y = vv.getY() + diffY;
				
				// look for vertices on the same axis but only if there is only one movable vertex
				final VisualVertex vHor = (movableVertices.size() == 1) ? getVertexOnSameHorAxis(vertexToMove, y, vpasTolerance) : null;
				final VisualVertex vVer = (movableVertices.size() == 1) ? getVertexOnSameVerAxis(vertexToMove, x, vpasTolerance) : null;
				
				// adjust the vertex position to the next axis if necessary
				x = (vVer != null) ? vVer.getX() : x;
				y = (vHor != null) ? vHor.getY() : y;
				
				// if a vertex is adjusted via VPAS set the corresponding flag
				if(vVer != null)
					vertexAdjustedToAxisX = true;
				if(vHor != null)
					vertexAdjustedToAxisY = true;
				
				// set new vertex position
				vv.setPosition(x, y);
			}
			
			// if there is only one vertex that was moved then recalculate only the incident edges otherwise recompute
			// all edge positions
			if(movableVertices.size() == 1) {
				for(VisualEdge edge : visualEdges)
					if(edge.dockTo(vertexToMove))
						computeEdgePosition(edge);
				// update all attachment points
				computeVertexAttachmentPoints();
			}
			else
				computeEdgePositions();
			// update the edge labeling because the edge positions might change
			computeDynamicEdgeLabeling();
			
			// the position of a vertex changed that means adjust the drawing area
			adjustDrawingAreaToVertices();
			
			// save current coordinate for next move step
			if(!vertexAdjustedToAxisX)
				mousePos.x = e.getX();
			if(!vertexAdjustedToAxisY)
				mousePos.y = e.getY();
			
			// enable the move flag
			vertexMoved = true;
			
			// repaint the graph to show movement
			graphPanel.repaint();
		}
		else if(edgeToolCtrlPoints.size() > 0) {
			// save current coordinate for next move step
			mousePos.x = e.getX();
			mousePos.y = e.getY();
			
			// repaint the graph to show the current cursor position
			graphPanel.repaint();
		}
		else if(mouseDown && selAreaRect != null) {
			if(e.getX() < mousePos.x) {
				selAreaRect.x = e.getX();
				selAreaRect.width = mousePos.x - e.getX();
			}
			else {
				selAreaRect.x = mousePos.x;
				selAreaRect.width = e.getX() - mousePos.x;
			}
			
			if(e.getY() < mousePos.y) {
				selAreaRect.y = e.getY();
				selAreaRect.height = mousePos.y - e.getY();
			}
			else {
				selAreaRect.y = mousePos.y;
				selAreaRect.height = e.getY() - mousePos.y;
			}

			// repaint the graph to show the current selection area
			graphPanel.repaint();
		}
	}
	
	/**
	 * Mouse double click event when user double clicks onto the drawing area.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseDblClicked(final MouseEvent e) {
		// graph may not be modified or the selected tool is not the cursor? then exit because nothing may be done here!
		if(editable == false || selTool != Tool.CURSOR)
			return;
		
		showProperties();
	}
	
	/**
	 * Adds a new visual vertex.
	 * 
	 * @param vv the visual vertex
	 * @param x the x position
	 * @param y the y position
	 * @since 1.0
	 */
	private void addVisualVertex(final VisualVertex vv, final int x, final int y) {
		vv.setPosition(x, y);
		vv.setIndex(visualVertices.size());
		visualVertices.add(vv);
		visualVerticesByID.put(vv.getVertex().getID(), vv);
	}
	
	/**
	 * Adds a new visual edge.
	 * 
	 * @param ve the visual edge
	 * @since 1.0
	 */
	private void addVisualEdge(final VisualEdge ve) {
		ve.setIndex(visualEdges.size());
		visualEdges.add(ve);
		visualEdgesByID.put(ve.getEdge().getID(), ve);
	}
	
	/**
	 * Updates the visibility of the toolbar buttons.
	 * 
	 * @since 1.0
	 */
	private void updateToolBarButtonVisibility() {
		// update the visibility state of the toolbar buttons
		cursorBtn.setVisible(editable || showCursorToolAlways);
		addVertexBtn.setVisible(editable && !hideGraphToolsAlways);
		addEdgeBtn.setVisible(editable && !hideGraphToolsAlways);
		deleteObjectBtn.setVisible(editable && !hideGraphToolsAlways);
		showPropertiesBtn.setVisible(editable);
		zoomInBtn.setVisible(editable || showZoomToolsAlways);
		zoomOutBtn.setVisible(editable || showZoomToolsAlways);
		
		// update the visibility state of the separators
		toolBarSeparators[0].setVisible(cursorBtn.isVisible());
		toolBarSeparators[1].setVisible(addEdgeBtn.isVisible());
		toolBarSeparators[2].setVisible(deleteObjectBtn.isVisible());
		toolBarSeparators[3].setVisible(showPropertiesBtn.isVisible());
		
		toolBar.setVisible(isToolbarVisible());
	}
	
	/**
	 * Sets the editable state of the graph view meaning whether the user can edit the graph that is displayed in the view
	 * or not.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The cursor button and the zoom in/out buttons remain unaffected by the editable state. If you want to disable/enable this buttons use
	 * {@link #setShowCursorToolAlways(boolean)}/{@link #setShowZoomToolsAlways(boolean)}.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @see #hasRestorableEditMode()
	 * @param editable <code>true</code> if user may modify the graph otherwise <code>false</code>
	 * @since 1.0
	 */
	private void internalSetEditable(final boolean editable) {
		this.editable = editable;
		
		// restore the previous edit mode if possible otherwise begin scene record if necessary
		if(!editable && restorableEditMode && nonEditableScene == null) {
			nonEditableScene = new GraphScene<V, E>(this);
			nonEditableScene.begin();
		}
		else if(editable && restorableEditMode && nonEditableScene != null) {
			nonEditableScene.end(false);
			// only reverse the scene when the graph data structure does not changed
			if(!graphDataStructureChanged)
				nonEditableScene.reverse();
			nonEditableScene = null;
		}
		
		// set the visibility of the toolbar buttons
		updateToolBarButtonVisibility();
		
		// select the cursor tool if the view is not editable anymore
		if(!editable)
			selectTool(Tool.CURSOR);
		
		// reset change flag
		graphDataStructureChanged = false;
	}
	
	/**
	 * Sets the current zoom value in percent.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param z the zoom, like 100%, 50%, 150%, ...
	 * @since 1.0
	 */
	private void internalSetZoom(final int z) {
		if(z < 1)
			return;
		
		lastZoom = zoom;
		zoom = z;
		
		// adjust the radius of the vertex circles
		radiusOfVertexF = (radiusOfVertexF / lastZoom) * zoom;
		radiusOfVertex = (int)radiusOfVertexF;
		
		// adjust the offset distance and arrow length of edges
		currEdgeOffsetDistanceF = (currEdgeOffsetDistanceF / lastZoom) * zoom;
		currEdgeOffsetDistance = (int)currEdgeOffsetDistanceF;
		loopOffsetDistanceF = (loopOffsetDistanceF / lastZoom) * zoom;
		loopOffsetDistance = (int)loopOffsetDistanceF;
		edgeArrowLengthF = (edgeArrowLengthF / lastZoom) * zoom;
		edgeArrowLength = (int)edgeArrowLengthF;
		
		// adjust the font size
		fontSizeF = (fontSizeF / lastZoom) * zoom;
		font = font.deriveFont(fontSizeF);
		
		// zoom changed that means recalculation of the vertex and edge positions
		adjustPositionsToZoom = true;
		
		// display the zoom value as a title extension
		if(zoom != 100)
			extendTitle(" (" + zoom + "%)");
		else
			extendTitle("");
	}
	
	/**
	 * Gets the visual vertex with the given caption.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param caption the caption of the vertex its visual component is searched
	 * @return the visual vertex or <code>null</code> if the graph view has no visual vertex with the specified caption
	 * @since 1.0
	 */
	private VisualVertex internalGetVisualVertexByCaption(final String caption) {
		for(VisualVertex v : visualVertices)
			if(v.getVertex().getCaption().equals(caption))
				return v;
		
		return null;
	}
	
	/**
	 * Deselects all selected vertices and edges, that means no object is selected anymore.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @since 1.0
	 */
	private void internalDeselectAll() {
		for(VisualVertex v : visualVertices)
			v.setSelected(false);
		for(VisualEdge e : visualEdges)
			e.setSelected(false);
		
		selVertices.clear();
		selEdges.clear();
		
		// clear the keyboard input string because the selection changed
		keyTypedString = "";
	}
	
	/**
	 * Sets the graph of the graph view.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * For each vertex and edge of the graph a visual component will be created. To position the vertices you should
	 * call {@link #layoutGraph(GraphLayout)} otherwise existing vertices will be positioned at (0,0).
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @see #layoutGraph(GraphLayout)
	 * @param graph a <b>valid</b> graph
	 * @since 1.0
	 */
	private void internalSetGraph(final Graph<V, E> graph) {
		this.graph = graph;

		// if their already exists an observer from the last graph then first remove the observer because this one is not needed any more
		if(idObserver != null)
			idObserver.remove();
		// create the observer for graph object id modifications
		idObserver = new AccessibleIDObserver(graph) {
			
			@Override
			protected void vertexIDModified(int oldID, int newID) {
				final VisualVertex vv = GraphView.this.visualVerticesByID.get(oldID);
				if(vv != null)
					GraphView.this.visualVerticesByID.put(newID, vv);
			}
			
			@Override
			protected void edgeIDModified(int oldID, int newID) {
				final VisualEdge ve = GraphView.this.visualEdgesByID.get(oldID);
				if(ve != null)
					GraphView.this.visualEdgesByID.put(newID, ve);
			}
		};
		
		internalDeselectAll();;
		
		// clear the lists of visual components
		visualVertices.clear();
		visualVerticesByID.clear();
		visualEdges.clear();
		visualEdgesByID.clear();
		
		// create a new visual component for each vertex of the graph
		for(int i = 0; i < graph.getOrder(); i++)
			addVisualVertex(new VisualVertex(graph.getVertex(i)), 0, 0);

		// create a new visual component for each edge of the graph
		for(int i = 0; i < graph.getSize(); i++)
			addVisualEdge(new VisualEdge(graph.getEdge(i), getVisualVertex(graph.getEdge(i).getPredecessor()), getVisualVertex(graph.getEdge(i).getSuccessor())));
		
		// compute the initial visual data
		computeEdgePositions();
		computeDynamicEdgeLabeling();
		
		graphDataStructureChanged = true;
	}
	
	/**
	 * Layouts the graph that means all vertices of the graph are automatically positioned by the
	 * layout algorithm specified in the {@link GraphLayout}.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @see #createCircleGraphLayout()
	 * @param graphLayout a <b>valid</b> graph layout to layout the vertices of the graph automatically
	 * @since 1.0
	 */
	private void internalLayoutGraph(final GraphLayout graphLayout) {
		graphLayout.layout(graph, this);
		
		// do recalculation
		adjustDrawingAreaToVertices();
		updateEdgeOffsetIndices();
		computeEdgePositions();
		computeDynamicEdgeLabeling();
		computeVertexAttachmentPoints();
	}
	
	/**
	 * Transfers a graph of another graph view into this one.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * By doing that the current graph will be emptied.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param gtp a <b>valid</b> graph transfer protocol
	 * @since 1.0
	 */
	private <N extends Vertex, L extends Edge> void internalTransferGraph(final GraphTransferProtocol<N, L> gtp) {
		GraphTransferProtocol<N, L>.TransferData td;
		N v;
		VisualVertex vv;
		VisualEdge ve;
		VisualVertex predecessor;
		VisualVertex successor;
		
		// if the current data should be cleared then remove the edges and vertices from the graph
		if(gtp.getClearExistingGraph()) {
			removeAllEdges();
			removeAllVertices();
		}
		
		// transfer all vertices
		for(int i = 0; i < gtp.getGraph().getOrder(); i++) {
			v = gtp.getGraph().getVertex(i);
			td = gtp.getVertexTransferData().get(i);
			// no valid transfer data? then continue with the next vertex!
			if(td == null)
				continue;
			
			vv = getVisualVertexByCaption(v.getCaption());
			// create a visual equivalent and transfer the properties but only if a visual equivalent is not already existing
			if(vv == null)
				vv = createVisualVertex(0, 0);
			if(vv != null)
				vv.deserialize(td);
		}
		
		// transfer all edges
		for(int i = 0; i < gtp.getGraph().getSize(); i++) {
			td = gtp.getEdgeTransferData().get(i);
			// no valid transfer data? then continue with the next edge!
			if(td == null)
				continue;
			
			// get visual predecessor and successor of this graph that means we search by caption because
			// the original predecessor and successor are in the transfered graph
			predecessor = getVisualVertexByCaption(gtp.getGraph().getEdge(i).getPredecessor().getCaption());
			successor = getVisualVertexByCaption(gtp.getGraph().getEdge(i).getSuccessor().getCaption());
			
			// create a visual equivalent and transfer the properties
			ve = createVisualEdge(predecessor, successor);
			if(ve != null)
				ve.deserialize(td);
		}
		
		// restore the zoom so that the font, etc. is adjusted and clear the adjustment flag because the transferred vertex
		// positions are already zoomed
		internalSetZoom(gtp.getZoom());
		adjustPositionsToZoom = false;
		
		// do recalculation
		adjustDrawingAreaToVertices();
		updateEdgeOffsetIndices();
		computeEdgePositions();
		computeDynamicEdgeLabeling();
		computeVertexAttachmentPoints();
		
		graphDataStructureChanged = true;
	}
	
	/**
	 * Loads a visual graph from a file.
	 * 
	 * @param filename a <b>valid</b> file name (if the file name does not end with {@link #VISUALGRAPHFILE_EXT} then the extension is added)
	 * @throws IOException
	 * <ul>
	 * 		<li>if graph file could not be loaded</li>
	 * </ul>
	 * @since 1.0
	 */
	private void internalLoad(String filename) throws IOException {
		final ObjectFile oldGraph = createVGF("tmp");
		
		// correct extension?
		if(!filename.toLowerCase().endsWith(VISUALGRAPHFILE_EXT))
			filename += VISUALGRAPHFILE_EXT;
		
		// load graph file
		final ObjectFile of = new ObjectFile(filename);
		of.load();
		
		if(!loadVGFData(of)) {
			// if the data could not be loaded try to restore the old graph
			loadVGFData(oldGraph);
			throw new IOException("Invalid data in file!");
		}
		
		graphDataStructureChanged = true;
	}
	
	/**
	 * Loads the data from a visual graph file.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method invokes {@link #deselectAll()}, {@link #removeAllEdges()} and {@link #removeAllVertices()}.
	 * 
	 * @param of the visual graph file
	 * @return <code>true</code> if the data could be loaded otherwise <code>false</code>
	 * @since 1.0
	 */
	private boolean loadVGFData(final ObjectFile of) {
		VisualVertex v;
		VisualVertex vExist;
		VisualEdge e;
		Serializer s;
		
		// activate loading flag so that the calculations can be done at the end
		loadingVGF = true;
		
		// clear all data (do not use graph.removeAll() and visualVertices/visualEdges.clear() because it could be that some
		// edges/vertices are not allowed to be removed)
		deselectAll();
		removeAllEdges();
		removeAllVertices();
		
		// load all vertices
		for(int i = 0; i < of.getSerializerCount("vertex"); i++) {
			// add a new visual vertex
			v = createVisualVertex(0, 0);
			// get a serializer for the vertex
			s = of.getSerializer(v, "vertex");
			
			// check if there is already another vertex with the caption stored in the current serializer
			vExist = getVisualVertexByCaption(s.getString("caption"));
			if(vExist != null && vExist != v) {
				// there is another vertex with the caption of the currently loaded vertex so
				// remove the loaded one and change the object relation in the file that edges
				// have a valid anchor
				removeVisualVertex(v);
				of.updateObject(s.getID(), vExist);
				v = vExist;
			}
			
			if(v != null) {
				// deserialize vertex
				v.deserialize(s);
			}
		}
		
		// load all edges
		for(int i = 0; i < of.getSerializerCount("edge"); i++) {
			// get serializer by a dummy edge
			s = of.getSerializer(new VisualEdge(), "edge");
			
			// get the vertices of the edge
			@SuppressWarnings("unchecked")
			VisualVertex v1 = (VisualVertex)of.getObject(s.getInt("vertex1"));
			@SuppressWarnings("unchecked")
			VisualVertex v2 = (VisualVertex)of.getObject(s.getInt("vertex2"));
			
			if(v1 == null || v2 == null)
				return false;
			
			// add new edge
			e = createVisualEdge(v1, v2);
			
			if(e != null) {
				// deserialize edge
				e.deserialize(s);
			}
		}
		
		// load view data
		deserialize(of.getSerializer(this, "graphView"));
		
		// deactivate the loading flag
		loadingVGF = false;
		
		// recalculate edge positions delete zoom flag because the positions of the vertices are already zoomed
		computeEdgePositions();
		updateEdgeOffsetIndices();
		adjustDrawingAreaToVertices();
		adjustPositionsToZoom = false;
		
		return true;
	}
	
	/**
	 * Saves the visual graph to a file.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @see #resetVisualAppearance()
	 * @param filename a <b>valid</b> file name (if the file name does not end with {@link #VISUALGRAPHFILE_EXT} then the extension is added)
	 * @throws IOException
	 * <ul>
	 * 		<li>if graph file could not be saved</li>
	 * </ul>
	 * @since 1.0
	 */
	private void internalSave(final String filename) throws IOException {
		final ObjectFile of = createVGF(filename);
		
		// save visual graph
		of.save();
	}
	
	/**
	 * Creates a visual graph file of the current graph in the view.
	 * 
	 * @param filename the file name of the file
	 * @return the vgf
	 * @since 1.0
	 */
	private ObjectFile createVGF(String filename) {
		// clear selection
		deselectAll();
		
		Serializer s;
		
		// correct extension?
		if(!filename.toLowerCase().endsWith(VISUALGRAPHFILE_EXT))
			filename += VISUALGRAPHFILE_EXT;
		
		// save graph file
		final ObjectFile of = new ObjectFile(filename);
		
		// serialize all visual vertices and edges
		for(VisualVertex v : visualVertices)
			v.serialize(of.getSerializer(v, "vertex"));
		for(VisualEdge e : visualEdges) {
			s = of.getSerializer(e, "edge");
			e.serialize(s);
			
			// add the id's of the connected vertices
			s.addInt("vertex1", of.getSerializer(e.getPredecessor(), "vertex").getID());
			s.addInt("vertex2", of.getSerializer(e.getSuccessor(), "vertex").getID());
		}
		
		// finally serialize the graph view
		serialize(of.getSerializer(this, "graphView"));
		
		
		return of;
	}
	
	/**
	 * Saves the graph as an image in the PNG format.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param filename a <b>valid</b> file name (if the file name does not end with ".png" then the extension is added)
	 * @throws IOException
	 * <ul>
	 * 		<li>if graph image could not be saved</li>
	 * </ul>
	 * @since 1.0
	 */
	private void internalSaveAsPNG(String filename) throws IOException {
		// clear selection
		deselectAll();
		
		if(!filename.toLowerCase().endsWith(".png"))
			filename += ".png";
		
		final Dimension imgSize = graphPanel.getSize();
		final BufferedImage bufImg = new BufferedImage(imgSize.width, imgSize.height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2d = bufImg.createGraphics();
		
		// fill white background
		g2d.setBackground(Color.white);
		g2d.fill(new Rectangle(imgSize));
		
		// draw graph
		paint(g2d, false);
		
		// save image
		ImageIO.write(bufImg, "png", FileUtils.createFilePath(filename));
	}
	
	/**
	 * Sets the default visual appearance for all vertices and edges.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @since 1.0
	 */
	private void internalResetVisualAppearance() {
		for(VisualVertex v : visualVertices) {
			v.setBackground(DEF_VERTEXBACKGROUND);
			v.setForeground(DEF_VERTEXFOREGROUND);
			v.setEdgeWidth(DEF_VERTEXEDGEWIDTH);
		}
		
		for(VisualEdge e : visualEdges) {
			e.setColor(DEF_EDGECOLOR);
			e.setLineWidth(DEF_EDGELINEWIDTH);
		}
	}
	
	/**
	 * Gets a vertex from the given position.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @return the vertex under the position or <code>null</code> if there is no vertex
	 * @since 1.0
	 */
	private VisualVertex internalGetVertexFromPosition(final int x, final int y) {
		VisualVertex v;
		int radius;
		int dX;
		int dY;
		
		for(int i = visualVertices.size() - 1; i >= 0; i--) {
			v = visualVertices.get(i);
			radius = internalGetScaledVertexRadius(v);
			dX = v.getX() - x;
			dY = v.getY() - y;
			
			if(dX*dX + dY*dY <= radius*radius)
				return v;
		}
		
		return null;
	}
	
	/**
	 * Gets an edge from the given position.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @return the edge under the position or <code>null</code> if there is no edge
	 * @since 1.0
	 */
	private VisualEdge internalGetEdgeFromPosition(final int x, final int y) {
		for(VisualEdge e : visualEdges)
			if(e.getSpline().contains(x, y, SELECT_EDGE_TOLARANCE))
				return e;
		
		return null;
	}
	
	/**
	 * Gets the scaled radius of the given vertex.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param v the vertex
	 * @return the radius of the vertex
	 * @since 1.0
	 */
	private int internalGetScaledVertexRadius(final VisualVertex v) {
		if(v == null)
			return 0;
		
		if(v.getScale() == 1.0f)
			return radiusOfVertex;	// avoid calculation and casting if there is nothing to scale
		else
			return (int)(radiusOfVertex * v.getScale());
	}
	
	/**
	 * Gets a vertex name that is currently not used by another vertex.
	 * 
	 * @return a possible name of a vertex
	 * @since 1.0
	 */
	private String getFreeVertexName() {
		boolean captionExists;
		
		for(int i = 1;; i++) {
			captionExists = false;
			
			for(VisualVertex v : visualVertices) {
				if(v.getVertex().getCaption().equalsIgnoreCase("" + i)) {
					captionExists = true;
					break;
				}
			}
			
			if(!captionExists)
				return "" + i;
		}
	}
	
	/**
	 * Creates edges between vertices based on the positions in the given path.
	 * <br><br>
	 * If there is a position where no vertex could be find, this position is skipped.
	 * 
	 * @param path the path
	 * @since 1.0
	 */
	private void createEdgesByPath(final List<Point> path) {
		Point p;
		VisualVertex v;
		VisualVertex lastValidVertex = null;
		
		for(int i = 0; i < path.size(); i++) {
			p = path.get(i);
			v = internalGetVertexFromPosition(p.x, p.y);
			
			if(v != null) {
				if(lastValidVertex != null)
					createVisualEdge(lastValidVertex, v);
				
				lastValidVertex = v;
			}
		}
	}
	
	/**
	 * Gets a list of vertices that are inside of the given rectangle.
	 * 
	 * @param rect the rectangle
	 * @return the list of vertices
	 * @since 1.0
	 */
	private List<VisualVertex> getVerticesFromRect(final Rectangle rect) {
		final List<VisualVertex> res = new ArrayList<VisualVertex>();
		
		for(VisualVertex v : visualVertices) {
			if(rect.contains(v.getX(), v.getY()))
				res.add(v);
		}
		
		return res;
	}
	
	/**
	 * Gets a vertex that is located on the same horizontal axis deducting a tolerance value.
	 * 
	 * @param v the visual vertex
	 * @param y the y position of the vertex
	 * @param tolerance the tolerance to the axis
	 * @return the vertex that is deducting the tolerance on the same hor. axis as the specified vertex or <code>null</code> if there is no other vertex on the same hor. axis
	 * @since 1.0
	 */
	private VisualVertex getVertexOnSameHorAxis(final VisualVertex v, final int y, final int tolerance) {
		for(VisualVertex u : visualVertices) {
			// the given vertex may not be checked because he is obviously on the same axis
			if(u == v)
				continue;
			
			// both vertices have an horizontal difference less than the tolerance?
			// then we have found a vertex on the same hor. axis!s
			if(Math.abs(u.getY() - y) <= tolerance)
				return u;
		}
		
		return null;
	}
	
	/**
	 * Gets a vertex that is located on the same vertical axis deducting a tolerance value.
	 * 
	 * @param v the visual vertex
	 * @param x the x position of the vertex
	 * @param tolerance the tolerance to the axis
	 * @return the vertex that is deducting the tolerance on the same ver. axis as the specified vertex or <code>null</code> if there is no other vertex on the same ver. axis
	 * @since 1.0
	 */
	private VisualVertex getVertexOnSameVerAxis(final VisualVertex v, final int x, final int tolerance) {
		for(VisualVertex u : visualVertices) {
			// the given vertex may not be checked because he is obviously on the same axis
			if(u == v)
				continue;
			
			// both vertices have an vertical difference less than the tolerance?
			// then we have found a vertex on the same ver. axis!s
			if(Math.abs(u.getX() - x) <= tolerance)
				return u;
		}
		
		return null;
	}
	
	/**
	 * Gets a list of edges that are inside of the given rectangle.
	 * 
	 * @param rect the rectangle
	 * @return the list of edges
	 * @since 1.0
	 */
	private List<VisualEdge> getEdgesFromRect(final Rectangle rect) {
		final List<VisualEdge> res = new ArrayList<VisualEdge>();
		
		for(VisualEdge e : visualEdges) {
			if(rect.contains(e.getX1(), e.getY1()) && rect.contains(e.getX2(), e.getY2()))
				res.add(e);
		}
		
		return res;
	}
	
	/**
	 * Selects a visual vertex.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param vthe vertex
	 * @param multiSelect <code>true</code> if multi select should be used (that means more than one vertex or edge can be selected) otherwise <code>false</code>
	 * @param changeState <code>true</code> if the vertex should be deselected if he is selected
	 * @since 1.0
	 */
	private void internalSelectVertex(final VisualVertex v, final boolean multiSelect, final boolean changeState) {
		if(!multiSelect)
			deselectAll();
		
		v.setSelected(!changeState || !v.isSelected());
		
		if(v.isSelected()) {
			selVertices.add(v);
			// notify listeners
			fireVertexSelected(v);
		}
		else
			selVertices.remove(v);
		
		// clear the keyboard input string because the selection changed
		keyTypedString = "";
	}
	
	/**
	 * Selects a visual edge.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param e the edge
	 * @param multiSelect <code>true</code> if multi select should be used (that means more than one vertex or edge can be selected) otherwise <code>false</code>
	 * @param changeState <code>true</code> if the edge should be deselected if she is selected
	 * @since 1.0
	 */
	private void internalSelectEdge(final VisualEdge e, final boolean multiSelect, final boolean changeState) {
		if(!multiSelect)
			deselectAll();
		
		e.setSelected(!changeState || !e.isSelected());
		
		if(e.isSelected()) {
			selEdges.add(e);
			// notify listeners
			fireEdgeSelected(e);
		}
		else
			selEdges.remove(e);
		
		// clear the keyboard input string because the selection changed
		keyTypedString = "";
	}
	
	/**
	 * Checks the visibility of all vertices, that means if the vertex is currently visible (in the visible area
	 * of the graph).
	 * 
	 * @since 1.0
	 */
	private void checkVisibilityOfVertices() {
		final Rectangle viewPortRect = scrollPane.getViewport().getViewRect();
		int radius;
		int vf;
		
		for(VisualVertex v : visualVertices) {
			// get the specific radius of the current vertex
			radius = internalGetScaledVertexRadius(v);
			// reset current flags
			vf = 0;
			
			// check if vertex is outside of the visible area of the drawing panel
			if(v.getX() + radius < viewPortRect.x)
				vf = vf | VisualVertex.VF_HIDDEN_OOB_LEFT;
			if(v.getX() - radius > viewPortRect.x + viewPortRect.width)
				vf = vf | VisualVertex.VF_HIDDEN_OOB_RIGHT;
			if(v.getY() + radius < viewPortRect.y)
				vf = vf | VisualVertex.VF_HIDDEN_OOB_TOP;
			if(v.getY() - radius > viewPortRect.y + viewPortRect.height)
				vf = vf | VisualVertex.VF_HIDDEN_OOB_BOTTOM;
			
			// no flags set then vertex is visible otherwise set the flags
			v.setVisibilityFlags((vf == 0) ? VisualVertex.VF_VISIBLE : vf);
		}
	}
	
	/**
	 * Indicates if the given vertex is completely visible in the current display area of the graph.
	 * 
	 * @param v the vertex
	 * @return <code>true</code> if the vertex rectangle is entirely enclosed by the viewport otherwise <code>false</code>
	 * @since 1.0
	 */
	private boolean isVertexInViewport(final VisualVertex v) {
		final Rectangle viewPortRect = scrollPane.getViewport().getViewRect();
		final int radius = internalGetScaledVertexRadius(v);
		final Rectangle vertexRect = new Rectangle(v.getX() - radius, v.getY() - radius, radius*2, radius*2);
		
		// checks if the vertex rectangle is entirely enclosed by the viewport
		return viewPortRect.contains(vertexRect);
	}
	
	/**
	 * Ensures that the given vertex is in the visible area of the graph.
	 * 
	 * @param v the vertex
	 * @since 1.0
	 */
	private void ensureVertexVisibility(final VisualVertex v) {
		// vertex is completely visible? then avoid scrolling!
		if(isVertexInViewport(v))
			return;
		
		final Rectangle viewPortRect = scrollPane.getViewport().getViewRect();
		
		// scroll the viewport so that the vertex is in the middle
		scrollPane.getViewport().setViewPosition(new Point(v.getX() - viewPortRect.width / 2, v.getY() - viewPortRect.height / 2));
	}
	
	/**
	 * Indicates if the given edge is completely visible in the current display area of the graph.
	 * 
	 * @param e the edge
	 * @return <code>true</code> if the edge points are in the viewport otherwise <code>false</code>
	 * @since 1.0
	 */
	private boolean isEdgeInViewport(final VisualEdge e) {
		final Rectangle viewPortRect = scrollPane.getViewport().getViewRect();
		
		// an edge is completely visible if all three points of the edge are visible
		return viewPortRect.contains(e.getPredecessor().getX(), e.getPredecessor().getY()) &&
			   viewPortRect.contains(e.getSupportX(), e.getSupportY()) &&
			   viewPortRect.contains(e.getSuccessor().getX(), e.getSuccessor().getY());
	}

	
	/**
	 * Ensures that the given edge is in the visible area of the graph.
	 * 
	 * @param e the edge
	 * @since 1.0
	 */
	private void ensureEdgeVisibility(final VisualEdge e) {
		// edge is completely visible? then avoid scrolling!
		if(isEdgeInViewport(e))
			return;
		
		final Rectangle viewPortRect = scrollPane.getViewport().getViewRect();
		
		// scroll the viewport so that the support point of the edge is in the middle
		scrollPane.getViewport().setViewPosition(new Point(e.getSupportX() - viewPortRect.width / 2, e.getSupportY() - viewPortRect.height / 2));
	}
	
	/**
	 * Indicates if the toolbar is visible or not.
	 * 
	 * @return <code>true</code> if toolbar is visible otherwise <code>false</code>
	 * @since 1.0
	 */
	private boolean isToolbarVisible() {
		return editable || showZoomToolsAlways || showCursorToolAlways;
	}
	
	/**
	 * Updates the offset indices of all edges. The offset index describes how much the
	 * edge is away from the center between the two connected vertices.
	 * <br><br>
	 * This is used when there are more than one edge between two nodes.
	 * 
	 * @since 1.0
	 */
	private void updateEdgeOffsetIndices() {
		VisualEdge ve;
		int oldOffsetIndex;
		List<EdgeOffset> edgesToUpdate;
		boolean edgePositionsRecalculated = false;
		
		// for all vertices v element VertexSet
		for(VisualVertex v : visualVertices) {
			// for all vertices u element VertexSet
			for(VisualVertex u : visualVertices) {
				/*
				 * do not ignore equal vertices (i.e. v == u) because loops must also be included
				 * into the offset index calculation!
				 */
				
				edgesToUpdate = requestOffsetEdges(v, u);
				if(edgesToUpdate == null)
					continue;
				
				// set the offset index for every edge that goes from v -> u
				for(EdgeOffset eo : edgesToUpdate) {
					ve = visualEdgesByID.get(eo.edge.getID());
					
					if(ve != null) {
						oldOffsetIndex = ve.getOffsetIndex();
						ve.setOffsetIndex(eo.offset);
						
						// the offset has changed? then recalculate the position (because the support point is translated)
						if(oldOffsetIndex != eo.offset) {
							computeEdgePosition(ve);
							edgePositionsRecalculated = true;
						}
					}
				}
			}
		}
		
		if(edgePositionsRecalculated)
			computeVertexAttachmentPoints();
	}
	
	/**
	 * Computes the positions of all edges. Furthermore {@link #computeDynamicEdgeLabeling()} and {@link #computeVertexAttachmentPoints()}
	 * are invoked.
	 * 
	 * @since 1.0
	 */
	private void computeEdgePositions() {
		for(VisualEdge e : visualEdges)
			computeEdgePosition(e);
		
		computeDynamicEdgeLabeling();
		computeVertexAttachmentPoints();
	}
	
	/**
	 * Computes the positions of the given edge.
	 * 
	 * @param e the edge
	 * @since 1.0
	 */
	private void computeEdgePosition(final VisualEdge e) {
		int cosMultiR;
		int sinMultiR;
		double sqrt;
		int dX;
		int dY;
		int x1;
		int y1;
		int x2;
		int y2;
		float v_x;
		float v_y;
		float u_x;
		float u_y;
		double s;
		int distance;
		int direction;
		int radius;
		
		if(!e.getEdge().isLoop()) {
			/*
			 * Calculation of the support point:
			 * Example:
			 *   |
			 * 3 |           * p2
			 *   |    p .   /
			 * 2 |         /
			 *   |        /
			 * 1 |       * p1
			 *   |___________________________
			 *       1   2   3   4   5
			 * Let P1( x1 | y1 ) and P2( x2 | y2 ) be the centers of the circles. We want to find a Point P( x | y )
			 * that has a distance of d pixels away from the line that is described by P1 and P2.
			 * 
			 * 1. Calculate a vector vec(v) = (x y) that goes from P1 to the center of the line. This vector is defined as:
			 *    vec(v) = ((x2-x1)/2 (y2-y1)/2).
			 * 2. Calculate a vector vec(u) that is orthogonal to vec(v). Let vec(v) = (x y) then a vector that is orthogonal to vec(v) is
			 *    vec(u) = (y -x) (remember that vec(v)*vec(u) = 0, another one could be vec(u') = (-y x)).
			 *    ==> the orthonogal vector vec(u) decides how the offset of the edge is applied (top or bottom orientated)
			 * 3. Calculate a scalar for vec(u) so that the length of vec(u) is d:
			 *    Let vec(u) = (a b) and d := |vec(u)|.
			 *    |s|*|vec(u)| = d <=> |vec(s*a s*b)| = d <=> sqrt((s*a)^2 + (s*b)^2) = d |^2 <=> (s*a)^2 + (s*b)^2 = d^2 <=>
			 *    (a^2 + b^2)*s^2 = d^2 | /(a^2 + b^2) <=> s^2 = d^2 / (a^2 + b^2) | sqrt <=> s = sqrt(d^2 / (a^2 + b^2))
			 * 4. Calculate the point P:
			 *    Let vec(p) be the vector to P and vec(p1) be the vector to P1. then vec(p) = vec(p1) + vec(v) + s*vec(u) and this is
			 *    equivalent to:
			 *    x = x1 + x + s*a
			 *    y = y1 + y + s*b
			 *    (with vec(v) = (x y) and vec(u) = (a b))
			 */
			distance = currEdgeOffsetDistance * e.getOffsetIndex();
			direction = (e.getOffsetIndex() < 0) ? -1 : 1;
			x1 = e.getPredecessor().getX();
			y1 = e.getPredecessor().getY();
			x2 = e.getSuccessor().getX();
			y2 = e.getSuccessor().getY();
			v_x = (x2 - x1) / 2;
			v_y = (y2 - y1) / 2;
			u_x = v_y;
			u_y = -v_x;
			s = direction * Math.sqrt((distance*distance) / (u_x*u_x + u_y*u_y));
			
			e.setSupportX((int)(x1 + v_x + s*u_x));
			e.setSupportY((int)(y1 + v_y + s*u_y));
		}
		else {
			radius = internalGetScaledVertexRadius(e.getPredecessor());
			// a loop is always displayed at the top of a vertex
			e.setSupportX(e.getPredecessor().getX());
			e.setSupportY(e.getPredecessor().getY() - radius - Math.abs(e.getOffsetIndex()) * currEdgeOffsetDistance);
		}
		
		/*
		 * A point P on an unit circle (that means the center of circle is in the origin and radius = 1) is defined as:
		 * P( cos(alpha) | sin(alpha) ).
		 * Let r be the radius of the circle then a more general definition of P is:
		 * P( cos(alpha)*r | sin(alpha)*r ).
		 * Finally a circle has not always the origin as center of circle, that means a point P on a circle
		 * with a center of M( x | y ) and a radius r is at:
		 * P( x + cos(alpha)*r | x + sin(alpha)*r ).
		 * 
		 * Remember:
		 * sin(alpha) = (opposite / hypotenuse),
		 * cos(alpha) = (adjacent / hypotenuse).
		 * 
		 * Now we have two circle center points M1( x1 | y1 ) and M2( x2 | y2 ) and we want to draw a line
		 * from the edge of circle 1 to the edge of circle 2:
		 * P1( x1 + ((y2 - y1)/sqrt((x2-x1)^2 + (y2-y1)^2))*r | y1 + ((x2 - x1)/sqrt((x2-x1)^2 + (y2-y1)^2))*r ),
		 * P2( x2 + ((y1 - y2)/sqrt((x2-x1)^2 + (y2-y1)^2))*r | y2 + ((x1 - x2)/sqrt((x2-x1)^2 + (y2-y1)^2))*r ).
		 * 
		 * Because y2 - y1 = -(y1 - y2) (the same is true for x) we can also write:
		 * P1( x1 + ((y2 - y1)/sqrt((x2-x1)^2 + (y2-y1)^2))*r | y1 + ((x2 - x1)/sqrt((x2-x1)^2 + (y2-y1)^2))*r ),
		 * P2( x2 - ((y2 - y1)/sqrt((x2-x1)^2 + (y2-y1)^2))*r | y2 - ((x2 - x1)/sqrt((x2-x1)^2 + (y2-y1)^2))*r ).
		 * 
		 * --> to optimize the calculation we can pre-calculate:
		 * dX = x2 - x1
		 * dY = y2 - y1
		 * sqrt = sqrt(dX^2 + dY^2)
		 * cos(alpha)*r = (dX / sqrt) * r
		 * sin(alpha)*r = (dY / sqrt) * r
		 */
		
		// calculate the scaled radius of the predecessor vertex
		radius = internalGetScaledVertexRadius(e.getPredecessor());
		
		// calculate circle point for predecessor
		dX = e.getSupportX() - e.getPredecessor().getX();
		dY = e.getSupportY() - e.getPredecessor().getY();
		sqrt = Math.sqrt(dX*dX + dY*dY);
		cosMultiR = (int)((dX / sqrt) * (radius + e.getPredecessor().getEdgeWidth() + 1));	// +1 for offset correction
		sinMultiR = (int)((dY / sqrt) * (radius + e.getPredecessor().getEdgeWidth() + 1));	// +1 for offset correction
		e.setX1(e.getPredecessor().getX() + cosMultiR);
		e.setY1(e.getPredecessor().getY() + sinMultiR);

		// calculate the scaled radius of the successor vertex
		radius = internalGetScaledVertexRadius(e.getSuccessor());
		
		// calculate circle point for successor
		dX = e.getSuccessor().getX() - e.getSupportX();
		dY = e.getSuccessor().getY() - e.getSupportY();
		sqrt = Math.sqrt(dX*dX + dY*dY);
		cosMultiR = (int)((dX / sqrt) * (radius + e.getSuccessor().getEdgeWidth() + 1));	// +1 for offset correction
		sinMultiR = (int)((dY / sqrt) * (radius + e.getSuccessor().getEdgeWidth() + 1));	// +1 for offset correction
		e.setX2(e.getSuccessor().getX() - cosMultiR);
		e.setY2(e.getSuccessor().getY() - sinMultiR);
		
		// finally create the spline that visually describes the edge 
		e.createSpline();
		
		// set the support point as the default label position
		e.setLabelPosition(e.getSupportX(), e.getSupportY());
	}
	
	/**
	 * Computes the dynamic label positions of all edges.
	 * <br><br>
	 * <b>Dynamic edge labeling</b>:<br>
	 * The dynamic edge labeling tries to find a good position for each label of the edges so that the label does not
	 * collide with another edge or label and furthermore tries to prevent that multiple labels overly.<br>
	 * This is less performant than setting the label to the center of an edge but brings a more esthetic image
	 * of the graph. So you have the option to disable this dynamic calculation if the graphical performance of the
	 * graph view suffers.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It must be guaranteed that all edges have their verified positions.
	 * 
	 * @see #computeEdgePositions()
	 * @since 1.0
	 */
	private void computeDynamicEdgeLabeling() {
		if(!dynamicEdgeLabeling)
			return;
		
		final SplineIntersectionPoint[][] intersectionTable = new SplineIntersectionPoint[visualEdges.size()][visualEdges.size()];
		final boolean[] intersected = new boolean[visualEdges.size()];
		SplineIntersectionPoint intersectionPoint;
		
		// determine all intersections between the edges of the graph
		for(VisualEdge e1 : visualEdges) {
			for(VisualEdge e2: visualEdges) {
				// computation of intersection not possible or already done? then continue with the next edge!
				if(e1 == e2 || intersectionTable[e1.getIndex()][e2.getIndex()] != null)
					continue;
				
				intersectionPoint = e1.getSpline().intersect(e2.getSpline());
				
				// no intersection? then create a dummy intersection point who mark the computation between
				// the two edges as done!
				if(intersectionPoint == null)
					intersectionPoint = new SplineIntersectionPoint(e1.getSpline(), e2.getSpline(), 0, 0, -1, -1);
				else
					intersected[e1.getIndex()] = intersected[e2.getIndex()] = true;
				
				intersectionTable[e1.getIndex()][e2.getIndex()] = intersectionTable[e2.getIndex()][e1.getIndex()] = intersectionPoint;
			}
		}
		
		// compute the label position of each edge
		for(int i = 0; i < visualEdges.size(); i++) {
			final VisualEdge e = visualEdges.get(i);
			
			// edge will not be intersected? so the label position is the support point (center of the edge)
			if(!intersected[e.getIndex()])
				e.setLabelPosition(e.getSupportX(), e.getSupportY());
			else {
				Point2D.Float labelPoint;
				
				// find all valid intersection points
				final List<SplineIntersectionPoint> intersectionPoints = new ArrayList<SplineIntersectionPoint>(5);
				for(int j = 0; j < visualEdges.size(); j++) {
					intersectionPoint = intersectionTable[e.getIndex()][j];
					if(intersectionPoint != null && intersectionPoint.getSegmentIndexOfSpline1() != -1)
						intersectionPoints.add(intersectionPoint);
				}
				
				// if the edge is a straight line (offset index == 0) then we cannot check the segment indices as with
				// a "real" spline because a straight line has only one segment
				if(e.getOffsetIndex() == 0) {
					final Point edgeStartPoint = new Point(e.getX1(), e.getY1());
					final Point edgeEndPoint = new Point(e.getX2(), e.getY2());
					
					// order the intersection points ascending (consider the real points because the edge
					// is represented as a straight line (offset index == 0) and has no multiple segments)
					Collections.sort(intersectionPoints, new Comparator<SplineIntersectionPoint>() {
	
						@Override
						public int compare(SplineIntersectionPoint p1, SplineIntersectionPoint p2) {
							return (int)Point.distanceSq(edgeStartPoint.x, edgeStartPoint.y, p1.x, p1.y) - (int)Point.distanceSq(edgeStartPoint.x, edgeStartPoint.y, p2.x, p2.y);
						}
					});
					
					Point segStartPoint = edgeStartPoint;
					Point segEndPoint = edgeStartPoint;
					Point lastStartPoint = segStartPoint;
					Point currPoint;
					double maxSqDist = 0.0;
					double currSqDist;
					
					// find the segment on the spline that has the greatest distance
					for(int k = 0; k <= intersectionPoints.size(); k++) {
						// get the current segment end point and calculate the segment distance (use only distanceSq(...)
						// because of performance issues since the square distance is enough to decide which segment is the greates one)
						currPoint = (k < intersectionPoints.size()) ? intersectionPoints.get(k) : edgeEndPoint;
						currSqDist = Point.distanceSq(lastStartPoint.x, lastStartPoint.y, currPoint.x, currPoint.y);
						
						// do we have a greater segment? the save the new segment bounds
						if(currSqDist > maxSqDist) {
							segStartPoint = lastStartPoint;
							segEndPoint = currPoint;
							maxSqDist = currSqDist;
						}
						
						lastStartPoint = currPoint;
					}
					
					// the label is positioned in the center of the greatest segment
					labelPoint = new Point2D.Float((segStartPoint.x + segEndPoint.x) / 2, (segStartPoint.y + segEndPoint.y) / 2);
				}
				else {
					// order the intersection points ascending (consider the segments of the spline
					// because the offset index of the edge is > 0)
					Collections.sort(intersectionPoints, new Comparator<SplineIntersectionPoint>() {
	
						@Override
						public int compare(SplineIntersectionPoint p1, SplineIntersectionPoint p2) {
							if(e.getSpline() == p1.getSpline1())
								return p1.getSegmentIndexOfSpline1() - p2.getSegmentIndexOfSpline1();
							else
								return p1.getSegmentIndexOfSpline2() - p2.getSegmentIndexOfSpline2();
						}
					});
					
					int segIndexMin = 0;
					int segIndexMax = 0;
					int lastSegIndex = 0;
					int currSegIndex;
					SplineIntersectionPoint currPoint;
					
					// find the segment on the spline that has the greatest distance
					for(int k = 0; k <= intersectionPoints.size(); k++) {
						currPoint = (k < intersectionPoints.size()) ? intersectionPoints.get(k) : null;
						
						// is the point part of the intersection set?
						if(currPoint != null) {
							// look which segment index is needed
							if(e.getSpline() == currPoint.getSpline1())
								currSegIndex = currPoint.getSegmentIndexOfSpline1();
							else
								currSegIndex = currPoint.getSegmentIndexOfSpline2();
						}
						else
							currSegIndex = e.getSpline().getPoints().length - 1;	// no? then choose the final segment index
						
						// do we have a greater segment then before? then save the new segment indices
						if(currSegIndex - lastSegIndex > segIndexMax - segIndexMin) {
							segIndexMin = lastSegIndex;
							segIndexMax = currSegIndex;
						}
						
						lastSegIndex = currSegIndex;
					}
					
					// the label is positioned in the center of the greatest segment
					labelPoint = e.getSpline().getPoints()[(segIndexMin + segIndexMax) / 2];
				}
				
				e.setLabelPosition((int)labelPoint.x, (int)labelPoint.y);
			}
		}
	}
	
	/**
	 * Computes the attachment points of all vertices.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The incident edges of the given visual vertex have to be computed first otherwise the calculated point is invalid.
	 * 
	 * @since 1.0
	 */
	private void computeVertexAttachmentPoints() {
		for(VisualVertex vv : visualVertices)
			computeVertexAttachmentPoint(vv);
	}
	
	/**
	 * Computes the attachment point of the specified vertex.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The incident edges of the given visual vertex have to be computed first otherwise the calculated point is invalid.
	 * 
	 * @param vv the visual vertex its attachment point should be calculated
	 * @since 1.0
	 */
	private void computeVertexAttachmentPoint(final VisualVertex vv) {
		final int radius = internalGetScaledVertexRadius(vv);
		final V v = vv.getVertex();
		VisualEdge ve;
		int x2;
		int y2;
		
		// set default attachment position to the bottom center of the vertex
		if(v.getIncidentEdgeCount() < 1)
			vv.setAttachmentPosition(vv.getX(), vv.getY() + radius);
		else if(v.getIncidentEdgeCount() < 2) {
			ve = visualEdgesByID.get(v.getIncidentEdge(0).getID());
			if(ve != null) {
				// get the endpoint of the current edge on the current vertex
				x2 = (ve.getEdge().getPredecessor() == v) ? ve.getX1() : ve.getX2();
				y2 = (ve.getEdge().getPredecessor() == v) ? ve.getY1() : ve.getY2();
				
				// if there is only one incident edge the attachment point should lie at the opposite of the edge endpoint
				// vec(a) = vec(vertex_center) - vec(edge_endpoint), vec(attachment_point) = vec(edge_endpoint) + 2*vec(a)
				vv.setAttachmentPosition(x2 + (2 * (vv.getX() - x2)), y2 + (2 * (vv.getY() - y2)));
			}
		}
		else {
			/*
			 * An attachment point is the point of a vertex in the center of the largest arc between two edges:
			 *     *__    * endpoints of incident edges
			 *     /  \,--- attachment point
			 *     \__/
			 *     *
			 * 
			 * Calculation:
			 * 1. take an origin which is the vector of the first incident edge
			 * 2. calculate the angles between the origin and all other points on the vertex circle
			 * 3. order these angles ascending
			 * 4. find the largest arc using the order angles
			 * 5. calculate a middle vector between the enclosing vectors of the arc
			 */
			
			// get the first edge that is the origin
			ve = visualEdgesByID.get(v.getIncidentEdge(0).getID());
			// get the endpoint of the first edge on the current vertex
			final int origin_x2 = (ve.getEdge().getPredecessor() == v) ? ve.getX1() : ve.getX2();
			final int origin_y2 = (ve.getEdge().getPredecessor() == v) ? ve.getY1() : ve.getY2();
			// define the origin vector that is used to calculate the intermediate angles
			final int vec_origin_x = origin_x2 - vv.getX();
			final int vec_origin_y = origin_y2 - vv.getY();
			final double vec_origin_len = Math.sqrt(vec_origin_x * vec_origin_x + vec_origin_y * vec_origin_y);
			final List<AttachmentPointData> entries = new ArrayList<AttachmentPointData>(v.getIncidentEdgeCount());
			int vec_x2;
			int vec_y2;
			double angle;
			
			entries.add(new AttachmentPointData(0.0, origin_x2, origin_y2));
			
			for(int i = 1; i < v.getIncidentEdgeCount(); i++) {
				ve = visualEdgesByID.get(v.getIncidentEdge(i).getID());
				if(ve == null)
					continue;
				
				// get the endpoint of the current edge on the current vertex
				x2 = (ve.getEdge().getPredecessor() == v) ? ve.getX1() : ve.getX2();
				y2 = (ve.getEdge().getPredecessor() == v) ? ve.getY1() : ve.getY2();
				// calculate the vector (circle_center to edge_endpoint)
				vec_x2 = x2 - vv.getX();
				vec_y2 = y2 - vv.getY();
				// calculate the angle between the vectors
				angle = Math.toDegrees(Math.acos(((vec_origin_x * vec_x2) + (vec_origin_y * vec_y2)) / (vec_origin_len * Math.sqrt(vec_x2 * vec_x2 + vec_y2 * vec_y2))));
				
				// if the ednpoint of the current edge is at the right side of the origin then we have to take the greater angle
				// because we calculate the angles clockwise
				if(MathUtils.ccw(vv.getX(), vv.getY(), origin_x2, origin_y2, x2, y2) < 0)
					angle = 360.0 - angle;
				
				entries.add(new AttachmentPointData(angle, x2, y2));
			}
			
			// order the entries ascending
			Collections.sort(entries, new Comparator<AttachmentPointData>() {
				
				@Override
				public int compare(AttachmentPointData o1, AttachmentPointData o2) {
					if(o1.angle < o2.angle)
						return -1;
					else if(o1.angle > o2.angle)
						return 1;
					else
						return 0;
				}
			});

			// find the largest arc between two circle points
			AttachmentPointData max_arc_ape1 = null;
			AttachmentPointData max_arc_ape2 = null;
			AttachmentPointData last_ape;
			AttachmentPointData curr_ape;
			double max_arc = 0.0;
			double max_arc_angle = 0.0;
			double arc;
			double curr_angle;
			
			for(int i = 1; i <= entries.size(); i++) {
				last_ape = entries.get(i - 1);
				curr_ape = (i < entries.size()) ? entries.get(i) : entries.get(0);
				curr_angle = (i < entries.size()) ? curr_ape.angle : 360.0;
				arc = Math.PI * radius * ((curr_angle - last_ape.angle) / 180);
				if(arc > max_arc) {
					max_arc = arc;
					max_arc_angle = curr_angle - last_ape.angle;
					max_arc_ape1 = last_ape;
					max_arc_ape2 = curr_ape;
				}
			}
			
			if(max_arc_ape1 == null || max_arc_ape2 == null)
				vv.setAttachmentPosition(vv.getX(), vv.getY());	// this should never happen but to prevent from errors check the case
			else {
				// calculate a middle vector between the two vectors that define the arc
				final int vec_arc_x1 = max_arc_ape1.circlepoint_x - vv.getX();
				final int vec_arc_y1 = max_arc_ape1.circlepoint_y - vv.getY();
				final int vec_arc_x2 = max_arc_ape2.circlepoint_x - vv.getX();
				final int vec_arc_y2 = max_arc_ape2.circlepoint_y - vv.getY();
				int vec_middle_x = vec_arc_x1 + vec_arc_x2;
				int vec_middle_y = vec_arc_y1 + vec_arc_y2;
				
				// if the angle between the points is 180° then determine an orthogonal vector to v1 as the middle vector
				if((vec_middle_x == 0 && vec_middle_y == 0) || (max_arc_angle > MAX_ARC_ANGLE_EPSILONNEG && max_arc_angle < MAX_ARC_ANGLE_EPSILONPOS)) {
					vec_middle_x = -vec_arc_y1;
					vec_middle_y = vec_arc_x1;
				}
				// find a scalar so that the length of the middle vector is equal to the radius
				double s = Math.sqrt((double)(radius * radius) / (vec_middle_x * vec_middle_x + vec_middle_y * vec_middle_y));
				// because we always want to take the larger arc look whether the middle vector is in the larger arc otherwise reverse its direction
				if(MathUtils.ccw(vv.getX(), vv.getY(), max_arc_ape1.circlepoint_x, max_arc_ape1.circlepoint_y, max_arc_ape2.circlepoint_x, max_arc_ape2.circlepoint_y) < 0)
					s *= -1.0;
				
				vv.setAttachmentPosition(vv.getX() + (int)(s * vec_middle_x), vv.getY() + (int)(s * vec_middle_y));
			}
		}
	}
	
	/**
	 * Loads the default and advanced vertex properties.
	 * 
	 * @param plm the properties list model
	 * @param vertex the vertex
	 * @since 1.0
	 */
	private void loadVertexProperties(final PropertiesListModel plm, final V vertex) {
		// load default properties
		final Property propCaption = new TextProperty(vertexPropCaption, "", vertex.getCaption());
		
		// add default properties
		plm.add(propCaption);
		
		// load advanced properties
		loadAdvancedVertexProperties(plm, vertex);
	}
	
	/**
	 * Applies the default and advanced vertex properties.
	 * 
	 * @param plm the properties list model
	 * @param vertex the vertex
	 * @since 1.0
	 */
	private void applyVertexProperties(final PropertiesListModel plm, final V vertex) {
		// apply default properties
		final TextProperty propCaption = plm.getTextProperty(vertexPropCaption);
		if(propCaption != null)
			vertex.setCaption(propCaption.getValue());
		
		// apply advanced properties
		applyAdvancedVertexProperties(plm, vertex);
	}
	
	/**
	 * Loads the default and advanced edge properties.
	 * 
	 * @param plm the properties list model
	 * @param edge the edge
	 * @since 1.0
	 */
	private void loadEdgeProperties(final PropertiesListModel plm, final E edge) {
		String edgePropDirectedDesc;
		
		switch(graph.getType()) {
			case DIRECTED:
				edgePropDirectedDesc = edgePropDirectedDescDG;
				break;
			case UNDIRECTED:
				edgePropDirectedDesc = edgePropDirectedDescUDG;
				break;
			default:
				edgePropDirectedDesc = "";
		}
		
		// load default properties
		final Property propWeight = new NumericProperty(edgePropWeight, "", edge.getWeight());
		final Property propDirected = new BooleanProperty(edgePropDirected, edgePropDirectedDesc, edge.isDirected());
		
		// add default properties
		plm.add(propWeight);
		plm.add(propDirected);
		
		// load advanced properties
		loadAdvancedEdgeProperties(plm, edge);
	}
	
	/**
	 * Applies the default and advanced vertex properties.
	 * 
	 * @param plm the properties list model
	 * @param vertex the vertex
	 * @since 1.0
	 */
	private void applyEdgeProperties(final PropertiesListModel plm, final E edge) {
		// apply default properties
		final NumericProperty propWeight = plm.getNumericProperty(edgePropWeight);
		if(propWeight != null)
			edge.setWeight(propWeight.getValue().floatValue());
		final BooleanProperty propDirected = plm.getBooleanProperty(edgePropDirected);
		if(propDirected != null)
			edge.setDirected(propDirected.getValue());
		
		// apply advanced properties
		applyAdvancedEdgeProperties(plm, edge);
	}
	
	/**
	 * Fires the {@link GraphViewListener#vertexAdded(VisualVertex)} event.
	 * 
	 * @param v the vertex that is added
	 * @since 1.0
	 */
	private void fireVertexAdded(final VisualVertex v) {
		for(GraphViewListener<V, E> l : listeners)
			l.vertexAdded(v);
	}
	
	/**
	 * Fires the {@link GraphViewListener#vertexRemoved(VisualVertex)} event.
	 * 
	 * @param v the vertex that is removed
	 * @since 1.0
	 */
	private void fireVertexRemoved(final VisualVertex v) {
		for(GraphViewListener<V, E> l : listeners)
			l.vertexRemoved(v);
	}
	
	/**
	 * Fires the {@link GraphViewListener#vertexSelected(VisualVertex)} event.
	 * 
	 * @param v the vertex that is selected
	 * @since 1.0
	 */
	private void fireVertexSelected(final VisualVertex v) {
		for(GraphViewListener<V, E> l : listeners)
			l.vertexSelected(v);
	}
	
	/**
	 * Fires the {@link GraphViewListener#edgeAdded(VisualEdge)} event.
	 * 
	 * @param e the edge that is added
	 * @since 1.0
	 */
	private void fireEdgeAdded(final VisualEdge e) {
		for(GraphViewListener<V, E> l : listeners)
			l.edgeAdded(e);
	}
	
	/**
	 * Fires the {@link GraphViewListener#edgeRemoved(VisualEdge)} event.
	 * 
	 * @param e the edge that is removed
	 * @since 1.0
	 */
	private void fireEdgeRemoved(final VisualEdge e) {
		for(GraphViewListener<V, E> l : listeners)
			l.edgeRemoved(e);
	}
	
	/**
	 * Fires the {@link GraphViewListener#vertexAdded(VisualVertex)} event.
	 * 
	 * @param e the edge that is selected
	 * @since 1.0
	 */
	private void fireEdgeSelected(final VisualEdge e) {
		for(GraphViewListener<V, E> l : listeners)
			l.edgeSelected(e);
	}
	
	/**
	 * Changes the orientation (position) of the toolbar.
	 * 
	 * @param orientation the new orientation (which has to be one of {@link BorderLayout#WEST}, {@link BorderLayout#NORTH}, {@link BorderLayout#EAST}, {@link BorderLayout#SOUTH})
	 * @since 1.0
	 */
	private void changeToolBarOrientation(String orientation) {
		content.remove(toolBar);
		
		switch(orientation) {
			case BorderLayout.NORTH:
			case BorderLayout.SOUTH:
				toolBar.setOrientation(JToolBar.HORIZONTAL);
				break;
			case BorderLayout.EAST:
			case BorderLayout.WEST:
				toolBar.setOrientation(JToolBar.VERTICAL);
				break;
			default:
				toolBar.setOrientation(JToolBar.VERTICAL);
				orientation = BorderLayout.WEST;
		}
		
		content.add(toolBar, orientation);
	}
	
	/**
	 * Shows the properties of the selected object.
	 * 
	 * @since 1.0
	 */
	private void showProperties() {
		if(editable == false)
			return;
		
		final int selCount = selVertices.size() + selEdges.size();
		
		// inform the user that he must select ONE object
		if(selCount != 1) {
			JOptionPane.showMessageDialog(this, showPropsInfoMsg, showPropsInfoTitle, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		// show vertex or edge properties
		if(selVertices.size() > 0)
			vertexPropsDlg.show(selVertices.get(0));
		else if(selEdges.size() > 0)
			edgePropsDlg.show(selEdges.get(0));
	}
	
	/**
	 * Deletes all selected objects (vertices and edges) from the graph.
	 * 
	 * @since 1.0
	 */
	private void deleteSelectedObjects() {
		if(editable == false)
			return;
		
		// ask user if he is sure to delete the selected objects
		if(JOptionPane.showConfirmDialog(this, deleteObjectsWarningMsg, deleteObjectsWarningTitle, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
			// first remove all selected edges
			for(int i = selEdges.size() - 1; i >= 0; i--)
				removeVisualEdge(selEdges.get(i));
			// secondly remove all selected vertices
			for(int i = selVertices.size() - 1; i >= 0; i--)
				removeVisualVertex(selVertices.get(i));
			
			// redraw the graph
			graphPanel.repaint();
		}
	}
	
	/**
	 * Adjusts all vertex and edge positions as well as the custom objects to the new zoom value.
	 * 
	 * @since 1.0
	 */
	private void adjustObjectsToZoom() {
		// adjust the vertex positions to the new zoom value
		for(VisualVertex v : visualVertices)
			v.setPosition((int)(((float)v.getX() / lastZoom) * zoom), (int)(((float)v.getY() / lastZoom) * zoom));
		// adjust the custom visual objects to the new zoom value
		for(CustomVisualObject cvo : customVisualObjects) {
			cvo.setX((int)(((float)cvo.getX() / lastZoom) * zoom + 0.5f));
			cvo.setY((int)(((float)cvo.getY() / lastZoom) * zoom + 0.5f));
			cvo.setWidth((int)(((float)cvo.getWidth() / lastZoom) * zoom));
			cvo.setHeight((int)(((float)cvo.getHeight() / lastZoom) * zoom));
		}
	
		// vertex positions changed so adjust the positions of the edges
		computeEdgePositions();
		
		// the positions changed so check if we need to adjust the drawing area size
		adjustDrawingAreaToVertices();
	}

	/**
	 * Adjusts the dimension of the drawing area to the vertices, that means the drawing area must be
	 * as large as the farthermost vertex.
	 * 
	 * @since 1.0
	 */
	private void adjustDrawingAreaToVertices() {
		int xMax = 0;
		int yMax = 0;
		int xRight;
		int yRight;
		int radius;
		
		for(VisualVertex v : visualVertices) {
			radius = internalGetScaledVertexRadius(v);
			xRight = v.getX() + radius;
			yRight = v.getY() + radius;
			
			if(xRight > xMax)
				xMax = xRight;
			if(yRight > yMax)
				yMax = yRight;
		}
		
		xMax += DRAWINGAREA_PADDING;
		yMax += DRAWINGAREA_PADDING;
		
		drawingArea.width = xMax;
		drawingArea.height = yMax;
		graphPanel.setPreferredSize(drawingArea);
		graphPanel.revalidate();
	}
	
	/**
	 * Paints the graph.
	 * 
	 * @param g the graphics context
	 * @param ignoreInvisibleObjects <code>true</code> if only the visible vertices/edges should be drawn or <code>false</code> if all vertices/edges should be rendered
	 * @since 1.0
	 */
	private void paint(final Graphics2D g, final boolean ignoreInvisibleObjects) {
		// enable antialiasing (interpolation: nearest neighbor (fast), bilinear (slower), bicubic (slowest, but the best))
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		// zoom changed? then recalculate vertex and edge positions
		if(adjustPositionsToZoom) {
			adjustObjectsToZoom();
			adjustPositionsToZoom = false;
		}
		
		// check which vertices are currently visible
		checkVisibilityOfVertices();
		
		beforePaint(g);
		
		// draw visible edges and vertices
		drawEdges(g, ignoreInvisibleObjects);
		drawVertices(g, ignoreInvisibleObjects);
		
		// draw the custom visual objects
		for(CustomVisualObject cvo : customVisualObjects)
			cvo.draw(g, font);
		
		// edge tool is in use? then draw its path
		if(edgeToolCtrlPoints.size() > 0)
			drawEdgeToolPath(g);
		
		// draw the selection area if necessary
		if(selAreaRect != null) {
			final Stroke oldStroke = g.getStroke();
			g.setStroke(selAreaStroke);
			g.setColor(Color.black);
			g.draw(selAreaRect);
			g.setStroke(oldStroke);
		}
	}
	
	/**
	 * Draws all vertices.
	 * 
	 * @param g the graphics context
	 * @param ignoreInvisibleVertices <code>true</code> if only the visible vertices should be drawn or <code>false</code> if all vertices should be rendered
	 * @since 1.0
	 */
	private void drawVertices(final Graphics2D g, final boolean ignoreInvisibleVertices) {
		int radius;
		int x;
		int y;
		
		for(VisualVertex v : visualVertices) {
			if(ignoreInvisibleVertices && !v.isVisible())
				continue;
			
			radius = internalGetScaledVertexRadius(v);
			
			// calculate the left top position of the circle
			x = v.getX() - radius;
			y = v.getY() - radius;
			
			// set attributes of the renderer
			vertexRenderer.setBackground(v.getBackground());
			vertexRenderer.setForeground(v.isSelected() ? selectionColor : v.getForeground());
			vertexRenderer.setPositionCenter(v.getX(), v.getY());
			vertexRenderer.setPositionLeftTop(x, y);
			vertexRenderer.setDiameter(radius*2);
			vertexRenderer.setFont(font);
			vertexRenderer.setEdgeWidth(v.getEdgeWidth());
			vertexRenderer.setAttachmentPoint(v.getAttachmentX(), v.getAttachmentY());
			
			// draw vertex
			vertexRenderer.draw(g, v.getVertex());
		}
	}
	
	/**
	 * Draws all edges.
	 * 
	 * @param g the graphics context
	 * @param ignoreInvisibleEdges <code>true</code> if only the visible edges should be drawn or <code>false</code> if all edges should be rendered
	 * @since 1.0
	 */
	private void drawEdges(final Graphics2D g, final boolean ignoreInvisibleEdges) {
		for(VisualEdge e : visualEdges) {
			if(ignoreInvisibleEdges && !e.isVisible())
				continue;
			
			// set attributes of the renderer
			edgeRenderer.setBackground(e.getColor());
			edgeRenderer.setForeground(e.isSelected() ? selectionColor : e.getColor());
			edgeRenderer.setDrawArrow(e.getEdge().isDirected());
			edgeRenderer.setFirstPosition(e.getX1(), e.getY1());
			edgeRenderer.setSecondPosition(e.getX2(), e.getY2());
			edgeRenderer.setControlPosition(e.getSupportX(), e.getSupportY());
			edgeRenderer.setFont(font);
			edgeRenderer.setLineWidth(e.getLineWidth());
			edgeRenderer.setArrowLength(edgeArrowLength);
			edgeRenderer.setLabelPosition(e.getLabelX(), e.getLabelY());
			
			// set the pre-calculated spline
			edgeRenderer.setSpline(e.getSpline());
			
			// draw edge
			edgeRenderer.draw(g, e.getEdge());
		}
	}
	
	private void drawEdgeToolPath(final Graphics2D g) {
		final int radius = EDGETOOL_CIRCLE_RADIUS * 2;
		Point currP;
		Point nextP;
		
		for(int i = 0; i < edgeToolCtrlPoints.size(); i++) {
			currP = edgeToolCtrlPoints.get(i);
			nextP = (i < edgeToolCtrlPoints.size() - 1) ? edgeToolCtrlPoints.get(i + 1) : mousePos;
			
			g.setColor(edgeToolColor);
			g.drawOval(currP.x - EDGETOOL_CIRCLE_RADIUS, currP.y - EDGETOOL_CIRCLE_RADIUS, radius, radius);
			g.drawLine(currP.x, currP.y, nextP.x, nextP.y);
		}
	}
	
	/**
	 * Represents the visual component of a {@link Vertex}.
	 * <br><br>
	 * A visual vertex has a background color, a foreground color (color of the vertex edge and the caption), an edge width
	 * and a scale factor. With the scale factor you can display vertices with different size. Call the corresponding
	 * methods to change the apprearance of the vertex.<br>
	 * If you want to ensure that the vertex is in the visible area of the graph then you have to call {@link #ensureVisibility()}.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public class VisualVertex implements Serializable {
		
		/** the corresponding vertex */
		private final V v;
		/** the x position of the center of circle */
		private int x;
		/** the y position of the center of circle */
		private int y;
		/** flag that indicates if vertex is selected or not */
		private boolean selected;
		/** flag that indicates if the vertex is hidden that means outside of the currently visible area of the graph */
		private int visibilityFlags;
		/** the background color */
		private Color background;
		/** the foreground color */
		private Color foreground;
		/** the line width of the vertex edge */
		private int edgeWidth;
		/** the scale factor of the vertex circle */
		private float scale;
		/** the index of the vertex in the list of vertices */
		private int index;
		/** the x position of the attachment point */
		private int attachmentX;
		/** the y position of the attachment point */
		private int attachmentY;
		
		/** visibility flag: vertex is inside the visible area */
		static final int VF_VISIBLE = 1;
		/** visibility flag: vertex is not inside the visible area, out of bounds at the left side */
		static final int VF_HIDDEN_OOB_LEFT = 2;
		/** visibility flag: vertex is not inside the visible area, out of bounds at the right side */
		static final int VF_HIDDEN_OOB_RIGHT = 4;
		/** visibility flag: vertex is not inside the visible area, out of bounds at the top side */
		static final int VF_HIDDEN_OOB_TOP = 8;
		/** visibility flag: vertex is not inside the visible area, out of bounds at the bottom side */
		static final int VF_HIDDEN_OOB_BOTTOM = 16;
		
		/**
		 * Creates a new visual vertex.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS CONSTRUCTOR</i>!
		 * 
		 * @param v the vertex
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if v is null</li>
		 * </ul>
		 * @since 1.0
		 */
		VisualVertex(final V v) throws IllegalArgumentException {
			if(v == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.v = v;
			this.x = 0;
			this.y = 0;
			this.selected = false;
			this.visibilityFlags = VF_VISIBLE;
			this.background = GraphView.DEF_VERTEXBACKGROUND;
			this.foreground = GraphView.DEF_VERTEXFOREGROUND;
			this.edgeWidth = GraphView.DEF_VERTEXEDGEWIDTH;
			this.scale = 1.0f;
			this.index = -1;
			this.attachmentX = 0;
			this.attachmentY = 0;
		}
		
		/**
		 * Gets the vertex.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the vertex
		 * @since 1.0
		 */
		public final V getVertex() {
			return v;
		}
		
		/**
		 * Gets the index of the vertex in the list of vertices.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the index of the vertex in the list
		 * @since 1.0
		 */
		public final int getIndex() {
			return index;
		}
		
		/**
		 * Sets the index of the vertex in the list of vertices.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param index the index of the vertex in the list
		 * @since 1.0
		 */
		final void setIndex(final int index) {
			this.index = index;
		}
		
		/**
		 * Gets the background color of the vertex.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the background color
		 * @since 1.0
		 */
		public final Color getBackground() {
			return background;
		}
		
		/**
		 * Sets the background color of the vertex.
		 * <br><br>
		 * <b>This method is thread-safe!</b><br>
		 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
		 * 
		 * @param c the background color
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if c is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public final void setBackground(final Color c) throws IllegalArgumentException {
			if(c == null)
				throw new IllegalArgumentException("No valid argument!");
			
			background = c;
			GraphView.this.autoRepaint();
		}
		
		/**
		 * Gets the foreground color of the vertex.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the foreground color
		 * @since 1.0
		 */
		public final Color getForeground() {
			return foreground;
		}
		
		/**
		 * Sets the foreground color of the vertex.
		 * <br><br>
		 * <b>This method is thread-safe!</b><br>
		 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
		 * 
		 * @param c the foreground color
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if c is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public final void setForeground(final Color c) throws IllegalArgumentException {
			if(c == null)
				throw new IllegalArgumentException("No valid argument!");
			
			foreground = c;
			GraphView.this.autoRepaint();
		}
		
		/**
		 * Gets the line width of the vertex edge.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the line width of the vertex edge
		 * @since 1.0
		 */
		public final int getEdgeWidth() {
			return edgeWidth;
		}
		
		/**
		 * Sets the line width of the vertex edge.
		 * <br><br>
		 * <b>This method is thread-safe!</b><br>
		 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
		 * 
		 * @param w the line width of the vertex edge
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if <code>w < 1</code></li>
		 * </ul>
		 * @since 1.0
		 */
		public final void setEdgeWidth(final int w) throws IllegalArgumentException {
			if(w < 1)
				throw new IllegalArgumentException("No valid argument!");
			
			edgeWidth = w;
			GraphView.this.autoRepaint();
		}
		
		/**
		 * Gets the scale factor of the vertex circle.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the scale factor that means a value <code>> 0.0f</code>
		 * @since 1.0
		 */
		public final float getScale() {
			return scale;
		}
		
		/**
		 * Sets the scale factor of the vertex circle.
		 * <br><br>
		 * <b>This method is thread-safe!</b><br>
		 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
		 * 
		 * @param scale the scale factor that means a value <code>> 0.0f</code> (<code>1.0f</code> means normal size)
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if scale <code><= 0.0f</code></li>
		 * </ul>
		 * @since 1.0
		 */
		public final void setScale(final float scale) throws IllegalArgumentException {
			if(scale <= 0.0f)
				throw new IllegalArgumentException("No valid argument!");
			
			this.scale = scale;
			GraphView.this.autoRepaint();
		}
		
		/**
		 * Gets the x position of the center of circle.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the x position of the vertex center
		 * @since 1.0
		 */
		public final int getX() {
			return x;
		}
		
		/**
		 * Sets the x position of the center of circle.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param x the x position of the vertex center
		 * @since 1.0
		 */
		final void setX(final int x) {
			this.x = x;
		}
		
		/**
		 * Gets the y position of the center of circle.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the y position of the vertex center
		 * @since 1.0
		 */
		public final int getY() {
			return y;
		}
		/**
		 * Sets the y position of the center of circle.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param y the y position of the vertex center
		 * @since 1.0
		 */
		final void setY(final int y) {
			this.y = y;
		}
		
		/**
		 * Sets the position of the center of circle.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param x the x position of the vertex center
		 * @param y the y position of the vertex center
		 * @since 1.0
		 */
		final void setPosition(final int x, final int y) {
			setX(x);
			setY(y);
		}
		
		/**
		 * Gets the x position of the attachment point.
		 * <br><br>
		 * The attachment point is a point at the vertex circle that is optimal to attach objects (like text etc.) to the vertex without
		 * impair the look and feel meaning the attachment point is the point that lies in the center of the largest arc between two edge
		 * endpoints of the vertex.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the x position of the attachment point
		 * @since 1.0
		 */
		public final int getAttachmentX() {
			return attachmentX;
		}
		
		/**
		 * Sets the x position of the attachment point.
		 * <br><br>
		 * The attachment point is a point at the vertex circle that is optimal to attach objects (like text etc.) to the vertex without
		 * impair the look and feel meaning the attachment point is the point that lies in the center of the largest arc between two edge
		 * endpoints of the vertex.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param x the x position of the attachment point
		 * @since 1.0
		 */
		final void setAttachmentX(final int x) {
			attachmentX = x;
		}
		
		/**
		 * Gets the y position of the attachment point.
		 * <br><br>
		 * The attachment point is a point at the vertex circle that is optimal to attach objects (like text etc.) to the vertex without
		 * impair the look and feel meaning the attachment point is the point that lies in the center of the largest arc between two edge
		 * endpoints of the vertex.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the y position of the attachment point
		 * @since 1.0
		 */
		public final int getAttachmentY() {
			return attachmentY;
		}
		
		/**
		 * Sets the y position of the attachment point.
		 * <br><br>
		 * The attachment point is a point at the vertex circle that is optimal to attach objects (like text etc.) to the vertex without
		 * impair the look and feel meaning the attachment point is the point that lies in the center of the largest arc between two edge
		 * endpoints of the vertex.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param y the y position of the attachment point
		 * @since 1.0
		 */
		final void setAttachmentY(final int y) {
			attachmentY = y;
		}
		
		/**
		 * Sets the position of the attachment point.
		 * <br><br>
		 * The attachment point is a point at the vertex circle that is optimal to attach objects (like text etc.) to the vertex without
		 * impair the look and feel meaning the attachment point is the point that lies in the center of the largest arc between two edge
		 * endpoints of the vertex.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param x the x position of the attachment point
		 * @param x the y position of the attachment point
		 * @since 1.0
		 */
		final void setAttachmentPosition(final int x, final int y) {
			attachmentX = x;
			attachmentY = y;
		}
		
		/**
		 * Indicates if the vertex is currently selected.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return <code>true</code> if vertex is selected otherwise <code>false</code>
		 * @since 1.0
		 */
		public final boolean isSelected() {
			return selected;
		}
		
		/**
		 * Sets if the vertex is currently selected.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param selected <code>true</code> if vertex is selected otherwise <code>false</code>
		 * @since 1.0
		 */
		final void setSelected(final boolean selected) {
			this.selected = selected;
		}
		
		/**
		 * Gets the visibility flag(s) of the vertex.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @see #VF_VISIBLE
		 * @see #VF_HIDDEN_OOB_LEFT
		 * @see #VF_HIDDEN_OOB_RIGHT
		 * @see #VF_HIDDEN_OOB_TOP
		 * @see #VF_HIDDEN_OOB_BOTTOM
		 * @return flag that indicates the out of bounds status
		 * @since 1.0
		 */
		final int getVisibilityFlags() {
			return visibilityFlags;
		}
		
		/**
		 * Sets the visibility flag(s) of the vertex.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @see #VF_VISIBLE
		 * @see #VF_HIDDEN_OOB_LEFT
		 * @see #VF_HIDDEN_OOB_RIGHT
		 * @see #VF_HIDDEN_OOB_TOP
		 * @see #VF_HIDDEN_OOB_BOTTOM
		 * @param vf flag that indicates the out of bounds status
		 * @since 1.0
		 */
		final void setVisibilityFlags(final int vf) {
			this.visibilityFlags = vf;
		}
		
		/**
		 * Indicates if the vertex is currently inside the visible area of the graph.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return <code>true</code> if vertex is visible otherwise <code>false</code>
		 * @since 1.0
		 */
		public final boolean isVisible() {
			return (visibilityFlags & VF_VISIBLE) != 0;
		}
		
		/**
		 * Ensures that the vertex is in the visible area of the graph.
		 * <br><br>
		 * That means if he is not then the graph is scrolled to the position of the vertex so that he is
		 * completely visible in the viewport.
		 * <br><br>
		 * <b>This method is thread-safe!</b><br>
		 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
		 * 
		 * @since 1.0
		 */
		public final void ensureVisibility() {
			if(EDT.isExecutedInEDT())
				GraphView.this.ensureVertexVisibility(this);
			else
				EDT.execute(new GuiJob(getClass().getSimpleName() + ".ensureVisibility") {
					@Override
					protected void execute() throws Throwable {
						GraphView.this.ensureVertexVisibility(VisualVertex.this);
					}
				});
		}
		
		/**
		 * Gets the string representation of the visual vertex. This is the caption of the vertex.
		 * 
		 * @return the string representation of the vertex
		 * @since 1.0
		 */
		@Override
		public String toString() {
			return v.toString();
		}

		@Override
		public void serialize(Serializer s) {
			// serialize properties
			s.addInt("x", x);
			s.addInt("y", y);
			s.addObject("background", background);
			s.addObject("foreground", foreground);
			s.addInt("edgeWidth", edgeWidth);
			s.addFloat("scale", scale);
			
			// serialize the related vertex data
			v.serialize(s);
		}

		@Override
		public void deserialize(Serializer s) {
			// deserialize properties
			x = s.getInt("x");
			y = s.getInt("y");
			background = (Color)s.getObject("background", Color.white);
			foreground = (Color)s.getObject("foreground", Color.black);
			edgeWidth = s.getInt("edgeWidth", 1);
			scale = s.getFloat("scale", 1.0f);
			
			// deserialize vertex data
			v.deserialize(s);
		}
		
	}
	
	/**
	 * Represents the visual component of an {@link Edge}.
	 * <br><br>
	 * A visual edge has a color (the color of the line and the label) and a line width. With the corresponding
	 * methods you can change the apprearance of the edge.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public class VisualEdge implements Serializable {
		
		/** the corresponding edge */
		private final E e;
		/** the visual predecessor vertex of the edge */
		private final VisualVertex predecessor;
		/** the visual successor vertex of the edge */
		private final VisualVertex successor;
		/** the x position of the first coordinate that docks the line of the edge to the predecessor */
		private int x1;
		/** the y position of the first coordinate that docks the line of the edge to the predecessor */
		private int y1;
		/** the x position of the second coordinate that docks the line of the edge to the successor */
		private int x2;
		/** the y position of the second coordinate that docks the line of the edge to the successor */
		private int y2;
		/** the x position of the support point */
		private int supportX;
		/** the y position of the support point */
		private int supportY;
		/** the x position of the label */
		private int labelX;
		/** the y position of the label */
		private int labelY;
		/** the spline that represents the edge as a curve */
		private CatmullRomSpline spline;
		/** flag that indicates if edge is selected or not */
		private boolean selected;
		/** the color of the edge */
		private Color color;
		/** the line width of the edge */
		private int lineWidth;
		/** the offset index of the edge */
		private int offsetIndex;
		/** the index of the edge in the list of visual edges */
		private int index;
		
		/**
		 * Creates a new visual edge.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS CONSTRUCTOR</i>!
		 * 
		 * @param e the edge
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if e is null</li>
		 * 		<li>if predecessor is null</li>
		 * 		<li>if successor is null</li>
		 * </ul>
		 * @since 1.0
		 */
		VisualEdge(final E e, final VisualVertex predecessor, final VisualVertex successor) throws IllegalArgumentException {
			if(e == null || predecessor == null || successor == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.e = e;
			this.predecessor = predecessor;
			this.successor = successor;
			this.x1 = 0;
			this.y1 = 0;
			this.x2 = 0;
			this.y2 = 0;
			this.supportX = 0;
			this.supportY = 0;
			this.labelX = 0;
			this.labelY = 0;
			this.spline = null;
			this.selected = false;
			this.color = GraphView.DEF_EDGECOLOR;
			this.lineWidth = GraphView.DEF_EDGELINEWIDTH;
			this.offsetIndex = 0;
			this.index = -1;
		}
		
		/**
		 * <b>Attention</b>:<br>
		 * This constructor may only be used to load serialized data for an edge from an object file!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS CONSTRUCTOR AND DO NOT MAKE USE OF THIS CONSTRUCTOR ANYWHERE ELSE THEN IN {@link GraphView#load(String)}</i>!
		 * 
		 * @since 1.0
		 */
		VisualEdge() {
			this.e = null;
			this.predecessor = null;
			this.successor = null;
		}
		
		/**
		 * Gets the edge.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the edge
		 * @since 1.0
		 */
		public final E getEdge() {
			return e;
		}
		
		/**
		 * Gets the predecessor of the edge.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the predecessor
		 * @since 1.0
		 */
		public final VisualVertex getPredecessor() {
			return predecessor;
		}
		
		/**
		 * Gets the successor of the edge.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the successor
		 * @since 1.0
		 */
		public final VisualVertex getSuccessor() {
			return successor;
		}
		
		/**
		 * Gets the index of the edge in the list of edges.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the index of the edge in the list
		 * @since 1.0
		 */
		public final int getIndex() {
			return index;
		}
		
		/**
		 * Sets the index of the edge in the list of edges.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param index the index of the edge in the list
		 * @since 1.0
		 */
		final void setIndex(final int index) {
			this.index = index;
		}
		
		/**
		 * Gets the offset index of the edge. This index describs how much the
		 * support point of the edge is away from the center between the predecessor and successor.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the offset index
		 * @since 1.0
		 */
		public final int getOffsetIndex() {
			return offsetIndex;
		}
		
		/**
		 * Sets the offset index of the edge. This index describs how much the
		 * support point of the edge is away from the center between the predecessor and successor.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param index the offset index
		 * @since 1.0
		 */
		final void setOffsetIndex(final int index) {
			this.offsetIndex = index;
		}
		
		/**
		 * Gets the color of the edge.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the color
		 * @since 1.0
		 */
		public final Color getColor() {
			return color;
		}
		
		/**
		 * Sets the color of the edge.
		 * <br><br>
		 * <b>This method is thread-safe!</b><br>
		 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
		 * 
		 * @param c the color
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if c is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public final void setColor(final Color c) throws IllegalArgumentException {
			if(c == null)
				throw new IllegalArgumentException("No valid argument!");
			
			color = c;
			GraphView.this.autoRepaint();
		}
		
		/**
		 * Gets the line width of the edge.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the line width
		 * @since 1.0
		 */
		public final int getLineWidth() {
			return lineWidth;
		}
		
		/**
		 * Sets the line width of the edge.
		 * <br><br>
		 * <b>This method is thread-safe!</b><br>
		 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
		 * 
		 * @param w the line width
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if <code>w < 1</code></li>
		 * </ul>
		 * @since 1.0
		 */
		public final void setLineWidth(final int w) throws IllegalArgumentException {
			if(w < 1)
				throw new IllegalArgumentException("No valid argument!");
			
			lineWidth = w;
			GraphView.this.autoRepaint();
		}
		
		/**
		 * Gets x position of the first coordinate that docks the line of the edge to the predecessor.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the x position of the first coordinate
		 * @since 1.0
		 */
		public final int getX1() {
			return x1;
		}
		
		/**
		 * Sets x position of the first coordinate that docks the line of the edge to the predecessor.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param x1 the x position of the first coordinate
		 * @since 1.0
		 */
		final void setX1(final int x1) {
			this.x1 = x1;
		}
		
		/**
		 * Gets y position of the first coordinate that docks the line of the edge to the predecessor.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the y position of the first coordinate
		 * @since 1.0
		 */
		public final int getY1() {
			return y1;
		}
		
		/**
		 * Sets y position of the first coordinate that docks the line of the edge to the predecessor.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param y1 the y position of the first coordinate
		 * @since 1.0
		 */
		final void setY1(final int y1) {
			this.y1 = y1;
		}
		
		/**
		 * Gets x position of the second coordinate that docks the line of the edge to the successor.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the x position of the second coordinate
		 * @since 1.0
		 */
		public final int getX2() {
			return x2;
		}
		
		/**
		 * Sets x position of the second coordinate that docks the line of the edge to the successor.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param x2 the x position of the second coordinate
		 * @since 1.0
		 */
		final void setX2(final int x2) {
			this.x2 = x2;
		}
		
		/**
		 * Gets y position of the second coordinate that docks the line of the edge to the successor.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the y position of the second coordinate
		 * @since 1.0
		 */
		public final int getY2() {
			return y2;
		}
		
		/**
		 * Sets y position of the second coordinate that docks the line of the edge to the successor.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param y2 the y position of the second coordinate
		 * @since 1.0
		 */
		final void setY2(final int y2) {
			this.y2 = y2;
		}
		
		/**
		 * Gets the x position of the support point.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the x position of the support point
		 * @since 1.0
		 */
		public final int getSupportX() {
			return supportX;
		}
		
		/**
		 * Sets the x position of the support point.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param x the x position of the support point
		 * @since 1.0
		 */
		final void setSupportX(final int x) {
			supportX = x;
		}
		
		/**
		 * Gets the y position of the support point.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the y position of the support point
		 * @since 1.0
		 */
		public final int getSupportY() {
			return supportY;
		}
		
		/**
		 * Sets the y position of the support point.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param y the y position of the support point
		 * @since 1.0
		 */
		final void setSupportY(final int y) {
			supportY = y;
		}
		
		/**
		 * Gets the x position of the edge label.
		 * <br><br>
		 * This position is a <b>suggestion</b> meaning that the position of the label is chosen so that it does
		 * not collide with other edges or labels.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the x position of the label
		 * @since 1.0
		 */
		public final int getLabelX() {
			return labelX;
		}
		
		/**
		 * Gets the y position of the edge label.
		 * <br><br>
		 * This position is a <b>suggestion</b> meaning that the position of the label is chosen so that it does
		 * not collide with other edges or labels.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the y position of the label
		 * @since 1.0
		 */
		public final int getLabelY() {
			return labelY;
		}
		
		/**
		 * Sets the position of the edge label.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param x the x position of the label
		 * @param y the y position of the label
		 * @since 1.0
		 */
		final void setLabelPosition(final int x, final int y) {
			labelX = x;
			labelY = y;
		}
		
		/**
		 * Indicates if the edge is currently inside the visible area of the graph.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return <code>true</code> if edge is visible otherwise <code>false</code>
		 * @since 1.0
		 */
		public final boolean isVisible() {
			// ignore the first check if the edge is a loop (otherwise a loop would be always invisible because
			// the predecessor and successor are equal which means are on the same position)
			if(!e.isLoop()) {
				final int diffX = successor.getX() - predecessor.getX();
				final int diffY = successor.getY() - predecessor.getY();
				final int length = (int)Math.sqrt(diffX*diffX + diffY*diffY);
				
				// firstly: look if the edge is covert by the predecessor circle and successor circle which
				// means that the distance of the vertices is smaller then the sum of their radii
				if(length <= GraphView.this.internalGetScaledVertexRadius(predecessor) + GraphView.this.internalGetScaledVertexRadius(successor))
					return false;
			}
			
			/*
			 * secondly:
			 * the edge is visible if the visibility flags of the vertices do not fit
			 * that means for example if v1 is OOB_LEFT && OOB_TOP and v2 is OOB_TOP then
			 * the edge is not visible (because both are out of bounds at the top side)
			 * but if v1 is OOB_LEFT && v2 is OOB_RIGHT then there
			 * can be no assurance that the edge is definitely not visible therefore the edge is visible
			 */
			final int b = predecessor.getVisibilityFlags() & successor.getVisibilityFlags();
			return (b == 0 || b == VisualVertex.VF_VISIBLE);
		}
		
		/**
		 * Indicates if the edge docks to the given vertex that means if
		 * <code>v == getPredecessor() || v == getSuccessor()</code>.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @param v the vertex
		 * @return <code>true</code> if the edge docks to the vertex otherwise <code>false</code>
		 * @since 1.0
		 */
		public final boolean dockTo(final VisualVertex v) {
			return (v == predecessor || v == successor);
		}
		
		/**
		 * Gets the spline of the edge that represents the edge visually.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return the spline
		 * @since 1.0
		 */
		public final CatmullRomSpline getSpline() {
			return spline;
		}
		
		/**
		 * Creates a spline for the edge based on the first, second and the support point.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @since 1.0
		 */
		final void createSpline() {
			if(offsetIndex == 0) {
				// if offset is zero then the edge is a straight line, therefore display the spline as a line
				spline = new CatmullRomSpline(new Point[] { new Point(x1, y1), new Point(x2, y2) }, 1);
			}
			else if(e.isLoop()) {
				// a loop is described by a four points spline
				spline = new CatmullRomSpline(new Point[] { new Point(x1, y1), new Point(supportX - GraphView.this.loopOffsetDistance * offsetIndex, supportY), new Point(supportX + GraphView.this.loopOffsetDistance * offsetIndex, supportY), new Point(x2, y2) });
			}
			else {
				final int diffX = x2 - x1;
				final int diffY = y2 - y1;
				final double dist = Math.sqrt(diffX*diffX + diffY*diffY);
				int interpolation = 24;
				
				// if the distance between the edge end points is smaller 100 pixels then use a less interpolation and
				// if it is greater 400 pixel then use a larger interpolation
				if(dist <= 100)
					interpolation = 12;
				else if(dist >= 400)
					interpolation = 32;
				
				// otherwise create a normal spline defined by the start, support and end point
				spline = new CatmullRomSpline(new Point[] { new Point(x1, y1), new Point(supportX, supportY), new Point(x2, y2) }, interpolation);
			}
		}
		
		/**
		 * Indicates if the edge is currently selected.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @return <code>true</code> if edge is selected otherwise <code>false</code>
		 * @since 1.0
		 */
		public final boolean isSelected() {
			return selected;
		}
		
		/**
		 * Sets if the edge is currently selected.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
		 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
		 * 
		 * @param selected <code>true</code> if edge is selected otherwise <code>false</code>
		 * @since 1.0
		 */
		final void setSelected(final boolean selected) {
			this.selected = selected;
		}
		
		/**
		 * Ensures that the edge is in the visible area of the graph.
		 * <br><br>
		 * That means if she is not then the graph is scrolled to the position of the edge so that she is
		 * completely visible in the viewport.
		 * <br><br>
		 * <b>This method is thread-safe!</b>
		 * 
		 * @since 1.0
		 */
		public final void ensureVisibility() {
			if(EDT.isExecutedInEDT())
				GraphView.this.ensureEdgeVisibility(this);
			else
				EDT.execute(new GuiJob(getClass().getSimpleName() + ".ensureVisibility") {
					@Override
					protected void execute() throws Throwable {
						GraphView.this.ensureEdgeVisibility(VisualEdge.this);
					}
				});
		}
		
		/**
		 * Gets the string representation of the visual edge. The format is <code>(predecessor.toString(), successor.toString())</code>.
		 * 
		 * @return the string representation of the edge
		 * @since 1.0
		 */
		@Override
		public String toString() {
			return "(" + predecessor + ", " + successor + ")";
		}

		@Override
		public void serialize(Serializer s) {
			// serialize properties
			s.addObject("color", color);
			s.addInt("lineWidth", lineWidth);
			
			// serialize edge data
			e.serialize(s);
		}

		@Override
		public void deserialize(Serializer s) {
			// deserialize properties
			color = (Color)s.getObject("color", Color.black);
			lineWidth = s.getInt("lineWidth", 1);
			
			// deserialize edge data
			e.deserialize(s);
		}
		
	}
	
	/**
	 * Graph layout algorithm that arranges the vertices of the graph in a circle.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public class CircleGraphLayout extends GraphLayout {
		
		/** the default padding from a vertex to the center of circle for 3 vertices so calculate a proportional padding using <code>DEF_PADDING / 3 * graph.getOrder()</code> */
		private static final int DEF_PADDING = 60;
		/** the default offset from the left side of the graph view */
		private static final int DEF_OFFSET_LEFT = 50;
		/** the default offset from the top side of the graph view */
		private static final int DEF_OFFSET_TOP = 20;

		@Override
		public <N extends Vertex, L extends Edge> void layout(Graph<N, L> graph, GraphView<N, L> graphView) {
			// the default values are defined for a none zoom which means a zoom of 100%
			final int defOffsetLeft = (int)(((float)DEF_OFFSET_LEFT  / 100.0f) * GraphView.this.zoom);
			final int defOffsetTop = (int)(((float)DEF_OFFSET_TOP  / 100.0f) * GraphView.this.zoom);
			final int proportionalPadding = (graph.getOrder() > 2) ? DEF_PADDING * (graph.getOrder() / 2) : DEF_PADDING;
			int padding = (int)(((float)(proportionalPadding)  / 100.0f) * GraphView.this.zoom);
			
			// the translation angle of a vertex in the circle is alpha = 360°/n with n=amount of vertices
			final double translationAngle = Math.toRadians(360.0 / graph.getOrder());
			
			// calculate the padding between two vertices in the isosceles triangle
			final double vertexPadding = Math.sqrt(2 * padding*padding * (1 - Math.cos(translationAngle)));
			// calculate the minimum padding between two vertices (its 2*radius + e)
			final int minVertexPadding = 2 * GraphView.this.radiusOfVertex + 5;
			/*
			 * If the current padding between two vertices is lower than the minimum then
			 * calculate a new padding:
			 * 
			 *    /\      c^2 = 2 * p^2 * (1 - cos(alpha))
			 * p /  \ p
			 *  /    \    c=2*radius + e, alpha => p = c / sqrt(2 - 2*cos(alpha))
			 * *------*
			 *    c
			 */
			if(vertexPadding < minVertexPadding)
				padding = (int)(minVertexPadding / Math.sqrt(2 - 2*Math.cos(translationAngle)));
			
			// calculate the center of circle
			final int cocX = defOffsetLeft + 2 * GraphView.this.radiusOfVertex + padding;
			final int cocY = defOffsetTop + 2 * GraphView.this.radiusOfVertex + padding;
			
			GraphView<N, L>.VisualVertex v;
			double currentAngle = 0.0;
			
			// go through all vertices and arrange them onto the circle
			for(int i = 0; i < graph.getOrder(); i++) {
				v = graphView.getVisualVertex(graph.getVertex(i));
				
				/*
				 * Calculate the vertex position onto the circle:
				 * P( cos(alpha) | sin(alpha) )
				 * 
				 * To arrange the vertices clockwise invert the values.
				 */
				if(v != null)
					v.setPosition(cocX - (int)(Math.cos(currentAngle) * padding), cocY - (int)(Math.sin(currentAngle) * padding));
				
				currentAngle += translationAngle;
			}
		}
		
	}
	
	/**
	 * A mapping object between an edge and its offset.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.2
	 */
	protected class EdgeOffset {
		
		/** the edge */
		public final E edge;
		/** the offset of the edge */
		public final int offset;
		
		/**
		 * Creates a new edge offset.
		 * 
		 * @param edge the edge
		 * @param offset the offset
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if edge is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public EdgeOffset(final E edge, final int offset) throws IllegalArgumentException {
			if(edge == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.edge = edge;
			this.offset = offset;
		}
	}
	
	/**
	 * The selection type.<br>
	 * Available types:
	 * <ul>
	 * 		<li>{@link SelectionType#NONE}</li>
	 * 		<li>{@link SelectionType#VERTICES_ONLY}</li>
	 * 		<li>{@link SelectionType#EDGES_ONLY}</li>
	 * 		<li>{@link SelectionType#BOTH}</li>
	 * </ul>
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public enum SelectionType {
		
		/** selection is not allowed */
		NONE,
		
		/** only vertices are selectable */
		VERTICES_ONLY,
		
		/** only edges are selectable */
		EDGES_ONLY,
		
		/** both (vertices and edges) are selectable */
		BOTH
	}
	
	/**
	 * The available tool set.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this class!<br>
	 * <i>DO NOT REMOVE THE PRIVATE VISIBILITY OF THIS ENUM</i>!
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private enum Tool {
		
		/** the cursor tool to select and move vertices/edges */
		CURSOR,
		
		/** the vertex tool to add new vertices by clicking with the mouse inside of the graph */
		VERTEX,
		
		/** the edge tool to add new edges by connect vertices */
		EDGE,
		
		/** a custom tool that is implemented in a subclass */
		CUSTOM
	}
	
	/**
	 * Represents the drawing area of the graph.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this class!<br>
	 * <i>DO NOT REMOVE THE PRIVATE VISIBILITY OF THIS CLASS</i>!
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class GraphDrawingPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			GraphView.this.paint((Graphics2D)g, true);
		}
	}
	
	/**
	 * Helper class to manage attachment point data in {@link GraphView#computeVertexAttachmentPoint(VisualVertex)}.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class AttachmentPointData {
		
		/** the total angle */
		public final double angle;
		/** the x position of the circle point */
		public final int circlepoint_x;
		/** the y position of the circle point */
		public final int circlepoint_y;
		
		/**
		 * Creates a new data.
		 * 
		 * @param angle the total angle
		 * @param circlepoint_x the x position of the circle point
		 * @param circlepoint_y the y position of the circle point
		 */
		public AttachmentPointData(final double angle, final int circlepoint_x, final int circlepoint_y) {
			this.angle = angle;
			this.circlepoint_x = circlepoint_x;
			this.circlepoint_y = circlepoint_y;
		}
		
	}
	
	/**
	 * Base implementation of a properties dialog.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this class!<br>
	 * <i>DO NOT REMOVE THE PRIVATE VISIBILITY OF THIS CLASS</i>!
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 * @param <T> the type if object whose properties should be shown
	 */
	private abstract class PropertiesDialog<T> extends PopupWindow {
		
		/** the properties list */
		private final PropertiesList list;
		/** the ok button */
		private final JButton okBtn;
		/** the cancel button */
		private final JButton cancelBtn;
		/** the current object whose properties are shown */
		private T currObject;
		
		/**
		 * Creates a new properties dialog.<br>
		 * The following language labels are available:
		 * <ul>
		 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the properties dialog in which the properties of a vertex or edge are shown</li>
		 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the properties dialog in which the properties of a vertex or edge are shown</li>
		 * </ul>
		 * 
		 * @param langFile the language file or <code>null</code> if there is no language file
		 * @param langID the language id
		 * @since 1.0
		 */
		public PropertiesDialog(final LanguageFile langFile, final String langID) {
			currObject = null;
			this.setSize(180, 180);
			
			final JPanel btnBarPanel = new JPanel();
			list = new PropertiesList(null);
			okBtn = new JButton(LanguageFile.getLabel(langFile, "DLG_BTN_OK", langID, "Ok"));
			cancelBtn = new JButton(LanguageFile.getLabel(langFile, "DLG_BTN_CANCEL", langID, "Cancel"));
			
			// set layouts of panels
			content.setLayout(new BorderLayout());
			btnBarPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			
			// add main panels to content
			content.add(list, BorderLayout.CENTER);
			content.add(btnBarPanel, BorderLayout.SOUTH);
			
			btnBarPanel.add(okBtn);
			btnBarPanel.add(cancelBtn);
			
			okBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(currObject != null) {
						applyObjectProperties(list.getModel(), currObject);
						currObject = null;
					}
					
					// close dialog
					PropertiesDialog.this.close();
					// repaint the graph
					GraphView.this.graphPanel.repaint();
				}
			});
			
			cancelBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// clsoe the dialog
					currObject = null;
					PropertiesDialog.this.close();
				}
			});
		}
		
		/**
		 * Shows the dialog.
		 * 
		 * @param object the object which properties should be displayed
		 * @since 1.0
		 */
		public void show(T object) {
			currObject = object;
			
			// clear current properties
			list.removeAll();
			
			// load the properties of the specific object and show the popup
			loadObjectProperties(list.getModel(), object);
			this.show(GraphView.this.showPropertiesBtn, GraphView.this.showPropertiesBtn.getSize().width + 1, 0);
		}
		
		/**
		 * Applies the object properties.
		 * 
		 * @param plm the properties list model
		 * @param o the object
		 * @since 1.0
		 */
		protected abstract void applyObjectProperties(final PropertiesListModel plm, final T o);
		
		/**
		 * Loads the objects properties.
		 * 
		 * @param plm the properties list model
		 * @param o the object
		 * @since 1.0
		 */
		protected abstract void loadObjectProperties(final PropertiesListModel plm, final T o);
		
	}
	
	/**
	 * Represents a dialog for modifying vertex properties.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this class!<br>
	 * <i>DO NOT REMOVE THE PRIVATE VISIBILITY OF THIS CLASS</i>!
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class VertexPropertiesDialog extends PropertiesDialog<VisualVertex> {

		/**
		 * Creates a new vertex properties dialog.<br>
		 * The following language labels are available:
		 * <ul>
		 * 		<li><i>GRAPHVIEW_PROPERTIESDLG_VERTEX_TITLE</i>: the title of the properties dialog in which the properties of a vertex are shown</li>
		 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the properties dialog in which the properties of a vertex or edge are shown</li>
		 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the properties dialog in which the properties of a vertex or edge are shown</li>
		 * </ul>
		 * 
		 * @param langFile the language file or <code>null</code> if there is no language file
		 * @param langID the language id
		 * @since 1.0
		 */
		public VertexPropertiesDialog(final LanguageFile langFile, final String langID) {
			super(langFile, langID);
			
			this.setTitle(LanguageFile.getLabel(langFile, "GRAPHVIEW_PROPERTIESDLG_VERTEX_TITLE", langID, "Vertex Properties"));
		}

		@Override
		protected void applyObjectProperties(PropertiesListModel plm, VisualVertex o) {
			GraphView.this.applyVertexProperties(plm, o.getVertex());
		}

		@Override
		protected void loadObjectProperties(PropertiesListModel plm, VisualVertex o) {
			GraphView.this.loadVertexProperties(plm, o.getVertex());
		}
		
	}
	
	/**
	 * Represents a dialog for modifying edge properties.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this class!<br>
	 * <i>DO NOT REMOVE THE PRIVATE VISIBILITY OF THIS CLASS</i>!
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class EdgePropertiesDialog extends PropertiesDialog<VisualEdge> {

		/**
		 * Creates a new edge properties dialog.<br>
		 * The following language labels are available:
		 * <ul>
		 * 		<li><i>GRAPHVIEW_PROPERTIESDLG_EDGE_TITLE</i>: the title of the properties dialog in which the properties of an edge are shown</li>
		 * 		<li><i>DLG_BTN_OK</i>: the caption of the ok button of the properties dialog in which the properties of a vertex or edge are shown</li>
		 * 		<li><i>DLG_BTN_CANCEL</i>: the caption of the cancel button of the properties dialog in which the properties of a vertex or edge are shown</li>
		 * </ul>
		 * 
		 * @param langFile the language file or <code>null</code> if there is no language file
		 * @param langID the language id
		 * @since 1.0
		 */
		public EdgePropertiesDialog(final LanguageFile langFile, final String langID) {
			super(langFile, langID);
			
			this.setTitle(LanguageFile.getLabel(langFile, "GRAPHVIEW_PROPERTIESDLG_EDGE_TITLE", langID, "Edge Properties"));
		}

		@Override
		protected void applyObjectProperties(PropertiesListModel plm, VisualEdge o) {
			GraphView.this.applyEdgeProperties(plm, o.getEdge());
		}

		@Override
		protected void loadObjectProperties(PropertiesListModel plm, VisualEdge o) {
			GraphView.this.loadEdgeProperties(plm, o.getEdge());
		}
		
	}
	
	/**
	 * Class to controll all events in the graph view.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this class!<br>
	 * <i>DO NOT REMOVE THE PRIVATE VISIBILITY OF THIS CLASS</i>!
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class EventController implements MouseListener, MouseMotionListener, ActionListener, KeyListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2)
				GraphView.this.mouseDblClicked(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			GraphView.this.mouseDown(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			GraphView.this.mouseUp(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			GraphView.this.mouseMove(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			GraphView.this.mouseMove(e);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == GraphView.this.cursorBtn)
				GraphView.this.selectTool(Tool.CURSOR);
			else if(e.getSource() == GraphView.this.addVertexBtn)
				GraphView.this.selectTool(Tool.VERTEX);
			else if(e.getSource() == GraphView.this.addEdgeBtn)
				GraphView.this.selectTool(Tool.EDGE);
			else if(e.getSource() == GraphView.this.deleteObjectBtn)
				GraphView.this.deleteSelectedObjects();
			else if(e.getSource() == GraphView.this.showPropertiesBtn)
				GraphView.this.showProperties();
			else if(e.getSource() == GraphView.this.zoomInBtn)
				GraphView.this.zoomIn();
			else if(e.getSource() == GraphView.this.zoomOutBtn)
				GraphView.this.zoomOut();
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			GraphView.this.keyReleased(e);
		}

		@Override
		public void keyTyped(KeyEvent e) {
			GraphView.this.keyTyped(e);
		}
		
	}

}