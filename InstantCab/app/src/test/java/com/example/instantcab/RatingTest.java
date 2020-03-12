
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
 * This is the test class for the Rating object
 * @author kbojakli
 */

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
