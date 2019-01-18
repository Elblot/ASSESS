/*
 *  SystemImpl.java
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import regexp.fromFSA.Equation;
import regexp.fromFSA.EquationSystem;
import regexp.fromFSA.Variable;

public class SystemImpl implements EquationSystem {
	/**
	 * The cached value of the '{@link #getEquation() <em>Equation</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEquation()
	 * @ordered
	 */
	private List<Equation> equation;
	
	private HashMap<Variable,ArrayList<Variable>> containsTable;

	/**
	 * The cached value of the '{@link #getObjective() <em>Objective</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObjective()
	 * @ordered
	 */
	private Variable objective;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public SystemImpl() {
		super();
		this.equation = new ArrayList<Equation>();
		this.objective = null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public List<Equation> getEquation() {
		return equation;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public void setEquation(List<Equation> eq) {
		this.equation = eq;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public Variable getObjective() {
		return objective;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public void setObjective(Variable newObjective) {
		this.objective = newObjective;
	}

	public HashMap<Variable, ArrayList<Variable>> getContainsTable() {
		return containsTable;
	}

	public void setContainsTable(
			HashMap<Variable, ArrayList<Variable>> containsTable) {
		this.containsTable = containsTable;
	}

} //SystemImpl
