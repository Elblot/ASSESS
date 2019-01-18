/*
 *  SimpleAlg.java
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

package miners;

import java.util.ArrayList;

import fsa.GenerateDOT;
import fsa.State;
import fsa.Transition;
import traces.Method;
import traces.ObjectClass;
import traces.ObjectInstance;
import traces.Statement;
import traces.Trace;
import fsa.FSA;
import java.util.HashMap;
/**
 * 
 * The SimpleAlg algorithm as detailled in "Ivan Beschastnikh, Yuriy Brun, Jenny Abrahamson, Michael D. Ernst, Arvind Krishnamurthy: Unifying FSM-inference algorithms through declarative specification. 252-261 ICSE 2013."
 * 
 * @author Sylvain Lamprier
 *
 */
public class SimpleAlg implements FSAminer {
	private static final long serialVersionUID = 1L;
	
	@Override
	public FSA transform(ArrayList<Trace> traces) {
		FSA fsa = new FSA();
		State root=new State();
		fsa.addState(root);
		HashMap<String,State> states=new HashMap<String,State>();
		fsa.setInitialState(root);
		for (Trace trace : traces) {
			int size=trace.getSize();
			State last=root;
			for(int i=1;i<=size;i++){
				State.nbManipSeq++;
				Statement st=trace.getByIndex(i);
				String sttext=st.getText();
				ArrayList<Transition> succs=last.getSuccesseurs(st);
				
				State next=null;
				if(states.containsKey(sttext)){
					next=states.get(sttext);
				}
				else{
					next=new State();
					State.nbVisitsStates++;
					fsa.addState(next);
					states.put(sttext, next);
				}
				if(succs.size()==0){
					Transition t=new Transition(last,st,next);
					fsa.addTransition(t);
				}
				last=next;
			}
			fsa.addFinalState(last);
		}
		
		return fsa;
	}

	@Override
	public String getName() {
		
		return "SimpleAlg";
	}

	public static void main(String args[]) {
		
		ObjectClass dm = new ObjectClass("dummy Class");
		ObjectInstance obj = new ObjectInstance("dummy obj", dm) ;
		ArrayList<ObjectInstance> list = new ArrayList<ObjectInstance>();
		Trace t1 = new Trace();
		Method m1 = new Method("0", list, dm);
		Statement st1 = new Statement(obj, m1, obj);
		Method m3 = new Method("1", list, dm);
		Statement st3 = new Statement(obj, m3, obj);
		t1.add(st1);
		t1.add(st1);
		Trace t2 = new Trace();
		t2.add(st3);
		t2.add(st3);
		Trace t3 = new Trace();
		t3.add(st1);
		t3.add(st1);
		t3.add(st1);
		t3.add(st1);
		Trace t4 = new Trace();
		t4.add(st1);
		t4.add(st3);
		t4.add(st1);
		t4.add(st3);
		Trace t5 = new Trace();
		t5.add(st1);
		t5.add(st3);
		t5.add(st3);
		t5.add(st1);
		Trace t6 = new Trace();
		t6.add(st3);
		t6.add(st1);
		t6.add(st3);
		t6.add(st1);
		ArrayList<Trace> tabTrace=new ArrayList<Trace>();
		tabTrace.add(t1);
		tabTrace.add(t2);
		tabTrace.add(t3);
		tabTrace.add(t4);
		tabTrace.add(t5);
		tabTrace.add(t6);
		SimpleAlg alg = new SimpleAlg();
		FSA fsa = alg.transform(tabTrace);
		GenerateDOT.printDot(fsa, "test.dot");
	}
	
}
