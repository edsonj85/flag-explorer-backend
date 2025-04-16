package com.edsonj.flagexplorer.exceptions;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String name) {
        super("Country not found: %s".formatted(name) );
    }
}
