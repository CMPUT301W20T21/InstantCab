package com.example.instantcab;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EnterRouteActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<EnterRouteActivity> rule =
            new ActivityTestRule<>(EnterRouteActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        solo.assertCurrentActivity("Wrong Activity", EnterRouteActivity.class);

        /*
        Cannot implement, this is how it should go
        solo.clickOnMap();
        solo.clickOnButton("DESTINATION");
        solo.clickOnButton("NEXT");
         */

    }

    /* not actually gonna test
    @Test
    public void checkSwitchActivity(){
        solo.assertCurrentActivity("Wrong Activity", PreviewRequestActivity.class);
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
