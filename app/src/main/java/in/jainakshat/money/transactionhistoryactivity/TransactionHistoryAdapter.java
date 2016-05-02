package in.jainakshat.money.transactionhistoryactivity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.zip.Inflater;

import in.jainakshat.money.R;

/**
 * Created by Akshat on 5/3/2016.
 */
public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyViewHolder> {

    private ArrayList<String> mData;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(ArrayList<String> data) {
        this.mData = data;
    }

}
