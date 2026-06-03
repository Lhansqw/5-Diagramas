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

<img width="273" height="298" alt="{23C4FC46-6BF4-48E2-B97B-7A2DD15EE010}" src="https://github.com/user-attachments/assets/151e909f-f728-462b-b851-595c87ecd841" />


---

### D2 — Diagrama de Componentes

<img width="411" height="265" alt="{52453B7F-2A03-4FDC-B7FF-049E1355C43C}" src="https://github.com/user-attachments/assets/85a62530-a4bd-4dde-af7f-2fe95c537715" />


---

### D3 — Diagrama Conceptual de Clases

<img width="331" height="318" alt="{1963E524-51CA-43F3-9CA2-7B06112CA7A4}" src="https://github.com/user-attachments/assets/8c8a18ad-1da2-47fa-b276-70f8e6d1dd7c" />


### D4 — Arquitectura por Capas

<img width="429" height="380" alt="{6A029E8F-7C35-4952-B0A7-54F37BE18496}" src="https://github.com/user-attachments/assets/c845eed9-1554-4d5f-9a95-9fe5c92c76fa" />


### D5 — Diagrama de Despliegue

<img width="309" height="320" alt="{81F51C93-F755-4555-96FB-6D0FAB93CD92}" src="https://github.com/user-attachments/assets/db4474b0-e0f7-4561-904d-adec0f0aebf6" />


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
