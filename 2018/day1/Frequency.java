import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Frequency {
	private static List<Integer> loadFile(String filename) {
		FileInputStream fis = null;
		Scanner sc = null;

		ArrayList<Integer> res = new ArrayList<Integer>();

		File f = new File(filename);

		try {
			fis = new FileInputStream(f);
			sc = new Scanner(fis);

			while(sc.hasNextInt()) {
				res.add(sc.nextInt());
			}

			sc.close();
			sc = null;
			fis = null;
		} catch(IOException e) {
			e.printStackTrace();
			res = null;
		} finally {
			if(sc != null) { sc.close(); }
			else if(fis != null) { try { fis.close(); } catch(IOException e) { e.printStackTrace(); } }
		}

		return res;
	}

	public static void main(String args[]) {
		if(args.length < 1) {
			System.err.println("Please supply data input filename");
			return;
		}

		List<Integer> measurements = loadFile(args[0]);
		if(measurements == null) {
			System.err.println("Failed to load "+args[0]);
			return;
		}

		HashMap<Integer, Boolean> hmSeenFrequency = new HashMap<Integer, Boolean>(); /* Used for part 2: Repeated frequency */
		boolean doneRepeatedSearch = false;

		int currentValue = 0;
		for(Integer currentMeasurement : measurements) {
			currentValue += currentMeasurement;
			if((hmSeenFrequency.containsKey(currentValue)) && (!doneRepeatedSearch)) {
				System.out.println("Encountered repeated value during first iteration: "+currentValue);
				doneRepeatedSearch = true;
			}
			hmSeenFrequency.put(currentValue, true);
		}
		System.out.println("Value after last measurement: "+currentValue);

		while(!doneRepeatedSearch) {
			for(Integer currentMeasurement : measurements) {
				currentValue += currentMeasurement;
				if((hmSeenFrequency.containsKey(currentValue)) && (!doneRepeatedSearch)) {
					System.out.println("Encountered repeated value during first iteration: "+currentValue);
					doneRepeatedSearch = true;
					break;
				}
				hmSeenFrequency.put(currentValue, true);
			}
		}
	}
}
