package application;

import java.awt.image.BufferedImage;

public class ImageFilters {

	public ImageFilters() {

	}

	public static BufferedImage processImageFun(BufferedImage image,
			int[] function) {
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
				R = function[R];
				G = function[G];
				B = function[B];

				rtPixels[idx] = ((A << 24) | (R << 16) | (G << 8) | B);
			}
		}
		dest.setRGB(0, 0, imageWidth, imageHeight, rtPixels, 0, imageWidth);
		return dest;
	}

	//git remote add origin https://github.com/wypaczonymuzg/graphic-filters.git
	//git push -u origin master
	
	public static BufferedImage processImageFunGamma(BufferedImage image,double gamma) {
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		int curPixel;
		int idx;
		double A, R, G, B;
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
				R =  (Math.pow((R/255), gamma)*255);
				G =  (Math.pow((G/255), gamma)*255);
				B =  (Math.pow((B/255), gamma)*255);

				rtPixels[idx] = (((int)A << 24) | ((int)R << 16) | ((int)G << 8) |(int) B);
			}
		}
		dest.setRGB(0, 0, imageWidth, imageHeight, rtPixels, 0, imageWidth);
		return dest;
	}

	public static BufferedImage processImageConv(BufferedImage image,
			float[][] kernel, int factor, int offset) {
		int kernelSize = 3;
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		int[] srcPixels = new int[imageWidth * imageHeight];
		int[] rtPixels = new int[imageWidth * imageHeight];
		image.getRGB(0, 0, imageWidth, imageHeight, srcPixels, 0, imageWidth);

		int sumA = 0;
		int sumR = 0;
		int sumG = 0;
		int sumB = 0;
		int  R, G, B;
		int idx;
		int curPixel;

		BufferedImage dest = new BufferedImage(imageWidth, imageHeight,
				image.getType());

		for (int x = kernelSize / 2, w = imageWidth - kernelSize / 2 - 1; x < w; ++x) {
			for (int y = kernelSize / 2, h = imageHeight - kernelSize / 2 - 1; y < h; ++y) {

				sumA = 0;
				sumR = 0;
				sumG = 0;
				sumB = 0;

				idx = (x) + (y) * imageWidth;

				for (int mx = -kernelSize / 2; mx < kernelSize / 2 + 1; ++mx) {
					for (int my = -kernelSize / 2; my < kernelSize / 2 + 1; ++my) {

						curPixel = srcPixels[(x + mx) + (y + my) * imageWidth];

						sumA += ((curPixel >> 24) & 0x000000FF)
								* kernel[mx + kernelSize / 2][my + kernelSize
										/ 2];
						sumR += ((curPixel >> 16) & 0x000000FF)
								* kernel[mx + kernelSize / 2][my + kernelSize
										/ 2];
						sumG += ((curPixel >> 8) & 0x000000FF)
								* kernel[mx + kernelSize / 2][my + kernelSize
										/ 2];
						sumB += ((curPixel >> 0) & 0x000000FF)
								* kernel[mx + kernelSize / 2][my + kernelSize
										/ 2];

					}
				}
				//A = ((int) (sumA / factor + offset));
				R = ((int) (sumR / factor + offset));
				G = ((int) (sumG / factor + offset));
				B = ((int) (sumB / factor + offset));
/*
				if (A < 0) {
					A = 255;
				} else if (A > 255) {
					A = 255;
				}
*/
				if (R < 0) {
					R = 0;
				} else if (R > 255) {
					R = 255;
				}

				if (G < 0) {
					G = 0;
				} else if (G > 255) {
					G = 255;
				}

				if (B < 0) {
					B = 0;
				} else if (B > 255) {
					B = 255;
				}

				rtPixels[idx] = ((255 << 24) | (R << 16) | (G << 8) | B);

			}

		}

		dest.setRGB(0, 0, imageWidth, imageHeight, rtPixels, 0, imageWidth);
		return dest;

	}

}
