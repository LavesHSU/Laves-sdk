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

package lavesdk.algorithm.plugin;

import java.io.File;

import javax.swing.filechooser.FileNameExtensionFilter;

import lavesdk.LAVESDKV;
import lavesdk.algorithm.AlgorithmExercise;
import lavesdk.algorithm.RTEListener;
import lavesdk.algorithm.AlgorithmRTE;
import lavesdk.algorithm.plugin.extensions.BipartiteGraphToolBarExtension;
import lavesdk.algorithm.plugin.extensions.CompleteBipartiteGraphToolBarExtension;
import lavesdk.algorithm.plugin.extensions.CompleteGraphToolBarExtension;
import lavesdk.algorithm.plugin.extensions.CircleLayoutToolBarExtension;
import lavesdk.algorithm.plugin.extensions.ToolBarExtension;
import lavesdk.algorithm.plugin.views.AlgorithmTextView;
import lavesdk.algorithm.plugin.views.ExecutionTableView;
import lavesdk.algorithm.plugin.views.GraphView;
import lavesdk.algorithm.plugin.views.LegendView;
import lavesdk.algorithm.plugin.views.MatrixView;
import lavesdk.algorithm.plugin.views.View;
import lavesdk.algorithm.plugin.views.ViewContainer;
import lavesdk.algorithm.plugin.views.ViewGroup;
import lavesdk.algorithm.text.AlgorithmStep;
import lavesdk.algorithm.text.AlgorithmText;
import lavesdk.configuration.Configuration;
import lavesdk.gui.widgets.PropertiesListModel;
import lavesdk.gui.widgets.Property;
import lavesdk.language.LanguageFile;
import lavesdk.sandbox.Sandbox;

/**
 * Represents a plugin in a {@link PluginHost}.
 * <br><br>
 * <b>Information</b>:<br>
 * To extend the host application with new features (that means algorithm visualizations) every plugin
 * has to implement this interface.<br><br>
 * The execution of your algorithm has to be implemented in a separate class by inherit from {@link AlgorithmRTE}.
 * <br><br>
 * <b>Goal</b>:<br>
 * The goal of an algorithm plugin is not to build an efficient implementation of the algorithm but to visualize and animate the algorithm
 * in a way the user can understand the algorithm more easily.
 * <br><br>
 * <b>Create a new plugin project</b>:<br>
 * Open your development environment and create a new project. Copy the LAVESDK JAR (lavesdk-x.x.jar) to your project folder
 * (<b>not in a subfolder</b>, otherwise it could be integrated in your plugin JAR, see "Release your plugin"). After that you
 * have to add the JAR to your build path (in Eclipse you do this by right-clicking onto the jar file in your project root and go to
 * <code>Build Path -> Add to Build Path</code>). Finally create your plugin class and implement {@link AlgorithmPlugin}.<br>
 * <b>>> Update the text file encoding (important)</b>:<br>
 * If you use text file resources like {@link LanguageFile}s it is recommended to update the text file encoding parameter of your
 * plugin project. In Eclipse you have to go to <code>Project -> Properties -> Resources</code> enable <code>Other</code> and select
 * the corresponding encoding that is used in your text files. {@link LanguageFile} uses <code>UTF-8</code> by default but it is also
 * possible to specify another charset.<br>
 * If your language labels are not displayed correctly it might be a reason that you does not set the right charset in {@link LanguageFile}
 * or in your project properties!
 * <br><br>
 * <b>Release your plugin</b>:<br>
 * To release your algorithm plugin you have to export your algorithm plugin project to a <b>JAR file</b>
 * (not a runnable JAR!).<br>
 * You should ensure that you exclude the LAVESDK JAR from exporting it into your plugin JAR because this brings several advantages:
 * <ol>
 * 		<li>the plugin has a much smaller (file) size</li>
 * 		<li>the plugin can be loaded much faster by the host system</li>
 * 		<li>the plugin must not be updated due to bugs or new version releases of the LAVESSDK (because the LAVESDK is
 *          provided by the host system which means that only the host system has to be updated but not the plugins)</li>
 * </ol>
 * To export your plugin using eclipse (as the IDE) you go to <code>File -> Export... -> Java > JAR file</code>. If you use external
 * libraries except the lavesdk-x.x.jar you should ensure that you integrate this libraries into the JAR file (e.g. by using a subfolder
 * \lib that contains all the external libraries).
 * <br><br>
 * <b>Test your algorithm plugin</b>:<br>
 * To test your algorithm plugin you can use the build-in {@link Sandbox} which provides a GUI for the basic functionality
 * of the algorithm.
 * <br><br>
 * <b>Events</b>:<br>
 * If a new algorithm visualization is created (meaning that the user activates a plugin in the host application) {@link #onCreate(ViewContainer, PropertiesListModel)}
 * is invoked to load the content of the plugin and initialize a new visualization. By {@link #onClose()} the plugin is notified when
 * the user deactivates the plugin meaning that another plugin is activated.<br>
 * Furthermore the plugin is notified about when the algorithm runtime environment starts, resumes, pauses and stops ({@link #beforeStart(lavesdk.algorithm.RTEvent)}/
 * {@link #beforeResume(lavesdk.algorithm.RTEvent)}/{@link #beforePause(lavesdk.algorithm.RTEvent)}/{@link #onStop()}). By way of example
 * the <i>beforeStart</i>-event could be used to check if a vertex is selected in a {@link GraphView} and if not the start is canceled or something like that.
 * <br><br>
 * <b>Attention</b>:<br>
 * This interface may only be implemented by classes of external software (that means plugins) and may not be implemented by
 * classes of the LAVESDK!
 * 
 * @see AlgorithmRTE
 * @see Sandbox
 * @see Validator
 * @author jdornseifer
 * @version 1.1
 * @since 1.0
 */
