package miners.AdaptiveMiner;

import java.util.ArrayList;
import java.io.Serializable;
import traces.Trace;
import traces.Statement;
import java.util.HashMap;

import core.Prog;
import core.Sequence;
public abstract class MinerFeatureProducer implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public abstract ArrayList<Double> getFeatures(ArrayList<Trace> obs);
	public abstract int getNbFeatures();
	public abstract String toString();
	public MinerFeatureProducer loadFromString(String s) throws Exception{
		MinerFeatureProducer ret=null;
		String[] sp=s.split("\t");
		ret=(MinerFeatureProducer)Class.forName(sp[0]).getConstructor(Class.forName("java.lang.String")).newInstance(s);
		return ret;
	}
	/*public static void main(String[] args){
		ArrayList<Integer> list=new ArrayList<Integer>();
		list.add(4); list.add(3); list.add(7);
		System.out.println(list);
	}*/
}


class NewNGramsRatio extends MinerFeatureProducer {
	private static final long serialVersionUID = 1L;
	
	private Integer ngrams;
	private double ratioObs;
	
	public NewNGramsRatio(double ratioObservations,Integer ngrams){
		this.ratioObs=ratioObservations;
		this.ngrams=ngrams;
	}
	public NewNGramsRatio(String s){
		String[] sp=s.split("\t");
		this.ratioObs=Double.valueOf(sp[1]);
		/*this.ngrams=new ArrayList<Integer>();
		s=sp[2].substring(1,sp[2].length()-1);
		sp=s.split(", ");
		for(String st:sp){
			ngrams.add(Integer.parseInt(st));
		}*/
		this.ngrams=Integer.parseInt(sp[2]);
	}
	public int getNbFeatures(){
		return(ngrams);
	}
	public ArrayList<Double> getFeatures(ArrayList<Trace> obs){
			
		
		int nbObs=obs.size();
		double nbObs1=ratioObs*nbObs;
		ArrayList<Trace> obs1=new ArrayList<Trace>();
		ArrayList<Trace> obs2=new ArrayList<Trace>();
		int i=0;
		for(i=0;i<nbObs1;i++){
			obs1.add(obs.get(i));
		}
		for(int j=i;j<nbObs;j++){
			obs2.add(obs.get(j));
		}
		
		Sequence root1=buildSequencesFor(obs1);
		Sequence root2=buildSequencesFor(obs2);
		Sequence.lockedTree=true;
		HashMap<Integer,Integer> sim=root2.countSimilarSeqsIn(root1);
		HashMap<Integer,Integer> nbs=root2.countSeqs();
		ArrayList<Double> ratios=new ArrayList<Double>();
		for(i=1;i<=this.ngrams;i++){
			Integer sn=sim.get(i);
			Integer nb=nbs.get(i);
			sn=(sn==null)?0:sn;
			double r=(nb==null)?0:(sn*1.0/nb);
			ratios.add(r);
		}
		return ratios;
	}
	
	private Sequence buildSequencesFor(ArrayList<Trace> traces){
		Sequence.root=new Sequence();
		Sequence.lockedTree=false;
		for(Trace t:traces){
			HashMap<Integer,Sequence> lasts=new HashMap<Integer,Sequence>();
			Sequence last=Sequence.root;
			for(int j=0;j<ngrams;j++){
				lasts.put(j, last);
				last=last.getForwardChild("xxx");
			}
			for(int i=1;i<t.getSize()+ngrams;i++){
				String ch="";
				if(i<=t.getSize()){
					Statement st=t.getByIndex(i);
					ch=st.getText();
				}
				else{
					ch="xxx";
				}
				for(int j=(ngrams-1);j>=0;j--){
					last=lasts.get(j);
					last=last.getForwardChild(ch);
					if(j<(ngrams-1)){
						lasts.put(j+1, last);
					}
				}
			}
			
		}
		return Sequence.root;
	}
	
	public String toString(){
		return this.getClass()+"\t"+this.ratioObs+"\t"+ngrams;
	}
	
}