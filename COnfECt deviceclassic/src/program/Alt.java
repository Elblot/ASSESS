/*
 *  Alt.java
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


package program;

import java.util.ArrayList;


import java.util.HashMap;

import traces.*;

import java.util.HashSet;
import java.util.List;

import core.Ensemble;
import dataGenerator.Etat_Prog;
import dataGenerator.StrBlocChoice;
import dataGenerator.VocabulaireVideException;
import fsa.FSA;
import fsa.State;
import fsa.Transition;


import regexp.Alternation;
import regexp.Regexp;

/**
 * 
 * Represents an alternative branching bloc of a program.
 * 
 * @author Sylvain Lamprier
 *
 */
public class Alt extends Block implements Alternation {
	private static final long serialVersionUID = 1L;
	
	
	public static double more=0.5;  // probabilite d'ajouter chaque else (a part le premier) 
	ArrayList<Block> blocs=null;
	double[] p=null;  
	double sum=0; // Somme des elements de p
	int nb=0;
	ArrayList<Etat_Prog> conditions=null;
	
	// Pour methode copie ou construction manuelle
	/*public Alt(int profondeur){
		this(new ArrayList<Bloc>(),profondeur);
	}*/
	
	// Pour construction manuelle
	/*public Alt(ArrayList<Bloc> blocs){ //,int profondeur){
		super(profondeur);
		this.blocs=blocs;
		initProbasAndConditions();
		
		
	}*/
	
	// Pour construction manuelle
	/*public Alt(ArrayList<Bloc> blocs){
	    this(blocs,0);
	}*/
	
