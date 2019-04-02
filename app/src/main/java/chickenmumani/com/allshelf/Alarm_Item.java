package chickenmumani.com.allshelf;

public class Alarm_Item {
    private String uid;
    private String date;
    private String text;
    private String type;
    private String boardid;

    public Alarm_Item(String type, String boardid, String uid, String date, String text){
        this.type = type;
        this.boardid = boardid;
        this.uid = uid;
        this.date = date;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public String getBoardid() {
        return boardid;
    }

    public String getUid() {
        return uid;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}

