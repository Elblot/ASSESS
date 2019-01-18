/*
 *  DataGenerator.java
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

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import fsa.GenerateDOT;
import fsa.FSA;

import program.Actor;
import program.Alt;
import program.BlockList;
import program.Call;
import program.Loop;
import program.Main;


import utils.Keyboard;


import traces.*;
//import fr.lip6.meta.traceGenerator.table.*;
//import fr.lip6.meta.traceGenerator.util.*;


//import fr.lip6.meta.behavioral.transformations.BrzGenerateSequenceDiagram;


/**
 * Main class for the generation of artificial data (programs + traces).
 * 
 * According to parameters defined in an instance of DataGeneratorConfig, 
 * produces a directory containing a given number of artificial programs with extracted execution traces. 
 * 
 * Contains a main method that allows to launch the generation according to a configuration file (whose format is given in the documentation of the DataGeneratorConfig class), a number of programs to produce, a number of traces per program and strategy to extract, and an ouput directory to leave the generated data.
 *  
 * @author Sylvain Lamprier
 * 
 */
public class DataGenerator {
	private DataGeneratorConfig config;
	
	DataGenerator(DataGeneratorConfig conf){
		this.config=conf;
		BlockList.nb_max_concat=config.getNb_max_concat();
		Call.pfils=config.getPchild();
		Alt.more=config.getMore();
		Loop.p_max_loop=config.getP_max_loop(); 
		Loop.nb_max_loops=config.getNb_max_loops();
		Etat_Prog.n_vars=config.getNb_vars_stateVector();
		//Traces_generator_from_Main.nb_vars_etat=config.getNb_vars_etat();
		Traces_generator_from_Main.unbiased_traces=config.getUnbiased_traces();
		Traces_generator_from_Main.uniform_traces=config.getUniform_traces();
		Traces_generator_from_Main.biased_traces=config.getBiased_traces();
		Traces_generator_from_Main.stateVector_0=config.getStateVector_0();
		Traces_generator_from_Main.stateVector_02=config.getStateVector_02();
		Traces_generator_from_Main.stateVector_05=config.getStateVector_05();
		Traces_generator_from_Main.stateVector_1=config.getStateVector_1();
		Call.setClosing=(config.getSetClosing()==1);
	}
	
	static void deleteRecursive(File f)  {
		if (f.exists()){
			if (f.isDirectory()){
			  File[] childs=f.listFiles();
			  int i=0;
			  for(i=0;i<childs.length;i++){
				  deleteRecursive(childs[i]);
			  }
			  
			}
			f.delete();
		 }
	}
	
