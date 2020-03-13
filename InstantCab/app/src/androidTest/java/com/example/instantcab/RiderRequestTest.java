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

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests the change in Activity once the Cancel_Request button is clicked
 * @author lijiangn
 */
public class RiderRequestTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<RiderRequest> rule =
            new ActivityTestRule<>(RiderRequest.class,true,true);

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

    // RiderMapsActivity isn't ready
    @Test
    public void checkCancel(){
        solo.assertCurrentActivity("Wrong Activity", RiderRequest.class);

        //solo.clickOnButton("CANCEL REQUEST");

        //solo.assertCurrentActivity("Wrong Activity", RiderMapsActivity.class);
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
