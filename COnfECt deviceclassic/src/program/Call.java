/*
 *  Call.java
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

import java.util.Iterator;
import java.util.List;

import core.Ensemble;
import dataGenerator.Etat_Prog;
import dataGenerator.StrBlocChoice;
import dataGenerator.VocabulaireVideException;
import fsa.*;
import traces.Statement;


import regexp.Regexp;
import regexp.Concatenation;


/**
 * 
 * The call of a method.
 * It gathers the opening, the bloc representing eventual sub-blocs of the methd and the closing of the method.
 * 
 * @author Sylvain Lamprier
 *
 */
public class Call extends Block implements Concatenation {
	private static final long serialVersionUID = 1L;
	
	public static double pfils=0.3; // Probabilite que bloc soit != de null
	protected Instruction call;
	protected Block bloc=null;    // Bloc fils
	protected Instruction closing_call;
	public static boolean setClosing=true;
	
	 
	// Pour methode copie
	public Call(int profondeur){
		super(profondeur);
	}
	
	// Pour construction manuelle(avec fils)
	public Call(Statement s,Block fils,int profondeur,boolean close){
		super(profondeur);
		call=new Instruction(s.getCopy(false));
		if (setClosing){
			closing_call=new Instruction(s.getCopy(true));
		}
		if (fils!=null){
			setBloc(fils);
		}
	}
		
	
	// Pour construction manuelle(avec fils)
	public Call(Statement s,Block fils,int profondeur){
		this(s,fils,profondeur,setClosing);
	}
	
	// Pour construction manuelle(avec fils)
	public Call(Statement s,Block fils,boolean close){
			this(s,fils,0,close);
	}
	
	// Pour construction manuelle(avec fils)
	public Call(Statement s,Block fils){
		this(s,fils,0,setClosing);
	}
	
	// Pour construction manuelle(sans fils)
	public Call(Statement s,int profondeur){
		this(s,null,profondeur,setClosing);
	}
	
	
	// Pour construction manuelle(sans fils)
	public Call(Statement s,boolean close){
			this(s,null,0,close);
	}
	
	// Pour construction manuelle(sans fils)
	public Call(Statement s){
		this(s,null,0,setClosing);
	}
	
