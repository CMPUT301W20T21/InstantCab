package com.example.instantcab;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class PreviewRequestActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<PreviewRequestActivity> rule =
            new ActivityTestRule<>(PreviewRequestActivity.class, true, true);

    /**
     //     * Runs before all tests and creates solo instance
     //     * @throws Exception
     //     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        solo.assertCurrentActivity("Wrong Activity", PreviewRequestActivity.class);

        solo.clickOnButton("SEND REQUEST");
    }

    /*Gonna cause trouble in the next activity and give errors because cannot get current user
    But this is how it should go
    @Test
    public void checkSwitchActivity(){
        solo.assertCurrentActivity("Wrong Activity", RiderRequest.class);
    }
     */

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}

