package chickenmumani.com.allshelf;

public class Comment_Item {

    private String date;
    private String name;
    private String text;
    private String uuid;


    public Comment_Item(String date, String name, String uuid, String text) {
        this.date = date;
        this.name = name;
        this.text = text;
        this.uuid = uuid;
    }

    public void setName(String name){ this.name = name; }

    public void setDate(String date) { this.date = date; }

    public String getUuid() { return this.uuid;}

    public String getName() { return this.name; }

    public String getText() { return this.text; }

    public String getDate() { return this.date; }
}

