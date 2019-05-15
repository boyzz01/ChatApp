package developer.ard.chatapp.model;

public class Grup
{
    public String date;

    public String nama;
    public String id;
    public String image;
    public Grup()
    {

    }


    public Grup(String date, String nama, String id, String image) {
        this.date = date;
        this.nama = nama;
        this.id = id;
        this.image = image;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
