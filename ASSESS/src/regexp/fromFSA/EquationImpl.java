/*
 *  EquationImpl.java
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
import regexp.RegexpUtil;
import regexp.fromFSA.Equation;
import regexp.fromFSA.Variable;


/**
 * 
 */
public class EquationImpl implements Equation {
 
	protected Variable left;

	protected Regexp right;

	public EquationImpl() {
		super();
	}

	public Variable getLeft() {
		return left;
	}

	public void setLeft(Variable newLeft) {
		this.left = newLeft;
	}

	public Regexp getRight() {
		return right;
	}

	public void setRight(Regexp newRight) {
		this.right = newRight;
	}
	
	public String toString(){
		return "left="+left+","+"right="+RegexpUtil.reg2String(right);
	}

} //EquationImpl
