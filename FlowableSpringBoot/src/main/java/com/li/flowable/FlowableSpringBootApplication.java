package com.li.flowable;

//import com.li.flowable.controller.AppDispatcherServletConfiguration;
//import com.li.flowable.controller.ApplicationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

//@Import(value = {ApplicationConfiguration.class,
//        AppDispatcherServletConfiguration.class})

//@ComponentScan(basePackages = {"com.li.flowable.*"})
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class FlowableSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowableSpringBootApplication.class, args);
    }

}
