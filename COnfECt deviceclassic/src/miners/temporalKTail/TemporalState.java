/*
 *  TemporalState.java
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


package miners.temporalKTail;

import java.util.HashMap;

import traces.Statement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import core.Sequence;

import fsa.EpsilonTransitionChecker;
import fsa.FSA;
import fsa.State;
import fsa.Transition;

/**
 * 
 * A state that contains temporal dependencies informations.
 * 
 * @author Sylvain Lamprier
 *
 */
public class TemporalState extends State {
	HashSet<Sequence> requiredPast=null;
	HashSet<Sequence> requiredFuture=null;
	HashMap<Sequence,Integer> ensuredPast=null;  // Si mode ==2, contient les stricts
	HashMap<Sequence,Integer> ensuredFuture=null;
	HashSet<Sequence> changedPast=null;
	HashSet<Sequence> changedFuture=null;
	HashSet<Sequence> past=null;
	HashSet<Sequence> future=null;
	
	//static int updateMode=1; // 0 => intersections / unions d'ensembles, 1=> retraits / ajouts d'ensembles
	//HashMap<Integer,HashSet<Sequence>> possiblePast=null;
	//HashMap<Integer,HashSet<Sequence>> possibleFuture=null;
	
	//HashMap<String,HashMap<Sequence,Integer>> beganFutureSequences=null;
	
	//HashMap<String,HashMap<Sequence,Integer>> beganPastSequences=null;
	//HashMap<String,HashMap<Sequence,Integer>> forbidenPast=null;
	//HashMap<String,HashMap<Sequence,Integer>> forbidenFuture=null;
	//HashMap<String,Sequence> forbidInFuture=null;
	//HashMap<String,Sequence> forbidInPast=null;
	public static TemporalRules rules=new TemporalRules();
	
	public static HashSet<Sequence> possiblePre=new HashSet<Sequence>();
	public static int maxSize=1;
	//public static int mode=0; // 0 => approximated for preSize >1, >0 => exact but more complex
	public static boolean change=false;   // To check wether change in intersection or union
	public static Sequence root=null;
	public TemporalState(){
		//this(new HashSet<Sequence>(),new HashSet<Sequence>(),new HashMap<Sequence,Integer>(),new HashMap<Sequence,Integer>(), new HashSet<Sequence>(), new HashSet<Sequence>());
	}
	public TemporalState(HashSet<Sequence> requiredPast, HashSet<Sequence> requiredFuture,HashMap<Sequence,Integer> ensuredPast, HashMap<Sequence,Integer> ensuredFuture,HashSet<Sequence> past, HashSet<Sequence> future){
		this.requiredFuture=requiredFuture;
		this.requiredPast=requiredPast;
		this.ensuredFuture=ensuredFuture;
		this.ensuredPast=ensuredPast;
		this.past=past;
		this.future=future;
	}
	
	public static  HashSet<Sequence> intersect(HashSet<Sequence> h1,HashSet<Sequence> h2){
		HashSet<Sequence> inter=new HashSet<Sequence>();
		change=false;
		for(Sequence s:h1){
			if (h2.contains(s)){
					inter.add(s);
			}
			else{
				change=true;
			}
			nbManipSeq++;
		}
		return(inter);
	}
	public static  HashSet<Sequence> firstMinusSecond(HashSet<Sequence> h1,HashSet<Sequence> h2){
		HashSet<Sequence> inter=new HashSet<Sequence>();
		change=false;
		for(Sequence s:h1){
			if (!h2.contains(s)){
					inter.add(s);
			}
			else{
				change=true;
			}
			nbManipSeq++;
		}
		return(inter);
	}
	public static HashSet<Sequence> union(HashSet<Sequence> h1,HashSet<Sequence> h2){
		change=false;
		HashSet<Sequence> union=new HashSet<Sequence>();
		for(Sequence s:h1){
			union.add(s);
			nbManipSeq++;
		}
		for(Sequence s:h2){
			union.add(s);
			nbManipSeq++;
		}
		if (union.size()>h1.size()){
			change=true;
		}
		return(union);
	}
	
	public static  HashSet<Sequence> addSecondToFirst(HashSet<Sequence> h1,HashSet<Sequence> h2){
		change=false;
		HashSet<Sequence> added=new HashSet<Sequence>();
	
		for(Sequence s:h2){
			if(!h1.contains(s)){
				h1.add(s);
				added.add(s);
				change=true;
			}
			nbManipSeq++;
		}
		return added;
	}
	
	
	/**
	 * Removes elements in second set from the first one. 
	 * @param h1 First set (the one from which we remove elements).
	 * @param h2 Second Set (the one containing elements to remove).
	 * @return	Removed elements
	 */
	public static HashSet<Sequence> removeSecondFromFirst(HashSet<Sequence> h1,HashSet<Sequence> h2){
		change=false;
		HashSet<Sequence> removed=new HashSet<Sequence>();
		
		for(Sequence s:h2){
			if(h1.contains(s)){
				h1.remove(s);
				removed.add(s);
				change=true;
			}
			nbManipSeq++;
		}
		return removed;
	}
	public static HashMap<Sequence,Integer> copy(HashMap<Sequence,Integer> h){
		HashMap<Sequence,Integer> ret=new HashMap<Sequence,Integer>();
		for(Sequence s:h.keySet()){
			int nb=h.get(s);
			ret.put(s, nb);
			nbManipSeq++;
		}
		return(ret);
	}
	public static HashSet<Sequence> copy(HashSet<Sequence> h){
		HashSet<Sequence> ret=new HashSet<Sequence>();
		for(Sequence s:h){
			ret.add(s);
			nbManipSeq++;
		}
		return(ret);
	}
	
	/*public void updateRequiredFuture(HashSet<Sequence> f){
	    HashSet<Sequence> set;
		if (requiredFuture!=null){
				set=union(requiredFuture,f);
		}
		else{
			set=copy(f);
			change=true;
		}
	    if(change){
			requiredFuture=set;
			for(Transition t:this.getSuccesseurs()){
				TemporalState ts=(TemporalState)t.getTarget();
				HashSet<Sequence> ret=null;
				if (EpsilonTransitionChecker.isEpsilonTransition(t)){
					ret=copy(requiredFuture);
				}
				else{
					Statement statement=(Statement)t.getTrigger();
					String ch=statement.getText();
					//ret=insertAfterAll(ch, past);
					Sequence s=root.getForwardChild(ch);
					//s.addAfter(ch);
					ret=copy(requiredFuture);
					ret.remove(s);
					//System.out.println(ret);
				}
				ts.updateRequiredFuture(ret);
			}
		}
	}*/
	public void updateRequiredFuture(HashSet<Sequence> f){
		updateRequiredFuture(f,0);
	}
	public void updateRequiredFuture(HashSet<Sequence> f,int updateMode){
		nbVisitsStates++;
	    if (requiredFuture!=null){
			if(updateMode==0){	
				requiredFuture=union(requiredFuture,f);
				f=requiredFuture;
			}
			else{
				f=addSecondToFirst(requiredFuture,f);
			}	
			
		}
		else{
			requiredFuture=copy(f);
			change=true;
		}
	    if(change){
			for(Transition t:this.getSuccesseurs()){
				nbManipSeq++;
				TemporalState ts=(TemporalState)t.getTarget();
				HashSet<Sequence> ret=null;
				if (EpsilonTransitionChecker.isEpsilonTransition(t)){
					ret=copy(f);
				}
				else{
					Statement statement=(Statement)t.getTrigger();
					String ch=statement.getText();
					//ret=insertAfterAll(ch, past);
					Sequence s=root.getForwardChild(ch);
					//s.addAfter(ch);
					ret=copy(f);
					ret.remove(s);
					//System.out.println(ret);
				}
				ts.updateRequiredFuture(ret,updateMode);
			}
		}
	}
	public void updateRequiredPast(HashSet<Sequence> p){
		updateRequiredPast(p,0);
	}
	public void updateRequiredPast(HashSet<Sequence> p,int updateMode){
		nbVisitsStates++;
	     if (requiredPast!=null){
			if(updateMode==0){	
				requiredPast=union(requiredPast,p);
				p=requiredPast;
			}
			else{
				p=addSecondToFirst(requiredPast,p);
			}	
			
		}
		else{
			requiredPast=copy(p);
			change=true;
		}
	    if(change){
			for(Transition t:this.getPredecesseurs()){
				nbManipSeq++;
				TemporalState ts=(TemporalState)t.getSource();
				HashSet<Sequence> ret=null;
				if (EpsilonTransitionChecker.isEpsilonTransition(t)){
					ret=copy(p);
				}
				else{
					Statement statement=(Statement)t.getTrigger();
					String ch=statement.getText();
					//ret=insertAfterAll(ch, past);
					//Sequence s=new Sequence();
					//s.addAfter(ch);
					Sequence s=root.getForwardChild(ch);
					
					ret=copy(p);
					ret.remove(s);
					//System.out.println(ret);
				}
				ts.updateRequiredPast(ret,updateMode);
			}
		}
	}
	
