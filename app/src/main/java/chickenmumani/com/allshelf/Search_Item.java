package chickenmumani.com.allshelf;

import android.graphics.drawable.Drawable;

public class Search_Item {

    private String title;
    private String author;
    private String company;
    private String isbn;
    private String cover ;

    public Search_Item(String title, String author, String company, String isbn, String iconDrawable) {
        this.title = title;
        this.author = author;
        this.company = company;
        this.isbn = isbn;
        this.cover = iconDrawable;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
