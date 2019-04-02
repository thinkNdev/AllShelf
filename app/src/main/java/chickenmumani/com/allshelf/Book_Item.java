package chickenmumani.com.allshelf;

public class Book_Item {
    private String author;
    private String imgurl;
    private String isbn;
    private String time;
    private String title;

    public Book_Item(String TITLE, String AUTHOR, String ISBN, String TIME, String IMGURL){
        title = TITLE;
        author = AUTHOR;
        isbn = ISBN;
        time = TIME;
        imgurl = IMGURL;
    }

    public String getTitle(){
        return title;
    }

    public String getAuthor(){
        return author;
    }

    public String getISBN(){
        return isbn;
    }

    public String getTime(){
        return time;
    }

    public String getImgUrl(){
        return imgurl;
    }
}
