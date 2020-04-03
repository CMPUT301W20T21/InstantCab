package com.example.instantcab;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RiderMapsActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LogActivity> rule =
            new ActivityTestRule<>(LogActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        solo.assertCurrentActivity("Wrong Activity", LogActivity.class);

        solo.enterText((EditText) solo.getView(R.id.logEmail), "rider@demo.com");

        solo.enterText((EditText) solo.getView(R.id.logPass), "12345678");

        solo.clickOnButton("Login");

        solo.assertCurrentActivity("Wrong Activity", RiderMapsActivity.class);


    }

    @Test
    public void checkSwitchActivity(){
        solo.clickOnText("Make a request");

        solo.assertCurrentActivity("Wrong Activity", EnterRouteActivity.class);
    }

    @Test
    public void checkRequestSwitchActivity(){
        solo.clickOnMenuItem("Current Request");

        solo.assertCurrentActivity("Wrong Activity", RiderRequest.class);
    }

    @Test
    public void checkProfileSwitchActivity(){
        solo.clickOnMenuItem("Profile");

        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
    }

    @Test
    public void checkLogOutSwitchActivity(){
        solo.clickOnMenuItem("Log Out");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
