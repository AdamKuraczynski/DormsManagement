package pl.kurs.akademiki;

import javax.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
public class Rezerwacja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_rezerwacji;

    @Temporal(TemporalType.DATE)
    private Date data_rozpoczecia;

    @Temporal(TemporalType.DATE)
    private Date data_zakonczenia;

    @ManyToOne
    @JoinColumn(name = "id_studenta")
    private Studenci student;

    public int getId_rezerwacji() {
        return id_rezerwacji;
    }

    public void setId_rezerwacji(int id_rezerwacji) {
        this.id_rezerwacji = id_rezerwacji;
    }

    public Date getData_rozpoczecia() {
        return data_rozpoczecia;
    }

    public void setData_rozpoczecia(Date data_rozpoczecia) {
        this.data_rozpoczecia = data_rozpoczecia;
    }

    public Date getData_zakonczenia() {
        return data_zakonczenia;
    }

    public void setData_zakonczenia(Date data_zakonczenia) {
        this.data_zakonczenia = data_zakonczenia;
    }

    public Studenci getStudent() {
        return student;
    }

    public void setStudent(Studenci student) {
        this.student = student;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return String.format("Id rezerwacji: %d, Data rozpoczecia: %s, Data zakonczenia: %s",
                id_rezerwacji,
                data_rozpoczecia != null ? dateFormat.format(data_rozpoczecia) : "N/A",
                data_zakonczenia != null ? dateFormat.format(data_zakonczenia) : "N/A");
    }
    
}
