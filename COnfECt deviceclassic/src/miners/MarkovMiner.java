/*
 *  MARKOV.java
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
 * Markov Miner.
 * 
 * Needs to be verified
 * 
 * @author Asma KHADHRAOUI
 *
 */
public class MarkovMiner implements FSAminer {
	private static final long serialVersionUID = 1L;
	public static int transitionIndex = 0;
	public static final String pathToWorkFolder = "./BME_tests/";
	public static int nbreTransition = 0;
	
	private Map<String,Integer> occurrencesMap;	
	
	private MatrixProbability firstOrderMatrix;			// Matrice de premier ordre (un seul statement par ligne et un par colonne)
	private MatrixProbability secondOrderMatrix;		// Matrice de second ordre (deux statements successifs par ligne et un statement par colonne)
	private ArrayList<String> firstOrderLinesLabels;	// Tableau des labels des lignes et des colonnes de la matrice de 1er ordre et les labels des colonnes de la matrice de 2nd ordre
	private ArrayList<String> secondOrderLinesLabels;	// Tableau des labels des lignes de la matrice de 2nd ordre
	private Map<String, ArrayList<State>> statesBeforeReduce;		// Tableau des etats avant la reduction (chaque etat represente le label d'une ligne de la matrice de second ordre)
	private Map<String, Statement> statements;
	private ArrayList<String> illegalSequences;
	private ArrayList<String> legalSequences;
	
	private int minOccurrences;			// Seuil minimal d'apparition d'une sequence de statements au dessous duquel la sequence est ignoree
	private double minProbability;		// Seuil minimal de probabilite d'une sequence de statements au dessous duquel la sequence est ignoree (les deux seuils doivent etre respectes)
	
	private Map<String, State> finalLTSstates;
	private Map<String, Statement> finalStatements;
	private Map<String, Statement> initialStatements;
	
	public MarkovMiner() {
		super();
		this.minOccurrences = 0;
		this.minProbability = 0.0;
		this.occurrencesMap = new HashMap<String, Integer>();
		this.firstOrderLinesLabels = new ArrayList<String>();
		this.secondOrderLinesLabels = new ArrayList<String>();
		this.statesBeforeReduce = new HashMap<String, ArrayList<State>>();
		this.statements = new HashMap<String, Statement>();
		this.illegalSequences = new ArrayList<String>();
		this.finalLTSstates = new HashMap<String, State>();
		this.legalSequences = new ArrayList<String>();
		this.finalStatements = new HashMap<String, Statement>();
		this.initialStatements = new HashMap<String, Statement>();
	}

	public MarkovMiner(int minOccurrences, double minProbability){
		super();
		this.minOccurrences = minOccurrences;
		this.minProbability = minProbability;
		this.occurrencesMap = new HashMap<String, Integer>();
		this.firstOrderLinesLabels = new ArrayList<String>();
		this.secondOrderLinesLabels = new ArrayList<String>();
		this.statesBeforeReduce = new HashMap<String, ArrayList<State>>();
		this.statements = new HashMap<String, Statement>();
		this.illegalSequences = new ArrayList<String>();
		this.finalLTSstates = new HashMap<String, State>();
		
		this.legalSequences = new ArrayList<String>();
		
		this.finalStatements = new HashMap<String, Statement>();
		this.initialStatements = new HashMap<String, Statement>();
	}
	
	public Map<String, Statement> getInitialStatements() {
		return initialStatements;
	}

	public void setInitialStatements(Map<String, Statement> initialStatements) {
		this.initialStatements = initialStatements;
	}

	public Map<String, Statement> getFinalStatements() {
		return finalStatements;
	}

	public void setFinalStatements(Map<String, Statement> finalStatements) {
		this.finalStatements = finalStatements;
	}

	public ArrayList<String> getLegalSequences() {
		return legalSequences;
	}

	public void setLegalSequences(ArrayList<String> legalSequences) {
		this.legalSequences = legalSequences;
	}

	public int getMinOccurrences() {
		return minOccurrences;
	}

	public void setMinOccurrences(int minOccurrences) {
		this.minOccurrences = minOccurrences;
	}

	public double getMinProbability() {
		return minProbability;
	}

	public void setMinProbability(double minProbability) {
		this.minProbability = minProbability;
	}

	public Map<String, Integer> getoccurrencesMap() {
		return occurrencesMap;
	}

	public void setOccurrencesMap(Map<String, Integer> occurrencesMap) {
		this.occurrencesMap = occurrencesMap;
	}
	
