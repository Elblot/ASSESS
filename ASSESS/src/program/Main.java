/*
 *  Main.java
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


package program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;

import dataGenerator.Etat_Prog;
import dataGenerator.StrBlocChoice;
import dataGenerator.Traces_generator_from_Main;
import dataGenerator.VocabulaireVideException;
import fsa.*;




import traces.Statement;
import traces.Trace;
import traces.ObjectInstance;

/**
 * 
 * Entry point of any program.
 * 
 * @author Sylvain Lamprier
 *
 */
public class Main extends BlockList {
	private static final long serialVersionUID = 1L;
	
	protected ArrayList<Actor> acteurs=null; // acteurs du SD
	protected int longueur_trace_max=100;
	protected int nb_vars_etat=20;
	protected transient FSA no_epsilon_fsa=null;
	private boolean valid=false;
	
	// Pour construction manuelle
	public Main(ArrayList<Block> blocs, ArrayList<Actor> acteurs){
		this(blocs,acteurs,100);
	}
	public Main(ArrayList<Actor> acteurs){
		this(new ArrayList<Block>(), acteurs);
	}
	public Main(ArrayList<Actor> acteurs,int long_trace){
		this(new ArrayList<Block>(), acteurs, long_trace);
	}
	public Main(ArrayList<Block> blocs, ArrayList<Actor> acteurs,int long_trace){
		super(blocs,0);
	    this.acteurs=acteurs;
		//setProfondeur(0);
		longueur_trace_max=long_trace;
		//toLTS();
		//no_epsilon_lts.computeUnifDistribution(longueur_trace_max);
		nb_min=1;
		valid=false;
		affecte=true;
	}
	public Main(ArrayList<Block> blocs, ArrayList<Actor> acteurs,int long_trace,int nb_vars_etat){
		this(blocs,acteurs,long_trace);
		this.nb_vars_etat=nb_vars_etat;
	}
	// Pour generation automatique
	public Main(StrBlocChoice stc,Actor last,HashSet<Statement> discard,int profondeur,ArrayList<Actor> acteurs) throws VocabulaireVideException{
		this(stc,last,discard,profondeur,acteurs,1000,20);
	}
	public Main(StrBlocChoice stc,Actor last,HashSet<Statement> discard,int profondeur,ArrayList<Actor> acteurs,int long_trace,int nb_vars_etat) throws VocabulaireVideException{
		super(stc,last,discard,profondeur);
		System.out.println("Generation Structure Ok");
		 this.acteurs=acteurs;
		 longueur_trace_max=long_trace;
		 this.nb_vars_etat=nb_vars_etat;
		 toFSA();
		 System.out.println("Construction LTS Ok");
		 no_epsilon_fsa.computeUnifDistribution(longueur_trace_max);
		 System.out.println("Uniform Distribution Computation Ok");
		 valid=true;
	}
	
	private void validate(){
		if (!valid){
			setProfondeur(0);
			getFSA();
			valid=true;
		}
		
	}
	
	/*public void setObjects(String[] objects){
		 this.acteurs=acteurs;
	}
	
	public String[] getObjects(){
		return(objects);
	}*/
	
	public int getNb_vars_etat(){
		return(this.nb_vars_etat);
	}
	
	public void save(String fileName) throws FileNotFoundException{
		validate();
		File dotFile =new File(fileName);

		 FileOutputStream fout = new FileOutputStream(dotFile);

		 PrintStream out = new PrintStream(fout);

		 for (Actor ac : acteurs) {
			out.println(ac);
		}
		out.println();
		String ret=""+this;
		//System.out.println(ret);
		out.print(ret);
		//System.out.println("@@How much stringliteral: "+counter);
		out.close();
		
	}
	
	public void serialize(String fileName) throws IOException{
		
		    validate();
			// ouverture d'un flux de sortie vers le fichier "personne.serial"
			FileOutputStream fos = new FileOutputStream(fileName);

			// creation d'un "flux objet" avec le flux fichier
			ObjectOutputStream oos= new ObjectOutputStream(fos);
			try {
				// serialisation : ecriture de l'objet dans le flux de sortie
				oos.writeObject(this); 
				
				// on vide le tampon
				oos.flush();
				//System.out.println("le bloc a ete serialise");
			} finally {
				//fermeture des flux
				try {
					oos.close();
				} finally {
					fos.close();
				}
			}
		
	}
	
