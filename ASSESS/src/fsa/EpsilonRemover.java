/*
 *  EpsilonRemover.java
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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
/**
 * 
 * Class for the removal of epsilon (empty) transitions.
 * 
 * Grounded on the backward method described in : 
 * http://www.lrde.epita.fr/dload/20070523-Seminar/delmon-eps-removal-vcsn-report.pdf
 * 
 * @author : Sylvain Lamprier
 */
public class EpsilonRemover {
	
	/**
	 * Epsilon removal 
	 * 
	 * @param lts Automaton in which epsilon transitions have to be removed
	 */
	public static void removeEpsilon(FSA lts){
		EpsilonTransitionChecker checker=new EpsilonTransitionChecker();
		boolean ok=false;
		int nbp=0;
		while(!ok){
			nbp++;
			ok=true;
			//System.out.println("Epsilon removal passe "+nbp);
			HashSet<State> states=new HashSet<State>(lts.getStates());
			for(State s:states){
				//ArrayList<Transition> suc=lts.successeurs(s);
				ArrayList<Transition> suc=s.getSuccesseurs();
				for(Transition t:suc){
					if (checker.isEpsilonTransition(t)){
						//System.out.println(t+" est epsilon");
						ok=false;
						State target=t.getTarget();
						System.out.println("state: " + target.toString());
						//ArrayList<Transition> suc2=lts.successeurs(target);
						//ArrayList<Transition> suc2=target.getPredecesseurs();
						//OLD  
						ArrayList<Transition> suc2=target.getSuccesseurs();
						System.out.println(Arrays.deepToString(suc2.toArray()));
						lts.removeTransition(t);
						for(Transition t2:suc2){
							State cible=t2.getTarget();
							Transition t3=new Transition(s,t2.getTrigger(),cible);
							if ((checker.isEpsilonTransition(t3)) && (cible==s)){
								//System.out.println("Nouvelle transition "+t3+" Non ajoutee car espilon et pointe sur soi-meme");
							}
							//else if ((checker.isEpsilonTransition(t2))){} 
							else{
								System.out.println("Nouvelle transition "+t3);
							    lts.addTransition(t3);
							   
							}
						}
						if (lts.getFinalStates().contains(target)){
							lts.addFinalState(s);
							//System.out.println("Nouvel etat final "+s);
						}
						//ArrayList<Transition> pred=lts.predecesseurs(target);
						ArrayList<Transition> pred=target.getPredecesseurs();
						if ((lts.getInitialState()!=target) && (pred.size()==0)){
							//System.out.println("Etat plus atteignable, on supprime "+target);
							lts.removeState(target);
						}
						
					}
				}
			}
			
		}
		//System.out.println("Fin Epsilon removal");
		
	}
}
