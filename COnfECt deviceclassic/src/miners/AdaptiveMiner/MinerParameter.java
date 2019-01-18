package miners.AdaptiveMiner;

import java.util.ArrayList;

class MinerParameter<T>{
	ArrayList<T> vals;
	Class<T> parType;
	int current=-1;
	
	public MinerParameter(double minVal, double maxVal, double step){
		try{
			this.parType=(Class<T>)Class.forName("java.lang.Double");
			vals=new ArrayList<T>();
			for(double i=minVal;i<=maxVal;i+=step){
				if(!vals.contains(i)){
					vals.add((T)new Double(i));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public MinerParameter(int minVal, int maxVal, int step){
		try{
			this.parType=(Class<T>)Class.forName("java.lang.Integer");
			vals=new ArrayList<T>();
			for(int i=minVal;i<=maxVal;i+=step){
				if(!vals.contains(i)){
					vals.add((T)new Integer(i));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public MinerParameter(float minVal, float maxVal, float step){
		try{
			this.parType=(Class<T>)Class.forName("java.lang.Float");
			vals=new ArrayList<T>();
			for(float i=minVal;i<=maxVal;i+=step){
				if(!vals.contains(i)){
					vals.add((T)new Float(i));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public MinerParameter(long minVal, long maxVal, long step){
		try{
			this.parType=(Class<T>)Class.forName("java.lang.Long");
			vals=new ArrayList<T>();
			for(long i=minVal;i<=maxVal;i+=step){
				if(!vals.contains(i)){
					vals.add((T)new Long(i));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public MinerParameter(ArrayList<T> vals){
		//this.parType=parType;
		this.vals=vals;
		this.parType=(Class<T>)vals.get(0).getClass();
	}
	public void reinit(){
		current=-1;
	}
	public T next(){
		current++;
		if(current>=vals.size()){
			current=-1;
			return null;
		}
		return vals.get(current);
	}
	public Class<T> getType(){
		return parType;
	}
	
}