/*
 *  Result.java
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


package evaluation;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.DecimalFormat;

import core.Prog;

import program.Actor;
import miners.FSAminer;
import traces.Trace;

/**
 *  Class allowing to store evaluation results 
 * 
 * @author Sylvain Lamprier
 *
 */
public class Result implements Serializable{
	
	/**
	 * Experiment concerned by the evaluation result
	 */
	Experiment experiment;
	
	/**
	 * Names of data concerned by the evaluation result.
	 * In classical use, only one data name is contained in this list.
	 * When the result is produced by the {@link #getStats} methods, it contains all of the data names on which statistics are produced
	 * 
	 */
	ArrayList<String> data; // a priori une seule donnee si c'est un result normal, plusieurs seulement si c'est un result resultant un getStats
	//ArrayList<Double> scores;
	
	/**
	 * Names of metrics used (only useful to keep an given order in the results). 
	 */
	ArrayList<String> score_names;
	
	/**
	 * Pairs X,Y where X is an evaluation metric name and Y the score obtained according to this metric
	 */
	HashMap<String,Double> scores;
	
	/**
	 * True if the result has been produced by the getStats method
	 */
	boolean isStats;
	
	// juste pour ne pas avoir a changer toutes les constructions de Result
	Result(FSAminer algo, Prog prog, EvalMeasure ev){
		this(algo,prog);
	}
	
	Result(FSAminer algo, Prog prog){
		//this(algo,prog.getRepTraces(),prog.getSDFile(),measure);
		this(algo,prog.toString());
	}
	Result(FSAminer algo, String prog){
		//this(algo,initArrayList(srep_traces),initArrayList(ssd_file),measure);
		//this(algo,new ArrayList<String>({srep_traces}),new ArrayList<String>({ssd_file}),measure);
		this(algo,initArrayList(prog));
	}
	
	Result(FSAminer algo, ArrayList<String> progs){ // ArrayList<String> sd_file, EvalMeasure measure){
		this(algo,progs,false);
	}
	
	private Result(FSAminer algo, ArrayList<String> progs, boolean isStats){
		this(new Experiment(algo.getName()),progs,isStats);
	}
	
	/**
	 * Produces a result for experiment expe on the data given as second argument.
	 * @param expe 	Experiment concerned by the result
	 * @param data  Data concerned by the resultt
	 */
	public Result(Experiment expe,String data){
		this(expe,initArrayList(data),false);
	}
	
	private Result(Experiment expe,ArrayList<String> donnees, boolean isStats){
		this.experiment=expe;
		this.data=donnees;
		scores=new HashMap<String,Double>();
		this.isStats=isStats;
		score_names=new ArrayList<String>();
		//score_names=new ArrayList<String>();
	}
	
	/**
	 * Adds a score to the result
	 * @param name    Name of the evaluation metric
	 * @param val	  Score obtained
	 */
	public void addScore(String name,double val){
		if(!scores.containsKey(name)){
			scores.put(name,val);
			score_names.add(name);
		}
		else{
			System.out.println("Le score "+name+" existe deja !!!");
		}
	}
	
