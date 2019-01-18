/*
 *  ApplyLemmaToEquation.java
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

package regexp.fromFSA;

import java.util.ArrayList;
import java.util.List;

import regexp.Alternation;
import regexp.Concatenation;
import regexp.KleeneStar;
import regexp.Regexp;
import regexp.fromFSA.Equation;
import regexp.fromFSA.RegexpExtractionUtils;
import regexp.fromFSA.Variable;
import regexp.AlternationImpl;
import regexp.ConcatenationImpl;

import regexp.KleeneStarImpl;


public class ApplyLemmaToEquation {

	
	public Equation transform(Equation from) {
		applyLemma(from);
		return from;
	}

	private void applyLemma(Equation eq) {
		if (eq.getRight() instanceof Alternation) {
			Alternation alt = (Alternation) eq.getRight();
			List<Regexp> rightList = alt.getExp();
			ArrayList<Regexp> removeList = new ArrayList<Regexp>();
			
			for(Regexp element: rightList) {
				if(RegexpExtractionUtils.simplyContains(element, eq.getLeft())) {
					removeList.add(element);
				}
			}
			
			if(removeList.size() == rightList.size()) {
				eq.setRight(new EmptySet());
				System.out.println("all elements have this Varialbe: applylemma");//TODO delete
			} else {
				rightList.removeAll(removeList);
				
				//R = aR+bR+c ==> R = (a+b)*c
				Alternation aplusb = new AlternationImpl();
				KleeneStar aStar = new KleeneStarImpl();
				aStar.setExp(aplusb);
				
				//delete the variable of remove list
				for(Regexp exp: removeList) {
					if(exp instanceof Concatenation) {
						((Concatenation) exp).getExp().remove(((Concatenation) exp).getExp().size() - 1);
						aplusb.getExp().add(exp);//TODO maybe exp is alt(a+b) or... need to be unfold after
					} else if(exp instanceof Alternation){
						 System.out.println("PB: alternation haven't unfolded before this action: applyLemma");
					} else if(exp instanceof Variable) {
						//R = aR+R+b ==> R = (a+λ)*b
						aplusb.getExp().add(new Epsilon());
					} else {
						//not possible
					}
				}
				
				//add this * to all other non-removed elements' head
				Alternation newRight = new AlternationImpl();
				ArrayList<Regexp> newRightList = (ArrayList<Regexp>)newRight.getExp();
				
				for(Regexp exp: rightList) {
					Concatenation newConc = new ConcatenationImpl();
					newConc.getExp().add(aStar);
					if(exp instanceof Concatenation) {
						//make sure the Variable is outside of the ()
						newConc.getExp().addAll(((Concatenation) exp).getExp());
						newRightList.add(newConc);
					} else if(exp instanceof EmptySet){
						//do nothing
					} else if(exp instanceof Epsilon) {
						if(!newRightList.contains(aStar)) 
							newRightList.add(aStar);
					} else {
						newConc.getExp().add(aStar);
						newConc.getExp().add(exp);
						newRightList.add(newConc);
					}
					
				}
				
				//set right
				eq.setRight(newRight);
				
			}
				
		} else if (eq.getRight() instanceof Concatenation) {//DONE		
			// X = ABCX ==> X = (ABC)*{} = {} brzozowski[5.1, 5.2]		
			eq.setRight(new EmptySet());
		} else if(eq.getRight() instanceof KleeneStar) {
			System.out.println("PB applylemma: find a var in Star");
		} else if(eq.getRight() instanceof Variable) {
			System.out.println("PB applylemma: find a R = R, wasted equation");
		}
		
	}

}
