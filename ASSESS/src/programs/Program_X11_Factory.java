/*
 *  Program_X11_Factory.java
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

import traces.Method;
import traces.ObjectInstance;
import traces.ObjectClass;
import traces.ObjectInstance;
import traces.Statement;
import traces.Trace;

import java.util.ArrayList;

import fsa.EpsilonRemover;
import fsa.GenerateDOT;
import fsa.FSA;

import program.Actor;
import program.Alt;
import program.BlockList;
import program.Call;
import program.Loop;
import program.Main;


/**
 * 
 * The factory of an X11 windowing system.
 * 
 * @author Sylvain Lamprier
 *
 */
public class Program_X11_Factory {
	public static Main create(){
		ObjectClass main=new ObjectClass("Static");
		Actor a0 = new Actor("a0", main);
		ArrayList<Actor> actors=new ArrayList<Actor>();
		Main sd=new Main(actors,100);
		int i=0;
		ObjectClass Void=new ObjectClass("void");
		Call a=new Call(new Statement("S"+i++,a0,new Method("A",new ArrayList<ObjectInstance>(),Void),a0));
		Call b=new Call(new Statement("S"+i++,a0,new Method("B",new ArrayList<ObjectInstance>(),Void),a0));
		Call c=new Call(new Statement("S"+i++,a0,new Method("C",new ArrayList<ObjectInstance>(),Void),a0));
		Call d=new Call(new Statement("S"+i++,a0,new Method("D",new ArrayList<ObjectInstance>(),Void),a0));
		Call e=new Call(new Statement("S"+i++,a0,new Method("E",new ArrayList<ObjectInstance>(),Void),a0));
		Call f=new Call(new Statement("S"+i++,a0,new Method("F",new ArrayList<ObjectInstance>(),Void),a0));
		Call g=new Call(new Statement("S"+i++,a0,new Method("G",new ArrayList<ObjectInstance>(),Void),a0));
		Call h=new Call(new Statement("S"+i++,a0,new Method("H",new ArrayList<ObjectInstance>(),Void),a0));
		
		Alt alt1=new Alt();
		sd.add(alt1);
		
		BlockList alist=new BlockList();
		alt1.add(alist);
		alist.add(a);
		Alt alt2=new Alt();
		alist.add(alt2);
		Loop loop1=new Loop();
		alt2.add(loop1);
		alt2.add(e);
		BlockList ed=new BlockList();
		ed.add(e);
		ed.add(d);
		loop1.setBloc(ed);
		alist.add(e);
		
		BlockList blist=new BlockList();
		alt1.add(blist);
		blist.add(b);
		blist.add(e);
		
		BlockList clist=new BlockList();
		alt1.add(clist);
		clist.add(c);
		clist.add(f);
		
		BlockList glist=new BlockList();
		alt1.add(glist);
		glist.add(g);
		glist.add(h);
		glist.add(f);
		
		return(sd);
	}
	
	public static void main(String[] args) {
		//Call.setClosing=false;
		Main sd=create();
		boolean ok=sd.saveModel("X11");
		if (ok){
			//Regexp reg=BrzozowskiSDgeneration.generate(sd.getLTS());
			//System.out.println(reg);
			sd.genereAllTraces("X11", 1000);
		}
	}
}
