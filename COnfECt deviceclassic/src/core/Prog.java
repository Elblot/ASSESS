/*
 *  Prog.java
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
//import fr.lip6.meta.traceGenerator.util.TraceIO;
import regexp.Regexp;
import regexp.RegexpUtil;
import traces.*;
import dataGenerator.*;
import fsa.EpsilonRemover;
import fsa.GenerateDOT;
import fsa.FSA;

import java.util.ArrayList;
import java.io.Serializable;
import java.io.File;
import java.io.FileNotFoundException;

import program.Main;
public class Prog implements Serializable{
	private transient ArrayList<Trace> observees;
	private transient ArrayList<Trace> uniformes;
	private String rep_traces;
	private String sd_file; 
	private String rep_name;
	private String rep_traces_name;
	private String sd_file_name; 
	private transient FSA fsa;
	private transient FSA regularfsa;
	private transient Regexp regexp;
	
	private transient Main sd;
	/*public Prog(LTS lts, ArrayList<Trace> observees){
		this.observees=observees;
		this.lts=lts;
	}
	public Prog(Main sd, ArrayList<Trace> observees){
		this.observees=observees;
		this.lts=sd.toLTS();
		EpsilonRemover.removeEpsilon(lts);
	}*/
	/*public Prog(LTS lts, String repertoire){
		this.observees=Trace.getTracesFromDir(repertoire);
		this.lts=lts;
	}*/
	public Prog(String sd_file, String rep_traces){
		//System.out.println("Construction prog "+sd_file+","+rep_traces);
		this.sd_file=sd_file;
		this.rep_traces=rep_traces;
		File f=new File(sd_file);
		rep_name=f.getParentFile().getParentFile().getName()+"_"+f.getParentFile().getName();
		sd_file_name=f.getName();
		sd_file_name=sd_file_name.substring(0, sd_file_name.indexOf("."));
		f=new File(rep_traces);
		rep_traces_name=f.getName();
		
		this.observees=null;
		sd=null;
		this.fsa=null;
		
		//System.out.println("Construction prog ok ");
	}
	public String getName(){
		return(rep_name);
	}
	public Main getSD(){
		if (sd==null){
			//System.out.println("Lecture sd from "+sd_file);
			sd=Main.deserialize(sd_file);
		}
		return(sd);
	}
	public FSA getFSA(){
		if (fsa==null){
			getSD();
			//System.out.println("SD to LTS "+sd);
			this.fsa=sd.getFSA();
			//System.out.println("Epsilon removal ");
			//EpsilonRemover.removeEpsilon(lts);
		}
		//GenerateDOT.printDot(lts, "./Results/"+"Prog_"+this.toString()+".dot");
		return(fsa);
	}
	
	public Regexp getRegexp(){
		if(regexp==null){
			regexp=RegexpUtil.FSA2Regexp(getFSA());
		}
		return regexp;
	}
	public FSA getRegularFSA(){
		if(regularfsa==null){
			regularfsa=RegexpUtil.reg2FSA(getRegexp());
		}
		return regularfsa;
	}
	public ArrayList<Trace> getObservations(){
		if(observees==null){
			//System.out.println("Lecture traces from "+rep_traces);
			this.observees=Trace.getTracesFromDir(rep_traces);
			
			/*int i=0;
			for(Trace tt:observees){
				i++;
				for(Statement st:tt.getStatements()){
					System.out.println(st);
				}
				//TraceIO.save(tt.getTraceGenResult(), rep_traces,"_"+i+"_",false);
			}*/
			/*int max=0;
			int min=-1;
			double moy=0.0;
			for(Trace o:observees){
				if(max<o.getSize()){
					max=o.getSize();
				}
				if ((min==-1) || (min>o.getSize())){
					min=o.getSize();
				}
				moy+=o.getSize();
			}
			if (observees.size()>0){
				moy/=observees.size();
			}
			getLTS();*/
			/*lts.longueur_max_traces=(int)(2*moy);
			System.out.println("max = "+((int)(2*moy)));
			*/
			/*lts.longueur_max_traces=2*max;
			System.out.println("max = "+max);
			if (min==-1){
				min=0;
			}
			lts.longueur_min_traces=min;*/
			
		}
		return(observees);
	}
	public ArrayList<Trace> getUniformes(int nb){
		if (nb==-1){
			if (observees==null){
				getObservations();
			}
			nb=observees.size();
		}
		if((uniformes==null) || (uniformes.size()<nb)){
			//System.out.println("Generation traces uniformes nb = "+nb);
			//if (uniformes!=null){
			//	System.out.println("size uniformes = "+uniformes.size());
			//}
			uniformes=getFSA().genereTraces(nb);
			
		}
		if (nb==uniformes.size()){
			return(uniformes);
		}
		else{
			ArrayList<Trace> ret=new ArrayList<Trace>();
			for(int i=0;i<nb;i++){
				ret.add(uniformes.get(i));
			}
			return(ret);
		}
	}
	
	
	public String getSDFile(){
		return(sd_file);
	}
	public String getRepTraces(){
		return(rep_traces);
	}
	
	public String toString(){
		return("Prog_"+rep_name+"_sd="+sd_file_name+"_traces="+rep_traces_name);
	}
	
	public void releaseMem(){
		/*Runtime runtime = Runtime.getRuntime();
		System.out.print( "used : " + ( runtime.totalMemory()-runtime.freeMemory() ) );
		System.out.print( "  committed : " + runtime.totalMemory() );
		System.out.println( "  max : " + runtime.maxMemory() );*/
		
		//observees.clear();
		observees=null;
		//uniformes.clear();
		uniformes=null;
		sd=null;
		fsa=null;
		System.gc();
		
		/*runtime = Runtime.getRuntime();
		System.out.print( "used : " + ( runtime.totalMemory()-runtime.freeMemory() ) );
		System.out.print( "  committed : " + runtime.totalMemory() );
		System.out.println( "  max : " + runtime.maxMemory() );*/
	}
	
}
