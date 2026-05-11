package com.clinic.model;

public class Appointment {

    private int id;
    private String patientName;
    private String doctorName;
    private String date;
    private String time;
    private String status;

    public Appointment(int id, String patientName, String doctorName, String date, String time, String status) {
        this.id = id;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public int getId() { return id; }
    public String getPatientName() { return patientName; }
    public String getDoctorName() { return doctorName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
}