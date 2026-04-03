package com.apurv.dao.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "buzzwords")
public class BuzzWord {
    @Id
    private String id;
    private String value;
    
    // Add any additional fields and methods as needed
}
