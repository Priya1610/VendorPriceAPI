package com.matsuri.hometask.vendorpriceapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matsuri.hometask.vendorpriceapi.domain.Price;
import com.matsuri.hometask.vendorpriceapi.repository.InMemoryPriceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

@Service
public class PriceService {
    //@Autowired
    private final InMemoryPriceRepository inMemoryPriceRepository;

    public PriceService(InMemoryPriceRepository inMemoryPriceRepository){
        this.inMemoryPriceRepository = inMemoryPriceRepository;
    }

    private static final HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
    private static final Logger logger = LogManager.getLogger(PriceService.class);

    public List<Price> pricesByVendor(String vendor) {
        return inMemoryPriceRepository.findPricesByVendor(vendor);
    }

    public List<Price> pricesByInstrument(String instrument) {
        return inMemoryPriceRepository.findPricesByInstrument(instrument);
    }

    public void savePrices(Price price) {
        price.setCreatedAt(Instant.now());
        inMemoryPriceRepository.save(price);
        logger.info("Price successfully created {}", price);
         try {
            /*considering subscriber is notified on new price publish. for production ready code Messaging queue
            will be the best approach. Comsider it as a sample code to demonstrate how system/systems could be notified
            */
            notifyDownstreamSystem(new ObjectMapper().writeValueAsString(price), "http://example:8087");
        } catch (Exception e) {
            logger.error("Exception during downstream notification {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
    private void notifyDownstreamSystem(String payload, String url) {
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
        } catch (Exception e) {
            logger.error("Exception while forming Http request to notify downstream {}" , e.getMessage());
            throw new RuntimeException(e);
        }
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> {
                    logger.info(response.statusCode());
                });
    }
    

    public void evictOldData(){
        logger.info("Clean up data older than 30 days called");
        Instant evictTime = Instant.now().minus(30, ChronoUnit.DAYS);
        inMemoryPriceRepository.evictOlddata(evictTime);
    }
}
