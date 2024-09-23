package pl.kurs.akademiki.gui;
import pl.kurs.akademiki.Pokoje;
import pl.kurs.akademiki.Akademiki;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PokojeGUI {
	
    private static JList<Pokoje> pokojeList;
    private static DefaultListModel<Pokoje> listModel;
	
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pokoje GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        

        frame.setVisible(true);
        fetchPokoje();
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel numberLabel = new JLabel("Numer pokoju:");
        numberLabel.setBounds(10, 10, 150, 25);
        panel.add(numberLabel);

        final JTextField numberField = new JTextField(20);
        numberField.setBounds(170, 10, 160, 25);
        panel.add(numberField);

        JLabel placesLabel = new JLabel("Ilosc dostepnych miejsc:");
        placesLabel.setBounds(10, 45, 200, 25);
        panel.add(placesLabel);

        final JTextField placesField = new JTextField(20);
        placesField.setBounds(210, 45, 160, 25);
        panel.add(placesField);

        JLabel bedsLabel = new JLabel("Ilosc dostepnych lozek:");
        bedsLabel.setBounds(10, 80, 200, 25);
        panel.add(bedsLabel);

        final JTextField bedsField = new JTextField(20);
        bedsField.setBounds(210, 80, 160, 25);
        panel.add(bedsField);

        JLabel akademikLabel = new JLabel("Id akademika:");
        akademikLabel.setBounds(10, 115, 200, 25);
        panel.add(akademikLabel);

        final JTextField akademikField = new JTextField(20);
        akademikField.setBounds(210, 115, 160, 25);
        panel.add(akademikField);

        JButton createButton = new JButton("Create Room");
        createButton.setBounds(10, 150, 150, 25);
        panel.add(createButton);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String number = numberField.getText();
                String places = placesField.getText();
                String beds = bedsField.getText();
                String akademik = akademikField.getText();
                createPokoj(number, places, beds, akademik);
            }
        });

        JButton deleteButton = new JButton("Usu  Pokoj");
        deleteButton.setBounds(170, 150, 150, 25);
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = pokojeList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Pokoje selectedPokoj = pokojeList.getModel().getElementAt(selectedIndex);
                    int pokojId = selectedPokoj.getId_pokoju();
                    deletePokoj(pokojId);
                } else {
                    JOptionPane.showMessageDialog(null, "Wybierz pokoj do usuni cia.");
                }
            }
        });

        JButton refreshButton = new JButton("Refresh List");
        refreshButton.setBounds(330, 150, 150, 25);
        panel.add(refreshButton);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchPokoje();
            }
        });

        listModel = new DefaultListModel<>();
        pokojeList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(pokojeList);
        scrollPane.setBounds(10, 200, 760, 350);
        panel.add(scrollPane);
    }

    private static void createPokoj(String numer_pokoju, String ilosc_dostepnych_miejsc, String ilosc_lozek, String id_akademika) {
        try {
            URL url = new URL("http://localhost:8080/take/pokoje");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = "{\"numer_pokoju\":\"" + numer_pokoju + "\", \"ilosc_dostepnych_miejsc\":\"" + ilosc_dostepnych_miejsc + "\", \"ilosc_lozek\":\"" + ilosc_lozek + "\", \"akademiki\":{\"id_akademika\":\"" + id_akademika + "\"}}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Pok j zosta  utworzony!");
                fetchPokoje();
            } else {
                System.out.println("B  d podczas tworzenia pokoju. Kod odpowiedzi: " + code);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    
    private static void fetchPokoje() {
        try {
            URL url = new URL("http://localhost:8080/take/pokoje");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String jsonResponse = response.toString();
            updatePokojeList(parsePokojeResponse(jsonResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private static List<Pokoje> parsePokojeResponse(String jsonResponse) {
        List<Pokoje> pokojeList = new ArrayList<>();

        if (jsonResponse == null || jsonResponse.trim().isEmpty() || jsonResponse.equals("[]")) {
            System.out.println("Brak pokoi w bazie.");
            return pokojeList;
        }
        jsonResponse = jsonResponse.substring(1, jsonResponse.length() - 1);

        String[] pokojeEntries = jsonResponse.split("\\},\\{");

        for (String entry : pokojeEntries) {
            entry = entry.replace("{", "").replace("}", "");
            String[] fields = entry.split(",");

            Pokoje pokoj = new Pokoje();

            for (String field : fields) {
                String[] keyValue = field.split(":");
                String key = keyValue[0].replace("\"", "").trim();
                String value = keyValue[1].replace("\"", "").trim();
                switch (key) {
                    case "id_pokoju":
                        pokoj.setId_pokoju(Integer.parseInt(value));
                        break;
                    case "numer_pokoju":
                        pokoj.setNumer_pokoju(Integer.parseInt(value));
                        break;
                    case "ilosc_dostepnych_miejsc":
                        pokoj.setIlosc_dostepnych_miejsc(Integer.parseInt(value));
                        break;
                    case "ilosc_lozek":
                        pokoj.setIlosc_lozek(Integer.parseInt(value));
                        break;
                    case "id_akademika":
                        Akademiki akademik = new Akademiki();
                        akademik.setId_akademika(Integer.parseInt(value));
                        pokoj.setAkademiki(akademik);
                        break;
                }
            }

            pokojeList.add(pokoj);
        }

        return pokojeList;
    }

    private static void updatePokojeList(List<Pokoje> pokojeNames) {
        listModel.clear();
        
        if (pokojeNames.isEmpty()) {
            System.out.println("Brak pokoi do wy wietlenia.");
        } else {
            for (Pokoje pokoj : pokojeNames) {
                listModel.addElement(pokoj);
            }
        }
    }

    private static void deletePokoj(int pokojId) {
        try {
            String deleteUrl = "http://localhost:8080/take/pokoje/" + pokojId;
            System.out.println("URL for DELETE: " + deleteUrl);

            URL url = new URL(deleteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                System.out.println("Pok j zosta  usuni ty!");
                fetchPokoje();
            } else {
                System.out.println("B  d podczas usuwania pokoju. Kod odpowiedzi: " + responseCode);
                fetchPokoje();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}