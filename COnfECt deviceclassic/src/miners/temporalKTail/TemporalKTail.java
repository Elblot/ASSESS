/*
 *  TemporalKTail.java
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


package miners.temporalKTail;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import program.Actor;

import miners.FSAminer;
//import fr.lip6.meta.traceGenerator.table.TraceGenResult;
//import fr.lip6.meta.traceGenerator.util.TraceIO;
import traces.Method;
import traces.ObjectClass;
import traces.Statement;
import traces.Trace;
import dataGenerator.*;
//import fr.lip6.meta.strategie.*;
import fsa.EpsilonRemover;
import fsa.GenerateDOT;
import fsa.FSA;
import fsa.State;
import fsa.Transition;

/**
 * KTAIL with temporal dependencies.
 * 
 * @author Sylvain Lamprier
 *
 */
public class TemporalKTail implements FSAminer {
	private static final long serialVersionUID = 1L;
	
	//StatementComparaisonStrategy strategy;
	int mode=0; // -2 => Lo exact, -1 => Lo approx, >=0 => our approach
	int horizon;
	double support_level=0.2;
	int maxPreRules=1;
	int maxPreStricts=1;
	int updateMode=1; // 0 => intersections / unions d'ensembles, 1=> retraits / ajouts d'ensembles (uniquement pour mode>=0)
	
	public TemporalKTail(int k,double support,int maxPreRules){
		this(k,support,maxPreRules,maxPreRules);
	}
	
	/**
	 * Behavioral steering with temporal dependencies between atomic sequences of events
	 * 
	 * @param mode   // -1 => Lo approximation, >=0 => our approach
	 * @param k		 // size of the future considered by KTAIL
	 * @param support minimal ratio of traces in which the pre part of rules has to be observed
	 */
	public TemporalKTail(int mode,int k,double support){
		this(k,support,1,0);
		this.mode=mode;
		
	}
	/**
	 * 
	 * @param k             size of the future considered by KTAIL
	 * @param support       minimal ratio of traces in which the pre part of rules has to be observed
	 * @param maxPreRules   maximal size for sequences of pre part of rules
	 * @param maxPreStricts maximal size for sequences of pre part of strict rules
	 */
	public TemporalKTail(int k,double support,int maxPreRules,int maxPreStricts){
		horizon=k;
		support_level=support;
		//if (mode>=1){
		this.maxPreRules=maxPreRules;
		this.maxPreStricts=maxPreStricts;
		
		//TemporalState.mode=mode;
	}
	
	/**
	 * 
	 * @param k             size of the future considered by KTAIL
	 * @param support       minimal ratio of traces in which the pre part of rules has to be observed
	 * @param maxPreRules   maximal size for sequences of pre part of rules
	 * @param maxPreStricts maximal size for sequences of pre part of strict rules
	 * @param updateMode 	mode for updates of required / sure events sets in the efficient approach (for mode>=0). // 0 => intersections / unions of sets, 1=> removal / adds of sets (default=1).
	 */
	public TemporalKTail(int k,double support,int maxPreRules,int maxPreStricts,int updateMode){
		this(k,support,maxPreRules,maxPreStricts);
		this.updateMode=updateMode;
	}
		
	
	public TemporalKTail(int k,double support){
		this(k,support,1);
	}
	public TemporalKTail(int k){
		this(k,0.2);
	}
	
	
	
