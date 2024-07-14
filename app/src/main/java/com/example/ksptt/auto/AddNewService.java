package com.example.ksptt.auto;

import androidx.annotation.NonNull;

import com.google.auto.service.AutoService;

@AutoService({TaskService.class, TestService.class})
public class AddNewService implements TaskService, TestService {
    @Override
    public void action(@NonNull String tag) {

    }

    @Override
    public void test(@NonNull String msg) {

    }
}
