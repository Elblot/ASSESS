/*
 *  ConcatenationImpl.java
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

import java.util.ArrayList;
import java.util.List;

import program.Block;
import fsa.FSA;
import fsa.State;
import fsa.Transition;
import regexp.Concatenation;
import regexp.Regexp;
import regexp.fromFSA.Epsilon;
import regexp.fromFSA.RegexpExtractionUtils;
import traces.Statement;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Concatenation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link fr.lip6.meta.regexp.impl.ConcatenationImpl#getExp <em>Exp</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConcatenationImpl implements Concatenation {
	/**
	 * The cached value of the '{@link #getExp() <em>Exp</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExp()
	 * @generated
	 * @ordered
	 */
	private List<Regexp> exp;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConcatenationImpl() {
		super();
	}

	public List<Regexp> getExp() {
		if (exp == null) {
			exp = new ArrayList<Regexp>();
		}
		return exp;
	}

	public FSA buildFSA(){
		FSA fsa=new FSA();
		
		State source=new State();
		State target=new State();
		fsa.setInitialState(source);
		fsa.setFinalState(target);
		fsa.addState(source);
		fsa.addState(target);
		State init=null;
		State fin=null;
		
		for(Regexp b:exp){
			FSA blts=b.buildFSA();
			init=blts.getInitialState();
			fin=blts.getFinalState();
			fsa.addStates(blts.getStates());
			fsa.addTransitions(blts.getTransitions());
			Transition tr=new Transition(source,new Statement(),init);
			fsa.addTransition(tr);
			source=fin;
			
		}
		Transition tr=new Transition(source,new Statement(),target);
		fsa.addTransition(tr);
		return fsa;
	}
	
	public Regexp getLast(){
		if(exp.size()==0){
			return new Epsilon();
		}
		return exp.get(exp.size()-1);
	}
	public Regexp removeLast(){
		if(exp.size()==0){
			return new Epsilon();
		}
		return exp.remove(exp.size()-1);
	}
	
	public Regexp getFirst(){
		if(exp.size()==0){
			return new Epsilon();
		}
		return exp.get(0);
	}
	public Regexp removeFirst(){
		if(exp.size()==0){
			return new Epsilon();
		}
		return exp.remove(0);
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ConcatenationImpl)){
			return false;
		}
		return RegexpExtractionUtils.reg2String(this).equals(RegexpExtractionUtils.reg2String((Concatenation)obj));
	}

	@Override
	public int hashCode() {
		int result = RegexpExtractionUtils.reg2String(this).hashCode();
		//System.out.println("hashcode''''''''''''''''''''''''''''''''''''''" + result);
		return result;
	}
	
	public Regexp copy(){
		ConcatenationImpl conc=new ConcatenationImpl();
		List<Regexp> exps=conc.getExp();
		for(Regexp e:exp){
			exps.add(e.copy());
		}
		return conc;
	}
	
} //ConcatenationImpl
