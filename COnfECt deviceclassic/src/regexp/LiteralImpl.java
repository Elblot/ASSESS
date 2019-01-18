/*
 *  LiteralImpl.java
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

package regexp;

import regexp.fromFSA.RegexpExtractionUtils;
import traces.Statement;
import fsa.FSA;
import fsa.State;
import fsa.Transition;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Literal</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link regexp.LiteralImpl#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LiteralImpl implements Literal {
	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final Object VALUE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected Object value = VALUE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LiteralImpl() {
		super();
	}

	public Object getValue() {
		return value;
	}

	
	public void setValue(Object newValue) {
		value = newValue;
	}

	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (value: ");
		result.append(value);
		result.append(')');
		return result.toString();
	}
	
	public FSA buildFSA(){
		FSA fsa=new FSA();
		State source=new State();
		State target=new State();
		fsa.setInitialState(source);
		fsa.setFinalState(target);
		fsa.addState(source);
		fsa.addState(target);
		Statement st=null;
		if(value instanceof Statement){
			st=(Statement)value;
		}
		else{
			if(value instanceof String){
				st=Statement.strStringifier.getStatement((String)value);
			}
		}
		Transition tr=new Transition(source,st,target);
		fsa.addTransition(tr);
		return fsa;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof LiteralImpl)){
			return false;
		}
		return RegexpExtractionUtils.reg2String(this).equals(RegexpExtractionUtils.reg2String((Literal)obj));
	}

	@Override
	public int hashCode() {
		int result = RegexpExtractionUtils.reg2String(this).hashCode();
		//System.out.println("hashcode''''''''''''''''''''''''''''''''''''''" + result);
		return result;
	}

	public Regexp copy(){
		return this;
	}
	
} //LiteralImpl
