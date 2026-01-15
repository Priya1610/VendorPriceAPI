package com.matsuri.hometask.vendorpriceapi.controller;

import com.matsuri.hometask.vendorpriceapi.domain.Price;
import com.matsuri.hometask.vendorpriceapi.service.PriceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/prices")
public class PriceController {

    @Autowired
    private PriceService priceService;

    private static final Logger logger = LogManager.getLogger(PriceController.class);

    @PostMapping
    public ResponseEntity<String> savePrices(@RequestBody Price price){
        if(price.getVendorName().isEmpty() || price.getVendorName().equals("null")){
            logger.warn("Invalid vendor name received during savePrices: {}", price.getVendorName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Vendor name cannot be null or empty");
        }
        if(price.getInstrumentName().isEmpty() || price.getInstrumentName().equals("null")){
            logger.warn("Invalid instrument name received during savePrices: {}", price.getInstrumentName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Instrument name cannot be null or empty");
        }
        priceService.savePrices(price);
        logger.info("Prices published successfully");
        return ResponseEntity.ok("Prices published successfully");
    }

    @GetMapping("/vendor/{vendor}")
    public ResponseEntity<List<Price>> pricesByVendor(@PathVariable String vendor) {
        if(vendor.equals("null") || vendor.isBlank()){
            logger.warn("Incorrect vendor input received during GET /vendor/{} call" , vendor);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,"Vendor param cannot be empty or null"
            );
        }
        logger.info("GET /vendor/{} called" , vendor);
        return ResponseEntity.ok(priceService.pricesByVendor(vendor));
    }

    @GetMapping("/instrument/{instrument}")
    public ResponseEntity<List<Price>> pricesByInstrument(@PathVariable String instrument) {
        if(instrument.equals("null") || instrument.isBlank()){
            logger.warn("Incorrect instrument input received during GET /instrument/{} call" , instrument);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,"Instrument param cannot be empty or null"
            );
        }
        logger.info("GET /instrument/{} called" , instrument);
        return ResponseEntity.ok(priceService.pricesByInstrument(instrument));
    }
}
