/*
 *  RegexpExtractionUtils.java
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

import java.util.HashMap;
import java.util.List;

import regexp.Alternation;
import regexp.Concatenation;
import regexp.KleeneStar;
import regexp.Regexp;
import regexp.Literal;
import regexp.fromFSA.Equation;
import regexp.fromFSA.EquationSystem;
import regexp.fromFSA.Variable;

/**
 * 
 * 
 * @author Lom Messan Hillah
 * @author Tewfik Ziaidi
 * @author Sylvain Lamprier
 *
 * Defines tools for comparisons performed during the regexp extraction process 
 *
 */

public class RegexpExtractionUtils {

	
	
	/** a simple contains method, to find if there is the same variable*/ 
	public static boolean simplyContains(Regexp container, Variable contained) {
		if ((container instanceof Variable) && contained.equals(container)) {
			return true;
		} else {
			if (container instanceof Alternation) {
				for (Regexp exp : ((Alternation)container).getExp()) {
					if(exp instanceof Concatenation) {
						//last one
						List<Regexp> list = ((Concatenation)exp).getExp();
						Regexp last = list.get(list.size()-1);
						if (last instanceof Variable) {
							if (((Variable)last).equals(contained))
								return true;
						}
					} else if(exp instanceof Alternation) {//TODO test
						System.out.println("Not possible: alt in alt :RegexpUtil");
					} else if(exp instanceof Variable) {
						if(contained.equals(exp)) 
							return true;
					} else if(exp instanceof KleeneStar) {//TODO test
						if(((KleeneStar) exp).getExp() instanceof Variable)
							System.out.println("Not possible: var in star :RegexpUtil");
					}
				}				
			} else if (container instanceof Concatenation) {
				//last one
				List<Regexp> list = ((Concatenation)container).getExp();
				Regexp last = list.get(list.size()-1);
				if (last instanceof Variable) {
					if (((Variable)last).equals(contained))
						return true;
				}
			} else if (container instanceof KleeneStar) {//impossible
				if(((KleeneStar) container).getExp() instanceof Variable) {
					System.out.println("BIG PB: KLEENESTAR CONTAINS VARIABLE when compare simplyContains :RegexpUtil");//TODO
					return contained.equals(((KleeneStar)container).getExp());
				}

			} 			
		}		
		
		return false;
	}

	/**check the Regexp to see whether there are Variable object*/
	public static boolean containsVar(Regexp container) {
		if(container instanceof Alternation) {
			List<Regexp> exps = ((Alternation) container).getExp();
			for(Regexp exp: exps) {
				if (exp instanceof Variable) {
					return true;
				} else if(exp instanceof Concatenation) {
					//verify the last one
					Regexp last = ((Concatenation) exp).getExp().get(((Concatenation) exp).getExp().size()-1);
					if(last instanceof Variable) {
						return true;
					}
				} else if(exp instanceof KleeneStar) {
					Regexp cexp = ((KleeneStar) exp).getExp();

					if(cexp instanceof Variable) {
						System.out.println("==IMPOSSIBLE: a Variable was found in Star!!!!!!!!!!== :RegexpUtil");
						return true;
					}
				}
			}
		} else if(container instanceof Concatenation) {
			List<Regexp> exps = ((Concatenation) container).getExp();

			//verify the last one
			Regexp last = exps.get(exps.size()-1);
			if(last instanceof Variable) {
				return true;
			}
		} else if(container instanceof KleeneStar) {
			Regexp exp = ((KleeneStar) container).getExp();

			if(exp instanceof Variable) {
				System.out.println("==IMPOSSIBLE: a Variable was found in Star!!!!!!!!!!== :RegexpUtil");
				return true;
			}
		}
		
		return false;
	}
	
	/**print method for testing equation variations*/
	public static void printEquation(Equation e) {
		Regexp reg = e.getRight();
		String eqs = "E: "+ e.getLeft().getName() + " = ";
		eqs += reg2String(reg);
		System.out.println(eqs);
		
	}
	
	public static String reg2String(Regexp reg) {
		String s = "";
		
		if(reg instanceof Alternation) {
			s += " ( ";
			List<Regexp> list = ((Alternation) reg).getExp();
			for(int i=0;i<list.size();i++) {
				Regexp re=list.get(i);
				s += (reg2String(re));
				if(i<list.size()-1){
					s+=" + ";
				}
			}
			s += " ) ";
			
		} else if(reg instanceof Concatenation) {
			s += " [ ";
			List<Regexp> list = ((Concatenation) reg).getExp();
			for(Regexp re : list) {
				s += reg2String(re);
			}
			s += " ] ";
			
		} else if(reg instanceof KleeneStar) {
			s += " { ";
			Regexp re = ((KleeneStar) reg).getExp();
			s += reg2String(re);
			s += " }* ";
			
		} else if(reg instanceof Epsilon) {
			s += " epsilon ";
			
		} else if(reg instanceof EmptySet) {
			s +="{}";
			
		} else if(reg instanceof Literal) {
			s += ((Literal) reg).getValue();
			
		} else if(reg instanceof Variable) {
			s += ((Variable) reg).getName();
			
		} else {
			System.out.println("Wrong reg to print :RegexpUtil.java");
		}
		
		return s;
	}
	
	public static void printSys(EquationSystem sys) {
		for(Equation e: sys.getEquation()) {
			printEquation(e);
		}
	}
	
	/** a simple contains method, to find if there is the same variable*/ 
	public static boolean eqContainsVariable(EquationSystem sys,Equation equation, Variable contained) {
		for(Variable v: sys.getContainsTable().get(equation.getLeft())) {
			if (v.equals(contained))
				return true;
		}
		return false;
	}
	
	/*public static HashMap<Regexp,Regexp> getLastEls(Alternation alt){
		
	}*/
	
	///**simply verify each element of a alternation and unfold or delete the lambda */
	/*public static void simpleSimplify() {//TODO
		
	}*/
}
