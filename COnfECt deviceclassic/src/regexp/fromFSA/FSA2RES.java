/*
 *  FSA2RES.java
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
import java.util.HashMap;
import java.util.List;

import fsa.FSA;
import fsa.State;
import fsa.Transition;
import fsa.Trigger;
import regexp.Alternation;
import regexp.Concatenation;
import regexp.Regexp;
import regexp.Literal;
import regexp.AlternationImpl;
import regexp.ConcatenationImpl;
import regexp.LiteralImpl;
import regexp.RegexpUtil;


/**
 * 
 * Performs the transformation of an FSA into an equation system.
 * 
 * @author Sylvain Lamprier, Tewfik Ziaidi, Lom Messan Hillah
 *
 */
public class FSA2RES {

	/**This table indicate which variables are included in the counter equation,
	 * it can be used for searching trivial/non-trivial equation and for 
	 * replacing variable */
	private HashMap<Variable,ArrayList<Variable>> containsTable;
	
	public EquationSystem transform(FSA fsa) {
		
		EquationSystem sys = new SystemImpl(); 
		this.containsTable = new HashMap<Variable,ArrayList<Variable>>();
		sys.setContainsTable(this.containsTable);
		
		for (State state : fsa.getStates()) {
			//an equation: left is a variable, right is an alternation
			Equation eq = new EquationImpl();
			Variable v  = new VariableImpl();
			
			v.setName(state.getName());
			eq.setLeft(v);
			
			Alternation alt = new AlternationImpl();
			eq.setRight(alt);
			
			ArrayList<Variable> contains = new ArrayList<Variable>();
			
			//set values for "right": R[i] = a[1]R[1] + a[2]R[2] +  ...  
			for (Transition transition : fsa.getTransitions()) {
				if (transition.getSource().equals(state)) {	
					//a
					Concatenation conc = new ConcatenationImpl();
					conc.getExp().add(translateTrigger(transition.getTrigger()));

					//R
					Variable var  = new VariableImpl();
					var.setName(transition.getTarget().getName());
					conc.getExp().add(var);
					
					//aR
					alt.getExp().add(conc);
					
					//table
					contains.add(var);
				}
			}
			
			//lambda
			if (fsa.getFinalStates().contains(state)) {
				alt.getExp().add(new Epsilon());
			}
			
			//set table
			this.containsTable.put(v, contains);
			
			sys.getEquation().add(eq);				
		}
				
		Variable obj  = new VariableImpl();
		obj.setName(fsa.getInitialState().getName());
		sys.setObjective(obj);
		//printSys(sys);//TODO
		return sys;
	}
	
	private Regexp translateTrigger(Trigger trigger) {
		Literal literal = new LiteralImpl();
		literal.setValue(trigger);
		return literal;
	}

	/*private Regexp translateTrigger(Trigger trigger) {
		StringLiteral literal = new StringLiteralImpl();
		literal.setValue(trigger.toString());
		return literal;
	}*/
	
	/**used for verifying the construction of the equation system*/
	public static void printSys(EquationSystem sys) {
		for(Equation e: sys.getEquation()) {
			String equation = "E: " + e.getLeft().getName()+ " = " + RegexpUtil.reg2String(e.getRight()); //getLiteral(e.getRight());
			System.out.println(equation);
		}
	}
	
	/**used for verifying the construction of the equation system*/
	public static void printEquation(Equation e) {
			String equation = "E: " + e.getLeft().getName()+ " = " + RegexpUtil.reg2String(e.getRight()); //getLiteral(e.getRight());
			System.out.println(equation);
		
	}
	
	public static String getLiteral(Regexp reg) {
		String s = "";
		List<Regexp> list = ((Alternation) reg).getExp();
		for(Regexp r: list) {
			if(r instanceof Concatenation) {
				for(Regexp re: ((Concatenation) r).getExp()) {
					if(re instanceof Literal){
						s += ((Literal) re).getValue();
					}else if(re instanceof Variable){
						s += ((Variable) re).getName();
					}else {
						System.out.println("WRONG CONC..."+re.getClass());
					}
				}
				s += " + ";
			}else if (r instanceof Epsilon) {
				s+= "epsilon";
			}else {
				System.out.println("WRONG EQUATION "+r);
			}
		}
		return s;
	}

	public HashMap<Variable, ArrayList<Variable>> getContainsTable() {
		return containsTable;
	}

	public void setContainsTable(
			HashMap<Variable, ArrayList<Variable>> containsTable) {
		this.containsTable = containsTable;
	}

}
