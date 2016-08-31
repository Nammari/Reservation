package nammari.reservation.ui.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import nammari.reservation.R;
import nammari.reservation.api.Api;
import nammari.reservation.model.Customer;
import nammari.reservation.model.CustomerFields;
import nammari.reservation.ui.activity.TableReservationActivity;
import nammari.reservation.ui.adapter.CustomerAdapter;
import nammari.reservation.ui.controls.SimpleVerticalSpaceItemDecoration;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 *
 * didn't handle search for orientation change the have a bug in support library
 * https://code.google.com/p/android/issues/detail?id=219379&sort=-opened&colspec=ID%20Status%20Priority%20Owner%20Summary%20Stars%20Reporter%20Opened
 */

public class CustomerListFragment extends MultiStateRecyclerViewFragment implements CustomerAdapter.OnCustomerInteractionListener, RealmChangeListener<RealmResults<Customer>> {

    public static final String TAG = CustomerListFragment.class.getSimpleName();

    public CustomerListFragment() {
    }


    private CustomerAdapter adapter;
    private Realm realm;
    private Subscription customersSubscription;
    private String mQuery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new SimpleVerticalSpaceItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, getResources().getDisplayMetrics())));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new CustomerAdapter(LayoutInflater.from(getContext()), null, this);
        getRecyclerView().setAdapter(adapter);
        showLoading();
        downloadCustomers();
    }


    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
        //get all customers from database and display them in recyclerview asynchronously
        RealmResults<Customer> customers;
        if (TextUtils.isEmpty(mQuery)) {
            customers = getAllCustomers();
        } else {
            customers = searchForCustomer(mQuery);
        }
        // Tell Realm to notify our listener when the customers results
        // have changed (items added, removed, updated, anything of the sort).
        customers.addChangeListener(this);
        adapter.setData(customers);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.customers_menu, menu);
        setupSearchView(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void setupSearchView(Menu menu) {
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Activity.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(() -> {
            mQuery = null;
            return false;
        });

        if (!TextUtils.isEmpty(mQuery)) {
            MenuItemCompat.expandActionView(searchMenuItem);
            searchView.clearFocus();
            searchView.setQuery(mQuery, true);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter.getData() != null) {
            adapter.getData().removeChangeListener(this);
        }
        realm.close();
        if (customersSubscription != null && !customersSubscription.isUnsubscribed()) {
            customersSubscription.unsubscribe();
        }
    }

    @Override
    void onRetryButtonClicked() {
        downloadCustomers();
    }

    @Override
    public void onCustomerItemClicked(Customer customer) {
        startActivity(TableReservationActivity.createIntent(getActivity(), customer.getId()));
    }

    @Override
    public void onChange(RealmResults<Customer> element) {

        if (element.isEmpty()) {
            if (customersSubscription == null || customersSubscription.isUnsubscribed()) {
                showEmpty(getString(TextUtils.isEmpty(mQuery) ? R.string.empty : R.string.empty_search));
            }
        } else {
            showMainView();
        }
        adapter.setData(element);
        adapter.notifyDataSetChanged();
    }

    private void downloadCustomers() {
        customersSubscription =
                Api.downloadCustomers()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(aBoolean -> {
                            if (aBoolean != null && aBoolean) {
                                showMainView();
                            } else {
                                if (adapter.getItemCount() == 0) {
                                    showError(null);
                                }
                            }
                        }, throwable -> {
                            if (adapter.getItemCount() == 0) {
                                showError(throwable);
                            }
                        });
    }


    private void search(String term) {

        if (!term.equals(mQuery)) {
            mQuery = term;
            RealmResults<Customer> customers;
            if (TextUtils.isEmpty(mQuery)) {
                customers = getAllCustomers();
            } else {
                customers = searchForCustomer(mQuery);
            }
            customers.addChangeListener(this);
            adapter.setData(customers);
            adapter.notifyDataSetChanged();
        }
    }


    private RealmResults<Customer> getAllCustomers() {
        return realm.where(Customer.class).findAllAsync();
    }

    private RealmResults<Customer> searchForCustomer(String term) {
        return realm.where(Customer.class).contains(CustomerFields.FIRST_NAME, term, Case.INSENSITIVE).or().contains(CustomerFields.LAST_NAME, term, Case.INSENSITIVE).findAllAsync();
    }
}
