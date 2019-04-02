package chickenmumani.com.allshelf;

public class Friend_ListItem {

    private String uid;
    private String name;
    private String img;

    public Friend_ListItem(String uid, String name, String img) {
        this.uid = uid;
        this.name = name;
        this.img = img;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
