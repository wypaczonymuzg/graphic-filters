package application;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Octree {

	public static BufferedImage createTree(BufferedImage image, int maxLeaves) {

		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		int curPixel;
		int idx;
		int A, R, G, B;
		int[] srcPixels = new int[imageWidth * imageHeight];
		int[] rtPixels = new int[imageWidth * imageHeight];

		image.getRGB(0, 0, imageWidth, imageHeight, srcPixels, 0, imageWidth);

		BufferedImage dest = new BufferedImage(imageWidth, imageHeight,
				image.getType());
		rootNode root = new rootNode(maxLeaves);

		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				idx = (x) + (y) * imageWidth;
				curPixel = srcPixels[x + y * imageWidth];

				A = ((curPixel >> 24) & 0x000000FF);
				R = ((curPixel >> 16) & 0x000000FF);
				G = ((curPixel >> 8) & 0x000000FF);
				B = ((curPixel >> 0) & 0x000000FF);

				int k = index(R, G, B, 0);
				octreeNode node = root.addChild(k, R, G, B, 1);

				for (int i = 1; i < 8; i++) {
					k = index(R, G, B, i);
					node = node.addChild(k, R, G, B, i + 1);

				}
				root.merge();

			}
		}
		/*
		 * for (int x = 0; x < imageWidth; x++) { for (int y = 0; y <
		 * imageHeight; y++) { idx = (x) + (y) * imageWidth;
		 * 
		 * rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B); curPixel =
		 * srcPixels[x + y * imageWidth]; } }
		 */
		root.createPallete();

		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				idx = (x) + (y) * imageWidth;
				curPixel = srcPixels[x + y * imageWidth];

				int nearest = -1;
				int bestDistanceFoundYet = Integer.MAX_VALUE;
				// We iterate on the array...
				for (int i = 0; i < root.lista.size(); i++) {
					// if we found the desired number, we return it.
					if (root.lista.get(i) == curPixel) {
						rtPixels[idx] = root.lista.get(i);
					} else {
						// else, we consider the difference between the desired
						// number and the current number in the array.
						int d = Math.abs(curPixel - root.lista.get(i));
						if (d < bestDistanceFoundYet) {
							// For the moment, this value is the nearest to the
							// desired number...
							nearest = root.lista.get(i);
						}
					}
				}

				rtPixels[idx] = nearest;
				curPixel = srcPixels[x + y * imageWidth];
			}
		}

		dest.setRGB(0, 0, imageWidth, imageHeight, rtPixels, 0, imageWidth);
		return dest;

	}

	private static int bit(int c, int depth) {
		return ((c >> (7 - depth)) & 0x1);

	}

	private static int index(int R, int G, int B, int depth) {
		return (bit(R, depth) << 2 | bit(G, depth) << 1 | bit(B, depth) & 1);
	}

	private static class rootNode {
		int leaves;
		int maxLeaves;
		octreeNode[] childs;
		List<Integer> lista = new ArrayList<>();

		rootNode(int maxLeaves) {
			this.maxLeaves = maxLeaves;
			leaves = 0;
			childs = new octreeNode[8];
		}

		public void incLeaves() {
			leaves++;

		}

		public void createPallete() {
			for (int i = 0; i < 8; i++) {
				if (childs[i] != null)
					if (childs[i].leaf == true) {
						int A = 0;
						int R = childs[i].sumR / childs[i].count;
						int G = childs[i].sumG / childs[i].count;
						int B = childs[i].sumB / childs[i].count;
						int pixel = ((A << 24) | (R << 16) | (G << 8) | B);
						lista.add(pixel);
					}
				if (childs[i] != null) {
					childs[i].createPallete();
				}
			}

			return;
		}

		public void merge() {
			while (leaves > maxLeaves) {
				int min = 0;
				int k = 9;
				for (int i = 0; i < 8; i++) {

					if (childs[i] != null) {
						if (min == 0) {
							min = childs[i].getCount();
							k = i;
						}
						if (min < childs[i].getCount()) {
							min = childs[i].getCount();
							k = i;
						}

					}

				}
				if (k != 9)
					childs[k].merge();

			}
		}

		public void decLeaves() {
			leaves--;
		}

		public void merge(octreeNode node) {
			int min = 0;
			int k = 9;
			if (node.leaf == true) {
				if (node.parent != null) {
					node.parent.addR(node.sumR);
					node.parent.addG(node.sumG);
					node.parent.addB(node.sumB);
					node.parent.addCount(node.count);
					node.parent.remove(node);

				}
				return;
			}
			for (int i = 0; i < 8; i++) {

				if (node.childs[i] != null) {
					if (min == 0) {
						min = node.childs[i].getCount();
						k = i;
					}
					if (min < node.childs[i].getCount()) {
						min = node.childs[i].getCount();
						k = i;
					}

				}

			}
			if (k != 9)
				merge(node.childs[k]);
		}

		public octreeNode addChild(int index, int R, int G, int B, int depth) {
			if (childs[index] != null) {
				childs[index].incCount();

				return childs[index];
			} else {
				childs[index] = new octreeNode(depth, R, G, B, null, this);
				incLeaves();
				return childs[index];
			}
		}
	}

	private static class octreeNode {
		boolean leaf;
		int depth;
		int sumR;
		int sumG;
		int sumB;
		int count;
		octreeNode[] childs;
		octreeNode parent;
		rootNode root;

		octreeNode(int d, int R, int G, int B, octreeNode parent, rootNode root) {
			depth = d;
			sumR = R;
			sumG = G;
			sumB = B;
			count = 1;
			this.root = root;
			leaf = true;
			this.parent = parent;
			childs = new octreeNode[8];
		}

		public void createPallete() {
			for (int i = 0; i < 8; i++) {
				if (childs[i] != null)
					if (childs[i].leaf == true) {
						int A = 255;
						int R = childs[i].sumR / childs[i].count;
						int G = childs[i].sumG / childs[i].count;
						int B = childs[i].sumB / childs[i].count;
						int pixel = ((A << 24) | (R << 16) | (G << 8) | B);
						root.lista.add(pixel);
					}
				if (childs[i] != null) {
					childs[i].createPallete();
				}

			}
			return;
		}

		public void merge() {
			while (root.leaves > root.maxLeaves) {
				int min = 0;
				int k = 9;
				for (int i = 0; i < 8; i++) {

					if (childs[i] != null) {
						if (min == 0) {
							min = childs[i].getCount();
							k = i;
						}
						if (min < childs[i].getCount()) {
							min = childs[i].getCount();
							k = i;
						}

					}

				}
				if (k != 9)
					merge(childs[k]);
			}
		}

		public void merge(octreeNode node) {
			int min = 0;
			int k = 9;
			if (node.leaf == true) {
				if (node.parent != null) {
					node.parent.addR(node.sumR);
					node.parent.addG(node.sumG);
					node.parent.addB(node.sumB);
					node.parent.addCount(node.count);
					node.parent.remove(node);

				}
				return;
			}
			for (int i = 0; i < 8; i++) {

				if (node.childs[i] != null) {
					if (min == 0) {
						min = node.childs[i].getCount();
						k = i;
					}
					if (min < node.childs[i].getCount()) {
						min = node.childs[i].getCount();
						k = i;
					}

				}

			}
			if (k != 9)
				merge(node.childs[k]);
		}

		public void remove(octreeNode node) {
			for (int i = 0; i < 8; i++) {
				if (childs[i] == node) {
					childs[i] = null;
					for (int j = 0; j < 8; j++) {
						if (childs[i] != null) {
							return;
						}
					}
					this.leaf = true;
					root.decLeaves();
					return;
				}
			}

		}

		public void addR(int R) {
			sumR = sumR + R;
		}

		public void addG(int G) {
			sumG = sumG + G;
		}

		public void addB(int B) {
			sumB = sumB + B;
		}

		public void incCount() {
			count++;
		}

		public int getCount() {
			return count;
		}

		public void addCount(int nC) {
			count = count + nC;
		}

		public octreeNode addChild(int index, int R, int G, int B, int depth) {
			if (depth == 8) {
				addR(R);
				addG(G);
				addB(B);
				incCount();
				return this;
			}
			if (childs[index] != null) {
				childs[index].incCount();
				return childs[index];
			} else {
				childs[index] = new octreeNode(depth, R, G, B, this, root);
				if (leaf == true) {
					root.decLeaves();
				}
				leaf = false;
				root.incLeaves();

				return childs[index];

			}
		}
	}

}
