/*
 *  ReplaceEquation.java
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
import regexp.RegexpUtil;
import regexp.fromFSA.Equation;
import regexp.fromFSA.EquationSystem;
import regexp.fromFSA.RegexpExtractionUtils;
import regexp.fromFSA.Variable;
import regexp.AlternationImpl;
import regexp.ConcatenationImpl;

/**
 * 
 * Replace a variable in an equation system.  
 * 
 * Three steps to replace a equation:
 * 1- find out the variable to replace
 * 2- replace the variable
 * 3- unfold the unfoldable item*/
public class ReplaceEquation {

	/*if we have to substitute one equation with another entirely*/
	private boolean substitute = false;
	/*whether the Regexp contains a Variable, true means this replacement have to be unfolded*/
	private boolean containsVar = false;
	/*whether the Regexp contains a Variable, true means this replacement have to be unfolded*/
	private boolean modified = false;

	public EquationSystem transform(EquationSystem sys, Equation eq) {
		return replaceEquation(sys, eq);
	}

	private EquationSystem replaceEquation(EquationSystem sys, Equation eq) {
		if(RegexpExtractionUtils.containsVar(eq.getRight())) this.containsVar = true;

		//System.out.println("Equation a remplacer : ");
		///RegexpUtil.printEquation(eq);
		
		Equation toRemove = null;
		for (Equation otherEq : sys.getEquation()) {
			if (eq.getLeft().equals(otherEq.getLeft())) {
				toRemove = otherEq;
			} else {
				//if othereq contains eq(var)
				if(sys.getContainsTable().get(otherEq.getLeft()).contains(eq.getLeft())) {
					//System.out.println("Equation a modifier : ");
					//RegexpUtil.printEquation(otherEq);
					
					//RegexpUtil.printSys(sys);
					//System.out.println("@@@@@@@@@@@@@@@@@ now sub @@@@@@@@@@@@@@@@");
					//1 & 2 find out and replace the variable
					//System.out.println(RegexpUtil. reg2String(otherEq.getRight()));
					otherEq.setRight(replaceVariable(otherEq.getRight(), eq));
					//System.out.println(RegexpUtil. reg2String(otherEq.getRight()));
					
					/*if(this.substitute) {// R1 = R2
						otherEq.setRight(eq.getRight());
						this.substitute = false;
					}*/
					//add new var to replaced list
					sys.getContainsTable().get(otherEq.getLeft()).remove(eq.getLeft());
					for(Variable v: sys.getContainsTable().get(eq.getLeft())){
						if(!sys.getContainsTable().get(otherEq.getLeft()).contains(v)) {
							sys.getContainsTable().get(otherEq.getLeft()).add(v);
						}
					}
					
	                otherEq.setRight(this.unfold(otherEq.getRight()));
					//System.out.println("unfold : "+RegexpUtil.reg2String(otherEq.getRight()));
					otherEq.setRight((Regexp) new NormalizeRegexp(false).transform(otherEq.getRight()));
					//FSA2RES.printEquation(otherEq);
					/*otherEq.setRight((Regexp) new NormalizeRegexp(false).transform(otherEq.getRight()));
					FSA2RES.printEquation(otherEq);*/
					
				}
				
			}
		}
		
		sys.getEquation().remove(toRemove);
		sys.getContainsTable().remove(eq.getLeft());
		
		
		return sys;		
	}
	
	public Regexp unfold(Regexp r) {
		//A(B+C)=AB+AC
		if(r instanceof ConcatenationImpl){
			ConcatenationImpl conc=(ConcatenationImpl)r;
			List<Regexp> exps=conc.getExp();
			Regexp last=exps.get(exps.size()-1);
			if(last instanceof AlternationImpl){
				exps.remove(exps.size()-1);
				AlternationImpl ret=new AlternationImpl();
				List<Regexp> nalts=ret.getExp();
				List<Regexp> alts=((AlternationImpl)last).getExp();
				for(Regexp a:alts){
					ConcatenationImpl nconc=(ConcatenationImpl)(conc.copy());
					List<Regexp> nexps=nconc.getExp();
					nexps.add(a.copy());
					nalts.add(nconc);
				}
				return ret;
			}
		}
		//(A+B)C=AB+AC
		else if(r instanceof AlternationImpl){
			//System.out.println("Alt : "+RegexpUtil.reg2String(r));
			AlternationImpl alt=(AlternationImpl)r;
			List<Regexp> exps=alt.getExp();
		
			for(int i=0;i<exps.size();i++){
				exps.set(i, unfold(exps.get(i)));
			}
		}
		return r;
	}
			
