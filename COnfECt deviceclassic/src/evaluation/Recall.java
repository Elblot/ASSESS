/*
 *  Recall.java
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

import traces.Trace;
import java.util.ArrayList;

import core.Prog;

import fsa.FSA;

public class Recall extends EvalMeasure {
	private static final long serialVersionUID = 1L;
	
	protected boolean useUniformTraces;
	protected int nb_traces_a_test;   // Nombre de traces a tester (pour uniforme seulement). -1 => Autant que d'observations
	protected boolean traces_completes; // Teste t-on des traces entieres ou considere t-on des sous-traces
	
	public Recall(boolean useUniformTraces,int nb,boolean traces_completes){
		this.nb_traces_a_test=nb;
		this.useUniformTraces=useUniformTraces;
		this.traces_completes=traces_completes;
	}
	public Recall(){
		this(true,-1,true);
	}
	public Recall(int nb){
		this(true,nb,true);
	}
	public Recall(boolean useUniformTraces){
		this(useUniformTraces,-1,true);
	}
	public Recall(int nb,boolean traces_completes){
		this(true,nb,traces_completes);
	}
	
	public String getName(){
		String s="";
		if (!traces_completes){
			s="Size";
		}
		if (this.useUniformTraces){
		    return("Recall"+s+"_UniformTraces_Sur_"+nb_traces_a_test+"_traces");
		}
		else{
			return("Recall"+s+"_Observations");
		}
	}
	
	public Result eval(Hyp hyp){
		Result res=new Result(hyp.getAlgo(),hyp.getProg(),this);
		FSA lts_hyp=hyp.getFSA();
		Prog prog=hyp.getProg();
		ArrayList<Trace> traces=null;
		if (this.useUniformTraces){
				traces=prog.getUniformes(nb_traces_a_test);
		}
		else{
			    traces=prog.getObservations();
		}
		double score=0.0;
		for(Trace t: traces){
			if (this.traces_completes){
				if(lts_hyp.accepteTrace(t)){score+=1.0;}
			}
			else{
				score+=lts_hyp.accepteSousTrace(t);
			}
		}
		double recall=0.0;
		if (traces.size()>0){ 
			recall=score/traces.size();
		}
		res.addScore(this.getName(), recall);
		System.out.println(this.getName()+" => "+ recall);
		
		return(res);
	}
}
