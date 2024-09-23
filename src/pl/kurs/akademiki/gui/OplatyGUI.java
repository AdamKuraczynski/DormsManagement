package pl.kurs.akademiki.gui;

import pl.kurs.akademiki.Oplaty;

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

public class OplatyGUI {

    private static JList<Oplaty> oplatyList;
    private static DefaultListModel<Oplaty> listModel;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        JFrame frame = new JFrame("Oplaty GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
        fetchOplaty();
    }

    private static void placeComponents(JPanel panel) {

        panel.setLayout(null);

        JLabel kwotaLabel = new JLabel("Kwota:");
        kwotaLabel.setBounds(10, 10, 80, 25);
        panel.add(kwotaLabel);

        final JTextField kwotaField = new JTextField(20);
        kwotaField.setBounds(100, 10, 160, 25);
        panel.add(kwotaField);

        JLabel walutaLabel = new JLabel("Waluta:");
        walutaLabel.setBounds(10, 40, 80, 25);
        panel.add(walutaLabel);

        final JTextField walutaField = new JTextField(20);
        walutaField.setBounds(100, 40, 160, 25);
        panel.add(walutaField);

        JLabel dataOplatyLabel = new JLabel("Data Oplaty:");
        dataOplatyLabel.setBounds(10, 70, 80, 25);
        panel.add(dataOplatyLabel);

        final JTextField dataOplatyField = new JTextField(20);
        dataOplatyField.setBounds(100, 70, 160, 25);
        panel.add(dataOplatyField);

        JLabel dataNastepnejOplatyLabel = new JLabel("Data Nast. Oplaty:");
        dataNastepnejOplatyLabel.setBounds(10, 100, 120, 25);
        panel.add(dataNastepnejOplatyLabel);

        final JTextField dataNastepnejOplatyField = new JTextField(20);
        dataNastepnejOplatyField.setBounds(130, 100, 160, 25);
        panel.add(dataNastepnejOplatyField);

        JLabel rezerwacjaLabel = new JLabel("ID Rezerwacji:");
        rezerwacjaLabel.setBounds(10, 130, 100, 25);
        panel.add(rezerwacjaLabel);

        final JTextField rezerwacjaField = new JTextField(20);
        rezerwacjaField.setBounds(120, 130, 160, 25);
        panel.add(rezerwacjaField);

        JButton createButton = new JButton("Create Payment");
        createButton.setBounds(10, 170, 150, 25);
        panel.add(createButton);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String kwotaStr = kwotaField.getText();
                String waluta = walutaField.getText();
                String dataOplatyStr = dataOplatyField.getText();
                String dataNastepnejOplatyStr = dataNastepnejOplatyField.getText();
                String idRezerwacjiStr = rezerwacjaField.getText();
                createOplata(kwotaStr, waluta, dataOplatyStr, dataNastepnejOplatyStr, idRezerwacjiStr);
            }
        });

        JButton refreshButton = new JButton("Refresh List");
        refreshButton.setBounds(170, 170, 150, 25);
        panel.add(refreshButton);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchOplaty();
            }
        });

        JButton updateButton = new JButton("Update Payment");
        updateButton.setBounds(330, 170, 150, 25);
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = oplatyList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Oplaty selectedOplata = oplatyList.getModel().getElementAt(selectedIndex);
                    int idOplaty = selectedOplata.getId_oplaty();

                    String kwotaStr = kwotaField.getText();
                    String waluta = walutaField.getText();
                    String dataOplatyStr = dataOplatyField.getText();
                    String dataNastepnejOplatyStr = dataNastepnejOplatyField.getText();
                    String idRezerwacjiStr = rezerwacjaField.getText();

                    updateOplata(idOplaty, kwotaStr, waluta, dataOplatyStr, dataNastepnejOplatyStr, idRezerwacjiStr);
                } else {
                    JOptionPane.showMessageDialog(null, "Wybierz oplate do aktualizacji.");
                }
            }
        });

        JButton deleteButton = new JButton("Delete Payment");
        deleteButton.setBounds(490, 170, 150, 25);
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = oplatyList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Oplaty selectedOplata = oplatyList.getModel().getElementAt(selectedIndex);
                    int idOplaty = selectedOplata.getId_oplaty();
                    System.out.println("Id Oplaty do usuniecia: " + idOplaty);
                    if (idOplaty > 0) {
                        deleteOplata(idOplaty);
                    } else {
                        System.out.println("Id Oplaty jest nieprawid�owe: " + idOplaty);
                        JOptionPane.showMessageDialog(null, "Nieprawid�owe ID op�aty.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Wybierz oplate do usuniecia.");
                }
            }
        });



        listModel = new DefaultListModel<>();
        oplatyList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(oplatyList);
        scrollPane.setBounds(10, 210, 760, 340);
        panel.add(scrollPane);
    }

    private static void createOplata(String kwotaStr, String waluta, String dataOplatyStr, String dataNastepnejOplatyStr, String idRezerwacjiStr) {
        try {
            if (kwotaStr == null || kwotaStr.trim().isEmpty() ||
                waluta == null || waluta.trim().isEmpty() ||
                dataOplatyStr == null || dataOplatyStr.trim().isEmpty() ||
                dataNastepnejOplatyStr == null || dataNastepnejOplatyStr.trim().isEmpty() ||
                idRezerwacjiStr == null || idRezerwacjiStr.trim().isEmpty()) {

                JOptionPane.showMessageDialog(null, "All fields are required and must be valid.");
                return;
            }

            double kwota = Double.parseDouble(kwotaStr);
            int idRezerwacji = Integer.parseInt(idRezerwacjiStr);
            Date dataOplaty = dateFormat.parse(dataOplatyStr);
            Date dataNastepnejOplaty = dateFormat.parse(dataNastepnejOplatyStr);

            URL url = new URL("http://localhost:8080/take/oplaty");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = String.format(
                "{\"kwota\": \"%s\", \"waluta\": \"%s\", \"data_oplaty\": \"%s\", \"data_nastepnej_oplaty\": \"%s\", \"rezerwacja\": {\"id_rezerwacji\": %d}}",
                kwota, waluta, dataOplatyStr, dataNastepnejOplatyStr, idRezerwacji
            );

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_CREATED || code == HttpURLConnection.HTTP_OK) {
                System.out.println("Oplata zostala utworzona!");
                fetchOplaty();
            } else if (code == HttpURLConnection.HTTP_BAD_REQUEST) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Blad podczas tworzenia oplaty. Szczegoly: " + response.toString());
                JOptionPane.showMessageDialog(null, "Blad podczas tworzenia oplaty:\n" + response.toString());
            } else {
                System.out.println("Blad podczas tworzenia oplaty. Kod odpowiedzi: " + code);
                JOptionPane.showMessageDialog(null, "Blad podczas tworzenia oplaty. Kod odpowiedzi: " + code);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Wyjatek podczas tworzenia oplaty:\n" + ex.getMessage());
        }
    }


    private static void fetchOplaty() {
        try {
            URL url = new URL("http://localhost:8080/take/oplaty");
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
            updateOplatyList(parseOplatyResponse(jsonResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Oplaty> parseOplatyResponse(String jsonResponse) {
        List<Oplaty> oplatyList = new ArrayList<>();

        if (jsonResponse == null || jsonResponse.trim().isEmpty() || jsonResponse.equals("[]")) {
            System.out.println("Brak oplat w bazie.");
            return oplatyList;
        }

        jsonResponse = jsonResponse.trim();
        if (jsonResponse.startsWith("[") && jsonResponse.endsWith("]")) {
            jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);
        }

        List<String> oplatyJsonList = splitJsonObjects(jsonResponse);

        for (String oplataJson : oplatyJsonList) {
            try {
                Oplaty oplata = parseOplataJson(oplataJson);
                oplatyList.add(oplata);
            } catch (Exception e) {
                System.out.println("Blad parsowania oplaty: " + e.getMessage());
            }
        }

        return oplatyList;
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
                    // End of an object
                    jsonObjects.add(currentObject.toString().trim());
                    currentObject.setLength(0);
                }
            }
        }

        return jsonObjects;
    }


    private static Oplaty parseOplataJson(String oplataJson) {
        Oplaty oplata = new Oplaty();

        oplataJson = oplataJson.trim();
        if (oplataJson.startsWith("{") && oplataJson.endsWith("}")) {
            oplataJson = oplataJson.substring(1, oplataJson.length() - 1);
        }

        Map<String, String> fields = new HashMap<>();
        String[] keyValuePairs = oplataJson.split(",");

        for (String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            if (entry.length == 2) {
                String key = entry[0].trim().replace("\"", "");
                String value = entry[1].trim().replace("\"", "");
                fields.put(key, value);
            }
        }

        oplata.setId_oplaty(Integer.parseInt(fields.getOrDefault("id_oplaty", "0")));

        String kwotaStr = fields.getOrDefault("kwota", "0 PLN");
        String[] kwotaParts = kwotaStr.split(" ");
        if (kwotaParts.length == 2) {
            oplata.setKwota(Double.parseDouble(kwotaParts[0]));
            oplata.setWaluta(kwotaParts[1]);
        } else {
            oplata.setKwota(Double.parseDouble(kwotaParts[0]));
            oplata.setWaluta("");
        }

        try {
            Date dataOplaty = dateFormat.parse(fields.getOrDefault("data_oplaty", ""));
            oplata.setData_oplaty(dataOplaty);
        } catch (Exception e) {
            oplata.setData_oplaty(null);
        }

        try {
            Date dataNastepnejOplaty = dateFormat.parse(fields.getOrDefault("data_nastepnej_oplaty", ""));
            oplata.setData_nastepnej_oplaty(dataNastepnejOplaty);
        } catch (Exception e) {
            oplata.setData_nastepnej_oplaty(null);
        }

        return oplata;
    }


    private static void updateOplatyList(List<Oplaty> oplaty) {
        listModel.clear();

        if (oplaty.isEmpty()) {
            System.out.println("Brak oplat do wyswietlenia.");
        } else {
            for (Oplaty oplata : oplaty) {
                listModel.addElement(oplata);
            }
        }
    }

    private static void updateOplata(int idOplaty, String kwotaStr, String waluta, String dataOplatyStr, String dataNastepnejOplatyStr, String idRezerwacjiStr) {
        try {
            if (kwotaStr == null || kwotaStr.trim().isEmpty() ||
                waluta == null || waluta.trim().isEmpty() ||
                dataOplatyStr == null || dataOplatyStr.trim().isEmpty() ||
                dataNastepnejOplatyStr == null || dataNastepnejOplatyStr.trim().isEmpty() ||
                idRezerwacjiStr == null || idRezerwacjiStr.trim().isEmpty()) {

                JOptionPane.showMessageDialog(null, "All fields are required and must be valid.");
                return;
            }

            double kwota = Double.parseDouble(kwotaStr);
            int idRezerwacji = Integer.parseInt(idRezerwacjiStr);
            Date dataOplaty = dateFormat.parse(dataOplatyStr);
            Date dataNastepnejOplaty = dateFormat.parse(dataNastepnejOplatyStr);

            String updateUrl = "http://localhost:8080/take/oplaty";
            System.out.println("URL for UPDATE: " + updateUrl);

            URL url = new URL(updateUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            String jsonInputString = String.format(
                "{\"id_oplaty\": %d, \"kwota\": %s, \"waluta\": \"%s\", \"data_oplaty\": \"%s\", \"data_nastepnej_oplaty\": \"%s\", \"rezerwacja\": {\"id_rezerwacji\": %d}}",
                idOplaty, kwota, waluta, dataOplatyStr, dataNastepnejOplatyStr, idRezerwacji
            );
            
            System.out.println(jsonInputString);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Oplata zostala zaktualizowana!");
                fetchOplaty();
            } else {
                System.out.println("Blad podczas aktualizacji oplaty. Kod odpowiedzi: " + responseCode);
                JOptionPane.showMessageDialog(null, "Blad podczas aktualizacji oplaty. Kod odpowiedzi: " + responseCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Wyjatek podczas aktualizacji oplaty:\n" + ex.getMessage());
        }
    }

    private static void deleteOplata(int idOplaty) {
        try {
            String deleteUrl = "http://localhost:8080/take/oplaty/" + idOplaty;
            System.out.println("URL for DELETE: " + deleteUrl);

            URL url = new URL(deleteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                System.out.println("Oplata zostala usunieta!");
                fetchOplaty();
            } else {
                System.out.println("Blad podczas usuwania oplaty. Kod odpowiedzi: " + responseCode);
                JOptionPane.showMessageDialog(null, "Blad podczas usuwania oplaty. Kod odpowiedzi: " + responseCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Wyjatek podczas usuwania oplaty:\n" + ex.getMessage());
        }
    }
}
