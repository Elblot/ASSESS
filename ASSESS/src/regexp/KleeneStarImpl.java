/*
 *  KleeneStarImpl.java
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

import java.util.List;

import fsa.FSA;
import fsa.State;
import fsa.Transition;
import regexp.KleeneStar;
import regexp.Regexp;
import regexp.fromFSA.RegexpExtractionUtils;
import traces.Statement;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Kleene Star</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link fr.lip6.meta.regexp.impl.KleeneStarImpl#getExp <em>Exp</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class KleeneStarImpl implements KleeneStar {
	/**
	 * The cached value of the '{@link #getExp() <em>Exp</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExp()
	 * @generated
	 * @ordered
	 */
	protected Regexp exp;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public KleeneStarImpl() {
		super();
	}

		/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Regexp getExp() {
		return exp;
	}

	
	public void setExp(Regexp newExp) {
		exp = newExp;
	}

	/*public void accept(RegexpVisitor visitor) {
		// TODO Auto-generated method stub
		//System.out.println(" [c:loop] ");
		this.getExp().accept(visitor);
		//System.out.println(" [/c]");
		visitor.visit(this);
	}*/
	
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
		
		FSA blts=exp.buildFSA();
		init=blts.getInitialState();
		fin=blts.getFinalState();
		fsa.addStates(blts.getStates());
		fsa.addTransitions(blts.getTransitions());
		Transition tr=new Transition(source,new Statement(),init);
		fsa.addTransition(tr);
		Transition tr2=new Transition(fin,new Statement(),target);
		fsa.addTransition(tr2);
		Transition tr3=new Transition(target,new Statement(),source);
		fsa.addTransition(tr3);
		Transition tr4=new Transition(source,new Statement(),target);
		fsa.addTransition(tr4);
		return fsa;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof KleeneStarImpl)){
			return false;
		}
		return RegexpExtractionUtils.reg2String(this).equals(RegexpExtractionUtils.reg2String((KleeneStar)obj));
	}

	@Override
	public int hashCode() {
		int result = RegexpExtractionUtils.reg2String(this).hashCode();
		//System.out.println("hashcode''''''''''''''''''''''''''''''''''''''" + result);
		return result;
	}
	
	public Regexp copy(){
		KleeneStarImpl k=new KleeneStarImpl();
		k.setExp(exp.copy());
		return k;
	}

} //KleeneStarImpl
