import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class chainprog {
    static int printiter = 10000;
    static int askiter = 200000;

    /*  Main: Initialize the table of probabilities, open file input streams
     *  and call the functions to populate the probabilities table and
     *  calculate probabilities.
     *  Once the initialization stage is over, we call our decryption function.
     *  */
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		double[][] asciitable = new double[128][128];
		FileInputStream inputStreamone = null;
		FileInputStream inputStreamtwo = null;
		String file = "";
		
		asciitable = initializetable(asciitable);
		
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter a file name: ");
		file = scan.next();
		try{
		    inputStreamone = new FileInputStream(file);
		} catch (IOException e){
			System.out.println("The file doesn't exist! Here the error:");
			System.out.print(e.getMessage());
			scan.close();
			return;
		}
		try{
			inputStreamtwo = new FileInputStream(file);
		} catch (IOException e){
			System.out.println("The file doesn't exist! Here the error:");
			System.out.print(e.getMessage());
			inputStreamone.close();
			scan.close();
			return;
		}

		asciitable = poptable(asciitable, inputStreamone, inputStreamtwo);
		asciitable = probtable(asciitable);
		
		decrypter(asciitable);
		scan.close();
	}

    /*  Poptable:  Reads through the input file and populates table with
     *  appropriate occurences.*/
	public static double[][] poptable(double[][] asciitable, FileInputStream inputStreamone, FileInputStream inputStreamtwo) throws IOException {
		int r1 = 0;
		int r2 = 0;
		r2 = inputStreamtwo.read();
		for(;;){
			if ((r1 = inputStreamone.read()) == -1)
			{
				break;
			}
			if ((r2 = inputStreamtwo.read()) == -1)
			{
				break;
			}
			
			asciitable[r1][r2]++;
		}
		return asciitable;
	}
	
    /*  Probtable:  Calculates the probabilities for each cell in the
     *  probabilities table */
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
				if (asciitable[i][j]/(total[i] + asciitable.length) == 0)
				{
					continue;
				}
				asciitable[i][j] = -1 * Math.log(asciitable[i][j]/(total[i] + asciitable.length));
			}
		}
		return asciitable;
	}
	
    /*  Decrypter:  Main decryption function.  Asks user for an encrypted
     *  string -> passes the encrypted string and probabilities table to the
     *  decrypttext function to decrypt.  Decrypter will call decrypttext until
     *  the user tells it to stop */
	public static void decrypter(double[][] asciitable)
	{
		String encryptstring = "";
		double probability;
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter an encrypted text to decrypt: ");
		encryptstring = scan.nextLine();
		//scan.close();
        Scanner scan2 = new Scanner(System.in);
        while(true)
        {
	        decrypttext(encryptstring, asciitable);
            System.out.print("Do you want to try again?: ");
            String answer = scan2.nextLine();
            if (answer.equals("no"))
            {
                break;
            }
        }
        //scan2.close();
	}
	
    /*  decrypttext:  Decryttext decrypted the provded encrypted string using
     *  the Metropolis Random Walk. */
	public static void decrypttext(String estring, double[][] asciitable)
	{
		int iter = 0;
		int cont = 0;
        int numberofswaps = 0;
		double prob = 0;
		double probnew = 0;
		String oldstring = estring;
		String newstring = estring;
        String staticstring = estring;
		String response = "";
		int[] matchfunction = new int[128];
		int[] newmatchfunction;
		Scanner scan = new Scanner(System.in);
		
		//Initialization
		matchfunction = initializefunction(matchfunction);
		newmatchfunction = matchfunction.clone();
		oldstring = convertstring(oldstring, matchfunction);
		prob = calculateprob(oldstring,asciitable);
		
		//Iteration
		while (cont == 0)
		{
            swap(newmatchfunction);
			newstring = convertstring(estring, newmatchfunction);
			probnew = calculateprob(newstring, asciitable);

			if ((prob > probnew) || ((-1*Math.log(Math.random())) > (probnew - prob)))
			{
				prob = probnew;
				oldstring = newstring;
				matchfunction = newmatchfunction.clone();
                numberofswaps++;
			}
			else 
			{
				newmatchfunction = matchfunction.clone();
			}
            newstring = staticstring;
			iter++;

			if (iter % printiter == 0)
			{
				System.out.println(iter+". "+oldstring+" "+prob+" #ofSwaps: "+numberofswaps);
			}
            // Ask the user if they want to continue decrypting the provided
            // string
			if (iter % askiter == 0)
			{
				System.out.print("Do you want to continue decrypting? (Current Prob: "+prob+") (# of swaps: "+numberofswaps+"): ");
				response = scan.nextLine();
				response.toLowerCase();
				if (response.equals("no"))
				{
					//break out of the while loop + exit decryption
					cont = 1;
				}
			}
		}
		//scan.close();
		System.out.println("The decryption process has ended.");
	}
	
    /*  Calculateprob:  Calcuates probability of a string occuring using the
     *  probabilites table(asciitable) */
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
			probability += asciitable[(int)indexone][(int)indextwo];
		}
		return probability;
	}
	
    /*  Spitrandom:  Generates round integer between 0 and 127(inclusive) */
	public static int spitrandom()
	{
		Random rand = new Random();
		return rand.nextInt(128-0) + 0;		
	}
	
    /*  Convertstring:  Converts the given string using the matchfunction
     *  provided */
	public static String convertstring(String estring, int[] matchfunction)
	{
		String cstring = "";
		for (int index = 0; index < estring.length(); index++)
		{
			cstring += (char)matchfunction[(int)estring.charAt(index)];
		}
		return cstring;
	}
	
    /*  Printtable:  Prints the probabilities table out */
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
	
    /*  Initializetable:  Populates each cell of the probabilities table with 1
     *  */
	public static double[][] initializetable(double[][] asciitable)
	{
		for (int indexone = 0; indexone < asciitable.length; indexone++)
		{
			for (int indextwo = 0; indextwo < asciitable[0].length; indextwo++)
			{
				asciitable[indexone][indextwo] = 1;
			}
		}
		return asciitable;
	}
	
    /*  Initializefunction:  Initializes the mapping function.  Randomizes
     *  mappings inside the mapping function */
	public static int[] initializefunction(int[] matchfunction)
	{
		for (int index = 0; index < matchfunction.length; index++)
		{
			matchfunction[index] = index;
		}
        for (int i = 0; i < 100; i++)
        {
            swap(matchfunction);
        }
		return matchfunction;
	}

    /*  Swap:  Swaps two random mappings in a mapping function */
    public static void swap(int[] matchfunction)
    {
        int rand1 = 0;
        int rand2 = 0;
        int temp = 0;
        do
        {
		    rand1 = spitrandom();
        } while ((rand1 < 32) || (rand1 > 126));
        do
        {
		    rand2 = spitrandom();
        } while ((rand1 == rand2) || (rand2 < 32 || rand2 > 126));
		temp = matchfunction[rand1];
		matchfunction[rand1] = matchfunction[rand2];
		matchfunction[rand2] = temp;

    }
}
