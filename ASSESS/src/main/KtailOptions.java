package main;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class KtailOptions {
	
	public static void setOptions(String[] args) throws Exception {
		final Options options = configParameters();
	    final CommandLineParser parser = new DefaultParser();
	    try {
		    final CommandLine line = parser.parse(options, args);
		    
		    MainC.algo = line.getOptionValue("algo");
		    MainC.dir = line.getOptionValue("dir");
		    MainC.dest = line.getOptionValue("dest");
		    
		    if(!MainC.algo.equals("strict") && !MainC.algo.equals("strong") && !MainC.algo.equals("weak")) {
		    	System.out.println(MainC.algo);
		    	throw new Exception();
		    }
		    
		    // Rank
		    boolean hasRank = line.hasOption("rank");
		    if (hasRank) {
		    	final String rankFromParamaters = line.getOptionValue("rank", "");
		    	try {
		    		MainC.rank = Integer.valueOf(rankFromParamaters);
		    		if (MainC.rank <= 1) {
		    			System.err.println("rank must be > 1");
		    			System.exit(3);
		    		}
		    	} catch (Exception e) {
		    		System.err.println("Bad parameter: rank");
		    		System.exit(3);
		    	}
		    }
		    
		    boolean hasCoeff = line.hasOption("coeff");
		    if (hasCoeff) {
		    	final String coeffFromParamaters = line.getOptionValue("coeff", "");
		    	try {
		    		MainC.coeff = Float.valueOf(coeffFromParamaters);
		    		if (MainC.coeff <= 0 || MainC.coeff >= 1) {
		    			System.err.println("coeff must be >0 and <1");
		    			System.exit(3);
		    		}
		    	} catch (Exception e) {
		    		System.err.println("Bad parameter: coeff");
		    		System.exit(3);
		    	}
		    }
		    
		    
		    // Timer
		    boolean timerMode = line.hasOption("timer");
		    if(timerMode) {
		    	MainC.timerMode = true;
		    }
		    
		    // HideCall
		    boolean hideCall = line.hasOption("hide");
		    if(hideCall) {
		    	MainC.hide = true;
		    }
		    
		    boolean tmp = line.hasOption("tmp");
		    if(tmp) {
		    	MainC.tmp = true;
	    }
	    }catch(Exception e) {
	    	System.out.println("Usage : MainC -d <directory> -a <algorithm> -o <destination>\n"
	    			+ "algorithm : weak, strong, strict\n"
	    			+ "Options :\n"
	    			+ "-t\tshow the duration of each step of the program\n"
	    			+ "-k\tchoose the number of k-futur\n"
	    			+ "-w\tshow temporal files used to make .dot files\n"
	    			+ "-c\thide call/return transitions");  
	    	System.exit(1);}
	}
	
	private static Options configParameters() {
	
		final Option dirFileOption = Option.builder("d")
				.longOpt("dir")
				.desc("directory to use")
				.hasArg(true)
				.argName("dir")
				.required(true)
				.build();
		
	    final Option algoFileOption = Option.builder("a") 
	            .longOpt("algo") //
	            .desc("Algorithm: strict (strict) / strong (strong) / weak (weak)") 
	            .hasArg(true) 
	            .argName("algo")
	            .required(true) 
	            .build();
	
	    final Option rankFileOption = Option.builder("k") 
	            .longOpt("rank") 
	            .desc("set the number of k-future") 
	            .hasArg(true) 
	            .argName("numeric") 
	            .required(false) 
	            .build();
	
	    final Option timerFileOption = Option.builder("t") 
	            .longOpt("timer") 
	            .desc("Timer") 
	            .hasArg(false) 
	            .required(false) 
	            .build();
	    
	    final Option tmpOption = Option.builder("w")
				.longOpt("tmp")
				.desc("show temporal file used to create .dot files")
				.hasArg(false)
				.argName("tmp")
				.required(false)
				.build();
	    
	    final Option destinationOption = Option.builder("o")
				.longOpt("dest")
				.desc("set the name of the directory where files will be placed")
				.hasArg(true)
				.argName("dest")
				.required(true)
				.build();
	    
	    final Option hideCallOption = Option.builder("c")
				.longOpt("hide")
				.desc("hide call/return transitions")
				.hasArg(false)
				.argName("hide")
				.required(false)
				.build();
	    
	    final Option CoeffOption = Option.builder("n")
				.longOpt("coeff")
				.desc("select the correlation coefficient")
				.hasArg(true)
				.argName("coeff")
				.required(false)
				.build();
	
	    final Options options = new Options();
	
	    options.addOption(dirFileOption);
	    options.addOption(algoFileOption);
	    options.addOption(rankFileOption);
	    options.addOption(timerFileOption);
	    options.addOption(tmpOption);
	    options.addOption(destinationOption);
	    options.addOption(hideCallOption);
	    options.addOption(CoeffOption);
	
	    return options;
	}
}
