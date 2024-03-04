package com.example.batch_01.sample13;


import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
public class CustomItemReader implements ItemReader<String> {
    int i = 0;
    @Override
    public String read() throws CustomException {
        i++;
        if (i%3==0){
            throw new CustomException("skip exception");
        }
        System.out.println("itemReader : " + i);
        return i > 20 ? null : String.valueOf(i);
    }
}
