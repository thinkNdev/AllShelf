package chickenmumani.com.allshelf;

public class Post_Item {

    private String uid;
    private String book;
    private String isbn;
    private String profile;
    private String uname;
    private int star;
    private String date;
    private boolean isfav;
    private int favcount;
    private String postimg;
    private String posttext;
    private String key;

    public Post_Item(String key, String uid, String book, String isbn, String profile, String uname, int star, String date, boolean isfav, int favcount, String postimg, String posttext) {
        this.key = key;
        this.uid = uid;
        this.book = book;
        this.isbn = isbn;
        this.profile = profile;
        this.uname = uname;
        this.star = star;
        this.date = date;
        this.isfav = isfav;
        this.favcount = favcount;
        this.postimg = postimg;
        this.posttext = posttext;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String uid) {
        this.book = uid;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getIsfav() {
        return isfav;
    }

    public void setIsfav(boolean isfav) {
        this.isfav = isfav;
    }

    public int getFavcount() {
        return favcount;
    }

    public void setFavcount(int favcount) {
        this.favcount = favcount;
    }

    public String getPostimg() {
        return postimg;
    }

    public void setPostimg(String postimg) {
        this.postimg = postimg;
    }

    public String getPosttext() {
        return posttext;
    }

    public void setPosttext(String posttext) {
        this.posttext = posttext;
    }
}
