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

import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;

/**
 * Represents an option of an {@link OptionComboButton}.
 * <br><br>
 * An option can have an icon with optional text or only text but never none of both. An option can only be the active
 * option of an {@link OptionComboButton} if the option has an icon. If the option does not have an icon the action of the
 * option can be performed indeed but the option cannot become the active one.<br>
 * For example this could be used to add options for configuration purpose that do not implement a concrete action.
 * <br><br>
 * The options of an {@link OptionComboButton} are displayed in a drop down menu if the user clicks onto the arrow of the button.
 * <br><br>
 * <b>Perform an action</b>:<br>
 * Use an {@link ActionListener} ({@link #addActionListener(ActionListener)}) to become notified when a user clicks the option.
 * You have to implement the action that is invoked by the option inside the action performed event of the {@link ActionListener}.
 * <br><br>
 * <b>User data</b>:<br>
 * You can store additional information by using {@link #setUserData(Object)} that could be used as the data behind an action.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class Option extends JMenuItem {

	private static final long serialVersionUID = 1L;
	
	/** flag that indicates whether the option should be separated from the options above */
	private final boolean separated;
	/** the compound icon of the option icon and the arrow or <code>null</code> if no icon is set */
	private Icon compoundIcon;
	/** the compound rollover icon of the option icon and the arrow or <code>null</code> if no icon is set */
	private Icon compoundRolloverIcon;
	/** the icon to display the option as disabled or <code>null</code> if no icon is set */
	private Icon compoundDisabledIcon;
	/** the user data of the option */
	private Object userData;
	
	/**
	 * Creates a new option.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * An option can have an icon with optional text or only text but never none of both. An option can only be the active
	 * option of an {@link OptionComboButton} if the option has an icon. If the option does not have an icon the action of the
	 * option can be performed indeed but the option cannot become the active one.<br>
	 * For example this could be used to add options for configuration purpose that do not implement a concrete action.
	 * 
	 * @param icon the icon of the option or <code>null</code>
	 * @param text the text/description of the option or <code>null</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if icon is null and the text of the option is null or empty to (see <b>Notice</b>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public Option(final Icon icon, final String text) throws IllegalArgumentException {
		this(icon, text, false);
	}
	
	/**
	 * Creates a new option.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * An option can have an icon with optional text or only text but never none of both. An option can only be the active
	 * option of an {@link OptionComboButton} if the option has an icon. If the option does not have an icon the action of the
	 * option can be performed indeed but the option cannot become the active one.<br>
	 * For example this could be used to add options for configuration purpose that do not implement a concrete action.
	 * 
	 * @param icon the icon of the option or <code>null</code>
	 * @param text the text/description of the option or <code>null</code>
	 * @param separated <code>true</code> if the option should be separated from the options above (a separator is added at the top of the option) otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if icon is null and the text of the option is null or empty to (see <b>Notice</b>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public Option(final Icon icon, final String text, final boolean separated) throws IllegalArgumentException {
		super(text, icon);
		
		this.separated = separated;
		this.userData = null;
	}
	
	/**
	 * Gets the icon of the option.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * An option can have an icon with optional text or only text but never none of both. An option can only be the active
	 * option of an {@link OptionComboButton} if the option has an icon. If the option does not have an icon the action of the
	 * option can be performed indeed but the option cannot become the active one.<br>
	 * For example this could be used to add options for configuration purpose that do not implement a concrete action.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the icon or <code>null</code> if this option does not have an icon
	 * @since 1.0
	 */
	@Override
	public Icon getIcon() {
		if(EDT.isExecutedInEDT())
			return super.getIcon();
		else
			return EDT.execute(new GuiRequest<Icon>() {
				@Override
				protected Icon execute() throws Throwable {
					return Option.super.getIcon();
				}
			});
	}
	
	/**
	 * Sets the icon of the option.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * An option can have an icon with optional text or only text but never none of both. An option can only be the active
	 * option of an {@link OptionComboButton} if the option has an icon. If the option does not have an icon the action of the
	 * option can be performed indeed but the option cannot become the active one.<br>
	 * For example this could be used to add options for configuration purpose that do not implement a concrete action.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param icon the icon or <code>null</code> if this option should not have an icon
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if icon is null and the text of the option is null or empty to (see <b>Notice</b>)</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void setIcon(final Icon icon) throws IllegalArgumentException {
		final String text = getText();
		
		if(icon == null && (text == null || text.isEmpty()))
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT()) {
			super.setIcon(icon);
			createCompoundIcons(icon);
		}
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setIcon") {
				@Override
				protected void execute() throws Throwable {
					Option.super.setIcon(icon);
					createCompoundIcons(icon);
				}
			});
	}
	
	/**
	 * Gets the text of the option.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * An option can have an icon with optional text or only text but never none of both. An option can only be the active
	 * option of an {@link OptionComboButton} if the option has an icon. If the option does not have an icon the action of the
	 * option can be performed indeed but the option cannot become the active one.<br>
	 * For example this could be used to add options for configuration purpose that do not implement a concrete action.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the text or <code>null</code> if this option does not have a text
	 * @since 1.0
	 */
	@Override
	public String getText() {
		if(EDT.isExecutedInEDT())
			return super.getText();
		else
			return EDT.execute(new GuiRequest<String>() {
				@Override
				protected String execute() throws Throwable {
					return Option.super.getText();
				}
			});
	}
	
	/**
	 * Sets the text of the option.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * An option can have an icon with optional text or only text but never none of both. An option can only be the active
	 * option of an {@link OptionComboButton} if the option has an icon. If the option does not have an icon the action of the
	 * option can be performed indeed but the option cannot become the active one.<br>
	 * For example this could be used to add options for configuration purpose that do not implement a concrete action.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param text the text or <code>null</code> if this option should not have a text
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if text is null or empty and the icon of the option is null too (see <b>Notice</b>)</li>
	 * </ul>
	 * @since 1.0
	 */
	@Override
	public void setText(final String text) throws IllegalArgumentException {
		if(getIcon() == null && (text == null || text.isEmpty()))
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			super.setText(text);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setText") {
				@Override
				protected void execute() throws Throwable {
					Option.super.setText(text);
				}
			});
	}
	
	/**
	 * Indicates whether the option is separated from the options above.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the option should be separated from the options above otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean isSeparated() {
		return separated;
	}
	
	/**
	 * Gets the user data of the object.
	 * <br><br>
	 * You can store additional information in the user data of the option.
	 * 
	 * @return the user data or <code>null</code> if there is no user data
	 * @since 1.0
	 */
	public Object getUserData() {
		return userData;
	}
	
	/**
	 * Sets the user data of the object.
	 * <br><br>
	 * You can store additional information in the user data of the option.
	 * 
	 * @param userData the user data or <code>null</code> if the option should not have user data
	 * @since 1.0
	 */
	public void setUserData(final Object userData) {
		this.userData = userData;
	}
	
	/**
	 * Gets the compound icon of the option.
	 * <br><br>
	 * This icon is the result of connecting the option's icon and an arrow icon.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the compound icon or <code>null</code> if the option does not have an icon
	 * @since 1.0
	 */
	Icon getCompoundIcon() {
		return compoundIcon;
	}
	
	/**
	 * Gets the compound icon of the option to display a rollover effect.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the compound icon for a rollover effect or <code>null</code> if the option does not have an icon
	 * @since 1.0
	 */
	Icon getCompoundRolloverIcon() {
		return compoundRolloverIcon;
	}
	
	/**
	 * Gets the compound icon of the option to display a disabled effect.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @return the compound icon for a disabled effect or <code>null</code> if the option does not have an icon
	 * @since 1.0
	 */
	Icon getCompoundDisabledIcon() {
		return compoundDisabledIcon;
	}
	
	/**
	 * Indicates whether the option is activatable or not.
	 * 
	 * @return <code>true</code> if the option has an icon meaning the option is activatable otherwise <code>false</code>
	 * @since 1.0
	 */
	boolean isActivatable() {
		return (compoundIcon != null);
	}
	
	/**
	 * Fires the action performed event of the option.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this package!<br>
	 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS METHOD</i>!
	 * 
	 * @since 1.0
	 */
	void fireActionPerformed() {
		doClick(0);
	}
	
	/**
	 * Creates the icons for the option.
	 * 
	 * @param icon the user icon or <code>null</code>
	 * @since 1.0
	 */
	private void createCompoundIcons(final Icon icon) {
		if(icon != null) {
			compoundIcon = new OptionIcon(icon, false);
			compoundRolloverIcon = new OptionIcon(icon, true);
			
			// create an image for the disabled icon
			final BufferedImage disabledIcon = new BufferedImage(compoundIcon.getIconWidth(), compoundIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			final Graphics2D g = disabledIcon.createGraphics();
			compoundIcon.paintIcon(null, g, 0, 0);
			g.dispose();
			
			// use the image to create a disabled icon by the look & feel (an icon, like an option icon, cannot be used
			// to create a disabled icon because the method needs a static icon and not a self painted one like a normal icon)
			compoundDisabledIcon = UIManager.getLookAndFeel().getDisabledIcon(null, new ImageIcon(disabledIcon));
		}
		else {
			compoundIcon = null;
			compoundRolloverIcon = null;
			compoundDisabledIcon = null;
		}
	}

}
