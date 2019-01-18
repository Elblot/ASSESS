/*
 *  TemporalRuleMiner.java
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
//import java.util.Set;
import java.util.HashSet;

import traces.ObjectClass;
import traces.Method;
import traces.ObjectInstance;
import traces.Statement;
import traces.Trace;
import java.util.HashMap;

//import javax.annotation.processing.SupportedAnnotationTypes;

import core.Sequence;

import fsa.FSA;
import fsa.Transition;

import program.Actor;
/**
 * Miner of temporal dependencies
 * 
 * @author Sylvain Lamprier
 */
public class TemporalRuleMiner {
	//private boolean strict=true;
	private int maxPreRules=1;
	private int maxPreStricts=1;
	//private int maxPostSize=1;
	private double support=0.2;
	private HashMap<Integer,HashMap<Integer,HashSet<Sequence>>> currentSeqs=null; // last sequences computed (for pre computation)
	private HashSet<Sequence> supported=null; // pre conds 
	private Sequence preRoot;
	TemporalRules rules=null; // rules
	//private static int mode=1;
	
	/**
	 * Default constructor.
	 * maximal length of pre part of rules = 1
	 * maximal length of pre part of strict rules = 0
	 * support = 0.2
	 */
	public TemporalRuleMiner(){
		this(1,0,0.2);
	}
	
	/**
	 * 
	 * @param maxPreRules  maximal length of pre part of rules
	 * @param maxPreStricts maximal length of pre part of strict rules
	 * @param support 	minimal ratio of traces in which the pre part of rules has to be observed
	 */
	public TemporalRuleMiner(int maxPreRules, int maxPreStricts, double support){
		//this.strict=strict;
		this.maxPreRules=maxPreRules;
		this.maxPreStricts=maxPreStricts;
		//this.maxPostSize=maxPostSize;
		this.support=support;
		this.preRoot=new Sequence();
	}
	
	
	/**
	 * 
	 * @return a set of mined rules
	 */
	public TemporalRules getRules(){
		return(rules);
	}
	
	
	/**
	 * 
	 * @param trace
	 * @return a LTS corresponding to the trace given as argument
	 */
	public FSA getLTSForSingleTrace(Trace trace){
		//int seqSize=maxPreSize;
		TemporalState.possiblePre=supported;
		TemporalState.rules=rules;
		FSA lts=new FSA();
		TemporalState s0=new TemporalState();
		lts.addState(s0);
		lts.setInitialState(s0);
		//NewTemporalState.maxSize=seqSize;
		TemporalState source=s0;
		int size=trace.getSize();
		TemporalState first=source;
		TemporalState last=null;
		if (size>0){
			Statement st=trace.getByIndex(1);
			for(int i=2;i<=size+1;i++){
				TemporalState nstate=new TemporalState();
				lts.addState(nstate);
				lts.addTransition(new Transition(source,st,nstate));
				source=nstate;
				if (i<=size){
					st=trace.getByIndex(i);
				}
				else{
					lts.addFinalState(nstate);
					last=nstate;
				}
			}
		}
		else{
			lts.addFinalState(source);
		}
		
		/*if (computePast){
				first.updatePast(new HashSet<Sequence>(), strategy);
		}
		last.updateFuture(new HashSet<Sequence>(), strategy);*/
		
			
		return(lts);
	}
	
	public FSA getLTSForSingleTraceWithAllRequirements(Trace trace){
		//NewTemporalState.mode=mode;
		TemporalState.possiblePre=supported;
		TemporalState.rules=rules;
		
		FSA lts=getLTSForSingleTrace(trace);
		TemporalState start=(TemporalState)lts.getInitialState();
		//HashMap<String,HashMap<Sequence,Integer>> require=NewTemporalState.computeRequireSet(strategy,supported,true);
		//start.computeFutureRequirements(require, new HashSet<Sequence>(), new HashSet<Sequence>(), new HashSet<Sequence>(), strategy);
		start.computeFutureRequirements("",new HashMap<Sequence,Integer>(),new HashSet<Sequence>(),new HashSet<Sequence>(),new HashMap<Sequence,Integer>());
				
		TemporalState end=(TemporalState)lts.getFinalState(); //.get(0);
		//require=NewTemporalState.computeRequireSet(strategy,supported,false);
		//end.computePastRequirements(require, new HashSet<Sequence>(), new HashSet<Sequence>(), new HashSet<Sequence>(), strategy);
		end.computePastRequirements("",new HashMap<Sequence,Integer>(),new HashSet<Sequence>(),new HashSet<Sequence>(),new HashMap<Sequence,Integer>());
		//start.display(true);
		
		return(lts);
	}

