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
 * Class:		PopupWindow
 * Task:		The base class of a popup window
 * Created:		06.12.13
 * LastChanges:	24.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;

/**
 * The base class of a popup window.
 * <br><br>
 * Use {@link #content} to load your components into the popup window and {@link #show(Component, int, int)}
 * to make the popup visible.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public abstract class PopupWindow {
	
	/** the popup window */
	private final JPopupMenu popup;
	/** the base container of the popup */
	private final JPanel popupRootContent;
	/** the title of the popup window */
	private final JLabel title;
	/** the content that contains the components of the popup window */
	protected final JPanel content;
	
	/** the default width of the window */
	private static final int DEF_WIDTH = 100;
	/** the default height of the window */
	private static final int DEF_HEIGHT = 200;
	/** the padding between the components in the popup window */
	private static final int OUTER_PADDING = 2;
	
	/**
	 * Creates a new popup window in default size.
	 * 
	 * @since 1.0
	 */
	public PopupWindow() {
		this(DEF_WIDTH, DEF_HEIGHT);
	}
	
	/**
	 * Creates a new popup window.
	 * 
	 * @param width the width of the window
	 * @param height the height of the window
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if width is <code>< 0</code></li>
	 * 		<li>if height is <code>< 0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public PopupWindow(final int width, final int height) throws IllegalArgumentException {
		if(width < 0 || height < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		popup = new JPopupMenu();
		popupRootContent = new JPanel();
		title = new JLabel();
		content = new JPanel();
		
		popup.setLayout(new FlowLayout(FlowLayout.CENTER, OUTER_PADDING, OUTER_PADDING));
		popup.add(popupRootContent);
		popup.setBackground(SystemColor.control);
		popupRootContent.setLayout(new BorderLayout());
		popupRootContent.add(title, BorderLayout.NORTH);
		popupRootContent.add(content, BorderLayout.CENTER);
		popupRootContent.setBackground(popup.getBackground());
		content.setBackground(popupRootContent.getBackground());
		
		//title is invisible by default because there is no title now
		title.setVisible(false);
		
		setSize(new Dimension(width, height));
	}
	
	/**
	 * Gets the content panel of the popup window.
	 * <br><br>
	 * The content panel contains the component(s) of the popup window.
	 * 
	 * @see #content
	 * @return the content panel
	 * @since 1.0
	 */
	public final JPanel getContentPanel() {
		return content;
	}
	
	/**
	 * Gets the title of the popup window.
	 * 
	 * @return the title
	 * @since 1.0
	 */
	public String getTitle() {
		return title.getText();
	}
	
	/**
	 * Sets the title of the popup window.
	 * 
	 * @param title the title
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setTitle(final String title) throws IllegalArgumentException {
		if(title == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.title.setText(title);
		this.title.setVisible(!title.isEmpty());
	}
	
	/**
	 * Gets the size of the popup window.
	 * 
	 * @return the size
	 * @since 1.0
	 */
	public Dimension getSize() {
		return popup.getSize();
	}
	
	/**
	 * Sets the size of the popup window.
	 * 
	 * @param width the width
	 * @param height the height
	 * @since 1.0
	 */
	public void setSize(final int width, final int height) {
		setSize(new Dimension(width, height));
	}
	
	/**
	 * Sets the size of the popup window.
	 * 
	 * @param dimension the size
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if dimension is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setSize(final Dimension dimension) throws IllegalArgumentException {
		if(dimension == null)
			throw new IllegalArgumentException("No valid argument!");
		
		popup.setSize(dimension);
		popupRootContent.setPreferredSize(dimension);
		content.setPreferredSize(dimension);
	}
	
	/**
	 * Sets the size of the popup window that orients oneself towards the preferred size of the component.
	 * 
	 * @param component the component
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if component is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setSize(final JComponent component) throws IllegalArgumentException {
		if(component == null)
			throw new IllegalArgumentException("No valid argument!");
		
		setSize(component.getPreferredSize().width + 2 * OUTER_PADDING, component.getPreferredSize().height + 2 * OUTER_PADDING);
	}
	
	/**
	 * Displays the popup window.
	 * 
	 * @param component the component in whose space the popup window is to appear
	 * @param x the x position at which the popup is to be displayed
	 * @param y the y position at which the popup is to be displayed
	 * @since 1.0
	 */
	public void show(final Component component, final int x, final int y) {
		show(component, x, y, 0);
	}
	
	/**
	 * Displays the popup window.
	 * 
	 * @param component the component in whose space the popup window is to appear
	 * @param x the x position at which the popup is to be displayed
	 * @param y the y position at which the popup is to be displayed
	 * @param delay the delay in milliseconds the popup window is open or <code>0</code> if the window has now delay
	 * @since 1.0
	 */
	public void show(final Component component, final int x, final int y, final int delay) {
		popup.show(component, x, y);
		
		if(delay > 0) {
			final Timer t = new Timer(delay, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					PopupWindow.this.popup.setVisible(false);
				}
			});
			t.start();
		}
	}
	
	/**
	 * Closes the popup window (manually).
	 * 
	 * @since 1.0
	 */
	public void close() {
		popup.setVisible(false);
	}

}
