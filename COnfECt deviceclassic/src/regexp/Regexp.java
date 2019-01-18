/*
 *  Regexp.java
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

import fsa.FSA;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Regexp</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see fr.lip6.meta.regexp.RegexpPackage#getRegexp()
 * @model abstract="true"
 * @generated
 */
public interface Regexp {
	//TODO public void accept(RegexpVisitor visitor);
	
	public FSA buildFSA();
	
	public Regexp copy();
	
} // Regexp
