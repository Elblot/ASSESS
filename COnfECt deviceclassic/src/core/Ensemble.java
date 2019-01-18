/*
 *  Ensemble.java
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


package core;

import java.util.HashSet;
import java.util.Iterator;

import traces.Statement;

public class Ensemble {
	public static <E> HashSet<E> intersection(HashSet<E> h1,HashSet<E> h2){
		HashSet<E> inter=new HashSet<E>();
		Iterator<E> it=h2.iterator();
		while(it.hasNext()){
			E s=it.next();
			if (h1.contains(s)){
				inter.add(s);
			}
		}
		return(inter);
	}
	public static <E> HashSet<E> minus(HashSet<E> h1,HashSet<E> h2){
		HashSet<E> minus=new HashSet<E>();
		Iterator<E> it=h1.iterator();
		while(it.hasNext()){
			E s=it.next();
			if (!(h2.contains(s))){
				minus.add(s);
			}
		}
		return(minus);
	}
	public static <E>  HashSet<E> union(HashSet<E> h1,HashSet<E> h2){
		HashSet<E> union=new HashSet<E>();
		Iterator<E> it=h1.iterator();
		while(it.hasNext()){
			E s=it.next();
			union.add(s);
		}
		it=h2.iterator();
		while(it.hasNext()){
			E s=it.next();
			union.add(s);
		}
		return(union);
	}
	
	public static <E>  HashSet<E> copyHashSet(HashSet<E> h){
		HashSet<E> copy=new HashSet<E>();
		Iterator<E> it=h.iterator();
		while(it.hasNext()){
			copy.add(it.next());
		}
		return(copy);
	}
	
	public  static <E> void addHashSet(HashSet<E> h1,HashSet<E> h2){
		Iterator<E> it=h2.iterator();
		while(it.hasNext()){
			E s=it.next();
			h1.add(s);
		}
	}
}