	public ArrayList<String> getFirstOrderLinesLabels() {
		return firstOrderLinesLabels;
	}

	public void setFirstOrderLinesLabels(ArrayList<String> firstOrderLinesLabels) {
		this.firstOrderLinesLabels = firstOrderLinesLabels;
	}

	public ArrayList<String> getSecondOrderLinesLabels() {
		return secondOrderLinesLabels;
	}

	public void setSecondOrderLinesLabels(ArrayList<String> secondOrderLinesLabels) {
		this.secondOrderLinesLabels = secondOrderLinesLabels;
	}
	
	public Map<String, ArrayList<State>> getStatesBeforeReduce() {
		return statesBeforeReduce;
	}

	public void setStatesBeforeReduce(Map<String, ArrayList<State>> statesBeforeReduce) {
		this.statesBeforeReduce = statesBeforeReduce;
	}	

	public Map<String, Statement> getStatements() {
		return statements;
	}

	public void setStatements(Map<String, Statement> statements) {
		this.statements = statements;
	}
	
	public ArrayList<String> getIllegalSequences() {
		return illegalSequences;
	}

	public void setIllegalSequences(ArrayList<String> illegalSequences) {
		this.illegalSequences = illegalSequences;
	}
	
	public Map<String, State> getFinalLTSstates() {
		return finalLTSstates;
	}

	public void setFinalLTSstates(Map<String, State> finalLTSstates) {
		this.finalLTSstates = finalLTSstates;
	}

	/* 
	 * computeOccurrences calcule les occurences de chaque sequence de longueur deux ou trois statements successifs
	 * elle met a jour la map occurrencesMap dont la cle correspond a la sequence en question et la valeur correspond a l'occurence
	 * dans un deuxieme temps, elle met a jour les matrices firstOrderMatrix et secondOrderMatrix a chaud apres chaque modification d'une ligne de la map occurrencesMap
	 */	
	public void computeOccurrences(ArrayList<Trace> traces){
		this.firstOrderMatrix = new MatrixProbability();
		this.secondOrderMatrix = new MatrixProbability();
		for(Trace trace:traces){
			String newKey = "";
			int stmtCount = 0;		
			String firstOrderKey = "";
			String secondOrderKey = "";
			while(stmtCount < trace.getStatements().size()){
				if(stmtCount == 0 && !this.initialStatements.containsKey(trace.getStatements().get(stmtCount).toString()) ){
					this.initialStatements.put(trace.getStatements().get(stmtCount).toString(), trace.getStatements().get(stmtCount));
				}
				newKey = trace.getStatements().get(stmtCount).toString();	// Fixer le statements lu
				if( ! this.statements.containsKey(newKey)){
					this.statements.put(newKey, trace.getStatements().get(stmtCount));
				}
				
				firstOrderKey = newKey;
				if( ! this.firstOrderLinesLabels.contains(newKey)){	// Ajouter une nouvelle colonne pour le premier statement parmi les trois a traiter
					this.firstOrderLinesLabels.add(newKey);
				}
				
				for(int i = stmtCount+1; i < stmtCount+3 && i < trace.getStatements().size(); i++){
					String currentStatement = trace.getStatements().get(i).toString();
					
					secondOrderKey = newKey;	// A ce stade, secondOrderKey est compose de deux statements, il s'agit d'une eventuelle nouvelle entree de la matrice de 2nd ordre
					newKey = newKey+currentStatement;	// A ce stade, newKey est compose de trois statements successifs, il s'agit d'une eventuelle nouvelle entree de la map occurrencesMap
					
					if( ! this.firstOrderLinesLabels.contains(currentStatement)){	// Ajouter une nouvelle colonne pour le deuxieme et le troisieme statements parmi les trois a traiter
						this.firstOrderLinesLabels.add(currentStatement);
					}
					
					if(this.occurrencesMap.containsKey(newKey)){		// Deja rencontree donc entree deja presente dans firstOrder
						this.occurrencesMap.put(newKey, this.occurrencesMap.get(newKey)+1);
					}
					else{
						this.occurrencesMap.put(newKey, 1);
					}
					if(i == stmtCount+1){	// deux statements lus
						if( ! this.secondOrderLinesLabels.contains(newKey)){
							this.secondOrderLinesLabels.add(newKey);
						}
						this.firstOrderMatrix.updateOccurrenceProbability(firstOrderKey, this.firstOrderLinesLabels.indexOf(currentStatement));
						if(i == trace.getStatements().size()-1){  // Traitement du cas exceptionnel des deux derniers statements
							if( ! this.finalStatements.containsKey(trace.getStatements().get(i).toString())){
								this.finalStatements.put(trace.getStatements().get(i).toString(), trace.getStatements().get(i));
							}
							this.secondOrderMatrix.updateOccurrenceProbability(secondOrderKey+currentStatement, -1);
						}
					}
					if(i == stmtCount+2){  // trois statements lus
						this.secondOrderMatrix.updateOccurrenceProbability(secondOrderKey, this.firstOrderLinesLabels.indexOf(currentStatement));
					}
				}
				stmtCount++;
			}
		}
//		this.displayMatrix();
	}

