/*
 *  Program_Example_Factory.java
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

package programs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import fsa.EpsilonRemover;
import fsa.GenerateDOT;
import fsa.FSA;

import program.Actor;
import program.Alt;
import program.Block;
import program.BlockList;
import program.Call;
import program.Main;



import miners.KTail.*;
//import regexp.BrzGenerateSequenceDiagram;


//import fr.lip6.meta.strategie.StatementComparaisonClassLevel;
//import fr.lip6.meta.traceGenerator.table.TraceGenResult;
//import fr.lip6.meta.traceGenerator.util.TraceIO;
import traces.ObjectInstance;
import traces.ObjectClass;
import traces.ObjectInstance;
import traces.Statement;
import traces.Method;
import traces.Trace;

/**
 * 
 * The factory of a tiny example of program.
 * 
 * @author Sylvain Lamprier
 *
 */
public class Program_Example_Factory {

	
	
	public static Main create(){
		
		ObjectClass c1=new ObjectClass("c1");
		ObjectClass c2=new ObjectClass("c2");
		ObjectClass c3=new ObjectClass("c3");
		ObjectClass c4=new ObjectClass("void");
		
		Actor o1=new Actor("o1",c1);
		Actor o2=new Actor("o2",c2);
		Actor o3=new Actor("o3",c3);
		
		ArrayList<Actor> actors=new ArrayList<Actor>();
		actors.add(o1); actors.add(o2); actors.add(o3);
		
		/* Call call1=new Call(new Statement("S1",o1,new MethodJava("M1",new ArrayList<Parametre>(),c2),o2));
		Call call2=new Call(new Statement("S2",o2,new MethodJava("M2",new ArrayList<Parametre>(),c4),o1));
		
		ArrayList<Bloc> blocsAlt = new ArrayList<Bloc>();
		blocsAlt.add(call1);
		blocsAlt.add(call2);
		
		Alt alt = new Alt(blocsAlt);
		
		ArrayList<Bloc> blocs = new ArrayList<Bloc>();
		blocs.add(alt);
		Main main = new Main(blocs,actors,100); */
		
		
		// Autre exemple avec des statements en commun pour merge
		Call call1=new Call(new Statement("S1",o1,new Method("M1",new ArrayList<ObjectInstance>(),c2),o2));
		Call call2=new Call(new Statement("S2",o2,new Method("M2",new ArrayList<ObjectInstance>(),c2),o3));
		Call call3=new Call(new Statement("S3",o1,new Method("M3",new ArrayList<ObjectInstance>(),c2),o2));
		Call call4=new Call(new Statement("S4",o2,new Method("M4",new ArrayList<ObjectInstance>(),c2),o3));
		Call call5=new Call(new Statement("S5",o1,new Method("M5",new ArrayList<ObjectInstance>(),c2),o3));
		Call call6=new Call(new Statement("S6",o1,new Method("M6",new ArrayList<ObjectInstance>(),c2),o2));
		Call call7=new Call(new Statement("S7",o1,new Method("M7",new ArrayList<ObjectInstance>(),c2),o2));
		Call call8=new Call(new Statement("S8",o2,new Method("M8",new ArrayList<ObjectInstance>(),c1),o3));
		
		
		ArrayList<Block> bloc1 = new ArrayList<Block>();
		BlockList b1= new BlockList(0);
		b1.add(call2);
		b1.add(call3);
		bloc1.add(b1);
		
		BlockList b2= new BlockList(0);
		b2.add(call5);
		
		Alt alt8 = new Alt();
		alt8.add(call8);
		alt8.add(call4);
		
		//b2.add(call4);
		
		b2.add(alt8);
		
		Alt alt1 = new Alt();
		alt1.add(b1);
		alt1.add(b2);
		

		ArrayList<Block> b3 = new ArrayList<Block>();
		b3.add(call1);
		b3.add(alt1);
		BlockList list = new BlockList(b3);
		
		Alt alt2 = new Alt();
		alt2.add(list);
		alt2.add(call4);
		
		ArrayList<Block> bloc7 = new ArrayList<Block>();
		BlockList b7= new BlockList(0);
		b7.add(call7);
		b7.add(call4);
		bloc7.add(b7);
		
		alt2.add(b7);
		
		
		
		ArrayList<Block> grandBloc = new ArrayList<Block>();
		grandBloc.add(alt2);
		Main main = new Main(grandBloc,actors,100);
		return(main);
	}

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main sd=create();
		boolean ok=sd.saveModel("Exemple");
		if (ok){
		   sd.genereAllTraces("Exemple", 10);
		}
		// TODO Auto-generated method stub
		/*Main sd=createSD();	// Creation d'un diagramme de sequence
		String output="C:/Exemple";	 
		File f=new File(output);	// Creation du repertoire de sortie C:/Exemple
		if (f.exists()){
			Reader reader = new InputStreamReader(System.in);
			BufferedReader input = new BufferedReader(reader);
			System.out.print("Warning : "+output+" already exists, overwrite ? (Y/N)");
			try{
			   String ok = input.readLine();
			   if ((ok.compareTo("Y")!=0) && (ok.compareTo("y")!=0)){return;}
			}
			catch(Exception e){
				System.out.println(e);
			}
			deleteRecursive(f);
		}
		boolean ok = sd.saveModel(output);
		if (ok){
		  sd.genereAllTraces("C:/Exemple", 50);
		}*/
		/*try{
			 
			 File repit = new File ("./Exemple/1");
			 deleteRecursive(repit);
			 repit.mkdirs();
			 
			 // Sauvegarde SD
			 sd.save("./Exemple/1/exemple_sd_model.txt");
			 sd.serialize("./Exemple/1/exemple_sd_model.sd");
			 
			 //LTS
			 LTS lts=sd.toLTS();
			 //System.out.println(lts);
			 GenerateDOT.printDot(lts, "./Exemple/1/exemple_sd_model.dot");  
			 EpsilonRemover.removeEpsilon(lts);
			 //System.out.println(lts);
			 GenerateDOT.printDot(lts, "./Exemple/1/exemple_sd_model_no_epsilon.dot");
			 
			 
			 // Traces
			 TraceGenResult trg=null;
		     String sreptraces="./Exemple/1/biased_traces";
			 repit = new File (sreptraces);
			 repit.mkdirs();
			 ArrayList<Trace> traces=new ArrayList<Trace>();
			 for(int y=0;y<1000;y++){
				trg=new TraceGenResult();
				Trace trace=sd.getBiasedTrace();
				trg.addTrace(trace);
				trace.serialize(sreptraces+"/trace"+y+".trace");
				TraceIO.save(trg, sreptraces,"_"+y+"_",false);	
				traces.add(trace);
		     }
		 }
		 catch(FileNotFoundException e){
			   System.out.println(e.getMessage());
		 }
		 catch(IOException e){
			   System.out.println(e.getMessage());
		 }*/
	}
	
	public static void deleteRecursive(File f)  {
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
}


