package nammari.reservation.schedule;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;

import io.realm.Realm;
import io.realm.RealmResults;
import nammari.reservation.model.Customer;
import nammari.reservation.model.Table;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 * <p>
 * <p>
 * Remove all reservation for all tables in db task
 */

public class RemoveReservationsJob extends Job {


    public static final String TAG = RemoveReservationsJob.class.getSimpleName();

    /**
     * @param params
     * @return
     */
    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Realm realm = Realm.getDefaultInstance();
        try {
            RealmResults<Table> tableRealmResults = realm.where(Table.class).findAll();
            if (!tableRealmResults.isEmpty()) {
                final int size = tableRealmResults.size();
                realm.beginTransaction();
                for (int i = 0; i < size; i++) {
                    tableRealmResults.get(i).setReserved(false);
                    tableRealmResults.get(i).setUserId(Customer.NO_CUSTOMER_ID);
                }
                realm.commitTransaction();
            }
        } finally {
            realm.close();
        }
        return Result.SUCCESS;
    }
}
