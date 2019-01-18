/*
 *  Equation.java
 * 
 *  Copyright (C) 2012-2013 Sylvain Lamprier, Tewfik Ziaidi, Lom Messan Hillah and Nicolas Baskiotis
 * 
 *  This file is part of CARE.
 * 
 *   CARE is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   CARE is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with CARE.  If not, see <http://www.gnu.org/licenses/>.
 */

package regexp.fromFSA;


import regexp.Regexp;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Equation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link fr.lip6.meta.RegExpEquationSystem.Equation#getLeft <em>Left</em>}</li>
 *   <li>{@link fr.lip6.meta.RegExpEquationSystem.Equation#getRight <em>Right</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.lip6.meta.RegExpEquationSystem.RegExpEquationSystemPackage#getEquation()
 * @model
 * @generated
 */
public interface Equation {
	/**
	 * Returns the value of the '<em><b>Left</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Left</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Left</em>' containment reference.
	 * @see #setLeft(Variable)
	 * @see fr.lip6.meta.RegExpEquationSystem.RegExpEquationSystemPackage#getEquation_Left()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Variable getLeft();

	/**
	 * Sets the value of the '{@link fr.lip6.meta.RegExpEquationSystem.Equation#getLeft <em>Left</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Left</em>' containment reference.
	 * @see #getLeft()
	 * @generated
	 */
	void setLeft(Variable value);

	/**
	 * Returns the value of the '<em><b>Right</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Right</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Right</em>' containment reference.
	 * @see #setRight(Regexp)
	 * @see fr.lip6.meta.RegExpEquationSystem.RegExpEquationSystemPackage#getEquation_Right()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Regexp getRight();

	/**
	 * Sets the value of the '{@link fr.lip6.meta.RegExpEquationSystem.Equation#getRight <em>Right</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Right</em>' containment reference.
	 * @see #getRight()
	 * @generated
	 */
	void setRight(Regexp value);

} // Equation
