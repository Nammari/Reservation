package nammari.reservation;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nammari.reservation.ui.activity.CustomersActivity;
import nammari.reservation.util.RecyclerViewItemCountAssertion;
import nammari.reservation.util.RestServiceTestHelper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by nammari on 8/31/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */
@RunWith(AndroidJUnit4.class)
public class TestSearch {


    @Rule
    public ActivityTestRule<CustomersActivity> mActivityRule =
            new ActivityTestRule<>(CustomersActivity.class);
    private MockWebServer server;


    @Before
    /**
     * assign a mock server implementation to simulate api responses .
     */
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        String serverUrl = server.url("/").toString();
        Constants.CUSTOMER_ENDPOINT = serverUrl + "customers";
        Constants.TABLES_ENDPOINT = serverUrl + "tables";
    }

    @Test
    public void testSearch() throws Exception {

        String customerList = "customer_list.json";
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(InstrumentationRegistry.getInstrumentation().getContext(), customerList)));
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.action_search)).perform(ViewActions.click());
        onView(isAssignableFrom(EditText.class)).perform(ViewActions.typeText("mother"), ViewActions.pressKey(KeyEvent.KEYCODE_ENTER));

        onView(ViewMatchers.withId(R.id.recyclerivew)).check(new RecyclerViewItemCountAssertion(1));

    }



    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }
}
