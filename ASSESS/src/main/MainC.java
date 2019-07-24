package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fsa.FSA;
import fsa.GenerateDOT;
import miners.KTail.KTail;
import traces.Trace;


public class MainC {
	
	static String algo;
	static int rank = 2;
	static float coeff = (float) 0.5;
	static String dir;
	static boolean timerMode;
	static boolean tmp;
	static String dest;
	static boolean hide;
	
	//main entry of the program 
	public static void main(String[] args) throws Exception {
		final long timeProg1 = System.currentTimeMillis();
		KtailOptions.setOptions(args);
		String[] tracesF = FilesManagement.getTraces(dir);
		
		//1st part : Trace analysis
		final long timeCor1 = System.currentTimeMillis();
		String s = Correlation.analysis(tracesF);
		final long timeCor2 = System.currentTimeMillis();
		
		
		//2nd part : synchronization : grouping
		final long timeClust1 = System.currentTimeMillis();
		FilesManagement.sortFile(dest);
		final long timeClust2 = System.currentTimeMillis();
		Group c = new Group(s);
		ArrayList<ArrayList<Trace>> alTraces = c.Synchronization();
		
		
		//3rd part : synchronization : KTail
		final long timeKTail1 = System.currentTimeMillis();
		KTail instance = new KTail(rank, algo);
		int i = 1;
		for (ArrayList<Trace> traces : alTraces) {
			FSA test = instance.transform(traces);
			if(hide) {
				test.hideCall();
			}
			FilesManagement.BuildTi(dest, traces, i);			
	    	GenerateDOT.printDot(test, dest+"/C"+i+"tmp.dot");
	    	i++;
		}
		final long timeKTail2 = System.currentTimeMillis();
		
		//4th part : parser
		final long timePars1 = System.currentTimeMillis();
		
		int j;
		Pattern pat = Pattern.compile(".*C\\d+tmp.dot");
		File dossier = new File(dest);
		if (!dossier.exists()) {
		}
		File[] racine = dossier.listFiles();
		for (File dot : racine) {
			String dotStr = dot.toString();
			Matcher m = pat.matcher(dotStr);
			if(m.matches()) {
				j = Integer.parseInt(dotStr.substring(MainC.dest.length()+2, dotStr.length()-7));
				ParserDot parser = new ParserDot(j);
				String fileName = parser.parser(dot);
				GraphExporter.generatePngFileFromDotFile(fileName);
				if(!tmp) {
					dot.delete();
				}
			}
		}
		final long timePars2 = System.currentTimeMillis();
		
		final long timeProg2 = System.currentTimeMillis();
		if (timerMode) {
			System.out.println("Correlation Duration: " + (timeCor2 - timeCor1) + " ms");
			System.out.println("Clustering Duration: " + (timeClust2 - timeClust1) + " ms");
			System.out.println("KTail Duration: " + (timeKTail2 - timeKTail1) + " ms");
			System.out.println("Parser Duration: " + (timePars2 - timePars1) + " ms");
            System.out.println("Program Duration: " + (timeProg2 - timeProg1) + " ms");
        }	
		
		File x = new File(MainC.dest+"/RESULTAT.txt");
		if(x.exists()) {
			BufferedWriter br = new BufferedWriter(new FileWriter(x, true));
			br.write("--------------------------------\nTOTAL :\n"+ParserDot.nbEtatTot+" States\n"
				+ParserDot.nbTransitionTot+" Transitions\n"+ "Correlation Duration: " 
				+ (timeCor2 - timeCor1) + " ms\nClustering Duration: " + (timeClust2 - timeClust1) 
				+ " ms\n"+ "KTail Duration: " + (timeKTail2 - timeKTail1) + " ms\nParser Duration: " 
				+ (timePars2 - timePars1) + " ms\n"+ "Program Duration: " + (timeProg2 - timeProg1) + " ms\n");
			br.close();
		}
	}
	
}