	//public void updatePossiblePast()
	
	public void updatePast(HashSet<Sequence> p){
		updatePast(p,0);
	}
	
	public void updatePast(HashSet<Sequence> p,int updateMode){
		nbVisitsStates++;
	    if (past!=null){
			if(updateMode==0){
				past=intersect(past,p);
				p=past;
			}
			else{
				p=removeSecondFromFirst(past, p);
			}
		}
		else{
			past=copy(p);
			change=true;
		}
	    if(change){
			for(Transition t:this.getSuccesseurs()){
				nbManipSeq++;
				TemporalState ts=(TemporalState)t.getTarget();
				HashSet<Sequence> ret=null;
				if (EpsilonTransitionChecker.isEpsilonTransition(t)){
					ret=copy(p);
				}
				else{
					Statement statement=(Statement)t.getTrigger();
					String ch=statement.getText();
					//ret=insertAfterAll(ch, past);
					//Sequence s=new Sequence();
					//s.addAfter(ch);
					Sequence s=root.getForwardChild(ch);
					
					ret=copy(p);
					if(updateMode==0){
						ret.add(s);
					}
					else{
						ret.remove(s);
					}
					//System.out.println(ret);
				}
				ts.updatePast(ret,updateMode);
			}
		}
	}
	
	
	
	
	
	
	
	/*public static HashSet<Sequence> insertAfterAll(String ch,HashSet<Sequence> h){
		return(insertAfterAll(ch,h,true));
	}
	public static HashSet<Sequence> insertAfterAll(String ch,HashSet<Sequence> h, boolean keepOlds){
		HashSet<Sequence> ret=new HashSet<Sequence>();
		for(Sequence s:h){
			if (keepOlds){
				ret.add(s.copy());
			}
			if (s.size()<=maxSize){
				Sequence ns=s.copy();
				ns.addAfter(ch);
				ret.add(ns);
			}
		}
		Sequence s=new Sequence();
		s.addAfter(ch);
		ret.add(s);
		return(ret);
	}
	*/
	public void updateFuture(HashSet<Sequence> f){
		updateFuture(f,0);
	}
	public void updateFuture(HashSet<Sequence> f,int updateMode){
		nbVisitsStates++;
	    if (future!=null){
	    	if(updateMode==0){
				future=intersect(future,f);
				f=future;
			}
			else{
				f=removeSecondFromFirst(future, f);
			}
		}
		else{
			future=copy(f);
			change=true;
		}
	    if(change){
			for(Transition t:this.getPredecesseurs()){
				nbManipSeq++;
				TemporalState ts=(TemporalState)t.getSource();
				HashSet<Sequence> ret=null;
				if (EpsilonTransitionChecker.isEpsilonTransition(t)){
					ret=copy(f);
				}
				else{
					Statement statement=(Statement)t.getTrigger();
					String ch=statement.getText();
					//Sequence s=new Sequence();
					//s.addAfter(ch);
					Sequence s=root.getForwardChild(ch);
					ret=copy(f);
					if(updateMode==0){
						ret.add(s);
					}
					else{
						ret.remove(s);
					}
					
				}
				ts.updateFuture(ret,updateMode);
			}
		}
	}
	
	/*public void propagateFutureForbiden(HashMap<String,HashMap<Sequence,Integer>> f, HashMap<String,HashMap<Sequence,Integer>> seen, StatementComparaisonStrategy strategy){
		if (forbidenFuture==null){
			forbidenFuture=new HashMap<String,HashMap<Sequence,Integer>>();
		}
		change=false;
		for(String s:f.keySet()){
			HashMap<Sequence,Integer> h=forbidenFuture.get(s);
			if (h==null){
				h=new HashMap<Sequence,Integer>();
				forbidenFuture.put(s,h);
			}
			HashMap<Sequence,Integer> fh=f.get(s);
			HashMap<Sequence,Integer> b=seen.get(s);
			for(Sequence seq:fh.keySet()){
				if (b!=null){
					Integer nbs=b.get(seq);
					//if ((nbs!=null) 
						
				}
			}
			
		}
		
	    if(change){
			//future=inter;
			for(Transition t:this.getPredecesseurs()){
				NewTemporalState ts=(NewTemporalState)t.getSource();
				HashSet<Sequence> ret=null;
				if (LTSEpsilonTransitionChecker.isEpsilonTransition(t)){
					ret=copy(future);
				}
				else{
					Statement statement=(Statement)t.getTrigger();
					String ch=strategy.getText(statement);
					Sequence s=new Sequence();
					s.addAfter(ch);
					ret=copy(future);
					ret.add(s);
				}
				ts.updateFuture(ret, strategy);
			}
		}
	}*/
	
	/*public static HashSet<Sequence> insertBeforeAll(String ch,HashSet<Sequence> h){
		return(insertBeforeAll(ch,h,true));
	}
	
	public static HashSet<Sequence> insertBeforeAll(String ch,HashSet<Sequence> h,boolean keepOlds){
		HashSet<Sequence> ret=new HashSet<Sequence>();
		for(Sequence s:h){
			if (keepOlds){
				ret.add(s.copy());
			}
			if (s.size()<=maxSize){
				Sequence ns=s.copy();
				ns.addBefore(ch);
				ret.add(ns);
			}
		}
		Sequence s=new Sequence();
		s.addAfter(ch);
		ret.add(s);
		return(ret);
	}*/
	
	
	/*private void modifyBeganSeqs(HashMap<String,HashSet<Sequence>> beganSeqs, String ch, boolean forward){
		HashSet<Sequence> set=null;
		
		if (beganSeqs.containsKey(ch)){
			HashSet<Sequence> h=beganSeqs.get(ch);
			HashSet<Sequence> asup=new HashSet<Sequence>();
			for(Sequence seq:h){
				String next=seq.next(forward);
				if (next.length()==0){
					asup.add(seq);
					//System.out.println(seq+" : " +nb+" : "+next);
				}
				else{
					asup.add(seq);
					HashSet<Sequence> h2=beganSeqs.get(next);
					if(h2==null){
						h2=new HashSet<Sequence>();
						beganSeqs.put(next, h2);
					}
					h2.add(seq);
				}
				
			}
			for(Sequence seq:asup){
				h.remove(seq);
			}
			if (h.size()==0){
				beganSeqs.remove(ch);
			}
				
		}
	}
	*/
	
