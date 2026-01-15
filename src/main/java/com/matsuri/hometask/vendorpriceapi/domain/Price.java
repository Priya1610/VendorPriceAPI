package com.matsuri.hometask.vendorpriceapi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    @NonNull
    private String vendorName;
    @NonNull
    private String instrumentName;
    private double price;
    @JsonIgnore
    private Instant createdAt;
}
