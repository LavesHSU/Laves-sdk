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

package lavesdk.resources;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.swing.ImageIcon;

import lavesdk.language.LanguageFile;

/**
 * This class is responsible for accessing resource files of the LAVESDK.
 * <br><br>
 * Use {@link #getInstance()} to get an instance of the resource manager. After that you can access the resource data directly
 * by using the specific resource like {@link #SUCCEEDED_ICON}, {@link #LANGUAGE_FILE}, and so on.
 * 
 * @author jdornseifer
 * @version 1.2
 * @since 1.0
 */
public class Resources {
	
	/** the resource manager instance */
	private static Resources instance;
	
	/** URL to the succeeded icon */
	public final String SUCCEEDED_ICON_PATH = "icons/succeeded.png";
	/** URL to the failed icon */
	public final String FAILED_ICON_PATH = "icons/failed.png";
	/** URL to the failed hint icon */
	public final String FAILED_HINT_ICON_PATH = "icons/failed_hint.png";
	/** URL to the sufficient icon */
	public final String SUFFICIENT_ICON_PATH = "icons/sufficient.png";
	/** path to the close icon */
	public final String CLOSE_ICON_PATH = "icons/close.png";
	/** path to the close hover icon */
	public final String CLOSE_HOVER_ICON_PATH = "icons/close_hover.png";
	/** path to the vertex add icon */
	public final String VERTEX_ADD_ICON_PATH = "icons/vertex_add.png";
	/** path to the edge icon */
	public final String EDGE_ADD_ICON_PATH = "icons/edge_add.png";
	/** path to the delete icon */
	public final String DELETE_ICON_PATH = "icons/delete.png";
	/** path to the mouse cursor icon */
	public final String MOUSECURSOR_ICON_PATH = "icons/cursor.png";
	/** path to the zoom in icon */
	public final String ZOOM_IN_ICON_PATH = "icons/zoom_in.png";
	/** path to the zoom out icon */
	public final String ZOOM_OUT_ICON_PATH = "icons/zoom_out.png";
	/** path to the properties icon */
	public final String PROPERTIES_ICON_PATH = "icons/properties.png";
	/** path to the bipartite graph in icon */
	public final String BIPARTITE_GRAPH_ICON_PATH = "icons/bipartite_graph.png";
	/** path to the complete graph in icon */
	public final String COMPLETE_GRAPH_ICON_PATH = "icons/complete_graph.png";
	/** path to the complete graph in icon */
	public final String CREATE_COMPLETE_GRAPH_ICON_PATH = "icons/create_complete_graph.png";
	/** path to the create complete bipartite graph icon */
	public final String COMPLETE_BIPARTITE_GRAPH_ICON_PATH = "icons/complete_bipartite_graph.png";
	/** path to the create complete bipartite graph icon */
	public final String CREATE_COMPLETE_BIPARTITE_GRAPH_ICON_PATH = "icons/create_complete_bipartite_graph.png";
	/** path to the circle layout icon */
	public final String CIRCLE_LAYOUT_ICON_PATH = "icons/circle_layout.png";
	/** path to the partition layout icon */
	public final String BIPARTITE_LAYOUT_ICON_PATH = "icons/bipartite_layout.png";
	/** path to the matrix to graph icon */
	public final String MATRIX_TO_GRAPH_ICON_PATH = "icons/matrix_to_graph.png";
	/** path to the breakpoint icon */
	public final String BREAKPOINT_ICON_PATH = "icons/breakpoint.png";
	/** path to the active breakpoint icon */
	public final String BREAKPOINT_ACTIVE_ICON_PATH = "icons/breakpoint_active.png";
	/** path to the skip breakpoints icon */
	public final String SKIP_BREAKPOINTS_ICON_PATH = "icons/skip_breakpoints.png";
	/** path to the start icon */
	public final String START_ICON_PATH = "icons/start.png";
	/** path to the start finish icon */
	public final String START_FINISH_ICON_PATH = "icons/start_finish.png";
	/** path to the play pause icon */
	public final String PLAY_PAUSE_ICON_PATH = "icons/play_pause.png";
	/** path to the stop icon */
	public final String STOP_ICON_PATH = "icons/stop.png";
	/** path to the pause icon */
	public final String PAUSE_ICON_PATH = "icons/pause.png";
	/** path to the next step icon */
	public final String NEXTSTEP_ICON_PATH = "icons/next_step.png";
	/** path to the previous step icon */
	public final String PREVSTEP_ICON_PATH = "icons/prev_step.png";
	/** path to the new algorithm icon */
	public final String NEW_ICON_PATH = "icons/new.png";
	/** path to the save icon */
	public final String SAVE_ICON_PATH = "icons/save.png";
	/** path to the open icon */
	public final String OPEN_ICON_PATH = "icons/open.png";
	/** path to the slower icon */
	public final String SLOWER_ICON_PATH = "icons/slower.png";
	/** path to the faster icon */
	public final String FASTER_ICON_PATH = "icons/faster.png";
	/** path to the reset execution speed factor icon */
	public final String RESET_EXECSPEED_ICON_PATH = "icons/reset_execspeed.png";
	/** path to the font size up icon */
	public final String FONTSIZE_UP_ICON_PATH = "icons/fontsize_up.png";
	/** path to the font size down icon */
	public final String FONTSIZE_DOWN_ICON_PATH = "icons/fontsize_down.png";
	/** path to the normal font size icon */
	public final String FONTSIZE_NORMAL_ICON_PATH = "icons/fontsize_normal.png";
	/** path to the collapse icon */
	public final String COLLAPSE_ICON_PATH = "icons/collapse.png";
	/** path to the expand icon */
	public final String EXPAND_ICON_PATH = "icons/expand.png";
	/** path to the pause before stop icon */
	public final String PAUSE_BEFORE_STOP_ICON_PATH = "icons/pause_before_stop.png";
	/** path to the dropdown arrow icon */
	public final String DROPDOWN_ARROW_ICON_PATH = "icons/dropdown_arrow.png";
	/** path to the exercise solve icon */
	public final String EXERCISE_SOLVE_ICON_PATH = "icons/exercise_solve.png";
	/** path to the exercise give up icon */
	public final String EXERCISE_GIVEUP_ICON_PATH = "icons/exercise_giveup.png";
	/** path to the exercise input hint icon */
	public final String EXERCISE_INPUTHINT_ICON_PATH = "icons/exercise_inputhint.png";
	/** path to the exercise mode icon */
	public final String EXERCISE_MODE_ICON_PATH = "icons/exercise_mode.png";
	/** path to the question icon */
	public final String QUESTION_ICON_PATH = "icons/question.png";
	/** path to the refresh icon */
	public final String REFRESH_ICON_PATH = "icons/refresh.png";
	/** path to the instructions icon */
	public final String INSTRUCTIONS_ICON_PATH = "icons/instructions.png";
	/** path to the sort up icon */
	public final String SORT_UP_ICON_PATH = "icons/sort_up.png";
	/** path to the sort down icon */
	public final String SORT_DOWN_ICON_PATH = "icons/sort_down.png";
	/** path to the annotation icon */
	public final String ANNOTATION_ICON_PATH = "icons/annotation.png";
	/** path to the random graph icon */
	public final String RANDOM_GRAPH_ICON_PATH = "icons/random_graph.png";
	/** path to the language file */
	public final String LANGUAGE_FILE_PATH = "files/language.txt";
	/** path to the properties file of the LAVESDK */
	public final String LAVESDK_PROPERTIES_PATH = "files/lavesdk.properties";