	// Pour generation automatique
	public Call(StrBlocChoice stc,Actor last,HashSet<Statement> discard,int profondeur,int deterministe) throws VocabulaireVideException{
		this(stc,last,discard,profondeur,true,deterministe);
	}
	// Pour generation automatique
	public Call(StrBlocChoice stc,Actor last,HashSet<Statement> discard,int profondeur,boolean fils_allowed,int deterministe) throws VocabulaireVideException{
		super(profondeur);
		hauteur=1;
		size=1;
		nb_instructions=2;
		double pbloc=Math.random();
		boolean abloc=false;
		if (pbloc<pfils){
			abloc=true;
		}
		if (!fils_allowed){abloc=false;}
		Statement st;
		HashSet<Statement> vocabulaire=getVocabulaire(last);
		HashSet<Statement> valide=vocabulaire;
		if (deterministe==1){
		   valide=Ensemble.minus(vocabulaire,discard);
		}
		if (valide.size()>0){
			
			
			
			
			int x=(int)(Math.random()*valide.size());
			Iterator<Statement> it=valide.iterator();
			int i=0;
			while(i<x){
				it.next();
				i++;
			}
			st=it.next();
			//call=new Instruction(st.getCopy(false));
			//closing_call=new Instruction(st.getCopy(true));
			call=new Instruction(st);
			if (setClosing){
				closing_call=new Instruction(st.getClosingStatement());
			}
			System.out.println("Call : "+call.getStatement());
			
			if (abloc){
				Actor receiver=(Actor)st.getReceiver();
				bloc=stc.choose(receiver,new HashSet<Statement>(),profondeur+1);
				nb_instructions+=bloc.getNbInstructions();
				hauteur=bloc.getHauteur()+1;
				size+=bloc.getSize();
			}
			
			vide_possible=0;
			first_instructions.add(call.getStatement());
			if (setClosing){
				System.out.println("End call : "+closing_call.getStatement());
			}
			else{
				System.out.println("End call : "+call.getStatement());
			}
		}
		else{
			throw new VocabulaireVideException("Plus aucune instruction valide pour assurer le determinisme du diagramme");
		}
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
	
	
	public HashSet<Statement> getVocabulaire(ObjectInstance sender){
		HashSet<Statement> voc=new HashSet<Statement>();
		ArrayList<Statement> st=((Actor)sender).getPossibleStatements();
		for(int i=0;i<st.size();i++){
				voc.add(st.get(i));
		}
		return(voc);
	}
	
	
	protected void genereTrace(Trace trace){
		//System.out.println(call.getStatement());
		trace.add(call.getStatement());
		if (bloc!=null){
			bloc.genereTrace(trace);
		}
		//System.out.println(closing_call.getStatement());
		if (closing_call!=null){
			trace.add(closing_call.getStatement());
		}
	}
	protected void genereBiasedTrace(Trace trace){
		//System.out.println(call.getStatement());
		trace.add(call.getStatement());
		if (bloc!=null){
			bloc.genereBiasedTrace(trace);
		}
		//System.out.println(closing_call.getStatement());
		if (closing_call!=null){
			trace.add(closing_call.getStatement());
		}
	}
	protected void genereBiasedTrace(Trace trace,Etat_Prog etat){
		//System.out.println(call.getStatement());
		trace.add(call.getStatement());
		call.appliqueEffet(etat);
		if (bloc!=null){
			bloc.genereBiasedTrace(trace,etat);
		}
		//System.out.println(closing_call.getStatement());
		if (closing_call!=null){
			trace.add(closing_call.getStatement());
			closing_call.appliqueEffet(etat);
		}
	}
	public HashSet<Statement> getFirstInstructions(){
		HashSet<Statement> ret=new HashSet<Statement>();
		ret.add(call.getStatement());
		return(ret);
	}
	public List<Regexp> getExp(){
		List<Regexp> l=new ArrayList<Regexp>();
		l.add(call);
		if (bloc!=null){
			l.add(bloc);
		}
		if (closing_call!=null){
			l.add(closing_call);
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
			ret+=tabs+"Call id="+id_bloc+bref+" call="+call.getStatement().toString()+"\n";
			if (bloc!=null){
				ret+=bloc;
			}
			ret+=tabs+"Closing Call id="+id_bloc+bref+"\n";
			return(ret);
	}
		
	public Block copie(int prof){
	    Call copie=new Call(prof); 
	    copie.discard_future=Ensemble.copyHashSet(discard_future);
	    copie.first_instructions=Ensemble.copyHashSet(first_instructions);
	    copie.vide_possible=vide_possible;
	    copie.hauteur=hauteur;
	    copie.nb_instructions=nb_instructions;
	    copie.size=size;
	    copie.ref=this;
	    copie.call=call;
	    if (bloc!=null){
	    	copie.bloc=bloc.copie(prof+1);
	    }
	    copie.closing_call=closing_call;
		return(copie);	
	}
	
	protected void setProfondeur(int prof){
		profondeur=prof;
		if (bloc!=null){
		   bloc.setProfondeur(prof+1);
		}
	}
	
	
	public FSA buildFSA(){
		
		/*if (ref!=null){
			lts=ref.lts;
		}
		else{*/
			fsa=new FSA();
			
			State source=new State();
			State afterCall=null; 
			State target=new State();
			
			Transition tr=null;  
			Transition tr2=null; 
			if (bloc==null){
					afterCall=new State();
					fsa.addState(afterCall);
					tr=new Transition(source,call.getStatement(),afterCall);
					if(closing_call!=null){
						tr2=new Transition(afterCall,closing_call.getStatement(),target);
					}
					else{
						//System.out.println("ha");
						tr2=new Transition(afterCall,new Statement(),target);
					}
			}
			else{
				bloc.buildFSA();
				FSA blts=bloc.fsa;
				afterCall=blts.getInitialState();
				tr=new Transition(source,call.getStatement(),afterCall);
				if(closing_call!=null){
					tr2=new Transition(blts.getFinalState(),closing_call.getStatement(),target);
				}
				else{
					//System.out.println("ha");
					tr2=new Transition(blts.getFinalState(),new Statement(),target);
				}
				fsa.addStates(blts.getStates());
				fsa.addTransitions(blts.getTransitions());
			}
			fsa.addTransition(tr);
			fsa.addTransition(tr2);
			fsa.addState(source);
			fsa.addState(target);
			fsa.setInitialState(source);
			fsa.setFinalState(target);
			return fsa;
		//}
	}
	
	public void computeNbBlocks(HashMap<String,Integer> nbs, HashSet<String> calls){
		Integer n=nbs.get("Call");
		n=(n==null)?0:n;
		nbs.put("Call",n+1);
		calls.add(call.getStatement().toString());
		if(this.ref==null){
			n=nbs.get("uniqueCall");
			n=(n==null)?0:n;
			nbs.put("uniqueCall",n+1);
			
		}
		if(bloc!=null){
			bloc.computeNbBlocks(nbs, calls);
		}
	}
	
	
}