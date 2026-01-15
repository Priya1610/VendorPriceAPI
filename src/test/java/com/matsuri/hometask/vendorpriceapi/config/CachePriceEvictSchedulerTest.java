package com.matsuri.hometask.vendorpriceapi.config;

import com.matsuri.hometask.vendorpriceapi.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CachePriceEvictSchedulerTest {
    @Mock
    private PriceService priceService;
    @InjectMocks
    private CachePriceEvictSchedulerConfig cachePriceEvictScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_priceEvictScheduler(){
        cachePriceEvictScheduler.evictOldPrices();
        verify(priceService, times(1)).evictOldData();
    }
}
