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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;

/**
 * Represents a drop down button with multiple {@link Option}s.
 * <br><br>
 * Add an option to the button using {@link #add(Option)} or {@link #add(Component)}. The button only accepts options of
 * the type or subtype of {@link Option}.
 * <br><br>
 * Set the active option manually using {@link #setActiveOption(Option)} (initially the first option that is added becomes the active one).
 * 
 * @see Option
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class OptionComboButton extends JButton {

	private static final long serialVersionUID = 1L;
	
	/** the popup menu that contains the options */
	private final JPopupMenu popupMenu;
	/** the active option or <code>null</code> if there is no active option */
	private Option activeOption;
	/** flag that indicates whether the component is initialized */
	private boolean initialized;
	/** the model of the button */
	private final OptionComboButtonModel model;
	/** the popup listener of the menu */
	private final PopupMenuListener popupListener;
	/** flag that indicates whether the arrow of the button was pressed or not */
	private boolean arrowPressed;
	
	/**
	 * Creates a new option combo button.
	 * 
	 * @since 1.0
	 */
	public OptionComboButton() {
		this(null);
	}
	
	/**
	 * Creates a new option combo button.
	 * 
	 * @param options the options that should be provided by the button
	 * @since 1.0
	 */
	public OptionComboButton(final Option[] options) {
		initialized = false;
		popupMenu = new JPopupMenu();
		activeOption = null;
		model = new OptionComboButtonModel();
		arrowPressed = false;

		setModel(model);
		setActiveOption(null, false);
		
		initialized = true;
		
		// add the initial options if necessary
		if(options != null)
			for(Option o : options)
				add(o);
		
		popupListener = new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				OptionComboButton.this.model.setPopUpOpened(false);
				OptionComboButton.this.popupMenu.removePopupMenuListener(this);
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		};
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(OptionComboButton.this.activeOption == null || !OptionComboButton.this.model.isEnabled())
					return;
				
				// open the popup menu if the user clicks onto the arrow
				if(isPositionOntoArrow(e.getX(), e.getY())) {
					if(!OptionComboButton.this.model.isPopUpOpened() && OptionComboButton.this.popupMenu.getComponentCount() > 0) {
						OptionComboButton.this.model.setPopUpOpened(true);
						OptionComboButton.this.popupMenu.addPopupMenuListener(popupListener);
						OptionComboButton.this.popupMenu.show(OptionComboButton.this, 0, OptionComboButton.this.getHeight());
					}
					
					OptionComboButton.this.arrowPressed = true;
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(OptionComboButton.this.activeOption == null || !OptionComboButton.this.model.isEnabled())
					return;
				
				// the user has not pressed the arrow and clicked onto the option icon?
				if(!OptionComboButton.this.arrowPressed && !isPositionOntoArrow(e.getX(), e.getY())) {
					// close the popup
					OptionComboButton.this.popupMenu.removePopupMenuListener(OptionComboButton.this.popupListener);
					OptionComboButton.this.model.setPopUpOpened(false);
					// and allow that an action performed event can be fired
					OptionComboButton.this.arrowPressed = false;
				}
				
				// if the user has not pressed the arrow and there is a valid active option then fire the action performed event
				if(!OptionComboButton.this.arrowPressed && OptionComboButton.this.activeOption != null)
					OptionComboButton.this.activeOption.fireActionPerformed();
				
				OptionComboButton.this.arrowPressed = false;
				
				// consume the event meaning that it is avoided that the combo button creates an action performed event to
				e.consume();
			}
			
		});
	}
	
	/**
	 * Gets the active option of the combo button meaning the option that is currently provided by the button.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the active option or <code>null</code> if there is no active option
	 * @since 1.0
	 */
	public Option getActiveOption() {
		if(EDT.isExecutedInEDT())
			return activeOption;
		else
			return EDT.execute(new GuiRequest<Option>() {
				@Override
				protected Option execute() throws Throwable {
					return activeOption;
				}
			});
	}
	
	/**
	 * Sets the active option of the combo button.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The action performed event of the option is fired.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @see #setActiveOption(Option, boolean)
	 * @param option the option that should be activated or <code>null</code> if their should not be an activated option
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the specified option does not exist in the options of the combo button</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setActiveOption(final Option option) throws IllegalArgumentException {
		setActiveOption(option, true);
	}
	
	/**
	 * Sets the active option of the combo button.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param option the option that should be activated or <code>null</code> if their should not be an activated option
	 * @param fireActionPerformed <code>true</code> if the action performed event of the option should be fired otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if the specified option does not exist in the options of the combo button</li>
	 * </ul>
	 * @since 1.0
	 */
	public void setActiveOption(final Option option, final boolean fireActionPerformed) throws IllegalArgumentException {
		if(option != null && !contains(option))
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			internalSetActiveOption(option, fireActionPerformed);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setActiveOption") {
				@Override
				protected void execute() throws Throwable {
					internalSetActiveOption(option, fireActionPerformed);
				}
			});
	}
	
	/**
	 * Indicates whether the combo button contains the specified option.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param option the option
	 * @return <code>true</code> if the option exists in the combo button otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean contains(final Option option) {
		if(EDT.isExecutedInEDT())
			return internalContains(option);
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return internalContains(option);
				}
			});
	}
	
	/**
	 * Adds a new option to the combo button.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param option the option to be added
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if option is null</li>
	 * 		<li>if option already exists</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final Option option) throws IllegalArgumentException {
		if(option == null || contains(option))
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			internalAdd(option);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".add") {
				@Override
				protected void execute() throws Throwable {
					internalAdd(option);
				}
			});
	}
	
	/**
	 * Gets the number of options.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the number of options
	 * @since 1.0
	 */
	public int getOptionCount() {
		if(EDT.isExecutedInEDT())
			return popupMenu.getComponentCount();
		else
			return EDT.execute(new GuiRequest<Integer>() {
				@Override
				protected Integer execute() throws Throwable {
					return popupMenu.getComponentCount();
				}
			});
	}
	
	/**
	 * Gets the option at a given index.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param index the index
	 * @return the option
	 * @throws ArrayIndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getOptionCount()</code>)</li>
	 * </ul>
	 */
	public Option getOption(final int index) throws ArrayIndexOutOfBoundsException {
		if(EDT.isExecutedInEDT())
			return (Option)popupMenu.getComponent(index);
		else
			return EDT.execute(new GuiRequest<Option>() {
				@Override
				protected Option execute() throws Throwable {
					return (Option)popupMenu.getComponent(index);
				}
			});
	}
	
	/**
	 * Removes the option from the combo button.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param option the option to be removed
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if option is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void remove(final Option option) throws IllegalArgumentException {
		if(option == null)
			throw new IllegalArgumentException("No valid argument!");
		
		if(EDT.isExecutedInEDT())
			internalRemove(option);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".remove") {
				@Override
				protected void execute() throws Throwable {
					internalRemove(option);
				}
			});
	}
	
	/**
	 * The icon of an option combo button may not be changed meaning this method does nothing!
	 * 
	 * @param defaultIcon the icon
	 * @since 1.0
	 */
	@Override
	public void setIcon(Icon defaultIcon) {
		// this is not allowed
	}
	
	/**
	 * The text of an option combo button may not be changed meaning this method does nothing!
	 * 
	 * @param text the text
	 * @since 1.0
	 */
	@Override
	public void setText(String text) {
		// this is not allowed
	}
	
	/**
	 * The rollover icon of an option combo button may not be changed meaning this method does nothing!
	 * 
	 * @param rolloverIcon the icon
	 * @since 1.0
	 */
	@Override
	public void setRolloverIcon(Icon rolloverIcon) {
		// this is not allowed
	}
	
	/**
	 * The rollover selected icon of an option combo button may not be changed meaning this method does nothing!
	 * 
	 * @param rolloverSelectedIcon the icon
	 * @since 1.0
	 */
	@Override
	public void setRolloverSelectedIcon(Icon rolloverSelectedIcon) {
		// this is not allowed
	}
	
	/**
	 * The disabled icon of an option combo button may not be changed meaning this method does nothing!
	 * 
	 * @param disabledIcon the icon
	 * @since 1.0
	 */
	@Override
	public void setDisabledIcon(Icon disabledIcon) {
		// this is not allowed
	}
	
	/**
	 * The disabled selected icon of an option combo button may not be changed meaning this method does nothing!
	 * 
	 * @param disabledSelectedIcon the icon
	 * @since 1.0
	 */
	@Override
	public void setDisabledSelectedIcon(Icon disabledSelectedIcon) {
		// this is not allowed
	}
	
	@Override
	protected void addImpl(Component component, Object constraints, int index) {
		if(!initialized)
			super.addImpl(component, constraints, index);
		else if(component instanceof Option)
			add((Option)component);
		else
			throw new IllegalArgumentException("It is only possible to add Option components to an OptionComboButton!");
	}
	
	/**
	 * Sets the active option of the combo button.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param option the option that should be activated or <code>null</code> if their should not be an activated option
	 * @param fireActionPerformed <code>true</code> if the action performed event of the option should be fired otherwise <code>false</code>
	 * @since 1.0
	 */
	private void internalSetActiveOption(final Option option, final boolean fireActionPerformed) {
		// if option is not activatable then only perform the action of the option
		if(option == null || option.isActivatable()) {
			activeOption = option;
			
			final Icon icon = (activeOption != null) ? activeOption.getCompoundIcon() : OptionIcon.getArrowIcon();
			final Icon rolloverIcon = (activeOption != null) ? activeOption.getCompoundRolloverIcon() : OptionIcon.getArrowIcon();
			final Icon disabledIcon = (activeOption != null) ? activeOption.getCompoundDisabledIcon() : OptionIcon.getArrowIcon();
			
			// set the icons of the active option to the combo button
			super.setIcon(icon);
			super.setRolloverIcon(rolloverIcon);
			super.setRolloverSelectedIcon(rolloverIcon);
			super.setDisabledIcon(disabledIcon);
			super.setDisabledSelectedIcon(disabledIcon);
			
			// the text of the option becomes the tooltip text of the combo button
			setToolTipText((activeOption != null) ? activeOption.getText() : null);
		}
		
		// invoke the action performed event if necessary
		if(activeOption != null && fireActionPerformed)
			activeOption.fireActionPerformed();
	}
	
	/**
	 * Indicates whether the combo button contains the specified option.
	 * <br><br>
	 * This method is for internal purposes only!
	 * 
	 * @param option the option
	 * @return <code>true</code> if the option exists in the combo button otherwise <code>false</code>
	 * @since 1.0
	 */
	private boolean internalContains(final Option option) {
		final Component[] components = popupMenu.getComponents();
		
		for(Component c : components)
			if(c == option)
				return true;
		
		return false;
	}
	
	/**
	 * Adds a new option to the combo button.
	 * 
	 * @param option a <b>valid</b> option to be added
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if option is null</li>
	 * 		<li>if option already exists</li>
	 * </ul>
	 * @since 1.0
	 */
	private void internalAdd(final Option option) {
		// if the option should be separated from the others then add a separator first before the option
		// is added to the menu
		if(option.isSeparated())
			popupMenu.add(new JSeparator());
		
		popupMenu.add(option);
		
		// we need to get notification of when an option is selected
		option.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				OptionComboButton.this.setActiveOption(option, false);
			}
		});
		
		// no current active option? then set this one as the active one
		if(activeOption == null)
			setActiveOption(option, false);
	}
	
	/**
	 * Removes the option from the combo button.
	 * 
	 * @param option a <b>valid</b> option to be removed
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if option is null</li>
	 * </ul>
	 * @since 1.0
	 */
	private void internalRemove(final Option option) throws IllegalArgumentException {
		popupMenu.remove(option);
		
		if(option == activeOption)
			setActiveOption(null, false);
	}
	
	/**
	 * Indicates whether the specified position is onto the arrow area of the button.
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @return <code>true</code> if the position is onto the arrow area otherwise <code>false</code>
	 * @since 1.0
	 */
	private boolean isPositionOntoArrow(final int x, final int y) {
		return x >= getWidth() - OptionIcon.getArrowAreaWidth() - getInsets().right;
	}
	
	/**
	 * The model of the button.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class OptionComboButtonModel extends DefaultButtonModel {
		
		private static final long serialVersionUID = 1L;
		
		/** flag that indicates whether the popup is opened */
		private boolean popupOpened;
		
		/**
		 * Creates a new model.
		 * 
		 * @since 1.0
		 */
		public OptionComboButtonModel() {
			popupOpened = false;
		}
		
		/**
		 * Indicates whether the popup is opened.
		 * 
		 * @return <code>true</code> if the popup is opened otherwise <code>false</code>
		 * @since 1.0
		 */
		public boolean isPopUpOpened() {
			return popupOpened;
		}
		
		/**
		 * Sets whether the popup is opened.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * This locks some other methods of the model to simulate a correct behavior of a drop down button.
		 * 
		 * @param opened <code>true</code> if the popup is opened otherwise <code>false</code>
		 * @since 1.0
		 */
		public void setPopUpOpened(final boolean opened) {
			final boolean oldOpened = popupOpened;
			
			if(oldOpened == opened)
				return;
			
			if(opened) {
				if(!isPressed() && isEnabled()) {
					stateMask |= PRESSED + ARMED;
					fireStateChanged();
					popupOpened = true;
				}
			}
			else {
				popupOpened = false;
				setArmed(false);
				setPressed(false);
				setRollover(false);
				setSelected(false);
			}
		}
		
		@Override
		public void setPressed(boolean b) {
			if(!popupOpened)
				super.setPressed(b);
		}
		
		@Override
		public void setArmed(boolean b) {
			if(!popupOpened)
				super.setArmed(b);
		}
		
		@Override
		public void setRollover(boolean b) {
			if(!popupOpened)
				super.setRollover(b);
		}
		
		@Override
		public void setSelected(boolean b) {
			if(!popupOpened)
				super.setSelected(b);
		}
		
		@Override
		public void setEnabled(boolean b) {
			if(!popupOpened)
				super.setEnabled(b);
		}
		
		@Override
		protected void fireStateChanged() {
			if(!popupOpened)
				super.fireStateChanged();
		}
	}

}
