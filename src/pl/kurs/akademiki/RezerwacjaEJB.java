package pl.kurs.akademiki;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class RezerwacjaEJB {

    @PersistenceContext(name = "akademiki")
    EntityManager manager;

    public void create(Rezerwacja rezerwacja) {
        System.out.println("Tworzenie rezerwacji!");
        manager.persist(rezerwacja);
    }

    public void delete(int id_rezerwacji) {
        try {
        	Rezerwacja rezerwacja = manager.find(Rezerwacja.class, id_rezerwacji);
            if (rezerwacja != null) {
                manager.remove(rezerwacja);
            } else {
            	throw new IllegalArgumentException("Rezerwacja o podanym ID nie istnieje.");
            }
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("B��d podczas usuwania rezerwacji.", e);
        }
    }

    public Rezerwacja find(int id_rezerwacji) {
        return manager.find(Rezerwacja.class, id_rezerwacji);
    }

    public List<Rezerwacja> get() {
        TypedQuery<Rezerwacja> query = manager.createQuery("select r from Rezerwacja r", Rezerwacja.class);
        return query.getResultList();
    }

    public void update(Rezerwacja rezerwacja) {
        manager.merge(rezerwacja);
    }
    public List<Rezerwacja> getRezerwacjeByStudentId(int id_studenta) {
        TypedQuery<Rezerwacja> query = manager.createQuery(
            "SELECT r FROM Rezerwacja r WHERE r.student.id_studenta = :id_studenta", Rezerwacja.class);
        query.setParameter("id_studenta", id_studenta);
        return query.getResultList();
    }
}
