
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

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * This is the test doc for the profile object
 * @author kbojakli
 */

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
