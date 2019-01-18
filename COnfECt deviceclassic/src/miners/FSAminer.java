/*
 *  FSAminer.java
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

//import fr.lip6.meta.strategie.StatementComparaisonStrategy;
import traces.Trace;
import java.io.Serializable;

import fsa.FSA;
/**
 * Abstract class every miner must extend
 * @author Sylvain Lamprier
 *
 */
public interface FSAminer extends Serializable{

	/**
	 * Builds an FSA from a set of traces  
	 * @param traces
	 * @return an FSA representing the behavior of the system having generated the traces 
	 */
	public abstract FSA transform(ArrayList<Trace> traces);
	
	/**
	 * 
	 * @return the name of the miner
	 */
	public abstract String getName();
}
