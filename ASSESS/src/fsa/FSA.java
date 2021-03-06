/*
 *  FSA.java
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


package fsa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;
import java.math.BigInteger;

import traces.Statement;
import traces.Trace;
import java.util.HashMap;
import java.util.LinkedHashMap;
/**
 * Class representing a Finite State Automaton behavioral model. 
 * 
 * @author Nicolas Baskiotis
 * @author Lom Messan Hillah
 * @author Tewfik Ziaidi
 * @author Sylvain Lamprier
 *
 */
public class FSA implements Serializable{
	
	/* initial state for FSA */
    
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Initial State
	 */
	private State initialState;
	
	// lamprier : Un ou plusieurs etats finaux ???
	//private State finalState;

	/** 
	 * Final States 
	 */
	private HashSet<State> finalStates;
	
	/** 
	 * Transitions list 
	 */
	private HashMap<String,Transition> transitions;

	/**
	 * Set of states
	 */
	//private LinkedHashSet<State> states;
	private LinkedHashMap<String,State> states;

	// lamprier : Pourquoi sauvegarder des traces ici ?
	//private ArrayList<Trace> traces;

	// lamprier : je ne comprends pas trop a quoi cet attribut correspond
	//private ArrayList<LTS> subLTSList;

	//lamprier : pour la generation uniforme
	//private HashMap<Transition,ArrayList<BigInteger>> transit;
	/**
	 * Possible lengths for traces generation
	 */
	private ArrayList<Integer> possibleLengths=null;
	
	/**
	 * Maximal length for traces generated by method genereTrace
	 */
	public int max_trace_length=0; 
	//public int longueur_min_traces=0; 
	
	
	/**
	 *  default constructor 
	 */
	public FSA() {

		finalStates = new  HashSet<State>();
		transitions = new LinkedHashMap<String,Transition>();
		//states = new LinkedHashSet<State>(); 
		states = new LinkedHashMap<String,State>(); 
		
		//traces = new ArrayList<Trace>();
		//subLTSList = new ArrayList<LTS>();
		
	}

	/**
	 * Constructor that takes as parameter a list trace and builds a prefix-tree automaton
	 * @param listeTrace
	 */
	public FSA(ArrayList<Trace> listeTrace) {

		finalStates = new HashSet<State>();
		transitions  =new LinkedHashMap<String,Transition>();
		//states = new LinkedHashSet<State>();
		states = new LinkedHashMap<String,State>(); 
		//subLTSList = new ArrayList<LTS>();
		//traces = listeTrace;
		int identifiant = 0;

		State initialState = new State("S0"); // L'etat initial
		State finalState = new State("E"); // set the final state
		addState(initialState);
        addState(finalState);
		setInitialState(initialState); // set the initial state
		setFinalState(finalState);
        
		for (Trace trace : listeTrace) {
			State currentState = initialState; // current state
			ArrayList<Statement> liste_Statements = new ArrayList<Statement>();
			liste_Statements = trace.getStatements();
			int size = trace.getSize();
			State state = null;
			if (size==0){
				this.addFinalState(initialState);
			}
			for (int i = 0; i < size; i++) {
				ArrayList<Transition> lt=currentState.getSuccesseurs(liste_Statements.get(i));
				if(lt.size()>0){
					Transition te=lt.get(0);
					if(te.getTarget()!=finalState){
						te.addWeight(1);
						currentState = te.getTarget();
						continue;
					}
				}
				
				if (i == size - 1) {
						state = finalState;
				} else {
						identifiant++;
						state = new State("S" + identifiant);
						addState(state);
				}
				Transition t = new Transition(currentState, liste_Statements.get(i), state);
				addTransition(t);
				currentState = state;
				
			}
		}
		//finalState.setId(identifiant+1);
		
	}

	public void addTransitions(ArrayList<Transition> tr){
		//transitions.addAll(tr);
		for(Transition t:tr){
			/*State source=t.getSource();
			State target=t.getTargetState();
			source.addSuccesseur(t);
			target.addPredecesseur(t);*/
			//System.out.println("tr: " + tr.toString());
			addTransition(t);
		}
	}
	
	public void addStates(ArrayList<State> states){
		//this.states.addAll(states);
		for(State s:states){
			this.states.put(s.getName(),s);
		}
	}
	public void addStates(Set<State> states){
		//this.states.addAll(states);
		for(State s:states){
			this.states.put(s.getName(),s);
		}
	}
	
