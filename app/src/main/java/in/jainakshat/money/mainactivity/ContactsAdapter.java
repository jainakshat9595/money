package in.jainakshat.money.mainactivity;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.jainakshat.money.R;
import in.jainakshat.money.db.models.ContactModel;

/**
 * Created by Akshat on 4/28/2016.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    public interface ItemClickHandler {
        void OnItemClicked(ContactModel contactModel);
        void OnItemLongClicked(ContactModel contactModel);
    }

    private ItemClickHandler listener;

    private ArrayList<ContactModel> mData;
    private Context mContext;

    public ContactsAdapter(Context context) {
        this.mContext = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLinearLayout_contact_main_row;
        private TextView mTextView_name;
        private TextView mTextView_value;
        private RelativeLayout mRelativeLayout_value;
        private RelativeLayout mRelativeLayout_text;

        public MyViewHolder(View itemView) {
            super(itemView);
            mLinearLayout_contact_main_row = (LinearLayout) itemView.findViewById(R.id.contact_main_row);
            mTextView_name = (TextView) itemView.findViewById(R.id.contact_name);
            mTextView_value = (TextView) itemView.findViewById(R.id.contact_value);
            mRelativeLayout_value = (RelativeLayout) itemView.findViewById(R.id.contact_value_outer_view);
            mRelativeLayout_text = (RelativeLayout) itemView.findViewById(R.id.contact_text_outer_view);
            Typeface karla_font = Typeface.createFromAsset(mContext.getAssets(), "fonts/Karla-Regular.ttf");
            mTextView_name.setTypeface(karla_font);
            mTextView_value.setTypeface(karla_font);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.mLinearLayout_contact_main_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.OnItemClicked(mData.get(position));
                }
            }
        });
        holder.mLinearLayout_contact_main_row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.mRelativeLayout_text.setBackgroundColor(mContext.getResources().getColor(R.color.md_grey_200));
                if(listener != null) {
                    listener.OnItemLongClicked(mData.get(position));
                }
                return true;
            }
        });
        holder.mTextView_name.setText(mData.get(position).getName());
        String net_value = mData.get(position).getNet_value();
        holder.mTextView_value.setText(net_value);
        if(Integer.parseInt(net_value) > 0) {
            holder.mRelativeLayout_value.setBackgroundColor(mContext.getResources().getColor(R.color.md_green_200));
        }
        else if(Integer.parseInt(net_value) < 0) {
            holder.mRelativeLayout_value.setBackgroundColor(mContext.getResources().getColor(R.color.md_red_200));
        }
        else {
            holder.mRelativeLayout_value.setBackgroundColor(mContext.getResources().getColor(R.color.md_orange_200));
        }
        holder.mRelativeLayout_text.setBackgroundColor(mContext.getResources().getColor(R.color.md_white_1000));
    }

    public void setData(ArrayList<ContactModel> data) {
        this.mData = data;
    }

    public void setOnItemClickHandler(ItemClickHandler listener) { this.listener = listener; }

    public void unselectItem() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
