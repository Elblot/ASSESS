/*
 *  KRI.java
 * 
 *  Copyright (C) 2012-2013 Asma Khadhraoui, Sylvain Lamprier, Tewfik Ziaidi, Lom Messan Hillah and Nicolas Baskiotis
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
import java.util.Iterator;
import java.util.Map;

import fsa.GenerateDOT;
import fsa.FSA;
import fsa.State;
import fsa.Transition;

import miners.FSAminer;
import traces.Statement;
import traces.Trace;
import traces.*;

public class KRI implements FSAminer {

	private static final long serialVersionUID = 1L;
	public static final String pathToWorkFolder = "./BME_tests/";

	private int degree = 1;
	private Map<String, ArrayList<State>> stmtsPrevStates;
	
	public KRI(){
		this.stmtsPrevStates = new HashMap<String, ArrayList<State>>();
	}
	
	public KRI(int degree){
		this.stmtsPrevStates = new HashMap<String, ArrayList<State>>();
		this.degree = degree;
	}
	
	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	@Override
	public FSA transform(ArrayList<Trace> traces) {
		FSA finalLTS = new FSA();
		this.stmtsPrevStates = new HashMap<String, ArrayList<State>>();
		State rootState = new State();
		finalLTS.setInitialState(rootState);
		for (Trace t : traces) { // Parcourir toutes les traces (t represente un objet de type Trace)
			if(t.getStatements().size() == 0){
				finalLTS.addFinalState(finalLTS.getInitialState());
			}
			else{
				this.parseTrace(t, finalLTS);
			}
		}

		for(Iterator<String> iterStmts = this.stmtsPrevStates.keySet().iterator() ; iterStmts.hasNext();){
			String keyStmt = (String)iterStmts.next();
			ArrayList<State> varStates = this.stmtsPrevStates.get(keyStmt);
			if(varStates.size() > 1){	
				this.mergeStates(finalLTS, keyStmt, varStates);
			}
		}
		return finalLTS;
	}
	
	public void parseTrace(Trace t, FSA finalLTS) {
		State rightState = null;
		State leftState = finalLTS.getInitialState();
	
		String sequence = "";
		int countStmt = 0;
		
		ArrayList<String> tmpStmts = new ArrayList<String>();
		for(int i = 0; i < t.getStatements().size(); i++){
			Statement currentStmt = t.getStatements().get(i);
			tmpStmts.add(t.getStatements().get(i).toString());
			sequence += t.getStatements().get(i).toString();		// Sequence = sequence de longeur k statements
			
			countStmt++;
			State newLeftState = null;
			for(Transition tr:leftState.getSuccesseurs()){
				if(tr.getLabel().compareTo(currentStmt.toString()) == 0){
					newLeftState = tr.getTarget();
					break;
				}
			}
			
			if(newLeftState == null){		// Si aucun etat emettant la meme transition n'a ete trouve,
				rightState = new State();	// creer un nouvel etat destination 
				Transition newTransition = new Transition(leftState, currentStmt, rightState); 	// et une nouvelle transition
				finalLTS.addTransition(newTransition);	
				leftState = rightState;					// Fixer le nouvel etat a gauche comme le dernier etat ajoute
				if(countStmt == this.degree){			// Si le degre k de kRI a ete atteint, on enregistre l'etat destination comme etat ayant k precedents formant sequence
					this.addElementToStmtsPrevs(sequence, rightState);			// Ajouter sequence comme predecesseurs a rightState
					sequence = sequence.substring(tmpStmts.get(0).length());	// Preparer la nouvelle sequence
					tmpStmts.remove(0);											// Enlever le statement le plus ancien de k du tableau tmpStmts, tmpStmts aura toujours une taille <= k
					countStmt--;
				}
			}
			else{	// Si leftState emet ce meme statement ==> passer au statement suivant
				leftState = newLeftState;
				if(i == t.getStatements().size()-1){
					rightState = leftState;
				}
				if(countStmt == this.degree){			// Si le degre k de kRI a ete atteint, on enregistre l'etat destination comme etat ayant k precedents formant sequence
					sequence = sequence.substring(tmpStmts.get(0).length());	// Preparer la nouvelle sequence
					tmpStmts.remove(0);											// Enlever le statement le plus ancien de k du tableau tmpStmts, tmpStmts aura toujours une taille <= k
					countStmt--;
				}
			}
		}
		if(rightState != null){
			if(!finalLTS.getFinalStates().contains(rightState)){
				finalLTS.addFinalState(rightState);
			}
		}
	}
	
	public void addElementToStmtsPrevs(String key, State state){
		ArrayList<State> states = null;
		if( ! this.stmtsPrevStates.containsKey(key)){
			states = new ArrayList<State>();
		}
		else{
			states = this.stmtsPrevStates.get(key);
		}
		states.add(state);
		this.stmtsPrevStates.put(key, states);
	}

	/*
	 * Method : mergeStates
	 * Description : fusionne les etats d'un tableau passe en parametre, elle garde le premier etat du tableau  
	 * 					et supprime le reste des etats
	 * keyStmt : le statement rentrant dans tous les etats du tableau states
	 */
	public void mergeStates(FSA lts, String keyStmt, ArrayList<State> states){
		// Creation du nouvel etat
		State newStateMerged = new State();
		
		for(int i = 0; i < states.size(); i++){
			State tmpState = states.get(i);	// Recuperation de l'etat a fusionner
			
			for(Transition tr:tmpState.getPredecesseurs()){
				Transition newTransition = new Transition(tr.getSource(), tr.getTrigger(), newStateMerged);   // Creation de la nouvelle transition avec newStateMerged comme etat destination
				if( ! newStateMerged.getPredecesseurs().contains(newTransition) ) {   // Test de l'existence de la transition
					newStateMerged.addPredecesseur(newTransition);
					lts.addTransition(newTransition);
				}
			}
			
			for(Transition tr:tmpState.getSuccesseurs()){
				Transition newTransition = new Transition(newStateMerged, tr.getTrigger(), tr.getTarget());
				if ( ! newStateMerged.getSuccesseurs().contains(newTransition) ) {
					newStateMerged.addSuccesseur(newTransition);
					lts.addTransition(newTransition);
				}
			}
			
			if(lts.getFinalStates().contains(tmpState)){
				lts.addFinalState(newStateMerged);
			}
			
			if(lts.getInitialState().getName().compareTo(tmpState.getName()) == 0){
				lts.setInitialState(newStateMerged);
			}
			
			lts.removeState(tmpState);
		}
	}
	
	@Override
	public String getName() {
		return "KRI-" + this.degree;
	}
	
	
	public static void main(String args[]) {
		/*KRI algo = new KRI(2);
		FSA newLTS = new FSA();
		ArrayList<Trace> traces = new ArrayList<Trace>();
		try {
			traces = Trace.getTracesFromDir(pathToWorkFolder+"TracesIN/");
			newLTS = algo.transform(traces);
			GenerateDOT.printDot(newLTS,"./LTSResults/KRI_lts.dot");
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		ObjectClass dm = new ObjectClass("dummy Class");
		ObjectInstance obj = new ObjectInstance("dummy obj", dm) ;
		ArrayList<ObjectInstance> list = new ArrayList<ObjectInstance>();
		Trace t1 = new Trace();
		Method m1 = new Method("0", list, dm);
		Statement st1 = new Statement(obj, m1, obj);
		Method m3 = new Method("1", list, dm);
		Statement st3 = new Statement(obj, m3, obj);
		t1.add(st1);
		t1.add(st1);
		Trace t2 = new Trace();
		t2.add(st3);
		t2.add(st3);
		Trace t3 = new Trace();
		t3.add(st1);
		t3.add(st1);
		t3.add(st1);
		t3.add(st1);
		Trace t4 = new Trace();
		t4.add(st1);
		t4.add(st3);
		t4.add(st1);
		t4.add(st3);
		Trace t5 = new Trace();
		t5.add(st1);
		t5.add(st3);
		t5.add(st3);
		t5.add(st1);
		Trace t6 = new Trace();
		t6.add(st3);
		t6.add(st1);
		t6.add(st3);
		t6.add(st1);
		ArrayList<Trace> tabTrace=new ArrayList<Trace>();
		tabTrace.add(t1);
		tabTrace.add(t2);
		tabTrace.add(t3);
		tabTrace.add(t4);
		tabTrace.add(t5);
		tabTrace.add(t6);
		KRI angluin = new KRI(3);
		FSA fsa = angluin.transform(tabTrace);
		GenerateDOT.printDot(fsa, "test.dot");
	}
	
}