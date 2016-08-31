package nammari.reservation.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import nammari.reservation.R;
import nammari.reservation.api.Api;
import nammari.reservation.model.Customer;
import nammari.reservation.model.Table;
import nammari.reservation.model.TableFields;
import nammari.reservation.ui.adapter.TableAdapter;
import nammari.reservation.ui.controls.ItemDecorationAlbumColumns;
import nammari.reservation.util.TableReservationDBUtil;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */

public class TableGridFragment extends MultiStateRecyclerViewFragment implements TableAdapter.OnTableInteractionListener, RealmChangeListener<RealmResults<Table>>, OnReservationListener {


    public static final String TAG = TableGridFragment.class.getSimpleName();


    public static final String ARG_CURRENT_USER_ID = "arg_current_user_id";


    public static TableGridFragment newInstance(long currentUserId) {
        TableGridFragment fragment = new TableGridFragment();
        Bundle args = new Bundle(1);
        args.putLong(ARG_CURRENT_USER_ID, currentUserId);
        fragment.setArguments(args);
        return fragment;
    }


    public TableGridFragment() {
    }


    long currentUserId;
    TableAdapter adapter;
    Realm realm;
    Subscription tablesSubscription;
    RealmAsyncTask currentRealmTask = null;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        int gridSize = getResources().getInteger(R.integer.grid_size);
        getRecyclerView().setLayoutManager(new GridLayoutManager(getContext(), gridSize));
        getRecyclerView().addItemDecoration(new ItemDecorationAlbumColumns(getResources().getDimensionPixelSize(R.dimen.table_grid_spacing), gridSize));
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentUserId = getArguments().getLong(ARG_CURRENT_USER_ID);
        adapter = new TableAdapter(LayoutInflater.from(getContext()), null, currentUserId, this);
        getRecyclerView().setAdapter(adapter);
        showLoading();
    }

    @Override
    void onRetryButtonClicked() {
        downloadTables();
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
        //get all tables from database and display them in recyclerview asynchronously
        RealmResults<Table> tables = realm.where(Table.class).findAllSortedAsync(TableFields.POSITION);
        // Tell Realm to notify our listener when the customers results
        // have changed (items added, removed, updated, anything of the sort).
        tables.addChangeListener(this);
        adapter.setData(tables);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (adapter.getData() != null) {
            adapter.getData().removeChangeListener(this);
        }
        realm.close();
        if (tablesSubscription != null && !tablesSubscription.isUnsubscribed()) {
            tablesSubscription.unsubscribe();
        }
        if (currentRealmTask != null && !currentRealmTask.isCancelled()) {
            currentRealmTask.cancel();
            onReservationTaskComplete();
        }
    }

    //table reservation logic goes here
    @Override
    public void onTableClicked(final Table table) {
        //synchronized writes
        if (currentRealmTask != null) {
            return;
        }
        //is table reserved
        if (table.isReserved()) {
            //did the current user reserve it ?
            if (table.getUserId() != null && table.getUserId() == currentUserId) {
                //remove reservation
                final long tableToRemoveReservePosition = table.getPosition();
                currentRealmTask = TableReservationDBUtil.removeReservationAtTablePosition(realm, tableToRemoveReservePosition, this);
            } else {
                //another user reserve this table , he's the only one how can remove the reservation
                Snackbar.make(getRecyclerView(), R.string.cant_reserve_table, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            //available table, reserve it for the current user
            List<Table> tables = adapter.getTables();
            //did the current user reserve the other table ?
            Table previousSelectedTableForCurrentUser = null;
            for (Table item : tables) {
                if (item.getUserId() != null && currentUserId == item.getUserId()) {
                    previousSelectedTableForCurrentUser = item;
                    break;
                }
            }
            final long tableToReservePosition = table.getPosition();
            final long previousTablePosition = previousSelectedTableForCurrentUser == null ? Customer.NO_CUSTOMER_ID : previousSelectedTableForCurrentUser.getPosition();
            currentRealmTask = TableReservationDBUtil.reserveTableForCustomerAndRemovePreviousReservedTable(realm, currentUserId, tableToReservePosition, previousTablePosition, this);
        }
    }


    private void downloadTables() {
        tablesSubscription = Api.downloadTables()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(aBoolean -> {
                    if (aBoolean != null && aBoolean) {
                        showMainView();
                    } else {
                        showError(null);
                    }
                }, this::showError);
    }


    @Override
    public void onChange(RealmResults<Table> elements) {
        if (elements.isEmpty()) {
            //load from server
            downloadTables();
        } else {
            //set items from database in adapter
            adapter.setData(elements);
            adapter.notifyDataSetChanged();
            showMainView();
        }
    }


    @Override
    public void onReservationSucceed() {
        if (getRecyclerView() != null) {
            Snackbar.make(getRecyclerView(), R.string.reservation_success, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveReservationSucceed() {
        if (getRecyclerView() != null) {
            Snackbar.make(getRecyclerView(), R.string.reservation_removed, Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onError(Throwable throwable) {
        if (getRecyclerView() != null) {
            Snackbar.make(getRecyclerView(), R.string.error_text, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onReservationTaskComplete() {
        currentRealmTask = null;
    }
}
