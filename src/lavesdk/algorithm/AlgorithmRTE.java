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

package lavesdk.algorithm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lavesdk.algorithm.AlgorithmExercise.ExamResult;
import lavesdk.algorithm.enums.AlgorithmStartOption;
import lavesdk.algorithm.plugin.AlgorithmPlugin;
import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.algorithm.plugin.security.HostSecurity;
import lavesdk.algorithm.plugin.views.GraphView;
import lavesdk.algorithm.plugin.views.TextAreaView;
import lavesdk.algorithm.plugin.views.View;
import lavesdk.algorithm.text.AlgorithmStep;
import lavesdk.algorithm.text.AlgorithmText;
import lavesdk.gui.EDT;
import lavesdk.gui.GuiJob;
import lavesdk.gui.GuiRequest;
import lavesdk.logging.enums.LogType;

/**
 * The runtime environment of an algorithm.
 * <br><br>
 * Implement your algorithm by inherit from this class and override the methods {@link #executeStep(int, AlgorithmStateAttachment)},
 * {@link #createInitialState(AlgorithmState)}, {@link #storeState(AlgorithmState)}, {@link #restoreState(AlgorithmState)},
 * {@link #rollBackStep(int, int)} and {@link #adoptState(int, AlgorithmState)}.<br>
 * For information about implementing the algorithm step-by-step look at {@link #executeStep(int, AlgorithmStateAttachment)}.
 * <br><br>
 * To increase/decrease the execution speed of the algorithm set the factor ({@link #setExecSpeedFactor(float)}) to a greater/smaller
 * value than <code>1.0f</code>. Skip/ignore all breakpoints by using {@link #setSkipBreakpoints(boolean)}.
 * <br><br>
 * Use {@link #setMinStepDwellTime(long)} to specify the execution time that a step should have at least. This prevents from overrunning
 * steps especially if the user steps back in the algorithm.
 * <br><br>
 * <b>Listener</b>:<br>
 * Use {@link #addListener(RTEListener)} to add a {@link RTEListener} to listen to runtime events like:
 * <ul>
 * 		<li>when does the algorithm start? ({@link RTEListener#beforeStart(RTEvent)})</li>
 * 		<li>when does the algorithm resume? ({@link RTEListener#beforeResume(RTEvent)})</li>
 * 		<li>when does the algorithm pause? ({@link RTEListener#beforePause(RTEvent)})</li>
 * 		<li>when does the algorithm stop? ({@link RTEListener#onStop()})</li>
 * </ul>
 * <b>The plugin is automatically added as a listener of runtime events</b>.
 * <br><br>
 * <b>Attention</b>:<br>
 * The algorithm runtime environment is only functioning if the host application is registered using
 * {@link #registerHost(lavesdk.algorithm.plugin.PluginHost)}. <b>This must only be done by the host application</b>!
 * <br><br>
 * <b>Exercise mode</b>:<br>
 * The algorithm can be executed in two different modes, the normal mode on the one hand which allows the user to
 * start, resume, pause, stop and go the previous or next step and the exercise mode on the other hand. In the exercise mode
 * it is only possible to start and stop the algorithm, the execution is done fully automatic so the user can only give right answers
 * to the questions to go one step further. If the user cannot solve an exercise he can give it up to continue.<br>
 * Use {@link #setExerciseModeEnabled(boolean)} to change the mode of the runtime environment.
 * <br><br>
 * <b>Visualization Policy (please note)</b>:<br>
 * The LAVESDK uses the Swing framework to display the graphical user interface of algorithms or in general to display the GUI.
 * Furthermore each algorithm has its own runtime environment (RTE) that runs in an own thread. This brings us a hazard of
 * unpredictable behavior (like thread interference or memory consistency errors) when a RTE thread updates the GUI. That's because
 * the GUI is handled by another thread (the event dispatch thread, EDT) and moreover the Swing GUI is especially not thread-safe.<br>
 * But the thread-safe modification of the GUI must be possible because the visualization of an algorithm is done in the RTE thread,
 * meaning another thread as the EDT.
 * <br><br>
 * Therefore it is stated that each visual component that is part of the LAVESDK has to be thread-safe and should be tagged as one.
 * If this is not the case it is up to you to ensure that all the <b>thread-unsafe calls</b> you make to modify the GUI of
 * your algorithm plugin are shifted to the EDT.<br>
 * You can shift a GUI action to the EDT from the RTE thread by using a {@link GuiJob} or a {@link GuiRequest}.<br>
 * <u>Example</u>: you have a <code>TextView</code> component that has a thread-unsafe <code>setText(...)</code> method and a <code>ListView</code> component
 * that has a thread-unsafe <code>getEntry(...)</code> method
 * <pre>
 * ...
 * protected void executeStep(int stepID, AlgorithmStateAttachment asa) throws Exception {
 *     ...
 *     switch(stepID) {
 *         case 3:
 *             // a step that changes a specific set
 *         
 *             // visualize the changed set but the setText()-method of the TextView is not thread-safe
 *             // so shift the call to the EDT
 *             EDT.execute(new GuiJob() {
 *                 protected void execute() throws Throwable {
 *                     textView.setText(set);
 *                 }
 *             });
 *             
 *             nextStepID = 4;
 *             break;
 *         case 7:
 *            // a step that has to modify an entry of a ListView component its getEntry() method is not thread-safe
 *            final Entry e = EDT.execute(new GuiRequest&lt;Entry&gt;() {
 *                protected Entry execute() throws Throwable {
 *                    return listView.getEntry(index);
 *                }
 *            });
 *            
 *            // modify the entry
 *            ...
 *            break;
 *     }
 *     
 *     return nextStepID;
 * }
 * ...
 * </pre>
 * 
 * @author jdornseifer
 * @version 1.3
 * @since 1.0
 */
public abstract class AlgorithmRTE extends HostSecurity {
    
	/** the thread in which the runtime environment of the algorithm is running or <code>null</code> if currently no thread is existing */
	private Thread thread;
	/** the runtime environment */
    private final RuntimeEnvironment rte;
    /** the algorithm plugin that uses the runtime environment */
    private final AlgorithmPlugin plugin;
    /** the initial state of the algorithm */
    private final AlgorithmState initialState;
    /** the listeners of the rte */
    private final List<RTEListener> listeners;
    /** a custom exercise provider or <code>null</code> if there is no custom provider */
    private final AlgorithmExerciseProvider customExerciseProvider;
    
    /** flag for the {@link RTEListener#beforeStart(RTEvent)} event */
    private static final short RTEVENT_BEFORESTART = 1;
    /** flag for the {@link RTEListener#beforeResume(RTEvent)} event */
    private static final short RTEVENT_BEFORERESUME = 2;
    /** flag for the {@link RTEListener#beforePause(RTEvent)} event */
    private static final short RTEVENT_BEFOREPAUSE = 3;
    /** flag for the {@link RTEListener#onStop()} event */
    private static final short RTEVENT_ONSTOP = 4;
    /** flag for the {@link RTEListener#onRunning()} event */
    private static final short RTEVENT_ONRUNNING = 5;
    /** flag for the {@link RTEListener#onPause()} event */
    private static final short RTEVENT_ONPAUSE = 6;
    
    /**
     * Creates a new algorithm runtime environment.
     * 
     * @param plugin the algorithm plugin that uses the runtime environment
     * @param text the algorithm text that is executed in the runtime environment
     * @throws IllegalArgumentException
     * <ul>
     * 		<li>if plugin is null</code>
     * 		<li>if text is null</code>
     * </ul>
	 * @since 1.0
     */
    public AlgorithmRTE(final AlgorithmPlugin plugin, final AlgorithmText text) throws IllegalArgumentException {
    	this(plugin, text, null);
    }
    
    /**
     * Creates a new algorithm runtime environment.
     * 
     * @param plugin the algorithm plugin that uses the runtime environment
     * @param text the algorithm text that is executed in the runtime environment
     * @param provider a custom provider that handles and displays exercises of the algorithm or <code>null</code> if the default provider of the host should be used
     * @throws IllegalArgumentException
     * <ul>
     * 		<li>if plugin is null</code>
     * 		<li>if text is null</code>
     * </ul>
	 * @since 1.0
     */
    public AlgorithmRTE(final AlgorithmPlugin plugin, final AlgorithmText text, final AlgorithmExerciseProvider provider) throws IllegalArgumentException {
    	if(plugin == null)
    		throw new IllegalArgumentException("No valid argument!");
    	
    	this.thread = null;
        this.rte = new RuntimeEnvironment(text);
        this.plugin = plugin;
        this.listeners = new ArrayList<RTEListener>();
        this.customExerciseProvider = provider;
        
        // create the initial state of the algorithm
        initialState = new AlgorithmState(plugin, RuntimeEnvironment.INITIALSTATE_STEPID);
        createInitialState(initialState);
        // very important: freeze the initial state and DO NOT add this one to the history!!!
        initialState.freeze();
        
        // add the plugin as the first listener of runtime events
        listeners.add(plugin);
    }
    
