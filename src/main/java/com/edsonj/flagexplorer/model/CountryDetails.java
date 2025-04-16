package com.edsonj.flagexplorer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryDetails {
    private String name;
    private int population;
    private String capital;
    private String flag;
}
