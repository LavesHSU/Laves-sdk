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
 * Interface:	ProgressListener
 * Task:		Inform about the progress of something
 * Created:		01.09.13
 * LastChanges:	01.09.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.utils;

/**
 * Listener to listen to progress events.
 * <br><br>
 * With {@link #totalProgress(int)} you can retrieve the amount of steps the running progress need to finish
 * and with {@link #currentProgress(int)} you can retrieve the current step at which the running progress is.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public interface ProgressListener {
	
	/**
	 * Gets the total amount to finish the progress.
	 * 
	 * @param amount total amount of progress
	 * @since 1.0
	 */
	public void totalProgress(final int amount);
	
	/**
	 * Gets the current step of the progress indicated by a number between <code>0</code> and <code>{@link #totalProgress(int)}</code>.
	 * 
	 * @param step the current step
	 * @since 1.0
	 */
	public void currentProgress(final int step);

}
