/*
 *  Block.java
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

import java.io.File;

import traces.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.io.Serializable;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;

import dataGenerator.Etat_Prog;
import fsa.*;
import regexp.Regexp;
import traces.ObjectInstance;

/**
 * 
 * The basic brick of the program structure.
 * 
 * Represents a part of code of the container program. 
 * 
 * @author Sylvain Lamprier
 *
 */
public abstract class Block implements Serializable,Regexp{
	private static final long serialVersionUID = 1L; 
	public static int nb_blocs=0;
	protected int id_bloc;
	protected int vide_possible; // -1 => pas encore determine, 0 => non ,  1 => possible qu'aucune instruction ne sorte de sa trace 
	protected HashSet<Statement> first_instructions;
	protected HashSet<Statement> discard_future;
	protected int profondeur;  // niveau par rapport a la racine (racine est a 1)
	protected int hauteur;  // taille de la branche la plus longue
	protected int size; // nombre de blocs de l'arbre enracine en ce bloc
	protected int nb_instructions; // nombre d'instructions sous ce bloc 
	protected Block ref;  // Si copie d'un bloc existant => reference a ce bloc (sinon null)
	protected transient FSA fsa; // Pour construction FSA
	protected boolean affecte; // true si deja affecte a une structure
	//public static int serialCount=0;
	public Block(int profondeur){
		affecte=false;
		vide_possible=-1;
		first_instructions=new HashSet<Statement>();
		discard_future=new HashSet<Statement>();
		nb_blocs++;
		id_bloc=nb_blocs;
		this.profondeur=profondeur;
		hauteur=1;
		size=1;
		nb_instructions=0;
		ref=null;
	}
	public Block(){
		this(0);
	}
	public abstract Block copie(int prof);
	
	public Regexp copy(){
		return copie(0);
	}
	
	public int getHauteur(){
		return(hauteur);
	}
	public int getSize(){
		return(size);
	}
	
	public int getNbInstructions(){
		return(nb_instructions);
	}
	
	protected abstract void genereTrace(Trace trace);
	
	protected abstract void genereBiasedTrace(Trace trace);
	
	protected abstract void genereBiasedTrace(Trace trace,Etat_Prog etat);
	
	// Pour eviter non determinisme
	public HashSet<Statement> getFirstInstructions(){ 
		return(first_instructions);
	}
	public HashSet<Statement> getDiscardFuture(){ 
		return(discard_future);
	}
	
	public int getVidePossible(){
		return(vide_possible);
	}
	
	public abstract FSA buildFSA();
	
	//public static int counter = 0;
	
	
	
	
	protected abstract void setProfondeur(int prof);
	
	
	
	// copie de surface de la pile
	public ArrayList<ObjectInstance> copyPile(ArrayList<ObjectInstance> pile){
		
		ArrayList<ObjectInstance> newpile=new ArrayList<ObjectInstance>();
		for(int i=0;i<pile.size();i++){
			newpile.add(pile.get(i));
		}
		return(newpile);
	}
	
	
	/*private void writeObject(ObjectOutputStream out) throws IOException {
		// appel des mecanismes de serialisation par defaut
		out.defaultWriteObject();
		System.out.println("Classe bloc serial = "+this.getClass());
		// on incremente notre compteur de serialisation
		serialCount++;
		
	}*/
	
	
	/*public int hashCode(){
		System.out.println(this);	
		return(super.hashCode());
	}*/
	
	
	public abstract void  computeNbBlocks(HashMap<String,Integer> nbs, HashSet<String> calls);
	

}