public interface AlgorithmPlugin extends RTEListener {
	
	/**
	 * Initializes the plugin and is called once before the plugin is used.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The constructor of a concrete plugin class is never invoked when it is loaded, that means all initialization work
	 * must be done in this method!
	 * 
	 * @param host {@link PluginHost}, this is the interface to the host application and gives access to communication
	 * @param resLoader the resource loader of the plugin that must be used to load resource files from within the plugin JAR
	 * @param config the configuration of the plugin or <code>null</code> if there is no configuration available for the plugin
	 * @since 1.0
	 */
	public void initialize(final PluginHost host, final ResourceLoader resLoader, final Configuration config);
	
	/**
	 * Gets the name of the algorithm or method.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use a {@link LanguageFile} and {@link PluginHost#getLanguageID()} to return a language dependent
	 * name.<br>
	 * <u>Example</u>:
	 * <pre>
	 * final LanguageFile myLangFile = new LanguageFile("langFile.txt");
	 * ...
	 * public String getName() {
	 *     return LanguageFile.getLabel(myLangFile, "ALGORITHM_NAME", host.getLanguageID(), "My Algorithm Name");
	 * }
	 * ...
	 * </pre>
	 * 
	 * @return the name of the algorithm/method
	 * @since 1.0
	 */
	public String getName();
	
	/**
	 * Gets the description of the algorithm or method.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use a {@link LanguageFile} and {@link PluginHost#getLanguageID()} to return a language dependent
	 * description.<br>
	 * <u>Example</u>:
	 * <pre>
	 * final LanguageFile myLangFile = new LanguageFile("langFile.txt");
	 * ...
	 * public String getDescription() {
	 *     return LanguageFile.getLabel(myLangFile, "ALGORITHM_DESC", host.getLanguageID(), "My Algorithm Description");
	 * }
	 * ...
	 * </pre>
	 * 
	 * @return the description of the algorithm/method plugin (<b>can contain html tags to format the text</b>) or an empty string if the algorithm/method plugin should not have a description
	 * @since 1.0
	 */
	public String getDescription();
	
	/**
	 * Gets the type of the algorithm or method meaning for example whether the algorithm is exact, a heuristic or something like that.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use a {@link LanguageFile} and {@link PluginHost#getLanguageID()} to return a language dependent
	 * algorithm type.<br>
	 * <u>Example</u>:
	 * <pre>
	 * final LanguageFile myLangFile = new LanguageFile("langFile.txt");
	 * ...
	 * public String getType() {
	 *     return LanguageFile.getLabel(myLangFile, "ALGORITHM_TYPE", host.getLanguageID(), "Heuristic");
	 * }
	 * ...
	 * </pre>
	 * 
	 * @return the type of the algorithm/method
	 * @since 1.0
	 */
	public String getType();
	
