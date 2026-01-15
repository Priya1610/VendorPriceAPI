package com.matsuri.hometask.vendorpriceapi.repository;

import com.matsuri.hometask.vendorpriceapi.domain.Price;
import com.matsuri.hometask.vendorpriceapi.exception.InstrumentDataNotFoundException;
import com.matsuri.hometask.vendorpriceapi.exception.VendorDataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryPriceRepositoryTest {

    private InMemoryPriceRepository inMemoryPriceRepository;

    @BeforeEach
    void setUp() {
        inMemoryPriceRepository = new InMemoryPriceRepository();
    }

    @Test
    public void test_save_findPricesByVendor_shouldReturnPrices(){
        Price price = new Price("Vendor","Instrument",100.00, Instant.now());
        inMemoryPriceRepository.save(price);
        List<Price> result = inMemoryPriceRepository.findPricesByVendor("Vendor");
        assertEquals(1, result.size());
        assertEquals("Vendor", result.get(0).getVendorName());
    }
    @Test
    public void test_findPricesByVendor_shouldThrowException(){
        assertThrows(VendorDataNotFoundException.class,
                ()-> inMemoryPriceRepository.findPricesByVendor("unknownVendor"));
    }
    @Test
    public void test_save_findPricesByInstrument_shouldReturnPrices(){
        Price price = new Price("Vendor","Instrument",200.00, Instant.now());
        inMemoryPriceRepository.save(price);
        List<Price> result = inMemoryPriceRepository.findPricesByInstrument("Instrument");
        assertEquals(1, result.size());
        assertEquals("Instrument", result.get(0).getInstrumentName());
    }
    @Test
    public void test_findPricesByInstrument_shouldThrowException(){
        assertThrows(InstrumentDataNotFoundException.class,
                ()-> inMemoryPriceRepository.findPricesByInstrument("unknownInstrument"));
    }
}
