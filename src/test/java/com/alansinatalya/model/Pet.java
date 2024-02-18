package com.alansinatalya.model;

import java.util.List;

public class Pet {
    private String type;
    private String name;
    private int age;
    private List<String> sound;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<String> getSound() {
        return sound;
    }
}
