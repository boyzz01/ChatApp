package developer.ard.chatapp.model;

public class Pesan {

    private String pengirim;
    private String penerima;
    private String pesan;
    private long waktu;
    private boolean seen;
    private String ukuran,url,ekstension,type;
    private String id,pushid;

    public Pesan(String pengirim, String penerima, String pesan,long waktu,boolean seen,String ukuran,String url,String ekstension,String type,String id,String pushid) {
        this.pengirim = pengirim;
        this.penerima = penerima;
        this.pesan = pesan;
        this.seen = seen;
        this.ukuran = ukuran;
        this.url = url;
        this.ekstension = ekstension;
        this.type = type;
        this.id = id;
        this.pushid = pushid;
    }

    public String getPushid() {
        return pushid;
    }

    public void setPushid(String pushid) {
        this.pushid = pushid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Pesan() {
    }

    public String getUkuran() {
        return ukuran;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEkstension() {
        return ekstension;
    }

    public void setEkstension(String ekstension) {
        this.ekstension = ekstension;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getWaktu() {
        return waktu;
    }

    public void setWaktu(long waktu) {
        this.waktu = waktu;
    }


    public String getPengirim() {
        return pengirim;
    }

    public void setPengirim(String pengirim) {
        this.pengirim = pengirim;
    }

    public String getPenerima() {
        return penerima;
    }

    public void setPenerima(String penerima) {
        this.penerima = penerima;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }
}