	// computes pre conds for traces with eq strategy according to a size of sequences. Modifies currentSeqs which is a list for each trace index of starting of sequences of sizePre-1 events (replaced by seqs of new size). Uses supported which is a list of supported pres of inferior sizes 
	private void computePreSize(ArrayList<Trace> traces,int sizePre){
			HashMap<Sequence,Integer> counts=new HashMap<Sequence,Integer>();
			//HashMap<Integer,HashSet<Sequence>>
			int nbTraces=traces.size();
			ArrayList<HashMap<Sequence,HashMap<Integer,Integer>>> iseqs=new ArrayList<HashMap<Sequence,HashMap<Integer,Integer>>>();
			HashMap<Sequence,HashMap<Integer,ArrayList<Integer>>> pos=new HashMap<Sequence,HashMap<Integer,ArrayList<Integer>>>(); 
			HashMap<Integer,HashSet<Sequence>> tr_atoms=new HashMap<Integer,HashSet<Sequence>>();
			int t=1;
			int nbt=traces.size();
			for(Trace trace:traces){
				//System.out.println("Trace "+t);
				int size=trace.getSize();
				for(int i=1;i<=size;i++){
					Statement statement=trace.getByIndex(i);
					String st=statement.getText();
					Sequence seq=preRoot.getForwardChild(st);
					HashMap<Integer,ArrayList<Integer>> a=pos.get(seq);
					if (a==null){
						a=new HashMap<Integer,ArrayList<Integer>>();
						pos.put(seq, a);
					}
					ArrayList<Integer> apos=a.get(t);
					if (apos==null){
						apos=new ArrayList<Integer>();
						a.put(t, apos);
					}
					apos.add(i);
				}
				t++;
			}
			HashMap<Sequence,HashMap<Integer,Integer>> iseqa=new HashMap<Sequence,HashMap<Integer,Integer>>();
			iseqs.add(iseqa);
			HashSet<Sequence> asup=new HashSet<Sequence>(); 
			for(Sequence atom:pos.keySet()){
				HashMap<Integer,ArrayList<Integer>> h=pos.get(atom);
				int nb=h.size();
				double r=(1.0*nb)/(1.0*nbt);
				if (r>=support){
					supported.add(atom);
					HashMap<Integer,Integer> sa=new HashMap<Integer,Integer>();
					for(Integer i:h.keySet()){
						int first=h.get(i).get(0);
						sa.put(i, first);
						HashSet<Sequence> at=tr_atoms.get(i);
						if (at==null){
							at=new HashSet<Sequence>();
							tr_atoms.put(i, at);
						}
						at.add(atom);
					}
					iseqa.put(atom, sa);
				}
				else{
					asup.add(atom);
				}
			}
			for(Sequence atom:asup){
				pos.remove(atom);
			}
			System.out.println("fin atoms");
			
			for(int j=2;j<=sizePre;j++){
				HashMap<Sequence,HashMap<Integer,Integer>> oseq=iseqs.get(j-2);
				HashMap<Sequence,HashMap<Integer,Integer>> nseq=new HashMap<Sequence,HashMap<Integer,Integer>>();
				iseqs.add(nseq);
				for(Sequence seq:oseq.keySet()){
					HashMap<Integer,Integer> sh=oseq.get(seq);
					for(Integer i:sh.keySet()){
						int posi=sh.get(i);
						HashSet<Sequence> tatoms=tr_atoms.get(i);
						for(Sequence a:tatoms){
							ArrayList<Integer> ar=pos.get(a).get(i);
							for(int x:ar){
								if (x>posi){
									Sequence ns=seq.getForwardChild(a.getString());
									//Sequence ns=new Sequence(seq.toString(),j-1);
									//ns.addAfter(a.toString());
									HashMap<Integer,Integer> h=nseq.get(ns);
									if(h==null){
										h=new HashMap<Integer,Integer>();
										nseq.put(ns, h);
									}
									h.put(i, x);
									break;
								}
							}
						}
					}
				}
				asup=new HashSet<Sequence>();
				for(Sequence seq:nseq.keySet()){
					int nb=nseq.get(seq).size();
					double r=(1.0*nb)/(1.0*nbt);
					if (r>=support){
						supported.add(seq);
						//System.out.println(supported.size());
					}
					else{
						asup.add(seq);
					}
				}
				for(Sequence seq:asup){
					nseq.remove(seq);
				}
				System.out.println("fin pre "+j);
				
			}
			//System.out.println(supported);
			System.out.println(supported.size());
			
	}
	