    /**
     * Adds a new listener to listen to runtime events of the algorithm.
     * 
     * @see RTEAdapter
     * @param listener the listener
     * @since 1.0
     */
    public final void addListener(final RTEListener listener) {
    	if(listener == null || listeners.contains(listener))
    		return;
    	
    	listeners.add(listener);
    }
    
    /**
     * Removes the listener from the algorithm runtime environment.
     * 
     * @param listener the listener
     * @since 1.0
     */
    public final void removeListener(final RTEListener listener) {
    	if(listener == null || listener == plugin)
    		return;
    	
    	listeners.remove(listener);
    }
    
    /**
     * Starts or resumes the execution of the algorithm.<br>
     * This triggers the {@link RTEListener#beforeStart(RTEvent)} or {@link RTEListener#beforeResume(RTEvent)} and the
     * {@link RTEListener#onRunning()} event.
     * 
     * @see #pause()
     * @see #stop()
     * @since 1.0
     */
    public final void start() {
    	start(AlgorithmStartOption.NORMAL);
    }
    
    /**
     * Starts or resumes the execution of the algorithm.<br>
     * This triggers the {@link RTEListener#beforeStart(RTEvent)} or {@link RTEListener#beforeResume(RTEvent)} and the
     * {@link RTEListener#onRunning()} event.
     * <br><br>
     * <b>Notice</b>:<br>
     * Each step of the algorithm has a specific dwell time (either a custom one determined by the algorithm developer or a predefined one determined by {@link #setMinStepDwellTime(long)}).
     * This dwell time can be ignored using the {@link AlgorithmStartOption#START_TO_FINISH} option. This means that only breakpoints are regarded.
	 * <br><br>
	 * If the runtime environment is in exercise mode it is not possible to pause and resume the work. The execution of the algorithm
	 * is performed fully automatic by the runtime environment during the exercise.
     * 
     * @see #pause()
     * @see #stop()
     * @param option the start option of the algorithm
     * @throws IllegalArgumentException
     * <ul>
     * 		<li>if option is null</li>
     * </ul>
     * @since 1.0
     */
    public final void start(final AlgorithmStartOption option) throws IllegalArgumentException {
    	if(option == null)
    		throw new IllegalArgumentException("No valid argument!");
    	
    	// only the active plugin has permission to start the rte
    	if(!isActivePlugin(plugin)) {
    		writeLogMessage(plugin, "Plugin tries to start its algorithm runtime environment but is not active!", LogType.WARNING);
    		return;
    	}
    	
    	// rte is running? then break up
        if(isRunning())
        	return;
        
        if(!isStarted()) {
        	// fire runtime event and break up if the event is canceled
        	if(!fireRuntimeEvent(RTEVENT_BEFORESTART))
        		return;
        	
        	// we start from the beginning
        	rte.restart(option);
        	
        	// restore the initial data of the time of freezing
        	initialState.unfreeze();
        	// restore the initial state
        	restoreState(initialState);
        	
        	// start a new thread if rte is stopped or not started yet
        	synchronized(this) {
        		thread = new Thread(rte);
        		thread.start();
        	}
        }
        else if(rte.isPaused()) {
        	// fire runtime event and break up if the event is canceled
        	if(!fireRuntimeEvent(RTEVENT_BEFORERESUME))
        		return;
        	
        	/*
        	 * INFO:
        	 * If the rte is in exercise mode then it is not possible to resume its work. Because of the rte
        	 * cannot be paused, isRunning() returns true which means that the work cannot be resumed because
        	 * of the check above so we to do anything here.
        	 */

            // let the runtime environment resume its work
        	rte.resume(option);
        }
        
        fireRuntimeEvent(RTEVENT_ONRUNNING);
    }
    
    /**
     * Pauses the runtime environment of the algorithm until it is resumed by calling {@link #start()}.<br>
     * This triggers the {@link RTEListener#beforePause(RTEvent)} and the {@link RTEListener#onPause()} event.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If the runtime environment is in exercise mode it is not possible to pause and resume the work. The execution of the algorithm
	 * during the exercise is performed fully automatic by the runtime environment.
     * 
     * @see #start()
     * @see #stop()
     * @since 1.0
     */
    public final void pause() {
    	// only the active plugin has permission to pause the rte
    	if(!isActivePlugin(plugin)) {
    		writeLogMessage(plugin, "Plugin tries to pause its algorithm runtime environment but is not active!", LogType.WARNING);
    		return;
    	}
    	
    	if(isStarted() && !isExerciseModeEnabled()) {
        	// fire runtime event and break up if the event is canceled
        	if(!fireRuntimeEvent(RTEVENT_BEFOREPAUSE))
        		return;
        	
    		rte.pause();
    		
    		// pause is done so notify the listeners
    		fireRuntimeEvent(RTEVENT_ONPAUSE);
    	}
    }
    
    /**
     * Stops the runtime environment of the algorithm. This means that the rte has to be restarted by calling
     * {@link #start()}.<br>
     * This triggers the {@link RTEListener#onStop()} event.
     * 
     * @see #pause()
     * @since 1.0
     */
    public final void stop() {
    	// only the active plugin has permission to stop the rte
    	if(!isActivePlugin(plugin)) {
    		writeLogMessage(plugin, "Plugin tries to stop its algorithm runtime environment but is not active!", LogType.WARNING);
    		return;
    	}
    	
    	if(!isStarted())
    		return;
    	
    	// interrupt runtime thread
    	// (important: further actions are done in the onStop() method which is invoked automatically by the rte -> see run())
    	synchronized(this) {
    		if(thread != null)
    			thread.interrupt();
    	}
    }
    
    /**
     * Goes to the next step of the algorithm. This is only possible if the runtime environment was started once
     * and not stopped until yet.
     * <br><br>
     * <b>Notice</b>:<br>
	 * If the exercise mode is enabled ({@link #isExerciseModeEnabled()}) then it is not possible to go to the next step.
     * 
     * @since 1.0
     */
    public final void nextStep() {
    	// only the active plugin has permission to go to the next step
    	if(!isActivePlugin(plugin)) {
    		writeLogMessage(plugin, "Plugin tries to go to the next step in its algorithm runtime environment but is not active!", LogType.WARNING);
    		return;
    	}
    	
    	if(!isStarted())
    		return;
    	
    	// skips the execution of the current step (this is only possible if the rte is not in exercise mode)
    	rte.skipStep();
    }
    
    /**
     * Goes to the previous step of the algorithm. This is only possible if the runtime environment was started once
     * and not stopped until yet.
     * <br><br>
     * <b>Notice</b>:<br>
	 * If the exercise mode is enabled ({@link #isExerciseModeEnabled()}) then it is not possible to go to the previous step.
     * 
     * @since 1.0
     */
    public final void prevStep() {
    	// only the active plugin has permission to go to the previous step
    	if(!isActivePlugin(plugin)) {
    		writeLogMessage(plugin, "Plugin tries to go to the previous step in its algorithm runtime environment but is not active!", LogType.WARNING);
    		return;
    	}
    	
    	if(!isStarted())
    		return;
    	
    	// go one step back (this is only possible if the rte is not in exercise mode)
    	rte.goStepBack();
    }
    
    /**
     * Indicates if the runtime environment is started.
     * 
     * @return <code>true</code> if the rte is started otherwise <code>false</code>
     * @since 1.0
     */
    public final boolean isStarted() {
    	return rte.isStarted();
    }
    
    /**
     * Indicates if the algorithm runtime environment is currently in running mode. This means that
     * it is started and not paused.
     * 
     * @return <code>true</code> if the rte is running otherwise <code>false</code>
     */
    public final boolean isRunning() {
        return isStarted() && !rte.isPaused();
    }
	
	/**
	 * Gets the execution speed factor of the rte.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * A value smaller <code>1.0f</code> means a <b>slower</b> and a value greater <code>1.0f</code> a
	 * <b>faster execution</b>. For example a factor of <code>2.0f</code> means that the execution of the
	 * algorithm is two times faster as normal and a factor of <code>0.5f</code> means that the execution is only
	 * half as fast as normal (<code>0.1f</code> means ten times slower as normal).
	 * 
	 * @return the execution speed factor (a value <code>> 0.0f</code>)
	 * @since 1.0
	 */
	public final float getExecSpeedFactor() {
		return rte.getExecSpeedFactor();
	}
	
