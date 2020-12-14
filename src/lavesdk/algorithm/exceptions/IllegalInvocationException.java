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
 * Class:		IllegalInvocationException
 * Task:		Exception for illegal incovations
 * Created:		10.11.13
 * LastChanges:	12.11.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.exceptions;

/**
 * Exception for an illegal invocation of a method.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class IllegalInvocationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new exception.
	 * 
	 * @param msg message of the exception
	 * @since 1.0
	 */
	public IllegalInvocationException(final String msg) {
		super(msg);
	}

}
