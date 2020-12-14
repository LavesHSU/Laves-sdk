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
 * Interface:	AlgorithmStateAttachment
 * Task:		Attachment of an algorithm state
 * Created:		14.05.14
 * LastChanges:	21.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm;

/**
 * Represents an attachment of an {@link AlgorithmState}.
 * <br><br>
 * The attachment can be used to attach data to the current algorithm state that is available only during the current step is executed.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public interface AlgorithmStateAttachment {
	
	/**
	 * Attaches an object to the algorithm state.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Keep in mind that the attachment of an {@link AlgorithmState} is a mutable object storage meaning that changes to the object after the
	 * object was added to the state cannot be made undone.
	 * 
	 * @param key the key
	 * @param attachment the object to attach
	 * @return the object
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if key is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public <T> T addAttachment(final String key, final T attachment) throws IllegalArgumentException;
	
	/**
	 * Gets an attachment of the algorithm state.
	 * 
	 * @param key the key
	 * @return the object or <code>null</code> if the state does not contain an attachment with the given key and type
	 * @since 1.0
	 */
	public <T> T getAttachment(final String key);
	
	/**
	 * Gets an attachment of the algorithm state.
	 * 
	 * @param key key the key
	 * @param defValue the default return value
	 * @return the object or defValue if the state does not contain an attachment with the given key and type
	 * @since 1.0
	 */
	public <T> T getAttachment(final String key, final T defValue);

}
