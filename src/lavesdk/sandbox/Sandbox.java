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

package lavesdk.sandbox;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import lavesdk.LAVESDKV;
import lavesdk.algorithm.AlgorithmExerciseProvider;
import lavesdk.algorithm.RTEvent;
import lavesdk.algorithm.RTEListener;
import lavesdk.algorithm.AlgorithmRTE;
import lavesdk.algorithm.enums.AlgorithmStartOption;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.algorithm.plugin.ResourceLoader;
import lavesdk.algorithm.plugin.ValidationReport;
import lavesdk.algorithm.plugin.Validator;
import lavesdk.algorithm.plugin.enums.MessageIcon;
import lavesdk.algorithm.plugin.extensions.ToolBarExtension;
import lavesdk.algorithm.plugin.views.ExercisesListView;
import lavesdk.algorithm.plugin.views.ViewContainer;
import lavesdk.algorithm.plugin.views.ViewGroup;
import lavesdk.algorithm.text.AlgorithmText;
import lavesdk.configuration.Configuration;
import lavesdk.gui.widgets.InformationBar;
import lavesdk.gui.widgets.Option;
import lavesdk.gui.widgets.OptionComboButton;
import lavesdk.language.LanguageFile;
import lavesdk.logging.LogFile;
import lavesdk.logging.enums.LogType;
import lavesdk.resources.Resources;
import lavesdk.utils.FileUtils;

/**
 * Use the sandbox as a test application for your algorithm plugin.
 * <br><br>
 * Create a new sandbox using either {@link #Sandbox(AlgorithmPlugin)} or {@link #Sandbox(AlgorithmPlugin, String)} and invoke
 * {@link #setVisible(boolean)} to make the sandbox visible, like:
 * <pre>
 * // replace new MyPlugin() with the instantiation of your plugin you would like to test
 * public class PluginTest extends Sandbox {
 *     private static final long serialVersionUID = 1L;
 *     
 *     public PluginTest() throws IllegalArgumentException {
 *         super(new MyPlugin());
 *     }
 *     
 *     public static void main(String[] args) {
 *         SwingUtilities.invokeLater(new Runnable() {
 *             public void run() {
 *                 new PluginTest().setVisible(true);
 *             }
 *         });
 *     }
 * }
 * </pre>
 * You can test all the basic functionality of your algorithm.
 * <br><br>
 * <b>Log file</b>:<br>
 * You can find a log file in the folder of your test application (the class which inherits from {@link Sandbox}).
 * This logs all the information messages, warnings or errors of the plugin. If your plugin implementation crashs first
 * have a look at this file. An empty log file means that no messages were logged.
 * 
 * @author jdornseifer
 * @version 1.2
 * @since 1.0
 */
public class Sandbox extends JFrame implements PluginHost {

	private static final long serialVersionUID = 1L;
	
