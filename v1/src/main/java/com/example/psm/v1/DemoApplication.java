package com.example.psm.v1;

import org.apache.tomcat.jni.Library;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.SpringVersion;


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
	public static void main(String[] args) {
		
		//context should be close to avoid leaking of memory
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(WebConfig.class);
		ctx.refresh();
		ctx.close();

		//load library in main before proceeding deeper
		//using Library class to avoid from redundant load error
		Library.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		System.out.println("Spring version: " + SpringVersion.getVersion());
		SpringApplication.run(DemoApplication.class, args);

	}

	// @Bean
	// public CommandLineRunner demo(CustomerRepository repository) {
	// 	return (args) -> {
	// 		// save a couple of customers
	// 		// repository.save(new Customer("Jack", "Bauer"));
	// 		// repository.save(new Customer("Chloe", "O'Brian"));
	// 		// repository.save(new Customer("Kim", "Bauer"));
	// 		// repository.save(new Customer("David", "Palmer"));
	// 		// repository.save(new Customer("Michelle", "Dessler"));

	// 		// fetch all customers
	// 		log.info("Customers found with findAll():");
	// 		log.info("-------------------------------");
	// 		for (Customer customer : repository.findAll()) {
	// 			log.info(customer.toString());
	// 		}
	// 		log.info("");

	// 		// fetch an individual customer by ID
	// 		repository.findById(1L)
	// 			.ifPresent(customer -> {
	// 				log.info("Customer found with findById(1L):");
	// 				log.info("--------------------------------");
	// 				log.info(customer.toString());
	// 				log.info("");
	// 			});

	// 		// fetch customers by last name
	// 		log.info("Customer found with findByLastName('Bauer'):");
	// 		log.info("--------------------------------------------");
	// 		repository.findByLastName("Bauer").forEach(bauer -> {
	// 			log.info(bauer.toString());
	// 		});
	// 		// for (Customer bauer : repository.findByLastName("Bauer")) {
	// 		// 	log.info(bauer.toString());
	// 		// }
	// 		log.info("");
	// 	};
	// }

}