	/** the succeeded icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon SUCCEEDED_ICON;
	/** the failed icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon FAILED_ICON;
	/** the failed hint icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon FAILED_HINT_ICON;
	/** the sufficient icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon SUFFICIENT_ICON;
	/** the close icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon CLOSE_ICON;
	/** the close hover icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon CLOSE_HOVER_ICON;
	/** the vertex add icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon VERTEX_ADD_ICON;
	/** the edge add icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon EDGE_ADD_ICON;
	/** the delete icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon DELETE_ICON;
	/** the mouse cursor icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon MOUSECURSOR_ICON;
	/** the zoom in icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon ZOOM_IN_ICON;
	/** the zoom out icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon ZOOM_OUT_ICON;
	/** the properties icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon PROPERTIES_ICON;
	/** the bipartite graph icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon BIPARTITE_GRAPH_ICON;
	/** the complete graph icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon COMPLETE_GRAPH_ICON;
	/** the create complete graph icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon CREATE_COMPLETE_GRAPH_ICON;
	/** the complete bipartite graph icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon COMPLETE_BIPARTITE_GRAPH_ICON;
	/** the create complete bipartite graph icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon CREATE_COMPLETE_BIPARTITE_GRAPH_ICON;
	/** the circle layout icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon CIRCLE_LAYOUT_ICON;
	/** the partition layout icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon BIPARTITE_LAYOUT_ICON;
	/** the matrix to graph icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon MATRIX_TO_GRAPH_ICON;
	/** the breakpoint icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon BREAKPOINT_ICON;
	/** the active breakpoint icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon BREAKPOINT_ACTIVE_ICON;
	/** the skip breakpoints icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon SKIP_BREAKPOINTS_ICON;
	/** the start icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon START_ICON;
	/** the start finish icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon START_FINISH_ICON;
	/** the play pause icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon PLAY_PAUSE_ICON;
	/** the stop icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon STOP_ICON;
	/** the pause icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon PAUSE_ICON;
	/** the next step icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon NEXTSTEP_ICON;
	/** the previous step icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon PREVSTEP_ICON;
	/** the new algorithm icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon NEW_ICON;
	/** the save icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon SAVE_ICON;
	/** the open icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon OPEN_ICON;
	/** the slower icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon SLOWER_ICON;
	/** the faster icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon FASTER_ICON;
	/** the reset execution speed factor icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon RESET_EXECSPEED_ICON;
	/** the font size up icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon FONTSIZE_UP_ICON;
	/** the font size down icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon FONTSIZE_DOWN_ICON;
	/** the normal font size icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon FONTSIZE_NORMAL_ICON;
	/** the collapse icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon COLLAPSE_ICON;
	/** the expand icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon EXPAND_ICON;
	/** the pause before stop icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon PAUSE_BEFORE_STOP_ICON;
	/** the drop down arrow icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon DROPDOWN_ARROW_ICON;
	/** the exercise solve icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon EXERCISE_SOLVE_ICON;
	/** the exercise give up icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon EXERCISE_GIVEUP_ICON;
	/** the exercise input hint icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon EXERCISE_INPUTHINT_ICON;
	/** the exercise mode icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon EXERCISE_MODE_ICON;
	/** the question icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon QUESTION_ICON;
	/** the refresh icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon REFRESH_ICON;
	/** the instructions icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon INSTRUCTIONS_ICON;
	/** the sort up icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon SORT_UP_ICON;
	/** the sort down icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon SORT_DOWN_ICON;
	/** the annotation icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon ANNOTATION_ICON;
	/** the random graph icon or <code>null</code> if the resource could not be loaded */
	public final ImageIcon RANDOM_GRAPH_ICON;
	/** an empty image */
	public final Image EMPTY_IMAGE;
	
