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

package lavesdk.language;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import lavesdk.utils.FileUtils;

/**
 * Loads and represents a language file that contains label identifiers and corresponding label descriptions in specified languages.
 * <br><br>
 * <b>Label identifiers</b>:<br>
 * A label id is marked with a <code>$</code> and is a so called wildcard for a language dependent label.<br>
 * Example:
 * <pre>
 * $FILE
 * #de = Datei
 * #en = File
 * </pre>
 * To get the description of a label in a specific language, use {@link #getLabel(String, String)}.
 * <br>
 * A language file can contain (<i>must not</i>) the <code>$LANGUAGES</code> label. Under this label you can specify
 * the languages and their description the language file contains, like:
 * <pre>
 * $LANGUAGES
 * #de = Deutsch
 * #en = English
 * </pre>
 * To get all available languages in this language file, use {@link #getAvailableLanguages()}.
 * <br><br>
 * <b>Line breaks</b>:<br>
 * Because a label in a language file can not include line breaks you can integrate line breaks manually in the description
 * of a label by using the sequence <code>%n</code> like
 * <pre>
 * $HELLO_WORLD_LABEL
 * #de = Hallo%nWelt!
 * #en = Hello%nWorld!
 * </pre>
 * <b>Language identifiers</b>:<br>
 * With language identifiers you can specify several descriptions in different languages for one label. A language id starts
 * with <code>#</code> followed by the id string. With the assignment operator <code>=</code> you can assign a language
 * specific description of the label.<br>
 * For examples of language identifiers see above!
 * <br><br>
 * <b>Comments</b>:<br>
 * A language file can contain comments. Every line that starts with <code>//</code> is marked as a comment and is skipped.
 * But beware, a comment can not start behind a label id or something else, that means the whole line is a comment or nothing!
 * <br><br>
 * <b>Charset</b>:<br>
 * It is recommended to use an unicode charset in a language file so that it is possible to display several languages. You have to consider
 * two things when you use another charset as the default one like UTF-8, UTF-16, etc.:
 * <ul>
 * 		<li>specify the encoding of your language file using the <code>charset</code> parameter of a constructor (by default the charset
 * 			is <code>UTF-8</code></li>
 * 		<li><b>update the text file encoding property of your project</b> (in Eclipse this can be done in <code>Project -> Properties -> Resources</code>,
 * 			enable <code>Other</code> and select the corresponding encoding</li>
 * </ul> 
 * <br><br>
 * <b>Example file</b>:
 * <pre>
 * // This is a language file of software XYZ.
 * // Copyright (c) 2013, XYZ Company
 * 
 * $LANGUAGES
 * #en = English
 * #de = Deutsch
 * 
 * $FILE
 * #en = File
 * #de = Datei
 * 
 * $FILE_NEW
 * #en = New...
 * #de = Neu...
 * 
 * $FILE_SAVE
 * #en = Save
 * #de = Speichern
 * 
 * $FILE_SAVE_AS
 * #en = Save as...
 * #de = Speichern unter...
 * 
 * ...
 * </pre>
 * <b>Including other files</b>:<br>
 * Use {@link #include(LanguageFile)} to include another language file to this one.
 * <br><br>
 * <b>Tip</b>:<br>
 * If you cannot guarantee that you have a valid language file use the static method {@link #getLabel(LanguageFile, String, String, String)}
 * to load label descriptions.
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class LanguageFile {
	
	/** data structure of the language labels with label id as key and a list of entries as value */
	private final Map<String, List<LabelEntry>> labels;
	/** the list of available languages in this language file if the corresponding label {@link #LANGUAGES_LABEL} exists */
	private final List<LabelEntry> languages;
	
	/** token of a comment that means, starts a line with this token then the line is identified as a comment */
	private static final String COMMENT_TOKEN = "//";
	/** token of a language label that means, starts a line with this token then the line is identified as a label */
	private static final String LABELID_TOKEN = "$";
	/** token of a language id that means, starts a line with this token then the line is identified as a language id */
	private static final String LANGID_TOKEN = "#";
	/** token of a line break meaning if a label contains this token then it is converted into a platform independent line break (remember that this has to be a regex pattern so each "\" has to be escaped) */
	private static final String LINEBREAK_TOKEN = "%n";
	/** token of the assignment operator for label descriptions to the specific language */
	private static final String ASSIGNEMENTOPERATOR_TOKEN = "=";
	
	/** predefined label that identifies the available languages and their description in this language file */
	private static final String LANGUAGES_LABEL = "LANGUAGES";
	/** platform independent line break */
	private static final String LINEBREAK = FileUtils.LINESEPARATOR;
	/** pattern to trim the whitespace at the left side of a string */
	private final static Pattern LTRIM = Pattern.compile("^\\s+");
	
	/**
	 * Loads a new language file.
	 * 
	 * @param filename the file path and name of the language file
	 * @throws IOException
	 * <ul>
	 * 		<li>if the language file could not be read</li>
	 * </ul>
	 * @since 1.0
	 */
	public LanguageFile(final String filename) throws IOException {
		this(new File(filename));
	}
	
	/**
	 * Loads a new language file.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * By default the charset is <code>UTF-8</code>. Use {@link #LanguageFile(File, String)} to specify another one.
	 * 
	 * @param file the language file
	 * @throws IOException
	 * <ul>
	 * 		<li>if the language file could not be read</li>
	 * </ul>
	 * @since 1.0
	 */
	public LanguageFile(final File file) throws IOException {
		this(new FileInputStream(file), "UTF-8");
	}
	
	/**
	 * Loads a new language file.
	 * 
	 * @param file the language file
	 * @param charset the charset of the language file
	 * @throws IOException
	 * <ul>
	 * 		<li>if the language file could not be read</li>
	 * 		<li>if the charset is null</li>
	 * 		<li>if the charset is empty</li>
	 * </ul>
	 * @since 1.0
	 */
	public LanguageFile(final File file, final String charset) throws IOException {
		this(new FileInputStream(file), charset);
	}
	
	/**
	 * Loads a new language file.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * By default the charset is <code>UTF-8</code>. Use {@link #LanguageFile(InputStream, String)} to specify another one.
	 * 
	 * @param stream the language file stream
	 * @throws IOException
	 * <ul>
	 * 		<li>if the language file stream could not be read</li>
	 * </ul>
	 * @since 1.0
	 */
	public LanguageFile(final InputStream stream) throws IOException {
		this(stream, "UTF-8");
	}
	
	/**
	 * Loads a new language file.
	 * 
	 * @param stream the language file stream
	 * @param charset the charset of the language file
	 * @throws IOException
	 * <ul>
	 * 		<li>if the language file stream could not be read</li>
	 * 		<li>if the charset is null</li>
	 * 		<li>if the charset is empty</li>
	 * </ul>
	 * @since 1.0
	 */
	public LanguageFile(final InputStream stream, final String charset) throws IOException {
		if(stream == null || charset == null || charset.isEmpty())
			throw new IOException("input stream is null");
		
		final InputStreamReader isr = new InputStreamReader(stream, charset);
		final BufferedReader reader = new BufferedReader(isr);
		String line;
		String data;
		String currLabelID = null;
		String currLangID;
		String currLabelDesc;
		List<LabelEntry> currLabelEntries = null;
		boolean langLabelRead = false;
		
		// create new data structure for labels
		labels = new HashMap<String, List<LabelEntry>>();
		languages = new ArrayList<LabelEntry>();
		
		try {
			while((line = reader.readLine()) != null) {
				// delete whitespace characters at the beginning of the line
				data = ltrim(line);
				
				// skip this line if it is a comment or empty
				if(data.startsWith(COMMENT_TOKEN) || data.isEmpty())
					continue;
				
				// new label?
				if(data.startsWith(LABELID_TOKEN)) {
					// extract the label id name which follows the token character
					currLabelID = data.substring(1, data.length());
					
					if(!currLabelID.equalsIgnoreCase(LANGUAGES_LABEL)) {
						currLabelEntries = new ArrayList<LabelEntry>();
						labels.put(currLabelID, currLabelEntries);
					}
					else {
						// the LANGUAGES label can only be specified once
						currLabelEntries = !langLabelRead ? languages : null;
						langLabelRead = true;
					}
				}
				else if(data.startsWith(LANGID_TOKEN) && currLabelEntries != null) {
					final int assignmentOpPos = data.indexOf(ASSIGNEMENTOPERATOR_TOKEN);
					
					// no assignment operator?
					if(assignmentOpPos < 1)
						continue;
					
					// extract the language id (example #en = .... -> en)
					currLangID = data.substring(1, assignmentOpPos - 1).trim();
					// extract the description, remove left whitespaces and replace all line breaks into platform independet ones
					currLabelDesc = ltrim(data.substring(assignmentOpPos + 1, data.length())).replaceAll(LINEBREAK_TOKEN, LINEBREAK);
					
					// create a new label entry
					currLabelEntries.add(new LabelEntry(currLangID, currLabelDesc));
				}
			}
		}
		catch(IOException e) {
			throw e;
		}
		finally {
			// release system resources
			reader.close();
			isr.close();
		}
	}
	
	/**
	 * Gets the available languages in this language file if the <code>$LANGUAGES</code> label exists.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Languages and their description can be specified under the <code>$LANGUAGES</code> label, like:
	 * <pre>
	 * $LANGUAGES
	 * #de = Deutsch
	 * #en = English
	 * </pre>
	 * 
	 * @return the list of available languages
	 * @since 1.0
	 */
	public List<LabelEntry> getAvailableLanguages() {
		return languages;
	}
	
	/**
	 * Gets a language specific label.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * if the label id does not have the specified language id then the label description of the
	 * first language id of the label is returned.
	 * 
	 * @param labelID the label id
	 * @param langID the language id
	 * @return the label description or an empty string if the specified label id does not exist
	 * @since 1.0
	 */
	public String getLabel(final String labelID, final String langID) {
		return getLabel(labelID, langID, null);
	}
	
	/**
	 * Gets a language specific label.
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * If the label id does not have the specified language id then the label description of the
	 * default language id of the label is returned. If the specified default language id does not
	 * exist then the first available label description is returned.
	 * 
	 * @param labelID the label id
	 * @param langID the language id
	 * @param defValue the default value of the label if it does not exist in the given language or <code>null</code> if no default value is defined (then the labelID is returned)
	 * @return the label description or the default value if no suitable language is found
	 * @since 1.0
	 */
	public String getLabel(final String labelID, final String langID, final String defValue) {
		final List<LabelEntry> entries = labels.get(labelID);
		
		if(entries != null) {
			// search for suitable language and return the description
			for(LabelEntry entry : entries)
				if(entry.langID.equalsIgnoreCase(langID))
					return entry.description;
		}
		
		// no suitable language found then return the default value or if it is not defined then the label
		return (defValue != null) ? defValue : labelID;
	}
	
	/**
	 * Gets a the description of a label of a language file.
	 * 
	 * @param file the language file or <code>null</code> if no language file exists
	 * @param labelID the label id
	 * @param langID the language id
	 * @return the label description of the specified language or an empty string if the specified label id does not exist
	 * @since 1.0
	 */
	public static String getLabel(final LanguageFile file, final String labelID, final String langID) {
		return getLabel(file, labelID, langID, "");
	}
	
	/**
	 * Gets a the description of a label of a language file.
	 * 
	 * @param file the language file or <code>null</code> if no language file exists
	 * @param labelID the label id
	 * @param langID the language id
	 * @param defValue the default value of the label if it does not exist in the given language or <code>null</code> if no default value is defined (then the labelID is returned)
	 * @return the label description of the specified language or the default value if the language file is <code>null</code> or no suitable language is found
	 * @since 1.0
	 */
	public static String getLabel(final LanguageFile file, final String labelID, final String langID, final String defValue) {
		final String labelDesc = (file != null) ? file.getLabel(labelID, langID, defValue) : defValue;
		return (labelDesc != null) ? labelDesc : labelID;
	}
	
	/**
	 * Includes the language data of the given file into this one.
	 * 
	 * @param file the language file its labels should be transfered to this one
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if file is null</li>
	 * 		<li>if file is this language file</li>
	 * </ul>
	 * @since 1.0
	 */
	public void include(final LanguageFile file) throws IllegalArgumentException {
		if(file == null || file == this)
			throw new IllegalArgumentException("No valid argument!");
		
		final Iterator<String> it = file.labels.keySet().iterator();
		String label;
		List<LabelEntry> labelEntries;
		
		// supplement the available languages
		addEntriesToList(languages, file.languages);
		
		while(it.hasNext()) {
			label = it.next();
			labelEntries = file.labels.get(label);
			
			// if there is already a label with that key then supplement the entries list
			// otherwise add the new label
			if(labels.containsKey(label))
				addEntriesToList(labels.get(label), labelEntries);
			else
				labels.put(label, labelEntries);
		}
	}
	
	/**
	 * Supplements the existing entries with other entries but only if they do not exist.
	 * 
	 * @param existingEntries the existing list of entries
	 * @param supplementaryEntries the supplementary list of entries
	 * @since 1.0
	 */
	private void addEntriesToList(final List<LabelEntry> existingEntries, final List<LabelEntry> supplementaryEntries) {
		boolean langIDExists;
		
		for(LabelEntry entry : supplementaryEntries) {
			langIDExists = false;
			
			// look for an existing entry with an identical language id
			for(LabelEntry existingEntry : existingEntries) {
				if(entry.langID.equals(existingEntry.langID)) {
					langIDExists = true;
					break;
				}
			}
			
			// no language id found? then supplement the existing list with the entry
			if(!langIDExists)
				existingEntries.add(entry);
		}
	}
	
	/**
	 * Trims the whitespaces at the left side of the given string.
	 *  
	 * @param string the string
	 * @return the left trimmed string
	 * @since 1.0
	 */
	private String ltrim(final String string) {
		return LTRIM.matcher(string).replaceAll("");
	}

}
