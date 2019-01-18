/*
 *  DataGeneratorConfig.java
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
import java.util.HashMap;
/**
 * Configuration class for artificial data (programs + traces) generation. 
 *
 *<p>
 *<pre>
 * The configuration file must contains one pair variable-value per line, with the variable name and the value are separated by a = symbol 
 *
 *	EXAMPLE OF CONFIGURATION FILE :
 *
 * pcall=1.0 				 // proba of choosing a call bloc 
 * pconcat=0.1 			     // proba of choosing a concatenation bloc (bloclist) 
 * palt=0.1 				 // proba of choosing an alternative bloc 
 * ploop=0.1 				 // proba of choosing a loop bloc 
 * popt=0.1 				 // proba of choosing an optionnal bloc 
 *
 * pexist=0.3 				 // proba of choosing an existing bloc 
 * max_depth=10 			 // depth from which only call blocs (without child) can be produced (note that this depth can be exceeded by the choice of an existing bloc)
 * set_closing=1 			 // if = 1 => closing of methods are included in the structure and generated in traces
 * nb_classes=10 			 // number of classes 
 * nb_objects_per_class=1 	 // number of objects per class
 * nb_meth_per_object=10 	 // number of methods per object 
 * proportion=1.0 			 // proportion of methods to use among the nb_classes * nb_objects_per_class * nb_meth_per_objects possible 
 * 
 * nb_max_concat=10 	 	 // Maximal number of blocs in a bloclist
 * pchild=0.5 	 			 // Proba for call blocs to have a child bloc  (controls the imbrication level)
 * more=0.3 			 	 // proba to add each else in a alt bloc (execpt the first one which is always present)
 * 
 * max_trace_length=100 	 // maximal length for a trace (longer traces are possible but, if after 10 traces randomly taken from the program whose length > max_trace_length, the program is rebuilt)
 *
 * unbiased_traces=1 		 // 1 => generation of unbiased traces (each choice of a branch has the same proba)
 * biased_traces=1 		 	 // 1 => generation of biased traces (each choice has a specific proba that has been defined during the construction of the program)
 * uniform_traces=1 		 // 1=> generation of uniform traces (for each a length is randomly taken between  0 et max_trace_length, and then, a path in the program structure is uniformly taken among the possible paths of this length)
 * stateVector_0=0 		 	 // 1 => simulation of real execution traces, where successive choices depend on the current state of the program. The state of the program (represented by a vector) determines the branches to select at each branching point. The state of the program changes according to effects set for each bloc of the program. These effects are defined during the construction of the program and are always applied in the same manner (deterministic program => p_random_effect=0).
 * stateVector_0.2=1 		 // 1 => simulation of real execution traces, where successive choices depend on the current state of the program. The state of the program (represented by a vector) determines the branches to select at each branching point. The state of the program changes according to effects set for each bloc of the program. These effects are defined during the construction of the program. With a proba of 0.2, the effect of a particular bloc is not applied: a randomly chosen effect vector is applied in place (non deterministic program => p_random_effect=0.2).
 * stateVector_0.5=0 		 // 1 => simulation of real execution traces, where successive choices depend on the current state of the program. The state of the program (represented by a vector) determines the branches to select at each branching point. The state of the program changes according to effects set for each bloc of the program. These effects are defined during the construction of the program. With a proba of 0.5, the effect of a particular bloc is not applied: a randomly chosen effect vector is applied in place (non deterministic program => p_random_effect=0.5).
 * stateVector_1=0 		 	 // 1 => simulation of real execution traces, where successive choices depend on the current state of the program. The state of the program (represented by a vector) determines the branches to select at each branching point. The state of the program changes according to randomly chosen effect vectors (non deterministic program => p_random_effect=1).
 * nb_max_loops=10 		 	 //Maximal number of iterations in a loop (for stateVector traces generations)
 * nb_vars_stateVector=20 	 // Number of variables in the vector representing the state of the program
 *
 *</pre>
 *
 * @author Sylvain Lamprier
 *
 */
public class DataGeneratorConfig {
	
