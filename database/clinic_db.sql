CREATE DATABASE clinic_db;

USE clinic_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_name VARCHAR(100) NOT NULL,
    doctor_name VARCHAR(100),
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) DEFAULT 'BOOKED'
);

INSERT INTO users (username, password, full_name, phone)
VALUES ('wano', '123456', 'Wan Nur Humairah', '0123456789');

INSERT INTO appointments (patient_name, doctor_name, appointment_date, appointment_time)
VALUES ('Wan Nur Humairah', 'Dr Ahmad', '2026-03-10', '10:30:00');

SHOW TABLES;

SELECT * FROM users;
SELECT * FROM appointments;

