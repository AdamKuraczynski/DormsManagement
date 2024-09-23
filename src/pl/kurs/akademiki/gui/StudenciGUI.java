package pl.kurs.akademiki.gui;

import pl.kurs.akademiki.Studenci;

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

public class StudenciGUI {

    private static JList<Studenci> studenciList;
    private static DefaultListModel<Studenci> listModel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Studenci GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
        fetchStudenci();
    }

    private static void placeComponents(JPanel panel) {

        panel.setLayout(null);

        JLabel imieLabel = new JLabel("Imie:");
        imieLabel.setBounds(10, 10, 80, 25);
        panel.add(imieLabel);

        final JTextField imieField = new JTextField(20);
        imieField.setBounds(100, 10, 160, 25);
        panel.add(imieField);

        JLabel nazwiskoLabel = new JLabel("Nazwisko:");
        nazwiskoLabel.setBounds(10, 40, 80, 25);
        panel.add(nazwiskoLabel);

        final JTextField nazwiskoField = new JTextField(20);
        nazwiskoField.setBounds(100, 40, 160, 25);
        panel.add(nazwiskoField);

        JLabel numerIndeksuLabel = new JLabel("Nr Indeksu:");
        numerIndeksuLabel.setBounds(10, 70, 80, 25);
        panel.add(numerIndeksuLabel);

        final JTextField numerIndeksuField = new JTextField(20);
        numerIndeksuField.setBounds(100, 70, 160, 25);
        panel.add(numerIndeksuField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, 100, 80, 25);
        panel.add(emailLabel);

        final JTextField emailField = new JTextField(20);
        emailField.setBounds(100, 100, 160, 25);
        panel.add(emailField);

        JLabel pokojLabel = new JLabel("ID Pokoju:");
        pokojLabel.setBounds(10, 130, 80, 25);
        panel.add(pokojLabel);

        final JTextField pokojField = new JTextField(20);
        pokojField.setBounds(100, 130, 160, 25);
        panel.add(pokojField);

        JButton createButton = new JButton("Create Student");
        createButton.setBounds(10, 170, 150, 25);
        panel.add(createButton);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String imie = imieField.getText();
                String nazwisko = nazwiskoField.getText();
                String numerIndeksu = numerIndeksuField.getText();
                String email = emailField.getText();
                String idPokojuStr = pokojField.getText();
                int idPokoju = idPokojuStr.isEmpty() ? 0 : Integer.parseInt(idPokojuStr);
                createStudent(imie, nazwisko, numerIndeksu, email, idPokoju);
            }
        });

        JButton refreshButton = new JButton("Refresh List");
        refreshButton.setBounds(170, 170, 150, 25);
        panel.add(refreshButton);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchStudenci();
            }
        });

        JButton updateButton = new JButton("Update Student");
        updateButton.setBounds(330, 170, 150, 25);
        panel.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = studenciList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Studenci selectedStudent = studenciList.getModel().getElementAt(selectedIndex);
                    int studentId = selectedStudent.getId_studenta();
                    String imie = imieField.getText();
                    String nazwisko = nazwiskoField.getText();
                    String numerIndeksu = numerIndeksuField.getText();
                    String email = emailField.getText();
                    String idPokojuStr = pokojField.getText();
                    int idPokoju = idPokojuStr.isEmpty() ? 0 : Integer.parseInt(idPokojuStr);
                    updateStudent(studentId, imie, nazwisko, numerIndeksu, email, idPokoju);
                } else {
                    JOptionPane.showMessageDialog(null, "Wybierz studenta do aktualizacji.");
                }
            }
        });

        JButton deleteButton = new JButton("Delete Student");
        deleteButton.setBounds(490, 170, 150, 25);
        panel.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = studenciList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Studenci selectedStudent = studenciList.getModel().getElementAt(selectedIndex);
                    int studentId = selectedStudent.getId_studenta();
                    deleteStudent(studentId);
                } else {
                    JOptionPane.showMessageDialog(null, "Wybierz studenta do usuniecia.");
                }
            }
        });

        listModel = new DefaultListModel<>();
        studenciList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(studenciList);
        scrollPane.setBounds(10, 210, 760, 340);
        panel.add(scrollPane);
    }

    private static void createStudent(String imie, String nazwisko, String numerIndeksu, String email, int idPokoju) {
        try {
            URL url = new URL("http://localhost:8080/take/studenci");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = String.format(
                "{\"imie\": \"%s\", \"nazwisko\": \"%s\", \"numer_indeksu\": \"%s\", \"email\": \"%s\", \"pokoj\": {\"id_pokoju\": %d}}",
                imie, nazwisko, numerIndeksu, email, idPokoju
            );

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_CREATED || code == HttpURLConnection.HTTP_OK) {
                System.out.println("Student zostal utworzony!");
                fetchStudenci();
            } else {
                System.out.println("Blad podczas tworzenia studenta. Kod odpowiedzi: " + code);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void fetchStudenci() {
        try {
            URL url = new URL("http://localhost:8080/take/studenci");
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
            updateStudentList(parseStudentResponse(jsonResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Studenci> parseStudentResponse(String jsonResponse) {
        List<Studenci> studentList = new ArrayList<>();

        if (jsonResponse == null || jsonResponse.trim().isEmpty() || jsonResponse.equals("[]")) {
            System.out.println("Brak studentow w bazie.");
            return studentList;
        }

        jsonResponse = jsonResponse.trim();
        String[] studentEntries = jsonResponse.split("\\},\\{");

        for (String entry : studentEntries) {
            entry = entry.replace("[", "").replace("]", "").replace("{", "").replace("}", "");
            String[] parts = entry.split(",");

            int id = 0;
            String imie = "";
            String nazwisko = "";
            String numerIndeksu = "";
            String email = "";

            for (String part : parts) {
                String[] keyValue = part.split(":");
                if (keyValue.length < 2) continue;

                String key = keyValue[0].replace("\"", "").trim();
                String value = keyValue[1].replace("\"", "").trim();

                switch (key) {
                    case "id_studenta":
                        id = Integer.parseInt(value);
                        break;
                    case "imie":
                        imie = value;
                        break;
                    case "nazwisko":
                        nazwisko = value;
                        break;
                    case "numer_indeksu":
                        numerIndeksu = value;
                        break;
                    case "email":
                        email = value;
                        break;
                    default:
                        break;
                }
            }

            Studenci student = new Studenci();
            student.setId_studenta(id);
            student.setImie(imie);
            student.setNazwisko(nazwisko);
            student.setNumer_indeksu(numerIndeksu);
            student.setEmail(email);

            studentList.add(student);
        }

        return studentList;
    }


    private static void updateStudentList(List<Studenci> studentList) {
        listModel.clear();

        if (studentList.isEmpty()) {
            System.out.println("Brak studentow do wyswietlenia.");
        } else {
            for (Studenci student : studentList) {
                listModel.addElement(student);
            }
        }
    }

    private static void updateStudent(int studentId, String imie, String nazwisko, String numerIndeksu, String email, int idPokoju) {
        try {
            String updateUrl = "http://localhost:8080/take/studenci";
            System.out.println("URL for UPDATE: " + updateUrl);

            URL url = new URL(updateUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            String jsonInputString = String.format(
                "{\"id_studenta\": %d, \"imie\": \"%s\", \"nazwisko\": \"%s\", \"numer_indeksu\": \"%s\", \"email\": \"%s\", \"pokoj\": {\"id_pokoju\": %d}}",
                studentId, imie, nazwisko, numerIndeksu, email, idPokoju
            );

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Student zostal zaktualizowany!");
                fetchStudenci();
            } else {
                System.out.println("Blad podczas aktualizacji studenta. Kod odpowiedzi: " + responseCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void deleteStudent(int studentId) {
        try {
            String deleteUrl = "http://localhost:8080/take/studenci/" + studentId;
            System.out.println("URL for DELETE: " + deleteUrl);

            URL url = new URL(deleteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Student zostal usuniety!");
                fetchStudenci();
            } else {
                System.out.println("Blad podczas usuwania studenta. Kod odpowiedzi: " + responseCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
