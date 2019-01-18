/*
 *  TailleFSA.java
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

import fsa.*;
public class SizeFSA extends EvalMeasure{
	private static final long serialVersionUID = 1L;
	
	public String getName(){
		return("Taille_FSA");
	}
	public Result eval(Hyp hyp){
		Result res=new Result(hyp.getAlgo(),hyp.getProg(),this);
		Prog prog=hyp.getProg();
		FSA fsa=prog.getFSA();
		int nb_states=fsa.getStates().size();
		int nb_trans=fsa.getTransitions().size();
		res.addScore("Nb_States_Prog", ((double)nb_states));
		System.out.println("Nb_States_Prog"+" => "+ ((double)nb_states));
		res.addScore("Nb_Transitions_Prog", ((double)nb_trans));
		System.out.println("Nb_Transitions_Prog"+" => "+ ((double)nb_trans));
		fsa=hyp.getFSA();
		nb_states=fsa.getStates().size();
		nb_trans=fsa.getTransitions().size();
		res.addScore("Nb_States_Hyp", ((double)nb_states));
		System.out.println("Nb_States_Hyp"+" => "+ ((double)nb_states));
		res.addScore("Nb_Transitions_Hyp", ((double)nb_trans));
		System.out.println("Nb_Transitions_Hyp"+" => "+ ((double)nb_trans));
		
		return(res);
	}
}
