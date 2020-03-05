package com.example.instantcab;

public class Profile {
    private String email;
    private String username;
    private String phone;
    private String type;

    public Profile() {}

    public Profile(String email, String username, String phone, String type){

    }

    public String getEmail(){
        return email;
    }
    public String getUsername(){
        return username;
    }
    public String getPhone(){
        return phone;
    }
    public String getType(){
        return type;
    }
}