	public void setStates(ArrayList<State> new_states) {
		states = new LinkedHashMap<String,State>();
		for(State s:new_states){
			this.states.put(s.getName(),s);
		}
	}
	
	public  void setFinalState(State finalState) {
		finalStates=new HashSet<State>();
		finalStates.add(finalState);
	}

	public State getState(String name){
		return states.get(name);
	}
	
	/**
	 * Returns one final state. (only to be used when we are sure that there exist only one final state). 
	 * @return a state
	 */
	public State getFinalState(){
		if (finalStates.size()==0){
			return null;
		}
		State ret=null;
		for(State s:finalStates){
			ret=s;
			break;
		}
		return(ret);
	}
	
	public void addState(State state) {
		//this.states.add(state);
		this.states.put(state.getName(), state);
		//System.out.println("Add state "+state);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void removeState(State state) {
		if (initialState==state){
			initialState=null;
			System.out.println("Plus d etat initial !!");
		}
		if (finalStates.contains(state)){
			finalStates.remove(state);
			if (finalStates.size()==0){
				System.out.println("Plus d etat final !!");
			}
		}
		ArrayList<Transition> suc=state.getSuccesseurs();
		for(Transition t:suc){
			removeTransition(t);
		}
		ArrayList<Transition> pred=state.getPredecesseurs();
		for(Transition t:pred){
			removeTransition(t);
		}
		this.states.remove(state);
	}
	
	public void addFinalState(State finalstate) {
		this.finalStates.add(finalstate);
	}
	public void removeFinalState(State finalstate) {
		this.finalStates.remove(finalstate);
	}

	public void addTransition(Transition transition) {
		addTransition(transition,true);
	}
	
	public void mergeState(State state1, State state2) {
		int nPred = state2.getPredecesseurs().size();
		for(int i = 0; i < nPred; i++) {
			Transition t = new Transition(state2.getPredecesseurs().get(0).getSource(), state2.getPredecesseurs().get(0).getTrigger(), state1);
			if(state2.getPredecesseurs().get(0).getSource() != state1) {
				state1.addPredecesseur(t);
				this.addTransition(t);
			}
			this.removeTransition(new Transition(state2.getPredecesseurs().get(0).getSource(), state2.getPredecesseurs().get(0).getTrigger(), state2));
		}
		int nSucc = state2.getSuccesseurs().size();
		for(int i = 0; i < nSucc; i++) {
			Transition t = new Transition(state1, state2.getSuccesseurs().get(0).getTrigger(), state2.getSuccesseurs().get(0).getTarget());
			if(state1 != state2.getSuccesseurs().get(0).getTarget()) {
				state1.addSuccesseur(t);
				this.addTransition(t);
			}
			this.removeTransition(new Transition(state2, state2.getSuccesseurs().get(0).getTrigger(), state2.getSuccesseurs().get(0).getTarget()));
		}
		if(this.finalStates.contains(state2)) {
			this.finalStates.add(state1);
		}
		if(this.initialState.equals(state2)) {
			this.initialState = state1;
		}
		this.removeState(state2);
	}
	
	public void hideCall() {
		ArrayList<Transition> a = this.getTransitions();
		ArrayList<Transition> sauv = new ArrayList<Transition>();
		int size = a.size();
		boolean b = true;
		while(b) {
			a = this.getTransitions();
			a.removeAll(sauv);
			size = a.size();
			b = false;
			for(int i = 0; i < size; i++) {
				if (a.get(i).toString().contains("call")) {
					State state1 = a.get(i).getSource();
					State state2 = a.get(i).getTarget();
					this.removeTransition(a.get(i));
					this.mergeState(state1, state2);
					b = true;

					break;
				}
				if (a.get(i).toString().contains("return")) {
					State state1 = a.get(i).getSource();
					State state2 = a.get(i).getTarget();
					this.removeTransition(a.get(i));
					this.mergeState(state1, state2);
					b = true;

					break;
				}
				sauv.add(a.get(i));
			}
		}
	}
	
	
	
	
	
	/*public void hideCall() {
		ArrayList<Transition> a = this.getTransitions();
		int size = a.size();
		for(int i = 0; i < size; i++) {
			System.out.println(a.get(i));
			if (a.get(i).toString().contains("call")) {
				State state1 = a.get(i).getSource();
				State state2 = a.get(i).getTarget();
				this.removeTransition(a.get(i));
				this.mergeState(state1, state2);
			}
			if (a.get(i).toString().contains("return")) {
				State state1 = a.get(i).getSource();
				State state2 = a.get(i).getTarget();
				this.removeTransition(a.get(i));
				this.mergeState(state1, state2);
			}
		}
	}*/
	
