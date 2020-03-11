package com.example.instantcab;

public class Request {
    private String email;
    private Double startLatitude;
    private Double startLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private String fare;
    private String status;
    private String startLocationName;
    private String destinationName;

    public Request(){}

    public Request(String email, Double startLatitude, Double startLongitude, Double destinationLatitude, Double destinationLongitude,
                    String fare, String status, String startLocationName, String destinationName){
        this.email = email;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.fare = fare;
        this. status = status;
        this.startLocationName = startLocationName;
        this.destinationName = destinationName;
    }

    public String getEmail() {
        return email;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public String getFare() {
        return fare;
    }

    public String getStatus() {
        return status;
    }

    public String getStartLocationName() {
        return startLocationName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
