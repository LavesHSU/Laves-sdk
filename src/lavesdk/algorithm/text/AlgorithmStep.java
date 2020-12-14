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
 * Class:		AlgorithmStep
 * Task:		A step inside a paragraph
 * Created:		09.09.13
 * LastChanges:	30.03.15
 * LastAuthor:	jdornseifer
 */

package lavesdk.algorithm.text;

import java.util.ArrayList;
import java.util.List;

import lavesdk.algorithm.AlgorithmExercise;
import lavesdk.algorithm.text.exceptions.InvalidIdentifierException;
import lavesdk.algorithm.text.exceptions.InvalidLaTeXFormulaException;
import lavesdk.utils.FileUtils;

import org.scilab.forge.jlatexmath.ParseException;

/**
 * Represents a step inside of an {@link AlgorithmParagraph}.
 * <br><br>
 * A step is a part of the algorithm with a text that describes what is done. A step can contain one or multiple (e.g. a loop)
 * instructions that is/are done by the algorithm. Furthermore the text can contain latex expressions (<i>see section about LaTeX</i>)
 * and line breaks (if necessary).<br>
 * The steps of an {@link AlgorithmParagraph} are executed consecutively.
 * <br><br>
 * <b>LaTeX</b>:<br>
 * The text of a step can contain latex formulas. Mark a formula with the prefix <code>_latex{</code>
 * and the postfix <code>}</code>, like <code>"This is a fraction: _latex{\\frac{1}{2}}."</code>.
 * <br><br>
 * You can integrate parameters (optional) by using the parameter brackets <code>(</code> and <code>)</code> behind <code>_latex</code>, like
 * <code>"This is a fraction: _latex(-2){\\frac{1}{2}}."</code>. The use of the parameters is contextual and must not have an effect.
 * <br><br>
 * <b>Line breaks</b>:<br>
 * You can integrate line breaks into the step text by using the direct line break escape character "\n" like <code>new AlgorithmStep(p, "This is my\nstep text!", 1);</code>.
 * Keep in mind that you cannot use this sequence in latex formulas. There you have to use the latex specific line break sequence.
 * <br><br>
 * <b>Indent</b>:<br>
 * You can specify a text indent when you create the step with {@link #AlgorithmStep(AlgorithmParagraph, String, int, int)}. If the text of a step has
 * an indent not equal <code>0</code> the text is separated off the other steps and is indented by the specified factor. This makes it possible to stagger an algorithm text.<br>
 * <u>Example</u>:
 * <pre>
 * This is the text of step 1 --> Indent=0
 *   This is the text of step 2 --> Indent=2
 *  This is the text of step 3 --> Indent=1
 * ...
 * </pre>
 * <b>Breakpoint</b>:<br>
 * Each step can have a breakpoint. Use {@link #setBreakpoint(boolean)} to specify if the step should have a breakpoint. If a step has a
 * breakpoint then the runtime environment of the algorithm pauses and the user has to resume it.
 * <br><br>
 * <b>Exercises</b>:<br>
 * Each step can be associated with an {@link AlgorithmExercise} that the user has to solve when the step is entered. Use {@link #setExercise(AlgorithmExercise)}
 * to specify an exercise of the step.
 * <br><br>
 * <b>Annotations</b>:<br>
 * You can annotate a step by setting an {@link Annotation} object using {@link #setAnnotation(Annotation)}. You can use annotations to explain an
 * algorithm step more exact.
 * <br><br>
 * Use {@link #getText()} to get the specified text and {@link #getFormula(int)} to get a latex formula
 * of the step.
 * <br><br>
 * To get a tokenized view of the text, iterate over the {@link TextToken}s by using {@link #getTextTokenCount()} and {@link #getTextToken(int)}.
 * 
 * @see AlgorithmText
 * @see AlgorithmParagraph
 * @see LaTeXFormula
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class AlgorithmStep {
	
	/** the paragraph to which the step belongs */
	private final AlgorithmParagraph paragraph;
	/** the original text of the step */
	private final String text;
	/** the list of latex formulas inside the text of the step */
	private final List<LaTeXFormula> formulas;
	/** a {@link AlgorithmText}-wide identifier for the step */
	private final int id;
	/** the indent of the text */
	private final int indent;
	/** flag that indicates if the step has a breakpoint */
	private boolean hasBreakpoint;
	/** the corresponding exercise of the step or <code>null</code> if the step does not have an exercise */
	private AlgorithmExercise<?> exercise;
	/** the annotation of the step or <code>null</code> if the step does not have an annotation */
	private Annotation annotation;
	/** the list of {@link TextToken}s */
	private final List<TextToken> textTokens;
	
	/** the function for a latex formula */
	private static final String LATEX_FORMULA_FUNC = "_latex";
	/** the symbol for the beginning character of a latex formula */
	private static final String LATEX_FORMULA_BEGIN = "{";
	/** the symbol for the end of a latex formula */
	private static final String LATEX_FORMULA_END = "}";
	/** the symbol for the open bracket of the parameters */
	private static final String LATEX_FUNC_PARAM_OPEN = "(";
	/** the symbol for the close bracket of the parameters */
	private static final String LATEX_FUNC_PARAM_CLOSE = ")";
	/** the beginning tag of the replace sequence for latex functions which encloses the index of the formula (<b>may not contain spaces</b>) */
	private static final String LATEX_TAG_BEGIN = "_lF(";
	/** the end tag of the replace sequence for latex functions which encloses the index of the formula (<b>may not contain spaces</b>) */
	private static final String LATEX_TAG_END = ")_";
	
	/**
	 * Creates a new step.
	 * 
	 * @param paragraph the corresponding paragraph
	 * @param text the text of the step (<b>can contain latex formulas and line breaks</b>)
	 * @param id the identifier of the step
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if paragraph is null</li>
	 * 		<li>if text is null</li>
	 * 		<li>if id is <code>< 1</code></li>
	 * </ul>
	 * @throws InvalidIdentifierException
	 * <ul>
	 * 		<li>if the id of the step is already existing</li>
	 * </ul>
	 * @throws InvalidLaTeXFormulaException
	 * <ul>
	 * 		<li>if the text contains an invalid latex expression</li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmStep(final AlgorithmParagraph paragraph, final String text, final int id) throws IllegalArgumentException, InvalidIdentifierException, InvalidLaTeXFormulaException {
		this(paragraph, text, id, 0);
	}
	
	/**
	 * Creates a new step.
	 * 
	 * @param paragraph the corresponding paragraph
	 * @param text the text of the step (<b>can contain latex formulas and line breaks</b>)
	 * @param id the identifier of the step
	 * @param indent the indent of the text of the step (the default value is <code>0</code>)
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if paragraph is null</li>
	 * 		<li>if text is null</li>
	 * 		<li>if id is <code>< 1</code></li>
	 * 		<li>if indent is <code>< 0</code></li>
	 * </ul>
	 * @throws InvalidIdentifierException
	 * <ul>
	 * 		<li>if the id of the step is already existing</li>
	 * </ul>
	 * @throws InvalidLaTeXFormulaException
	 * <ul>
	 * 		<li>if the text contains an invalid latex expression</li>
	 * </ul>
	 * @since 1.0
	 */
	public AlgorithmStep(final AlgorithmParagraph paragraph, final String text, final int id, final int indent) throws IllegalArgumentException, InvalidIdentifierException, InvalidLaTeXFormulaException {
		if(paragraph == null || text == null || id < 1 || indent < 0)
			throw new IllegalArgumentException("No valid argument!");
		
		this.paragraph = paragraph;
		this.text = text;
		this.formulas = new ArrayList<LaTeXFormula>(3);
		this.id = id;
		this.indent = indent;
		this.hasBreakpoint = false;
		this.exercise = null;
		this.annotation = null;
		this.textTokens = new ArrayList<TextToken>();
		
		// add step to paragraph
		paragraph.addStep(this);
		
		// tokenize the step text
		createTokenizedText();
	}
	
	/**
	 * Gets the associated paragraph.
	 * 
	 * @return the paragraph to which this step belongs
	 * @since 1.0
	 */
	public final AlgorithmParagraph getParagraph() {
		return paragraph;
	}
	
	/**
	 * Gets the text of the step, that means the original text with all latex formulas and so on.
	 * <br><br>
	 * <b>Latex formulas</b>:<br>
	 * A step text can contain latex formulas. Mark a formula with the prefix <code>_latex{</code>
	 * and the postfix <code>}</code>, like <code>String formula = "This is a fraction: _latex{\\frac{1}{2}}."</code>.
	 * 
	 * @return the original text
	 * @since 1.0
	 */
	public final String getText() {
		return text;
	}
	
	/**
	 * Gets the number of tokens of the text.
	 * <br><br>
	 * A text token is either a sequence of text which covers at the most a word, its punctuation characters and the surrounding whitespace
	 * characters or a latex formula.<br>
	 * 
	 * @return the amount of tokens the text has
	 * @since 1.0
	 */
	public final int getTextTokenCount() {
		return textTokens.size();
	}
	
	/**
	 * Gets a token of the text at a specific index.
	 * <br><br>
	 * A text token is either a sequence of text which covers at the most a word, its punctuation characters and the surrounding whitespace
	 * characters or a latex formula.<br>
	 * 
	 * @param index the index
	 * @return the token
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getTextTokenCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public final TextToken getTextToken(final int index) throws IndexOutOfBoundsException {
		return textTokens.get(index);
	}
	
	/**
	 * Gets the number of formulas that the step text contain.
	 * 
	 * @return the number of formulas
	 * @since 1.0
	 */
	public int getFormulaCount() {
		return formulas.size();
	}
	
	/**
	 * Gets the formula at the given index.
	 * 
	 * @see LaTeXFormula
	 * @param index the index
	 * @return the formula
	 * @throws IndexOutOfBoundsException
	 * <ul>
	 * 		<li>if the index is out of range (<code>index < 0 || index >= getFormulaCount()</code>)</li>
	 * </ul>
	 * @since 1.0
	 */
	public LaTeXFormula getFormula(final int index) throws IndexOutOfBoundsException {
		return formulas.get(index);
	}
	
	/**
	 * Gets the identifier of the step.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The first step has an id of <code>0</code>.
	 * 
	 * @return the identifier
	 * @since 1.0
	 */
	public final int getID() {
		return id;
	}
	
	/**
	 * Gets the indent of the text of the step.
	 * <br><br>
	 * If the text of a step has an indent not equal <code>0</code> the text is separated off the other steps and is indented by the specified factor.
	 * This makes it possible to stagger an algorithm text.<br>
	 * <u>Example</u>:
	 * <pre>
	 * This is the text of step 1 --> Indent=0
	 *   This is the text of step 2 --> Indent=2
	 *  This is the text of step 3 --> Indent=1
	 * ...
	 * </pre>
	 * 
	 * @return the indent
	 * @since 1.0
	 */
	public final int getIndent() {
		return indent;
	}
	
	/**
	 * Indicates if the step has currently a breakpoint.
	 * <br><br>
	 * If a step has a breakpoint then the runtime environment of the algorithm stops (pauses) at this step
	 * until the user resumes the execution.
	 * 
	 * @return <code>true</code> if the step has a breakpoint otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean hasBreakpoint() {
		return hasBreakpoint;
	}
	
	/**
	 * Sets if the step has currently a breakpoint.
	 * <br><br>
	 * If a step has a breakpoint then the runtime environment of the algorithm stops (pauses) at this step
	 * until the user resumes the execution.
	 * 
	 * @param breakpoint <code>true</code> if the step has a breakpoint otherwise <code>false</code>
	 * @since 1.0
	 */
	public void setBreakpoint(final boolean breakpoint) {
		hasBreakpoint = breakpoint;
	}
	
	/**
	 * Gets the exercise that is associated with the step.
	 * <br><br>
	 * If the step is entered the exercise is presented to the user and he has to solve it.
	 * 
	 * @see AlgorithmExercise
	 * @return the exercise or <code>null</code> if the step does not have an exercise
	 * @since 1.0
	 */
	public AlgorithmExercise<?> getExercise() {
		return exercise;
	}
	
	/**
	 * Sets the exercise that is associated with the step.
	 * <br><br>
	 * If the step is entered the exercise is presented to the user and he has to solve it.
	 * 
	 * @see AlgorithmExercise
	 * @see AlgorithmText#setFinalExercise(AlgorithmExercise)
	 * @param exercise the exercise or <code>null</code> if the step should not have an exercise
	 * @since 1.0
	 */
	public void setExercise(final AlgorithmExercise<?> exercise) {
		this.exercise = exercise;
	}
	
	/**
	 * Gets the annotation of the step.
	 * 
	 * @see Annotation
	 * @return the annotation or <code>null</code> if the step does not have an annotation
	 * @since 1.0
	 */
	public Annotation getAnnotation() {
		return annotation;
	}
	
	/**
	 * Sets the annotation of the step.
	 * 
	 * @see Annotation
	 * @param annotation the annotation or <code>null</code> if the step should not have an annotation
	 * @since 1.0
	 */
	public void setAnnotation(final Annotation annotation) {
		this.annotation = annotation;
	}
	
	@Override
	public String toString() {
		return text;
	}
	
	/**
	 * Creates tokens for the <b>text of the step</b>. A token is a word of the text which retains the spaces and punctuation characters
	 * at the end (or if necessary at the beginning), a <b>latex formula</b> or a <b>line break</b>.
	 * 
	 * @throws InvalidLaTeXFormulaException
	 * <ul>
	 * 		<li>if the text contains an invalid latex expression</li>
	 * </ul>
	 * @since 1.0
	 */
	private void createTokenizedText() throws InvalidLaTeXFormulaException {
		char c;
		char nextC;
		String token = "";
		int ltEndIndex;
		final List<String> tokens = new ArrayList<String>();
		boolean hasNonWhitespaceCharacter = false;
		
		textTokens.clear();
		
		// parse latex expressions and replace the os specific line breaks by the escape sequence for easier tokenization
		final String text = parseFormulas(this.text).replaceAll(FileUtils.LINESEPARATOR, "\n");
		
		// tokenize the text
		for(int i = 0; i < text.length(); i++) {
			// get current and next character
			c = text.charAt(i);
			nextC = (i < text.length() - 1) ? text.charAt(i + 1) : ' ';
			
			// a token is a line break, a latex formula or a sequence of text (this sequence of text can contain a start
			// sequence and/or an end sequence of whitespace characters
			if(c == '\n') {
				// do we have already text? then firstly create this token
				if(!token.isEmpty())
					tokens.add(token);
				// create the line break token
				tokens.add("\n");
				
				// start a new token
				token = "";
				hasNonWhitespaceCharacter = false;
			}
			else if(Character.isWhitespace(c) && !Character.isWhitespace(nextC) && hasNonWhitespaceCharacter) {
				// the text sequence ends if we encounter of a whitespace followed by a non whitespace
				tokens.add(token + c);
				token = "";
				hasNonWhitespaceCharacter = false;
			}
			else if(c == LATEX_TAG_BEGIN.charAt(0) && text.indexOf(LATEX_TAG_BEGIN, i) >= 0) {
				// find the end of the formula tag
				ltEndIndex = text.indexOf(LATEX_TAG_END, i);
				
				if(ltEndIndex < 0)
					throw new ParseException("text \"" + text + "\" cannot be divided into token at position " + i + " (no valid latex tag)");
				
				// there is already an token then save it first
				if(!token.isEmpty())
					tokens.add(token);
				
				// extract the formula tag and add the formula token
				token = text.substring(i, ltEndIndex + LATEX_TAG_END.length());
				tokens.add(token);
				
				// start a new token
				token = "";
				hasNonWhitespaceCharacter = false;
				
				// update the current character index at the end of the formula
				i = ltEndIndex + LATEX_TAG_END.length() - 1;
			}
			else {
				// expand the token by the current character 
				token += c;
				hasNonWhitespaceCharacter = hasNonWhitespaceCharacter || !Character.isWhitespace(c);
			}
		}
		
		// we have still a token? then add this last token
		if(!token.isEmpty())
			tokens.add(token);
		
		int formulaIndex;
		
		// create text tokens
		for(String t : tokens) {
			if(t.contains("\n")) {
				// it is a line break token
				textTokens.add(new TextToken(TextTokenType.LINEBREAK, null, null));
			}
			else if(t.startsWith(LATEX_TAG_BEGIN) && t.endsWith(LATEX_TAG_END)) {
				// it is a formula token then extract the index of the formula and add the token
				try {
					formulaIndex = new Integer(t.substring(LATEX_TAG_BEGIN.length(), t.length() - LATEX_TAG_END.length()));
					textTokens.add(new TextToken(TextTokenType.FORMULA, null, getFormula(formulaIndex)));
				}
				catch(NumberFormatException | IndexOutOfBoundsException e) {
					// cannot parse the formula index? then there is something wrong -> add it as text token to make this visible
					textTokens.add(new TextToken(TextTokenType.STRING, t, null));
				}
			}
			else {
				// it is a string token
				textTokens.add(new TextToken(TextTokenType.STRING, t, null));
			}
		}
	}
	
	/**
	 * Parses the given text and generates latex formulas for each latex expression.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * The latex formulas are replaced by {@value #LATEX_TAG_BEGIN} <code>formula index</code> {@value #LATEX_TAG_END}.
	 * 
	 * @see #LATEX_FORMULA_BEGIN
	 * @see #LATEX_FORMULA_END
	 * @param text the text
	 * @return the text without the latex formulas (replaced by the latex tags, see {@link #LATEX_TAG_BEGIN}/{@link #LATEX_TAG_END})
	 * @throws ParseException
	 * <ul>
	 * 		<li>if the text contains an invalid latex expression</li>
	 * </ul>
	 * @since 1.0
	 */
	private String parseFormulas(final String text) throws InvalidLaTeXFormulaException {
		StringBuilder s = new StringBuilder(text);
		String formula;
		String parameters;
		int latexFuncIndex = -1;
		int latexScopeBeginIndex;
		int latexScopeEndIndex;
		int innerScopeBeginIndex;
		
		formulas.clear();
		
		while(true) {
			// find the first occurrence of a latex formula in the text
			latexFuncIndex = s.indexOf(LATEX_FORMULA_FUNC, latexFuncIndex);
			
			// no latex symbol found? then break up!
			if(latexFuncIndex < 0)
				break;
			
			// find the beginning of the latex formula scope
			latexScopeBeginIndex = s.indexOf(LATEX_FORMULA_BEGIN, latexFuncIndex + LATEX_FORMULA_FUNC.length());
			// find first closing bracket
			latexScopeEndIndex = s.indexOf(LATEX_FORMULA_END, latexFuncIndex);
			innerScopeBeginIndex = latexScopeBeginIndex + LATEX_FORMULA_BEGIN.length();
			
			// filter all inner brackets of the same character, that means if you have "_latex{\frac{1}{2}}" then
			// we must find the last closing bracket and filter the ones that are elements of the latex formula
			while((innerScopeBeginIndex = s.indexOf(LATEX_FORMULA_BEGIN, innerScopeBeginIndex + 1)) < latexScopeEndIndex && innerScopeBeginIndex >= 0)
				latexScopeEndIndex = s.indexOf(LATEX_FORMULA_END, latexScopeEndIndex + 1);
			
			// no valid closing bracket? then break up!
			if(latexScopeEndIndex < 0)
				break;
			
			// extract the parameters of the function
			parameters = s.substring(latexFuncIndex + LATEX_FORMULA_FUNC.length(), latexScopeBeginIndex).trim();
			// parameters have to be enclosed by the open and close bracket
			if(parameters.startsWith(LATEX_FUNC_PARAM_OPEN) && parameters.endsWith(LATEX_FUNC_PARAM_CLOSE))
				parameters = parameters.substring(LATEX_FUNC_PARAM_OPEN.length(), parameters.length() - LATEX_FUNC_PARAM_CLOSE.length());
			else
				parameters = "";
			
			// extract the formula
			formula = s.substring(latexScopeBeginIndex + LATEX_FORMULA_BEGIN.length(), latexScopeEndIndex - LATEX_FORMULA_BEGIN.length() + 1);
			
			// create a new formula and add the formula to the list
			formulas.add(new LaTeXFormula(this, formula, parameters));
			
			// after that (!) replace the function by its tag
			s.replace(latexFuncIndex, latexScopeEndIndex + 1, LATEX_TAG_BEGIN + (formulas.size() - 1) + LATEX_TAG_END);
		}
		
		return s.toString();
	}
	
	/**
	 * The type of a {@link TextToken}.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public enum TextTokenType {
		
		/** token is a string */
		STRING,
		
		/** token is a latex formula */
		FORMULA,
		
		/** token is a line break */
		LINEBREAK
		
	}
	
	/**
	 * Represents a token of the text of an {@link AlgorithmStep}.
	 * <br><br>
	 * A text token is either a sequence of text (string) which covers at the most a word, its punctuation characters and the surrounding whitespace
	 * characters or a latex formula.
	 * <br><br>
	 * To identify a token use {@link #type}.
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public class TextToken {
		
		/** the type of the token */
		public final TextTokenType type;
		/** the text of the token or <code>null</code> if the token does not consists of a string */
		public final String string;
		/** the formula of the token or <code>null</code> if the token does not consists of a formula */
		public final LaTeXFormula formula;
		
		/**
		 * Creates a new text token.
		 * <br><br>
		 * <b>Attention</b>:<br>
		 * This is only possible from the inside of the LAVESDK or more precisely from the inside of this class!<br>
		 * <i>DO NOT REMOVE THE PRIVATE VISIBILITY OF THIS CONSTRUCTOR</i>!
		 * 
		 * @param type the type
		 * @param string the text or <code>null</code> if it is not a string token
		 * @param formula the formula or <code>null</code> if it is not a formula token
		 * @throws IllegalArgumentException
		 * <ul>
		 * 		<li>if type is null</li>
		 * 		<li>if text is null but its a string token</li>
		 * 		<li>if formula is null but its a formula token</li>
		 * </ul>
		 */
		private TextToken(final TextTokenType type, final String string, final LaTeXFormula formula) throws IllegalArgumentException {
			if(type == null || (type == TextTokenType.STRING && string == null) || (type == TextTokenType.FORMULA && formula == null))
				throw new IllegalArgumentException("No valid argument!");
			
			this.type = type;
			this.string = string;
			this.formula = formula;
		}
		
		@Override
		public String toString() {
			if(type == TextTokenType.STRING)
				return string;
			else
				return formula.toString();
		}
		
	}

}
