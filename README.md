# 📚 Sistema de Gestión de Librería — Grupo 5

Proyecto desarrollado para el módulo de **Acceso a Datos (ADT - Reto 0)** del ciclo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma (**DAM**), en **CIFP Tartanga LHII**.

---

## 📖 Descripción General

El **Sistema de Gestión de Librería** es una aplicación de consola en Java diseñada para administrar el catálogo de libros, los usuarios registrados y las operaciones de préstamos y devoluciones de la librería. 

El proyecto destaca por su **arquitectura híbrida de persistencia**:
- **Bases de Datos Relacionales (JDBC / MySQL)** para la gestión persistente de Usuarios y Libros.
- **Archivos JSON (org.json)** con mecanismo automatizado de **Backup & Recovery** para la gestión de Préstamos.

---

## ✨ Funcionalidades Principales

| Icono | Operación | Descripción | 
| :---: | :--- | :--- | 
| 📖 | **Registrar Libro** | Añade un nuevo libro al catálogo con su título, autor, género y ruta. 
| 👤 | **Registrar Usuario** | Da de alta usuarios con validación estricta de email y teléfono. 
| 🔄 | **Realizar Préstamo** | Permite prestar uno o más libros disponibles a un usuario registrado.
| 📥 | **Devolver Libro** | Procesa la devolución de libros prestados y restituye su disponibilidad. 
| 🔍 | **Consultar Libros Disponibles** | Muestra el catálogo de libros cuyo estado actual sea disponible.
| 📋 | **Consultar Préstamos de Usuario** | Lista todos los préstamos vigentes o pasados asociados a un usuario. 
| 📜 | **Historial de Préstamos** | Muestra la trazabilidad completa de préstamos para un libro específico.

---

## 🛠️ Arquitectura y Patrones de Diseño

El proyecto implementa buenas prácticas de desarrollo software con una clara separación de responsabilidades:

- **Modelo-Vista-Controlador (MVC)**: Desacoplamiento entre la lógica de presentación de la consola (`Main.java`, `Util.java`), los controladores de negocio (`LibroController`, `PrestamoController`) y el modelo de datos.
- **DAO (Data Access Object)**: Interfaces genéricas (`DaoLibro`, `UsuarioDao`, `PrestamoDao`) que abstraen el acceso a datos.
- **Patrón Repositorio / Implementación**: Clases concretas (`AccesoLibro`, `AccesoUsuario`, `AccesoPrestamo`) que implementan las interfaces DAO utilizando JDBC y JSON.
- **Patrón Singleton**: Garantiza instancias únicas para los controladores y repositorios (`getInstance()`).
- **Manejo de Excepciones Personalizadas**: Manejo robusto de errores mediante `AccesoDatosException`, `LibroNoEncontradoException`, `EmailInvalidoException` y `TelefonoInvalidoException`.
- **Tolerancia a Fallos / Backup Automático**: Si el archivo `prestamos.json` se corrompe o no se puede leer, la aplicación restaura automáticamente la copia de seguridad `prestamos_backup.json`.

---

## 🗄️ Modelo de Datos

### 1. Base de Datos MySQL (`libreriadb`)

```sql
CREATE DATABASE libreriadb;
USE libreriadb;

CREATE TABLE USUARIO (
    ID_USUARIO INT PRIMARY KEY AUTO_INCREMENT,
    NOMBRE VARCHAR(50),
    EMAIL VARCHAR(50),
    TELEFONO VARCHAR(9)
);

CREATE TABLE LIBRO (
    ID_LIBRO INT PRIMARY KEY AUTO_INCREMENT,
    TITULO VARCHAR(50),
    AUTOR VARCHAR(50),
    GENERO ENUM('FANTASIA', 'MISTERIO', 'ROMANCE'),
    DISPONIBLE BOOLEAN,
    RUTA VARCHAR(50)
);
```

### 2. Estructura JSON (`prestamos.json`)

