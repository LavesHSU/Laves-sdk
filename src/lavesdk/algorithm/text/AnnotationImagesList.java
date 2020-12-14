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
 * Class:		AnnotationImagesList
 * Task:		List of image URLs
 * Created:		16.05.14
 * LastChanges:	06.05.15
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.text;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a list of images of an {@link Annotation}.
 * <br><br>
 * <b>As URL</b>:<br>
 * Use {@link #add(String, URL)} to add a new mapping between an image key and the url of the image resource. The image key is used in
 * the image html tag <code>img</code> to identify the image. By way of example we have a mapping that uses the image key <code>img1</code> then
 * we have to used it in the image tag like the following: <code>&lt;img src="img1"&gt;</code>.
 * 
 * @author jdornseifer
 * @version 1.3
 * @since 1.0
 */
public class AnnotationImagesList {
	
	/** the mapping between image key and image url */
	private final Map<String, URL> imageURLsList;
	
	/**
	 * Creates a new empty images list.
	 * 
	 * @since 1.0
	 */
	public AnnotationImagesList() {
		imageURLsList = new HashMap<String, URL>();
	}
	
	/**
	 * Adds a new image url to the list.
	 * 
	 * @param imgKey the image key that is used in the html img tag like <code>&lt;img src="img1"&gt;</code>
	 * @param imgURL the url of the image resource that should be displayed
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if imgKey is null</li>
	 * 		<li>if imgKey is empty</li>
	 * 		<li>if the list already contains an image with the specified key</li>
	 * 		<li>if imgURL is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public void add(final String imgKey, final URL imgURL) throws IllegalArgumentException {
		if(imgKey == null || imgKey.isEmpty() || imageURLsList.containsKey(imgKey) || imgURL == null)
			throw new IllegalArgumentException("No valid argument!");
		
		imageURLsList.put(imgKey, imgURL);
	}
	
	/**
	 * Gets the url of an image.
	 * 
	 * @param imgKey the key of the image
	 * @return the url of the image or <code>null</code> if there is no image associated with the specified key
	 * @since 1.0
	 */
	public URL get(final String imgKey) {
		return imageURLsList.get(imgKey);
	}

}
