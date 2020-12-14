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

package lavesdk.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import lavesdk.utils.FileUtils;

/**
 * Formatter for a {@link LogFile}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class LogFormat extends SimpleFormatter {
	
	/** formatter for dates */
	private final SimpleDateFormat dateFormat;
	
	/**
	 * Creates a new format.
	 * 
	 * @since 1.0
	 */
	public LogFormat() {
		dateFormat = new SimpleDateFormat("EEE, dd.MM.yyyy hh:mm:ss");
	}
	
	@Override
	public synchronized String format(LogRecord r) {
		return dateFormat.format(new Date(r.getMillis())) + ": [" + r.getLevel().getLocalizedName() + "] " + r.getMessage() + FileUtils.LINESEPARATOR;
	}

}