	/**check the Regexp to see whether there are Variable object
	 * @param eq the equation to be replaced
	 * @param reg the Regexp need to be checked
	 **/
	public Regexp replaceVariable(Regexp reg, Equation eq) {
		Variable var = eq.getLeft();
		if(reg instanceof Variable){
			if(reg.equals(var)){
				return eq.getRight().copy();
			}
		}
		else if(reg instanceof Alternation) {
			List<Regexp> exps = ((Alternation) reg).getExp();
			Alternation nalt=new AlternationImpl();
			List<Regexp> nexps = nalt.getExp();
			for(Regexp exp: exps) {
				nexps.add(replaceVariable(exp,eq));
			}
			return nalt;
		}
		else if(reg instanceof Concatenation) {
			List<Regexp> exps = ((Concatenation) reg).getExp();
			if(exps.size()>0){
				//System.out.println(exps.get(exps.size()-1));
				exps.set(exps.size()-1, replaceVariable(exps.get(exps.size()-1),eq));
			}
		}
		else if (reg instanceof KleeneStar) {
			Regexp exp=((KleeneStar) reg).getExp();
			((KleeneStar) reg).setExp(replaceVariable(exp,eq));
			return reg;
		}
		return reg;
				
	}
	
	
	/**check the Regexp to see whether there are Variable object
	 * @param eq the equation to be replaced
	 * @param container the Regexp need to be checked
	 **/
	public void replaceVariable_old(Regexp container, Equation eq) {
		Variable var = eq.getLeft();
		
		if(container instanceof Alternation) {
			List<Regexp> exps = ((Alternation) container).getExp();
			ArrayList<Regexp> directEquals = new ArrayList<Regexp>();
			for(Regexp exp: exps) {
				if (exp instanceof Variable) {
					if(var.equals(exp)) {
						directEquals.add(exp);
						this.modified = true;
					}
				} else if(exp instanceof Concatenation) {
					List<Regexp> concList = ((Concatenation) exp).getExp();
					//verify the last one
					Regexp last = concList.get(concList.size()-1);
					if(last instanceof Variable) {
						if(var.equals(last)) {
							concList.remove(concList.size()-1);
							concList.add(eq.getRight());
							this.modified = true;
						}
					}
					
				} else if(exp instanceof KleeneStar) {
					Regexp cexp = ((KleeneStar) exp).getExp();

					if(cexp instanceof Variable) {
						System.out.println("==IMPOSSIBLE: a Variable was found in KleeneStar!!!!!!!!!!== :ReplaceEquation");
						if(var.equals(cexp)) {
							((KleeneStar) exp).setExp(eq.getRight());
							this.modified = true;
						}
					}
				}
			}
			if(directEquals.size()>0) {
				exps.removeAll(directEquals);
				exps.add(eq.getRight());
			}
		} else if(container instanceof Concatenation) {
			List<Regexp> exps = ((Concatenation) container).getExp();

			//verify the last one only, because the variable can only exist at last place in each concatenation
			Regexp last = exps.get(exps.size()-1);
			if(last instanceof Variable) {
				if(var.equals(last)) {
					exps.remove(exps.size()-1);
					exps.add(eq.getRight());
					this.modified = true;
				}
			}
		} else if(container instanceof KleeneStar) {//it seems impossible to have this condition, but i have to test
			Regexp exp = ((KleeneStar) container).getExp();

			if(exp instanceof Variable) {
				System.out.println("==IMPOSSIBLE: a Variable was found in Star!!!!!!!!!!== :ReplaceEquation");//TODO
				if(var.equals(exp)) {
					((KleeneStar) container).setExp(eq.getRight());
					this.modified = true;
				}
			}
		} else if(container instanceof Variable) {//it seems impossible to have this condition, but i have to test
			System.out.println("==Variable equals to Variable!!!!!!!!!!==:ReplaceEquation");//TODO
			if(var.equals(container)) {
				this.substitute = true;
				this.modified = true;
			}
		}
	}
	
	
	/**unfold alternations in the concatenation, only unfold elements which contains Variable*/
	public Regexp unfold_old(Regexp right) {
		
		
		if((right instanceof Alternation) && this.containsVar) {//R = abX + (c+d)Y + efZ ...
			Alternation alt = (Alternation) right;
			List<Regexp> list = alt.getExp();
			ArrayList<Regexp> toAdd = new ArrayList<Regexp>();
			ArrayList<Regexp> toRemove = new ArrayList<Regexp>();
			
			for(Regexp exp: list) {//for abX
				//System.out.println("unfold "+RegexpUtil.reg2String(exp));
				if(exp instanceof Alternation) {//a+(b+c)=a+b+c
					
					
					toRemove.add(exp);
					for(Regexp newexp: ((Alternation)exp).getExp()) {
						toAdd.add(newexp);
					}
				} else if(exp instanceof Concatenation) {//a+...+cdX
					toRemove.add(exp);
					Regexp newexp = this.unfoldConcatenation((Concatenation) exp);//TODO  possible pb here
					if(newexp instanceof Alternation){
						for(Regexp e: ((Alternation) newexp).getExp()){
							toAdd.add(e);
						}
					} else {
						toAdd.add(newexp);
					}
				}
			}
			list.removeAll(toRemove);
			list.addAll(toAdd);
			
			return right;
			
		} else if((right instanceof Concatenation) && this.containsVar) {// R = abcX where X may be any RegexpImpl
			Concatenation conc = (Concatenation) right;
			List<Regexp> list = conc.getExp();
			Regexp last = list.get(list.size()-1);
			if((last instanceof Alternation)&& RegexpExtractionUtils.containsVar(last)) {//abc(d+(eR)) = abcd+abceR
				AlternationImpl newRight = new AlternationImpl();
				List<Regexp> newRightList = newRight.getExp();
				List<Regexp> lastList = (( Alternation) last).getExp();
				list.remove(last);
				for(Regexp e: lastList) {
					ConcatenationImpl newConc = new ConcatenationImpl();
					List<Regexp> newConcList = newConc.getExp();
					newConcList.addAll(list);
					//TODO abc (eR)
					if(e instanceof Concatenation) {
						for(Regexp eexp: ((Concatenation) e).getExp()) {
							newConcList.add(eexp);
							newRightList.add(newConc);
							System.out.println("111111111111111111111111111111111111111:replace");
						}
					} else {
						//complete new element and add new element to the new right of equation 
						newConcList.add(e);
						newRightList.add(newConc);
					}
					
				}
				return newRight;//new alt
			} else if((last instanceof Concatenation)&& RegexpExtractionUtils.containsVar(last)) {//abc(deR) = abcdeR
				List<Regexp> lastList = ((Concatenation) last).getExp();
				list.remove(last);
				for(int i=0; i<lastList.size();i++) {
					list.add(lastList.get(i));
				}
				return right;
			} else if((last instanceof KleeneStar)&& RegexpExtractionUtils.containsVar(last)) {//abc(R)* ??? impossible for me
				System.out.println("STRANGE CASE: Variable in STAR :ReplaceEquation");//TODO DELETE
				return null;
			}
			return right;
		} 
		
		return right;
	}
	
