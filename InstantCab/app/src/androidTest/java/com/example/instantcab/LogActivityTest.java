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

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Checks the LogActivity to see if they switch to the correct Activity for each type of user
 * @author kbojakli
 */
public class LogActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<LogActivity> rule =
            new ActivityTestRule<>(LogActivity.class,true,true);

    /**
     * Runs before all tests and creates an instance
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * Checks if the user changes to the rider homepage if they are a rider class
     */
    @Test
    public void checkRiderChange(){
        solo.assertCurrentActivity("Wrong Activity", LogActivity.class);

        solo.enterText(((EditText) solo.getView(R.id.logEmail)), "rider@test.com");
        solo.enterText(((EditText) solo.getView(R.id.logPass)), "123456");

        Button log = (Button) solo.getView(R.id.logButton);
        solo.clickOnView(log);

        solo.assertCurrentActivity("Wrong Activity", RiderMapsActivity.class);
    }

    /**
     * Checks if the user switches to the driver homepage if they are of the driver class
     */
    @Test
    public void checkDriverChange(){
        solo.assertCurrentActivity("Wrong Activity", LogActivity.class);

        solo.enterText(((EditText) solo.getView(R.id.logEmail)), "driver@test.com");
        solo.enterText(((EditText) solo.getView(R.id.logPass)), "123456");

        Button log = (Button) solo.getView(R.id.logButton);
        solo.clickOnView(log);

        solo.assertCurrentActivity("Wrong Activity", DriverHomeActivity.class);
    }

    /**
     * Closes Activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
