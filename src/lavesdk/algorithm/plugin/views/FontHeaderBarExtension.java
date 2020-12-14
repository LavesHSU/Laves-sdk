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
 * Class:		FontHeaderBarExtension
 * Task:		Extend the header bar with font operations
 * Created:		06.02.14
 * LastChanges:	06.02.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.plugin.views;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;

/**
 * Extends the header bar of a {@link View} with font size operations.
 * <br><br>
 * The available operations are increase/decrease/normalize the font size of the view.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #FontHeaderBarExtension(View, float, boolean, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>TEXTVIEW_FONTSIZE_UP_BTN_TOOLTIP</i>: the tooltip text of the font size up button in the header bar extension</li>
 * 		<li><i>TEXTVIEW_FONTSIZE_DOWN_BTN_TOOLTIP</i>: the tooltip text of the font size down button in the header bar extension</li>
 * 		<li><i>TEXTVIEW_FONTSIZE_NORMAL_BTN_TOOLTIP</i>: the tooltip text of the normal font size button in the header bar extension</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class FontHeaderBarExtension extends ViewHeaderBarExtension {
	
	/** the font size up button */
	private final JButton  fontSizeUpBtn;
	/** the font size down button */
	private final JButton  fontSizeDownBtn;
	/** the font size normal button */
	private final JButton  fontSizeNormalBtn;
	/** the event controller */
	private final EventController eventController;
	/** the normal font size */
	private float normalFontSize;

	/**
	 * Creates a new font header bar extension.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the extension. The following language labels are available:
	 * <ul>
	 * 		<li><i>TEXTVIEW_FONTSIZE_UP_BTN_TOOLTIP</i>: the tooltip text of the font size up button in the header bar extension</li>
	 * 		<li><i>TEXTVIEW_FONTSIZE_DOWN_BTN_TOOLTIP</i>: the tooltip text of the font size down button in the header bar extension</li>
	 * 		<li><i>TEXTVIEW_FONTSIZE_NORMAL_BTN_TOOLTIP</i>: the tooltip text of the normal font size button in the header bar extension</li>
	 * </ul>
	 * 
	 * @param view the view its header bar should be extended by this extension
	 * @param normalFontSize the normal or default font size
	 * @param separated <code>true</code> if a separator should be added automatically at the end of creating the extension otherwise <code>false</code>
	 * @param langFile the language file or <code>null</code> if the extension should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages should be shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if view is null</li>
	 * 		<li>if normaleFontSize is <code>< 1</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public FontHeaderBarExtension(final View view, final float normalFontSize, final boolean separated, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		super(view, separated, langFile, langID);
		
		if(normalFontSize < 1)
			throw new IllegalArgumentException("No valid argument!");
		
		this.normalFontSize = normalFontSize;
		this.fontSizeUpBtn = new JButton(Resources.getInstance().FONTSIZE_UP_ICON);
		this.fontSizeDownBtn = new JButton(Resources.getInstance().FONTSIZE_DOWN_ICON);
		this.fontSizeNormalBtn = new JButton(Resources.getInstance().FONTSIZE_NORMAL_ICON);
		this.eventController = new EventController();
		
		// load the tooltips of the header bar buttons
		fontSizeUpBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "TEXTVIEW_FONTSIZE_UP_BTN_TOOLTIP", langID, "Font Size Up") + "</html>");
		fontSizeDownBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "TEXTVIEW_FONTSIZE_DOWN_BTN_TOOLTIP", langID, "Font Size Down") + "</html>");
		fontSizeNormalBtn.setToolTipText("<html>" + LanguageFile.getLabel(langFile, "TEXTVIEW_FONTSIZE_NORMAL_BTN_TOOLTIP", langID, "Standard Font Size") + "</html>");
		
		// add the button listener
		fontSizeUpBtn.addActionListener(eventController);
		fontSizeDownBtn.addActionListener(eventController);
		fontSizeNormalBtn.addActionListener(eventController);
	}

	@Override
	protected void createExtension() {
		addComponent(fontSizeDownBtn);
		addComponent(fontSizeNormalBtn);
		addComponent(fontSizeUpBtn);
	}
	
	/**
	 * Changes the font size.
	 * 
	 * @param diff the difference to the old one or <code>0.0f</code> to reset the normal size
	 * @since 1.0
	 */
	private void changeFontSize(final float diff) {
		final Font f = view.getFont();
		if(f != null)
			view.setFont((diff != 0.0f) ? f.deriveFont(f.getSize() + diff) : f.deriveFont(normalFontSize));
	}
	
	/**
	 * Handles the events of the buttons.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class EventController implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == fontSizeUpBtn)
				FontHeaderBarExtension.this.changeFontSize(2.0f);
			else if(e.getSource() == fontSizeDownBtn)
				FontHeaderBarExtension.this.changeFontSize(-2.0f);
			else if(e.getSource() == fontSizeNormalBtn)
				FontHeaderBarExtension.this.changeFontSize(0.0f);
		}
		
	}

}
