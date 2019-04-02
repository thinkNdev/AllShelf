package chickenmumani.com.allshelf;

public class Notice_Item {

    private String id;
    private String notcontent_title;
    private String notcontent_date;

    public Notice_Item(String id, String notcontent_title, String notcontent_date) {
        this.id = id;
        this.notcontent_title = notcontent_title;
        this.notcontent_date = notcontent_date;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public void setTitle(String title) {
        notcontent_title = title ;
    }

    public void setDate(String date) {
        notcontent_date = date;
    }

    public String getTitle() {
        return this.notcontent_title ;
    }

    public String getDate() {
        return this.notcontent_date ;
    }

}