	/**
	 * Sets the execution speed factor of the rte.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * A value smaller <code>1.0f</code> means a <b>slower</b> and a value greater <code>1.0f</code> a
	 * <b>faster execution</b>. For example a factor of <code>2.0f</code> means that the execution of the
	 * algorithm is two times faster as normal and a factor of <code>0.5f</code> means that the execution is only
	 * half as fast as normal (<code>0.1f</code> means ten times slower as normal).
	 * 
	 * @param factor the execution speed factor (a value <code>> 0.0f</code>)
	 * @since 1.0
	 */
	public final void setExecSpeedFactor(final float factor) {
		rte.setExecSpeedFactor(factor);
	}
	
	/**
	 * Gets the minimal dwell time that a step must have.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If a step has no dwell time it is hard to go back over this step ("go to previous step") because it is always transition into the next step.
	 * So if a step does not have the minimal dwell time the runtime environment is automatically paused for the remaining time and
	 * an overrun of the step is prevented.<br>
	 * The default value is <code>500</code> milliseconds.
	 * 
	 * @return the minimal execution time in milliseconds
	 * @since 1.0
	 */
	public final long getMinStepDwellTime() {
		return rte.getMinDwellTime();
	}
	
	/**
	 * Sets the minimal dwell time that a step must have.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If a step has no dwell time it is hard to go back over this step ("go to previous step") because it is always transition into the next step.
	 * So if a step does not have the minimal dwell time the runtime environment is automatically paused for the remaining time and
	 * an overrun of the step is prevented.<br>
	 * The default value is <code>500</code> milliseconds.
	 * 
	 * @param millis the minimal dwell time in milliseconds
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if millis is <code>< 0</code></li>
	 * </ul>
	 * @since 1.0
	 */
	public final void setMinStepDwellTime(final long millis) throws IllegalArgumentException {
		rte.setMinDwellTime(millis);
	}
	
	/**
	 * Indicates if the breakpoints will currently be skipped/ignored.
	 * 
	 * @return <code>true</code> if breakpoints are ignored otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean getSkipBreakpoints() {
		return rte.getSkipBreakpoints();
	}
	
	/**
	 * Sets if the breakpoints should currently be skipped/ignored.
	 * 
	 * @param skip <code>true</code> if breakpoints are ignored otherwise <code>false</code>
	 * @since 1.0
	 */
	public final void setSkipBreakpoints(final boolean skip) {
		rte.setSkipBreakpoints(skip);
	}
	
	/**
	 * Indicates whether the runtime environment should be paused before it transitions into stop.
	 * 
	 * @return <code>true</code> if the runtime environment pauses the execution before it transitions into stop otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean getPauseBeforeStop() {
		return rte.getPauseBeforeTerminate();
	}
	
	/**
	 * Sets whether the runtime environment should be paused before it transitions into stop.
	 * 
	 * @param pause <code>true</code> if the environment should pause the execution before it transitions into stop otherwise <code>false</code>
	 * @since 1.0
	 */
	public final void setPauseBeforeTerminate(final boolean pause) {
		rte.setPauseBeforeTerminate(pause);
	}
	
	/**
	 * Indicates whether the exercise mode of the runtime environment is enabled.
	 * <br><br>
	 * Exercises of the algorithm are only presented to the user when the exercise mode is enabled.
	 * 
	 * @return <code>true</code> if the exercise mode is enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public final boolean isExerciseModeEnabled() {
		return rte.isExerciseModeEnabled();
	}
	
	/**
	 * Sets whether the exercise mode of the runtime environment should be enabled.
	 * <br><br>
	 * Exercises of the algorithm are only presented to the user when the exercise mode is enabled.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * This is only possible if the plugin has this mode ({@link AlgorithmPlugin#hasExerciseMode()} and the rte is not started yet (meaning
	 * the exercise mode can only be activated if the rte is stopped).
	 * The {@link PluginHost} is notified about the changed mode by {@link PluginHost#rteModeChanged()}.
	 * 
	 * @param enabled <code>true</code> if the exercise mode should be enabled otherwise <code>false</code>
	 * @since 1.0
	 */
	public final void setExerciseModeEnabled(final boolean enabled) {
		// if the plugin does not have an exercise mode then it is not possible to enable this mode
		if(!plugin.hasExerciseMode())
			return;
		
		rte.setExerciseModeEnabled(enabled);
	}
    
    /**
     * Allows the runtime environment to sleep for a specific amount of time.
     * 
     * @param millis the sleeping time in milliseconds
     * @since 1.0
     */
    protected final void sleep(final long millis) {
    	if(!isStarted() || millis <= 0)
    		return;
    	
    	/*
    	 * INFO:
    	 * It may not be checked against isRunning() otherwise a step is completely
    	 * processed although he has inner steps in the following scenario:
    	 * The user pauses the rte and goes to the next step.
    	 * That means if the execution of the next step uses multiple sleeps for inner steps
    	 * all these invocations are ignored because the rte is in pause mode which means isRunning()
    	 * is false.
    	 */
    	
    	rte.sleep(millis);
    }
    
    /**
     * Executes a step of the algorithm.
     * <br><br>
     * <b>Visualize a step</b>:<br>
     * Use {@link #sleep(long)} to let the algorithm sleep for a specific amount of time to visualize the current step.<br>
     * Please note that if you modify a GUI component (for example a {@link GraphView}) you should invoke the <i>repaint</i> method after
     * you make the modification.<br>
     * <u>Example</u>: Change the background color of a vertex with the caption "v".
     * <pre>
     * ...
     * // get the visual component of the vertex
     * GraphView<Vertex, Edge>.VisualVertex vv = graphView.getVisualVertexByCaption("v");
     * // change the background and afterwards repaint the graph view
     * vv.setBackground(Color.yellow);
     * graphView.repaint();
     * </pre>
     * <b>Important (please note)</b>:<br>
     * The execution of algorithm steps runs in an own thread of the runtime environment. Because the Swing framework is
     * not thread-safe this brings us a hazard of unpredictable behavior if you update the GUI (meaning you visualize something)
     * from the inside of this method because the method is executed in another thread then the GUI (the GUI is executed in a thread
     * called the event dispatch thread, EDT).<br>
     * To avoid this hazard of unpredictable behavior you have to consider the following: if you invoke a method of a visual component
     * that is not tagged as <b>thread-safe</b> then you have to implement it on your own<br>
	 * <u>Example</u>: the method <code>setText(...)</code> of a component <code>TextView</code> is <b>not tagged as thread-safe</b>:
	 * <pre>
	 * final Set&lt;Integer&gt; set;
	 * ...
	 * // visualize the current set
	 * EDT.execute(new GuiJob() {
	 *     protected void execute() {
	 *        textView.setText(set.toString());
	 *     }
	 * });
	 * ...
     * </pre>
     * If a method is <b>tagged as thread-safe</b> (like "This method is thread-safe!") then you can invoke the method directly in the runtime environment without shift it
     * to the EDT.
     * 
     * @param stepID the id of the step that should be executed
     * @param asa the attachment of the current algorithm state that can be used to attach data that is available only during the execution of the current step
     * @return the id of the following step or <code>< 0</code> if the algorithm is terminated
     * @throws Exception
     * <ul>
     * 		<li>a step can throw an exception while the step is in execution which results in a breakup of the whole algorithm execution but prevents from a crash -<br>
     *          the exception will be logged (via the connected host) but this is an <b>unusual behavior</b> of the algorithm and should be avoided</li>
     * </ul>
     * @since 1.0
     */
    protected abstract int executeStep(final int stepID, final AlgorithmStateAttachment asa) throws Exception;
    
    /**
     * Stores the current state of the algorithm.
     * <br><br>
     * Use the <i>add</i> methods ({@link AlgorithmState#addInt(String, int)}/{@link AlgorithmState#addSet(String, lavesdk.math.Set)}/...) to
     * store the current content of the variables you use in your algorithm implementation.
     * <br><br>
     * <b>Example</b>:
     * <pre>
     * public class MyAlgorithm extends AlgorithmRTE {
     *     ...
     *     private int k;
     *     private float B;
     *     private List&lt;Integer&gt; indices;
     *     ...
     *     
     *     protected void storeState(AlgorithmState state) {
     *         state.addInt("k", k);
     *         state.addFloat("B", B);
     *         state.addList("indices", indices);
     *     }
     *     
     *     protected void restoreState(AlgorithmState state) {
     *         k = state.getInt("k");
     *         B = state.getFloat("B");
     *         indices = state.getList("indices");
     *     }
     *     
     *     ...
     *     
     *     protected int executeStep(int stepID, AlgorithmStateAttachment asa) {
     *         ...
     *         // implement each step of the algorithm
     *         switch(stepID) {
     *             case ...: k = indices.get(i); indices.remove(i); ...
     *             case ...: B = 0.5*k*i; ...
     *         }
     *         
     *         return nextStep;
     *     }
     *     
     *     ...
     * }
     * </pre>
     * <b>Attention</b>:<br>
     * If you want to store custom objects in an algorithm state ensure that these objects implement {@link Serializable}
     * and are <code>public</code> and independent classes.<br>
     * If a state cannot be frozen (error message: "Algorithm state could not be frozen!") a reason might be that you try to store
     * custom objects that are not <code>public</code> or not independent (like nested classes).
     * 
     * @param state the state where the algorithm variables/data should be stored
     * @since 1.0
     */
    protected abstract void storeState(final AlgorithmState state);
    
