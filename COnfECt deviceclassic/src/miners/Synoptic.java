/*
 *  Synoptic.java
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
import java.util.HashMap;

import traces.Method;
import traces.ObjectClass;
import traces.Statement;
import traces.Trace;
import utils.Keyboard;
import fsa.EpsilonRemover;
import fsa.FSA;
import fsa.GenerateDOT;
import fsa.State;
import fsa.Transition;
import miners.KTail.StateNode;
import miners.temporalKTail.TemporalKTail;
import miners.temporalKTail.TemporalRuleMiner;
import miners.temporalKTail.TemporalRules;
import java.util.HashSet;
import java.util.AbstractMap.SimpleEntry;

import core.Sequence;

import program.Actor;
/**
 * 
 * The Synoptic algorithm as defined in "I. Beschastnikh, J. Abrahamson, Y. Brun, and M. D. Ernst. Synoptic: Studying Logged Behavior with Inferred Models. In Proc. of FSE, 2011." 
 * 
 * 
 * 
 * @author Sylvain Lamprier
 *
 */



public class Synoptic implements FSAminer {
	private static final long serialVersionUID = 1L;
	
	private double support;
	private int horizon; // -1 use Simple Alg as init, else KTail with k=horizon
	private int finalCoarsing;
	
	public Synoptic(double support, int k, int finalCoarsing){
		this.support=support;
		this.horizon=k;
		this.finalCoarsing=finalCoarsing;
	}
	
	
	private void simpleAlg(FSA fsa,ArrayList<Trace> traces){
		SynopticState root=new SynopticState("root");
		fsa.addState(root);
		HashMap<String,SynopticState> states=new HashMap<String,SynopticState>();
		fsa.setInitialState(root);
		HashSet<SynopticTrace> synTraces=new HashSet<SynopticTrace>();
		for (Trace trace : traces) {
			SynopticTrace synt=new SynopticTrace();
			synTraces.add(synt);
			int size=trace.getSize();
			SynopticState last=root;
			
			for(int i=1;i<=size;i++){
				State.nbManipSeq++;
				last.setTrace(synt,i-1);
				Statement st=trace.getByIndex(i);
				synt.add(st);
				String sttext=st.getText();
				ArrayList<Transition> succs=last.getSuccesseurs(st);
				
				SynopticState next=null;
				if(states.containsKey(sttext)){
					next=states.get(sttext);
				}
				else{
					next=new SynopticState();
					State.nbVisitsStates++;
					fsa.addState(next);
					states.put(sttext, next);
				}
				if(succs.size()==0){
					Transition t=new Transition(last,st,next);
					fsa.addTransition(t);
				}
				last=next;
			}
			last.setTrace(synt,size);
			fsa.addFinalState(last);
			synt.computePositions();
			//synt.computeFutures(fposts);
			//synt.computePasts(pposts);
		  }
	}
	
	private void kTail(FSA fsa,ArrayList<Trace> traces){
		StateNode root=new StateNode(); 
		HashSet<StateNode> all_states=new HashSet<StateNode>();
		HashSet<SynopticState> all_synStates=new HashSet<SynopticState>();
		HashSet<State> initial_states=new HashSet<State>();
		HashSet<SynopticState> initial_synStates=new HashSet<SynopticState>();
		HashSet<State> final_states=new HashSet<State>();
		HashSet<SynopticState> final_synStates=new HashSet<SynopticState>();
		HashSet<Transition> allTransitions=new HashSet<Transition>();
		HashMap<StateNode,SynopticState> synStates=new HashMap<StateNode,SynopticState>();
		boolean vide=false;
		for(Trace t:traces){
			SynopticTrace synt=new SynopticTrace();
			
			int size=t.getSize();
			if (size==0){
				vide=true;
			}
			ArrayList<Statement> thorizon=new ArrayList<Statement>();
			for(int i=1;((i<=size) && (i<=horizon));i++){
				Statement st=t.getByIndex(i);
				thorizon.add(st);
			}
			StateNode last_node=root.getSetStateNode(new ArrayList<Statement>(thorizon));
			SynopticState last_synNode=null;
			if(synStates.containsKey(last_node)){
				last_synNode=synStates.get(last_node);
			}
			else{
				last_synNode=new SynopticState();
				synStates.put(last_node, last_synNode);
			}
			//all_states.add(last_node);
			all_synStates.add(last_synNode);
			//initial_states.add(last_node);
			initial_synStates.add(last_synNode);
			
			for(int i=1;i<=size;i++){
				if (i<=(size-horizon)){
					Statement st=t.getByIndex(i+horizon);
					thorizon.add(st);
				}
				else{
					thorizon.add(new Statement());
				}
				Statement first=thorizon.remove(0);
				last_synNode.setTrace(synt,i-1);
				synt.add(first);
				
				StateNode stnode=root.getSetStateNode(new ArrayList<Statement>(thorizon));
				SynopticState synNode=null;
				if(synStates.containsKey(stnode)){
					synNode=synStates.get(stnode);
				}
				else{
					synNode=new SynopticState();
					synStates.put(stnode, synNode);
				}
				//all_states.add(last_node);
				all_synStates.add(last_synNode);
				if (i==size){
					   //final_states.add(stnode);
					   final_synStates.add(synNode);
					   all_synStates.add(synNode);
					   //all_states.add(stnode);
				}
				//last_node.addTransition(first,stnode);
				Transition tr=new Transition(last_synNode,first,synNode);
				allTransitions.add(tr);
				last_node=stnode;
				last_synNode=synNode;
			}
			last_synNode.setTrace(synt,size);
			synt.computePositions();
				
		}
		
		SynopticState source=new SynopticState();
		fsa.addStates(new ArrayList<State>(all_synStates));
		fsa.addState(source);
		fsa.setInitialState(source);
		fsa.setFinalStates(new ArrayList<State>(final_synStates));
		if (vide){
			fsa.addFinalState(source);
		}
		for(SynopticState s:initial_synStates){
			Transition tr=new Transition(source,new Statement(),s);
			fsa.addTransition(tr);
		}
		
		fsa.addTransitions(new ArrayList<Transition>(allTransitions));
		//GenerateDOT.printDot(fsa, "test2.dot");
	}

