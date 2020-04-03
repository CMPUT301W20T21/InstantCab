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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a Check and Flag class that checks if the Strings inputted for profiles are valid
 * @author kbojakli
 */
public class CheckFlag {

    /**
     * Checks if email is in correct format
     * @param email
     * @return
     */
    //Email check is used with assistance of https://www.geeksforgeeks.org/check-email-address-valid-not-java/
    public static boolean isEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return Boolean.FALSE;
        return pat.matcher(email).matches();
    }

    /**
     * Checks if password is greater than six digits
     * @param password
     * @return
     */
    public static boolean isPassword(String password){
        int count = 0;

        for(int i = 0; i < password.length(); i++){
            if(password.charAt(i) != ' '){
                count++;

            }
        }

        if( count <= 6){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * Check if phone number is in correct format ###-###-####
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
        Matcher matcher = pattern.matcher(phone);

        if (matcher.matches()) {
            return Boolean.TRUE;
        }
        else {
            return Boolean.FALSE;
        }
    }
}
