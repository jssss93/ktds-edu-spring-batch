package com.example.batch_01.sample20;

import com.example.batch_01.sample16.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.retry.support.RetryTemplate;

@RequiredArgsConstructor
public class CustomItemProcessor_20 implements ItemProcessor<String, String> {
    
    private final RetryTemplate retryTemplate;

    @Override
    public String process(String item) throws Exception {
        String result = retryTemplate.execute(context -> {
            if (item.equals("1") || item.equals("3")){
                throw new RetryableException("retry");
            }
            System.out.println("itemProcessor : " + item);
            return item;
        },
        context -> {
            System.out.println("recover : "+ item);
            return item; // 정상을 뱉어버려서 아무 이상없이 그대로 진행됨
        });
        return result;
    }
}