    /**
     * Restores a state of the algorithm.
     * <br><br>
     * Use the <i>get</i> methods ({@link AlgorithmState#addInt(String, int)}/{@link AlgorithmState#addSet(String, lavesdk.math.Set)}/...) to
     * assign the data of the state to the variables you use in your algorithm implementation.
     * <br><br>
     * <b>Example</b>:
     * <pre>
     * public class MyAlgorithm extends AlgorithmRTE {
     *     ...
     *     private int k;
     *     private float B;
     *     private List&lt;Integer&gt; indices;
     *     ...
     *     
     *     protected void storeState(AlgorithmState state) {
     *         state.addInt("k", k);
     *         state.addFloat("B", B);
     *         state.addList("indices", indices);
     *     }
     *     
     *     protected void restoreState(AlgorithmState state) {
     *         k = state.getInt("k");
     *         B = state.getFloat("B");
     *         indices = state.getList("indices");
     *     }
     *     
     *     ...
     *     
     *     protected int executeStep(int stepID, AlgorithmStateAttachment asa) {
     *         ...
     *         // implement each step of the algorithm
     *         switch(stepID) {
     *             case ...: k = indices.get(i); indices.remove(i); ...
     *             case ...: B = 0.5*k*i; ...
     *         }
     *         
     *         return nextStep;
     *     }
     *     
     *     ...
     * }
     * </pre>
     * <b>Attention</b>:<br>
     * If a state cannot be made unfrozen (error message: "Algorithm state could not be unfrozen!") a reason might be that you try to restore
     * custom objects that are not <code>public</code> or not independent (like nested classes).
     * 
     * @param state the state where the algorithm variables/data should be stored
     * @since 1.0
     */
    protected abstract void restoreState(final AlgorithmState state);
    
    /**
     * Creates the initial state of the algorithm meaning the initial values of the variables
     * which are used in your algorithm.
     * <br><br>
     * This state is used to restore the initial state of the algorithm if for example the runtime environment is stopped ({@link #stop()})
     * and the algorithm should start from scratch.
     * <br><br>
     * <b>Notice</b>:<br>
     * This method is invoked at creation time of the algorithm runtime environment which means in the constructor (see example) so it is
     * only called once.
     * <br><br>
     * <b>Example</b>:
     * <pre>
     * public class MyAlgorithm extends AlgorithmRTE {
     *     ...
     *     private int k;
     *     private float B;
     *     private List&lt;Integer&gt; indices;
     *     ...
     *     
     *     public MyAlgorithm(AlgorithmPlugin plugin) {
     *         super(plugin);
     *         ...
     *         // the variables of the algorithm are initialized through createInitialState(...)
     *     }
     *     
     *     protected void createInitialState(AlgorithmState state) {
     *         k = state.addInt("k", 0);
     *         B = state.addFloat("B", 0.0f);
     *         indices = state.addList("indices", new ArrayList<>());
     *     }
     *     
     *     ...
     * }
     * </pre>
     * 
     * @see #storeState(AlgorithmState)
     * @see #restoreState(AlgorithmState)
     * @param state the state where the algorithm variables/data should be stored
     * @since 1.0
     */
    protected abstract void createInitialState(final AlgorithmState state);
    
    /**
     * Rolls back the specified step.
     * <br><br>
     * This method is triggered if a state is popped from the state history meaning that the algorithm is set
     * to a previous step so that an earlier state of the algorithm is restored.<br>
     * It is invoked {@link #restoreState(AlgorithmState)} before a step is rolled back meaning it is possible to access the
     * state the algorithm had before the given step was executed.
     * <br><br>
     * <b>Use this method to undo the visualization of a step</b>.
     * <br><br>
     * <u>Example</u>:
     * <pre>
	 * protected void rollBackStep(int stepID) {
	 *     switch(stepID) {
	 *         ...
	 *         case StepIDs.INITIALIZE_SETS:
	 *             // the step to initialize the sets of the algorithm (like "Let A:={v1}, B:={v | v in V and (v, v1) in E} ...")
	 *             // should be rolled back meaning we delete the text in the specific view that shows the sets
	 *             textAreaView.setText("");
	 *             break;
	 *         ...
	 *     }
	 * }
     * </pre>
     * <b>Important (please note)</b>:<br>
     * The execution of algorithm steps runs in an own thread of the runtime environment. Because the Swing framework is
     * not thread-safe this brings us a hazard of unpredictable behavior if you update the GUI (meaning you visualize something)
     * from the inside of this method because the method is executed in another thread then the GUI (the GUI is executed in a thread
     * called the event dispatch thread, EDT).<br>
     * To avoid this hazard of unpredictable behavior you have to consider the following: if you invoke a method of a visual component
     * that is not tagged as <b>thread-safe</b> then you have to implement it on your own<br>
	 * Example: the method <code>setText(...)</code> of a component <code>TextView</code> is <b>not tagged as thread-safe</b>:
	 * <pre>
	 * final Set&lt;Integer&gt; set;
	 * ...
	 * // visualize the current set
	 * EDT.execute(new GuiJob() {
	 *     protected void execute() {
	 *        textView.setText(set.toString());
	 *     }
	 * });
	 * ...
     * </pre>
     * If a method is <b>tagged as thread-safe</b> (like "This method is thread-safe!") then you can invoke the method directly in the runtime environment without shift it
     * to the EDT.
     * 
     * @param stepID the id of the step to roll back
     * @param nextStepID the id of the next step that would have been executed if the current step were not rolled back (this information is useful when a step has several execution branches with each doing different visualization work); might be <code>< 0</code> if no further step follows and the algorithm terminates
     * @since 1.0
     */
    protected abstract void rollBackStep(final int stepID, final int nextStepID);
    
    /**
     * Lets the algorithm adopt the given state.
     * <br><br>
     * This has to be performed <b>if and only if</b> an exercise of an {@link AlgorithmStep} overrides {@link AlgorithmExercise#getApplySolutionToAlgorithm()} meaning
     * that it returns <code>true</code>.<br>
     * A state is adopted directly before the related step is executed.<br>
     * Request the solution(s) by use of the associated key(s) defined in {@link AlgorithmExercise#applySolutionToAlgorithm(AlgorithmState, Object[])}.
     * <br><br>
     * Applying the solution to the algorithm is useful when a step can be executed in several ways so adopting a solution lets the
	 * algorithm continue with the way the user has chosen.
	 * <br><br>
	 * <u>Example</u>: The user has to select a path in a graph from a specific vertex to another one which means in general that their
	 * are more than one correct paths. The algorithm can choose any path too, that is the path must not be equal to the user's choice.
	 * Applying the solution of the user to the algorithm let the algorithm take the same path as the user has specified and it ensures
	 * a synchronous execution.
     * 
     * @param stepID the id of the step for whom this adoption is done
     * @param state the state the algorithm should adopt (<b>in general this state is not equal a state stored or restored with {@link #storeState(AlgorithmState)}/{@link #restoreState(AlgorithmState)}</b>)
     * @since 1.0
     */
    protected abstract void adoptState(final int stepID, final AlgorithmState state);
    
    /**
     * Gets the views that are used in the runtime environment to visualize the algorithm.
     * <br><br>
     * <b>Example</b>:<br>
     * The algorithm uses a {@link GraphView} <code>graphView</code> and a {@link TextAreaView} <code>textAreaView</code> to visualize the algorithm,
     * so the method should return them as follows:
     * <pre>
     * protected View[] getViews() {
     *     return new View[] { graphView, textAreaView };
     * }
     * </pre>
     * These view information are used to disable the repaint mechanism of the views when an algorithm step is skipped by use of {@link #nextStep()}
     * or {@link #prevStep()}. Disabling the repaint mechanism might enhance the performance during a step is skipped.
     * 
     * @return the array of views that are used to visualize the algorithm or <code>null</code>
     * @since 1.0
     */
    protected abstract View[] getViews();
    
    @Override
    protected final void hostAccepted() {
    	// there is set a secured host system for the runtime environment? then set the exercise provider that should be used
    	rte.setExerciseProvider((customExerciseProvider != null) ? customExerciseProvider : getDefaultExerciseProvider());
    }
    
