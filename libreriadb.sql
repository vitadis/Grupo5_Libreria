CREATE DATABASE libreriadb;
USE libreriadb;

CREATE TABLE USUARIO(
ID_USUARIO INT PRIMARY KEY,
NOMBRE VARCHAR(50),
EMAIL VARCHAR(50),
TELEFONO VARCHAR(9));

CREATE TABLE LIBRO(
ID_LIBRO INT PRIMARY KEY AUTO_INCREMENT,
TITULO VARCHAR(50),
AUTOR VARCHAR(50),
GENERO ENUM("FANTASIA", "MISTERIO", "ROMANCE"),
DISPONIBLE BOOLEAN,
RUTA VARCHAR(50));

-- Inserción de datos en USUARIO
INSERT INTO USUARIO (ID_USUARIO, NOMBRE, EMAIL, TELEFONO) VALUES
(1, 'Laura García', 'laura.garcia@email.com', '612345678'),
(2, 'Carlos Mendoza', 'carlos.m@email.com', '623456789'),
(3, 'Elena Romero', 'elena.romero@email.com', '634567890'),
(4, 'David Torres', 'david.t@email.com', '645678901'),
(5, 'Sofía Navarro', 'sofia.n@email.com', '656789012');

-- Inserción de datos en LIBRO
INSERT INTO LIBRO (ID_LIBRO, TITULO, AUTOR, GENERO, DISPONIBLE, RUTA) VALUES
(1, 'Daga sin nombre', 'A.S. Velada', 'FANTASIA', TRUE, 'Libreria_Project_G5\\src\\res\\DagaSinNombre.jpg'),
(2, 'Mamá nos dijo adiós', 'Magdalena Latapi', 'ROMANCE', TRUE, 'Libreria_Project_G5\\src\\res\\MamaNosDijoAdios.jpg'),
(3, 'Moby Dick', 'Herman Melville', 'FANTASIA', TRUE, 'Libreria_Project_G5\\src\\res\\MobyDick.jpg'),
(4, 'La naranja mecánica', 'Anthony Burgess', 'MISTERIO', TRUE, 'Libreria_Project_G5\\src\\res\\NaranjaMecanica.jpg'),
(5, 'Pinocho', 'Carlo Collodi', 'FANTASIA', TRUE, 'Libreria_Project_G5\\src\\res\\Pinocho.jpg');