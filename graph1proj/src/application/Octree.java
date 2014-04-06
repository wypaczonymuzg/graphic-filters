package application;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Octree {
	public void createTree(BufferedImage image){
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

		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				idx = (x) + (y) * imageWidth;
				curPixel = srcPixels[x + y * imageWidth];

				A = ((curPixel >> 24) & 0x000000FF);
				R = ((curPixel >> 16) & 0x000000FF);
				G = ((curPixel >> 8) & 0x000000FF);
				B = ((curPixel >> 0) & 0x000000FF);

				// A=function[A];
				if (R < 128) {
					R = 0;
				} else {
					R = 255;
				}
				if (B < 128) {
					B = 0;
				} else {
					B = 255;
				}
				if (G < 128) {
					G = 0;
				} else {
					G = 255;
				}

				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				curPixel = srcPixels[x + y * imageWidth];

			}
		}
		dest.setRGB(0, 0, imageWidth, imageHeight, rtPixels, 0, imageWidth);
		return ;
		
	}
	
	
	
	
	
	
	/*
	private final int currDepth; // the current depth of this node
	private final OctreeNode[] nodes; // the child nodes

	// private final ArrayList<GameObject> objects; // the objects stored at
	// this node
	OctreeNode(){
		
		
	}
	
	
	void insert(OctreeNode node, Color c, int depth)
	   {
	   if (*node == null)
	   CreateAndInit(node, depth);
	   if (isLeaf(*node)) {
	   (*node)->pixels_count++;
	   addColors(*node, c);
	   } else
	   insert(&(*node)->child[Branch(c, depth)], c, depth + 1);
	   }
	   */
}
