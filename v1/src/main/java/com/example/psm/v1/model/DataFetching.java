package com.example.psm.v1.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.*;
import java.util.concurrent.*;

import com.example.psm.v1.database.UploadImg;
import com.example.psm.v1.database.UploadImgRepository;
import com.example.psm.v1.database.User;
import com.example.psm.v1.database.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Set;
import org.thymeleaf.util.ArrayUtils;


/**
 * DataFetching
 */
@Service
public class DataFetching {

    @Autowired
    UploadImgRepository history;

    @Autowired
    UserRepository user;

    public String getAuthIc(String name) {
        Iterable<User> ics;
        ics = user.findAll();
        for (User var : ics) {
            if (var.getName().equals(name)) {
                String ic = var.getIdentityCard();
                return ic;
            }
        }
        return "something wrong fetching data";
    }


    public Iterable<UploadImg> img() {
        
        Iterable<UploadImg> allHistory = history.findAll();
        System.out.println(allHistory);
        return allHistory;
    }

    public Iterable<UploadImg> imgIndividu(int id) {
        
        Iterable<UploadImg> allHistory = history.findById(id);
        System.out.println(allHistory);

        return allHistory;
    }

    public Iterable<UploadImg> fetchUserhistory(String id) {
        
        Iterable<UploadImg> allHistory = history.fetchUserHistory(id);
        List<UploadImg> allHistoryNoDupe = new ArrayList<>();
        for (UploadImg var : allHistory) {
            allHistoryNoDupe.add(var);
        }
        List<UploadImg> listWithNoDupe = new ArrayList<>();
        listWithNoDupe = removeDuplicates(allHistoryNoDupe);
    
        System.out.println(allHistory);
    
        return listWithNoDupe;
    }

    // to remove duplication in fetchUserHistory
    private List<UploadImg> removeDuplicates(List<UploadImg> listWithDuplicates) {
        /* Set of all attributes seen so far */
        Set<Integer> attributes = new HashSet<Integer>();
        /* All confirmed duplicates go in here */
        List<UploadImg> duplicates = new ArrayList<UploadImg>();
    
        for(UploadImg x : listWithDuplicates) {
            if(attributes.contains(x.getDate().getMonth()+1)) {
                duplicates.add(x);
            }
            attributes.add(x.getDate().getMonth()+1);
        }

        /* Clean list without any dups */
        listWithDuplicates.removeAll(duplicates);
        
        return listWithDuplicates;
    }

    public static <T> Predicate<T> distinctByMonth(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public List<Double> getConvertToImgString(String id){
        String result;
        List<Double> price = new ArrayList<>();
        Set<Double> priceNoDupe = new LinkedHashSet<>();
        for (UploadImg var : fetchUserhistory(id)) {
            price.add(var.getPrice());
        }

        Double[] priceArr = new Double[12];

        if(price.size() > 12){

            for(int i = 0; i < 13; i++){
                priceArr[i] = price.get(i);
            }

            price.clear();

            for (Double var : priceArr) {
                price.add(var);
            }
        }

        /*
        problem when remove duplicate of date is when using
        same alogrith, it remove same amount that even has
        different date.

        current solution:
        comment the remove algorithm of amount

        note:
        this is temporary solution
        */


        // priceNoDupe.addAll(price);
        // price.clear();
        // price.addAll(priceNoDupe);
        Collections.reverse(price);
        

        result = price.toString();
        System.out.println(result);
        return price;
    }

    public List<String> getConvertToImgDateString(String id){
        String result;
        List<String> date = new ArrayList<>();
        Set<String> dateNoDupe = new LinkedHashSet<>();
        for (UploadImg var : fetchUserhistory(id)) {
            date.add(String.valueOf( var.getDate().getMonth() + 1)+"-"+ String.valueOf(var.getDate().getYear()-100));
        }

        String[] dateArr = new String[12];

        if (date.size() > 12) {
            
            for(int i = 0; i < 13; i++){
                dateArr[i] = date.get(i);
            }

            date.clear();
            for (String var : dateArr) {
                date.add(var);
            }
        }
        
        dateNoDupe.addAll(date);
        date.clear();
        date.addAll(dateNoDupe);
        Collections.reverse(date);

        System.out.println(date);
        return date;
    }

    public Double getCurrentMonthMeterRead(int currentDate, String id){
        Double result = 0.0;
        
        for (UploadImg var : fetchUserhistory(id)) {
            if (currentDate == var.getDate().getMonth() + 1) {
                System.out.println("prev date: "+var.getDate().getMonth());
                result = var.getImgRead();
                break;
            }
        
        }
        
        return result;
    }

    public Double getCurrentMonthCost(int currentDate, String id){
        Double result = 0.0;
        
        for (UploadImg var : fetchUserhistory(id)) {
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


    public String getOwnerName(String id){
        String name = user.getUserInfo(id);
        return name;
    }

    public String getOwnerAddress(String id){

        Iterable<User> u =  user.getOwnerAddress(id);
        String area= " " , houseID = "";
        for (User var : u) {
            area = var.getArea();
            houseID = var.getHouseID();
        }
        
        String address = houseID+" "+area;

        
        return address;

    }
}