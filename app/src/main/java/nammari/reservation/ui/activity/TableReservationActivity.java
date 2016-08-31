package nammari.reservation.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import nammari.reservation.model.Customer;
import nammari.reservation.ui.fragment.TableGridFragment;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */

public class TableReservationActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, TableGridFragment.newInstance(getIntent().getLongExtra(TableGridFragment.ARG_CURRENT_USER_ID, Customer.NO_CUSTOMER_ID)), TableGridFragment.TAG)
                    .commit();
        }
    }

    public static Intent createIntent(Context context, long userId) {
        Intent intent = new Intent(context, TableReservationActivity.class);
        intent.putExtra(TableGridFragment.ARG_CURRENT_USER_ID, userId);
        return intent;
    }
}
