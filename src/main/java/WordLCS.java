import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

/**
 * @author Nisha Narayanaswamy
 * 		
 * Class calculates the Longest Common Subsequence(LCS) between two text documents
 *      
 */
public class WordLCS {

	/**
	 * Calculates the length of LCS by comparing words in text document.
	 * Length represents the longest number of words matched.
	 * @param strActual Manually extracted content that is used for evaluation
	 * @param strProcessed Content processor output
	 */
	private void calculateLCS(String strActual, String strProcessed) {

		strActual = preprocessText(strActual);
		strProcessed = preprocessText(strProcessed);
		String[] arrActual = strActual.split(" ");
		String[] arrProcessed = strProcessed.split(" ");

		int[][] lengthLCS = new int[arrActual.length][arrProcessed.length];  //Matrix to store length of LCS in terms of words		
		char[][] textLCS = new char[arrActual.length][arrProcessed.length];	//Matrix to store the words matched in the LCS

		//Calculate the length of LCS
		for (int i = 1; i < arrActual.length; i++) {
			for (int j = 1; j < arrProcessed.length; j++) { 				
				
				// If last word compared matched, then increment length
				if (arrActual[i].equals(arrProcessed[j])) { 	//case-sensitive comparison
					lengthLCS[i][j] = lengthLCS[i - 1][j - 1] + 1;
					textLCS[i][j] = 's'; 	//s = same = matched word
				}					
				// If word does not match, then use the maximum of previous largest length stored
				else if (lengthLCS[i][j - 1] > lengthLCS[i - 1][j]){
					lengthLCS[i][j] = lengthLCS[i][j - 1];
					textLCS[i][j] = 'j';
				}
				else{
					lengthLCS[i][j] = lengthLCS[i - 1][j];
					textLCS[i][j] = 'i';
				}
			}
		}
		
		// store length of the longest common subsequence
		int lenLCS = lengthLCS[(arrActual.length - 1)][(arrProcessed.length - 1)];

		System.out.println("Length of actual reference text : " + (arrActual.length - 1));
		System.out.println("Length of context processor text : " + (arrProcessed.length - 1));
		System.out.println("The length of the longest common subsequence is " + lenLCS);
		
/*		printLCS(textLCS, arrActual, arrActual.length-1, arrProcessed.length-1);
		System.out.println(" ");*/

		// calculate performance metrics
		performanceMetrics(arrActual.length, arrProcessed.length, lenLCS);
	}
	
	
	/**
	 * Prints the words matched in the longest common subsequence
	 * @param textLCS contains LCS results
	 * @param arrActual contains the reference text 
	 * @param i reference text index
	 * @param j content processor text index
	 */
	public void printLCS(char[][] textLCS, String[] arrActual, int i, int j) {
		if (i == 0 || j == 0) // reached end
			return;
		if (textLCS[i][j] == 's') { // found a match
			printLCS(textLCS, arrActual, i - 1, j - 1);
			System.out.print(arrActual[i] + " ");
		} else if (textLCS[i][j] == 'j')
			printLCS(textLCS, arrActual, i, j - 1);
		else
			printLCS(textLCS, arrActual, i - 1, j);
	}

	/**
	 * Method calculates three performance metrics - Precision, Recall, F-1 score for a document
	 * @param lenActual Total length of reference text
	 * @param lenProcessed Total length of content processor text
	 * @param lenLCS Length of longest common subsequence in terms of words
	 */
	public void performanceMetrics(int lenActual, int lenProcessed, int lenLCS) {
		float precision = 0;
		float recall = 0;
		float F1score = 0;
		
		precision = ((float)lenLCS / lenActual)*100;
		recall = ((float)lenLCS / lenProcessed)*100;
		F1score = (2 * precision * recall) / (precision + recall);
		
		System.out.println("********************************************************");
		System.out.println(" Precision = " + precision + "%");
		System.out.println(" Recall = " + recall + "%");
		System.out.println(" F-1 score = " + F1score + "%");
	}

	/**
	 * Method removes extra spaces
	 */
	public String preprocessText(String strText) {
		strText = strText.replaceAll("\\s+", " "); // replace multiple whitespace characters with single space
		strText = strText.trim();
		strText = String.format("%" + (strText.length() + 1) + "s", strText);	//Append one leading space to simplify LCS calculation
		return strText;
	}

	public static void main(String[] args) {
		File fileActual, fileProcessed;
		String strActual = null, strProcessed = null;
		WordLCS wLcs = new WordLCS();
		fileActual = new File("E:/evaluation/GoldStandard/Why women professors.txt"); 	// Manually extracted content that is used for evaluation  
		fileProcessed = new File("E:/evaluation/ContentProcessor/OUT_Why women professors.txt"); // Content processor output 
		try {
			strActual = FileUtils.readFileToString(fileActual);	
			strProcessed = FileUtils.readFileToString(fileProcessed);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Reference file name" + fileActual);
		//compare actual vs. content processor output using Longest Common Subsequence algorithm
		wLcs.calculateLCS(strActual, strProcessed);
	}
}
