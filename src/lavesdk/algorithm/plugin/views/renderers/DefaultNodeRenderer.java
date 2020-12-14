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
 * Class:		DefaultNodeRenderer
 * Task:		Default renderer of nodes
 * Created:		12.12.14
 * LastChanges:	12.12.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views.renderers;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import lavesdk.math.graph.network.Node;
import lavesdk.utils.MathUtils;

/**
 * The default renderer of nodes.
 * <br><br>
 * Based on {@link DefaultVertexRenderer} and the excess of a node is drawn at the attachment point of a node.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.3
 */
public class DefaultNodeRenderer extends DefaultVertexRenderer<Node> {
	
	/** the x position of the weight */
	private int attachmentX;
	/** the y position of the weight */
	private int attachmentY;
	
	/**
	 * Creates a new renderer.
	 * 
	 * @since 1.0
	 */
	public DefaultNodeRenderer() {
		super();
		
		attachmentX = 0;
		attachmentY = 0;
	}
	
	@Override
	public void setAttachmentPoint(int x, int y) {
		super.setAttachmentPoint(x, y);
		
		attachmentX = x;
		attachmentY = y;
	}
	
	@Override
	public void draw(Graphics2D g, Node o) {
		super.draw(g, o);
		
		// if the node does not have an excess then quit
		if(!o.hasExcess())
			return;
		
		final String weightAsString = MathUtils.formatFloat(o.getExcess());
		final FontMetrics fm = g.getFontMetrics();
		g.drawString(weightAsString, (attachmentX < xCenter) ? attachmentX - fm.stringWidth(weightAsString) - 2 : attachmentX + 2, (attachmentY > yCenter) ? attachmentY + fm.getAscent() + fm.getLeading() : attachmentY - 2);
	}

}
