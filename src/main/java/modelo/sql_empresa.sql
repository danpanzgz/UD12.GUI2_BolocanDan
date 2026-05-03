CREATE TABLE empleados (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           dni VARCHAR(9) NOT NULL UNIQUE,
                           nombre VARCHAR(100) NOT NULL,
                           apellido VARCHAR(150) NOT NULL,
                           direccion VARCHAR(200) NOT NULL,
                           telefono VARCHAR(15) NOT NULL,
                           puesto VARCHAR(50) NOT NULL
);