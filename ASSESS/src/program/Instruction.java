/*
 *  Instruction.java
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
import java.io.Serializable;
import java.util.HashSet;

import traces.*;

import java.util.Iterator;

import dataGenerator.Etat_Prog;
import fsa.FSA;
import fsa.State;
import fsa.Transition;
import regexp.Literal;
import regexp.Regexp;

/**
 * 
 * Represents an opening or a closing statement of a method.
 * 
 * @author Sylvain Lamprier
 *
 */
public class Instruction implements Serializable,Literal {
	private static final long serialVersionUID = 1L; 
	protected Statement call;
	//protected int nb_vars;
	protected double[] effect;
	//protected double[] effect2; // Deuxieme effet permettant d'introduire du non determinisme dans le programme 
	//protected double p_ef2; // proba d'appliquer l'effet2 plutot que le premier
	protected static double p_random_effect=0.2;
	//protected double pmute;
	public Instruction(Statement s){ //,int nb_vars,double p_stoch){
		call=s;
		//this.nb_vars=nb_vars;
		effect=Etat_Prog.genereVec();
		//pmute=0.2;
	}
	
	/**
	 * Returns a  copy of this Instruction.
	 *  The statement is not copied. 
	 *  The effect table is a new vector with new values. 
	 */
	public Regexp copy(){
		return new Instruction(call);
	}
	
	@Override
	public Object getValue(){
		return(call);
	}
	
	@Override
	public void setValue(Object s){
		if(s instanceof Statement){
			call=(Statement)s;
		}
		else{
			//System.out.println("Not Allowed !");
			throw new RuntimeException("Setting the value as something different than a statement is not allowed ");
		}
	}
	public Statement getStatement(){
		return(call);
	}
	public void appliqueEffet(Etat_Prog e){
		double p=Math.random();
		if (p<p_random_effect){
			e.addVec((new Etat_Prog()).getVec());
		}
		else{
		    e.addVec(effect);
		}
		e.normalize();
		
	}
	public Instruction(Instruction inst){
		this.call=inst.call;
		this.effect=inst.effect;
	}
	
	public FSA buildFSA(){
		FSA fsa=new FSA();
		State source=new State();
		State target=new State();
		fsa.setInitialState(source);
		fsa.setFinalState(target);
		fsa.addState(source);
		fsa.addState(target);
		Transition tr=new Transition(source,call,target);
		fsa.addTransition(tr);
		return fsa;
	}
	
}
