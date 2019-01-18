/*
 *  FMeasure.java
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import fsa.FSA;

public class FMeasure extends EvalMeasure {
	private static final long serialVersionUID = 1L;
	protected double beta; // parametre beta de la f-measure (1 par defaut)
	protected int nb_traces_a_test;   // Nombre de traces a tester. -1 => Autant que d'observations
	protected boolean traces_completes; // Teste t-on des traces entieres ou considere t-on des sous-traces
	public FMeasure(double beta,int nb,boolean traces_completes){
		nb_traces_a_test=nb;
		this.traces_completes=traces_completes;
		this.beta=beta;
	}
	public FMeasure(int nb,boolean traces_completes){
		this(1,nb,traces_completes);
	}
	public FMeasure(){
		this(-1,true);
	}
	public FMeasure(double beta){
		this(beta,-1,true);
	}
	public FMeasure(int nb){
		this(nb,true);
	}
	public FMeasure(double beta,int nb){
		this(beta,nb,true);
	}
	public FMeasure(boolean traces_completes){
		this(1,traces_completes);
	}
	public FMeasure(double beta, boolean traces_completes){
		this(beta,-1,traces_completes);
	}
	public String getName(){
		DecimalFormat format = new DecimalFormat();
	    format.setMaximumFractionDigits(1);
		if (traces_completes){
			return("F"+format.format(beta)+"-Measure_Sur_"+nb_traces_a_test+"_traces");
		}
		else{
			return("F"+format.format(beta)+"-MeasureSize_Sur_"+nb_traces_a_test+"_traces");
		}
	}
	public Result eval(Hyp hyp){
		Result res=new Result(hyp.getAlgo(),hyp.getProg(),this);
		Recall rec=new Recall(nb_traces_a_test,traces_completes);
		Precision prec=new Precision(nb_traces_a_test,traces_completes);
		Result r=rec.eval(hyp);
		double rscore=r.getScores().get(rec.getName());
		res.add(r);
		r=prec.eval(hyp);
		double pscore=r.getScores().get(prec.getName());
		res.add(r);
		double fscore=0.0;
		double denom=(beta*beta*pscore)+rscore;
		if (denom!=0){
			fscore=(1.0+beta*beta)*pscore*rscore/denom;
		}
		res.addScore(getName(), fscore);
		System.out.println(this.getName()+" => "+ fscore);
		//int nbs=hyp.getLTS().getStates().size();
		//if (nbs==0){nbs=1;}
		//res.addScore("State"+getName(), (fscore/nbs));
		//System.out.println("State"+this.getName()+" => "+ (fscore/nbs));
		return(res);
	}
}