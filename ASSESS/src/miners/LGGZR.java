/*
 *  LGGZR.java
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


/**
 * 
 * The LGG-ZR algorithm, as defined in Frederic Tantini, Alain Terlutte, Fabien Torre: Sequences Classification by Least General Generalisations. ICGI 2010: 189-202. 
 * LGG-ZR is a classical grammatical inference technique that ensures that the output FSA is a 0-reversible automata that corresponds of the least general generalization of the input traces.
 * 
 * To be verified 
 * 
 * @author Asma Khadhraoui
 *
 */
public class LGGZR implements FSAminer {

	private static final long serialVersionUID = 1L;
	public static final String pathToWorkFolder = "./BME_tests/";

	@Override
	public FSA transform(ArrayList<Trace> traces) { 
		FSA finalLTS = new FSA();
		State rootState = new State();
		finalLTS.setInitialState(rootState);
		for (Trace t : traces) { // Parcourir toutes les traces (t represente un objet de type Trace)
			if(t.getStatements().size() == 0){
				finalLTS.addFinalState(finalLTS.getInitialState());
			}
			else{
				analyzeTrace(rootState, t, 0, finalLTS);	
			}
		}
		
		mergeStates(finalLTS, "", new ArrayList<State>(finalLTS.getFinalStates()));
		return finalLTS;
	}

	public FSA analyzeTrace(State rootState, Trace t, int indexStmt, FSA finalLTS) {
		Statement firstVarStmt = null;
		State rootMobile = null;
		State rightState = null;
		State leftState = null;
		if(indexStmt < t.getSize()){
			firstVarStmt = t.getStatements().get(indexStmt); // Pour chaque statement de la trace t
			if (firstVarStmt != null) { // Test object != null pour eviter des bugs eventuels lors de l'execution
				// Pour le premier statement firstVarStmt de la trace t
				for(Transition tr:rootState.getSuccesseurs()){
					if(tr.getLabel().compareTo(firstVarStmt.toString()) == 0){
						rootMobile = tr.getTarget();
						break;
					}
				}
				if (rootMobile == null) { // )) si le statement firstVarStmt n'appartient pas aux statements directement lies a la racine
											// ==> ajouter une NOUVELLE transition partant de la racine vers un nouvel etat, ajouter le reste de la trace a la suite de ce nouvel etat
					rightState = new State();
					Transition newTransition = new Transition(rootState, firstVarStmt, rightState);
					rootState.addSuccesseur(newTransition);
					rightState.addPredecesseur(newTransition);
					finalLTS.addState(rightState);
					finalLTS.addTransition(newTransition);
					leftState = rightState;
					for (int i = (indexStmt+1); i < t.getStatements().size(); i++) {
						Statement stmt = t.getStatements().get(i);
						rightState = new State();
						newTransition = new Transition(leftState, stmt, rightState);
						leftState.addSuccesseur(newTransition);
						rightState.addPredecesseur(newTransition);
						finalLTS.addState(rightState);
						finalLTS.addTransition(newTransition);
						leftState = rightState;
					}
					finalLTS.addFinalState(rightState);
				} else { // )) si celui ci appartient aux statements directement lies a la racine (rootState)
					indexStmt++;
					if(indexStmt == t.getSize() && !finalLTS.getFinalStates().contains(rootMobile)){
						finalLTS.addFinalState(rootMobile);
					}
					else{
						analyzeTrace(rootMobile, t, indexStmt, finalLTS);	
					}
				}
	
				/*
				 * Description de l'algo :
				 * 
				 * Debut de l'algo pour la trace t Pour le premier statement
				 * firstVarStmt de la trace t )) si celui ci appartient aux
				 * statements directement lies a la racine (rootState) ==>
				 * positionner une variable rootMobile sur l'etat targetState
				 * (determine a partir du rootState et le statement actuel
				 * firstVarStmt) appliquer l'algorithme recursif sur le reste de la
				 * trace avec comme racine l'etat rootMobile Pour ce faire,
				 * supprimer le statement de la trace et la passer en parametre de
				 * la fonction
				 * 
				 * )) si le statement firstVarStmt n'appartient pas au statements
				 * directement lies a la racine ==> ajouter une NOUVELLE transition
				 * partant de la racine vers un nouvel etat, ajouter le reste de la
				 * trace a la suite de ce nouvel etat
				 */
	
			}
		}
		return finalLTS;
	}

	@Override
	public String getName() {
		return "LGG-ZR";
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
				if(!lts.getFinalStates().contains(newStateMerged)){
					lts.setFinalState(newStateMerged);
				}
			}
			
			if(lts.getInitialState().getName().compareTo(tmpState.getName()) == 0){
				lts.setInitialState(newStateMerged);
			}
			
