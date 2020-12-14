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

import javax.swing.JComponent;

/**
 * Represents a base component with base functionality.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class BaseComponent extends JComponent {

	private static final long serialVersionUID = 1L;

	/** flag that indicates whether the repaint of the component is disabled */
	private boolean repaintDisabled;
	
	/**
	 * Creates a new base component.
	 * 
	 * @since 1.0
	 */
	public BaseComponent() {
		repaintDisabled = false;
	}
	
	/**
	 * Indicates whether the repaint of the component is disabled.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the repaint mechanism is disabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isRepaintDisabled() {
		return repaintDisabled;
	}
	
	/**
	 * Sets whether the repaint of the component is disabled meaning the component cannot be repainted until the disable state is revoked.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The {@link #repaint()} is invoked automatically.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param disabled <code>true</code> if the repaint mechanism should be disabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setRepaintDisabled(final boolean disabled) {
		repaintDisabled = disabled;
		repaint();
	}
	
	/**
	 * Repaints the component but only if {@link #isRepaintDisabled()} returns <code>false</code>.
	 * <br><br>
	 * To add additional functionality to the repaint method override {@link #repaintComponent()}.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	@Override
	public final void repaint() {
		if(!repaintDisabled)
			repaintComponent();
	}
	
	/**
	 * Repaints the component.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * <b>This method may only be invoked by {@link #repaint()}.</b> You can override this method to repaint sub-components but ensure that
	 * the super type method is invoked too meaning:
	 * <pre>
	 * protected void repaintComponent() {
	 *     // repaint the parent
	 *     super.repaintComponent();
	 *     
	 *     // repaint sub-components ...
	 * }
	 * </pre>
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	protected void repaintComponent() {
		super.repaint();
	}

}
