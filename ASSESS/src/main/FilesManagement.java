package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import traces.Trace;

public class FilesManagement {

	/* sort files in the folder to simplify the covering next */
	public static void sortFile(String dest) throws IOException {
		File repertoire = new File(dest+"/trace/tmp/");		
		File[] files = repertoire.listFiles();
		Arrays.sort(files, new FileComparator());
		int j = 1;
		for (File f: files) {
			InputStream input = new FileInputStream(f);
			File out = new File(dest+"/trace/T"+j);
			OutputStream output = new FileOutputStream(out);
			IOUtils.copy(input, output);
			f.delete();
			input.close();
			output.close();
			j++;
		}
		repertoire.delete();
	}

	/*get list of traces */   
	public static String[] getTraces(String dir){
		File d = new File(dir);
		String[] traces = null;
		if (d.exists()) {
			if (d.isDirectory()) {
				traces = d.list();
				String[] tracesP = new String[traces.length];
				for(int i = 0; i<traces.length; i++) {
					tracesP[i] = MainC.dir+"/"+traces[i];
				}
				return tracesP;
			}
		}
		return traces;
	}

	public static void BuildTi(String dest, ArrayList<Trace> traces, int i) {
		File dir = new File(dest + "/trace/C" + i + "/");
		dir.mkdir();
		int j = 1;
		for (Trace trace: traces) {
			try {
				BufferedWriter bw;
				bw = new BufferedWriter(new FileWriter(new File(dest+"/trace/C" + i + "/T" + j), true));
				bw.write(trace.toString());
				bw.close();
			}catch (FileNotFoundException e) {
				System.out.println("file not found " + e);
			}catch (IOException e){
				System.out.println("error " + e);
			}
			j++;
		}   	
	}

}
