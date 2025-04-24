package com.cliqshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.cliqshop")
public class CliQShopBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CliQShopBackendApplication.class, args);
		System.out.println("The CliQShop Backend has been started...!");
	}

}
