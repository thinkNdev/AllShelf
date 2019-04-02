package chickenmumani.com.allshelf;

public class Shelf_Item {

    private String isbn;
    private String title;
    private String author;
    private String imgurl;
    private String time;

    public Shelf_Item(String isbn, String title, String author, String imgurl, String time) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.imgurl = imgurl;
        this.time = time;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