	public void updateIllegalSequences(){		
		for(int i = 0; i < this.secondOrderLinesLabels.size(); i++){
			ArrayList<Double> currentValues = this.secondOrderMatrix.getValues().get(this.secondOrderLinesLabels.get(i));
			if(currentValues != null){
				int sizeCurrentValues = currentValues.size();
				String secondOrderLabel = this.secondOrderLinesLabels.get(i);
				for(int k = 0; k < sizeCurrentValues; k++){
					if(currentValues.get(k) == 0.00){
						this.illegalSequences.add(secondOrderLabel+this.firstOrderLinesLabels.get(k));
					}
					else{
						this.legalSequences.add(secondOrderLabel+this.firstOrderLinesLabels.get(k));
					}
				}
				for(int k = sizeCurrentValues; k < this.firstOrderLinesLabels.size(); k++){
					this.illegalSequences.add(secondOrderLabel+this.firstOrderLinesLabels.get(k));
				}
			}
		}
	}
	
	public void updateStatesWithSequences(FSA lts){
		for(int i = 0; i < lts.getStates().size(); i++){
			State currentState = lts.getStateList().get(i);
			
			for(int p = 0; p < currentState.getPredecesseurs().size(); p++){	// Pour chaque predecesseurs
				Transition currentPred = currentState.getPredecesseurs().get(p);
				for(int s = 0; s < currentState.getSuccesseurs().size(); s++){
					Transition currentSucc = currentState.getSuccesseurs().get(s);
					String currentSequence = currentPred.getSource().getLabel()+currentState.getLabel()+currentSucc.getTarget().getLabel();
					if(this.illegalSequences.contains(currentSequence)){		// Il faut dissocier cette sequence currentSequence
						Boolean transitionManaged = false;
						// Recuperer la liste des copies de l'etat
						ArrayList<State> stateToSplitArray = this.statesBeforeReduce.get(currentState.getLabel());
						// Verifier si une des copies de l'etat peut accepter une des transitions (en commencant par la deuxieme moitie de le sequence illegale)
						for(int indexCopies = 1; indexCopies < stateToSplitArray.size() && !transitionManaged; indexCopies++){
							State currentCopy = stateToSplitArray.get(indexCopies);
							if( isValidSuccTransition(currentSucc, currentCopy, lts) || isValidPredTransition(currentPred, currentCopy, lts) ){
								transitionManaged = true;
							}
						}
						if( ! transitionManaged ){
							State newState = new State();
							newState.setLabel(currentState.getLabel());
							Transition newTransition = new Transition(newState, new Statement(), currentSucc.getTarget()); 
							lts.addTransition(newTransition);
							lts.removeTransition(currentSucc);
							this.correctLegalPredSequences(newState, currentSucc, lts);
							stateToSplitArray.add(newState);
							this.statesBeforeReduce.put(currentState.getLabel(), stateToSplitArray);
						}
					}
				}
			}
			
		}
		
		for(int i = 0; i < lts.getStates().size(); i++){
			State state = lts.getStateList().get(i);
			transitionIndex++;
			Statement stmt = new Statement();
			stmt.setName(String.valueOf(transitionIndex));		// Mise a jour du nom du statement pour les successeurs
			for(int j = 0; j < state.getSuccesseurs().size(); j++){
				state.getSuccesseurs().get(j).setTrigger(stmt);
			}
		}
	}
	
	protected ArrayList<State> getStatesSuccesseurs(State state){
		ArrayList<State> statesSuccesseurs = new ArrayList<State>();
		for(int i = 0; i < state.getSuccesseurs().size(); i++){
			statesSuccesseurs.add(state.getSuccesseurs().get(i).getTarget());
		}
		return statesSuccesseurs;
	}
	protected ArrayList<State> getStatesPredecesseurs(State state){
		ArrayList<State> statesPredecesseurs = new ArrayList<State>();
		for(int i = 0; i < state.getPredecesseurs().size(); i++){
			statesPredecesseurs.add(state.getPredecesseurs().get(i).getSource());
		}
		return statesPredecesseurs;
	}