	public void countPast(String ch, HashMap<Sequence,Integer> set){
		nbVisitsStates++;
		ensuredPast=new HashMap<Sequence,Integer>();
		changedPast=new HashSet<Sequence>();
		for(Sequence seq:set.keySet()){
			nbManipSeq++;
			ensuredPast.put(seq, (int)set.get(seq));
		}
		for(Sequence seq:set.keySet()){
			nbManipSeq++;
			//if(seq.toString().compareTo("R:R.b();:@:;R:R.e()")==0){
			//	System.out.println(this.name+":seq => "+seq+":"+set.get(seq));
			//}
			if ((ch.length()>0) && (seq.size()<maxSize)){
				//int nb=set.get(seq);
				//Sequence ns=seq.copy();
				//ns.addAfter(ch);
				Sequence ns=seq.getForwardChild(ch);
				
				//System.out.println(ns);
				if (ns!=null){
				//if (possiblePre.contains(ns)){
					//System.out.println(ns+"ok");
					Integer on=ensuredPast.get(ns);
					int o=0;
					if (on!=null){
						o=on;
					}
					o++;
					/*if (o>nb){
						o=nb;
					}*/
					ensuredPast.put(ns,o);
					this.changedPast.add(ns);
					/*if(ns.toString().compareTo("R:R.b();:@:;R:R.e()")==0){
						System.out.println(this.name+":ns => "+ns+":"+(o));
					}*/
				}
			}
		}
		if (ch.length()>0){
			//Sequence ns=new Sequence();
			//ns.addAfter(ch);
			Sequence ns=root.getForwardChild(ch);
			
			if (ns!=null){ //(possiblePre.contains(ns)){
				Integer on=ensuredPast.get(ns);
				int o=0;
				if (on!=null){
					o=on;
				}
				ensuredPast.put(ns,o+1);
				this.changedPast.add(ns);
				//if(ns.toString().compareTo("R:R.b();:@:;R:R.e()")==0){
				//	System.out.println(this.name+":ns2 => "+ns+":"+(o+1));
				//}
			}
		}
		if(this.getSuccesseurs().size()==1){
			Transition t=getSuccesseurs().get(0);
			TemporalState ns=(TemporalState)t.getTarget();
			Statement statement=(Statement)t.getTrigger();
			String nch=statement.getText();
			ns.countPast(nch, ensuredPast);
		}
	}
	
	public void countFuture(String ch, HashMap<Sequence,Integer> set){
		nbVisitsStates++;
		ensuredFuture=new HashMap<Sequence,Integer>();
		changedFuture=new HashSet<Sequence>();
		for(Sequence seq:set.keySet()){
			nbManipSeq++;
			ensuredFuture.put(seq, set.get(seq));
		}
		for(Sequence seq:set.keySet()){
			nbManipSeq++;
			if ((ch.length()>0) && (seq.size()<maxSize)){
				//int nb=set.get(seq);
				//Sequence ns=seq.copy();
				//ns.addBefore(ch);
				Sequence ns=seq.getBackwardChild(ch);
				
				if (ns!=null){ //possiblePre.contains(ns)){
					Integer on=ensuredFuture.get(ns);
					int o=0;
					if (on!=null){
						o=on;
					}
					o++;
					/*if(o>nb){
						o=nb;
					}*/
					ensuredFuture.put(ns,o);
					this.changedFuture.add(ns);
				}
			}
		}
		if (ch.length()>0){
			//Sequence ns=new Sequence();
			//ns.addAfter(ch);
			Sequence ns=root.getForwardChild(ch);
			
			if (ns!=null){ //possiblePre.contains(ns)){
				Integer on=ensuredFuture.get(ns);
				int o=0;
				if (on!=null){
					o=on;
				}
				ensuredFuture.put(ns,o+1);
				this.changedFuture.add(ns);
			}
		}
		if(this.getPredecesseurs().size()==1){
			Transition t=getPredecesseurs().get(0);
			TemporalState ns=(TemporalState)t.getSource();
			Statement statement=(Statement)t.getTrigger();
			String nch=statement.getText();
			ns.countFuture(nch, ensuredFuture);
		}
	}
	
	
	
