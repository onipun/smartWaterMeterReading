package com.example.psm.v1.controller;

import com.example.psm.v1.database.Person;
import com.example.psm.v1.database.UploadImg;
import com.example.psm.v1.database.User;
import com.example.psm.v1.database.UserRepository;
import com.example.psm.v1.model.DataFetching;
import com.example.psm.v1.model.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * AdminController
 */

@Controller  
@RequestMapping(path="/admin")
public class AdminController {

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;

    @Autowired
	PersonRepository personRepository;
    
    @Autowired
    ApplicationContext applicationContext;

    Iterable<UploadImg> allHistory;
    
@GetMapping("/search")
public @ResponseBody Iterable<UploadImg> name(@RequestParam String id ) {

    DataFetching dataFetching = applicationContext.getBean(DataFetching.class);
    
    allHistory = dataFetching.fetchUserhistory(id);

    return allHistory;
}

@GetMapping("/load")
public String name(Model model) {
    model.addAttribute("history", allHistory);
    return "fragments/dataUserTable.html :: dataUserTable";
}


@GetMapping("/register")
public String registerNewUserForm(){

    return "fragments/dataUserTable.html :: registerUserTable";
}

@GetMapping("/register-form")
@ResponseBody //to return a string instead of a page
public String registerFormProc(Model model,@RequestParam("reg_ic") String id ,@RequestParam("reg_username") String name,
@RequestParam("reg_email") String email, @RequestParam("reg_houseNumber") String houseID,
@RequestParam("reg_houseArea") String area, @RequestParam("reg_houseAddress") String address){
    

    try {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHouseID(houseID);
        user.setArea(area);
        user.setAddress(address);
        user.setIdentityCard(id);

        userRepository.save(user);

        Person person = new Person();
        person.setName(name);

        personRepository.create(person);
    } catch (Exception e) {
        //TODO: handle exception
        System.out.println(e);
        return e.toString();
    }

    System.out.println("Success register new user.");
    return "Success register new user.";
}
    
}