	/** the plugin that should be tested */
	private final AlgorithmPlugin plugin;
	/** the runtime environment of the algorithm */
	private final AlgorithmRTE rte;
	/** the language file */
	private LanguageFile langFile;
	/** the language id */
	private final String langID;
    /** a mapping between execution speed factors (values) and integer values (keys from <code>1</code> to <code>execSpeedFactors.size()</code>) */
    private final Map<Integer, Float> execSpeedFactors;
    /** the key of the normal execution speed (<code>1.0f</code>) in the {@link #execSpeedFactors} map */
    private final int normalExecSpeedKey;
	/** the event controller */
	private final EventController eventController;
	/** the toolbar of the sandbox */
	private final JToolBar toolBar;
	/** the panel with the content meaning the plugin and the information bar with the assumption and the instructions */
	private final JPanel contentPanel;
	/** the information bar with the assumption and the instructions of the plugin */
	private final InformationBar infoBar;
	/** the exercises list as the default {@link AlgorithmExerciseProvider} */
	private final ExercisesListView exercisesList;
	/** the split pane that splits the exercises list from the view container */
	private final ViewGroup splitPane;
	/** the view container of the plugin */
	private final ViewContainer viewContainer;
	/** the toolbar button for: new algorithm */
	private final JButton newBtn;
	/** the toolbar button for: save */
	private final JButton saveBtn;
	/** the toolbar button for: open */
	private final JButton openBtn;
	/** the toolbar button for: change execution mode */
	private final JButton modeBtn;
	/** the toolbar button for: start rte */
	private final OptionComboButton startBtn;
	/** the option for: normal start */
	private final Option startBtnNormal;
	/** the option for: start to finish */
	private final Option startBtnToFinish;
	/** the option for: play and pause */
	private final Option startBtnPlayPause;
	/** the toolbar button for: pause rte */
	private final JButton pauseBtn;
	/** the toolbar button for: stop rte */
	private final JButton stopBtn;
	/** the toolbar button for: next step */
	private final JButton nextBtn;
	/** the toolbar button for: previous step */
	private final JButton prevBtn;
	/** the toolbar button for: skip breakpoints */
	private final JToggleButton skipBreakpointsBtn;
	/** the toolbar button for: pause before stop */
	private final JToggleButton pauseBeforeStopBtn;
	/** the toolbar slider for: execution speed */
	private final JSlider execSpeedSlider;
	/** the toolbar button for: slower execution speed */
	private final JButton slowerBtn;
	/** the toolbar button for: faster execution speed */
	private final JButton fasterBtn;
	/** the toolbar button for: reset execution speed */
	private final JButton resetExecSpeedBtn;
	/** the toolbar label for the current execution speed factor of the rte */
	private final JLabel execSpeedLbl;
	/** the log file of the sandbox (can be <code>null</code>) */
	private final LogFile logFile;
	/** the language dependent label of the exercise mode */
	private final String msgTitleExerciseMode;
	/** the language dependent info message of the exercise mode */
	private final String msgInfoExerciseMode;
	/** flag that indicates whether the plugin is invalid */
	private final boolean validPlugin;
	
	/** the width of the slider in the toolbar */
	private static final int EXECSPEED_SLIDER_WIDTH = 100;
	
