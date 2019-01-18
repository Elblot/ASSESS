

import java.lang.String;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import java.lang.Math;

public class TraceSimilarity {


    /* calculate distance matrix */
    public static double[][] matrix(ArrayList<ArrayList<String>> newtraces){
    	int size = newtraces.size();
    	double[][] res = new double[size][size];
    	int i = 0;
    	for (ArrayList<String> t1: newtraces){
    		int j = 0;
    		for (ArrayList<String> t2: newtraces){
    			res[i][j] = 1 - similarityCoef(t1, t2);
    			++j;
    		}
    		++i;
    	}
    	return res;
    }
    
    
    private static double similarityCoef(ArrayList<String> t1, ArrayList<String> t2){
    	String[] symbol1 = parseSymbol(t1);
    	String[] symbol2 = parseSymbol(t2);
    	String[] symbol12 = new String[0];
    	String[] parameters1 = parseParameters(t1);
		String[] parameters2 = parseParameters(t2);
		String[] parameters12 = new String[0];
		for (String s: symbol1){
			if (ArrayUtils.contains(symbol2, s)){
				symbol12 = Utility.stringAdd(symbol12, s);
			}
		}
		for (String p: parameters1){
			if (ArrayUtils.contains(parameters2, p)){
				parameters12 = Utility.stringAdd(parameters12, p);
			}
		}
		double ressymbol = ((double) symbol12.length / (double) Math.min(symbol1.length, symbol2.length));
		double resparameters = ((double) parameters12.length / (double) Math.min(parameters1.length, parameters2.length));
		double res = (ressymbol + resparameters) / 2;
		return res;
    }


    private static String[] parseSymbol(ArrayList<String> t1){
    	String[] res = new String[0];
    	for(int i = 0; i < t1.size(); i++) {
			if (!(t1.get(i).contains("call_") || t1.get(i).contains("return_"))) { 
    			String symbol = t1.get(i).substring(0, t1.get(i).indexOf("("));
    			if (!ArrayUtils.contains(res, symbol)){
    				res = Utility.stringAdd(res, symbol);
    			}
			} 
		}
		return res;
    }

    
    private static String[] parseParameters(ArrayList<String> t1){
    		String[] res = new String[0];
    		for(int i = 0; i < t1.size(); i++) {
    			String tmp = t1.get(i).replace("(", ";");
				tmp = tmp.replace(")", ";");
				String[] parameters = tmp.split(";");
				int j = 0;
				for (String p: parameters){
					if ( j != 0 && !ArrayUtils.contains(res, p) &&
					   ( p.length() < 5 || !p.substring(0,5).equals("CEFSM"))){ 
		   				res = Utility.stringAdd(res, p);
					}
					++j;
				} 
			}
    		return res;
    }
}