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

package lavesdk.algorithm.plugin.extensions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;

/**
 * Base implementation of a toolbar extension.
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public abstract class ToolBarExtension {
	
	/** the components that extend the toolbar */
	private final List<JComponent> components;
	/** flag that indicates whether the toolbar is enabled */
	private boolean enabled;
	
	/**
	 * Creates a new toolbar extension.
	 * <br><br>
	 * Use {@link #addButton(JButton)} or {@link #addSeparator()} to create the extension components.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * You have to handle the event listeners on your own (preferably in the concrete constructor of your extension)
	 * 
	 * @param startsWithSeparator <code>true</code> if the extension should be separated from the other elements in the toolbar otherwise <code>false</code>
	 * @since 1.0
	 */
	public ToolBarExtension(final boolean startsWithSeparator) {
		components = new ArrayList<JComponent>();
		enabled = true;
		
		if(startsWithSeparator)
			addSeparator();
	}
	
	/**
	 * Applies the extension to the specified toolbar.
	 * 
	 * @param toolBar the toolbar
	 * @since 1.0
	 */
	public final void apply(final JToolBar toolBar) {
		for(JComponent c : components)
			toolBar.add(c);
	}
	
	/**
	 * Removes this extension from the specified toolbar.
	 * 
	 * @param toolBar the toolbar where this extension should be removed
	 * @since 1.0
	 */
	public final void remove(final JToolBar toolBar) {
		for(JComponent c : components)
			toolBar.remove(c);
	}
	
	/**
	 * Adds a new button to the extension.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The components are shown in the toolbar as they were added.
	 * 
	 * @param button toolbar button
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if button is null</li>
	 * </ul>
	 */
	protected final void addButton(final JButton button) throws IllegalArgumentException {
		if(button == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(!components.contains(button))
			components.add(button);
		
		button.setEnabled(enabled);
	}
	
	/**
	 * Adds a new separator to the extension.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The components are shown in the toolbar as they were added.
	 * 
	 * @since 1.0
	 */
	protected final void addSeparator() {
		components.add(new JToolBar.Separator());
	}
	
	/**
	 * Indicates whether the toolbar extension is enabled.
	 * 
	 * @return <code>true</code> if the extension is enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Sets whether the toolbar extension is enabled.
	 * 
	 * @param enabled <code>true</code> if the extension should be enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public final void setEnabled(final boolean enabled) {
		if(this.enabled == enabled)
			return;
		
		this.enabled = enabled;
		
		for(JComponent c : components)
			c.setEnabled(enabled);
	}
	
	/**
	 * Indicates whether the toolbar extension should be shown in the menu of the host application.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It is only possible to show one option of the extension, even if there are more.
	 * 
	 * @see #getMenuOptionText()
	 * @see #showMenuOption()
	 * @return <code>true</code> if the extension or one of its options should be shown in the menu otherwise <code>false</code>
	 * @since 1.1
	 */
	public boolean getShowInMenu() {
		return false;
	}
	
	/**
	 * Gets the text of the option that is shown in the menu of the host application.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It is only possible to show one option of the extension, even if there are more.
	 * 
	 * @see #getShowInMenu()
	 * @see #showMenuOption()
	 * @return text of the option of the extension taht is shown in the host's menu
	 * @since 1.1
	 */
	public String getMenuOptionText() {
		return "NONE";
	}
	
	/**
	 * Gets the icon of the menu option.
	 * 
	 * @return the icon or <code>null</code> if there is no icon
	 * @since 1.1
	 */
	public Icon getMenuOptionIcon() {
		return null;
	}
	
	/**
	 * Invokes the option that is shown in the menu of the host application.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * It is only possible to show one option of the extension, even if there are more.
	 * 
	 * @see #getShowInMenu()
	 * @see #getMenuOptionText()
	 * @since 1.1
	 */
	public void showMenuOption() {
	}

}
