package pl.kurs.akademiki;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class AkademikiEJB {

    @PersistenceContext(name = "akademiki")
    EntityManager manager;

    public void create(Akademiki akademik) {
        try {
            System.out.println("Tworzenie akademika!");
            manager.persist(akademik);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("B��d podczas tworzenia akademika.", e);
        }
    }

    public void delete(int id_akademika) {
        try {
            Akademiki akademik = manager.find(Akademiki.class, id_akademika);
            if (akademik != null) {
                manager.remove(akademik);
            } else {
                throw new IllegalArgumentException("Akademik o podanym ID nie istnieje.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("B��d podczas usuwania akademika.", e);
        }
    }

    public Akademiki find(int id_akademika) {
        Akademiki akademik = manager.find(Akademiki.class, id_akademika);
        if (akademik == null) {
            throw new IllegalArgumentException("Akademik o podanym ID nie istnieje.");
        }
        return akademik;
    }

    public List<Akademiki> get() {
        Query q = manager.createQuery("select a from Akademiki a");
        @SuppressWarnings("unchecked")
        List<Akademiki> list = q.getResultList();
        return list;
    }

    public void update(Akademiki akademik) {
        try {
            manager.merge(akademik);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("B��d podczas aktualizacji akademika.", e);
        }
    }
}
