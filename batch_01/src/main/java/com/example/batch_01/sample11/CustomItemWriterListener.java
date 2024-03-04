package com.example.batch_01.sample11;

import org.springframework.batch.core.ItemWriteListener;

import com.example.batch_01.sample01.User;

import java.util.List;

public class CustomItemWriterListener implements ItemWriteListener<User> {
    private int count;

    public void beforeWrite(List<? extends User> items) {
        count++;
        System.out.println("before writer : "+ count);
    }

    public void afterWrite(List<? extends User> items) {
        System.out.println("after writer : "+ count);

    }

    public void onWriteError(Exception exception, List<? extends User> items) {
        System.out.println("error writer : "+ count);

    }
}