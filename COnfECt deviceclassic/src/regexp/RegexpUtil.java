/*
 *  RegExpUtil.java
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

package regexp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;












import program.Main;
import programs.Program_ATM_Factory;
import regexp.fromFSA.BrzozowskiExtraction;
import regexp.fromFSA.EmptySet;
import regexp.fromFSA.Epsilon;
import regexp.fromFSA.RegexpExtractionUtils;
//import regexp.fromLTS.EmptySet;
//import regexp.fromLTS.Epsilon;
import regexp.fromFSA.Variable;
import traces.Method;
import traces.ObjectClass;
import traces.ObjectInstance;
import traces.Statement;
import fsa.EpsilonRemover;
import fsa.FSA;
import fsa.GenerateDOT;
import fsa.State;
import fsa.Transition;
/**
 * 
 * 
 * @author Lom Messan Hillah
 * @author Tewfik Ziaidi
 * @author Sylvain Lamprier
 *
 * Defines tools for regular expressions  
 *
 */

public class RegexpUtil {
	public static boolean contains(Regexp container, Regexp contained) {
		if (container.equals(contained)) {
			return true;
		} else {
			if (container instanceof Alternation) {
				for (Regexp exp : ((Alternation)container).getExp()) {
					if (contains(exp, contained)) {
						return true;
					}
				}				
			} else if (container instanceof Concatenation) {
					for (Regexp exp : ((Concatenation)container).getExp()) {
						if (contains(exp, contained)) {
							return true;
						}
					}				
			} else if (container instanceof KleeneStar) {
				return contains(((KleeneStar)container).getExp(), contained);
			} 			
		}		
		return false;
	}
	
	public static String reg2String(Regexp reg) {
		return RegexpExtractionUtils.reg2String(reg);
		/*String s = "";
		
		if(reg instanceof Alternation) {
			s += " { ";
			List<Regexp> list = ((Alternation) reg).getExp();
			for(int i=0;i<list.size();i++) {
				Regexp re=list.get(i);
				s += (reg2String(re));
				if(i<list.size()-1){
					s+=" + ";
				}
			}
			s += " } ";
			
		} else if(reg instanceof Concatenation) {
			s += " { ";
			List<Regexp> list = ((Concatenation) reg).getExp();
			for(Regexp re : list) {
				s += reg2String(re);
			}
			s += " } ";
			
		} else if(reg instanceof KleeneStar) {
			s += " { ";
			Regexp re = ((KleeneStar) reg).getExp();
			s += reg2String(re);
			s += " }* ";
			
		} else if(reg instanceof Epsilon) {
			s += " epsilon ";
			
		} else if(reg instanceof EmptySet) {
			s +="{}";
			
		}
		else if(reg instanceof Literal) {
			s += ((Literal) reg).getValue();
			
		} else if(reg instanceof Variable) {
			s += ((Variable) reg).getName();
			
		} else {
			System.out.println("Wrong reg to print :RegexpUtil.java");
		}
		
		return s;*/
	}
	
