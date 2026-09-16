CREATE DATABASE libreriadb;
USE libreriadb;

CREATE TABLE USUARIO(
ID_USUARIO INT PRIMARY KEY,
NOMBRE VARCHAR(50),
EMAIL VARCHAR(50),
TELEFONO VARCHAR(9));

CREATE TABLE LIBRO(
ID_LIBRO INT PRIMARY KEY,
TITULO VARCHAR(50),
AUTOR VARCHAR(50),
GENERO ENUM("FANTASIA", "MISTERIO", "ROMANCE"),
DISPONIBLE BOOLEAN,
RUTA VARCHAR(50));

/*ESTA TABLA ES EL FICHERO*/
/*CREATE TABLE PRESTAMO(
ID_PRESTAMO INT AUTO_INCREMENT PRIMARY KEY,
ID_USER INT,
ID_LIBRO INT,
FECHA_INICIO DATE, 
FECHA_FIN DATE,
DEVUELTO BOOLEAN,
FOREIGN KEY (ID_LIBRO) REFERENCES LIBRO (ID_LIBRO),
FOREIGN KEY (ID_USUARIO) REFERENCES PRESTAMO (ID_USUARIO));*/

-- Inserción de datos en USUARIO
INSERT INTO USUARIO (ID_USUARIO, NOMBRE, EMAIL, TELEFONO) VALUES
(1, 'Laura García', 'laura.garcia@email.com', '612345678'),
(2, 'Carlos Mendoza', 'carlos.m@email.com', '623456789'),
(3, 'Elena Romero', 'elena.romero@email.com', '634567890'),
(4, 'David Torres', 'david.t@email.com', '645678901'),
(5, 'Sofía Navarro', 'sofia.n@email.com', '656789012');

-- Inserción de datos en LIBRO
INSERT INTO LIBRO (ID_LIBRO, TITULO, AUTOR, GENERO, DISPONIBLE, RUTA) VALUES
(1, 'El Nombre del Viento', 'Patrick Rothfuss', 'FANTASIA', TRUE, '/libros/fantasia/nombre_viento.pdf'),
(2, 'Diez Negritos', 'Agatha Christie', 'MISTERIO', TRUE, '/libros/misterio/diez_negritos.epub'),
(3, 'Orgullo y Prejuicio', 'Jane Austen', 'ROMANCE', FALSE, '/libros/romance/orgullo_prejuicio.pdf'),
(4, 'El Imperio Final', 'Brandon Sanderson', 'FANTASIA', TRUE, '/libros/fantasia/imperio_final.epub'),
(5, 'El Sabueso de los Baskerville', 'Arthur Conan Doyle', 'MISTERIO', FALSE, '/libros/misterio/sabueso.pdf'),
(6, 'Bajo la Misma Estrella', 'John Green', 'ROMANCE', TRUE, '/libros/romance/bajo_misma_estrella.epub');