	public void computeFutureRequirements(String ch, HashMap<Sequence,Integer> set,HashSet<Sequence> active, HashSet<Sequence> p, HashMap<Sequence,Integer> ens){
		nbVisitsStates++;
		//possiblePast=new HashMap<Integer,HashSet<Sequence>>();
		//forbidInFuture=new HashMap<String,HashSet<Sequence>>();
		
		HashMap<Sequence,Integer> nset=new HashMap<Sequence,Integer>();
		ensuredFuture=new HashMap<Sequence,Integer>(ens);
		changedPast=new HashSet<Sequence>();
		past=new HashSet<Sequence>(p);
		//past=copy(p);
		
		//HashSet<Sequence> nactive=copy(active);
		
		requiredFuture=new HashSet<Sequence>(active);
		//requiredFuture=copy(active);
		Sequence sch=root.getForwardChild(ch);
		if(requiredFuture.contains(sch)){
			requiredFuture.remove(sch);
		}
		/*for(Sequence seq:set.keySet()){
			nset.put(seq, set.get(seq));
		}*/
		for(Sequence seq:set.keySet()){
			nbManipSeq++;
			if ((ch.length()>0) && (seq.size()<maxSize)){
				//int nb=set.get(seq);
				//Sequence ns=seq.copy();
				//ns.addAfter(ch);
				Sequence ns=seq.getForwardChild(ch);
				
				if (ns!=null){ //possiblePre.contains(ns)){
					Integer on=set.get(ns);
					int o=0;
					if (on!=null){
						o=on;
					}
					o++;
					/*if (o>nb){
						o=nb;
					}*/
					nset.put(ns,o);
					this.changedPast.add(ns);
					/*if(ns.toString().compareTo("R:R.b();:@:;R:R.e()")==0){
						System.out.println(this.name+":ns => "+ns+":"+(o));
					}*/
				}
			}
		}
		for(Sequence seq:nset.keySet()){
			nbManipSeq++;
			set.put(seq, nset.get(seq));
		}
		nset=set;
		if (ch.length()>0){
			//Sequence ns=new Sequence();
			//ns.addAfter(ch);
			Sequence ns=root.getForwardChild(ch);
			
			if (ns!=null){ //possiblePre.contains(ns)){
				past.add(ns);
				Integer on=nset.get(ns);
				int o=0;
				if (on!=null){
					o=on;
				}
				nset.put(ns,o+1);
				this.changedPast.add(ns);
				//if(ns.toString().compareTo("R:R.b();:@:;R:R.e()")==0){
				//	System.out.println(this.name+":ns2 => "+ns+":"+(o+1));
				//}
			}
		}
		//if (rules.futureStricts.size()>0){
		for(Sequence seq:changedPast){
			nbManipSeq++;
			/*if (mode>0){
				HashSet<Sequence> ps=possiblePast.get(seq.size());
				if (ps==null){
					ps=new HashSet<Sequence>();
					possiblePast.put(seq.size(), ps);
				}
				ps.add(seq);
			}*/
			 
			HashSet<Sequence> posts=rules.futureRules.get(seq);
			if (posts!=null){
					for(Sequence post:posts){
						requiredFuture.add(post);
					}
			}
			if (rules.futureStricts.size()>0){
				int nb=0;
				if (ensuredFuture.containsKey(seq)){
					nb=ensuredFuture.get(seq);
					nb--;
					if (nb>0){
						ensuredFuture.put(seq,nb);
					}
					else{
						ensuredFuture.remove(seq);
					}
				}
				
				nb=nset.get(seq);
				posts=rules.futureStricts.get(seq);
				if (posts!=null){
					for(Sequence post:posts){
						nbManipSeq++;
						Integer inbp=nset.get(post);
						int nbp=(inbp==null)?0:inbp;
						int diff=nb-nbp;
						Integer iodiff=ensuredFuture.get(post);
						int odiff=(iodiff==null)?0:iodiff;
						if ((diff>0) && (diff>odiff)){
							ensuredFuture.put(post,diff);
						}
					}
				}
			}
			//HashSet<Sequence> posts=rules.futureRules.get(seq);
		 }
		//}
		//for(Sequence seq:changedPast){
			
		//}
		/*if (mode>0){
			modifyBeganSeqs(beganSeqs,ch,true);
			for(String st:beganSeqs.keySet()){
				HashSet<Sequence> h=forbidInFuture.get(st);
				if (.contains(post)){
				
				}
			}
		}*/
			/*HashSet<Sequence> forwards=seq.getForwardChilds();
			for(Sequence forward:forwards){
				posts=rules.pastRules.get(forward);
				boolean blem=false;
				if (posts!=null){
					for(Sequence post:posts){
						if (!past.contains(post)){
							blem=true;
							break;
						}
					}
				}
				if (blem){
					Sequence fo=forward;
					for(int j=0;j<seq.size();j++){
						fo=fo.parentBackward;
					}
				}
			}*/
		//}
		this.changedPast=null;
		if (this.getSuccesseurs().size()==1){
			Transition t=this.getSuccesseurs().get(0);
			TemporalState ts=(TemporalState)t.getTarget();
			HashSet<Sequence> ret=null;
			Statement statement=(Statement)t.getTrigger();
			String chaine=statement.getText();
			ts.computeFutureRequirements(chaine,nset,requiredFuture,past,ensuredFuture);
		}
		
	}
	 
	
	/*public void computeFutureRequirements(String ch, HashMap<Sequence,Integer> set,HashSet<Sequence> active, HashSet<Sequence> p, StatementComparaisonStrategy strategy){
		//possiblePast=new HashMap<Integer,HashSet<Sequence>>();
		//forbidInFuture=new HashMap<String,HashSet<Sequence>>();
		
		HashMap<Sequence,Integer> nset=new HashMap<Sequence,Integer>();
		ensuredFuture=new HashMap<Sequence,Integer>();
		changedPast=new HashSet<Sequence>();
		past=new HashSet<Sequence>(p);
		//past=copy(p);
		
		//HashSet<Sequence> nactive=copy(active);
		
		requiredFuture=new HashSet<Sequence>(active);
		//requiredFuture=copy(active);
		Sequence sch=root.getForwardChild(ch);
		if(requiredFuture.contains(sch)){
			requiredFuture.remove(sch);
		}
		
		for(Sequence seq:set.keySet()){
			if ((ch.length()>0) && (seq.size()<maxSize)){
				//int nb=set.get(seq);
				//Sequence ns=seq.copy();
				//ns.addAfter(ch);
				Sequence ns=seq.getForwardChild(ch);
				
				if (possiblePre.contains(ns)){
					Integer on=set.get(ns);
					int o=0;
					if (on!=null){
						o=on;
					}
					o++;
					
					nset.put(ns,o);
					this.changedPast.add(ns);
					
				}
			}
		}
		for(Sequence seq:nset.keySet()){
			set.put(seq, nset.get(seq));
		}
		nset=set;
		if (ch.length()>0){
			//Sequence ns=new Sequence();
			//ns.addAfter(ch);
			Sequence ns=root.getForwardChild(ch);
			
			if (possiblePre.contains(ns)){
				past.add(ns);
				Integer on=nset.get(ns);
				int o=0;
				if (on!=null){
					o=on;
				}
				nset.put(ns,o+1);
				this.changedPast.add(ns);
				//if(ns.toString().compareTo("R:R.b();:@:;R:R.e()")==0){
				//	System.out.println(this.name+":ns2 => "+ns+":"+(o+1));
				//}
			}
		}
		if (rules.futureStricts.size()>0){
		 for(Sequence seq:nset.keySet()){
			
			if (rules.futureStricts.size()>0){
				int nb=nset.get(seq);
				HashSet<Sequence> posts=rules.futureStricts.get(seq);
				if (posts!=null){
					for(Sequence post:posts){
						Integer inbp=nset.get(post);
						int nbp=(inbp==null)?0:inbp;
						int diff=nb-nbp;
						Integer iodiff=ensuredFuture.get(post);
						int odiff=(iodiff==null)?0:iodiff;
						if ((diff>0) && (diff>odiff)){
							ensuredFuture.put(post,diff);
						}
					}
				}
			}
			//HashSet<Sequence> posts=rules.futureRules.get(seq);
		 }
		}
		for(Sequence seq:changedPast){
			HashSet<Sequence> posts=rules.futureRules.get(seq);
			if (posts!=null){
				for(Sequence post:posts){
					requiredFuture.add(post);
				}
			}
		}
		
		if (this.getSuccesseurs().size()==1){
			Transition t=this.getSuccesseurs().get(0);
			NewTemporalState ts=(NewTemporalState)t.getTargetState();
			HashSet<Sequence> ret=null;
			Statement statement=(Statement)t.getTrigger();
			String chaine=strategy.getText(statement);
			ts.computeFutureRequirements(chaine,nset,requiredFuture,past,strategy);
		}
		
	}*/
	
	public void computePastRequirements(String ch,HashMap<Sequence,Integer> set,HashSet<Sequence> active, HashSet<Sequence> f, HashMap<Sequence,Integer> ens){
		nbVisitsStates++;
		//possibleFuture=new HashMap<Integer,HashSet<Sequence>>();
		//forbidInFuture=new HashSet<Sequence>();
		
		HashMap<Sequence,Integer> nset=new HashMap<Sequence,Integer>();
		ensuredPast=new HashMap<Sequence,Integer>(ens);
		changedFuture=new HashSet<Sequence>();
		future=new HashSet<Sequence>(f);
		//future=copy(f);
		//future=f.clone();
		//HashSet<Sequence> nactive=copy(active);
		
		requiredPast=new HashSet<Sequence>(active);
		//requiredPast=copy(active);
		//Sequence sch=new Sequence(ch,1);
		Sequence sch=root.getForwardChild(ch);
		
		if(requiredPast.contains(sch)){
			requiredPast.remove(sch);
		}
		/*for(Sequence seq:set.keySet()){
			nset.put(seq, set.get(seq));
		}*/
		//nset=set;
		for(Sequence seq:set.keySet()){
			nbManipSeq++;
			if ((ch.length()>0) && (seq.size()<maxSize)){
				//int nb=set.get(seq);
				//Sequence ns=seq.copy();
				//ns.addBefore(ch);
				Sequence ns=seq.getBackwardChild(ch);
				
				if (ns!=null){ //possiblePre.contains(ns)){
					Integer on=set.get(ns);
					int o=0;
					if (on!=null){
						o=on;
					}
					o++;
					/*if (o>nb){
						o=nb;
					}*/
					nset.put(ns,o);
					this.changedFuture.add(ns);
					/*if(ns.toString().compareTo("R:R.b();:@:;R:R.e()")==0){
						System.out.println(this.name+":ns => "+ns+":"+(o));
					}*/
				}
			}
		}
		for(Sequence seq:nset.keySet()){
			nbManipSeq++;
			set.put(seq, nset.get(seq));
		}
		nset=set;
		if (ch.length()>0){
			//Sequence ns=new Sequence();
			//ns.addAfter(ch);
			Sequence ns=root.getForwardChild(ch);
			
			if (ns!=null){ //possiblePre.contains(ns)){
				future.add(ns);
				Integer on=nset.get(ns);
				int o=0;
				if (on!=null){
					o=on;
				}
				nset.put(ns,o+1);
				this.changedFuture.add(ns);
				//if(ns.toString().compareTo("R:R.b();:@:;R:R.e()")==0){
				//	System.out.println(this.name+":ns2 => "+ns+":"+(o+1));
				//}
			}
		}
		
		for(Sequence seq:changedFuture){
			nbManipSeq++;
			
			if (rules.pastStricts.size()>0){
				int nb=0;
				if (ensuredPast.containsKey(seq)){
					nb=ensuredPast.get(seq);
					nb--;
					if (nb>0){
						ensuredPast.put(seq,nb);
					}
					else{
						ensuredPast.remove(seq);
					}
				}
				
				nb=nset.get(seq);
				//int nb=nset.get(seq);
				HashSet<Sequence> posts=rules.pastStricts.get(seq);
				if (posts!=null){
					for(Sequence post:posts){
						nbManipSeq++;
						Integer inbp=nset.get(post);
						int nbp=(inbp==null)?0:inbp;
						int diff=nb-nbp;
						Integer iodiff=ensuredPast.get(post);
						int odiff=(iodiff==null)?0:iodiff;
						if ((diff>0) && (diff>odiff)){
							ensuredPast.put(post,diff);
						}
					}
				}
			}
			
			
			HashSet<Sequence> posts=rules.pastRules.get(seq);
			if (posts!=null){
				for(Sequence post:posts){
					nbManipSeq++;
					requiredPast.add(post);
					//System.out.println(ch+" : add req past : "+seq+"=>"+post);
				}
			}
			/*HashSet<Sequence> forwards=seq.getForwardChilds();
			for(Sequence forward:forwards){
				posts=rules.pastRules.get(forward);
				boolean blem=false;
				if (posts!=null){
					for(Sequence post:posts){
						if (!past.contains(post)){
							blem=true;
							break;
						}
					}
				}
				if (blem){
					Sequence fo=forward;
					for(int j=0;j<seq.size();j++){
						fo=fo.parentBackward;
					}
				}
			}*/
		}
		this.changedFuture=null;
		if (this.getPredecesseurs().size()==1){
			Transition t=this.getPredecesseurs().get(0);
			TemporalState ts=(TemporalState)t.getSource();
			HashSet<Sequence> ret=null;
			Statement statement=(Statement)t.getTrigger();
			String chaine=statement.getText();
			ts.computePastRequirements(chaine,nset,requiredPast,future,ensuredPast);
		}
		
	}
	