	public boolean joinStates(FSA lts, State state, ArrayList<State> statesPeriph){
		//System.out.println("Traitement de : " + state);
		boolean found = false;
		statesPeriph.add(state);
		ArrayList<State> successeurs = this.getStatesSuccesseurs(state);
		if( !successeurs.contains(lts.getFinalState()) && !successeurs.contains(lts.getInitialState())){
			for(int i = 0; i < successeurs.size(); i++){
				//System.out.println("Traitement nett : " + successeurs.get(i).getName());
				if( ! statesPeriph.contains(successeurs.get(i))){
					statesPeriph.add(successeurs.get(i));
					found |= this.joinStates(lts, successeurs.get(i), statesPeriph);
				}
			}
		}
		else{
			found = true;
//			System.out.println("Final successeur de : " + state.getName());
		}
		//System.out.println("Trt de : " + state.getName() + " -- fini  =  " + found);
		return found;
	}
	
	@Override
	public FSA transform(ArrayList<Trace> traces) {
		nbreTransition = 0;
		this.occurrencesMap = new HashMap<String, Integer>();
		this.firstOrderLinesLabels = new ArrayList<String>();
		this.secondOrderLinesLabels = new ArrayList<String>();
		this.statesBeforeReduce = new HashMap<String, ArrayList<State>>();
		this.statements = new HashMap<String, Statement>();
		this.illegalSequences = new ArrayList<String>();
		this.finalLTSstates = new HashMap<String, State>();
		this.legalSequences = new ArrayList<String>();
		this.finalStatements = new HashMap<String, Statement>();
		this.initialStatements = new HashMap<String, Statement>();
		
		FSA finalLTS = new FSA();
	
		this.computeOccurrences(traces);
		finalLTS = this.firstOrderMatrixtoLTS();
	
		this.updateIllegalSequences();
		if(finalLTS.getStates().size() > 0){
			finalLTS.setInitialState(finalLTS.getStateList().get(0));
		}
		else{
			State state = new State();
			finalLTS.setInitialState(state);
			finalLTS.setFinalState(state);
		}
		
		this.updateStatesWithSequences(finalLTS);
		
		finalLTS = this.translateStatementLTS(finalLTS);

		if(finalLTS.getInitialState() == null){
			finalLTS.setInitialState(finalLTS.getStateList().get(0));
		}
		if(finalLTS.getFinalState() == null){
			finalLTS.setFinalState(finalLTS.getStateList().get(finalLTS.getStates().size()-1));
		}
		ArrayList<State> statesToMergeWithInitial = new ArrayList<State>();
		ArrayList<State> statesToMergeWithFinal = new ArrayList<State>();
		for(int i = 0; i < finalLTS.getStates().size(); i ++){
			State state = finalLTS.getStateList().get(i);
			if(state.getPredecesseurs().size() == 0){
				statesToMergeWithInitial.add(state);
			}
			if(state.getSuccesseurs().size() == 0){
				statesToMergeWithFinal.add(state);
			}
		}
		statesToMergeWithInitial.add(finalLTS.getInitialState());
		statesToMergeWithFinal.add(finalLTS.getFinalState());
		this.mergeStates(finalLTS, "", statesToMergeWithInitial);
		this.mergeStates(finalLTS, "", statesToMergeWithFinal);
		if(finalLTS.getFinalState().getPredecesseurs().size() == 0 || finalLTS.getInitialState().getSuccesseurs().size() == 0){
			finalLTS.setFinalState(finalLTS.getInitialState());
		}

		ArrayList<State> statesToMerge = new ArrayList<State>();
		for(int i = 0; i < finalLTS.getStates().size(); i++){
			ArrayList<State> statesPeriph = new ArrayList<State>();
			State state = finalLTS.getStateList().get(i);
			boolean foundInitialOrFinal = false;
			
			foundInitialOrFinal = this.joinStates(finalLTS, state, statesPeriph);
			if( !foundInitialOrFinal && state != finalLTS.getFinalState() && state != finalLTS.getInitialState()){
				//System.out.println("State : " + finalLTS.getStates().get(i).getName() + " a fusionner");
				statesToMerge.add(finalLTS.getStateList().get(i));
			}	
		}
		
		for(int i = 0; i < statesToMerge.size(); i++){
			State state = statesToMerge.get(i);
			for(int s = 0; s < state.getSuccesseurs().size(); s++){
				Transition tr = new Transition(state, state.getSuccesseurs().get(s).getTrigger(), finalLTS.getFinalState());
				finalLTS.addTransition(tr);
			}
			for(int p = 0; p < state.getPredecesseurs().size(); p++){
				Transition tr = new Transition(finalLTS.getInitialState(), state.getPredecesseurs().get(p).getTrigger(), state);
				finalLTS.addTransition(tr);
			}
		}
		return finalLTS;
	}
	
	
	public Boolean isValidPredTransition(Transition currentPred, State copyState, FSA lts){
		Boolean transitionValid = true;
		
		for(int i = 0; i < copyState.getSuccesseurs().size() && transitionValid; i++){
			String sequence = currentPred.getSource().getLabel() + copyState.getLabel() + copyState.getSuccesseurs().get(i).getTarget().getLabel();
			if(this.illegalSequences.contains(sequence)){	// Transition non valide ==> sortir avec false
				transitionValid = false;
			}
		}
		if(transitionValid){
			Transition probNewTransition = new Transition(currentPred.getSource(), new Statement(), copyState);
			lts.addTransition(probNewTransition);
			this.correctLegalSuccSequences(copyState, currentPred, lts);
			lts.removeTransition(currentPred);
		}
		return transitionValid;
	}
	
