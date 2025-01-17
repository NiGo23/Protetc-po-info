package org.example;

// GaleShapley.java
import java.util.*;

public class GaleShapley {
    public static Map<Student, School> match(List<Student> students, List<School> schools) {
        Map<Student, School> matches = new HashMap<>();
        Map<School, Queue<Student>> preferences = new HashMap<>();

        for (School school : schools) {
            preferences.put(school, new LinkedList<>());
        }

        while (!students.isEmpty()) {
            Student student = ((LinkedList<Student>) students).removeFirst();
            for (int i = 0; i < student.getPreferences().length; i++) {
                String preferredSchoolName = student.getPreferences()[i];
                for (School school : schools) {
                    if (school.getName().equals(preferredSchoolName) && school.hasCapacity()) {
                        matches.put(student, school);
                        school.incrementCurrentCapacity();
                        break;
                    }
                }

                if (matches.containsKey(student)) {
                    break;
                }
            }
        }

        return matches;
    }
}
