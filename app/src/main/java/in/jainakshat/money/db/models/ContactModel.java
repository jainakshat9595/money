package in.jainakshat.money.db.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Akshat on 4/29/2016.
 */

public class ContactModel implements Parcelable {

    private String type_id;
    private String name;
    private String number;
    private String net_value;
    private String timestamp;
    public String timestamp_created;

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public void setNet_value(String net_value) {
        this.net_value = net_value;
    }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setTimestamp_created(String timestamp_created) { this.timestamp_created = timestamp_created; }

    public String getType_id() {
        return this.type_id;
    }
    public String getName() {
        return this.name;
    }
    public String getNumber() {
        return this.number;
    }
    public String getNet_value() { return this.net_value; }
    public String getTimestamp() { return this.timestamp; }
    public String getTimestamp_created() { return this.timestamp_created; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type_id);
        dest.writeString(name);
        dest.writeString(number);
        dest.writeString(net_value);
        dest.writeString(timestamp);
        dest.writeString(timestamp_created);
    }

    private ContactModel(Parcel in){
        this.type_id = in.readString();
        this.name = in.readString();
        this.number = in.readString();
        this.net_value = in.readString();
        this.timestamp = in.readString();
        this.timestamp_created = in.readString();
    }

    public ContactModel() {

    }

    public static final Parcelable.Creator<ContactModel> CREATOR = new Parcelable.Creator<ContactModel>() {

        @Override
        public ContactModel createFromParcel(Parcel source) {
            return new ContactModel(source);
        }

        @Override
        public ContactModel[] newArray(int size) {
            return new ContactModel[size];
        }
    };


}