	public Boolean isValidSuccTransition(Transition currentSucc, State copyState, FSA lts){
		Boolean transitionValid = true;
		
		for(int i = 0; i < copyState.getPredecesseurs().size() && transitionValid; i++){
			String sequence = copyState.getPredecesseurs().get(i).getSource().getLabel() + copyState.getLabel() + currentSucc.getTarget().getLabel();
			if(this.illegalSequences.contains(sequence)){  // Transition non valide ==> sortir avec false
				transitionValid = false;
			}
		}
		if(transitionValid){
			Transition probNewTransition = new Transition(copyState, new Statement(), currentSucc.getTarget());
			lts.addTransition(probNewTransition);
			this.correctLegalPredSequences(copyState, currentSucc, lts);
			lts.removeTransition(currentSucc);
		}
		
		return transitionValid;
	}
	
	
	public void correctLegalPredSequences(State copyState, Transition currentSucc, FSA lts){	// Original state = currentSucc.getSource()
		ArrayList<Transition> predecesseursToTest = currentSucc.getSource().getPredecesseurs();
		for(int p = 0; p < predecesseursToTest.size(); p++){
			Transition predTransition = predecesseursToTest.get(p);
			Boolean predExists = false;
			if(this.legalSequences.contains(predTransition.getSource().getLabel()+copyState.getLabel()+currentSucc.getTarget().getLabel())){
				// Il faut corriger ce deplacement de currentSucc
				for(int indexPredCopy = 0; indexPredCopy < copyState.getPredecesseurs().size() && !predExists; indexPredCopy++){
					if(copyState.getPredecesseurs().get(indexPredCopy).getSource().getLabel().compareTo(predTransition.getSource().getLabel()) == 0){
						predExists = true;
					}
				}
				if( ! predExists){
					Transition newPredTransition = new Transition(predTransition.getSource(), new Statement(), copyState);
					lts.addTransition(newPredTransition);
				}
			}
		}
	}
	
	
	public void correctLegalSuccSequences(State copyState, Transition currentPred, FSA lts){	// Original state = currentPred.getTargetState()
		ArrayList<Transition> successeursToTest = currentPred.getTarget().getSuccesseurs();
		for(int s = 0; s < successeursToTest.size(); s++){
			Transition succTransition = successeursToTest.get(s);
			Boolean succExists = false;
			if(this.legalSequences.contains(currentPred.getSource().getLabel() + copyState.getLabel() + succTransition.getTarget().getLabel())){
				// Il faut corriger ce deplacement de currentPred
				for(int indexSuccCopy = 0; indexSuccCopy < copyState.getSuccesseurs().size() && !succExists; indexSuccCopy++){
					if(copyState.getSuccesseurs().get(indexSuccCopy).getTarget().getLabel().compareTo(succTransition.getTarget().getLabel()) == 0){
						succExists = true;
					}
					if( ! succExists){
						Transition newSuccTransition = new Transition(copyState, new Statement(), succTransition.getTarget());
						lts.addTransition(newSuccTransition);
					}
				}
			}
		}
	}
	
