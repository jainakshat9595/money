package in.jainakshat.money.utills;

/**
 * Created by Akshat on 25-05-2017.
 */

public class Logger {

    public static void logg(String message) {
        System.out.println("MoneyApp Logg, "+message);
    }

    public static void logg(String tag, String message) {
        System.out.println("MoneyApp Logg, "+tag+": "+message);
    }

}
