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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.Border;

import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.configuration.Configuration;
import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.gui.widgets.BaseComponent;
import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;

/**
 * Base implementation of a view of a {@link AlgorithmPlugin}.
 * <br><br>
 * <b>Creating a new view component (please note)</b>:<br>
 * If you create a new view component you have to consider the following: <b>You have to ensure that all public methods of the
 * view are thread-safe</b> and you have to tag them with a short note like "This method is thread-safe!".<br>
 * This is necessary because the LAVESDK uses the Swing framework to display the graphical user interface of algorithms or in
 * general to display the GUI and Swing is not thread-safe. Meaning if you invoke a method of a visual component from another thread
 * than the event dispatch thread (EDT) of Swing you hazard unpredictable behavior (like thread interference or memory consistency errors)
 * of the component. Because an algorithm visualization is executed in an own thread you have to face up to this issue.
 * Furthermore this reduces the work of a plugin developer who must not take care about the thread-safety of a call to a visual component.<br>
 * there are two helpers to execute actions in the EDT, {@link GuiJob} and {@link GuiRequest}. If your method does not have a return type
 * you should use a {@link GuiJob} to shift the action to the EDT otherwise you should use a {@link GuiRequest}.<br>
 * <u>Example</u>:
 * <pre>
 * String text;
 * ...
 * // possibility 1:
 * public String getText() {
 *     return EDT.execute(new GuiRequest&lt;String&gt;() {
 *         protected String execute() throws Throwable {
 *             return text;
 *         }
 *     });
 * }
 * 
 * public void setText(final String newText) {
 *     EDT.execute(new GuiJob() {
 *         protected void execute() throws Throwable {
 *             text = newText;
 *         }
 *     });
 * }
 * 
 * // possibility 2: to avoid creating a GUI action if the current thread is the EDT you can take a little more effort
 * public String getText() {
 *     if(EDT.isExecutedInEDT())
 *         return text;
 *     else
 *         return EDT.execute(new GuiRequest&lt;String&gt;() {
 *             protected String execute() throws Throwable {
 *                 return text;
 *             }
 *         });
 * }
 * 
 * public void setText(final String newText) {
 *     if(EDT.isExecutedInEDT())
 *         text = newText;
 *     else
 *         EDT.execute(new GuiJob() {
 *             protected void execute() throws Throwable {
 *                 text = newText;
 *             }
 *         });
 * }
 * ...
 * </pre>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #View(String, boolean, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * <br><br>
 * <b>Layout</b>:<br>
 * Every view has a header bar and a {@link #content}. The header bar consists of a title (the name of the view)
 * and a close button to close a view manually by the user. To set a tooltip for the close button use {@link #setCloseButtonToolTip(String)}.
 * <br><br>
 * <b>Content</b>:<br>
 * The {@link #content} is the area in which the view is expandable. This is a {@link JPanel} where you can add view specific components.
 * Use the {@link JPanel#setLayout(java.awt.LayoutManager)} method to set a layout for the content area and use the {@link JPanel#add(java.awt.Component)}
 * method to add your own components to the view.
 * <br><br>
 * <b>Grouping views</b>:<br>
 * To group views together use a {@link ViewGroup}. This is a container to display several views either horizontal or vertical.
 * Between each views is a divider (a so called sash). The user can use this divider to increase or decrease the sizes of the
 * views at runtime.
 * <br><br>
 * <b>Focus view</b>:<br>
 * By default every view is focusable. To determine this by yourself invoke {@link #setFocusable(boolean)} and to set the focus manually
 * call {@link #requestFocus()}.<br>
 * Additionally it is only possible to listen to keyboard events if the view holds the focus, so if you want to use a {@link KeyListener}
 * you have to handle the manual focus.
 * <br><br>
 * <b>Auto repaint</b>:<br>
 * Override {@link #getAutoRepaint()} and {@link #setAutoRepaint(boolean)} and make the methods public to allow that a view respectively
 * (some) methods of the view can support auto repainting.
 * <br><br>
 * <b>Extend the header bar</b>:<br>
 * Use {@link #addHeaderBarComponent(JComponent)} to add view specific components to the right of the header bar. Furthermore it is possible to
 * extend {@link ViewHeaderBarExtension} to create a custom headerbar.
 * <br><br>
 * <b>View configuration</b>:<br>
 * You can load and save the configuration of a view by using {@link #loadConfiguration(Configuration, String)}/{@link #saveConfiguration(Configuration, String)}.
 * This enables you the option that a view's state can be stored persistent in case that a concrete view
 * implements the reading and writing of configuration data.
 * 
 * @see ViewGroup
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public abstract class View extends BaseComponent {

	private static final long serialVersionUID = 1L;
	
	/** the title of the view (shown in the header bar) */
	private final String title;
	/** flag that indicates if the view is closable or not */
	private final boolean closable;
	/** the header bar with the title (at the left) and the close button (at the right) of the view */
	private final JToolBar headerBar;
	/** the label that shows the name/title of the view */
	private final JLabel titleLbl;
	/** the button to set the view invisible */
	private final JButton closeBtn;
	/** the language file of the view */
	protected final LanguageFile langFile;
	/** the language id */
	protected final String langID;
	/** the content that contains the component(s) of the view (add your components here) */
	protected final JPanel content;
	/** the controller of the component events */
	private final EventController eventController;
	/** flag that indicates that the view is initialized */
	private boolean initialized;
	/** flag that indicates whether auto repaint is allowed in the view */
	private boolean autoRepaint;
	
	/** the default highlight color */
	private static final Color DEF_HIGHLIGHT = new Color(105, 155, 215);
	
	/**
	 * Creates a new view.
	 * 
	 * @param title the name of the view which is displayed as the title in the header bar
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if name is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public View(final String title) throws IllegalArgumentException {
		this(title, true);
	}
	
	/**
	 * Creates a new view.
	 * 
	 * @param title the name of the view which is displayed as the title in the header bar
	 * @param closable <code>true</code> if the view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public View(final String title, final boolean closable) throws IllegalArgumentException {
		this(title, closable, null, null);
	}
	
	/**
	 * Creates a new view.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the view. The following language labels are available:
	 * <ul>
	 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
	 * </ul>
	 * 
	 * @param title the name of the view which is displayed as the title in the header bar
	 * @param closable <code>true</code> if the view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langFile the language file or <code>null</code> if the view should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public View(final String title, final boolean closable, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		if(title == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.title = title;
		this.closable = closable;
		this.headerBar = new JToolBar(JToolBar.HORIZONTAL);
		this.titleLbl = new JLabel(title);
		this.closeBtn = new JButton(Resources.getInstance().CLOSE_ICON);
		this.langFile = langFile;
		this.langID = langID;
		this.content = new JPanel();
		this.eventController = new EventController();
		this.autoRepaint = false;
		
		// the title is aligned in the center (vertical) of the header bar
		titleLbl.setVerticalAlignment(JLabel.CENTER);
		titleLbl.setVerticalTextPosition(JLabel.CENTER);
		
		// set the hand cursor to mark the close button (label) as clickable
		closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		closeBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "VIEW_CLOSE_TOOLTIP", langID, "Close") + "</html>");
		closeBtn.setVisible(closable);
		
		// define the layout of the view
		super.setLayout(new BorderLayout());
		
		// the header bar is defined by title and close button
		headerBar.setFloatable(false);
		headerBar.add(titleLbl);
		headerBar.add(Box.createHorizontalGlue());
		headerBar.add(closeBtn);
		
		// add the components to the view
		add(headerBar, BorderLayout.NORTH);
		add(content, BorderLayout.CENTER);
		
		// add listeners
		closeBtn.addMouseListener(eventController);
		closeBtn.addActionListener(eventController);
		
		// a view should be focusable (only possible if View extends JComponent because a JPanel is not focusable)
		setFocusable(true);
		
		initialized = true;
	}
	
	/**
	 * Gets the name or title of the view.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The name is shown as the title of the view in the header bar.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the name of the view
	 */
	public final String getTitle() {
		return title;
	}
	
	/**
	 * Extends the title by a string meaning that the extension is appended to the title.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The extension does not have an effect on the title it only changes the display text but not the title itself.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param extension the extension string
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if extension is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public final void extendTitle(final String extension) throws IllegalArgumentException {
		if(extension == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			this.titleLbl.setText(title + extension);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".extendTitle") {
				@Override
				protected void execute() throws Throwable {
					View.this.titleLbl.setText(title + extension);
				}
			});
	}
	
	/**
	 * Indicates if the view is closable using the "X" in the header bar.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if view is closable otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean isClosable() {
		return closable;
	}
	
	/**
	 * Gets the content panel of the view.
	 * <br><br>
	 * The content panel contains the component(s) of the view.
	 * 
	 * @see #content
	 * @return the content panel
	 * @since 1.0
	 */
	public final JPanel getContentPanel() {
		return content;
	}
	
	/**
	 * Loads the configuration data of the view from the given configuration.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It is used <code>"VIEW_" + getTitle()</code> as the configuration key. If you have <b>changing titles</b>
	 * (for example because of language independent titles) you should <b>create a unique configuration key on your own</b>
	 * by using {@link #loadConfiguration(Configuration, String)}.
	 * 
	 * @see #readConfigurationData(Configuration)
	 * @param cfg the configuration
	 * @since 1.0
	 */
	public final void loadConfiguration(final Configuration cfg) {
		loadConfiguration(cfg, "VIEW_" + title);
	}
	
	/**
	 * Loads the configuration data of the view from the given configuration.
	 * 
	 * @see #readConfigurationData(Configuration)
	 * @param cfg the configuration
	 * @param key the configuration key of the view (<b>must be unique based on the specified configuration otherwise false data could be loaded</b>)
	 * @since 1.0
	 */
	public final void loadConfiguration(final Configuration cfg, final String key) {
		if(cfg == null)
			return;
		
		final Configuration cd = cfg.getConfiguration(key);
		
		if(cd == null)
			return;
		else
			readConfigurationData(cd);
	}
	
	/**
	 * Saves the configuration data of the view to the given configuration.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It is used <code>"VIEW_" + getTitle()</code> as the configuration key. If you have <b>changing titles</b>
	 * (for example because of language independent titles) you should <b>create a unique configuration key on your own</b>
	 * by using {@link #saveConfiguration(Configuration, String)}.
	 * 
	 * @see #writeConfigurationData(Configuration)
	 * @param cfg the configuration or <code>null</code> if the view has no configuration
	 * @since 1.0
	 */
	public final void saveConfiguration(final Configuration cfg) {
		saveConfiguration(cfg, "VIEW_" + title);
	}
	

	/**
	 * Saves the configuration data of the view to the given configuration.
	 * 
	 * @see #writeConfigurationData(Configuration)
	 * @param cfg the configuration
	 * @param key the configuration key of the view (<b>must be unique based on the specified configuration otherwise existing data could be overwritten</b>)
	 * @since 1.0
	 */
	public final void saveConfiguration(final Configuration cfg, final String key) {
		if(cfg == null)
			return;
		
		final Configuration cd = new Configuration();
		writeConfigurationData(cd);
		
		cfg.addConfiguration(key, cd);
	}
	
	/**
	 * Resets the view which means sets the initial state of the view.
	 * 
	 * @since 1.0
	 */
	public abstract void reset();
	
	/**
	 * Highlights the view meaning the view is enclosed by a highlight border.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #highlight(boolean, Color)
	 * @param enabled <code>true</code> if the highlight should be enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public void highlight(final boolean enabled) {
		highlight(enabled, DEF_HIGHLIGHT);
	}
	
	/**
	 * Highlights the view meaning the view is enclosed by a highlight border.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param enabled <code>true</code> if the highlight should be enabled otherwise <code>false</code>
	 * @param color the color of the highlight border
	 * @since 1.0
	 */
	public void highlight(final boolean enabled, final Color color) {
		if(EDT.isExecutedInEDT())
			super.setBorder(enabled ? BorderFactory.createMatteBorder(2, 2, 2, 2, (color == null) ? DEF_HIGHLIGHT : color) : null);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".highlight") {
				@Override
				protected void execute() throws Throwable {
					View.super.setBorder(enabled ? BorderFactory.createMatteBorder(2, 2, 2, 2, (color == null) ? DEF_HIGHLIGHT : color) : null);
				}
			});
	}
	
	/**
	 * The border of a view may not be set meaning this method does nothing!
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If you want to enclose the view with a highlight border use {@link #highlight(boolean)}.
	 * 
	 * @param border the border
	 * @since 1.0
	 */
	@Override
	public void setBorder(Border border) {
		// not allowed
	}
	
	/**
	 * The layout of a view may not be changed meaning this method does nothing!
	 * 
	 * @param mgr the layout manager
	 * @since 1.0
	 */
	@Override
	public final void setLayout(LayoutManager mgr) {
		// to set a layout from outside of the view is not allowed!
	}
	
	/**
	 * It is not possible to remove the components of the view meaning this method does nothing!
	 * 
	 * @since 1.0
	 */
	@Override
	public void removeAll() {
		// do nothing!
	}
	
	/**
	 * Indicates whether the view is visible or not. When a view is not visible it indicates
	 * that the view is closed by the user or manually.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the view is not visible meaning the view is closed otherwise <code>false</code>
	 * @since 1.0
	 */
	@Override
	public boolean isVisible() {
		if(EDT.isExecutedInEDT())
			return super.isVisible();
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return View.super.isVisible();
				}
			});
	}
	
	/**
	 * Sets whether the view should be visible or not.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param visible <code>true</code> if the view should be visible otherwise <code>false</code>
	 * @since 1.0
	 */
	@Override
	public void setVisible(final boolean visible) {
		if(EDT.isExecutedInEDT())
			super.setVisible(visible);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setVisible") {
				@Override
				protected void execute() throws Throwable {
					View.super.setVisible(visible);
				}
			});
	}
	
	@Override
	public String toString() {
		return View.class.getSimpleName() + "[" + title + "]";
	}
	
	/**
	 * Adding external components is locked meaning that no components can be added directly to the view.
	 * <br><br>
	 * Use {@link #content} or {@link #getContentPanel()} to add components to the view.
	 * 
	 * @param comp the component
	 * @param constraints the constraints
	 * @param index the index
	 * @since 1.0
	 */
	@Override
	protected final void addImpl(Component comp, Object constraints, int index) {
		// it is not allowed that components are added from extern
		if(!initialized)
			super.addImpl(comp, constraints, index);
	}
	
	/**
	 * Indicates whether auto repaint is enabled in the view.
	 * 
	 * @return <code>true</code> if auto repainting is allowed otherwise <code>false</code>
	 */
	protected boolean getAutoRepaint() {
		return autoRepaint;
	}
	
	/**
	 * Sets whether the view is allowed to be auto repainted.
	 * <br><br>
	 * If a view is not auto repaintable you have to invoke {@link #repaint()} on your own to visualize the changes you made to a component or view.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Enable auto repainting means that if a method is marked as auto repainted the {@link #repaint()} method of the view is automatically
	 * invoked to make the changes visible. But ensure that this does not overload the EDT (event dispatch thread).<br>
	 * <u>Example</u>: you want to update the background color of <code>n</code> objects in a loop and you activate the auto repaint option.<br>
	 * This produces <code>n</code> calls to {@link #repaint()} instead of one call at the end of the loop.<br>
	 * If that is done during the execution of an algorithm it might be that the visualization of the algorithm does not run smoothly. A reason for
	 * that might be that the implementation requests data of a GUI component (which requires a {@link GuiRequest}) and in consequence of that
	 * the algorithm must wait until the pending event is processed in the EDT.<br>
	 * <b>Therefore be careful if you enable auto repaint!</b>
	 * 
	 * @param autoRepaint <code>true</code> if auto repainting should be enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	protected void setAutoRepaint(final boolean autoRepaint) {
		this.autoRepaint = autoRepaint;
	}
	
	/**
	 * Repaints the view but only if auto repainting is allowed.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	protected void autoRepaint() {
		// repaint is thread-safe
		if(autoRepaint)
			repaint();
	}
	
	/**
	 * Reads the configuration data of the view.
	 * <br><br>
	 * By default this method reads the visibility flag (key "visible") of the view. If you want to read custom
	 * configuration data override this method like:
	 * <pre>
	 * ...
	 * protected void readConfigurationData(Configuration cd) {
	 *     super.readConfigurationData(cd);
	 *     // load custom data like
	 *     // foreground = cd.getColor("foreground");
	 *     // ...
	 * }
	 * ...
	 * </pre>
	 * 
	 * @param cd the configuration data
	 * @since 1.0
	 */
	protected void readConfigurationData(final Configuration cd) {
		setVisible(cd.getBoolean("visible", true));
	}
	
	/**
	 * Writes the configuration data of the view.
	 * <br><br>
	 * By default this method writes the visibility flag (key "visible") of the view. If you want to write custom
	 * configuration data override this method like:
	 * <pre>
	 * ...
	 * protected void writeConfigurationData(Configuration cd) {
	 *     super.writeConfigurationData(cd);
	 *     // write custom data like
	 *     // cd.addColor("foreground", foreground);
	 *     // ...
	 * }
	 * ...
	 * </pre>
	 * 
	 * @param cd the configuration data
	 * @since 1.0
	 */
	protected void writeConfigurationData(final Configuration cd) {
		cd.addBoolean("visible", isVisible());
	}
	
	/**
	 * Adds a component to the header bar.
	 * <br><br>
	 * The header bar is organized from right to left meaning the first added component is finally the first component
	 * at the left side of the header bar next to the view title and the last added component is the one left to the close button.<br>
	 * That means if you add a new component to the header bar it is arranged directly left to the close button.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * To add a separator use <code>JToolBar.Separator</code> as the component or call {@link #addHeaderBarSeparator()}.
	 * 
	 * @see #addHeaderBarSeparator()
	 * @param component the component
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if component is null</li>
	 * </ul>
	 * @since 1.0
	 */
	protected final void addHeaderBarComponent(final JComponent component) throws IllegalArgumentException {
		if(component == null)
			throw new IllegalArgumentException("No valid argument!");
		
		headerBar.add(component, headerBar.getComponentCount() - 1);
		headerBar.repaint();
	}
	
	/**
	 * Adds a separator to the header bar.
	 * 
	 * @see #addHeaderBarComponent(JComponent)
	 * @since 1.0
	 */
	protected final void addHeaderBarSeparator() {
		addHeaderBarComponent(new JToolBar.Separator());
	}
	
	/**
	 * Removes the specified component from the header bar.
	 * 
	 * @param component the component
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if component is null</li>
	 * </ul>
	 * @since 1.0
	 */
	protected final void removeHeaderBarComponent(final JComponent component) throws IllegalArgumentException {
		if(component == null)
			throw new IllegalArgumentException("No valid argument!");
		
		headerBar.remove(component);
		headerBar.repaint();
	}
	
	/**
	 * Sets the tooltip text of the close button in the upper right corner of the view.
	 * 
	 * @param toolTip the tooltip text
	 * @since 1.0
	 */
	protected final void setCloseButtonToolTip(final String toolTip) {
		closeBtn.setToolTipText(toolTip);
	}
	
	/**
	 * Closes the view meaning that the visibility of the view is set to <code>false</code>.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Override this method when you need to check whether the view can be closed although the view is closable.<br>
	 * This could be useful if a view performs some actions and the user may not close the view during the performance of an action.
	 * 
	 * @since 1.0
	 */
	protected void close() {
		super.setVisible(false);
	}
	
	/**
	 * This method is invoked before the view is removed from its {@link ViewContainer}.
	 * <br><br>
	 * You can use this method if you need to remove something too that refers to the point at which the view is removed
	 * from its container.
	 * 
	 * @since 1.0
	 */
	protected void beforeRemove() {
	}
	
	/**
	 * Class to control the events of the components of a view.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class EventController implements MouseListener, ActionListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// if mouse entered the close button then hover the icon
			if(e.getComponent() == View.this.closeBtn)
				View.this.closeBtn.setIcon(Resources.getInstance().CLOSE_HOVER_ICON);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// if mouse exited the close button then reset icon
			if(e.getComponent() == View.this.closeBtn)
				View.this.closeBtn.setIcon(Resources.getInstance().CLOSE_ICON);
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// if close button is clicked then disable view
			if(e.getSource() == View.this.closeBtn)
				View.this.close();
		}
		
	}

}
