package dev.ws.tiketku.Model;

/**
 * Created by Wawan on 5/22/2019
 */
public class MyTicket {
    String nama_wisata;
    String lokasi;
    String date_wisata;
    String time_wisata;
    String id_tiket;
    String ketentuan;
    String jumlah_tiket;

    public MyTicket(){

    }

    public MyTicket(String nama_wisata, String lokasi, String jumlah_tiket, String date_wisata, String time_wisata, String id_tiket, String ketentuan) {
        this.id_tiket = id_tiket;
        this.date_wisata = date_wisata;
        this.time_wisata = time_wisata;
        this.nama_wisata = nama_wisata;
        this.ketentuan = ketentuan;
        this.lokasi = lokasi;
        this.jumlah_tiket = jumlah_tiket;
    }

    public String getDate_wisata() {
        return date_wisata;
    }

    public void setDate_wisata(String date_wisata) {
        this.date_wisata = date_wisata;
    }

    public String getTime_wisata() {
        return time_wisata;
    }

    public void setTime_wisata(String time_wisata) {
        this.time_wisata = time_wisata;
    }

    public String getId_tiket() {
        return id_tiket;
    }

    public void setId_tiket(String id_tiket) {
        this.id_tiket = id_tiket;
    }

    public String getKetentuan() {
        return ketentuan;
    }

    public void setKetentuan(String ketentuan) {
        this.ketentuan = ketentuan;
    }

    public String getNama_wisata() {
        return nama_wisata;
    }

    public void setNama_wisata(String nama_wisata) {
        this.nama_wisata = nama_wisata;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getJumlah_tiket() {
        return jumlah_tiket;
    }

    public void setJumlah_tiket(String jumlah_tiket) {
        this.jumlah_tiket = jumlah_tiket;
    }
}
