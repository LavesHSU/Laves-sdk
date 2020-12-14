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

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;

import lavesdk.resources.Resources;
import lavesdk.utils.PopupWindow;

/**
 * Displays a state in a popup ({@link #showState(boolean, JButton, String)}).
 * <br><br>
 * <b>Attention</b>:<br>
 * This class may only be used by classes of the LAVESDK or more precisely from the inside of this package!<br>
 * <i>DO NOT REMOVE THE PACKAGE VISIBILITY OF THIS CLASS</i>!
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
class StatePopup extends PopupWindow {
	
	/**
	 * Creates a new state popup.
	 * 
	 * @param state the state
	 * @param text the text of the state
	 * @since 1.0
	 */
	private StatePopup(final boolean state, final String text) {
		super();
		
		final JLabel stateLbl = new JLabel(text);
		stateLbl.setIcon(state ? Resources.getInstance().SUCCEEDED_ICON : Resources.getInstance().FAILED_ICON);
		
		content.setLayout(new FlowLayout());
		content.add(stateLbl);
		
		setSize(stateLbl);
	}
	
	/**
	 * Shows the state in a popup window.
	 * 
	 * @param state state
	 * @param btn the button of that checks the state (the popup is displayed below this button)
	 * @since 1.0
	 */
	public static void showState(final boolean state, final JButton btn, final String stateText) {
		final StatePopup popup = new StatePopup(state, stateText);
		popup.show(btn, 0, btn.getHeight(), 1500);
	}
	
}
