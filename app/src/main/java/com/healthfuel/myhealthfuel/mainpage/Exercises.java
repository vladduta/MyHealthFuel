package com.healthfuel.myhealthfuel.mainpage;

public class Exercises {
    String name, type;
    int image, gif, MET;


    public Exercises(String name, String type, int image, int gif, int MET) {
        this.name = name;
        this.type = type;
        this.image = image;
        this.gif = gif;
        this.MET = MET;
    }


    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public int getImage() {
        return image;
    }
    public int getGif() {
        return gif;
    }
    public int getMET() {return MET;}


}
