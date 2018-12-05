import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.lang.Character;

import java.util.HashMap;

public class Alchemy {
	public static String loadSequence(String filename) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		String res;

		File f = new File(filename);

		try {
			fis = new FileInputStream(f);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);

			String line;
			res = "";
			while((line = br.readLine()) != null) {
				res = res + line;
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

	public static String reactPolymer(String input) {
		String seq = input;
		boolean reduced;
		do {
			reduced = false;

			for(int i = 0; i < seq.length()-1; i++) {
				char a = seq.charAt(i);
				char b = seq.charAt(i+1);
				if((a != b) && (Character.toLowerCase(a) == Character.toLowerCase(b))) {
					/* Reduce */
					seq = seq.substring(0, i) + seq.substring(i+2);
					i = -1;
					reduced = true;
					continue;
				}
			}
		} while(reduced);
		return seq;
	}

	public static String reactPolymerRemoved(String input, char removePoly) {
		String seq = "";
		for(int i = 0; i < input.length(); i++) {
			if(Character.toLowerCase(input.charAt(i)) != removePoly) {
				seq = seq + input.charAt(i);
			}
		}

		return reactPolymer(seq);
	}

	public static String getUsedLettersLower(String input) {
		String res = "";
		HashMap<Character, Boolean> hmChars = new HashMap<Character, Boolean>();

		for(int i = 0; i < input.length(); i++) {
			char c = Character.toLowerCase(input.charAt(i));
			hmChars.put(c, true);
		}

		for(Character c : hmChars.keySet()) {
			res = res + c;
		}

		return res;
	}

	public static void main(String args[]) {
		if(args.length < 1) {
			System.err.println("Please supply input data filename");
			return;
		}

		String seq = loadSequence(args[0]);
		if(seq == null) {
			System.err.println("Failed to load sequence from "+args[0]);
			return;
		}

		String originalReacted = reactPolymer(seq);

		String letters = getUsedLettersLower(seq);

		System.out.println("Result of original reaction: "+originalReacted+"\n\n\n");
		System.out.println("Result length of original reaction: "+originalReacted.length());

		char minLengthLetter = 0;
		int minLength = 0;
		String minReducedReacted = null;

		System.out.print("Trying to remove:");
		for(int i = 0; i < letters.length(); i++) {
			char c = letters.charAt(i);
			System.out.print(" "+c);
			String reacted = reactPolymerRemoved(seq, c);
			if(minLength == 0) {
				minLength = reacted.length();
				minLengthLetter = c;
				minReducedReacted = reacted;
			} else if(minLength > reacted.length()) {
				minLength = reacted.length();
				minLengthLetter = c;
				minReducedReacted = reacted;
			}
		}
		System.out.println("");

		System.out.println("Minimum length after removal of "+minLengthLetter);
		System.out.println("Result of reduced reaction: "+minReducedReacted+"\n\n\n");
		System.out.println("Result length of reduced reaction: "+minReducedReacted.length());
	}
}
