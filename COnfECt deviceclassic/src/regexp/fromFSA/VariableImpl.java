/*
 *  VariableImpl.java
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
import regexp.fromFSA.Variable;
import regexp.Regexp;
import traces.Statement;

 
public class VariableImpl implements Variable {


	protected String name;

	public VariableImpl() {
		super();
	}
	
	public VariableImpl(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof VariableImpl)){
			return false;
		}
		return ((Variable) obj).getName().equals(this.name);
	}

	@Override
	public int hashCode() {
		int result = this.name.hashCode();
		//System.out.println("hashcode''''''''''''''''''''''''''''''''''''''" + result);
		return result;
	}
	
	public FSA buildFSA(){
		throw new RuntimeException("Should not be here...");
		
	}

	public Regexp copy(){
		return this;
	}
} 
