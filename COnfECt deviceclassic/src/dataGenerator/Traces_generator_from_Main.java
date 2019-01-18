/*
 *  Traces_generator_from_Main.java
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

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import fsa.*;

import program.Main;

//import fr.lip6.meta.traceGenerator.table.TraceGenResult;
//import fr.lip6.meta.traceGenerator.util.TraceIO;
import traces.Trace;

/***
* Traces_generator_from_Main defines methods for generating traces from program structures. 
* Different kinds of traces may be generated according to the setting of the static variables of the class.
* 
* @author      Sylvain Lamprier
* @version     1.0
*/
public class Traces_generator_from_Main {
	//public static int nb_vars_etat=20;
	//public static int unbiased_traces=1; // 1 => traces non biaisees generees (chaque choix de branche a la meme proba)
	//public static int biased_traces=1; //1 => traces biaisees generees (chaque choix de branche a une probabilite definie lors de la creation du squelette)
	//public static int uniform_traces=1; // 1=> traces uniformes generees (pour chaque trace on tire une longueur possible entre 0 et longueur_trace_max puis on tire un chemin uniformement selon les traces possibles pour cette taille)
	//public static int stateVector_0=1; // 1 => traces simulees selon un etat du programme et des effets sur cet etat a chaque instruction. L etat du programme determine les branches choisies. Les effets sur l'etat sont definis lors de la creation du squelette et sont toujours appliques de la meme maniere (prog deterministe => p_random_effect=0).
	//public static int stateVector_02=1; // 1 => traces simulees selon un etat du programme et des effets sur cet etat a chaque instruction. L etat du programme determine les branches choisies. Les effets sur l'etat sont definis lors de la creation du squelette. Dans 20% des cas on ne les applique pas mais on leur substitue un effet tire aleatoirement. (prog non deterministe => p_random_effect=0.2).
	//public static int stateVector_05=1; // 1 => traces simulees selon un etat du programme et des effets sur cet etat a chaque instruction. L etat du programme determine les branches choisies. Les effets sur l'etat sont definis lors de la creation du squelette. Dans 50% des cas on ne les applique pas mais on leur substitue un effet tire aleatoirement. (prog non deterministe => p_random_effect=0.5).
	//public static int stateVector_1=1; // 1 => traces simulees selon un etat du programme et des effets sur cet etat a chaque instruction. L etat du programme determine les branches choisies. Les effets sont aleatoires. (prog non deterministe => p_random_effect=1).
	
	/** Determines whether unbiased traces are generated when the genereAllTraces method is called. Such traces are generated if this variable is set to 1. <br>
	 * Unbiased traces correspond to traces that have been obtained by making successive choices of path, where each choice of a branch has the same proba); 
	 * */
	public static int unbiased_traces=1; 
	
	/** Determines whether biased traces are generated when the genereAllTraces method is called. Such traces are generated if this variable is set to 1. <br>
	* Biased traces correspond to traces that have been obtained by making successive choices of path, where each choice of a branch has a specific proba that has been defined during the construction of the program
	*/
	public static int biased_traces=1; 
	
	/** Determines whether uniform traces are generated when the genereAllTraces method is called. Such traces are generated if this variable is set to 1. <br>
	* Uniform traces correspond to traces that have been uniformly chosen from the set of possible traces: for each a length is randomly taken between  0 et max_trace_length, and then, a path in the program structure is uniformly taken among the possible paths of this length)
	*/
	public static int uniform_traces=1; 
	
	/** Determines whether stateVector_0 traces are generated when the genereAllTraces method is called. Such traces are generated if this variable is set to 1. <br>
	* stateVector_0 traces result from a simulation of real execution traces, where successive choices depend on the current state of the program. The state of the program (represented by a vector) determines the branches to select at each branching point. The state of the program changes according to effects set for each bloc of the program. These effects are defined during the construction of the program and are always applied in the same manner (deterministic program => p_random_effect=0).
	*/
	public static int stateVector_0=1; 
	
