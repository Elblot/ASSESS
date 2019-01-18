/*
 *  StrBlocChoice.java
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


package dataGenerator;

import java.util.ArrayList;


import traces.*;
import java.util.HashSet;

import program.Actor;
import program.Block;

public abstract class StrBlocChoice {
	   //protected ArrayList<Bloc> existing;  //Blocs deja existants
	   //protected HashSet<String> vocabulaire;  //Liste instructions possibles
	   protected ArrayList<Actor> objets; // Liste des objets a manipuler
	   double pe; //proba de choisir un bloc existant 
	   double pi; //proba de produire une instruction
	   double pc; // proba produire bloc concatenation
	   double pa; // proba produire bloc alt
	   double pl; //proba produire bloc loop
	   double po; // proba produire bloc opt
	   double sum; // somme de tout ca (ce ne sont pas forcemment des probas mais des quantites permettant de simuler des probas)
	   int max_prof; // profondeur maximale (si < 0, pas de prof max)
	   int deterministe; // 1 => sd determinant 
	   public StrBlocChoice(ArrayList<Actor> objets,double pi,double pe,double pc,double pa,double pl,double po,int max_profondeur,int deterministe){
			this.objets=objets;
		   this.pi=pi;
			this.pe=pe;
			this.pc=pc;
			this.pa=pa;
			this.pl=pl;
			this.po=po;
			sum=pe+pi+pc+pa+pl+po;
			max_prof=max_profondeur;
		    //this.existing=existing;
		    //this.vocabulaire=voc;
		    this.deterministe=deterministe;
	   }
	   public abstract Block choose(Actor last,HashSet<Statement> discard,int profondeur) throws VocabulaireVideException;
}
