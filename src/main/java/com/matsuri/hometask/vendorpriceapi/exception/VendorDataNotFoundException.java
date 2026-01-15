package com.matsuri.hometask.vendorpriceapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VendorDataNotFoundException extends RuntimeException{

    public VendorDataNotFoundException(String message){
        super("Vendor not found :" +message);
    }
}
