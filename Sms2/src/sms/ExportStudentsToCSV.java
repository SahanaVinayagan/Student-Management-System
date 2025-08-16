package sms;
import sms.Database;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class ExportStudentsToCSV {

    public static void exportData() {
        try (Connection conn = Database.getConnection()) {
            // Join tabletud and student_logins tables to get the password along with student details
            String query = "SELECT t.id, t.full_name, t.usn, t.branch, t.semester, t.cgpa, t.days_present, t.days_total, t.attendance_percent, sl.original_password " +
                           "FROM tabletud t " +
                           "JOIN student_logins sl ON t.usn = sl.usn";  
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Specify the full path to the CSV file
            FileWriter csvWriter = new FileWriter("exported_students_csvfile.csv");    //enter the path of the CSV file
            csvWriter.append("ID,Full Name,USN,Branch,Semester,CGPA,Days Present,Total Days,Attendance,Password\n");

            while (rs.next()) {
                csvWriter.append(rs.getInt("id") + ",");
                csvWriter.append(rs.getString("full_name") + ",");
                csvWriter.append(rs.getString("usn") + ",");
                csvWriter.append(rs.getString("branch") + ",");
                csvWriter.append(rs.getInt("semester") + ",");
                csvWriter.append(rs.getFloat("cgpa") + ",");
                csvWriter.append(rs.getInt("days_present") + ",");
                csvWriter.append(rs.getInt("days_total") + ",");
                csvWriter.append(rs.getFloat("attendance_percent") + ",");
                csvWriter.append(rs.getString("original_password") + "\n");
            }

            csvWriter.flush();
            csvWriter.close();
            System.out.println("Student data exported successfully!");

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        exportData();
    }
}
