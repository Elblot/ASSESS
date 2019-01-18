package dataGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import utils.Keyboard;



public class TrainingDataGenerator {
	private ArrayList<DataGeneratorConfig> confs;
	private ArrayList<Integer> nbProgs;
	private ArrayList<Integer> nbTraces;
	private String outrep;
	
	
	/**
	 * 
	 * Constructor of TrainingDataGenerator.  
	 * Reads a file describing a list of files of configuration for data generation (these files must be in the format described in DataGeneratorConfig), each being associated with an interval of programs ids to product and the number of traces for each
	 * This input file must be in the following format (one configuration per line) :
	 * 
	 * <number of programs of this kind to be generated>;<number of traces for each program>;<file path>;<anything you want (not considered)> 
	 * 
	 * @param outputRep	The output directory for the generated data
	 * @param fileConfs The input file containing the list of configuration files to use
	 */
	public TrainingDataGenerator(String outputRep,String fileConfs) throws Exception{
		this(outputRep);
		lireFileConfs(fileConfs);
		
	}
	
	/**
	 * 
	 * Constructor of TrainingDataGenerator. 
	 * 
	 * @param outputRep The output directory for the generated data
	 */
	public TrainingDataGenerator(String outputRep){
		confs=new ArrayList<DataGeneratorConfig>();
		nbProgs=new ArrayList<Integer>();
		nbTraces=new ArrayList<Integer>();
		this.outrep=outputRep;
		
	}
	public void addTrainingConf(DataGeneratorConfig conf, int nbProgs, int nbTraces){
		this.confs.add(conf);
		this.nbProgs.add(nbProgs);
		this.nbTraces.add(nbTraces);
	}
	
	
	private void lireFileConfs(String filename) throws Exception{
		BufferedReader r;
        String line;
        DataGeneratorConfig.prompt=false;
        r = new BufferedReader(new FileReader(filename)) ;
        while((line=r.readLine()) != null) {
        	  String[] s=line.split(";");
        	  if(s.length<3){
        		  System.out.println("Format Problem with input of trainingDataGenerator : line ignored : "+line);
        		  continue;
        	  }
        	  int nbP=Integer.parseInt(s[0]);
        	  int nbT=Integer.parseInt(s[1]);
        	  DataGeneratorConfig gen=new DataGeneratorConfig(s[2]);
        	  confs.add(gen);
        	  nbProgs.add(nbP);
        	  nbTraces.add(nbT);
        	  
        	  
          }
          r.close();
	}
	
	public void genereTrainingData()  throws IOException{
		File f=new File(outrep);
		if (f.exists()){
			Reader reader = new InputStreamReader(System.in);
			BufferedReader input = new BufferedReader(reader);
			System.out.print("Warning : "+outrep+" already exists, overwrite ? (Y/N)");
			String ok = input.readLine();
			if ((ok.compareTo("Y")!=0) && (ok.compareTo("y")!=0)){return;}
			DataGenerator.deleteRecursive(f);
			
		}
		f.mkdirs();
		for(int i=0;i<confs.size();i++){
			DataGeneratorConfig conf=confs.get(i);
			DataGenerator gen=new DataGenerator(conf);
			gen.genere("config"+i+"_", nbProgs.get(i), nbTraces.get(i), outrep, false);
			
		}
		print(outrep+"/configs.txt");
	}
	private void print(String filename)  throws IOException{
		File f=new File(filename);
		
		PrintWriter ecrivain =  new PrintWriter(new BufferedWriter(new FileWriter(filename)));
		for(int i=0;i<confs.size();i++){
			ecrivain.println(nbProgs.get(i)+";"+nbTraces.get(i)+";"+"./"+f.getParent()+"/"+"config"+i+".txt");
		}
	    ecrivain.close();
	}
	
	public static void main(String[] args){
		try{

			DataGeneratorConfig.prompt=false;
			
			
			String outdir=Keyboard.saisirLigne("Output Directory?");
			TrainingDataGenerator gen=new TrainingDataGenerator(outdir); //"./train1/configs.txt");
			int i=1;
			boolean ok=true;
			while(ok){
				String c=Keyboard.saisirLigne("Configuration File "+i+"?");
				DataGeneratorConfig conf=new DataGeneratorConfig(c);
				
				int nbp=Keyboard.enterInt("Nb Progs to Generate?");
				int nbt=Keyboard.enterInt("Nb Traces per Prog and Type of Trace?");
				gen.addTrainingConf(conf, nbp, nbt);
				String r=Keyboard.saisirLigne("Add another Data Generator Config ? (y/n)");
				if((r.compareTo("y")!=0) && (r.compareTo("Y")!=0)){
					ok=false;
				}
				i++;
			}
			gen.genereTrainingData();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
