package pl.kurs.akademiki;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/rezerwacje")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RezerwacjaREST {

    @EJB
    private RezerwacjaEJB bean;

    @POST
    public Response create(Rezerwacja rezerwacja) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (rezerwacja.getData_rozpoczecia() == null) {
                errors.put("data_rozpoczecia", "Data rozpocz�cia nie mo�e by� nullem ani pusta.");
            }
            if (rezerwacja.getData_zakonczenia() == null) {
                errors.put("data_zakonczenia", "Data zako�czenia nie mo�e by� nullem ani pusta.");
            }
            if (rezerwacja.getStudent() == null || rezerwacja.getStudent().getId_studenta() <= 0) {
                errors.put("student", "Student nie mo�e by� nullem i musi mie� prawid�owe ID.");
            }

            if (!errors.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
            }
            bean.create(rezerwacja);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dataRozpoczeciaFormatted = dateFormat.format(rezerwacja.getData_rozpoczecia());
            String dataZakonczeniaFormatted = dateFormat.format(rezerwacja.getData_zakonczenia());
            String response = "Rezerwacja zosta�a utworzona!\n\n" +
                    "Rezerwacja ID: " + rezerwacja.getId_rezerwacji() + "\n" +
                    "Data rozpocz�cia: " + dataRozpoczeciaFormatted + "\n" +
                    "Data zako�czenia: " + dataZakonczeniaFormatted + "\n" +
                    "Student ID: " + rezerwacja.getStudent().getId_studenta();

            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Rezerwacja nie zosta�a utworzona. Wyst�pi� b��d.")
                    .build();
        }
    }


    @GET
    @Path("/{id_rezerwacji}")
    public Response find(@PathParam("id_rezerwacji") int id_rezerwacji) {
        Rezerwacja rezerwacja = bean.find(id_rezerwacji);
        if (rezerwacja != null) {
            return Response.ok(rezerwacja).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Rezerwacja o podanym ID nie istnieje")
                    .build();
        }
    }

    @GET
    public List<Rezerwacja> get() {
        return bean.get();
    }

    @PUT
    public Response update(Rezerwacja rezerwacja) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (rezerwacja.getId_rezerwacji() <= 0) {
                errors.put("id_rezerwacji", "ID rezerwacji nie mo�e by� nullem lub puste.");
            }
            if (rezerwacja.getData_rozpoczecia() == null) {
                errors.put("data_rozpoczecia", "Data rozpocz�cia nie mo�e by� nullem ani pusta.");
            }
            if (rezerwacja.getData_zakonczenia() == null) {
                errors.put("data_zakonczenia", "Data zako�czenia nie mo�e by� nullem ani pusta.");
            }
            if (rezerwacja.getStudent() == null || rezerwacja.getStudent().getId_studenta() <= 0) {
                errors.put("student", "Student nie mo�e by� nullem i musi mie� prawid�owe ID.");
            }

            if (!errors.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
            }

            bean.update(rezerwacja);
            return Response.ok("Rezerwacja zosta�a zaktualizowana!").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Rezerwacja nie zosta�a zaktualizowana. Wyst�pi� b��d.")
                    .build();
        }
    }

    @DELETE
    @Path("/{id_rezerwacji}")
    public Response delete(@PathParam("id_rezerwacji") int id_rezerwacji) {
        try {
        	bean.delete(id_rezerwacji);
            return Response.ok().build();
        } catch (Exception e) {
        	e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Rezerwacja nie zosta�a usuni�ta.")
                           .build();
        }
    }
}