	public FSA translateStatementLTS(FSA lts){
		FSA newLTS = new FSA();
		
		for(int i = 0; i < lts.getTransitions().size(); i++){
			Transition leftTransition = lts.getTransitions().get(i);
			String leftStateName = ((Statement)leftTransition.getTrigger()).getName();
			String rightStateName;
			Boolean foundRightState = false;
			
			State leftState = this.finalLTSstates.get(leftStateName);
			if(leftState == null){
				leftState = new State(leftStateName);
				this.finalLTSstates.put(leftStateName, leftState);
			}
			for(int j = 0; j < lts.getTransitions().size(); j++){
				Transition rightTransition = lts.getTransitions().get(j);
				if(leftTransition.getTarget() == rightTransition.getSource()){
					rightStateName = ((Statement)rightTransition.getTrigger()).getName();
					foundRightState = true;
					State rightState;
					rightState = this.finalLTSstates.get(rightStateName);
					if(rightState == null){
						rightState = new State(rightStateName);
						this.finalLTSstates.put(rightStateName, rightState);
					}
					Transition newTransition = new Transition(leftState, this.statements.get(leftTransition.getTarget().getLabel()) , rightState);
					newLTS.addTransition(newTransition);
				}
			}
			if(!foundRightState){
				State rightState = new State(String.valueOf(transitionIndex++));
				Transition newTransition = new Transition(leftState, this.statements.get(leftTransition.getTarget().getLabel()), rightState);
				newLTS.addTransition(newTransition);
			}
			if(leftTransition.getSource().getPredecesseurs().size() == 0){
				State initState = new State(String.valueOf(transitionIndex++));
				this.finalLTSstates.put(initState.getName(), initState);
				Transition newTransition = new Transition(initState, this.statements.get(leftTransition.getSource().getLabel()), leftState);
				newLTS.addTransition(newTransition);
			}
		}
		
		State initialState = new State();
		State finalState = new State();
		ArrayList<Transition> newTransitions = new ArrayList<Transition>();
		ArrayList<Transition> transitionsToRemove = new ArrayList<Transition>();
		for(int i = 0; i < newLTS.getTransitions().size(); i++){
			Transition tr = newLTS.getTransitions().get(i);
			if(this.initialStatements.containsKey(tr.getTrigger().toString())){
				Transition newTransition = new Transition(initialState, tr.getTrigger(), tr.getTarget());
				newTransitions.add(newTransition);
				if(tr.getSource().getPredecesseurs().size() == 0){
					transitionsToRemove.add(tr);
				}
			}
			if(this.finalStatements.containsKey(tr.getTrigger().toString())){
				Transition newTransition = new Transition(tr.getSource(), tr.getTrigger(), finalState);
				newTransitions.add(newTransition);
				if(tr.getTarget().getSuccesseurs().size() == 0){
					transitionsToRemove.add(tr);
				}
			}
		}
		for(int i = 0; i < newTransitions.size(); i++){
			newLTS.addTransition(newTransitions.get(i));
		}
		for(int i = 0; i < transitionsToRemove.size(); i++){
			newLTS.removeTransition(transitionsToRemove.get(i));
		}
		newLTS.setInitialState(initialState);
		newLTS.setFinalState(finalState);
		
		return newLTS;
	}
	
	public void initiateStatesBeforeReduce(){
		for(int i = 0; i < this.firstOrderLinesLabels.size(); i++){
			State newState = new State();
			newState.setLabel(this.firstOrderLinesLabels.get(i));
			ArrayList<State> newStateArray = new ArrayList<State>();
			newStateArray.add(newState);
			this.statesBeforeReduce.put(this.firstOrderLinesLabels.get(i), newStateArray);
		}
	}
	
	public FSA firstOrderMatrixtoLTS(){
		FSA newLTS = new FSA();
		this.initiateStatesBeforeReduce();
		
		for(Iterator<String> firstOrderIter = this.firstOrderMatrix.getValues().keySet().iterator(); firstOrderIter.hasNext(); ){
			String iter = firstOrderIter.next();
			ArrayList<Double> values = this.firstOrderMatrix.getValues().get(iter);
			for(int i = 0; i < values.size(); i++){
				String occurrenceString = iter+this.firstOrderLinesLabels.get(i);
				if(this.occurrencesMap.containsKey(occurrenceString)){
					if(this.occurrencesMap.get(occurrenceString) >= this.minOccurrences && values.get(i) >= this.minProbability){
						State leftState = this.statesBeforeReduce.get(iter).get(0);
						State rightState = this.statesBeforeReduce.get(this.firstOrderLinesLabels.get(i)).get(0);
						Transition newTransition = new Transition(leftState, new Statement(), rightState);
						newLTS.addTransition(newTransition);
					}
				}
			}
		}
		
		return newLTS;
	}
	
