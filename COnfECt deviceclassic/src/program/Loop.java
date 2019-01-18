/*
 *  Loop.java
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

import core.Ensemble;
import dataGenerator.Etat_Prog;
import dataGenerator.StrBlocChoice;
import dataGenerator.VocabulaireVideException;
import fsa.FSA;
import fsa.State;
import fsa.Transition;



import regexp.KleeneStar;
import regexp.Regexp;
import traces.ObjectInstance;


/**
 *  Represents a loop of a program.
 * 
 * @author Sylvain Lamprier
 */
public class Loop extends Block implements KleeneStar {
	public static double ploop=0.5; // Proba de boucler (pour unbiased)
	public static double p_max_loop=0.9;  // proba max de boucler (pour biased afin d'eviter de choisir des probas trop proches de 1)
	public static int nb_max_loops=100;
	Block bloc=null;
	double p=0.5; //probabilite d'executer bloc (1-p => proba de sortie de boucle)
	//ArrayList<Etat_Prog> conditions;
	Etat_Prog conditions;
	//Etat_Prog cond_passe;
	
	// Pour methode copie
	public Loop(int profondeur){
		this(null,profondeur);
	}
	
	// Pour construction manuelle
	public Loop(Block b,int profondeur){
		super(profondeur);
		if (b!=null){
			setBloc(b);
		}
		creerConditions();
		//cond_exec=new Etat_Prog();
		//cond_passe=new Etat_Prog();
	}
	// Pour construction manuelle
	public Loop(Block b){
		this(b,0);
	}
	// Pour construction manuelle
	public Loop(){
			this(0);
	}
	
	// Pour construction manuelle avec proba (pour biased traces)
	public Loop(Block b,int profondeur,double w){
		this(b,profondeur);
		p=w;
	}
	// Pour construction manuelle avec proba (pour biased traces)
	public Loop(Block b,double w){
		this(b,0,w);
	}
	
	// Pour generation automatique
	public Loop(StrBlocChoice stc,Actor last,HashSet<Statement> discard,int profondeur)throws VocabulaireVideException{
		super(profondeur);
		//cond_exec=new Etat_Prog();
		//cond_passe=new Etat_Prog();
		creerConditions();
		System.out.println("Loop");
		this.p=Math.random();
		if (p>p_max_loop){p=p_max_loop;}
		//ArrayList<ObjectJava> copypile=copyPile(pile);
		bloc=stc.choose(last,discard,profondeur+1);
		first_instructions=bloc.getFirstInstructions();
		vide_possible=1; //bloc.getVidePossible();
		discard_future=Ensemble.union(discard,bloc.getDiscardFuture());
		Ensemble.addHashSet(discard_future,first_instructions);
		hauteur=bloc.getHauteur()+1;
		size=bloc.getSize()+1;
		nb_instructions=bloc.getNbInstructions();
	}
	
	void creerConditions(){
		conditions=new Etat_Prog();
	}
	
	public void setBloc(Block b){
		if (b.affecte){
			Block b2=b.copie(profondeur+1);
			b=b2;
		}
		else{
			b.affecte=true;
		}
		this.bloc=b;
	}
	
	/*protected void creerConditions(){
		conditions=new ArrayList<Etat_Prog>();
		conditions.add(new Etat_Prog());
		for(int i=1;i<nb_max_loops;i++){
			Etat_Prog ep=new Etat_Prog(conditions.get(i-1));
			ep.addVec(Etat_Prog.genereVec());
			//ep.normalize();
			//int x=((int)(Math.random()*ep.getNbVars()));
			//ep.setVar(x, ep.getVar(x)+(Math.random()*(Etat_Prog.sup-Etat_Prog.inf))+Etat_Prog.inf);
			conditions.add(ep);
		}
	}*/
	
