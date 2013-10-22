import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;
import java.util.Timer;
import java.util.regex.Pattern;


public class LexicalAnalyzer {


	private String[] keywords = { "and","as","assert","break","class","continue","def"
			,"del","elif","else","except","exec","finally","for","from","global"
			,"if","import","in","is","lambda","None","not","or","pass","print","raise"
			,"return","try","while","with","yield"
	};


	private String[] delimiters = { "(",")","[","]","{","}",",",":",".",";","'",};
	private String[] operators = {"+","-","*","**","/","//","%","<<",">>","&","|","^"
			,"~","<",">",">=","<=","==","!=","++","--","=","+=","-=","*=","%="
	};

	private Stack<Integer> tabsStack;
	private int length = 0;
	

	public LexicalAnalyzer(File input) throws FileNotFoundException {
		super();

		tabsStack =  new Stack<Integer>();
		boolean comment = true;
		tabsStack.add(0);

		FileInputStream file = new FileInputStream(input);
		Scanner in = new Scanner(file);

		while(in.hasNext()){

			String[] line = this.countTabs(in.nextLine()).split("\\s");
			for (int index = 0; index < line.length; index++) {
				//If he finds a comment he jump the line
				if(line[index].equals("#")){
					comment = false;
					break;
				}
				else{
					this.Classified(line[index]);
				}
			}

			//Before going to the next line we need to add the \n at the final
			// of each line.
			if(comment){
				this.print("\\n", "NEWLINE");
				length++;
			}
			
		}

		//For each left token in the stack a DEDENT is push into the arrays
		while(!tabsStack.isEmpty()){
			tabsStack.pop();
			this.print("<-","DEDENT");
			length++;
		}
	}

	
	private String countTabs(String nextLine) {
		// TODO Auto-generated method stub
		char[] l = nextLine.toCharArray();
		int identationLevel = 0;
		int count = 0;
		int i = 0;

		while (Character.toString(l[i]).equals(" ")) {
			count++;
			i++;
		}

		//To know how many tabs there are
		identationLevel = count/2;

		if(identationLevel == tabsStack.peek()){
			return nextLine.trim();
		}
		else if (identationLevel > tabsStack.peek()) {
			tabsStack.add(identationLevel);
			while(identationLevel>0){
				this.print("\\t", "INDENT");
				length++;
				identationLevel--;
			}
		}
		else {
			//For each element bigger in the inside of the stack is push out and create a DEDENT.
			for (int index = 0; index < tabsStack.size(); index++) {

				if(tabsStack.get(index) > identationLevel){
					tabsStack.remove(index);
					this.print("<-", "DEDENT");
					length++;
				}
			}
		}

		return nextLine.trim();
	}

	//This method will dicide wich type the lexema is part and put in the the map.
	private boolean Classified(String lexema) {

		//If it is an identifier [a-zA-Z_0-9]
		if (Pattern.matches("\\s*", lexema)) {
			return false;
		}
		else if(Pattern.matches("[a-zA-Z][a-zA-Z_0-9]*", lexema)){

			//This means is not a  keyword
			if(!this.isMember(keywords, lexema))
			{
				this.print(lexema, "IDENTIFIER");
				length++;
			}
			else{
				//Is a keyword the lexema
				this.print(lexema, "KEYWORD");
				length++;
			}
		}
		//This means is an Integer, Double, float, imaginary, hexadecimal,octal,binary,long or any Numerical Literal
		else if (Pattern.matches("[[-][0-9][jJ].[A-F][Ox][bB][lL][eE]]+", lexema)) {
			this.print(lexema, "LITERAL");
			length++;
		}
//		else if (Pattern.matches("[+-%&|^~=/<>!][*/<>=+-]*", lexema)) {
//			this.print(lexema, tokens[3]);
//			length++;
//		}
		else if (this.isMember(operators,lexema)) {
			this.print(lexema, "OPERATOR");
			length++;
		}
		//This means is a delimiter
		else if (this.isMember(delimiters, lexema)) {
			this.print(lexema, "DELIMITER");
			length++;
		}
		else{
			//Significa que tiene una mezcla sin espacio como por ejemplo:
			// x++; x+=1  hola;
			this.reClassified(lexema);
		}
		return true;
	}

	private void reClassified(String lexema) {
		char[] l = lexema.toCharArray();
		StringBuffer lex = new StringBuffer();
		for (int i = 0; i < l.length; i++) {
			String c = Character.toString(l[i]);

			if(this.isMember(delimiters, c) ){
				this.Classified(lex.toString());
				this.Classified(c);
				lex.delete(0, lex.length());
			}
			else if (this.isMember(operators, c)) {
				this.Classified(lex.toString());
				lex.delete(0, lex.length());
				if(i != 0 && this.isMember(operators, Character.toString(l[i-1]))){
					this.Classified(Character.toString(l[i-1]) + c);
				}
			}
			else {
				lex.append(c);

			}
		}
	}


	public boolean isMember(String[] array, String member) {
		boolean isMember = false;
		for (int i = 0; i < array.length; i++) {
			String string = array[i];

			if(string.equals(member)){
				isMember = true;
				break;
			}
		}
		return isMember;
	}
	
	private void print(String key, String value) {
		System.out.println("'"+key+"'"+"\t\t"+ value);
	}
	public int size() {
		return length;
	}
}