	/** Determines whether stateVector_02 traces are generated when the genereAllTraces method is called. Such traces are generated if this variable is set to 1. <br>
	* stateVector_02 traces result from a simulation of real execution traces, where successive choices depend on the current state of the program. The state of the program (represented by a vector) determines the branches to select at each branching point. The state of the program changes according to effects set for each bloc of the program. These effects are defined during the construction of the program. With a proba of 0.2, the effect of a particular bloc is not applied: a randomly chosen effect vector is applied in place (non deterministic program => p_random_effect=0.2) 
	*/
	public static int stateVector_02=1;  
	
	/** Determines whether stateVector_05 traces are generated when the genereAllTraces method is called. Such traces are generated if this variable is set to 1. <br>
	* stateVector_05 traces result from a simulation of real execution traces, where successive choices depend on the current state of the program. The state of the program (represented by a vector) determines the branches to select at each branching point. The state of the program changes according to effects set for each bloc of the program. These effects are defined during the construction of the program. With a proba of 0.5, the effect of a particular bloc is not applied: a randomly chosen effect vector is applied in place (non deterministic program => p_random_effect=0.5) 
	*/
	public static int stateVector_05=1;  
	
	/** Determines whether stateVector_05 traces are generated when the genereAllTraces method is called. Such traces are generated if this variable is set to 1. <br>
	* stateVector_05 traces result from a simulation of real execution traces, where successive choices depend on the current state of the program. The state of the program (represented by a vector) determines the branches to select at each branching point. The state of the program changes according to randomly chosen effect vectors (non deterministic program => p_random_effect=1). 
	*/
	public static int stateVector_1=1; 
	
	
	static boolean genereAllTraces(String srep,String nom_prog,int nb,Main sd) throws IOException{
	      return(genereAllTraces(srep+"/"+nom_prog,nb,sd));
	}
	
	/** Generates traces whose corresponding static variable (see variables of this class) is set to 1.  
	 * 
	 * @param srepi				The directory where to create folders of traces
	 * @param nb				The number of traces of each type to generate
	 * @param sd				The program concerned by the trace generation
	 * @return					A boolean indicating whether everything worked fine
	 * @throws IOException		when creating files in srepi is not allowed
	 */
	public static boolean genereAllTraces(String srepi,int nb,Main sd) throws IOException{
		boolean ret=true;
		if (unbiased_traces==1){
			ret=genereTraces(srepi,nb,sd);
			if (!ret){
				return(false);
			}
		}
		if (uniform_traces==1){	
			ret=genereUniformesTraces(srepi,nb,sd);
			if (!ret){
				return(false);
			}
		}
		if (biased_traces==1){
			ret=genereBiasedTraces(srepi,nb,sd);
			if (!ret){
				return(false);
			}
		}
		//if (pm==-1){
		//	pm=0;
		//	while(pm<=1){
		if (stateVector_0==1){
			ret=genereEtatTraces(srepi,nb,sd,0);
			if (!ret){
				return(false);
			}
		}
		if (stateVector_02==1){
			ret=genereEtatTraces(srepi,nb,sd,0.2);
			if (!ret){
				return(false);
			}
		}
		if (stateVector_05==1){
			ret=genereEtatTraces(srepi,nb,sd,0.5);
			if (!ret){
				return(false);
			}
		}
		if (stateVector_1==1){
			ret=genereEtatTraces(srepi,nb,sd,1);
			if (!ret){
				return(false);
			}
		}
		/*for(int i=0;i<pm.length;i++){
		        ret=genereEtatTraces(srepi,nb,sd,nb_vars_etat,pm[i]);
				if (!ret){
					return(false);
				}
				//pm+=0.1;
			
		}*/
		/*else{
		  ret=genereEtatTraces(srepi,nb,sd,nb_vars,pm);
		  if (!ret){
				return(false);
		  }
		}*/
		return(true);
	}	
	
	// Automate avec autant de chances pour chaque decision
	static boolean genereTraces(String srepi,int nb,Main sd) throws IOException{
		   boolean ret=true;
		   //TraceGenResult trg=null;
		   String sreptraces=srepi+"/Unbiased_traces";
		   File repit = new File (sreptraces);
		   repit.mkdirs();
		   for(int y=0;y<nb;y++){
			   //trg=new TraceGenResult();
			   boolean ok=false;
			   int i=0;
			   Trace trace=null;
			   while((i<(y+10)) && (!ok)){
			       trace=sd.getTrace();
			       if (trace.getSize()<=sd.getLongueurTraceMax()){
			    	   ok=true;
			       }
			       i++;
			   }
			   if (trace.getSize()>sd.getLongueurTraceMax()){
				   return(false);
			   }
			   //trg.addTrace(trace);
			   trace.serialize(sreptraces+"/trace"+y+".trace");
			   trace.save(sreptraces+"/trace"+y+".txt");
				
			   //TraceIO.save(trg, sreptraces,"_"+y+"_",false);
		   }
		   return(ret);
	}
	
