/*
 *  EquationSystem.java
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

/**

 * </copyright>
 *
 * $Id$
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>System</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link EquationSystem#getEquation <em>Equation</em>}</li>
 *   <li>{@link EquationSystem#getObjective <em>Objective</em>}</li>
 * </ul>
 * </p>
 *
 * @see fr.lip6.meta.RegExpEquationSystem.RegExpEquationSystemPackage#getSystem()
 * @model
 * @generated
 */
public interface EquationSystem {
	/**
	 * Returns the value of the '<em><b>Equation</b></em>' containment reference list.
	 * The list contents are of type {@link fr.lip6.meta.RegExpEquationSystem.Equation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Equation</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Equation</em>' containment reference list.
	 * @see fr.lip6.meta.RegExpEquationSystem.RegExpEquationSystemPackage#getSystem_Equation()
	 * @model containment="true" required="true"
	 * @generated
	 */
	List<Equation> getEquation();

	/**
	 * Returns the value of the '<em><b>Objective</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Objective</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Objective</em>' containment reference.
	 * @see #setObjective(Variable)
	 * @see fr.lip6.meta.RegExpEquationSystem.RegExpEquationSystemPackage#getSystem_Objective()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Variable getObjective();

	/**
	 * Sets the value of the '{@link fr.lip6.meta.RegExpEquationSystem.EquationSystem#getObjective <em>Objective</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Objective</em>' containment reference.
	 * @see #getObjective()
	 * @generated
	 */
	void setObjective(Variable value);
	public HashMap<Variable, ArrayList<Variable>> getContainsTable();

	public void setContainsTable(
			HashMap<Variable, ArrayList<Variable>> containsTable);

} // System

