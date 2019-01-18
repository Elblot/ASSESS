/*
 *  Opt.java
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
import traces.ObjectInstance;

/**
 * 
 * Represents an optional bloc of a program.
 * 
 * @author Sylvain Lamprier
 *
 */
public class Opt extends Block implements Alternation {
	public static double popt=0.5; // Probability of every bloc to be executed (for not biased traces extraction)
	Block bloc=null;
	double p=0.5; // probability of the bloc to be executed (for biased traces extraction)
	Etat_Prog cond_exec;
	Etat_Prog cond_passe;
	/**
	 *  For copy and manual construction
	 * @param profondeur
	 */
	public Opt(int profondeur){
		this(null,profondeur);
	}
	
	/** 
	 * For manual construction
	 * @param b
	 * @param profondeur
	 */
	public Opt(Block b,int profondeur){
		super(profondeur);
		if (b!=null){
			setBloc(b);
		}
		cond_exec=new Etat_Prog();
		cond_passe=new Etat_Prog();
	}
	
	/** 
	 * For manual construction
	 * @param b
	 */
	public Opt(Block b){
		this(b,0);
	}
	
	/** 
	 * For manual construction
	 */
	public Opt(){
			this(0);
	}
		
	
	/** 
	 * For manual construction with probability of being executed (useful for biased traces extraction). 
	 * @param b
	 * @param profondeur
	 * @param w
	 */
	public Opt(Block b,int profondeur,double w){
		this(b,profondeur);
		p=w;
	}
	
	/** 
	 * For manual construction with probability of being executed (useful for biased traces extraction). 
	 * @param b
	 * @param w
	 */
	public Opt(Block b,double w){
		this(b,0,w);
	}
	
	/**
	 * For automatic generation. 
	 * @param stc
	 * @param last
	 * @param discard
	 * @param profondeur
	 * @throws VocabulaireVideException
	 */
	public Opt(StrBlocChoice stc,Actor last,HashSet<Statement> discard,int profondeur) throws VocabulaireVideException{
		super(profondeur);
		cond_exec=new Etat_Prog();
		cond_passe=new Etat_Prog();
		vide_possible=1;
		System.out.println("Opt");
		this.p=Math.random();
		//ArrayList<ObjectJava> copypile=copyPile(pile);
		bloc=stc.choose(last,discard,profondeur+1);
		first_instructions=bloc.getFirstInstructions();
		discard_future=Ensemble.union(discard,bloc.getDiscardFuture());
		Ensemble.addHashSet(discard_future,first_instructions);
		hauteur=bloc.getHauteur()+1;
		size=bloc.getSize()+1;
		nb_instructions=bloc.getNbInstructions();
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
	
	protected void genereTrace(Trace trace){
		//System.out.println("opt");
		if (bloc==null){
			throw new RuntimeException("Opt doit contenir un fils");
		}
		double x=Math.random();
		if(x<popt){
		   bloc.genereTrace(trace);
		}
	}
	protected void genereBiasedTrace(Trace trace){
		if (bloc==null){
			throw new RuntimeException("Opt doit contenir un fils");
		}
		//System.out.println("opt");
		double x=Math.random();
		if(x<p){
		   bloc.genereBiasedTrace(trace);
		}
	}
	protected void genereBiasedTrace(Trace trace,Etat_Prog etat){
		if (bloc==null){
			throw new RuntimeException("Opt doit contenir un fils");
		}
		//System.out.println("opt");
		double score_exec=etat.cosine(cond_exec.getVec());
		double score_passe=etat.cosine(cond_passe.getVec());
		if(score_exec>score_passe){
		   bloc.genereBiasedTrace(trace,etat);
		}
	}
	public List<Regexp> getExp(){
		if (bloc==null){
			throw new RuntimeException("Opt doit contenir un fils");
		}
		List<Regexp> l=new ArrayList<Regexp>();
		l.add(bloc);
		return(l);
	}
	
	public String toString(){
		if (bloc==null){
			throw new RuntimeException("Opt doit contenir un fils");
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
		ret+=tabs+"Opt id="+id_bloc+bref+" p="+p+"\n";
		ret+=""+bloc;
		ret+=tabs+"End Opt"+"\n";
		return(ret);
	}
	public Block copie(int prof){
		
	    Opt copie=new Opt(prof); 
	    copie.discard_future=Ensemble.copyHashSet(discard_future);
	    copie.first_instructions=Ensemble.copyHashSet(first_instructions);
	    copie.vide_possible=vide_possible;
	    copie.ref=this;
	    copie.hauteur=hauteur;
	    copie.nb_instructions=nb_instructions;
	    copie.size=size;
	    copie.p=p;
	    if (bloc!=null){
	    	copie.bloc=bloc.copie(profondeur+1);
	    }
	    copie.cond_exec=cond_exec;
	    copie.cond_passe=cond_passe;
		return(copie);	
	}
	protected void setProfondeur(int prof){
		if (bloc==null){
			throw new RuntimeException("Opt doit contenir un fils");
		}
		profondeur=prof;
		bloc.setProfondeur(prof+1);
	}
	
	public FSA buildFSA(){
		if (bloc==null){
			throw new RuntimeException("Opt doit contenir un fils");
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
			Transition tr3=new Transition(source,new Statement(),target);
			fsa.addTransition(tr3);
			
			return fsa;
		//}
	}
	public void computeNbBlocks(HashMap<String,Integer> nbs, HashSet<String> calls){
		Integer n=nbs.get("Opt");
		n=(n==null)?0:n;
		nbs.put("Opt",n+1);
		if(this.ref==null){
			n=nbs.get("uniqueOpt");
			n=(n==null)?0:n;
			nbs.put("uniqueOpt",n+1);
			
		}
		if(bloc!=null){
			bloc.computeNbBlocks(nbs, calls);
		}
	}
}
