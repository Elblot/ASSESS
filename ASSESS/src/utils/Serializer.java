package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
public class Serializer {
	public static void serialize(String fileName, Serializable o) throws IOException{
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos= new ObjectOutputStream(fos);
			try {
				oos.writeObject(o); 
				oos.flush();
			} finally {
				try {
					oos.close();
				} finally {
					fos.close();
				}
			}
	}
	
	public static Object deserialize(String fileName) { 
		Object ret=null;
		try{
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois= new ObjectInputStream(fis);
			try {	
				ret = ois.readObject(); 
			} finally {
				try {
					ois.close();
				} finally {
					fis.close();
				}
			}
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}
		catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException(e.toString());
			
		}
		return(ret);
	}
}
