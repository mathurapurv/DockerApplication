package com.apurv.dao;

import com.apurv.dao.entity.BuzzWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MongoDaoImpl {

    private final BuzzWordRepository buzzWordRepository;

    @Autowired
    public MongoDaoImpl(BuzzWordRepository buzzWordRepository) {
        this.buzzWordRepository = buzzWordRepository;
    }

    // Save or update a buzzword
    public BuzzWord saveBuzzWord(BuzzWord buzzWord) {
        return buzzWordRepository.save(buzzWord);
    }

    // Find a buzzword by ID
    public Optional<BuzzWord> findById(String id) {
        return buzzWordRepository.findById(id);
    }
    
    // Find a buzzword by its exact value
    public Optional<BuzzWord> findByValue(String value) {
        return buzzWordRepository.findByValue(value);
    }

    // Find all buzzwords
    public List<BuzzWord> findAll() {
        return buzzWordRepository.findAll();
    }

    // Search buzzwords containing text (case-insensitive)
    public List<BuzzWord> search(String text) {
        return buzzWordRepository.findByValueContainingIgnoreCase(text);
    }

    // Check if a buzzword with the given value exists
    public boolean existsByValue(String value) {
        return buzzWordRepository.existsByValue(value);
    }

    // Delete a buzzword by ID
    public void deleteById(String id) {
        buzzWordRepository.deleteById(id);
    }

    // Check if a buzzword exists by ID
    public boolean existsById(String id) {
        return buzzWordRepository.existsById(id);
    }
}
