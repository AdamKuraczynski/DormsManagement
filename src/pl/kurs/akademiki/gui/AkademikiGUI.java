package pl.kurs.akademiki.gui;
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

public class AkademikiGUI {
	
    private static JList<Akademiki> akademikList;
    private static DefaultListModel<Akademiki> listModel;
	
    public static void main(String[] args) {
        JFrame frame = new JFrame("Akademiki GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        

        frame.setVisible(true);
        fetchAkademiki();
    }

    private static void placeComponents(JPanel panel) {
    	
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("Nazwa:");
        nameLabel.setBounds(10, 10, 80, 25);
        panel.add(nameLabel);

        final JTextField nameField = new JTextField(20);
        nameField.setBounds(100, 10, 160, 25);
        panel.add(nameField);

        JLabel addressLabel = new JLabel("Adres:");
        addressLabel.setBounds(10, 40, 80, 25);
        panel.add(addressLabel);

        final JTextField addressField = new JTextField(20);
        addressField.setBounds(100, 40, 160, 25);
        panel.add(addressField);

        JLabel telefonLabel = new JLabel("Telefon:");
        telefonLabel.setBounds(10, 70, 80, 25);
        panel.add(telefonLabel);

        final JTextField telefonField = new JTextField(20);
        telefonField.setBounds(100, 70, 160, 25);
        panel.add(telefonField);

        JButton createButton = new JButton("Create Akademik");
        createButton.setBounds(10, 110, 150, 25);
        panel.add(createButton);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String address = addressField.getText();
                String telefon = telefonField.getText();
                createAkademik(name, address, telefon);
            }
        });
        
        JButton deleteButton = new JButton("Delete Akademik");
        deleteButton.setBounds(490, 110, 150, 25);
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = akademikList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Akademiki selectedAkademik = akademikList.getModel().getElementAt(selectedIndex);
                    int akademikId = selectedAkademik.getId_akademika();
                    deleteAkademik(akademikId);
                } else {
                    JOptionPane.showMessageDialog(null, "Wybierz akademika do usuni�cia.");
                }
            }
        });

        
        JButton refreshButton = new JButton("Refresh List");
        refreshButton.setBounds(170, 110, 150, 25);
        panel.add(refreshButton);
        
        listModel = new DefaultListModel<>();
        akademikList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(akademikList);
        scrollPane.setBounds(10, 220, 760, 300);
        panel.add(scrollPane);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchAkademiki();
            }
        });
        
        JButton updateButton = new JButton("Update Akademik");
        updateButton.setBounds(330, 110, 150, 25);
        panel.add(updateButton);
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = akademikList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Akademiki selectedAkademik = akademikList.getModel().getElementAt(selectedIndex);
                    int akademikId = selectedAkademik.getId_akademika();
                    String name = nameField.getText();
                    String address = addressField.getText();
                    String telefon = telefonField.getText();
                    updateAkademik(akademikId, name, address, telefon);
                } else {
                    JOptionPane.showMessageDialog(null, "Wybierz akademika do aktualizacji.");
                }
            }
        });
    }
    
    private static void createAkademik(String nazwa, String adres, String telefon) {
        try {
            URL url = new URL("http://localhost:8080/take/akademiki");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = String.format("{\"nazwa\": \"%s\", \"adres\": \"%s\", \"telefon\": \"%s\"}", nazwa, adres, telefon);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                System.out.println("Akademik zosta� utworzony!");
                fetchAkademiki();
            } else {
                System.out.println("B��d podczas tworzenia akademika.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void fetchAkademiki() {
        try {
            URL url = new URL("http://localhost:8080/take/akademiki");
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
            updateAkademikList(parseAkademikResponse(jsonResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static List<Akademiki> parseAkademikResponse(String jsonResponse) {
        List<Akademiki> akademikList = new ArrayList<>();
        
        if (jsonResponse == null || jsonResponse.trim().isEmpty() || jsonResponse.equals("[]")) {
            System.out.println("Brak akademikow w bazie.");
            return akademikList;
        }

        jsonResponse = jsonResponse.replace("[", "").replace("]", "");
        String[] akademikEntries = jsonResponse.split("\\},\\{");

        for (String entry : akademikEntries) {
            entry = entry.replace("{", "").replace("}", "");
            String[] parts = entry.split(",");

            try {
                int id = Integer.parseInt(parts[0].split(":")[1].replace("\"", ""));
                String nazwa = parts[1].split(":")[1].replace("\"", "");
                String adres = parts[2].split(":")[1].replace("\"", "");
                String telefon = parts[3].split(":")[1].replace("\"", "");

                Akademiki akademik = new Akademiki();
                akademik.setId_akademika(id);
                akademik.setNazwa(nazwa);
                akademik.setAdres(adres);
                akademik.setTelefon(telefon);

                akademikList.add(akademik);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Blad parsowania akademika: " + entry);
            }
        }

        return akademikList;
    }

    private static void updateAkademikList(List<Akademiki> akademikNames) {
        listModel.clear();
        
        if (akademikNames.isEmpty()) {
            System.out.println("Brak akademikow do wyswietlenia.");
        } else {
            for (Akademiki akademik : akademikNames) {
                listModel.addElement(akademik);
            }
        }
    }

    private static void deleteAkademik(int akademikId) {
        try {
            String deleteUrl = "http://localhost:8080/take/akademiki/" + akademikId;
            System.out.println("URL for DELETE: " + deleteUrl);

            URL url = new URL(deleteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                System.out.println("Akademik zostal usuniety!");
                fetchAkademiki();
            } else {
                System.out.println("Blad podczas usuwania akademika. Kod odpowiedzi: " + responseCode);
                fetchAkademiki();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void updateAkademik(int akademikId, String nazwa, String adres, String telefon) {
        try {
            String updateUrl = "http://localhost:8080/take/akademiki";
            System.out.println("URL for UPDATE: " + updateUrl);

            URL url = new URL(updateUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            String jsonInputString = String.format(
                "{\"id_akademika\": %d, \"nazwa\": \"%s\", \"adres\": \"%s\", \"telefon\": \"%s\"}",
                akademikId, nazwa, adres, telefon
            );

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Akademik zostal zaktualizowany!");
                fetchAkademiki();
            } else {
                System.out.println("Blad podczas aktualizacji akademika. Kod odpowiedzi: " + responseCode);
                fetchAkademiki();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}