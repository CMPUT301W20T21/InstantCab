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

 * This Profile object is used to create the user profile to set in the firebase database
 * @author kbojakli
 */

/**

 * Database collections were created with the assistance of the firebase tutorial
 * https://firebase.google.com/docs/firestore/manage-data/add-data#custom_objects
 */
public class Profile {
    private String email;
    private String username;
    private String phone;
    private String type;

    public Profile() {}

    public Profile(String email, String username, String phone, String type){
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.type = type;
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
