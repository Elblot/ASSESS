/*
 *  NormalizeRegexp.java
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

package regexp.fromFSA;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import regexp.Alternation;
import regexp.AlternationImpl;
import regexp.Concatenation;
import regexp.ConcatenationImpl;
import regexp.KleeneStar;
import regexp.Regexp;

import java.util.HashSet;
public class NormalizeRegexp {

	boolean facto=false;

	public NormalizeRegexp(boolean factoBegin){
		this.facto=factoBegin;
	}
	public NormalizeRegexp(){
		this(false);
	}
	
	public Regexp transform(Regexp from) {
		return toNormalForm(from);
	}

	private Regexp toNormalForm(Regexp root) {
		//System.out.println("NORMALIZE: " + root.toString());
		if (root instanceof Alternation) {
			Alternation alt = (Alternation) root;
			List<Regexp> list = alt.getExp();
			
			if (list.size() == 1) { // (A) = A
				return toNormalForm(alt.getExp().get(0));
			}
			ArrayList<Regexp> toRemove = new ArrayList<Regexp>();
			ArrayList<Regexp> toAdd = new ArrayList<Regexp>();

			HashSet<String> dits=new HashSet<String>();
			for (int i = 0; i < list.size(); ++i) {
				Regexp exp = list.get(i);
				Regexp fn  = toNormalForm(exp);
				
				if (fn instanceof Alternation) { // ((A+B)+C) = (C+A+B)
					for (Regexp exp0 : ((Alternation)fn).getExp()) {
						String ch=RegexpExtractionUtils.reg2String(exp0);
						if(!dits.contains(ch)){
							toAdd.add(exp0);
						}
						dits.add(ch);
					}
					//toRemove.add(exp);					
				} /*else if (fn instanceof EmptySet) { // A+{} = A					
					toRemove.add(exp);					
				}*/ 
				else {
					//list.set(i, fn);
					String ch=RegexpExtractionUtils.reg2String(fn);
					if(!dits.contains(ch)){
						toAdd.add(fn);
					}
					dits.add(ch);
				}
			}
			
			list.clear(); //removeAll(toRemove);
			list.addAll(toAdd);
			
			/*if(true){
				return alt;
			}*/
			
			//System.out.println("A+A=A");
			
			HashMap<Regexp,HashSet<Regexp>> h=new HashMap<Regexp,HashSet<Regexp>>();
			if(facto){	
				// XA + YA = (X+Y)A 
				h=new HashMap<Regexp,HashSet<Regexp>>();
				for(Regexp r:alt.getExp()){
					if(r instanceof ConcatenationImpl){
						Regexp last=((ConcatenationImpl) r).getLast();
						/*Regexp remain=r;
						if(((ConcatenationImpl) r).getExp().size()==0){
							remain=new Epsilon();
						}*/
						
						HashSet<Regexp> hh=h.get(last);
						if(hh==null){
							hh=new HashSet<Regexp>();
							h.put(last, hh);
						}
						hh.add(r);
					}
					/*else if(r instanceof AlternationImpl){
						HashSet<Regexp> last=((AlternationImpl) r).getLast();
						for(Regexp l:last){
							HashSet<Regexp> hh=h.get(last);
							if(hh==null){
								hh=new HashSet<Regexp>();
								h.put(l, hh);
							}
							hh.add(r);
						}
					}*/
					else{
						HashSet<Regexp> hh=h.get(r);
						if(hh==null){
							hh=new HashSet<Regexp>();
							h.put(r, hh);
						}
						hh.add(r);
					}
				}
				/*if(h.size()==alt.getExp().size()){
					return alt;
				}*/
				if(h.size()!=alt.getExp().size()){
					ArrayList<Regexp> lc=new ArrayList<Regexp>();
					for(Entry<Regexp, HashSet<Regexp>> e:h.entrySet()){
						Regexp k=e.getKey();
						HashSet<Regexp> hh=e.getValue();
						ArrayList<Regexp> la;
						if(hh.size()==1){
							la=new ArrayList<Regexp>(hh);
							lc.add(toNormalForm(la.get(0)));
							continue;
						}
						//HashSet<Regexp> ha=new HashSet<Regexp>();
						la=new ArrayList<Regexp>();
						for(Regexp f:hh){
							Regexp re=null;
							if(f instanceof ConcatenationImpl){
								((ConcatenationImpl)f).removeLast();
								re=f;
							}
							else{
								re=new Epsilon();
							}
							la.add(re);
						}
						((AlternationImpl)alt).setExp(la);
						Concatenation conc=new ConcatenationImpl();
						List<Regexp> rs=conc.getExp();
						rs.add(alt);
						rs.add(k);
						lc.add(toNormalForm(conc));
					}
				
				
					Regexp nr=null;
					if(h.size()>1){
						nr=new AlternationImpl();
						((AlternationImpl) nr).setExp(lc);
					}
					else{
						nr=lc.get(0);
						return nr;
					}
					alt=(Alternation)nr;
				}
			
			
				// AX+AY = A(X+Y)
						
				
				// facto begin
				h=new HashMap<Regexp,HashSet<Regexp>>();
				for(Regexp r:alt.getExp()){
					if(r instanceof ConcatenationImpl){
						Regexp first=((ConcatenationImpl) r).getFirst();
						
						
						HashSet<Regexp> hh=h.get(first);
						if(hh==null){
							hh=new HashSet<Regexp>();
							h.put(first, hh);
						}
						hh.add(r);
					}
					else{
						HashSet<Regexp> hh=h.get(r);
						if(hh==null){
							hh=new HashSet<Regexp>();
							h.put(r, hh);
						}
						hh.add(r);
					}
				}
				if(h.size()!=alt.getExp().size()){
					
					ArrayList<Regexp> lc=new ArrayList<Regexp>();
					for(Entry<Regexp, HashSet<Regexp>> e:h.entrySet()){
						Regexp k=e.getKey();
						HashSet<Regexp> hh=e.getValue();
						ArrayList<Regexp> la;
						if(hh.size()==1){
							la=new ArrayList<Regexp>(hh);
							lc.add(toNormalForm(la.get(0)));
							continue;
						}
						//HashSet<Regexp> ha=new HashSet<Regexp>();
						la=new ArrayList<Regexp>();
						for(Regexp f:hh){
							Regexp re=null;
							if(f instanceof ConcatenationImpl){
								((ConcatenationImpl)f).removeFirst();
								re=f;
							}
							else{
								re=new Epsilon();
							}
							la.add(re);
						}
						((AlternationImpl)alt).setExp(la);
						Concatenation conc=new ConcatenationImpl();
						List<Regexp> rs=conc.getExp();
						rs.add(k);
						rs.add(alt);
						
						lc.add(toNormalForm(conc));
					}
				
					Regexp nr=null;
					if(h.size()>1){
						nr=new AlternationImpl();
						((AlternationImpl) nr).setExp(lc);
					}
					else{
						return lc.get(0);
						//return nr;
					}
					
					alt=(Alternation)nr;
				}
			//}
			
			
			}
			return alt;
			
			
			/*int i=0;
			while(i < list.size()){      // A + A = A
				Regexp exp = list.get(i);
				String st=RegexpExtractionUtils.reg2String(exp);
				//System.out.println(st);
				if (!dits.contains(st)){
					dits.add(st);
				}
				else{
					list.remove(i);
					i--;
				}
				i++;
			}*/
			
