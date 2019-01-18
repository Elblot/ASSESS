/*
 *  Program_ATM_Factory.java
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

package programs;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import fsa.EpsilonRemover;
import fsa.GenerateDOT;
import fsa.FSA;

import program.Actor;
import program.Alt;
import program.Block;
import program.BlockList;
import program.Call;
import program.Loop;
import program.Main;
import program.Opt;




import traces.Method;
import traces.ObjectInstance;
import traces.ObjectClass;
import traces.ObjectInstance;
import traces.Statement;
import traces.Trace;

/**
 * 
 * The factory of an ATM program.
 * 
 * @author Tewfik Ziaidi, Sylvain Lamprier
 *
 */
public class Program_ATM_Factory {

	
	
	public static Main create(){
		
		ObjectClass ihm=new ObjectClass("UserIHM");
		Actor ihm0 = new Actor("ihm0", ihm);
		ObjectClass account=new ObjectClass("Account");
		Actor account0 = new Actor("account0", account);
		ObjectClass bank=new ObjectClass("Bank");
		Actor bank0 = new Actor("bank0",bank);
		ObjectClass atm=new ObjectClass("ATM");
		Actor atm0 = new Actor("atm0", atm);
		ObjectClass cons=new ObjectClass("Consortium");
		Actor cons0 = new Actor("cons0", cons);
		ObjectClass accGen = new ObjectClass("AccountGenerator");
		Actor accGen0 = new Actor("accGen0", accGen);
		
		ArrayList<Actor> actors = new ArrayList<Actor>();
		actors.add(ihm0);
		actors.add(cons0);
		actors.add(atm0);
		actors.add(bank0);
		actors.add(account0);
		actors.add(accGen0);
		int i=0;
		
		
		/*
		 * InsertCard Bloc 
		 */	
		Call checkAccount=new Call(new Statement("S"+i++,atm0,new Method("checkAccount",new ArrayList<ObjectInstance>(),bank),bank0));
		Call checkCard = new Call(new Statement("S"+i++,atm0,new Method("checkCard",new ArrayList<ObjectInstance>(),atm),atm0), checkAccount);
		
		Call requestPassword=new Call(new Statement("S"+i++,atm0,new Method("requestPass",new ArrayList<ObjectInstance>(),ihm),ihm0));
		
		Call ejectCard=new Call(new Statement("S"+i++,atm0,new Method("ejectCard",new ArrayList<ObjectInstance>(),ihm),ihm0));
		Call requestTakeCard=new Call(new Statement("S1"+i++,atm0,new Method("requestTakeCard",new ArrayList<ObjectInstance>(),ihm),ihm0));
		BlockList bl2 = new BlockList();
		bl2.add(ejectCard);
		bl2.add(requestTakeCard);
		
		/*
		 * Big ALT Bloc 
		 */	
		///////////// FIRST ALTERNATIVE
		Call cancelUponPassword=new Call(new Statement("S"+i++,ihm0,new Method("cancelUponPasswordRequest",new ArrayList<ObjectInstance>(),atm),atm0));
		Call ackCancelUponPasswdRequest=new Call(new Statement("S"+i++,atm0,new Method("ackCancelUponPasswdRequest",new ArrayList<ObjectInstance>(),ihm),ihm0));
		Call ejectCard2=new Call(new Statement("S"+i++,atm0,new Method("ejectCard",new ArrayList<ObjectInstance>(),ihm),ihm0));
		Call requestTakeCard2=new Call(new Statement("S1"+i++,atm0,new Method("requestTakeCard",new ArrayList<ObjectInstance>(),ihm),ihm0));
		BlockList bl3 = new BlockList();
		bl3.add(cancelUponPassword);
		bl3.add(ackCancelUponPasswdRequest);
		bl3.add(ejectCard2);
		bl3.add(requestTakeCard2);
		
		/////////////// SECOND ALTERNATIVE - Contains an ALT
		
		/* Enter password in a loop*/
		
		/* Loop on enterpassword*/
		Call isPasswordAvailable=new Call(new Statement("S"+i++,atm0,new Method("isPasswordAvailable",new ArrayList<ObjectInstance>(),bank),bank0));	
		Call enterPassword=new Call(new Statement("S"+i++,ihm0,new Method("enterPassword",new ArrayList<ObjectInstance>(),atm),atm0), isPasswordAvailable);
		Loop loop = new Loop(enterPassword);
		BlockList block_enterPassword = new BlockList();
		block_enterPassword.add(enterPassword);
		block_enterPassword.add(loop);
		
		// Loop EnterPassword SEQ ALT
		
		/* First alternative */
		Call ejectCard3=new Call(new Statement("S"+i++,atm0,new Method("ejectCard",new ArrayList<ObjectInstance>(),ihm),ihm0));
		Call requestTakeCard3=new Call(new Statement("S1"+i++,atm0,new Method("requestTakeCard",new ArrayList<ObjectInstance>(),ihm),ihm0));
		BlockList bl4 = new BlockList();
		bl4.add(ejectCard3);
		bl4.add(requestTakeCard3);
		
		
		/* Second alternative :  Withdaw money */
		
		Call getSolde=new Call(new Statement("S"+i++,bank0,new Method("getSolde",new ArrayList<ObjectInstance>(),account),account0));
		
		Call setSolde=new Call(new Statement("S"+i++,bank0,new Method("setSolde",new ArrayList<ObjectInstance>(),account),account0));
		Call getSolde2=new Call(new Statement("S"+i++,bank0,new Method("getSolde",new ArrayList<ObjectInstance>(),account),account0));
		BlockList bl6 = new BlockList();
		bl6.add(setSolde);
		bl6.add(getSolde2);
		
		// Opt setSolde SEQ getSolde
		Opt opt1 = new Opt(bl6);
		
		BlockList bl7 = new BlockList();
		bl7.add(getSolde);
		bl7.add(opt1);

		Call remove=new Call(new Statement("S"+i++,atm0,new Method("remove",new ArrayList<ObjectInstance>(),bank),bank0),bl7);
		
		Call getMoney=new Call(new Statement("S"+i++,atm0,new Method("getMoney",new ArrayList<ObjectInstance>(),atm),atm0),remove);
		
		Call ejectCard4=new Call(new Statement("S"+i++,atm0,new Method("ejectCard",new ArrayList<ObjectInstance>(),ihm),ihm0));
		Call requestTakeCard4=new Call(new Statement("S1"+i++,atm0,new Method("requestTakeCard",new ArrayList<ObjectInstance>(),ihm),ihm0));
		BlockList bl8 = new BlockList();
		bl8.add(ejectCard4);
		bl8.add(requestTakeCard4);
		
		Call ejectCardUponTooHighWithdraw=new Call(new Statement("S"+i++,atm0,new Method("ejectCardUponTooHighWithdraw",new ArrayList<ObjectInstance>(),ihm),ihm0));	
		Call requestTakeCard5=new Call(new Statement("S1"+i++,atm0,new Method("requestTakeCard",new ArrayList<ObjectInstance>(),ihm),ihm0));
		BlockList bl9 = new BlockList();
		bl9.add(ejectCardUponTooHighWithdraw);
		bl9.add(requestTakeCard5);
		
		Alt alt1 = new Alt();
		alt1.add(bl8);
		alt1.add(bl9);
		
		BlockList bl10 = new BlockList();
		bl10.add(getMoney);
		bl10.add(alt1);
		
		Call withdrawMoney=new Call(new Statement("S"+i++,ihm0,new Method("withdrawMoney",new ArrayList<ObjectInstance>(),atm),atm0),bl10);
		
		// Build the alt with eject/request take card and withdrawmoney
		Alt alt0 = new Alt();
		alt0.add(bl4);
		alt0.add(withdrawMoney);
	
		// bloc list of second alternative of BIG ALT = enterpasswd SEQ the second alt (alt0)
		BlockList bl11 = new BlockList();
		bl11.add (block_enterPassword);
		bl11.add(alt0);
		
		// Build the BIG ALT
		Alt alt2 = new Alt();
		alt2.add(bl3);
		alt2.add(bl11);
		
		// requestPassword SEQ the BIG ALT bloc
		BlockList bl22 = new BlockList();
		bl22.add(requestPassword);
		bl22.add(alt2);
		
		// Alt fils de InsertCard
		Alt alt3 = new Alt();	
		alt3.add(bl2); // eject + request take card
		alt3.add(bl22); // request  pass + BIG ALT
		
		// Build InsertCard
		BlockList bl1 = new BlockList();
		bl1.add(checkCard);
		bl1.add(alt3);	
		
		Call insertCard = new Call(new Statement("S"+i++,ihm0,new Method("insertCard",new ArrayList<ObjectInstance>(),atm),atm0), bl1);
		
		// Build displayMainScreen
		
		Call displayMainScreen=new Call(new Statement("S"+i++,atm0,new Method("displayMainScreen",new ArrayList<ObjectInstance>(),ihm),ihm0), insertCard);
		
		ArrayList<Block> b0 = new ArrayList<Block>();
		b0.add(displayMainScreen);
		// Fin du SD !
			
		Main main = new Main(b0, actors,200);
	 return main;
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main sd=create();
		boolean ok=sd.saveModel("ATM");
		if (ok){
		   sd.genereAllTraces("ATM", 1000);
		}
		/*try{
			 
			 File repit = new File ("./ATM-Closing/1/");
			 deleteRecursive(repit);
			 repit.mkdirs();
			 
			 // Sauvegarde SD
			 sd.save("./ATM-Closing/1/ATM_sd_model.txt");
			 sd.serialize("./ATM-Closing/1/atm_sd_model.sd");
			 
			 //LTS
			 LTS lts=sd.toLTS();
			 //System.out.println(lts);
			 GenerateDOT.printDot(lts, "./ATM-Closing/1/atm_sd_model.dot");  
			 EpsilonRemover.removeEpsilon(lts);
			 //System.out.println(lts);
			 GenerateDOT.printDot(lts, "./ATM-Closing/1/atm_sd_model_no_epsilon.dot");
			 
			 
			 // Traces
			 TraceGenResult trg=null;
		     String sreptraces="./ATM-Closing/1/biased_traces";
			 repit = new File (sreptraces);
			 repit.mkdirs();
			 ArrayList<Trace> traces=new ArrayList<Trace>();
			 for(int y=0;y<2;y++){
				trg=new TraceGenResult();
				Trace trace=sd.getBiasedTrace();
				trg.addTrace(trace);
				trace.serialize(sreptraces+"/trace"+y+".trace");
				TraceIO.save(trg, sreptraces,"_"+y+"_",false);	
				traces.add(trace);
		     }
		 }
		 catch(Exception e){
			   System.out.println(e.getMessage());
		 }*/
	}
	
	/*public static void deleteRecursive(File f)  {
		if (f.exists()){
			if (f.isDirectory()){
			  File[] childs=f.listFiles();
			  int i=0;
			  for(i=0;i<childs.length;i++){
				  deleteRecursive(childs[i]);
			  }
			  
			}
			f.delete();
		 }
	}*/
}
