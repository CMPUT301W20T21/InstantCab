/**Copyright 2020 CMPUT301W20T21

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.*/

package com.example.instantcab;

/**
 * Request object to store information of a request and is used in firebase
 *
 * @author lshang
 */
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
    private String driver;
    private String driverName;
    private String riderName;


    public Request(){}

    /**
     *
     * @param email
     * @param startLatitude
     * @param startLongitude
     * @param destinationLatitude
     * @param destinationLongitude
     * @param fare
     * @param status
     * @param startLocationName
     * @param destinationName
     */
    public Request(String email, Double startLatitude, Double startLongitude, Double destinationLatitude, Double destinationLongitude,
                    String fare, String status, String startLocationName, String destinationName, String riderName){
        this.email = email;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.fare = fare;

        this.status = status;
        this.startLocationName = startLocationName;
        this.destinationName = destinationName;
        this.driver = null;
        this.driverName = null;
        this.riderName = riderName;

    }

    public Request(String email, Double startLatitude, Double startLongitude, Double destinationLatitude, Double destinationLongitude,
                   String fare, String status, String startLocationName, String destinationName, String driver, String driverName, String riderName){
        this.email = email;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.fare = fare;

        this.status = status;
        this.startLocationName = startLocationName;
        this.destinationName = destinationName;
        this.driver = driver;
        this.driverName = driverName;
        this.riderName = riderName;

    }

    /**
     *
     * @return request's rider email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @return start location latitude
     */
    public Double getStartLatitude() {
        return startLatitude;
    }

    /**
     *
     * @return start location longitude
     */
    public Double getStartLongitude() {
        return startLongitude;
    }

    /**
     *
     * @return destination latitude
     */
    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    /**
     *
     * @return destination longitude
     */
    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    /**
     *
     * @return fare
     */
    public String getFare() {
        return fare;
    }

    /**
     *
     * @return request status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @return start location address
     */
    public String getStartLocationName() {
        return startLocationName;
    }

    /**
     *
     * @return destination address
     */
    public String getDestinationName() {
        return destinationName;
    }

    /**
     *
     * @return email of driver who accepted the request
     */
    public String getDriver() { return driver; }

    /**
     *
     * @return driver's username
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     *
     * @return rider's username
     */
    public String getRiderName() {
        return riderName;
    }

    /**
     * set request status
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * set request driver's email
     * @param email
     */
    public void setDriver(String email){
        this.driver = email;
    }

    /**
     * set request driver's username
     * @param driverName
     */
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}

