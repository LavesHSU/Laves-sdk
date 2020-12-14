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
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import lavesdk.algorithm.plugin.AlgorithmPlugin;

/**
 * Represents the container in which a {@link AlgorithmPlugin} is shown.
 * <br><br>
 * <b>Layout</b>:<br>
 * Initial the container has a <i>NullLayout</i>, set your own layout by invoking {@link #setLayout(java.awt.LayoutManager)}.
 * <br><br>
 * <b>Components</b>:<br>
 * The container only accepts {@link ViewGroup}s or (subclasses of) {@link View}s as components that are
 * displayed in this container. All other components such as {@link JPanel}, {@link JLabel}, etc. are ignored or must be integrated
 * in a custom view.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public final class ViewContainer extends JPanel {

	private static final long serialVersionUID = 1L;
	
	/** the border to display the gap between inner components and the container border */
	private final Border paddingBorder;
	
	/** the default padding of the container which means the gap between inner components and the container border */
	private static final int DEF_PADDING = 5;
	
	/**
	 * Creates a new container.
	 * 
	 * @since 1.0
	 */
	public ViewContainer() {
		this(DEF_PADDING);
	}
	
	/**
	 * Creates a new container.
	 * 
	 * @param padding the gap between inner components and the container border
	 * @since 1.0
	 */
	public ViewContainer(final int padding) throws IllegalArgumentException {
		super();
		
		if(padding < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		this.paddingBorder = (padding > 0) ? BorderFactory.createEmptyBorder(padding, padding, padding, padding) : null;
		
		super.setLayout(null);
		super.setBorder(paddingBorder);
	}
	
	/**
	 * Sets the border of the container.
	 * 
	 * @param b the border
	 * @since 1.0
	 */
	@Override
	public void setBorder(Border b) {
		// if we have a padding then we need to create a compound border to obtain the padding
		if(paddingBorder != null)
			super.setBorder((b != null) ? BorderFactory.createCompoundBorder(b, paddingBorder) : paddingBorder);
		else
			super.setBorder(b);
	}
	
	/**
	 * Removes a component from the container.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If the component is a {@link View} then it is invoked {@link View#beforeRemove()}.
	 * 
	 * @since 1.0
	 */
	@Override
	public void remove(int index) {
		final Component c = getComponent(index);

		if(c instanceof View)
			((View)c).beforeRemove();
		
		super.remove(index);
	}
	
	/**
	 * Queries all views that this container contains.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * The views are not cached, that means every time this method is invoked the whole
	 * component structure of this container is checked. So this method can consume time.
	 * 
	 * @return all views in this container
	 * @since 1.0
	 */
	public List<View> queryAllViews() {
		final List<View> views = new ArrayList<View>();
		
		// query all views in this container
		queryAllViews(this, views);
		
		return views;
	}
	
	@Override
	protected void addImpl(Component c, Object constraints, int index) {
		// ignore all components that are not of type ViewGroup or View
		if(!(c instanceof ViewGroup) && !(c instanceof View))
			return;
		
		super.addImpl(c, constraints, index);
	}
	
	/**
	 * Queries all views that this container contains.
	 * 
	 * @param container the current container
	 * @param views the list of current views
	 * @since 1.0
	 */
	private void queryAllViews(final Container container, final List<View> views) {
		// run through all components of the current container
		for(Component c : container.getComponents()) {
			// component is a view then add it to the list
			if(c instanceof View)
				views.add((View)c);
			
			// component itself is a container then check the children
			if(c instanceof Container)
				queryAllViews((Container)c, views);
		}
	}

}
