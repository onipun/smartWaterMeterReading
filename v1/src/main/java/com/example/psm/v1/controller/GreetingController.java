package com.example.psm.v1.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.example.psm.v1.model.DataFetching;
import com.example.psm.v1.processing.TextDetection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class GreetingController {

    @Autowired
    ApplicationContext applicationContext;

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name" ,required=false, defaultValue="World") String name,
    @RequestParam(value = "age", required = false, defaultValue = "no stated") String age,  Model model, HttpServletRequest request) {
        model.addAttribute("name", name);
        model.addAttribute("age", age);

        String src = "syamil.jpg";
        model.addAttribute("img", src);

       TextDetection textDetection = applicationContext.getBean(TextDetection.class);
       textDetection.imgProc();
    
        return "greeting";
    }

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {

        DataFetching dataFetching = applicationContext.getBean(DataFetching.class);
        model.addAttribute("history", dataFetching.getConvertToImgString());
        model.addAttribute("historyDate", dataFetching.getConvertToImgDateString()); 
        return "index";
    }

    @GetMapping("/index.html")
    public String indexHtml(Model model, HttpServletRequest requests) {

        DataFetching dataFetching = applicationContext.getBean(DataFetching.class);
        model.addAttribute("history", dataFetching.getConvertToImgString());
        model.addAttribute("historyDate", dataFetching.getConvertToImgDateString()); 
        
        int date = new Date().getMonth();
        model.addAttribute("currentDatePrice", dataFetching.getCurrentMonthCost(date)); 
        model.addAttribute("previousDatePrice", dataFetching.getCurrentMonthCost(date-1));
        model.addAttribute("delayPayment", dataFetching.getUnpayStatus(date)); 
        return "index";
    }

    @GetMapping("/buttons.html")
    public String buttons() {

        return "buttons";
    }

    @GetMapping("/cards.html")
    public String cards() {
        return "cards";
    }

    @GetMapping("/utilities-color.html")
    public String utilitiesColor() {
        return "utilities-color";
    }

    
    @GetMapping("/utilities-border.html")
    public String utilitiesBorder() {
        return "utilities-border";
    }
    
    @GetMapping("/utilities-animation.html")
    public String utilitiesAnimation() {
        return "utilities-animation";
    }
    
    @GetMapping("/utilities-other.html")
    public String utilitiesOther() {
        return "utilities-other";
    }

    @GetMapping("/login.html")
    public String login() {
        return "login";
    }

    @GetMapping("/register.html")
    public String register() {
        return "register";
    }

    @GetMapping("/forgot-password.html")
    public String forgotPassword() {
        return "forgot-password";
    }

    
    @GetMapping("/404.html")
    public String p404() {
        return "404";
    }

    @GetMapping("/blank.html")
    public String blank() {
        return "blank";
    }
    
    @GetMapping("/charts.html")
    public String charts() {
        return "charts";
    }

    @GetMapping("/tables.html")
    public String tables(Model model, HttpServletRequest request) {
        
       DataFetching dataFetching = applicationContext.getBean(DataFetching.class);
       model.addAttribute("history", dataFetching.img());
        return "tables";
    }    

}