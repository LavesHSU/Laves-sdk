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
 * Class:		MathUtils
 * Task:		Utility functions for mathematical purposes
 * Created:		29.01.14
 * LastChanges:	12.05.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.utils;

import java.awt.Point;
import java.text.NumberFormat;

/**
 * Utility functions for mathematical purposes that should extend (but not replace) the {@link Math} utility functions.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class MathUtils {
	
	private MathUtils() {
	}
	
	/**
	 * Formats a float value.
	 * 
	 * @param value the float value
	 * @return the integer value or the float value with its decimal places if the value is not an integer
	 * @since 1.0
	 */
	public static String formatFloat(final float value) {
		return formatDouble(value);
	}
	
	/**
	 * Formats a double value.
	 * 
	 * @param value the double value
	 * @return the integer value or the double value with its decimal places if the value is not an integer
	 * @since 1.0
	 */
	public static String formatDouble(final double value) {
		final int valueAsInteger = (int)value;
		
		return (value % valueAsInteger == 0) ? "" + valueAsInteger : NumberFormat.getInstance().format(value);
	}
	
	/**
	 * Gets the maximum value of a set of values.
	 * 
	 * @param values the set of values
	 * @return the maximum value
	 * @since 1.0
	 */
	public static int max(final int... values) {
		int max = Integer.MIN_VALUE;
		
		for(int value : values)
			max = Math.max(max, value);
		
		return max;
	}
	
	/**
	 * Performs a counter clockwise test that specifies whether a point p2 (x2,y2) is left (counter clockwise) or
	 * right (clockwise) to a vector p0p1.
	 * 
	 * @param x0 the x position of point p0
	 * @param y0 the y position of point p0
	 * @param x1 the x position of point p1
	 * @param y1 the y position of point p1
	 * @param x2 the x position of point p2
	 * @param y2 the y position of point p2
	 * @return <code>0</code> if p2 is collinear to vec(p0p1), <code>> 0</code> if p2 is at the left side of vec(p0p1) (counter clockwise) or <code>< 0</code> if p2 is at the right side of vec(p0p1) (clockwise)
	 * @since 1.0
	 */
	public static int ccw(final int x0, final int y0, final int x1, final int y1, final int x2, final int y2) {
		final double c = (x1 - x0) * (y2 - y0) - (x2 - x0) * (y1 - y0);
		if(Math.abs(c) <= 0.00000001)
			return 0;
		else
			return (c > 0) ? 1 : -1;
	}
	
	/**
	 * Performs a counter clockwise test that specifies whether a point p2 is left (counter clockwise) or right (clockwise) to a vector p0p1.
	 * 
	 * @param p0 the point p0
	 * @param p1 the point p1
	 * @param p2 the point p2
	 * @return <code>0</code> if p2 is collinear to vec(p0p1), <code>> 0</code> if p2 is at the left side of vec(p0p1) (counter clockwise) or <code>< 0</code> if p2 is at the right side of vec(p0p1) (clockwise)
	 * @since 1.0
	 */
	public static int ccw(final Point p0, final Point p1, final Point p2) {
		return ccw(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y);
	}

}
