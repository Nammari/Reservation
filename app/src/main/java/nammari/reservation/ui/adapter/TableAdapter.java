package nammari.reservation.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import nammari.reservation.R;
import nammari.reservation.model.Table;

/**
 * Created by nammari on 8/30/16.
 * email : nammariahmad@gmail.com
 * phone : +962798939560
 */

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private RealmResults<Table> data;
    private final long userId;
    private OnTableInteractionListener mOnTableInteractionListener;


    public TableAdapter(LayoutInflater inflater, RealmResults<Table> data, long userId, OnTableInteractionListener mOnTableInteractionListener) {
        this.inflater = inflater;
        this.data = data;
        this.mOnTableInteractionListener = mOnTableInteractionListener;
        this.userId = userId;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.adapter_table_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Table item = data.get(position);
        //table number
        holder.tableNumber.setText(String.format(Locale.US, "%d", item.getPosition()));

        //background
        if (item.isReserved()) {
            if (item.getUserId() != null && userId == item.getUserId()) {
                holder.itemView.setBackgroundResource(R.drawable.reserved_for_current_user_table_background);
            } else {
                holder.itemView.setBackgroundResource(R.drawable.reserved_table_background);
            }
        } else {
            holder.itemView.setBackgroundResource(R.drawable.unreserved_table_background);
        }

        holder.itemView.setOnClickListener(view -> {
            if (mOnTableInteractionListener != null) {
                mOnTableInteractionListener.onTableClicked(item);
            }
        });

    }


    public void setData(RealmResults<Table> data) {
        this.data = data;
    }

    public RealmResults<Table> getData() {
        return this.data;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public interface OnTableInteractionListener {
        void onTableClicked(Table table);
    }


    public List<Table> getTables() {
        return data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.table_number)
        TextView tableNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