	/**
	 * Gets the author of the plugin.
	 * 
	 * @return the author of the plugin
	 * @since 1.0
	 */
	public String getAuthor();
	
	/**
	 * Gets the author contact details of the plugin.
	 * 
	 * @return the author contact details
	 * @since 1.1
	 */
	public String getAuthorContact();
	
	/**
	 * Gets the assumptions of the algorithm or method.
	 * <br><br>
	 * <u>Example</u>: A simple, non-negative weighted, connected graph.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use a {@link LanguageFile} and {@link PluginHost#getLanguageID()} to return a language dependent
	 * assumption.<br>
	 * <u>Example</u>:
	 * <pre>
	 * final LanguageFile myLangFile = new LanguageFile("langFile.txt");<br>
	 * ...
	 * public String getAssumptions() {
	 *     return LanguageFile.getLabel(myLangFile, "ASSUMPTIONS", host.getLanguageID(), "A simple, non-negative weighted, connected graph.");
	 * }
	 * ...
	 * </pre>
	 * 
	 * @return the assumptions of the plugin (<b>can contain html tags to format the text</b>) or an empty string if the plugin should not have assumptions
	 * @since 1.0
	 */
	public String getAssumptions();
	
	/**
	 * Gets the affiliation of the problem the plugin is intended to solve or the algorithm relates to.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use a {@link LanguageFile} and {@link PluginHost#getLanguageID()} to return a language dependent
	 * problem affiliation.<br>
	 * <u>Example</u>:
	 * <pre>
	 * final LanguageFile myLangFile = new LanguageFile("langFile.txt");<br>
	 * ...
	 * public String getProblemAffiliation() {
	 *     return LanguageFile.getLabel(myLangFile, "PROBLEMAFFILIATION", host.getLanguageID(), "Shortest path problem");
	 * }
	 * ...
	 * </pre>
	 * 
	 * @return the affiliation of the problem the algorithm solves like Shortest path problem, Chinese postman problem, ...
	 * @since 1.0
	 */
	public String getProblemAffiliation();
	
	/**
	 * Gets the subject in which the algorithm is applied.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use a {@link LanguageFile} and {@link PluginHost#getLanguageID()} to return a language dependent subject.<br>
	 * <u>Example</u>:
	 * <pre>
	 * final LanguageFile myLangFile = new LanguageFile("langFile.txt");
	 * ...
	 * public String getSubject() {
	 *     return LanguageFile.getLabel(myLangFile, "SUBJECT", host.getLanguageID(), "Logistics");
	 * }
	 * ...
	 * </pre>
	 * 
	 * @return the subject or field like logistics, operations research, ...
	 */
	public String getSubject();
	
	/**
	 * Gets the instructions of the plugin.
	 * <br><br>
	 * The instructions can be presented to the user so they could be used to explain the user what to do or something like that.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use a {@link LanguageFile} and {@link PluginHost#getLanguageID()} to return language dependent instructions.<br>
	 * <u>Example</u>:
	 * <pre>
	 * final LanguageFile myLangFile = new LanguageFile("langFile.txt");
	 * ...
	 * public String getInstructions() {
	 *     return LanguageFile.getLabel(myLangFile, "INSTRUCTIONS", host.getLanguageID(), "Do this, do not that!");
	 * }
	 * ...
	 * </pre>
	 * 
	 * @return the instructions of the plugin (<b>can contain html tags to format the text</b>) or an empty string if the plugin should not have instructions
	 * @since 1.0
	 */
	public String getInstructions();
	
	/**
	 * Gets the version of the plugin.
	 * 
	 * @return version of the plugin
	 * @since 1.0
	 */
	public String getVersion();
	
