/*
 *  EsperanceLongueurTracesUniformes.java
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


package evaluation;
import java.util.ArrayList;

import core.Prog;

import fsa.FSA;
import traces.*;
public class EsperanceLongueurTracesUniformes extends EvalMeasure{
	private static final long serialVersionUID = 1L;
	
	public String getName(){
		return("EsperanceTailleUniformes");
	}
	public Result eval(Hyp hyp){
		Result res=new Result(hyp.getAlgo(),hyp.getProg(),this);
		Prog prog=hyp.getProg();
		FSA lts=prog.getFSA();
		double l=lts.getEsperanceLongueur();
		res.addScore("EsperanceTailleUniformes_Prog",l);
		System.out.println("EsperanceTailleUniformes_Prog => "+ l);
		lts=hyp.getFSA();
		l=lts.getEsperanceLongueur();
		res.addScore("EsperanceTailleUniformes_Hyp",l);
		System.out.println("EsperanceTailleUniformes_Hyp => "+ l);
		return(res);
	}
}
