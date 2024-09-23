package pl.kurs.akademiki;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

@Path("/oplaty")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OplatyREST {

    @EJB
    private OplatyEJB bean;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.00", DecimalFormatSymbols.getInstance(Locale.US));

    @POST
    public Response create(Oplaty oplata) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (oplata.getKwota() <= 0) {
                errors.put("kwota", "Kwota musi by� wi�ksza ni� 0.");
            }
            if (oplata.getWaluta() == null || oplata.getWaluta().isEmpty()) {
                errors.put("waluta", "Waluta nie mo�e by� nullem lub pusty.");
            }
            if (oplata.getData_oplaty() == null) {
                errors.put("data_oplaty", "Data oplaty nie mo�e by� nullem.");
            }
            if (oplata.getData_nastepnej_oplaty() == null) {
                errors.put("data_nastepnej_oplaty", "Data nast�pnej oplaty nie mo�e by� nullem.");
            }
            if (oplata.getRezerwacja() == null || oplata.getRezerwacja().getId_rezerwacji() <= 0) {
                errors.put("rezerwacja", "Rezerwacja musi by� okre�lona i mie� prawidlowe ID.");
            }

            if (!errors.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
            }

            bean.create(oplata);

            String formattedKwota = decimalFormat.format(oplata.getKwota());
            String formattedDataOplaty = dateFormat.format(oplata.getData_oplaty());
            String formattedDataNastepnejOplaty = dateFormat.format(oplata.getData_nastepnej_oplaty());

            String responseMessage = String.format(
                "Oplata zostala utworzona!\n\nKwota: %s %s\nData oplaty: %s\nData nast�pnej oplaty: %s\nRezerwacja ID: %d",
                formattedKwota.trim(),
                oplata.getWaluta(),
                formattedDataOplaty,
                formattedDataNastepnejOplaty,
                oplata.getRezerwacja().getId_rezerwacji()
            );

            return Response.status(Response.Status.CREATED).type(MediaType.TEXT_PLAIN).entity(responseMessage).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Collections.singletonMap("error", "Oplata nie zostala utworzona. Wyst�pil bl�d."))
                           .build();
        }
    }

    @GET
    @Path("/{id_oplaty}")
    public Response find(@PathParam("id_oplaty") int id_oplaty) {
        try {
            Oplaty oplata = bean.find(id_oplaty);

            if (oplata == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity(Collections.singletonMap("error", "Oplata o podanym ID nie istnieje."))
                               .build();
            }

            String formattedKwota = decimalFormat.format(oplata.getKwota());
            String formattedDataOplaty = dateFormat.format(oplata.getData_oplaty());
            String formattedDataNastepnejOplaty = dateFormat.format(oplata.getData_nastepnej_oplaty());

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("id_oplaty", oplata.getId_oplaty());
            responseMap.put("kwota", formattedKwota.trim() + " " + oplata.getWaluta());
            responseMap.put("data_oplaty", formattedDataOplaty);
            responseMap.put("data_nastepnej_oplaty", formattedDataNastepnejOplaty);
            responseMap.put("rezerwacja_id", oplata.getRezerwacja().getId_rezerwacji());

            return Response.ok(responseMap).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Collections.singletonMap("error", "Wyst�pil bl�d podczas wyszukiwania oplaty."))
                           .build();
        }
    }

    @GET
    public Response get() {
        try {
            List<Oplaty> oplatyList = bean.get();

            List<Map<String, Object>> responseList = new ArrayList<>();
            for (Oplaty oplata : oplatyList) {
                Map<String, Object> responseMap = new HashMap<>();
                String formattedKwota = decimalFormat.format(oplata.getKwota());
                String formattedDataOplaty = dateFormat.format(oplata.getData_oplaty());
                String formattedDataNastepnejOplaty = dateFormat.format(oplata.getData_nastepnej_oplaty());

                responseMap.put("id_oplaty", oplata.getId_oplaty());
                responseMap.put("kwota", formattedKwota.trim() + " " + oplata.getWaluta());
                responseMap.put("data_oplaty", formattedDataOplaty);
                responseMap.put("data_nastepnej_oplaty", formattedDataNastepnejOplaty);
                responseMap.put("rezerwacja_id", oplata.getRezerwacja().getId_rezerwacji());

                responseList.add(responseMap);
            }

            return Response.ok(responseList).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Collections.singletonMap("error", "Wyst�pil bl�d podczas pobierania oplat."))
                           .build();
        }
    }

    @PUT
    public Response update(Oplaty oplata) {
        try {
            Map<String, String> errors = new HashMap<>();
            if (oplata.getId_oplaty() <= 0) {
                errors.put("id_oplaty", "ID oplaty nie mo�e by� nullem lub pusty.");
            }
            if (oplata.getKwota() <= 0) {
                errors.put("kwota", "Kwota musi by� wi�ksza ni� 0.");
            }
            if (oplata.getWaluta() == null || oplata.getWaluta().isEmpty()) {
                errors.put("waluta", "Waluta nie mo�e by� nullem lub pusty.");
            }
            if (oplata.getData_oplaty() == null) {
                errors.put("data_oplaty", "Data oplaty nie mo�e by� nullem.");
            }
            if (oplata.getData_nastepnej_oplaty() == null) {
                errors.put("data_nastepnej_oplaty", "Data nast�pnej oplaty nie mo�e by� nullem.");
            }
            if (oplata.getRezerwacja() == null || oplata.getRezerwacja().getId_rezerwacji() <= 0) {
                errors.put("rezerwacja", "Rezerwacja musi by� okre�lona i mie� prawidlowe ID.");
            }

            if (!errors.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
            }

            bean.update(oplata);

            return Response.status(Response.Status.CREATED).type(MediaType.TEXT_PLAIN).entity("Oplata zostala zaktualizowana!").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Collections.singletonMap("error", "Oplata nie zostala zaktualizowana."))
                           .build();
        }
    }

    @DELETE
    @Path("/{id_oplaty}")
    public Response delete(@PathParam("id_oplaty") int id_oplaty) {
        try {
            bean.delete(id_oplaty);
            return Response.ok("Op�ata zosta�a usuni�ta!").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Op�ata nie zosta�a usuni�ta.")
                           .build();
        }
    }
}
