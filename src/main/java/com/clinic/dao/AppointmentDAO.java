package com.clinic.dao;

import com.clinic.model.Appointment;
import com.clinic.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentDAO {
    private static final Logger LOGGER = Logger.getLogger(AppointmentDAO.class.getName());

    public List<String> getAvailableSlots(String doctorName, LocalDate appointmentDate) {
        List<String> allSlots = getDoctorSlots(doctorName, appointmentDate);
        List<String> takenSlots = new ArrayList<>();

        String sql = """
                SELECT appointment_time
                FROM appointments
                WHERE doctor_name = ?
                  AND appointment_date = ?
                  AND status IN ('Pending', 'Approved')
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, doctorName);
            pst.setDate(2, java.sql.Date.valueOf(appointmentDate));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    takenSlots.add(rs.getString("appointment_time"));
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving taken slots", e);
        }

        allSlots.removeAll(takenSlots);
        return allSlots;
    }

    public boolean bookAppointment(int userId, String doctorName, LocalDate appointmentDate,
                                   String appointmentTime, String notes) {

        if (!isSlotStillAvailable(doctorName, appointmentDate, appointmentTime)) {
            return false;
        }

        String sql = """
                INSERT INTO appointments (user_id, doctor_name, appointment_date, appointment_time, notes, status)
                VALUES (?, ?, ?, ?, ?, 'Pending')
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, userId);
            pst.setString(2, doctorName);
            pst.setDate(3, java.sql.Date.valueOf(appointmentDate));
            pst.setString(4, appointmentTime);
            pst.setString(5, notes == null || notes.isBlank() ? null : notes.trim());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error booking appointment", e);
            return false;
        }
    }

    public boolean isSlotStillAvailable(String doctorName, LocalDate appointmentDate, String appointmentTime) {
        String sql = """
                SELECT COUNT(*)
                FROM appointments
                WHERE doctor_name = ?
                  AND appointment_date = ?
                  AND appointment_time = ?
                  AND status IN ('Pending', 'Approved')
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, doctorName);
            pst.setDate(2, java.sql.Date.valueOf(appointmentDate));
            pst.setString(3, appointmentTime);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking slot availability", e);
        }

        return false;
    }

    private List<String> getDoctorSlots(String doctorName, LocalDate date) {
        List<String> slots = new ArrayList<>();
        DayOfWeek day = date.getDayOfWeek();

        switch (doctorName) {
            case "Dr Ali" -> {
                if (day != DayOfWeek.SUNDAY) {
                    slots.add("9:00 AM");
                    slots.add("10:00 AM");
                    slots.add("11:00 AM");
                    slots.add("2:00 PM");
                    slots.add("3:00 PM");
                }
            }
            case "Dr Sarah" -> {
                if (day != DayOfWeek.SATURDAY) {
                    slots.add("10:30 AM");
                    slots.add("11:30 AM");
                    slots.add("1:30 PM");
                    slots.add("4:00 PM");
                }
            }
            case "Dr John" -> {
                if (day != DayOfWeek.SUNDAY) {
                    slots.add("8:30 AM");
                    slots.add("9:30 AM");
                    slots.add("12:00 PM");
                    slots.add("3:30 PM");
                }
            }
        }

        return slots;
    }

    public List<Appointment> getAppointmentsByUser(int userId) {
        List<Appointment> list = new ArrayList<>();

        String sql = """
            SELECT a.appointment_id AS id,
                   u.full_name,
                   a.doctor_name,
                   a.appointment_date,
                   a.appointment_time,
                   a.status
            FROM appointments a
            JOIN users u ON a.user_id = u.id
            WHERE a.user_id = ?
            ORDER BY a.appointment_date ASC, a.appointment_time ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, userId);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(new Appointment(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("doctor_name"),
                            rs.getString("appointment_date"),
                            rs.getString("appointment_time"),
                            rs.getString("status")
                    ));
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving appointments for user", e);
        }

        return list;
    }

    public boolean cancelAppointment(int appointmentId) {
        String sql = """
                UPDATE appointments
                SET status = 'Cancelled'
                WHERE appointment_id = ?
                  AND status IN ('Pending', 'Approved')
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, appointmentId);
            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cancelling appointment", e);
            return false;
        }
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();

        String sql = """
                SELECT a.appointment_id AS id,
                       u.full_name,
                       a.doctor_name,
                       a.appointment_date,
                       a.appointment_time,
                       a.status
                FROM appointments a
                JOIN users u ON a.user_id = u.id
                ORDER BY a.appointment_date DESC, a.appointment_time DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new Appointment(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("doctor_name"),
                        rs.getString("appointment_date"),
                        rs.getString("appointment_time"),
                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all appointments", e);
        }

        return list;
    }

    public boolean updateAppointmentStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, newStatus);
            pst.setInt(2, appointmentId);

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating appointment status", e);
            return false;
        }
    }

    public List<Appointment> getPendingAppointments() {
    List<Appointment> list = new ArrayList<>();

    String sql = """
            SELECT a.appointment_id AS id,
                   u.full_name,
                   a.doctor_name,
                   a.appointment_date,
                   a.appointment_time,
                   a.status
            FROM appointments a
            JOIN users u ON a.user_id = u.id
            WHERE a.status = 'Pending'
            ORDER BY a.appointment_date ASC, a.appointment_time ASC
            """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            list.add(new Appointment(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("doctor_name"),
                    rs.getString("appointment_date"),
                    rs.getString("appointment_time"),
                    rs.getString("status")
            ));
        }

    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Error retrieving pending appointments", e);
    }

    return list;
}
}