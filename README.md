# 🧪 GestionLabs — Sistema de Gestión de Laboratorios

> Arquitectura profesional para la gestión de turnos, reservas y equipos en laboratorios universitarios.  
> Desarrollado con **Spring Boot 3**, **Spring Data MongoDB**, **JWT** y frontend en **HTML + Fetch API**.

---

## 📋 Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Arquitectura del Sistema](#arquitectura-del-sistema)
  - [D1 — Diagrama de Contexto](#d1--diagrama-de-contexto)
  - [D2 — Diagrama de Componentes](#d2--diagrama-de-componentes)
  - [D3 — Diagrama Conceptual de Clases](#d3--diagrama-conceptual-de-clases)
  - [D4 — Arquitectura por Capas](#d4--arquitectura-por-capas)
  - [D5 — Diagrama de Despliegue](#d5--diagrama-de-despliegue)
- [Stack Tecnológico](#stack-tecnológico)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [Endpoints API REST](#endpoints-api-rest)
- [Equipo](#equipo)

---

## Descripción General

**GestionLabs** es un sistema universitario que permite a **Estudiantes** reservar turnos en laboratorios y a **Administradores** gestionar equipos, franjas horarias, mantenimientos y reportes — todo a través de una API REST segura con autenticación JWT.

| Actor | Capacidades |
|---|---|
| 🎓 **Estudiante** | Registrar, cancelar y consultar reservas · Ver disponibilidad en tiempo real |
| 🔑 **Administrador** | Gestionar labs y equipos · Bloquear franjas · Generar reportes · Programar mantenimientos |

---

## Arquitectura del Sistema

### D1 — Diagrama de Contexto

> Vista de alto nivel: cómo interactúan los actores externos con el sistema central y la base de datos.

![Diagrama de Contexto](docs/img/d1_contexto.png)

---

### D2 — Diagrama de Componentes

> Módulos internos del sistema organizados por responsabilidad: Autenticación, Reservas, Mantenimiento y Notificaciones.

![Diagrama de Componentes](docs/img/d2_componentes.png)

Los componentes principales son:

| Módulo | Servicios |
|---|---|
| **Auth & Usuarios** | `AuthService / JWT` · `UsuarioService` |
| **Gestión de Reservas y Laboratorios** | `ReservaService` · `LaboratorioService` · `EquipoService` |
| **Notificaciones** | `NotificacionService` · `SMTP Mail Sender` |
| **Gestión Mantenimiento & Reportes** | `MantenimientoService` · `ReporteService (Solo Consulta)` |

---

### D3 — Diagrama Conceptual de Clases

> Relaciones entre las entidades del dominio: `Usuario`, `Administrador`, `Reserva`, `FranjaHoraria`, `Laboratorio`, `Equipo`, `Notificacion`, `Reporte`, `Mantenimiento`.

![Diagrama Conceptual de Clases](docs/img/d3_clases.png)

**Entidades principales:**

```
Usuario
├── id: String
├── nombre: String
├── correo: String
└── contraseña: String
    ├── [Estudiante] matricula, carrera
    └── [Administrador] nivel

Reserva
├── id: String
├── estado: String
├── FranjaHoraria (horaInicio, horaFin)
└── → Notificacion, Reporte

Laboratorio
├── id, nombre
└── → Equipo (nombre, estado)
       └── → Mantenimiento (descripcion)
```

---

### D4 — Arquitectura por Capas

> Organización técnica del código en capas: **Controladores → DTOs → Servicios → Repositorios**, siguiendo principios de separación de responsabilidades.

![Arquitectura por Capas](docs/img/d4_capas.png)

| Capa | Componentes |
|---|---|
| **Controllers** | `AuthController` · `UsuarioController` · `ReservaController` · `EquipoController` · `LaboratorioController` · `AdministradorController` |
| **DTOs** | `UsuarioDTO/AuthDTO` · `ReservaDTO` · `EquipoDTO` · `LaboratorioDTO` |
| **Services (Lógica de Negocio)** | `AuthService` · `UsuarioService` · `ReservaService` · `EquipoService` · `LaboratorioService` · `ReporteService` · `NotificacionService` · `MantenimientoService` |
| **Repositories (Acceso a Datos)** | `UsuarioRepository` · `ReservaRepository` · `EquipoRepository` · `LaboratorioRepository` · `MantenimientoRepository` |

---

### D5 — Diagrama de Despliegue

> Infraestructura en producción: dispositivos cliente, servidor Spring Boot 3.3 con Tomcat embebido, MongoDB Atlas y sistema externo de correo.

![Diagrama de Despliegue](docs/img/d5_despliegue.png)

```
┌─────────────────┐     HTTPS :8080     ┌────────────────────────────────┐
│ Dispositivo     │ ──────────────────► │  Servidor de Aplicación        │
│ Administrador   │                     │  Spring Boot 3.3 / Tomcat      │
│ Navegador Web   │                     │  ┌─────────────────────────┐   │
└─────────────────┘                     │  │ API REST                │   │
                                        │  │ Auth JWT & Sesiones     │   │
┌─────────────────┐     HTTPS :8080     │  │ Gestión Reservas/Labs   │   │
│ Dispositivo     │ ──────────────────► │  │ Módulo Notificaciones   │   │
│ Estudiante      │                     │  └─────────────────────────┘   │
│ Navegador Web   │                     └────────────────┬───────────────┘
└─────────────────┘                                      │
                                                         │
┌─────────────────┐                     ┌───────────────▼───────────────┐
│ Sistema Correo  │ ◄────── SMTP ──────  │  Base de Datos                │
│ SendGrid/AWS    │                     │  MongoDB Atlas                 │
│ Notificaciones  │                     │  Users, Labs, Reservations...  │
└─────────────────┘                     └───────────────────────────────┘
```

---

## Stack Tecnológico

| Capa | Tecnología |
|---|---|
| **Backend** | Java 17 · Spring Boot 3.3 · Spring Data MongoDB · Spring Security · JWT |
| **Frontend** | HTML5 · CSS3 · JavaScript (Fetch API) |
| **Base de Datos** | MongoDB Atlas (NoSQL documental) |
| **Notificaciones** | SMTP / SendGrid / AWS SES |
| **Servidor** | Apache Tomcat (embebido) · Puerto `8080` |
| **Autenticación** | JSON Web Tokens (JWT) |

---

## Estructura del Proyecto

```
gestionlabs/
├── src/main/java/com/gestionlabs/
│   ├── controller/          ← REST Controllers (@RestController)
│   │   ├── AuthController.java
│   │   ├── ReservaController.java
│   │   ├── LaboratorioController.java
│   │   └── EquipoController.java
│   ├── dto/                 ← Data Transfer Objects
│   │   ├── UsuarioDTO.java
│   │   └── ReservaDTO.java
│   ├── model/               ← Entidades MongoDB (@Document)
│   │   ├── Usuario.java
│   │   ├── Reserva.java
│   │   ├── Laboratorio.java
│   │   └── Equipo.java
│   ├── repository/          ← Spring Data MongoDB
│   ├── service/             ← Lógica de negocio
│   └── security/            ← JWT + Spring Security
├── src/main/resources/
│   ├── application.properties
│   └── static/              ← Frontend HTML/JS
├── docs/
│   └── img/                 ← Diagramas de arquitectura
└── pom.xml
```

---

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

---

## Endpoints API REST

| Método | URL | Descripción | Auth |
|---|---|---|---|
| `POST` | `/api/auth/login` | Iniciar sesión (retorna JWT) | ❌ |
| `POST` | `/api/auth/register` | Registrar usuario | ❌ |
| `GET` | `/api/laboratorios` | Listar laboratorios disponibles | ✅ |
| `GET` | `/api/laboratorios/{id}/franjas` | Ver franjas horarias disponibles | ✅ |
| `POST` | `/api/reservas` | Crear reserva | ✅ |
| `GET` | `/api/reservas/mis-reservas` | Ver mis reservas | ✅ |
| `DELETE` | `/api/reservas/{id}` | Cancelar reserva | ✅ |
| `GET` | `/api/admin/reportes` | Generar reportes | 🔑 Admin |
| `POST` | `/api/admin/equipos` | Registrar equipo | 🔑 Admin |
| `PUT` | `/api/admin/equipos/{id}/mantenimiento` | Programar mantenimiento | 🔑 Admin |

> ✅ Requiere token JWT en header `Authorization: Bearer <token>`  
> 🔑 Requiere rol `ADMINISTRADOR`

---

## Equipo

> Proyecto universitario — Ingeniería de Software · 2025

| Integrante | Rol |
|---|---|
| — | Backend / Spring Boot |
| — | Frontend / HTML + JS |
| — | Base de Datos / MongoDB |
| — | Documentación / Diagramas |
