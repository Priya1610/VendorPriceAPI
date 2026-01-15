package com.matsuri.hometask.vendorpriceapi.service;

import com.matsuri.hometask.vendorpriceapi.domain.Price;
import com.matsuri.hometask.vendorpriceapi.exception.InstrumentDataNotFoundException;
import com.matsuri.hometask.vendorpriceapi.exception.VendorDataNotFoundException;
import com.matsuri.hometask.vendorpriceapi.repository.InMemoryPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.when;

public class PriceServiceTest {
    private InMemoryPriceRepository inMemoryPriceRepository;

    private PriceService priceService;

    private Map<String, List<Price>> vendorPrices;
    private Map<String, List<Price>> instrumentPrices;

    @BeforeEach
    void setUp() {
        vendorPrices = new ConcurrentHashMap<>();
        instrumentPrices = new ConcurrentHashMap<>();
        inMemoryPriceRepository =
                new InMemoryPriceRepository(vendorPrices, instrumentPrices);
        priceService = new PriceService(inMemoryPriceRepository);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_pricesByVendor_matchFound(){
        Price price = new Price("Vendor1","Instrument1",100.00, Instant.now());
        priceService.savePrices(price);
        List<Price> result = priceService.pricesByVendor("Vendor1");

        assertEquals(1, result.size());
        assertEquals("Vendor1", result.get(0).getVendorName());
    }

    @Test
    public void test_pricesByVendor_invalidVendor(){
        assertThrows(VendorDataNotFoundException.class, () ->
                priceService.pricesByVendor("invalidVendor"));
       }
    @Test
    public void test_pricesByInstrument_matchFound(){
        Price price = new Price("Vendor1","Instrument1",200.00, Instant.now());
        priceService.savePrices(price);
        List<Price> result = priceService.pricesByInstrument("Instrument1");
        assertEquals(1, result.size());
        assertEquals("Instrument1", result.get(0).getInstrumentName());
    }

    @Test
    public void test_pricesByInstrument_invalidInstrument(){
        assertThrows(InstrumentDataNotFoundException.class, () ->
                priceService.pricesByInstrument("invalidInstrument"));
          }
    @Test
    public void test_savePrices(){
        Price price = new Price("Vendor1","Instrument1",200.00, Instant.now());
        priceService.savePrices(price);
        List<Price> vendorPrices = priceService.pricesByVendor("Vendor1");
        List<Price> instrumentPrices = priceService.pricesByInstrument("Instrument1");

        assertThat(vendorPrices).hasSize(1);
        assertThat(instrumentPrices).hasSize(1);
    }
    @Test
    void test_evictOldData_shouldHandleEmptyCaches() {
        priceService.evictOldData();
        assertThat(vendorPrices).isEmpty();
        assertThat(instrumentPrices).isEmpty();
    }
    @Test
    void test_evictOldData_shouldEvictDataOlderThan30Days() {
        Instant evictTime = Instant.now();
        Instant oldData = evictTime.minus(31, ChronoUnit.DAYS);
        Instant latestData = evictTime.minus(5, ChronoUnit.DAYS);
        vendorPrices.put("vendor", new ArrayList<>(List.of(
                new Price("test1", "instru1", 100.0, oldData),
                new Price("test2", "instru2", 200.0, latestData)
        )));
       priceService.evictOldData();
       assertThat(vendorPrices.get("vendor")).hasSize(1);
    }
    @Test
    void test_evictOldData_shouldNotEvictDataCreatedBeforeEvictTime() {
        Instant evictTime = Instant.now();
        Instant minusTime1 = evictTime.minusSeconds(10);
        Instant minusTime2 = evictTime.minusSeconds(20);
        vendorPrices.put("vendor", new ArrayList<>(List.of(
                new Price("test1","instru1",100.00,minusTime1)
                )));
        instrumentPrices.put("instrument", new ArrayList<>(List.of(
                new Price("test1","instru1",100.00,minusTime2)
                )));
        priceService.evictOldData();
        assertEquals(1, vendorPrices.size());
        assertEquals(1, instrumentPrices.size());
        assertEquals("test1",vendorPrices.get("vendor").get(0).getVendorName());
        assertEquals("instru1",instrumentPrices.get("instrument").get(0).getInstrumentName());
    }
}