	// computes pre conds for traces with eq strategy according to a size of sequences. Modifies currentSeqs which is a list for each trace index of starting of sequences of sizePre-1 events (replaced by seqs of new size). Uses supported which is a list of supported pres of inferior sizes 
	private void computePreSize2(ArrayList<Trace> traces,int sizePre){
		HashMap<Sequence,Integer> counts=new HashMap<Sequence,Integer>();
		int nbTraces=traces.size();
		HashMap<Integer,HashMap<Integer,HashSet<Sequence>>> seqs=currentSeqs;
		
		int j=sizePre;
		//for(int j=1;j<=sizePre;j++){
			counts=new HashMap<Sequence,Integer>();
			HashMap<Integer,HashMap<Integer,HashSet<Sequence>>> nseqs=new HashMap<Integer,HashMap<Integer,HashSet<Sequence>>>();
			int idt=1;
			for(Trace trace:traces){
				HashSet<Sequence> vus=new HashSet<Sequence>();
				HashMap<Integer,HashSet<Sequence>> fs=new HashMap<Integer,HashSet<Sequence>>();
				nseqs.put(idt, fs);
				HashMap<Integer,HashSet<Sequence>> fh=new HashMap<Integer,HashSet<Sequence>>();
				if (j>1){
					fh=seqs.get(idt);
				}
				else{
					HashSet<Sequence> h=new HashSet<Sequence>();
					h.add(new Sequence());
					fh.put(0,h);
				}
				int size=trace.getSize();
				for(int i=1;i<=size;i++){
					Statement statement=trace.getByIndex(i);
					String st=statement.getText();
					//System.out.println("st "+st);
					HashSet<Sequence> nh=new HashSet<Sequence>();
					fs.put(i, nh);
					for(Integer k:fh.keySet()){
					//for(int k=0;k<i;k++){
					  if(k<i){	
						HashSet<Sequence> h=fh.get(k);
						for(Sequence sq:h){
							//Sequence fromSup=supported.get(sq);
							//if(j==1){
							if ((j==1) || (supported.contains(sq))){ //(supports.get(sq)>support)){
								Sequence nsq=sq.copy();
								nsq.addAfter(st);
								//forwardParent
								//System.out.println("ns = "+nsq);
								nh.add(nsq);
								if (!vus.contains(nsq)){
									vus.add(nsq);
									Integer nombre=counts.get(nsq);
									int cq=0;
									if (nombre!=null){
										cq=nombre;
									}	
									counts.put(nsq, cq+1);
								}
								
								//System.out.println(nh);
							}
						}
					  }
					}
				}
				idt++;
			}
			//System.out.println(nseqs);
			currentSeqs=nseqs;
			if (nbTraces>0){
				for(Sequence sq:counts.keySet()){
					double cq=(1.0*counts.get(sq))/nbTraces;
					//System.out.println(sq+" => "+cq);
					if (cq>=support){
						supported.add(sq);
					}
				}
			}
			
		//}
		//return(supported);
	}
	
