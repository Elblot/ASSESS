/*
 *  Hyp.java
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


package evaluation;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import miners.FSAminer;
import traces.*;
//import fr.lip6.meta.behavioral.transformations.BrzozowskiSDgeneration;
import regexp.Regexp;
import regexp.RegexpUtil;

import java.util.ArrayList;
import java.util.Date;

import core.Prog;
import fsa.GenerateDOT;
import fsa.FSA;
import fsa.State;
public class Hyp {
	public static boolean genereDot=false;
	public static String dotsRep="";
	protected Prog prog;
	protected FSAminer algo;
	protected transient FSA fsa;
	protected transient FSA regularfsa;
	protected transient Regexp regexp;
	protected transient ArrayList<Trace> uniformes;
	protected double timeToBuildFSA; // temps en secondes mis par l algo a construire le FSA
	protected double maxMemUsed;
	protected double nbVisitsStates;
	protected double nbManipSeq;
	
	//protected int nb_uniformes;
	
	public Hyp(Prog prog, FSAminer algo){
		//System.out.println("Construction Hyp ");
		System.out.println("Prog "+prog.toString());
		this.prog=prog;
		this.algo=algo;
		System.out.println("Transformation par "+algo.getName());
		try{
			System.runFinalization();
		}
		catch(Error e){
			System.out.println(e);
		}
		System.gc();
		
		this.nbVisitsStates=State.nbVisitsStates;
		this.nbManipSeq=State.nbManipSeq;
		ArrayList<Trace> traces=prog.getObservations();
		MemoryUse mem=new MemoryUse();
		mem.start();
		
		long tempsT1 = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		//Date dStartDate = new Date();
		
		//System.out.println("Transformation");
		this.fsa=algo.transform(traces);
		//System.out.println("ici");
		//Date dEndDate = new Date();
		long tempsT2 = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
		timeToBuildFSA = ((1.0*tempsT2)/1000000000) - ((1.0*tempsT1)/1000000000);
		try{
			mem.fin=true;
			mem.join();
		}
		catch(Exception e){
			System.out.println(e);
		}
		maxMemUsed=mem.maxUsed/(1024.0*1024.0);
		this.nbVisitsStates=State.nbVisitsStates-this.nbVisitsStates;
		this.nbManipSeq=State.nbManipSeq-this.nbManipSeq;
		
		if (genereDot){
			File ged=new File(dotsRep);
			ged.mkdirs();
			//GenerateDOT.printDot(lts, dotsRep+"/Hyp_"+prog.toString()+"_"+algo.getName()+".dot");
			GenerateDOT.printDot(fsa, dotsRep+"/Hyp_"+prog.getName()+"_"+algo.getName()+".dot");
			//this.lts.computeFutureLTS();
			//Regexp reg=BrzozowskiSDgeneration.generate(this.lts);
			//System.out.println(reg);
			
		}
		
		
		this.fsa.max_trace_length=prog.getFSA().max_trace_length;
		//System.out.println("Construction Hyp ok");
		/*this.nb_uniformes=nb;
		if (nb<0){
			this.nb_uniformes=prog.getObservations().size();
		}*/
	}
	public double getTimeToBuildLTS(){
		return(this.timeToBuildFSA);
	}
	public double getNbVisitsStatesToBuildLTS(){
		return(this.nbVisitsStates);
	}
	public double getNbManipSeqToBuildLTS(){
		return(this.nbManipSeq);
	}
	public double getMaxMemUsedToBuildLTS(){
		return(this.maxMemUsed);
	}
	
	public Prog getProg(){
		return(prog);
	}
	public FSAminer getAlgo(){
		return(algo);
	}
	public FSA getFSA(){
		return(fsa);
	}
	public Regexp getRegexp(){
		if(regexp==null){
			regexp=RegexpUtil.FSA2Regexp(getFSA());
		}
		return regexp;
	}
	public FSA getRegularFSA(){
		if(regularfsa==null){
			regularfsa=RegexpUtil.reg2FSA(getRegexp());
		}
		return regularfsa;
	}
	public ArrayList<Trace> getUniformes(int nb){
		if (nb==-1){
			nb=prog.getObservations().size();
		}
		if((uniformes==null) || (uniformes.size()<nb)){
			uniformes=fsa.genereTraces(nb);
		}
		if (nb==uniformes.size()){
			return(uniformes);
		}
		else{
			ArrayList<Trace> ret=new ArrayList<Trace>();
			for(int i=0;i<nb;i++){
				ret.add(uniformes.get(i));
			}
			return(ret);
		}
	}
	
	
	
}

class MemoryUse extends Thread{
	public long maxUsed=0;
	public long init=0;
	//public long totalMemory=0;
	public boolean fin=false;
	public MemoryMXBean memoryBean;
	//Thread principal;
	public MemoryUse(){
		
		//this.principal=principal;
		//System.out.println(principal.getState());
		System.gc();
		fin=false;
		memoryBean = ManagementFactory.getMemoryMXBean();
		MemoryUsage mem=memoryBean.getHeapMemoryUsage(); 
		init=mem.getUsed();
		//totalMemory=Runtime.getRuntime().totalMemory();
		//init=totalMemory-Runtime.getRuntime().freeMemory();
		maxUsed=0;
		setPriority(Thread.MIN_PRIORITY);
	}
	public void run(){
		
		while (!fin){
			//System.out.println(principal.getState());
			//System.out.println(this.getState());
			MemoryUsage mem=memoryBean.getHeapMemoryUsage(); 
			long x=mem.getUsed()-init;
			if (x>maxUsed){
				maxUsed=x;
			}
			//System.out.println(mem);
			//System.out.println("MaxMem = "+maxUsed+", init="+init);
			
			try{
				sleep(100);
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		MemoryUsage mem=memoryBean.getHeapMemoryUsage(); 
		long x=mem.getUsed()-init;
		if (x>maxUsed){
			maxUsed=x;
		}
	}
}
