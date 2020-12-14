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

package lavesdk.graphics;

import java.awt.Point;

/**
 * Represents a spline intersection point.
 * <br><br>
 * With {@link #getSegmentIndexOfSpline1()} and {@link #getSegmentIndexOfSpline2()} to identify the segments of the two splines
 * that intersect.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class SplineIntersectionPoint extends Point {

	private static final long serialVersionUID = 1L;

	/** the first spline */
	private final CatmullRomSpline spline1;
	/** the second spline */
	private final CatmullRomSpline spline2;
	/** the index of the segment in spline 1 that intersects with a segment of spline 2 */
	private final int segIndexSpline1;
	/** the index of the segment in spline 2 that intersects with a segment of spline 1 */
	private final int segIndexSpline2;
	
	/**
	 * Creates a new intersection point.
	 * 
	 * @param spline1 the first spline
	 * @param spline2 the second spline
	 * @param x the x position of the intersection point
	 * @param y the y position of the intersection point
	 * @param segIndexSpline1 the index of the segment end point in spline 1 that intersects with a segment of spline 2
	 * @param segIndexSpline2 the index of the segment end point in spline 2 that intersects with a segment of spline 2
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if spline1 is null</li>
	 * 		<li>if spline2 is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public SplineIntersectionPoint(final CatmullRomSpline spline1, final CatmullRomSpline spline2, final int x, final int y, final int segIndexSpline1, final int segIndexSpline2) throws IllegalArgumentException {
		super(x, y);
		
		if(spline1 == null || spline2 == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.spline1 = spline1;
		this.spline2 = spline2;
		this.segIndexSpline1 = segIndexSpline1;
		this.segIndexSpline2 = segIndexSpline2;
	}
	
	/**
	 * Gets the first spline that is part of the intersection.
	 * 
	 * @return the first spline
	 * @since 1.0
	 */
	public final CatmullRomSpline getSpline1() {
		return spline1;
	}
	
	/**
	 * Gets the second spline that is part of the intersection.
	 * 
	 * @return the second spline
	 * @since 1.0
	 */
	public final CatmullRomSpline getSpline2() {
		return spline2;
	}
	
	/**
	 * Gets the index of the segment in spline 1 that intersects with a segment of spline 2.
	 * <br><br>
	 * This index describes the second point of the segment meaning that <code>getSegmentIndexOfSpline1() - 1</code>
	 * is the first segment point. So you can use <code>spline1.getPoints[point.getSegmentIndexOfSpline1() - 1]</code>
	 * to get the start point and <code>spline1.getPoints[point.getSegmentIndexOfSpline1()]</code> to get the end point
	 * of the segment.
	 * 
	 * @return the index of the intersection segment in spline 1
	 * @since 1.0
	 */
	public final int getSegmentIndexOfSpline1() {
		return segIndexSpline1;
	}
	
	/**
	 * Gets the index of the segment in spline 2 that intersects with a segment of spline 1.
	 * <br><br>
	 * This index describes the second point of the segment meaning that <code>getSegmentIndexOfSpline2() - 1</code>
	 * is the first segment point. So you can use <code>spline2.getPoints[point.getSegmentIndexOfSpline2() - 1]</code>
	 * to get the start point and <code>spline2.getPoints[point.getSegmentIndexOfSpline2()]</code> to get the end point
	 * of the segment.
	 * 
	 * @return the index of the intersection segment in spline 1
	 * @since 1.0
	 */
	public final int getSegmentIndexOfSpline2() {
		return segIndexSpline2;
	}

}
