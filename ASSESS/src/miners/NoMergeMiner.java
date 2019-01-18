/*
 *  NoMergeMiner.java
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

package miners;
import java.util.ArrayList;

import fsa.FSA;

import traces.Trace;

/**
 * Builds an FSA only accepting input traces (no generalization). 
 * Simplest FSA miner from traces: each input trace leads to the production of a specific branch of the FSA, and then, an initial state gets one output transition per sub-FSA representing an input trace.  
 * 
 * @author Sylvain Lamprier
 *
 */
public class NoMergeMiner implements FSAminer{
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getName(){
		return("Un_LTS_Par_Trace");
	}
	
	@Override
	public FSA transform(ArrayList<Trace> traces) {
		FSA lts=new FSA(traces);
		return(lts);
	}
}