	/**
	 *  Adds a given transition to the FSA.
	 *  If the transition does not already belong to the FSA, adds the transition as a successor of the source state and as a predecessor of the target state 
	 * 
	 *  @param transition   transition to add
	 *  @param addStates 	If true => add states to fsa if not alredy belong to
	 */
	public void addTransition(Transition transition,boolean addStates) {
		String str=transition.toString();
		Transition te=transitions.get(str);
		if(te!=null){
			te.setWeight(te.getWeight()+transition.getWeight());
			return;
		}
		this.transitions.put(str,transition);
		State source=transition.getSource();
		//System.out.println("so: " + source.toString());
		State target=transition.getTarget();
		//System.out.println("ta: " + target.toString());
		if (addStates){
			if (!this.states.containsKey(source.getName())) {
				this.states.put(source.getName(),source);
			}
			if (!this.states.containsKey(target.getName())) {
				this.states.put(target.getName(),target);
			}
		}
		source.addSuccesseur(transition);
		target.addPredecesseur(transition);
	}

	
	public void removeTransition(Transition transition) {
		transitions.remove(transition.toString());
		State source=transition.getSource();
		State target=transition.getTarget();
		source.removeSuccesseur(transition);
		target.removePredecesseur(transition);
	}
	
	// lamprier : Oula, ca m'a l'air d'etre nimp cette removeTransition !! Quand on retire une transition, on retire automatiquement les states qui lui correspondent ?
	// Je met en removeTransition_old pour ne pas la supprimer mais je pense qu'il faudra le faire (on peut envisager de retirer les State mais il faut d'abord regarder si ils ne sont plus du tout lies au fsa)
/*	public void removeTransition_old(Transition transition) {
		
		Iterator<Transition> tranIter = transitions.iterator();
		 while(tranIter.hasNext()) {
			 Transition trans = (Transition) tranIter.next();
				if (trans.equals(transition))
					tranIter.remove();
		}
		
        Iterator<State> it = states.iterator();
		 while(it.hasNext()) {
				State state = (State) it.next();
				if (state.equals(transition.getTargetState()))
				  it.remove();
		}
		 
		 it = states.iterator();
		 while(it.hasNext()) {
				State state = (State) it.next();
				if (state.equals(transition.getSource()))
				  it.remove();
		}
	
	}
	*/
	
	public State getInitialState() {
		return this.initialState;
	}

	/*public ArrayList<State> getFinalStates() {
		if (subLTSList.isEmpty())
		return finalStates;
		ArrayList<State> list = new ArrayList<State>();
		for (LTS lts : subLTSList ) {
		      list.addAll(lts.getFinalStates());	
		}
		return list;
	}*/

	/*public ArrayList<State> getFinalStates() {
		ArrayList<State> fs=new ArrayList<State>(finalStates);
		return fs;
	}*/

	public HashSet<State> getFinalStates() {
		return finalStates;
	}
	
	public ArrayList<Transition> getTransitions() {
		ArrayList<Transition> list = new ArrayList<Transition>();
		list.addAll(transitions.values());
		return(list);
	}	
	/*public ArrayList<Transition> getTransitions() {
		ArrayList<Transition> list = new ArrayList<Transition>();
		list.addAll(transitions);
		if (subLTSList.isEmpty())
			return list;
		
		for (LTS lts : subLTSList) {
			list.addAll(lts.getTransitions());
		}
		return list;
	
	}*/
	
	// La meme mais en faisant une copie des transitions
	public ArrayList<Transition> getTransitionsCopy() {
		ArrayList<Transition> list = new ArrayList<Transition>();
		for(Transition t:transitions.values()){
		   list.add(new Transition(t));
		}
		
		return list;
	
	}

	
	public HashSet<State> getStates() {
		
		return(new LinkedHashSet<State>(states.values()));
		//return states;
	}
	public ArrayList<State> getStateList() {
		
		ArrayList<State> list = new ArrayList<State>();
		list.addAll(states.values());
		return(list);
	}
	
	/*public ArrayList<State> getStates() {
		
		ArrayList<State> list = new ArrayList<State>();
		list.addAll(states);
		if (subLTSList.isEmpty())
				return list;
		for (LTS lts : subLTSList) {
			list.addAll(lts.getStates());
		}
		return list;
	}*/


	/*public ArrayList<Trace> getTraces() {
		return this.traces;
	}*/

