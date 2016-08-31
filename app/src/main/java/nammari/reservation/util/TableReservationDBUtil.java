package nammari.reservation.util;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import nammari.reservation.model.Customer;
import nammari.reservation.model.Table;
import nammari.reservation.model.TableFields;
import nammari.reservation.ui.fragment.OnReservationListener;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */

public class TableReservationDBUtil {

    /**
     * Remove reservation for table at position and update UI with snack bar
     *
     * @param realm                 realm instance
     * @param position              position to remove reservation from
     * @param onReservationListener to be used to update UI for user
     * @return RealmAsyncTask : to be canceled when needed to prevent memory leaks
     */
    public static RealmAsyncTask removeReservationAtTablePosition(Realm realm, final long position, final OnReservationListener onReservationListener) {

        return realm.executeTransactionAsync(realm1 -> {
            Table tableToRemoveReservation = realm1.where(Table.class).equalTo(TableFields.POSITION, position).findFirst();
            tableToRemoveReservation.setReserved(false);
            tableToRemoveReservation.setUserId(Customer.NO_CUSTOMER_ID);
        }, () -> {
            if (onReservationListener != null) {
                onReservationListener.onRemoveReservationSucceed();
                onReservationListener.onReservationTaskComplete();
            }
        }, error -> {
            if (onReservationListener != null) {
                onReservationListener.onError(error);
                onReservationListener.onReservationTaskComplete();
            }
        });
    }

    /**
     * Reserve table at position for user id and remove previous reserved table for that user .
     *
     * @param realm                       realm instance
     * @param currentUserId               current user id to reserve table for
     * @param positionToReserve           table position to reserve
     * @param positionToRemoveReservation table position to remove reservation
     * @param onReservationListener       to be used to update UI for user
     * @return
     */
    public static RealmAsyncTask reserveTableForCustomerAndRemovePreviousReservedTable(Realm realm, final long currentUserId, final long positionToReserve, final long positionToRemoveReservation, final OnReservationListener onReservationListener) {

        return realm.executeTransactionAsync(realm1 -> {
            if (positionToRemoveReservation != Customer.NO_CUSTOMER_ID) {
                Table tableToRemoveReservation = realm1.where(Table.class).equalTo(TableFields.POSITION, positionToRemoveReservation).findFirst();
                tableToRemoveReservation.setReserved(false);
                tableToRemoveReservation.setUserId(Customer.NO_CUSTOMER_ID);
            }
            Table tableToReserve = realm1.where(Table.class).equalTo(TableFields.POSITION, positionToReserve).findFirst();
            tableToReserve.setReserved(true);
            tableToReserve.setUserId(currentUserId);
        }, () -> {
            if (onReservationListener != null) {
                onReservationListener.onReservationSucceed();
                onReservationListener.onReservationTaskComplete();
            }
        }, error -> {
            if (onReservationListener != null) {
                onReservationListener.onError(error);
                onReservationListener.onReservationTaskComplete();
            }
        });
    }


}
