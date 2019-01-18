/*
 *  Sequence.java
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
import java.util.ArrayList;
import java.util.HashSet;
import java.lang.StringBuilder;
import java.util.HashMap;

/**
 * 
 * Represents a sequence of strings
 * 
 * @author Sylvain Lamprier
 *
 */
public class Sequence {
	//private String[] seq;
	//int lastIndex=0;
	//private boolean locked=false;
	//private StringBuilder sb; 
	//public static int comparisonMode=0; // 0 => equality of chain represented by seq, 1 => the shortest is included in the longest
	//private int next=-2;
	
	/**
	 * The string represented by the sequence
	 */
	String string="";
	
	/**
	 * Size of the sequence (number of strings)
	 */
	private int size=0;
	
	public static Sequence root=new Sequence();
	
	//Sequence parentForward=null; // (by forward construction)
	//Sequence parentBackward=null;
	
	/**
	 * Table of pairs (X,Y) where Y is a sur-sequence of this sequence: Y=this.string+X  
	 */
	HashMap<String,Sequence> forwardChilds=null;
	
	/**
	 * Table of pairs (X,Y) where Y is a sur-sequence of this sequence: Y=X+this.string  
	 */
	HashMap<String,Sequence> backwardChilds=null;
	//public static HashMap<String,Sequence> inTree=new HashMap<String,Sequence>();
	//private boolean locked=false;
	/*public Sequence(ArrayList<String> seq){
		//this(seq,0);
		this.seq=seq;
		this.chaine="";
		
	}*/
	/*public Sequence(ArrayList<String> seq,int l){
		this.seq=seq;
		this.lastIndex=l;
	}*/
	
	/**
	 * True is the tree of sequences (defined by forward and backward childs) is locked, i.e. no more sequences can be built 
	 */
	public static boolean lockedTree=false;
	//public static Sequence=new Sequence;
	
	public Sequence(){
		//this.seq=null;
		string="";
		//size=0;
		//forwardChilds=new HashMap<String,Sequence>();
		//backwardChilds=new HashMap<String,Sequence>();
		
		//sb=new StringBuilder();
	}
	private Sequence(String ch,int size){
		//this.seq=null;
		string=ch;
		this.size=size;
		//forwardChilds=new HashMap<String,Sequence>();
		//backwardChilds=new HashMap<String,Sequence>();
		
		//sb=new StringBuilder();
	}
	
	/***
	 * Returns the sequence Y from forwardChilds whose string = this.String+ch.
	 * If lockedTree is true and no such sequence belongs to forwardChilds => returns null.
	 * If lockedTree is false and no such sequence belongs to forwardChilds => creates it, adds it to forwardChilds and returns it.
	 * 
	 * @param ch	String to append at the end of this.string to get the seeked sequence
	 * @return a sequence or null
	 */
	public Sequence getForwardChild(String ch){
		if (forwardChilds==null){
			forwardChilds=new HashMap<String,Sequence>();
		}
		Sequence ret=forwardChilds.get(ch);
		if ((ret==null) && (!lockedTree)){
			ret=new Sequence(string,size);
			ret.addAfter(ch);
			//ret.parentForward=this;
			forwardChilds.put(ch,ret);
		}
		return(ret);
	}
	
	/***
	 * Returns the sequence Y from backwardChilds whose string = ch+this.String.
	 * If lockedTree is true and no such sequence belongs to backwardChilds => returns null.
	 * If lockedTree is false and no such sequence belongs to backwardChilds => creates it, adds it to backwardChilds and returns it.
	 * 
	 * @param ch	String to append on the beginning of this.string to get the seeked sequence
	 * @return a sequence or null
	 */
	
	public Sequence getBackwardChild(String ch){
		if (backwardChilds==null){
			backwardChilds=new HashMap<String,Sequence>();
		}
		Sequence ret=backwardChilds.get(ch);
		if ((ret==null) && (!lockedTree)){
			ret=new Sequence(string,size);
			ret.addBefore(ch);
			//ret.parentBackward=this;
			backwardChilds.put(ch,ret);
		}
		return(ret);
	}
	