	public ArrayList<Trace> genereTraces(int nb) {
		if (possibleLengths==null){
			computeUnifDistribution(max_trace_length);
		}
		
		ArrayList<Trace> traces=new ArrayList<Trace>();
		for(int i=0;i<nb;i++){
			int l=getRandLongueur();
			if (l>=0){
			   traces.add(generationUnif(l));
			}
			//traces.add(genereTrace());
		}
		return(traces);
	}
	
	public ArrayList<Integer> getLongueurPossibles(){
		return(this.possibleLengths);
	}
	
	
	// retourne une longueur possible (-1 si aucune possible)
	public int getRandLongueur(){
		int ret=0;
		if (possibleLengths.size()==0){
			return(-1);
		}
		int lmax=possibleLengths.get(possibleLengths.size()-1);
		int lmin=possibleLengths.get(0);
		//int pos=(int)(Math.random()*(longueur_max_traces+1));
		//int pos=(int)(Math.random()*(lmax+1));
		int pos=(int)(Math.random()*(lmax-lmin+1))+lmin;
		ret=chercherPlusProcheLongueur(pos);
		
		//int pos=(int)(Math.random()*(this.longueurs_possibles.size()));
		//ret=this.longueurs_possibles.get(pos);
		return(ret);
	}
	
	public int chercherPlusProcheLongueur(int l){
		//System.out.println("cherche "+l);
		int min=-1;
		int lmin=-1;
		for(int i=0;i<this.possibleLengths.size();i++){
			int x=this.possibleLengths.get(i);
			int diff=Math.abs(l-x);
			if ((min==-1) || (diff<min)){
				min=diff;
				lmin=x;
			}
		}
		//System.out.println("ok "+lmin);
		return(lmin);
		
	}
	
	
	public void computeUnifDistribution(int lmax){
		
		//transit=new HashMap<Transition,ArrayList<BigInteger>>();
		max_trace_length=lmax;
		possibleLengths=new ArrayList<Integer>();
		if (finalStates.contains(initialState)){
			possibleLengths.add(0);
		}
		
		for (Transition t:transitions.values()){
			ArrayList<BigInteger> g=new ArrayList<BigInteger>(lmax);
			for (int ii=0;ii<lmax;ii++){
				g.add(BigInteger.valueOf(0));
			}
			//transit.put(t,g);
			t.setTransit(g);
		}
		for (State s:this.finalStates){
			ArrayList<Transition> trIn=s.getPredecesseurs();
			ArrayList<BigInteger>g;
			boolean lpossible=false;
			for (Transition tr:trIn){
				//g=transit.get(tr);
				g=tr.getTransit();
				g.set(0, BigInteger.valueOf(1));
				//transit.put(tr,g);
				State src = tr.getSource();
				if ((!lpossible) && (src==initialState)){
					possibleLengths.add(1);
					lpossible=true;
				}
			}	
		}
		for (int lg=1;lg<lmax;lg++){
				//System.out.println(lg);
				boolean lpossible=false;
				for (Transition tr:transitions.values()){
						//System.out.println("Pour :"+tr);
					    //ArrayList<BigInteger> res=transit.get(tr);
					    ArrayList<BigInteger> res=tr.getTransit();
				    
					    State tg = tr.getTarget();
						ArrayList<Transition> suc=tg.getSuccesseurs();
						ArrayList<BigInteger> resSuc;
						for (Transition tgSuc:suc){
							//resSuc=transit.get(tgSuc);
							resSuc=tgSuc.getTransit();
							BigInteger ss=res.get(lg).add(resSuc.get(lg-1));
							/*if (ss>100000){
						
								ss=100000;
							}*/
							res.set(lg,ss);
							//System.out.println(lg+","+tgSuc+":"+resSuc.get(lg-1));
						}
						State src = tr.getSource();
						if ((!lpossible) && (src==initialState) && (res.get(lg).longValue()>0)){
							possibleLengths.add(lg+1);
							lpossible=true;
						}
						//System.out.println(lg+","+tr+":"+res.get(lg));
				}
		}	
		
		
		
/*		for (int lg=0;lg<lmax;lg++){
			for (Transition tr:transitions){
				if (transit.containsKey(tr)){
					State tg = tr.getSource();
					ArrayList<Transition> pred=tg.getPredecesseurs();
					ArrayList<Double> res= transit.get(tr);
					double resPred;
					double sc;
					sc=res.get(lg);
					if ((pred.size()>0)){
						if (lg<(lmax-1)){
							resPred=(transit.get(pred.get(0))).get(lg+1);
							if (resPred!=0){
								res.set(lg,sc/resPred);
							}
						}
					}
					else{
						double count=0;
						for (Transition initTr:tg.getSuccesseurs()){
							count+=(transit.get(initTr)).get(lg);
						}
						if (count!=0) res.set(lg,sc/count);	
					}	
						
				}	
			}
		}
		*/
		/*unifDistrib=new ArrayList<HashMap<Transition,Double>>();
		for (Transition tr:transitions){
			
		}*/
		//System.out.println("Distrib computed");
	}
	
