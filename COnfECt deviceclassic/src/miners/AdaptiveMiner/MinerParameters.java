package miners.AdaptiveMiner;

import java.util.ArrayList;
class MinerParameters {
	ArrayList<MinerParameter<?>> pars;
	ArrayList<Object> current;
	boolean first=true;
	MinerParameters(){
		pars=new ArrayList<MinerParameter<?>>();
		current=new ArrayList<Object>();
	}
	MinerParameters(ArrayList<MinerParameter<?>> pars){
		this.pars=pars;
		current=new ArrayList<Object>();
		for(MinerParameter<?> p:pars){
			current.add(null);
		}
	}
	public void addParam(MinerParameter<?> p){
		pars.add(p);
	}
	public void reinit(){
		current=new ArrayList<Object>();
		for(MinerParameter<?> mp:pars){
			mp.reinit();
			current.add(null);
		}
		first=true;
	}
	public Object[] next(){
		boolean ok=true;
		if(first){
			for(int i=0;i<pars.size();i++){
				MinerParameter<?> mp=pars.get(i);
				mp.reinit();
				current.set(i, mp.next());
			}
			first=false;
		}
		else{
			ok=changeCurrent(pars.size()-1);
		}
		if(ok){
			return current.toArray();
		}
		else{
			reinit();
			return null;
		}
	}
	private boolean changeCurrent(int i){
		boolean ok=true;
		MinerParameter<?> p=pars.get(i);
		Object v=p.next();
		if(v==null){
			if(i==0){
				return false;
			}
			ok=changeCurrent(i-1);
			if(ok){
				v=p.next();
			}
		}
		current.set(i, v);
		return ok;
	}
	public Class<?>[] getParamTypes(){
		Class<?>[] types=new Class<?>[pars.size()];
		for(int i=0;i<pars.size();i++){
			types[i]=pars.get(i).getType();
		}
		return types;
	}
	
	public String getParamTypesList(){
		String s="";
		for(int i=0;i<pars.size();i++){
			s+=pars.get(i).getType()+";";
		}
		return s;
	}
	
	public static void main(String[] args){
	  try{	
		MinerParameters m=new MinerParameters();
		ArrayList<String> s=new ArrayList<String>();
		s.add("x");
		s.add("v");
		
		MinerParameter<?> p=new MinerParameter(s);
		m.addParam(p);
		System.out.println(p.parType);
		
		p=new MinerParameter(-1,1,1);
		m.addParam(p);
		p=new MinerParameter(0.0,5.0,0.5);
		m.addParam(p);
		p=new MinerParameter(0.1f,1.0f,0.2f);
		m.addParam(p);
		p=new MinerParameter(0l,10l,2l);
		m.addParam(p);
		m.reinit();
		Object[] vals;
		while((vals=m.next())!=null){
			for(Object o:vals){
				System.out.print(o+",");
				
			}
			System.out.print("\n");
		}
	  }
	  catch(Exception e){
		  e.printStackTrace();
	  }
	}
	
}
