package com.example.instantcab;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ProfileTest {

    private Profile mockProfile(){
        return new Profile("user@email.com", "User", "999-999-9999", "Driver" );
    }

    @Test
    public void testGetEmail(){
        Profile profile = mockProfile();

        assertEquals("user@email.com", profile.getEmail());
    }

    @Test
    public void testGetUsername(){
        Profile profile = mockProfile();

        assertEquals("User", profile.getUsername());
    }

    @Test
    public void testGetPhone(){
        Profile profile = mockProfile();

        assertEquals("999-999-9999", profile.getPhone());
    }

    @Test
    public void testGetType(){
        Profile profile = mockProfile();

        assertEquals("Driver", profile.getType());
    }

}