```json
[
  {
    "idPrestamo": 1,
    "idUsuario": 1,
    "idLibro": 2,
    "fechaPrestamo": "2026-09-29",
    "fechaDevolucion": null,
    "devuelto": false
  }
]
```

---

## 📂 Estructura del Proyecto

```text
Libreria_Project_G5/
├── build.xml                       # Script de construcción Ant
├── libreriadb.sql                  # Script de creación e inserción de datos MySQL
└── src/
    ├── controller/                 # Controladores de la aplicación
    │   ├── LibroController.java
    │   └── PrestamoController.java
    ├── dao/                        # Interfaces DAO (Contratos de Acceso a Datos)
    │   ├── DaoLibro.java
    │   ├── PrestamoDao.java
    │   └── UsuarioDao.java
    ├── dataBase/                   # Archivos de persistencia local JSON
    │   ├── prestamos.json
    │   └── prestamos_backup.json
    ├── exceptions/                 # Excepciones personalizadas del dominio
    │   ├── AccesoDatosException.java
    │   ├── EmailInvalidoException.java
    │   ├── LibroNoEncontradoException.java
    │   └── TelefonoInvalidoException.java
    ├── main/                       # Punto de entrada de la aplicación
    │   └── Main.java
    ├── model/                      # Entidades del Modelo de Datos
    │   ├── Genero.java (Enum)
    │   ├── Libro.java
    │   ├── Prestamo.java
    │   └── Usuario.java
    ├── repository/                 # Implementaciones concretas de persistencia
    │   ├── AccesoDataBase.java     # Gestión de conexiones JDBC
    │   ├── AccesoLibro.java
    │   ├── AccesoPrestamo.java
    │   └── AccesoUsuario.java
    └── utilidades/                 # Utilidades generales y configuración
        ├── Sentencias.java
        ├── Util.java
        └── configGlobal.properties # Configuración de conexión MySQL
```

---

## ⚙️ Requisitos e Instalación

### Requisitos Previos

- **JDK 21** o superior.
- **MySQL Server 8.0+**.
- **Apache NetBeans IDE** (o cualquier IDE Java compatible con Ant/Maven).
- **Librería JSON (`org.json`)** y **Driver MySQL Connector/J** añadidos a las librerías del proyecto.

### Configuración del Entorno

1. **Importar la Base de Datos**:
   Ejecuta el archivo [`libreriadb.sql`](//libreriadb.sql) en tu cliente de MySQL (MySQL Workbench, phpMyAdmin o CLI):
   ```bash
   mysql -u root -p < libreriadb.sql
   ```

2. **Configurar las credenciales de MySQL**:
   Modifica el archivo [`configGlobal.properties`](//Libreria_Project_G5/src/utilidades/configGlobal.properties) con las credenciales de tu servidor MySQL local:
   ```properties
   DB = libreriadb
   Conn = jdbc:mysql://localhost:3306/libreriadb?serverTimezone=Europe/Madrid&useSSL=false
   DBUser = tu_usuario
   DBPass = tu_contraseña
   Driver = com.mysql.jdbc.Driver
   ```

3. **Ejecutar la Aplicación**:
   - Abre el proyecto `Libreria_Project_G5` en NetBeans.
   - Haz clic secundario en el proyecto y selecciona **Run** o ejecuta directamente [`Main.java`](//Libreria_Project_G5/src/main/Main.java).

4. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/tu_usuario/Libreria_Project_G5.git
   ```
---

## 👥 Equipo de Desarrollo — Grupo 5

Proyecto realizado por los alumnos del Grupo 5 para el módulo de **Acceso a Datos**:

- 👤 **Christian** https://github.com/FoxyGamer1156
- 👤 **Joel** https://github.com/vitadis
- 👤 **Hodei Torres** https://github.com/HodeiTorres33
- 👤 **An** https://github.com/Azkona05

---

<p center="align">
  <i>CIFP Tartanga LHII — DAM 2026-2027</i>
</p>