    /**
     * Stops and resets the runtime environment of the algorithm meaning that the current runtime environment
     * thread is terminated if necessary and the executing step of the {@link AlgorithmText}
     * is set to the initial one.
     * <br><br>
     * Furthermore the runtime event "on stop" is fired.
     * <br><br>
     * <b>Attention</b>:<br>
     * This method may only be invoked by the {@link RuntimeEnvironment#run()} method!
     * 
     * @since 1.0
     */
    private void onStop() {
    	// clear the runtime thread
    	synchronized(this) {
    		thread = null;
    	}
        
        // notify the listeners
        fireRuntimeEvent(RTEVENT_ONSTOP);
    }
    
    /**
     * Fires the declared runtime event for each listener.
     * 
     * @see #RTEVENT_BEFORESTART
     * @see #RTEVENT_BEFORERESUME
     * @see #RTEVENT_BEFOREPAUSE
     * @see #RTEVENT_ONSTOP
     * @see #RTEVENT_ONRUNNING
     * @param event the event
     * @return <code>true</code> if the event should be done otherwise <code>false</code>
     * @since 1.0
     */
    private boolean fireRuntimeEvent(final int event) {
    	for(int i = 0; i < listeners.size(); i++) {
    		final RTEListener l = listeners.get(i);
    		final RTEvent e = new RTEvent(rte.getExecutingStepID());
    		
    		EDT.execute(new GuiJob(getClass().getSimpleName() + ".fireRuntimeEvent", true) {
				@Override
				protected void execute() throws Throwable {
		    		switch(event) {
			    		case RTEVENT_BEFORESTART:
			    			l.beforeStart(e);
			    			break;
			    		case RTEVENT_BEFORERESUME:
			    			l.beforeResume(e);
			    			break;
			    		case RTEVENT_BEFOREPAUSE:
			    			l.beforePause(e);
			    			break;
			    		case RTEVENT_ONSTOP:
			    			l.onStop();
			    			break;
			    		case RTEVENT_ONRUNNING:
			    			l.onRunning();
			    			break;
			    		case RTEVENT_ONPAUSE:
			    			l.onPause();;
			    			break;
		    		}
				}
			});
    		
    		if(!e.doit)
    			return false;
    	}
    	
    	return true;
    }
    
    /**
     * The runtime environment (rte) is responsible for controlling the schedule of an algorithm step-by-step.
	 * <br><br>
	 * <b>Attention</b>:<br>
	 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this class!<br>
	 * <i>DO NOT REMOVE THE PRIVATE VISIBILITY OF THIS CLASS</i>!
     * 
     * @author jdornseifer
     * @version 1.3
     * @since 1.0
     */
    private final class RuntimeEnvironment implements Runnable, AlgorithmExerciseController {
    	
    	/** monitor to lock for synchronization */
    	private final Object monitor;
        /** the algorithm text that is executed in the runtime environment */
        private final AlgorithmText text;
        /** the history of the algorithm states */
        private final Stack<AlgorithmState> stateHistory;
        /** the provider of the exercises or <code>null</code> */
        private AlgorithmExerciseProvider exerciseProvider;
        /** flag that indicates whether the exercise mode is enabled */
        private boolean exerciseModeEnabled;
        /** flag that indicates whether the runtime environment is started or not */
        private boolean started;
        /** the step id of the current step which is executed */
        private int executingStepID;
    	/** flag that indicates whether the rte is paused */
        private boolean paused;
    	/** a runtime-wide flag that indicates whether the rte should be terminated (this is done at the end of the step that is in execution at this time) */
        private boolean terminateRTE;
    	/** flag that indicates whether the step that is currently in execution should be skipped (see also {@link #enableSkipStepFlag()} and {@link #disableSkipStepFlag()}) */
        private boolean skipCurrStep;
        /** flag that indicates whether the last step should be restored */
        private boolean stepBack;
        /** factor to increase or decrease the speeding of the rte */
        private float sleepFactor;
    	/** flag that indicates whether the breakpoints should be skipped */
        private boolean skipBreakpoints;
        /** the minimal dwell time that a step must have to prevent from overrunning steps especially when the user steps back in the algorithm */
        private long minDwellTime;
        /** the current start option of the algorithm */
        private AlgorithmStartOption currStartOpt;
        /** flag that indicates whether the schedule should be paused before it is terminated */
        private boolean pauseBeforeTerminate;
        
        /** the step id of the initial state which is an invalid step id related to the algorithm steps and only be supposed to identify the initial algorithm state */
        private static final int INITIALSTATE_STEPID = -1;
    	
    	/**
    	 * Creates a new rte based on the properties of the specified one.
    	 * 
    	 * @param text the algorithm text
	     * @throws IllegalArgumentException
	     * <ul>
	     * 		<li>if text is null</code>
	     * </ul>
    	 * @since 1.0
    	 */
    	public RuntimeEnvironment(final AlgorithmText text) throws IllegalArgumentException {
    		if(text == null)
    			throw new IllegalArgumentException("No valid argument!");
    		
    		this.monitor = new Object();
    		this.text = text;
    		this.stateHistory = new Stack<AlgorithmState>();
    		this.exerciseProvider = null;
    		this.exerciseModeEnabled = false;
    		this.started = false;
    		this.executingStepID = INITIALSTATE_STEPID;
    		this.paused = false;
    		this.terminateRTE = false;
    		this.skipCurrStep = false;
    		this.stepBack = false;
    		this.sleepFactor = 1.0f;
    		this.skipBreakpoints = false;
    		this.minDwellTime = 500;
    		this.currStartOpt = AlgorithmStartOption.NORMAL;
    		this.pauseBeforeTerminate = false;
            
            // currently there is no step in execution
            text.setExecutingStepID(executingStepID);
    	}
    	
    	/**
    	 * Restarts the rte meaning that the rte is set to its initial state before the algorithm is executed.
    	 * 
    	 * @param option the start option
    	 * @since 1.0
    	 */
    	public synchronized void restart(final AlgorithmStartOption option) {
    		// already started? then break up
    		if(started)
    			return;
    		
    		this.currStartOpt = !exerciseModeEnabled ? option : AlgorithmStartOption.NORMAL;
    		this.started = true;
    		this.executingStepID = text.getFirstStepID();
    		this.paused = false;
    		this.terminateRTE = false;
    		this.skipCurrStep = false;
    		this.stepBack = false;
    		this.stateHistory.clear();
    	}
    	
    	/**
    	 * Indicates whether the rte is started.
    	 * 
    	 * @see #restart()
    	 * @return <code>true</code> if the rte is started otherwise <code>false</code>
    	 * @since 1.0
    	 */
    	public boolean isStarted() {
    		return started;
    	}
		
		/**
		 * Resumes the work of the runtime environment but only if the rte was started previously and it is not enabled the exercise mode.
		 * <br><br>
		 * If the rte is in exercise mode it is not possible to pause and resume the rte. The execution of the algorithm during the exercise is
		 * performed fully automatic.
		 * 
		 * @see #pause()
    	 * @param option the start option
		 * @since 1.0
		 */
		public void resume(final AlgorithmStartOption option) {
			// the rte cannot resume its work when it is not started yet or is in exercise mode
			// if the algorithm is in exercise mode then it is only possible to start and stop the algorithm,
			// resume/pause/prevStep/nextStep are inactive
			if(!started || exerciseModeEnabled)
				return;
			
			synchronized(this) {
				this.currStartOpt = !exerciseModeEnabled ? option : AlgorithmStartOption.NORMAL;
				this.paused = false;
			}
			
			wakeUp();
		}
		