	/**
	 * Returns the number of (non epsilon) statements in the regexp
	 * @return
	 */
	public static int getSize(Regexp reg){
		int nb=0;
		if(reg instanceof Alternation) {
			
			List<Regexp> list = ((Alternation) reg).getExp();
			for(int i=0;i<list.size();i++) {
				Regexp re=list.get(i);
				nb += (getSize(re));
				
			}
			
			
		} else if(reg instanceof Concatenation) {
			List<Regexp> list = ((Concatenation) reg).getExp();
			for(Regexp re : list) {
				nb += (getSize(re));
			}
			
			
		} else if(reg instanceof KleeneStar) {
			Regexp re = ((KleeneStar) reg).getExp();
			nb += (getSize(re));
			
		} else if(reg instanceof Epsilon) {
			nb=0; 
			
		} else if(reg instanceof EmptySet) {
			nb=0;
		}
		else if(reg instanceof Literal) {
			nb=1; 
		} else {
			System.out.println("Wrong reg to print :RegexpUtil.java");
		}
		return nb;
	}
	/**
	 * Returns the mean depth of the regexp. 
	 * Depth is increased only when an alternation or a kleenestar is met. 
	 * The mean is computed only on Literals. 
	 * @return
	 */
	public static double getMeanDepth(Regexp reg){
		HashMap<String,Integer> counts=getDepthCounts(reg,0);
		Integer nb=counts.get("nb");
		Integer sum=counts.get("sum");
		double ret=0.0;
		if(nb>0){
			ret=sum/nb;
		}
		return ret;
	}
	private static HashMap<String,Integer> getDepthCounts(Regexp reg,int depth){
		HashMap<String,Integer> ret=new HashMap<String,Integer>();		
		if(reg instanceof Alternation) {
			
			List<Regexp> list = ((Alternation) reg).getExp();
			for(int i=0;i<list.size();i++) {
				Regexp re=list.get(i);
				HashMap<String,Integer> r=getDepthCounts(re,depth+1);
				Integer n=ret.get("nb");
				n=(n==null)?0:n;
				Integer s=ret.get("sum");
				s=(s==null)?0:s;
				ret.put("nb", n+r.get("nb"));
				ret.put("sum", s+r.get("sum"));
				
			}
			
			
		} else if(reg instanceof Concatenation) {
			List<Regexp> list = ((Concatenation) reg).getExp();
			for(Regexp re : list) {
				HashMap<String,Integer> r=getDepthCounts(re,depth);
				Integer n=ret.get("nb");
				n=(n==null)?0:n;
				Integer s=ret.get("sum");
				s=(s==null)?0:s;
				ret.put("nb", n+r.get("nb"));
				ret.put("sum", s+r.get("sum"));
			}
			
			
		} else if(reg instanceof KleeneStar) {
			Regexp re = ((KleeneStar) reg).getExp();
			HashMap<String,Integer> r=getDepthCounts(re,depth+1);
			Integer n=ret.get("nb");
			n=(n==null)?0:n;
			Integer s=ret.get("sum");
			s=(s==null)?0:s;
			ret.put("nb", n+r.get("nb"));
			ret.put("sum", s+r.get("sum"));
			
		} else if(reg instanceof Epsilon) {
			ret.put("nb",0);
			ret.put("sum",0);
			
		} else if(reg instanceof EmptySet) {
			ret.put("nb",0);
			ret.put("sum",0);
		}
		else if(reg instanceof Literal) {
			ret.put("nb",1);
			ret.put("sum",depth);
		} else {
			System.out.println("Wrong reg to print :RegexpUtil.java");
		}
		return ret;
	}
	
	public static Regexp FSA2Regexp(FSA fsa) {
		return BrzozowskiExtraction.generate(fsa);
	}
	public static FSA reg2FSA(Regexp reg) {
		FSA fsa=reg.buildFSA();
		EpsilonRemover.removeEpsilon(fsa);
		return fsa;
	}
	
	public static FSA FSA2regularFSA(FSA fsa) {
		Regexp reg=BrzozowskiExtraction.generate(fsa);
		return reg2FSA(reg);
	}
	
	public static void main(String args[]){
		Main atm=Program_ATM_Factory.create();
		FSA fsa=atm.getFSA();
		FSA fsa2=FSA2regularFSA(fsa);
		GenerateDOT.printDot(fsa, "lts.dot");
		GenerateDOT.printDot(fsa2, "fromRegexp.dot");
 	}
	
	public static void main2(String args[]){
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
		//try{
			Regexp reg=BrzozowskiExtraction.generate(lts);
			System.out.println(reg);
		/*}
		catch(Exception e){
			System.out.println(e);
		}*/
		FSA fsa2=reg2FSA(reg);
		GenerateDOT.printDot(lts, "lts.dot");
		GenerateDOT.printDot(fsa2, "fromRegexp.dot");
	}
}