	@Override
	public String getName() {
		return "MARKOV_"+this.minOccurrences+"_"+this.minProbability;
	}
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
				lts.setFinalState(newStateMerged);
			}
			
			if(lts.getInitialState().getName().compareTo(tmpState.getName()) == 0){
				lts.setInitialState(newStateMerged);
			}
			
			lts.removeState(tmpState);
		}
	}
	
	public static void main(String args[]){
		MarkovMiner markov = new MarkovMiner(0,0.0);
		
		ArrayList<Trace> traces = new ArrayList<Trace>();
		try {
			traces = Trace.getTracesFromDir(pathToWorkFolder+"TracesIN/");
			FSA newLTS = new FSA();
			newLTS = markov.transform(traces);
			

			
			GenerateDOT.printDot(newLTS,"./LTSResults/MARKOV_lts.dot");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void displayMatrix(){
		// Affichage du resultat
		int maxSizeKey = 0;
		for(int s = 0; s < this.firstOrderLinesLabels.size(); s++){
			if(maxSizeKey < this.firstOrderLinesLabels.get(s).length()){
				maxSizeKey = this.firstOrderLinesLabels.get(s).length();
			}
		}
		System.out.println("\nMatrice de premier ordre :");
		// Affichage des libelles (entete)
		for(int s = 0; s < maxSizeKey; s++){
			System.out.print(" ");
		}
		for(int s = 0; s < this.firstOrderLinesLabels.size(); s++){
			System.out.print("|" + this.firstOrderLinesLabels.get(s));
			for(int t = this.firstOrderLinesLabels.get(s).length(); t < maxSizeKey; t++){
				System.out.print(" ");
			}
		}
		System.out.println("");
		// Affichage des valeurs
		for(Iterator<String> firstOrderIter = this.firstOrderMatrix.getValues().keySet().iterator(); firstOrderIter.hasNext(); ){
			String iter = firstOrderIter.next();
			ArrayList<Double> values = this.firstOrderMatrix.getValues().get(iter);
			System.out.print(iter);
			for(int s = iter.length(); s < maxSizeKey; s++){
				System.out.print(" ");
			}
			for(int i = 0; i < values.size(); i++){
				System.out.printf("|   %1.2f",values.get(i));
				for(int s = 7; s < maxSizeKey; s++){
					System.out.print(" ");
				}
			}
			System.out.println("");
		}
		
		int maxSizeKey2 = 0;
		for(int s = 0; s < this.secondOrderLinesLabels.size(); s++){
			if(maxSizeKey2 < this.secondOrderLinesLabels.get(s).length()){
				maxSizeKey2 = this.secondOrderLinesLabels.get(s).length();
			}
		}
		System.out.println("\nMatrice de second ordre :");
		// Affichage des libelles (entete)
		for(int s = 0; s < maxSizeKey2; s++){
			System.out.print(" ");
		}
		for(int s = 0; s < this.firstOrderLinesLabels.size(); s++){
			System.out.print("|" + this.firstOrderLinesLabels.get(s));
			for(int t = this.firstOrderLinesLabels.get(s).length(); t < maxSizeKey; t++){
				System.out.print(" ");
			}
		}
		System.out.println("");
		// Affichage des valeurs
		for(Iterator<String> secondOrderIter = this.secondOrderMatrix.getValues().keySet().iterator(); secondOrderIter.hasNext(); ){
			String iter = secondOrderIter.next();
			ArrayList<Double> values = this.secondOrderMatrix.getValues().get(iter);
			System.out.print(iter);
			for(int s = iter.length(); s < maxSizeKey2; s++){
				System.out.print(" ");
			}
			for(int i = 0; i < values.size(); i++){
				System.out.printf("|   %1.2f",values.get(i));
				for(int s = 7; s < maxSizeKey; s++){
					System.out.print(" ");
				}
			}
			
			System.out.println("");
		}
		
		System.out.println("");
	}
	
	/*
	 * Description du programme :
	 * 		Traiter etat par etat :
	 * 			[FOR] Pour chaque etat :
	 * 				[IF] Verifier s'il se presente comme le milieu d'une sequence illegale
	 * 					[FOR] Pour chaque sequence illegale retrouvee :
	 * 						[IF] Verifier si cet etat a deje une ou plusieurs copies :
	 * 							[FOR] Pour chaque copie et tant que transition non deplacee :
	 * 								Tenter de deplacer la deuxieme moitie de la sequence sur la copie en cours et donc comme transition sortante (successeur) de cette copie
	 * 								[IF] Si cette moitie existe deje dans cette copie 
	 * 								[THEN] supprimer cette transition
	 * 								[ELSE] simuler le placement de cette transition et :
	 * 									[IF] verifier qu'elle ne constitue pas de sequences illegales
	 * 									[ENDIF]
	 * 									[IF] si aucune sequence illegale trouvee
	 * 									[THEN] placer cette transition definitivement et corriger les sequences permises qui sont devenues non permises avec ce placement
	 * 									[ENDIF]
	 * 								[ENDIF]
	 * 								[IF] Si tentative de placement de la deuxieme moitie echoue
	 * 								[THEN] Tenter de deplacer la premiere moitie de la sequence sur la copie en cours et donc comme transition rentrante (predecesseur) de cette copie
	 * 									[IF] Si cette moitie existe deje dans cette copie
	 * 									[THEN] supprimer cette transition
	 * 									[ELSE] simuler le placement de cette transition et :
	 * 										[IF] verifier qu'elle ne constitue pas de sequences illegales
	 * 										[ENDIF]
	 * 										[IF] si aucune sequence illegale trouvee
	 * 										[THEN] placer cette transition definitivement et corriger les sequences permises qui sont devenues non permises avec ce placement
	 * 										[ENDIF]
	 * 									[ENDIF]
	 * 								[ENDIF]
	 * 							[ENDFOR]
	 * 						[ELSE] (pas de copies en cours)
	 * 							creer une nouvelle copie, deplacer la deuxieme moitie et corriger les sequences permises qui sont devenues non permises avec ce placement
	 * 						[ENDIF]
	 * 					[ENDFOR]
	 * 				[ENDIF]
	 * 			[ENDFOR]
	 */
}

/**
 * 
 * Matrix of transition probabilities used by the MARKOV class.
 * 
 * @author Asma Khadhraoui
 *
 */
class MatrixProbability {
	private static final long serialVersionUID = 1L;
	
	private Map<String,Integer> globalOccurrencesEvents;
	private Map<String,ArrayList<Double>> values;
	
	public MatrixProbability() {
		super();
		this.globalOccurrencesEvents = new HashMap<String, Integer>();
		this.values = new HashMap<String,ArrayList<Double>>();				// Mise e jour prevue
	}
	
	public MatrixProbability(Map<String,Integer> globalOccurrencesEvents,
			Map<String, ArrayList<Double>> values) {
		super();
		this.globalOccurrencesEvents = globalOccurrencesEvents;
		this.values = values;
	}

	public Map<String,Integer> getGlobalOccurrencesEvents() {
		return globalOccurrencesEvents;
	}

	public void setGlobalOccurrencesEvents(
			Map<String, Integer> globalOccurrencesEvents) {
		this.globalOccurrencesEvents = globalOccurrencesEvents;
	}

	public Map<String,ArrayList<Double>> getValues() {
		return values;
	}
	
	public void setValues(Map<String,ArrayList<Double>> values) {
		this.values = values;
	}
	
	private void addGlobalOccurrenceEvents(String key){
		if( ! this.globalOccurrencesEvents.containsKey(key)){
			this.globalOccurrencesEvents.put(key, 1);
		}
		else{
			this.globalOccurrencesEvents.put(key, this.globalOccurrencesEvents.get(key)+1);
		}
	}
	
	public void updateOccurrenceProbability(String key, int columnIndex){
		this.addGlobalOccurrenceEvents(key);	// Mise a jour du nombre d'occurence global de l'evenement (element en lignes)
		if( ! this.values.containsKey(key)){
			this.values.put(key, new ArrayList<Double>());
		}
		if(this.values.get(key).size() <= columnIndex){
			for(int i = this.values.get(key).size(); i <= columnIndex; i++){
				this.values.get(key).add(0.0);
			}
		}
		ArrayList<Double> eventValues = this.values.get(key);
		Double oldValue, newValue;
		for(int k = 0; k < eventValues.size(); k++){
			if(eventValues.get(k) != 0.0 || k == columnIndex){
				oldValue = eventValues.get(k);
				int deltaOccurrence = 0;
				if(k == columnIndex || columnIndex == -1){
					deltaOccurrence = 1;
				}
				newValue = (oldValue*(this.globalOccurrencesEvents.get(key)-1)+deltaOccurrence)/this.globalOccurrencesEvents.get(key);
				this.values.get(key).set(k, newValue);
			}
		}
	}
}