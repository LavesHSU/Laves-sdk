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

package lavesdk.algorithm.text;

/**
 * Represents an annotation of an {@link AlgorithmStep}.
 * <br><br>
 * <b>Formatting the annotation</b>:<br>
 * You can format your annotation text by using html tags. If you want to integrate images into your annotation you have to consider the following:
 * as the source of an image tag you do not use a path to an image file but instead you use image keys that you have defined in an
 * {@link AnnotationImagesList}.<br>
 * <u>Example</u>:<br>
 * <pre>
 * // create an empty images list
 * final AnnotationImagesList ail = new AnnotationImagesList();
 * // and add our image resource files, e.g. by using the resource loader of the plugin
 * ail.add("img1", resLoader.getResource("main/resources/image1.png");
 * ail.add("img2", resLoader.getResource("main/resources/image2.png");
 * 
 * // create the annotation text
 * final String text = "&lt;b&gt;My Annotation&lt;/b&gt;&lt;br&gt;This is image one: &lt;img src=\"img1\"&gt;&lt;br&gt;And this is image two: &lt;img src=\"img2\"&gt;";
 * // finally create the annotation
 * final Annotation a = new Annotation(text, ail);
 * </pre>
 * 
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public class Annotation {
	
	/** the text of the annotation */
	private final String text;
	/** the image list of the annotation */
	private final AnnotationImagesList images;
	
	/**
	 * Creates a new annotation.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use {@link #Annotation(String, AnnotationImagesList)} to integrate images into the annotation.
	 * 
	 * @param text the text of the annotation (<b>can contain html tags to format the text</b>, for further information see the class documentation too)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if text is null</li>
	 * 		<li>if text is empty</li>
	 * </ul>
	 * @since 1.1
	 */
	public Annotation(final String text) throws IllegalArgumentException {
		this(text, null);
	}
	
	/**
	 * Creates a new annotation.
	 * 
	 * @param text the text of the annotation (<b>can contain html tags to format the text</b>, for further information see the class documentation too)
	 * @param images the list of images that are used in the annotation text by using <code>&lt;img src="imgKey"&gt;</code> or <code>null</code> if there are no images in the text
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if text is null</li>
	 * 		<li>if text is empty</li>
	 * </ul>
	 * @since 1.0
	 */
	public Annotation(final String text, final AnnotationImagesList images) throws IllegalArgumentException {
		if(text == null || text.isEmpty())
			throw new IllegalArgumentException("No valid argument!");
		
		this.text = text;
		this.images = images;
	}
	
	/**
	 * Gets the text of the annotation.
	 * 
	 * @return the text of the annotation (<b>can contain html tags to format the text</b>)
	 * @since 1.0
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Gets the images list of the annotation.
	 * 
	 * @return the images list or <code>null</code>
	 * @since 1.0
	 */
	public AnnotationImagesList getImagesList() {
		return images;
	}

}
