package nammari.reservation;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.realm.Realm;
import nammari.reservation.model.Table;
import nammari.reservation.model.TableFields;
import nammari.reservation.ui.activity.CustomersActivity;
import nammari.reservation.util.RestServiceTestHelper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by nammari on 8/31/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */
@RunWith(AndroidJUnit4.class)
public class TestReservation {


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
    public void testReservation() throws Exception {
        String customerList = "customer_list.json";
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(InstrumentationRegistry.getInstrumentation().getContext(), customerList)));
        String tables = "tables.json";
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(InstrumentationRegistry.getInstrumentation().getContext(), tables)));


        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);


        onView(withId(R.id.recyclerivew)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));


        onView(withId(R.id.recyclerivew)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        Realm realm = Realm.getDefaultInstance();
        List<Table> result = realm.where(Table.class).equalTo(TableFields.POSITION, 1).findAll();
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        Table firstTable = result.get(0);
        assertThat(firstTable.isReserved()).isTrue();
        realm.close();
    }


    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }
}
