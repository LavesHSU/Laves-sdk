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
 * Enum:		MessageIcon
 * Task:		Type of the icon of a message
 * Created:		01.09.13
 * LastChanges:	21.11.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.enums;

import javax.swing.JOptionPane;

/**
 * Type of a message icon.
 * <br><br>
 * <b>Available</b>:<br>
 * <ul>
 * 		<li>{@link #INFO}</li>
 * 		<li>{@link #QUESTION}</li>
 * 		<li>{@link #WARNING}</li>
 * 		<li>{@link #ERROR}</li>
 * </ul>
 * <br>
 * Use {@link #toMessageType()} to get a {@link JOptionPane} message type of the icon.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public enum MessageIcon {
	
	/**
	 * Shows an platform independent information icon.
	 * <br><br>
	 * Use this when a message with information character is displayed.
	 */
	INFO,
	
	/**
	 * Show an platform independent question mark icon.
	 * <br><br>
	 * Use this when a message with question character is displayed.
	 */
	QUESTION,
	
	/**
	 * Shows an platform independent warning icon.
	 * <br><br>
	 * Use this when a message with warning character is displayed.
	 */
	WARNING,
	
	/**
	 * Shows an platform independent error icon.
	 * <br><br>
	 * Use this when a message with error character is displayed.
	 */
	ERROR;
	
	/**
	 * Converts the given message icon to a {@link JOptionPane} message type.
	 * 
	 * @return the message type
	 * @since 1.0
	 */
	public int toMessageType() {
		switch(this) {
			case QUESTION:	return JOptionPane.QUESTION_MESSAGE;
			case WARNING:	return JOptionPane.WARNING_MESSAGE;
			case ERROR:		return JOptionPane.ERROR_MESSAGE;
			case INFO:
			default:		return JOptionPane.INFORMATION_MESSAGE;
		}
	}

}
