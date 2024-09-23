package pl.kurs.akademiki;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class StudenciEJB {

    @PersistenceContext(name = "akademiki")
    EntityManager manager;

    public void create(Studenci student) {
        System.out.println("Tworzenie studenta!");
        manager.persist(student);
    }

    public void delete(int id_studenta) {
        try {
        	Studenci student = manager.find(Studenci.class, id_studenta);
            if (student != null) {
                manager.remove(student);
            } else {
            	throw new IllegalArgumentException("Student o podanym ID nie istnieje.");
            }
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("B��d podczas usuwania studenta.", e);
        }
    }

    public Studenci find(int id_studenta) {
        return manager.find(Studenci.class, id_studenta);
    }

    public List<Studenci> get() {
        Query q = manager.createQuery("select s from Studenci s");
        @SuppressWarnings("unchecked")
        List<Studenci> list = q.getResultList();
        return list;
    }

    public void update(Studenci student) {
        manager.merge(student);
    }
}