	/** 
	 * Configuration parameters
	 */
	protected HashMap<String,String> vars;
	static boolean prompt=true;
	
	/*protected int max_prof=10;  // profondeur a partir de laquelle on ne peut produire que des instructions (cette profondeur peut etre depassee par choix d'un bloc existant)
	protected int nb_roles=10;  // nombre de roles
	protected int nb_meth_par_role=10;  // nombre de methodes par role
	protected double proportion=1;  // proportion de mots a conserver parmi les nb_roles * nb_meth_par_role possibles
	protected int nb_max_concat=10; // Nombre maximal de blocs dans une liste de blocs
	protected double pfils=0.5; // Proba pour les Call d'avoir un fils
	protected double more=0.3;	 // probabilite d'ajouter chaque else dans un alt (a part le premier qui est automatique) 
	//double ploop;  // Proba de boucler sur les loop (pour unbiased)
	protected double p_max_loop=0.9;  // proba max de boucler sur un loop (pour biased afin d'eviter de choisir des probas trop proches de 1)
	//double popt;    // Probabilite de traiter les blocs opt (pour not biased)
	protected double pi=1;     // proba de choisir un bloc instruction 
	protected double pe=0.3;	 // proba de choisir un bloc existant 
	protected double pc=0.1;   // proba de choisir un bloc concatenation (bloclist) 
	protected double pa=0.1;   // proba de choisir un bloc alternative
	protected double pl=0.1;   // proba de choisir un bloc loop 
	protected double po=0.1;   // proba de choisir un bloc opt
	
	protected int deterministe=0; // 1 => sd determinant 
	
	protected int longueur_trace_max=100; // longueur de trace maximale
	
	
	protected int unbiased_traces=1; // 1 => traces non biaisees generees (chaque choix de branche a la meme proba)
	protected int biased_traces=1; //1 => traces biaisees generees (chaque choix de branche a une probabilite definie lors de la creation du squelette)
	protected int uniform_traces=1; // 1=> traces uniformes generees (pour chaque trace on tire une longueur possible entre 0 et longueur_trace_max puis on tire un chemin uniformement selon les traces possibles pour cette taille)
	protected int etat_traces_a=1; // 1 => traces simulees selon un etat du programme et des effets sur cet etat a chaque instruction. L etat du programme determine les branches choisies. Les effets sur l'etat sont definis lors de la creation du squelette et sont toujours appliques de la meme maniere (prog deterministe => p_random_effect=0).
	protected int etat_traces_b=1; // 1 => traces simulees selon un etat du programme et des effets sur cet etat a chaque instruction. L etat du programme determine les branches choisies. Les effets sur l'etat sont definis lors de la creation du squelette. Dans 20% des cas on ne les applique pas mais on leur substitue un effet tire aleatoirement. (prog non deterministe => p_random_effect=0.2).
	protected int etat_traces_c=1; // 1 => traces simulees selon un etat du programme et des effets sur cet etat a chaque instruction. L etat du programme determine les branches choisies. Les effets sur l'etat sont definis lors de la creation du squelette. Dans 50% des cas on ne les applique pas mais on leur substitue un effet tire aleatoirement. (prog non deterministe => p_random_effect=0.5).
	protected int etat_traces_d=1; // 1 => traces simulees selon un etat du programme et des effets sur cet etat a chaque instruction. L etat du programme determine les branches choisies. Les effets sont aleatoires. (prog non deterministe => p_random_effect=1).
	protected int nb_vars_etat=20; // Nombre de variables de l'etat du programme pour les etat traces 
	//double p_random_effect;    // Pour biased etat : Probabilite de ne pas prendre l'effet par defaut de l'instruction mais un autre effet tire alea (taux de non determinisme) => + p_random_effect est eleve moins on a de traces semblables (si p_random_effect==-1, on cree des traces pour tous les p_random_effect de 0 a 1, par pas de 0.1)
	protected int nb_max_loops=10; //Nombre maximal de passage dans la boucle (pour etat_traces)
	*/
	
