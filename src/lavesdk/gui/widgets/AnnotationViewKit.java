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

package lavesdk.gui.widgets;

import java.net.URL;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

import lavesdk.algorithm.text.AnnotationImagesList;

/**
 * A html editor kit to display annotations that use an {@link AnnotationImagesList} to display images.
 * 
 * @author jdornseifer
 * @version 1.3
 * @since 1.0
 */
public class AnnotationViewKit extends HTMLEditorKit {

	private static final long serialVersionUID = 1L;
	
	/** the view factory instance */
	private final AnnotationViewFactory viewFactory;
	
	/**
	 * Creates a new annotation view kit.
	 * 
	 * @param imagesList the list of the annotation images or <code>null</code>
	 * @since 1.0
	 */
	public AnnotationViewKit(final AnnotationImagesList imagesList) {
		viewFactory = new AnnotationViewFactory(imagesList);
	}
	
	@Override
	public ViewFactory getViewFactory() {
		return viewFactory;
	}
	
	/**
	 * The view factory.
	 * 
	 * @author jdornseifer
	 * @version 1.3
	 * @since 1.0
	 */
	private static class AnnotationViewFactory extends HTMLFactory {
		
		/** the images list or <code>null</code> */
		private final AnnotationImagesList imagesList;
		
		/**
		 * Creates a new view factory.
		 * 
		 * @param imagesList the list of annotation images or <code>null</code>
		 * @since 1.0
		 */
		public AnnotationViewFactory(final AnnotationImagesList imagesList) {
			this.imagesList = imagesList;
		}
		
		@Override
		public View create(Element elem) {
			final AttributeSet attrs = elem.getAttributes();
			final Object o = attrs.getAttribute(StyleConstants.NameAttribute);
			
			// load a custom image view for the html tag "img"
			if(o instanceof HTML.Tag) {
				HTML.Tag kind = (HTML.Tag)o;
				if(kind == HTML.Tag.IMG)
					return new AnnotationImageView(elem);
			}
			
			return super.create(elem);
		}
		

		
		/**
		 * A custom image view that can display images using an {@link AnnotationImagesList}.
		 * 
		 * @author jdornseifer
		 * @version 1.3
		 */
		private class AnnotationImageView extends ImageView {

			/**
			 * Creates a new annotation image view.
			 * 
			 * @param elem the element
			 * @since 1.0
			 */
			public AnnotationImageView(final Element elem) {
				super(elem);
			}
			
			@Override
			public URL getImageURL() {
				if(AnnotationViewFactory.this.imagesList == null)
					return null;
				
				final String sourceKey = (String)getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
				
				if(sourceKey == null)
					return null;
				else
					return AnnotationViewFactory.this.imagesList.get(sourceKey);
			}
			
		}
		
	}

}
