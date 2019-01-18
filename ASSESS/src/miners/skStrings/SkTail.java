/*
 *  SkTail.java
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


package miners.skStrings;

import fsa.*;
import traces.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * The SkTail class which represents a k-sized sequence starting from a given state of an fsa.  
 * 
 * @author Sylvain Lamprier
 *
 */
public class SkTail implements Comparable<SkTail> {
	private static final long serialVersionUID = 1L;
	 //State init;
	 ArrayList<State> states;
	private static int nbTails=0;
	 ArrayList<Statement> statements;
	 
	private int id;
	/**
	 * Proba of occurence of this tail.
	 */
	double proba=-1.0;
	
	ArrayList<Double> probas=new ArrayList<Double>();
	
	boolean complete;
	//static HashMap<State,HashSet<SkTail>> byInits=new HashMap<State,HashSet<SkTail>>();
	static HashMap<State,HashSet<SkTail>> byStates=new HashMap<State,HashSet<SkTail>>();
	static HashMap<State,ArrayList<SkTail>> byInits=new HashMap<State,ArrayList<SkTail>>();
	static HashMap<State,Double> sumProbas=new HashMap<State,Double>();
	public SkTail(State init){
		complete=false;
		//this.init=init;
		this.states=new ArrayList<State>();
		states.add(init);
		this.statements=new ArrayList<Statement>();
		//this.probas=probas;
		nbTails++;
		id=nbTails;
		/*HashSet<SkTail> i=byInits.get(init);
		if(i==null){
			i=new HashSet<SkTail>();
			byInits.put(init, i);
		}
		i.add(this);
		*/
	}
	public SkTail(SkTail sk){
		this(sk.states.get(0));
		for(int i=1;i<sk.states.size();i++){
			states.add(sk.states.get(i));
		}
		for(int i=0;i<sk.statements.size();i++){
			statements.add(sk.statements.get(i));
		}
		for(int i=0;i<sk.probas.size();i++){
			probas.add(sk.probas.get(i));
		}
	}
	public String toString(){
		return statements.toString()+" = "+proba+" => states = "+states;
	}
	
	/*public void terminateWith(SkTail sk,double nb){
		int k=nb;
		if(sk.statements.size()<nb){
			k=sk.statements.size();
		}
		for(int i=0;i<k;i++){
			statements.add(sk.statements.get(i));
			states.add(sk.states.get(i+1));
			probas.add(sk.probas.get(i));
		}
		double x=1;
		if(k<nb){
			x=sk.probas.get(k);
		}
		setComplete(x);
	}*/
	void setComplete(double n){
		probas.add(n);
		complete=true;
		for(State st:states){
			HashSet<SkTail> i=byStates.get(st);
			if(i==null){
				i=new HashSet<SkTail>();
				byStates.put(st, i);
			}
			i.add(this);
		}
	}
	
	void addTransition(State target,Statement st, double p){
		probas.add(p);
		states.add(target);
		statements.add(st);
		HashSet<SkTail> i=byStates.get(target);
		if(i==null){
			i=new HashSet<SkTail>();
			byStates.put(target, i);
		}
		i.add(this);
	}
	
	void computeProba(){
		if(!complete){
			throw new RuntimeException("Cannot compute proba on a non complete tail");
		}
		this.proba=1.0;
		for(double x:probas){
			proba*=x;
		}
		if(proba<0.001){
			proba=0.001;
		}
	}
	
	public int compareTo(SkTail sk){
		if(proba==sk.proba){
			return 0;
		}
		if(proba<sk.proba){
			return 1;
		}
		return -1;
	}
	
	/*
	 * Methode qui renvoit vrai si l'instance courante et celle passee en parametre sont egales
	 */
	public boolean equals(Object o){
		if(o==this) return true;
		else if((o==null) || (o.getClass() != this.getClass()))return false;
		SkTail tail = (SkTail)o;	
		return this.id==tail.id;
		
	}
	
	@Override
	public int hashCode(){
		return id;
	}
	
	public boolean isEquivalentTo(SkTail o){
		if(statements.size()!=o.statements.size()){
			return false;
		}
		for(int i=0;i<statements.size();i++){
			Statement st1=statements.get(i);
			Statement st2=o.statements.get(i);
			if(!st1.equals(st2)){
				return false;
			}
		}
		return true;
	}
	
	public boolean isEquivalentTo(ArrayList<Statement> o){
		if(statements.size()!=o.size()){
			return false;
		}
		for(int i=0;i<statements.size();i++){
			Statement st1=statements.get(i);
			Statement st2=o.get(i);
			if(!st1.equals(st2)){
				return false;
			}
		}
		return true;
	}
	
	public boolean isTheSameAs(SkTail o){
		if(statements.size()!=o.statements.size()){
			return false;
		}
		for(int i=0;i<statements.size();i++){
			Statement st1=statements.get(i);
			Statement st2=o.statements.get(i);
			if(!st1.equals(st2)){
				return false;
			}
		}
		for(int i=0;i<states.size();i++){
			State st1=states.get(i);
			State st2=o.states.get(i);
			if(!st1.equals(st2)){
				return false;
			}
		}
		return true;
	}
	
	public static boolean hasEquivalent(State s,ArrayList<Statement> o){
		ArrayList<SkTail> i=byInits.get(s);
		if(i==null) return false;
		for(SkTail sk:i){
			if(sk.isEquivalentTo(o)){
				return true;
			}
		}
		return false;
	}
	public void remove(){
		ArrayList<SkTail> i=byInits.get(states.get(0));
		if(i!=null){
			i.remove(this);
		}
		for(State st:states){
			HashSet<SkTail> j=byStates.get(st);
			if(j!=null){
				j.remove(this);
			}
		}
	}
	/*static SkTail merge(SkTail x,SkTail y,int index){
		
		ArrayList<State> sx=x.states;
		ArrayList<Statement> stx=x.statements;
		if(index>=stx.size()){
			throw new RuntimeException("merge index too high");
		}
		if(index<=0){
			throw new RuntimeException("merge index too low");
		}
		ArrayList<State> sy=y.states;
		ArrayList<Statement> sty=y.statements;
		
		ArrayList<Statement> nst=new ArrayList<Statement>();
		ArrayList<State> ns=new ArrayList<State>();
		ArrayList<Double> np=new ArrayList<Double>();
		State nInit=x.init;
		
		
		for(int i=0;i<index;i++){
			ns.add(sx.get(i));
			nst.add(stx.get(i));
			
		}
		for(int i=index;i<sty.size();i++){
			ns.add(sy.get(i));
			nst.add(sty.get(i));
			
		}
		ns.add(sy.get(sy.size()-1));
		SkTail ret=new SkTail(nInit,ns,nst);
		return ret;
	}*/
	
	/*static void mergeSkTails(State x,State y){
		HashSet<SkTail> sy=byStates.get(y);
		HashSet<SkTail> sx=byStates.get(x);
		HashSet<SkTail> ns=new HashSet<SkTail>();
		if((sy!=null) && (sx!=null)){
			for(SkTail sk:sy){
				int j=1;
				while(j>=0){
					j=sk.states.indexOf(y);
					sk.states.set(j, x);
					
				}
			}
		}
		
		
		HashSet<SkTail> i=byInits.get(y);
		if(i!=null){
			for(SkTail sk:i){
				sk.init=x;
			}
		}
		byInits.remove(y);
		i=byStates.get(y);
		if(i!=null){
			for(SkTail sk:i){
				int j=1;
				while(j>=0){
					j=sk.states.indexOf(y);
					sk.states.set(j, x);
				}
			}
		}
	}*/
}
