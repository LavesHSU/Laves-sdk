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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;

/**
 * Represents an item in a {@link Legend}.
 * <br><br>
 * An item is identified by its name and can have a text and an icon. Create a legend item by use of {@link #LegendItem(String, String, String, Icon)}.<br>
 * Use {@link #createCircleIcon(Color, Color, int)}, {@link #createLineIcon(Color, int)},  {@link #createRectangleIcon(Color, Color, int)}
 * to create predefined icons or load custom icons by using {@link ImageIcon}.
 * <br><br>
 * <b>Groups</b>:<br>
 * Items can be assigned to groups. You can specify the group name the item corresponds to when you create the item.
 * Call {@link #getGroupName()} to get the name of the group the item is associated with. The items of a group can be hidden
 * if the user collapse the group meaning that he does not want to see the items of the group anymore until he expands the group
 * again.
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class LegendItem extends JPanel {

	private static final long serialVersionUID = 1L;
	
	/** the name of the item */
	private final String itemName;
	/** the name of the group this item is related to */
	private final String groupName;
	/** the label of the text */
	private final JLabel lblText;
	/** the label of the icon */
	private final JLabel lblIcon;
	/** flag that indicates whether the item is initialized */
	private boolean initialized;
	
	/** the default size of an icon */
	private static final int DEF_ICONSIZE = 16;
	
	/** the orientation of an icon content: from left to right */
	public static final int LINETYPE_LEFT_TO_RIGHT = 1;
	/** the orientation of an icon content: from top to bottom */
	public static final int LINETYPE_TOP_TO_BOTTOM = 2;
	/** the orientation of an icon content: from top left to bottom right (diagonal) */
	public static final int LINETYPE_TOPLEFT_TO_BOTTOMRIGHT = 3;
	/** the orientation of an icon content: from bottom left to top right (diagonal) */
	public static final int LINETYPE_BOTTOMLEFT_TO_TOPRIGHT = 4;
	
	/**
	 * Creates a new legend item.
	 * 
	 * @param itemName the name of the item by what it is identified in a {@link Legend} (has to be unique based on the corresponding legend)
	 * @param groupName the name of the group this item is related to or an empty string if the item should appear in the default group at the beginning of the legend
	 * @param text the text of the item (<b>you can use html tags to format the text</b>)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if itemName is null</li>
	 * 		<li>if itemName is empty</li>
	 * 		<li>if groupName is null</li>
	 * 		<li>if text is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public LegendItem(final String itemName, final String groupName, final String text) throws IllegalArgumentException {
		this(itemName, groupName, text, null);
	}
	
	/**
	 * Creates a new legend item.
	 * 
	 * @param itemName the name of the item by what it is identified in a {@link Legend} (has to be unique based on the corresponding legend)
	 * @param groupName the name of the group this item is related to or an empty string if the item should appear in the default group at the beginning of the legend
	 * @param text the text of the item (<b>you can use html tags to format the text</b>)
	 * @param icon the icon of the item (use {@link #createCircleIcon(Color, Color, int)}, {@link #createLineIcon(Color, int)}, ... to create predefined icons) or <code>null</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if itemName is null</li>
	 * 		<li>if itemName is empty</li>
	 * 		<li>if groupName is null</li>
	 * 		<li>if text is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public LegendItem(final String itemName, final String groupName, final String text, final Icon icon) throws IllegalArgumentException {
		super();
		
		if(itemName == null || itemName.isEmpty() || groupName == null || text == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.initialized = false;
		this.itemName = itemName;
		this.groupName = groupName;
		this.lblText = new JLabel(!text.isEmpty() ? "<html>" + text + "</html>" : "");
		this.lblIcon = new JLabel(icon);
		
		super.setLayout(new BorderLayout(5, 5));
		add(lblIcon, BorderLayout.WEST);
		add(lblText, BorderLayout.CENTER);
		setBackground(Color.white);
		setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		
		initialized = true;
	}
	
	/**
	 * Gets the name of the item.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the item name
	 * @since 1.0
	 */
	public final String getItemName() {
		return itemName;
	}
	
	/**
	 * Gets the name of the group.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the group name
	 * @since 1.0
	 */
	public final String getGroupName() {
		return groupName;
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
			return (lblText != null) ? lblText.getFont() : super.getFont();
		else
			return EDT.execute(new GuiRequest<Font>() {
				@Override
				protected Font execute() throws Throwable {
					return (lblText != null) ? lblText.getFont() : LegendItem.super.getFont();
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
			if(lblText != null) lblText.setFont(font);
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setFont") {
				@Override
				protected void execute() throws Throwable {
					if(lblText != null) lblText.setFont(font);
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
	 * The components of an item may not be removed meaning this method does nothing!
	 * 
	 * @since 1.0
	 */
	@Override
	public void removeAll() {
	}
	
	@Override
	protected void addImpl(Component component, Object constraints, int index) {
		if(!initialized)
			super.addImpl(component, constraints, index);
	}
	
	/**
	 * Creates a circle icon.
	 * 
	 * @param background the background color of the circle
	 * @param foreground the foreground color of the circle
	 * @param edgeWidth the line width of the edge
	 * @return the icon
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if background is null</li>
	 * 		<li>if foreground is null</li>
	 * 		<li>if edgeWidth <code><0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public static Icon createCircleIcon(final Color background, final Color foreground, final int edgeWidth) throws IllegalArgumentException {
		return createCircleIcon(background, foreground, edgeWidth, DEF_ICONSIZE, DEF_ICONSIZE);
	}
	
	/**
	 * Creates a circle icon.
	 * 
	 * @param background the background color of the circle
	 * @param foreground the foreground color of the circle
	 * @param edgeWidth the line width of the edge
	 * @param width the width of the icon
	 * @param height the height of the icon
	 * @return the icon
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if background is null</li>
	 * 		<li>if foreground is null</li>
	 * 		<li>if edgeWidth <code><0</code></li>
	 * 		<li>if width <code><0</code></li>
	 * 		<li>if height <code><0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public static Icon createCircleIcon(final Color background, final Color foreground, final int edgeWidth, final int width, final int height) throws IllegalArgumentException {
		if(background == null || foreground == null || edgeWidth < 0 || width < 0 || height < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		return new Icon() {
			
			private final Stroke edgeStroke = new BasicStroke(edgeWidth);
			
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				final Graphics2D g2d = (Graphics2D)g;
				int edgeCorr = (int)((float)edgeWidth / 2.0f + 0.5f);
				
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				
				g2d.setColor(background);
				g2d.fillOval(x + edgeCorr, y + edgeCorr, width - 2 * edgeCorr, height - 2 * edgeCorr);
				
				g2d.setColor(foreground);
				final Stroke oldStroke = g2d.getStroke();
				g2d.setStroke(edgeStroke);
				g2d.drawOval(x + edgeCorr, y + edgeCorr, width - 2 * edgeCorr, height - 2 * edgeCorr);
				g2d.setStroke(oldStroke);
			}
			
			@Override
			public int getIconWidth() {
				return height;
			}
			
			@Override
			public int getIconHeight() {
				return width;
			}
		};
	}
	
	/**
	 * Creates a line icon with a default line type of {@link #LINETYPE_LEFT_TO_RIGHT}.
	 * 
	 * @param color the color of the line
	 * @param lineWidth the line width
	 * @return the icon
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if color is null</li>
	 * 		<li>if lineWidth <code><0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public static Icon createLineIcon(final Color color, final int lineWidth) throws IllegalArgumentException {
		return createLineIcon(color, lineWidth, LINETYPE_LEFT_TO_RIGHT);
	}
	
	/**
	 * Creates a line icon.
	 * 
	 * @param color the color of the line
	 * @param lineWidth the line width
	 * @param lineType the line type like {@link #LINETYPE_LEFT_TO_RIGHT}, {@link #LINETYPE_TOP_TO_BOTTOM}, ...
	 * @return the icon
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if color is null</li>
	 * 		<li>if lineWidth <code><0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public static Icon createLineIcon(final Color color, final int lineWidth, final int lineType) throws IllegalArgumentException {
		return createLineIcon(color, lineWidth, lineType, DEF_ICONSIZE, DEF_ICONSIZE);
	}
	
	/**
	 * Creates a line icon.
	 * 
	 * @param color the color of the line
	 * @param lineWidth the line width
	 * @param lineType the line type like {@link #LINETYPE_LEFT_TO_RIGHT}, {@link #LINETYPE_TOP_TO_BOTTOM}, ...
	 * @param width the width of the icon
	 * @param height the height of the icon
	 * @return the icon
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if color is null</li>
	 * 		<li>if lineWidth <code><0</code></li>
	 * 		<li>if width <code><0</code></li>
	 * 		<li>if height <code><0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public static Icon createLineIcon(final Color color, final int lineWidth, final int lineType, final int width, final int height) throws IllegalArgumentException {
		if(color == null || lineWidth < 0 || width < 0 || height < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		return new Icon() {
			
			private final Stroke lineStroke = new BasicStroke(lineWidth);
			
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				final Graphics2D g2d = (Graphics2D)g;
				int x1;
				int y1;
				int x2;
				int y2;
				
				switch(lineType) {
					case LINETYPE_TOP_TO_BOTTOM:
						x1 = x2 = x + width / 2 + 1;
						y1 = y;
						y2 = y + height;
						break;
					case LINETYPE_TOPLEFT_TO_BOTTOMRIGHT:
						x1 = x;
						y1 = y;
						x2 = y + width;
						y2 = y + height;
						break;
					case LINETYPE_BOTTOMLEFT_TO_TOPRIGHT:
						x1 = x;
						y1 = y + height;
						x2 = y + width;
						y2 = y;
						break;
					case LINETYPE_LEFT_TO_RIGHT:
					default:
						x1 = x;
						y1 = y2 = y + height / 2 + 1;
						x2 = x + width;
						break;
				}
				
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				
				g2d.setColor(color);
				final Stroke oldStroke = g2d.getStroke();
				g2d.setStroke(lineStroke);
				g2d.drawLine(x1, y1, x2, y2);
				g2d.setStroke(oldStroke);
			}
			
			@Override
			public int getIconWidth() {
				return width;
			}
			
			@Override
			public int getIconHeight() {
				return height;
			}
		};
	}
	
	/**
	 * Creates a rectangle icon.
	 * 
	 * @param background the background color of the rectangle
	 * @param foreground the foreground color of the rectangle
	 * @param edgeWidth the line width of the edge
	 * @return the icon
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if background is null</li>
	 * 		<li>if foreground is null</li>
	 * 		<li>if edgeWidth <code><0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public static Icon createRectangleIcon(final Color background, final Color foreground, final int edgeWidth) throws IllegalArgumentException {
		return createRectangleIcon(background, foreground, edgeWidth, DEF_ICONSIZE, DEF_ICONSIZE);
	}
	
	/**
	 * Creates a rectangle icon.
	 * 
	 * @param background the background color of the rectangle
	 * @param foreground the foreground color of the rectangle
	 * @param edgeWidth the line width of the edge
	 * @param width the width of the icon
	 * @param height the height of the icon
	 * @return the icon
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if background is null</li>
	 * 		<li>if foreground is null</li>
	 * 		<li>if edgeWidth <code><0</code></li>
	 * 		<li>if width <code><0</code></li>
	 * 		<li>if height <code><0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public static Icon createRectangleIcon(final Color background, final Color foreground, final int edgeWidth, final int width, final int height) throws IllegalArgumentException {
		if(background == null || foreground == null || edgeWidth < 0 || width < 0 || height < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		return new Icon() {
			
			private final Stroke edgeStroke = new BasicStroke(edgeWidth);
			
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				final Graphics2D g2d = (Graphics2D)g;
				int edgeCorr = (int)((float)edgeWidth / 2 + 0.5f);
				
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				
				g2d.setColor(background);
				g2d.fillRect(x + edgeCorr, y + edgeCorr, width - 2 * edgeCorr, height - 2 * edgeCorr);

				g2d.setColor(foreground);
				final Stroke oldStroke = g2d.getStroke();
				g2d.setStroke(edgeStroke);
				g2d.drawRect(x + edgeCorr, y + edgeCorr, width - 2 * edgeCorr, height - 2 * edgeCorr);
				g2d.setStroke(oldStroke);
			}
			
			@Override
			public int getIconWidth() {
				return height;
			}
			
			@Override
			public int getIconHeight() {
				return width;
			}
		};
	}

}