	public String getName(){
		return("Temporal_KTail_k="+horizon+"_support="+this.support_level+"_maxPreRules="+this.maxPreRules+"_maxPreStricts="+this.maxPreStricts+"_mode="+mode+"_update="+updateMode);
	}
	
	
	@Override
	public FSA transform(ArrayList<Trace> traces) {
		TemporalStateNode.mode=mode;
		TemporalStateNode.updateMode=updateMode;
		TemporalRuleMiner ti=new TemporalRuleMiner(maxPreRules,maxPreStricts,support_level);
		ti.mineRules(traces);
		TemporalRules rules=ti.getRules();
		FSA fsa=new FSA();
		HashSet<TemporalState> final_states=new HashSet<TemporalState>(); 
		HashSet<TemporalState> initial_states=new HashSet<TemporalState>(); 
		TemporalStateNode root=new TemporalStateNode(); 
		boolean vide=false;
		int itrace=1;
		for(Trace trace:traces){
			System.out.println("Trace "+itrace);
			itrace++;
			
			FSA tmp=null;
			if(mode>=0){
				tmp=ti.getLTSForSingleTraceWithAllRequirements(trace);
			}
			else{
				tmp=ti.getLTSForSingleTrace(trace);
			}
			System.out.println("req ok ");
			int size=trace.getSize();
			if (size==0){
				vide=true;
				continue;
			}
			ArrayList<State> temps=new ArrayList<State>(tmp.getStates());
			ArrayList<Statement> thorizon=new ArrayList<Statement>();
			for(int i=1;((i<=size) && (i<=horizon));i++){
				Statement st=trace.getByIndex(i);
				thorizon.add(st);
			}
			HashSet<TemporalState> fs=new HashSet<TemporalState>(final_states);
			fs.add((TemporalState)tmp.getFinalState());
			HashSet<TemporalState> is=new HashSet<TemporalState>(initial_states);
			is.add((TemporalState)tmp.getInitialState());
			TemporalState last_node=root.getSetTemporalState((TemporalState)temps.get(0),null,fsa,null,new ArrayList<Statement>(thorizon),is,fs);
			
			fsa.addState(last_node);
			initial_states.add(last_node);
			is.add(last_node);
			fsa.setInitialState(last_node);
			
			for(int i=1;i<=size;i++){
				if (i<=(size-horizon)){
					Statement st=trace.getByIndex(i+horizon);
					thorizon.add(st);
				}
				else{
					thorizon.add(new Statement());
				}
				Statement first=thorizon.remove(0);
				//System.out.println(thorizon);
				TemporalState stnode=root.getSetTemporalState((TemporalState)temps.get(i),last_node,fsa,first,new ArrayList<Statement>(thorizon),is,fs);
				fsa.addState(stnode);
				if (i==size){
					   final_states.add(stnode);
					   fsa.addFinalState(stnode);
				}
				
				last_node=stnode;
			}
		}
		
		TemporalState source=new TemporalState();
		//lts.addStates(new ArrayList<State>(all_states));
		fsa.setInitialState(source);
		//lts.setFinalStates(new ArrayList<State>(final_states));
		if (vide){
			fsa.addFinalState(source);
		}
		for(State s:initial_states){
			Transition tr=new Transition(source,new Statement(),s);
			fsa.addTransition(tr);
		}
		//GenerateDOT.printDot(lts, ".a.dot");
		EpsilonRemover.removeEpsilon(fsa);
		//rules.checkSimpleRules(fsa, 10000);
		return(fsa);
	}
	
	
	
	public static void main(String[] args){
		ObjectClass classe=new ObjectClass("X"); 
		Actor main = new Actor("main", classe);
		int i=0;
		Statement a=new Statement("S"+i++,main,new Method("A"),main);
		Statement b=new Statement("S"+i++,main,new Method("B"),main);
		Statement c=new Statement("S"+i++,main,new Method("C"),main);
		Statement d=new Statement("S"+i++,main,new Method("D"),main);
		Trace t1=new Trace();
		t1.add(b);
		t1.add(b);
		t1.add(b);
		t1.add(c);
		t1.add(c);
		t1.add(c);
		Trace t2=new Trace();
		t2.add(a);
		t2.add(b);
		t2.add(c);
		t2.add(a);
		t2.add(b);
		t2.add(c);
		Trace t3=new Trace();
		t3.add(a);
		t3.add(b);
		t3.add(c);
		t3.add(d);
		Trace t4=new Trace();
		t4.add(a);
		t4.add(d);
		ArrayList<Trace> traces=new ArrayList<Trace>();
		traces.add(t1);
		traces.add(t2);
		traces.add(t3);
		traces.add(t4);
		TemporalKTail alg=new TemporalKTail(0,2,0.0);
		FSA fsa=alg.transform(traces);
		GenerateDOT.printDot(fsa, "test.dot");
		System.out.println(State.nbManipSeq);
		System.out.println(State.nbVisitsStates);
		
	}
}
	