	/**
	 * Reads a config file and stores variables and values in the vars table which is used by {@link DataGenerator} to produce artificial programs and traces
	 * 
	 * @param filename	The configuration file
	 * @throws Exception when filename cannot be read
	 */
	public DataGeneratorConfig(String filename) throws Exception{
		vars=new HashMap<String,String>();
		
		this.lireFicConfig(filename);
		
	}
	
	
	/**
	 * Default configuration (call toString to get spec)
	 */
	public DataGeneratorConfig(){
		vars=new HashMap<String,String>();
	}
	
	//public abstract void config();

	
	public String toString(){
		String ret="\n// *******Structure**********  //\n\n";
		ret+="pcall="+getPcall()+" \t\t\t\t // proba of choosing a call bloc \n";
		ret+="pconcat="+getPconcat()+" \t\t\t\t // proba of choosing a concatenation bloc (bloclist) \n";
		ret+="palt="+getPalt()+" \t\t\t\t // proba of choosing an alternative bloc \n";
		ret+="ploop="+getPloop()+" \t\t\t\t // proba of choosing a loop bloc \n";
		ret+="popt="+getPopt()+" \t\t\t\t // proba of choosing an optionnal bloc \n";
		ret+="pexist="+getPexist()+" \t\t\t\t // proba of choosing an existing bloc \n\n";
		
		
		ret+="max_depth="+getMax_depth()+" \t\t\t // depth from which only call blocs (without child) can be produced (note that this depth can be exceeded by the choice of an existing bloc)\n";
		ret+="set_closing="+getSetClosing()+" \t\t\t // if = 1 => closing of methods are included in the structure and generated in traces\n";
		
		ret+="nb_classes="+getNb_classes()+" \t\t\t // number of classes \n";
		ret+="nb_objects_per_class="+getNb_objects_per_class()+" \t\t\t // number of objects per class\n";
		ret+="nb_meth_per_object="+getNb_meth_per_object()+" \t\t // number of methods per object \n";
		ret+="proportion="+getProportion()+" \t\t\t // proportion of methods to use among the nb_classes * nb_objects_per_class * nb_meth_per_objects possible \n\n";
	    
		ret+="nb_max_concat="+getNb_max_concat()+" \t // Maximal number of blocs in a bloclist\n";
		ret+="pchild="+getPchild()+" \t // Proba for call blocs to have a child bloc  (controls the imbrication level)\n";
		ret+="more="+getMore()+" \t\t\t // proba to add each else in a alt bloc (execpt the first one which is always present)\n"; 
	    //ret+="derterministe="+deterministe+"  \t\t\t // SD garanti deterministe (1) ou non (0)\n";
	    
		ret+="\n// *******Traces**********  //\n\n";
	    
		ret+="max_trace_length="+getMax_trace_length()+" // maximal length for a trace (longer traces are possible but, if after 10 traces randomly taken from the program whose length > max_trace_length, the program is rebuilt)\n\n";
	    
		ret+="unbiased_traces="+getUnbiased_traces()+" // 1 => generation of unbiased traces (each choice of a branch has the same proba)\n";
		ret+="biased_traces="+getBiased_traces()+" //1 => generation of biased traces (each choice has a specific proba that has been defined during the construction of the program)\n";
		ret+="uniform_traces="+getUniform_traces()+" // 1=> generation of uniform traces (for each a length is randomly taken between  0 et max_trace_length, and then, a path in the program structure is uniformly taken among the possible paths of this length)\n";
		ret+="stateVector_0="+getStateVector_0()+" // 1 => simulation of real execution traces, where successive choices depend on the current state of the program. The state of the program (represented by a vector) determines the branches to select at each branching point. The state of the program changes according to effects set for each bloc of the program. These effects are defined during the construction of the program and are always applied in the same manner (deterministic program => p_random_effect=0).\n";
		ret+="stateVector_0.2="+getStateVector_02()+" // 1 => simulation of real execution traces, where successive choices depend on the current state of the program. The state of the program (represented by a vector) determines the branches to select at each branching point. The state of the program changes according to effects set for each bloc of the program. These effects are defined during the construction of the program. With a proba of 0.2, the effect of a particular bloc is not applied: a randomly chosen effect vector is applied in place (non deterministic program => p_random_effect=0.2).\n";
		ret+="stateVector_0.5="+getStateVector_05()+" // 1 => simulation of real execution traces, where successive choices depend on the current state of the program. The state of the program (represented by a vector) determines the branches to select at each branching point. The state of the program changes according to effects set for each bloc of the program. These effects are defined during the construction of the program. With a proba of 0.5, the effect of a particular bloc is not applied: a randomly chosen effect vector is applied in place (non deterministic program => p_random_effect=0.5).\n";
		ret+="stateVector_1="+getStateVector_1()+" // 1 => simulation of real execution traces, where successive choices depend on the current state of the program. The state of the program (represented by a vector) determines the branches to select at each branching point. The state of the program changes according to randomly chosen effect vectors (non deterministic program => p_random_effect=1).\n";
		
		//ret+="p_max_loop="+getP_max_loop()+" \t\t // proba max de boucler sur un loop (pour biased afin d'eviter de choisir des probas trop proches de 1)\n";
		ret+="nb_max_loops="+getNb_max_loops()+" \t\t //Maximal number of iterations in a loop (for stateVector traces generations)\n";
		ret+="nb_vars_stateVector="+getNb_vars_stateVector()+" \t\t // Number of variables in the vector representing the state of the program\n";
		return(ret);
	}
	
