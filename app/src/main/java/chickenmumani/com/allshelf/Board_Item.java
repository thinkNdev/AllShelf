package chickenmumani.com.allshelf;

public class Board_Item {
    private String dbid;
    private String name;
    private String uuid;
    private String date;
    private String title;
    private String content;
    private String bookname;
    private String isbn;
    private String imgurl;


    public Board_Item(String dbid, String date, String name, String uuid, String title, String content, String bookname, String isbn, String imgurl) {
        this.dbid = dbid;
        this.date = date;
        this.name = name;
        this.uuid = uuid;
        this.title = title;
        this.content = content;
        this.bookname = bookname;
        this.isbn = isbn;
        this.imgurl = imgurl;
    }

    public void setName(String name){ this.name = name; }

    public void setTitle(String title) {
        this.title = title ;
    }

    public void setDate(String date) { this.date = date; }

    public String getDbid(){ return this.dbid; }

    public String getUuid(){ return this.uuid; }

    public String getName() { return this.name; }

    public String getTitle() { return this.title; }

    public String getContent() { return this.content; }

    public String getDate() { return this.date; }

    public String getBookname(){ return this.bookname; }

    public String getIsbn(){ return this.isbn; }

    public String getImgurl(){ return this.imgurl; }
}
