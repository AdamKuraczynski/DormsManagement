package pl.kurs.akademiki;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Oplaty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_oplaty;

    private double kwota;
    private String waluta;

    @Temporal(TemporalType.DATE)
    private Date data_oplaty;

    @Temporal(TemporalType.DATE)
    private Date data_nastepnej_oplaty;

    @ManyToOne
    @JoinColumn(name = "id_rezerwacji")
    private Rezerwacja rezerwacja;

    public int getId_oplaty() {
        return id_oplaty;
    }

    public void setId_oplaty(int id_oplaty) {
        this.id_oplaty = id_oplaty;
    }

    public double getKwota() {
        return kwota;
    }

    public void setKwota(double kwota) {
        this.kwota = kwota;
    }

    public String getWaluta() {
        return waluta;
    }

    public void setWaluta(String waluta) {
        this.waluta = waluta;
    }

    public Date getData_oplaty() {
        return data_oplaty;
    }

    public void setData_oplaty(Date data_oplaty) {
        this.data_oplaty = data_oplaty;
    }

    public Date getData_nastepnej_oplaty() {
        return data_nastepnej_oplaty;
    }

    public void setData_nastepnej_oplaty(Date data_nastepnej_oplaty) {
        this.data_nastepnej_oplaty = data_nastepnej_oplaty;
    }

    public Rezerwacja getRezerwacja() {
        return rezerwacja;
    }

    public void setRezerwacja(Rezerwacja rezerwacja) {
        this.rezerwacja = rezerwacja;
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Kwota: %.2f, Waluta: %s, Data op�aty: %s, Data nast�pnej op�aty: %s, Rezerwacja: %s",
                             id_oplaty, kwota, waluta, data_oplaty, data_nastepnej_oplaty, rezerwacja);
    }

}