	// Pour construction manuelle
	public Alt(){
		   blocs=new ArrayList<Block>(); 
	}
	
	
	
	
	// Pour construction manuelle avec probas (pour biased traces)
	/*public Alt(ArrayList<Bloc> blocs,int profondeur,double[] w){
		this(blocs,profondeur);
		p=w;
		sum=0;
		
		for(int i=0;i<nb;i++){
			sum+=p[i];
		}
	}*/
	// Pour construction manuelle avec probas (pour biased traces)
	/*public Alt(ArrayList<Bloc> blocs,double[] w){
		this(blocs,0,w);
	}*/
	public void setProbas(double[] w){
		if (w.length!=blocs.size()){
			System.out.println("W has not same size as alternatives");
		}
		else{
			p=w;
			sum=0;
			
			for(int i=0;i<nb;i++){
				sum+=p[i];
			}
		}
	}
	
	
	// Pour generation automatique
	public Alt(StrBlocChoice stc,Actor last,HashSet<Statement> discard,int profondeur) throws VocabulaireVideException{
		super(profondeur);
		System.out.println("Alt");
		vide_possible=0;
		blocs=new ArrayList<Block>();
		HashSet<Statement> disc=Ensemble.copyHashSet(discard);
		size=1;
		//ArrayList<ObjectJava> copypile=copyPile(pile);
		Block bloc1=stc.choose(last,disc,profondeur+1);
		blocs.add(bloc1);
		HashSet<Statement> f1=bloc1.getFirstInstructions();
		Ensemble.addHashSet(disc,f1);
		Ensemble.addHashSet(first_instructions,f1);
		Ensemble.addHashSet(discard_future,bloc1.getDiscardFuture());
		int hb=bloc1.getHauteur()+1;
		if (hb>hauteur){hauteur=hb;}
		size+=bloc1.getSize();
		nb_instructions+=bloc1.getNbInstructions();
		
		//copypile=copyPile(pile);
		Block bloc2=stc.choose(last,disc,profondeur+1);
		blocs.add(bloc2);
		HashSet<Statement> f2=bloc2.getFirstInstructions();
		Ensemble.addHashSet(disc,f2);
		Ensemble.addHashSet(first_instructions,f2);
		Ensemble.addHashSet(discard_future,bloc2.getDiscardFuture());
		hb=bloc2.getHauteur()+1;
		if (hb>hauteur){hauteur=hb;}
		size+=bloc2.getSize();
		nb_instructions+=bloc2.getNbInstructions();
		
		nb=2;
		while(Math.random()<more){
			//copypile=copyPile(pile);
			Block blocn=stc.choose(last,disc,profondeur+1);
			blocs.add(blocn);
			HashSet<Statement> fn=blocn.getFirstInstructions();
			Ensemble.addHashSet(disc,fn);
			Ensemble.addHashSet(first_instructions,fn);
			Ensemble.addHashSet(discard_future,blocn.getDiscardFuture());
			hb=blocn.getHauteur()+1;
			if (hb>hauteur){hauteur=hb;}
			size+=blocn.getSize();
			nb_instructions+=blocn.getNbInstructions();
			nb++;
		}
		
		p=new double[nb];
		sum=0;
		conditions=new ArrayList<Etat_Prog>();
		for(int i=0;i<nb;i++){
			if (blocs.get(i).getVidePossible()==1){
				vide_possible=1;
			}
			p[i]=Math.random();
			sum+=p[i];
			conditions.add(new Etat_Prog());
		}
	}
	protected void genereBiasedTrace(Trace trace){
		//System.out.println("alt");
		if (nb<2){
			throw new RuntimeException("Alt doit contenir au moins deux blocs (sinon utiliser Opt). Bloc = "+this);
		}
		double x=Math.random()*sum;
		int i=0;
		while((x>=0) && (i<nb)){
			x=x-p[i];
			i++;
		}
		i--;
		blocs.get(i).genereBiasedTrace(trace);
	}
	protected void genereBiasedTrace(Trace trace,Etat_Prog etat){
		if (nb<2){
			throw new RuntimeException("Alt doit contenir au moins deux blocs (sinon utiliser Opt). Bloc = "+this);
		}
		//System.out.println("alt");
		//ArrayList<Double> scores=new ArrayList<Double>();
		double max=-1000;
		int imax=0;
		int i=0;
		for(Etat_Prog con:conditions){
			double score=etat.cosine(con.getVec());
			//System.out.println("score "+i+"="+score);
			if ((i==0) || (score>max)){
				max=score;
				imax=i;
			}
			i++;
		}
		//System.out.println("imax = "+imax);
		blocs.get(imax).genereBiasedTrace(trace,etat);
	}
	protected void genereTrace(Trace trace){
		if (nb<2){
			throw new RuntimeException("Alt doit contenir au moins deux blocs (sinon utiliser Opt). Bloc = "+this);
		}
		//System.out.println("alt");
		int x=(int)(Math.random()*nb);
		blocs.get(x).genereTrace(trace);
	}
	public List<Regexp> getExp(){
		if (nb<2){
			throw new RuntimeException("Alt doit contenir au moins deux blocs (sinon utiliser Opt). Bloc = "+this);
		}
		List<Regexp> l = new ArrayList<Regexp>();
		for(int i=0;i<nb;i++){
		   l.add(blocs.get(i));
		}
		return(l);
	}
	public String toString(){
		
		String tabs="";
		for(int i=1;i<profondeur;i++){
			tabs+="\t";
		}
		String ret="";
		String bref="";
		if (ref!=null){
			bref=" ref="+ref.id_bloc;
		}
		ret+=tabs+"Alt id="+id_bloc+bref+"\n";
		for(int i=0;i<nb;i++){
			if (i==nb-1){
		        ret+=tabs+"Else p="+(p[i]/sum)+":\n";
			}
			else{
				ret+=tabs+"Cond "+(i+1)+" p="+(p[i]/sum)+":\n";
			}
			ret+=blocs.get(i);
		}
		ret+=tabs+"End Alt"+"\n";
		return(ret);
	}
	
	public Block copie(int prof){
		
	    Alt copie=new Alt();
	    copie.profondeur=prof;
	    copie.discard_future=Ensemble.copyHashSet(discard_future);
	    copie.first_instructions=Ensemble.copyHashSet(first_instructions);
	    copie.vide_possible=vide_possible;
	    copie.ref=this;
	    copie.hauteur=hauteur;
	    copie.nb_instructions=nb_instructions;
	    copie.size=size;
	    copie.nb=nb;
	    copie.p=new double[nb];
	    copie.blocs=new ArrayList<Block>();
	    for(int i=0;i<nb;i++){
	    	copie.p[i]=p[i];
	    	copie.blocs.add(blocs.get(i).copie(prof+1));
	    }
	    copie.sum=sum;
	    copie.conditions=conditions;
		return(copie);	
	}
	/*public void setBlocs(ArrayList<Bloc> blocs) {
		// TODO Auto-generated method stub
		this.blocs=blocs;
		initProbasAndConditions();
	}*/
	
