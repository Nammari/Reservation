package nammari.reservation.api;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.realm.Realm;
import nammari.reservation.Constants;
import nammari.reservation.model.Customer;
import nammari.reservation.model.Table;
import nammari.reservation.util.HttpUtils;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */

public class Api {

    public static Observable<Boolean> downloadCustomers() {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                InputStream inputStream = null;
                Realm realm = null;
                try {
                    //download customer list from amazon s3
                    inputStream = HttpUtils.executeGet(Constants.CUSTOMER_ENDPOINT);
                    List<Customer> customers = LoganSquare.parseList(inputStream, Customer.class);
                    //insert items to db
                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(customers);
                    realm.commitTransaction();
                    subscriber.onNext(Boolean.TRUE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    subscriber.onError(ex);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException ex) {
                            //ignore
                        }
                    }
                    if (realm != null) {
                        realm.close();
                    }
                }
                subscriber.onCompleted();
            }
        });
    }


    public static Observable<Boolean> downloadTables() {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                InputStream inputStream = null;
                Realm realm = null;
                try {
                    //download customer list from s3
                    inputStream = HttpUtils.executeGet(Constants.TABLES_ENDPOINT);
                    List<Boolean> tables = LoganSquare.parseList(inputStream, Boolean.class);
                    //insert items to db
                    final int size = tables.size();
                    if (size > 0) {
                        realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        for (int i = 0; i < size; i++) {
                            Table table = realm.createObject(Table.class);
                            table.setPosition(i + 1);
                            table.setReserved(tables.get(i));
                        }
                        realm.commitTransaction();
                    }
                    subscriber.onNext(Boolean.TRUE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    subscriber.onError(ex);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException ex) {
                            //ignore
                        }
                    }
                    if (realm != null) {
                        realm.close();
                    }
                }
                subscriber.onCompleted();
            }
        });
    }


}
