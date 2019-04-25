package com.example.psm.v1.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // This tells Hibernate to make a table out of this class
public class Worker{

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    int id;
    String name;
    String phone;
    String authLevel;
    
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getAuthLevel(){
        return authLevel;
    }

    public void setAuthLevel(String authLevel){
        this.authLevel = authLevel;
    }


    
}