			lts.removeState(tmpState);
		}
	}
	
	public void mergeState(FSA lts, State state){
		Map<String,ArrayList<State>> map = new HashMap<String,ArrayList<State>>();
		ArrayList<Transition> predecesseurs = state.getPredecesseurs();
		ArrayList<Transition> successeurs = state.getSuccesseurs();
		int sizePredecesseurs = predecesseurs.size();
		int sizeSuccesseurs = successeurs.size();

		for(int i = 0; i < sizePredecesseurs; i++){
			// Pour chaque transition entrante dans l'etat actuel
			String keyStmt = predecesseurs.get(i).getLabel();	// Recuperer le label
			if(!map.keySet().contains(keyStmt)){				// Verifier si le label a deja ete ajoute a la map
				map.put(keyStmt, new ArrayList<State>());		// Si non, l'ajouter et initialiser son tableau d'etats a vide
			}
			ArrayList<State> varStates = map.get(keyStmt);		// Recuperer le tableau des etats sources des predecesseurs
			varStates.add(predecesseurs.get(i).getSource());	// Ajouter l'etat source du predecesseur au tableau
			map.put(keyStmt,varStates);							// Mise a jour de la map
		}
		/* A l'issue de cette boucle on se retrouve avec une map dont les cles sont les differents statements entrants dans l'etat
		 *   et les valeurs les tableaux des etats sources de chaque statement
		 * une map de la forme 
		 *      KEY             VALUE
		 *      stmt1          [Sx,Sy,Sz]
		 *      stmt2          [Su,Si,Sj]
		 *      ...				...
		 *	Dans la suite, parcourir la map cle par cle, pour chaque cle fusionner tous les etats dans le premier de la liste 
		*/
		for(Iterator<String> iter = map.keySet().iterator(); iter.hasNext();){
			String key = (String)iter.next();	// Cle actuelle
			ArrayList<State> varStates = map.get(key);	// Liste des etats a fusionner et qui emettent le statement "key" vers l'etat state passe en parametres
			if(varStates != null){					
				if(varStates.size() > 1){		// eq. plusieurs etats emettent le statement "key" ==> fusionner les etat
					mergeStates(lts, key, varStates);
				}
				if(varStates.get(0).getPredecesseurs().size() > 1){
					mergeState(lts,varStates.get(0));
				}
			}
		}

		map = new HashMap<String,ArrayList<State>>();	// Reinitiatlisation de la map
		// Pour chaque transition sortante de l'etat actuel
		for(int i = 0; i < sizeSuccesseurs; i++){
			String keyStmt = successeurs.get(i).getLabel();		// Recuperer le label
			if(!map.keySet().contains(keyStmt)){		// Verifier si le label a deja ete ajoute
				map.put(keyStmt, new ArrayList<State>());	// sinon l'ajouter a la map et initialiser son tableau d'etats a vide
			}
			ArrayList<State> varStates = map.get(keyStmt);	// Recuperer le tableau des etats destination
			varStates.add(successeurs.get(i).getTarget());	// Ajouter l'etat destination du successeur au tableau
			map.put(keyStmt,varStates);					// Mise a jour de la map
		}
		/*
		 * A l'issue de cette boucle, on se retrouve avec une map dont les cles sont les differents statments sortants de l'etat actuel
		 *  et les valeurs les tableaux des etats destination a fusionner
		 *  
		 *  Pour la suite, parcourir la map cle par cle, pour chaque cle fusionner tous les etats destination dans le premier
		 */
		for(Iterator<String> iter = map.keySet().iterator(); iter.hasNext();){
			String key = (String)iter.next();	// Cle actuelle
			ArrayList<State> varStates = map.get(key);	// Liste des etats destinations qui recoivent le statment "key" de l'etat passe en parametres
			if(varStates != null){
				if(varStates.size() > 1){	// eq. plusieurs etats recoivent le meme statement "key" ==> fusionner les etats
					mergeStates(lts, key, varStates);
				}
				if(varStates.get(0).getSuccesseurs().size() > 1){
					mergeState(lts,varStates.get(0));
				}
			}
		}
	}
	
	public static void main(String args[]) {
		LGGZR algo = new LGGZR();
		ArrayList<Trace> traces = new ArrayList<Trace>();
		try {
			traces = Trace.getTracesFromDir(pathToWorkFolder+"TracesIN/");
			FSA newLTS = new FSA();

			newLTS = algo.transform(traces);

			algo.mergeState(newLTS, newLTS.getFinalState());
			
			GenerateDOT.printDot(newLTS, "./LTSResults/LGGZR_lts.dot");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
