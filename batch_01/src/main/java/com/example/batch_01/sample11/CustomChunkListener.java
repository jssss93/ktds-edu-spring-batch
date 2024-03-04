package com.example.batch_01.sample11;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

public class CustomChunkListener implements ChunkListener {
    private int count;

    @Override
    public void beforeChunk(ChunkContext context) {
        count++;
        System.out.println("before chunk : "+ count);
    }

    @Override
    public void afterChunk(ChunkContext context) {
        System.out.println("after chunk : "+ count);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        System.out.println("error chunk : "+ count);
    }
}