	/*deal with concatenation*/
	private Regexp unfoldConcatenation(Concatenation conc) {//only unfold element with V
		List<Regexp> list = conc.getExp();
		Regexp last = list.get(list.size()-1);
		if((last instanceof Alternation)&& RegexpExtractionUtils.containsVar(last)) {//abc(d+eR) = abcd+abceR
			//System.out.println("unfold "+RegexpUtil.reg2String(last));
			AlternationImpl newRight = new AlternationImpl();
			List<Regexp> newRightList = newRight.getExp();
			List<Regexp> lastList = (( Alternation) last).getExp();
			list.remove(last);
			for(Regexp e: lastList) {
				ConcatenationImpl newConc = new ConcatenationImpl();
				List<Regexp> newConcList = newConc.getExp();
				newConcList.addAll(list);
				//System.out.println("unfold "+RegexpUtil.reg2String(newConc));
				if(e instanceof Concatenation) {
					for(Regexp eexp: ((Concatenation) e).getExp()) {
						newConcList.add(eexp);
						
						//System.out.println("22222222222222222222222222222222222222222:replace");//TODO
					}
				} else {
					//complete new element and add new element to the new right of equation 
					newConcList.add(e);
					//newRightList.add(newConc);
				}
				newRightList.add(newConc);
			}
			//System.out.println("New right "+RegexpUtil.reg2String(newRight));
			return newRight;//new alt
		} else if((last instanceof Concatenation)&& RegexpExtractionUtils.containsVar(last)) {//abc(deR) = abcdeR
			List<Regexp> lastList = ((Concatenation) last).getExp();
			list.remove(last);
			for(int i=0; i<lastList.size();i++) {
				list.add(lastList.get(i));
			}
			return conc;
		} else if((last instanceof KleeneStar)&& RegexpExtractionUtils.containsVar(last)) {//abc(R)* ??? impossible for me
			System.out.println("STRANGE CASE: Variable in STAR :ReplaceEquation");//TODO DELETE
			return null;
		}
		return conc;
	}
	
}
