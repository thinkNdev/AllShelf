package chickenmumani.com.allshelf;

import android.graphics.drawable.Drawable;

public class Message_Item {

    private String id;
    private Drawable iconDrawable ;
    private String message_msg;
    private String message_date;

    public Message_Item(String id, Drawable iconDrawable, String message_msg, String message_date) {
        this.id = id;
        this.iconDrawable = iconDrawable;
        this.message_msg = message_msg;
        this.message_date = message_date;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public String getMessage_msg() {
        return message_msg;
    }

    public void setMessage_msg(String message_msg) {
        this.message_msg = message_msg;
    }

    public String getMessage_date() {
        return message_date;
    }

    public void setMessage_date(String message_date) {
        this.message_date = message_date;
    }
}
