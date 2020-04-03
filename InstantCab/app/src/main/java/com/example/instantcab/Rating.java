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


 * This collection is used to create a easy goto collection for the firebase database
 * @author kbojakli
 */

/**

 * Database collections were created with the assistance of the firebase tutorial
 * https://firebase.google.com/docs/firestore/manage-data/add-data#custom_objects
 */

//Ask about the difference between int and Integer and which one should be used in this case
public class Rating {
    private int Good;
    private int Bad;

    public Rating(){}

    public Rating(int good, int bad){
        this.Good = good;
        this.Bad = bad;

    }

    public int getGood(){
        return Good;
    }

    public int getBad(){
        return Bad;
    }

    public void setBad(int bad) {
        Bad = bad;
    }

    public void setGood(int good) {
        Good = good;
    }
}