	private void mineRulesSize(ArrayList<Trace> traces, int preSize){
		TemporalState.possiblePre=supported;
		if (preSize>0){
			TemporalState.maxSize=preSize;
		}
		//System.out.println("pre ok");
		int it=0;
		for(Trace trace:traces){
			
			it++;
			System.out.println("trace "+it);
			FSA lts=getLTSForSingleTrace(trace);
			TemporalState start=(TemporalState)lts.getInitialState();
			start.countPast("",new HashMap<Sequence,Integer>());
			TemporalState end=(TemporalState)lts.getFinalState(); //.get(0);
			end.countFuture("",new HashMap<Sequence,Integer>());
			System.out.println("trace "+it+" propagated");
			/*HashMap<Sequence,HashSet<Sequence>> nfutureRules=new HashMap<Sequence,HashSet<Sequence>>();
			HashMap<Sequence,HashSet<Sequence>> npastRules=new HashMap<Sequence,HashSet<Sequence>>();
			HashMap<Sequence,HashSet<Sequence>> nfutureStricts=new HashMap<Sequence,HashSet<Sequence>>();
			HashMap<Sequence,HashSet<Sequence>> npastStricts=new HashMap<Sequence,HashSet<Sequence>>();*/
			//HashSet<Sequence> newFuture=new HashSet<Sequence>();
			//HashSet<Sequence> newPast=new HashSet<Sequence>();
			
			
			//int i=1;
			//start.display(true);
			TemporalState ns=start;
			//new HashSet<Sequence>();
			boolean stop=false;
			while(!stop){
				//String st=strategy.getText(trace.getByIndex(i));
				//i++;
				
				// Pour future rules
				HashSet<Sequence> npast=ns.changedPast;
				HashMap<Sequence,Integer> future=ns.ensuredFuture;
				//ns.display(false);
				for(Sequence pre:npast){
					//System.out.println(pre);
					//if (!past.contains(seq)){
						//if ((seq.size()<=maxPreSize) &&(preSeq.contains(seq))){
						if((pre.size()==preSize) || (preSize==-1)){
							
							HashSet<Sequence> subPre=new HashSet<Sequence>();
							//if(pre.size()>1){subPre=pre.getSubs(true);}
							
							
							int nppre=0;
							int nfpre=0;
							if (maxPreStricts>=pre.size()){
								Integer inppre=ns.ensuredPast.get(pre);
								
								if (inppre!=null){
									nppre=inppre;
								}
								Integer infpre=ns.ensuredFuture.get(pre);
								if (infpre!=null){
									nfpre=infpre;
								}
							}
							
							// Pas encore vu ce pre, on conserve tous les pre:post 
							if (!rules.vusFuture.contains(pre)){ // && (!futureRules.containsKey(seq))){
								rules.vusFuture.add(pre);
								HashSet<Sequence> posts=new HashSet<Sequence>();
								rules.futureRules.put(pre, posts);
								HashSet<Sequence> sposts=new HashSet<Sequence>();
								if (maxPreStricts>=pre.size()){
									rules.futureStricts.put(pre, sposts);
									
								}
								/*HashSet<Sequence> deja=new HashSet<Sequence>();
								for(Sequence post:future.keySet()){
									boolean deja=false;
									for(Sequence sub:subPre){
										if (maxPreStricts>=preSize){
											HashSet<Sequence> subp=futureStricts.get(sub);
											
									}
								}*/
								for(Sequence post:future.keySet()){
									boolean deja=false;
									if ((maxPreRules>=pre.size()) && (post.size()==1)){
									  for(Sequence sub:subPre){
										HashSet<Sequence> subp=rules.futureStricts.get(sub);
										if (subp!=null){
												if (subp.contains(post)){
													deja=true;
													break;
												}
										}
										
										subp=rules.futureRules.get(sub);
										if (subp!=null){
												if (subp.contains(post)){
													//System.out.println("future :"+pre+"=>"+post+" deja : "+sub+" => "+post);
													
													deja=true;
													break;
												}
										}
											
									  }
									}
										
									if ((maxPreStricts>=pre.size()) && (maxPreStricts>=post.size())){ // && (post.size()==1)){
										Integer inppost=ns.ensuredPast.get(post);
										int nbp=nppre-((inppost!=null)?inppost:0);
										Integer infpost=ns.ensuredFuture.get(post);
										int nbf=((infpost!=null)?infpost:0)-nfpre;
										//System.out.println(pre+" => "+post+" = "+nppre+" "+inppost+" "+nfpre+" "+infpost);
										if ((nbp>=0) && (nbf>=0) && ((nbp-nbf)==0)){
											sposts.add(post);
											
											
										}
									}
									
									if ((!deja) && (maxPreRules>=pre.size()) && (post.size()==1)){ // On ne conserve que les regles sous forme canonique
										//System.out.println("add future en "+i+": "+seq+"="+post);
										posts.add(post);
									}
								}
							}
							else{
								// deja vu ce pre on supprime tous les post pas dans future 
								HashSet<Sequence> posts=rules.futureRules.get(pre);
								if (posts!=null){
									supNb0Posts(posts,future);
								}
								if (maxPreStricts>=pre.size()){
									posts=rules.futureStricts.get(pre);
									if (posts!=null){
										supNb0Posts(posts,future);
									}
									// On verifie que toutes les regles sont toujours strictes symetriques 
									HashSet<Sequence> asup=new HashSet<Sequence>();
									for(Sequence post:posts){
										Integer inppost=ns.ensuredPast.get(post);
										int nbp=nppre-((inppost!=null)?inppost:0);
										Integer infpost=ns.ensuredFuture.get(post);
										int nbf=((infpost!=null)?infpost:0)-nfpre;
										if (!((nbp>=0) && (nbf>=0) && ((nbp-nbf)==0))){
											asup.add(post);
										}
									}
									for(Sequence post:asup){
										posts.remove(post);
										
									}
								}
								
							}
						}
					//}
				}


				// Pour past rules
				HashSet<Sequence> nfuture=ns.changedFuture;
				HashMap<Sequence,Integer> past=ns.ensuredPast;
				for(Sequence pre:nfuture){
					if((pre.size()==preSize) || (preSize==-1)){
						
							HashSet<Sequence> subPre=new HashSet<Sequence>();
							//if(pre.size()>1){subPre=pre.getSubs(false);}
							
							
							int nppre=0;
							int nfpre=0;
							if (maxPreStricts>=pre.size()){
								Integer inppre=ns.ensuredPast.get(pre);
								if (inppre!=null){
									nppre=inppre;
								}
								Integer infpre=ns.ensuredFuture.get(pre);
								if (infpre!=null){
									nfpre=infpre;
								}
							}
							
							// Pas encore vu ce pre, on conserve tous les pre:post 
							if (!rules.vusPast.contains(pre)){ 
								rules.vusPast.add(pre);
								HashSet<Sequence> posts=new HashSet<Sequence>();
								rules.pastRules.put(pre, posts);
								HashSet<Sequence> sposts=new HashSet<Sequence>();
								if (maxPreStricts>=pre.size()){
									rules.pastStricts.put(pre, sposts);
								}
								for(Sequence post:past.keySet()){
									boolean deja=false;
									if ((maxPreRules>=pre.size()) && (post.size()==1)){
									  for(Sequence sub:subPre){
										  //System.out.println("pre : "+pre+" post : "+post+" sub : "+sub);
										HashSet<Sequence> subp=rules.pastStricts.get(sub);
										if (subp!=null){
												if (subp.contains(post)){
													//System.out.println("past :"+pre+"=>"+post+" deja : "+sub+" => "+post);
													deja=true;
													break;
												}
												
										}
										
										subp=rules.pastRules.get(sub);
										if (subp!=null){
												if (subp.contains(post)){
													//System.out.println("past :"+pre+"=>"+post+" deja : "+sub+" => "+post);
													
													deja=true;
													break;
												}
										}
											
									  }
									}
									
									if ((maxPreStricts>=pre.size()) && (maxPreStricts>=post.size())){ // && (post.size()==1)){
										Integer inppost=ns.ensuredPast.get(post);
										int nbp=((inppost!=null)?inppost:0)-nppre;
										Integer infpost=ns.ensuredFuture.get(post);
										int nbf=nfpre-((infpost!=null)?infpost:0);
										if ((nbp>=0) && (nbf>=0) && ((nbp-nbf)==0)){
											sposts.add(post);
										}
									}
									
									if ((!deja) && (maxPreRules>=pre.size()) && (post.size()==1)){ // On ne conserve que les regles sous forme canonique
										//System.out.println("add past : "+pre+"="+post);
										posts.add(post);
									}
								}
							}
							else{
								// deja vu ce pre on supprime tous les post pas dans past 
								HashSet<Sequence> posts=rules.pastRules.get(pre);
								if (posts!=null){
									supNb0Posts(posts,past);
								}
								if (maxPreStricts>=pre.size()){
									posts=rules.pastStricts.get(pre);
									if (posts!=null){
										supNb0Posts(posts,past);
									}
									// On verifie que toutes les regles sont toujours strictes symetriques 
									HashSet<Sequence> asup=new HashSet<Sequence>();
									for(Sequence post:posts){
										Integer inppost=ns.ensuredPast.get(post);
										int nbp=((inppost!=null)?inppost:0)-nppre;
										Integer infpost=ns.ensuredFuture.get(post);
										int nbf=nfpre-((infpost!=null)?infpost:0);
										if (!((nbp>=0) && (nbf>=0) && ((nbp-nbf)==0))){
											asup.add(post);
										}
									}
									for(Sequence post:asup){
										posts.remove(post);
									}
								}
								
							}
						}
					//}
				}
				if (ns.getSuccesseurs().size()==0){
					stop=true;
				}
				else{
					ns=(TemporalState)ns.getSuccesseurs().get(0).getTarget();
				}
				
			}
			
			
		}
		
		/*if (maxPreStricts>=preSize){
			//nettoyageRules(futureStricts);
			//nettoyageRules(pastStricts);
		}*/
	}
	
