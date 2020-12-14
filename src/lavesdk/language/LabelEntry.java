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

package lavesdk.language;

import lavesdk.utils.FileUtils;

/**
 * Represents an entry of a label in a specific language.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class LabelEntry {
	
	/** the language id of this entry */
	public final String langID;
	/** the description or text of this entry in a specified language ({@link #langID}) */
	public final String description;
	
	/**
	 * Creates a new entry.
	 * 
	 * @param langID the language id
	 * @param description the label description (can contain "\n" as an escaped line break, these line breaks are converted in the platform specific line break sequence)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if langID is null</li>
	 * 		<li>if description is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public LabelEntry(final String langID, final String description) throws IllegalArgumentException {
		if(langID == null || description == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.langID = langID;
		this.description = description.replaceAll("\\n", FileUtils.LINESEPARATOR);
	}
	
	@Override
	public String toString() {
		return description + " [" + langID + "]";
	}
}
