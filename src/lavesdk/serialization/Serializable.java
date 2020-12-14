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
 * Interface:	Serializable
 * Task:		Serialize object data
 * Created:		30.09.13
 * LastChanges:	30.09.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.serialization;

/**
 * Interface for serialize and deserialize object data to and from a file.
 * 
 * @see ObjectFile
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public interface Serializable {
	
	/**
	 * Serializes (saves) the object data to the given serializer.
	 * 
	 * @param s the serializer for the object
	 * @since 1.0
	 */
	public void serialize(final Serializer s);
	
	/**
	 * Deserializes (loads) the object data from the given serializer.
	 * 
	 * @param s the serializer
	 * @since 1.0
	 */
	public void deserialize(final Serializer s);

}