	/**
	 * Gets the version of the LAVESDK that this plugin uses.
	 * <br><br>
	 * This is a safeguard to avoid runtime exceptions because of missing methods, classes or interfaces that the plugin uses.
	 * That means that the plugin is only runnable in host systems with a greater or an equal sdk version.
	 * <br><br>
	 * <b>How to determine the used LAVESDK version</b>:<br>
	 * there are two ways to find out the version of your LAVESDK. The safest way is to display {@link LAVESDKV#CURRENT} like
	 * adding <code>System.out.println("LAVESDKV=" + LAVESDKV.CURRENT);</code> to a runnable snippet of code and execute it to read
	 * the version information from the console.<br>
	 * Furthermore the version of the LAVESDK should always be visible in the name of the LAVESDK JAR (lavesdk-x.x.jar) where "x.x" is the
	 * version information with the major and minor number.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * Keep in mind that whenever you update the plugin by compiling it with another LAVESDK version you have to adjust this version
	 * information!
	 * 
	 * @return the used version of the LAVESDK like <code>new LAVESDKV(1, 0);</code>
	 * @since 1.0
	 */
	public LAVESDKV getUsedSDKVersion();
	
	/**
	 * Gets the runtime environment of the algorithm.
	 * <br><br>
	 * Implement your algorithm by inherit from {@link AlgorithmRTE} and return your implementation here.
	 * 
	 * @return the runtime environment of the algorithm or <code>null</code> if the plugin does not have a runtime environment
	 * @since 1.0
	 */
	public AlgorithmRTE getRuntimeEnvironment();
	
	/**
	 * Gets the text of the algorithm that is represented by this plugin.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * You should return a base copy of your algorithm text by using {@link AlgorithmText#getBaseCopy()} so that the text
	 * may not be changed externally.
	 * 
	 * @return the algorithm text or <code>null</code> if the plugin does not have an algorithm text
	 * @since 1.0.0
	 */
	public AlgorithmText getText();
	
	/**
	 * Indicates whether the plugin has an exercise mode meaning that {@link AlgorithmExercise}s have been assigned to {@link AlgorithmStep}s
	 * of the algorithm.
	 * <br><br>
	 * The exercise mode enables an interactive way to teach users instead of only visualize the algorithm. You can define an {@link AlgorithmExercise}
	 * for each {@link AlgorithmStep} of the algorithm. During the execution the user has to solve these exercises. The exercise mode is fully
	 * automatic so you do not need more to do than defining {@link AlgorithmExercise}s. Additionally the user has not the possibility to pause the
	 * algorithm or to switch to the previous or next step during the exercise because the exercise mode is handled fully automatic by the
	 * runtime environment.
	 * 
	 * @return <code>true</code> if the plugin has an interactive exercise mode otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean hasExerciseMode();
	
	/**
	 * Gets the current configuration of the plugin.
	 * <br><br>
	 * The plugin host manages a configuration file for each plugin. Use the configuration to store persistent
	 * information e.g. about the visual appearance, which view is visible and so on. This enables the user the option to
	 * customize each plugin.
	 * 
	 * @return the configuration of the plugin or <code>null</code> if the plugin does not have a configuration
	 * @since 1.0
	 */
	public Configuration getConfiguration();
	
	/**
	 * Indicates whether the plugin has creator preferences.
	 * <br><br>
	 * The <i>creator preferences</i> are presented to the user when he wants to create a new plugin. In this
	 * preferences you can let the user determine initial properties of the plugin.<br>
	 * By way of example you can give the user the option to decide whether he wants to use the algorithm on a directed
	 * or an undirected graph:
	 * <pre>
	 * ...
	 * public void loadCreatorPreferences(PropertiesListModel plm) {
	 *     final BooleanPropertyGroup bpg = new BooleanPropertyGroup(list);
	 *     plm.add(new BooleanProperty("directed", "Apply the algorithm to a directed graph.", bpg));
	 *     plm.add(new BooleanProperty("undirected", "Apply the algorithm to an undirected graph.", bpg));
	 *     ...
	 * }
	 * ...
	 * </pre>
	 * 
	 * @see #loadCreatorPreferences(PropertiesListModel)
	 * @return <code>true</code> if the plugin has creator preferences otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean hasCreatorPreferences();
	
	/**
	 * Loads the creator preferences of the plugin.
	 * <br><br>
	 * The <i>creator preferences</i> are presented to the user when he wants to create a new plugin. In this
	 * preferences you can let the user determine initial properties of the plugin.<br>
	 * By way of example you can give the user the option to decide whether he wants to use the algorithm on a directed
	 * or an undirected graph:
	 * <pre>
	 * ...
	 * public void loadCreatorPreferences(PropertiesListModel plm) {
	 *     final BooleanPropertyGroup bpg = new BooleanPropertyGroup(list);
	 *     plm.add(new BooleanProperty("directed", "Apply the algorithm to a directed graph.", bpg));
	 *     plm.add(new BooleanProperty("undirected", "Apply the algorithm to an undirected graph.", bpg));
	 *     ...
	 * }
	 * ...
	 * </pre>
	 * 
	 * @param plm the properties list model in which the <i>creator preferences</i> can be loaded
	 * @since 1.0
	 */
	public void loadCreatorPreferences(final PropertiesListModel plm);
	
