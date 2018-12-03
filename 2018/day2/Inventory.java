import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class Inventory {
	private static ArrayList<String> loadFile(String filename) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		ArrayList<String> res = new ArrayList<String>();

		File f = new File(filename);

		try {
			fis = new FileInputStream(f);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);

			String line;
			while((line = br.readLine()) != null) {
				res.add(line);
			}

			br.close();
		} catch(IOException e) {
			e.printStackTrace();
			res = null;
		} finally {
			if(br != null) { try { br.close(); } catch(IOException e) { e.printStackTrace(); } }
			else if(isr != null) { try { isr.close(); } catch(IOException e) { e.printStackTrace(); } }
			else if(fis != null) { try { fis.close(); } catch(IOException e) { e.printStackTrace(); } }
		}

		return res;
	}

	private static HashMap<Character, Integer> getCharacterCounts(String str) {
		HashMap<Character, Integer> symbolCounter = new HashMap<Character, Integer>();

		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			if(symbolCounter.containsKey(c)) {
				symbolCounter.put(c, symbolCounter.get(c)+1);
			} else {
				symbolCounter.put(c, 1);
			}
		}

		return symbolCounter;
	}

	private static boolean hasRepeated2(HashMap<Character, Integer> hmData) {
		for(HashMap.Entry<Character, Integer> entry : hmData.entrySet()) {
			if(entry.getValue() == 2) {
				return true;
			}
		}
		return false;
	}
	private static boolean hasRepeated3(HashMap<Character, Integer> hmData) {
		for(HashMap.Entry<Character, Integer> entry : hmData.entrySet()) {
			if(entry.getValue() == 3) {
				return true;
			}
		}
		return false;
	}

	private static int editDistance(String a, String b) {
		int distance = 0;
		int length;
		if(a.length() > b.length()) {
			distance = a.length() - b.length();
			length = b.length();
		} else {
			distance = b.length() - a.length();
			length = a.length();
		}

		for(int i = 0; i < length; i++) {
			if(a.charAt(i) != b.charAt(i)) {
				distance++;
			}
		}

		return distance;
	}

	private static String getCommonChars(String a, String b) {
		String res = "";
		int length = (a.length() > b.length()) ? b.length() : a.length();

		for(int i = 0; i < length; i++) {
			if(a.charAt(i) == b.charAt(i)) {
				res = res + a.charAt(i);
			}
		}

		return res;
	}

	public static void main(String args[]) {
		if(args.length < 1) {
			System.err.println("Please supply input file name");
			return;
		}

		ArrayList<String> data = loadFile(args[0]);

		int countDouble = 0;
		int countTripplet = 0;

		for(String line : data) {
			HashMap<Character, Integer> letterCounts = getCharacterCounts(line);
			if(hasRepeated2(letterCounts)) { countDouble++; }
			if(hasRepeated3(letterCounts)) { countTripplet++; }
		}

		System.out.println("IDs with doublets: "+countDouble);
		System.out.println("IDs with tripplets: "+countTripplet);
		System.out.println("\"Checksum\" " + (countDouble * countTripplet));

		/*
			Part 2
		*/
		for(int i = 0; i < data.size(); i++) {
			for(int j = 0; j < i; j++) {
				if(editDistance(data.get(i), data.get(j)) == 1) {
					System.out.println("IDs with edit distance 1: "+data.get(i)+", "+data.get(j));
					System.out.println("Common characters: "+getCommonChars(data.get(i), data.get(j)));
				}
			}
		}
	}
}
