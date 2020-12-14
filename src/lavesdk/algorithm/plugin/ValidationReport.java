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
 * Class:		ValidatorResult
 * Task:		Result of a validation process of a plugin
 * Created:		21.11.13
 * LastChanges:	08.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin;


/**
 * The report of a validation process of an {@link AlgorithmPlugin}.
 * <br><br>
 * The flag {@link #ok} describes whether the process has succeeded or failed. If he has failed then you can
 * use {@link #message} to get a detailed description why the plugin is not valid.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class ValidationReport {
	
	/** flag that indicates if the process succeeded (<code>true</code>) or failed (<code>false</code>) */
	public final boolean ok;
	/** the description of why the plugin has failed the validation process */
	public final String message;
	/** the number of errors that have occurred */
	public final int errorCount;
	/** the number of warnings that have occurred */
	public final int warningCount;
	
	/**
	 * Creates a new report.
	 * 
	 * @param ok flag that indicates if the process succeeded (<code>true</code>) or failed (<code>false</code>)
	 * @param message the description of why the plugin has failed the validation process
	 * @param errorCount the number of errors that have occurred
	 * @param warningCount the number of warnings that have occurred
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if message is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public ValidationReport(final boolean ok, final String message, final int errorCount, final int warningCount) throws IllegalArgumentException {
		if(message == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.ok = ok;
		this.message = message;
		this.errorCount = errorCount;
		this.warningCount = warningCount;
	}

}