	/**
	 * Indicates that the plugin is activated in the host application by the user to visualize/create <b>a
	 * new instance</b> of the algorithm. Therefore the views of the plugin have to be loaded into
	 * the host container of the application.
	 * <br><br>
	 * Use {@link ViewGroup}s to group views together. All views of a group are separated by a sash. The sash
	 * can be used by the user to change the sizes of the views in a group.<br>
	 * You can restore the old weight distribution of the views by using {@link ViewGroup#restoreWeights(lavesdk.serialization.Serializer, String, float[])}.
	 * <br><br>
	 * The LAVESDK provides several view implementations like an {@link AlgorithmTextView} to visualize an {@link AlgorithmText}, a {@link GraphView}
	 * to visualize graph data structures, a {@link MatrixView} to visualize matrices and so on. If there is no suitable view you can create your own by extending {@link View}.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This method is called once and that each time when the plugin is enabled because the user wants to
	 * visualize/create a new instance of the algorithm.
	 * 
	 * @see View
	 * @see ViewGroup
	 * @see AlgorithmTextView
	 * @see GraphView
	 * @see ExecutionTableView
	 * @see LegendView
	 * @see #hasCreatorPreferences()
	 * @param container the container in which the views are displayed
	 * @param creatorPreferences the properties list model that contains the <i>creator preferences</i> of the plugin or <code>null</code> if there are currently no creator preferences available (<b>although the plugin has creator preferences</b>)
	 * @since 1.0
	 */
	public void onCreate(final ViewContainer container, final PropertiesListModel creatorPreferences);
	
	/**
	 * Indicates that the plugin is closed/disabled which means that it is no longer active in the host application.
	 * You can use this method to reset all views or to save the current state in the configuration like the visibility of
	 * each view and so on.
	 * 
	 * @since 1.0
	 */
	public void onClose();
	
	/**
	 * Indicates whether the plugin provides customization meaning that the user can customize the plugin.
	 * <br><br>
	 * <b>Plugin-Customization</b>:<br>
	 * The customization of a plugin is controlled by the host application meaning that the user can select the plugin in a customization
	 * dialog and is able to modify properties of the plugin.<br>
	 * The customization/configuration is composed of {@link Property}s where each one of them describes a customizable point.<br>
	 * To store the customization properties persistent you can finally add them to the plugin's {@link Configuration}.<br>
	 * <u>Example</u>:<br>
	 * By way of example you can give the user the possibility to change the highlight color of the {@link AlgorithmTextView} (and so on...):
	 * <pre>
	 * ...
	 * public void loadCustomization(PropertiesListModel plm) {
	 *     // create a property for the highlight foreground color and load the current highlight
	 *     // foreground color from the configuration
	 *     final Property highlightFG = new ColorProperty("highlight foreground",
	 *                                          "changes the highlight foreground color in the algorithm text view",
	 *                                          config.getColor("highlightFG"));
	 *     // add the property to the properties list model
	 *     plm.add(highlightFG);
	 * }
	 * ...
	 * public void applyCustomization(PropertiesListModel plm) {
	 *     // get the highlight foreground color property from the model
	 *     final ColorProperty highlightFG = plm.getColorProperty("highlightFG");
	 *     // apply data to the algorithm text view of the plugin and to the configuration
	 *     if(highlightFG != null) {
	 *         config.addColor("highlightFG", highlightFG.getValue());
	 *         algoTextView.setHighlightForeground(highlightFG.getValue());
	 *     }
	 * }
	 * ...
	 * </pre>
	 * 
	 * @see #getConfiguration()
	 * @return <code>true</code> if the plugin has a customization otherwise <code>false</code> (meaning that the user cannot customize the plugin)
	 * @since 1.0
	 */
	public boolean hasCustomization();
	
