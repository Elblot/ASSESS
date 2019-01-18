/*
 *  TailleTraces.java
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
public class TailleTraces extends EvalMeasure {
	private static final long serialVersionUID = 1L;
	
	protected int nb_uniformes;
	public TailleTraces(int nb_uniformes){
		this.nb_uniformes=nb_uniformes;
	}
	public String getName(){
		return("Taille_traces");
	}
	public Result eval(Hyp hyp){
		Result res=new Result(hyp.getAlgo(),hyp.getProg(),this);
		Prog prog=hyp.getProg();
		ArrayList<Trace> traces=prog.getObservations();
		int nb_statements=0;
		int nb=0;
		for(Trace t:traces){
			nb_statements+=t.getStatements().size();
			nb++;
		}
		double moy=((double) nb_statements)/nb;
		res.addScore("Taille_Observations",moy);
		System.out.println("Taille_Observations => "+ moy);
		
		traces=prog.getUniformes(nb_uniformes);
		nb_statements=0;
		nb=0;
		for(Trace t:traces){
			nb_statements+=t.getStatements().size();
			nb++;
		}
		moy=((double) nb_statements)/nb;
		
		
		res.addScore("Taille_Uniformes_Prog",moy);
		System.out.println("Taille_Uniformes_Prog => "+ moy);
		
		traces=hyp.getUniformes(nb_uniformes);
		nb_statements=0;
		nb=0;
		for(Trace t:traces){
			nb_statements+=t.getStatements().size();
			nb++;
		}
		moy=((double) nb_statements)/nb;
		
		res.addScore("Taille_Uniformes_Hyp",moy);
		System.out.println("Taille_Uniformes_Hyp => "+ moy);
		return(res);
	}
}
