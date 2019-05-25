package com.example.psm.v1;

import java.util.Hashtable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.naming.*;
import javax.naming.directory.*;

import com.example.psm.v1.database.Person;
import com.example.psm.v1.database.User;
import com.example.psm.v1.model.PersonRepository;

import org.apache.tomcat.jni.Library;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.SpringVersion;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//     @SpringBootApplication is a convenience annotation that adds all of the following:
//     @Configuration tags the class as a source of bean definitions for the application context.
//     @EnableAutoConfiguration tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
//     Normally you would add @EnableWebMvc for a Spring MVC app, but Spring Boot adds it automatically when it sees spring-webmvc on the classpath. This flags the application as a web application and activates key behaviors such as setting up a DispatcherServlet.
//     @ComponentScan tells Spring to look for other components, configurations, and services in the hello package, allowing it to find the controllers.
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@SpringBootApplication
public class DemoApplication {

	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);
	@Autowired
	PersonRepository personRepository;

	public static void main(String[] args) {

		// context should be close to avoid leaking of memory
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(WebConfig.class);
		ctx.refresh();
		ctx.close();

		// load library in main before proceeding deeper
		// using Library class to avoid from redundant load error
		Library.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		System.out.println("Spring version: " + SpringVersion.getVersion());
		SpringApplication.run(DemoApplication.class, args);

		
	}

	@PostConstruct
	public void name() {
		
		log.info("ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
		

		// Person p = new Person();
		// p.setName("admin");
		// if (personRepository.create(p)) {
		// 	log.info("true");
		// 	log.info("new User created: "+p.getName());
		// } else {
		// 	log.info("false");
		// }

		List<String> names = personRepository.getAllPersonNames();
		for (String var : names) {
			log.info(var);	
		}
		
		


		log.info("ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
		// System.exit(-1);
	}

}
