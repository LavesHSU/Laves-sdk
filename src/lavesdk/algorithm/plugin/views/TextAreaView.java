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

package lavesdk.algorithm.plugin.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import lavesdk.configuration.Configuration;
import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.language.LanguageFile;
import lavesdk.resources.Resources;

/**
 * Displays a text area view.
 * <br><br>
 * You can set the text by calling {@link #setText(String)}.
 * <br><br>
 * <b>Language dependent labels, tooltips and messages</b>:<br>
 * Use {@link #TextAreaView(String, boolean, LanguageFile, String)} to specify a language file from which
 * labels, tooltips and messages are read by the given language id. The following language labels are available:
 * <ul>
 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
 * 		<li><i>TEXTVIEW_FONTSIZE_UP_BTN_TOOLTIP</i>: the tooltip text of the font size up button in the header bar</li>
 * 		<li><i>TEXTVIEW_FONTSIZE_DOWN_BTN_TOOLTIP</i>: the tooltip text of the font size down button in the header bar</li>
 * 		<li><i>TEXTVIEW_FONTSIZE_NORMAL_BTN_TOOLTIP</i>: the tooltip text of the normal font size button in the header bar</li>
 * </ul>
 * <b>>> Prepared language file</b>:<br>
 * Within the LAVESDK there is a prepared language file that contains all labels for visual components inside the SDK. You
 * can find the file under lavesdk/resources/files/language.txt or use {@link Resources#LANGUAGE_FILE}.
 * <br><br>
 * <b>Save and load the configuration</b>:<br>
 * You can save and load a configuration of the text area view by using {@link #saveConfiguration(Configuration)} and {@link #loadConfiguration(Configuration)}.
 * It is saved or restored the visibility of the view and the font size of the view. This makes it possible that you can store the state of
 * the view persistent.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class TextAreaView extends View {
	
	private static final long serialVersionUID = 1L;
	
	/** the text area of the view */
	private final JTextArea textArea;
	/** the default font size of the view */
	private final float defFontSize;
	
	/**
	 * Creates a new text area view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public TextAreaView(String title) throws IllegalArgumentException {
		this(title, true, null, null);
	}
	
	/**
	 * Creates a new text area view.
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param closable <code>true</code> if the text area view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a text area view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public TextAreaView(String title, boolean closable) throws IllegalArgumentException {
		this(title, closable, null, null);
	}

	/**
	 * Creates a new text area view.
	 * <br><br>
	 * <b>Language</b>:<br>
	 * You can specify a {@link LanguageFile} and a language id to display language dependent
	 * messages and tooltips in the text area view. The following language labels are available:
	 * <ul>
	 * 		<li><i>VIEW_CLOSE_TOOLTIP</i>: the tooltip text of the close button in the header bar of the view</li>
	 * 		<li><i>TEXTVIEW_FONTSIZE_UP_BTN_TOOLTIP</i>: the tooltip text of the font size up button in the header bar</li>
	 * 		<li><i>TEXTVIEW_FONTSIZE_DOWN_BTN_TOOLTIP</i>: the tooltip text of the font size down button in the header bar</li>
	 * 		<li><i>TEXTVIEW_FONTSIZE_NORMAL_BTN_TOOLTIP</i>: the tooltip text of the normal font size button in the header bar</li>
	 * </ul>
	 * 
	 * @param title the title of the view which is displayed in the header bar
	 * @param closable <code>true</code> if the text area view can be closed by the user using the "X" in the header bar otherwise <code>false</code> (if a text area view is not closable he must be set invisible manually using {@link #setVisible(boolean)} if required)
	 * @param langFile the language file or <code>null</code> if the text area view should not use language dependent labels, tooltips or messages (in this case the predefined labels, tooltips and messages are shown)
	 * @param langID the language id
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if title is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public TextAreaView(String title, boolean closable, LanguageFile langFile, String langID) throws IllegalArgumentException {
		super(title, closable, langFile, langID);

		textArea = new JTextArea();
		textArea.setFont(UIManager.getFont("TextField.font"));
		textArea.setEditable(false);
		defFontSize = textArea.getFont().getSize();
		
		// extend the header bar with the functionality of increase/decrease/normalize the font size
		new FontHeaderBarExtension(this, defFontSize, closable, langFile, langID).apply();
		
		content.setLayout(new BorderLayout());
		content.add(new JScrollPane(textArea), BorderLayout.CENTER);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getAutoRepaint() {
		return super.getAutoRepaint();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAutoRepaint(boolean autoRepaint) {
		super.setAutoRepaint(autoRepaint);
	}

	/**
	 * Resets the text area view meaning that the text is set to an empty string.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @since 1.0
	 */
	@Override
	public void reset() {
		if(EDT.isExecutedInEDT())
			textArea.setText("");
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".reset") {
				@Override
				protected void execute() throws Throwable {
					textArea.setText("");
				}
			});
	}
	
	/**
	 * Indicates whether the text area view is editable.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return <code>true</code> if the text area is editable otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean isEditable() {
		if(EDT.isExecutedInEDT())
			return textArea.isEditable();
		else
			return EDT.execute(new GuiRequest<Boolean>() {
				@Override
				protected Boolean execute() throws Throwable {
					return textArea.isEditable();
				}
			});
	}
	
	/**
	 * Sets whether the text area view is editable.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param editable <code>true</code> if the text area should be editable otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setEditable(final boolean editable) {
		if(EDT.isExecutedInEDT())
			textArea.setEditable(editable);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setEditable") {
				@Override
				protected void execute() throws Throwable {
					textArea.setEditable(editable);
				}
			});
	}
	
	/**
	 * Gets the text of the view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the text
	 * @since 1.0
	 */
	public String getText() {
		if(EDT.isExecutedInEDT())
			return textArea.getText();
		else
			return EDT.execute(new GuiRequest<String>() {
				@Override
				protected String execute() throws Throwable {
					return textArea.getText();
				}
			});
	}
	
	/**
	 * Sets the text of the view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param text the text
	 * @since 1.0
	 */
	public void setText(final String text) {
		if(EDT.isExecutedInEDT())
			textArea.setText(text);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setText") {
				
				@Override
				protected void execute() throws Throwable {
					textArea.setText(text);
				}
			});
	}
	
	/**
	 * Gets the font of the text area view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @return the font
	 * @since 1.0
	 */
	public Font getFont() {
		if(EDT.isExecutedInEDT())
			return textArea.getFont();
		else
			return EDT.execute(new GuiRequest<Font>() {
				@Override
				protected Font execute() throws Throwable {
					return textArea.getFont();
				}
			});
	}
	
	/**
	 * Sets the font of the text area in the view.
	 * <br><br>
	 * <b>This method is thread-safe!</b>
	 * 
	 * @param font the font
	 * @since 1.0
	 */
	@Override
	public void setFont(final Font font) {
		if(EDT.isExecutedInEDT())
			textArea.setFont(font);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setFont") {
				@Override
				protected void execute() throws Throwable {
					textArea.setFont(font);
				}
			});
	}
	
	/**
	 * Sets the background of the text area in the view.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param color the background color
	 * @since 1.0
	 */
	@Override
	public void setBackground(final Color color) {
		if(EDT.isExecutedInEDT())
			textArea.setBackground(color);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setBackground") {
				@Override
				protected void execute() throws Throwable {
					textArea.setBackground(color);
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * Sets the foreground of the text area in the view.
	 * <br><br>
	 * <b>This method is thread-safe!</b><br>
	 * <b>This method is auto repaintable (see {@link #setAutoRepaint(boolean)})!</b>
	 * 
	 * @param color the foreground color
	 * @since 1.0
	 */
	@Override
	public void setForeground(final Color color) {
		if(EDT.isExecutedInEDT())
			textArea.setForeground(color);
		else
			EDT.execute(new GuiJob(getClass().getSimpleName() + ".setForeground") {
				@Override
				protected void execute() throws Throwable {
					textArea.setForeground(color);
				}
			});
		
		autoRepaint();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void autoRepaint() {
		if(getAutoRepaint())
			textArea.repaint();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Furthermore it is read the font size (key "fontSize") of the text area view.
	 * 
	 * @since 1.0
	 */
	@Override
	protected void readConfigurationData(Configuration cd) {
		super.readConfigurationData(cd);
		
		setFont(getFont().deriveFont(cd.getFloat("fontSize", defFontSize)));
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * Furthermore it is written the font size (key "fontSize") of the text area view.
	 * 
	 * @since 1.0
	 */
	@Override
	protected void writeConfigurationData(Configuration cd) {
		super.writeConfigurationData(cd);
		
		cd.addFloat("fontSize", getFont().getSize2D());
	}

}
