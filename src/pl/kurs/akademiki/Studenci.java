package pl.kurs.akademiki;

import javax.persistence.*;
import java.util.List;

@Entity
public class Studenci {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_studenta;

    private String imie;
    private String nazwisko;
    private String numer_indeksu;
    private String email;

    @ManyToOne
    @JoinColumn(name = "id_pokoju")
    private Pokoje pokoj;

    public int getId_studenta() {
        return id_studenta;
    }

    public void setId_studenta(int id_studenta) {
        this.id_studenta = id_studenta;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getNumer_indeksu() {
        return numer_indeksu;
    }

    public void setNumer_indeksu(String numer_indeksu) {
        this.numer_indeksu = numer_indeksu;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Pokoje getPokoj() {
        return pokoj;
    }

    public void setPokoj(Pokoje pokoj) {
        this.pokoj = pokoj;
    }
    
    @Override
    public String toString() {
        return String.format("ID: %d, Imie: %s, Nazwisko: %s, Nr Indeksu: %s, Email: %s",
                this.id_studenta, this.imie, this.nazwisko, this.numer_indeksu, this.email);
    }

}
