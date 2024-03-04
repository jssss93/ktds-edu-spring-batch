package com.example.batch_01.sample11;

import org.springframework.batch.core.ItemReadListener;

public class CustomItemReadListener implements ItemReadListener {
    private int count;

    @Override
    public void beforeRead() {
        count++;
        System.out.println("before reader : "+ count);
    }

    @Override
    public void afterRead(Object item) {
        System.out.println("after reader : "+ count);

    }

    @Override
    public void onReadError(Exception ex) {
        System.out.println("error reader : "+ count);

    }
}