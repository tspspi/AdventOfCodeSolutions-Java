import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.List;

public class Fabric {
	private static class FabricClaim {
		private int id;
		private int x;
		private int y;
		private int width;
		private int height;

		public FabricClaim(int id, int x, int y, int width, int height) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public int getId() { return this.id; }
		public int getX() { return this.x; }
		public int getY() { return this.y; }
		public int getXMax() { return this.x + this.width; }
		public int getYMax() { return this.y + this.height; }
		public int getWidth() { return this.width; }
		public int getHeight() { return this.height; }
	}

	private static ArrayList<FabricClaim> loadFile(String filename) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		ArrayList<FabricClaim> res = new ArrayList<FabricClaim>();

		File f = new File(filename);

		try {
			fis = new FileInputStream(f);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);

			String line;
			while((line = br.readLine()) != null) {
				/* Parse the single line with some ugly slicing and substringing */
				line = line.substring(1);
				String parts[] = line.split(" @ ");
				int id = Integer.parseInt(parts[0]);
				parts = parts[1].split(",");
				int x = Integer.parseInt(parts[0]);
				parts = parts[1].split(": ");
				int y = Integer.parseInt(parts[0]);
				parts = parts[1].split("x");
				int width = Integer.parseInt(parts[0]);
				int height = Integer.parseInt(parts[1]);

				res.add(new FabricClaim(id, x, y, width, height));
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

	public static void main(String args[]) {
		int minX, maxX;
		int minY, maxY;

		if(args.length < 1) {
			System.err.println("Please supply input file name");
			return;
		}

		ArrayList<FabricClaim> claims = loadFile(args[0]);
		if(claims == null) {
			System.err.println("Failed to load claims");
			return;
		}
		if(claims.size() == 0) {
			System.err.println("Failed to load claims");
			return;
		}

		/* Determine minimum and maximum XY for our further calculations */
		minX = claims.get(0).getX();
		maxX = claims.get(0).getX()+claims.get(0).getWidth();
		minY = claims.get(0).getY();
		maxY = claims.get(0).getY()+claims.get(0).getHeight();

		for(FabricClaim claim : claims) {
			if(claim.getX() < minX) {
				minX = claim.getX();
			}
			if(claim.getY() < minY) {
				minY = claim.getY();
			}
			if(claim.getXMax() > maxX) {
				maxX = claim.getXMax();
			}
			if(claim.getYMax() > maxY) {
				maxY = claim.getYMax();
			}
		}

		/* Create and zero a counting area */
		int counts[][] = new int[maxX][maxY];
		for(int x = 0; x < maxX; x++) {
			for(int y = 0; y < maxY; y++) {
				counts[x][y] = 0;
			}
		}

		/* Count area usage */
		for(FabricClaim claim : claims) {
			for(int x = claim.getX(); x < claim.getXMax(); x++) {
				for(int y = claim.getY(); y < claim.getYMax(); y++) {
					counts[x][y]++;
				}
			}
		}

		/* Count how many areas are overlapping */
		int overlappingCounter = 0;
		for(int x = 0; x < counts.length; x++) {
			for(int y = 0; y < counts[0].length; y++) {
				if(counts[x][y] > 1) {
					overlappingCounter++;
				}
			}
		}

		System.out.println(overlappingCounter + " square inches are overlapping");

		/* Locate single NON overlapping */
		for(FabricClaim claim : claims) {
			boolean overlaps = false;
			for(int x = claim.getX(); x < claim.getXMax(); x++) {
				for(int y = claim.getY(); y < claim.getYMax(); y++) {
					if(counts[x][y] > 1) {
						overlaps = true;
						break;
					}
				}
				if(overlaps) {
					break;
				}
			}

			if(!overlaps) {
				System.out.println("Non overlapping area: "+claim.getId());
			}
		}
	}
}
