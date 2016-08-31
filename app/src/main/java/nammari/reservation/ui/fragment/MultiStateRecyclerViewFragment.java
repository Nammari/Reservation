package nammari.reservation.ui.fragment;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import nammari.reservation.R;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */

public abstract class MultiStateRecyclerViewFragment extends Fragment {


    public static final String TAG = MultiStateRecyclerViewFragment.class.getSimpleName();


    @IntDef({STATE_MAIN_VIEW, STATE_ERROR_VIEW, STATE_LOADING_VIEW, STATE_EMPTY_VIEW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ViewState {
    }

    public static final int STATE_MAIN_VIEW = 0;
    public static final int STATE_ERROR_VIEW = 1;
    public static final int STATE_LOADING_VIEW = 2;
    public static final int STATE_EMPTY_VIEW = 3;

    public MultiStateRecyclerViewFragment() {
    }


    @BindView(R.id.loading_container)
    View loadingView;
    @BindView(R.id.error_container)
    View errorView;
    @BindView(R.id.empty)
    TextView emptyView;
    @BindView(R.id.recyclerivew)
    RecyclerView recyclerView;

    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multi_state_recyclerview, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public void showLoading() {
        setCurrentViewState(STATE_LOADING_VIEW);
    }


    public void showError(Throwable t) {
        setCurrentViewState(STATE_ERROR_VIEW);
    }


    public void showMainView() {
        setCurrentViewState(STATE_MAIN_VIEW);
    }

    public void showEmpty(String text) {
        emptyView.setText(text);
        setCurrentViewState(STATE_EMPTY_VIEW);
    }

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    private void setCurrentViewState(@ViewState int state) {
        recyclerView.setVisibility(state == STATE_MAIN_VIEW ? View.VISIBLE : View.GONE);
        errorView.setVisibility(state == STATE_ERROR_VIEW ? View.VISIBLE : View.GONE);
        loadingView.setVisibility(state == STATE_LOADING_VIEW ? View.VISIBLE : View.GONE);
        emptyView.setVisibility(state == STATE_EMPTY_VIEW ? View.VISIBLE : View.GONE);
    }

    abstract void onRetryButtonClicked();

    @OnClick({R.id.retry})
    public void onClick(View view) {
        if (view.getId() == R.id.retry) {
            onRetryButtonClicked();
        }
    }
}