		/**
		 * Wakes up the runtime environment that it can resume its work.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * To resume the work of the rte please use {@link #resume(boolean)} instead of this method because this method is part of
		 * the {@link AlgorithmExerciseController} and should only be used by exercises.
		 * 
		 * @since 1.0
		 */
		public void wakeUp() {
			synchronized(monitor) {
				monitor.notify();
			}
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.0
		 */
		@Override
		public AlgorithmExerciseProvider getExerciseProvider() {
			return exerciseProvider;
		}
		
		/**
		 * Sets the provider of the exercises.
		 * 
		 * @param provider the provider
		 * @since 1.0
		 */
		public synchronized void setExerciseProvider(final AlgorithmExerciseProvider provider) {
			exerciseProvider = provider;
		}
		
		/**
		 * Pauses the runtime environment but only if the rte is not in exercise mode.
		 * <br><br>
		 * If the rte is in exercise mode it is not possible to pause and resume the rte. The execution of the algorithm during the exercise is
		 * performed fully automatic.
		 * 
		 * @see #resume()
		 * @since 1.0
		 */
		public synchronized void pause() {
			// if the algorithm is in exercise mode then it is only possible to start and stop the algorithm,
			// resume/pause/prevStep/nextStep are inactive
			if(exerciseModeEnabled)
				return;
			
			paused = true;
		}
		
		/**
		 * Indicates whether the runtime environment is paused.
		 * 
		 * @return <code>true</code> if rte is paused otherwise <code>false</code>
		 * @since 1.0
		 */
		public boolean isPaused() {
			return paused;
		}
		
		/**
		 * Lets the runtime sleep for a specific amount of time.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * If {@link #skipStep()} is invoked all sleep invocations are suppress for the current step in execution.
		 * 
		 * @param millis time in milliseconds
		 * @since 1.0
		 */
		public void sleep(final long millis) {
			if(skipCurrStep || terminateRTE)
				return;
			
			try {
				// sleep if possible (notice: wait(0); does not have the effect that waiting is ignored (it needs
				// also a notify()))
				if(sleepFactor > 0.0f) {
					synchronized(monitor) {
		                monitor.wait((long)(millis * sleepFactor));
		            }
				}
				
				// if rte is paused in the meanwhile wait after sleeping until rte is unpaused
				checkPause();
			}
			catch(InterruptedException e) {
				terminateRTE = true;
				enableSkipStepFlag();
			}
		}
		
		/**
		 * Suppresses all sleep and pause invocations for the current run.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * {@link #wakeUp()} is invoked automatically to wake up the rte from its current state (sleep or pause).<br>
		 * <b>If the exercise mode is enabled ({@link #isExerciseModeEnabled()}) then it is not possible to skip steps.</b>
		 * 
		 * @since 1.0
		 */
		public void skipStep() {
			synchronized(this) {
				// if the algorithm is in exercise mode then it is only possible to start and stop the algorithm,
				// resume/pause/prevStep/nextStep are inactive
				if(exerciseModeEnabled)
					return;
				
				enableSkipStepFlag();
			}
			
			// wake up the rte
			wakeUp();
		}
		
		/**
		 * Goes one step back in the execution of the algorithm and restores the step's state.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * {@link #skipStep()} is automatically invoked which means additionally that {@link #wakeUp()} is also called
		 * to wake up the rte from its current state (sleep or pause).<br>
		 * <b>If the exercise mode is enabled ({@link #isExerciseModeEnabled()}) then it is not possible to step back.</b>
		 * 
		 * @since 1.0
		 */
		public void goStepBack() {
			synchronized(this) {
				// if the algorithm is in exercise mode then it is only possible to start and stop the algorithm,
				// resume/pause/prevStep/nextStep are inactive
				if(exerciseModeEnabled)
					return;
				
				stepBack = true;
			}
			
			// skip the current step and wake up the rte
			skipStep();
		}
    	
    	/**
    	 * Gets the identifier of the step that is currently in execution.
    	 * 
    	 * @return the step id or {@link #INITIALSTATE_STEPID} if the rte is not started yet
    	 * @since 1.0
    	 */
    	public int getExecutingStepID() {
    		return executingStepID;
    	}
		
		/**
		 * Indicates if the breakpoints will currently be skipped/ignored.
		 * 
		 * @return <code>true</code> if breakpoints are ignored otherwise <code>false</code>
		 * @since 1.0
		 */
		public boolean getSkipBreakpoints() {
			return skipBreakpoints;
		}
		
		/**
		 * Sets if the breakpoints should currently be skipped/ignored.
		 * 
		 * @param skip <code>true</code> if breakpoints are ignored otherwise <code>false</code>
		 * @since 1.0
		 */
		public synchronized void setSkipBreakpoints(final boolean skip) {
			skipBreakpoints = skip;
		}
		
		/**
		 * Gets the execution speed factor of the rte.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * A value smaller <code>1.0f</code> means a <b>slower</b> and a value greater <code>1.0f</code> a
		 * <b>faster execution</b>. For example a factor of <code>2.0f</code> means that the execution of the
		 * algorithm is two times faster as normal and a factor of <code>0.5f</code> means that the execution is only
		 * half as fast as normal (<code>0.1f</code> means ten times slower as normal).
		 * 
		 * @return the execution speed factor (a value <code>> 0.0f</code>)
		 * @since 1.0
		 */
		public float getExecSpeedFactor() {
			return 1.0f / sleepFactor;
		}
		
		/**
		 * Sets the execution speed factor of the rte.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * A value smaller <code>1.0f</code> means a <b>slower</b> and a value greater <code>1.0f</code> a
		 * <b>faster execution</b>. For example a factor of <code>2.0f</code> means that the execution of the
		 * algorithm is two times faster as normal and a factor of <code>0.5f</code> means that the execution is only
		 * half as fast as normal (<code>0.1f</code> means ten times slower as normal).
		 * 
		 * @param factor the execution speed factor (a value <code>> 0.0f</code>)
		 * @since 1.0
		 */
		public synchronized void setExecSpeedFactor(final float factor) {
			if(factor > 0.0f)
				sleepFactor = 1.0f / factor;
		}
		
		/**
		 * Gets the minimal dwell time that a step must have.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * If a step has no dwell time it is hard to go back over this step ("go to previous step") because it is always transition into the next step.
		 * So if a step does not have the minimal dwell time the runtime environment is automatically paused for the remaining time and
		 * an overrun of the step is prevented.<br>
		 * The default value is <code>500</code> milliseconds.
		 * 
		 * @return the minimal dwell time in milliseconds
		 * @since 1.0
		 */
		public long getMinDwellTime() {
			return minDwellTime;
		}
		
		/**
		 * Sets the minimal dwell time that a step must have.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * If a step has no dwell time it is hard to go back over this step ("go to previous step") because it is always transition into the next step.
		 * So if a step does not have the minimal dwell time the runtime environment is automatically paused for the remaining time and
		 * an overrun of the step is prevented.<br>
		 * The default value is <code>500</code> milliseconds.
		 * 
		 * @param millis the minimal dwell time in milliseconds
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if millis is <code>< 0</code></li>
		 * </ul>
		 * @since 1.0
		 */
		public synchronized void setMinDwellTime(final long millis) throws IllegalArgumentException {
			if(millis < 0)
				throw new IllegalArgumentException("No valid argument!");
			
			minDwellTime = millis;
		}
		
		/**
		 * Indicates whether the runtime environment should be paused before it transitions into terminate state.
		 * 
		 * @return <code>true</code> if the rte pauses the execution before it transitions into terminate state otherwise <code>false</code>
		 * @since 1.0
		 */
		public boolean getPauseBeforeTerminate() {
			return pauseBeforeTerminate;
		}
		
		/**
		 * Sets whether the runtime environment should be paused before it transitions into terminate state.
		 * 
		 * @param pause <code>true</code> if the rte pauses the execution before it transitions into terminate state otherwise <code>false</code>
		 * @since 1.0
		 */
		public synchronized void setPauseBeforeTerminate(final boolean pause) {
			pauseBeforeTerminate = pause;
		}
		
		/**
		 * Indicates whether the exercise mode of the rte is enabled.
		 * <br><br>
		 * Exercises of the algorithm are only presented to the user when the exercise mode is enabled.
		 * 
		 * @return <code>true</code> if the exercise mode is enabled otherwise <code>false</code>
		 * @since 1.0
		 */
		public boolean isExerciseModeEnabled() {
			return exerciseModeEnabled;
		}
		
		/**
		 * Sets whether the exercise mode of the rte should be enabled.
		 * <br><br>
		 * Exercises of the algorithm are only presented to the user when the exercise mode is enabled.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * This is only possible if the rte is not started yet (meaning the exercise mode can only be activated if the rte is stopped).
		 * The {@link PluginHost} is notified about the changed mode by {@link PluginHost#rteModeChanged()}.
		 * 
		 * @param enabled <code>true</code> if the exercise mode should be enabled otherwise <code>false</code>
		 * @since 1.0
		 */
		public void setExerciseModeEnabled(final boolean enabled) {
			AlgorithmExerciseProvider provider = null;
			
			synchronized(this) {
				// if the rte is already started or the flag does not changed then it is not possible that the
				// exercution mode is switched
				if(started || exerciseModeEnabled == enabled)
					return;
				
				exerciseModeEnabled = enabled;
				provider = exerciseProvider;
			}
			
			// if the state of the exercise mode is changed then we have to update the handler of the provider because
			// a provider can be used in multiple runtime environments
			if(provider != null)
				provider.setHandler(this);
			
			if(getHost() != null)
				getHost().rteModeChanged();
			
			// switch the visibility state of the provider
			if(provider != null) {
				final AlgorithmExerciseProvider ep = provider;
				EDT.execute(new GuiJob(getClass().getSimpleName() + ".setExerciseModeEnabled") {
					@Override
					protected void execute() throws Throwable {
						ep.setVisible(enabled);
					}
				});
			}
		}
		
		@Override
		public PluginHost getHost() {
			return AlgorithmRTE.this.getHost();
		}
		
		/**
		 * Does the schedule of the algorithm in the background.
		 * 
		 * @since 1.0
		 */
		@Override
		public void run() {
			final Thread rteThread = AlgorithmRTE.this.thread;
			int nextStepID = -1;
			long dwellTimeStart;
			long dwellTime;
			boolean processed = false;
			AlgorithmExercise<?> exercise = null;
			boolean playAndPauseAllowed = false;
			
			terminateRTE = false;
			
			// notify the exercise provider that the exam begins but only if the exercise mode is enabled
			if(exerciseModeEnabled) {
				EDT.execute(new GuiJob(getClass().getSimpleName() + ".run") {
					@Override
					protected void execute() throws Throwable {
						if(RuntimeEnvironment.this.exerciseProvider != null)
							RuntimeEnvironment.this.exerciseProvider.beginExam();
					}
				});
			}
			
			while(!processed && !rteThread.isInterrupted()) {
				try {
					// set the step that is currently in execution
					text.setExecutingStepID(executingStepID);
					
					// cache the state of the current executing step
					pushStateHistory(executingStepID);
					
					// check for a breakpoint before the step is executed (if the step has a breakpoint then the rte is paused)
					if(!checkBreakpoint(executingStepID)) {
						// if a step has a breakpoint it is not necessary to pause again
					
						// if the play and pause start option is enabled then we pause the algorithm before the current step is executed
						// meaning if the user presses play and pause the current step is executed and the next step is activated but not performed
						// (play and pause is allowed after one step is executed otherwise the rte will pause before the first step is executed)
						if(playAndPauseAllowed && currStartOpt == AlgorithmStartOption.PLAY_AND_PAUSE) {
							AlgorithmRTE.this.pause();
							checkPause();
						}
					}
					
					// if the current start option is "run until the algorithm ends" then enable the skip step flag before each step is executed
					if(currStartOpt == AlgorithmStartOption.START_TO_FINISH)
						enableSkipStepFlag();
					
					// get the exercise of the current step
					exercise = getCurrentExercise();
					
					// execute the current step normally when the rte is not in exercise mode or the step does not have
					// an exercise otherwise communicate with the exercise for execution
					if(!exerciseModeEnabled || exercise == null) {
						dwellTimeStart = System.currentTimeMillis();
						
						// execute the current step
						nextStepID = executeCurrentStep();
	
						// measure the dwell time of the step
						dwellTime = System.currentTimeMillis() - dwellTimeStart;
						// step does not achieve the minimal dwell time? then sleep the remaining time
						if(dwellTime < minDwellTime)
							sleep(minDwellTime - dwellTime);
					}
					else
						nextStepID = processExercise(exercise, text.getStepByID(executingStepID));
					
					// valid next step?
					if(nextStepID > 0 && !terminateRTE) {
						// pause the rte if necessary
			            checkPause();
			            
			            if(stepBack) {
			            	// go to the predecessor state of the executing step
			            	nextStepID = popStateHistory(executingStepID, nextStepID);
			            	// the state is restored
			            	stepBack = false;
			            }
					}
					else
						processed = true;	// no more steps? that means the process is done!
	            }
	            catch(InterruptedException e) {
	            	terminateRTE = true;
	            }
	            
	            // reset flags that are supposed to manipulate the current step in execution
				disableSkipStepFlag();
	            stepBack = false;

	            // schedule is done?
	            if(processed && !terminateRTE) {
	            	exercise = text.getFinalExercise();
	            	
	            	try {
		            	// if the exercise mode is enabled and there is a final exercise then
	            		if(exerciseModeEnabled && exercise != null)
	            			processExercise(exercise, null);
		            	
		            	// pause the algorithm before it terminates but only if the exercise mode is not enabled
			            // (this has to be done after the skip step flag is reset otherwise it has no effect)
		            	if(!exerciseModeEnabled && pauseBeforeTerminate) {
			            	AlgorithmRTE.this.pause();
			            	checkPause();
			            	
			            	// give the user the possibility to step back from the end of the algorithm schedule
			            	if(stepBack) {
				            	// go to the predecessor state of the executing step
				            	nextStepID = popStateHistory(executingStepID, nextStepID);
				            	// reset the flags
								disableSkipStepFlag();
				            	stepBack = false;
				            	processed = false;
				            }
		            	}
	            	}
	            	catch(InterruptedException e) {
	            		terminateRTE = true;
	            	}
	            }
	            
	            // terminate the runtime environment?
	            if(terminateRTE)
	            	rteThread.interrupt();
	            
	            // important: set the algorithm to the next step
	            executingStepID = nextStepID;
	            // after the first step is executed play and pause option is enabled
	            playAndPauseAllowed = true;
        	}
	    	
	    	// if runtime thread is not interrupted yet then do it first
	    	if(!rteThread.isInterrupted())
	    		rteThread.interrupt();
			
			// notify the exercise provider that the exam ends but only if the exercise mode is enabled
			if(exerciseModeEnabled) {
				EDT.execute(new GuiJob(getClass().getSimpleName() + ".run") {
					@Override
					protected void execute() throws Throwable {
						if(RuntimeEnvironment.this.exerciseProvider != null)
							RuntimeEnvironment.this.exerciseProvider.endExam(RuntimeEnvironment.this.terminateRTE);
					}
				});
			}
	    	
	    	done();
		}
		
		/**
		 * Enables the {@link #skipCurrStep} flag and disables the repaint mechansim of each view of {@link AlgorithmRTE#getViews()}.
		 * 
		 * @since 1.0
		 */
		private void enableSkipStepFlag() {
			final boolean oldState = skipCurrStep;
			skipCurrStep = true;
			
			if(skipCurrStep != oldState) {
				// disable the repaint mechanism of the algorithm views
				final View[] views = AlgorithmRTE.this.getViews();
				if(views != null) {
					for(View view : views) {
						if(view != null)
							view.setRepaintDisabled(true);
					}
				}
			}
		}
		
		/**
		 * Disables the {@link #skipCurrStep} flag and enables the repaint mechansim of each view of {@link AlgorithmRTE#getViews()}.
		 * Furthermore each view is repainted automatically.
		 * 
		 * @since 1.0
		 */
		private void disableSkipStepFlag() {
			final boolean oldState = skipCurrStep;
			skipCurrStep = false;
			
			if(skipCurrStep != oldState) {
				// enable the repaint mechanism of the algorithm views (the views are repainted automatically in setRepaintDisabled(...))
				final View[] views = AlgorithmRTE.this.getViews();
				if(views != null) {
					for(View view : views) {
						if(view != null)
							view.setRepaintDisabled(false);
					}
				}
			}
		}
		
		/**
		 * Processes the specified exercise.
		 * 
		 * @param exercise the exercise
		 * @param the related step or <code>null</code>
		 * @return the id of the next step
		 * @throws InterruptedException
		 * <ul>
		 * 		<li>if the processing is interrupted</li>
		 * </ul>
		 * @since 1.0
		 */
		private int processExercise(final AlgorithmExercise<?> exercise, final AlgorithmStep step) throws InterruptedException {
			final AlgorithmState oldState = stateHistory.peek();
			ExamResult examResult = ExamResult.FAILED;
			int nextStepID = -1;
			InterruptedException ex = null;
			
			// enter the processing of the exercise
			exercise.enter(this, step);
			
			try {
				do {
					// a solution is requested and entered by the user while the rte sleeps
					oldState.unfreeze();
					EDT.execute(new GuiJob() {
						
						@Override
						protected void execute() throws Throwable {
							exercise.beforeRequestSolution(oldState);
						}
					});
					
					// catch an InterruptedException from sleep so that we can execute the afterRequestSolution event
					try {
						// let the rte sleep for an undefinite period of time until the exercise wakes up the rte
						sleep();
						
						// if the user has input a solution for the exercise then skip the current execution to compare only
						// the results
						if(!exercise.isOmitted())
							enableSkipStepFlag();
					}
					catch(InterruptedException e) {
						ex = e;
					}
					
					// a solution is requested and entered by the user while the rte sleeps, the request ends before
					// the step is execute so that their could be removed objects that were added in beforeRequestSolution()
					final boolean omitted = exercise.isOmitted();
					EDT.execute(new GuiJob() {
						
						@Override
						protected void execute() throws Throwable {
							exercise.afterRequestSolution(omitted);
						}
					});
					
					// after executed the event rethrow the exception so that the further execution is skipped
					if(ex != null)
						throw ex;
					
					// a solution can only be applied if the exercise is not omitted
					if(exercise.getApplySolutionToAlgorithm() && !exercise.isOmitted()) {
						// firtsly check whether the solution is correct
						examResult = exercise.examine(oldState);
						
						// exercise succeeded?
						if(examResult == ExamResult.SUCCEEDED) {
							// transfer the solution of the user in a state so that this state can be adopted by the algorithm
							final AlgorithmState transferState = new AlgorithmState(plugin, executingStepID);
							exercise.transferSolution(transferState);
							
							// let the algorithm adopt the state
							AlgorithmRTE.this.adoptState(executingStepID, transferState);
							
							// only execute the step if the exercise was solved correct
							nextStepID = executeCurrentStep();
						}
					}
					else {
						// if the exercise does not applys a solution to the algorithm execute the current step
						// so that the exercise can request the algorithm state after this step or visualizes the way to solve the step
						nextStepID = executeCurrentStep();
					}
					
					// if the solution of the exercise is applied to the algorithm the examination is already done;
					// do further checking but only if the rte was not terminated during the execution of the current step
					if(!terminateRTE && !exercise.getApplySolutionToAlgorithm()) {
						// examine the exercise with the current state of the algorithm
						examResult = exercise.examine(requestState(executingStepID));
						
						// if the exercise failed the state before the step must be restored and rolled back so that the exercise
						// can be solved once again
						if(examResult == ExamResult.FAILED && !exercise.isOmitted()) {
							oldState.unfreeze();
							AlgorithmRTE.this.restoreState(oldState);
							AlgorithmRTE.this.rollBackStep(executingStepID, nextStepID);
						}
					}
					
					// reset the skip flag for a next run
					disableSkipStepFlag();
				} while(examResult != ExamResult.SUCCEEDED && !terminateRTE && !exercise.isOmitted());
			}
			catch(InterruptedException e) {
				throw e;
			}
			finally {
				// we always have to exit the exercise although the rte is interrupted because
				// the user stops the algorithm or something like that
				exercise.exit(examResult);
			}
			
			return nextStepID;
		}
		
		/**
		 * Checks if the rte should be paused and if so, transfers the rte in pause mode.
		 * 
		 * @return <code>true</code> if the rte was paused otherwise <code>false</code>
		 * @throws InterruptedException
		 * <ul>
		 * 		<li>if pause is interrupted by the thread meaning that the thread is interrupted and should be terminated</li>
		 * </ul>
		 * @since 1.0
		 */
		private boolean checkPause() throws InterruptedException {
			// in exercise mode pausing is not allowed because the execution flow is fully automatic
			if(exerciseModeEnabled || skipCurrStep || terminateRTE)
				return false;
			
			// to set the monitor in synchronized mode costs time so do this only if
        	// the flag is set
            if(paused) {
                synchronized(monitor) {
                	// attention: pause could be suppressed although the paused flag is set so
                	// the skip flag has to be checked each time too
                    while(paused && !skipCurrStep)
                    	monitor.wait();
                }
                
                return true;
            }
            
            return false;
		}
		
		/**
		 * Checks if the specified step has a breakpoint. If so then the rte is paused and must be resumed using {@link #work()}.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * If {@link #skipBreakpoints} is <code>true</code> then the method does nothing and returns.
		 * 
		 * @param stepID the step id
		 * @return <code>true</code> if the rte was paused because the current step had a breakpoint otherwise <code>false</code>
		 * @throws InterruptedException
		 * <ul>
		 * 		<li>if pause is interrupted by the thread meaning that the thread is interrupted and should be terminated</li>
		 * </ul>
		 * @since 1.0
		 */
		private boolean checkBreakpoint(final int stepID) throws InterruptedException {
			// in exercise mode breakpoints are not allowed because the execution flow is fully automatic
			if(exerciseModeEnabled || skipBreakpoints || terminateRTE)
				return false;
			
			final AlgorithmStep step = text.getStepByID(stepID);
			
			// step has a breakpoint? then go into pause mode
			if(step != null && step.hasBreakpoint())
				AlgorithmRTE.this.pause();
			
			return checkPause();
		}
		
		/**
		 * Lets the runtime environment sleep for an indefinite period of time.
		 * 
		 * @throws InterruptedException
		 * <ul>
		 * 		<li>if the runtime thread is interrupted</li>
		 * </ul>
		 * @since 1.0
		 */
		private void sleep() throws InterruptedException {
			if(skipCurrStep || terminateRTE)
				return;
			
			synchronized(monitor) {
				monitor.wait();
			}
		}
		
		/**
		 * Requests the current state of the algorithm.
		 * <br><br>
		 * <b>Notice</b>:<br>
		 * The state is frozen!
		 * 
		 * @param stepID the id of the current step
		 * @return the state
		 * @since 1.0
		 */
		private AlgorithmState requestState(final int stepID) {
			// save the current state of the algorithm
			final AlgorithmState state = new AlgorithmState(plugin, stepID);
			AlgorithmRTE.this.storeState(state);
			
			// very important: freeze the state against changes after the state is queried
			state.freeze();
			
			return state;
		}
		
		/**
	     * Stores the current algorithm state and pushes it to the history stack.
	     * 
	     * @param nextStepID the id of the next step which state is stored
	     * @since 1.0
	     */
	    private void pushStateHistory(final int nextStepID) {
			// save the current state of the algorithm add it to the stack
			stateHistory.push(requestState(nextStepID));
	    }
	    
	    /**
	     * Restores the algorithm state before the specified step and rolls back all steps up to the one that
	     * is restored.
	     * 
	     * @see AlgorithmRTE#rollBackStep(int)
	     * @param stepID the id of the step which predecessor state should be restored
	     * @param nextStepID the id of the next step
	     * @return the id of the step that has to be executed next
	     * @since 1.0
	     */
	    private int popStateHistory(final int stepID, final int nextStepID) {
	    	AlgorithmState state = null;
	    	boolean predStepFound = false;
	    	boolean currStepFound = false;
	    	int lastNextStepID = nextStepID;
	    	
	    	// find the step of the given step id and roll back all steps until the required step is found
	    	while(stateHistory.size() > 0 && !predStepFound) {
	    		state = stateHistory.pop();
	    		currStepFound = currStepFound || (state.getStepID() == stepID);
	    		// we have found the predecessor step of the current (given) step if the given step was found
	    		// and the current popped state has another id then the one from the given state
	    		predStepFound = currStepFound && (state.getStepID() != stepID);
	    		
    			state.unfreeze();
    			AlgorithmRTE.this.restoreState(state);
    			AlgorithmRTE.this.rollBackStep(state.getStepID(), lastNextStepID);
    			lastNextStepID = state.getStepID();
	    	}
	    	
	    	// no valid state then execute the first step next
	    	if(state == null)
	    		return text.getFirstStepID();
	    	else
	    		return state.getStepID();
	    }
	    
	    /**
	     * Executes the current step meaning the step with the id {@link #executingStepID}.
	     * <br><br>
	     * <b>Notice</b>:<br>
	     * This method invokes the {@link AlgorithmRTE#executeStep(int, AlgorithmStateAttachment)} method. If their occur an exception then this exception is logged
	     * and the {@link #terminateRTE} flag is set.
	     * 
	     * @return the step id of the next step that has to be executed or <code>-1</code> if the algorithm is finished
	     * @since 1.0
	     */
	    private int executeCurrentStep() {
			try {
				return AlgorithmRTE.this.executeStep(executingStepID, stateHistory.peek());
			}
			catch(Exception e) {
				AlgorithmRTE.this.writeLogMessage(AlgorithmRTE.this.plugin, "execution of step id " + executingStepID + " failed", e, LogType.ERROR);
				terminateRTE = true;
			}
			
			return -1;
	    }
	    
	    /**
	     * Gets the exercise of the step that is currently in execution.
	     * <br><br>
	     * <b>Notice</b>:<br>
	     * The method returns <code>null</code> automatically if the exercise mode is not enabled!
	     * 
	     * @return the exercise of the current step or <code>null</code> if there is no exercise
	     * @since 1.0
	     */
	    private AlgorithmExercise<?> getCurrentExercise() {
	    	// if the rte is not in exercise mode then the current step must not be checked for a valid exercise
	    	if(!exerciseModeEnabled)
	    		return null;
	    	
	    	final AlgorithmStep step = text.getStepByID(executingStepID);
	    	return (step != null) ? step.getExercise() : null;
	    }
	    
	    /**
	     * Indicates that the runtime job is done meaning that the rte is reset and {@link AlgorithmRTE#onStop()}
	     * is invoked.
	     * 
	     * @since 1.0
	     */
	    private void done() {
	    	started = false;
	    	skipCurrStep = false;
	    	
			// release the state history because the algorithm is finished
			stateHistory.clear();
	        // currently no step is in execution
	        text.setExecutingStepID(INITIALSTATE_STEPID);
			
			// important: reset the algorithm runtime environment
			AlgorithmRTE.this.onStop();
	    }
    }
    
}