	// genere n progs avec nb_traces chacun (pour chaque type de traces)
	void genere(String prefixName,int n,int nb_traces,String output,boolean deleteOld) throws IOException{
		String srep=output;
		if (output.length()==0){
			String format = "dd.MM.yyyy_H.mm.ss";
			java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat( format );
			Date date = new Date(); 
			String sdate=formater.format(date);
			srep="./Progs/Model_"+sdate;
		}
		System.out.println("Output = "+srep);
		File rep = new File (srep);
		if(deleteOld){
			deleteRecursive(rep);
		}
		if(!rep.exists()){
			rep.mkdirs();
		}
		if(prefixName.length()==0){
			config.printConf(srep);
			prefixName="_";
		}
		else{
			config.printConf(srep,prefixName);   
		}
	    
	    ArrayList<Actor> objets=new ArrayList<Actor>();	    
		HashSet<String> voc=genereVocabulaire(config.getNb_classes(),config.getNb_objects_per_class(),config.getNb_meth_per_object(),config.getProportion(),objets);
		int nb_obj=objets.size();
		/*String[] obj=new String[nb_obj];
		for(int i=0;i<nb_obj;i++){
			obj[i]=objets.get(i).toString();
		}*/
		int nb_sdg=0;
		for(int i=0;i<n;i++){
			nb_sdg++;
	    	String srepi=srep; //+"/"+(i+1);
		    
			//
			try{
				for(int z=0;z<nb_obj;z++){
					objets.get(z).reinit();
				}
			   //ArrayList<ObjectJava> actifs=new ArrayList<ObjectJava>();
			   //actifs.add(objets.get(0));
			   Main sd=new Main(new BlocChoice(objets,config.getPcall(),config.getPexist(),config.getPconcat(),config.getPalt(),config.getPloop(),config.getPopt(),config.getMax_depth(),config.getDeterministe()),objets.get(0),new HashSet<Statement>(),1,objets,config.getMax_trace_length(),config.getNb_vars_stateVector());
			   //sd.setObjects(obj);
			   //sd.nettoie_non_deterministe();
			   
			   /*LTS lts=sd.getLTS();
			   GenerateDOT.printDot(lts, srepi+"/blocs_sd_model.dot");*/
			   //double[] pmutes={0.0,0.2,0.5,1};
			   if(!Traces_generator_from_Main.genereAllTraces(srep,prefixName+(i+1), nb_traces, sd)){
				   i--;
				   continue;
			   }
			   //sd.genereTrace();
			   //Main sd2=null;
			   try{
				 //BrzGenerateSequenceDiagram.counter=0;
				 
			     //BrzGenerateSequenceDiagram.generateSD(sd,obj,srepi+"/brz_sd_model.txt");
			     
			     sd.saveModel(srepi,prefixName+(i+1));
			     System.out.println("Generation SD ok");
			     //sd2=Main.deserialize(srepi+"/blocs_sd_model.sd");
			     //sd2.save(srepi+"/blocs_sd_model2.txt");
			   }
			   catch(Exception e){
				   System.out.println(e.getMessage());
			   }
			  
			   //System.out.println("Serial Bloc = "+Bloc.serialCount);
			   //Bloc.serialCount=0;
			   
			   
			   
			   
			   
			   /*System.out.println(lts);
			   GenerateDOT.printDot(lts, srepi+"/blocs_sd_model.dot");
			   
			   EpsilonRemover.removeEpsilon(lts);
			   System.out.println(lts);
			   GenerateDOT.printDot(lts, srepi+"/blocs_sd_model_no_epsilon.dot");*/
			   
			   
			   // convertTodfa semble faire n'importe quoi... 
			   //LTS dfa=(new NFAToDFA()).convertToDFA(lts);
			   //System.out.println("DFA ok");
			   //GenerateDOT.printDot(dfa, srepi+"/blocs_sd_model_dfa.dot");
			   
			   
			   
			   // Partie a remplacer par generateur Nico
			   // ************************************
			   
			   /*sreptraces=srepi+"/traces";
			   repit = new File (sreptraces);
			   repit.mkdirs();
			   for(int y=0;y<nb_traces;y++){
				   trg=new TraceGenResult();
				   Trace trace=sd.getTrace();
				   trg.addTrace(trace);
				   TraceIO.serializeObject(trg, sreptraces+"/trace"+y+".trace");
			   }*/
			   //TraceIO.save(trg, sreptraces);
			   // **************************************
			   
			   
			}
			catch(VocabulaireVideException e){
				System.out.println(e.getMessage());
			}
	    }
		System.out.println("SD generes (y compris supprimes) = "+nb_sdg);
	}
	
	// genere n progs avec nb_traces chacun (pour chaque type de traces)
	private void genere(int n,int nb_traces,String output) throws IOException{
		this.genere("",n, nb_traces, output,true);
	}
	
	
	
	
	
	
	
