/*
 *  BlocChoice.java
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

import core.Ensemble;

import program.Actor;
import program.Alt;
import program.Block;
import program.BlockList;
import program.Call;
import program.Loop;
import program.Opt;


public class BlocChoice extends StrBlocChoice {
	
	
	/*BlocChoice1(ArrayList<Actor> objets,ArrayList<Bloc> existing,double pi,double pe,double pc,double pa,double pl,double po,int max_profondeur,int deterministe){
		super(objets,existing,pi,pe,pc,pa,pl,po,max_profondeur,deterministe);
	}*/
	BlocChoice(ArrayList<Actor> objets,double pi,double pe,double pc,double pa,double pl,double po,int max_profondeur,int deterministe){
		super(objets,pi,pe,pc,pa,pl,po,max_profondeur,deterministe);
	}
	public Block choose(Actor last,HashSet<Statement> discard,int profondeur) throws VocabulaireVideException{
		Block ret=null;
		
		double x=Math.random()*sum;
		if((x<pi)||(profondeur==max_prof)){
			ret=new Call(this,last,discard,profondeur,(profondeur<max_prof),deterministe);
		}
		else if(x<(pe+pi)){
			ArrayList<Block> existing=last.getExistingBlocs(); 
			if (existing.size()>0){
				int select=(int)(Math.random()*existing.size());
				System.out.println("Existing "+select);
				ret=existing.get(select);
				if ((deterministe==1) && (Ensemble.intersection(discard,ret.getFirstInstructions()).size()>0)){
					System.out.println("Existing Interdit, on choisit autre chose");
					//ret=new Call(this,last,discard,profondeur,deterministe);
					ret=choose(last,discard,profondeur);
				}
				else{
					ret=ret.copie(profondeur);
				}
			}
			else{
				  ret=choose(last,discard,profondeur);
			}
			
		}
        else if(x<(pe+pi+pc)){
        	ret=new BlockList(this,last,discard,profondeur);
		}
        else if(x<(pe+pi+pc+pa)){
			ret=new Alt(this,last,discard,profondeur);
		}
        else if(x<(pe+pi+pc+pa+pl)){
        	ret=new Loop(this,last,discard,profondeur);
		}
        else {
        	ret=new Opt(this,last,discard,profondeur);
		}
		last.addBloc(ret);
		return(ret);
	}
}