	/**
	 * Creates the sandbox.
	 * 
	 * @param plugin the plugin that should be tested
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if plugin is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Sandbox(final AlgorithmPlugin plugin) throws IllegalArgumentException {
		this(plugin, "en");
	}
	
	/**
	 * Creates the sandbox.
	 * 
	 * @param plugin the plugin that should be tested
	 * @param langID the language id that should be used (like <i>en</i>, <i>de</i>, ...)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if plugin is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Sandbox(final AlgorithmPlugin plugin, final String langID) throws IllegalArgumentException {
		if(plugin == null)
			throw new IllegalArgumentException("No valid argument!");
		
		// firstly set up the system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		// initialize main data of the host
		this.plugin = plugin;
		this.langID = langID;
		this.langFile = Resources.getInstance().LANGUAGE_FILE;
		this.execSpeedFactors = new HashMap<Integer, Float>();
		this.normalExecSpeedKey = createExecSpeedFactors();
		
		// create a log file for the sandbox application
		LogFile lf;
		try {
			lf = new LogFile("log.txt");
		} catch (IOException e) {
			lf = null;
		}
		this.logFile = lf;
		

		// language dependent labels, tooltips and messages
		this.msgTitleExerciseMode = LanguageFile.getLabel(langFile, "MSG_INFO_TITLE_EXERCISEMODE", langID, "Exercise Mode");
		this.msgInfoExerciseMode = LanguageFile.getLabel(langFile, "MSG_INFO_EXERCISEMODE", langID, "The exercise mode can only be activated when the algorithm is stopped!");
		
		// initialize plugin
		plugin.initialize(this, new ResourceLoader(plugin.getClass().getClassLoader()), new Configuration());
		
		this.rte = plugin.getRuntimeEnvironment();
		this.eventController = new EventController();
		this.contentPanel = new JPanel(new BorderLayout());
		this.infoBar = new InformationBar(this, langFile, langID);
		this.exercisesList = new ExercisesListView(langFile, langID);
		this.exercisesList.setVisible(false);
		this.viewContainer = new ViewContainer(0);
		this.splitPane = new ViewGroup(ViewGroup.HORIZONTAL, 4);
		this.splitPane.add(exercisesList);
		this.splitPane.add(viewContainer);
		this.splitPane.setWeights(new float[] { 0.25f, 0.75f });
		this.splitPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		this.toolBar = new JToolBar();
		this.toolBar.setFloatable(false);
		this.newBtn = new JButton(Resources.getInstance().NEW_ICON);
		this.newBtn.setToolTipText(LanguageFile.getLabel(langFile, "FILE_NEW", langID, "New..."));
		this.newBtn.addActionListener(eventController);
		this.saveBtn = new JButton(Resources.getInstance().SAVE_ICON);
		this.saveBtn.setToolTipText(LanguageFile.getLabel(langFile, "FILE_SAVE_AS", langID, "Save as..."));
		this.saveBtn.addActionListener(eventController);
		this.openBtn = new JButton(Resources.getInstance().OPEN_ICON);
		this.openBtn.setToolTipText(LanguageFile.getLabel(langFile, "FILE_OPEN", langID, "Open..."));
		this.openBtn.addActionListener(eventController);
		this.modeBtn = new JButton(Resources.getInstance().EXERCISE_MODE_ICON);
		this.modeBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_EXERCISE_MODE", langID, "Exercise Mode"));
		this.modeBtn.addActionListener(eventController);
		this.startBtnNormal = new Option(Resources.getInstance().START_ICON, LanguageFile.getLabel(langFile, "RTE_START", langID, "Start/Resume"));
		this.startBtnNormal.addActionListener(eventController);
		this.startBtnToFinish = new Option(Resources.getInstance().START_FINISH_ICON, LanguageFile.getLabel(langFile, "RTE_START_TO_FINISH", langID, "Start/Resume to Finish"));
		this.startBtnToFinish.addActionListener(eventController);
		this.startBtnPlayPause = new Option(Resources.getInstance().PLAY_PAUSE_ICON, LanguageFile.getLabel(langFile, "RTE_PLAY_AND_PAUSE", langID, "Play And Pause"));
		this.startBtnPlayPause.addActionListener(eventController);
		this.startBtn = new OptionComboButton(new Option[] { startBtnNormal, startBtnToFinish, startBtnPlayPause });
		this.stopBtn = new JButton(Resources.getInstance().STOP_ICON);
		this.stopBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_STOP", langID, "Stop"));
		this.stopBtn.addActionListener(eventController);
		this.pauseBtn = new JButton(Resources.getInstance().PAUSE_ICON);
		this.pauseBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_PAUSE", langID, "Pause"));
		this.pauseBtn.addActionListener(eventController);
		this.nextBtn = new JButton(Resources.getInstance().NEXTSTEP_ICON);
		this.nextBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_NEXT_STEP", langID, "Next Step"));
		this.nextBtn.addActionListener(eventController);
		this.prevBtn = new JButton(Resources.getInstance().PREVSTEP_ICON);
		this.prevBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_PREV_STEP", langID, "Previous Step"));
		this.prevBtn.addActionListener(eventController);
		this.skipBreakpointsBtn = new JToggleButton(Resources.getInstance().SKIP_BREAKPOINTS_ICON);
		this.skipBreakpointsBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_SKIP_BREAKPOINTS", langID, "Skip All Breakpoints"));
		this.skipBreakpointsBtn.addActionListener(eventController);
		this.pauseBeforeStopBtn = new JToggleButton(Resources.getInstance().PAUSE_BEFORE_STOP_ICON);
		this.pauseBeforeStopBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_PAUSE_BEFORE_STOP", langID, "Pause Before Stop"));
		this.pauseBeforeStopBtn.addActionListener(eventController);
		this.execSpeedSlider = new JSlider(1, execSpeedFactors.size(), normalExecSpeedKey);
		this.execSpeedSlider.setToolTipText(LanguageFile.getLabel(langFile, "RTE_EXECSPEED_CHANGE", langID, "Change Execution Speed"));
		this.slowerBtn = new JButton(Resources.getInstance().SLOWER_ICON);
		this.slowerBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_EXECSPEED_SLOWER", langID, "Slower"));
		this.slowerBtn.addActionListener(eventController);
		this.fasterBtn = new JButton(Resources.getInstance().FASTER_ICON);
		this.fasterBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_EXECSPEED_FASTER", langID, "Faster"));
		this.fasterBtn.addActionListener(eventController);
		this.resetExecSpeedBtn = new JButton(Resources.getInstance().RESET_EXECSPEED_ICON);
		this.resetExecSpeedBtn.setToolTipText(LanguageFile.getLabel(langFile, "RTE_EXECSPEED_RESET", langID, "Reset Execution Speed"));
		this.resetExecSpeedBtn.addActionListener(eventController);
		this.execSpeedLbl = new JLabel(getExecSpeedFactorAsText(1.0f));
		this.execSpeedLbl.setToolTipText(LanguageFile.getLabel(langFile, "RTE_EXECSPEED", langID, "Execution Speed"));
		
		// set up the sandbox appearance
		setTitle("Sandbox - Plugin: " + plugin.getName() + ", Plugin-Version: " + plugin.getVersion() + " (LAVESDK-Version: " + LAVESDKV.CURRENT + ")");
		setLayout(new BorderLayout());
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPanel.add(infoBar, BorderLayout.NORTH);
		contentPanel.add(splitPane, BorderLayout.CENTER);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		// add default buttons to toolbar
		toolBar.add(newBtn);
		toolBar.addSeparator();
		toolBar.add(saveBtn);
		toolBar.add(openBtn);
		toolBar.addSeparator();
		toolBar.add(modeBtn);
		toolBar.addSeparator();
		toolBar.add(startBtn);
		toolBar.add(pauseBtn);
		toolBar.add(stopBtn);
		toolBar.addSeparator();
		toolBar.add(prevBtn);
		toolBar.add(nextBtn);
		toolBar.addSeparator();
		toolBar.add(pauseBeforeStopBtn);
		toolBar.add(skipBreakpointsBtn);
		toolBar.addSeparator();
		toolBar.add(slowerBtn);
		toolBar.add(execSpeedSlider);
		toolBar.add(fasterBtn);
		toolBar.add(execSpeedLbl);
		toolBar.add(resetExecSpeedBtn);
		
		// restrict the slider to a specific size otherwise he would take the remaining toolbar space
		final Dimension sliderDim = new Dimension(EXECSPEED_SLIDER_WIDTH, execSpeedSlider.getPreferredSize().height);
		execSpeedSlider.setPreferredSize(sliderDim);
		execSpeedSlider.setMaximumSize(sliderDim);
		execSpeedSlider.setMinimumSize(sliderDim);
		execSpeedSlider.addChangeListener(eventController);
		
		// the save as and the open button are only enabled if the plugin supports this actions and the change mode button
		// is only available if the plgin has an exercise mode
		saveBtn.setEnabled(plugin.getSaveFileFilters() != null);
		openBtn.setEnabled(plugin.getOpenFileFilters() != null);
		modeBtn.setEnabled(plugin.hasExerciseMode());
		
		// validate the plugin
		final ValidationReport vr = Validator.validate(plugin, false);
		
		// if the plugin is invalid then show the errors and warnings
		if(!vr.ok) {
			JOptionPane.showMessageDialog(this, vr.message, "Invalid plugin", JOptionPane.ERROR_MESSAGE);
			validPlugin = (vr.errorCount == 0);
		}
		else
			validPlugin = true;
		
		// check whether the used sdk version of the plugin is correct
		if(LAVESDKV.CURRENT.compareTo(plugin.getUsedSDKVersion()) != 0)
			JOptionPane.showMessageDialog(this, "The \"used SDK version\" property of the plugin returns a wrong version.\nThe plugin uses the SDK version " + LAVESDKV.CURRENT + " but the property returns version " + plugin.getUsedSDKVersion() + "!", "Used SDK version", JOptionPane.WARNING_MESSAGE);
		
		// load the toolbar extensions
		if(plugin.getToolBarExtensions() != null) {
			for(ToolBarExtension tbe : plugin.getToolBarExtensions()) {
				if(tbe != null)
					tbe.apply(toolBar);
			}
		}
		
		if(rte != null) {
			// register the sandbox as the host of the algorithm runtime environment
			rte.registerHost(this);
			// add the listener to listen to algorithm events and set the initial state of the rte controls
			rte.addListener(eventController);
			
		}
		checkRTECtrlsState();
		
		// update the information bar
		infoBar.update(plugin);
		
		// enable the plugin
		plugin.onCreate(viewContainer, null);
	}

	@Override
	public String getLanguageID() {
		return langID;
	}

	@Override
	public LanguageFile getLanguageFile() {
		return langFile;
	}

	@Override
	public boolean isActivePlugin(AlgorithmPlugin plugin) {
		return plugin == this.plugin;
	}

	@Override
	public void showMessage(AlgorithmPlugin plugin, String msg, String title,
			MessageIcon icon) {
		if(!isActivePlugin(plugin))
			return;
		
		JOptionPane.showMessageDialog(this, msg, title, icon.toMessageType());
	}

	@Override
	public void writeLogMessage(AlgorithmPlugin plugin, String msg, LogType type) {
		writeLogMessage(plugin, msg, null, type);
	}

	@Override
	public void writeLogMessage(AlgorithmPlugin plugin, String msg, Exception e, LogType type) {
		if(logFile != null)
			logFile.writeToLog(plugin, msg, e, type);
	}

	@Override
	public boolean checkPermission(PluginHost host) {
		return host == this;
	}

	@Override
	public AlgorithmExerciseProvider getDefaultExerciseProvider() {
		return exercisesList;
	}
	
	@Override
	public void rteModeChanged() {
		modeBtn.setSelected(rte.isExerciseModeEnabled());
		
		checkRTECtrlsState();
	}
	
	@Override
	public void adaptDialog(JDialog dialog) {
		dialog.setLocationRelativeTo(this);
	}

	@Override
	public int getPluginCount() {
		return 1;
	}

	@Override
	public String getPluginName(int index) throws IndexOutOfBoundsException {
		return plugin.getName();
	}

	@Override
	public String getPluginDescription(int index) throws IndexOutOfBoundsException {
		return plugin.getDescription();
	}

	@Override
	public String getPluginType(int index) throws IndexOutOfBoundsException {
		return plugin.getType();
	}

	@Override
	public String getPluginAuthor(int index) throws IndexOutOfBoundsException {
		return plugin.getAuthor();
	}

	@Override
	public String getPluginAuthorContact(int index) throws IndexOutOfBoundsException {
		return plugin.getAuthorContact();
	}

	@Override
	public String getPluginAssumptions(int index) throws IndexOutOfBoundsException {
		return plugin.getAssumptions();
	}

	@Override
	public String getPluginProblemAffiliation(int index) throws IndexOutOfBoundsException {
		return plugin.getProblemAffiliation();
	}

	@Override
	public String getPluginSubject(int index) throws IndexOutOfBoundsException {
		return plugin.getSubject();
	}

	@Override
	public String getPluginInstructions(int index) throws IndexOutOfBoundsException {
		return plugin.getInstructions();
	}

	@Override
	public String getPluginVersion(int index) throws IndexOutOfBoundsException {
		return plugin.getVersion();
	}

	@Override
	public AlgorithmText getPluginText(int index) throws IndexOutOfBoundsException {
		return plugin.getText();
	}
	
	/**
	 * Shows the sandbox but only if the plugin is valid meaning that their have not been occurred errors when validating
	 * the plugin.
	 * 
	 * @param visible <code>true</code> to make the sandbox visible
	 * @since 1.0
	 */
	@Override
	public void setVisible(boolean visible) {
		if(validPlugin)
			super.setVisible(visible);
		else
			dispose();
	}
	
