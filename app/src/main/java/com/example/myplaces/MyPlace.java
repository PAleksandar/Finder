package com.example.myplaces;

public class MyPlace {
    String name;
    String description;

    public MyPlace(String nme, String desc)
    {
        this.name=nme;
        this.description=desc;
    }

    public MyPlace(String nme)
    {
        this(nme,"");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
