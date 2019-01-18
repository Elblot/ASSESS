/*
 *  TemporalRules.java
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

import java.util.ArrayList;
import java.util.HashMap;
import fsa.FSA;
import java.util.HashSet;

import core.Sequence;

import traces.Statement;
import traces.Trace;

public class TemporalRules {
	HashMap<Sequence,HashSet<Sequence>> futureRules=new HashMap<Sequence,HashSet<Sequence>>();
	HashMap<Sequence,HashSet<Sequence>> pastRules=new HashMap<Sequence,HashSet<Sequence>>();
	HashMap<Sequence,HashSet<Sequence>> futureStricts=new HashMap<Sequence,HashSet<Sequence>>();
	HashMap<Sequence,HashSet<Sequence>> pastStricts=new HashMap<Sequence,HashSet<Sequence>>();
	HashMap<Sequence,HashSet<Sequence>> postfutureRules=new HashMap<Sequence,HashSet<Sequence>>();
	HashMap<Sequence,HashSet<Sequence>> postpastRules=new HashMap<Sequence,HashSet<Sequence>>();
	HashSet<Sequence> vusFuture=new HashSet<Sequence>();
	HashSet<Sequence> vusPast=new HashSet<Sequence>();
	
	public TemporalRules(){
		this(new HashMap<Sequence,HashSet<Sequence>>(),new HashMap<Sequence,HashSet<Sequence>>(),new HashMap<Sequence,HashSet<Sequence>>(),new HashMap<Sequence,HashSet<Sequence>>(),new HashSet<Sequence>(),new HashSet<Sequence>());
	}
	public TemporalRules(HashMap<Sequence,HashSet<Sequence>> futureRules,HashMap<Sequence,HashSet<Sequence>> pastRules,HashMap<Sequence,HashSet<Sequence>> futureStricts,HashMap<Sequence,HashSet<Sequence>> pastStricts,HashSet<Sequence> vusFuture,HashSet<Sequence> vusPast){
		this.futureRules=futureRules;
		this.pastRules=pastRules;
		this.pastStricts=pastStricts;
		this.futureStricts=futureStricts;
		this.vusFuture=vusFuture;
		this.vusPast=vusPast;
	}
	
	
	
	public HashMap<Sequence, HashSet<Sequence>> getFutureRules() {
		return futureRules;
	}
	public HashMap<Sequence, HashSet<Sequence>> getPastRules() {
		return pastRules;
	}
	public HashMap<Sequence, HashSet<Sequence>> getPostfutureRules() {
		return postfutureRules;
	}
	public HashMap<Sequence, HashSet<Sequence>> getPostpastRules() {
		return postpastRules;
	}
	
	public void nettoyer(){
		suppVides(futureRules);
		suppVides(pastRules);
		suppVides(futureStricts);
		suppVides(pastStricts);
		suppInBothSets(futureStricts,futureRules);
		suppInBothSets(pastStricts,pastRules);
	}
	
	
	
	private void suppVides(HashMap<Sequence,HashSet<Sequence>> set){
		HashSet<Sequence> asup=new HashSet<Sequence>();
		for(Sequence seq:set.keySet()){
			if (set.get(seq).size()==0){
				asup.add(seq);
			}
		}
		for(Sequence seq:asup){
			set.remove(seq);
		}
	}
	private void suppInBothSets(HashMap<Sequence,HashSet<Sequence>> set,HashMap<Sequence,HashSet<Sequence>> set2){
		for(Sequence pre:set.keySet()){
			HashSet<Sequence> posts2=set2.get(pre);
			if (posts2!=null){
				HashSet<Sequence> posts=set.get(pre);
				for(Sequence post:posts){
					posts2.remove(post);
				}
			}
		}
	}
	
	public void indexByPosts(){
		for(Sequence s:futureRules.keySet()){
			HashSet<Sequence> posts=futureRules.get(s);
			for(Sequence post:posts){
				HashSet<Sequence> pres=postfutureRules.get(post);
				if (pres==null){
					pres=new HashSet<Sequence>();
					postfutureRules.put(post, pres);
				}
				pres.add(s);
			}
		}
		for(Sequence s:pastRules.keySet()){
			HashSet<Sequence> posts=pastRules.get(s);
			for(Sequence post:posts){
				HashSet<Sequence> pres=postpastRules.get(post);
				if (pres==null){
					pres=new HashSet<Sequence>();
					postpastRules.put(post, pres);
				}
				pres.add(s);
			}
		}
	}
	
	public void eliminerNonSymetric(){
		suppSymetricNotInSet(futureStricts,pastStricts);
		suppSymetricNotInSet(pastStricts,futureStricts);
	}
	private void suppSymetricNotInSet(HashMap<Sequence,HashSet<Sequence>> set,HashMap<Sequence,HashSet<Sequence>> set2){
		HashSet<Sequence> asup=new HashSet<Sequence>();
		for(Sequence pre:set.keySet()){
				HashSet<Sequence> posts=set.get(pre);
				for(Sequence post:posts){
					HashSet<Sequence> posts2=set2.get(post);
					if ((posts2==null) || (!posts2.contains(pre))){
						asup.add(post);
					}
				}
				for(Sequence post:asup){
					posts.remove(post);
				}
				
		}
	}
	
	public void checkSimpleRules(FSA fsa,int nb){
		System.out.println(Sequence.lockedTree);
		fsa.max_trace_length=100;
		ArrayList<Trace> traces=new ArrayList<Trace>();
		
			traces=fsa.genereTraces(nb);
		
		System.out.println("Traces generees");
		for(Sequence seq:futureRules.keySet()){
			for(Trace t:traces){
				HashSet<Sequence> vus=new HashSet<Sequence>();
				for(int i=(t.getSize()-1);i>=0;i--){
					Statement st=t.getStatement(i);
					String ch=st.getText();
					Sequence s=Sequence.root.getForwardChild(ch);
					if(s==null){
						System.out.println(ch);
					}
					vus.add(s);
					if(s.equals(seq)){
						HashSet<Sequence> posts=futureRules.get(seq);
						for(Sequence post:posts){
							if(!vus.contains(post)){
								System.out.println("Probleme future rule, pre "+seq+" sans post "+post);
								return;
							}
						}
						break;
					}
				}
			}
		}
		for(Sequence seq:pastRules.keySet()){
			for(Trace t:traces){
				HashSet<Sequence> vus=new HashSet<Sequence>();
				for(int i=0;i<t.getSize();i++){
					Statement st=t.getStatement(i);
					String ch=st.getText();
					Sequence s=Sequence.root.getForwardChild(ch);
					vus.add(s);
					if(s.equals(seq)){
						HashSet<Sequence> posts=pastRules.get(seq);
						for(Sequence post:posts){
							if(!vus.contains(post)){
								System.out.println("Probleme past rule, pre "+seq+" sans post "+post);
								return;
							}
						}
						break;
					}
				}
			}
		}
		
	}
}