	/**
	 * Updates the execution speed of the runtime environment with the current value of the {@link #execSpeedSlider}.
	 * 
	 * @since 1.0
	 */
	private void updateExecSpeed() {
		final float execSpeedFactor = execSpeedFactors.get(execSpeedSlider.getValue());
		if(rte != null)
			rte.setExecSpeedFactor(execSpeedFactor);
		
		execSpeedLbl.setText(getExecSpeedFactorAsText(execSpeedFactor));
		execSpeedLbl.repaint();
	}
	
	/**
	 * Creates the execution speed factors and stores them in {@link #execSpeedFactors}.
	 * 
	 * @return the key of the normal execution speed (<code>1.0f</code>)
	 * @since 1.0
	 */
	private int createExecSpeedFactors() {
		int normalFactorKey;
		
		execSpeedFactors.put(1, 0.1f);
		execSpeedFactors.put(2, 0.125f);
		execSpeedFactors.put(3, 0.25f);
		execSpeedFactors.put(4, 0.5f);
		execSpeedFactors.put(5, 0.75f);
		execSpeedFactors.put((normalFactorKey = 6), 1.0f);
		execSpeedFactors.put(7, 2.0f);
		execSpeedFactors.put(8, 4.0f);
		execSpeedFactors.put(9, 8.0f);
		execSpeedFactors.put(10, 16.0f);
		
		return normalFactorKey;
	}
	
