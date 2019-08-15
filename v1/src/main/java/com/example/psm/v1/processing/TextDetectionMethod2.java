package com.example.psm.v1.processing;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.Point;


/**
 * TextDetectionMethod2
 * 
 * even though not using singleton class type,
 * the spring framework still use a same object
 * even did declare new object for same class.
 * so that, all list need to add "clear()".
 *
 *
 */
public class TextDetectionMethod2 {

	// private static TextDetectionMethod2 instance = null;
	static Map<String, Double> result = new HashMap<>();
	static List<String> result2 = new ArrayList<>();

	// public static TextDetectionMethod2 getInstance() {
	// 	if (instance == null) {
	// 		instance = new TextDetectionMethod2();
	// 	}
	// 	return instance;
	// }


	public String imgProc(String imgPath, Boolean isTrain) {

		Mat frame = Imgcodecs.imread(imgPath);

		// kiv
		// Mat frame =
		// Imgcodecs.imread("C:\\images\\cropped\\photo_2019-07-02_20-13-57copy.jpg");
		// displayImage(toBufferedImage(frame));
		Mat copyImgFromOrigForFinal = new Mat();

		Mat copyImgFromOrig = new Mat();
		Imgproc.cvtColor(frame, copyImgFromOrig, Imgproc.COLOR_BGR2GRAY);
		copyImgFromOrig.copyTo(copyImgFromOrigForFinal);

		System.out.println("image shape size: " + copyImgFromOrig.size() + " image type: " + copyImgFromOrig.channels());

		// to preserve edge when blurring
		Imgproc.bilateralFilter(copyImgFromOrigForFinal, copyImgFromOrig, 5, 35, 35);

		// Canny edge detection or using threshold before suing findcontour
		Imgproc.adaptiveThreshold(copyImgFromOrig, copyImgFromOrig, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY, 115, 4);

		// to filter clear small details
		Imgproc.medianBlur(copyImgFromOrig, copyImgFromOrig, 5);

		// find contour
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(copyImgFromOrig, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);


		String axis = null;
		if (copyImgFromOrig.height() > copyImgFromOrig.width()) {
			axis = "horizontal";
		} else if (copyImgFromOrig.width() > copyImgFromOrig.height()) {
			axis = "vertical";
		} else {
			System.out.println("image can't be specify axis");
			System.exit(0);
		}

		

		// contour filter and sort
		List<MatOfPoint> contoursFilter = new ArrayList<>();
		for (int i = 0; i < contours.size(); i++) {
			Rect bound = Imgproc.boundingRect(contours.get(i));
			if (axis == "horizontal") {
				contoursFilter.add(contours.get(i));
			} else if (axis == "vertical") {
				if( bound.width > bound.height && bound.width < 140 && bound.width> 115 && bound.height< 58 && bound.height > 13)
				contoursFilter.add(contours.get(i));
			}

		}

		Mat copyImgFromOrigForContour = new Mat();
		frame.copyTo(copyImgFromOrigForContour);

		MatOfPoint2f approxCurve = new MatOfPoint2f();
		List<Rect> contourFilterRect = new ArrayList<>();

		for (int j = 0; j < contoursFilter.size(); j++) {

			// Convert contours(i) from MatOfPoint to MatOfPoint2f
			MatOfPoint2f contour2f = new MatOfPoint2f(contoursFilter.get(j).toArray());
			// Processing on mMOP2f1 which is in type MatOfPoint2f
			double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
			Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

			// Convert back to MatOfPoint
			MatOfPoint points = new MatOfPoint(approxCurve.toArray());

			// Get bounding rect of contour
			Rect rect = Imgproc.boundingRect(points);
			contourFilterRect.add(rect);

		}

		// draw digit into new blank background image
		// note: only black or white image mean 1 channel
		// this step won't be used for final image
		Mat blankBackground = new Mat(copyImgFromOrigForContour.size(), CvType.CV_8UC1, new Scalar(0));
		System.out.println("size of: " + blankBackground.width() + " " + blankBackground.height());
		for (int i = 0; i < contoursFilter.size(); i++) {
			Imgproc.drawContours(blankBackground, contoursFilter, i, new Scalar(255));
		}

		String re = findDigitContourByMatchingShape(contoursFilter, axis);

		/*
		 * below is training part only enable when intent to save new image as trainning
		 * data also to test training image contour
		 */

		if (isTrain) {
			saveTrainningData(blankBackground, contourFilterRect, contoursFilter);
		}
		// toTestTrainnedDataContour("2\\1561901594137");

		return re;

	}

