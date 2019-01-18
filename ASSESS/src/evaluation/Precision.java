/*
 *  Precision.java
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

//import fr.lip6.meta.traceGenerator.util.TraceIO;
//import fr.lip6.meta.traceGenerator.table.TraceGenResult;
import traces.Trace;
import java.util.ArrayList;

import core.Prog;

import fsa.FSA;

public class Precision extends EvalMeasure {
	private static final long serialVersionUID = 1L;
	
	protected int nb_traces_a_test;   // Nombre de traces a tester. -1 => Autant que d'observations
	protected boolean traces_completes; // Teste t-on des traces entieres ou considere t-on des sous-traces
	public Precision(int nb,boolean traces_completes){
		nb_traces_a_test=nb;
		this.traces_completes=traces_completes;
	}
	public Precision(){
		this(-1,true);
	}
	public Precision(int nb){
		this(nb,true);
	}
	public Precision(boolean traces_completes){
		this(-1,traces_completes);
	}
	public String getName(){
		if (traces_completes){
			return("Precision_Sur_"+nb_traces_a_test+"_traces");
		}
		else{
			return("PrecisionSize_Sur_"+nb_traces_a_test+"_traces");
		}
	}
	public Result eval(Hyp hyp){
		Result res=new Result(hyp.getAlgo(),hyp.getProg(),this);
		//LTS lts_hyp=hyp.getLTS();
		Prog prog=hyp.getProg();
		FSA lts_ref=prog.getFSA();
		ArrayList<Trace> traces=hyp.getUniformes(this.nb_traces_a_test);
		double score=0.0;
		int i=0;
		for(Trace t: traces){
			if (this.traces_completes){
				/*System.out.println("taille trace "+i+" = "+t.getSize());
				TraceGenResult trg=t.getTraceGenResult();
				TraceIO.save(trg,".", "trace"+i,false);*/
				if(lts_ref.accepteTrace(t)){score+=1.0;}
			}
			else{
				score+=lts_ref.accepteSousTrace(t);
			}
			i++;
		}
		double prec=0.0;
		if (traces.size()>0){
			prec=score/traces.size();
		}
		//System.out.println(nb_ok);
		//System.out.println(prec);
		res.addScore(getName(), prec);
		System.out.println(this.getName()+" => "+ prec);
		
		return(res);
	}
}
