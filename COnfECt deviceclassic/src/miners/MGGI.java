/*
 *  MGGI.java
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
import java.util.Map;

import fsa.GenerateDOT;
import fsa.FSA;
import fsa.State;
import fsa.Transition;

import miners.FSAminer;
import traces.Statement;
import traces.Trace;

public class MGGI implements FSAminer {

	private static final long serialVersionUID = 1L;
	public static final String pathToWorkFolder = "./BME_tests/";
	
	public MGGI(){
		super();
	}

	@Override
	public FSA transform(ArrayList<Trace> traces) {
		FSA finalLTS = new FSA();
		State rootState = new State();
		Map<String,State> statesMap = new HashMap<String,State>();
		rootState.setLabel("epsilon0");
		statesMap.put("epsilon0", rootState);
		finalLTS.setInitialState(rootState);
		for (Trace trace : traces) { // Parcourir toutes les traces (t represente un objet de type Trace)
			// Pour chaque transition, s'il s'agit de la meme transition qu'on vient de rencontrer (previousTransitionPassed)
			//  ==> incrementer le parsedTransitionCount et creer la transition entre le previousState et un nouvel etat
			int sameTransitionParsedCount = 0;
			String lastTransitionParsed = "";
			if(trace.getStatements().size() > 0){
				parseTrace(rootState, trace, 0, statesMap, lastTransitionParsed, sameTransitionParsedCount, finalLTS); // finalLTS passe en parametres pour marquer les etats finaux au fur et a mesure
			}
			else{
				finalLTS.addFinalState(finalLTS.getInitialState());
			}
		}
		return finalLTS;
	}

	public void parseTrace(State lastState, Trace trace, int indexStmt, Map<String,State> statesMap,  String lastTransitionParsed, int sameTransitionParsedCount, FSA finalLTS) {
		Statement firstVarStmt = null;
		State rightState = null;
		String probNewLabel = "";
		
		firstVarStmt = trace.getStatements().get(indexStmt);
		if(firstVarStmt.toString().compareTo(lastTransitionParsed) == 0){
			sameTransitionParsedCount++;
		}
		else{
			sameTransitionParsedCount = 1;
		}
		if (firstVarStmt != null) { // Test object != null pour eviter des bugs eventuels lors de l'execution
			// Pour le premier statement firstVarStmt de la trace t
			probNewLabel = firstVarStmt.toString() + sameTransitionParsedCount;

			if(statesMap.containsKey(probNewLabel)){
				rightState = statesMap.get(probNewLabel);
			}
			else{
				rightState = new State();
				rightState.setLabel(probNewLabel);
				statesMap.put(probNewLabel, rightState);
			}
			
			Transition newTransition = new Transition(lastState, firstVarStmt, rightState);
			finalLTS.addTransition(newTransition);
						
			lastTransitionParsed = firstVarStmt.toString();
			indexStmt++;
			if(trace.getSize() == indexStmt){
				if( !finalLTS.getFinalStates().contains(rightState) ){
					finalLTS.addState(rightState);
					finalLTS.addFinalState(rightState);
				}
			}
			else{
				parseTrace(rightState, trace, indexStmt, statesMap, lastTransitionParsed, sameTransitionParsedCount, finalLTS);
			}
		}
	}
	
	@Override
	public String getName() {
		return "MGGI";
	}

	public static void main(String args[]){
		MGGI mggi = new MGGI();
		ArrayList<Trace> traces = new ArrayList<Trace>();
		try {
			traces = Trace.getTracesFromDir(pathToWorkFolder+"TracesIN/");
			FSA newLTS = new FSA();
			newLTS = mggi.transform(traces);
			GenerateDOT.printDot(newLTS,"./LTSResults/MGGI_lts.dot");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
