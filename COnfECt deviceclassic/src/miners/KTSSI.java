/*
 *  KTSSI.java
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
import traces.Trace;

public class KTSSI implements FSAminer {

	private static final long serialVersionUID = 1L;
	public static final String pathToWorkFolder = "./BME_tests/";
	private int degree;
	
	public KTSSI(int degree){
		this.degree = degree;
	}

	public int getDegree(){
		return this.degree;
	}
	
	public void setDegree(int degree){
		this.degree = degree;
	}
	
	@Override
	public FSA transform(ArrayList<Trace> traces) { 
		FSA finalLTS = new FSA();
		State rootState = new State();
		Map<String,State> foundStates = new HashMap<String,State>();
		rootState.setLabel("epsilon");
		finalLTS.setInitialState(rootState);
		foundStates.put("epsilon", rootState);
		for (Trace t : traces) { // Parcourir toutes les traces (t represente un objet de type Trace)
			if(t.getStatements().size() > 0){
				parseTrace(t, foundStates, finalLTS);
			}
			else{
				finalLTS.addFinalState(finalLTS.getInitialState());
			}
		}
		
		return finalLTS;
	}

	public void parseTrace(Trace t, Map<String,State> foundStates, FSA finalLTS) {
		State rightState = null;
		State leftState = finalLTS.getInitialState();
		String probNewLabel = "";
	
		int countStmt = 0;
		
		ArrayList<String> tmpStmts = new ArrayList<String>();
		for(int i = 0; i < t.getStatements().size(); i++){
			tmpStmts.add(t.getStatements().get(i).toString());
			
			probNewLabel = probNewLabel+t.getStatements().get(i).toString();
			countStmt++;
			if(countStmt <= this.degree){
				
				// Ajouter transition
				if(foundStates.containsKey(probNewLabel)){
					rightState = foundStates.get(probNewLabel);
				}
				else{
					rightState = new State();
					rightState.setLabel(probNewLabel);
					foundStates.put(probNewLabel, rightState);
				}
				Transition newTransition = new Transition(leftState, t.getStatements().get(i), rightState);
				finalLTS.addTransition(newTransition);
				leftState = rightState;
				
				if(countStmt == this.degree ){
					probNewLabel = probNewLabel.substring(tmpStmts.get(0).length());
					tmpStmts.remove(0);
					countStmt--;
				}
			}
		}
		finalLTS.addFinalState(rightState);
	}

	@Override
	public String getName() {
		return "KTSSI_"+this.degree;
	}	
	
	public static void main(String args[]) {
		int degree = 2;
		KTSSI algo = new KTSSI(degree);
		ArrayList<Trace> traces = new ArrayList<Trace>();
		try {
			traces = Trace.getTracesFromDir(pathToWorkFolder+"TracesIN/");
			FSA newLTS = new FSA();
			newLTS = algo.transform(traces);
			GenerateDOT.printDot(newLTS,"./LTSResults/KTSSI_lts.dot");
			for(State state:newLTS.getStates()){
				System.out.println(state.getName() + "  -->  " + state.getLabel());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
