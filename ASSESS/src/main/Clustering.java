package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.SingleLinkage;
import traces.Method;
import traces.Statement;
import traces.Trace;

public class Clustering {
	double similarity = 0.4; // clustering initial traces
	String s;
	int sizeTracei;
	int[]clusters;
	
	
	public Clustering(String s) {
		this.s = s;
	}
	
	
	public ArrayList<ArrayList<Trace>> clustering() throws Exception{
		ArrayList<ArrayList<String>> newtraces = addFileClust();
		try {
        	double[][] matrix = TraceSimilarity.matrix(newtraces);
        	SingleLinkage link = null;
        	link = new SingleLinkage(matrix);
        	HierarchicalClustering clusterise = new HierarchicalClustering(link);
        	clusters = clusterise.partition(similarity);
        }catch (IllegalArgumentException e) {
            clusters = new int[newtraces.size()];
        }
		ArrayList<ArrayList<Trace>> alTraces = finalClustering(clusters, newtraces);
		return alTraces;
	}
	
	
	//read file to stock lines in an ArrayList.
	public ArrayList<ArrayList<String>> addFileClust() throws Exception {
		int n = 1;
		ArrayList<ArrayList<String>> newtraces = new ArrayList<ArrayList<String>>();
        File f = new File(s + "/trace" + n);
        while (f.exists()){
        	BufferedReader br = new BufferedReader(new FileReader(f));
        	ArrayList<String> alString = new ArrayList<String>();
        	String line = br.readLine();
        	while(line != null) {
        		alString.add(line);
        		line = br.readLine();
        	}
        	br.close();
            newtraces.add(alString);
            ++n;
            f = new File(s + "/trace" + n);
        }
        sizeTracei = newtraces.size();
        switch(MainC.algo) {
        case "weak":
        case "strong":
			int k = 1;
	        File Tn = new File(s + "/T" + k);
	        while (Tn.exists()){
	        	BufferedReader br = new BufferedReader(new FileReader(Tn));
	        	ArrayList<String> alString = new ArrayList<String>();
	        	String line = br.readLine();
	        	while(line != null) {
	        		alString.add(line);
	        		line = br.readLine();
	        	}
	        	br.close();
	            newtraces.add(alString);
	            ++k;
	            Tn = new File(s + "/T" + k);
	        }
        }
        return newtraces;
	}
	

	
	//sort files in a ArrayList (depends of the algorithm)
	public ArrayList<ArrayList<Trace>> finalClustering(int[] clusters, ArrayList<ArrayList<String>> newtraces){
		int nbClust = Utility.maxArray(clusters);
		ArrayList<ArrayList<Trace>> alTrace = new ArrayList<ArrayList<Trace>>();
    	for(int i = 0; i <= nbClust; i++) {
    		ArrayList<Trace> a = new ArrayList<Trace>();
        	for(int j = 0; j < clusters.length; j++) {
        		if (clusters[j] == i) {
        			if (j >= sizeTracei) {
        				Trace t = fileToTrace(s + "/T" + (j - sizeTracei +1));
        				a.add(t);
        			}
        			else {
        				Trace t = fileToTrace(s + "/trace" + (j+1));
        				a.add(t);
        			}
        		}
        	}
        	alTrace.add(a);
        }
    	
    	switch(MainC.algo) {
    	case "strict" :
    		int i = 1;
    		Trace t = fileToTrace(s + "/T" + (i));
    		while(t!=null) {
    			ArrayList<Trace> a = new ArrayList<Trace>();
    			a.add(t);
    			alTrace.add(a);
    			i++;
    			t = fileToTrace(s + "/T" + (i));
    		}
    	}
    	return alTrace;
	}
	
	
	//read files to transform them in Trace.
	private Trace fileToTrace(String file) {
		File f = new File(file);
		if(!f.exists()) {
			return null;
		}
		Trace trace = new Trace();
		int i, j = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line2, line = br.readLine();
			while (line != null) {
				if (line.contains("call")) {
					String str = line.substring(line.indexOf("T")+1);
					i = Integer.parseInt(str);
					switch(MainC.algo) {
					case "weak":
						j = clusters[sizeTracei+i-1]+1;
						break;
					case "strong":
						line2 = br.readLine();
						j = clusters[sizeTracei+i-1]+1;
						if(line2 != null && line2.contains("return")) {
							line = br.readLine();
						}
						else {
							line = "call_C"+j;
							Method m = new Method(line);
							Statement st = new Statement(m);
							trace.add(st);
							line = line2;
						}
						continue;						
					case "strict":
						j = Utility.maxArray(clusters) +1 +i;
						break;
					}
					line = "call_C"+j;
				}
				if (line.contains("return")) {
					String str = line.substring(line.indexOf("T")+1);
					i = Integer.parseInt(str);
					switch(MainC.algo) {
					case "weak":
					case "strong":
						j = clusters[sizeTracei+i-1]+1;
						break;
						
					case "strict":
						j = Utility.maxArray(clusters) +1 +i;
						break;
					}
					line = "call_C"+j;
					line = "return_C"+j;	
				}
				Method m = new Method(line);
				Statement st = new Statement(m);
				trace.add(st);
				line = br.readLine();
			}
			br.close();
		}
		catch(Exception e) {
			System.out.println("pb bufferedReader fileToTrace");
			System.exit(3);
		}
		return trace;
	}
	
}
