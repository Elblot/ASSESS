/*
 *  TemporalStateNode.java
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
import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;

import core.Sequence;

import fsa.GenerateDOT;
import fsa.FSA;
import fsa.State;
import fsa.Transition;
import traces.Statement;
import traces.Trace;

/**
 * 
 * A tree structure containing Temporal States.
 * 
 * @author Sylvain Lamprier
 *
 */
public class TemporalStateNode{
		private HashMap<String, TemporalStateNode> childs; // Si pas feuille, noeud fils
		//private State state=null;   // Si feuille, contient l'etat
		private Statement statement=null;
		//private HashMap<StateNode, Transition> transitions; 
		//private HashSet<Transition> transitions;
		private TemporalStateNode parent=null;
		private ArrayList<TemporalState> states;
		public static int mode=0;
		static int updateMode=1; // 0 => intersections / unions d'ensembles, 1=> retraits / ajouts d'ensembles
		
		//public static HashMap<String,HashSet<String>> future=null;
		//public static HashMap<String,HashSet<String>> past=null;
		public TemporalStateNode(){   
			childs=new HashMap<String,TemporalStateNode>();
			//transitions=new HashMap<StateNode,Transition>();
			//transitions=new HashSet<Transition>();
			states=new ArrayList<TemporalState>();
			//state=new State();
			//state=null;
		}
		public TemporalStateNode(Statement s, TemporalStateNode parent){
			childs=new HashMap<String,TemporalStateNode>();
			statement=s;
			this.parent=parent;
			states=new ArrayList<TemporalState>();
			//state=new State();
			//transitions=new HashMap<StateNode,Transition>();
			//transitions=new HashSet<Transition>();
		}
		public TemporalStateNode getSetTemporalStateNode(ArrayList<Statement> s){
			TemporalStateNode ret=null;
			if ((s.size()==0)){
				ret=this;
			}
			else{
				Statement statement=s.remove(0);
				//System.out.println("Statement : "+statement);
				String sttext=statement.getText();
				
				if (!childs.containsKey(sttext)){
					TemporalStateNode sn=new TemporalStateNode(statement,this);
					childs.put(sttext,sn);
				}
				if (sttext.length()==0){
					s=new ArrayList<Statement>();
				}
				ret=childs.get(sttext).getSetTemporalStateNode(s);
			}
			return(ret);
		}
		
		
		

			

		public TemporalState getSetTemporalState(TemporalState ts, TemporalState last_state, FSA lts, Statement statement, ArrayList<Statement> s,HashSet<TemporalState> initial_states, HashSet<TemporalState> final_states){
			//System.out.println("getSetTemporalState pour "+ts+" et "+statement);
			TemporalStateNode node=getSetTemporalStateNode(s);
			TemporalState state=null;
			 
			 
			boolean fusion=false;
			boolean blem=false;
			for(TemporalState temp:node.states){
				boolean ok=false;
				if (mode>=0){
					if (temp.checkConstraintsCompatibility(ts)){
						ok=true;
					}
				}
				else{
					if (mode==-2){
						ok=temp.exactLoChecking(ts,last_state,lts,statement,initial_states,final_states);
					}
					else{
						ok=temp.approxLoChecking(ts,last_state,lts,statement);
					}
					
					
				}
				
				//System.out.println(statement);
				//temp.display(false);
				//ts.display(false);
				
				if (ok){
					state=temp;
					fusion=true;
					//System.out.println("fusion "+ts+" avec "+temp);
					break;
				}
				else{
					//System.out.println("pas de fusion de "+ts+" avec "+temp);
					//System.out.println("pas fusion");
					blem=true;
				}
			}
			if (!fusion){
				state=new TemporalState(ts.requiredPast,ts.requiredFuture,ts.ensuredPast,ts.ensuredFuture,ts.past,ts.future);
				/*state.beganFutureSequences=ts.beganFutureSequences;
				state.beganPastSequences=ts.beganPastSequences;
				state.forbidenFuture=ts.forbidenFuture;
				state.forbidenPast=ts.forbidenPast;*/
				node.states.add(state);
				lts.addState(state);
			}
			
			if (last_state!=null){
				Transition tr=new Transition(last_state,statement,state);
				lts.addTransition(tr);
			}
			
			if (mode>=0){
				if(updateMode==0){
					HashSet<Sequence> requiredPast=TemporalState.union(state.requiredPast,ts.requiredPast);
					HashSet<Sequence> requiredFuture=TemporalState.union(state.requiredFuture,ts.requiredFuture);
					HashSet<Sequence> past=TemporalState.intersect(state.past,ts.past);
					HashSet<Sequence> future=TemporalState.intersect(state.future,ts.future);
					HashSet<Sequence> lrequiredPast=TemporalState.copy(requiredPast);
					if(statement!=null){
						String ch=statement.getText();
						Sequence st=TemporalState.root.getForwardChild(ch);
						lrequiredPast.remove(st);
					}
					
					if (last_state!=null){
						last_state.updateRequiredPast(lrequiredPast);
					}
					state.updateRequiredPast(requiredPast);
					state.updateRequiredFuture(requiredFuture);
					state.updateFuture(future);
					state.updatePast(past);
				
					ts.updatePast(past);
					ts.updateRequiredFuture(requiredFuture);
					
					
				}
				else{
					HashSet<Sequence> requiredPast=TemporalState.union(state.requiredPast,ts.requiredPast);
					if(statement!=null){
						String ch=statement.getText();
						Sequence st=TemporalState.root.getForwardChild(ch);
						requiredPast.remove(st);
					}
					
					if (last_state!=null){
						last_state.updateRequiredPast(requiredPast);
					}
					HashSet<Sequence> srp=TemporalState.copy(state.requiredPast);
					HashSet<Sequence> srf=TemporalState.copy(state.requiredFuture);
					HashSet<Sequence> sp=TemporalState.copy(state.past);
					HashSet<Sequence> sf=TemporalState.copy(state.future);
					HashSet<Sequence> trp=TemporalState.copy(ts.requiredPast);
					HashSet<Sequence> trf=TemporalState.copy(ts.requiredFuture);
					HashSet<Sequence> tp=TemporalState.copy(ts.past);
					HashSet<Sequence> tf=TemporalState.copy(ts.future);
					
					state.updateRequiredPast(TemporalState.firstMinusSecond(trp,srp),1);
					state.updateRequiredFuture(TemporalState.firstMinusSecond(trf,srf),1);
					state.updatePast(TemporalState.firstMinusSecond(sp,tp),1);
					state.updateFuture(TemporalState.firstMinusSecond(sf,tf),1);
					
					ts.updateRequiredFuture(TemporalState.firstMinusSecond(srf,trf),1);
					ts.updatePast(TemporalState.firstMinusSecond(tp,sp),1);
				}
				
			}
			/*if (NewTemporalState.mode>=0){
				
			}*/
			/*else{
				System.out.println("Ensure ok");
			}*/
			//
			//System.out.println("fin"+lts.getStates().size());
			/*if (blem){
				GenerateDOT.printDot(lts, "a.dot");
				Reader reader = new InputStreamReader(System.in);
				BufferedReader input = new BufferedReader(reader);
				System.out.print("tapez touche pour continuer");
				try{
					String ok = input.readLine();
				}
				catch(IOException e){System.out.println(e);}
			}*/
			
			return(state);
		}
		
		
		
}