	/**
	 * Gets an execution speed factor as a text representation.
	 * 
	 * @param factor the factor
	 * @return the text representation
	 * @since 1.0
	 */
	private String getExecSpeedFactorAsText(final float factor) {
		final int intFactor = (int)factor;
		final String result = ((float)intFactor == factor) ? "" + intFactor : "" + factor;
		
		return result + "x";
	}
	
	/**
	 * Checks (and sets) the state of each runtime environment control (start, stop, ...) whether it is enabled or not.
	 * 
	 * @since 1.0
	 */
	private void checkRTECtrlsState() {
		final boolean rteState = (rte != null);
		
		modeBtn.setEnabled(rteState && plugin.hasExerciseMode());
		startBtn.setEnabled(rteState && !rte.isRunning());
		pauseBtn.setEnabled(rteState && rte.isRunning() && !rte.isExerciseModeEnabled());
		stopBtn.setEnabled(rteState && rte.isStarted());
		nextBtn.setEnabled(rteState && rte.isStarted() && !rte.isExerciseModeEnabled());
		prevBtn.setEnabled(rteState && rte.isStarted() && !rte.isExerciseModeEnabled());
		skipBreakpointsBtn.setEnabled(rteState);
		pauseBeforeStopBtn.setEnabled(rteState);
		slowerBtn.setEnabled(rteState);
		execSpeedSlider.setEnabled(rteState);
		fasterBtn.setEnabled(rteState);
	}
	