	/*public void computePastRequirements(String ch,HashMap<Sequence,Integer> set,HashSet<Sequence> active, HashSet<Sequence> f, StatementComparaisonStrategy strategy){
		//possibleFuture=new HashMap<Integer,HashSet<Sequence>>();
		//forbidInFuture=new HashSet<Sequence>();
		
		HashMap<Sequence,Integer> nset=new HashMap<Sequence,Integer>();
		ensuredPast=new HashMap<Sequence,Integer>();
		changedFuture=new HashSet<Sequence>();
		future=new HashSet<Sequence>(f);
		//future=copy(f);
		//future=f.clone();
		//HashSet<Sequence> nactive=copy(active);
		
		requiredPast=new HashSet<Sequence>(active);
		//requiredPast=copy(active);
		//Sequence sch=new Sequence(ch,1);
		Sequence sch=root.getForwardChild(ch);
		
		if(requiredPast.contains(sch)){
			requiredPast.remove(sch);
		}
		
		//nset=set;
		for(Sequence seq:set.keySet()){
			if ((ch.length()>0) && (seq.size()<maxSize)){
				//int nb=set.get(seq);
				//Sequence ns=seq.copy();
				//ns.addBefore(ch);
				Sequence ns=seq.getBackwardChild(ch);
				
				if (possiblePre.contains(ns)){
					Integer on=set.get(ns);
					int o=0;
					if (on!=null){
						o=on;
					}
					o++;
					nset.put(ns,o);
					this.changedFuture.add(ns);
				
				}
			}
		}
		for(Sequence seq:nset.keySet()){
			set.put(seq, nset.get(seq));
		}
		nset=set;
		if (ch.length()>0){
			//Sequence ns=new Sequence();
			//ns.addAfter(ch);
			Sequence ns=root.getForwardChild(ch);
			
			if (possiblePre.contains(ns)){
				future.add(ns);
				Integer on=nset.get(ns);
				int o=0;
				if (on!=null){
					o=on;
				}
				nset.put(ns,o+1);
				this.changedFuture.add(ns);
				//if(ns.toString().compareTo("R:R.b();:@:;R:R.e()")==0){
				//	System.out.println(this.name+":ns2 => "+ns+":"+(o+1));
				//}
			}
		}
		if(rules.pastStricts.size()>0){
		  for(Sequence seq:nset.keySet()){
			
			if (rules.pastStricts.size()>0){
				int nb=nset.get(seq);
				HashSet<Sequence> posts=rules.pastStricts.get(seq);
				if (posts!=null){
					for(Sequence post:posts){
						Integer inbp=nset.get(post);
						int nbp=(inbp==null)?0:inbp;
						int diff=nb-nbp;
						Integer iodiff=ensuredPast.get(post);
						int odiff=(iodiff==null)?0:iodiff;
						if ((diff>0) && (diff>odiff)){
							ensuredPast.put(post,diff);
						}
					}
				}
			}
			//HashSet<Sequence> posts=rules.futureRules.get(seq);
		  }
		}
		for(Sequence seq:changedFuture){
			HashSet<Sequence> posts=rules.pastRules.get(seq);
			if (posts!=null){
				for(Sequence post:posts){
					requiredPast.add(post);
					//System.out.println(ch+" : add req past : "+seq+"=>"+post);
				}
			}
			
		}
		if (this.getPredecesseurs().size()==1){
			Transition t=this.getPredecesseurs().get(0);
			NewTemporalState ts=(NewTemporalState)t.getSource();
			HashSet<Sequence> ret=null;
			Statement statement=(Statement)t.getTrigger();
			String chaine=strategy.getText(statement);
			ts.computePastRequirements(chaine,nset,requiredPast,future,strategy);
		}
		
	}*/
	
	
	/*public void computeFutureRequirements2(HashMap<String,HashMap<Sequence,Integer>> require, HashSet<Sequence> seen, HashSet<Sequence> active,HashSet<Sequence> p, StatementComparaisonStrategy strategy){
		beganPastSequences=new HashMap<String,HashMap<Sequence,Integer>>();
		forbidenFuture=new HashMap<String,HashMap<Sequence,Integer>>();
		
		past=copy(p);
		HashSet<Sequence> nactive=copy(active);
		requiredFuture=active;
		if(rules.futureStricts.size()>0){
			ensuredFuture=new HashMap<Sequence,Integer>();
		}
		if((rules.futureStricts.size()>0) || (mode>0)){
			
		   for(String s:require.keySet()){
				HashMap<Sequence,Integer> h=require.get(s);
				for(Sequence seq:h.keySet()){
					
				   if(rules.futureStricts.size()>0){
					 int nb=h.get(seq);
				     if (nb>0){	 
						HashSet<Sequence> posts=rules.futureStricts.get(seq);
						if (posts!=null){	
							for(Sequence post:posts){
								ensuredFuture.put(post,nb);
							}
						}
					 }
					}
					
					if ((mode>0) && (seq.getNext()>1) && (seq.getNext()<=seq.size())){
							HashSet<Sequence> posts=rules.futureRules.get(seq);
							if (posts!=null){
								//boolean nonactive=false;
								HashMap<Sequence,Integer> b=beganPastSequences.get(s);
								if (b==null){
									b=new HashMap<Sequence,Integer>();
									beganPastSequences.put(s, b);
								}
								Sequence seqc=seq.copy(true);
								b.put(seqc,seq.getNext());
							}
							posts=rules.pastRules.get(seq);
							if (posts!=null){
								boolean pb=false;
								for(Sequence post:posts){
									if (!seen.contains(post)){
										pb=true;
										break;
									}
								}
								if (pb){
									Sequence seqc=seq.copy(true);
									int x=seq.getNext()-3;
									seqc.setNext(x);
									String last=seqc.get(x+1);
									HashMap<Sequence,Integer> b=forbidenFuture.get(last);
									if (b==null){
										b=new HashMap<Sequence,Integer>();
										forbidenFuture.put(last, b);
									}
									b.put(seqc,x);
								}
							}
					}
					
				}
		   }
		}
		if (this.getSuccesseurs().size()==1){
			Transition t=this.getSuccesseurs().get(0);
			NewTemporalState ts=(NewTemporalState)t.getTargetState();
			HashSet<Sequence> ret=null;
			Statement statement=(Statement)t.getTrigger();
			String ch=strategy.getText(statement);
			Sequence ns=new Sequence();
			ns.addAfter(ch);
			if (nactive.contains(ns)){
				nactive.remove(ns);
			}
			modifyRequire(require,ch,true);
			for(Sequence seq:this.changedPast){
				seen.add(seq);
				HashSet<Sequence> posts=rules.futureRules.get(seq);
				if (posts!=null){
					for(Sequence post:posts){
						nactive.add(post);
					}
				}
			}
			p.add(ns);
			ts.computeFutureRequirements2(require,seen,nactive,p,strategy);
		}
	}
	public void computePastRequirements2(HashMap<String,HashMap<Sequence,Integer>> require, HashSet<Sequence> seen, HashSet<Sequence> active,HashSet<Sequence> f, StatementComparaisonStrategy strategy){
		beganFutureSequences=new HashMap<String,HashMap<Sequence,Integer>>();
		forbidenPast=new HashMap<String,HashMap<Sequence,Integer>>();
		future=copy(f);
		HashSet<Sequence> nactive=copy(active);
		requiredPast=active;
		if(rules.pastStricts.size()>0){
			ensuredPast=new HashMap<Sequence,Integer>();
		}
		if((rules.pastStricts.size()>0) || (mode>0)){
			
		   for(String s:require.keySet()){
				HashMap<Sequence,Integer> h=require.get(s);
				for(Sequence seq:h.keySet()){
					
				   if(rules.pastStricts.size()>0){
					 int nb=h.get(seq);
				     if (nb>0){	 
						HashSet<Sequence> posts=rules.pastStricts.get(seq);
						if (posts!=null){	
							for(Sequence post:posts){
								ensuredPast.put(post,nb);
							}
						}
					 }
					}
					
					if ((mode>0) && (seq.getNext()>=-1) && (seq.getNext()<(seq.size()-2))){
							HashSet<Sequence> posts=rules.pastRules.get(seq);
							if (posts!=null){
								//boolean nonactive=false;
								HashMap<Sequence,Integer> b=beganFutureSequences.get(s);
								if (b==null){
									b=new HashMap<Sequence,Integer>();
									beganFutureSequences.put(s, b);
								}
								Sequence seqc=seq.copy(true);
								b.put(seqc,seq.getNext());
							}
							posts=rules.futureRules.get(seq);
							if (posts!=null){
								boolean pb=false;
								for(Sequence post:posts){
									if (!seen.contains(post)){
										pb=true;
										break;
									}
								}
								
								if (pb){
									Sequence seqc=seq.copy(true);
									int x=seq.getNext()+3;
									seqc.setNext(x);
									String last=seqc.get(x-1);
									HashMap<Sequence,Integer> b=forbidenPast.get(last);
									if (b==null){
										b=new HashMap<Sequence,Integer>();
										forbidenPast.put(last, b);
									}
									b.put(seqc,x);
								}
							}
					}
					
				}
		   }
		}
		if (this.getPredecesseurs().size()==1){
			Transition t=this.getPredecesseurs().get(0);
			NewTemporalState ts=(NewTemporalState)t.getSource();
			HashSet<Sequence> ret=null;
			Statement statement=(Statement)t.getTrigger();
			String ch=strategy.getText(statement);
			Sequence ns=new Sequence();
			ns.addBefore(ch);
			if (nactive.contains(ns)){
				nactive.remove(ns);
			}
			modifyRequire(require,ch,false);
			for(Sequence seq:this.changedFuture){
				seen.add(seq);
				HashSet<Sequence> posts=rules.pastRules.get(seq);
				if (posts!=null){
					for(Sequence post:posts){
						nactive.add(post);
					}
				}
			}
			f.add(ns);
			ts.computePastRequirements2(require,seen,nactive,f,strategy);
		}
	}*/
	
