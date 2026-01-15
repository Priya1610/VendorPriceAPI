package com.matsuri.hometask.vendorpriceapi.config;

import com.matsuri.hometask.vendorpriceapi.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CachePriceEvictSchedulerConfig {
    @Autowired
    private PriceService priceService;

    // Runs once every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void evictOldPrices() {
        priceService.evictOldData();
    }
}
