package com.matsuri.hometask.vendorpriceapi.repository;

import com.matsuri.hometask.vendorpriceapi.domain.Price;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PriceRepository {

    List<Price> findPricesByVendor(String vendor);

    List<Price> findPricesByInstrument(String instrument);

    void save(Price price);

    void evictOlddata(Instant evictTime);
}
