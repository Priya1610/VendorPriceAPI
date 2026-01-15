package com.matsuri.hometask.vendorpriceapi.repository;

import com.matsuri.hometask.vendorpriceapi.controller.PriceController;
import com.matsuri.hometask.vendorpriceapi.domain.Price;
import com.matsuri.hometask.vendorpriceapi.exception.InstrumentDataNotFoundException;
import com.matsuri.hometask.vendorpriceapi.exception.VendorDataNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPriceRepository implements PriceRepository{

    private Map<String, List<Price>> vendorPrices;
    private Map<String, List<Price>> instrumentPrices;

    private static final Logger logger = LogManager.getLogger(InMemoryPriceRepository.class);

    //Constructor for junit test cases
    public InMemoryPriceRepository(Map<String, List<Price>> vendorPrices,
                                   Map<String, List<Price>> instrumentPrices){
        this.vendorPrices = vendorPrices;
        this.instrumentPrices = instrumentPrices;
    }
    //default constructor
    public InMemoryPriceRepository() {
       this(new ConcurrentHashMap<>() , new ConcurrentHashMap<>());
    }

    @Override
    public List<Price> findPricesByVendor(String vendor) {
        return new ArrayList<>(Optional.ofNullable(vendorPrices.get(vendor))
                .orElseThrow(() -> new VendorDataNotFoundException(vendor))
        );
    }
    @Override
    public List<Price> findPricesByInstrument(String instrument) {
        return new ArrayList<>(Optional.ofNullable(instrumentPrices.get(instrument))
                .orElseThrow(() -> new InstrumentDataNotFoundException(instrument))
        );
    }
    @Override
    public void save(Price price) {
        vendorPrices
                .computeIfAbsent(price.getVendorName(), p -> Collections.synchronizedList(new ArrayList<>()))
                .add(price);

        instrumentPrices
                .computeIfAbsent(price.getInstrumentName(), p -> Collections.synchronizedList(new ArrayList<>()))
                .add(price);
    }

    @Override
    public void evictOlddata(Instant evictTime) {
        if(!vendorPrices.isEmpty() || !instrumentPrices.isEmpty()) {
            updatePriceCache(vendorPrices, evictTime);
            updatePriceCache(instrumentPrices, evictTime);
            logger.info("Vendor map size {}" ,vendorPrices.size());
            logger.info("Instrument map size {}" ,instrumentPrices.size());
        }else{
            logger.warn("Vendor or Instrument cache map is empty");
        }
    }

    private void updatePriceCache(Map<String, List<Price>> cachePrices, Instant evictTime) {
      // thread safe eviction
        cachePrices.forEach((key,prices) ->{
            synchronized (prices){
                prices.removeIf(p-> p.getCreatedAt().isBefore(evictTime));
            }
        });
    }
}