	/*public HashMap<String,Sequence> getForwardChilds(){
		if (forwardChilds==null){
			forwardChilds=new HashMap<String,Sequence>();
		}
		return forwardChilds;
	}*/
	
	/*public static void setInBackWardsTree(Sequence root,HashSet<Sequence> seqs){
		
	}*/
	
	//public void removeFromForwardChilds(String ch)
	
	public String getString(){
		return string;
	}
	
	public static void addInTree(Sequence root,HashSet<Sequence> seqs){
		HashMap<String,Sequence> sm=new HashMap<String,Sequence>();
		for(Sequence s:seqs){
			s.forwardChilds=new HashMap<String,Sequence>();
			s.backwardChilds=new HashMap<String,Sequence>();
			sm.put(s.string, s);
		}
		root.forwardChilds=new HashMap<String,Sequence>();
		root.backwardChilds=new HashMap<String,Sequence>();
		addInTree(root,sm);
	}
	private static void addInTree(Sequence root,HashMap<String,Sequence> seqs){
		for(Sequence s:seqs.values()){
			int i=s.string.lastIndexOf(";:@:;");
			if(i<0){
					root.forwardChilds.put(s.string,s);
					//s.parentForward=root;
					root.backwardChilds.put(s.string,s);
					//s.parentBackward=root;
			}
			else{
				String nch=s.string.substring(0, i);
				Sequence p=seqs.get(nch);
				p.forwardChilds.put(s.string.substring(i+5),s);
				i=s.string.indexOf(";:@:;");
				nch=s.string.substring(i+5);
				p=seqs.get(nch);
				p.backwardChilds.put(s.string.substring(0,i),s);
			}
		}
	}
	
	
	/*public void addForwardChild(String ch,Sequence s){
		forwardChilds.put(s.chaine, s);
		s.parentForward=this;
	}
	public void addBackwardChild(String ch,Sequence s){
		backwardChilds.put(s.chaine, s);
		s.parentBackward=this;
	}*/
	/*
	public HashSet<Sequence> getForwardChilds(){
		 HashSet<Sequence> ret=new HashSet<Sequence>();
		 for(Sequence s:forwardChilds.values()){
			 ret.add(s);
			 ret.addAll(s.getForwardChilds());
		 }
		 return(ret);
	}*/
	
	
	public void addAfter(String el){
		//seq.add(el);
		//sb.append(";:@:;");
		//sb.append(el);
		size++;
		StringBuilder sb=new StringBuilder(string);
		if (string.length()>0){
			//chaine=chaine+";:@:;";
			sb.append(";:@:;");
		}
		sb.append(el);
		string=sb.toString();
		//chaine=chaine+el; //this.toString();
	}
	public void addBefore(String el){
		//seq.add(0,el);
		//chaine=this.toString();
		//sb.insert(0, ";:@:;");
		//sb.insert(0, el);
		size++;
		StringBuilder sb=new StringBuilder(string);
		if (string.length()>0){
			//chaine=";:@:;"+chaine;
			sb.insert(0,";:@:;");
		}
		sb.insert(0,el);
		//chaine=el+chaine;
		string=sb.toString();
	}
	
	/*public HashSet<Sequence> getSubs(boolean forward){
		HashSet<Sequence> subs=new HashSet<Sequence>();
		if (seq==null){
			this.seq=chaine.split(";:@:;");
		}
		if (forward){
			for(int i=1;i<seq.length;i++){
				Sequence s=new Sequence();
				subs.add(s);
				for(Sequence se:subs){
					se.addAfter(seq[i]);
				}
			}
		}
		else{
			for(int i=seq.length-2;i>=0;i--){
				Sequence s=new Sequence();
				subs.add(s);
				for(Sequence se:subs){
					se.addBefore(seq[i]);
				}
			}
		}
		return(subs);
	}
	*/
	/*public void lock(){
		locked=true;
	}*/
	