	@Override
	public FSA transform(ArrayList<Trace> traces) {
		TemporalRuleMiner ti=new TemporalRuleMiner(1,0,support);
		ti.mineRules(traces);
		TemporalRules rules=ti.getRules();
		//rules.indexByPosts();
		//HashSet<Sequence> fposts=new HashSet<Sequence>(rules.getPostfutureRules().keySet());
		//HashSet<Sequence> pposts=new HashSet<Sequence>(rules.getPostfutureRules().keySet());
		Sequence.lockedTree=false;
		
		FSA fsa = new FSA();
		
		
		if(horizon<0){
			simpleAlg(fsa,traces);
		}
		else{
			kTail(fsa,traces);
		}
	
		HashMap<Sequence, HashSet<Sequence>> futureRules=rules.getFutureRules(); 
		HashMap<Sequence, HashSet<Sequence>> pastRules=rules.getPastRules(); 
		processRules(fsa,futureRules,true);
		processRules(fsa,pastRules,false);			
			
		//GenerateDOT.printDot(fsa, "test2.dot");
		if(this.finalCoarsing>=0){
			HashSet<State> states=fsa.getStates();
			HashMap<String,HashSet<SynopticState>> horizons=new HashMap<String,HashSet<SynopticState>>();
			for(State state:states){
				SynopticState st=(SynopticState)state;
				String h=st.getHorizon(fsa.getFinalStates(),finalCoarsing);
				HashSet<SynopticState> hs=horizons.get(h);
				if(hs==null){
					hs=new HashSet<SynopticState>();
					horizons.put(h, hs);
				}
				hs.add(st);
			}
			//System.out.println(horizons);
			for(HashSet<SynopticState> hs:horizons.values()){
				HashSet<SynopticState> nhs=new HashSet<SynopticState>();
				HashSet<SynopticState> asupp=new HashSet<SynopticState>();
				
				for(SynopticState s1:hs){
					for(SynopticState s2:hs){
						if(s1.traces.size()==0){
							continue;
						}
						if(s2.traces.size()==0){
							continue;
						}
						if(s1==s2){continue;}
						if(nhs.contains(s2)){continue;}
						if(asupp.contains(s2)){continue;}
						//System.out.println(s1+"+"+s2);
						if(s1.checkCompatibility(s2)){
							/*System.out.println("compatibles ");
							System.out.println("s1.requiredFuture = "+s1.requiredFuture);
							System.out.println("s1.requiredPast = "+s1.requiredPast);
							System.out.println("s2.requiredFuture = "+s2.requiredFuture);
							System.out.println("s2.requiredPast = "+s2.requiredPast);*/
							
							
							Transition nt;
							for(Transition t:s2.getSuccesseurs()){
								nt=new Transition(s1,t.getTrigger(),t.getTarget());
								fsa.addTransition(nt);
							}
							
							for(Transition t:s2.getPredecesseurs()){
								nt=new Transition(t.getSource(),t.getTrigger(),s1);
								fsa.addTransition(nt);
							}
							if(fsa.getFinalStates().contains(s2)){
								//System.out.println("Final "+s1);
								fsa.addFinalState(s1);
							}
							if(fsa.getInitialState()==s2){
								fsa.setInitialState(s1);
							}
							fsa.removeState(s2);
							asupp.add(s2);
							s1.requiredFuture.addAll(s2.requiredFuture);
							s1.requiredPast.addAll(s2.requiredPast);
							s1.traces.addAll(s2.traces);
							
						}
					}
					nhs.add(s1);
				}
				
			}
		}
		
		
		//ArrayList<State> stateList=fsa.getStateList();
		EpsilonRemover.removeEpsilon(fsa);
		//rules.checkSimpleRules(fsa, 10000);
		//System.out.println("rules ok");
		return fsa;
	}

	
	public void processRules(FSA fsa, HashMap<Sequence, HashSet<Sequence>> rules, boolean forward){
		System.out.println((forward)?"future rules":"past rules");
		ArrayList<State> stateList;
		for(Sequence pref:rules.keySet()){
			HashSet<Sequence> posts=rules.get(pref);
			for(Sequence postf:posts){
				//System.out.println(pref+"=>"+postf+" "+forward);
				stateList=fsa.getStateList();
				//System.out.println(stateList);
				//ArrayList<State> nstateList=new ArrayList<State>();
				for(State s:stateList){
					if(fsa.getInitialState()==s){
						//System.out.println("init "+s);
						continue;
					}
					//System.out.println(s);
					//GenerateDOT.printDot(fsa, "test3.dot");
					State.nbVisitsStates++;
					SynopticState syn=(SynopticState)s;
					/*if(syn.getName().compareTo("S28")==0){
						System.out.println(syn.traces);
					}
					Keyboard.saisirLigne("tapez touche");
						*/
					HashMap<Boolean,HashSet<SimpleEntry<Integer,SynopticTrace>>> check=syn.checkRule(pref, postf, forward);
					if((check.get(true).size()>0) && (check.get(false).size()>0)){
						//System.out.println("Separation "+syn+" pour future "+pref+"=>"+postf);
						SynopticState nun=new SynopticState();
						fsa.addState(nun);
						nun.requiredFuture.addAll(syn.requiredFuture);
						if(forward){
							nun.requiredFuture.add(postf);
						}
						else{
							nun.requiredPast.add(postf);
						}
						SynopticState ndeux=new SynopticState();
						fsa.addState(ndeux);
						ndeux.requiredFuture.addAll(syn.requiredFuture);
						/*if(forward){
							ndeux.notRequiredFuture.add(postf);
						}
						else{
							ndeux.notRequiredPast.add(postf);
						}*/
						//nstateList.add(nun);
						//nstateList.add(ndeux);
						State.nbVisitsStates++;
						State.nbVisitsStates++;
						//System.out.println(nun);
						//System.out.println(ndeux);
						HashMap<SimpleEntry<Integer,SynopticTrace>,SynopticState> tr=new HashMap<SimpleEntry<Integer,SynopticTrace>,SynopticState>();
						for(SimpleEntry<Integer,SynopticTrace> t:check.get(true)){
							tr.put(t,nun);
							nun.setTrace(t);
						}
						for(SimpleEntry<Integer,SynopticTrace> t:check.get(false)){
							tr.put(t,ndeux);
							ndeux.setTrace(t);
						}
						
						for(SimpleEntry<Integer,SynopticTrace> t:tr.keySet()){
							State.nbManipSeq++;
							SynopticTrace trace=t.getValue();
							SynopticState ns=tr.get(t);
							//System.out.println("ns "+ns+" "+t.getKey()+" "+trace);
							SynopticState prev=trace.getState(t.getKey()-1);
							//System.out.println(prev);
							Transition nt=null;
							if(t.getKey()==0){
								nt=new Transition(fsa.getInitialState(),new Statement(),ns);
							}
							else{
								nt=new Transition(prev,trace.getStatement(t.getKey()-1),ns);
							}
							fsa.addTransition(nt);
							SynopticState next=trace.getState(t.getKey()+1);
							//System.out.println(next);
							if(next==null){
								fsa.addFinalState(ns);
							}
							else{
								nt=new Transition(ns,trace.getStatement(t.getKey()),next);
								fsa.addTransition(nt);
							}
							
						
						}
						
						
						
						fsa.removeState(syn);
					}
					else{
						//nstateList.add(syn);
						if(check.get(true).size()>0){
							//System.out.println(syn+" requires "+postf);
							if(forward){
								syn.requiredFuture.add(postf);
							}
							else{
								syn.requiredPast.add(postf);
							}
						}
						/*else{
							if(forward){
								syn.notRequiredFuture.add(postf);
							}
							else{
								syn.notRequiredPast.add(postf);
							}
						}*/
					}
				}
				//stateList=nstateList;
			}
		}
	}
	
	@Override
	public String getName() {
		return "Synoptic_k="+horizon+"_support="+support+"_finalCoarsing="+finalCoarsing;
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
		Synoptic alg=new Synoptic(0.0,-1,0);
		FSA fsa=alg.transform(traces);
		GenerateDOT.printDot(fsa, "test.dot");
		System.out.println(State.nbManipSeq);
		System.out.println(State.nbVisitsStates);
		
	}

}
