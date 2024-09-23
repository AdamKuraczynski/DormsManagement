package pl.kurs.akademiki.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainFrame {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(null);

        JButton oplatyButton = new JButton("Oplaty");
        oplatyButton.setBounds(50, 50, 200, 50);
        frame.add(oplatyButton);
        oplatyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OplatyGUI oplatyGUI = new OplatyGUI();
                oplatyGUI.main(new String[]{});
            }
        });

        JButton rezerwacjaButton = new JButton("Rezerwacje");
        rezerwacjaButton.setBounds(50, 120, 200, 50);
        frame.add(rezerwacjaButton);
        rezerwacjaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RezerwacjaGUI rezerwacjaGUI = new RezerwacjaGUI();
                rezerwacjaGUI.main(new String[]{});
            }
        });

        JButton studenciButton = new JButton("Studenci");
        studenciButton.setBounds(50, 190, 200, 50);
        frame.add(studenciButton);
        studenciButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudenciGUI studenciGUI = new StudenciGUI();
                studenciGUI.main(new String[]{});
            }
        });

        JButton pokojeButton = new JButton("Pokoje");
        pokojeButton.setBounds(50, 260, 200, 50);
        frame.add(pokojeButton);
        pokojeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PokojeGUI pokojeGUI = new PokojeGUI();
                pokojeGUI.main(new String[]{}); 
            }
        });

        JButton akademikiButton = new JButton("Akademiki");
        akademikiButton.setBounds(50, 330, 200, 50);
        frame.add(akademikiButton);
        akademikiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AkademikiGUI akademikiGUI = new AkademikiGUI();
                akademikiGUI.main(new String[]{});
            }
        });

        JLabel studentIdLabel = new JLabel("Student ID:");
        studentIdLabel.setBounds(300, 50, 100, 25);
        frame.add(studentIdLabel);

        final JTextField studentIdField = new JTextField();
        studentIdField.setBounds(400, 50, 150, 25);
        frame.add(studentIdField);

        JButton showReservationsButton = new JButton("Show Reservations");
        showReservationsButton.setBounds(300, 90, 200, 50);
        frame.add(showReservationsButton);

        showReservationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentId = studentIdField.getText();
                if (studentId == null || studentId.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid Student ID.");
                } else {
                    fetchReservationsForStudent(studentId);
                }
            }
        });

        frame.setVisible(true);
    }

    private static void fetchReservationsForStudent(String studentId) {
        try {
            String apiUrl = "http://localhost:8080/take/rezerwacje";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            List<String> reservations = parseReservations(response.toString(), studentId);
            if (reservations.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No reservations found for student ID: " + studentId);
            } else {
                StringBuilder reservationsText = new StringBuilder("Reservations for Student ID " + studentId + ":\n");
                for (String reservation : reservations) {
                    reservationsText.append(reservation).append("\n");
                }
                JOptionPane.showMessageDialog(null, reservationsText.toString());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching reservations for Student ID " + studentId + ".\n" + ex.getMessage());
        }
    }

    private static List<String> parseReservations(String jsonResponse, String studentId) {
        List<String> reservations = new ArrayList<>();

        try {
            jsonResponse = jsonResponse.trim();
            if (jsonResponse.startsWith("[") && jsonResponse.endsWith("]")) {
                jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);
            }
            String[] reservationEntries = jsonResponse.split("\\},\\{");

            for (String entry : reservationEntries) {
                entry = entry.startsWith("{") ? entry : "{" + entry;
                entry = entry.endsWith("}") ? entry : entry + "}";

                String studentIdFromJson = extractJsonValue(entry, "\"id_studenta\":", ",");

                if (studentIdFromJson.equals(studentId)) {
                    int idRezerwacji = Integer.parseInt(extractJsonValue(entry, "\"id_rezerwacji\":", ","));
                    String dataRozpoczecia = extractJsonValue(entry, "\"data_rozpoczecia\":", ",").replace("\"", "");
                    String dataZakonczenia = extractJsonValue(entry, "\"data_zakonczenia\":", ",").replace("\"", "");
                    String imie = extractJsonValue(entry, "\"imie\":", ",").replace("\"", "");
                    String nazwisko = extractJsonValue(entry, "\"nazwisko\":", ",").replace("\"", "");
                    int numerPokoju = Integer.parseInt(extractJsonValue(entry, "\"numer_pokoju\":", ","));
                    String akademikNazwa = extractJsonValue(entry, "\"nazwa\":", "}").replace("\"", "");

                    String reservationString = String.format(
                        "Id rezerwacji: %d, Data rozpoczecia: %s, Data zakonczenia: %s, Student: %s %s, Pokoj: %d, Akademik: %s",
                        idRezerwacji, dataRozpoczecia, dataZakonczenia, imie, nazwisko, numerPokoju, akademikNazwa
                    );
                    reservations.add(reservationString);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reservations;
    }

    private static String extractJsonValue(String json, String startPattern, String endPattern) {
        try {
            int startIndex = json.indexOf(startPattern);
            if (startIndex == -1) return "";
            startIndex += startPattern.length();
            int endIndex = json.indexOf(endPattern, startIndex);
            if (endIndex == -1) endIndex = json.length();
            return json.substring(startIndex, endIndex).trim();
        } catch (Exception e) {
            return "";
        }
    }

}