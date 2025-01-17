package org.example;

import javax.swing.*;
import java.awt.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

public class Menu {
    public JPanel panel1;
    private JButton loadFileButton;
    private JTextField npmSpacesField;
    private JTextField smgSpacesField;

    public Menu() {
        createUIComponents(); // Ensure components are initialized

        // Set layout and add components to the panel
        panel1.setLayout(new GridLayout(3, 2, 10, 10));
        panel1.add(new JLabel("Свободни места в НПМГ:"));
        panel1.add(npmSpacesField);
        panel1.add(new JLabel("Свободни места в СМГ:"));
        panel1.add(smgSpacesField);
        panel1.add(loadFileButton);

        // Action listener for Load File button
        loadFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select an Excel File");
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                processExcelFile(selectedFile.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(null, "No file selected.");
            }
        });
    }

    private void createUIComponents() {
        // Initialize the components
        panel1 = new JPanel();
        npmSpacesField = new JTextField();
        smgSpacesField = new JTextField();
        loadFileButton = new JButton("Load File");
    }

    private void processExcelFile(String filePath) {
        List<Student> students = new ArrayList<>();
        int npmCapacity = Integer.parseInt(npmSpacesField.getText().trim());
        int smgCapacity = Integer.parseInt(smgSpacesField.getText().trim());
        List<School> schools = Arrays.asList(
                new School("НПМГ", npmCapacity),
                new School("СМГ", smgCapacity)
        );
        //System.out.println(schools[0].getCapacity());


        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
            for (Row row : sheet) {
                if (row.getRowNum() < 5) continue; // Skip header rows (adjust as needed)

                Cell idCell = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (idCell == null || idCell.getCellType() != CellType.NUMERIC) continue;

                int id = (int) idCell.getNumericCellValue();

                Cell preferencesCell = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (preferencesCell == null || preferencesCell.getCellType() != CellType.STRING) continue;

                String[] preferences = preferencesCell.getStringCellValue().split(",");

                double[] scores = new double[preferences.length];
                for (int i = 0; i < preferences.length; i++) {
                    Cell scoreCell = row.getCell(2 + i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    scores[i] = (scoreCell != null && scoreCell.getCellType() == CellType.NUMERIC)
                            ? scoreCell.getNumericCellValue()
                            : 0;
                }

                students.add(new Student(id, preferences, scores));
            }

            // Match students to schools
            Map<Student, School> matches = GaleShapley.match(students, schools);

            // Group students by school for ranking
            Map<School, List<Student>> schoolToStudents = new HashMap<>();
            for (Map.Entry<Student, School> entry : matches.entrySet()) {
                schoolToStudents
                        .computeIfAbsent(entry.getValue(), k -> new ArrayList<>())
                        .add(entry.getKey());
            }

            // Sort students in each school by their scores for ranking
            for (List<Student> studentList : schoolToStudents.values()) {
                studentList.sort((s1, s2) -> Double.compare(s2.getHighestScore(), s1.getHighestScore()));
            }

            // Write results back to Excel
            for (Row row : sheet) {
                if (row.getRowNum() < 5) continue; // Skip header rows

                Cell idCell = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (idCell == null || idCell.getCellType() != CellType.NUMERIC) continue;
                int id = (int) idCell.getNumericCellValue();

                for (Student student : matches.keySet()) {
                    if (student.getId() == id) {
                        School assignedSchool = matches.get(student);
                        List<Student> rankedStudents = schoolToStudents.get(assignedSchool);
                        int rank = rankedStudents.indexOf(student) + 1; // Rank is 1-based

                        // Add assigned school to a new column
                        Cell schoolCell = row.createCell(row.getLastCellNum());
                        schoolCell.setCellValue(assignedSchool.getName());

                        // Add rank to another new column
                        Cell rankCell = row.createCell(row.getLastCellNum());
                        rankCell.setCellValue(rank);
                        break;
                    }
                }
            }

            // Save updated Excel file
            try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                workbook.write(fos);
            }

            JOptionPane.showMessageDialog(null, "Matching and ranking completed. Results written to Excel file.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error processing file: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
