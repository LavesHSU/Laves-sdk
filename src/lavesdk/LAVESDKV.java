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
 * Class:		LAVESDKV
 * Task:		Version information
 * Created:		27.01.14
 * LastChanges:	27.01.14
 * LastAuthor:	jdornseifer
 */

package lavesdk;

import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.resources.Resources;

/**
 * Represents the version information of the LAVESDK.
 * <br><br>
 * Use {@link #CURRENT} to get the version information of the current LAVESDK (meaning the LAVESDK that is in use). Use {@link #MINIMUM}
 * to get the minimum version information of the current LAVESDK.<br>
 * An algorithm plugin is only compatible with the current LAVESDK if it uses a version that is greater or equal the minimum
 * version and less or equal the current version. This is a safeguard to avoid runtime exceptions because of missing methods,
 * classes or interfaces that the plugin uses.
 * <br><br>
 * Use {@link #checkCompatibility(AlgorithmPlugin)} to check whether a plugin is compatible with the current LAVESDK version or not.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class LAVESDKV implements Comparable<LAVESDKV> {
	
	/** the major number of the version */
	private final int major;
	/** the minor number of the version */
	private final int minor;
	
	/** contains the version information of the current LAVESDK */
	public static final LAVESDKV CURRENT;
	/** contains the minimum version information of the current LAVESDK */
	public static final LAVESDKV MINIMUM;
	
	static {
		// read the version information from the properties file
		Integer majorNum = new Integer(Resources.getInstance().LAVESDK_PROPERTIES.getProperty("version_major", "1"));
		Integer minorNum = new Integer(Resources.getInstance().LAVESDK_PROPERTIES.getProperty("version_minor", "0"));
		Integer minMajorNum = new Integer(Resources.getInstance().LAVESDK_PROPERTIES.getProperty("min_version_major", "1"));
		Integer minMinorNum = new Integer(Resources.getInstance().LAVESDK_PROPERTIES.getProperty("min_version_minor", "0"));
		
		if(majorNum.intValue() < 1)
			majorNum = new Integer(1);
		if(minorNum.intValue() < 0)
			minorNum = new Integer(0);
		if(minMajorNum.intValue() < 1)
			minMajorNum = new Integer(1);
		if(minMinorNum.intValue() < 0)
			minMinorNum = new Integer(0);
		
		CURRENT = new LAVESDKV(majorNum.intValue(), minorNum.intValue());
		MINIMUM = new LAVESDKV(minMajorNum.intValue(), minMinorNum.intValue());
	}
	
	/**
	 * Creates a new LAVESDK version.
	 * 
	 * @param major the major number of the version (greater or equal <code>1</code>)
	 * @param minor the major number of the version (greater or equal <code>0</code>)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if major is <code>< 1</code></li>
	 * 		<li>if minor is <code>< 0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public LAVESDKV(final int major, final int minor) throws IllegalArgumentException {
		if(major < 1)
			throw new IllegalArgumentException("No valid argument!");
		else if(minor < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		this.major = major;
		this.minor = minor;
	}
	
	/**
	 * Indicates whether the specified LAVESDK version is compatible with the current LAVESDK version meaning it is a version greater or equal
	 * the minimum ({@link #MINIMUM} and less or equal the maximum ({@link #CURRENT}) of the current version.
	 * 
	 * @param version the version to be checked
	 * @return <code>true</code> if the specified version is compatible otherwise <code>false</code>
	 * @since 1.0
	 */
	public static boolean checkCompatibility(final LAVESDKV version) {
		if(version == null)
			return false;
		else
			return version.compareTo(MINIMUM) >= 0 && version.compareTo(CURRENT) <= 0;
	}
	
	/**
	 * Indicates whether the specified plugin is compatible with the current LAVESDK version meaning if it uses a version that is greater or
	 * equal the minimum version ({@link #MINIMUM}) and less or equal the current version ({@link #CURRENT}) of the LAVESDK.
	 * 
	 * @param plugin the plugin to be checked
	 * @return <code>true</code> if the specified plugin is compatible otherwise <code>false</code>
	 * @since 1.0
	 */
	public static boolean checkCompatibility(final AlgorithmPlugin plugin) {
		return checkCompatibility(plugin.getUsedSDKVersion());
	}
	
	/**
	 * Compares this version with the specified version and returns a negative integer, zero, or a positive integer
	 * as this version is less than, equal to, or greater than the specified version.
	 * 
	 * @param version the version to be compared
	 * @return a negative integer, zero, or a positive integer as this version is less than, equal to, or greater than the specified version
	 * @since 1.0
	 */
	@Override
	public int compareTo(LAVESDKV version) {
		if(version == null)
			return 1;
		
		final int deltaMajor = this.major - version.major;
		final int deltaMinor = this.minor - version.minor;
		
		if(deltaMajor == 0)
			return deltaMinor;
		else
			return deltaMajor;
	}
	
	/**
	 * Formats the version.
	 * 
	 * @return a version string in the format <code>major.minor</code>
	 * @since 1.0
	 */
	@Override
	public String toString() {
		return "" + major + "." + minor;
	}

}
