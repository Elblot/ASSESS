/*
 *  Program_CVS_Factory.java
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
 * The factory of a CVS program.
 * 
 * @author Sylvain Lamprier
 *
 */
public class Program_CVS_Factory {
	public static Main create(){
		ObjectClass main=new ObjectClass("Static");
		Actor a0 = new Actor("a0", main);
		ObjectClass ftp=new ObjectClass("FTP");
		Actor ftp0 = new Actor("ftp0", ftp);
		
		
		ArrayList<Actor> actors = new ArrayList<Actor>();
		actors.add(a0);
		actors.add(ftp0);
		
		ArrayList<Block> mblocs=new ArrayList<Block>();
		Main sd = new Main(mblocs, actors,100);
		
		int i=0;
		Call start=new Call(new Statement("S"+i++,a0,new Method("init",new ArrayList<ObjectInstance>(),ftp),ftp0));
		mblocs.add(start);
		
		//System.out.println("SD ="); //+sd);
		
		BlockList mlist=new BlockList();
		start.setBloc(mlist);
		
		Call connect=new Call(new Statement("S"+i++,ftp0,new Method("connect",new ArrayList<ObjectInstance>(),ftp),ftp0));
		mlist.add(connect);
		Call login=new Call(new Statement("S"+i++,ftp0,new Method("login",new ArrayList<ObjectInstance>(),ftp),ftp0));
		mlist.add(login);
		Alt scenarios=new Alt();
		mlist.add(scenarios);
		Call logout=new Call(new Statement("S"+i++,ftp0,new Method("logout",new ArrayList<ObjectInstance>(),ftp),ftp0));
		mlist.add(logout);
		Call disconnect=new Call(new Statement("S"+i++,ftp0,new Method("disconnect",new ArrayList<ObjectInstance>(),ftp),ftp0));
		mlist.add(disconnect);
		
		
		BlockList uplist=new BlockList();
		Loop upload=new Loop(uplist);
		scenarios.add(upload);
		Call setType=new Call(new Statement("S"+i++,ftp0,new Method("setFileType",new ArrayList<ObjectInstance>(),ftp),ftp0));
		uplist.add(setType);
		//ArrayList<Bloc> bupalt=new ArrayList<Bloc>();
		Alt upalt=new Alt();
		uplist.add(upalt);
		Call storeFile=new Call(new Statement("S"+i++,ftp0,new Method("storeFile",new ArrayList<ObjectInstance>(),ftp),ftp0));
		Call appendFile=new Call(new Statement("S"+i++,ftp0,new Method("appendFile",new ArrayList<ObjectInstance>(),ftp),ftp0));
		BlockList bupalt1=new BlockList();
		upalt.add(bupalt1);
		bupalt1.add(storeFile);
		bupalt1.add(appendFile);
		Call rename=new Call(new Statement("S"+i++,ftp0,new Method("rename",new ArrayList<ObjectInstance>(),ftp),ftp0));
		BlockList bupalt2=new BlockList();
		upalt.add(bupalt2);
		bupalt2.add(rename);
		bupalt2.add(storeFile);
		
		scenarios.add(storeFile);
		
		BlockList delete=new BlockList();
		scenarios.add(delete);
		Call cgdir=new Call(new Statement("S"+i++,ftp0,new Method("changeWorkingDirectory",new ArrayList<ObjectInstance>(),ftp),ftp0));
		delete.add(cgdir);
		Loop deleteLoop=new Loop();
		delete.add(deleteLoop);
		BlockList insideDeleteLoop=new BlockList();
		deleteLoop.setBloc(insideDeleteLoop);
		delete.add(appendFile);
		Call listNames=new Call(new Statement("S"+i++,ftp0,new Method("listNames",new ArrayList<ObjectInstance>(),ftp),ftp0));
		insideDeleteLoop.add(listNames);
		Call deleteFile=new Call(new Statement("S"+i++,ftp0,new Method("deleteFile",new ArrayList<ObjectInstance>(),ftp),ftp0));
		insideDeleteLoop.add(new Loop(deleteFile));
		insideDeleteLoop.add(cgdir);
		
		Call mkdir=new Call(new Statement("S"+i++,ftp0,new Method("makeDirectory",new ArrayList<ObjectInstance>(),ftp),ftp0));
		Loop makeDirs=new Loop(mkdir);
		scenarios.add(makeDirs);
		
		Call rmdir=new Call(new Statement("S"+i++,ftp0,new Method("removeDirectory",new ArrayList<ObjectInstance>(),ftp),ftp0));
		Loop removeDirs=new Loop(rmdir);
		scenarios.add(removeDirs);
		
		BlockList download=new BlockList();
		scenarios.add(download);
		Call listFiles=new Call(new Statement("S"+i++,ftp0,new Method("listFiles",new ArrayList<ObjectInstance>(),ftp),ftp0));
		download.add(listFiles);
		Call retrieveFile=new Call(new Statement("S"+i++,ftp0,new Method("retrieveFile",new ArrayList<ObjectInstance>(),ftp),ftp0));
		Loop retrieve=new Loop(retrieveFile);
		download.add(retrieve);
		Opt opt=new Opt();
		download.add(opt);
		BlockList optlist=new BlockList();
		opt.setBloc(optlist);
		optlist.add(cgdir);
		BlockList optlist2=new BlockList();
		Loop loopopt=new Loop(optlist2);
		optlist.add(loopopt);
		optlist2.add(listFiles);
		optlist2.add(new Opt(new Loop(retrieveFile)));
		optlist2.add(cgdir);
		
		
		return sd;
	}
	public static void main(String[] args) {
		Main sd=create();
		boolean ok=sd.saveModel("CVS");
		if (ok){
			sd.genereAllTraces("CVS", 1000);
		}
	}
}
