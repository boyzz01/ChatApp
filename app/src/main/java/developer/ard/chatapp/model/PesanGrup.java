package developer.ard.chatapp.model;

public class PesanGrup
{
    private String pengirim;
    private String grupId;
    private String pesan;
    private long waktu;
    private String ukuran,url,ekstension,type;
    private String id,pushid;

    public PesanGrup(String pengirim, String grupId, String pesan, long waktu, String ukuran, String url, String ekstension, String type, String id, String pushid) {
        this.pengirim = pengirim;
        this.grupId = grupId;
        this.pesan = pesan;
        this.waktu = waktu;
        this.ukuran = ukuran;
        this.url = url;
        this.ekstension = ekstension;
        this.type = type;
        this.id = id;
        this.pushid = pushid;
    }

    public PesanGrup()
    {

    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPushid() {
        return pushid;
    }

    public void setPushid(String pushid) {
        this.pushid = pushid;
    }

    public String getPengirim() {
        return pengirim;
    }

    public void setPengirim(String pengirim) {
        this.pengirim = pengirim;
    }

    public String getGrupId() {
        return grupId;
    }

    public void setGrupId(String grupId) {
        this.grupId = grupId;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public long getWaktu() {
        return waktu;
    }

    public void setWaktu(long waktu) {
        this.waktu = waktu;
    }
}
