package pl.kurs.akademiki;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Akademiki {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_akademika;
    private String nazwa;
    private String adres;
    private String telefon;

    public int getId_akademika() {
        return id_akademika;
    }

    public void setId_akademika(int id_akademika) {
        this.id_akademika = id_akademika;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    @Override
    public String toString() {
        return nazwa + ", " + adres + ", " + telefon;
    }
}
