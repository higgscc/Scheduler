package sample.interfaces;

public interface Message {
	void completed();
	
	String getGroup();
	boolean isLastInGroup();
}
