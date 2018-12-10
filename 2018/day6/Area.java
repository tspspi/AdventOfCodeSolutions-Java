import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

import java.lang.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Area {
	private static class Coordinate {
		private int x,y;
		private boolean isInfinite;
		private int area;

		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
			this.isInfinite = false;
			this.area = 0;
		}

		public int getX() { return this.x; }
		public int getY() { return this.y; }

		public int bresDistance(Coordinate o) {
			return Math.abs(this.x - o.x) + Math.abs(this.y - o.y); 
		}
		public int bresDistance(int x, int y) {
			return Math.abs(this.x - x) + Math.abs(this.y - y);
		}

		public void setInfinite() { this.isInfinite = true; }
		public boolean isInfinite() { return this.isInfinite; }

		public void areaInc() { this.area++; }
		public int getArea() { return this.area; }

		public String toString() { return "" + x + ", " + y; }
	}
	
	private static List<Coordinate> loadFile(String filename) {
		FileInputStream fis = null;
                Scanner sc = null;

		ArrayList<Coordinate> res = new ArrayList<Coordinate>();

		File f = new File(filename);

		try {
			fis = new FileInputStream(f);
			sc = new Scanner(fis);

			sc.useDelimiter(", |\n");

			while(sc.hasNext()) {
				int x = sc.nextInt();
				int y = sc.nextInt();

				Coordinate c = new Coordinate(x,y);

				res.add(c);
			}
		} catch(IOException e) {
			e.printStackTrace();
			res = null;
		} finally {
			if(sc != null) { sc.close(); }
                        else if(fis != null) { try { fis.close(); } catch(IOException e) { e.printStackTrace(); } }
		}

		return res;
	}

	public static final void main(String args[]) {
		int minX, maxX, minY, maxY;
		int xOff = 0;
		int yOff = 0;

		if(args.length < 1) {
			System.err.println("Please specify input filename");
			return;
		}

		List<Coordinate> coords = loadFile(args[0]);
		if(coords == null) {
			System.err.println("Failed to load input data");
			return;
		}

		// Initialize min and max tracking
		minX = coords.get(0).getX();
		maxX = minX;
		minY = coords.get(0).getY();
		maxY = minY;

		for(Coordinate c : coords) {
			if(minX > c.getX()) { minX = c.getX(); }
			if(maxX < c.getX()) { maxX = c.getX(); }
			if(minY > c.getY()) { minY = c.getY(); }
			if(maxY < c.getY()) { maxY = c.getY(); }
		}

		xOff = 0 - minX + 1;
		yOff = 0 - minY + 1;

		// Create our calculation field (ranging from min-1 to max+1 to detect borders
		Coordinate nearest[][] = new Coordinate[maxX-minX+3][maxY-minY+3];

		int totalFieldCount = (maxX-minX+3)*(maxY-minY+3);
		int currentField = 0;

		System.out.print("Calculating min distance: ");
		for(int x = minX-1; x < maxX+1; x++) {
			for(int y = minY-1; y < maxY+1; y++) {
				currentField++;
				if(currentField % 100 == 0) {
					System.out.println("("+x+";"+y+") "+(((double)currentField/(double)totalFieldCount)*100.0)+"%");
				}
				int minDist = coords.get(0).bresDistance(x,y);
				Coordinate minCoord = coords.get(0);

				for(Coordinate c : coords) {
					int newDist = c.bresDistance(x,y);
					if(newDist < minDist) {
						minDist = newDist;
						minCoord = c;
					} else if(newDist == minDist) {
						minCoord = null;
					}
				}

				nearest[x+xOff][y+yOff] = minCoord;
			}
		}
		System.out.println("done");

		// Determine which ones are infinite
		for(int x = minX-1; x < maxX+1; x++) {
			if(nearest[x+xOff][0] != null) {
				nearest[x+xOff][0].setInfinite();
			}
			if(nearest[x+xOff][maxY+yOff] != null) {
				nearest[x+xOff][maxY+yOff].setInfinite();
			}
		}
		for(int y = minY - 1; y < maxY+1; y++) {
			if(nearest[0][y+yOff] != null) {
				nearest[0][y+yOff].setInfinite();
			}
			if(nearest[maxX+xOff][y+yOff] != null) {
				nearest[maxX+xOff][y+yOff].setInfinite();
			}
		}

		// Sum up area ...
		for(int x = minX-1; x < maxX+1; x++) {
                        for(int y = minY-1; y < maxY+1; y++) {
				if(nearest[x+xOff][y+yOff] != null) {
					nearest[x+xOff][y+yOff].areaInc();
				}
			}
		}


		// Search for max area ...
		int maxArea = 0;
		Coordinate maxAreaC = null;
		for(Coordinate c : coords) {
			if((maxArea < c.getArea()) && (!c.isInfinite())) {
				maxArea = c.getArea();
				maxAreaC = c;
			}
		}
		System.out.println("Maximum finite area: "+maxArea);

		int lowTotals = 0;
		for(int x = minX; x < maxX+1; x++) {
			for(int y = minY; y < maxY+1; y++) {
				int fieldSum = 0;
				for(Coordinate c : coords) {
					fieldSum += c.bresDistance(x,y);
				}
				if(fieldSum < 10000) {
					lowTotals++;
				}
			}
		}
		System.out.println("Low total distance area: "+lowTotals);
	}
}