	/** the language file or <code>null</code> if the resource could not be loaded */
	public final LanguageFile LANGUAGE_FILE;
	/** the properties file of the LAVESDK or <code>null</code> if the resource could not be loaded */
	public final Properties LAVESDK_PROPERTIES;
	
	/**
	 * Creates the resources.
	 * 
	 * @since 1.0
	 */
	private Resources() {
		SUCCEEDED_ICON = getImageIconFromURL(getURL(SUCCEEDED_ICON_PATH));
		FAILED_ICON = getImageIconFromURL(getURL(FAILED_ICON_PATH));
		FAILED_HINT_ICON = getImageIconFromURL(getURL(FAILED_HINT_ICON_PATH));
		SUFFICIENT_ICON = getImageIconFromURL(getURL(SUFFICIENT_ICON_PATH));
		CLOSE_ICON = getImageIconFromURL(getURL(CLOSE_ICON_PATH));
		CLOSE_HOVER_ICON = getImageIconFromURL(getURL(CLOSE_HOVER_ICON_PATH));
		VERTEX_ADD_ICON = getImageIconFromURL(getURL(VERTEX_ADD_ICON_PATH));
		EDGE_ADD_ICON = getImageIconFromURL(getURL(EDGE_ADD_ICON_PATH));
		DELETE_ICON = getImageIconFromURL(getURL(DELETE_ICON_PATH));
		MOUSECURSOR_ICON = getImageIconFromURL(getURL(MOUSECURSOR_ICON_PATH));
		ZOOM_IN_ICON = getImageIconFromURL(getURL(ZOOM_IN_ICON_PATH));
		ZOOM_OUT_ICON = getImageIconFromURL(getURL(ZOOM_OUT_ICON_PATH));
		PROPERTIES_ICON = getImageIconFromURL(getURL(PROPERTIES_ICON_PATH));
		BIPARTITE_GRAPH_ICON = getImageIconFromURL(getURL(BIPARTITE_GRAPH_ICON_PATH));
		COMPLETE_GRAPH_ICON = getImageIconFromURL(getURL(COMPLETE_GRAPH_ICON_PATH));
		CREATE_COMPLETE_GRAPH_ICON = getImageIconFromURL(getURL(CREATE_COMPLETE_GRAPH_ICON_PATH));
		COMPLETE_BIPARTITE_GRAPH_ICON = getImageIconFromURL(getURL(COMPLETE_BIPARTITE_GRAPH_ICON_PATH));
		CREATE_COMPLETE_BIPARTITE_GRAPH_ICON = getImageIconFromURL(getURL(CREATE_COMPLETE_BIPARTITE_GRAPH_ICON_PATH));
		CIRCLE_LAYOUT_ICON = getImageIconFromURL(getURL(CIRCLE_LAYOUT_ICON_PATH));
		BIPARTITE_LAYOUT_ICON = getImageIconFromURL(getURL(BIPARTITE_LAYOUT_ICON_PATH));
		MATRIX_TO_GRAPH_ICON = getImageIconFromURL(getURL(MATRIX_TO_GRAPH_ICON_PATH));
		BREAKPOINT_ICON = getImageIconFromURL(getURL(BREAKPOINT_ICON_PATH));
		BREAKPOINT_ACTIVE_ICON = getImageIconFromURL(getURL(BREAKPOINT_ACTIVE_ICON_PATH));
		SKIP_BREAKPOINTS_ICON = getImageIconFromURL(getURL(SKIP_BREAKPOINTS_ICON_PATH));
		START_ICON = getImageIconFromURL(getURL(START_ICON_PATH));
		START_FINISH_ICON = getImageIconFromURL(getURL(START_FINISH_ICON_PATH));
		PLAY_PAUSE_ICON = getImageIconFromURL(getURL(PLAY_PAUSE_ICON_PATH));
		STOP_ICON = getImageIconFromURL(getURL(STOP_ICON_PATH));
		PAUSE_ICON = getImageIconFromURL(getURL(PAUSE_ICON_PATH));
		NEXTSTEP_ICON = getImageIconFromURL(getURL(NEXTSTEP_ICON_PATH));
		PREVSTEP_ICON = getImageIconFromURL(getURL(PREVSTEP_ICON_PATH));
		NEW_ICON = getImageIconFromURL(getURL(NEW_ICON_PATH));
		SAVE_ICON = getImageIconFromURL(getURL(SAVE_ICON_PATH));
		OPEN_ICON = getImageIconFromURL(getURL(OPEN_ICON_PATH));
		SLOWER_ICON = getImageIconFromURL(getURL(SLOWER_ICON_PATH));
		FASTER_ICON = getImageIconFromURL(getURL(FASTER_ICON_PATH));
		RESET_EXECSPEED_ICON = getImageIconFromURL(getURL(RESET_EXECSPEED_ICON_PATH));
		FONTSIZE_UP_ICON = getImageIconFromURL(getURL(FONTSIZE_UP_ICON_PATH));
		FONTSIZE_DOWN_ICON = getImageIconFromURL(getURL(FONTSIZE_DOWN_ICON_PATH));
		FONTSIZE_NORMAL_ICON = getImageIconFromURL(getURL(FONTSIZE_NORMAL_ICON_PATH));
		COLLAPSE_ICON = getImageIconFromURL(getURL(COLLAPSE_ICON_PATH));
		EXPAND_ICON = getImageIconFromURL(getURL(EXPAND_ICON_PATH));
		PAUSE_BEFORE_STOP_ICON = getImageIconFromURL(getURL(PAUSE_BEFORE_STOP_ICON_PATH));
		DROPDOWN_ARROW_ICON = getImageIconFromURL(getURL(DROPDOWN_ARROW_ICON_PATH));
		EXERCISE_SOLVE_ICON = getImageIconFromURL(getURL(EXERCISE_SOLVE_ICON_PATH));
		EXERCISE_GIVEUP_ICON = getImageIconFromURL(getURL(EXERCISE_GIVEUP_ICON_PATH));
		EXERCISE_INPUTHINT_ICON = getImageIconFromURL(getURL(EXERCISE_INPUTHINT_ICON_PATH));
		EXERCISE_MODE_ICON = getImageIconFromURL(getURL(EXERCISE_MODE_ICON_PATH));
		QUESTION_ICON = getImageIconFromURL(getURL(QUESTION_ICON_PATH));
		REFRESH_ICON = getImageIconFromURL(getURL(REFRESH_ICON_PATH));
		INSTRUCTIONS_ICON = getImageIconFromURL(getURL(INSTRUCTIONS_ICON_PATH));
		SORT_UP_ICON = getImageIconFromURL(getURL(SORT_UP_ICON_PATH));
		SORT_DOWN_ICON = getImageIconFromURL(getURL(SORT_DOWN_ICON_PATH));
		ANNOTATION_ICON = getImageIconFromURL(getURL(ANNOTATION_ICON_PATH));
		RANDOM_GRAPH_ICON = getImageIconFromURL(getURL(RANDOM_GRAPH_ICON_PATH));
		EMPTY_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		LANGUAGE_FILE = getLanguageFileFromStream(getStream(LANGUAGE_FILE_PATH));
		LAVESDK_PROPERTIES = getPropertiesFileFromStream(getStream(LAVESDK_PROPERTIES_PATH));
	}
	