	/**
	 * Loads the customization of the plugin. This is only possible if {@link #hasCustomization()} returns <code>true</code>.
	 * <br><br>
	 * <b>Plugin-Customization</b>:<br>
	 * The customization of a plugin is controlled by the host application meaning that the user can select the plugin in a customization
	 * dialog and is able to modify properties of the plugin.<br>
	 * The customization/configuration is composed of {@link Property}s where each one of them describes a customizable point.<br>
	 * To store the customization properties persistent you can finally add them to the plugin's {@link Configuration}.<br>
	 * <u>Example</u>:<br>
	 * By way of example you can give the user the possibility to change the highlight color of the {@link AlgorithmTextView} (and so on...):
	 * <pre>
	 * ...
	 * public void loadCustomization(PropertiesListModel plm) {
	 *     // create a property for the highlight foreground color and load the current highlight
	 *     // foreground color from the configuration
	 *     final Property highlightFG = new ColorProperty("highlight foreground",
	 *                                          "changes the highlight foreground color in the algorithm text view",
	 *                                          config.getColor("highlightFG"));
	 *     // add the property to the properties list model
	 *     plm.add(highlightFG);
	 * }
	 * ...
	 * public void applyCustomization(PropertiesListModel plm) {
	 *     // get the highlight foreground color property from the model
	 *     final ColorProperty highlightFG = plm.getColorProperty("highlightFG");
	 *     // apply data to the algorithm text view of the plugin and to the configuration
	 *     if(highlightFG != null) {
	 *         config.addColor("highlightFG", highlightFG.getValue());
	 *         algoTextView.setHighlightForeground(highlightFG.getValue());
	 *     }
	 * }
	 * ...
	 * </pre>
	 * 
	 * @see #getConfiguration()
	 * @see #hasCustomization()
	 * @param plm the properties list model in which the the customization properties can be loaded
	 * @since 1.0
	 */
	public void loadCustomization(final PropertiesListModel plm);
	
	/**
	 * Applys the customization to the plugin. This is only possible if {@link #hasCustomization()} returns <code>true</code>.
	 * <br><br>
	 * <b>Plugin-Customization</b>:<br>
	 * The customization of a plugin is controlled by the host application meaning that the user can select the plugin in a customization
	 * dialog and is able to modify properties of the plugin.<br>
	 * The customization/configuration is composed of {@link Property}s where each one of them describes a customizable point.<br>
	 * To store the customization properties persistent you can finally add them to the plugin's {@link Configuration}.<br>
	 * <u>Example</u>:<br>
	 * By way of example you can give the user the possibility to change the highlight color of the {@link AlgorithmTextView} (and so on...):
	 * <pre>
	 * ...
	 * public void loadCustomization(PropertiesListModel plm) {
	 *     // create a property for the highlight foreground color and load the current highlight
	 *     // foreground color from the configuration
	 *     final Property highlightFG = new ColorProperty("highlight foreground",
	 *                                          "changes the highlight foreground color in the algorithm text view",
	 *                                          config.getColor("highlightFG"));
	 *     // add the property to the properties list model
	 *     plm.add(highlightFG);
	 * }
	 * ...
	 * public void applyCustomization(PropertiesListModel plm) {
	 *     // get the highlight foreground color property from the model
	 *     final ColorProperty highlightFG = plm.getColorProperty("highlightFG");
	 *     // apply data to the algorithm text view of the plugin and to the configuration
	 *     if(highlightFG != null) {
	 *         config.addColor("highlightFG", highlightFG.getValue());
	 *         algoTextView.setHighlightForeground(highlightFG.getValue());
	 *     }
	 * }
	 * ...
	 * </pre>
	 * 
	 * @see #getConfiguration()
	 * @see #hasCustomization()
	 * @param plm the properties list model in which the the customization properties can be loaded
	 * @since 1.0
	 */
	public void applyCustomization(final PropertiesListModel plm);
	
