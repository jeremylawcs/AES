import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CAlgos
{
	// recieves two lines of binary input, prints the input 
	// encrypts and prints encrypted plaintext, decrypts and prints plaintext
	public static void main(String[] args) throws FileNotFoundException
	{
		String eOrD = args[0];
		Scanner fileScanner = null;
		try
		{
			fileScanner = new Scanner(new File(args[1]));

		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("File not found!");
		}
		finally
		{
			// take input
			String binaryIn = fileScanner.nextLine();
			String binaryInCopy = binaryIn;
			// take key
			String keyIn = fileScanner.nextLine();
			String keyInCopy = keyIn;
			// initialization 
			int[][] input = new int[4][4];
			int[][] key = new int[4][4];
			// used to keep track of x and y values of 2d array
			int l = 0, m = 0;
			// do this for 16 bytes
			for (int i = 0; i<16; i++) 
			{
				// System.out.println(l + " " + m);
				String in = "";
				// add the first character of binaryIn to 'in' and 
				// remove it from original string
				// 8 times for 8 bits = 1 byte
				for (int j = 0; j<8; j++) 
				{
					in += binaryIn.charAt(0);
					binaryIn = binaryIn.substring(1);
				}
				String keyWordIn = "";
				// do the same for the key
				for (int j = 0; j<8; j++) 
				{
					keyWordIn += keyIn.charAt(0);
					keyIn = keyIn.substring(1);
				}
				// in is a binary number, turn it into a decimal number
				input[l][m] = Integer.parseInt(in, 2);
				key[l][m] = Integer.parseInt(keyWordIn, 2);
				// keep track of which index we are at in the 2d array
				if((i+1)%4 == 0 && i != 0)
				{
					m++;
					l = 0;
				}
				else
				{
					l++;
				}
			}
			ArrayList<int[][]> keyShedule = new ArrayList<int[][]>();
			keyShedule = AES.keyExpansion(key);
			PrintWriter writeToFile = null;
			if(eOrD.equals("E") || eOrD.equals("e"))
			{
				ArrayList<int[][]> whatDoInput	 = AES.avFlipBit(binaryInCopy);
				ArrayList<int[][]> whatDoKey	 = AES.avFlipBit(keyInCopy);
				// Print out the hexadecimal representation of each array element in a cube
				// long startTime = System.nanoTime();
				long startTime = System.currentTimeMillis();
				// encrypt the input using the key given
				int[][] output = AES.encrypt(input, keyShedule);
				long duration = (System.currentTimeMillis() - startTime);
				ArrayList<ArrayList<String>> data = AES.generateAvalancheData(whatDoInput, whatDoKey);
				float firstCount = 0;
				float secondCount = 0;
				ArrayList<Integer> aES0List = new ArrayList<Integer>();
				ArrayList<Integer> aES1List = new ArrayList<Integer>();
				ArrayList<Integer> aES2List = new ArrayList<Integer>();
				ArrayList<Integer> aES3List = new ArrayList<Integer>();
				ArrayList<Integer> aES4List = new ArrayList<Integer>();
				ArrayList<Integer> aES0ListSecond = new ArrayList<Integer>();
				ArrayList<Integer> aES1ListSecond = new ArrayList<Integer>();
				ArrayList<Integer> aES2ListSecond = new ArrayList<Integer>();
				ArrayList<Integer> aES3ListSecond = new ArrayList<Integer>();
				ArrayList<Integer> aES4ListSecond = new ArrayList<Integer>();
				for (int j = 0; j<55; j++) 
				{
					firstCount = secondCount = 0;
					for (int i = 1; i<129; i++) 
					{
						firstCount += AES.bitsDifferent(data.get(j).get(0), data.get(j).get(i));
					}
					if(j < 11)
						aES0List.add(new Integer(Math.round(firstCount/128)));
					else if(11 <= j && j < 22)
						aES1List.add(new Integer(Math.round(firstCount/128)));
					else if(22 <= j && j < 33)
						aES2List.add(new Integer(Math.round(firstCount/128)));
					else if(33 <= j && j < 44)
						aES3List.add(new Integer(Math.round(firstCount/128)));
					else if(j < 55)
						aES4List.add(new Integer(Math.round(firstCount/128)));
					for (int i = 130; i<258; i++) 
					{
						secondCount += AES.bitsDifferent(data.get(j).get(0), data.get(j).get(i));
					}
					if(j < 11)
						aES0ListSecond.add(new Integer(Math.round(secondCount/128)));
					else if(11 <= j && j < 22)
						aES1ListSecond.add(new Integer(Math.round(secondCount/128)));
					else if(22 <= j && j < 33)
						aES2ListSecond.add(new Integer(Math.round(secondCount/128)));
					else if(33 <= j && j < 44)
						aES3ListSecond.add(new Integer(Math.round(secondCount/128)));
					else if(j < 55)
						aES4ListSecond.add(new Integer(Math.round(secondCount/128)));
				}
				try
				{
					writeToFile = new PrintWriter("output.txt", "UTF-8");
					writeToFile.println("ENCRYPTION");
					writeToFile.println("Plaintext P: \t" + AES.matrixToString(input));
					writeToFile.println("Key K: \t\t\t" + AES.matrixToString(key));
					writeToFile.println("Ciphertext C: \t" + AES.matrixToString(output));
					writeToFile.println("Running Time: \t"+duration+"ms");
					writeToFile.println("Avalanche:\nP and Pi under K");
					writeToFile.println("Round\t\tAES0\tAES1\tAES2\tAES3\tAES4");
					for(int i = 0; i<aES0List.size()-1;i++)
					{
						writeToFile.println("  " + i + "\t\t\t" + aES0List.get(i) + "\t\t" + aES1List.get(i) + "\t\t" + aES2List.get(i) + "\t\t" + aES3List.get(i) + "\t\t" + aES4List.get(i));
					}
						writeToFile.println("  10\t\t" + aES0List.get(10) + "\t\t" + aES1List.get(10) + "\t\t" + aES2List.get(10) + "\t\t" + aES3List.get(10) + "\t\t" + aES4List.get(10));

					writeToFile.println("P under K and Ki");
					writeToFile.println("Round\t\tAES0\tAES1\tAES2\tAES3\tAES4");
					for(int i = 0; i<aES0List.size()-1;i++)
					{
						writeToFile.println("  " + i + "\t\t\t" + aES0ListSecond.get(i) + "\t\t" + aES1ListSecond.get(i) + "\t\t" + aES2ListSecond.get(i) + "\t\t" + aES3ListSecond.get(i) + "\t\t" + aES4ListSecond.get(i));
					}
					writeToFile.println("  10\t\t" + aES0ListSecond.get(10) + "\t\t" + aES1ListSecond.get(10) + "\t\t" + aES2ListSecond.get(10) + "\t\t" + aES3ListSecond.get(10) + "\t\t" + aES4ListSecond.get(10));
					writeToFile.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if(eOrD.equals("d") || eOrD.equals("D"))
			{
				try
				{
					writeToFile = new PrintWriter("output.txt", "UTF-8");
					int[][] decrypted = AES.decrypt(input, keyShedule);
					writeToFile.println("DECRYPTION");
					writeToFile.println("Ciphertext C: \t"+binaryInCopy);
					writeToFile.println("Key K: \t\t\t"+keyInCopy);
					writeToFile.println("Plaintext P: \t"+AES.matrixToString(decrypted));
					writeToFile.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("Invalid input. Program is now exitting.");
			}

		}
	}

	
}