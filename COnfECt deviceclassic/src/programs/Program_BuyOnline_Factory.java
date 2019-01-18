/*
 *  Program_BuyOnline_Factory.java
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

import program.Actor;
import program.Alt;
import program.Call;
import program.Loop;
import program.Main;

import traces.ObjectClass;
import traces.Method;
import traces.ObjectInstance;
import traces.Statement;
import dataGenerator.*;

/**
 * 
 * The factory of a BuyOnline program.
 * 
 * @author Tewfik Ziaidi, Sylvain Lamprier
 *
 */
public class Program_BuyOnline_Factory {
	public static Main create(){
		ObjectClass ihm=new ObjectClass("UserIHM");
		Actor ihm0 = new Actor("webSite", ihm);
		ObjectClass buySite=new ObjectClass("BuySystem");
		Actor buy0 = new Actor("market", buySite);
		ObjectClass cart=new ObjectClass("Cart");
		Actor cart0 = new Actor("cart0",cart);
		
		ArrayList<Actor> actors = new ArrayList<Actor>();
		actors.add(ihm0);
		actors.add(buy0);
		actors.add(cart0);
		
		Call addArticle=new Call(new Statement(ihm0,new Method("addArticle",new ArrayList<ObjectInstance>(),new ObjectClass("void")),buy0));
		Call addCart=new Call(new Statement(buy0,new Method("addToCart",new ArrayList<ObjectInstance>(),new ObjectClass("void")),cart0));
		Call pay=new Call(new Statement(ihm0,new Method("payCommand",new ArrayList<ObjectInstance>(),new ObjectClass("void")),buy0));
		Call validate=new Call(new Statement(buy0,new Method("validate",new ArrayList<ObjectInstance>(),new ObjectClass("void")),cart0));
		Call cancel=new Call(new Statement(ihm0,new Method("cancel",new ArrayList<ObjectInstance>(),new ObjectClass("void")),buy0));
		addArticle.setBloc(addCart);
		pay.setBloc(validate);
		
		
		Main sd=new Main(actors);
		Loop l=new Loop();
		l.setBloc(addArticle);
		sd.add(l);
		Alt alt=new Alt();
		alt.add(pay);
		alt.add(cancel);
		sd.add(alt);
		
		return(sd);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main sd=create();
		boolean ok=sd.saveModel("Progs/Buy");
		if (ok){
		   sd.genereAllTraces("Progs/Buy", 10);
		}
	}
}