	String toStringFrench(){
		String ret="\n// *******Structure**********  //\n\n";
		ret+="pcall="+getPcall()+" \t\t\t\t // proba de choisir un bloc call de methode \n";
		ret+="pconcat="+getPconcat()+" \t\t\t\t // proba de choisir un bloc concatenation (bloclist) \n";
		ret+="palt="+getPalt()+" \t\t\t\t // proba de choisir un bloc alternative \n";
		ret+="ploop="+getPloop()+" \t\t\t\t // proba de choisir un bloc loop \n";
		ret+="popt="+getPopt()+" \t\t\t\t // proba de choisir un bloc opt \n\n";
		ret+="pexist="+getPexist()+" \t\t\t\t // proba de choisir un bloc existant \n";
		
		
		ret+="max_depth="+getMax_depth()+" \t\t\t // profondeur a partir de laquelle on ne peut produire que des instructions (cette profondeur peut etre depassee par choix d'un bloc existant)\n";
		ret+="set_closing="+getSetClosing()+" \t\t\t // if = 1 => closing of methods are included in the structure and generated in traces\n";
		
		ret+="nb_classes="+getNb_classes()+" \t\t\t // nombre de classes\n";
		ret+="nb_objects_per_class="+getNb_objects_per_class()+" \t\t\t // nombre d'objets par classe\n";
		ret+="nb_meth_per_object="+getNb_meth_per_object()+" \t\t // nombre de methodes par objet\n";
		ret+="proportion="+getProportion()+" \t\t\t // proportion de mots a conserver parmi les nb_classes * nb_objects_per_class * nb_meth_per_objects possibles \n\n";
	    
		ret+="nb_max_concat="+getNb_max_concat()+" \t // Nombre maximal de blocs dans une liste de blocs\n";
		ret+="pchild="+getPchild()+" \t // Proba pour les blocs instruction d'avoir un bloc fils (controle sur le niveau d'imbrication)\n";
		ret+="more="+getMore()+" \t\t\t // probabilite d'ajouter chaque else dans un alt (a part le premier qui est automatique)\n"; 
	    //ret+="derterministe="+deterministe+"  \t\t\t // SD garanti deterministe (1) ou non (0)\n";
	    
		ret+="\n// *******Traces**********  //\n\n";
	    
		ret+="max_trace_length="+getMax_trace_length()+" // longueur de trace maximale (si 10 traces successives tirees > longueur_trace_max, alors on reconstruit un nouveau programme)\n\n";
	    
		ret+="unbiased_traces="+getUnbiased_traces()+" // 1 => traces non biaisees generees (chaque choix de branche a la meme proba)\n";
		ret+="biased_traces="+getBiased_traces()+" //1 => traces biaisees generees (chaque choix de branche a une probabilite definie lors de la creation du squelette)\n";
		ret+="uniform_traces="+getUniform_traces()+" // 1=> traces uniformes generees (pour chaque trace on tire une longueur possible entre 0 et longueur_trace_max puis on tire un chemin uniformement selon les traces possibles pour cette taille)\n";
		ret+="stateVector_0="+getStateVector_0()+" // 1 => traces simulees selon un etat du programme et des effets sur cet etat a chaque instruction. L etat du programme determine les branches choisies. Les effets sur l'etat sont definis lors de la creation du squelette et sont toujours appliques de la meme maniere (prog deterministe => p_random_effect=0).\n";
		ret+="stateVector_0.2="+getStateVector_02()+" // 1 => traces simulees selon un etat du programme et des effets sur cet etat a chaque instruction. L etat du programme determine les branches choisies. Les effets sur l'etat sont definis lors de la creation du squelette. Dans 20% des cas on ne les applique pas mais on leur substitue un effet tire aleatoirement. (prog non deterministe => p_random_effect=0.2).\n";
		ret+="stateVector_0.5="+getStateVector_05()+" // 1 => traces simulees selon un etat du programme et des effets sur cet etat a chaque instruction. L etat du programme determine les branches choisies. Les effets sur l'etat sont definis lors de la creation du squelette. Dans 50% des cas on ne les applique pas mais on leur substitue un effet tire aleatoirement. (prog non deterministe => p_random_effect=0.5).\n";
		ret+="stateVector_1="+getStateVector_1()+" // 1 => traces simulees selon un etat du programme et des effets sur cet etat a chaque instruction. L etat du programme determine les branches choisies. Les effets sont aleatoires. (prog non deterministe => p_random_effect=1).\n";
		
		//ret+="p_max_loop="+getP_max_loop()+" \t\t // proba max de boucler sur un loop (pour biased afin d'eviter de choisir des probas trop proches de 1)\n";
		ret+="nb_max_loops="+getNb_max_loops()+" \t\t //Nombre maximal de passage dans la boucle (pour etat_traces)\n";
		ret+="nb_vars_stateVector="+getNb_vars_stateVector()+" \t\t // Nombre de variables de l'etat du programme pour les etat traces\n";
		return(ret);
	}
	
