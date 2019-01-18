/*
 *  SimplifyRES.java
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

import fsa.FSA;
import fsa.GenerateDOT;
import program.Actor;
import program.Alt;
import program.BlockList;
import program.Call;
import program.Loop;
import program.Main;
import regexp.Regexp;
import regexp.RegexpUtil;
import regexp.fromFSA.Equation;
import regexp.fromFSA.EquationSystem;
import traces.Method;
import traces.ObjectClass;
import traces.ObjectInstance;
import traces.Statement;
public class SimplifyRES {

	public EquationSystem transform(EquationSystem from) {
		EquationSystem sys = from;

		int loopcount = 0; // avoid infinite loops
		//System.out.println("==loopcount: " + loopcount + "  equation size: " + sys.getEquation().size());//TODO
		//FSA2RES.printSys(sys);
		//System.out.println("Original system size: " + sys.getEquation().size());
		while(!isSimplified(sys)) {	
			Equation eq = getTrivialEquation(sys);
			//FSA2RES.printSys(sys);
			
			while (eq != null) {//if (eq != null) {//
				//System.out.println(eq);
				eq.setRight(new NormalizeRegexp().simplyNormalize(eq.getRight()));
				sys = (EquationSystem) new ReplaceEquation().transform(sys, eq);
				//FSA2RES.printSys(sys);
				eq = getTrivialEquation(sys);
			}

			/*System.out.println("===========================");
			FSA2RES.printSys(sys);
			System.out.println("****************************");
			*/
			
			
			
			Equation eq2 = getNonTrivialSolvableEquation(sys);
			if(eq2 != null) {//if(eq2 != null) {//
				//System.out.println(eq2);
				
				sys = applyLemma(sys, eq2);
				sys.getContainsTable().get(eq2.getLeft()).remove(eq2.getLeft());
				eq2.setRight((Regexp) new NormalizeRegexp(false).transform(eq2.getRight()));
				
				//eq2 = getNonTrivialSolvableEquation(sys);
			}
						
			loopcount++;
			//System.out.println("==loopcount: " + loopcount + "  equation size: " + sys.getEquation().size());//TODO
			//FSA2RES.printSys(sys);
		}
		normalizeAllEquations(sys);
		RegexpExtractionUtils.printSys(sys);
		
		return sys;
	}

	
	/*S1 =  [ 
	        ATM[atm0]:UserIHM[ihm0].displayMainScreen()UserIHM[ihm0]:ATM[atm0].insertCard()ATM[atm0]:ATM[atm0].checkCard()ATM[atm0]:Bank[bank0].checkAccount()ATM[atm0]:Bank[bank0].closing_checkAccount()ATM[atm0]:ATM[atm0].closing_checkCard() 
	        (  
	        		[ ATM[atm0]:UserIHM[ihm0].ejectCard()ATM[atm0]:UserIHM[ihm0].closing_ejectCard()ATM[atm0]:UserIHM[ihm0].requestTakeCard()ATM[atm0]:UserIHM[ihm0].closing_requestTakeCard() ]  
	        		+ 
	        		[ ATM[atm0]:UserIHM[ihm0].requestPass()ATM[atm0]:UserIHM[ihm0].closing_requestPass() 
	        		  (  
	        				  [ UserIHM[ihm0]:ATM[atm0].enterPassword()ATM[atm0]:Bank[bank0].isPasswordAvailable()ATM[atm0]:Bank[bank0].closing_isPasswordAvailable()UserIHM[ihm0]:ATM[atm0].closing_enterPassword() 
	        				    {  [ UserIHM[ihm0]:ATM[atm0].enterPassword()ATM[atm0]:Bank[bank0].isPasswordAvailable()ATM[atm0]:Bank[bank0].closing_isPasswordAvailable()UserIHM[ihm0]:ATM[atm0].closing_enterPassword() ]  }*
	        				    (  
	        				    	[ ATM[atm0]:UserIHM[ihm0].ejectCard()ATM[atm0]:UserIHM[ihm0].closing_ejectCard()ATM[atm0]:UserIHM[ihm0].requestTakeCard()ATM[atm0]:UserIHM[ihm0].closing_requestTakeCard() ]  
	        				    	+ 
	        				    	[ UserIHM[ihm0]:ATM[atm0].withdrawMoney()ATM[atm0]:ATM[atm0].getMoney()ATM[atm0]:Bank[bank0].remove()Bank[bank0]:Account[account0].getSolde()Bank[bank0]:Account[account0].closing_getSolde() 
	        				    	  (  
	        				    			  epsilon  
	        				    			  +  
	        				    			  [ Bank[bank0]:Account[account0].setSolde()Bank[bank0]:Account[account0].closing_setSolde()Bank[bank0]:Account[account0].getSolde()Bank[bank0]:Account[account0].closing_getSolde() ]  
	        				    	   ) 
	        				    	   ATM[atm0]:Bank[bank0].closing_remove()ATM[atm0]:ATM[atm0].closing_getMoney() 
	        				    	   (  
	        				    			   [ ATM[atm0]:UserIHM[ihm0].ejectCardUponTooHighWithdraw()ATM[atm0]:UserIHM[ihm0].closing_ejectCardUponTooHighWithdraw() ]  
	        				    			   +  
	        				    			   [ ATM[atm0]:UserIHM[ihm0].ejectCard()ATM[atm0]:UserIHM[ihm0].closing_ejectCard() ]  
	        				    	    ) 
	        				    	    ATM[atm0]:UserIHM[ihm0].requestTakeCard()ATM[atm0]:UserIHM[ihm0].closing_requestTakeCard()UserIHM[ihm0]:ATM[atm0].closing_withdrawMoney() ] 
	        				     )  
	        				   ]  
	        				   +  
	        				   [  
	        				      (  
	        				         epsilon  
	        				         +  
	        				         [ UserIHM[ihm0]:ATM[atm0].cancelUponPasswordRequest()UserIHM[ihm0]:ATM[atm0].closing_cancelUponPasswordRequest()ATM[atm0]:UserIHM[ihm0].ackCancelUponPasswdRequest()ATM[atm0]:UserIHM[ihm0].closing_ackCancelUponPasswdRequest() ]  
	        				      ) 
	        				      ATM[atm0]:UserIHM[ihm0].ejectCard()ATM[atm0]:UserIHM[ihm0].closing_ejectCard()ATM[atm0]:UserIHM[ihm0].requestTakeCard()ATM[atm0]:UserIHM[ihm0].closing_requestTakeCard() 
	        				   ] 
	        				   +  
	        				   [ 
	        				     UserIHM[ihm0]:ATM[atm0].withdrawMoney()ATM[atm0]:ATM[atm0].getMoney()ATM[atm0]:Bank[bank0].remove()Bank[bank0]:Account[account0].getSolde()Bank[bank0]:Account[account0].closing_getSolde() (  epsilon  +  [ Bank[bank0]:Account[account0].setSolde()Bank[bank0]:Account[account0].closing_setSolde()Bank[bank0]:Account[account0].getSolde()Bank[bank0]:Account[account0].closing_getSolde() ]  ) ATM[atm0]:Bank[bank0].closing_remove()ATM[atm0]:ATM[atm0].closing_getMoney() (  [ ATM[atm0]:UserIHM[ihm0].ejectCardUponTooHighWithdraw()ATM[atm0]:UserIHM[ihm0].closing_ejectCardUponTooHighWithdraw() ]  +  [ ATM[atm0]:UserIHM[ihm0].ejectCard()ATM[atm0]:UserIHM[ihm0].closing_ejectCard() ]  ) ATM[atm0]:UserIHM[ihm0].requestTakeCard()ATM[atm0]:UserIHM[ihm0].closing_requestTakeCard()UserIHM[ihm0]:ATM[atm0].closing_withdrawMoney() ]  )  ]  ) UserIHM[ihm0]:ATM[atm0].closing_insertCard()ATM[atm0]:UserIHM[ihm0].closing_displayMainScreen() ] 

*/
	
	private void normalizeAllEquations(EquationSystem sys) {
		for (Equation eq : sys.getEquation()) {
			eq.setRight((Regexp) new NormalizeRegexp(true)
					.transform(eq.getRight()));
		}
	}

	private EquationSystem applyLemma(EquationSystem sys, Equation eq) {		
		
		for (Equation eq0 : sys.getEquation()) {
			if (eq.getLeft().equals(eq0.getLeft())) {
				//eq0.setRight(new NormalizeRegexp().transform(eq0.getRight()));
				Equation newEq = (Equation) new ApplyLemmaToEquation().transform(eq0);
				//eq0.setRight(new NormalizeRegexp().transform(newEq.getRight()));
				break;
			}
		}
		
		return sys;		
	}

	private boolean isSimplified(EquationSystem sys) {
		return sys.getEquation().size() == 1
				&& getNonTrivialSolvableEquation(sys) == null
				&& getTrivialEquation(sys) == null;
	}

	/**
	 * get the equation which is contained by itself
	 **/
	private Equation getNonTrivialSolvableEquation(EquationSystem sys) {//TODO REWRITE
		for (Equation eq : sys.getEquation()) {
			if (RegexpExtractionUtils.eqContainsVariable(sys, eq, eq.getLeft())) {
				//System.out.println("GOTTA NON-TRIVIAL E: ");// + eq.getLeft().getName());
				return eq;
			}
		}
		return null;
	}

	/**
	 * get the equation which is NOT contained by itself
	 **/
	private Equation getTrivialEquation(EquationSystem sys) {//DONE
		for (Equation eq : sys.getEquation()) {
			// we don't want to lose the objective variable!!
			if (!eq.getLeft().equals(sys.getObjective())
					&& !RegexpExtractionUtils.eqContainsVariable(sys, eq, eq.getLeft())) {
				//System.out.println("GOTTA TRIVIAL E");
				return eq;
			}
		}
		return null;
	}
	
	
	public static void main(String[] args){
		ObjectClass main=new ObjectClass("Static");
		Actor a0 = new Actor("a0", main);
		ArrayList<Actor> actors=new ArrayList<Actor>();
		Main sd=new Main(actors,100);
		int i=0;
		ObjectClass Void=new ObjectClass("void");
		Call a=new Call(new Statement("S"+i++,a0,new Method("A",new ArrayList<ObjectInstance>(),Void),a0));
		Call b=new Call(new Statement("S"+i++,a0,new Method("B",new ArrayList<ObjectInstance>(),Void),a0));
		Call c=new Call(new Statement("S"+i++,a0,new Method("C",new ArrayList<ObjectInstance>(),Void),a0));
		Call d=new Call(new Statement("S"+i++,a0,new Method("D",new ArrayList<ObjectInstance>(),Void),a0));
		Call e=new Call(new Statement("S"+i++,a0,new Method("E",new ArrayList<ObjectInstance>(),Void),a0));
		Call f=new Call(new Statement("S"+i++,a0,new Method("F",new ArrayList<ObjectInstance>(),Void),a0));
		Call g=new Call(new Statement("S"+i++,a0,new Method("G",new ArrayList<ObjectInstance>(),Void),a0));
		Call h=new Call(new Statement("S"+i++,a0,new Method("H",new ArrayList<ObjectInstance>(),Void),a0));
		
		Alt alt1=new Alt();
		sd.add(alt1);
		
		BlockList alist=new BlockList();
		alt1.add(alist);
		alist.add(a);
		Alt alt2=new Alt();
		alist.add(alt2);
		Loop loop1=new Loop();
		alt2.add(loop1);
		alt2.add(e);
		BlockList ed=new BlockList();
		ed.add(e);
		ed.add(d);
		loop1.setBloc(ed);
		alist.add(e);
		
		BlockList blist=new BlockList();
		alt1.add(blist);
		blist.add(b);
		blist.add(e);
		
		BlockList clist=new BlockList();
		alt1.add(clist);
		clist.add(c);
		clist.add(f);
		
		BlockList glist=new BlockList();
		alt1.add(glist);
		glist.add(g);
		glist.add(h);
		glist.add(f);
		
		FSA fsa=sd.getFSA();
		GenerateDOT.printDot(fsa, "fsa.dot");
		FSA fsa2=RegexpUtil.FSA2regularFSA(fsa);
		GenerateDOT.printDot(fsa2, "fromRegexp.dot");
	}
}