	private HashSet<String> genereVocabulaire(int nb_classes,int nb_objects_per_class, int nb_meth_per_object,double proportion,ArrayList<Actor> objets){
		HashSet<String> voc=new HashSet<String>();
		ArrayList<ObjectClass> acj=new ArrayList<ObjectClass>(); 
		int no=1;
		for(int i=0;i<nb_classes;i++){
			ObjectClass cj=new ObjectClass("C"+i);
			acj.add(cj);
			for(int j=0;j<nb_objects_per_class;j++){
				Actor oj=new Actor("O"+no,cj);
				no++;;
				objets.add(oj);
			}
		}
		int nb_st=0;
		int osize=objets.size();
		for(int j=0;j<osize;j++){
			Actor appele=objets.get(j);
			for(int k=0;k<nb_meth_per_object;k++){
				Method mj=new Method("M"+k,new ArrayList<ObjectInstance>(),acj.get((int)(Math.random()*acj.size())));
				for(int i=0;i<osize;i++){
					Actor appelant=objets.get(i);
					double x=Math.random();
					if (x<proportion){
						nb_st++;
						Statement st=new Statement("S"+nb_st,appelant,mj,appele);
						appelant.addPossibleStatement(st);
						voc.add(st.toString());
					}
				}
			}
		}
		
		return(voc);
		
	}

	
	/*
	public static SD_Random_Generator_Config  getConfig(String className) throws Exception{
			Class myClass = Class.forName( className );
			SD_Random_Generator_Config conf=(SD_Random_Generator_Config) myClass.newInstance(); 
			return(conf);
	}*/
	
	private static void go(String[] args){
		try{
			String conf_name=args[0];
			int nb_sd=Integer.parseInt(args[1]);
			int nb_traces=Integer.parseInt(args[2]);
			String output="";
			if (args.length==4){
				output=args[3];
				File f=new File(output);
				if (f.exists()){
					Reader reader = new InputStreamReader(System.in);
					BufferedReader input = new BufferedReader(reader);
					System.out.print("Warning : "+output+" already exists, overwrite ? (Y/N)");
					String ok = input.readLine();
					if ((ok.compareTo("Y")!=0) && (ok.compareTo("y")!=0)){return;}
				}
			}
			DataGeneratorConfig conf=new DataGeneratorConfig(conf_name);
			
			DataGenerator gen=new DataGenerator(conf);
			gen.genere(nb_sd,nb_traces,output);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	/*public static void main1(String[] args){
		String [] pars=new String[3];
		pars[0]="./Configs/configD.txt";
		pars[1]="10";
		pars[2]="100";
		go(pars);
	}*/
	
	/**
	 * main method that allows to launch the generation according to a configuration file (whose format is given in the documentation of the DataGeneratorConfig class), a number of programs to produce, a number of traces per program and strategy to extract, and an ouput directory to leave the generated data.
	 */
	public static void main(String[] args){
		
		//String class_conf="fr.lip6.meta.sdgenerator.SD_Random_Generator_Config1";
		DataGeneratorConfig.prompt=true;
		
		if (args.length<3){
			//System.out.println("Le programme attend les arguments suivants : \n - nom du fichier de configuration a utiliser \n - nombre de programmes a generer \n - nombre de traces a generer pour chaque programme et type de trace \n - nom du repertoire de sortie (facultatif). Par defaut un nom est genere selon la date courante");
			args=new String[4];
			args[0]=Keyboard.saisirLigne("Configuration File?");
			args[1]=Keyboard.saisirLigne("Nb Progs to Generate?");
			args[2]=Keyboard.saisirLigne("Nb Traces per Prog and Type of Trace?");
			args[3]=Keyboard.saisirLigne("Output Directory?");
		}
		String[] pars=args;
		go(pars);
		
	
			
			
			
			
			
			
			
			
		/*if(args.length==3){
			try{
				// C'est un numero de config 
				int iconf=Integer.parseInt(args[0]);
				class_conf="fr.lip6.meta.sdgenerator.SD_Random_Generator_Config"+iconf;
			}
			catch(NumberFormatException e){
				// C'est un nom de config
				class_conf=args[0];
			}
			nb_sd=Integer.parseInt(args[1]);
			nb_traces=Integer.parseInt(args[2]);
		}
		try{
			SD_Random_Generator_Config conf=getConfig(class_conf);
			System.out.println("Classe de Configuration utilisee : "+conf.getClass());
			SD_Random_Generator gen=new SD_Random_Generator(conf);
			gen.genere(nb_sd,nb_traces);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}*/
			
		
	}
}