	public Trace generationUnif(int lmax){
		return(generationUnif(lmax,false));
	}
	
	public Trace generationUnif(int lmax,boolean verbose){
		if (possibleLengths==null){
			computeUnifDistribution(max_trace_length);
		}
		if (verbose){
			//System.out.println("lmax = "+lmax);
		}
		Trace trace=new Trace();
		//TraceGenResult trg=new TraceGenResult();
		//trg.addTrace(trace);
		State current=initialState;
		
		//System.out.println(lmax+","+this.longueur_max_traces+","+this.longueur_min_traces);
		//lmax=this.longueur_max_traces;
		//ArrayList<Integer> lpossibles=new ArrayList<Integer>();
		double sum=0.0;
		BigInteger sumTot=BigInteger.ZERO;
		/*while(lmax>0){
			ArrayList<Transition> suc=current.getSuccesseurs();
			for(Transition tr:suc){
				double p=transit.get(tr).get(lmax-1);
				System.out.println(tr+"="+p);
				sumTot+=p;
			}
			lmax--;
		}*/
		if (lmax==0){
			return(trace);
		}
		//lmax++;
		
		for(int i=(lmax-1);i>=0;i--){
			
			ArrayList<Transition> suc=current.getSuccesseurs();
			
			sum=0.0;
			sumTot=BigInteger.ZERO;
			Transition choix=null;
			
			for(Transition tr:suc){
				
				BigInteger p=tr.getTransit(i);
				if (verbose){
					//System.out.println(i+":"+tr+"="+p);
				}
				
				sumTot=sumTot.add(p);
			}
			double x=Math.random();
			double stot=sumTot.doubleValue();
			//System.out.println(sumTot+"=>"+stot);
			for(Transition tr:suc){
				double p=0.0;
				//try{
					
				p=tr.getTransit(i).doubleValue()/stot;
				/*}
				catch(java.lang.OutOfMemoryError e){
					System.out.println(tr.getTransit(i));
					System.out.println(e);
					throw e;
				}*/
				
				sum+=p;
				if (verbose){
				   //System.out.println(tr.getTransit(i).doubleValue()+" "+stot+" "+p+" "+sum+" "+x);
				}
				if (x<sum){
					choix=tr;
					break;
				}
				
			}
			//System.out.println(sumTot+","+sum+","+x+","+suc.size());
			/*if ((choix==null) && (!verbose)){
				System.out.println(sumTot+","+sum+","+x+","+suc.size()+","+lmax);
				generationUnif(lmax,true);
			}*/
			if (choix==null){return(genereTrace());} // Il y a un probleme (certainement la conversion doubleValue qui ne tient pas dans un double et donne infinity) mais tant pis, on s'en sort en appelant l'autre methode moins formelle de generation de traces
			Trigger trig=choix.getTrigger();
			trace.add((Statement)trig);
			current=choix.getTarget();
		}
		//System.out.println("taille trace = "+trace.getSize());
		return(trace);
	}
	
	
	
	
	public double getEsperanceLongueur(){
		if (possibleLengths==null){
			computeUnifDistribution(max_trace_length);
		}
		BigInteger sum=BigInteger.ZERO;
		BigInteger nb=BigInteger.ZERO;
		ArrayList<Transition> trans=initialState.getSuccesseurs();
		
		for(int i=0;i<this.possibleLengths.size();i++){
			int l=this.possibleLengths.get(i);
			if (l==0){
				nb=nb.add(BigInteger.ONE);
			}
			else{
			  for(Transition tr:trans){
				BigInteger n=tr.getTransit(l-1);
				//System.out.println(l+":"+tr+"="+n);
				nb=nb.add(n);
				sum=sum.add(n.multiply(BigInteger.valueOf(l)));
			  }
			}
		}
		double ret=0.0;
		if (nb.intValue()>0){
			ret=(sum.doubleValue())/nb.doubleValue();
		}
		
		return(ret);
	}
	
