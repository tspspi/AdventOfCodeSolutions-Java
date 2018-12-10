import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.io.PrintWriter;
import java.io.FileNotFoundException;

import java.lang.Character;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class StarMessage {
	private static class Point {
		private int x;
		private int y;
		private int vx;
		private int vy;

		public Point(int x, int y, int vx, int vy) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
		}

		public int getX() { return this.x; }
		public int getY() { return this.y; }

		public void timeStep() {
			this.x += vx;
			this.y += vy;
		}
	}

	private static List<Point> loadData(String filename) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		List<Point> res = new ArrayList<Point>();

		File f = new File(filename);
		try {
			fis = new FileInputStream(f);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);

			String line;

			while((line = br.readLine()) != null) {
				int x = Integer.parseInt(line.substring(10, 16).trim());
				int y = Integer.parseInt(line.substring(18, 24).trim());
				int vx = Integer.parseInt(line.substring(36, 38).trim());
				int vy = Integer.parseInt(line.substring(39, 42).trim());

				res.add(new Point(x,y,vx,vy));
			}

			br.close();
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if(br != null) { try { br.close(); } catch(IOException e) { e.printStackTrace(); } }
			else if(isr != null) { try { isr.close(); } catch(IOException e) { e.printStackTrace(); } }
			else if(fis != null) { try { fis.close(); } catch(IOException e) { e.printStackTrace(); } }
		}

		return res;
	}

	private static void writeAsciiImage(List<Point> points, String filename) {
		File f = new File(filename);
		PrintWriter pw = null;

		int maxX = points.get(0).getX();
		int maxY = points.get(0).getY();
		for(Point p : points) {
			if(maxX < p.getX()) { maxX = p.getX(); }
			if(maxY < p.getY()) { maxY = p.getY(); }

			if(p.getX() < 0) { throw new RuntimeException(); }
			if(p.getY() < 0) { throw new RuntimeException(); }
		}
		maxX++;
		maxY++;

		char[][] imageData = new char[maxX][maxY];
		for(int x = 0; x < maxX; x++) {
			for(int y = 0; y < maxY; y++) {
				imageData[x][y] = ' ';
			}
		}
		for(Point p : points) {
			imageData[p.getX()][p.getY()] = '#';
		}

		try {
			pw = new PrintWriter(f);

			for(int y = 0; y < maxY; y++) {
				for(int x = 0; x < maxX; x++) {
					pw.print(imageData[x][y]);
				}
				pw.println();
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(SecurityException e) {
			e.printStackTrace();
		} finally {
			if(pw != null) { pw.close(); pw = null; }
		}
	}

	private static boolean allPos(List<Point> points) {
		for(Point p : points) {
			if(p.getX() < 0) { return false; }
			if(p.getY() < 0) { return false; }
		}
		return true;
	}

	public static void main(String args[]) {
		if(args.length < 1) {
			System.err.println("No input data specified");
			return;
		}

		List<Point> points = loadData(args[0]);
		if(points == null) {
			System.err.println("Failed to load input data");
			return;
		}

		System.out.println("Loaded "+points.size()+" points");

		/*
			Now determine first time all stars appear with positive
			coordinates
		*/

		int dt = 0;
		for(;;) {
			if(!allPos(points)) {
				dt++;
				for(Point p : points) { p.timeStep(); }
				continue;
			}

			break;
		}

		/*
			Now we have a few "frames" for which they are all positive

			We will create ASCII art from them
		*/
		while(allPos(points)) {
			String fn = "out" + dt + ".txt";
			writeAsciiImage(points, fn);
			dt++;
			for(Point p : points) { p.timeStep(); }
		}
	}
}
