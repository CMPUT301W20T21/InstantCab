package com.example.instantcab;

import org.junit.Test;
import org.junit.jupiter.api.TestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTest {
    private Request mockRequest(){
        return new Request("test@email.com", 50.0, 60.0, 55.0, 65.0, "12.0", "pending", "Hub Mall", "Cameron Library");
    }

    @Test
    public void testGetEmail(){
        Request request = mockRequest();

        assertEquals("test@email.com", request.getEmail());
    }

    @Test
    public void testGetStartLatitude(){
        Request request = mockRequest();

        assertEquals(50.0, (double)request.getStartLatitude());
    }

    @Test
    public void testGetStartLongitude(){
        Request request = mockRequest();

        assertEquals(60.0, (double)request.getStartLongitude());
    }

    @Test
    public void testGetDestinationLatitude(){
        Request request = mockRequest();

        assertEquals(55.0, (double)request.getDestinationLatitude());
    }

    @Test
    public void testGetDestinationLongitude(){
        Request request = mockRequest();

        assertEquals(65.0, (double)request.getDestinationLongitude());
    }

    @Test
    public void testGetFare(){
        Request request = mockRequest();

        assertEquals("12.0", request.getFare());
    }

    @Test
    public void testGetStatus(){
        Request request = mockRequest();

        assertEquals("pending", request.getStatus());
    }

    @Test
    public void testGetStartLocationName(){
        Request request = mockRequest();

        assertEquals("Hub Mall", request.getStartLocationName());
    }

    @Test
    public void testGetDestinationLocationName(){
        Request request = mockRequest();

        assertEquals("Cameron Library", request.getDestinationName());
    }

    @Test
    public void testSetStatus(){
        Request request = mockRequest();

        request.setStatus("Accepted");

        assertEquals("Accepted", request.getStatus());
    }

    @Test
    public void testGetDriver(){
        Request request = mockRequest();

        assertNull(request.getDriver());
    }

    @Test
    public void testSetDriver(){
        Request request = mockRequest();

        request.setDriver("Jane Doe");

        assertEquals("Jane Doe", request.getDriver());
    }
}