	/*public String next(){
		return(next(true));
	}
	
	public String next(boolean forward){
		if(next==-2){
			this.seq=chaine.split(";:@:;");
			next=forward?0:(seq.length-1);
		}
		String ret="";
		if (forward){
			if(next<seq.length){
				ret=seq[next];
				next++;
			}
			else{
				next=0;
			}
		}
		else{
			if(next>=0){
				ret=seq[next];
				next--;
			}
			else{
				next=seq.length-1;
			}
		}
		return(ret);
	}*/
	/*public void lock(){
		locked=true;
	}
	public boolean isLocked(){
		return locked;
	}*/
	/*public void add(String el,int l){
		seq.add(el);
		lastIndex=l;
	}*/
	/*public void setLastIndex(int l){
		this.lastIndex=l;
	}*/
	public int hashCode(){
		return(string.hashCode());
	}
	public boolean equals(Object o){
		if (o==null){
			return(false);
		}
		if (o.getClass()!=this.getClass()){
			return(false);
		}
		Sequence s=(Sequence)o;
		//System.out.print(this + " : "+s);
		
		
		if (string.compareTo(s.string)!=0){
				//System.out.println(" false");
			return(false);
		}
			
		//System.out.println(" true");
		
		return true;
	}
	
	// retourne -1 si sub, 1 si super, 0 si rien
	/*public int isSubOrSuperSequenceOf(Sequence s2){
		if (s2.size()<size()){
			return ((s2.isSubSequenceOf(this))?1:0);
		}
		else{
			return ((isSubSequenceOf(s2))?-1:0);
		}
	}
	
	public boolean isSuperSequenceOf(Sequence s2){
		if (s2.size()>size()){
			return(false);
		}
		else{
			return(s2.isSubSequenceOf(this));
		}
	}
	public boolean isSubSequenceOf(Sequence s2){
		if (s2.size()<size()){
			return(false);
		}
		if (seq.size()==0){
			return true;
		}
		ArrayList<String> seq2=s2.seq;
		int j=0;
		String y=seq.get(j);
		for(int i=0;i<seq2.size();i++){
			String x=seq2.get(i);
			if(x.compareTo(y)==0){
				j++;
				if (j>seq.size()){
					return true;
				}
				y=seq.get(j);
			}
		}
		return false;
	}*/
	public Sequence copy(){
		
		return(copy(false));
	}
	
	public Sequence copy(boolean tab){
		Sequence s=new Sequence();
		s.string=string;
		s.size=size;
		/*if (tab){
			s.seq=new String[size];
			for(int i=0;i<size;i++){
				s.seq[i]=seq[i];
			}
			s.next=next;
		}*/
		
		//s.lastIndex=this.lastIndex;
		return(s);
	}
	
	public String toString(){
		return(string);
	}
	public int size(){
		return size;
	}
	/*
	public void setNext(int next){
		this.next=next;
	}
	public int getNext(){
		return next;
	}
	public String get(int i){
		if (seq==null){
			this.seq=chaine.split(";:@:;");
		}
		if ((i<0) || (i>size)){
			return null;
		}
		return(seq[i]);
	}*/
	/*public void setParent(Sequence p){
		this.parent=p;
	}*/
	
	
	
	public HashMap<Integer,Integer> countSimilarSeqsIn(Sequence seq){
		lockedTree=true;
		HashMap<Integer,Integer> ret=new HashMap<Integer,Integer>();
		if(seq!=null){
			ret.put(size, 1);
			if(forwardChilds!=null){
			  for(String ch:this.forwardChilds.keySet()){
				HashMap<Integer,Integer> h=this.getForwardChild(ch).countSimilarSeqsIn(seq.getForwardChild(ch));
				for(Integer i:h.keySet()){
					Integer o=ret.get(i);
					o=(o==null)?0:o;
					ret.put(i, o+h.get(i));
				}
			  }
			}
		}
		return ret;
	}
	public HashMap<Integer,Integer> countSeqs(){
		lockedTree=true;
		HashMap<Integer,Integer> ret=new HashMap<Integer,Integer>();
		ret.put(size, 1);
		if(forwardChilds!=null){
		  for(String ch:this.forwardChilds.keySet()){
				HashMap<Integer,Integer> h=this.getForwardChild(ch).countSeqs();
				for(Integer i:h.keySet()){
					Integer o=ret.get(i);
					o=(o==null)?0:o;
					ret.put(i, o+h.get(i));
				}
		  }
		}
		
		return ret;
	}
	
}