	/**
	 * Does the new action meaning that the plugin is reloaded.
	 * 
	 * @since 1.0
	 */
	private void doNew() {
		plugin.onClose();
		// clear the view container
		viewContainer.removeAll();
		viewContainer.revalidate();
		// and reload plugin content
		plugin.onCreate(viewContainer, null);
	}
	
	/**
	 * Does the save action of the plugin.
	 * 
	 * @since 1.0
	 */
	private void doSaveAs() {
		final JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.setAcceptAllFileFilterUsed(false);
		
		// add all file filters of the plugin to the file chooser
		for(FileNameExtensionFilter fnef : plugin.getSaveFileFilters())
			if(fnef != null)
				fc.addChoosableFileFilter(fnef);
		
		final int result = fc.showSaveDialog(this);
		if(result == JFileChooser.APPROVE_OPTION) {
			final FileNameExtensionFilter fnef = (FileNameExtensionFilter)fc.getFileFilter();
			
			// validate the selected file in causa of a valid extension so that several file types
			// can be handled in the save method of the plugin
			plugin.save(FileUtils.validateFile(fc.getSelectedFile(), fnef.getExtensions()[0]));
		}
	}
	
	/**
	 * Does the open action of the plugin.
	 * 
	 * @since 1.0
	 */
	private void doOpen() {
		final JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.setAcceptAllFileFilterUsed(false);
		
		// add all file filters of the plugin to the file chooser
		for(FileNameExtensionFilter fnef : plugin.getOpenFileFilters())
			if(fnef != null)
				fc.addChoosableFileFilter(fnef);
		
		final int result = fc.showOpenDialog(this);
		if(result == JFileChooser.APPROVE_OPTION) {
			final FileNameExtensionFilter fnef = (FileNameExtensionFilter)fc.getFileFilter();
			
			// validate the selected file in causa of a valid extension so that several file types
			// can be handled in the open method of the plugin
			plugin.open(FileUtils.validateFile(fc.getSelectedFile(), fnef.getExtensions()[0]));
		}
	}
	
