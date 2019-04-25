package com.example.psm.v1.processing;

import org.opencv.core.Mat;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.dnn.*;
import org.opencv.dnn.Dnn;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class TextDetection {
    private static TextDetection instance = null;
    final String dataSetPath = "v1\\src\\main\\resources\\static\\dataset\\frozen_east_text_detection.pb";
    final String demoImg = "v1\\src\\main\\resources\\static\\images\\syamil.jpg";
    final String tessData = "v1\\src\\main\\resources\\static\\tessdata";
    public static TextDetection getInstance() {
        if (instance == null) {
            instance = new TextDetection();
        }
        return instance;
    }

    public TextDetection(){
        System.out.println("Text Detection is running");
        
        

        try {
            File f = ResourceUtils.getFile(tessData);

        if(f.exists()){
            System.out.println("File existed");
        }else{
            System.out.println("File not found!");
        }
            
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
        }
        
    }
    public String textRecognition(Mat[] digits) {
    	String result = null;
        
        
        ITesseract instance = new net.sourceforge.tess4j.Tesseract(); // JNA Interface Mapping
//       ITesseract instance = new Tesseract1(); // JNA Direct Mapping
        instance.setDatapath(tessData); // path to tessdata directory
        instance.setLanguage("ocr");
        instance.setTessVariable("tessedit_char_whitelist", "0123456789");
        instance.setTessVariable("tessedit_char_blacklist", "!?@#$%&*()<>_-+=/:;'\\\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        instance.setTessVariable("classify_bln_numeric_mode", "1");
        instance.setPageSegMode(10);
        

        for (int i = 0; i < 5; i++) {
        	try {
                result += instance.doOCR(resize(toBufferedImage(digits[i]), 474, 371)) + " ";
                
            } catch (TesseractException e) {
                System.err.println(e.getMessage());
            }	
		}
    	System.out.println(result);
    	return result;
    }
    public void imgProc(){
        Point[] vertices = null;
        RotatedRect rot = null;
        float scoreThresh = 0.5f;
        float nmsThresh = 0.4f;
        
        //major tuning has been done in order to be use for water meter
        //tuning at dividing into 8 segment still need to improvise, will do if there is much time
        //this code need to be use on highly stricted condition of image,  bit/no luminnance still do the job
        //Thanks to 'berak' for the EAST's c++ official code convert into java
        // Model from https://github.com/argman/EAST
        // You can find it here : https://github.com/opencv/opencv_extra/blob/master/testdata/dnn/download_models.py#L309
        
        Net net = Dnn.readNetFromTensorflow(dataSetPath);
        

        
        // input image
        Mat frame = Imgcodecs.imread(demoImg);
        //Mat frame = Imgcodecs.imread("C:\\Users\\shaufyq\\Desktop\\syamil.jpg");
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);

        Size siz = new Size(320, 320);
        int W = (int)(siz.width / 4); // width of the output geometry  / score maps
        int H = (int)(siz.height / 4); // height of those. the geometry has 4, vertically stacked maps, the score one 1
        
        Mat blob = Dnn.blobFromImage(frame, 1.0,siz, new Scalar(123.68, 116.78, 103.94), true, false);
        net.setInput(blob);
        List<Mat> outs = new ArrayList<>(2);
        List<String> outNames = new ArrayList<String>();
        outNames.add("feature_fusion/Conv_7/Sigmoid");
        outNames.add("feature_fusion/concat_3");
        net.forward(outs, outNames);

        // Decode predicted bounding boxes.
        //reshape(channel, row)
        Mat scores = outs.get(0).reshape(1, H);
        // My lord and savior : http://answers.opencv.org/question/175676/javaandroid-access-4-dim-mat-planes/
        Mat geometry = outs.get(1).reshape(1, 5 * H); // don't hardcode it !
        List<Float> confidencesList = new ArrayList<>();
        List<RotatedRect> boxesList = decode(scores, geometry, confidencesList, scoreThresh);

        // Apply non-maximum suppression procedure.
        MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confidencesList));
        RotatedRect[] boxesArray = boxesList.toArray(new RotatedRect[0]);
        MatOfRotatedRect boxes = new MatOfRotatedRect(boxesArray);
        MatOfInt indices = new MatOfInt(); 
        Dnn.NMSBoxesRotated(boxes, confidences, scoreThresh, nmsThresh, indices);
        
        
        // Render detections
        Point ratio = new Point((float)frame.cols()/siz.width, (float)frame.rows()/siz.height);
        int[] indexes = indices.toArray();
 
        for(int i = 0; i<indexes.length;++i) {
        	
            rot = boxesArray[indexes[0]];
            vertices = new Point[4];
            rot.points(vertices);
            for (int j = 0; j < 4; ++j) {
                vertices[j].x *= ratio.x;
                vertices[j].y *= ratio.y;
            }
            for (int j = 0; j < 4; ++j) {
                Imgproc.line(frame, vertices[j], vertices[(j + 1) % 4], new Scalar(0, 0,255), 1);  	
            }
        }
       
       
       
       
        rot = boxesArray[indexes[0]];
        vertices = new Point[4];
        rot.points(vertices);
        
        
        // "image_outputDigit" is the water meter, "image_output" is the home address
        Mat image_outputDigit = getBoxEach(vertices,rot,boxesArray,indexes,ratio,frame,true);
        

        
        //To divide into  8 segment
        System.out.println(image_outputDigit.cols() + " "+ image_outputDigit.rows());
        Mat[] digits = new Mat[10];
        Mat[] digitsD = new Mat[10];
        int aX =  image_outputDigit.cols();
        int aY =image_outputDigit.rows();
        int ratioToDivide = (aX / 8) - 2 ;
        int j = 0;
        int i = ratioToDivide;
        Point st = new Point(0,aY);
        while (j <= 4) {
            Point b = new Point(i, 0);
            Rect rec = new Rect(st,b);
            digits[j] = image_outputDigit.submat(rec);
            digitsD[j] = image_outputDigit.submat(rec);
            j++;
            st = new Point(i,aY);
            i += ratioToDivide;    
		}
        
        //thresh  and filter each segment
        for (int k = 0; k < 5; k++) {
        	Imgproc.cvtColor(digits[k], digits[k], Imgproc.COLOR_RGB2GRAY);
			Imgproc.threshold(digits[k],digits[k], 0, 255, Imgproc.THRESH_OTSU);
			Imgproc.morphologyEx(digits[k], digits[k], Imgproc.MORPH_OPEN, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(1, 5)));
			// displayImage(resize(toBufferedImage(digits[k]), 473, 371));
		}

        //masking and recognition
        for (int k = 0; k <= 4; k++) {
        	findContour(digits[k],digitsD[k]);
		}
        
        //resize into 300dpi, stated in tesseract for better recog
        for (int l = 0; l <= 4; l++) {
        	resize(toBufferedImage(digits[l]), 300, 300);
		}
        
        //hard-coded to OCR-A since we use in Malaysia's water meter font
        textRecognition(digits);
    }
    public static BufferedImage findContour(Mat img, Mat imgC) {
    	List<MatOfPoint> contours = new ArrayList<>();
    	Mat mask = new Mat(new Size(imgC.cols(), imgC.rows()), CvType.CV_8UC1);
    	mask.setTo(new Scalar(255));
    	Imgproc.Canny(img, mask,100,300,3,false );
    	Imgproc.findContours(mask, contours, new Mat(), Imgproc.RETR_TREE ,Imgproc.CHAIN_APPROX_SIMPLE);
    	System.out.println("contour number: "+ contours.size());
    	
    	//find largest contour's width and height
    	double largestWidth = 0;
		double largestheight = 0;
		int maxContourParameter = 0;
    	for (int i = 0; i < contours.size(); i++) {
			
			if (contours.get(i).width() > largestWidth || contours.get(i).height() > largestheight) {
				largestWidth = contours.get(i).width();
				largestheight = contours.get(i).height();
				maxContourParameter = i;
			}
		}
    	 MatOfPoint2f approxCurve = new MatOfPoint2f();
    	
    	
    	
    		 //Convert contours(i) from MatOfPoint to MatOfPoint2f
            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(maxContourParameter).toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);

            
             // draw enclosing rectangle (all same color, but you could use variable i to make them unique)
            Imgproc.rectangle(img, new Point(rect.x,rect.y-0.1), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(0, 0, 0), 1);
            
    	
            
            
    	//apply masking to img
    	Mat masking = new Mat(new Size(img.cols(), img.rows()), CvType.CV_8UC1);
    	masking.setTo(new Scalar(255));
    	
    	double[] data = {0,0,0};
    	for (int i = rect.x+1 ; i < rect.x+rect.width-2 ; i++) {
			for (int j = rect.y; j < rect.y+rect.height; j++) {
				masking.put(j, i, data);
			}
		}
    	
    	Core.bitwise_and(masking, masking, img, masking);
    	
    	BufferedImage imgChge = toBufferedImage(img);
    	
    	return imgChge;
    }
    public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
    public static Mat getBoxEach(Point[] vertices, RotatedRect rot, RotatedRect[] boxesArray, int[] indexes,Point ratio ,Mat frame, boolean digit) {
        int rangeY =  0;
        int rangeX = 0;
        Mat img_out = null;
        rot.points(vertices);
        
        
        if (digit) {
    	
	    	 for (int j = 0; j < 4; ++j) {
	             vertices[j].x *= ratio.x;
	             vertices[j].y *= ratio.y;
	         }
	         rangeX = (int) (vertices[2].x-vertices[0].x);
	         rangeY = (int) (vertices[0].y-vertices[2].y);
	         Rect rectCropDigit = new Rect(vertices[0],vertices[2]);
	         img_out = frame.submat(rectCropDigit);
			
		} else {
			rangeX = (int) (vertices[2].x-vertices[0].x);
	        rangeY = (int) (vertices[0].y-vertices[2].y);
			Rect rectCrop = new Rect((int)(vertices[0].x * ratio.x), (int)(vertices[0].y * ratio.x), (int) (rangeX * ratio.x), (int) (rangeY * ratio.y));
	        img_out = frame.submat(rectCrop);
		}
        
    	return img_out;
    }
    private static List<RotatedRect> decode(Mat scores, Mat geometry, List<Float> confidences, float scoreThresh) {
        // size of 1 geometry plane
        int W = geometry.cols();
        int H = geometry.rows() / 5;
        //System.out.println(geometry);
        //System.out.println(scores);

        List<RotatedRect> detections = new ArrayList<>();
        for (int y = 0; y < H; ++y) {
            Mat scoresData = scores.row(y);
            Mat x0Data = geometry.submat(0, H, 0, W).row(y);
            Mat x1Data = geometry.submat(H, 2 * H, 0, W).row(y);
            Mat x2Data = geometry.submat(2 * H, 3 * H, 0, W).row(y);
            Mat x3Data = geometry.submat(3 * H, 4 * H, 0, W).row(y);
            Mat anglesData = geometry.submat(4 * H, 5 * H, 0, W).row(y);

            for (int x = 0; x < W; ++x) {
                double score = scoresData.get(0, x)[0];
                if (score >= scoreThresh) {
                    double offsetX = x * 4.0;
                    double offsetY = y * 4.0;
                    double angle = anglesData.get(0, x)[0];
                    double cosA = Math.cos(angle);
                    double sinA = Math.sin(angle);
                    double x0 = x0Data.get(0, x)[0];
                    double x1 = x1Data.get(0, x)[0];
                    double x2 = x2Data.get(0, x)[0];
                    double x3 = x3Data.get(0, x)[0];
                    double h = x0 + x2;
                    double w = x1 + x3;
                    Point offset = new Point(offsetX + cosA * x1 + sinA * x2, offsetY - sinA * x1 + cosA * x2);
                    Point p1 = new Point(-1 * sinA * h + offset.x, -1 * cosA * h + offset.y);
                    Point p3 = new Point(-1 * cosA * w + offset.x,      sinA * w + offset.y); // original trouble here !
                    RotatedRect r = new RotatedRect(new Point(0.5 * (p1.x + p3.x), 0.5 * (p1.y + p3.y)), new Size(w, h), -1 * angle * 180 / Math.PI);
                    detections.add(r);
                    confidences.add((float) score);
                }
            }
        }
        return detections;
    }
    public static BufferedImage toBufferedImage(Mat m){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);  
        return image;

    }
}