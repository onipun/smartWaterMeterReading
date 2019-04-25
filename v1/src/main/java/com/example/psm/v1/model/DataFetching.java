package com.example.psm.v1.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.psm.v1.database.UploadImg;
import com.example.psm.v1.database.UploadImgRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.bytebuddy.asm.Advice.Exit;


/**
 * DataFetching
 */
@Service
public class DataFetching {

    @Autowired
    UploadImgRepository history;


    public Iterable<UploadImg> img() {
        
        Iterable<UploadImg> allHistory = history.findAll();
        System.out.println(allHistory);
        return allHistory;
    }

    public List<Integer> getConvertToImgString(){
        String result;
        List<Integer> price = new ArrayList<>();
        for (UploadImg var : img()) {
            price.add(var.getPrice());
        }

        result = price.toString();
        System.out.println(result);
        return price;
    }

    public List<Integer> getConvertToImgDateString(){
        String result;
        List<Integer> date = new ArrayList<>();
        for (UploadImg var : img()) {
            date.add(var.getDate().getMonth() + 1);
        }

        result = date.toString();
        System.out.println(result);
        return date;
    }

    public int getCurrentMonthCost(int currentDate){
        int result = 0;
        
        for (UploadImg var : img()) {
            if (currentDate == var.getDate().getMonth()) {
                result = var.getPrice();
                break;
            }
        
        }
        
        return result;
    }

    public int getUnpayStatus(int currentDate){
        int result = 0;
        
        for (UploadImg var : img()) {
            if (currentDate >= var.getDate().getMonth() && ("unpay").equals(var.getStatus())) {
                result++;
            }
        
        }
        
        return result;
    }
}