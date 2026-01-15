package com.matsuri.hometask.vendorpriceapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VendorPriceApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VendorPriceApiApplication.class, args);
    }

}
