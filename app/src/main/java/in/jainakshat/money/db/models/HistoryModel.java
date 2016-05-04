package in.jainakshat.money.db.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Akshat on 5/4/2016.
 */
public class HistoryModel implements Parcelable {

    private String contact_id;
    private String amount;
    private String year;
    private String month;
    private String date;
    private String action;

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public void setMonth(String month) {
        this.month = month;
    }
    public void setDate(String date) { this.date = date; }
    public void setAction(String action) { this.action = action; }

    public String getContact_id() {
        return this.contact_id;
    }
    public String getAmount() {
        return this.amount;
    }
    public String getYear() {
        return this.year;
    }
    public String getMonth() { return this.month; }
    public String getDate() { return this.date; }
    public String getAction() { return this.action; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contact_id);
        dest.writeString(amount);
        dest.writeString(year);
        dest.writeString(month);
        dest.writeString(date);
        dest.writeString(action);

    }

    private HistoryModel(Parcel in){
        this.contact_id = in.readString();
        this.amount = in.readString();
        this.year = in.readString();
        this.month = in.readString();
        this.date = in.readString();
        this.action = in.readString();
    }

    public HistoryModel() {

    }

    public static final Parcelable.Creator<HistoryModel> CREATOR = new Parcelable.Creator<HistoryModel>() {

        @Override
        public HistoryModel createFromParcel(Parcel source) {
            return new HistoryModel(source);
        }

        @Override
        public HistoryModel[] newArray(int size) {
            return new HistoryModel[size];
        }
    };

}
