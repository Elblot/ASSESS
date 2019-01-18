/*
 *  BlocList.java
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
import java.util.HashSet;

import traces.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import core.Ensemble;
import dataGenerator.Etat_Prog; 
import dataGenerator.StrBlocChoice;
import dataGenerator.VocabulaireVideException;
import fsa.FSA;
import fsa.State;
import fsa.Transition;


import regexp.Concatenation;
import regexp.Regexp;

/**
 * 
 * A concatenation bloc of various sub-blocs of program.
 * 
 * @author Sylvain Lamprier
 *
 */
public class BlockList extends Block implements Concatenation{
	private static final long serialVersionUID = 1L;
	public static int nb_max_concat=10; // Nombre maximal de blocs
	ArrayList<Block> blocs;
	protected int nb_min=2;
	
	// Pour methode copie
	public BlockList(int profondeur){
		super(profondeur);
		blocs = new ArrayList<Block>();
	}
	
	// Pour construction manuelle
	public BlockList(ArrayList<Block> blocs,int profondeur){
		super(profondeur);
		this.blocs=blocs;
	}
	
	// Pour construction manuelle
	public BlockList(){
		this(0);
	}
	
	// Pour construction manuelle
	public BlockList(ArrayList<Block> blocs){
		this(blocs,0);
	}
	
	// Pour generation automatique
	public BlockList(StrBlocChoice stc,Actor last,HashSet<Statement> discard,int profondeur) throws VocabulaireVideException{
		super(profondeur);
		size=1;
		int nb=(int)(Math.random()*nb_max_concat)+1;
		if (nb<2){nb=2;}
		blocs=new ArrayList<Block>();
		vide_possible=1;
		HashSet<Statement> disc=discard;
		for(int i=0;i<nb;i++){
			Block newb=stc.choose(last,disc,profondeur+1);
			blocs.add(newb);
			HashSet<Statement> fn=newb.getFirstInstructions();
			if (vide_possible==1){
				Iterator<Statement> it=fn.iterator();
				while(it.hasNext()){
					first_instructions.add(it.next());
				}
			}
			
			int nvidep=newb.getVidePossible();
			if (nvidep==0){
				vide_possible=0;
				disc=new HashSet<Statement>();
			}
			/*else{
				disc=union(disc,fn);
			}*/
			Ensemble.addHashSet(disc,newb.getDiscardFuture());
			
			int hb=newb.getHauteur()+1;
			if (hb>hauteur){hauteur=hb;}
			size+=newb.getSize();
			nb_instructions+=newb.getNbInstructions();
		}
		discard_future=disc;
	}
	public void add(Block b){
		if (b.affecte){
			Block b2=b.copie(profondeur+1);
			b=b2;
		}
		else{
			b.affecte=true;
		}
		
		this.blocs.add(b);
	}
	protected void genereTrace(Trace trace){
		if (blocs.size()<nb_min){
			throw new RuntimeException("BlocList doit contenir au moins "+nb_min+" blocs. Bloc = "+this);
		}
		//System.out.println("list");
		for(int i=0;i<blocs.size();i++){
			blocs.get(i).genereTrace(trace);
		}
	}
	protected void genereBiasedTrace(Trace trace){
		if (blocs.size()<nb_min){
			throw new RuntimeException("BlocList doit contenir au moins "+nb_min+" blocs. Bloc = "+this);
		}
		//System.out.println("list");
		for(int i=0;i<blocs.size();i++){
			blocs.get(i).genereBiasedTrace(trace);
		}
	}
	protected void genereBiasedTrace(Trace trace,Etat_Prog etat){
		if (blocs.size()<nb_min){
			throw new RuntimeException("BlocList doit contenir au moins "+nb_min+" blocs. Bloc = "+this);
		}
		//System.out.println("list");
		for(int i=0;i<blocs.size();i++){
			blocs.get(i).genereBiasedTrace(trace,etat);
		}
	}
	public List<Regexp> getExp(){
		if (blocs.size()<nb_min){
			throw new RuntimeException("BlocList doit contenir au moins "+nb_min+" blocs. Bloc = "+this);
		}
		List<Regexp> l=new ArrayList<Regexp>();
		for(int i=0;i<blocs.size();i++){
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
		ret+=tabs+"BlocList id="+id_bloc+bref+"\n";
		for(int i=0;i<blocs.size();i++){
			ret+=blocs.get(i);
		}
		ret+=tabs+"End BlocList"+"\n";
		return(ret);
	}
	public Block copie(int prof){
		
	    BlockList copie=new BlockList(prof); 
	    copie.discard_future=Ensemble.copyHashSet(discard_future);
	    copie.first_instructions=Ensemble.copyHashSet(first_instructions);
	    copie.vide_possible=vide_possible;
	    copie.ref=this;
	    copie.hauteur=hauteur;
	    copie.nb_instructions=nb_instructions;
	    copie.size=size; 
	    copie.blocs=new ArrayList<Block>();
	    copie.nb_min=nb_min;
	    for(int i=0;i<blocs.size();i++){
	    	copie.blocs.add(blocs.get(i).copie(prof+1));
	    }
		return(copie);	
	}
	protected void setProfondeur(int prof){
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
		if (blocs.size()<nb_min){
			throw new RuntimeException("BlocList doit contenir au moins "+nb_min+" blocs. Bloc = "+this);
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
			
			for(Block b:blocs){
				b.buildFSA();
				FSA blts=b.fsa;
				init=blts.getInitialState();
				fin=blts.getFinalState();
				fsa.addStates(blts.getStates());
				fsa.addTransitions(blts.getTransitions());
				Transition tr=new Transition(source,new Statement(),init);
				fsa.addTransition(tr);
				source=fin;
				
			}
			Transition tr=new Transition(source,new Statement(),target);
			fsa.addTransition(tr);
			return fsa;
		//}
	}
	
	protected void old_buildLTS(){
		if (blocs.size()<nb_min){
			throw new RuntimeException("BlocList doit contenir au moins "+nb_min+" blocs. Bloc = "+this);
		}
		if (ref!=null){
			fsa=ref.fsa;
		}
		else{
			fsa=new FSA();
			
			State source=null;
			State target=null;
			State old_target=null;
			int i=0;
			
			for(Block b:blocs){
				old_target=target;
				b.buildFSA();
				FSA blts=b.fsa;
				source=blts.getInitialState();
				target=blts.getFinalState();
				
				
				if (i==0){
					fsa.setInitialState(source);
				}
				else{
					fsa.replace(old_target, source);
				}
				
				if (i==blocs.size()-1){
					fsa.setFinalState(target);
				}
				
				fsa.addStates(blts.getStates());
				fsa.addTransitions(blts.getTransitionsCopy());
				i++;
			}
		}
	}
	
	public void computeNbBlocks(HashMap<String,Integer> nbs, HashSet<String> calls){
		Integer n=nbs.get("BlockList");
		n=(n==null)?0:n;
		nbs.put("BlockList",n+1);
		if(this.ref==null){
			n=nbs.get("uniqueBlockList");
			n=(n==null)?0:n;
			nbs.put("uniqueBlockList",n+1);
			
		}
		for(Block b:blocs){
			b.computeNbBlocks(nbs, calls);
		}
	}
}