	/**
	 * Changes the execution mode of the plugin.
	 * 
	 * @since 1.0
	 */
	private void doChangeMode() {
		if(rte.isStarted()) {
			JOptionPane.showMessageDialog(this, msgInfoExerciseMode, msgTitleExerciseMode, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		rte.setExerciseModeEnabled(!rte.isExerciseModeEnabled());
	}
	
	/**
	 * Starts the runtime environment but only if there is a runtime environment.
	 * 
	 * @param option the start option
	 * @since 1.0
	 */
	private void doStart(final AlgorithmStartOption option) {
		if(rte == null)
			return;
		
		rte.start(option);
	}
	
	/**
	 * Pauses the runtime environment but only if there is a runtime environment.
	 * 
	 * @since 1.0
	 */
	private void doPause() {
		if(rte == null)
			return;
		
		rte.pause();
	}
	
	/**
	 * Stops the runtime environment but only if there is a runtime environment.
	 * 
	 * @since 1.0
	 */
	private void doStop() {
		if(rte == null)
			return;
		
		rte.stop();
	}
	
	/**
	 * Goes to the previous step in the runtime environment but only if there is a runtime environment.
	 * 
	 * @since 1.0
	 */
	private void doPrevStep() {
		if(rte == null)
			return;
		
		rte.prevStep();
	}
	
	/**
	 * Goes to the next step in the runtime environment but only if there is a runtime environment.
	 * 
	 * @since 1.0
	 */
	private void doNextStep() {
		if(rte == null)
			return;
		
		rte.nextStep();
	}
	
	/**
	 * Changes the skip breakpoints flag of the runtime environment.
	 * 
	 * @since 1.0
	 */
	private void doSkipBreakpoints() {
		if(rte == null)
			return;
		
		rte.setSkipBreakpoints(skipBreakpointsBtn.isSelected());
	}
	
	/**
	 * Changes the pause before stop flag of the runtime environment.
	 * 
	 * @since 1.0
	 */
	private void doPauseBeforeStop() {
		if(rte == null)
			return;
		
		rte.setPauseBeforeTerminate(pauseBeforeStopBtn.isSelected());
	}
	
	/**
	 * Sets the enabled state of the toolbar extensions.
	 * 
	 * @param enabled <code>true</code> if the extensions should be enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	private void setToolBarExtensionsState(final boolean enabled) {
		if(plugin.getToolBarExtensions() != null) {
			for(ToolBarExtension tbe : plugin.getToolBarExtensions()) {
				if(tbe != null)
					tbe.setEnabled(enabled);
			}
		}
	}
	
	/**
	 * Handles the events of the sandbox.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	private class EventController implements ActionListener, ChangeListener, RTEListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == Sandbox.this.newBtn)
				Sandbox.this.doNew();
			else if(e.getSource() == Sandbox.this.saveBtn)
				Sandbox.this.doSaveAs();
			else if(e.getSource() == Sandbox.this.openBtn)
				Sandbox.this.doOpen();
			else if(e.getSource() == Sandbox.this.modeBtn)
				Sandbox.this.doChangeMode();
			else if(e.getSource() == Sandbox.this.startBtnNormal)
				Sandbox.this.doStart(AlgorithmStartOption.NORMAL);
			else if(e.getSource() == Sandbox.this.startBtnToFinish)
				Sandbox.this.doStart(AlgorithmStartOption.START_TO_FINISH);
			else if(e.getSource() == Sandbox.this.startBtnPlayPause)
				Sandbox.this.doStart(AlgorithmStartOption.PLAY_AND_PAUSE);
			else if(e.getSource() == Sandbox.this.pauseBtn)
				Sandbox.this.doPause();
			else if(e.getSource() == Sandbox.this.stopBtn)
				Sandbox.this.doStop();
			else if(e.getSource() == Sandbox.this.prevBtn)
				Sandbox.this.doPrevStep();
			else if(e.getSource() == Sandbox.this.nextBtn)
				Sandbox.this.doNextStep();
			else if(e.getSource() == Sandbox.this.skipBreakpointsBtn)
				Sandbox.this.doSkipBreakpoints();
			else if(e.getSource() == Sandbox.this.pauseBeforeStopBtn)
				Sandbox.this.doPauseBeforeStop();
			else if(e.getSource() == Sandbox.this.slowerBtn)
				Sandbox.this.execSpeedSlider.setValue(Sandbox.this.execSpeedSlider.getValue() - 1);
			else if(e.getSource() == Sandbox.this.fasterBtn)
				Sandbox.this.execSpeedSlider.setValue(Sandbox.this.execSpeedSlider.getValue() + 1);
			else if(e.getSource() == Sandbox.this.resetExecSpeedBtn)
				Sandbox.this.execSpeedSlider.setValue(Sandbox.this.normalExecSpeedKey);
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			Sandbox.this.updateExecSpeed();
		}

		@Override
		public void beforeStart(RTEvent e) {
		}

		@Override
		public void beforeResume(RTEvent e) {
		}

		@Override
		public void beforePause(RTEvent e) {
		}

		@Override
		public void onStop() {
			Sandbox.this.checkRTECtrlsState();
			Sandbox.this.setToolBarExtensionsState(true);
		}
		
		@Override
		public void onRunning() {
			Sandbox.this.checkRTECtrlsState();
			Sandbox.this.setToolBarExtensionsState(false);
		}
		
		@Override
		public void onPause() {
			Sandbox.this.checkRTECtrlsState();
		}
		
	}

}