	public void mineRules(ArrayList<Trace> traces){
		currentSeqs=new HashMap<Integer,HashMap<Integer,HashSet<Sequence>>>();
		supported=new HashSet<Sequence>();
		rules=new TemporalRules();
		preRoot=new Sequence();
		int maxPre=maxPreRules;
		if (maxPreStricts>maxPre){
			maxPre=maxPreStricts;
		}
		TemporalState.maxSize=maxPre;
		Sequence.lockedTree=false;
		
		//for(int i=1;i<=maxPre;i++){
			// Compute pre sequence
			computePreSize(traces,maxPre);
			//System.out.println("pre "+i+"ok");
		//}
			Sequence.lockedTree=true;
		/*for(int i=1;i<=maxPre;i++){
			mineRulesSize(traces,strategy,i);
			System.out.println("rules size "+i+" ok");
		}*/
			
		preRoot=new Sequence();
		Sequence.root=preRoot;
		Sequence.addInTree(preRoot, supported);
		TemporalState.root=preRoot;
		mineRulesSize(traces,-1);
		
		//if (mode>0){
			rules.eliminerNonSymetric();
		//}
		
		rules.nettoyer();
		
		/*System.out.println(rules.futureRules);
		System.out.println(rules.pastRules);
		System.out.println(rules.futureStricts);
		System.out.println(rules.pastStricts);*/
		
		/*if (NewTemporalState.mode>0){
			rules.indexByPosts();
		}*/
		/*
			Sequence root=new Sequence();
			Sequence.addInTree(root, supported);
			NewTemporalState.rootPre=root;
		}*/
		
	}
	
