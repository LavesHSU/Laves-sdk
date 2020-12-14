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
 * Class:		ViewHeaderBarExtension
 * Task:		Abstract basis class for an extension of a header bar
 * Created:		06.02.14
 * LastChanges:	07.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JToolBar;

import lavesdk.language.LanguageFile;

/**
 * Extends the header bar of a given view with further functionality.
 * <br><br>
 * Override {@link #createExtension()} to create your extension components and use {@link #apply()} to apply the header bar extension
 * to the specified view.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public abstract class ViewHeaderBarExtension {
	
	/** the view its header bar is extended by this extension */
	protected final View view;
	/** flag that indicates if the extension should be separated */
	protected final boolean separated;
	/** the language file or <code>null</code> */
	protected final LanguageFile langFile;
	/** the language id */
	protected final String langID;
	/** the components of the extension */
	private final List<JComponent> components;
	
	/**
	 * Creates a new header bar extension.
	 * 
	 * @param view the view its header bar should be extended by this extension
	 * @param separated <code>true</code> if a separator should be added automatically at the end of creating the extension otherwise <code>false</code>
	 * @param langFile the language file or <code>null</code> if the extension should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages should be shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if view is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ViewHeaderBarExtension(final View view, final boolean separated, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		if(view == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.view = view;
		this.separated = separated;
		this.langFile = langFile;
		this.langID = langID;
		this.components = new ArrayList<JComponent>(3);
	}
	
	/**
	 * Applies the extension to the view's header bar.
	 * 
	 * @since 1.0
	 */
	public final void apply() {
		// create the extension
		createExtension();
		
		// separate it from the rest
		if(separated)
			addSeparator();
	}
	
	/**
	 * Removes the extension for the header bar of the corresponding view.
	 * 
	 * @since 1.0
	 */
	public final void remove() {
		// remove all components that were added to the extension
		for(JComponent c : components)
			view.removeHeaderBarComponent(c);
		
		components.clear();
	}
	
	/**
	 * Creates the extension.
	 * <br><br>
	 * Use {@link #addComponent(JComponent)} and {@link #addSeparator()} to create your extension.
	 * <br><br>
	 * Remember that a header bar is organized from right to left meaning the first added component is finally the first component
	 * at the left side of the header bar next to the view title and the last added component is the one left to the close button.
	 * 
	 * @since 1.0
	 */
	protected abstract void createExtension();
	
	/**
	 * Adds a component to the extension.
	 * <br><br>
	 * A header bar is organized from right to left meaning the first added component is finally the first component
	 * at the left side of the header bar next to the view title.
	 * 
	 * @param component the component
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if component is null</li>
	 * </ul>
	 * @since 1.0
	 */
	protected final void addComponent(final JComponent component) throws IllegalArgumentException {
		if(component == null)
			throw new IllegalArgumentException("No valid argument!");
		
		view.addHeaderBarComponent(component);
		components.add(component);
	}
	
	/**
	 * Adds a separator to the extension.
	 * <br><br>
	 * A header bar is organized from right to left meaning the first added component is finally the first component
	 * at the left side of the header bar next to the view title and the last added component is the one left to the close button.
	 * 
	 * @since 1.0
	 */
	protected final void addSeparator() {
		addComponent(new JToolBar.Separator());
	}

}