	protected void genereBiasedTrace(Trace trace){
		if (bloc==null){
			throw new RuntimeException("Loop doit contenir un fils");
		}
		//System.out.println("Loop");
		//bloc.genereBiasedTrace();
		double x=Math.random();
		while(x<p){
		   bloc.genereBiasedTrace(trace);
		   x=Math.random();
		}
		//System.out.println("EndLoop");
		
	}
	/*protected void genereBiasedTrace(Trace trace,Etat_Prog etat){
		//System.out.println("opt");
		double score_exec=etat.cosine(conditions.get(0).getVec());
		//double score_passe=etat.dotProduct(cond_passe.getVec());
		int i=1;
		while((score_exec>=0) && (i<nb_max_loops)){
		   bloc.genereBiasedTrace(trace,etat);
		   score_exec=etat.cosine(conditions.get(i).getVec());
		   //score_passe=etat.cosine(cond_passe.getVec());
		   i++;
		}
		System.out.println((i-1)+" loops");
	}*/
	protected void genereBiasedTrace(Trace trace,Etat_Prog etat){
		if (bloc==null){
			throw new RuntimeException("Loop doit contenir un fils");
		}
		//System.out.println("opt");
		double score_exec=etat.cosine(conditions.getVec());
		//double score_passe=etat.dotProduct(cond_passe.getVec());
		int i=1;
		while((score_exec>=0) && (i<=nb_max_loops)){
		   bloc.genereBiasedTrace(trace,etat);
		   score_exec=etat.cosine(conditions.getVec());
		   //score_passe=etat.cosine(cond_passe.getVec());
		   i++;
		}
		//System.out.println((i-1)+" loops");
	}
	
	protected void genereTrace(Trace trace){
		if (bloc==null){
			throw new RuntimeException("Loop doit contenir un fils");
		}
		//System.out.println("Loop");
		//bloc.genereTrace();
		double x=Math.random();
		while(x<ploop){
		   bloc.genereTrace(trace);
		   x=Math.random();
		}
		//System.out.println("EndLoop");
		
	}
	
	public Regexp getExp(){
		if (bloc==null){
			throw new RuntimeException("Loop doit contenir un fils");
		}
		return(bloc);
	}
	public void setExp(Regexp r){
		System.out.println("Not Allowed!");
	}
	public String toString(){
		if (bloc==null){
			throw new RuntimeException("Loop doit contenir un fils");
		}
		String tabs="";
		for(int i=1;i<profondeur;i++){
			tabs+="\t";
		}
		String ret="";
		String bref="";
		if (ref!=null){
			bref=" ref="+ref.id_bloc;
		}
		ret+=tabs+"Loop id="+id_bloc+bref+" p="+p+"\n";
		ret+=""+bloc;
		ret+=tabs+"End Loop"+"\n";
		return(ret);
	}
	
	public Block copie(int prof){
		
	    Loop copie=new Loop(prof); 
	    copie.discard_future=Ensemble.copyHashSet(discard_future);
	    copie.first_instructions=Ensemble.copyHashSet(first_instructions);
	    copie.vide_possible=vide_possible;
	    copie.ref=this;
	    copie.hauteur=hauteur;
	    copie.nb_instructions=nb_instructions;
	    copie.size=size;
	    copie.p=p;
	    if (bloc!=null){
	    	copie.bloc=bloc.copie(prof+1);
	    }
	    //copie.cond_exec=cond_exec;
	    //copie.cond_passe=cond_passe;
		copie.conditions=conditions;
	    return(copie);	
	}
	protected void setProfondeur(int prof){
		profondeur=prof;
		bloc.setProfondeur(prof+1);
	}
	
	
	public FSA buildFSA(){
		if (bloc==null){
			throw new RuntimeException("Loop doit contenir un fils");
		}
		/*if (ref!=null){
			lts=ref.lts;
		}
		else{*/
			fsa=new FSA();
			
			State source=new State();
			State target=new State();
			
			fsa.setInitialState(source);
			fsa.setFinalState(target);
			fsa.addState(source);
			fsa.addState(target);
			State init=null;
			State fin=null;
			bloc.buildFSA();
			FSA blts=bloc.fsa;
			init=blts.getInitialState();
			fin=blts.getFinalState();
			fsa.addStates(blts.getStates());
			fsa.addTransitions(blts.getTransitions());
			Transition tr=new Transition(source,new Statement(),init);
			fsa.addTransition(tr);
			Transition tr2=new Transition(fin,new Statement(),target);
			fsa.addTransition(tr2);
			Transition tr3=new Transition(target,new Statement(),source);
			fsa.addTransition(tr3);
			Transition tr4=new Transition(source,new Statement(),target);
			fsa.addTransition(tr4);
			return fsa;
	//	}
	}
	
	public void computeNbBlocks(HashMap<String,Integer> nbs, HashSet<String> calls){
		Integer n=nbs.get("Loop");
		n=(n==null)?0:n;
		nbs.put("Loop",n+1);
		if(this.ref==null){
			n=nbs.get("uniqueLoop");
			n=(n==null)?0:n;
			nbs.put("uniqueLoop",n+1);
			
		}
		if(bloc!=null){
			bloc.computeNbBlocks(nbs, calls);
		}
	}
}
