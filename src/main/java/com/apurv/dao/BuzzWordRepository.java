package com.apurv.dao;

import com.apurv.dao.entity.BuzzWord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuzzWordRepository extends MongoRepository<BuzzWord, String> {
    
    // Find a buzzword by its exact value (case-sensitive)
    Optional<BuzzWord> findByValue(String value);
    
    // Find buzzwords containing the given text (case-insensitive)
    List<BuzzWord> findByValueContainingIgnoreCase(String valueFragment);
    
    // Check if a buzzword with the given value exists (case-sensitive)
    boolean existsByValue(String value);
}
