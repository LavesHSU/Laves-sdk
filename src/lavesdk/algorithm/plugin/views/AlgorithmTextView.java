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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.scilab.forge.jlatexmath.TeXIcon;

import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.algorithm.text.AlgorithmParagraph;
import lavesdk.algorithm.text.AlgorithmStep;
import lavesdk.algorithm.text.Annotation;
import lavesdk.algorithm.text.AlgorithmStep.TextToken;
import lavesdk.algorithm.text.AlgorithmStep.TextTokenType;
import lavesdk.algorithm.text.AlgorithmText;
import lavesdk.algorithm.text.AlgorithmTextListener;
import lavesdk.configuration.Configuration;
import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.gui.dialogs.AnnotationDialog;
import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;

/**
 * Displays the paragraphs and steps of an {@link AlgorithmText} and visualizes the executing {@link AlgorithmStep}.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #AlgorithmTextView(PluginHost, String, AlgorithmText, boolean, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
 * 		<li><i>TEXTVIEW_ADD_BREAKPOINT</i>: the text of the menu item to add a breakpoint</li>
 * 		<li><i>TEXTVIEW_REMOVE_BREAKPOINT</i>: the text of the menu item to remove a breakpoint</li>
 * 		<li><i>TEXTVIEW_SET_ALL_BREAKPOINTS</i>: the text of the menu item to set all breakpoints</li>
 * 		<li><i>TEXTVIEW_REMOVE_ALL_BREAKPOINTS</i>: the text of the menu item to remove all breakpoints</li>
 * 		<li><i>TEXTVIEW_SHOW_ANNOTATION</i>: the text of the menu item and the tooltip of the button to show the annotation of an algorithm step</li>
 * 		<li><i>TEXTVIEW_FONTSIZE_UP_BTN_TOOLTIP</i>: the tooltip text of the font size up button in the header bar</li>
 * 		<li><i>TEXTVIEW_FONTSIZE_DOWN_BTN_TOOLTIP</i>: the tooltip text of the font size down button in the header bar</li>
 * 		<li><i>TEXTVIEW_FONTSIZE_NORMAL_BTN_TOOLTIP</i>: the tooltip text of the normal font size button in the header bar</li>
 * 		<li><i>TEXTVIEW_TOGGLE_BREAKPOINT_BTN_TOOLTIP</i>: the tooltip text of the toggle breakpoint button in the header bar</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * <br><br>
 * <b>LaTeX specific</b>:<br>
 * You can use the first parameter of a latex formula to define an integral offset value (positive or negative) to
 * modify the vertical alignment of a formula.
 * <br><br>
 * <b>Highlight Colors</b>:<br>
 * Use {@link #setHighlightBackground(Color)} and {@link #setHighlightForeground(Color)} to change the colors that visualize a step that is currently in
 * execution.
 * <br><br>
 * <b>Save and load the configuration</b>:<br>
 * You can save and load a configuration of the algorithm text view by using {@link #saveConfiguration(Configuration)} and {@link #loadConfiguration(Configuration)}.
 * It is saved or restored the visibility of the view, the highlight background and foreground, the halted background and foreground, the font size of the
 * algorithm text view and the active breakpoints of the steps. This makes it possible that you can store the state of the view persistent.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class AlgorithmTextView extends View {

	private static final long serialVersionUID = 1L;
	
	/** the host application */
	private final PluginHost host;
	/** the algorithm text */
	private final AlgorithmText text;
	/** the scroll pane in which the text is displayed to be scrollable */
	private final JScrollPane scrollPane;
	/** the panel where the text is displayed */
	private final AlgorithmTextDrawingPanel textPanel;
	/** the event controller to handle events */
	private final EventController eventController;
	/** a list that represents the visual structure of the algoritghm text */
	private final List<VisualParagraph> visualParagraphs;
	/** the id of the paragraph that is currently executed */
	private int executingParagraphID;
	/** the background color of the highlighted step (that is the step that is currently in execution) */
	private Color highlightBGColor;
	/** the foreground color of the highlighted step (that is the step that is currently in execution) */
	private Color highlightFGColor;
	/** the background color of the highlighted step <b>when he has a breakpoint</b> */
	private Color haltedBGColor;
	/** the foreground color of the highlighted step <b>when he has a breakpoint</b> */
	private Color haltedFGColor;
	/** the font of the highlighted paragraph meaning the paragraph which has a step that is currently executed */
	private Font highlightFont;
	/** the (static) width of the paragraph column */
	private int paragraphColumnWidth;
	/** the selected step or <code>null</code> if no step is selected */
	private AlgorithmStep selectedStep;
	/** the selection background color (this is the system color for highlighted text) */
	private final Color selectionBGColor;
	/** the selection foreground color (this is the system color for highlighted text) */
	private final Color selectionFGColor;
	/** the popup menu of the view */
	private final JPopupMenu popupMenu;
	/** the menu item to add a breakpoint */
	private final JMenuItem pmimAddBreakpoint;
	/** the menu item to remove a breakpoint */
	private final JMenuItem pmimRemoveBreakpoint;
	/** the menu item to set all breakpoints */
	private final JMenuItem pmimSetAllBreakpoints;
	/** the menu item to remove all breakpoints */
	private final JMenuItem pmimRemoveAllBreakpoints;
	/** the menu item to show the annotation of an algorithm step */
	private final JMenuItem pmimShowAnnotation;
	/** the button on the toolbar to toggle a breakpoint of a selected step */
	private final JToggleButton toggleBreakpointBtn;
	/** the button on the toolbar to show the annotation of a selected step */
	private final JButton showAnnotationBtn;
	
	/** the breakpoint icon */
	private static Image breakpointIcon;
	/** the active breakpoint icon */
	private static Image activeBreakpointIcon;
	
	/** the width of the breakpoint icon */
	private static final int BREAKPOINT_WIDTH;
	/** the height of the breakpoint icon */
	private static final int BREAKPOINT_HEIGHT;
	/** the horizontal space of a breakpoint meaning {@link #BREAKPOINT_WIDTH} + a padding */
	private static final int BREAKPOINT_HSPACE;
	/** the right padding of the paragraph column */
	private static final int PARAGRAPHCOL_RIGHT_PADDING = 10;
	/** the padding of the text */
	private static final int TEXT_PADDING = 2;
	/** the default highlight background color */
	private static final Color DEF_HIGHLIGHTBGCOLOR = Color.white;
	/** the default highlight foreground color */
	private static final Color DEF_HIGHLIGHTFGCOLOR = new Color(0, 115, 200);
	/** the default halted background color */
	private static final Color DEF_HALTEDBGCOLOR = new Color(255, 242, 242);
	/** the default halted foreground color */
	private static final Color DEF_HALTEDFGCOLOR = new Color(0, 127, 127);
	
	static {
		final Dimension breakpointIconDim = new Dimension();
		final Dimension actBreakpointIconDim = new Dimension();
		
		// load the breakpoint icon as an image or create a dummy if the resource is not available
		if(Resources.getInstance().BREAKPOINT_ICON != null) {
			breakpointIcon = Resources.getInstance().BREAKPOINT_ICON.getImage();
			breakpointIconDim.width = Resources.getInstance().BREAKPOINT_ICON.getIconWidth();
			breakpointIconDim.height = Resources.getInstance().BREAKPOINT_ICON.getIconHeight();
		}
		else {
			breakpointIcon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			breakpointIconDim.width = breakpointIconDim.height = 16;
		}
		
		// load the active breakpoint icon as an image or create a dummy if the resource is not available
		if(Resources.getInstance().BREAKPOINT_ACTIVE_ICON != null) {
			activeBreakpointIcon = Resources.getInstance().BREAKPOINT_ACTIVE_ICON.getImage();
			actBreakpointIconDim.width = Resources.getInstance().BREAKPOINT_ACTIVE_ICON.getIconWidth();
			actBreakpointIconDim.height = Resources.getInstance().BREAKPOINT_ACTIVE_ICON.getIconHeight();
		}
		else {
			activeBreakpointIcon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			actBreakpointIconDim.width = actBreakpointIconDim.height = 16;
		}
		
		// determine the maximum image width of the breakpoint images
		BREAKPOINT_WIDTH = Math.max(breakpointIconDim.width, actBreakpointIconDim.width);
		BREAKPOINT_HEIGHT = Math.max(breakpointIconDim.height, actBreakpointIconDim.height);
		BREAKPOINT_HSPACE = BREAKPOINT_WIDTH + 1;
	}
	
	/**
	 * Creates a new view for the visualization of an algorithm text.
	 * 
	 * @param host the host application of the plugin that uses the algorithm text view
	 * @param title the title of the view which is displayed in the header bar
	 * @param text the algorithm text that should be displayed
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if host is null</li>
	 * 		<li>if title is null</li>
	 * 		<li>if text is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmTextView(final PluginHost host, final String title, final AlgorithmText text) throws IllegalArgumentException {
		this(host, title, text, true);
	}
	
	/**
	 * Creates a new view for the visualization of an algorithm text.
	 * 
	 * @param host the host application of the plugin that uses the algorithm text view
	 * @param title the title of the view which is displayed in the header bar
	 * @param text the algorithm text that should be displayed
	 * @param closable <code>true</code> if the text view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a text view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if host is null</li>
	 * 		<li>if title is null</li>
	 * 		<li>if text is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmTextView(final PluginHost host, final String title, final AlgorithmText text, final boolean closable) throws IllegalArgumentException {
		this(host, title, text, closable, null, null);
	}

	/**
	 * Creates a new view for the visualization of an algorithm text.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the text view. The following language labels are available:
	 * <ul>
	 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
	 * 		<li><i>TEXTVIEW_ADD_BREAKPOINT</i>: the text of the menu item to add a breakpoint</li>
	 * 		<li><i>TEXTVIEW_REMOVE_BREAKPOINT</i>: the text of the menu item to remove a breakpoint</li>
	 * 		<li><i>TEXTVIEW_SET_ALL_BREAKPOINTS</i>: the text of the menu item to set all breakpoints</li>
	 * 		<li><i>TEXTVIEW_REMOVE_ALL_BREAKPOINTS</i>: the text of the menu item to remove all breakpoints</li>
	 * 		<li><i>TEXTVIEW_SHOW_ANNOTATION</i>: the text of the menu item and the tooltip of the button to show the annotation of an algorithm step</li>
	 * 		<li><i>TEXTVIEW_FONTSIZE_UP_BTN_TOOLTIP</i>: the tooltip text of the font size up button in the header bar</li>
	 * 		<li><i>TEXTVIEW_FONTSIZE_DOWN_BTN_TOOLTIP</i>: the tooltip text of the font size down button in the header bar</li>
	 * 		<li><i>TEXTVIEW_FONTSIZE_NORMAL_BTN_TOOLTIP</i>: the tooltip text of the normal font size button in the header bar</li>
	 * 		<li><i>TEXTVIEW_TOGGLE_BREAKPOINT_BTN_TOOLTIP</i>: the tooltip text of the toggle breakpoint button in the header bar</li>
	 * </ul>
	 * 
	 * @param host the host application of the plugin that uses the algorithm text view
	 * @param title the title of the view which is displayed in the header bar
	 * @param text the algorithm text that should be displayed
	 * @param closable <code>true</code> if the text view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a text view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langFile the language file or <code>null</code> if the text view should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if host is null</li>
	 * 		<li>if title is null</li>
	 * 		<li>if text is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmTextView(final PluginHost host, final String title, final AlgorithmText text, final boolean closable, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		super(title, closable, langFile, langID);
		
		if(host == null || text == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.host = host;
		this.text = text;
		this.textPanel = new AlgorithmTextDrawingPanel();
		this.scrollPane = new JScrollPane(textPanel);
		this.eventController = new EventController();
		this.visualParagraphs = new ArrayList<VisualParagraph>();
		this.executingParagraphID = getExecutingParagraphID();
		this.highlightBGColor = DEF_HIGHLIGHTBGCOLOR;
		this.highlightFGColor = DEF_HIGHLIGHTFGCOLOR;
		this.haltedBGColor = DEF_HALTEDBGCOLOR;
		this.haltedFGColor = DEF_HALTEDFGCOLOR;
		this.selectionBGColor = SystemColor.textHighlight;
		this.selectionFGColor = SystemColor.textHighlightText;
		this.selectedStep = null;
		this.popupMenu = new JPopupMenu();
		this.pmimAddBreakpoint = new JMenuItem(LanguageFile.getLabel(langFile, "TEXTVIEW_ADD_BREAKPOINT", langID, "Add Breakpoint"), Resources.getInstance().BREAKPOINT_ICON);
		this.pmimRemoveBreakpoint = new JMenuItem(LanguageFile.getLabel(langFile, "TEXTVIEW_REMOVE_BREAKPOINT", langID, "Remove Breakpoint"));
		this.pmimSetAllBreakpoints = new JMenuItem(LanguageFile.getLabel(langFile, "TEXTVIEW_SET_ALL_BREAKPOINTS", langID, "Set All Breakpoints"));
		this.pmimRemoveAllBreakpoints = new JMenuItem(LanguageFile.getLabel(langFile, "TEXTVIEW_REMOVE_ALL_BREAKPOINTS", langID, "Remove All Breakpoints"));
		this.pmimShowAnnotation = new JMenuItem(LanguageFile.getLabel(langFile, "TEXTVIEW_SHOW_ANNOTATION", langID, "Show Annotation"), Resources.getInstance().ANNOTATION_ICON);
		this.toggleBreakpointBtn = new JToggleButton(Resources.getInstance().BREAKPOINT_ICON);
		this.showAnnotationBtn = new JButton(Resources.getInstance().ANNOTATION_ICON);
		
		// create the popup menu
		popupMenu.add(pmimAddBreakpoint);
		popupMenu.add(pmimRemoveBreakpoint);
		popupMenu.addSeparator();
		popupMenu.add(pmimSetAllBreakpoints);
		popupMenu.add(pmimRemoveAllBreakpoints);
		popupMenu.addSeparator();
		popupMenu.add(pmimShowAnnotation);
		
		// extend the header bar with the functionality of increase/decrease/normalize the font size and toggle breakpoints
		new FontHeaderBarExtension(this, AlgorithmText.FONTSIZE, true, langFile, langID).apply();
		addHeaderBarComponent(toggleBreakpointBtn);
		addHeaderBarSeparator();
		addHeaderBarComponent(showAnnotationBtn);
		if(closable)
			addHeaderBarSeparator();
		
		// load the tooltip texts of the toolbar buttons
		toggleBreakpointBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "TEXTVIEW_TOGGLE_BREAKPOINT_BTN_TOOLTIP", langID, "Toggle Breakpoint") + "</html>");
		showAnnotationBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "TEXTVIEW_SHOW_ANNOTATION", langID, "Show Annotation") + "</html>");
		
		// set the initial state of the toolbar buttons
		updateToolBarButtonsState();
		
		// set border layout to fill out the whole view
		content.setLayout(new BorderLayout());
		content.add(scrollPane, BorderLayout.CENTER);
		
		// the drawing area should have a white background
		textPanel.setBackground(Color.white);
		scrollPane.setBackground(Color.white);
		
		// set the default font for text panes (otherwise we cannot create the initial highlight font!)
		final Font defFont = UIManager.getFont("TextPane.font");
		super.setFont(defFont.deriveFont(text.getFontSize()));	// use super to avoid a second calculation of the latex formulas
		updateHighlightFont();
		
		// the default text color is black
		setForeground(Color.black);
		
		// create the visual tokens for the text
		createTextStructure();
		
		// add the listeners whose events must be handled
		addComponentListener(eventController);
		text.addTextListener(eventController);
		textPanel.addMouseListener(eventController);
		textPanel.addMouseMotionListener(eventController);
		pmimAddBreakpoint.addActionListener(eventController);
		pmimRemoveBreakpoint.addActionListener(eventController);
		pmimSetAllBreakpoints.addActionListener(eventController);
		pmimRemoveAllBreakpoints.addActionListener(eventController);
		pmimShowAnnotation.addActionListener(eventController);
		toggleBreakpointBtn.addActionListener(eventController);
		showAnnotationBtn.addActionListener(eventController);
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
	 * Gets the algorithm text that is displayed in the view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the algorithm text
	 * @since 1.0
	 */
	public AlgorithmText getText() {
		if(EDT.isExecutedInEDT())
			return text;
		else
			return EDT.execute(new GuiRequest<AlgorithmText>() {
				@Override
				protected AlgorithmText execute() throws Throwable {
					return text;
				}
			});
	}
	
	/**
	 * Gets the foreground color of the highlighted step. The highlighted step is the one which is currently
	 * in execution.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the foreground color of the executing step
	 * @since 1.0
	 */
	public Color getHighlightForeground() {
		if(EDT.isExecutedInEDT())
			return highlightFGColor;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return highlightFGColor;
				}
			});
	}
	
	/**
	 * Sets the foreground color of the highlighted step. The highlighted step is the one which is currently
	 * in execution.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param c the foreground color of the executing step
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if c is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setHighlightForeground(final Color c) throws IllegalArgumentException {
		if(c == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			highlightFGColor = c;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setHeighlightForeground") {
				@Override
				protected void execute() throws Throwable {
					highlightFGColor = c;
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * Gets the background color of the highlighted step. The highlighted step is the one which is currently
	 * in execution.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the background color of the executing step
	 * @since 1.0
	 */
	public Color getHighlightBackground() {
		if(EDT.isExecutedInEDT())
			return highlightBGColor;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return highlightBGColor;
				}
			});
	}
	
	/**
	 * Sets the background color of the highlighted step. The highlighted step is the one which is currently
	 * in execution.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param c the background color of the executing step
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if c is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setHighlightBackground(final Color c) throws IllegalArgumentException {
		if(c == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			highlightBGColor = c;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setHighlightBackground") {
				@Override
				protected void execute() throws Throwable {
					highlightBGColor = c;
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * Gets the foreground color of the highlighted step <b>when he is halted (meaning he has a breakpoint)</b>. The highlighted step is the one which is currently
	 * in execution.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the foreground color of the step with the currently active breakpoint
	 * @since 1.0
	 */
	public Color getHaltedForeground() {
		if(EDT.isExecutedInEDT())
			return haltedFGColor;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return haltedFGColor;
				}
			});
	}
	
	/**
	 * Sets the foreground color of the highlighted step <b>when he is halted (meaning he has a breakpoint)</b>. The highlighted step is the one which is currently
	 * in execution.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param c the foreground color of the step with the currently active breakpoint
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if c is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setHaltedForeground(final Color c) throws IllegalArgumentException {
		if(c == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			haltedFGColor = c;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setHaltedForeground") {
				@Override
				protected void execute() throws Throwable {
					haltedFGColor = c;
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * Gets the background color of the highlighted step <b>when he is halted (meaning he has a breakpoint)</b>. The highlighted step is the one which is currently
	 * in execution.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the background color of the step with the currently active breakpoint
	 * @since 1.0
	 */
	public Color getHaltedBackground() {
		if(EDT.isExecutedInEDT())
			return haltedBGColor;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return haltedBGColor;
				}
			});
	}
	
	/**
	 * Sets the background color of the highlighted step <b>when he is halted (meaning he has a breakpoint)</b>. The highlighted step is the one which is currently
	 * in execution.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param c the background color of the step with the currently active breakpoint
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if c is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setHaltedBackground(final Color c) throws IllegalArgumentException {
		if(c == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			haltedBGColor = c;
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setHaltedBackground") {
				@Override
				protected void execute() throws Throwable {
					haltedBGColor = c;
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * Deselects a step if a step is currently selected and updates the text layout.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	@Override
	public void reset() {
		if(EDT.isExecutedInEDT()) {
			deselectStep(true);
			computeTextLayout();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".reset") {
				@Override
				protected void execute() throws Throwable {
					deselectStep(true);
					computeTextLayout();
				}
			});
	}
	
	/**
	 * Sets the font of the algorithm text.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param f the font
	 * @since 1.0
	 */
	@Override
	public void setFont(final Font f) {
		if(EDT.isExecutedInEDT())
			internalSetFont(f);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setFont") {
				@Override
				protected void execute() throws Throwable {
					internalSetFont(f);
				}
			});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void repaintComponent() {
		super.repaintComponent();
		textPanel.repaint();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void autoRepaint() {
		if(getAutoRepaint())
			textPanel.repaint();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Furthermore it is read the highlight background and foreground (keys "highlightBG"/"highlightFG"), the halted
	 * background and foreground (keys "haltedBG"/"haltedFG"), the font size (key "fontSize") of the algorithm text view
	 * and a collection of step identifiers (key "breakpoints") which have a breakpoint.
	 * 
	 * @since 1.0
	 */
	@Override
	protected void readConfigurationData(Configuration cd) {
		super.readConfigurationData(cd);
		
		setHighlightBackground(cd.getColor("highlightBG", DEF_HIGHLIGHTBGCOLOR));
		setHighlightForeground(cd.getColor("highlightFG", DEF_HIGHLIGHTFGCOLOR));
		setHaltedBackground(cd.getColor("haltedBG", DEF_HALTEDBGCOLOR));
		setHaltedForeground(cd.getColor("haltedFG", DEF_HALTEDFGCOLOR));
		setFont(getFont().deriveFont(cd.getFloat("fontSize", AlgorithmText.FONTSIZE)));
		
		// load the breakpoints if possible
		final Collection<Integer> breakpoints = cd.getCollection("breakpoints", null);
		if(breakpoints != null) {
			for(Integer id : breakpoints) {
				final AlgorithmStep step = text.getStepByID(id);
				if(step != null)
					step.setBreakpoint(true);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Furthermore it is written the highlight background and foreground (keys "highlightBG"/"highlightFG"), the halted
	 * background and foreground (keys "haltedBG"/"haltedFG"), the font size (key "fontSize") of the algorithm text view
	 * and a collection of step identifiers (key "breakpoints") which have a breakpoint.
	 * 
	 * @since 1.0
	 */
	@Override
	protected void writeConfigurationData(Configuration cd) {
		super.writeConfigurationData(cd);
		
		cd.addColor("highlightBG", getHighlightBackground());
		cd.addColor("highlightFG", getHighlightForeground());
		cd.addColor("haltedBG", getHaltedBackground());
		cd.addColor("haltedFG", getHaltedForeground());
		cd.addFloat("fontSize", getFont().getSize2D());
		
		// save a list of step identifiers which have a breakpoint currently set
		final Collection<Integer> breakpoints = new ArrayList<Integer>();
		for(int i = 0; i < text.getStepCount(); i++)
			if(text.getStep(i).hasBreakpoint())
				breakpoints.add(text.getStep(i).getID());
		
		cd.addCollection("breakpoints", breakpoints);
	}
	
	/**
	 * The mouse is pressed.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseDown(final MouseEvent e) {
		final VisualToken vt = getTokenFromPosition(e.getX(), e.getY());
		
		if(vt != null)
			selectStep(vt, true);
		else
			deselectStep(true);
	}
	
	/**
	 * The mouse is released.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseUp(final MouseEvent e) {
		//the right mouse button is used? then open the popup menu
		if(SwingUtilities.isRightMouseButton(e)) {
			// disable the add/remove option if there is no step selected or the option is applicable
			pmimAddBreakpoint.setEnabled(selectedStep != null && !selectedStep.hasBreakpoint());
			pmimRemoveBreakpoint.setEnabled(selectedStep != null && selectedStep.hasBreakpoint());
			pmimShowAnnotation.setEnabled(selectedStep != null && selectedStep.getAnnotation() != null);
			popupMenu.show(textPanel, e.getX(), e.getY());
		}
	}
	
	/**
	 * The mouse is moved.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseMoved(final MouseEvent e) {
		if(getTokenFromPosition(e.getX(), e.getY()) != null)
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		else
			setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * The mouse has exited the view.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseExited(final MouseEvent e) {
		setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * The mouse was double clicked.
	 * 
	 * @param e the mouse event
	 * @since 1.0
	 */
	private void mouseDblClicked(final MouseEvent e) {
		// if a step is selected then toggle the breakpoint
		doToggleBreakpoint();
	}
	
	/**
	 * Sets the font of the algorithm text.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param f the font
	 * @since 1.0
	 */
	private void internalSetFont(final Font f) {
		super.setFont(f);
		
		text.setFontSize(f.getSize());
		updateHighlightFont();
	}
	
	/**
	 * Adds a breakpoint to the selected step.
	 * 
	 * @since 1.0
	 */
	private void doAddBreakpoint() {
		if(selectedStep == null)
			return;
		
		selectedStep.setBreakpoint(true);
		updateToolBarButtonsState();
		computeTextLayout();
	}
	
	/**
	 * Remove the breakpoint from the selected step.
	 * 
	 * @since 1.0
	 */
	private void doRemoveBreakpoint() {
		if(selectedStep == null)
			return;
		
		selectedStep.setBreakpoint(false);
		updateToolBarButtonsState();
		computeTextLayout();
	}
	
	/**
	 * Sets/Unsets all breakpoints meaning that the breakpoint is set or unset at each step of the text.
	 * 
	 * @param status <code>true</code> if all breakpoints should be set or <code>false</code> if all breakpoints should be removed
	 * @since 1.0
	 */
	private void doSetAllBreakpoints(final boolean status) {
		for(int i = 0; i < text.getStepCount(); i++)
			text.getStep(i).setBreakpoint(status);
		
		updateToolBarButtonsState();
		computeTextLayout();
	}
	
	/**
	 * Shows the annotation of the currently selected step.
	 * 
	 * @since 1.0
	 */
	private void doShowAnnotation() {
		if(selectedStep == null)
			return;
		
		final Annotation a = selectedStep.getAnnotation();
		new AnnotationDialog(host, a, langFile, langID).setVisible(true);
	}
	
	/**
	 * Toggles the breakpoint of the currently selected step.
	 * 
	 * @since 1.0
	 */
	private void doToggleBreakpoint() {
		if(selectedStep == null)
			return;
		
		selectedStep.setBreakpoint(!selectedStep.hasBreakpoint());
		toggleBreakpointBtn.setSelected(selectedStep.hasBreakpoint());
		
		computeTextLayout();
	}
	
	/**
	 * Updates the state of the toggle breakpoint button and the show annotation button in the header bar.
	 * 
	 * @since 1.0
	 */
	private void updateToolBarButtonsState() {
		toggleBreakpointBtn.setSelected(selectedStep != null && selectedStep.hasBreakpoint());
		toggleBreakpointBtn.setEnabled(selectedStep != null);
		showAnnotationBtn.setEnabled(selectedStep != null && selectedStep.getAnnotation() != null);
	}
	
	/**
	 * Selects a step (the previously selected step is deselected).
	 * 
	 * @param token the token its step should be selected
	 * @param repaint <code>true</code> if the text view should be repainted automatically otherwise <code>false</code>
	 * @since 1..0
	 */
	private void selectStep(final VisualToken token, final boolean repaint) {
		// deselect the currently selected step
		deselectStep(false);
		
		selectedStep = token.getStep();
		
		// select all related tokens
		for(VisualParagraph vp : visualParagraphs) {
			// check only the tokens of the paragraph that relates to the step
			if(vp.getParagraph().getID() != selectedStep.getParagraph().getID())
				continue;
			
			boolean selected = false;
			
			// select the tokens that relate to the step
			for(VisualToken vt : vp.getTokens()) {
				if(vt.getStep().getID() == selectedStep.getID()) {
					vt.setSelected(true);
					selected = true;
				}
				else if(selected) {
					// visual tokens of a step are always consecutively meaning that we can break up
					// if a sequence of tokens is selected
					break;
				}
			}
		}
		
		// update the toggle breakpoint button in the header bar
		updateToolBarButtonsState();
		
		// redraw the text if necessary
		if(repaint)
			textPanel.repaint();
	}
	
	/**
	 * Deselects the step but only if a step is currently selected.
	 * 
	 * @param repaint <code>true</code> if the text view should be repainted automatically otherwise <code>false</code>
	 * @since 1.0
	 */
	private void deselectStep(final boolean repaint) {
		// no step selected? then break up
		if(selectedStep == null)
			return;
		
		// clear selection
		for(VisualParagraph vp : visualParagraphs) {
			// check only the tokens of the paragraph that relates to the selected step
			if(vp.getParagraph().getID() != selectedStep.getParagraph().getID())
				continue;
			
			boolean deselected = false;
			
			// deselect the tokens that relate to the selected step
			for(VisualToken vt : vp.getTokens()) {
				if(vt.isSelected()) {
					vt.setSelected(false);
					deselected = true;
				}
				else if(deselected) {
					// visual tokens of a step are always consecutively meaning that we can break up
					// if a sequence of tokens is deselected
					break;
				}
			}
		}
		
		// step is now deselected
		selectedStep = null;
		
		// update the toggle breakpoint button in the header bar
		updateToolBarButtonsState();
		
		// redraw the text if necessary
		if(repaint)
			textPanel.repaint();
	}
	
	/**
	 * Gets the id of the paragraph which is currently executed.
	 * 
	 * @return the id of the executing paragraph
	 * @since 1.0
	 */
	private int getExecutingParagraphID() {
		final AlgorithmStep execStep = text.getStepByID(text.getExecutingStepID());
		return (execStep != null) ? execStep.getParagraph().getID() : -1;
	}
	
	/**
	 * Gets the visual token at the given position.
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @return the token or <code>null</code> if there is no token at the specified position
	 * @since 1.0
	 */
	private VisualToken getTokenFromPosition(final int x, final int y) {
		for(VisualParagraph vp : visualParagraphs) {
			// is the point inside of the current paragraph?
			if(y >= vp.getY() && y <= vp.getY() + vp.getHeight()) {
				// find the line that contains the specified point
				for(VisualTokenLine vtl : vp.getLines()) {
					if(y >= vtl.getY() && y <= vtl.getY() + vtl.getHeight() && x >= vtl.getX() && x <= vtl.getX() + vtl.getWidth()) {
						VisualToken vt;
						int currX = vtl.getX();
						
						// look for the token that is placed at the given x position (the y position
						// is already verified because the point is inside the current line)
						for(int i = 0; i < vtl.getTokenCount(); i++) {
							vt = vtl.getToken(i);
							if(x >= currX && x <= currX + vt.getWidth())
								return vt;
							
							currX += vt.getWidth();
						}
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if the executing paragraph changes and recalculates the text layout if necessary.<br>
	 * Additionally it is ensured that the executing step is visible and if not the view is scrolled
	 * to the line of the step.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The text view is automatically repainted.
	 * 
	 * @since 1.0
	 */
	private void updateTextLayout() {
		final Rectangle viewPortRect = scrollPane.getViewport().getViewRect();
		final int oldExecParagraphID = executingParagraphID;
		
		// get the paragraph of step step that is currently in execution
		executingParagraphID = getExecutingParagraphID();
		
		// the selected step is the step in execution? then deselect this step
		if(selectedStep != null && selectedStep.getID() == text.getExecutingStepID())
			deselectStep(false);
		
		// recalculate the text structure but only if the highlighted paragraph changed
		if(executingParagraphID != oldExecParagraphID)
			computeTextLayout();

		// ensure the visibility of the executing step
		if(executingParagraphID >= 0) {
			boolean done = false;
			
			for(VisualParagraph vp : visualParagraphs) {
				// find the executing paragraph
				if(vp.getParagraph().getID() == executingParagraphID) {
					// find the line of the first token of the executing step
					for(VisualToken vt : vp.getTokens()) {
						if(vt.getStep().getID() == text.getExecutingStepID() && vt.isFirstToken()) {
							// the line could be invalid if this method is invoked before the
							// tokens are computed so avoid a NullPointerException
							if(vt.getLine() != null) {
								final Point p = new Point();
								
								// line is not (completely) visible?
								if(vt.getLine().getY() < viewPortRect.y || vt.getLine().getY() > viewPortRect.y + viewPortRect.height ||
									vt.getLine().getX() < viewPortRect.x || vt.getLine().getX() > viewPortRect.x + viewPortRect.width) {
									// scroll to the y position of the associated line and to the x position of the line
									// but only if the line is not visible
									p.y = vt.getLine().getY();
									p.x = (vt.getLine().getX() > viewPortRect.x + viewPortRect.width) ? vt.getLine().getX() : viewPortRect.x;
									scrollPane.getViewport().setViewPosition(p);
								}
							}
							
							done = true;
							break;
						}
					}
				}
				
				if(done)
					break;
			}
		}
		
		// repaint the text
		textPanel.repaint();
	}
	
	/**
	 * Updates the {@link #highlightFont} meaning that the font is derived from the current
	 * font.
	 * <br><br>
	 * Furthermore the possible extents of all {@link VisualToken}s are updated and the paragraph column with
	 * and the text layout are recalculated.
	 * 
	 * @since 1.0
	 */
	private void updateHighlightFont() {
		final Font f = getFont();
		
		highlightFont = f.deriveFont(Font.BOLD);
		
		// update the possible extents of each token
		for(VisualParagraph vp : visualParagraphs)
			for(VisualToken vt : vp.getTokens())
				vt.updatePossibleExtents(getFont(), highlightFont);
		
		// recalculate text (text is redrawn in computeTextLayout())
		computeParagraphColWidth();
		computeTextLayout();
	}
	
	/**
	 * Computes the text layout meaning that the {@link VisualToken}s are divided in {@link VisualTokenLine}.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The text view is automatically repainted!
	 * 
	 * @since 1.0
	 */
	private void computeTextLayout() {
		final Rectangle rect = scrollPane.getBounds();
		final FontMetrics fmHighlight = getFontMetrics(highlightFont);
		final FontMetrics fmNormal = getFontMetrics(getFont());
		final int lineStartX = TEXT_PADDING + paragraphColumnWidth;
		final int indentWidth = fmNormal.stringWidth(" ");
		int currY = TEXT_PADDING;
		int textWidth = lineStartX;
		int textHeight = currY;
		VisualTokenLine currLine = null;
		int lastIndent;
		int currIndent;
		boolean baseLineCreated;
		boolean newLine;
		
		for(VisualParagraph vp : visualParagraphs) {
			// set the paragraph attributes
			vp.setX(TEXT_PADDING);
			vp.setY(currY);
			vp.setNameMetrics((vp.getParagraph().getID() == executingParagraphID) ? fmHighlight : fmNormal);
			
			vp.getLines().clear();
			baseLineCreated = false;
			newLine = false;
			lastIndent = 0;
			
			for(VisualToken vt : vp.getTokens()) {
				// get the indent of the current token and verify whether a new line has to be created
				// (this is necessary, if the indent differs from the last indent because a new indent needs a new line)
				currIndent = vt.getStep().getIndent();
				newLine = baseLineCreated && (currIndent != lastIndent);
				lastIndent = currIndent;
				
				// create a new base line for the tokens of the paragraph
				if(!baseLineCreated) {
					currLine = new VisualTokenLine();
					vp.getLines().add(currLine);
					currLine.setX(lineStartX + currIndent * indentWidth);
					currLine.setY(currY);
					
					baseLineCreated = true;
				}
				
				// firstly (!) compute the current extent of the token
				vt.computeExtent(vp.getParagraph().getID() == executingParagraphID);
				
				// add the token to the current line if it is a line break because a line break is ranked among
				// the previous line
				if(vt.getToken().type == TextTokenType.LINEBREAK) {
					currLine.addToken(vt);
					vt.setLine(currLine);
					newLine = true;
				}
				
				// secondly: create a new line if necessary
				// (each line must contain at least one token but if the current line has already a token and the
				//  current one does not fit into this line then create a new line for the paragraph)
				if(newLine || (currLine.hasTokens() && currLine.getX() + currLine.getWidth() + vt.getWidth() >= rect.x + rect.width - TEXT_PADDING - 1)) {
					// update the y position of the next line
					currY += currLine.getHeight();
					
					// create a new line
					currLine = new VisualTokenLine();
					vp.getLines().add(currLine);
					currLine.setX(lineStartX + currIndent * indentWidth);
					currLine.setY(currY);
				}
				
				// thirdly add the token to the line if it is not a line break (a line break is added earlier)
				if(vt.getToken().type != TextTokenType.LINEBREAK) {
					currLine.addToken(vt);
					vt.setLine(currLine);
				}
				
				// update the complete text width
				if(currLine.getX() + currLine.getWidth() > textWidth)
					textWidth = currLine.getX() + currLine.getWidth();
			}
			
			// compute the height of the paragraph which consists of the sum of all lines
			vp.computeHeight();
			
			// update the complete text height
			textHeight += vp.getHeight();
			
			// update the y position of the next paragraph
			currY += currLine.getHeight();
		}
		
		// add the right and the bottom padding
		textWidth += TEXT_PADDING;
		textHeight += TEXT_PADDING;
		
		// set the new size of the text panel
		textPanel.setPreferredSize(new Dimension(textWidth, textHeight));
		textPanel.revalidate();
		textPanel.repaint();
	}
	
	/**
	 * Computes the width of the paragraph column.
	 * 
	 * @see #paragraphColumnWidth
	 * @since 1.0
	 */
	private void computeParagraphColWidth() {
		final FontMetrics fm = getFontMetrics(highlightFont);
		int w;
		
		paragraphColumnWidth = 0;
		
		for(int i = 0; i < text.getParagraphCount(); i++) {
			// compute the width of the paragraph name
			w = fm.stringWidth(text.getParagraph(i).getName());
			// add a padding value if necessary
			if(w > 0)
				w += PARAGRAPHCOL_RIGHT_PADDING;
			
			// do we have a new column width?
			if(w > paragraphColumnWidth)
				paragraphColumnWidth = w;
		}
	}
	
	/**
	 * Creates the base structure of the text meaning that the {@link VisualToken}s are added to
	 * the related {@link VisualParagraph}s.
	 * 
	 * @since 1.0
	 */
	private void createTextStructure() {
		AlgorithmParagraph p;
		AlgorithmStep s;
		VisualParagraph vp;
		VisualToken vt;
		
		visualParagraphs.clear();
		
		// create text structure by adding all paragraphs of the algorithm and its tokens
		for(int i = 0; i < text.getParagraphCount(); i++) {
			p = text.getParagraph(i);
			vp = new VisualParagraph(p);
			
			// add visual component of current paragraph
			visualParagraphs.add(vp);
			
			// create a visual component for each token of each step
			for(int j = 0; j < p.getStepCount(); j++) {
				s = p.getStep(j);
				for(int k = 0; k < s.getTextTokenCount(); k++) {
					// create token and update the currently possible extents of the token
					vt = new VisualToken(s, s.getTextToken(k), k == 0);
					vt.updatePossibleExtents(getFont(), highlightFont);
					// add it to the corresponding paragraph
					vp.getTokens().add(vt);
				}
			}
		}
		
		computeParagraphColWidth();
	}
	
	/**
	 * Paints the text.
	 * 
	 * @param g the graphics context
	 * @since 1.0
	 */
	private void paint(final Graphics2D g) {
		VisualToken vt;
		boolean bold;
		int currX;
		
		for(VisualParagraph vp : visualParagraphs) {
			// set paint properties
			bold = (vp.getParagraph().getID() == executingParagraphID);
			g.setFont(bold ? highlightFont : getFont());
			g.setColor(Color.black);
			
			// draw the name of the paragraph
			g.drawString(vp.getParagraph().getName(), vp.getX(), vp.getNameY());
			
			// draw all lines
			for(VisualTokenLine vtl : vp.getLines()) {
				currX = vtl.getX();
				
				// draw all tokens in the current line
				for(int i = 0; i < vtl.getTokenCount(); i++) {
					vt = vtl.getToken(i);
					vt.draw(g, currX, vtl.getY(), vtl.getBaseline(), bold, vt.getStep().getID() == text.getExecutingStepID(), getFont(), highlightFont);
					
					// update the x position of the next token
					currX += vt.getWidth();
				}
			}
		}
	}
	
	/**
	 * Represents the drawing area of the algorithm text.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this class!<br>
	 * <i>DO NOT REMOVE THE PRIVATE VISIBILITY OF THIS CLASS</i>!
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class AlgorithmTextDrawingPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			AlgorithmTextView.this.paint((Graphics2D)g);
		}
		
	}
	
	/**
	 * Represents the visual component of an {@link AlgorithmParagraph}.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class VisualParagraph {
		
		/** the associated algorithm paragraph */
		private final AlgorithmParagraph paragraph;
		/** the list of all {@link VisualToken}s that this paragraph contains */
		private final List<VisualToken> tokens;
		/** the list of all lines that this paragraph contains */
		private final List<VisualTokenLine> lines;
		/** the x position of the paragraph */
		private int x;
		/** the y position of the paragraph */
		private int y;
		/** the height of the paragraph */
		private int height;
		/** the font height of the name of the paragraph */
		private int nameHeight;
		/** the font ascent of the name */
		private int nameAscent;
		
		/**
		 * Creates a new visual paragraph.
		 * 
		 * @param paragraph the associated paragraph
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if paragraph is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public VisualParagraph(final AlgorithmParagraph paragraph) throws IllegalArgumentException {
			if(paragraph == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.paragraph = paragraph;
			this.tokens = new ArrayList<VisualToken>();
			this.lines = new ArrayList<VisualTokenLine>();
			this.x = 0;
			this.y = 0;
			this.height = 0;
			this.nameHeight = 0;
			this.nameAscent = 0;
		}
		
		/**
		 * Gets the corresponding paragraph.
		 * 
		 * @return the paragraph
		 * @since 1.0
		 */
		public final AlgorithmParagraph getParagraph() {
			return paragraph;
		}
		
		/**
		 * Gets the list of visual tokens that this paragraph contains.
		 * 
		 * @return the list of tokens
		 * @since 1.0
		 */
		public final List<VisualToken> getTokens() {
			return tokens;
		}
		
		/**
		 * Gets the lines of which this paragraph consists.
		 * 
		 * @return the lines of the paragraph
		 * @since 1.0
		 */
		public final List<VisualTokenLine> getLines() {
			return lines;
		}
		
		/**
		 * Gets the x position of the paragraph.
		 * 
		 * @return the x position
		 * @since 1.0
		 */
		public final int getX() {
			return x;
		}
		
		/**
		 * Sets the x position of the paragraph.
		 * 
		 * @param x the x position
		 * @since 1.0
		 */
		public final void setX(final int x) {
			this.x = x;
		}
		
		/**
		 * Gets the y position of the paragraph.
		 * 
		 * @return the y position
		 * @since 1.0
		 */
		public final int getY() {
			return y;
		}
		
		/**
		 * Sets the y position of the paragraph.
		 * 
		 * @param y the y position
		 * @since 1.0
		 */
		public final void setY(final int y) {
			this.y = y;
		}
		
		/**
		 * Gets the complete height of the paragraph meaning the sum of the heights of all lines
		 * the paragraph consists of.
		 * 
		 * @return the paragraph height
		 * @since 1.0
		 */
		public final int getHeight() {
			return height;
		}
		
		/**
		 * Computes the complete height of the paragraph meaning the sum of the heights of all lines
		 * the paragraph consists of.
		 * 
		 * @since 1.0
		 */
		public final void computeHeight() {
			height = 0;
			
			for(VisualTokenLine vtl : lines)
				height += vtl.getHeight();
			
			// at least the paragraph is as high as his name is
			if(height < nameHeight)
				height = nameHeight;
		}
		
		/**
		 * Gets the y position of the paragraph text (name).
		 * 
		 * @return the y position of the name
		 * @since 1.0
		 */
		public final int getNameY() {
			// if this paragraph has at least one line position the name of the paragraph in the middle
			// of the first line
			if(lines.size() > 0)
				return y + lines.get(0).getBaseline();
			else
				return y + nameAscent;
		}
		
		/**
		 * Sets the name metrics.
		 * 
		 * @param fm the font metrics
		 * @since 1.0
		 */
		public final void setNameMetrics(final FontMetrics fm) {
			nameHeight = fm.getHeight();
			nameAscent = fm.getAscent();
		}
		
	}
	
	/**
	 * Represents the visual component of a {@link TextToken}.
	 * <br><br>
	 * Each token is completely painted meaning that a line break inside of a token is disallowed.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class VisualToken {
		
		/** the associated algorithm step */
		private final AlgorithmStep step;
		/** the corresponding token */
		private final TextToken token;
		/** flag that indicates if this visual token is the first one of the step */
		private final boolean firstToken;
		/** the width of the token */
		private int width;
		/** the height of the token */
		private int height;
		/** the baseline of the token (the baseline is the reference point at which the token is painted) */
		private int baseline;
		/** the width of the token in normal state */
		private int widthNormal;
		/** the height of the token in normal state */
		private int heightNormal;
		/** the width of the token in highlighted state meaning bold font */
		private int widthHighlighted;
		/** the height of the token in highlighted state meaning bold font */
		private int heightHighlighted;
		/** the baseline of the token in normal state (the baseline is the reference point at which the token is painted) */
		private int baselineNormal;
		/** the baseline of the token in highlighted state (the baseline is the reference point at which the token is painted) */
		private int baselineHighlighted;
		/** the y offset of the token */
		private final int formulaOffsetY;
		/** flag that indicates if the token is currently selected */
		private boolean selected;
		/** the line in which this token is located */
		private VisualTokenLine line;
		
		/**
		 * Creates a new visual token.
		 * 
		 * @param step the step
		 * @param token the corresponding text token
		 * @param firstToken <code>true</code> if this visual token is the first one of the step otherwise <code>false</code>
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if step is null</li>
		 * 		<li>if token is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public VisualToken(final AlgorithmStep step, final TextToken token, final boolean firstToken) throws IllegalArgumentException {
			if(step == null || token == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.step = step;
			this.token = token;
			this.width = 0;
			this.height = 0;
			this.firstToken = firstToken;
			this.widthNormal = 0;
			this.heightNormal = 0;
			this.widthHighlighted = 0;
			this.heightHighlighted = 0;
			this.baseline = 0;
			this.baselineNormal = 0;
			this.baselineHighlighted = 0;
			this.formulaOffsetY = (token.type == TextTokenType.FORMULA && token.formula.getParameterCount() > 0) ? token.formula.getIntParameter(0) : 0;
			this.selected = false;
			this.line = null;
		}
		
		/**
		 * Gets the corresponding algorithm step.
		 * 
		 * @return the step
		 * @since 1.0
		 */
		public final AlgorithmStep getStep() {
			return step;
		}
		
		/**
		 * Indicates whether this token is the first token of the corresponding step.
		 * 
		 * @return <code>true</code> if it is the first token of the step otherwise <code>false</code>
		 * @since 1.0
		 */
		public final boolean isFirstToken() {
			return firstToken;
		}
		
		/**
		 * Gets the correpsonding {@link TextToken} of this visual token.
		 * 
		 * @return the text token
		 * @since 1.0
		 */
		public final TextToken getToken() {
			return token;
		}
		
		/**
		 * Gets the width of the token.
		 * 
		 * @return the width of the token
		 * @since 1.0
		 */
		public final int getWidth() {
			return width;
		}
		
		/**
		 * Gets the height of the token.
		 * 
		 * @return the height
		 * @since 1.0
		 */
		public final int getHeight() {
			return height;
		}
		
		/**
		 * Gets the baseline of the token.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * The baseline is the reference point (of the font) at which the token is painted.
		 * 
		 * @return the baseline
		 * @since 1.0
		 */
		public final int getBaseline() {
			return baseline;
		}
		
		/**
		 * Gets the line in which this token is located.
		 * 
		 * @return the line or <code>null</code> if the line is not determined
		 * @since 1.0
		 */
		public final VisualTokenLine getLine() {
			return line;
		}
		
		/**
		 * Indicates if the token is selected.
		 * 
		 * @return <code>true</code> if the token is currently selected otherwise <code>false</code>
		 * @since 1.0
		 */
		public final boolean isSelected() {
			return selected;
		}
		
		/**
		 * Sets if the token is selected.
		 * 
		 * @param selected <code>true</code> if the token should be selected otherwise <code>false</code>
		 * @since 1.0
		 */
		public final void setSelected(final boolean selected) {
			this.selected = selected;
		}
		
		/**
		 * Sets the line in which this token is located.
		 * 
		 * @param line the line
		 * @since 1.0
		 */
		public final void setLine(final VisualTokenLine line) {
			this.line = line;
		}
		
		/**
		 *(Pre-)Calculates the possible extents of the token meaning the extent at normal state and
		 * the extent at highlighted state.
		 * 
		 * @param normal the normal font
		 * @param highlighted the highlighted font
		 * @since 1.0
		 */
		public void updatePossibleExtents(final Font normal, final Font highlighted) {
			if(token.type == TextTokenType.FORMULA) {
				// the possible sizes are the ones of the specific formula for both state
				widthNormal = token.formula.getNormal().getIconWidth();
				heightNormal = token.formula.getNormal().getIconHeight();
				baselineNormal = (int)(token.formula.getNormal().getBaseLine() * heightNormal);
				widthHighlighted = token.formula.getHighlighted().getIconWidth();
				heightHighlighted = token.formula.getHighlighted().getIconHeight();
				baselineHighlighted = (int)(token.formula.getHighlighted().getBaseLine() * heightHighlighted);
			}
			else if(token.type == TextTokenType.STRING) {
				// the possible sizes are the text extents for both states
				final FontMetrics fmNormal = AlgorithmTextView.this.getFontMetrics(normal);
				final FontMetrics fmHighlighted = AlgorithmTextView.this.getFontMetrics(highlighted);
				widthNormal = fmNormal.stringWidth(token.string);
				heightNormal = fmNormal.getHeight();
				baselineNormal = fmNormal.getAscent();
				widthHighlighted = fmHighlighted.stringWidth(token.string);
				heightHighlighted = fmHighlighted.getHeight();
				baselineHighlighted = fmHighlighted.getAscent();
			}
			else {
				// the possible sizes of a line break are only the height of the normal font
				final FontMetrics fmNormal = AlgorithmTextView.this.getFontMetrics(normal);
				widthNormal = widthHighlighted = 0;
				heightNormal = heightHighlighted = fmNormal.getHeight();
				baselineNormal = baselineHighlighted = fmNormal.getAscent();
			}
		}
		
		/**
		 * Computes the extent of the token.
		 * 
		 * @param highlighted <code>true</code> if the extent of the highlighted state should be applied or <code>false</code> for the extent of the normal state
		 * @since 1.0
		 */
		public void computeExtent(final boolean highlighted) {
			width = highlighted ? widthHighlighted : widthNormal;
			height = highlighted ? heightHighlighted : heightNormal;
			baseline = highlighted ? baselineHighlighted : baselineNormal;
			if(firstToken && step.hasBreakpoint())
				width += AlgorithmTextView.BREAKPOINT_HSPACE;
		}
		
		/**
		 * Draws the token.
		 * 
		 * @param g the graphics context
		 * @param x the x position of the token
		 * @param y the y position of the token (should be the y position of the corresponding line)
		 * @param baseline the base line at which this token should be painted (should be the baseline of the corresponding line)
		 * @param bold <code>true</code> for draw bold state otherwise <code>false</code>
		 * @param highlighted <code>true</code> for draw highlighted state meaning that the step of the token is currently executed (therefore <code>bold</code> should also be <code>true</code>) otherwise <code>false</code>
		 * @param normalFont the normal font
		 * @param highlightedFont the highlighted font
		 * @since 1.0
		 */
		public void draw(final Graphics2D g, int x, int y, final int baseline,final boolean bold, final boolean highlighted, final Font normalFont, final Font highlightedFont) {
			// set the specific font
			g.setFont(bold ? highlightedFont : normalFont);
			
			if(highlighted) {
				// if the text should be drawn in highlighted state (meaning the corresponding step is in execution)
				// then draw the highlight background of the step (with full height of line)
				g.setColor(step.hasBreakpoint() ? AlgorithmTextView.this.haltedBGColor : AlgorithmTextView.this.highlightBGColor);
				g.fillRect(x, line.getY(), width, line.getHeight());
				
				// for further painting use the highlight color as foreground color
				g.setColor(step.hasBreakpoint() ? AlgorithmTextView.this.haltedFGColor : AlgorithmTextView.this.highlightFGColor);
			}
			else if(selected) {
				// if token is selected then draw selection background behind the token
				// (important: only if the token should not be drawn highlighted!)
				g.setColor(AlgorithmTextView.this.selectionBGColor);
				g.fillRect(x, y + baseline - this.baseline + formulaOffsetY, width, height);
				
				// for further painting use the selection color as foreground color
				g.setColor(AlgorithmTextView.this.selectionFGColor);
			}
			else {
				// use the normal foreground color
				g.setColor(AlgorithmTextView.this.getForeground());
			}

			// token is the first one of its step and the corresponding step has a breakpoint? then draw the icon
			if(firstToken && step.hasBreakpoint()) {
				g.drawImage(highlighted ? AlgorithmTextView.activeBreakpointIcon : AlgorithmTextView.breakpointIcon, x, y + baseline - AlgorithmTextView.BREAKPOINT_HEIGHT + 3, null);
				// important: update the x position of the text
				x += AlgorithmTextView.BREAKPOINT_HSPACE;
			}
			
			if(token.type == TextTokenType.STRING) {
				// strings are drawn at its baseline
				g.drawString(token.string, x, y + baseline);
			}
			else if(token.type == TextTokenType.FORMULA) {
				final TeXIcon ico;
				
				// decide which icon should be used
				if(bold)
					ico = token.formula.getHighlighted();
				else
					ico = token.formula.getNormal();
				
				// set the current foreground color of the graphics context to paint the formula in this color
				ico.setForeground(g.getColor());
				// paint the formula
				ico.paintIcon(null, g, x, y + baseline - this.baseline + formulaOffsetY);
			}
		}
		
	}
	
	/**
	 * Represents a line of {@link VisualToken}s in a {@link VisualParagraph}.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class VisualTokenLine {
		
		/** the list of {@link VisualToken}s that this line represents */
		private final List<VisualToken> tokens;
		/** the x position of the line */
		private int x;
		/** the y position of the line */
		private int y;
		/** the width of the line */
		private int width;
		/** the height of the line */
		private int height;
		/** the baseline of the line */
		private int baseline;
		
		/**
		 * Creates a new line.
		 * 
		 * @since 1.0
		 */
		public VisualTokenLine() {
			tokens = new ArrayList<VisualToken>();
			x = 0;
			y = 0;
			width = 0;
			height = 0;
			baseline = 0;
		}
		
		/**
		 * Gets the number of tokens of which this line consists.
		 * 
		 * @return the number of token
		 * @since 1.0
		 */
		public final int getTokenCount() {
			return tokens.size();
		}
		
		/**
		 * Gets the token at the given index.
		 * 
		 * @param index the index
		 * @return the token
		 * @throws IndexOutOfBoundsException
		 * <ul>
		 * 		<li>if the index is out of range (<code>index < 0 || index >= getTokenCount()</code>)</li>
		 * </ul>
		 * @since 1.0
		 */
		public final VisualToken getToken(final int index) throws IndexOutOfBoundsException {
			return tokens.get(index);
		}
		
		/**
		 * Adds a new token to the line and extends the extent of the line.
		 * 
		 * @param token the token that should be added
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if token is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public final void addToken(final VisualToken token) throws IllegalArgumentException {
			if(token == null)
				throw new IllegalArgumentException("No valid argument!");
			
			if(tokens.add(token)) {
				// it is the first token in the line or the token is greater than the current height of the line?
				// then update the height to the new one
				if(tokens.size() == 1 || token.getHeight() > height)
					height = token.getHeight();
				// do we have a new baseline?
				if(token.getBaseline() > baseline)
					baseline = token.getBaseline();
				
				// the width of the line is expanded by the token
				width += token.getWidth();
			}
		}
		
		/**
		 * Indicates whether the line has tokens or not.
		 * 
		 * @return <code>true</code> if the line contains token otherwise <code>false</code>
		 * @since 1.0
		 */
		public final boolean hasTokens() {
			return (tokens.size() > 0);
		}
		
		/**
		 * Gets the x position of the line.
		 * 
		 * @return the x position
		 * @since 1.0
		 */
		public final int getX() {
			return x;
		}
		
		/**
		 * Sets the x position of the line.
		 * 
		 * @param x the x position
		 * @since 1.0
		 */
		public final void setX(final int x) {
			this.x = x;
		}
		
		/**
		 * Gets the y position of the line meaning the top edge of the line.
		 * 
		 * @return the y position
		 * @since 1.0
		 */
		public final int getY() {
			return y;
		}
		
		/**
		 * Sets the y position of the line meaning the top edge of the line.
		 * 
		 * @param y the y position
		 * @since 1.0
		 */
		public final void setY(final int y) {
			this.y = y;
		}
		
		/**
		 * Gets the width of the line.
		 * 
		 * @return the width
		 * @since 1.0
		 */
		public final int getWidth() {
			return width;
		}
		
		/**
		 * Gets the height of the line.
		 * 
		 * @return the height
		 * @since 1.0
		 */
		public final int getHeight() {
			return height;
		}
		
		/**
		 * Gets the baseline of the line. The baseline is the reference point at which the string tokens
		 * and formula tokens are positioned meaning that the bottom edge of the characters forms a straight line.
		 *  
		 * @return the baseline of the line (relative to the y position)
		 * @since 1.0
		 */
		public final int getBaseline() {
			return baseline;
		}
		
	}
	
	/**
	 * Handles the events of the algorithm text.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this class!<br>
	 * <i>DO NOT REMOVE THE PRIVATE VISIBILITY OF THIS CLASS</i>!
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class EventController implements AlgorithmTextListener, ComponentListener, MouseListener, MouseMotionListener, ActionListener {

		@Override
		public void structureChanged() {
			// this method can be executed in a RTE thread so shift it to the EDT
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".structureChanged") {
				
				@Override
				protected void execute() throws Throwable {
					AlgorithmTextView.this.createTextStructure();
					AlgorithmTextView.this.computeTextLayout();
				}
			});
		}

		@Override
		public void executingStepChanged() {
			// this method can be executed in a RTE thread so shift it to the EDT
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".executingStepChanged") {
				
				@Override
				protected void execute() throws Throwable {
					AlgorithmTextView.this.updateTextLayout();
				}
			});
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentResized(ComponentEvent e) {
			AlgorithmTextView.this.computeTextLayout();
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2)
				AlgorithmTextView.this.mouseDblClicked(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
			AlgorithmTextView.this.mouseExited(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			AlgorithmTextView.this.mouseDown(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			AlgorithmTextView.this.mouseUp(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			AlgorithmTextView.this.mouseMoved(e);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == AlgorithmTextView.this.pmimAddBreakpoint)
				AlgorithmTextView.this.doAddBreakpoint();
			else if(e.getSource() == AlgorithmTextView.this.pmimRemoveBreakpoint)
				AlgorithmTextView.this.doRemoveBreakpoint();
			else if(e.getSource() == AlgorithmTextView.this.pmimSetAllBreakpoints)
				AlgorithmTextView.this.doSetAllBreakpoints(true);
			else if(e.getSource() == AlgorithmTextView.this.pmimRemoveAllBreakpoints)
				AlgorithmTextView.this.doSetAllBreakpoints(false);
			else if(e.getSource() == AlgorithmTextView.this.pmimShowAnnotation)
				AlgorithmTextView.this.doShowAnnotation();
			else if(e.getSource() == AlgorithmTextView.this.toggleBreakpointBtn)
				AlgorithmTextView.this.doToggleBreakpoint();
			else if(e.getSource() == AlgorithmTextView.this.showAnnotationBtn)
				AlgorithmTextView.this.doShowAnnotation();
		}
		
	}

}
