#  GestionLabs — Sistema de Gestión de Laboratorios
---

## Descripción General

**GestionLabs** es un sistema universitario que permite a **Estudiantes** reservar turnos en laboratorios y a **Administradores** gestionar equipos, franjas horarias, mantenimientos y reportes — todo a través de una API REST segura con autenticación JWT.

| Actor | Capacidades |
|---|---|
|  **Estudiante** | Registrar, cancelar y consultar reservas · Ver disponibilidad en tiempo real |
|  **Administrador** | Gestionar labs y equipos · Bloquear franjas · Generar reportes · Programar mantenimientos |

---

## Arquitectura del Sistema

### D1 — Diagrama de Contexto


> Vista de alto nivel: cómo interactúan los actores externos con el sistema central y la base de datos.



<img width="744" height="555" alt="contex" src="https://github.com/user-attachments/assets/3fd03ae6-99a9-4552-9cd0-6704d3770f8f" />





---

### D2 — Diagrama de Componentes

<img width="964" height="493" alt="compos" src="https://github.com/user-attachments/assets/08a9d0b5-d113-4d3d-8608-94a5acb550ba" />





---

### D3 — Diagrama Conceptual de Clases

<img width="671" height="1195" alt="Untitled (2)" src="https://github.com/user-attachments/assets/7640700b-32a7-4943-80d1-f15a522e8bfd" />




### D4 — Arquitectura por Capas

<img width="958" height="805" alt="capas (1)" src="https://github.com/user-attachments/assets/c57562e1-4afc-4925-90b3-6da394a09172" />



### D5 — Diagrama de Despliegue
<img width="888" height="528" alt="despli" src="https://github.com/user-attachments/assets/33ce2654-5e09-4c2e-8735-a65dc124cca6" />


## Instalación y Ejecución

### Pre-requisitos

- Java 17+
- Maven 3.9+
- Cuenta en [MongoDB Atlas](https://cloud.mongodb.com) (gratuita)

### 1. Clonar el repositorio

```bash
git clone https://github.com/usuario/gestionlabs.git
cd gestionlabs
```

### 2. Configurar la conexión a MongoDB

En `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb+srv://<usuario>:<contraseña>@cluster0.xxx.mongodb.net/gestionlabs
spring.data.mongodb.database=gestionlabs
server.port=8080
```

### 3. Ejecutar

```bash
mvn spring-boot:run
```

Abre el navegador en: **http://localhost:8080**
