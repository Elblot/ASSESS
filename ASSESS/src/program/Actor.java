/*
 *  Actor.java
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

import traces.*;

import java.util.ArrayList;

/**
 * 
 * Objects that are manipulated during the program generation process.
 * 
 * @author Sylvain Lamprier
 *
 */
public class Actor extends ObjectInstance{
	private static final long serialVersionUID = 1L;
	protected transient ArrayList<Block> existing_blocs;
	private transient ArrayList<Statement> possibles; // Pour la generation aleatoire de SD, permet d'enregistrer tous les Statements que cet objet peut emettre
	//private ArrayList<Etat_Prog> effets; // Effets des statements sur l etat du programme
	
	public Actor(String name, ObjectClass classJava){
		super(name,classJava);
		existing_blocs=new ArrayList<Block>();
		possibles=new ArrayList<Statement>();
	}
	public void reinit(){
		existing_blocs=new ArrayList<Block>();
	}
	public ArrayList<Block> getExistingBlocs(){
		return(existing_blocs);
	}
	public void addBloc(Block b){
		existing_blocs.add(b);
	}
	/*public String toString(){
		return(toString2());  
	}*/
	public void addPossibleStatement(Statement s){
		possibles.add(s);
	}
	
	public ArrayList<Statement> getPossibleStatements(){
		return(possibles);
	}
}
