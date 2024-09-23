package pl.kurs.akademiki;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class OplatyEJB {

    @PersistenceContext(name = "akademiki")
    EntityManager manager;

    public void create(Oplaty oplaty) {
        System.out.println("Tworzenie op�aty!");
        manager.persist(oplaty);
    }

    public void delete(int id_oplaty) {
        try {
        	Oplaty oplaty = manager.find(Oplaty.class, id_oplaty);
            if (oplaty != null) {
                manager.remove(oplaty);
            } else {
            	throw new IllegalArgumentException("Op�ata o podanym ID nie istnieje.");
            }
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("B��d podczas usuwania op�aty.", e);
        }
    }

    public Oplaty find(int id_oplaty) {
        return manager.find(Oplaty.class, id_oplaty);
    }

    public List<Oplaty> get() {
        Query q = manager.createQuery("select o from Oplaty o");
        @SuppressWarnings("unchecked")
        List<Oplaty> list = q.getResultList();
        return list;
    }

    public void update(Oplaty oplaty) {
        manager.merge(oplaty);
    }
}