	/**
	 * 
	 * @return the scores list
	 */
	public HashMap<String,Double> getScores(){
		return(scores);
	}
	
	
	/**
	 * Adds a result to be merged with this one.
	 * Merges both lists of scores.
	 * It is supposed that r contains only score names that do not belong to this.scores 
	 * @param r  result to be merged with this one.
	 */
	// Ajoute un Result => fusion des listes de scores
	// On suppose que les noms donnes aux scores sont differents dans les differents Result fusionnes
	public void add(Result r){
		int i=0;
		for(String name:r.score_names){
			addScore(name,r.getScores().get(name));
		}
	}
	
	
	/**
	 * Returns a result containing means and standard deviation of scores belonging in different results. 
	 * It is supposed that all the results in the list given as argument concern the same experiment for a different data.
	 * @param results  the list of results on which statistics have to computed 
	 * @return  the result containing the statistics
	 */
	public static Result getStats(ArrayList<Result> results){
		ArrayList<String> donnees=new ArrayList<String>();
		HashMap<String,Double> sums=new HashMap<String,Double>();
		HashMap<String,Integer> nbs=new HashMap<String,Integer>();
		HashMap<String,Double> sums2=new HashMap<String,Double>();
		ArrayList<String> names=new ArrayList<String>();
		int i=0;
		Experiment expe=null;
		for(Result r:results){
			if (i==0){
				expe=r.experiment;
			}
			donnees.add(r.data.get(0));
			//rep_traces.add(r.getTracesDir(0));
			//sd_files.add(r.getSDFile(0));
			HashMap<String,Double> scores=r.scores;
			//ArrayList<String> score_names=r.score_names;
			for(String name:r.score_names){
				Double score=scores.get(name);
				Integer nb;
				Double sum;
				Double sum2;
				if(nbs.containsKey(name)){
					nb=nbs.get(name);
					sum=sums.get(name);
					sum2=sums2.get(name);
					
				}
				else{
					nb=new Integer(0);
					sum=new Double(0.0);
					sum2=new Double(0.0);
					names.add(name);
				}
				nbs.put(name, new Integer(nb.intValue()+1));
				sums.put(name,new Double(sum.doubleValue()+score));
				sums2.put(name,new Double(sum2.doubleValue()+(score*score)));
				
			}
			i++;
		}
		Result res=new Result(expe,donnees,true);
		//HashMap<String,Double> scores=res.scores;
		
		for(String s:names){ //nbs.keySet()){
			int nb=nbs.get(s).intValue();
			double sum=sums.get(s).doubleValue();
			double sum2=sums2.get(s).doubleValue();
			if (nb>0){
				sum/=nb;
				sum2/=nb;
			}
			//System.out.println(sum2);
			//System.out.println(sum);
			//System.out.println(sum*sum);
			double ec=Math.sqrt(sum2-sum*sum);
			//System.out.println(s+" "+ec);
			if (nb>1){
				ec=ec*Math.sqrt(((double)nb)/(nb-1));
			}
			
			res.addScore("moyenne_"+s, sum);
			res.addScore("ecart_"+s, ec);
			//System.out.println(s+" "+ec);
		}
		
		return(res);
	}
	
	
	void serialize(String filename) throws IOException{
		
			// ouverture d'un flux de sortie vers le fichier "personne.serial"
		    /*File ff=new File(rep_traces.get(0));
		    File p=ff.getParentFile();
		    File rep=new File(p.getAbsolutePath()+"/Results");
		    if (!rep.exists()){
		    	rep.mkdirs();
		    }*/
			//FileOutputStream fos = new FileOutputStream(rep+"/"+toString()+".result");
			FileOutputStream fos = new FileOutputStream(filename);
			// creation d'un "flux objet" avec le flux fichier
			ObjectOutputStream oos= new ObjectOutputStream(fos);
			try {
				// serialisation : ecriture de l'objet dans le flux de sortie
				oos.writeObject(this); 
				
				// on vide le tampon
				oos.flush();
				System.out.println("le result "+toString()+" a ete serialise");
			} finally {
				//fermeture des flux
				try {
					oos.close();
				} finally {
					fos.close();
				}
			}
		
	}
	
	static Result deserialize(String fileName) throws IOException{
		Result ret=null;
		try{
			
			// ouverture d'un flux d'entree depuis le fichier "personne.serial"
			FileInputStream fis = new FileInputStream(fileName);
			// creation d'un "flux objet" avec le flux fichier
			ObjectInputStream ois= new ObjectInputStream(fis);
			try {	
				// deserialisation : lecture de l'objet depuis le flux d'entree
				ret = (Result) ois.readObject(); 
			} finally {
				// on ferme les flux
				try {
					ois.close();
				} finally {
					fis.close();
				}
			}
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
			throw new IOException("Probleme classe");
		}
		return(ret);
	}
	
	static ArrayList<Result> getResultsFromDir(String repertoire){
		ArrayList<Result> results=new ArrayList<Result>();
		try{
			File rep=new File(repertoire);
			File[] files=rep.listFiles();
			for(int i=0;i<files.length;i++){
			   if (files[i].getName().indexOf(".result")>0){
				   Result result=deserialize(files[i].getAbsolutePath());
				   results.add(result);
			   }
			}
		}
		catch(IOException e){
			   System.out.println(e.getMessage());
		}
		return(results);
	}
	
