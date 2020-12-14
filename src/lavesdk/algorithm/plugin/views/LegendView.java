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
 * Class:		LegendView
 * Task:		View to display a legend
 * Created:		11.12.13
 * LastChanges:	12.12.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import lavesdk.configuration.Configuration;
import lavesdk.gui.widgets.Legend;
import lavesdk.gui.widgets.LegendItem;
import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;

/**
 * Displays a legend.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #LegendView(String, boolean, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * <br><br>
 * <b>Items</b>:<br>
 * Use {@link #add(LegendItem)} to add items to the legend view and call {@link #getItem(String)} to get the legend item
 * of a specific item name.<br>
 * Each item can contain a text (description) and an icon. You can use {@link LegendItem#createCircleIcon(Color, Color, int)}, ...
 * to create predefined icons or you can load custom icons by using {@link ImageIcon}.
 * <br><br>
 * <b>Groups</b>:<br>
 * Items can be assigned to groups. You can specify the group name the item corresponds to when you create the item.
 * All items of a specific group can be hidden and shown by the user using the collapse/expand button at the group.<br>
 * To change the colors of the groups use {@link #setGroupBackground(Color)}/{@link #setGroupForeground(Color)}.
 * <br><br>
 * <b>Save and load the configuration</b>:<br>
 * You can save and load a configuration of the legend view by using {@link #saveConfiguration(Configuration)} and {@link #loadConfiguration(Configuration)}.
 * It is saved or restored the visibility of the view. This makes it possible that you can store the state of the view persistent.
 * 
 * @see Legend
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class LegendView extends View {
	
	private static final long serialVersionUID = 1L;
	
	/** the legend of the view */
	private final Legend legend;
	/** the default font size of the view */
	private final float defFontSize;

	/**
	 * Creates a new legend view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public LegendView(String title) throws IllegalArgumentException {
		this(title, true);
	}

	/**
	 * Creates a new legend view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param closable <code>true</code> if the legend view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a legend view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public LegendView(String title, boolean closable) throws IllegalArgumentException {
		this(title, closable, null, null);
	}
	
	/**
	 * Creates a new legend view.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the legend view. The following language labels are available:
	 * <ul>
	 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
	 * </ul>
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param closable <code>true</code> if the legend view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a legend view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langFile the language file or <code>null</code> if the legend view should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public LegendView(String title, boolean closable, LanguageFile langFile, String langID) throws IllegalArgumentException {
		super(title, closable, langFile, langID);
		
		legend = new Legend();
		
		final Font f = UIManager.getFont("Label.font");
		setFont(f);
		defFontSize = getFont().getSize();
		
		// extend the header bar with the functionality of increase/decrease/normalize the font size
		new FontHeaderBarExtension(this, defFontSize, closable, langFile, langID).apply();
		
		content.setLayout(new BorderLayout());
		content.add(legend, BorderLayout.CENTER);
	}
	
	/**
	 * Adds a new item to the legend view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param item the item
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if item is null</li>
	 * 		<li>if item is already existing meaning that there is already an item with the name</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final LegendItem item) throws IllegalArgumentException {
		if(item == null)
			throw new IllegalArgumentException("No valid argument!");
		
		legend.add(item);
	}
	
	/**
	 * Gets the legend item of a specific name.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param itemName the name of the item
	 * @return the legend item or <code>null</code> if the legend view does not contain an item with the given name
	 * @since 1.0
	 */
	public LegendItem getItem(final String itemName) {
		return legend.getItem(itemName);
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
		return legend.getGroupBackground();
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
		legend.setGroupBackground(color);
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
		return legend.getGroupForeground();
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
		legend.setGroupForeground(color);
	}
	
	/**
	 * Gets the font of the legend view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the font
	 * @since 1.0
	 */
	public Font getFont() {
		return legend.getFont();
	}
	
	/**
	 * Sets the font of the legend in the view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param font the font
	 * @since 1.0
	 */
	@Override
	public void setFont(final Font font) {
		legend.setFont(font);
	}
	
	/**
	 * Removes all items from the legend.
	 * 
	 * @since 1.0
	 */
	public void removeAll() {
		legend.removeAll();
	}

	@Override
	public void reset() {
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Furthermore it is read the font size (key "fontSize") of the legend view.
	 * 
	 * @since 1.0
	 */
	@Override
	protected void readConfigurationData(Configuration cd) {
		super.readConfigurationData(cd);
		
		setFont(getFont().deriveFont(cd.getFloat("fontSize", defFontSize)));
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Furthermore it is written the font size (key "fontSize") of the legend view.
	 * 
	 * @since 1.0
	 */
	@Override
	protected void writeConfigurationData(Configuration cd) {
		super.writeConfigurationData(cd);
		
		cd.addFloat("fontSize", getFont().getSize2D());
	}

}
