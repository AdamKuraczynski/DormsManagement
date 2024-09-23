package pl.kurs.akademiki;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.NoResultException;
import java.util.List;

@Stateless
public class PokojeEJB {

    @PersistenceContext(name = "akademiki")
    EntityManager manager;

    public void create(Pokoje pokoje) {
        if (pokoje.getAkademiki() != null) {
            Akademiki akademiki = manager.find(Akademiki.class, pokoje.getAkademiki().getId_akademika());
            if (akademiki == null) {
                throw new IllegalArgumentException("Akademik o podanym ID nie istnieje");
            }
        }
        manager.persist(pokoje);
    }

    public void delete(int id_pokoju) {
        try {
        	Pokoje pokoje = manager.find(Pokoje.class, id_pokoju);
            if (pokoje != null) {
                manager.remove(pokoje);
            } else {
            	 throw new IllegalArgumentException("Pokoj o podanym ID nie istnieje.");
            }
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new RuntimeException("B��d podczas usuwania pokoju.", e);
        }
    }

    public Pokoje find(int id_pokoju) {
        return manager.find(Pokoje.class, id_pokoju);
    }

    public List<Pokoje> get() {
        Query q = manager.createQuery("select p from Pokoje p");
        @SuppressWarnings("unchecked")
        List<Pokoje> list = q.getResultList();
        return list;
    }

    public void update(Pokoje pokoje) {
        manager.merge(pokoje);
    }
}
