/*
 *  Keyboard.java
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


package utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Only allows an easier interaction with the user.
 */
public class Keyboard {

  private static final BufferedReader in =
		new BufferedReader (new InputStreamReader (System.in)) ;

  /**
   * Prints mess end returns the string entered by the user
   */
  public static String saisirLigne (String mess) {
	  System.out.println (mess) ;
	  try{return in.readLine () ;}
	  catch (IOException e){return null;}
  }

  /**
   * Prints mess end returns the integer entered by the user
   */
  public static int enterInt(String mess) {
	  System.out.println (mess) ;
	  boolean ok=false;
	  while(!ok){
		  try{
			  return Integer.parseInt(in.readLine());
		  }
		  catch(NumberFormatException e){
			  System.out.println("This is not an integer number");
		  }
		  catch (IOException e){throw new RuntimeException(e.toString());}
	  }
	  return -1;
  }
 
   
} 

