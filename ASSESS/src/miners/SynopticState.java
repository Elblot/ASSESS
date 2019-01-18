package miners;

import fsa.EpsilonTransitionChecker;
import fsa.State;
import fsa.Transition;
import traces.Statement;
import traces.Trace;
import miners.temporalKTail.TemporalState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

import core.Sequence;
public class SynopticState extends State {
	private static final long serialVersionUID = 1L;
	
	ArrayList<SimpleEntry<Integer,SynopticTrace>> traces;
	HashSet<Sequence> requiredFuture=new HashSet<Sequence>();
	HashSet<Sequence> requiredPast=new HashSet<Sequence>();
	//HashSet<Sequence> notRequiredFuture=new HashSet<Sequence>();
	//HashSet<Sequence> notRequiredPast=new HashSet<Sequence>();
	/*HashMap<Sequence,HashSet<Sequence>> safeFuture;
	HashMap<Sequence,HashSet<Sequence>> safePast;*/
	public SynopticState(String name){
		super(name);
		traces=new ArrayList<SimpleEntry<Integer,SynopticTrace>>();
		/*safeFuture=new HashMap<Sequence,HashSet<Sequence>>();
		safePast=new HashMap<Sequence,HashSet<Sequence>>();*/
		
	}
	public SynopticState(){
		super();
		traces=new ArrayList<SimpleEntry<Integer,SynopticTrace>>();
		/*safeFuture=new HashMap<Sequence,HashSet<Sequence>>();
		safePast=new HashMap<Sequence,HashSet<Sequence>>();*/
		
	}
	
	public void setTrace(SimpleEntry<Integer,SynopticTrace> s){
		traces.add(new SimpleEntry(s.getKey(),s.getValue()));
		s.getValue().setState(this,s.getKey());
	}
	public void setTrace(SynopticTrace t,int i){
		traces.add(new SimpleEntry(i,t));
		t.setState(this,i);
	}
	
	public SimpleEntry<Integer,SynopticTrace> getTrace(int i){
		SimpleEntry<Integer,SynopticTrace> ret=traces.get(i);
		return ret;
	}
	
	public HashMap<Boolean,HashSet<SimpleEntry<Integer,SynopticTrace>>> checkRule(Sequence pre, Sequence post, boolean forward){
		HashMap<Boolean,HashSet<SimpleEntry<Integer,SynopticTrace>>> ret=new HashMap<Boolean,HashSet<SimpleEntry<Integer,SynopticTrace>>>();
		HashSet<SimpleEntry<Integer,SynopticTrace>> traces1=new HashSet<SimpleEntry<Integer,SynopticTrace>>();
		HashSet<SimpleEntry<Integer,SynopticTrace>> traces2=new HashSet<SimpleEntry<Integer,SynopticTrace>>();
		ret.put(true, traces1);
		ret.put(false, traces2);
		boolean requires=false;
		for(SimpleEntry<Integer,SynopticTrace> t:traces){
			SynopticTrace trace=t.getValue();
			//if(!requires){
				if(trace.requires(t.getKey(),forward,pre,post)){
					//System.out.println(post+" required by :"+trace+" "+t.getKey());
					requires=true;
					
					break;
				}
			//}
		}
		
		if(!requires){
			traces2.addAll(traces);
		}
		else{
			for(SimpleEntry<Integer,SynopticTrace> t:traces){
				SynopticTrace trace=t.getValue();
				if(trace.has(t.getKey(),forward,post)){
					traces1.add(t);
					//System.out.println(pre+" "+post+" "+this+ "=>"+t+"t1");
				}
				else{
					traces2.add(t);
					//System.out.println(pre+" "+post+" "+this+ "=>"+t+"t2");
				}
			}
		}
		
		
		return ret;
	}
	
	public HashSet<String> computeHorizonsList(HashSet<State> finals, HashSet<String> hs, int k){
		
		if(k==0){
			return hs;
		}
		HashSet<String> horizons=new HashSet<String>();
		if(finals.contains(this)){
			for(String s:hs){
				horizons.add(s);
			}
		}
		
		ArrayList<Transition> succs=this.getSuccesseurs();
		//System.out.println(succs.size());
		for(Transition t:succs){
			nbManipSeq++;
			SynopticState ts=(SynopticState)t.getTarget();
			HashSet<String> ret=null;
			if (EpsilonTransitionChecker.isEpsilonTransition(t)){
				if(ts!=this){
					ret=ts.computeHorizonsList(finals, hs, k);
				}
				else{ret=new HashSet<String>();}
			}
			else{
				ret=new HashSet<String>();
				Statement statement=(Statement)t.getTrigger();
				String ch=statement.getText();
				for(String s:hs){
					s=s+ch+";:;";
					ret.add(s);
				}
				ret=ts.computeHorizonsList(finals, ret, k-1);
			}
			horizons.addAll(ret);
		}
		//System.out.println("at "+k+" "+horizons);
		return horizons;
	}
	
	public String getHorizon(HashSet<State> finals, int k){
		String ret="";
		HashSet<String> horizons=new HashSet<String>();
		horizons.add("");
		horizons=computeHorizonsList(finals, horizons, k);
		ArrayList<String> horizonsList=new ArrayList<String>(horizons); 
		Collections.sort(horizonsList);
		for(String s:horizonsList){
			ret+=s+"+++";
		}
		//System.out.println(this+" => "+ret);
		return ret;
	}
	
	public boolean checkCompatibility(SynopticState state2){ //,HashMap<Sequence, HashSet<Sequence>> futureRules,HashMap<Sequence, HashSet<Sequence>> pastRules){
		for(Sequence seq:requiredFuture){
			if(state2.traces.size()==0){
				return false;
			}
			if(state2.requiredFuture.contains(seq)) continue;
			for(SimpleEntry<Integer,SynopticTrace> t:state2.traces){
				SynopticTrace trace=t.getValue();
				if(!trace.has(t.getKey(),true,seq)){
					return false;
				}
			}
		}
		for(Sequence seq:requiredPast){
			if(state2.requiredPast.contains(seq)) continue;
			/*if((this.name.compareTo("S33")==0) && (state2.name.compareTo("S48")==0)){
				System.out.println(state2.traces);
			}*/
			if(state2.traces.size()==0){
				return false;
			}
			for(SimpleEntry<Integer,SynopticTrace> t:state2.traces){
				SynopticTrace trace=t.getValue();
				
				if(!trace.has(t.getKey(),false,seq)){
					return false;
				}
			}
		}
		for(Sequence seq:state2.requiredFuture){
			if(requiredFuture.contains(seq)) continue;
			if(traces.size()==0){
				return false;
			}
			for(SimpleEntry<Integer,SynopticTrace> t:traces){
				SynopticTrace trace=t.getValue();
				if(!trace.has(t.getKey(),true,seq)){
					return false;
				}
			}
		}
		for(Sequence seq:state2.requiredPast){
			if(requiredPast.contains(seq)) continue;
			if(traces.size()==0){
				return false;
			}
			for(SimpleEntry<Integer,SynopticTrace> t:traces){
				SynopticTrace trace=t.getValue();
				if(!trace.has(t.getKey(),false,seq)){
					return false;
				}
			}
		}
		return true;
	}
	
}
	
