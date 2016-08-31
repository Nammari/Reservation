package nammari.reservation.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import nammari.reservation.R;
import nammari.reservation.model.Customer;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {


    private LayoutInflater inflater;
    private RealmResults<Customer> data;
    private OnCustomerInteractionListener mOnCustomerInteractionListener;
    private final StringBuilder builder = new StringBuilder(128);

    public CustomerAdapter(LayoutInflater inflater, RealmResults<Customer> data, OnCustomerInteractionListener mOnCustomerInteractionListener) {
        this.inflater = inflater;
        this.data = data;
        this.mOnCustomerInteractionListener = mOnCustomerInteractionListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.adapter_customer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Customer item = data.get(position);
        builder.setLength(0);
        //id
        builder.append("#").append(item.getId());
        holder.customerId.setText(builder.toString());

        //name
        builder.setLength(0);

        if (item.getFirstName() != null) {
            builder.append(item.getFirstName());
            builder.append(" ");
        }

        if (item.getLastName() != null) {
            builder.append(item.getLastName());
        }

        holder.customerName.setText(builder.toString());

        holder.itemView.setOnClickListener(view -> {
            if (mOnCustomerInteractionListener != null) {
                mOnCustomerInteractionListener.onCustomerItemClicked(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public interface OnCustomerInteractionListener {
        void onCustomerItemClicked(Customer customer);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.customer_id)
        TextView customerId;
        @BindView(R.id.customer_name)
        TextView customerName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public void setData(RealmResults<Customer> data) {
        this.data = data;
    }

    public RealmResults<Customer> getData() {
        return this.data;
    }


}