	// Generation uniforme de traces a partir du FSA
	public Trace genereTrace() {
		Trace trace=new Trace();
		//TraceGenResult trg=new TraceGenResult();
		//trg.addTrace(trace);
		boolean ok=false;
		State current=initialState;
		while(!ok){
			ArrayList<Transition> trans=current.getSuccesseurs();
			int nb=trans.size();
			if (finalStates.contains(current)){
				nb++;
			}
			int choix=(int)(Math.random()*nb);
			if (choix==trans.size()){
				ok=true;
			}
			else{
				Transition choisie=trans.get(choix);
				Trigger trig=choisie.getTrigger();
				State cible=choisie.getTarget();
				if (!(trig instanceof Statement)){
					System.out.println("Probleme => Trigger non Statement... Que faire ?");
				}
				else{
					if(EpsilonTransitionChecker.isEpsilonTransition(choisie)){
						Statement st=(Statement)trig;
						trace.add(st);
						//System.out.println("Add "+st);
					}
				}
				current=cible;
			}
		}
		return(trace);
	}
	
	// Ne fonctionne que sur les non Epsilon transitions 
	public boolean accepteTrace(Trace t){
		State current=initialState;
		ArrayList<Statement> ls=t.getStatements();
		Stack<Trigger> pile=new Stack<Trigger>();
		// On veut pouvoir gerer comme une pile, donc on insere dans un Stack (sinon decalages couteux a chaque iteration)
	    int i;
		for(i=ls.size()-1;i>=0;i--){
			Statement s=ls.get(i);
	    	//System.out.println("Ajout pile : "+s);
	    	pile.push(s);
	    }
		for(State state:states.values()){
			state.reinitScoresSousTrace();
		}
		if (pile.size()>0){
	        return(accepteTraceFrom(pile,current));
		}
		return(finalStates.contains(current));
	}
	
	
	
	public boolean accepteTraceFrom(Stack<Trigger> trace,State state){
		//System.out.println("Taille trace ="+trace.size());
		if (trace.isEmpty()){
			return(finalStates.contains(state));
		}
		int sizet=trace.size();
		int deja=state.getScoreSousTrace(sizet);
		if (deja!=-1){
			return(false);
		}
		state.setScoreSousTrace(sizet,1);
		
		Trigger st=trace.peek();
		//System.out.println("ici : "+st+" a etat = "+state);
		ArrayList<Transition> possibilites=getTransitions(state,st);
		//possibilites.addAll(getTransitions(state,new Statement())); // On y ajoute les eventuelles epsilon transitions (meme si de preference on travaille sans epsilon)
		if (possibilites.size()==0){
			return(false);
		}
		boolean ok=false;
		int i=0;
		while((!ok) && (i<possibilites.size())){
			Transition trans=possibilites.get(i);
			//if (!(new LTSEpsilonTransitionChecker()).isEpsilonTransition(trans)){
			    trace.pop();
			//}
			//System.out.println(state+" : Test pour "+st+" transition = "+trans);
			ok=accepteTraceFrom(trace,trans.getTarget());
			/*if (!ok){
				System.out.println("Echec pour "+st+" transition = "+trans);
			}*/
			//if (!(new LTSEpsilonTransitionChecker()).isEpsilonTransition(trans)){
				trace.push(st);
			//}
			
		    i++;
		}
		
		return(ok);
	}
	
	
	// Meme chose que accepteTraceFrom mais en non recursif 
	public boolean accepteTraceFrom2(Stack<Trigger> trace,State state){
		//System.out.println("accepte2");
		//boolean ok=false;
		Stack<ArrayList<Transition>> todo=new Stack<ArrayList<Transition>>();
		Stack<Trigger> trigs=new Stack<Trigger>();
		Trigger st=trace.peek();
		ArrayList<Transition> trans=state.getSuccesseurs(st);
		//trans.addAll(state.getSuccesseurs(new Statement()));
		todo.push(trans);
		trigs.push(new Statement());
		while(!todo.isEmpty()){
			trans=todo.peek();
			if(trans.size()==0){
				todo.pop();
				Trigger trig=trigs.pop();
				//if (trig.toString().compareTo("")!=0){
					trace.push(trig);
				//}
			}
			else{
				Transition tr=trans.remove(trans.size()-1);
				Trigger trig=tr.getTrigger();
				//if (trig.toString().compareTo("")!=0){
				    trace.pop();
				//}
		    	State target=tr.getTarget();
		    	trigs.push(trig);
		    	if(trace.isEmpty()){
		    		if (finalStates.contains(target)){
		    			return(true);
		    		}
		    		else{
		    			trans=new ArrayList<Transition>();
			    		//trans.addAll(state.getSuccesseurs(new Statement()));
			    		todo.push(trans);
		    		}
		    	}
		    	else{
		    		st=trace.peek();
		    		trans=target.getSuccesseurs(st);
		    		//trans.addAll(state.getSuccesseurs(new Statement()));
		    		todo.push(trans);
		    	}
		    }
		}
		
		return(false);
	}
	
