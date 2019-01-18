package fsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import traces.Statement;
//TODO
public class Determinization {
	public static FSA getDFA(FSA fsa){
		FSA dfa=new FSA();
		HashSet<State> todo=new HashSet<State>();
		HashSet<State> done=new HashSet<State>();
		
		HashMap<String,HashSet<State>> contains=new HashMap<String,HashSet<State>>();
		HashMap<String,State> states=new HashMap<String,State>(); 
		State init=fsa.getInitialState();
		
		State s0=new State("init");
		dfa.addState(s0);
		dfa.setInitialState(s0);
		todo.add(s0);
		HashSet<State> h=new HashSet<State>();
		contains.put(s0.getName(),h);
		h.add(init);
		while(todo.size()>0){
			for(State st:todo){
				h=contains.get(st.getName());
				HashMap<Statement,TreeSet<State>> suc=new HashMap<Statement,TreeSet<State>>();
				for(State sc:h){
					ArrayList<Transition> succs=sc.getSuccesseurs();
				}
			}
			
		}
		return dfa;
	}
}
