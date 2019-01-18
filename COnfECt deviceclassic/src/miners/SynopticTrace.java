package miners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import core.Sequence;

import fsa.State;

import traces.Trace;
import traces.Statement;
public class SynopticTrace extends Trace {
	private static final long serialVersionUID = 1L;
	
	HashMap<Sequence,HashSet<Integer>> positions; 
	HashMap<Integer,SynopticState> states;
	
	public SynopticTrace(){
		states=new HashMap<Integer,SynopticState>();
	}
	
	public void setState(SynopticState s,int i){
		states.put(i,s);
	}
	
	public SynopticState getState(int i){
		return(states.get(i));
	}
	
	/*ArrayList<HashSet<Sequence>> futures;
	ArrayList<HashSet<Sequence>> pasts;
	public void computeFutures(HashSet<Sequence> posts){
		futures=new ArrayList<HashSet<Sequence>>();
		for(int i=0;i<this.getSize()+1;i++){
			HashSet<Sequence> set=new HashSet<Sequence>();
			futures.add(set);
		}
		HashSet<Sequence> set=new HashSet<Sequence>();
		for(int i=this.getSize()-1;i>=0;i--){
			Statement st=this.getByIndex(i+1);
			String ch=st.getText();
			Sequence s=Sequence.root.getForwardChild(ch);
			if(posts.contains(s)){
				set.add(s);
			}
			HashSet<Sequence> f=futures.get(i);
			for(Sequence seq:set){
				f.add(seq);
			}
			
		}
		
	}
	public void computePasts(HashSet<Sequence> posts){
		pasts=new ArrayList<HashSet<Sequence>>();
		for(int i=0;i<this.getSize()+1;i++){
			HashSet<Sequence> set=new HashSet<Sequence>();
			pasts.add(set);
		}
		HashSet<Sequence> set=new HashSet<Sequence>();
		for(int i=0;i<this.getSize();i++){
			Statement st=this.getByIndex(i+1);
			String ch=st.getText();
			Sequence s=Sequence.root.getForwardChild(ch);
			if(posts.contains(s)){
				set.add(s);
			}
			HashSet<Sequence> f=futures.get(i+1);
			for(Sequence seq:set){
				f.add(seq);
			}
			
		}
		
	}*/
	public void computePositions(){
		positions=new HashMap<Sequence,HashSet<Integer>>();
		for(int i=0;i<getSize();i++){
			State.nbManipSeq++;
			
			Statement st=this.getStatement(i);
			String ch=st.getText();
			Sequence s=Sequence.root.getForwardChild(ch);
			HashSet<Integer> set=positions.get(s);
			if(set==null){
				set=new HashSet<Integer>();
				positions.put(s, set);
			}
			set.add(i);
		}
	}
	public boolean requires(int index, boolean forward, Sequence pre, Sequence post){
		HashSet<Integer> preset=positions.get(pre);
		HashSet<Integer> postset=positions.get(post);
		if(preset==null){
			return false;
		}
		
		if(forward){
			int max=-1;
			for(Integer i:preset){
				State.nbManipSeq++;
				
				if((i<index) && (i>max)){
					max=i;
				}
				
			}
			if(max<0){
				return false;
			}
			for(Integer i:postset){
				State.nbManipSeq++;
				
				if((i<index) && (i>max)){
					return false;
				}
			}
			//System.out.println(index+" "+max);
			//System.out.println(postset);
		}
		else{
			int min=getSize()+1;
			for(Integer i:preset){
				State.nbManipSeq++;
				
				if((i>=index) && (i<min)){
					min=i;
				}
				
			}
			if(min>getSize()){
				return false;
			}
			for(Integer i:postset){
				State.nbManipSeq++;
				
				if((i>=index) && (i<min)){
					return false;
				}
			}
		}
			
		
		return true;
	}
	
	public boolean has(int index, boolean forward, Sequence post){
		HashSet<Integer> postset=positions.get(post);
		if(postset==null){
			return false;
		}
		if(forward){
			
			for(Integer i:postset){
				State.nbManipSeq++;
				
				if(i>=index){
					return true;
				}
				
			}
		}
		else{
			
			for(Integer i:postset){
				State.nbManipSeq++;
				
				if(i<index){
					return true;
				}
				
			}
		}
		return false;
	}
	
	
	
}
