package application;
import java.io.Serializable;

public class Puzzles implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String hint;
	private String sentence;
	private int diff;
	
	protected Puzzles(){};
	
	protected Puzzles(String h, String s, int d){
		hint = h;
		sentence = s;
		diff = d;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	protected String getHint(){return hint;}
	protected String getSentence(){return sentence;}
	protected int getDiff(){return diff;}

}
