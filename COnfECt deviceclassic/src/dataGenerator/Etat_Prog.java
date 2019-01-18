/*
 *  Etat_Prog.java
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

import java.io.Serializable;
import java.io.IOException;

public class Etat_Prog implements Serializable {
	public static int n_vars=20;  // Nombre de variables 
	public static double inf=-1;
	public static double sup=1;
	
	private double[] vars;
	private int nb_vars;
	
	//double p_mute; // Proba de mutation pour introduire du non determinisme dans le programme
	public Etat_Prog(){
		vars=genereVec();
		nb_vars=n_vars;
		//p_mute=pm;
	}
	public Etat_Prog(double[] vec){
		vars=vec;
		nb_vars=n_vars;
	}
	public Etat_Prog(Etat_Prog e){
		double[] vec=new double[n_vars];
		double[] vec2=e.getVec();
		for(int i=0; i<n_vars;i++){
			vec[i]=vec2[i];
		}
		vars=vec;
		nb_vars=n_vars;
	}
	public double getNorm(){
		double sum=0.0;
		for(int i=0; i<nb_vars;i++){
			double v=vars[i];
			sum+=v*v;
		}
		return(Math.sqrt(sum));
	}
	
	public  static double getNorm(double[] vec){
		double sum=0.0;
		for(int i=0; i<n_vars;i++){
			double v=vec[i];
			sum+=v*v;
		}
		return(Math.sqrt(sum));
	}
	
	public void normalize(){
		double norm=getNorm();
		if (norm!=0){
		  for(int i=0; i<nb_vars;i++){
			double v=vars[i];
			vars[i]=v/norm;
		  }
		}
	}
	public static double[] normalize(double[] vec){
		double norm=getNorm(vec);
		if (norm!=0){
		  for(int i=0; i<n_vars;i++){
			double v=vec[i];
			vec[i]=v/norm;
		  }
		}
		return(vec);
	}
	
	public double dotProduct(double[] vec){
		double ret=0.0;
		for(int i=0; i<nb_vars;i++){
			ret+=vars[i]*vec[i];
		}
		return(ret);
	}
	public double cosine(double[] vec){
		double ret=0.0;
		double sum_vars=0.0;
		double sum_vec=0.0;
		for(int i=0; i<nb_vars;i++){
			double v=vars[i];
			sum_vars+=v*v;
			double w=vec[i];
			sum_vec+=w*w;
			ret+=v*w;
		}
		double nvars=Math.sqrt(sum_vars);
		double nvec=Math.sqrt(sum_vec);
		double denom=nvars*nvec;
		if (denom>0){
			ret=ret/denom;
		}
		return(ret);
	}
	
	/*public double getScore(double[] vec){
		double m=Math.random();
		if (m<p_mut){
			
		}
	}*/
	
	public int getNbVars(){
		return(nb_vars);
	}
	public double[] getVec(){
		return(vars);
	}
	public void addVec(double[] vec){
		//System.out.println(nb_vars+" "+vars.length+" "+vec.length+" "+n_vars);
		for(int i=0; i<nb_vars;i++){
			vars[i]+=vec[i];
		}
	}
	
	public void setVar(int i,double d){
		vars[i]=d;
	}
	public double getVar(int i){
		return(vars[i]);
	}
	/*public void mutation(){
		
	}*/
	public static double[] genereVec(){
		double[] vec=new double[n_vars];
		
		for(int i=0; i<n_vars;i++){
			vec[i]=(Math.random()*(sup-inf))+inf;
		}
		vec=normalize(vec);
		//System.out.println(n_vars);
		return(vec);
	}
}
