package com.example.web;

import com.example.model.Climate;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Named("climateBean")
@RequestScoped
public class ClimateBean {

    public Climate[] getValues() {
        return Climate.values();
    }
}