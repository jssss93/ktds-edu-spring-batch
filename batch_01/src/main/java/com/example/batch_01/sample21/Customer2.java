package com.example.batch_01.sample21;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Customer2 {
    @Id
    private long id;
    private String name;
    private String age;

}
