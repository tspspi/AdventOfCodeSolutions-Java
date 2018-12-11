/*
	Fuel 9798
*/

public class Fuel {
	static int getPowerLevel(int x, int y, int serial) {
		int rackId = x + 10;
		int powerLevel = (y * rackId + serial) * rackId;

		powerLevel = (powerLevel / 100) % 10;
		return powerLevel - 5;
	}

	static int getSum(int[][] grid, int x, int y, int size) {
		int sum = 0;
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				sum = sum + grid[x+i][y+j];
			}
		}
		return sum;
	}
	static int getSum(int[][] grid, int x, int y) {
		return getSum(grid,x,y,3);
	}

	public static void main(String args[]) {
		if(args.length < 1) {
			System.err.println("Missing serial number as argument");
			return;
		}

		int serial = Integer.parseInt(args[0]);

		int grid[][] = new int[300][300];

		/* Generate table */
		for(int x = 0; x < 300; x++) {
			for(int y = 0; y < 300; y++) {
				grid[x][y] = getPowerLevel(x,y,serial);
			}
		}

		/* Locate maximum 3x3 square */
		int maxX = 0;
		int maxY = 0;
		long maxVal = 0;
		for(int x = 0; x < 300-2; x++) {
			for(int y = 0; y < 300-2; y++) {
				int sum = getSum(grid, x, y);
				if(sum > maxVal) {
					maxVal = sum;
					maxX = x;
					maxY = y;
				}
			}
		}

		System.out.println("Maximum 3x3 area "+maxVal+" at "+maxX+";"+maxY);

		/* Note: An summed area table would be better for part 2 ... */

		int maxSize = 1;
		maxVal = 0;
		for(int size = 1; size <= 300; size++) {
			for(int x = 0; x <= 300-size; x++) {
				for(int y = 0; y <= 300-size; y++) {
					int sum = getSum(grid,x,y,size);
					if(sum > maxVal) {
						maxVal = sum;
						maxX = x;
						maxY = y;
						maxSize = size;
					}
				}
			}
		}

		System.out.println("\nMaximum "+maxVal+" at area: "+maxX+","+maxY+","+maxSize);

	}
}