	void printConf(String rep) throws IOException{
		printConf(rep,"config");
	}
	void printConf(String rep,String name) throws IOException{
		PrintWriter ecrivain =  new PrintWriter(new BufferedWriter(new FileWriter(rep+"/"+name+".txt")));
		ecrivain.println(this.toString());
	    ecrivain.close();
	}

	int getSetClosing(){
		String val=vars.get("set_closing");
		if (val!=null){
			try{
		    	return(Integer.parseInt(val));
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de set_closing");
		    	throw e;
		    }
		}
		return(1);
	}
	
	int getMax_depth() {
		String val=vars.get("max_depth");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de max_depth");
		    	throw e;
		    }
		}
		return(10);
	}

	
	int getNb_classes() {
		String val=vars.get("nb_classes");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de nb_classes");
		    	throw e;
		    }
		}
		return(10);
	}
	
	int getNb_objects_per_class() {
		String val=vars.get("nb_objects_per_class");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de nb_objects_per_class");
		    	throw e;
		    }
		}
		return(1);
	}

	int getNb_meth_per_object() {
		String val=vars.get("nb_meth_per_object");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de nb_meth_per_object");
		    	throw e;
		    }
		}
		return(10); 
	}

	double getProportion() {
		String val=vars.get("proportion");
		if (val!=null){
			try{
		    	double ret=Double.parseDouble(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de proportion");
		    	throw e;
		    }
		}
		return(1); 
	}

	int getNb_max_concat() {
		String val=vars.get("nb_max_concat");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de nb_max_concat");
		    	throw e;
		    }
		}
		return(10); 
	}

	double getPchild() {
		String val=vars.get("pchild");
		if (val!=null){
			try{
		    	double ret=Double.parseDouble(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de pchild");
		    	throw e;
		    }
		}
		return(0.5); 
	}

	double getMore() {
		String val=vars.get("more");
		if (val!=null){
			try{
		    	double ret=Double.parseDouble(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de more");
		    	throw e;
		    }
		}
		return(0.3); 
	}

	double getP_max_loop() {
		String val=vars.get("p_max_loop");
		if (val!=null){
			try{
		    	double ret=Double.parseDouble(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de p_max_loop");
		    	throw e;
		    }
		}
		return(0.9); 
	}

	double getPcall() {
		String val=vars.get("pcall");
		if (val!=null){
			    try{
			    	double ret=Double.parseDouble(val);
			    	return(ret);
			    }
			    catch(NumberFormatException e){
			    	System.out.println("Probleme conversion valeur de pcall");
			    	throw e;
			    }
			    
		}
		return(1.0); 
	}

	double getPexist() {
		String val=vars.get("pexist");
		if (val!=null){
			   try{
			    	double ret=Double.parseDouble(val);
			    	return(ret);
			    }
			    catch(NumberFormatException e){
			    	System.out.println("Probleme conversion valeur de pexist");
			    	throw e;
			    }
		}
		return(0.3); 
	}

	double getPconcat() {
		String val=vars.get("pconcat");
		if (val!=null){
			   try{
			    	double ret=Double.parseDouble(val);
			    	return(ret);
			    }
			    catch(NumberFormatException e){
			    	System.out.println("Probleme conversion valeur de pconcat");
			    	throw e;
			    }
		}
		return(0.1); 
	}

	double getPalt() {
		String val=vars.get("palt");
		if (val!=null){
			try{
		    	double ret=Double.parseDouble(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de palt");
		    	throw e;
		    }
		}
		return(0.1); 
	}

	double getPloop() {
		String val=vars.get("ploop");
		if (val!=null){
			try{
		    	double ret=Double.parseDouble(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de ploop");
		    	throw e;
		    }
		}
		return(0.1); 
	}

	double getPopt() {
		String val=vars.get("popt");
		if (val!=null){
			try{
		    	double ret=Double.parseDouble(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de popt");
		    	throw e;
		    }
		}
		return(0.1); 
	}

	int getDeterministe() {
		String val=vars.get("determinist");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de deterministe");
		    	throw e;
		    }
		}
		return(0); 
	}

	int getMax_trace_length() {
		String val=vars.get("max_trace_length");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de max_trace_length");
		    	throw e;
		    }
		}
		return(100);
	}

	int getUnbiased_traces() {
		String val=vars.get("unbiased_traces");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de unbiased_traces");
		    	throw e;
		    }
		}
		return(0);
	}

	int getBiased_traces() {
		String val=vars.get("biased_traces");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de biased_traces");
		    	throw e;
		    }
		}
		return(0);
	}

	int getUniform_traces() {
		String val=vars.get("uniform_traces");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de uniform_traces");
		    	throw e;
		    }
		}
		return(1);
	}

	int getStateVector_0() {
		String val=vars.get("stateVector_0");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de stateVector_0");
		    	throw e;
		    }
		}
		return(0);
	}

	int getStateVector_02() {
		String val=vars.get("stateVector_0.2");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de stateVector_0.2");
		    	throw e;
		    }
		}
		return(1);
	}

	int getStateVector_05() {
		String val=vars.get("stateVector_0.5");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de stateVector_0.5");
		    	throw e;
		    }
		}
		return(0);
	}

	int getStateVector_1() {
		String val=vars.get("stateVector_1");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de stateVector_1");
		    	throw e;
		    }
		}
		return(0);
	}

	int getNb_vars_stateVector() {
		String val=vars.get("nb_vars_stateVector");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de nb_vars_stateVector");
		    	throw e;
		    }
		}
		return(20);
	}

	int getNb_max_loops() {
		String val=vars.get("nb_max_loops");
		if (val!=null){
			try{
		    	int ret=Integer.parseInt(val);
		    	return(ret);
		    }
		    catch(NumberFormatException e){
		    	System.out.println("Probleme conversion valeur de nb_max_loops");
		    	throw e;
		    }
		}
		return(10);
	}
	
	
	private void lireFicConfig(String filename) throws FileNotFoundException,IOException{
		 System.out.println("Chargement configuration");
		 HashMap<String,String> table=new HashMap<String,String>();
		 InputStream ips=new FileInputStream(filename); 
         InputStreamReader ipsr=new InputStreamReader(ips);
         BufferedReader br=new BufferedReader(ipsr);
         String ligne;
         int numligne=0;
         while ((ligne=br.readLine())!=null){
        	 numligne++;
        	 if (ligne.length()>0){
        	     String[] res=ligne.split("//");
         	     ligne=res[0].replaceAll(" ", "");
         	     ligne=ligne.replaceAll("\t", "");
         	     //System.out.println(ligne);
         	     if (ligne.length()>0){
         	    	 res=ligne.split("=");
         	    	 if (res.length==2){
         	    		 String key=res[0];
         	    		 String value=res[1];
         	    		 table.put(key, value);
         	    	 }
         	    	 else{
         	    		 System.out.println("Probleme fichier de config, ligne "+numligne+" ignoree");
         	    	 }
         	     }
        	 }
         }
         //setValuesFromTable(table);
         vars=table;
         System.out.println("Configuration chargee : \n \n "+this);
    	 
         if(prompt){
        	 //System.out.println("Config : \n \n"+this);
        	 Reader reader = new InputStreamReader(System.in);
        	 BufferedReader input = new BufferedReader(reader);
        	 System.out.print("Press a key to continue");
        	 String ok = input.readLine();
         }
	}
	/*private void setValuesFromTable(HashMap<String,String> table){
		for(String var: table.keySet()){
			if (var.compareTo("max_prof")==0){
				try{
				    max_prof=Integer.parseInt(table.get(var));
				}
				catch(Exception e){
					System.out.println("Probleme avec "+var+", variable ignoree");
				}
				continue;
			}*/
	
}
/*class SD_Random_Generator_Config1 extends SD_Random_Generator_Config{
	public void config(){ 
		/////////////////////////
		// Structure ///////////
		////////////////////////
		max_prof=10;
		nb_roles=10;  
		nb_meth_par_role=10;  
		proportion=1;
		pfils=0.5;
		more=0.3;
		nb_max_concat=10;
		
		// Poids types de blocs
		pi=1;      
		pe=0.3;	 
		pc=0.1;  
		pa=0.1; 
		pl=0.1;  
		po=0.1; 
		////////////////////////
		
		/////////////////////////
		// Traces     ///////////
		/////////////////////////
		unbiased_traces=1; 
		biased_traces=1; 
		uniform_traces=1;
		etat_traces_a=1; 
		etat_traces_b=1; 
		etat_traces_c=1; 
		etat_traces_d=1; 
		
		longueur_trace_max=100;
		p_max_loop=0.9; 
		nb_max_loops=10;
		nb_vars_etat=20;
	}
}
class SD_Random_Generator_Config2 extends SD_Random_Generator_Config{
	public void config(){ 
		/////////////////////////
		// Structure ///////////
		////////////////////////
		max_prof=10;
		nb_roles=10;  
		nb_meth_par_role=10;  
		proportion=1;
		pfils=0.5;
		more=0.3;
		nb_max_concat=10;
		
		// Poids types de blocs
		pi=1;      
		pe=0;	 
		pc=0.1;  
		pa=0.1; 
		pl=0;  
		po=0.1; 
		////////////////////////
		
		/////////////////////////
		// Traces     ///////////
		/////////////////////////
		unbiased_traces=0; 
		biased_traces=0; 
		uniform_traces=1;
		etat_traces_a=0; 
		etat_traces_b=1; 
		etat_traces_c=0; 
		etat_traces_d=0; 
		
		longueur_trace_max=100;
		p_max_loop=0.9; 
		nb_max_loops=10;
		nb_vars_etat=20;
	}
}
class SD_Random_Generator_Config3 extends SD_Random_Generator_Config{
	public void config(){
		/////////////////////////
		// Structure ///////////
		////////////////////////
		max_prof=10;
		nb_roles=10;  
		nb_meth_par_role=10;  
		proportion=1;
		pfils=0.5;
		more=0.3;
		nb_max_concat=10;
		
		// Poids types de blocs
		pi=1;      
		pe=0;	 
		pc=0.1;  
		pa=0; 
		pl=0;  
		po=0; 
		////////////////////////
		
		/////////////////////////
		// Traces     ///////////
		/////////////////////////
		unbiased_traces=0; 
		biased_traces=0; 
		uniform_traces=1;
		etat_traces_a=0; 
		etat_traces_b=1; 
		etat_traces_c=0; 
		etat_traces_d=0; 
		
		longueur_trace_max=100;
		p_max_loop=0.9; 
		nb_max_loops=10;
		nb_vars_etat=20;
	}
}
class SD_Random_Generator_Config4 extends SD_Random_Generator_Config{
	public void config(){
		/////////////////////////
		// Structure ///////////
		////////////////////////
		max_prof=10;
		nb_roles=10;  
		nb_meth_par_role=10;  
		proportion=1;
		pfils=0.5;
		more=0.3;
		nb_max_concat=10;
		
		// Poids types de blocs
		pi=1;      
		pe=0.3;	 
		pc=0.1;  
		pa=0; 
		pl=0;  
		po=0; 
		////////////////////////
		
		/////////////////////////
		// Traces     ///////////
		/////////////////////////
		unbiased_traces=0; 
		biased_traces=0; 
		uniform_traces=1;
		etat_traces_a=0; 
		etat_traces_b=1; 
		etat_traces_c=0; 
		etat_traces_d=0; 
		
		longueur_trace_max=100;
		p_max_loop=0.9; 
		nb_max_loops=10;
		nb_vars_etat=20;
	}
}
class SD_Random_Generator_Config5 extends SD_Random_Generator_Config{
	public void config(){
		/////////////////////////
		// Structure ///////////
		////////////////////////
		max_prof=10;
		nb_roles=10;  
		nb_meth_par_role=10;  
		proportion=1;
		pfils=0.5;
		more=0.3;
		nb_max_concat=10;
		
		// Poids types de blocs
		pi=1;      
		pe=0.3;	 
		pc=0.1;  
		pa=0; 
		pl=0.1;  
		po=0; 
		////////////////////////
		
		/////////////////////////
		// Traces     ///////////
		/////////////////////////
		unbiased_traces=0; 
		biased_traces=0; 
		uniform_traces=1;
		etat_traces_a=0; 
		etat_traces_b=1; 
		etat_traces_c=0; 
		etat_traces_d=0; 
		
		longueur_trace_max=100;
		p_max_loop=0.9; 
		nb_max_loops=10;
		nb_vars_etat=20;
	}
}
class SD_Random_Generator_Config6 extends SD_Random_Generator_Config{
	public void config(){
		/////////////////////////
		// Structure ///////////
		////////////////////////
		max_prof=10;
		nb_roles=10;  
		nb_meth_par_role=10;  
		proportion=1;
		pfils=0.5;
		more=0.3;
		nb_max_concat=10;
		
		// Poids types de blocs
		pi=1;      
		pe=0;	 
		pc=0.1;  
		pa=0; 
		pl=0.1;  
		po=0; 
		////////////////////////
		
		/////////////////////////
		// Traces     ///////////
		/////////////////////////
		unbiased_traces=0; 
		biased_traces=0; 
		uniform_traces=1;
		etat_traces_a=0; 
		etat_traces_b=1; 
		etat_traces_c=0; 
		etat_traces_d=0; 
		
		longueur_trace_max=100;
		p_max_loop=0.9; 
		nb_max_loops=10;
		nb_vars_etat=20;
	}
}*/
