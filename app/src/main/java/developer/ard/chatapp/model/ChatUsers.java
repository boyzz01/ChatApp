package developer.ard.chatapp.model;

/**
 * Created by AkshayeJH on 13/07/17.
 */

public class ChatUsers {

    public String date;

    public String nama;
    public String id;
    public String image;
    public String status;
    public long online;
    public String level;
    public String thumb_image;




    public boolean isSelected = false;




    public ChatUsers(){

    }


    public ChatUsers(String date, String nama, String id, String image, String status, long online, String level, boolean isSelected, String thumb_image)
    {
        this.date = date;
        this.nama = nama;
        this.id = id;
        this.image= image;
        this.status = status;
        this.online=online;
        this.level = level;
        this.isSelected = isSelected;
        this.thumb_image = thumb_image;


    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public long getOnline() {
        return online;
    }

    public void setOnline(long online) {
        this.online = online;
    }

    public String getDate() {
        return date;
    }
    public String getNama() { return  nama; }
    public String getId() { return  id; }
    public String getImage() { return  image; }
    public String getStatus() { return  status; }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
