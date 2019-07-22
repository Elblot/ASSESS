package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

public class Correlation {

	private static boolean callLoop = MainC.algo.equals("weak");
	private static ArrayList<String> identifiers = new ArrayList<String>();
 	
	//separate traces with the correlation coefficient between 2 line in a row.
	public static String analysis(String[] traces){
		String sigma;
		int j = 1;
		String fName = createDir();
		ArrayList<ArrayList<String>> alFiles = loadFiles(traces);
		
		//System.out.println(identifiers);
		buildID(alFiles);
		//System.out.println(identifiers);
		//int j = 1;
		for(ArrayList<String> alFile:alFiles) {
			String[] sequences = new String[0];
			sigma = alFile.get(0)+"\n";
			for(int i = 1; i < alFile.size(); i++) {
				if (coefficientID(alFile.get(i-1), alFile.get(i)) == 1){ /* coefficient here */
					sigma = sigma + alFile.get(i) + "\n";
				}
				else {
					sequences = Utility.stringAdd(sequences, sigma);
					sigma = alFile.get(i) + "\n";
				}
			}
			sequences = Utility.stringAdd(sequences, sigma);
			int id = Integer.parseInt(Identifier(sequences[0]));
			extract(id, sequences, j, fName);
			j+=identifiers.size();
		}
		System.out.println("identifiers = " + Arrays.deepToString(identifiers.toArray()));	
		return fName;
	}
	
	static void buildID(ArrayList<ArrayList<String>> files) {
		for(ArrayList<String> alFile:files) {
			for(int i = 1; i < alFile.size(); i++) {
				coefficientID(alFile.get(i-1), alFile.get(i));/* coefficient here */
			}
		}
	}
	
	public static ArrayList<ArrayList<String>> loadFiles(String[] traces){
		ArrayList<ArrayList<String>> alFiles = new ArrayList<ArrayList<String>>();
		String line;
		try {
			for(int i = 0; i < traces.length; i++) {
				ArrayList<String> alFile = new ArrayList<String>();
				File f = new File(traces[i]);
				BufferedReader br = new BufferedReader(new FileReader(f));
				line = br.readLine();
				while(line != null) {
					alFile.add(line);
					line = br.readLine();
				}
				alFiles.add(alFile);
				br.close();
			}
			return alFiles;
		}catch(Exception e) {}
		return null;
	}
	

    private static String createDir() {
    	String tmpName = null, fName = "RESULTS/"+MainC.dest;
    	int i = 1;
    	File x = new File(fName);
		while(x.exists()) {
			tmpName = fName+i;
			x = new File(tmpName);
			i++;
		}
		if (tmpName != null) {
			fName = tmpName;
		}
		MainC.dest = fName;
		
		fName = fName+"/trace";
		x = new File(fName);
		x.mkdirs();
		File tmp = new File(fName + "/tmp");
		tmp.mkdir();
		return fName;
	}

    public static void extract(int comp1, String[] sequences, int file, String fName){
    	try {
    		int i = 0;
    		String id;
    		int k = sequences.length;
			BufferedWriter bw;
    		if (sequences[0].contains("call")) {
    			id = Identifier(sequences[1]);
    			bw = new BufferedWriter(new FileWriter(new File(fName + "/tmp/T" + (file+Integer.parseInt(id)-1)), true));
    			bw.write(sequences[0]);
    			i = 1;
    		}
    		else {
    			id = Identifier(sequences[0]);
    			bw = new BufferedWriter(new FileWriter(new File(fName + "/tmp/T" + (file+Integer.parseInt(id)-1)), true));
    		}	
			for (int j= i+1; j < k; j++) {
				String n = "0";
				if (!sequences[j].contains("call") && !sequences[j].contains("return")) {
					n = Identifier(sequences[j]);
				}
				if (n.equals(id) && !sequences[j].contains("return")){
					n = Identifier(sequences[i+1]);
					bw.write(sequences[i]);
					if (callLoop) {
						bw.write("call_T" + n + "\n");
						bw.write("return_T" + n + "\n");
						String[] trace = {"call_T" + n + "\n"};
						String[] tracer = {"return_T" + n + "\n"};
						trace = (String[]) ArrayUtils.addAll(trace, Arrays.copyOfRange(sequences, i + 1, j));
						trace = (String[]) ArrayUtils.addAll(trace, tracer);
						extract(comp1, trace, file, fName);
					}
					else {
						String[] trace = Arrays.copyOfRange(sequences, i + 1, j);
						extract(comp1, trace, file, fName);
					}
					i = j;
				}
			}
			String n = "0";
			if (i != k-1 && !sequences[i+1].contains("call") && !sequences[i+1].contains("return")) {
				n = Identifier(sequences[i+1]);
			}
			bw.write(sequences[i]);
			if (i != k-1 && !sequences[i+1].contains("return")) {
				if (callLoop) {
					bw.write("call_T" + n + "\n");
					bw.write("return_T" + n + "\n");
					String[] trace = {"call_T" + n + "\n"};
					String[] tracer = {"return_T" + n + "\n"};
					trace = (String[]) ArrayUtils.addAll(trace, Arrays.copyOfRange(sequences, i + 1, k));
					trace = (String[]) ArrayUtils.addAll(trace, tracer);
					extract(comp1, trace, file, fName);
				}
				else {
					String[] trace = Arrays.copyOfRange(sequences, i + 1, k);
					extract(comp1, trace, file, fName);
				}
			}
			if (sequences[k-1].contains("return")) {
    			bw.write(sequences[k-1]);
    		}
			bw.close();
    	} catch (FileNotFoundException e) {
    		System.out.println("file not found " + e);
    	} catch (IOException e){
    		System.out.println("error " + e);
    	}
    }

