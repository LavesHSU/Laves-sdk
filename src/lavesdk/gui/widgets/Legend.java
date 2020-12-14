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
 * Class:		Legend
 * Task:		Representation of a legend
 * Created:		09.12.13
 * LastChanges:	12.12.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;

/**
 * Represents a legend that consists of {@link LegendItem}.
 * <br><br>
 * Use {@link #add(LegendItem)} to add items to the legend (<b>notice</b>: no other components than {@link LegendItem}s
 * are accepted to add to the legend) and use {@link #getItem(String)} to get a legend item from a specific name.
 * <br><br>
 * A legend can contain items or groups of items. Items without a group appear at the beginning of the legend and
 * items with a related group appear below the group header. The group header can be expanded and collapsed by the user
 * clicking on the toggle symbole of the group.
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class Legend extends JComponent {

	private static final long serialVersionUID = 1L;
	
	/** the mapping between the item name and the legend item */
	private final Map<String, LegendItem> items;
	/** the container of the {@link #itemPanel} */
	private final JPanel panelContainer;
	/** the panel that contains the items of the legend */
	private final JPanel itemPanel;
	/** the scroll pane */
	private final JScrollPane scrollPane;
	/** flag that indicates whether the legend is initialized */
	private boolean initialized;
	/** the background of a group */
	private Color groupBackground;
	/** the foreground of a group */
	private Color groupForeground;
	
	/**
	 * Creates a new legend.
	 * 
	 * @since 1.0
	 */
	public Legend() {
		super();
		
		initialized = false;
		items = new HashMap<String, LegendItem>();
		panelContainer = new JPanel();
		itemPanel = new JPanel();
		scrollPane = new JScrollPane(panelContainer);
		groupBackground = Color.white;
		groupForeground = new Color(40, 80, 180);
		
		panelContainer.setLayout(null);
		itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
		panelContainer.setBackground(Color.white);
		itemPanel.setBackground(Color.white);
		scrollPane.setBackground(Color.white);
		
		itemPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
		super.setFont(UIManager.getFont("Label.font"));
		
		panelContainer.add(itemPanel);
		
		super.setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		
		initialized = true;
		
		panelContainer.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				Legend.this.itemPanel.setSize(panelContainer.getWidth(), itemPanel.getPreferredSize().height);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}
	
	/**
	 * Gets the legend item of a specific name.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param itemName the name of the item
	 * @return the legend item or <code>null</code> if the legend does not contain an item with the given name
	 * @since 1.0
	 */
	public final LegendItem getItem(final String itemName) {
		if(EDT.isExecutedInEDT())
			return items.get(itemName);
		else
			return EDT.execute(new GuiRequest<LegendItem>() {
				@Override
				protected LegendItem execute() throws Throwable {
					return items.get(itemName);
				}
			});
	}
	
	/**
	 * Adds a new item to the legend.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param item the item
	 * @since 1.0
	 */
	public void add(final LegendItem item) {
		if(EDT.isExecutedInEDT())
			addImpl(item, null, -1);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".add") {
				@Override
				protected void execute() throws Throwable {
					addImpl(item, null, -1);
				}
			});
	}
	
	/**
	 * Adds a new component to the legend.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The only component that is allowed to be added to the legends is a {@link LegendItem}. Add another
	 * components ends in an {@link IllegalArgumentException}!
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param comp the component
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if comp is not of type {@link LegendItem}</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public Component add(final Component comp) {
		if(EDT.isExecutedInEDT())
			return super.add(comp);
		else
			return EDT.execute(new GuiRequest<Component>() {
				@Override
				protected Component execute() throws Throwable {
					return Legend.super.add(comp);
				}
			});
	}
	
	/**
	 * Gets the background color of the groups in the legend.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the background
	 * @since 1.0
	 */
	public Color getGroupBackground() {
		if(EDT.isExecutedInEDT())
			return groupBackground;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return groupBackground;
				}
			});
	}
	
	/**
	 * Sets the background color of the groups in the legend.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param color the background
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if color is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setGroupBackground(final Color color) throws IllegalArgumentException {
		if(color == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT()) {
			groupBackground = color;
			updateGroupColors();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setGroupBackground") {
				@Override
				protected void execute() throws Throwable {
					groupBackground = color;
					updateGroupColors();
				}
			});
	}
	
	/**
	 * Gets the foreground color of the groups in the legend.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the foreground
	 * @since 1.0
	 */
	public Color getGroupForeground() {
		if(EDT.isExecutedInEDT())
			return groupForeground;
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return groupForeground;
				}
			});
	}
	
	/**
	 * Sets the foreground color of the groups in the legend.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param color the foreground
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if color is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setGroupForeground(final Color color) throws IllegalArgumentException {
		if(color == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT()) {
			groupForeground = color;
			updateGroupColors();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setGroupForeground") {
				@Override
				protected void execute() throws Throwable {
					groupForeground = color;
					updateGroupColors();
				}
			});
	}
	
	/**
	 * Gets the font of the legend.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the font
	 * @since 1.0
	 */
	public Font getFont() {
		if(EDT.isExecutedInEDT())
			return super.getFont();
		else
			return EDT.execute(new GuiRequest<Font>() {
				@Override
				protected Font execute() throws Throwable {
					return Legend.super.getFont();
				}
			});
	}
	
	/**
	 * Sets the font of the legend.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param font the font
	 * @since 1.0
	 */
	@Override
	public void setFont(final Font font) {
		if(EDT.isExecutedInEDT()) {
			super.setFont(font);
			for(Component c : itemPanel.getComponents())
				c.setFont(font);
			updateSize();
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setFont") {
				@Override
				protected void execute() throws Throwable {
					Legend.super.setFont(font);
					for(Component c : itemPanel.getComponents())
						c.setFont(font);
					updateSize();
				}
			});
	}
	
	/**
	 * Removes all items from the legend.
	 * 
	 * @since 1.0
	 */
	@Override
	public void removeAll() {
		itemPanel.removeAll();
		items.clear();
	}
	
	/**
	 * The layout of a legend may not be changed meaning this method does nothing!
	 * 
	 * @param mgr the layout manager
	 * @since 1.0
	 */
	@Override
	public void setLayout(LayoutManager mgr) {
		// this is not allowed
	}
	
	/**
	 * Toggles the given group meaning that the items that relate to the group are set visible/invisible
	 * if the group is expanded/collapsed now.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @param group the group
	 * @since 1.0
	 */
	void toggleGroup(final LegendItemGroup group) {
		LegendItem li;
		
		// set the visible state of all legend items that are related to the group
		for(Component c : itemPanel.getComponents()) {
			if(c instanceof LegendItem) {
				li = (LegendItem)c;
				
				if(li.getGroupName().equals(group.getGroupName()))
					li.setVisible(group.isExpanded());
			}
		}
		
		updateSize();
	}
	
	@Override
	protected void addImpl(Component comp, Object constraints, int index) {
		if(!initialized)
			super.addImpl(comp, constraints, index);
		else if(comp instanceof LegendItem) {
			final LegendItem li = (LegendItem)comp;
			
			if(items.containsKey(li.getItemName()))
				throw new IllegalArgumentException("legend already contains an item with the name " + li.getItemName());
			
			items.put(li.getItemName(), li);
			
			// get the index of the last group element
			final int lastGroupItemIndex = getLastItemIndexOfGroup(li.getGroupName());
			
			// item is not associated with a group? then add it at the beginning (default group) of the legend
			if(li.getGroupName().isEmpty())
				itemPanel.add(comp, (lastGroupItemIndex >= 0) ? lastGroupItemIndex + 1 : 0);
			else {
				// item is associated with a group? then add the item as the last one or otherwise
				// add a new group at the end of the legend
				if(lastGroupItemIndex >= 0)
					itemPanel.add(comp, lastGroupItemIndex + 1);
				else {
					// first add the new group and second the item at the end of the legend (-1)
					itemPanel.add(new LegendItemGroup(this, li.getGroupName()), -1);
					itemPanel.add(comp, -1);
				}
			}
			
			// set the current font of the legend
			li.setFont(getFont());
			
			updateSize();
		}
	}
	
	/**
	 * Updates the size of the item panel.
	 * 
	 * @since 1.0
	 */
	private void updateSize() {
		final Dimension dim = itemPanel.getPreferredSize();

		itemPanel.setSize(panelContainer.getWidth(), dim.height);
		panelContainer.setPreferredSize(new Dimension(dim.width, dim.height));
		panelContainer.revalidate();
	}
	
	/**
	 * Gets the index of the last member of a given group.
	 * 
	 * @param groupName the group name
	 * @return the index of the last item in the group or <code>-1</code> if there is no group with the given name
	 * @since 1.0
	 */
	private int getLastItemIndexOfGroup(final String groupName) {
		Component c;
		LegendItem currItem;
		boolean groupNameExist = false;
		int lastIndex = -1;
		
		for(int i = 0; i < itemPanel.getComponentCount(); i++) {
			c = itemPanel.getComponent(i);
			
			if(c instanceof LegendItem)
				currItem = (LegendItem)c;
			else
				currItem = null;
			
			if(groupNameExist && (currItem == null || !currItem.getGroupName().equals(groupName)))
				break;
			else if(currItem != null && currItem.getGroupName().equals(groupName)) {
				groupNameExist = true;
				lastIndex = i;
			}
		}
		
		return lastIndex;
	}
	
	/**
	 * Updates the colors of all groups in the legend.
	 * 
	 * @since 1.0
	 */
	private void updateGroupColors() {
		LegendItemGroup lig;
		
		for(Component c : getComponents()) {
			if(c instanceof LegendItemGroup) {
				lig = (LegendItemGroup)c;
				lig.setBackground(groupBackground);
				lig.setForeground(groupForeground);
			}
		}
	}

}
