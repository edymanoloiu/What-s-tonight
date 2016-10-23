package com.example.Edi.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class City {
    @com.googlecode.objectify.annotation.Id
    private Long Id;
    private String name;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
