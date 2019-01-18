/*
 *  EmptySet.java
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

import fsa.FSA;
import fsa.State;
import fsa.Transition;
import regexp.Regexp;
import traces.Statement;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Empty Set</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see fr.lip6.meta.regexp.RegexpPackage#getEmptySet()
 * @model
 * @generated
 */
public class EmptySet implements Regexp {
	
	public FSA buildFSA(){
		FSA fsa=new FSA();
		State source=new State();
		State target=new State();
		fsa.setInitialState(source);
		fsa.setFinalState(target);
		fsa.addState(source);
		fsa.addState(target);
		Transition tr=new Transition(source,new Statement(),target);
		fsa.addTransition(tr);
		return fsa;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof EmptySet)){
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return "{}".hashCode();
	}
	
	public Regexp copy(){
		return this;
	}
	
} // EmptySet
