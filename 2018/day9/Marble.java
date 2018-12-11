import java.util.ArrayList;
import java.util.List;

/*
	Input: 416 players; last marble is worth 71975 points

	Note that this implementation will have very low performance
	when used for part two because of the O(n) delete, insert and
	get functions that are used. This could be more performant
	by keeping a reference to the node describing the current marble.
*/


public class Marble {
	public static void main(String args[]) {
		if(args.length < 2) {
			System.err.println("Require player count and maximum score");
			return;
		}

		boolean showDetails = false;
		if(args.length == 3) { showDetails = true; }

		int playerCount = 0;
		int maxMarble = 0;

		try {
			playerCount = Integer.parseInt(args[0]);
			maxMarble = Integer.parseInt(args[1]);
		} catch(NumberFormatException e) {
			System.err.println("An entered value was not a valid number");
			return;
		}

		long[] playerScores = new long[playerCount];
		List<Integer> circle = new ArrayList<Integer>();
		int currentMarble = 0;

		circle.add(0);

		for(int nextMarble = 1; nextMarble <= maxMarble; nextMarble++) {
			if(nextMarble % 10000 == 0) {
				System.out.println("Processing: "+(((float)nextMarble / (float)maxMarble)*100.0)+"%");
			}

			int currentPlayer = ((nextMarble-1) % playerCount);

			if(nextMarble % 23 == 0) {
				playerScores[currentPlayer] += (long)nextMarble;
				int removeIndex = (currentMarble - 7 + circle.size()) % circle.size();
				playerScores[currentPlayer] += (long)(circle.get(removeIndex));
				circle.remove(removeIndex);
				currentMarble = removeIndex;
			} else {
				int insertIndex = (currentMarble+1) % circle.size() + 1;
				currentMarble = insertIndex;
				circle.add(insertIndex, nextMarble);
			}

			if(showDetails) {
				System.out.print("[" + (currentPlayer+1) + "] ");
				for(int i = 0; i < circle.size(); i++) { System.out.print(" " + circle.get(i)); }
				System.out.println("");
			}
		} 

		System.out.println("\n\nScores:\n");
		long maxScore = 0;
		for(int i = 0; i < playerScores.length; i++) {
			if(playerScores[i] > maxScore) { maxScore = playerScores[i]; }
		}
		for(int i = 0; i < playerScores.length; i++) {
			if(playerScores[i] != maxScore) {
				System.out.println("Player "+i+": "+playerScores[i]);
			} else {
				System.out.println("Player "+i+": "+playerScores[i]+" (WINNER)");
			}
		}

		System.out.println("\n\nMax: "+maxScore);

		return;
	}
}
