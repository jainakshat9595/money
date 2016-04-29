package in.jainakshat.money.db.models;

/**
 * Created by Akshat on 4/29/2016.
 */

public class ContactModel {

    private String type_id;
    private String name;
    private String number;
    private String net_value;

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

    public String getType_id() {
        return this.type_id;
    }
    public String getName() {
        return this.name;
    }
    public String getNumber() {
        return this.number;
    }
    public String getNet_value() {
        return this.net_value;
    }


}