	private void initProbasAndConditions(){
		this.nb=blocs.size();
		/*if (nb<2){
			System.out.println("Alt doit contenir au moins deux blocs (sinon utiliser Opt)");
		}*/
		p=new double[nb];
		sum=0.0;
		conditions=new ArrayList<Etat_Prog>();
		for(int i=0;i<nb;i++){
			p[i]=1.0;
			sum++;
			conditions.add(new Etat_Prog());
		}
	}
	public void add(Block b) {
		
		if (b.affecte){
			Block b2=b.copie(profondeur+1);
			b=b2;
		}
		else{
			b.affecte=true;
		}
		this.blocs.add(b);
		initProbasAndConditions();
	}
	
	
	
	/*public HashSet<String> getFirstInstructions(){
		HashSet<String> ret=new HashSet<String>();
		for(int i=0;i<nb;i++){
			HashSet<String> fi=blocs.get(i).getFirstInstructions();
			Iterator<String> it=fi.iterator();
			while(it.hasNext()){
				ret.add(it.next());
			}
		}
		return(ret);
	}*/
	protected void setProfondeur(int prof){
		if (blocs.size()<2){
			throw new RuntimeException("Alt doit contenir au moins deux blocs (sinon utiliser Opt). Bloc = "+this);
		}
		profondeur=prof;
		for(Block b:blocs){
			b.setProfondeur(prof+1);
		}
	}
	
	
	
	public FSA buildFSA(){
		/*if (ref!=null){
			lts=ref.lts;
		}
		else{*/
			if (blocs.size()<2){
				throw new RuntimeException("Alt doit contenir au moins deux blocs (sinon utiliser Opt)");
			}
			fsa=new FSA();
			
			State source=new State();
			State target=new State();
			fsa.setInitialState(source);
			fsa.setFinalState(target);
			fsa.addState(source);
			fsa.addState(target);
			State init=null;
			State fin=null;
			int i=0;
			
			for(Block b:blocs){
				b.buildFSA();
				FSA blts=b.fsa;
				init=blts.getInitialState();
				fin=blts.getFinalState();
				fsa.addStates(blts.getStates());
				fsa.addTransitions(blts.getTransitions());
				Transition tr=new Transition(source,new Statement(),init);
				fsa.addTransition(tr);
				Transition tr2=new Transition(fin,new Statement(),target);
				fsa.addTransition(tr2);
				
				i++;
			}
			return fsa;
			
		//}
	}
	
	protected void old_buildLTS(){
		if (nb<2){
			throw new RuntimeException("Alt doit contenir au moins deux blocs (sinon utiliser Opt)");
		}
		if (ref!=null){
			fsa=ref.fsa;
		}
		else{
			fsa=new FSA();
			
			State source=new State();
			State target=new State();
			fsa.setInitialState(source);
			fsa.setFinalState(target);
			fsa.addState(source);
			fsa.addState(target);
			State init=null;
			State fin=null;
			int i=0;
			
			for(Block b:blocs){
				b.buildFSA();
				FSA blts=b.fsa;
				
				init=blts.getInitialState();
				fin=blts.getFinalState();
				fsa.addStates(blts.getStates());
				fsa.addTransitions(blts.getTransitionsCopy());
				
				fsa.replace(init, source);
				fsa.replace(fin, target);
				i++;
			}
			
		}
	}
	public void computeNbBlocks(HashMap<String,Integer> nbs, HashSet<String> calls){
		Integer n=nbs.get("Alt");
		n=(n==null)?0:n;
		nbs.put("Alt",n+1);
		if(this.ref==null){
			n=nbs.get("uniqueAlt");
			n=(n==null)?0:n;
			nbs.put("uniqueAlt",n+1);
			
		}
		for(Block b:blocs){
			b.computeNbBlocks(nbs, calls);
		}
	}
}