	/**
	 * Writes in a file the scores contained by a list of Result objects.
	 * It is supposed that all the results in the list given as argument concern the same experiment for a different data.
	 * Useful for comparing results obtained for a same experiment over different data.
	 * @param results
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public static void saveResults1DataPerLine(ArrayList<Result> results, String fileName) throws FileNotFoundException{
		File dotFile=new File(fileName);

		 FileOutputStream fout = new FileOutputStream(dotFile);

		 PrintStream out = new PrintStream(fout);
		 
		 String s="Pour Expe : \n";
		 s+=results.get(0).experiment.getDescription();
		 s+="\n";
		 out.println(s);
		 
		 s="";
		 s+="Programme "; 
		 int taillemaxdonnee=7;
		 ArrayList<String> noms_donnees=new ArrayList<String>();
		 for(Result res:results){
			 for(String ndo:res.data){
				 int x=ndo.length();
				 if (x>taillemaxdonnee){
					 taillemaxdonnee=x;
				 }
			 }
		  }
		  for(int j=7;j<taillemaxdonnee;j++){
				 s+=" ";
		  }
		  s+="\t\t";
		 
		  for(Result res:results){
			  for(String ndo:res.data){
					 int x=ndo.length();
					 for(int j=x;j<taillemaxdonnee;j++){
						 ndo+=" ";
					 }
					 noms_donnees.add(ndo);
			  }
		  }
		 
		  ArrayList<String> score_names=results.get(0).score_names;
		  HashSet<String> dits=new HashSet<String>();
		  dits.addAll(score_names);
		  for(Result res:results){
			  Set<String> scn=res.getScores().keySet();
			  for(String nn:scn){
				  if (!dits.contains(nn)){
					  score_names.add(nn);
					  dits.add(nn);
				  }
			  }
		  }
		  ArrayList<Integer> tailles=new ArrayList<Integer>();
		  for (String st : score_names) {
				   s+=st+"\t";
				   int tt=st.length();
				   tailles.add(tt);
		  }
		  out.println(s);
		  out.println();
		  s="";
		  DecimalFormat format = new DecimalFormat();
		  format.setMaximumFractionDigits(3);
		  format.setGroupingUsed(false);
		  
		  
		  
		  int ia=0;
		  for(Result res:results){
			   for(String ndo:res.data){
				   int i=0;
				   s+=noms_donnees.get(ia)+"\t\t";
				   ia++;
				   for (String name : score_names) {
					   String val="";
					   if (!res.scores.containsKey(name)){
						   val="NA";
					   }
					   else{
						   Double st=res.scores.get(name);
						   val=format.format(st);
					   }
					   int tt=val.length();
					   int ta=tailles.get(i);
					   s+=val;
					   for(int j=tt;j<ta;j++){
						   s+=" ";
					   }
					   s+="\t";
					   i++;
				   }
				   s+="\n";
			    }
			}
			out.println(s);
			out.close();
	}
	
	/**
	 *  Writes in a file the scores contained by a list of Result objects.
	 *  It is supposed that the results in the list given as argument concern different experiments for the same set of data.
	 *  Useful for comparing results obtained over different experiments.
	 * @param results
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public static void saveResults1ExpePerLine(ArrayList<Result> results, String fileName) throws FileNotFoundException{
		 File dotFile=new File(fileName);

		 FileOutputStream fout = new FileOutputStream(dotFile);

		 PrintStream out = new PrintStream(fout);
		 
		 /*String s="Pour Expes : \n";
		 int nume=1;
		 for(Result res:results){
			 String al=res.experiment.getDescription();
		     s+="Exp_"+nume+" : "+al+" Sur Donnnees : ";
		     for (String st : res.donnees) {
		    	 s+=st+", ";
		     }
		     //s+=res.donnees.size()+" Programmes ";
		     s+="\n";
		     nume++;
		 }
		 out.println(s);
		 */
		 
		 //out.println();
		 String s="";
		 s+="Algorithm "; 
		 int taillemaxexpe=10;
		 int nume=1;
		 ArrayList<String> noms_expes=new ArrayList<String>();
		 for(Result res:results){
			 String nnume=res.experiment.getDescription();
			 int x=nnume.length();
			 if (x>taillemaxexpe){
				 taillemaxexpe=x;
			 }
			 nume++;
		 }
		 for(int j=11;j<taillemaxexpe;j++){
			   s+=" ";
		 }
		 s+="\t\t";
		 nume=1;
		 for(Result res:results){
			 String nnume=res.experiment.getDescription();
			 int x=nnume.length();
			 for(int j=x;j<taillemaxexpe;j++){
				   nnume+=" ";
			 }
			 noms_expes.add(nnume);
			 nume++;
		 }
		 
