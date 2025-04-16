package com.edsonj.flagexplorer.model;

import lombok.Data;

import java.util.List;

@Data
public class CountryApiResponse {
    private Name name;
    private List<String> capital;
    private int population;
    private Flags flags;


    @Data
    public static class Name {
        private String common;
    }

    @Data
    public static class Flags {
        private String png;
    }
}
