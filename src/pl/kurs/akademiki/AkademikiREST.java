package pl.kurs.akademiki;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/akademiki")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AkademikiREST {

    @EJB
    private AkademikiEJB bean;

    @POST
    public Response create(Akademiki akademik) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (akademik.getNazwa() == null || akademik.getNazwa().isEmpty()) {
                errors.put("nazwa", "Nazwa nie mo�e by� nullem lub pusta.");
            }
            if (akademik.getAdres() == null || akademik.getAdres().isEmpty()) {
                errors.put("adres", "Adres nie mo�e by� nullem lub pusty.");
            }
            if (akademik.getTelefon() == null || akademik.getTelefon().isEmpty()) {
                errors.put("telefon", "Telefon nie mo�e by� nullem lub pusty.");
            }

            if (!errors.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
            }

            bean.create(akademik);

            return Response.status(Response.Status.CREATED)
                    .entity("Akademik zosta� utworzony!\n\n"+akademik)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Akademik nie zosta� utworzony. Wyst�pi� b��d.")
                           .build();
        }
    }

    @GET
    @Path("/{id_akademika}")
    public Response find(@PathParam("id_akademika") int id_akademika) {
        try {
            Akademiki akademik = bean.find(id_akademika);
            return Response.ok(akademik).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Akademik o podanym ID nie istnieje.")
                           .build();
        }
    }

    @GET
    public Response get() {
        try {
            List<Akademiki> akademikiList = bean.get();
            return Response.ok(akademikiList).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Wyst�pi� b��d podczas pobierania akademik�w.")
                           .build();
        }
    }

    @PUT
    public Response update(Akademiki akademik) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (akademik.getNazwa() == null || akademik.getNazwa().isEmpty()) {
                errors.put("nazwa", "Nazwa nie mo�e by� nullem lub pusta.");
            }
            if (akademik.getAdres() == null || akademik.getAdres().isEmpty()) {
                errors.put("adres", "Adres nie mo�e by� nullem lub pusty.");
            }
            if (akademik.getTelefon() == null || akademik.getTelefon().isEmpty()) {
                errors.put("telefon", "Telefon nie mo�e by� nullem lub pusty.");
            }

            if (!errors.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
            }

            bean.update(akademik);
            return Response.ok("Akademik zosta� zaktualizowany!").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Akademik nie zosta� zaktualizowany.")
                           .build();
        }
    }

    @DELETE
    @Path("/{id_akademika}")
    public Response delete(@PathParam("id_akademika") int id_akademika) {
        try {
            bean.delete(id_akademika);
            return Response.ok("Akademik zosta� usuni�ty!").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Akademik nie zosta� usuni�ty.")
                           .build();
        }
    }
}
