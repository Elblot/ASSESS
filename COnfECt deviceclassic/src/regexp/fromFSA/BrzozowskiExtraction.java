/*
 *  BrzozowskiExtraction.java
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


import java.io.FileNotFoundException;
import java.util.ArrayList;

import fsa.FSA;
import fsa.GenerateDOT;
import fsa.State;
import fsa.Transition;
import program.Main;


import traces.ObjectClass;
import traces.Method;
//import traces.Parameter;
import traces.ObjectInstance;
import traces.ObjectClass;


import traces.Statement;
import regexp.Regexp;
import regexp.RegexpUtil;


/**
 * 
 * Performs a  Brzozowski transfomation of an LTS to obtain a regular expression.
 * 
 * @author Sylvain Lamprier, Tewfik Ziaidi, Lom Messan Hillah
 *
 */
public class BrzozowskiExtraction {

	
	
		
    public static Regexp generate(FSA myFSA){
		
    	//FSA Determinization 
    	
    	
    	
		//create equation system
		FSA2RES fsa2RES= new FSA2RES();
		EquationSystem sys = fsa2RES.transform(myFSA);
		System.out.println("LTS STATES SIZE: " + myFSA.getStates().size());
		
		System.out.println("=EQ sys built=");
		FSA2RES.printSys(sys);
		//resolve equations TODO
		SimplifyRES simplify = new SimplifyRES();
		EquationSystem sysSimp =  (EquationSystem) simplify.transform(sys);
		//System.out.println("=EQ Simplified=");
		
		//obtain the answer and generate SD file TODO
		Equation lastEq = sysSimp.getEquation().get(0);
		Regexp reg = lastEq.getRight();
		//System.out.println("=EQ get 0 get right=");
		//System.out.println("REG from right++++++++++++++"+reg.getClass());//TODO
		//BrzGenerateSequenceDiagram.generateSD(reg, objects, fileName);
		//System.out.println("=EQ dont SD gene=");
		//TCMGenerateSequenceDiagram.generateSD(reg, objects, fileName);
		return(reg);
	}
    
    
	
	

   
    
	public static void main(String[] args) {
		FSA lts=new FSA();
		State s1=new State("s1");
		State s2=new State("s2");
		State s3=new State("s3");
		State s4=new State("s4");
		State s5=new State("s5");
		lts.addState(s1);
		lts.addState(s2);
		lts.addState(s3);
		lts.addState(s4);
		lts.addState(s5);
		lts.setInitialState(s1);
		lts.setFinalState(s5);
		
		
		ObjectClass ihm=new ObjectClass("UserIHM");
		ObjectInstance ihm0 = new ObjectInstance("ihm0", ihm);
		ObjectClass account=new ObjectClass("Account");
		ObjectInstance account0 = new ObjectInstance("account0", account);
		ObjectClass bank=new ObjectClass("Bank");
		ObjectInstance bank0 = new ObjectInstance("bank0",bank);
		ObjectClass atm=new ObjectClass("ATM");
		ObjectInstance atm0 = new ObjectInstance("atm0", atm);
		ObjectClass cons=new ObjectClass("Consortium");
		ObjectInstance cons0 = new ObjectInstance("cons0", cons);
		ObjectClass accGen = new ObjectClass("AccountGenerator");
		ObjectInstance accGen0 = new ObjectInstance("accGen0", accGen);
		
		ArrayList<ObjectInstance> actors = new ArrayList<ObjectInstance>();
		actors.add(ihm0);
		actors.add(cons0);
		actors.add(atm0);
		actors.add(bank0);
		actors.add(account0);
		actors.add(accGen0);
		int i=0;
		
		
		/*
		 * InsertCard Bloc 
		 */	
		Statement st1=new Statement("S"+i++,atm0,new Method("checkAccount",new ArrayList<ObjectInstance>(),bank),bank0);
		Statement st2=new Statement("S"+i++,atm0,new Method("checkCard",new ArrayList<ObjectInstance>(),atm),atm0);
		
		Statement st3=new Statement("S"+i++,atm0,new Method("requestPass",new ArrayList<ObjectInstance>(),ihm),ihm0);
		
		Statement st4=new Statement("S"+i++,atm0,new Method("ejectCard",new ArrayList<ObjectInstance>(),ihm),ihm0);
		Statement st5=new Statement("S1"+i++,atm0,new Method("requestTakeCard",new ArrayList<ObjectInstance>(),ihm),ihm0);
		Statement st6=new Statement("S1"+i++,atm0,new Method("quit",new ArrayList<ObjectInstance>(),ihm),ihm0);
		Statement st7=new Statement("S1"+i++,atm0,new Method("blabla",new ArrayList<ObjectInstance>(),ihm),ihm0);
		
		
		Transition tr1=new Transition(s1,st1,s2);
		Transition tr2=new Transition(s2,st2,s3);
		Transition tr3=new Transition(s2,st3,s4);
		Transition tr4=new Transition(s4,st4,s2);
		Transition tr5=new Transition(s4,st5,s5);
		Transition tr6=new Transition(s3,st6,s5);
		Transition tr7=new Transition(s5,st7,s2);
		lts.addTransition(tr1);
		lts.addTransition(tr2);
		lts.addTransition(tr3);
		//lts.addTransition(tr4);
		lts.addTransition(tr5);
		lts.addTransition(tr6);
		lts.addTransition(tr7);
		GenerateDOT.printDot(lts, "lts.dot");
		//try{
			Regexp reg=generate(lts);
			FSA fsa2=RegexpUtil.reg2FSA(reg);
			GenerateDOT.printDot(fsa2, "fromRegexp.dot");
			//System.out.println(reg);
		/*}
		catch(Exception e){
			System.out.println(e);
		}*/
		
	}
	
	public static void main2(String[] args){
		Main sd=Main.deserialize("ATM/Model/blocs_sd_model.sd");
		System.out.println(RegexpUtil.reg2String(sd));
	}
}      

