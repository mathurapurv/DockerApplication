package com.apurv.controller;

import com.apurv.dao.entity.BuzzWord;
import com.apurv.dao.MongoDaoImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class StringInverterController {

    private final MongoDaoImpl mongoDao;

    public StringInverterController(MongoDaoImpl mongoDao) {
        this.mongoDao = mongoDao;
    }

    @GetMapping("/invert")
    public String invertString(@RequestParam String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        return new StringBuilder(input).reverse().toString();
    }

    @GetMapping("/saveBuzzword")
    public BuzzWord saveBuzzword(@RequestParam String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }

        String invertedValue = new StringBuilder(input).reverse().toString();

        BuzzWord buzzWord = new BuzzWord();
        buzzWord.setId(input);  // Using input as ID
        buzzWord.setValue(invertedValue);

        return mongoDao.saveBuzzWord(buzzWord);
    }
}
