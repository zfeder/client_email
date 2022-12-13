package app;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email implements Serializable {
    private final int ID;
    private final String sender;
    private final String receiver;
    private final String object;
    private final String text;
    private final String date;
    private final String other_dest;

    public Email(int id, String send, String rec, String obj, String tex, String dat, String oth_dst) {
        this.ID = id;
        this.sender = send;
        this.receiver = rec;
        this.object = obj;
        this.text = tex;
        this.date = dat;
        this.other_dest = oth_dst;
    }

    public int getID() {
        return ID;
    }
    public String getSender() {
        return sender;
    }
    public String getReceiver() {
        return receiver;
    }
    public String getObject() {
        return object;
    }
    public String getText() {
        return text;
    }
    public String getDate() {
        return date;
    }
    public String getOther_dest() {
        return other_dest;
    }
    public static boolean verificationEmailAddress(String email) {
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
