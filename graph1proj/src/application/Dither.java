package application;

//p.aszklar@mini.pw.edu.pl
//
//
//w nodzie 
//sumaR
//sumaG
//sumaB
//ilosc
//paleta sumaR/ilosc=R

import java.awt.image.BufferedImage;
import java.util.Random;

public class Dither {

	public static BufferedImage ditherImage(BufferedImage image) {
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
		return dest;
	}

	public static BufferedImage ranDitherImage(BufferedImage image, int k) {
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		int curPixel;
		int idx;
		int A, R, G, B;
		int[] srcPixels = new int[imageWidth * imageHeight];
		int[] rtPixels = new int[imageWidth * imageHeight];
		int[] greyValues = new int[k];
		Random rand = new Random();

		int rNum;
		int conFact = 255 / (k);
		for (int i = 0; i < k; i++) {
			greyValues[i] = i * conFact;
		}

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

				int grey = (R + G + B) / 3;
				for (int i = 0; i < k-1; i++) {
					if (grey > greyValues[i] && grey < greyValues[i+1]) {
						rNum = rand.nextInt(greyValues[i]+1) + conFact;
						if (grey > rNum) {
							R = greyValues[i+1];
							G = greyValues[i+1];
							B = greyValues[i+1];
						}
						else{
							R = greyValues[i];
							G = greyValues[i];
							B = greyValues[i];
						}
					} else if (grey > greyValues[k-1] && grey <= 255) {
						rNum = rand.nextInt(greyValues[k-1]) + conFact;
						if (grey > rNum) {
							R = 255;
							G = 255;
							B = 255;
						}
						else{
							R = greyValues[k-1];
							G = greyValues[k-1];
							B = greyValues[k-1];
						}
					}
				}

				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
				curPixel = srcPixels[x + y * imageWidth];

			}
		}
		dest.setRGB(0, 0, imageWidth, imageHeight, rtPixels, 0, imageWidth);
		return dest;
	}

	

}