	public static Main deserialize(String fileName) { //throws IOException{
		Main ret=null;
		try{
			
			// ouverture d'un flux d'entree depuis le fichier "personne.serial"
			FileInputStream fis = new FileInputStream(fileName);
			// creation d'un "flux objet" avec le flux fichier
			ObjectInputStream ois= new ObjectInputStream(fis);
			try {	
				// deserialisation : lecture de l'objet depuis le flux d'entree
				ret = (Main) ois.readObject(); 
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
			//throw new IOException("Probleme classe");
		}
		catch(IOException e){
			e.printStackTrace();
			
		}
		return(ret);
	}
	
	public Trace getBiasedTrace_EtatProg(double p_random_effect){
		//System.out.println("Main");
		validate();
		Instruction.p_random_effect=p_random_effect;
		Etat_Prog.n_vars=nb_vars_etat;
		Etat_Prog etat=new Etat_Prog();
		Trace ret=new Trace();
		genereBiasedTrace(ret,etat);
		//System.out.println("End Main");
		return(ret);
	}
	
	public Trace getBiasedTrace(){
		validate();
		//System.out.println("Main");
		Trace ret=new Trace();
		genereBiasedTrace(ret);
		//System.out.println("End Main");
		return(ret);
	}
	public Trace getTrace(){
		validate();
		//System.out.println("Main");
		Trace ret=new Trace();
		genereTrace(ret);
		//System.out.println("End Main");
		return(ret);
	}
	
	public FSA toFSA(){
		buildFSA();
		//GenerateDOT.printDot(lts, "./blocs_sd_model.dot");
		EpsilonRemover.removeEpsilon(fsa);
		no_epsilon_fsa=fsa;
		no_epsilon_fsa.computeUnifDistribution(longueur_trace_max);
		return(no_epsilon_fsa);
	}
	public FSA getFSA(){
		if(no_epsilon_fsa==null){
			toFSA();
		}
		return(no_epsilon_fsa);
	}
	/*public LTS getNoEpsilonLTS(){
		
		if(no_epsilon_lts==null){
			LTS lt=getLTS();
			EpsilonRemover.removeEpsilon(lt);
			no_epsilon_lts=lt;
		}
		return(lts);
	}*/
	public int getLongueurTraceMax(){
		return(longueur_trace_max);
	}
	
	// Pour creation manuelle 
	public boolean saveModel(String srepi){ // throws Exception{
		validate();
		try{
		   return(saveModel(srepi,"Model"));
		}
		catch(Exception e){
			System.out.println("Saving Problem : \n"+e);
			return(false);
		}
	}
	
	public boolean saveModel(String rep,String nom_prog) throws Exception{
		validate();
		 String srepi=rep+"/"+nom_prog;
		 File repi = new File (srepi);
		 /*if (repi.exists()){
			 Reader reader = new InputStreamReader(System.in);
			 BufferedReader input = new BufferedReader(reader);
			 System.out.print("Warning : Directory "+srepi+" already exists, overwrite ? (Y/N)");
			 String ok = input.readLine();
			 if ((ok.compareTo("Y")!=0) && (ok.compareTo("y")!=0)){return false;}
			 deleteRecursive(repi);
		 }*/
		 
		 repi.mkdirs();
		 GenerateDOT.printDot(no_epsilon_fsa, srepi+"/blocs_sd_model.dot");
		 save(srepi+"/blocs_sd_model.txt");
	     serialize(srepi+"/blocs_sd_model.sd");
	     return(true);
	}
	
	public void genereAllTraces(String rep,int nb_traces,boolean unbiased, boolean biased, boolean uniform, boolean etat_traces_a, boolean etat_traces_b, boolean etat_traces_c, boolean etat_traces_d){ // throws IOException{
		validate();
		Traces_generator_from_Main.unbiased_traces=unbiased?1:0;
		Traces_generator_from_Main.biased_traces=biased?1:0;
		Traces_generator_from_Main.uniform_traces=uniform?1:0;
		Traces_generator_from_Main.stateVector_0=etat_traces_a?1:0;
		Traces_generator_from_Main.stateVector_02=etat_traces_b?1:0;
		Traces_generator_from_Main.stateVector_05=etat_traces_c?1:0;
		Traces_generator_from_Main.stateVector_1=etat_traces_d?1:0;
		genereAllTraces(rep,nb_traces);
	}
	
	// Pour execution manuelle
	public void genereAllTraces(String srepi, int nb_traces){ // throws IOException{
		validate();
		try{
		     Traces_generator_from_Main.genereAllTraces(srepi+"/Model", nb_traces, this);
		     System.out.println("Traces Generation Ok");
		}
		catch(IOException e){
			System.out.println("Traces generation problem: \n"+e);
		}
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
