package nammari.reservation.ui.fragment;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */

public interface OnReservationListener {


    void onReservationSucceed();


    void onRemoveReservationSucceed();


    void onError(Throwable throwable);

    void onReservationTaskComplete();


}
