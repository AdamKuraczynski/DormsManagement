package pl.kurs.akademiki;

import javax.persistence.*;

@Entity
public class Pokoje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_pokoju;

    private int numer_pokoju;

    private int ilosc_dostepnych_miejsc;

    private int ilosc_lozek;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "id_akademika")
    private Akademiki akademiki;

    public int getId_pokoju() {
        return id_pokoju;
    }

    public void setId_pokoju(int id_pokoju) {
        this.id_pokoju = id_pokoju;
    }

    public int getNumer_pokoju() {
        return numer_pokoju;
    }

    public void setNumer_pokoju(int numer_pokoju) {
        if (numer_pokoju < 0) {
            throw new IllegalArgumentException("Numer pokoju nie moze byc mniejsza niz 0");
        }
        this.numer_pokoju = numer_pokoju;
    }

    public int getIlosc_dostepnych_miejsc() {
        return ilosc_dostepnych_miejsc;
    }

    public void setIlosc_dostepnych_miejsc(int ilosc_dostepnych_miejsc) {
        if (ilosc_dostepnych_miejsc < 0) {
            throw new IllegalArgumentException("Ilosc dostepnych miejsc nie moze byc mniejsza niz 0");
        }
        this.ilosc_dostepnych_miejsc = ilosc_dostepnych_miejsc;
    }

    public int getIlosc_lozek() {
        return ilosc_lozek;
    }

    public void setIlosc_lozek(int ilosc_lozek) {
        if (ilosc_lozek < 0) {
            throw new IllegalArgumentException("Ilosc lozek nie moze byc mniejsza niz 0");
        }
        this.ilosc_lozek = ilosc_lozek;
    }

    public Akademiki getAkademiki() {
        return akademiki;
    }

    public void setAkademiki(Akademiki akademiki) {
        this.akademiki = akademiki;
    }

    @Override
    public String toString() {
        return String.format(
            "Pokoj nr %d, miejsca: %d, lozka: %d, Akademik ID: %s",
            numer_pokoju,
            ilosc_dostepnych_miejsc,
            ilosc_lozek,
            (akademiki != null ? akademiki.getId_akademika() : "brak")
        );
    }
}
