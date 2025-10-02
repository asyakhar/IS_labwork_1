package com.example.web;

import com.example.model.StandardOfLiving;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.util.Arrays;
import java.util.List;

@Named("standardOfLivingBean")
@RequestScoped
public class LivingBean {
    public StandardOfLiving[] getValues() {
        return StandardOfLiving.values();
    }
}

