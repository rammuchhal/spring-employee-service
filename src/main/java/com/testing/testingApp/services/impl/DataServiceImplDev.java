package com.testing.testingApp.services.impl;

import com.testing.testingApp.services.DataService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
public class DataServiceImplDev implements DataService {
    @Override
    public String getData() {
        return "Dev data";
    }
}