	// retourne une map<seq,b> de sequences seq de h desquelles s est une super ou une sub sequence (b = true si s est une super de seq)
	/*private HashMap<Sequence,Boolean> getSuperAndSubsOfASequence(Sequence s,Set<Sequence> h){
		HashMap<Sequence,Boolean> ret=new HashMap<Sequence,Boolean>();
		
		for(Sequence sh:h){
			int x=s.isSubOrSuperSequenceOf(sh);
			if (x==-1){
				ret.put(sh, false);
			}
			if (x==1){
				ret.put(sh, true);
			}
		}
		return(ret);
	}*/
	
	// pruning des rules selon article de Lo: Mining temporal rules for software maintenance
	// attention le theoreme 3 du papier a un soucis (c'est Y qu'on peut virer)
	/*private void nettoyageRules(HashMap<Sequence,HashSet<Sequence>> rules){
		
		
		for(Sequence pre:rules.keySet()){
				int sizePre=pre.size();
				HashSet<Sequence> posts=rules.get(pre);
				
				// Theoreme 3 => pruning entre pre super ou subs pour un meme post (on garde le plus petit)
				HashMap<Sequence,Boolean> seqs=getSuperAndSubsOfASequence(pre,rules.keySet());
				for(Sequence seq:seqs.keySet()){
					if (seq.size()!=sizePre){
						boolean b=seqs.get(seq);
						HashSet<Sequence> posts2=rules.get(seq);
						HashSet<Sequence> asup=new HashSet<Sequence>();
						for(Sequence post:posts){
							if (posts2.contains(post)){
								// Deux pre avec le meme post, et un pre inclus dans l autre, on sup la regle dont le pre est super de l autre
								if (b){
									asup.add(post);
									System.out.println("Suppression de la regle : "+pre+" => "+post+" rendue redondante par "+seq+" => "+post);
								}
								else{
									System.out.println("Suppression de la regle : "+seq+" => "+post+" rendue redondante par "+pre+" => "+post);
									posts2.remove(post);
								}
							}
						}
						for(Sequence post:asup){
							posts.remove(post);
						}
					}
				}
				
				// Theroreme 4 => pruning entre posts de meme pre avec l'un sup ou sub de l autre (on garde le plus grand)
				//TODO
		}
	}*/
	
