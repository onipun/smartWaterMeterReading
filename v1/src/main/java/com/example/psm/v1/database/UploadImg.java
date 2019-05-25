package com.example.psm.v1.database;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class UploadImg {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    int id;
    String imgName;
    int imgRead;
    int price;
    int uploadBy;
    String ownerId;
    Date date;
    String status;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getImgName(){
        return imgName;
    }

    public void setImgName(String imgName){
        this.imgName = imgName;
    }

    public void setImgRead(int imgRead){
        this.imgRead = imgRead;
    }

    public int getImgRead(){
        return imgRead;
    }

    public void setPrice(int price){
        this.price = price;
    }

    public int getPrice(){
        return price;
    }

    public int getUploadBy(){
        return uploadBy;
    }

    public void setUploadBy(int uploadBy){
        this.uploadBy = uploadBy;
    }

    public String getOwnerId(){
        return ownerId;
    }

    public void setOwnerId(String ownerId){
        this.ownerId = ownerId;
    }

    public Date getDate(){
        return date;
    } 

    public void setDate(Date date){
        this.date = date;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

}