	//Retourne la taille de la plus grande sequence de Statement reconnue (a n'importe quel niveau du FSA) / la taille de la trace complete
	// Ne fonctionne que sur les non epsilon
	public double accepteSousTrace(Trace t){
		
		ArrayList<Statement> ls=t.getStatements();
		Stack<Statement> pile=new Stack<Statement>();
		// On veut pouvoir gerer comme une pile, donc on insere dans un Stack (sinon decalages couteux a chaque iteration)
		int i;
		for(i=ls.size()-1;i>=0;i--){
			Statement s=ls.get(i);
	    	//System.out.println("Ajout pile : "+s);
	    	pile.push(s);
	    }
		
		for(State state:states.values()){
			state.reinitScoresSousTrace();
		}
		
		int max=0;
		// tant qu'on peut faire mieux
		while(pile.size()>max){
			
			Statement st=pile.peek();
			//System.out.println(st+" "+max);
			ArrayList<Transition> possibilites=getTransitions(st);
			if (possibilites.size()>0){
				for(Transition trans:possibilites){
					State etat=trans.getSource();
					int nb=nbAccepteTraceFrom(pile,etat);
					//System.out.println(nb);
					if(nb>max){
						max=nb;
					}
					
				}
			}
			pile.pop();
			
		}
		double ret=0.0;
		if(ls.size()>0){
			ret=((double)max)/ls.size();
		}
		else{
			if (finalStates.contains(initialState)){
				ret=1.0;
			}
		}
		
	    return(ret);
	}
	
	
	
	// Meme que accepteTraceFrom sauf qu'on retourne une taille de sequence reconnue et qu'on peut faire terminer ou on veut
    public int nbAccepteTraceFrom(Stack<Statement> trace,State state){
		
		if (trace.isEmpty()){
			return(0);
		}
		int sizet=trace.size();
		int old_max=state.getScoreSousTrace(sizet);
		if (old_max>=0){
			return(old_max);
		}
		
		Statement st=trace.peek();
		//System.out.println("ici : "+st+" a etat = "+state);
		ArrayList<Transition> possibilites=getTransitions(state,st);
		//possibilites.addAll(getTransitions(state,new Statement())); // On y ajoute les eventuelles epsilon transitions (meme si de preference on travaille sans epsilon)
		if (possibilites.size()==0){
			return(0);
		}
		boolean ok=false;
		int i=0;
		int max=0;
		while((!ok) && (i<possibilites.size())){
			Transition trans=possibilites.get(i);
			//if (!(new LTSEpsilonTransitionChecker()).isEpsilonTransition(trans)){
			    trace.pop();
			//}
			//System.out.println("Test pour "+st+" transition = "+trans);
			int nb=nbAccepteTraceFrom(trace,trans.getTarget());
			/*if (!ok){
				System.out.println("Echec pour "+st+" transition = "+trans);
			}*/
			//if (!(new LTSEpsilonTransitionChecker()).isEpsilonTransition(trans)){
				trace.push(st);
				nb=nb+1;
			//}
			if(nb>max){
				max=nb;
			}
		    i++;
		}
		//System.out.println(max+" et "+old_max);
		state.setScoreSousTrace(sizet,max);
		return(max);
	}
    
    /*public void isolateCycles(){
    	for(State st:states){
    		
    	}
    }*/
    
    /*public void computeFutureLTS(){
    	System.out.print("ComputeFutureLTS...");
		ArrayList<State> fstates=this.getFinalStates();
		for(State st:fstates){
			st.considerFuture(null,new HashSet<State>(),this);
			st.considerSureFuture(null,new HashSet<State>(),this);
		}
		System.out.println(" OK");
		for(State st:states){
			System.out.println("Future de "+st+ ": "+st.getFutures());
			System.out.println("Future de "+st+ ": "+st.getSureFutures());
		}
		
	}*/
	
	public ArrayList<Transition> getTransitions(State source,Trigger trig){
		ArrayList<Transition> suc=source.getSuccesseurs(trig);
		return(suc);
	}
	