	/*private void modifyRequire(HashMap<String,HashMap<Sequence,Integer>> require, String ch, boolean forward){
		HashSet<Sequence> set=null;
		if (forward){
			changedPast=new HashSet<Sequence>();
			set=changedPast;
		}
		else{
			changedFuture=new HashSet<Sequence>();
			set=changedFuture;
		}
		if (require.containsKey(ch)){
			HashMap<Sequence,Integer> h=require.get(ch);
			HashSet<Sequence> asup=new HashSet<Sequence>();
			for(Sequence seq:h.keySet()){
				String next=seq.next(forward);
				int nb=h.get(seq);
				if (next.length()==0){
					nb++;
					set.add(seq);
					next=seq.next(forward);
					//System.out.println(seq+" : " +nb+" : "+next);
				}
				if (next.compareTo(ch)!=0){
					asup.add(seq);
				}
				
				HashMap<Sequence,Integer> h2=require.get(next);
				if(h2==null){
					h2=new HashMap<Sequence,Integer>();
					require.put(next, h2);
				}
				h2.put(seq, nb);
				
			}
			for(Sequence seq:asup){
				h.remove(seq);
			}
			if (h.size()==0){
				require.remove(ch);
			}
				
		}
	}
	
	public static HashMap<String,HashMap<Sequence,Integer>> computeRequireSet(StatementComparaisonStrategy strategy,HashSet<Sequence> set,boolean forward){
		HashMap<String,HashMap<Sequence,Integer>> require=new HashMap<String,HashMap<Sequence,Integer>>();
		for(Sequence s:set){
			Sequence ns=s.copy();
			String next=ns.next(forward);
			
			HashMap<Sequence,Integer> h=require.get(next);
			if(h==null){
				h=new HashMap<Sequence,Integer>();
				require.put(next, h);
			}
			h.put(ns, 0);
		}
		return(require);
	}
	
	
	
	
	public void countSeqsPast(StatementComparaisonStrategy strategy){
		countSeqsPast(strategy,future);
	}
	public void countSeqsPast(StatementComparaisonStrategy strategy,HashSet<Sequence> set){
		System.out.println("Calcul set "+set.size());
		HashMap<String,HashMap<Sequence,Integer>> require=new HashMap<String,HashMap<Sequence,Integer>>();
		for(Sequence s:set){
			Sequence ns=s.copy();
			String next=ns.next();
			
			HashMap<Sequence,Integer> h=require.get(next);
			if(h==null){
				h=new HashMap<Sequence,Integer>();
				require.put(next, h);
			}
			h.put(ns, 0);
		}
		System.out.println("Calcul set "+set.size()+" ok");
		countSeqsPast(require,"",strategy);
		System.out.println("Count ok");
	}	
	private void countSeqsPast(HashMap<String,HashMap<Sequence,Integer>> require, String ch, StatementComparaisonStrategy strategy){
			modifyRequire(require,ch,true);
			//System.out.println(require);
			ensuredPast=new HashMap<Sequence,Integer>();
			for(String s:require.keySet()){
				HashMap<Sequence,Integer> h=require.get(s);
				for(Sequence seq:h.keySet()){
					int nb=h.get(seq);
					if (nb>0){
						ensuredPast.put(seq.copy(),nb);
					}
				}
			}
			for(Transition t:this.getSuccesseurs()){
				NewTemporalState ts=(NewTemporalState)t.getTargetState();
				String chaine="";
				if (!LTSEpsilonTransitionChecker.isEpsilonTransition(t)){
					Statement statement=(Statement)t.getTrigger();
					chaine=strategy.getText(statement);
				}
				ts.countSeqsPast(require,chaine,strategy);
			}
		
	}
	
	public void countSeqsFuture(StatementComparaisonStrategy strategy){
		countSeqsFuture(strategy,past);
	}
	public void countSeqsFuture(StatementComparaisonStrategy strategy,HashSet<Sequence> set){
		
		HashMap<String,HashMap<Sequence,Integer>> require=new HashMap<String,HashMap<Sequence,Integer>>();
		for(Sequence s:set){
			Sequence ns=s.copy();
			String next=ns.next(false);
			
			HashMap<Sequence,Integer> h=require.get(next);
			if(h==null){
				h=new HashMap<Sequence,Integer>();
				require.put(next, h);
			}
			h.put(ns, 0);
		}
		
		countSeqsFuture(require,"",strategy);
		
	}	
	private void countSeqsFuture(HashMap<String,HashMap<Sequence,Integer>> require, String ch, StatementComparaisonStrategy strategy){
			modifyRequire(require,ch,false);
			//System.out.println("require modified");
			//System.out.println(require);
			ensuredFuture=new HashMap<Sequence,Integer>();
			for(String s:require.keySet()){
				HashMap<Sequence,Integer> h=require.get(s);
				for(Sequence seq:h.keySet()){
					int nb=h.get(seq);
					if (nb>0){
						ensuredFuture.put(seq.copy(),nb);
					}
				}
			}
			for(Transition t:this.getPredecesseurs()){
				NewTemporalState ts=(NewTemporalState)t.getSource();
				String chaine="";
				if (!LTSEpsilonTransitionChecker.isEpsilonTransition(t)){
					Statement statement=(Statement)t.getTrigger();
					chaine=strategy.getText(statement);
				}
				ts.countSeqsFuture(require,chaine,strategy);
			}
		
	}*/
	
