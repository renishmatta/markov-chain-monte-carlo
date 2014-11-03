import static java.lang.System.*;

import java.io.*;
import java.util.Scanner;

public class chainprog {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		double[][] asciitable = new double[128][128];
		FileInputStream inputStreamone = null;
		FileInputStream inputStreamtwo = null;
		String file = "";
		char ch = '\0';
		
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter a file name: ");
		file = scan.next();
		try{
		inputStreamone = new FileInputStream(file);
		} catch (IOException e){
			System.out.println("The file doesn't exist! Here the error:");
			System.out.print(e.getMessage());
			return;
		}
		try{
			inputStreamtwo = new FileInputStream(file);
		} catch (IOException e){
			System.out.println("The file doesn't exist! Here the error:");
			System.out.print(e.getMessage());
			return;
		}

		asciitable = poptable(asciitable, inputStreamone, inputStreamtwo);
		asciitable = probtable(asciitable);
		probword(asciitable);
		printtable(asciitable);
		scan.close();
	}

	public static double[][] poptable(double[][] asciitable, FileInputStream inputStreamone, FileInputStream inputStreamtwo) throws IOException {
		int r1 = 0;
		int r2 = 0;
		int s1 = 0;
		int s2 = 0;
		r1 = inputStreamtwo.read();
		for(;;){
			if ((r1 = inputStreamone.read()) == -1)
			{
				break;
			}
			if ((r2 = inputStreamtwo.read()) == -1)
			{
				break;
			}
			
			s1 = (int)(char)r1;
			s2 = (int)(char)r2;
			asciitable[r1][r2]++;
			//System.out.println((char)s1+" "+(char)s2);
		}
		return asciitable;
	}
	
	public static double[][] probtable(double[][] asciitable)
	{
		int[] total = new int[128];
		for (int i = 0; i < asciitable.length; i++)
		{
			for (int j = 0; j < asciitable[0].length; j++)
			{
				total[i]+=asciitable[i][j];
			}
		}
		for (int i = 0; i < asciitable.length; i++)
		{
			if (total[i] == 0)
			{
				continue;
			}
			for (int j = 0; j < asciitable[0].length; j++)
			{
				if (asciitable[i][j]/total[i] == 0)
				{
					continue;
				}
				asciitable[i][j] = -1 * Math.log(asciitable[i][j]/total[i]);
			}
		}
		return asciitable;
	}
	
	public static void probword(double[][] asciitable)
	{
		String word = "";
		double probability;
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter a word: ");
		word = scan.next();
		probability = calculateprob(word, asciitable);
		System.out.println("The probability that the word is in the text is: "+probability);
		scan.close();
	}
	
	public static double calculateprob(String word, double[][] asciitable)
	{
		if (word.length() < 2)
		{
			System.out.println("The word entered is too small.");
			System.exit(0);
		}
		double probability = asciitable[(int)word.charAt(0)][(int)word.charAt(1)];
		for (int index = 1; index < word.length()-1; index++)
		{
			char indexone = word.charAt(index);
			char indextwo = word.charAt(index+1);
			probability += asciitable[indexone][indextwo];
		}
		return probability;
	}
	
	public static void printtable(double[][] asciitable)
	{
		for (int i = 0; i < asciitable.length; i++)
		{
			System.out.println("Row "+i+":");
			for (int j = 0; j < asciitable[0].length; j++)
			{
				System.out.print(asciitable[i][j]+" ");
			}
			System.out.println("");
		}
	}
}