		 /*HashSet<String> score_names=new HashSet<String>();
		 for(Result res:results){
			 for (String st : res.scores.keySet()) {
				 if (!score_names.contains(st)){
					 score_names.add(st);
				 }
			 }
		 }*/
		 
		 
		 ArrayList<String> score_names=results.get(0).score_names;
		 HashSet<String> dits=new HashSet<String>();
		 dits.addAll(score_names);
		 for(Result res:results){
			  Set<String> scn=res.getScores().keySet();
			  for(String nn:scn){
				  if (!dits.contains(nn)){
					  score_names.add(nn);
					  dits.add(nn);
				  }
			  }
		  }
		 
		 ArrayList<Integer> tailles=new ArrayList<Integer>();
		 for (String st : score_names) {
			   s+=st+"\t";
			   int tt=st.length();
			   tailles.add(tt);
		 }
		 out.println(s);
		 out.println();
		 s="";
		 DecimalFormat format = new DecimalFormat();
	     format.setMaximumFractionDigits(3);
	     format.setGroupingUsed(false);
	     int ia=0;
		 for(Result res:results){
		   int i=0;
		   s+=noms_expes.get(ia)+"\t\t";
		   ia++;
		   for (String name : score_names) {
			   String val="";
			   if (!res.scores.containsKey(name)){
				   val="NA";
			   }
			   else{
				   Double st=res.scores.get(name);
				   val=format.format(st);
			   }
			   int tt=val.length();
			   int ta=tailles.get(i);
			   s+=val;
			   for(int j=tt;j<ta;j++){
				   s+=" ";
			   }
			   s+="\t";
			   i++;
		   }
		   s+="\n";
		 }
		 out.println(s);
		 out.close();
	}
	
	
	static void saveResults1ExpeParLigneForRepTraces(HashMap<String,ArrayList<Result>> results, String fileName) throws FileNotFoundException{
		ArrayList<String> reps=new ArrayList<String>();
		reps.addAll(results.keySet());
		saveResults1ExpePerLineForTracesDirectory(results,reps,fileName);
	}
	
	
	/**
	 * Similar to saveResults1ExpePerLine but makes one table per measure.
	 * Useful to summarize results over different sets of traces. 
	 * @param results  a table of paris X,Y where X is the directory of traces on which the result Y has been computed.
	 * @param directory_names	allows to respect an order between sets of traces
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public static void saveResults1ExpePerLineForTracesDirectory(HashMap<String,ArrayList<Result>> results, ArrayList<String> directory_names, String fileName) throws FileNotFoundException{
		 File dotFile=new File(fileName);
		 FileOutputStream fout = new FileOutputStream(dotFile);
		 PrintStream out = new PrintStream(fout);
		 
		 
		 ArrayList<String> score_names=new ArrayList<String>();
		 HashSet<String> dits=new HashSet<String>();
		 for(String rep:directory_names){
			 ArrayList<Result> lres=results.get(rep);
			 if (lres!=null){
				 for(Result res:lres){
					 ArrayList<String> sc=res.score_names;
					 for (String st:sc){
						 if (!dits.contains(st)){
							 score_names.add(st);
							 dits.add(st);
						 }
					 }
				 }
			 }
		 }
		 
		 String s="";
		 for(String sc:score_names){
			 s="Mesure : "+sc+"\n";
			 ArrayList<ArrayList<String>> cells=new  ArrayList<ArrayList<String>>();
			 int nbli=1;
			 ArrayList<String> col1=new ArrayList<String>();
			 col1.add("Algorithm");
			 cells.add(col1);
			 int i=0;
			 for(String rep:directory_names){
				 ArrayList<String> col=new ArrayList<String>();
				 col.add(rep);
				 cells.add(col);
				 ArrayList<Result> lres=results.get(rep);
				 if (lres!=null){
					 for(Result res:lres){
						if (i==0){
							col1.add(res.experiment.getDescription());
							nbli++;
						}
						HashMap<String,Double> scores=res.getScores();
						if (scores.containsKey(sc)){
							col.add(""+scores.get(sc));
						}
						else{
							col.add("NA");
						}
					 }
				 }
				 i++;
			 }
			 ArrayList<Integer> tmax=new ArrayList<Integer>();
			 for(ArrayList<String> col:cells){
				 int max=5;
				 for(String el:col){
					 if (el.length()>max){
						 max=el.length();
					 }
				 }
				 tmax.add(max);
			 }
			 for(i=0;i<nbli;i++){
				 int j=0;
				 for(ArrayList<String> col:cells){
					 String el=col.get(i);
					 int tt=el.length();
					 int ta=tmax.get(j);
					 s+=el;
					 for(int k=tt;k<ta;k++){
						   s+=" ";
					 }
					 s+="\t\t";
					 j++;
				 }
				 s+="\n";
			 }
			 
			 out.println(s+"\n\n");
			 
			 s="";
		 }
		 
		 out.close();
	}
	
	private static ArrayList<String> initArrayList(String s){
		ArrayList<String> a=new ArrayList<String>();
		a.add(s);
		return(a);
	}

}





/*package fr.lip6.meta.behavioral.eval;

import fr.lip6.meta.behavioral.algos.AlgoTransformationTracesToLTS;
import fr.lip6.meta.sdgenerator.Actor;
import fr.lip6.meta.traces.Trace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.DecimalFormat;

// Classe d'enregistrement de resultats
//
// Bon c'est un peu nimp mais tant qu'on l'utilise correctement (voir classe Evaluator), c'est ok

public class Result implements Serializable{
	EvalMeasure measure;
	AlgoTransformationTracesToLTS algo;
	//ArrayList<String> rep_traces; // Repertoire des traces utilisees. On met dans une liste pour le cas d'un result statistique (voir getStats)
	//ArrayList<String> sd_file;    // Fichier du SD concerne. On met dans une liste pour le cas d'un result statistique (voir getStats)
	ArrayList<String> progs; //
	ArrayList<Double> scores;
	ArrayList<String> score_names;
	private boolean isStats; // true si c est un result de stats
	
	public Result(AlgoTransformationTracesToLTS algo, Prog prog, EvalMeasure measure){
		//this(algo,prog.getRepTraces(),prog.getSDFile(),measure);
		this(algo,prog.toString(),measure);
	}
	public Result(AlgoTransformationTracesToLTS algo, String prog, EvalMeasure measure){
		//this(algo,initArrayList(srep_traces),initArrayList(ssd_file),measure);
		//this(algo,new ArrayList<String>({srep_traces}),new ArrayList<String>({ssd_file}),measure);
		this(algo,initArrayList(prog),measure);
	}
	
	public Result(AlgoTransformationTracesToLTS algo, ArrayList<String> progs, EvalMeasure measure){ // ArrayList<String> sd_file, EvalMeasure measure){
		this(algo,progs,measure,false);
	}
	
	private Result(AlgoTransformationTracesToLTS algo, ArrayList<String> progs, EvalMeasure measure, boolean isStats){
		this.measure=measure;
		this.algo=algo;
		//this.rep_traces=rep_traces;
		//this.sd_file=sd_file;
		this.progs=progs;
		scores=new ArrayList<Double>();
		score_names=new ArrayList<String>();
		this.isStats=isStats;
	}
	
	
	public boolean isStats(){
		return(isStats);
	}
	
	public void addScore(String name,double val){
		if(!score_names.contains(name)){
			score_names.add(name);
			scores.add(new Double(val));
		}
		else{
			System.out.println("Le score "+name+" existe deja !!!");
		}
	}
	
	public ArrayList<Double> getScores(){
		return(scores);
	}
	
	public ArrayList<String> getScoreNames(){
		return(score_names);
	}
	
	
	
	// Ajoute un Result => fusion des listes de scores
	// On suppose que les resultats fusionnes sont pour un meme algo, pour un meme prog, sur des mesures d'eval differentes, et que les noms donnes aux scores sont differents dans les differents Result fusionnes
	public void add(Result r){
		int i=0;
		for(i=0;i<r.getScoreNames().size();i++){
			addScore(r.getScoreNames().get(i),r.getScores().get(i));
		}
	}
	
	//public String toString(){
	//	String s=measure.getName()+"_Pour_"+algo.getName()+"_Sur_";
	//	if (rep_traces.size()>1){
	//		s+=rep_traces.size()+"_Progs";
	//	}
	//	else{
	//		s+=sd_file.get(0)+"(rep_traces="+rep_traces.get(0)+")";
	//	}
	//	
	//	return(s);
	}//
	
	// Retourne un Result contenant les moyennes et ecarts-types des scores contenus dans la liste de Result passee en parametres
	// (on suppose que tous les result contenus dans la liste concernent un meme algo  et une meme mesure d'eval
	public static Result getStats(ArrayList<Result> results){
		ArrayList<String> progs=new ArrayList<String>();
		//ArrayList<String> rep_traces=new ArrayList<String>();
		//ArrayList<String> sd_files=new ArrayList<String>();
		HashMap<String,Double> sums=new HashMap<String,Double>();
		HashMap<String,Integer> nbs=new HashMap<String,Integer>();
		HashMap<String,Double> sums2=new HashMap<String,Double>();
		ArrayList<String> names=new ArrayList<String>();
		int i=0;
		AlgoTransformationTracesToLTS algo=null;
		EvalMeasure mes=null;
		for(Result r:results){
			if (i==0){
				algo=r.algo;
				mes=r.measure;
			}
			progs.add(r.progs.get(0));
			//rep_traces.add(r.getTracesDir(0));
			//sd_files.add(r.getSDFile(0));
			ArrayList<Double> scores=r.scores;
			ArrayList<String> score_names=r.score_names;
			for(int j=0;j<score_names.size();j++){
				String s=score_names.get(j);
				Double score=scores.get(j);
				Integer nb;
				Double sum;
				Double sum2;
				if(nbs.containsKey(s)){
					nb=nbs.get(s);
					sum=sums.get(s);
					sum2=sums2.get(s);
					
				}
				else{
					nb=new Integer(0);
					sum=new Double(0.0);
					sum2=new Double(0.0);
					names.add(s);
				}
				nbs.put(s, new Integer(nb.intValue()+1));
				sums.put(s,new Double(sum.doubleValue()+score));
				sums2.put(s,new Double(sum2.doubleValue()+(score*score)));
				
			}
			i++;
		}
		Result res=new Result(algo,progs,mes,true);
		
		ArrayList<Double> scores=res.scores;
		ArrayList<String> score_names=res.score_names;
		
		for(String s:names){ //nbs.keySet()){
			int nb=nbs.get(s).intValue();
			double sum=sums.get(s).doubleValue();
			double sum2=sums2.get(s).doubleValue();
			if (nb>0){
				sum/=nb;
				sum2/=nb;
			}
			//System.out.println(sum2);
			//System.out.println(sum);
			//System.out.println(sum*sum);
			double ec=Math.sqrt(sum2-sum*sum);
			//System.out.println(s+" "+ec);
			if (nb>1){
				ec=ec*Math.sqrt(((double)nb)/(nb-1));
			}
			score_names.add("moyenne_"+s);
			scores.add(sum);
			score_names.add("ecart_"+s);
			scores.add(ec);
			//System.out.println(s+" "+ec);
		}
		
		return(res);
	}
	
	
	public void serialize(String filename) throws IOException{
		
			// ouverture d'un flux de sortie vers le fichier "personne.serial"
		    
			//FileOutputStream fos = new FileOutputStream(rep+"/"+toString()+".result");
			FileOutputStream fos = new FileOutputStream(filename);
			// creation d'un "flux objet" avec le flux fichier
			ObjectOutputStream oos= new ObjectOutputStream(fos);
			try {
				// serialisation : ecriture de l'objet dans le flux de sortie
				oos.writeObject(this); 
				
				// on vide le tampon
				oos.flush();
				System.out.println("le result "+toString()+" a ete serialise");
			} finally {
				//fermeture des flux
				try {
					oos.close();
				} finally {
					fos.close();
				}
			}
		
	}
	
	public static Result deserialize(String fileName) throws IOException{
		Result ret=null;
		try{
			
			// ouverture d'un flux d'entree depuis le fichier "personne.serial"
			FileInputStream fis = new FileInputStream(fileName);
			// creation d'un "flux objet" avec le flux fichier
			ObjectInputStream ois= new ObjectInputStream(fis);
			try {	
				// deserialisation : lecture de l'objet depuis le flux d'entree
				ret = (Result) ois.readObject(); 
			} finally {
				// on ferme les flux
				try {
					ois.close();
				} finally {
					fis.close();
				}
			}
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
			throw new IOException("Probleme classe");
		}
		return(ret);
	}
	
	public static ArrayList<Result> getResultsFromDir(String repertoire){
		ArrayList<Result> results=new ArrayList<Result>();
		try{
			File rep=new File(repertoire);
			File[] files=rep.listFiles();
			for(int i=0;i<files.length;i++){
			   if (files[i].getName().indexOf(".result")>0){
				   Result result=deserialize(files[i].getAbsolutePath());
				   results.add(result);
			   }
			}
		}
		catch(IOException e){
			   System.out.println(e.getMessage());
		}
		return(results);
	}
	
	
	public static void saveResults(ArrayList<Result> results, String fileName) throws FileNotFoundException{
		 File dotFile=new File(fileName);

		 FileOutputStream fout = new FileOutputStream(dotFile);

		 PrintStream out = new PrintStream(fout);
		 //String s="Pour Repertoires de traces : ";
		 String s="Pour progs : ";
		 HashSet dits=new HashSet<String>();
		 boolean areStats=false;
		 for(Result res:results){
		   if(res.isStats){
			   areStats=true;
		   }
		   for (String st : res.progs) {
		     if (!dits.contains(st)){
		    	 s+=st+" ";
		    	 dits.add(st);
		     }
		     
		   }
		 }
		 out.println(s);
		 
		 s="Pour algos : ";
		 dits=new HashSet<String>();
		 for(Result res:results){
			 String al=res.algo.getName();
			 if (!dits.contains(al)){
		         s+=al+" ";
		         dits.add(al);
			 }
		 }
		 out.println(s);
		 s="Pour mesures : "+results.get(0).measure.getName();
		 out.println(s);
		 out.println();
		 s="";
		 ArrayList<String> noms_firstcol=new ArrayList<String>();
		 if (areStats){
			 s+="Algorithm "; 
			 int taillemaxalgo=10;
			 
			 for(Result res:results){
				 int x=res.algo.getName().length();
				 if (x>taillemaxalgo){
					 taillemaxalgo=x;
				 }
			 }
			 for(int j=10;j<taillemaxalgo;j++){
				 s+=" ";
			 }
			 s+="\t\t";
			 for(Result res:results){
				 String nalgo=res.algo.getName();
				 int x=nalgo.length();
				 for(int j=x;j<taillemaxalgo;j++){
					 nalgo+=" ";
				 }
				 noms_firstcol.add(nalgo);
			 }
		 }
		 else{
			 s+="Program "; 
			 int taillemaxprog=8;
			 
			 for(Result res:results){
				 int x=res.progs.get(0).length();
				 if (x>taillemaxprog){
					 taillemaxprog=x;
				 }
			 }
			 for(int j=10;j<taillemaxprog;j++){
				 s+=" ";
			 }
			 s+="\t\t";
			 for(Result res:results){
				 String nalgo=res.progs.get(0);
				 int x=nalgo.length();
				 for(int j=x;j<taillemaxprog;j++){
					 nalgo+=" ";
				 }
				 noms_firstcol.add(nalgo);
			 }
		 }
		 
		 ArrayList<Integer> tailles=new ArrayList<Integer>();
		 for (String st : results.get(0).score_names) {
			   s+=st+"\t";
			   int tt=st.length();
			   tailles.add(tt);
		 }
		 out.println(s);
		 out.println();
		 s="";
		 DecimalFormat format = new DecimalFormat();
	     format.setMaximumFractionDigits(3);
	     
	     int ia=0;
		 for(Result res:results){
		   int i=0;
		   s+=noms_firstcol.get(ia)+"\t\t";
		   ia++;
		   for (Double st : res.scores) {
			   String val=format.format(st);
			   int tt=val.length();
			   int ta=tailles.get(i);
			   s+=val;
			   for(int j=tt;j<ta;j++){
				   s+=" ";
			   }
			   s+="\t";
			   i++;
		   }
		   s+="\n";
		 }
		 out.println(s);
		 out.close();
	}
	
	
	private static ArrayList<String> initArrayList(String s){
		ArrayList<String> a=new ArrayList<String>();
		a.add(s);
		return(a);
	}
}*/