	/**
	 * Gets the toolbar extensions of the plugin.
	 * <br><br>
	 * With the extensions you can extend the toolbar of the host application by new components.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Every toolbar extension is disabled when the runtime environment is started to prevent thread interference.
	 * 
	 * @see ToolBarExtension
	 * @see CircleLayoutToolBarExtension
	 * @see BipartiteGraphToolBarExtension
	 * @see CompleteGraphToolBarExtension
	 * @see CompleteBipartiteGraphToolBarExtension
	 * @return an array of toolbar extensions or <code>null</code> if the plugin has no extensions
	 * @since 1.0
	 */
	public ToolBarExtension[] getToolBarExtensions();
	
	/**
	 * Saves the plugin content. This method is invoked when the user wants to save something in the host application.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * To enable this option you have to ensure that {@link #getSaveFileFilters()} returns valid filters.<br>
	 * If you have multiple file filters check the given file against its extension to decide which save operation
	 * you have to do.<br><br>
	 * <u>Example</u>:
	 * <pre>
	 * final FileNameExtensionFilter vgfFilter = new FileNameExtensionFilter("Visual Graph File (*.vgf)", "vgf");
	 * final FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png");
	 * ...
	 * public void save(File file) {
	 *     // use file filters to check the file
	 *     if(vgfFilter.accept(file))
	 *         saveAsVGF(...);
	 *     else if(pngFilter.accept(file))
	 *         saveAsPNG(...);
	 *     ...
	 *     
	 *     // or use the extension to check the file
	 *     if(file.getAbsolutePath().toLowerCase().endsWith(".vgf"))
	 *         saveAsVGF(...);
	 *     else if(file.getAbsolutePath().toLowerCase().endsWith(".png"))
	 *         saveAsPNG(...);
	 *     ...
	 * }
	 * </pre>
	 * 
	 * @param file the file
	 * @since 1.0
	 */
	public void save(final File file);
	
	/**
	 * Opens the plugin content. This method is invoked when the user wants to open something in the host application.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * To enable this option you have to ensure that {@link #getOpenFileFilters()} returns valid filters.<br>
	 * If you have multiple file filters check the given file against its extension to decide which open operation
	 * you have to do.<br><br>
	 * <u>Example</u>:
	 * <pre>
	 * final FileNameExtensionFilter vgfFilter = new FileNameExtensionFilter("Visual Graph File (*.vgf)", "vgf");
	 * final FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("Portable Network Graphics (*.png)", "png");
	 * ...
	 * public void open(File file) {
	 *     // use file filters to check the file
	 *     if(vgfFilter.accept(file))
	 *         openVGF(...);
	 *     else if(pngFilter.accept(file))
	 *         openPNG(...);
	 *     ...
	 *     
	 *     // or use the extension to check the file
	 *     if(file.getAbsolutePath().toLowerCase().endsWith(".vgf"))
	 *         openVGF(...);
	 *     else if(file.getAbsolutePath().toLowerCase().endsWith(".png"))
	 *         openPNG(...);
	 *     ...
	 * }
	 * </pre>
	 * 
	 * @param file the file
	 * @since 1.0
	 */
	public void open(final File file);
	
	/**
	 * Gets the save file filters for the plugin.
	 * <br><br>
	 * The file filters describe by what file types the plugin can handle with if the user wants to save
	 * something.<br>
	 * Each filter stands for a specific choice that means if you want to enable the options "save a graph file" and
	 * "save the graph as a png image", you should create two file filters, one for the graph file and one for the png.
	 * <br><br>
	 * The user can choose between the different filters in the host application more precisely in the file chooser of the
	 * host.
	 * 
	 * @return the file filters that are applied if the user wants to save something
	 * @since 1.0
	 */
	public FileNameExtensionFilter[] getSaveFileFilters();
	
	/**
	 * Gets the open file filters for the plugin.
	 * <br><br>
	 * The file filters describe by what file types the plugin can handle with if the user wants to open
	 * something.<br>
	 * Each filter stands for a specific choice that means if you want to enable the options "open a graph file" and
	 * "open algorithm data", you should create two file filters, one for the graph file and one for the algorithm data file.
	 * <br><br>
	 * The user can choose between the different filters in the host application more precisely in the file chooser of the
	 * host.
	 * 
	 * @return the file filters that are applied if the user wants to save something
	 * @since 1.0
	 */
	public FileNameExtensionFilter[] getOpenFileFilters();

}