	/**
	 * Gets the instance of the resource manager.
	 * 
	 * @return the instance
	 * @since 1.0
	 */
	public static Resources getInstance() {
		if(instance == null)
			instance = new Resources();
		
		return instance;
	}
	
	/**
	 * Gets the resource url of a given resource path.
	 * 
	 * @param path the path like {@link #SUCCEEDED_ICON_PATH}, {@link #LANGUAGE_FILE_PATH}, ...
	 * @return the url
	 * @since 1.0
	 */
	public URL getURL(final String path) {
		return Resources.class.getResource(path);
	}
	
	/**
	 * Gets the resource stream of a given resource path.
	 * 
	 * @param path the path like {@link #SUCCEEDED_ICON_PATH}, {@link #LANGUAGE_FILE_PATH}, ...
	 * @return the stream
	 * @since 1.0
	 */
	public InputStream getStream(final String path) {
		return Resources.class.getResourceAsStream(path);
	}
	
	/**
	 * Gets the icon of the specified url.
	 * 
	 * @param url the url
	 * @return the icon or <code>null</code> if the icon could not be loaded from the url
	 * @since 1.0
	 */
	private ImageIcon getImageIconFromURL(final URL url) {
		try {
			return new ImageIcon(url);
		}
		catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * Gets the language file of the specified stream.
	 * 
	 * @param stream the stream
	 * @return the language file or <code>null</code> if the language file could not be loaded from the stream
	 * @since 1.0
	 */
	private LanguageFile getLanguageFileFromStream(final InputStream stream) {
		try {
			return new LanguageFile(stream);
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Gets the properties file of the specified stream.
	 * 
	 * @param stream the stream
	 * @return the properties file or an empty properties file if it could not be read
	 * @since 1.0
	 */
	private Properties getPropertiesFileFromStream(final InputStream stream) {
		final Properties props = new Properties();
		try {
			props.load(stream);
		} catch (IOException e) {
		}
		
		return props;
	}

}