	// autant de chances pour chaque trace possible de longueur inferieure a sd.getLongueurTraceMax
    static boolean genereUniformesTraces(String srepi,int nb,Main sd) throws IOException{
			   boolean ret=true;
			   //int lmax=sd.getLongueurTraceMax();
			   FSA lts=sd.getFSA();
			   //TraceGenResult trg=null;
			   String sreptraces=srepi+"/Uniform_traces";
			   File repit = new File (sreptraces);
			   repit.mkdirs();
			   
			   for(int i=0;i<nb;i++){
				    //trg=new TraceGenResult();
					int l=lts.getRandLongueur();
					if (l<0){
						return(false);
					}
					Trace trace=lts.generationUnif(l);
					//trg.addTrace(trace);
					trace.serialize(sreptraces+"/trace"+i+".trace");
					trace.save(sreptraces+"/trace"+i+".txt");
					//traces.add(genereTrace());
				}
			    return(ret);
    }
		
	
	// Automate Probabiliste
	static boolean genereBiasedTraces(String srepi,int nb,Main sd) throws IOException{
		   boolean ret=true;
		   //TraceGenResult trg=null;
		   String sreptraces=srepi+"/Biased_traces";
		   File repit = new File (sreptraces);
		   repit.mkdirs();
		   for(int y=0;y<nb;y++){
			   //trg=new TraceGenResult();
			   boolean ok=false;
			   int i=0;
			   Trace trace=null;
			   while((i<(y+10)) && (!ok)){
				   trace=sd.getBiasedTrace();
			       if (trace.getSize()<=sd.getLongueurTraceMax()){
			    	   ok=true;
			       }
			       i++;
			   }
			   if (trace.getSize()>sd.getLongueurTraceMax()){
				   return(false);
			   }
			   
			   //trg.addTrace(trace);
			   trace.serialize(sreptraces+"/trace"+y+".trace");
			   //System.out.println("Serial Bloc = "+Bloc.serialCount);
			   trace.save(sreptraces+"/trace"+y+".txt");
				
			   //TraceIO.save(trg, sreptraces,"_"+y+"_",false);
			   
		   }
		   return(ret);
	}
	
	// Simulation de l execution d un programme par utilisation d un vecteur de variables latentes representant l etat du programme (les choix de branches sont realises selon l etat courant et chaque instruction a un effet sur cet etat)
	static boolean genereEtatTraces(String srepi,int nb,Main sd,double pm) throws IOException{
		   int nb_vars=sd.getNb_vars_etat();   
		   boolean ret=true;
		   DecimalFormat format = new DecimalFormat();
		   format.setMaximumFractionDigits(1);
		   //TraceGenResult trg=null;
		   String sreptraces=srepi+"/StateVector_traces_prandom="+format.format(pm)+"_nbvars="+nb_vars;
		   File repit = new File (sreptraces);
		   repit.mkdirs();
		   for(int y=0;y<nb;y++){
			   //trg=new TraceGenResult();
			   
			   boolean ok=false;
			   int i=0;
			   Trace trace=null;
			   while((i<(y+10)) && (!ok)){
				   trace=sd.getBiasedTrace_EtatProg(pm);
			       if (trace.getSize()<=sd.getLongueurTraceMax()){
			    	   ok=true;
			       }
			       i++;
			   }
			   if (trace.getSize()>sd.getLongueurTraceMax()){
				   return(false);
			   }
			   //trg.addTrace(trace);
			   trace.serialize(sreptraces+"/trace"+y+".trace");
			   trace.save(sreptraces+"/trace"+y+".txt");
				
			   //TraceIO.save(trg, sreptraces,"_"+y+"_",false);
		   }
		   return(ret);
	}
}
