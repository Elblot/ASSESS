package fsa;

public class CallStack {
	public FSA lts;
	public State pos;
	
	public CallStack(FSA model, State loop) {
		this.lts = model;
		this.pos = loop;
	}
	
	public FSA getFSA() {
		return this.lts;
	}
	
	public State getState() {
		return this.pos;
	}

}