/*			if (!EcoreUtil.equals(root, alt)) {
				return toNormalForm(alt);				
			} else {	*/			
			//return reg;
			//}
		} else if (root instanceof Concatenation) {
			Concatenation con = ((Concatenation) root);
			List<Regexp> list = ((Concatenation) root).getExp();

			if (list.size() == 1) { // (A) = A
				return toNormalForm(con.getExp().get(0));
			}

			for (Regexp exp : list) { // A{} = {}
				if (exp instanceof EmptySet) {
					return exp;
				}
				
			}			
			
			// associate
			ArrayList<Regexp> newList = new ArrayList<Regexp>();
			for (int i = 0; i < list.size(); ++i) {
				Regexp exp = list.get(i);
				Regexp fn  = toNormalForm(exp);
				
				if (fn instanceof Concatenation) { // (AB)C = ABC 
					for (Regexp exp0 : ((Concatenation)fn).getExp()) {
						newList.add(exp0);
					}
				} else if (fn instanceof Epsilon) {
					// AeB = AB
				} else {					
					newList.add(fn);
				}
			}
			list.clear();
			list.addAll(newList);
			
			//return root;
			

			
			
			//lampriers : je retire la distribution, c'est le contraire qu'on fait maintenant (facto des alternatives)
			
			// distribute (A+B)C = AC+BC 
			/*Alternation alt = null;
			for (Regexp exp : con.getExp()) {
					if (exp instanceof Alternation) {
							alt = (Alternation) exp;
					}					
			}
			
			if (alt == null) {
				if (!con.equals(root)) {//?? TODO
					return toNormalForm(con);				
				} else {				
					return con;
				}
			} else {
				// hypothesis: the distributed part will always be the last one
				con.getExp().remove(alt);

				Alternation nalt = new AlternationImpl();
				for (Regexp exp : alt.getExp()) {
					Concatenation ncon = new ConcatenationImpl();
					ncon.getExp().add(con);
					ncon.getExp().add(exp);
					nalt.getExp().add(ncon);
				}
				
				return toNormalForm(nalt);
			 
			}*/
		} else if (root instanceof KleeneStar) {
			
			Regexp exp = ((KleeneStar) root).getExp();

			if (exp instanceof KleeneStar) { // (A*)* = A*
				return toNormalForm(exp);
			} else if (exp instanceof EmptySet || exp instanceof Epsilon) { // ({})* = {},  (e)* = e
				return exp;			
			} else {
				((KleeneStar) root).setExp(toNormalForm(((KleeneStar) root).getExp()));
				return root;
			}
		}
		
		return root;
	}
	
	/**apply simple normalization to the Regexp (Unfold)*/
	public Regexp simplyNormalize(Regexp right) {//TODO
		//System.out.println("NORMALIZE: " + root.toString());
		
		if (right instanceof Alternation) {
			List<Regexp> rightList = ((Alternation) right).getExp();
			//()=empty set     ((abcd)) = (abcd)
			if(rightList.size() == 0) {
				return new EmptySet();
			} else if (rightList.size() == 1) {
				return rightList.get(0);
			} 
			
			/*//for each
			for(Regexp exp: rightList) {
				
			}*/
			
			
		} else if (right instanceof Concatenation) {
		
		} else if (right instanceof KleeneStar) {

		} 
		
		return right;
	}


}
