package com.matsuri.hometask.vendorpriceapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matsuri.hometask.vendorpriceapi.domain.Price;
import com.matsuri.hometask.vendorpriceapi.exception.InstrumentDataNotFoundException;
import com.matsuri.hometask.vendorpriceapi.exception.VendorDataNotFoundException;
import com.matsuri.hometask.vendorpriceapi.service.PriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceController.class)
public class PriceControllerTest {
    @MockBean
    PriceService priceService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void test_savePrices() throws Exception {
        Price price = new Price("Vendor1","Instrument1",110.50, Instant.now());
        doNothing().when(priceService).savePrices(price);
        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(price)))
                        .andExpect(status().isOk());
    }
    @Test
    public void test_savePrices_missingBody() throws Exception {
        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void test_savePrices_wrongJsonBody() throws Exception {
        mockMvc.perform(post("/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalid\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void test_getPricesByVendor() throws Exception {
        List<Price> priceList = Arrays.asList(new Price("VENDOR","Instrument1",110.50, Instant.now()),
                    new Price("VENDOR","Instrument2", 200.00,Instant.now()));
        when(priceService.pricesByVendor("VENDOR")).thenReturn(priceList);
        mockMvc.perform(get("/prices/vendor/{vendor}", "VENDOR")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].vendorName").value("VENDOR"));
    }
    @Test
    public void test_getPricesByVendor_missingPathVariable() throws Exception {
        mockMvc.perform(get("/prices/vendor/"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void test_getPricesByVendor_incorrectPathVariable() throws Exception {
        when(priceService.pricesByVendor("test")).thenThrow(new VendorDataNotFoundException("test"));
        mockMvc.perform(get("/prices/vendor/{vendor}", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_getPricesByInstrument() throws Exception {
        List<Price> priceList = Arrays.asList(new Price("Vendor1","Instrument",110.50, Instant.now()),
                new Price("Vendor2","Instrument", 200.00,Instant.now()));
        when(priceService.pricesByInstrument("Instrument")).thenReturn(priceList);
        mockMvc.perform(get("/prices/instrument/{instrument}", "Instrument")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].instrumentName").value("Instrument"));
    }
    @Test
    public void test_getPricesByInstrument_missingPathVariable() throws Exception {
        mockMvc.perform(get("/prices/instrument/"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void test_getPricesByInstrument_incorrectPathVariable() throws Exception {
        when(priceService.pricesByInstrument("test")).thenThrow(new InstrumentDataNotFoundException("test"));
        mockMvc.perform(get("/prices/instrument/{instrument}", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
