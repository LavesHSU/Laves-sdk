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
 * Class:		SolveExercisePane
 * Task:		Represents a dialog to solve algorithm exercises
 * Created:		26.03.14
 * LastChanges:	02.04.14
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.dialogs;

import lavesdk.algorithm.AlgorithmExercise;
import lavesdk.algorithm.plugin.PluginHost;
import lavesdk.gui.dialogs.SolveExerciseDialog.SolutionEntry;
import lavesdk.language.LanguageFile;

/**
 * Provides methods to show a {@link SolveExerciseDialog} in an easy way.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class SolveExercisePane {
	
	private SolveExercisePane() {
	}
	
	/**
	 * Shows the dialog to solve an exercise by requesting a user input.
	 * 
	 * @param host the host that is used to center the dialog in the application or <code>null</code> (centers the dialog in the screen)
	 * @param exercise the exercise that should be solved
	 * @param entries the entries where the user has to enter or select the solutions
	 * @return <code>true</code> if the user has clicked ok otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if exercise is null</li>
	 * 		<li>if entries is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static boolean showDialog(final PluginHost host, final AlgorithmExercise<?> exercise, final SolutionEntry<?>[] entries) throws IllegalArgumentException {
		return showDialog(host, exercise, entries, null);
	}
	
	/**
	 * Shows the dialog to solve an exercise by requesting a user input.
	 * 
	 * @param host the host that is used to center the dialog in the application or <code>null</code> (centers the dialog in the screen)
	 * @param exercise the exercise that should be solved
	 * @param entries the entries where the user has to enter or select the solutions
	 * @param inputHint a hint for the user how to format the input
	 * @return <code>true</code> if the user has clicked ok otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if exercise is null</li>
	 * 		<li>if entries is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static boolean showDialog(final PluginHost host, final AlgorithmExercise<?> exercise, final SolutionEntry<?>[] entries, final String inputHint) throws IllegalArgumentException {
		return showDialog(host, exercise, entries, null, null, inputHint);
	}
	
	/**
	 * Shows the dialog to solve an exercise by requesting a user input.
	 * 
	 * @param host the host that is used to center the dialog in the application or <code>null</code> (centers the dialog in the screen)
	 * @param exercise the exercise that should be solved
	 * @param entries the entries where the user has to enter or select the solutions
	 * @param langFile the language file or <code>null</code> if the dialog should not use language dependent labels for the title, the ok button and the cancel button (in this case the predefined labels are shown)
	 * @param langID the language id
	 * @return <code>true</code> if the user has clicked ok otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if exercise is null</li>
	 * 		<li>if entries is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static boolean showDialog(final PluginHost host, final AlgorithmExercise<?> exercise, final SolutionEntry<?>[] entries, final LanguageFile langFile, final String langID) throws IllegalArgumentException {
		return showDialog(host, exercise, entries, langFile, langID, null);
	}
	
	/**
	 * Shows the dialog to solve an exercise by requesting a user input.
	 * <br><br>
	 * The dialog displays the specified {@link SolutionEntry}s one below the other like:
	 * <pre>
	 * Label Entry 1 | Component Entry 1
	 * ---------------------------------
	 * Label Entry 2 | Component Entry 2
	 * ---------------------------------
	 * Label Entry 3 | Component Entry 3
	 * ---------------------------------
	 * ...
	 * </pre>
	 * <b>Example</b>: The user should enter two sets A and B.<br>
	 * First we have to define the entries where the user should enter the solutions:
	 * <pre>
	 * final SolutionEntry&lt;JTextField&gt; entryA = new SolutionEntry&lt;JTextField&gt;("A:", new JTextField());
	 * final SolutionEntry&lt;JTextField&gt; entryB = new SolutionEntry&lt;JTextField&gt;("B:", new JTextField());
	 * </pre>
	 * After that we create the array of entries:
	 * <pre>
	 * final SolutionEntry&lt;?&gt;[] entries = new SolutionEntry&lt;?&gt;[] { entryA, entryB };
	 * </pre>
	 * Finally we display the dialog to the user:
	 * <pre>
	 * if(SolveExercisePane.showDialog(myExercise, entries, myLangFile, myLangID)) {
	 *     System.out.println("The solution the user has entered:");
	 *     System.out.println("A: " + entryA.getComponent().getText());
	 *     System.out.println("B: " + entryB.getComponent().getText());
	 * }
	 * else
	 *     System.out.println("The user canceled the dialog!");
	 * </pre>
	 * 
	 * @param host the host that is used to center the dialog in the application or <code>null</code> (centers the dialog in the screen)
	 * @param exercise the exercise that should be solved
	 * @param entries the entries where the user has to enter or select the solutions
	 * @param langFile the language file or <code>null</code> if the dialog should not use language dependent labels for the title, the ok button and the cancel button (in this case the predefined labels are shown)
	 * @param langID the language id
	 * @param inputHint a hint for the user how to format the input
	 * @return <code>true</code> if the user has clicked ok otherwise <code>false</code>
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if exercise is null</li>
	 * 		<li>if entries is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public static boolean showDialog(final PluginHost host, final AlgorithmExercise<?> exercise, final SolutionEntry<?>[] entries, final LanguageFile langFile, final String langID, final String inputHint) throws IllegalArgumentException {
		final SolveExerciseDialog dlg = new SolveExerciseDialog(host, exercise, entries, inputHint, langFile, langID);
		
		// show the dialog
		dlg.setVisible(true);
		
		return !dlg.isCanceled();
	}

}
