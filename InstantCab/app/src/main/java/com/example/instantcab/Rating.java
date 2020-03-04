package com.example.instantcab;

public class Rating {
    private Integer Up;
    private Integer Down;

    public Rating(){}

    public Rating(Integer Up, Integer Down){
        this.Up = Up;
        this.Down = Down;

    }

    public Integer getUp(){
        return Up;
    }

    public Integer getDown(){
        return Down;
    }

}