	public static boolean equalEnsuredSets(HashMap<Sequence,Integer> a,HashMap<Sequence,Integer> b){
		if (a.size()!=b.size()){
			return false;
		}
		for(Sequence s:a.keySet()){
			nbManipSeq++;
			Integer nbb=b.get(s);
			if (nbb!=null){
				int nb=a.get(s);
				if (nb!=nbb){
					return false;
				}
			}
			else{return false;}
		}
		return true;
	}
	
	public boolean checkConstraintsCompatibility(TemporalState ts){
		//System.out.println("check Compatibility "+this+" with "+ts);
		//System.out.println(this.stringify());
		//System.out.println(ts.stringify());
		//System.out.println("**************************");
		
		/*if((name.compareTo("S1742")==0) && (ts.name.compareTo("S1990")==0)){
			System.out.println(this.getString());
			System.out.println(ts.getString());
		}*/
		if(ensuredPast!=null){
			if (!equalEnsuredSets(ensuredPast,ts.ensuredPast)){return(false);}
			if (!equalEnsuredSets(ensuredFuture,ts.ensuredFuture)){return(false);}
		}
		
		
		HashSet<Sequence> rp=ts.requiredPast;
		for(Sequence s:rp){
			nbManipSeq++;
			if(!past.contains(s)){return false;}
		}
		for(Sequence s:requiredPast){
			nbManipSeq++;
			if(!ts.past.contains(s)){return false;}
		}
		
		HashSet<Sequence> rf=ts.requiredFuture;
		for(Sequence s:rf){
			nbManipSeq++;
			if(!future.contains(s)){return false;}
		}
		for(Sequence s:requiredFuture){
			nbManipSeq++;
			if(!ts.future.contains(s)){return false;}
		}
		
		/*if (mode>0){
			HashMap<Integer,HashSet<Sequence>> p1=ts.possiblePast;
			HashMap<Integer,HashSet<Sequence>> p2=possibleFuture;
			HashSet<Sequence> x=future;
			for(Integer i:p1.keySet()){
				if (i<maxSize){
					HashSet<Sequence> ps1=p1.get(i);
					int diff=maxSize-i;
					for(int j=1;j<=diff;j++){
						HashSet<Sequence> ps2=p2.get(i);
						for(Sequence s1:ps1){
							for(Sequence s2:ps2){
								String ch=s1.chaine+s2.chaine;
								Sequence nse=new Sequence(ch,i+j);
								if ((!ps1.contains(nse)) && (!ps2.contains(nse))){
								HashSet<Sequence> posts=rules.futureRules.get(nse);
								if (posts!=null){
									for(Sequence post:posts){
										if (!x.contains(post)){
											return false;
										}
									}
								}}
								
							}
						}
					}
				}
			}
			p1=possiblePast;
			p2=ts.possibleFuture;
			x=ts.future;
			for(Integer i:p1.keySet()){
				if (i<maxSize){
					HashSet<Sequence> ps1=p1.get(i);
					int diff=maxSize-i;
					for(int j=1;j<=diff;j++){
						HashSet<Sequence> ps2=p2.get(i);
						for(Sequence s1:ps1){
							for(Sequence s2:ps2){
								String ch=s1.chaine+s2.chaine;
								Sequence nse=new Sequence(ch,i+j);
								if ((!ps1.contains(nse)) && (!ps2.contains(nse))){
								HashSet<Sequence> posts=rules.futureRules.get(nse);
								if (posts!=null){
									for(Sequence post:posts){
										if (!x.contains(post)){
											return false;
										}
									}
								}}
								
							}
						}
					}
				}
			}
			p1=ts.possibleFuture;
			p2=possiblePast;
			x=past;
			for(Integer i:p1.keySet()){
				if (i<maxSize){
					HashSet<Sequence> ps1=p1.get(i);
					int diff=maxSize-i;
					for(int j=1;j<=diff;j++){
						HashSet<Sequence> ps2=p2.get(i);
						for(Sequence s1:ps1){
							for(Sequence s2:ps2){
								String ch=s1.chaine+s2.chaine;
								Sequence nse=new Sequence(ch,i+j);
								if ((!ps1.contains(nse)) && (!ps2.contains(nse))){
								HashSet<Sequence> posts=rules.pastRules.get(nse);
								if (posts!=null){
									for(Sequence post:posts){
										if (!x.contains(post)){
											return false;
										}
									}
								}}
								
							}
						}
					}
				}
			}
			p1=possibleFuture;
			p2=ts.possiblePast;
			x=ts.past;
			for(Integer i:p1.keySet()){
				if (i<maxSize){
					HashSet<Sequence> ps1=p1.get(i);
					int diff=maxSize-i;
					for(int j=1;j<=diff;j++){
						HashSet<Sequence> ps2=p2.get(i);
						for(Sequence s1:ps1){
							for(Sequence s2:ps2){
								String ch=s1.chaine+s2.chaine;
								Sequence nse=new Sequence(ch,i+j);
								if ((!ps1.contains(nse)) && (!ps2.contains(nse))){
						 		 HashSet<Sequence> posts=rules.pastRules.get(nse);
						     	 if (posts!=null){
						 			for(Sequence post:posts){
										if (!x.contains(post)){
											return false;
										}
									}
						     	 }
								}
								
							}
						}
					}
				}
			}*/
			/*HashMap<String,HashMap<Sequence,Integer>> forbid=ts.forbidenFuture;
			for(String s:forbid.keySet()){
				HashMap<Sequence,Integer> seqs=beganFutureSequences.get(s);
				if (seqs!=null){
					HashMap<Sequence,Integer> fs=forbid.get(s);
					for(Sequence seq:fs.keySet()){
						Integer ns=seqs.get(seq);
						if ((ns!=null) && (ns==fs.get(seq))){
							return false;
						}
					}
				}
			}
			forbid=forbidenFuture;
			for(String s:forbid.keySet()){
				HashMap<Sequence,Integer> seqs=ts.beganFutureSequences.get(s);
				if (seqs!=null){
					HashMap<Sequence,Integer> fs=forbid.get(s);
					for(Sequence seq:fs.keySet()){
						Integer ns=seqs.get(seq);
						if ((ns!=null) && (ns==fs.get(seq))){
							return false;
						}
					}
				}
			}
			forbid=ts.forbidenPast;
			for(String s:forbid.keySet()){
				HashMap<Sequence,Integer> seqs=beganPastSequences.get(s);
				if (seqs!=null){
					HashMap<Sequence,Integer> fs=forbid.get(s);
					for(Sequence seq:fs.keySet()){
						Integer ns=seqs.get(seq);
						if ((ns!=null) && (ns==fs.get(seq))){
							return false;
						}
					}
				}
			}
			forbid=forbidenPast;
			for(String s:forbid.keySet()){
				HashMap<Sequence,Integer> seqs=ts.beganPastSequences.get(s);
				if (seqs!=null){
					HashMap<Sequence,Integer> fs=forbid.get(s);
					for(Sequence seq:fs.keySet()){
						Integer ns=seqs.get(seq);
						if ((ns!=null) && (ns==fs.get(seq))){
							return false;
						}
					}
				}
			}*/
		//}
		
		
		//System.out.println("Fusion ok");
		return(true);
	}
	
	
	public boolean exactLoChecking(TemporalState ts, TemporalState last_state, FSA lts, Statement statement, HashSet<TemporalState> initial_states, HashSet<TemporalState> final_states){
			TemporalState state=null;
			state=ts;
			boolean ok=true;
			//lts.addState(state);
			
			Transition tr=null;
			if (last_state!=null){
				tr=new Transition(last_state,statement,state);
				lts.addTransition(tr,false);
			}
			HashMap<Sequence,HashSet<Sequence>> future=TemporalState.rules.futureRules;
			for(Sequence pre:future.keySet()){
				nbManipSeq++;
				HashSet<Sequence> h=future.get(pre);
				for(Sequence post:h){
					nbManipSeq++;
					boolean r=this.checkRequired(pre,post,false);
					
					if (r){
						/*if((name.compareTo("S1742")==0) && (ts.name.compareTo("S1990")==0)){
							System.out.println(pre+" "+post);
							
						}*/
						if (!state.checkSureStatement(post,lts,true,initial_states,final_states)){
						//if (!state.requiredFuture(post,1)){
							ok=false;
							//System.out.println("1 "+pre+","+post);
							break;
						}
					}
					r=state.checkRequired(pre,post,false);
					if (r){
						if (!this.checkSureStatement(post,lts,true,initial_states,final_states)){
						//if (!temp.requiredFuture(post,1)){
							ok=false;
							//System.out.println("2 "+pre+","+post);
							break;
						}
					}
				}
				if (!ok){
					break;
				}
			}
			if (ok){
			 HashMap<Sequence,HashSet<Sequence>> past=TemporalState.rules.pastRules;
			 for(Sequence pre:past.keySet()){
				 nbManipSeq++;
				HashSet<Sequence> h=past.get(pre);
				for(Sequence post:h){
					nbManipSeq++;
					boolean r=this.checkRequired(pre,post,true);
					if (r){
						if (!state.checkSureStatement(post,lts,false,initial_states,final_states)){
						//if (!state.requiredPast(post,1)){
							ok=false;
							//System.out.println("3 "+pre+","+post);
							break;
						}
					}
					r=ts.checkRequired(pre,post,true);
					if (r){
						if (!this.checkSureStatement(post,lts,false,initial_states,final_states)){
						//if (!temp.requiredPast(post,1)){
							ok=false;
							//System.out.println("4 "+pre+","+post);
							break;
						}
					}
				}
				if (!ok){
					break;
				}
			  }
			}
		
			if (tr!=null){
			  lts.removeTransition(tr);
			  tr=null;
		    }
		    //lts.removeState(state);
		    state=null;
		    
		    return ok;
	}
	
