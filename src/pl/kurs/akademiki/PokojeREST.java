package pl.kurs.akademiki;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Path("/pokoje")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PokojeREST {

    @EJB
    private PokojeEJB bean;

    @POST
    public Response create(Pokoje pokoje) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (pokoje.getNumer_pokoju() <= 0) {
                errors.put("numer_pokoju", "Numer pokoju nie mo�e by� nullem lub pusty.");
            }
            if (pokoje.getIlosc_dostepnych_miejsc() <= 0) {
                errors.put("ilosc_dostepnych_miejsc", "Ilo�� dost�pnych miejsc nie mo�e by� nullem lub pusty.");
            }
            if (pokoje.getIlosc_lozek() <= 0) {
                errors.put("ilosc_lozek", "Ilo�� ��ek nie mo�e by� nullem lub pusty.");
            }
            if (pokoje.getAkademiki() == null || pokoje.getAkademiki().getId_akademika() <= 0) {
                errors.put("akademiki", "Akademik nie mo�e by� nullem i musi mie� prawid�owe ID.");
            }
            if (!errors.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
            }

            bean.create(pokoje);

            return Response.status(Response.Status.CREATED)
                    .entity("Pok�j zosta� utworzony!\n\n" + pokoje)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Pok�j nie zosta� utworzony. Wyst�pi� b��d.")
                    .build();
        }
    }

    @GET
    @Path("/{id_pokoju}")
    public Response find(@PathParam("id_pokoju") int id_pokoju) {
        Pokoje pokoje = bean.find(id_pokoju);
        if (pokoje != null) {
            return Response.ok(pokoje).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Pok�j o podanym ID nie istnieje").build();
        }
    }

    @GET
    public List<Pokoje> get() {
        return bean.get();
    }

    @PUT
    public Response update(Pokoje pokoje) {
        try {
            Map<String, String> errors = new HashMap<>();

            if (pokoje.getId_pokoju() <= 0) {
                errors.put("id_pokoju", "ID pokoju nie mo�e by� nullem lub pusty.");
            }

            if (pokoje.getNumer_pokoju() <= 0) {
                errors.put("numer_pokoju", "Numer pokoju nie mo�e by� nullem lub pusty.");
            }
            if (pokoje.getIlosc_dostepnych_miejsc() <= 0) {
                errors.put("ilosc_dostepnych_miejsc", "Ilo�� dost�pnych miejsc nie mo�e by� nullem lub pusty.");
            }
            if (pokoje.getIlosc_lozek() <= 0) {
                errors.put("ilosc_lozek", "Ilo�� ��ek nie mo�e by� nullem lub pusty.");
            }
            if (pokoje.getAkademiki() == null || pokoje.getAkademiki().getId_akademika() <= 0) {
                errors.put("akademiki", "Akademik nie mo�e by� nullem i musi mie� prawid�owe ID.");
            }

            if (!errors.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
            }

            bean.update(pokoje);

            return Response.ok("Pok�j zosta� zaktualizowany!").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Pok�j nie zosta� zaktualizowany. Wyst�pi� b��d.")
                           .build();
        }
    }

    @DELETE
    @Path("/{id_pokoju}")
    public Response delete(@PathParam("id_pokoju") int id_pokoju) {
        try {
        	bean.delete(id_pokoju);
            return Response.ok("Pok�j zosta� usuni�ty!").build();
        } catch (Exception e) {
        	e.printStackTrace();
        	return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Pok�j nie zosta� usuni�ty.")
                    .build();
        }
    	
    }
}
