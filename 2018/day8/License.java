import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

import java.lang.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Iterator;

public class License {
	private static class Node {
		private Node parent;
		private List<Node> childs;
		private List<Integer> metadata;

		private int expectedChilds;
		private int expectedData;

		public Node(Node parent) {
			this.parent = parent;
			this.childs = new ArrayList<Node>();
			this.metadata = new ArrayList<Integer>();

			this.expectedChilds = -1;
			this.expectedData = -1;
		}

		public Iterator<Node> itChilds() {
			return childs.iterator();
		}
		public int metaSum() {
			int sum = 0;
			for(Integer i : metadata) {
				sum += i;
			}

			for(Node n : childs) {
				sum += n.metaSum();
			}

			return sum;
		}

		public Node parseInt(int nextInt) {
			if(this.expectedChilds == -1) {
				this.expectedChilds = nextInt;
				return this;
			}
			if(this.expectedData == -1) {
				this.expectedData = nextInt;
				return this;
			}

			if(childs.size() < expectedChilds) {
				Node newNode = new Node(this);
				childs.add(newNode);
				newNode.parseInt(nextInt);
				return newNode;
			}

			metadata.add(nextInt);
			if(metadata.size() < expectedData) {
				return this;
			}

			return this.parent;
		}

		public int getValue() {
			if(childs.size() == 0) {
				int res = 0;
				for(Integer i : metadata) { res += i; }
				return res;
			}

			int res = 0;
			for(Integer i : metadata) {
				if(i > childs.size()) { continue; }
				if(i == 0) { continue; }

				res += childs.get(i-1).getValue();
			}
			return res;
		}

		public String toString() {
			return toString(0);
		}
		public String toString(int level) {
			String res = "";
			for(int i = 0; i < level; i++) {
				res = res + " ";
			}
			res = res + this.expectedChilds+" "+this.expectedData+"\n";
			for(Node n : childs) {
				res = res + n.toString(level+1);
			}
			return res;
		}
	}

	public static void main(String args[]) {
		if(args.length < 1) {
			System.err.println("No input file specified");
			return;
		}
		String filename = args[0];

                FileInputStream fis = null;
                Scanner sc = null;

		Node root = new Node(null);
		Node currentNode = root;

                File f = new File(filename);

                try {
                        fis = new FileInputStream(f);
                        sc = new Scanner(fis);

                        sc.useDelimiter(" |\n");

                        while(sc.hasNextInt()) {
                                int x = sc.nextInt();
				currentNode = currentNode.parseInt(x);
	                }
                } catch(IOException e) {
                        e.printStackTrace();
                } finally {
                        if(sc != null) { sc.close(); }
                        else if(fis != null) { try { fis.close(); } catch(IOException e) { e.printStackTrace(); } }
                }

		System.out.println("Data sum: "+root.metaSum());

		System.out.println("\"Value\" of root node: "+root.getValue());
	}
}
