/*
 *  NbBlocks.java
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import core.Prog;
import fsa.FSA;
import traces.*;
import program.*;
public class NbBlocks extends EvalMeasure {
	private static final long serialVersionUID = 1L;
	
	/*protected boolean nb_alt=false;
	protected boolean nb_loop=false;
	protected boolean nb_opt=false;
	protected boolean nb_call=true;
	protected boolean uniques=true;
	protected boolean 
	public NbBlocks(boolean nb_alt,boolean nb_loop,boolean nb_opt,boolean nb_call,boolean nb_unique_call){
		this.nb_alt=nb_alt;
		this.nb_loop=nb_loop;
		this.nb_opt=nb_opt;
		this.nb_call=nb_call;
		this.nb_unique_call=nb_unique_call;
	}*/
	public NbBlocks(){
		
	}
	public String getName(){
		return("NbBlocks");
	}
	public Result eval(Hyp hyp){
		Result res=new Result(hyp.getAlgo(),hyp.getProg(),this);
		Prog prog=hyp.getProg();
		
		Main m=prog.getSD();
		HashMap<String,Integer> nbs=new HashMap<String,Integer>();
		HashSet<String> calls=new HashSet<String>();
		m.computeNbBlocks(nbs, calls);
		int nbtot=0;
		int nbuniques=0;
		Integer n=nbs.get("BlockList");
		n=((n==null)?0:n);
		nbtot+=n;
		//if(nb_alt){
			res.addScore("nb_BlockList",n);
			System.out.println("nb_BlockList => "+n);
		//}
		n=nbs.get("uniqueBlockList");
		n=((n==null)?0:n);
		nbuniques+=n;
		//if(nb_alt){
			res.addScore("unique_BlockList",n);
			System.out.println("unique_BlockList => "+n);
		//}	
		
		n=nbs.get("Alt");
		n=((n==null)?0:n);
		nbtot+=n;
		//if(nb_alt){
			res.addScore("nb_Alt",n);
			System.out.println("nb_Alt => "+n);
		//}
		n=nbs.get("uniqueAlt");
		n=((n==null)?0:n);
		nbuniques+=n;
		
		//if(nb_alt){
			res.addScore("unique_Alt",n);
			System.out.println("unique_Alt => "+n);
		//}	
		n=nbs.get("Loop");
		n=((n==null)?0:n);
		nbtot+=n;
		//if(nb_loop){
			res.addScore("nb_Loop",n);
			System.out.println("nb_Loop => "+n);
		//}
		n=nbs.get("uniqueLoop");
		n=((n==null)?0:n);
		nbuniques+=n;
		
		//if(nb_loop){
			res.addScore("unique_Loop",n);
			System.out.println("unique_Loop => "+n);
		//}
		n=nbs.get("Opt");
		n=((n==null)?0:n);
		nbtot+=n;
		//if(nb_opt){
			res.addScore("nb_Opt",n);
			System.out.println("nb_Opt => "+n);
		//}
		n=nbs.get("uniqueOpt");
		n=((n==null)?0:n);
		nbuniques+=n;
		
		//if(nb_opt){
			res.addScore("unique_Opt",n);
			System.out.println("unique_Opt => "+n);
		//}
			
		n=nbs.get("Call");
		n=((n==null)?0:n);
		nbtot+=n;
		//if(nb_opt){
			res.addScore("nb_Call",n);
			System.out.println("nb_Call => "+n);
		//}
		n=nbs.get("uniqueCall");
		n=((n==null)?0:n);
		nbuniques+=n;
		//if(nb_opt){
			res.addScore("unique_Call",n);
			System.out.println("unique_Call => "+n);
		//}
		
		res.addScore("nb_Blocks",nbtot);
		System.out.println("nb_Blocks => "+nbtot);
		
		res.addScore("unique_Blocks",nbuniques);
		System.out.println("unique_Blocks => "+nbuniques);
		
		res.addScore("unique_MethodLabels",calls.size());
		System.out.println("unique_MethodLabels => "+calls.size());
		
		
		return(res);
	}
}
