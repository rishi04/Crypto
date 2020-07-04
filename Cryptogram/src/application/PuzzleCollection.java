package application;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class PuzzleCollection {
	private static ArrayList<Puzzles> list = new ArrayList<Puzzles>();
	private static String hint, sentence;
	private static int diff;
	private static Puzzles currPuzzle;
	private static Random rand;
	
	//5 - When the program starts, load the arraylist with all the puzzles by - Daksh
	PuzzleCollection(String fileName) throws FileNotFoundException, EmptyListException{
		FileInputStream file_ip = new FileInputStream(fileName);
		try{
			ObjectInputStream file = new ObjectInputStream(file_ip);
			try{
				while (true){
					Puzzles P = (Puzzles) file.readObject();
					list.add(P);
				}
			}
			catch(EOFException eof){
				file.close();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (list.size() == 0) throw new EmptyListException();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PuzzleCollection() {}
	
	public String SelectPuzzle(){
		rand = new Random();
		
		currPuzzle = list.get(rand.nextInt(list.size()));
		sentence = currPuzzle.getSentence();
		hint = currPuzzle.getHint();
		diff = currPuzzle.getDiff();
		System.out.println(sentence);
		return sentence;
	}
	
	public String pcGetHint(){return hint;}
	public int pcGetDiff(){return diff;}
	public int collectionSize(){ return list.size();}
	public String getChar(int x){return Character.toString(sentence.charAt(x));}

	public String encrypt(){
		char[] sub = GenerateKey();
		StringBuilder op = new StringBuilder();
		
		for(int i = 0; i < sentence.length();i++){
			if(sentence.charAt(i) != ' '){
				op.append(sub[sentence.charAt(i) - 65]);
			}
			else{
				op.append(' ');
			}
		}
		return op.toString();
	}
	
	//12 - Generate new key everytime by - Daksh
	private static char[] GenerateKey(){
		char[] key = new char[26];
		boolean[] visited = new boolean[26];
		rand = new Random();
		
		for(int i = 0; i < 26; i++){
			int x = rand.nextInt(26);
			if(visited[x] || x == i){
				i--;
				continue;
			}
			visited[x] = true;
			key[i] = (char)(x + 65);
		}
		System.out.println();
		return key;
	}
	
	//4 - Once admin logs out, write all the puzzles to the file - Krina
	public void updateDB(String fileName){
		try {
			file_write(fileName);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	static private void file_write(String fileName) throws FileNotFoundException, IOException{
    	FileOutputStream file_op = new FileOutputStream(fileName);           // FileNotFoundException 
        ObjectOutputStream file  = new ObjectOutputStream(file_op);
		for(Puzzles p : list){
			file.writeObject(p);
		}
		file.close();
	}
	
	public boolean checkPuzzle(String check){
		if(check.contentEquals(sentence)) return true;
		return false;
	}
	
	void addPuzzle(String s, String h, int d){
        list.add(new Puzzles(h, s, d));
        System.out.println("Puzzle added to list " + list.size());
	}
	
	class EmptyListException extends Exception{
		private static final long serialVersionUID = 1L;
	    public String toString(){
	      return ("No data in the database");
	   }
	}
};