	// supprime toutes les sequences de h dont le nombre qui leur est associe dans set est 0 (ou si il ne figurent pas dans set) 
	private void supNb0Posts(HashSet<Sequence> h,HashMap<Sequence,Integer> set){
		HashSet<Sequence> asup=new HashSet<Sequence>();
		for(Sequence post:h){
			//if(post.size()==1){
			if ((!set.containsKey(post)) || (set.get(post)==0)){
				asup.add(post);
			}
		}
		for(Sequence post:asup){
			h.remove(post);
		}
	}
	
	private void supElsPasDansSet(HashSet<Sequence> h,HashSet<Sequence> set){
		HashSet<Sequence> asup=new HashSet<Sequence>();
		for(Sequence post:h){
			if (!set.contains(post)){
				asup.add(post);
			}
		}
		for(Sequence post:asup){
			//if (seq.toString().compareTo("[R:R.b(), R:R.f()]")==0){
			//	System.out.println("supp past en "+i+": "+seq+"="+post);
				
			//}
			h.remove(post);
		}
	}
	
	
	
	public static void main(String[] args){
		Trace t=new Trace();
		ObjectClass cj=new ObjectClass("C");
		Actor oj=new Actor("R",cj);
		Method a=new Method("a",new ArrayList<ObjectInstance>(),cj);
		Method b=new Method("b",new ArrayList<ObjectInstance>(),cj);
		Method c=new Method("c",new ArrayList<ObjectInstance>(),cj);
		Method d=new Method("d",new ArrayList<ObjectInstance>(),cj);
		Method e=new Method("e",new ArrayList<ObjectInstance>(),cj);
		Method f=new Method("f",new ArrayList<ObjectInstance>(),cj);
		Method g=new Method("g",new ArrayList<ObjectInstance>(),cj);
		
		t.add(new Statement(oj,c,oj));
		t.add(new Statement(oj,a,oj));
		t.add(new Statement(oj,b,oj));
		t.add(new Statement(oj,b,oj));
		t.add(new Statement(oj,f,oj));
		t.add(new Statement(oj,g,oj));
		//t.add(new Statement(oj,c,oj));
		/*t.add(new Statement(oj,a,oj));
		t.add(new Statement(oj,c,oj));
		t.add(new Statement(oj,d,oj));
		t.add(new Statement(oj,d,oj));
		t.add(new Statement(oj,a,oj));
		t.add(new Statement(oj,b,oj));
		t.add(new Statement(oj,e,oj));
		t.add(new Statement(oj,b,oj));
		t.add(new Statement(oj,b,oj));
		t.add(new Statement(oj,e,oj));
		t.add(new Statement(oj,g,oj));
		t.add(new Statement(oj,f,oj));
		*/
		Trace t2=new Trace();
		t2.add(new Statement(oj,a,oj));
		t2.add(new Statement(oj,c,oj));
		t2.add(new Statement(oj,b,oj));
		t2.add(new Statement(oj,b,oj));
		t2.add(new Statement(oj,b,oj));
		t2.add(new Statement(oj,e,oj));
		t2.add(new Statement(oj,e,oj));
		t2.add(new Statement(oj,e,oj));
		t2.add(new Statement(oj,f,oj));
		t2.add(new Statement(oj,f,oj));
		t2.add(new Statement(oj,g,oj));
		TemporalRuleMiner ti=new TemporalRuleMiner(2,1,0.2);
		ArrayList<Trace> traces=new ArrayList<Trace>();
		traces.add(t);
		traces.add(t2);
		
		ti.mineRules(traces);
		//System.out.println(ti.rules.pastRules.get(new Sequence("R:R.c();:@:;R:R.f()",1)));
		ti.getLTSForSingleTraceWithAllRequirements(traces.get(0));
		ti.getLTSForSingleTraceWithAllRequirements(traces.get(1));
		
		//System.out.println(ti.rules.pastRules.get(new Sequence("R:R.b()",1)));
	}
}
