import java.io.File;
import java.io.FileNotFoundException;


public class Main {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		
		String[] files = {"google1.txt","google2.txt","google3.txt"
				,"google4.txt","google5.txt","google6.txt","google7.txt"};
		int sumLexemas = 0;
		for (int l = 0; l < 10; l++) {
			for (int i = 0; i < files.length; i++) {
				String string = files[i];
				System.out.println("--------------------------"+string+"----------------------------------");
				LexicalAnalyzer lexi = new LexicalAnalyzer(new File(string));
				sumLexemas+=lexi.size();
			}
		}
		
		//System.out.println(Pattern.matches("[+-%&|^~=/<>!][*/<>=+-]*", "++"));
		long endTime = System.currentTimeMillis();
		System.out.println("You have " +sumLexemas+" lexemas");
		System.out.println("That took " + (endTime - startTime)*Math.pow(10, -3) + " seconds");
		
	}

}
