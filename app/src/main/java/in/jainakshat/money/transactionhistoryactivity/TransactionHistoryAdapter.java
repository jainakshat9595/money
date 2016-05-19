package in.jainakshat.money.transactionhistoryactivity;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import in.jainakshat.money.R;
import in.jainakshat.money.db.models.HistoryModel;

/**
 * Created by Akshat on 5/3/2016.
 */
public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyViewHolder> {

    private static String MONTH[]={"January", "February", "March", "April", "May", "june", "July", "August", "September", "October", "November", "December"};

    private Context mContext;
    private ArrayList<HistoryModel> mData;

    public TransactionHistoryAdapter(Context context) { this.mContext = context; }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView_th_row_date;
        private TextView mTextView_th_row_amount;
        private TextView mTextView_th_row_tag;
        private TextView mTextView_th_row_description;

        public MyViewHolder(View itemView) {
            super(itemView);
            Typeface karla_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Karla-Regular.ttf");
            mTextView_th_row_date = (TextView) itemView.findViewById(R.id.th_row_date);
            mTextView_th_row_amount = (TextView) itemView.findViewById(R.id.th_row_amount);
            mTextView_th_row_tag = (TextView) itemView.findViewById(R.id.th_row_tag);
            mTextView_th_row_description = (TextView) itemView.findViewById(R.id.th_row_description);
            mTextView_th_row_date.setTypeface(karla_font);
            mTextView_th_row_amount.setTypeface(karla_font);
            mTextView_th_row_tag.setTypeface(karla_font);
            mTextView_th_row_description.setTypeface(karla_font);
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
        holder.mTextView_th_row_amount.setText(mData.get(position).getAmount());
        String action = mData.get(position).getAction();
        if(action.equals("Take")) {
            holder.mTextView_th_row_tag.setText("He/She took from you!");
        }
        else if(action.equals("Give")) {
            holder.mTextView_th_row_tag.setText("He/She gave you!");
        }
        else if(action.equals("Settle")) {
            holder.mTextView_th_row_tag.setText("Amount settled!");
        }
        holder.mTextView_th_row_date.setText(""+MONTH[Integer.parseInt(mData.get(position).getMonth())]+" "+mData.get(position).getDate()+", "+(Integer.parseInt(mData.get(position).getYear())+1900));
        holder.mTextView_th_row_description.setText(mData.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(ArrayList<HistoryModel> data) {
        this.mData = data;
    }

}
