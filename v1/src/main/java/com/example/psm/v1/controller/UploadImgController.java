package com.example.psm.v1.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.psm.v1.database.UploadImg;
import com.example.psm.v1.database.UploadImgRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(path="/upload")
public class UploadImgController{

    // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    @Autowired     
    private UploadImgRepository uploadImgRepository;
    private Date d1;


    // NOT TESTED YET, CONTINUE TOMMOROW (9/4/2014)
    @GetMapping(path="/add") // Map ONLY GET Requests
	public @ResponseBody String saveImgIntoDb (@RequestParam String imgName
			, @RequestParam int ownId, @RequestParam int uploadBy) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request

        d1 = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
        // String formattedDate = df.format(d1);

        UploadImg n = new UploadImg();
        
		n.setImgName(imgName);
        n.setOwnerId(ownId);
        n.setUploadBy(uploadBy);
        n.setDate(d1);
		uploadImgRepository.save(n);
		return "Saved";
	}

    @GetMapping("/img")
    public @ResponseBody String handleFileUpload( 
            @RequestParam("file") MultipartFile file){
            String name = "test11";
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = 
                        new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
                // insert img save into dir here


            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
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