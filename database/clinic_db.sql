CREATE DATABASE clinic_db;

USE clinic_db;

DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS cancelled_appointments;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    nric VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    doctor_name VARCHAR(50) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time VARCHAR(20) NOT NULL,
    notes VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointments_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- create one admin account
INSERT INTO users (username, password, full_name, phone, nric, role)
VALUES ('admin', 'admin123', 'System Admin', '0123456789', '000000000000', 'admin');

show tables;

select * from users;
select * from appointments;

SHOW INDEX FROM appointments;

ALTER TABLE appointments DROP INDEX unique_doctor_slot;