package nammari.reservation.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import nammari.reservation.ui.fragment.CustomerListFragment;

public class CustomersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new CustomerListFragment(), CustomerListFragment.TAG)
                    .commit();
        }
    }

}
