package pl.kurs.akademiki.gui;

import pl.kurs.akademiki.Rezerwacja;
import pl.kurs.akademiki.Studenci;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RezerwacjaGUI {

    private static JList<Rezerwacja> rezerwacjaList;
    private static DefaultListModel<Rezerwacja> listModel;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        JFrame frame = new JFrame("Rezerwacja GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
        fetchRezerwacje();
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel dataRozpoczeciaLabel = new JLabel("Data Rozpoczecia:");
        dataRozpoczeciaLabel.setBounds(10, 10, 120, 25);
        panel.add(dataRozpoczeciaLabel);

        final JTextField dataRozpoczeciaField = new JTextField(20);
        dataRozpoczeciaField.setBounds(140, 10, 160, 25);
        panel.add(dataRozpoczeciaField);

        JLabel dataZakonczeniaLabel = new JLabel("Data Zakonczenia:");
        dataZakonczeniaLabel.setBounds(10, 40, 120, 25);
        panel.add(dataZakonczeniaLabel);

        final JTextField dataZakonczeniaField = new JTextField(20);
        dataZakonczeniaField.setBounds(140, 40, 160, 25);
        panel.add(dataZakonczeniaField);

        JLabel studentLabel = new JLabel("ID Studenta:");
        studentLabel.setBounds(10, 70, 100, 25);
        panel.add(studentLabel);

        final JTextField studentField = new JTextField(20);
        studentField.setBounds(120, 70, 160, 25);
        panel.add(studentField);

        JButton createButton = new JButton("Create Reservation");
        createButton.setBounds(10, 110, 160, 25);
        panel.add(createButton);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dataRozpoczeciaStr = dataRozpoczeciaField.getText();
                String dataZakonczeniaStr = dataZakonczeniaField.getText();
                String idStudentaStr = studentField.getText();
                createRezerwacja(dataRozpoczeciaStr, dataZakonczeniaStr, idStudentaStr);
            }
        });

        JButton refreshButton = new JButton("Refresh List");
        refreshButton.setBounds(180, 110, 150, 25);
        panel.add(refreshButton);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchRezerwacje();
            }
        });

        JButton updateButton = new JButton("Update Reservation");
        updateButton.setBounds(340, 110, 160, 25);
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = rezerwacjaList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Rezerwacja selectedRezerwacja = rezerwacjaList.getModel().getElementAt(selectedIndex);
                    int idRezerwacji = selectedRezerwacja.getId_rezerwacji();

                    String dataRozpoczeciaStr = dataRozpoczeciaField.getText();
                    String dataZakonczeniaStr = dataZakonczeniaField.getText();
                    String idStudentaStr = studentField.getText();

                    updateRezerwacja(idRezerwacji, dataRozpoczeciaStr, dataZakonczeniaStr, idStudentaStr);
                } else {
                    JOptionPane.showMessageDialog(null, "Wybierz rezerwacje do aktualizacji.");
                }
            }
        });

        JButton deleteButton = new JButton("Delete Reservation");
        deleteButton.setBounds(510, 110, 150, 25);
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = rezerwacjaList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Rezerwacja selectedRezerwacja = rezerwacjaList.getModel().getElementAt(selectedIndex);
                    int idRezerwacji = selectedRezerwacja.getId_rezerwacji();
                    deleteRezerwacja(idRezerwacji);
                } else {
                    JOptionPane.showMessageDialog(null, "Wybierz rezerwacje do usuniecia.");
                }
            }
        });

        listModel = new DefaultListModel<>();
        rezerwacjaList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(rezerwacjaList);
        scrollPane.setBounds(10, 150, 760, 400);
        panel.add(scrollPane);
    }

    private static void createRezerwacja(String dataRozpoczeciaStr, String dataZakonczeniaStr, String idStudentaStr) {
        try {
            if (dataRozpoczeciaStr == null || dataRozpoczeciaStr.trim().isEmpty() ||
                dataZakonczeniaStr == null || dataZakonczeniaStr.trim().isEmpty() ||
                idStudentaStr == null || idStudentaStr.trim().isEmpty()) {

                JOptionPane.showMessageDialog(null, "All fields are required and must be valid.");
                return;
            }

            int idStudenta = Integer.parseInt(idStudentaStr);
            Date dataRozpoczecia = dateFormat.parse(dataRozpoczeciaStr);
            Date dataZakonczenia = dateFormat.parse(dataZakonczeniaStr);

            if (dataZakonczenia.before(dataRozpoczecia)) {
                JOptionPane.showMessageDialog(null, "Data Zakonczenia must be after Data Rozpoczecia.");
                return;
            }

            URL url = new URL("http://localhost:8080/take/rezerwacje");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = String.format(
                "{\"data_rozpoczecia\": \"%s\", \"data_zakonczenia\": \"%s\", \"student\": {\"id_studenta\": %d}}",
                dataRozpoczeciaStr, dataZakonczeniaStr, idStudenta
            );

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_CREATED || code == HttpURLConnection.HTTP_OK) {
                System.out.println("Rezerwacja zostala utworzona!");
                fetchRezerwacje();
            } else if (code == HttpURLConnection.HTTP_BAD_REQUEST) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JOptionPane.showMessageDialog(null, "Blad podczas tworzenia rezerwacji:\n" + response.toString());
            } else {
                JOptionPane.showMessageDialog(null, "Blad podczas tworzenia rezerwacji. Kod odpowiedzi: " + code);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Wyjatek podczas tworzenia rezerwacji:\n" + ex.getMessage());
        }
    }

    private static void fetchRezerwacje() {
        try {
            URL url = new URL("http://localhost:8080/take/rezerwacje");
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

            String jsonResponse = response.toString();
            updateRezerwacjeList(parseRezerwacjeResponse(jsonResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Rezerwacja> parseRezerwacjeResponse(String jsonResponse) {
        List<Rezerwacja> rezerwacjeList = new ArrayList<>();

        if (jsonResponse == null || jsonResponse.trim().isEmpty() || jsonResponse.equals("[]")) {
            return rezerwacjeList;
        }

        jsonResponse = jsonResponse.trim();
        if (jsonResponse.startsWith("[") && jsonResponse.endsWith("]")) {
            jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);
        }

        List<String> rezerwacjeJsonList = splitJsonObjects(jsonResponse);

        for (String rezerwacjaJson : rezerwacjeJsonList) {
            try {
                Rezerwacja rezerwacja = parseRezerwacjaJson(rezerwacjaJson);
                rezerwacjeList.add(rezerwacja);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rezerwacjeList;
    }


    private static List<String> splitJsonObjects(String json) {
        List<String> jsonObjects = new ArrayList<>();
        int braceDepth = 0;
        StringBuilder currentObject = new StringBuilder();

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            currentObject.append(c);

            if (c == '{') {
                braceDepth++;
            } else if (c == '}') {
                braceDepth--;
                if (braceDepth == 0) {
                    jsonObjects.add(currentObject.toString().trim());
                    currentObject.setLength(0);
                }
            }
        }

        return jsonObjects;
    }

    private static Rezerwacja parseRezerwacjaJson(String rezerwacjaJson) {
        Rezerwacja rezerwacja = new Rezerwacja();

        rezerwacjaJson = rezerwacjaJson.trim();
        if (rezerwacjaJson.startsWith("{") && rezerwacjaJson.endsWith("}")) {
            rezerwacjaJson = rezerwacjaJson.substring(1, rezerwacjaJson.length() - 1);
        }

        String[] keyValuePairs = rezerwacjaJson.split(",(?=\\s*\"[^\"]+\":)");

        for (String keyValuePair : keyValuePairs) {
            String[] keyValue = keyValuePair.split(":");

            if (keyValue.length != 2) {
                continue;
            }

            String key = keyValue[0].replace("\"", "").trim();
            String value = keyValue[1].replace("\"", "").trim();

            if (key.contains("student") || key.contains("pokoj") || key.contains("akademiki")) {
                continue;
            }

            switch (key) {
                case "id_rezerwacji":
                    rezerwacja.setId_rezerwacji(Integer.parseInt(value));
                    break;
                case "data_rozpoczecia":
                    try {
                        rezerwacja.setData_rozpoczecia(dateFormat.parse(value));
                    } catch (Exception e) {
                        rezerwacja.setData_rozpoczecia(null);
                    }
                    break;
                case "data_zakonczenia":
                    try {
                        rezerwacja.setData_zakonczenia(dateFormat.parse(value));
                    } catch (Exception e) {
                        rezerwacja.setData_zakonczenia(null);
                    }
                    break;
                case "student_id":
                    Studenci student = new Studenci();
                    student.setId_studenta(Integer.parseInt(value));
                    rezerwacja.setStudent(student);
                    break;
                default:
                    break;
            }
        }

        return rezerwacja;
    }



    private static void updateRezerwacjeList(List<Rezerwacja> rezerwacje) {
        listModel.clear();

        if (rezerwacje.isEmpty()) {
            System.out.println("Brak rezerwacji do wyswietlenia.");
        } else {
            for (Rezerwacja rezerwacja : rezerwacje) {
                listModel.addElement(rezerwacja);
            }
        }
    }

    private static void updateRezerwacja(int idRezerwacji, String dataRozpoczeciaStr, String dataZakonczeniaStr, String idStudentaStr) {
        try {
            if (dataRozpoczeciaStr == null || dataRozpoczeciaStr.trim().isEmpty() ||
                dataZakonczeniaStr == null || dataZakonczeniaStr.trim().isEmpty() ||
                idStudentaStr == null || idStudentaStr.trim().isEmpty()) {

                JOptionPane.showMessageDialog(null, "All fields are required and must be valid.");
                return;
            }

            int idStudenta = Integer.parseInt(idStudentaStr);
            Date dataRozpoczecia = dateFormat.parse(dataRozpoczeciaStr);
            Date dataZakonczenia = dateFormat.parse(dataZakonczeniaStr);

            if (dataZakonczenia.before(dataRozpoczecia)) {
                JOptionPane.showMessageDialog(null, "Data Zakonczenia must be after Data Rozpoczecia.");
                return;
            }

            String updateUrl = "http://localhost:8080/take/rezerwacje";

            URL url = new URL(updateUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            String jsonInputString = String.format(
                "{\"id_rezerwacji\": %d, \"data_rozpoczecia\": \"%s\", \"data_zakonczenia\": \"%s\", \"student\": {\"id_studenta\": %d}}",
                idRezerwacji, dataRozpoczeciaStr, dataZakonczeniaStr, idStudenta
            );

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                fetchRezerwacje();
            } else {
                JOptionPane.showMessageDialog(null, "Blad podczas aktualizacji rezerwacji. Kod odpowiedzi: " + responseCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Wyjatek podczas aktualizacji rezerwacji:\n" + ex.getMessage());
        }
    }

    private static void deleteRezerwacja(int idRezerwacji) {
        try {
            String deleteUrl = "http://localhost:8080/take/rezerwacje/" + idRezerwacji;
            System.out.println("Delete URL: " + deleteUrl);
            
            URL url = new URL(deleteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code for DELETE: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                System.out.println("Rezerwacja successfully deleted!");
                fetchRezerwacje();
            } else {
                System.out.println("Error during deletion. Response code: " + responseCode);
                JOptionPane.showMessageDialog(null, "Blad podczas usuwania rezerwacji. Kod odpowiedzi: " + responseCode);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Wyjatek podczas usuwania rezerwacji:\n" + ex.getMessage());
        }
    }


}
