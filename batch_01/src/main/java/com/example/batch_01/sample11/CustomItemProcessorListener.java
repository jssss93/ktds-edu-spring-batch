package com.example.batch_01.sample11;

import org.springframework.batch.core.ItemProcessListener;

import com.example.batch_01.sample01.User;

public class CustomItemProcessorListener implements ItemProcessListener<User, User> {
    private int count;

    @Override
    public void beforeProcess(User item) {
        count++;
        System.out.println("before processor : "+ count);
    }

    @Override
    public void afterProcess(User item, User result) {
        System.out.println("after processor : "+ count);

    }

    @Override
    public void onProcessError(User item, Exception e) {
        System.out.println("error processor : "+ count);

    }
}