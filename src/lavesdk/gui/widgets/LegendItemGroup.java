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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.resources.Resources;

/**
 * Represents a group header of a {@link LegendItem} group.
 * <br><br>
 * <b>Attention</b>:<br>
 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS CLASS</i>!
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
class LegendItemGroup extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/** the associated legend */
	private final Legend legend;
	/** the label for the text of the group */
	private final JLabel groupLbl;
	/** the expand/collapse button of the group */
	private final JLabel groupBtn;
	/** flag that indicates whether the group is initialized */
	private boolean initialized;
	/** flag that indicates of the group is expanded */
	private boolean expanded;
	
	/**
	 * Creates a new item group.
	 * 
	 * @param groupName the group name
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>legend is null</li>
	 * 		<li>groupName is null</li>
	 * 		<li>groupName is empty</li>
	 * </ul>
	 * @since 1.0
	 */
	public LegendItemGroup(final Legend legend, final String groupName) throws IllegalArgumentException {
		if(legend == null || groupName == null || groupName.isEmpty())
			throw new IllegalArgumentException("No valid argument!");
		
		this.initialized = false;
		this.legend = legend;
		this.groupLbl = new JLabel(groupName);
		this.expanded = true;
		this.groupBtn = new JLabel(Resources.getInstance().COLLAPSE_ICON);
		
		super.setLayout(new BorderLayout());
		add(groupLbl, BorderLayout.CENTER);
		add(groupBtn, BorderLayout.EAST);
		
		setBackground(legend.getGroupBackground());
		setForeground(legend.getGroupForeground());
		
		final Font f = legend.getFont();
		if(f != null)
			setFont(f);
		
		this.initialized = true;
		
		groupBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		groupBtn.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				LegendItemGroup.this.toggle();
			}
		});
	}
	
	/**
	 * Gets the name of the group.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the name of the group
	 * @since 1.0
	 */
	public final String getGroupName() {
		if(EDT.isExecutedInEDT())
			return groupLbl.getText();
		else
			return EDT.execute(new GuiRequest<String>() {
				@Override
				protected String execute() throws Throwable {
					return groupLbl.getText();
				}
			});
	}
	
	/**
	 * Gets the background color of the item group.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the background color
	 * @since 1.0
	 */
	@Override
	public Color getBackground() {
		if(EDT.isExecutedInEDT())
			return super.getBackground();
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return LegendItemGroup.super.getBackground();
				}
			});
	}
	
	/**
	 * Sets the background color of the item group.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param bg the background color
	 * @since 1.0
	 */
	@Override
	public void setBackground(final Color bg) {
		if(EDT.isExecutedInEDT())
			super.setBackground(bg);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setBackground") {
				@Override
				protected void execute() throws Throwable {
					LegendItemGroup.super.setBackground(bg);
				}
			});
	}
	
	/**
	 * Gets the foreground color of the item group.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the foreground color
	 * @since 1.0
	 */
	@Override
	public Color getForeground() {
		if(EDT.isExecutedInEDT())
			return super.getForeground();
		else
			return EDT.execute(new GuiRequest<Color>() {
				@Override
				protected Color execute() throws Throwable {
					return LegendItemGroup.super.getForeground();
				}
			});
	}
	
	/**
	 * Sets the foreground color of the item group.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param fg the foreground color
	 * @since 1.0
	 */
	@Override
	public void setForeground(final Color fg) {
		if(EDT.isExecutedInEDT())
			internalSetForeground(fg);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setForeground") {
				@Override
				protected void execute() throws Throwable {
					internalSetForeground(fg);
				}
			});
	}
	
	/**
	 * Indicates whether the group is expanded.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the group is expanded otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean isExpanded() {
		if(EDT.isExecutedInEDT())
			return expanded;
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return expanded;
				}
			});
	}
	
	/**
	 * Gets the font of the group.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the font
	 * @since 1.0
	 */
	public Font getFont() {
		if(EDT.isExecutedInEDT())
			return (groupLbl != null) ? groupLbl.getFont() : super.getFont();
		else
			return EDT.execute(new GuiRequest<Font>() {
				@Override
				protected Font execute() throws Throwable {
					return (groupLbl != null) ? groupLbl.getFont() : LegendItemGroup.super.getFont();
				}
			});
	}
	
	/**
	 * Sets the font of the group.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param font the font
	 * @since 1.0
	 */
	@Override
	public void setFont(final Font font) {
		if(EDT.isExecutedInEDT()) {
			if(groupLbl != null) groupLbl.setFont(font.deriveFont(Font.BOLD));
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setFont") {
				@Override
				protected void execute() throws Throwable {
					if(groupLbl != null)
						groupLbl.setFont(font.deriveFont(Font.BOLD));
				}
			});
	}
	
	/**
	 * The layout of an item group may not be changed meaning this method does nothing!
	 * 
	 * @param mgr the layout manager
	 * @since 1.0
	 */
	@Override
	public void setLayout(LayoutManager mgr) {
		// not allowed!
	}
	
	/**
	 * The components of an item group may not be removed meaning this method does nothing!
	 * 
	 * @since 1.0
	 */
	@Override
	public void removeAll() {
	}
	
	@Override
	protected void addImpl(Component c, Object constraints, int index) {
		if(!initialized)
			super.addImpl(c, constraints, index);
	}
	
	/**
	 * Toggles the group and its associated items in the legend.
	 * 
	 * @since 1.0
	 */
	private void toggle() {
		// toggle expanded state
		expanded = !expanded;
		
		// set icon
		if(expanded)
			groupBtn.setIcon(Resources.getInstance().COLLAPSE_ICON);
		else
			groupBtn.setIcon(Resources.getInstance().EXPAND_ICON);
		
		// finally toggle the group in the legend
		legend.toggleGroup(this);
	}
	
	/**
	 * Sets the foreground color of the item group.
	 * <br><br>
	 * This is an internal method.
	 * 
	 * @param fg the foreground color
	 * @since 1.0
	 */
	private void internalSetForeground(final Color fg) {
		super.setForeground(fg);
		
		if(groupLbl != null)
			groupLbl.setForeground(fg);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, fg));
	}

}
