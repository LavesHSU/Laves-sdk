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
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Catmull-Rom spline.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class CatmullRomSpline {
	
	/** the points of the spline */
	private final Point2D.Float[] splinePoints;
	/** the display path of the spline */
	private final GeneralPath path;
	
	/** the default interpolation factor */
	private static final int DEF_INTERPOLATION = 24;
	
	/**
	 * Creates a new spline.
	 * 
	 * @param points the control points crossed by the curve
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if points == null</li>
	 * 		<li>if <code>points.size() < 2</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public CatmullRomSpline(final List<Point> points) throws IllegalArgumentException {
		this(points, DEF_INTERPOLATION);
	}
	
	/**
	 * Creates a new spline.
	 * 
	 * @param points the control points crossed by the curve
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if points is null</li>
	 * 		<li>if <code>points.length < 2</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public CatmullRomSpline(final Point[] points) throws IllegalArgumentException {
		this(points, DEF_INTERPOLATION);
	}
	
	/**
	 * Creates a new spline.
	 * 
	 * @param points the control points crossed by the curve
	 * @param interpolation the interpolation factor that means the number of intermediate points between two control points
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if points is null</li>
	 * 		<li>if <code>points.length < 2</code></li>
	 * 		<li>if interpolation is <code>< 1</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public CatmullRomSpline(final Point[] points, final int interpolation) throws IllegalArgumentException {
		this(toPointList(points), interpolation);
	}
	
	/**
	 * Creates a new spline.
	 * 
	 * @param points the control points crossed by the curve
	 * @param interpolation the interpolation factor that means the number of intermediate points between two control points
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if points is null</li>
	 * 		<li>if <code>points.size() < 2</code></li>
	 * 		<li>if interpolation is <code>< 1</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public CatmullRomSpline(final List<Point> points, final int interpolation) throws IllegalArgumentException {
		if(points == null || points.size() < 2 || interpolation < 1)
			throw new IllegalArgumentException("No valid argument!");
		
		splinePoints = new Point2D.Float[(points.size() - 1) * interpolation + 1];
		
		// there are 3 control points? then calculate the spline
		if(points.size() >= 3) {
	        float increments = 1.0f / (float)interpolation;
	        Point p0;
	        Point p1;
	        Point p2;
	        Point p3;
	        CatmullRomSplineSegment crss;
	        
			// go though all control points
			for(int i = 0; i < points.size() - 1; i++) {
				// use 4 control points to calculate the intermediate curve points
				p0 = (i == 0) ? points.get(i) : points.get(i - 1);
				p1 = points.get(i);
				p2 = points.get(i + 1);
				p3 = (i + 2 == points.size()) ? points.get(i + 1) : points.get(i + 2);
				
				// create a segment of the four points
				crss = new CatmullRomSplineSegment(p0, p1, p2, p3);
				
				// interpolate the intermediate curve points
				for(int j = 0; j <= interpolation; j++)
					splinePoints[(i * interpolation) + j] = crss.q(j * increments);
			}
		}
		else {
			// if there are only 2 points then create a line
			splinePoints[0] = new Point2D.Float(points.get(0).x, points.get(0).y);
			splinePoints[1] = new Point2D.Float(points.get(1).x, points.get(1).y);
		}
		
		// generate the path for spline
		path = new GeneralPath();
		Point2D.Float p;

		for(int i = 0; i < splinePoints.length; i++) {
			p = splinePoints[i];
			
			if(i == 0)
				path.moveTo(p.x, p.y);
			else
				path.lineTo(p.x, p.y);
		}
	}
	
	/**
	 * Gets the display path of the spline.
	 * 
	 * @return the display path
	 * @since 1.0
	 */
	public GeneralPath getPath() {
		return path;
	}
	
	/**
	 * Checks if the given point is onto the curve.
	 * 
	 * @param p the point
	 * @return <code>true</code> if the point is onto the spline otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if p is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean contains(final Point p) throws IllegalArgumentException {
		if(p == null)
			throw new IllegalArgumentException("No valid argument!");
		
		return contains(p, 1);
	}
	
	/**
	 * Checks if the given point is onto the curve.
	 * 
	 * @param p the point
	 * @param tolerance the tolerance for the distance check to the curve (example: tolerance = 2, that means the point must not be exactly onto the curve but in a distance of 2 pixel)
	 * @return <code>true</code> if the point is onto the spline otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if p is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public boolean contains(final Point p, final int tolerance) throws IllegalArgumentException {
		if(p == null)
			throw new IllegalArgumentException("No valid argument!");
		
		return contains(p.x, p.y, tolerance);
	}
	
	/**
	 * Checks if the given point is onto the curve.
	 * 
	 * @param x the x position of the point
	 * @param y the y poisition of the point
	 * @return <code>true</code> if the point is onto the spline otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean contains(final int x, final int y) {
		return contains(x, y, 1);
	}
	
	/**
	 * Checks if the given point is onto the curve.
	 * 
	 * @param x the x position of the point
	 * @param y the y poisition of the point
	 * @param tolerance the tolerance for the distance check to the curve (example: tolerance = 2, that means the point must not be exactly onto the curve but in a distance of 2 pixel)
	 * @return <code>true</code> if the point is onto the spline otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean contains(final int x, final int y, final int tolerance) {
		Point2D.Float p0 = splinePoints[0];
		Point2D.Float p1;
		
		// go through all points of the spline and check the point against the segment defined by the points p0 and p1
		for(int i = 1; i < splinePoints.length; i++) {
			p1 = splinePoints[i];
			
			/*
			 * INFO:
			 * Do not check only the segment bounds to skip points which are supposed to be outside of the segment!
			 * If the spline intersects itself then it could be that an early segment fits from its x coordinates of the
			 * end points to the given x position and the distance is not inside the tolerance but a later segment
			 * would fit into the tolerance.
			 * That means: do not skip segments and break up only if a valid point-to-segment distance is found!
			 */
			
			if(Math.abs(Line2D.ptSegDist(p0.x, p0.y, p1.x, p1.y, x, y)) <= tolerance)
				return true;
			
			p0 = p1;
		}
		
		return false;
	}
	
	/**
	 * Gets all points of the curve.
	 * 
	 * @return the points of the spline
	 * @since 1.0
	 */
	public Point2D.Float[] getPoints() {
		return splinePoints;
	}
	
	/**
	 * Checks if this spline and the specified one intersect.
	 * 
	 * @param spline the spline that should be checked for intersection
	 * @return the intersection point or <code>null</code> if there is no intersection
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if spline is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public SplineIntersectionPoint intersect(final CatmullRomSpline spline) throws IllegalArgumentException {
		final List<SplineIntersectionPoint> points = intersect(spline, true);
		
		if(points.size() > 0)
			return points.get(0);
		else
			return null;
	}
	
	/**
	 * Checks whether this spline intersects the specified one multiple times.
	 * 
	 * @param spline the spline that should be checked for intersection
	 * @return the intersection points
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if spline is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public List<SplineIntersectionPoint> intersectMulti(final CatmullRomSpline spline) throws IllegalArgumentException {
		return intersect(spline, false);
	}
	
	/**
	 * Checks whether this spline intersects the specified one.
	 * 
	 * @param spline the spline that should be checked for intersection
	 * @param singleIntersection <code>true</code> if only one intersection point should be found otherwise <code>false</code>
	 * @return the intersection points
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if spline is null</li>
	 * </ul>
	 */
	private List<SplineIntersectionPoint> intersect(final CatmullRomSpline spline, final boolean singleIntersection) throws IllegalArgumentException {
		if(spline == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final List<SplineIntersectionPoint> intersectionPoints = new ArrayList<SplineIntersectionPoint>(singleIntersection ? 1 : 5);
		Point2D.Float lastP1 = this.splinePoints[0];
		Point2D.Float currP1;
		Point2D.Float lastP2;
		Point2D.Float currP2;
		SplineIntersectionPoint intersectionPoint;
		
		// check each line segment of spline one (edge 1) against each line segment of spline two (edge 2)
		for(int i = 1; i < this.splinePoints.length; i++) {
			currP1 = this.splinePoints[i];
			lastP2 = spline.splinePoints[0];
			
			for(int j = 1; j < spline.splinePoints.length; j++) {
				currP2 = spline.splinePoints[j];
				intersectionPoint = lineSegIntersection(lastP1, currP1, lastP2, currP2, i, j, spline);
				if(intersectionPoint != null) {
					intersectionPoints.add(intersectionPoint);
					if(singleIntersection)
						return intersectionPoints;
				}
				
				lastP2 = currP2;
			}
			
			lastP1 = currP1;
		}
		
		return intersectionPoints;
	}
	
	/**
	 * Checks whether the given line segments intersect.
	 * 
	 * @param p1 the start point of the segment in this spline
	 * @param p2 the end point of the segment in this spline
	 * @param q1 the start point of the segment in the other spline
	 * @param q2 the end point of the segment in the other spline
	 * @param segIndexP the segment index of the first segment
	 * @param segIndexQ the segment index of the second segment
	 * @param otherSpline the oher spline
	 * @return the intersection point or <code>null</code> if there is no intersection
	 * @since 1.0
	 */
	private SplineIntersectionPoint lineSegIntersection(final Point2D.Float p1, final Point2D.Float p2, final Point2D.Float q1, final Point2D.Float q2, final int segIndexP, final int segIndexQ, final CatmullRomSpline otherSpline) {
		/*
		 * Firstly we define the vector cross product for 2d space as: v x u = v(x)*u(y) - v(y)*u(x).
		 * 
		 * Example:
		 *   q+s o  o p+r
		 *        \/_________ intersection point
		 *        /\
		 *       /  \
		 *      /    \        with p,q,r and s as vectorss
		 *   p o      o q
		 * 
		 * Calculation of the intersection point of two line segments:
		 * 1. An intersection exists if we can find t,u such that p + t*r = q + u*s
		 * 2. Cross both sides with s: (p + t*r) x s = (q + u*s) x s <=> p x s + t*(r x s) = q x s + u*(s x s)
		 * 3. Since s x s = 0 it is: t*(r x s) = q x s - p x s <=> t*(r x s) = (q - p) x s
		 * 4. Solving for t it is: t = ((q - p) x s) / (r x s)
		 * 5. Solving for u it is: u = ((q - p) x r) / (r x s)
		 * 
		 * If t >= 0 && t <= 1 and u >= 0 && u <= 1 the two line segments intersect and the intersection point is: i = p + t*r.
		 * 
		 * Notice: below it is v = q - p
		 * 
		 * see also: http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
		 */
		
		final Point2D.Float r = new Point2D.Float(p2.x - p1.x, p2.y - p1.y);
		final Point2D.Float s = new Point2D.Float(q2.x - q1.x, q2.y - q1.y);
		final Point2D.Float v = new Point2D.Float(q1.x - p1.x, q1.y - p1.y);
		final float rCROSSs = r.x*s.y - r.y*s.x;
		
		// line segments are parallel? then an intersection is impossible!
		if(rCROSSs == 0)
			return null;
		
		final float t = (v.x*s.y - v.y*s.x) / rCROSSs;
		final float u = (v.x*r.y - v.y*r.x) / rCROSSs;
		
		// is the intersection point inside the line segment described by p1, p2?
		if(t >= 0.0f && t <= 1.0f && u >= 0.0f && u <= 1.0f)
			return new SplineIntersectionPoint(this, otherSpline,(int)(p1.x + t*r.x), (int)(p1.y + t*r.y), segIndexP, segIndexQ);
		else
			return null;
	}
	
	/**
	 * Converts a point array to a point list.
	 * 
	 * @param points the points
	 * @return the points as a {@link List}
	 * @since 1.0
	 */
	private static List<Point> toPointList(final Point[] points) {
		if(points == null)
			throw new IllegalArgumentException("No valid argument!");
		
		final List<Point> result = new ArrayList<Point>(points.length);
		for(Point p : points)
			result.add(p);
		
		return result;
	}
	
	/**
	 * Represents a catmull-rom spline segment of four points.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class CatmullRomSplineSegment {
		
		/** point 1 */
		private final Point p0;
		/** point 2 */
		private final Point p1;
		/** point 3 */
		private final Point p2;
		/** point 4 */
		private final Point p3;
		
		/**
		 * Creates a new segment.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * The specified control points should be one after another.
		 * 
		 * @param p0 control point 1
		 * @param p1 control point 2
		 * @param p2 control point 3
		 * @param p3 control point 4
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if p0 is null</li>
		 * 		<li>if p1 is null</li>
		 * 		<li>if p2 is null</li>
		 * 		<li>if p3 is null</li>
		 * </ul>
		 * @since 1.0
		 */
		public CatmullRomSplineSegment(final Point p0, final Point p1, final Point p2, final Point p3) throws IllegalArgumentException {
			if(p0 == null || p1 == null | p2 == null || p3 == null)
				throw new IllegalArgumentException("No valid argument!");
			
			this.p0 = p0;
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
		}
		
		/**
		 * Interpolates the points of the segment.
		 * 
		 * @param t the increments
		 * @return the point
		 * @since 1.0
		 */
		public Point2D.Float q(final float t) {
			final float tSq = t*t;
			final float tCubic = t*t*t;
			final float x = 0.5f * ((2 * p1.x) + (p2.x - p0.x)*t + (2*p0.x - 5*p1.x + 4*p2.x - p3.x)*tSq + (3*p1.x - p0.x - 3*p2.x + p3.x)*tCubic); 
			final float y = 0.5f * ((2 * p1.y) + (p2.y - p0.y)*t + (2*p0.y - 5*p1.y + 4*p2.y - p3.y)*tSq + (3*p1.y - p0.y - 3*p2.y + p3.y)*tCubic);
			
			return new Point2D.Float(x, y);
		}
		
	}

}
