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
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Checks the AcceptRequestTest to see if the TextView fields are modified with data
 */
public class AcceptRequestTest {

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
     * Checks if the TextView changes to corresponding information of a marker
     */
    @Test
    public void checkTextViewChange(){

        solo.enterText(((EditText) solo.getView(R.id.from)), "Hub Mall");
        solo.enterText(((EditText) solo.getView(R.id.to)),"Southgate Mall");
        solo.enterText(((EditText) solo.getView(R.id.username)), "John Doe");
        solo.enterText(((EditText) solo.getView(R.id.fare)), "$15.00");

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
