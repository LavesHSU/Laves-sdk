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
 * Class:		Mask
 * Task:		Mask objects with other objects or icons
 * Created:		20.12.13
 * LastChanges:	21.12.13
 * LastAuthor:	jdornseifer
 */

package lavesdk.gui.widgets;

import javax.swing.Icon;

/**
 * Represents a mask.
 * <br><br>
 * A mask can be used to replace objects with another object (mask object) or item (mask item).
 * <br><br>
 * <b>Predefined icon masks</b>:<br>
 * You can use {@link Symbol#getPredefinedSymbol(lavesdk.gui.widgets.Symbol.PredefinedSymbol)} to get a predefined
 * symbol as a mask icon.
 * <br><br>
 * <b>Examples</b>:<br>
 * 1. there is a string "ABC" in an {@link ExecutionTableColumn} and this string should be replaced by "CBA".<br>
 * Firstly you create the mask <code>mask = new ExecutionTableMask("ABC", "CBA");</code>. Secondly you add this mask to
 * the column and all cell contents of this column that equals "ABC" are replaced by "CBA".<br>
 * 2. there is a numeric value like {@link Integer#MAX_VALUE} in an {@link ExecutionTable} and this value should be replaced by an infinity
 * symbol.<br>
 * Firstly you create the mask <code>mask = new ExecutionTableMask(Integer.MAX_VALUE, Symbol.getPredefinedSymbol(PredefinedSymbol.INFINITY);</code>.
 * Secondly you add this mask to the column and all cell contents of this column that equals {@link Integer#MAX_VALUE} are replaced by the symbol.<br>
 * 
 * @author jdornseifer
 * @version 1.0
 * @since 1.0
 */
public class Mask {
	
	/** the object that should be masked */
	private final Object object;
	/** the mask as an object */
	private final Object maskObject;
	/** the mask as an icon */
	private final Icon maskIcon;
	/** the type of the mask */
	private final Type type;
	
	/**
	 * Creates a new mask.
	 * 
	 * @param object the object that should be masked
	 * @param mask the mask (should have the same type as the object)
	 * @since 1.0
	 */
	public Mask(final Object object, final Object mask) {
		this.object = object;
		this.maskObject = mask;
		this.maskIcon = null;
		this.type = Type.OBJECT_MASK;
	}
	
	/**
	 * Creates a new mask.
	 * 
	 * @param object the object that should be masked
	 * @param mask the mask like a predefined {@link Symbol}
	 * @throws IllegalArgumentException
	 * <ul>
	 * 		<li>if mask is null</li>
	 * </ul>
	 * @since 1.0
	 */
	public Mask(final Object object, final Icon mask) throws IllegalArgumentException {
		if(mask == null)
			throw new IllegalArgumentException("No valid argument!");
		
		this.object = object;
		this.maskObject = null;
		this.maskIcon = mask;
		this.type = Type.ICON_MASK;
	}
	
	/**
	 * Gets the type of the mask.
	 * 
	 * @return the type
	 * @since 1.0
	 */
	public final Type getType() {
		return type;
	}
	
	/**
	 * Gets the object that should be masked.
	 * <br><br>
	 * Let <code>S</code> be the set of objects that could be masked and let <code>m</code> be the mask. For all <code>o</code> in <code>S</code>
	 * with <code>m.matches(o)</code> <code>o</code> is replaced by the mask which is either the mask object ({@link #getMaskObject()})
	 * or the mask icon ({@link #getMaskIcon()}).
	 * <br><br>
	 * <b>Notice</b>:<br>
	 * Use {@link #matches(Object)} to check if an object matches to the mask.
	 * 
	 * @see #matches(Object)
	 * @return the object that should be masked (this can be <code>null</code>)
	 * @since 1.0
	 */
	public final Object getObject() {
		return object;
	}
	
	/**
	 * Gets the mask object if {@link #getType()} is {@link Type#OBJECT_MASK}.
	 * <br><br>
	 * Let <code>S</code> be the set of objects that could be masked and let <code>m</code> be the mask. For all <code>o</code> in <code>S</code>
	 * with <code>m.matches(o)</code> <code>o</code> is replaced by the mask which is either the mask object ({@link #getMaskObject()})
	 * or the mask icon ({@link #getMaskIcon()}).
	 * 
	 * @see #matches(Object)
	 * @return the mask
	 * @since 1.0
	 */
	public final Object getMaskObject() {
		return maskObject;
	}
	
	/**
	 * Gets the mask icon if {@link #getType()} is {@link Type#ICON_MASK}.
	 * <br><br>
	 * Let <code>S</code> be the set of objects that could be masked and let <code>m</code> be the mask. For all <code>o</code> in <code>S</code>
	 * with <code>m.matches(o)</code> <code>o</code> is replaced by the mask which is either the mask object ({@link #getMaskObject()})
	 * or the mask icon ({@link #getMaskIcon()}).
	 * 
	 * @see #matches(Object)
	 * @return the mask
	 * @since 1.0
	 */
	public final Icon getMaskIcon() {
		return maskIcon;
	}
	
	/**
	 * Indicates whether the given object matches the mask.
	 * <br><br>
	 * Let <code>S</code> be the set of objects that could be masked and let <code>m</code> be the mask. For all <code>o</code> in <code>S</code>
	 * with <code>m.matches(o)</code> <code>o</code> is replaced by the mask which is either the mask object ({@link #getMaskObject()})
	 * or the mask icon ({@link #getMaskIcon()}).
	 * 
	 * @param o the object o
	 * @return <code>true</code> if <i>o</i> matches to the mask's object otherwise <code>false</code>
	 * @since 1.0
	 */
	public boolean matches(final Object o) {
		if(object != null)
			return object.equals(o);
		else
			return (o == null);
	}
	
	/**
	 * The type of a mask.
	 * <br><br>
	 * Available types:
	 * <ul>
	 * 		<li>{@link #OBJECT_MASK}</li>
	 * 		<li>{@link #ICON_MASK}</li>
	 * </ul>
	 * 
	 * @author jdornseifer
	 * @version 1.0
	 * @since 1.0
	 */
	public enum Type {
		
		/** the mask type is an object */
		OBJECT_MASK,
		
		/** the mask type is an icon */
		ICON_MASK
		
	}

}
