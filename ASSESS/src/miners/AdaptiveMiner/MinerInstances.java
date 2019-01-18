package miners.AdaptiveMiner;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import miners.FSAminer;
import evaluation.Hyp;
import evaluation.Result;
import java.util.HashMap;
import java.io.Serializable;
public class MinerInstances<T extends miners.FSAminer> implements Serializable {
	private static final long serialVersionUID = 1L;
	String name;
	ArrayList<T> instances;
	
	
	public MinerInstances(Class<T> miner, MinerParameters minerParams){
		Constructor<T> construct=null;
		try{
			for(Constructor<?> c:miner.getConstructors()){
				System.out.println(c);
			}
			Class<?>[] params=minerParams.getParamTypes();
			construct=miner.getDeclaredConstructor(params);
			minerParams.reinit();
			instances=new ArrayList<T>();
			Object[] pars;
			while((pars=minerParams.next())!=null){
				T inst=construct.newInstance(pars);
				instances.add(inst);
			}
			name=miner+"("+minerParams.getParamTypesList()+")";
		}
		catch(NoSuchMethodException e){
			e.printStackTrace();
			throw new RuntimeException("Constructor not found");
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Problem in building instances");
		}
	}
	
	public String toString(){
		return name;
	}
	
	public int getSize(){
		return instances.size();
	}
	public T getInstance(int i){
		return instances.get(i);
	}
	
}
