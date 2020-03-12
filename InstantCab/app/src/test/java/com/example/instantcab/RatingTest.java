package com.example.instantcab;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RatingTest {

    private Rating mockRating(){
        return new Rating(5,4);
    }


    @Test
    public void testGetGood(){
        Rating rating = mockRating();

        assertEquals(5, rating.getGood());
    }

    @Test
    public void testGetBad(){
        Rating rating = mockRating();

        assertEquals(4, rating.getBad());
    }
}