	public static String findDigitContourByMatchingShape(List<MatOfPoint> contours, String axis) {
		result.clear();
		result2.clear();
		
		// to remove duplicate location
		Set<MatOfPoint> primesWithoutDuplicates = new LinkedHashSet<MatOfPoint>(contours);
		contours.clear();
		contours.addAll(primesWithoutDuplicates);

		File trainnedDataFolder = new File("v1\\src\\main\\resources\\static\\TrainnedData");
		File[] listOfFileDigit = trainnedDataFolder.listFiles();

		// reverse the file to match shape from 9 to 0
		for (int i = 0; i < listOfFileDigit.length / 2; i++) {
			File temp = listOfFileDigit[i];
			listOfFileDigit[i] = listOfFileDigit[listOfFileDigit.length - i - 1];
			listOfFileDigit[listOfFileDigit.length - i - 1] = temp;
		}

		List<MatOfPoint> contoursFilter = new ArrayList<>();
		contoursFilter.addAll(contours);

		if (axis == "horizontal") {
			// sort by x coordinates
			// utilize later
			Collections.sort(contoursFilter, new Comparator<MatOfPoint>() {
				@Override
				public int compare(MatOfPoint o1, MatOfPoint o2) {
					Moments m = Imgproc.moments(o1);
					double xa = m.m10 / m.m00;

					Moments m2 = Imgproc.moments(o2);
					double xb = m2.m10 / m2.m00;

					int result = 0;
					result = Double.compare(xa, xb);
					return result;
				}
			});

		} else if (axis == "vertical") {
			// sort by x coordinates
			// utilize later
			Collections.sort(contoursFilter, new Comparator<MatOfPoint>() {
				@Override
				public int compare(MatOfPoint o1, MatOfPoint o2) {
					Moments m = Imgproc.moments(o1);
					double ya = m.m01 / m.m00;

					Moments m2 = Imgproc.moments(o2);
					double yb = m2.m01 / m2.m00;

					int result = 0;
					result = Double.compare(yb, ya);
					return result;
				}
			});

		}

		/*
		 * recoginiton by comparing with raw data
		 */
		System.out.println(
				">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>first result using trainned data: <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

		for (MatOfPoint contour1 : contoursFilter) {
			Mat mat = new Mat(new Size(1280,960),  CvType.CV_8UC3, new Scalar(0));
			Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
			List<MatOfPoint> co = new ArrayList<>();
			co.add(contour1);
			Imgproc.drawContours(mat, co, 0, new Scalar(255), 1);
			co.clear();
			Imgproc.findContours(mat, co, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
			Rect ro = Imgproc.boundingRect(co.get(0));
			double ratioOfDigit = ro.width/ro.height;
			Mat re = new Mat();
			if (axis == "horizontal") {
				Imgproc.resize(mat.submat(ro), re, new Size(30, 90));
			}else
				Imgproc.resize(mat.submat(ro), re, new Size(90, 30));
			

			
			co.clear();
			Imgproc.findContours(re, co, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

			//to differetite between digit 0 and eight
			float depth = findConvexDefect(re);

			double acc = 1;
			String lastBestMatch = "0";
			String seclastBestMatch = "0";

			for (File file : listOfFileDigit) {
				File[] listOfFile = file.listFiles();
				String currentDigitMatching = file.getName();

				try {

					for (File data : listOfFile) {
						Mat cont = retrieveTrainnedDataContour(currentDigitMatching + "\\" + FilenameUtils.removeExtension(data.getName()), axis);

						double val;
						val = Imgproc.matchShapes(re, cont, 1, 0.0);

						if (val < acc) {
							acc = val;
							lastBestMatch = currentDigitMatching;
							System.out.println("matching score for: "+ currentDigitMatching +" : "+val);
						}
					}

					if (currentDigitMatching.equals("0")) {
						if (ratioOfDigit > 5 && lastBestMatch.equals("0")) {
							if (seclastBestMatch.equals("8") && depth < 1000) {
								seclastBestMatch = "0";
								result.put(seclastBestMatch, acc);
								result2.add(seclastBestMatch);
							}else if (seclastBestMatch.equals("0") && depth > 1000) {
								seclastBestMatch = "8";
								result.put(seclastBestMatch, acc);
								result2.add(seclastBestMatch);
							}else{
								result.put(seclastBestMatch, acc);
								result2.add(seclastBestMatch);
							}
						}else {
							if (lastBestMatch.equals("8") && depth < 1000) {
								lastBestMatch = "0";
								result.put(lastBestMatch, acc);
								result2.add(lastBestMatch);
							}else if (lastBestMatch.equals("0") && depth > 1000) {
								lastBestMatch = "8";
								result.put(lastBestMatch, acc);
								result2.add(lastBestMatch);
							}else{
								result.put(lastBestMatch, acc);
								result2.add(lastBestMatch);
							}
						}

					}

				} catch (NullPointerException e) {
					System.out.println("check folder TrainnedData, should be properly put into specify folder");
				}
			}
		}
		String finalResult = "";
		System.out.println("found contours: " + contours.size());
		System.out.println("filtered contours: " + contoursFilter.size());
		if (result2.size() < 4) {
			for (Map.Entry<String, Double> entry : result.entrySet()) {
			    System.out.println(entry.getKey() + " accuracy: " + entry.getValue());
			}
			finalResult = "result are less than 4! check server";
		} else {
			for (int j = 0; j < 4; j++) {
				System.out.print(result2.get(j));
				finalResult += result2.get(j);
			}
		}

		System.out.println("");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> End of process <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		return finalResult;

	}

	public static Mat retrieveTrainnedDataContour(String imageName, String axis) {
		Mat test = Imgcodecs.imread("v1\\src\\main\\resources\\static\\TrainnedData\\" + imageName + ".png");
		Imgproc.cvtColor(test, test, Imgproc.COLOR_BGR2GRAY);
		if (axis == "horizontal") {
			Imgproc.resize(test, test, new Size(30,90));
		}else
			Imgproc.resize(test, test, new Size(90,30));

		return test;
	}

	public static void saveTrainningData(Mat contourFiltered, List<Rect> roi, List<MatOfPoint> contoursFilter) {

		/*
		 * data should not submat from main image it should redraw into black background
		 * so that unwanted contour near the countour need wont disturb on retrieval
		 * later
		 */
		List<BufferedImage> temp = new ArrayList<>();

		for (int j = 0; j < contoursFilter.size(); j++) {
			Mat mat = new Mat(new Size(1280, 960), CvType.CV_8UC1, new Scalar(0));
			Imgproc.drawContours(mat, contoursFilter, j, new Scalar(255), 1);
			List<MatOfPoint> co = new ArrayList<>();
			Imgproc.findContours(mat, co, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
			Rect ro = Imgproc.boundingRect(co.get(0));
			Mat re = new Mat();
			Imgproc.resize(mat.submat(ro), re, new Size(60, 60));
			temp.add(toBufferedImage(re));
		}

		for (BufferedImage bufferedImage : temp) {
			try {
				// save image
				BufferedImage bi = bufferedImage;
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				File outputfile = new File(
						"v1\\src\\main\\resources\\static\\TrainnedData\\" + timestamp.getTime() + ".png");
				ImageIO.write(bi, "png", outputfile);
			} catch (IOException e) {
				System.out.println("save image: " + e);
			}
		}

	}

	public static float findConvexDefect(Mat srcImg) {
		/*
		    * 
		    * this method is to differentite digit 0 and eight
		    * it's seek the largest area of defect,
		    * if below 1000 then the number is 0,else is 8 
		    * 
		    * */
		
		Mat src = new Mat();
		srcImg.copyTo(src);
		System.out.println("Image type: "+src.type()+" image channel: "+src.channels());
		List<MatOfPoint> cSrc = new ArrayList<>();
		Imgproc.findContours(src, cSrc, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		Imgproc.cvtColor(src, src, Imgproc.COLOR_GRAY2BGR, 3);
		float largestDepth = 0;
		for (int i = 0; i < cSrc.size(); i++) {
			MatOfInt iHull = new MatOfInt();
			Imgproc.convexHull(cSrc.get(i),iHull);
			MatOfPoint hullContour = hull2Points(iHull, cSrc.get(i));
			

			MatOfInt4 iHullDefect = new MatOfInt4();
			Imgproc.convexityDefects(cSrc.get(i), iHull, iHullDefect);
			largestDepth = 0;
		   if(!iHullDefect.empty())
	        {
	            List<Integer> cdList = iHullDefect.toList();
	            System.out.println("defect array size; "+cdList.size());
	            Point data[] = cSrc.get(i).toArray();
	            for (int j = 0; j < cdList.size(); j = j+4) {
	                Point start = data[cdList.get(j)];
	                Point end = data[cdList.get(j+1)];
	                Point defect = data[cdList.get(j+2)];
	                float depth = cdList.get(j+3);
	                
	                if (depth > largestDepth) {
						largestDepth = depth;
					}

	                //Imgproc.circle(src, start, 5, new Scalar(0,0,255), 1);
	                //Imgproc.circle(src, end, 5, new Scalar(0,0,255), 1);
	                Imgproc.circle(src, defect, 1, new Scalar(0,255,0), -1);
	            }
	        }
			
		   
		   	//System.out.println("largest depth: "+largestDepth);
			//List<MatOfPoint> c = new ArrayList<>();
			//c.add(hullContour);
			//Imgproc.drawContours(src, c, 0, new Scalar(255));
			//displayImage(resize(toBufferedImage(src), 500, 500));
			
		}
		return largestDepth;
	}

	static MatOfPoint hull2Points(MatOfInt hull, MatOfPoint contour) {
	    List<Integer> indexes = hull.toList();
	    List<Point> points = new ArrayList<>();
	    MatOfPoint point= new MatOfPoint();
	    for(Integer index:indexes) {
	        points.add(contour.toList().get(index));
	    }
	    point.fromList(points);
	    return point;
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static BufferedImage toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;

	}

}
