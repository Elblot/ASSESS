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

import traces.Method;
import traces.Statement;
import traces.Trace;

public class Groupold {

	public static ArrayList<ArrayList<Trace>> Synchronization(String s) throws Exception {
		int index = 0;
		ArrayList<String[]> components = new ArrayList<String[]>();
		ArrayList<ArrayList<String>> newtraces = addFileClust(s);
		int[] clusters = new int[newtraces.size()];
		for (ArrayList<String> trace : newtraces) {
			String[] compnb = GetNumber(trace);
			if (!NewContains(components, compnb)) {
				components.add(compnb);
			}
			clusters[index] = NewIndexOf(components, compnb);
			index++;
		}
		System.out.println(Arrays.deepToString(components.toArray()));
		System.out.println(Arrays.toString(clusters));
		//ArrayList<ArrayList<Trace>> alTraces = finalClustering(clusters, newtraces, s);
		//return alTraces;
		return null;
	}

	public static boolean NewContains(ArrayList<String[]> components, String[] compnb) {
		for (String[] component : components) {
			if (Arrays.equals(component, compnb)) {
				return true;
			}
		}
		return false;
	}

	public static int NewIndexOf(ArrayList<String[]> components, String[] compnb) {
		int index = 0;
		for (String[] component : components) {
			if (Arrays.equals(component, compnb)) {
				return index;
			}
			index++;
		}
		return -9000;
	}

	public static String[] GetNumber(ArrayList<String> trace) {
		String Host = "????";
		String Dest = "????";
		String event = "";
		int index = 0;
		while (event == "") {
			if (trace.get(index).contains("call_") || trace.get(index).contains("return_")) {
				index++;
			} else {
				event = trace.get(index);
			}
		}
		int h = event.indexOf("Host=");
		if (h != -1) {
			Host = event.substring(h + 5, event.indexOf(";", h + 5));
		}
		int d = event.indexOf("Dest=");
		if (d != -1) {
			Dest = event.substring(d + 5, event.indexOf(";", d + 5));
		}
		String[] ID = { Host, Dest };
		Arrays.sort(ID);
		return ID;
	}

	public static ArrayList<ArrayList<String>> addFileClust(String s) throws Exception {
		int n = 1;
		ArrayList<ArrayList<String>> newtraces = new ArrayList<ArrayList<String>>();
		File f = new File(s + "/trace" + n);
		while (f.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(f));
			ArrayList<String> alString = new ArrayList<String>();
			String line = br.readLine();
			while (line != null) {
				alString.add(line);
				line = br.readLine();
			}
			br.close();
			newtraces.add(alString);
			++n;
			f = new File(s + "/trace" + n);
		}
		// sizeTracei = newtraces.size();
		int k = 1;
		File Tn = new File(s + "/T" + k);
		while (Tn.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(Tn));
			ArrayList<String> alString = new ArrayList<String>();
			String line = br.readLine();
			while (line != null) {
				alString.add(line);
				line = br.readLine();
			}
			br.close();
			newtraces.add(alString);
			++k;
			Tn = new File(s + "/T" + k);
		}
		return newtraces;
	}

	// sort files in a ArrayList (depends of the algorithm)
	public ArrayList<ArrayList<Trace>> finalClustering(int[] clusters, ArrayList<ArrayList<String>> newtraces,
			String s) {
		int nbClust = Utility.maxArray(clusters);
		ArrayList<ArrayList<Trace>> alTrace = new ArrayList<ArrayList<Trace>>();
		int sizeTracei = 1;
		File f = new File(s + "/trace" + sizeTracei);
		while (f.exists()) {
			++sizeTracei;
			f = new File(s + "/trace" + sizeTracei);
		}
		for (int i = 0; i <= nbClust; i++) {
			ArrayList<Trace> a = new ArrayList<Trace>();
			for (int j = 0; j < clusters.length; j++) {
				if (clusters[j] == i) {
					if (j >= sizeTracei) {
						Trace t = fileToTrace(s + "/T" + (j - sizeTracei + 1), sizeTracei);
						a.add(t);
					} else {
						Trace t = fileToTrace(s + "/trace" + (j + 1), sizeTracei);
						a.add(t);
					}
				}
			}
			alTrace.add(a);
		}
		return alTrace;
	}

	// read files to transform them in Trace.
	private Trace fileToTrace(String file, int sizeTracei) {
		File f = new File(file);
		if (!f.exists()) {
			return null;
		}
		Trace trace = new Trace();
		int i, j = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line2, line = br.readLine();
			while (line != null) {
				if (line.contains("call")) {
					String str = line.substring(line.indexOf("T") + 1);
					i = Integer.parseInt(str);
					line2 = br.readLine();
					//j = clusters[sizeTracei + i - 1] + 1;
					if (line2 != null && line2.contains("return")) {
						line = br.readLine();
					} else {
						line = "call_C" + j;
						Method m = new Method(line);
						Statement st = new Statement(m);
						trace.add(st);
						line = line2;
					}
					continue;
				}
				if (line.contains("return")) {
					String str = line.substring(line.indexOf("T") + 1);
					i = Integer.parseInt(str);
					//j = clusters[sizeTracei + i - 1] + 1;
					line = "call_C" + j;
					line = "return_C" + j;
				}
				Method m = new Method(line);
				Statement st = new Statement(m);
				trace.add(st);
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.out.println("pb bufferedReader fileToTrace");
			System.exit(3);
		}
		return trace;
	}
}
