package application;
	
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;
import application.PuzzleCollection.EmptyListException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class Cryptogram extends Application {
	
	private static String fileName;
	private static String encryptSentence;
	private static PuzzleCollection pc;
	private static Stage st;
	private static TextField textField[];
	private static TextField encrypt_t[];
	private static boolean[] alphaGuessed;
	private static int[] freq;
	private static HashMap<Character, ArrayList<Integer>> textboxes;
	private static ArrayList<Integer> textboxIndices;
	private static boolean doubleAlpha = false;
	private static int hintcnt;
	
	@Override
	public void start(Stage primaryStage) {
		st = primaryStage;
		st.setResizable(false);
		st.setTitle("Cryptogram");
		System.out.println("SPM Project: A Cryptogram\nCode by:- \n\tRikhav Nilesh Dedhia\n\tRishikesh Maddi"
				+ "\n\tDaksh Parikh\n\tKrina Karia");
		initiate();
	}
	
	private static void initiate(){
		try{
			pc = new PuzzleCollection(fileName);
			buildGui();
		}
		catch (FileNotFoundException | EmptyListException e) {
			pc = new PuzzleCollection();
			verifyAdmin();
		}
	}
	
	public static void main(String[] args) {
//		fileName = args[0];
		fileName = "Puzzle_database.txt";
		System.out.println(fileName);
		launch(args);
	}
	
	static void buildGui(){
		try {
			st.setHeight(255);
		    st.setWidth(670);
			HBox ButtonPane = new HBox(10);
			VBox UnusedFrePane = new VBox(10);
			
			Robot rb = new Robot();
			
			GridPane MainPain = new GridPane();
			GridPane HintDiffPane = new GridPane();
			GridPane PuzzlePane = new GridPane();
			
			Scene scene = new Scene(MainPain,400,400);
			
			Button Admin_B = new Button("Admin");
			Button Reset_B = new Button("Reset");
			Button NewPuzz_B = new Button("New Game");
			Button HintMe_B = new Button("Give Hint");
			Button Submit_B = new Button("Submit");
			Button About_B = new Button("About");
			
			Label UnusedAlpha_L = new Label("Unused Alphabets are: -");
			Label AlphaFre_L = new Label("Frequency of each Alphabets is: -");
			Label UnusedAlphaV_L = new Label("");
			Label AlphaFreV_L = new Label("");
			Label Hint_L = new Label("");
			Label Diff_L = new Label("");
			
			String Style = "-fx-padding: 10;" + 
	                  "-fx-border-style: solid inside;" + 
	                  "-fx-border-width: 2;" +
	                  "-fx-border-insets: 5;" + 
	                  "-fx-border-radius: 0;" + 
	                  "-fx-border-color: blue;";

			Font fSize = new Font(25);
			
			Admin_B.setPrefWidth(100);
			Reset_B.setPrefWidth(100);
			HintMe_B.setPrefWidth(100);
			NewPuzz_B.setPrefWidth(100);
			Submit_B.setPrefWidth(100);
			About_B.setPrefWidth(100);
			
			UnusedAlphaV_L.setFont(fSize);
			AlphaFreV_L.setFont(fSize);
			Hint_L.setFont(fSize);
			Diff_L.setFont(fSize);
			
			ButtonPane.setStyle(Style);
			UnusedFrePane.setStyle(Style);
			HintDiffPane.setStyle(Style);
			PuzzlePane.setStyle(Style);
			
			Admin_B.setOnAction(e -> {
				PuzzlePane.getChildren().removeAll();
				PuzzlePane.getChildren().clear();
		        verifyAdmin();
		    });
				
			//2 - Play a new puzzle by - Rishikesh
			NewPuzz_B.setOnAction(e ->{
				textboxes = new HashMap<Character, ArrayList<Integer>>();
				alphaGuessed = new boolean[26];
				freq = new int[26];
				
				PuzzlePane.getChildren().removeAll();
				PuzzlePane.getChildren().clear();
				HintDiffPane.getChildren().removeAll();
				HintDiffPane.getChildren().clear();
				
				hintcnt = 0;
				HintMe_B.setDisable(false);
				pc.SelectPuzzle();
				
				Hint_L.setText("Puzzle is about: - " + pc.pcGetHint());
				Diff_L.setText("Difficulty of Puzzle: - " + pc.pcGetDiff());
				HintDiffPane.add(Hint_L,0,0);
				HintDiffPane.add(Diff_L,0,1);
				encryptSentence = pc.encrypt();
				System.out.println(encryptSentence);
				
				textField = new TextField[encryptSentence.length()];
				encrypt_t = new TextField[encryptSentence.length()];
				
				int cnt = 0;
				int j = 0;
				for(int i = 0; i < encryptSentence.length(); i++){
					cnt++;
					
					textField[i] = new TextField();
					
					//Formatter to ensure only 1 Capital alphabet
					textField[i].setTextFormatter(new TextFormatter<String>((Change change) -> {
			            String newText = change.getControlNewText();
			            if (newText.length() > 1) {
			                return null ;
			            }
			            else{
			            	if(newText.length() != 0){
			            		if(!Character.isAlphabetic(newText.charAt(0))){
				                     return null;
				            	}
			            		if(!Character.isUpperCase(newText.charAt(0))){
			            			change.setText(newText.toUpperCase());
			            		}
			            	}
			                return change ;
			            }
			        }));
					
					textField[i].setMaxWidth(35);
				
					//Setting the listener to each textbox to listen to any alphabet entered	
					final int index = i;
					textField[i].textProperty().addListener((obs, oldVal, newVal) -> {
				        if(textboxIndices.size() > 0){
				        	return;
				        }
				        
				        //10 - Check if alphabet entered twice by - Rikhav
				        if(newVal.length() > 0){
				        	if(checkAlpha(newVal.charAt(0))){
				        		Alert alert = new Alert(AlertType.ERROR);
				                alert.setTitle("Error");
				                alert.setContentText("Alphabet \""+newVal + "\" already used");
				                alert.showAndWait();
				                doubleAlpha = true;
				                textField[index].setText("");
				                return;
				        	}
				        }
				        
				        if(doubleAlpha){
				        	doubleAlpha = false;
				        	return;
				        }
				        
				        updateTextBoxes(encryptSentence.charAt(index), newVal);
				        if(oldVal.length() > 0) alphaGuessed[oldVal.charAt(0) - 65] = false;
				        UnusedAlphaV_L.setText(getUnused());
				        UnusedFrePane.getChildren().removeAll();
				        UnusedFrePane.getChildren().clear();
				        UnusedFrePane.getChildren().addAll(UnusedAlpha_L,UnusedAlphaV_L, AlphaFre_L,AlphaFreV_L);
				        if(index < encryptSentence.length() - 1) rb.keyPress(KeyEvent.VK_TAB);
				    });
					
					encrypt_t[i] = new TextField(Character.toString(encryptSentence.charAt(i)));
					encrypt_t[i].setStyle("-fx-box-border: none;"+
										  "-fx-background-insets: 0;"+
										  "-fx-background-color: transparent, transparent;"+
										  "-fx-background-radius: 0, 0, 0, 0;"+
										  "-fx-text-inner-color: black;"+
										  "-fx-opacity: 1.0;");
					encrypt_t[i].setMaxWidth(35);
					encrypt_t[i].setDisable(true);
					
					if(encryptSentence.charAt(i) != ' '){
						
						PuzzlePane.add(textField[i], i%18, j + 1);
						freq[encryptSentence.charAt(i) - 65]++;
						
						//Mapping each unique encrypted character to the indices of textbox
						if(textboxes.containsKey(encryptSentence.charAt(i))){
							textboxIndices = textboxes.get(encryptSentence.charAt(i));
						}
						else{
							textboxIndices = new ArrayList<Integer>();
						}
						textboxIndices.add(i);
						textboxes.put(encryptSentence.charAt(i), textboxIndices);
						textboxIndices = new ArrayList<Integer>();
						
					}
					else{
						//Disabling textbox as it is a space in the sentence
						textField[i].setDisable(true);
						textField[i].setStyle("-fx-box-border: none;"+
								  "-fx-background-insets: 0;"+
								  "-fx-background-color: transparent, transparent;"+
								  "-fx-background-radius: 0, 0, 0, 0;");
						PuzzlePane.add(textField[i], i%18, j + 1);
						encrypt_t[i].setDisable(true);
						
					}
					PuzzlePane.add(encrypt_t[i], i%18, j+2);
					if(cnt == 18){
						j+=2;
						cnt = 0;
					}
				}
				
				AlphaFreV_L.setText(getFre());
				UnusedAlphaV_L.setText(getUnused());
				UnusedFrePane.getChildren().removeAll();
				UnusedFrePane.getChildren().clear();
				UnusedFrePane.getChildren().addAll(UnusedAlpha_L,UnusedAlphaV_L, AlphaFre_L,AlphaFreV_L);
		        
				st.setHeight(420 + (60 * ((encryptSentence.length() / 18) + 1)));
				System.out.println(textboxes);
			});
			
			//3 - Reset the puzzle i.e. make all text boxes empty by - Rikhav
			Reset_B.setOnAction(e ->{
				for(int i = 0; i < encryptSentence.length(); i++){
					if(textField[i].getText() != "") textField[i].setText("");
				}
				hintcnt = 0;
				HintMe_B.setDisable(false);
			});
			
			//11 - To submit the puzzle. by - Rishikesh and Krina
			Submit_B.setOnAction(e -> {
				StringBuilder sb = new StringBuilder();
				Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Messsage");
                
				for(int i = 0; i < encryptSentence.length(); i++){
					if(textField[i].isDisable()){
						sb.append(" ");
						continue;
					};
					if(textField[i].getText().length() == 0){
		                alert.setContentText("Please fill all the boxes before submitting");
		                alert.showAndWait();
		                return;
					}
					sb.append(textField[i].getText().charAt(0));
				}
				if(pc.checkPuzzle(sb.toString())){
	                alert.setContentText("You Won");
	                alert.showAndWait();
	                NewPuzz_B.fire();
				}
				else{
	                alert.setContentText("Incorrect guess");
	                alert.showAndWait();
	                Reset_B.fire();
	                return;
				}
			});
			
			//15 - To display "About Game" by - Rishikesh
			About_B.setOnAction(e -> {
				Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("About the game");
                alert.setContentText("Cryptogram are short sentences which are replaced by an encrypted sentences"
                		+ "using substitution cipher. Each alphabet is mapped to one and only one other alphabet. This"
                		+ " mapping is used to do the substitution.\n\nTo play the game, click on \"New\" and a new"
                		+ " puzzle will pop-up. Keep guessing the words by entering alphabet into textboxes.\n\nFor"
                		+ " help, you can use the Frequency of each alphabet used and the \"Alphabet Used\" column to "
                		+ "track the alphabet you have already used.\n\nOnce you think you have guessed the correct"
                		+ "sentence, click on \"Submit\" to get it checked.\n\nTo add a new puzzle to the list, click"
                		+ " on \"Admin\", login using the proper credentials and add new puzzles.");
                alert.showAndWait();
			});
			
			//14 - To give hint by - Rikhav and Daksh
			HintMe_B.setOnAction(e -> {
				if(hintcnt == 5){
					Alert alert = new Alert(AlertType.INFORMATION);
	                alert.setTitle("Hint");
	                alert.setContentText("You have used all 5 hints");
	                alert.showAndWait();
	                HintMe_B.setDisable(true);
	                return;
				}
				ArrayList<Integer> notGuessed = getNotGuessed(); 
				Random rand = new Random();
				if(notGuessed.size() == 0){
					Alert alert = new Alert(AlertType.INFORMATION);
	                alert.setTitle("Hint");
	                alert.setContentText("There are no Alphabets left to hint");
	                alert.showAndWait();
	                return;
				}
				hintcnt++;
				int x = rand.nextInt(notGuessed.size());
				String hint = pc.getChar(notGuessed.get(x));
				if(alphaGuessed[hint.charAt(0) - 65])
					correctTextField(hint);
				textField[notGuessed.get(x)].setText(hint);
			});
			
			ButtonPane.getChildren().addAll(Reset_B, NewPuzz_B, HintMe_B, Admin_B,Submit_B, About_B);
			
//			if(UnusedAlphaV.getText().length() == 0) UnusedFrePane.getChildren().addAll(UnusedAlpha, AlphaFre);
//			else UnusedFrePane.getChildren().addAll(UnusedAlpha,UnusedAlphaV, AlphaFre,AlphaFreV);
			
			MainPain.add(ButtonPane,0,0);
			MainPain.add(HintDiffPane, 0, 1);
			MainPain.add(PuzzlePane,0,2);
			MainPain.add(UnusedFrePane, 0, 3);
			st.setScene(scene);
			NewPuzz_B.fire();
			st.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//1 - Add a new puzzle by - Rikhav
	static void newPuzzle(){
		
		HBox hpane = new HBox(10);
		GridPane root = new GridPane();
		
		Label hint_l = new Label("Hint(Max 30 char)");
		Label sentence_l = new Label("Sentence(only alphabets & space allowed)");
		Label errors_l = new Label("");
		Label diff_l = new Label("Difficulty(1 - 5)");
		Label errord_l = new Label("");
		Label errord_e = new Label("");
		
		Button add = new Button("Add Puzzle");
		Button back = new Button("Back");
		
		String diffLevel[] = {"1","2","3","4","5"}; 

		ComboBox<String> combo_box = new ComboBox<String>(FXCollections.observableArrayList(diffLevel));
		
		TextField hint_txt = new TextField("");
		TextField sentence_txt = new TextField("");
		
		hint_txt.setTextFormatter(new TextFormatter<String>((Change change) -> {
            String newText = change.getControlNewText();
            if (newText.length() > 30) {
                return null ;
            }
            return change ;
        }));
		
		sentence_txt.setTextFormatter(new TextFormatter<String>((Change change) -> {
            String newText = change.getControlNewText();
            if(newText.length() != 0){
        		if((newText != null) && (newText.matches("^[a-zA-Z ]*$"))){
                    return change;
            	}
        		return null;
        	}
            return change;
		}));
		
		root.add(hint_l, 0, 0);
		root.add(hint_txt, 1, 0);
		root.add(sentence_l, 0, 1);
		root.add(sentence_txt, 1, 1);
		root.add(errors_l, 2, 1);
		root.add(diff_l, 0, 2);
		root.add(combo_box, 1, 2);
		root.add(errord_l, 2, 2);
		hpane.getChildren().addAll(add,back);
		root.add(hpane, 0, 3);
		root.add(errord_e, 1, 3);
		
		add.setOnAction(e -> {

			String hint, sentence;//
			int diff;//
			
	        hint = hint_txt.getText();
	        sentence = sentence_txt.getText().toUpperCase();
	        
	        if(hint.length() == 0 || sentence.length() == 0){
	        	errord_e.setText("Please enter all the details");
	        	errord_e.setTextFill(Color.RED);
	        	return;
	        }
	        
	        try{
	        	diff = Integer.parseInt(combo_box.getValue().toString());
	        	combo_box.valueProperty().set(null);
	        }catch(Exception z){
	        	errord_e.setText("Select a value from dropdown");
	        	errord_e.setTextFill(Color.RED);
	        	return;
	        }
	      
	        pc.addPuzzle(sentence, hint, diff);
	        sentence_txt.setText("");
	        hint_txt.setText("");
	        errord_e.setText("Puzzle added");
	        errord_e.setTextFill(Color.BLUE);
	    });
		
		back.setOnAction(e -> {
			pc.updateDB(fileName);
			initiate();
		});
		
		final Scene scene = new Scene(root, 500, 400);
	    st.setScene(scene);
	    st.setHeight(175);
	    st.setWidth(500);
	    st.show();
	}
	
	//7 - Make a string of frequency of alphabets by - Rishikesh
	private static String getFre(){
		StringBuilder frestr = new StringBuilder();
		for(int i = 0; i < 26; i++){
			if(freq[i] == 0) continue;
			frestr.append(((char)(i + 65)));
			frestr.append(freq[i]);
			frestr.append(";");
			frestr.append(" ");
		}
		return frestr.toString();
	}
	
	//6 - Make a string of unused alphabets by - Rikhav
	private static String getUnused(){
		StringBuilder unusedstr = new StringBuilder();
		for(int i = 0; i < 26; i++){
			if(!alphaGuessed[i]){
				unusedstr.append((char)(i + 65));
				unusedstr.append(" ");
			}
		}
		return unusedstr.toString();
	}
	
	//9 - To update all the textbox with same alphabet by - Rikhav
	private static void updateTextBoxes(char c, String newVal){
		if(newVal.length() > 0) alphaGuessed[newVal.charAt(0) - 65] = true;
		textboxIndices = textboxes.get(c);
		for(int i = 0; i < textboxIndices.size(); i++){
			textField[textboxIndices.get(i)].setText(newVal);
		}
		textboxIndices = new ArrayList<Integer>();
	}
	
	private static boolean checkAlpha(char c){
		return alphaGuessed[c - 65];
	}
	
	//13 - Admin Login by - Krina
	static void verifyAdmin(){
		Label id_l = new Label("Username");
		Label pass_l = new Label("Password");
		
		TextField id_txt = new TextField("");
		PasswordField pass_txt = new PasswordField();
		
		Button login = new Button("Login");
		Button back = new Button("Back");
		
		GridPane root = new GridPane();
		
		login.setOnAction(e -> {
			admin a = new admin();
			if(a.verifyAdmin(id_txt.getText(), pass_txt.getText())) {
				newPuzzle();
			}
			else{
				Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setContentText("Incorrect username or password");
	            alert.showAndWait();
			}
		});
		
		back.setOnAction(e -> {
			initiate();
		});
		
		root.add(id_l, 0, 0);
		root.add(id_txt, 1, 0);
		root.add(pass_l, 0, 1);
		root.add(pass_txt, 1, 1);
		root.add(login, 0, 2);
		root.add(back, 1, 2);
		
		final Scene scene = new Scene(root, 500, 400);
	    st.setScene(scene);
	    st.setHeight(140);
	    st.setWidth(300);
	    st.show();
	}
	
	static ArrayList<Integer> getNotGuessed(){
		ArrayList<Integer> notGuessed = new ArrayList<Integer>();
		Iterator<Entry<Character, ArrayList<Integer>>> it = textboxes.entrySet().iterator();
		while (it.hasNext()) { 
            Entry<Character, ArrayList<Integer>> mapElement = it.next(); 
            ArrayList<Integer> temp = mapElement.getValue();
            if(textField[temp.get(0)].getText().length() == 0){
            	notGuessed.add(temp.get(0));
            }
        }
		System.out.println(notGuessed);
		return notGuessed;
	}
	
	static void correctTextField(String hint){
		Iterator<Entry<Character, ArrayList<Integer>>> it = textboxes.entrySet().iterator();
		while (it.hasNext()) { 
            Entry<Character, ArrayList<Integer>> mapElement = it.next(); 
            ArrayList<Integer> temp = mapElement.getValue();
            if(textField[temp.get(0)].getText().equals(hint)){
            	textField[temp.get(0)].setText("");
                return;
            }
        }
	}
}