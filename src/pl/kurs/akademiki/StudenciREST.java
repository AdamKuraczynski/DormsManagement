package pl.kurs.akademiki;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/studenci")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudenciREST {

    @EJB
    private StudenciEJB bean;

    @EJB
    private RezerwacjaEJB rezerwacjeBean;

    @POST
    public Response create(Studenci student) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (student.getImie() == null || student.getImie().trim().isEmpty()) {
                errors.put("imie", "Imi� nie mo�e by� nullem lub puste.");
            }
            if (student.getNazwisko() == null || student.getNazwisko().trim().isEmpty()) {
                errors.put("nazwisko", "Nazwisko nie mo�e by� nullem lub puste.");
            }
            if (student.getNumer_indeksu() == null || student.getNumer_indeksu().trim().isEmpty()) {
                errors.put("numer_indeksu", "Numer indeksu nie mo�e by� nullem lub pusty.");
            }
            if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
                errors.put("email", "Email nie mo�e by� nullem lub pusty.");
            }
            if (student.getPokoj() == null || student.getPokoj().getId_pokoju() <= 0) {
                errors.put("pokoj", "Pok�j nie mo�e by� nullem i musi mie� prawid�owe ID.");
            }

            if (!errors.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
            }

            bean.create(student);
            String responseMessage = String.format(
                    "Student zosta� utworzony!\n\nID: %d\nImi�: %s\nNazwisko: %s\nNumer indeksu: %s\nEmail: %s\nPok�j ID: %d",
                    student.getId_studenta(),
                    student.getImie(),
                    student.getNazwisko(),
                    student.getNumer_indeksu(),
                    student.getEmail(),
                    student.getPokoj() != null ? student.getPokoj().getId_pokoju() : "Brak"
                );

                return Response.status(Response.Status.CREATED).type(MediaType.TEXT_PLAIN).entity(responseMessage).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Student nie zosta� utworzony. Wyst�pi� b��d.")
                    .build();
        }
    }

    @PUT
    public Response update(Studenci student) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (student.getId_studenta() <= 0) {
                errors.put("id_studenta", "ID studenta musi by� podane i prawid�owe.");
            }
            if (student.getImie() == null || student.getImie().trim().isEmpty()) {
                errors.put("imie", "Imi� nie mo�e by� nullem lub puste.");
            }
            if (student.getNazwisko() == null || student.getNazwisko().trim().isEmpty()) {
                errors.put("nazwisko", "Nazwisko nie mo�e by� nullem lub puste.");
            }
            if (student.getNumer_indeksu() == null || student.getNumer_indeksu().trim().isEmpty()) {
                errors.put("numer_indeksu", "Numer indeksu nie mo�e by� nullem lub pusty.");
            }
            if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
                errors.put("email", "Email nie mo�e by� nullem lub pusty.");
            }
            if (student.getPokoj() == null || student.getPokoj().getId_pokoju() <= 0) {
                errors.put("pokoj", "Pok�j nie mo�e by� nullem i musi mie� prawid�owe ID.");
            }

            if (!errors.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
            }

            bean.update(student);
            return Response.ok("Student zosta� zaktualizowany!").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Student nie zosta� zaktualizowany. Wyst�pi� b��d.")
                    .build();
        }
    }

    @GET
    @Path("/{id_studenta}")
    public Response find(@PathParam("id_studenta") int id_studenta) {
        Studenci student = bean.find(id_studenta);
        if (student != null) {
            return Response.ok(student).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Student o podanym ID nie istnieje").build();
        }
    }

    @GET
    public List<Studenci> get() {
        return bean.get();
    }

    @DELETE
    @Path("/{id_studenta}")
    public Response delete(@PathParam("id_studenta") int id_studenta) {
        try {
            bean.delete(id_studenta);
            return Response.ok("Student zosta� usuni�ty.").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Student nie zosta� usuni�ty.")
                    .build();
        }
    }

    @GET
    @Path("/{id_studenta}/rezerwacje")
    public Response getRezerwacje(@PathParam("id_studenta") int id_studenta) {
        try {
            List<Rezerwacja> rezerwacjeList = rezerwacjeBean.getRezerwacjeByStudentId(id_studenta);
            if (rezerwacjeList != null && !rezerwacjeList.isEmpty()) {
                return Response.ok(rezerwacjeList).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Brak rezerwacji dla studenta o podanym ID").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Wyst�pi� b��d podczas pobierania rezerwacji studenta.")
                    .build();
        }
    }
}
