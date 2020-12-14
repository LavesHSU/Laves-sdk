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

package lavesdk.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

/**
 * A group of {@link BooleanProperty}s.
 * <br><br>
 * In a group is only one property selected meaning that only one {@link BooleanProperty} has the value <code>true</code> the rest of the properties
 * in the group has the value <code>false</code>.<br>
 * This is equal to {@link JRadioButton}s in a {@link ButtonGroup}.
 * 
 * @author jdornseifer
 * @since 1.0
 * @since 1.0
 */
public class BooleanPropertyGroup {
	
	/** the corresponding {@link PropertiesListModel} */
	private final PropertiesListModel model;
	/** the properties in the group */
	private final List<BooleanProperty> properties;
	
	/**
	 * Creates a new group.
	 * 
	 * @param model the corresponding properties list model
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if model is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public BooleanPropertyGroup(final PropertiesListModel model) throws IllegalArgumentException {
		if(model == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.model = model;
		this.properties = new ArrayList<BooleanProperty>(3);
	}
	
	/**
	 * Adds a boolean property to the group.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param p the property
	 * @since 1.0
	 */
	void add(final BooleanProperty p) {
		if(p == null || properties.contains(p))
			return;
		
		properties.add(p);
	}
	
	/**
	 * Updates the properties of the group.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param p the property whose value changed (selected)
	 * @since 1.0
	 */
	void update(final BooleanProperty p) {
		if(p == null || !properties.contains(p))
			return;
		
		for(BooleanProperty bp : properties)
			bp.setValue(false);
		
		p.setValue(true);
		
		if(model.getList() != null)
			model.getList().repaint();
	}

}
