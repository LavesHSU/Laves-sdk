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

package lavesdk.algorithm.plugin;

import lavesdk.LAVESDKV;
import lavesdk.utils.MutableNumber;

/**
 * Use {@link #validate(AlgorithmPlugin, boolean)} to validate an {@link AlgorithmPlugin} meaning if it is functioning.
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class Validator {
	
	private Validator() {
	}
	
	/**
	 * Creates a validation report for the given plugin.
	 * <br><br>
	 * The report indicates if the plugin is valid. If the plugin is invalid the report contains a detailed
	 * description of what has failed.
	 * 
	 * @param plugin the plugin
	 * @param skipWarnings <code>true</code> if warnings should be ignored (meaning that a plugin with warnings is still considered as valid) otherwise <code>false</code>
	 * @return the validation report
	 * @since 1.0
	 */
	public static ValidationReport validate(final AlgorithmPlugin plugin, final boolean skipWarnings) {
		final StringBuilder errMsg = new StringBuilder();
		final MutableNumber<Integer> errCount = new MutableNumber<Integer>(0);
		final MutableNumber<Integer> warnCount = new MutableNumber<Integer>(0);
		
		// validate the properties of the plugin
		validate(plugin.getName(), "name", errMsg, errCount, warnCount, false, skipWarnings);
		validate(plugin.getDescription(), "description", errMsg, errCount, warnCount, true, skipWarnings);
		validate(plugin.getAuthor(), "type", errMsg, errCount, warnCount, true, skipWarnings);
		validate(plugin.getAuthor(), "author", errMsg, errCount, warnCount, true, skipWarnings);
		validate(plugin.getAuthorContact(), "author contact", errMsg, errCount, warnCount, true, skipWarnings);
		validate(plugin.getAssumptions(), "assumption", errMsg, errCount, warnCount, true, skipWarnings);
		validate(plugin.getProblemAffiliation(), "problem affiliation", errMsg, errCount, warnCount, true, skipWarnings);
		validate(plugin.getSubject(), "subject", errMsg, errCount, warnCount, true, skipWarnings);
		validate(plugin.getInstructions(), "instructions", errMsg, errCount, warnCount, true, skipWarnings);
		validate(plugin.getVersion(), "version", errMsg, errCount, warnCount, true, skipWarnings);
		
		// validate the runtime environment
		if(plugin.getRuntimeEnvironment() == null && !skipWarnings) {
			warnCount.value(warnCount.value() + 1);
			errMsg.append("Warning " + warnCount + ": plugin has no runtime environment\n");
		}
		// validate the algorithm text
		if(plugin.getText() == null && !skipWarnings) {
			warnCount.value(warnCount.value() + 1);
			errMsg.append("Warning " + warnCount + ": plugin has no algorithm text\n");
		}
		
		// validate the version information of the plugin
		if(plugin.getUsedSDKVersion() == null) {
			errCount.value(errCount.value() + 1);
			errMsg.append("Error " + errCount + ": plugin has no used sdk version\n");
		}
		else if(!LAVESDKV.checkCompatibility(plugin)) {
			errCount.value(errCount.value() + 1);
			errMsg.append("Error " + errCount + ": the used sdk version of the plugin is not compatible with the current version\n");
		}
		
		// insert a message header
		if(errCount.value() > 0 || warnCount.value() > 0)
			errMsg.insert(0, "" + errCount + " error(s) and " + warnCount + " warning(s) found!\n");
		
		return new ValidationReport(errCount.value() == 0 && (skipWarnings || warnCount.value() == 0), errMsg.toString(), errCount.value(), warnCount.value());
	}
	
	/**
	 * Checks a given string and writes an error or warning message if necessary.
	 * 
	 * @param s the string
	 * @param attrName the attribute
	 * @param errMsg the error message
	 * @param errCount the error counter
	 * @param errCount the warning counter
	 * @param canBeEmpty <code>true</code> if the string can be empty otherwise <code>false</code>
	 * @param skipWarnings <code>true</code> if warnings should be ignored (meaning that a plugin with warnings is still considered as valid) otherwise <code>false</code>
	 * @since 1.0
	 */
	private static void validate(final String s, final String attrName, final StringBuilder errMsg, final MutableNumber<Integer> errCount, final MutableNumber<Integer> warnCount, final boolean canBeEmpty, final boolean skipWarnings) {
		if(s == null) {
			errCount.value(errCount.value() + 1);
			errMsg.append("Error " + errCount + ": plugin has an invalid " + attrName + " (null)\n");
		}
		else if(s.isEmpty() && !canBeEmpty) {
			if(!skipWarnings) {
				warnCount.value(warnCount.value() + 1);
				errMsg.append("Warning " + warnCount + ": plugin has an empty " + attrName + "\n");
			}
		}
	}

}
