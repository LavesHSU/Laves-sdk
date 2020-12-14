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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.serialization.Serializer;

/**
 * Represents a group of components, such as {@link View}s or other {@link JComponent}s.
 * <br><br>
 * <br>Layout</b>:<br>
 * The group can layout the components horizontally or vertically. In an horizontal group you have horizontal and
 * in vertical groups you have vertical dividers (so called sashs), that means the user can adjust the size of the components
 * by dragging the sash between two components at runtime.
 * <br><br>
 * <b>Weights</b>:<br>
 * By invoking {@link #setWeights(float[])} you can set the weights of size in percent for the components they should occupy in the group.
 * E.g. you have two components and set the weights to 0.3 for component 1 and 0.7 to component 2. That means component 1 has 30% of the size
 * the group has and component 2 has 70%. If the group is resized (for example because the window is resized) the sizes of the components
 * were adjusted to the new size of the group.<br>
 * Use {@link #getWeights()} to get the current weights of the components in the group.<br>
 * You can use {@link #storeWeights(Serializer, String)} and {@link #restoreWeights(Serializer, String, float[])} to serialize and deserialize
 * the current weights of the group.
 * <br><br>
 * <b>Components</b>:<br>
 * To add components to the group use the <i>add</i> methods like {@link #add(Component)}.
 * 
 * @see View
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public final class ViewGroup extends JPanel {

	private static final long serialVersionUID = 1L;
	
	/** the orientation of the components in the group */
	private final int orientation;
	/** the padding meaning the free space between the group border and the inner components */
	private final int padding;
	/** controller for component events */
	private final EventController eventController;
	/** the components of the group */
	private final List<GroupComponent> components;
	/** a list of all currently visible components of the group */
	private final List<GroupComponent> visibleComponentsCache;
	/** a list of all sashs that are visible (must be redefined if {@link #visibleComponentsCache} is changed) */
	private final List<Sash> visibleSashs;
	/** the user defined weights for the children */
	private float[] weights;
	/** the weights of the visible components (<b>the array must have the size of {@link #visibleComponentsCache}</b>) */
	private float[] visibleWeights;
	/** flag that indicates if the {@link #visibleComponentsCache} has to be redefined */
	private boolean redefineVisibleComponents;
	/** flag that indicates if the {@link #visibleWeights} has to be redefined */
	private boolean redefineVisibleWeights;
	/** the sash that is currently moved by mouse */
	private Sash draggedSash;
	/** the current mouse position after a mouseDown event */
	private Point mousePos;
	/** the dragging cursor of this group */
	private final Cursor cursor;
	
	/** the orientation of the components is horizontal */
	public static final int HORIZONTAL = 0;
	/** the orientation of the components is vertical */
	public static final int VERTICAL = 1;
	
	/**
	 * Creates a new group.
	 * 
	 * @param orientation the orientation of the components which could be {@link #HORIZONTAL} or {@link #VERTICAL}
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if orientation is unequal {@link #HORIZONTAL} or {@link #VERTICAL}</li>
	 * </ul>
	 * @since 1.0
	 */
	public ViewGroup(final int orientation) throws IllegalArgumentException {
		this(orientation, 0);
	}
	
	/**
	 * Creates a new group.
	 * 
	 * @param orientation the orientation of the components which could be {@link #HORIZONTAL} or {@link #VERTICAL}
	 * @param padding the free space between the border of the group and the inner components
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if orientation is unequal {@link #HORIZONTAL} or {@link #VERTICAL}</li>
	 * </ul>
	 * @since 1.0
	 */
	public ViewGroup(final int orientation, final int padding) throws IllegalArgumentException {
		if(!checkOrientation(orientation))
			throw new IllegalArgumentException("No valid argument!");
		
		this.orientation = orientation;
		this.padding = (padding > 0) ? padding : 0;
		this.eventController = new EventController();
		this.components = new ArrayList<GroupComponent>();
		this.visibleComponentsCache = new ArrayList<GroupComponent>();
		this.visibleSashs = new ArrayList<Sash>();
		this.weights = null;
		this.visibleWeights = null;
		this.redefineVisibleComponents = true;
		this.redefineVisibleWeights = true;
		this.draggedSash = null;
		this.mousePos = null;
		this.cursor = Cursor.getPredefinedCursor((orientation == HORIZONTAL) ? Cursor.E_RESIZE_CURSOR : Cursor.N_RESIZE_CURSOR);
		
		// set base layout
		super.setLayout(null);
		
		// add controller for component events
		addComponentListener(eventController);
		addMouseListener(eventController);
		addMouseMotionListener(eventController);
	}
	
	/**
	 * Gets the orientation of the group.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #HORIZONTAL
	 * @see #VERTICAL
	 * @return the orientation
	 * @since 1.0
	 */
	public int getOrientation() {
		return orientation;
	}
	
	/**
	 * Gets the current weights of the components in this group.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the current weights
	 * @since 1.0
	 */
	public float[] getWeights() {
		if(EDT.isExecutedInEDT())
			return weights.clone();
		else
			return EDT.execute(new GuiRequest<float[]>() {
				@Override
				protected float[] execute() throws Throwable {
					return weights.clone();
				}
			});
	}
	
	/**
	 * Sets the weights of the components in this group.
	 * <br><br>
	 * E.g. you have two components and set the weights to 0.3 for component 1 and 0.7 to component 2. That means component 1 has 30% of the size
	 * the group has and component 2 has 70%. If the group is resized (for example because the window is resized) the sizes of the components
	 * were adjusted to the new size of the group.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param weights the weights (the sum of the weights must be 1 and the array must have the length of the number of components that this group has)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if weights is null</li>
	 * 		<li>if the length is not equal the number of components this group contains</li>
	 * 		<li>if the sum of the weights is not <code>1.0f</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public void setWeights(final float[] weights) throws IllegalArgumentException {
		if(weights == null)
			throw new IllegalArgumentException("No valid argument!");
		else if(weights.length != components.size())
			throw new IllegalArgumentException("Amount of weights must match to the amount of components!");
		
		if(EDT.isExecutedInEDT())
			internalSetWeights(weights);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setWeights") {
				@Override
				protected void execute() throws Throwable {
					internalSetWeights(weights);
				}
			});
	}
	
	/**
	 * The layout of a view group may not be changed meaning this method does nothing!
	 * 
	 * @param mgr the layout manager
	 * @since 1.0
	 */
	@Override
	public void setLayout(LayoutManager mgr) {
		// Do nothing! Layout may not be changed!
	}
	
	/**
	 * Stores the current weights of the components in the specified {@link Serializer}.
	 * 
	 * @param s the serializer
	 * @param key the data key
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if s is null</li>
	 * 		<li>if key is null</li>
	 * 		<li>if key already exists (and keys are not ovverridable)</li>
	 * </ul>
	 * @since 1.0
	 */
	public void storeWeights(final Serializer s, final String key) throws IllegalArgumentException {
		if(s == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final Float[] a = new Float[weights.length];
		
		for(int i = 0; i < a.length; i++)
			a[i] = weights[i];
		
		s.addArray(key, a);
	}
	
	/**
	 * Restores the weights of the components.
	 * 
	 * @param s the serializer that contains the weights
	 * @param key the data key
	 * @param def the default weights
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if s is null</li>
	 * 		<li>if def is null</li>
	 * 		<li>if the length of the stored or the default weights is not equal the number of components this group contains</li>
	 * 		<li>if the sum of the weights that should be restored is not <code>1.0f</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public void restoreWeights(final Serializer s, final String key, final float[] def) throws IllegalArgumentException {
		if(s == null || def == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final Float[] a = s.getArray(key);
		
		if(a == null)
			setWeights(def);
		else {
			final float[] weights = new float[a.length];
			for(int i = 0; i < weights.length; i++)
				weights[i] = a[i];
			setWeights(weights);
		}
	}
	
	/**
	 * The cursor of a view group may not be changed meaning this method does nothing!
	 * 
	 * @param c the cursor
	 * @since 1.0
	 */
	@Override
	public void setCursor(Cursor c) {
		// Do nothing! Cursor must not be changed!
	}
	
	/**
	 * The border of a view group may not be changed meaning this method does nothing!
	 * 
	 * @param b the border
	 * @since 1.0
	 */
	@Override
	public void setBorder(Border b) {
		// Do nothing! Border must not be changed!
	}
	
	/**
	 * Does the layout of the view group meaning the views and sub groups are arranged.
	 * 
	 * @since 1.0
	 */
	@Override
	public void doLayout() {
		final int dblPad = 2 * padding;
		
		// the visibility of components changed then redefine the currently visible components
		if(redefineVisibleComponents)
			requestVisibleComponents();
		
		// the group is only visible if at least one subcomponent is visible
		setVisible(visibleComponentsCache.size() > 0);
		
		// group is not visible? then break up!
		if(!isVisible())
			return;
		
		// get the weights of the visible components if necessary
		if(redefineVisibleComponents || redefineVisibleWeights || visibleWeights == null)
			visibleWeights = getVisibleWeights();
		
		if(orientation == HORIZONTAL) {
			final int totalWidth = getWidth() - (visibleSashs.size() * Sash.SIZE) - dblPad;
			int currX = padding;
			int compWidth;
			
			// calculate positions and widths of the components
			for(int i = 0; i < visibleComponentsCache.size() - 1; i++) {
				compWidth = (int)(totalWidth * visibleWeights[i]);
				visibleComponentsCache.get(i).setBounds(currX, padding, compWidth, getHeight() - dblPad);
				currX += compWidth + Sash.SIZE;
			}
			
			// the last component should fill out the rest of the panel
			// (a last component is always given [see condition at the beginning of doLayout()])
			visibleComponentsCache.get(visibleComponentsCache.size() - 1).setBounds(currX, padding, getWidth() - padding - currX, getHeight() - dblPad);
		}
		else {
			final int totalHeight = getHeight() - (visibleSashs.size() * Sash.SIZE) - dblPad;
			int currY = padding;
			int compHeight;
			
			// calculate positions and heights of the components
			for(int i = 0; i < visibleComponentsCache.size() - 1; i++) {
				compHeight = (int)(totalHeight * visibleWeights[i]);
				visibleComponentsCache.get(i).setBounds(padding, currY, getWidth() - dblPad, compHeight);
				currY += compHeight + Sash.SIZE;
			}
			
			// the last component should fill out the rest of the panel
			// (a last component is always given [see condition at the beginning of doLayout()])
			visibleComponentsCache.get(visibleComponentsCache.size() - 1).setBounds(padding, currY, getWidth() - dblPad, getHeight() - padding - currY);
		}
		
		// rest flags
		redefineVisibleComponents = false;
		redefineVisibleWeights = false;
	}
	
	/**
	 * Removes a component from the view group.
	 * 
	 * @param index the index of the component that should be removed
	 * @since 1.0
	 */
	@Override
	public void remove(int index) {
		super.remove(index);
		
		// remove the component listener from the component
		components.get(index).getComponent().removeComponentListener(eventController);
		
		// remove the component from the list of group components
		components.remove(index);
		// update indices
		for(int i = index; i < components.size(); i++)
			components.get(i).setIndex(i);
		
		// the weights are not valid anymore
		weights = null;
		
		// the visible components cache must be redefined
		redefineVisibleComponents = true;
	}
	
	@Override
	protected void addImpl(Component c, Object constraints, int index) {
		super.addImpl(c, constraints, index);
		
		// add the component to the group
		if(index >= 0) {
			components.add(index, new GroupComponent(c));
			
			// update indices
			for(int i = index; i < components.size(); i++)
				components.get(i).setIndex(i);
		}
		else {
			final GroupComponent gc = new GroupComponent(c);
			gc.setIndex(components.size());
			components.add(gc);
		}
		
		// the weights are not valid anymore
		weights = null;
		
		// the visible components cache must be redefined
		redefineVisibleComponents = true;
		
		// listen to component events
		c.addComponentListener(eventController);
	}
	
	/**
	 * Checks if the given orientation correspond to {@link #HORIZONTAL} or {@link #VERTICAL}.
	 * 
	 * @param orientation the orientation
	 * @return <code>true</code> if the orientation is valid otherwise <code>false</code>
	 * @since 1.0
	 */
	private static boolean checkOrientation(final int orientation) {
		return orientation == HORIZONTAL || orientation == VERTICAL;
	}
	
	/**
	 * Processes a mouseDown event.
	 * 
	 * @param e {@link MouseEvent}
	 * @since 1.0
	 */
	private void mouseDown(final MouseEvent e) {
		mousePos = new Point(e.getX(), e.getY());
		draggedSash = getSashFromPosition(mousePos.x, mousePos.y);
	}
	
	/**
	 * Processes a mouseUp event.
	 * 
	 * @param e {@link MouseEvent}
	 * @since 1.0
	 */
	private void mouseUp(final MouseEvent e) {
		draggedSash = null;
	}
	
	/**
	 * Processes a mouseDragged event.
	 * 
	 * @param e {@link MouseEvent}
	 * @since 1.0
	 */
	private void mouseDragged(final MouseEvent e) {
		if(mousePos == null || draggedSash == null)
			return;
		
		final Point newMousePos = new Point(e.getX(), e.getY());

		if(orientation == HORIZONTAL)
			draggedSash.moveHorizontal(newMousePos.x - mousePos.x);
		else
			draggedSash.moveVertical(newMousePos.y - mousePos.y);
		
		mousePos = newMousePos;
	}
	
	/**
	 * Processes a mouseMoved event.
	 * 
	 * @param e {@link MouseEvent}
	 * @since 1.0
	 */
	private void mouseMoved(final MouseEvent e) {
		// there is currently a drag action then break up to not reset the cursor
		if(draggedSash != null)
			return;
		
		if(getSashFromPosition(e.getX(), e.getY()) != null)
			super.setCursor(cursor);
		else
			super.setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * Creates default weights that means the weight of each component is <code>1 / number of components</code>.
	 * 
	 * @since 1.0
	 */
	private void createDefaultWeights() {
		weights = new float[components.size()];
		final float weight = 1.0f / weights.length;
		float sum = 0.0f;
		
		for(int i = 0; i < weights.length - 1; i++) {
			weights[i] = weight;
			sum += weight;
		}
		
		weights[weights.length - 1] = 1.0f - sum;
	}
	
	/**
	 * Requests the currently visible components of this group and saves them in {@link #visibleComponentsCache}.<br>
	 * Furthermore all {@link Sash}s between the visible components are created and saved in {@link #visibleSashs}.
	 * 
	 * @since 1.0
	 */
	private void requestVisibleComponents() {
		visibleComponentsCache.clear();
		visibleSashs.clear();
		
		for(GroupComponent c : components)
			if(c.isVisible())
				visibleComponentsCache.add(c);
		
		for(int i = 1; i < visibleComponentsCache.size(); i++)
			visibleSashs.add(new Sash(visibleComponentsCache.get(i - 1), visibleComponentsCache.get(i)));
	}
	
	/**
	 * Gets the weights of the currently visible components.
	 * <br><br>
	 * The weights are automatically adapted and extended by the unused weights (that means weights of invisible components).
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The list of visible components ({@link #visibleComponentsCache}) must be known!
	 * 
	 * @return the weights of the visible components
	 * @since 1.0
	 */
	private float[] getVisibleWeights() {
		final float[] visibleWeights = new float[visibleComponentsCache.size()];
		
		if(visibleWeights.length < 1)
			return visibleWeights;
		
		// if no weights were defined, create them
		if(weights == null)
			createDefaultWeights();
		
		GroupComponent c;
		float freeWeight = 0.0f;
		
		// assign the user defined weights of the components and calculate the free weight (this is the sum
		// of weights of invisible components)
		for(int i = 0, j = 0; i < components.size(); i++) {
			c = components.get(i);
			
			if(c.isVisible()) {
				visibleWeights[j] = weights[i];
				j++;
			}
			else
				freeWeight += weights[i];
		}
		
		// extend the visible weights
		final float extWeight = freeWeight / visibleWeights.length;
		float sum = 0.0f;
		for(int i = 0; i < visibleWeights.length - 1; i++) {
			visibleWeights[i] += extWeight;
			sum += visibleWeights[i];
		}
		
		// calculate weight of last component separately to avoid rounding errors
		visibleWeights[visibleWeights.length - 1] = 1.0f - sum;
		
		return visibleWeights;
	}
	
	/**
	 * Calculates the weights by the current sizes of the components.
	 * 
	 * @since 1.0
	 */
	private void adjustWeights() {
		if(visibleComponentsCache.size() < 1)
			return;
		
		if(orientation == HORIZONTAL) {
			final int totalWidth = getWidth() - (visibleSashs.size() * Sash.SIZE);
			float sum = 0.0f;
			
			for(int i = 0; i < visibleComponentsCache.size() - 1; i++) {
				visibleWeights[i] = (float)visibleComponentsCache.get(i).getSize().width / totalWidth;
				sum += visibleWeights[i];
			}
			
			if(visibleWeights.length > 0)
				visibleWeights[visibleWeights.length - 1] = 1.0f - sum;
		}
		else {
			final int totalHeight = getHeight() - (visibleSashs.size() * Sash.SIZE);
			float sum = 0.0f;
			
			for(int i = 0; i < visibleComponentsCache.size() - 1; i++) {
				visibleWeights[i] = (float)visibleComponentsCache.get(i).getSize().height / totalHeight;
				sum += visibleWeights[i];
			}
			
			if(visibleWeights.length > 0)
				visibleWeights[visibleWeights.length - 1] = 1.0f - sum;
		}
		
		// update the original weights of the components
		GroupComponent gc1;
		GroupComponent gc2;
		float visibleWeightGC1;
		float visibleWeightGC2;
		float intermediateWeight = 0.0f;
		float adjust;
		for(int i = 1; i < visibleComponentsCache.size(); i++) {
			gc1 = visibleComponentsCache.get(i - 1);
			visibleWeightGC1 = visibleWeights[i - 1];
			gc2 = visibleComponentsCache.get(i);
			visibleWeightGC2 = visibleWeights[i];
			
			// determine the intermediate weights of invisible components
			for(int j = gc1.getIndex() + 1; j <= gc2.getIndex() - 1; j++)
				intermediateWeight += weights[j];
			
			// compute the adjust value that is subtracted from each original weight of the current two components
			adjust = (intermediateWeight != 0.0f) ? intermediateWeight / 2.0f : 0.0f;
			
			weights[gc1.getIndex()] = visibleWeightGC1 - adjust;
			weights[gc2.getIndex()] = visibleWeightGC2 - adjust;
		}
		
		// ensure that the sum of all weights is 100%
		float sum = 0.0f;
		for(int i = 0; i < weights.length - 1; i++)
			sum += weights[i];
		if(weights.length > 0)
			weights[weights.length - 1] = 1.0f - sum;
	}
	
	/**
	 * Gets a sash from the given position.
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @return {@link Sash} or <code>null</code> if there is no sash at the specified position
	 * @since 1.0
	 */
	private Sash getSashFromPosition(final int x, final int y) {
		for(Sash s : visibleSashs) {
			if(orientation == HORIZONTAL && x >= s.getX() && x <= s.getX() + Sash.SIZE)
				return s;
			else if(orientation == VERTICAL && y >= s.getY() && y <= s.getY() + Sash.SIZE)
				return s;
		}
		
		return null;
	}
	
	/**
	 * Sets the weights of the components in this group.
	 * <br><br>
	 * E.g. you have two components and set the weights to 0.3 for component 1 and 0.7 to component 2. That means component 1 has 30% of the size
	 * the group has and component 2 has 70%. If the group is resized (for example because the window is resized) the sizes of the components
	 * were adjusted to the new size of the group.
	 * 
	 * @param weights <b>valid</b> weights (the sum of the weights must be 1 and the array must have the length of the number of components that this group has)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the sum of the weights is not <code>1.0f</code></li>
	 * </ul>
	 * @since 1.0
	 */
	private void internalSetWeights(final float[] weights) throws IllegalArgumentException {
		float sum = 0.0f;
		for(int i = 0; i < weights.length; i++)
			sum += weights[i];
		
		if(sum != 1.0f)
			throw new IllegalArgumentException("The sum of weights is not 1! Ensure that the total of weights is always 1!");
		
		this.weights = weights;
		
		// new weights that means redefine the visible ones
		redefineVisibleWeights = true;
		validate();
	}
	
	/**
	 * Wrapper for a component in the view group to separate the components real data and the ones computed by the group.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 */
	private final class GroupComponent {
		
		/** the component of the element */
		private final Component component;
		/** the x position of the component */
		private int x;
		/** the y position of the component */
		private int y;
		/** the size of the component */
		private Dimension size;
		/** the index of the component in the list of all components */
		private int index;
		
		/**
		 * Creates a new group component.
		 * 
		 * @param component the component
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if component is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public GroupComponent(final Component component) throws IllegalArgumentException {
			if(component == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.component = component;
			this.x = component.getX();
			this.y = component.getY();
			this.size = component.getSize();
			this.index = -1;
		}
		
		/**
		 * Gets the component of the element.
		 * 
		 * @return the component
		 * @since 1.0
		 */
		public final Component getComponent() {
			return component;
		}
		
		/**
		 * Gets the index of the component in the list of all components in the group.
		 * 
		 * @return the index of the component in the list or <code>-1</code> if the index was not set yet
		 * @since 1.0
		 */
		public final int getIndex() {
			return index;
		}
		
		/**
		 * Indicates whether the component is visible in the group.
		 * 
		 * @return <code>true</code> if the component is visible otherwise <code>false</code>
		 * @since 1.0
		 */
		public boolean isVisible() {
			return component.isVisible();
		}
		
		/**
		 * Gets the x position of the component in the group.
		 * 
		 * @return the x position
		 * @since 1.0
		 */
		public int getX() {
			return x;
		}
		
		/**
		 * Gets the y position of the component in the group.
		 * 
		 * @return the y position
		 * @since 1.0
		 */
		public int getY() {
			return y;
		}
		
		/**
		 * Gets the location of the component as a point.
		 * 
		 * @return the point of the component's left top corner
		 * @since 1.0
		 */
		public Point getLocation() {
			return new Point(x, y);
		}
		
		/**
		 * Gets the size of the component in the group.
		 * 
		 * @return the size
		 * @since 1.0
		 */
		public Dimension getSize() {
			return size;
		}
		
		/**
		 * Sets the size of the component in the group.
		 * 
		 * @param size the size
		 * @since 1.0
		 */
		public void setSize(final Dimension size) throws IllegalArgumentException {
			if(size == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.size = size;
			setBounds(x, y, size.width, size.height);
		}
		
		/**
		 * Sets the size of the component in the group.
		 * 
		 * @param width the width
		 * @param height the height
		 * @since 1.0
		 */
		public void setSize(final int width, final int height) {
			setSize(new Dimension(width, height));
		}
		
		/**
		 * Sets the bounds of the element.
		 * 
		 * @param bounds the bounds
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if bounds is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public void setBounds(final Rectangle bounds) throws IllegalArgumentException {
			if(bounds == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.x = bounds.x;
			this.y = bounds.y;
			this.size = new Dimension(bounds.width, bounds.height);
			component.setBounds(x, y, size.width, size.height);
		}
		
		/**
		 * Sets the bounds of the element.
		 * 
		 * @param x the x position
		 * @param y the y position
		 * @param width the width
		 * @param height the height
		 * @since 1.0
		 */
		public void setBounds(final int x, final int y, final int width, final int height) {
			setBounds(new Rectangle(x, y, width, height));
		}
		
		/**
		 * Validates the component.
		 * 
		 * @since 1.0
		 */
		public void validate() {
			component.validate();
		}
		
		/**
		 * Sets the index of the component in the list of all components of the group.
		 * 
		 * @param index the index
		 * @since 1.0
		 */
		void setIndex(final int index) {
			this.index = index;
		}
		
	}
	
	/**
	 * Represents a sash between two components of a {@link ViewGroup}.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 */
	private final class Sash {
		
		/** the first (left or top) component of the sash */
		public final GroupComponent firstComponent;
		/** the second (right or bottom) component of the sash */
		public final GroupComponent secondComponent;
		
		/** the size of a divider */
		public static final int SIZE = 5;
		
		/**
		 * Creates a new sash.
		 * 
		 * @param firstComponent the first (left or top) component
		 * @param secondComponent the second (right or bottom) component
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if firstComponent is null</li>
		 * 		<li>if secondComponent is null</li>
		 * </ul>
		 */
		public Sash(final GroupComponent firstComponent, final GroupComponent secondComponent) throws IllegalArgumentException {
			if(firstComponent == null || secondComponent == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.firstComponent = firstComponent;
			this.secondComponent = secondComponent;
		}
		
		/**
		 * Gets the x position of the sash in the group.
		 * 
		 * @return the x position
		 * @since 1.0
		 */
		public int getX() {
			if(ViewGroup.this.orientation == ViewGroup.HORIZONTAL)
				return secondComponent.getX() - SIZE;
			else
				return 0;
		}
		
		/**
		 * Gets the y position of the sash in the group.
		 * 
		 * @return the y position
		 * @since 1.0
		 */
		public int getY() {
			if(ViewGroup.this.orientation == ViewGroup.HORIZONTAL)
				return 0;
			else
				return secondComponent.getY() - SIZE;
		}
		
		/**
		 * Moves the sash horizontal.
		 * 
		 * @param diff the distance to move
		 * @since 1.0
		 */
		public void moveHorizontal(int diff) {
			final Dimension firstCompSize = firstComponent.getSize();
			final Dimension secondCompSize = secondComponent.getSize();
			final Point secondCompPos = secondComponent.getLocation();
			
			// the sash has not to be moved over the limits of the components
			if(firstCompSize.width + diff < 0 || secondCompSize.width - diff < 0)
				diff = 0;
			
			firstComponent.setSize(firstCompSize.width + diff, firstCompSize.height);
			secondComponent.setBounds(secondCompPos.x + diff, secondCompPos.y, secondCompSize.width - diff, secondCompSize.height);
			
			// adjust the weights because the sizes of the components have changed
			ViewGroup.this.adjustWeights();

			// both components must adjust the sizes of their children
			firstComponent.validate();
			secondComponent.validate();
		}
		
		/**
		 * Moves the sash vertical.
		 * 
		 * @param diff the distance to move
		 * @since 1.0
		 */
		public void moveVertical(int diff) {
			final Dimension firstCompSize = firstComponent.getSize();
			final Dimension secondCompSize = secondComponent.getSize();
			final Point secondCompPos = secondComponent.getLocation();
			
			// the sash has not to be moved over the limits of the components
			if(firstCompSize.height + diff < 0 || secondCompSize.height - diff < 0)
				diff = 0;
			
			firstComponent.setSize(firstCompSize.width, firstCompSize.height + diff);
			secondComponent.setBounds(secondCompPos.x, secondCompPos.y + diff, secondCompSize.width, secondCompSize.height - diff);
			
			// adjust the weights because the sizes of the components have changed
			ViewGroup.this.adjustWeights();
			
			// both components must adjust the sizes of their children
			firstComponent.validate();
			secondComponent.validate();
		}
		
	}
	
	/**
	 * The central event controller of component and mouse events.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 */
	private class EventController implements ComponentListener, MouseListener, MouseMotionListener {

		@Override
		public void componentHidden(ComponentEvent e) {
			// a sub component (view) is set invisible
			ViewGroup.this.redefineVisibleComponents = true;
			ViewGroup.this.revalidate();
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentResized(ComponentEvent e) {
			// we only want to listen to resize events of the view group
			if(e.getComponent() != ViewGroup.this)
				return;
			
			// the layout has to be recalculated
			// (important: use validate() instead of revalidate() otherwise the sashs cannot be moved)
			ViewGroup.this.validate();
		}

		@Override
		public void componentShown(ComponentEvent e) {
			// a sub component (view) is visible again
			ViewGroup.this.redefineVisibleComponents = true;
			ViewGroup.this.revalidate();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// if the mouse leaves the group reset cursor but only if there is no drag action
			// (otherwise it could be that the resize cursor is visible on other components inside the group)
			if(ViewGroup.this.draggedSash == null)
				ViewGroup.super.setCursor(Cursor.getDefaultCursor());
		}

		@Override
		public void mousePressed(MouseEvent e) {
			ViewGroup.this.mouseDown(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			ViewGroup.this.mouseUp(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			ViewGroup.this.mouseDragged(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			ViewGroup.this.mouseMoved(e);
		}
		
	}
}