	public boolean approxLoChecking(TemporalState ts, TemporalState last_state, FSA lts, Statement statement){
		TemporalState state=null;
		state=ts;
		boolean ok=true;
		//lts.addState(state);
		
		Transition tr=null;
		if (last_state!=null){
			tr=new Transition(last_state,statement,state);
			lts.addTransition(tr,false);
		}
		/*System.out.println(ts);
		System.out.println("avec");
		System.out.println(temp);*/
		HashSet<Sequence> statef=ts.getPossibleStatements(true);
		//System.out.println(statef);
		HashSet<Sequence> tempf=this.getPossibleStatements(true);
		//System.out.println(tempf);
		HashSet<Sequence> statep=state.getPossibleStatements(false);
		//System.out.println(statep);
		HashSet<Sequence> tempp=this.getPossibleStatements(false);
		//System.out.println(tempp);
		HashMap<Sequence,HashSet<Sequence>> future=TemporalState.rules.futureRules;
		for(Sequence pre:future.keySet()){
			nbManipSeq++;
			HashSet<Sequence> h=future.get(pre);
			if (statep.contains(pre)){
				for(Sequence post:h){
					if((!statep.contains(post)) && (!tempf.contains(post))){
						ok=false;
						//System.out.println("1 "+pre+","+post);
						break;
					}
				}
			}
			if ((ok) && (tempp.contains(pre))){
				for(Sequence post:h){
					nbManipSeq++;
					if((!tempp.contains(post)) && (!statef.contains(post))){
						ok=false;
						//System.out.println("2 "+pre+","+post);
						break;
					}
				}
			}
			if (!ok){
				break;
			}
		  }
		  if(ok){
			HashMap<Sequence,HashSet<Sequence>> past=TemporalState.rules.pastRules;
			for(Sequence pre:past.keySet()){
				nbManipSeq++;
				HashSet<Sequence> h=past.get(pre);
				if (statef.contains(pre)){
					for(Sequence post:h){
						nbManipSeq++;
						if((!statef.contains(post)) && (!tempp.contains(post))){
							ok=false;
							//System.out.println("3 "+pre+","+post);
							break;
						}
					}
				}
				if ((ok) && (tempf.contains(pre))){
					for(Sequence post:h){
						if((!tempf.contains(post)) && (!statep.contains(post))){
							ok=false;
							//System.out.println("4 "+pre+","+post);
							break;
						}
					}
				}
				if (!ok){
					break;
				}
			}
		}
		  
		if (tr!=null){
		  lts.removeTransition(tr);
		  tr=null;
	    }
	    //lts.removeState(state);
	    state=null;
	    
	    return ok;
	}
	
	
	
	
	public String getString(){
		String s=super.toString()+" => \n \t past = "+past+"\n \t future = "+future+"\n \t requiredPast = "+requiredPast+"\n \t requiredFuture = "+requiredFuture+"\n \t ensuredPast ="+ensuredPast+"\n \t ensuredFuture ="+ensuredFuture; //+"\n \t possiblePast = "+possiblePast+"\n \t possibleFuture = "+possibleFuture;// beganPast ="+beganPastSequences+"\n \t beganFuture ="+beganFutureSequences+"\n \t forbidenPast ="+forbidenPast+"\n \t forbidenFuture ="+forbidenFuture;
		return(s);
	}
	
	public void display(boolean rec){
		System.out.println(this.getString());
		if (rec){
			for(Transition t:this.getSuccesseurs()){
				TemporalState ts=(TemporalState)t.getTarget();
				ts.display(rec);
			}
		}
	}
}
