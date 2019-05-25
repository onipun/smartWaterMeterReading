package com.example.psm.v1.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.psm.v1.database.UploadImg;
import com.example.psm.v1.database.UploadImgRepository;
import com.example.psm.v1.processing.TextDetection;
import com.lowagie.text.pdf.codec.Base64.InputStream;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(path = "/upload")
public class UploadImgController {


    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    @Autowired
    private UploadImgRepository uploadImgRepository;
    private Date d1;
    ApplicationContext applicationContext;

    // NOT TESTED YET, CONTINUE TOMMOROW (9/4/2014)
    @GetMapping(path = "/add") // Map ONLY GET Requests
    public @ResponseBody String saveImgIntoDb(@RequestParam String imgName, @RequestParam int ownId,
            @RequestParam int uploadBy) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        d1 = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
        // String formattedDate = df.format(d1);

        UploadImg n = new UploadImg();

        n.setImgName(imgName);
        n.setOwnerId(String.valueOf(ownId));
        n.setUploadBy(uploadBy);
        n.setDate(d1);
        uploadImgRepository.save(n);
        return "Saved";
    }

    @PostMapping(value = "/uploadImage2")
    public @ResponseBody String handleFileUpload(@RequestPart("image") MultipartFile file) {
        try {
            InputStream in = (InputStream) file.getInputStream();
            File newFile = new File("C:\\ca.jpg");
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(newFile);
            byte[] img = file.getBytes();
            out.write(img);
            out.close();
            return "success";

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "failed";
        }
    }

    @PostMapping(value = "/img")
    public @ResponseBody String handleImageUpload(
            @RequestPart(value = "image",required = true) MultipartFile image, @RequestParam("identityCard") String identityCard) {
        

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                long timeInMili = timestamp.getTime();
                

		try{
            String path = "C:\\images\\"+timeInMili+".jpg";
			FileOutputStream fos = new FileOutputStream(path); //change the path where you want to save the image
            byte[] byteArray = image.getBytes();
			fos.write(byteArray);
            fos.close();
            TextDetection textDetection = new TextDetection();
            String imgRead = textDetection.imgProc(path);
            UploadImg uploadImg = new UploadImg();
            uploadImg.setDate(timestamp);
            uploadImg.setImgName(timeInMili+".jpg");
            uploadImg.setImgRead(Integer.valueOf(imgRead));
            uploadImg.setOwnerId(String.valueOf(identityCard));
            uploadImg.setPrice(150);
            uploadImg.setStatus("unpayed");
            uploadImgRepository.save(uploadImg);

            // if result contain null replace with "0"
            // not done yet
			return imgRead;
            
		}
		catch(Exception e){
            System.out.println(e);
            return "check server (logcat), something wrong while parsing at the server";
        }

    }

    // this is just only for testing purpose
    // localhost:8080/upload/add-test?imgRead=0038
    @GetMapping(path="/add-test") // Map ONLY GET Requests
	public @ResponseBody String saveImgDataTest (@RequestParam String imgRead) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request

        d1 = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
        // String formattedDate = df.format(d1);

        UploadImg n = new UploadImg();
        
		n.setImgRead(100);
        n.setPrice(200);
        n.setDate(d1);
		uploadImgRepository.save(n);
		return "Saved";
	}
}