	public ArrayList<Transition> getTransitions(Trigger trig){
		ArrayList<Transition> ret=new ArrayList<Transition>();
		for(Transition t:transitions.values()){
			Trigger tr=t.getTrigger();
			if (tr.equals(trig)){
				ret.add(t);
			}
		}
		return(ret);
	}
	
	
	public void setInitialState(State new_initial_state) {
		this.initialState = new_initial_state;
	}


	public void setFinalStates(ArrayList<State> new_final_states) {
		this.finalStates = new HashSet<State>(new_final_states);
	}


	

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/*public String toString() {
		String str = "-->(" + initialState.toString() + ")-->";
		ArrayList<Transition> list = this.getTransitions();
		for (Transition tr : list) {
			str += tr.toString();
		}
		str += "("
				+ list.get(list.size() - 1).getTargetState()
						.getName() + ")" + "-->(" + this.finalStates.get(0)
				+ ")";
		return str;
	}*/


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o == this)
			return true;
		else if ((o == null) || (o.getClass() != this.getClass()))
			return false;
		else {
			FSA fsa = (FSA) o;

			boolean initialS = this.initialState.equals(fsa.getInitialState());
			boolean finalS = true;
			boolean trans = true;
			ArrayList<Transition> list = this.getTransitions();
			if (fsa.getTransitions().size() == list.size()) {
				for (State fs:finalStates) {
					finalS = finalS
							&& fsa.getFinalStates().contains(fs);
				}
				for (State fs:fsa.getFinalStates()) {
					finalS = finalS
							&& finalStates.contains(fs);
				}
				for (int i = 0; i < list.size(); i++) {
					trans = trans
							&& list.get(i).equals(
									fsa.getTransitions().get(i));
				}
				return initialS && finalS && trans;
			} else {
				return false;
			}
		}
	}

	/*
	 * Methode qui renvoie vrai si l'automate passe en parametre contient les
	 * meme trigger que celui en instance
	 */

	public boolean compareTrigger(FSA fsa) {
		boolean egal = true;
		if (this.getTransitions().size() != fsa.getTransitions().size())
			return false;
        ArrayList<Transition> list = this.getTransitions();
		for (int i = 0; i < list.size(); i++) {
			egal = egal
					&& list.get(i).getTrigger().equals(
							fsa.getTransitions().get(i).getTrigger());
		}

		return egal;
	}

	
	/**
	 * Replaces a state by another in the fsa. 
 	 * Moves all output or input transitions of the old state to the new one.
 	 * Both states and all of their transitions have two belong to the fsa before this operation.  
	 * @param aremp replaced state. 
	 * @param par new state.
	 */
	 public void replace(State aremp, State par){
		if(!states.containsKey(aremp.getName())){
			throw new RuntimeException("Replace : replaced state "+aremp+" needs to belong to the fsa");
		}
		if(!states.containsKey(par.getName())){
			throw new RuntimeException("Replace : replacement state "+par+" needs to belong to the fsa");
		}
		ArrayList<Transition> suc=aremp.getSuccesseurs();
		ArrayList<Transition> pred=aremp.getPredecesseurs();
		
		// Au cas ou par n appartient pas au fsa
		/*ArrayList<Transition> suc2=par.getSuccesseurs();
		ArrayList<Transition> pred2=par.getPredecesseurs();
		addState(par);
		for (Transition t : suc2) {
			addTransition(t);
		}
		for (Transition t : pred2) {
			addTransition(t);
		}*/
			
		
		for (Transition t : suc) {
			removeTransition(t);
			t.setSourceState(par);
			addTransition(t);
			/*Transition te=transitions.get(t);
			if(te!=null){
				te.setWeight(te.getWeight()+t.getWeight());
			}
			else{
				addTransition(t);
			}*/
			/*if(par.getSuccesseurs().contains(t)){
				t.setSourceState(par);
			}
			par.addSuccesseur(t);
			*/		
		}
		for (Transition t : pred) {
			removeTransition(t);
			t.setTargetState(par);
			addTransition(t);
			/*Transition te=transitions.get(t);
			if(te!=null){
				te.setWeight(te.getWeight()+t.getWeight());
			}
			else{
				addTransition(t);
			}*/
			//par.addPredecesseur(t);
			
		}
		aremp.clearPredecesseurs();
		aremp.clearSuccesseurs();
		if (finalStates.contains(aremp)){
			finalStates.remove(aremp);
			finalStates.add(par);
			
		}
		if (getInitialState()==aremp){
			setInitialState(par);
		}
		removeState(aremp);
		
	}
}