     private static String Identifier(String sequence) {
    	String Host = "????";
    	String Dest = "????";
    	int h = sequence.indexOf("Host=");
    	if (h != -1) {
    		Host = sequence.substring(h + 5, sequence.indexOf(";", h+5));
    	}
    	int d = sequence.indexOf("Dest=");
    	if (d != -1) {
    		if ((sequence.indexOf(";", d+5) > 0) & (sequence.indexOf(";", d+5) < (sequence.indexOf(")", d+5)))) {
    			Dest = sequence.substring(d + 5, sequence.indexOf(";", d+5));
    		}
    		else {
    			Dest = sequence.substring(d + 5, sequence.indexOf(")", d+5));
    		}
    	}
    	String[] ID = {Host, Dest};
    	Arrays.sort(ID);
    	String n = Arrays.deepToString(ID);
    	System.out.println(n);;
    	return Integer.toString(identifiers.indexOf(n)+1);
    }
    
     
    /*  
     * 
     * put below the identifier parameters 
     *  
     * */ 
    /* get the correlation coefficient between two events based on ID */
    private static float coefficientID(String event1, String event2) {
    	String Host1 = "????";
    	String Dest1 = "????";
    	String Host2 = "????";
    	String Dest2 = "????";
    	int h1 = event1.indexOf("Host=");
    	if (h1 != -1) {
    		Host1 = event1.substring(h1 + 5, event1.indexOf(";", h1+5));
    	}
    	int h2 = event2.indexOf("Host=");
    	if (h2 != -1) {
    		Host2 = event2.substring(h2 + 5, event2.indexOf(";", h2+5));
    		/* 5 is the length of "Host=" */
    	}
    	int d1 = event1.indexOf("Dest=");
    	if (d1 != -1) {
    		if (event1.indexOf(";", d1+5) > 0) {
    			Dest1 = event1.substring(d1 + 5, event1.indexOf(";", d1+5));
    		}
    		else {
    			Dest1 = event1.substring(d1 + 5, event1.indexOf(")", d1+5));
    		}
    	}
    	int d2 = event2.indexOf("Dest=");
    	if (d2 != -1) {
    		if (event2.indexOf(";", d2+5) > 0) {
    			Dest2 = event2.substring(d2 + 5, event2.indexOf(";", d2+5));
    		}
    		else {
    			Dest2 = event2.substring(d2 + 5, event2.indexOf(")", d2+5));
    		}
    	}
    	String[] ID1 = {Host1, Dest1};
    	String[] ID2 = {Host2, Dest2};
    	Arrays.sort(ID1);
    	Arrays.sort(ID2);	
    	if (!identifiers.contains(Arrays.deepToString(ID1))) {
    		identifiers.add(Arrays.deepToString(ID1));
    	}
    	if (!identifiers.contains(Arrays.deepToString(ID2))) {
    		identifiers.add(Arrays.deepToString(ID2));
    	}
    	if (Arrays.equals(ID1, ID2)) {
    		return 1;
    	}
    	return 0;
    }
    
}