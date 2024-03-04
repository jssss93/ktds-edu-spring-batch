package com.example.batch_01.sample20;

import com.example.batch_01.sample16.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.classify.Classifier;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;

@RequiredArgsConstructor
public class CustomItemProcessor_20 implements ItemProcessor<String, String> {
    
    private final RetryTemplate retryTemplate;

    @Override
    public String process(String item) throws Exception {

        Classifier<Throwable,Boolean> rollbackClassifier = new BinaryExceptionClassifier(true);

        String result = retryTemplate.execute(new RetryCallback<String, RuntimeException>() {
                   @Override
                   public String doWithRetry(RetryContext context) throws RuntimeException {
                       if (item.equals("1") || item.equals("3")){
                           throw new RetryableException("retry");
                       }
                       System.out.println("itemProcessor : " + item);
                       return item;
                   }
               },
                new RecoveryCallback<String>() {
                    @Override
                    public String recover(RetryContext context) throws Exception {
                        System.out.println("recover : "+ item);
                        return item; // 정상을 뱉어버려서 아무 이상없이 그대로 진행됨
                    }
                });
        return result;
    }
}