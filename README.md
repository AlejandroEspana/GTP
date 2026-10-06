# Plataforma Web para la Gestión y Trazabilidad de Proyectos de Software de un Laboratorio

Sistema web desarrollado con **Spring Boot 3**, **Spring Security (JWT)**, **PostgreSQL** y **Angular 18**, diseñado para centralizar la información, documentación versionada, evidencias multimedia, tareas y trazabilidad cronológica (bitácora y auditoría) de los proyectos de un laboratorio universitario.

---

## 🏛️ Arquitectura y Principios de Diseño Limpio (Clean Code & DDD)

El proyecto adopta los patrones y principios de diseño limpio identificados en la arquitectura de referencia:

1. **Modelo de Dominio Rico (*Rich Domain Model*)**:
   - Las entidades del dominio (`Usuario`, `Proyecto`, `MiembroProyecto`, `Tarea`, `Documento`, `VersionDocumento`, `EvidenciaMultimedia`, `EntradaBitacora`, `RegistroAuditoria`) encapsulan reglas de negocio e invariantes dentro de sus constructores y métodos (evitando modelos anémicos).
   - Métodos con semántica de dominio: `cerrarProyecto()`, `vincularMiembro()`, `iniciarDesarrollo()`, `completar()`, `agregarVersion()`, etc.
   - Constructores sin argumentos protegidos (`protected Entity() {}`) para compatibilidad exclusiva con JPA/Hibernate.
   - Colecciones inmutables retornadas vía `Collections.unmodifiableList(...)`.

2. **Objetos de Valor (*Value Objects*)**:
   - `RangoFechas` implementado como Java `record` anotado con `@Embeddable`, asegurando inmutabilidad, validación temporal en constructor compacto y métodos de cálculo (`getDiasTranscurridosODuracion()`, `estaVigente()`).

3. **DTOs y Modern Java Records**:
   - Todos los DTOs de petición (`dto/request`) y respuesta (`dto/response`) están implementados como `record`s inmutables de Java.
   - Desacoplamiento total entre las capas de persistencia y la API REST mediante mappers especializados (`mapper/`).

4. **Seguridad y Control de Acceso basado en Roles (RBAC + JWT)**:
   - Autenticación sin estado (*stateless*) mediante tokens JWT firmados con HMAC-SHA256.
   - Roles del sistema: `ROLE_COORDINADOR`, `ROLE_ASESOR`, `ROLE_ESTUDIANTE`.
   - Matriz de permisos rigurosa aplicada mediante `@PreAuthorize` y validación de contexto de miembros de proyecto.

5. **Auditoría Automática e Inmutable**:
   - Cada operación sensible (creación de proyectos, asignación de miembros, cambio de estado de tareas, subida de documentos o consulta de fichas personales) genera un registro de auditoría inmutable con usuario, fecha/hora, acción e IP de origen.

---

## 👥 Matriz de Roles y Credenciales de Demostración

El sistema se inicializa automáticamente con los 3 perfiles para pruebas inmediatas:

| Rol | Correo Electrónico | Contraseña | Alcance y Permisos |
|---|---|---|---|
| **Coordinador** | `coordinador@laboratorio.edu` | `Admin123!` | Visión global, creación de proyectos, gestión de usuarios, dashboard global de KPIs, auditoría completa y fichas individuales. |
| **Asesor** | `asesor@laboratorio.edu` | `Asesor123!` | Gestión de proyectos asignados, vinculación de estudiantes, revisión de tareas, subida de documentos y actas. |
| **Estudiante** | `estudiante@laboratorio.edu` | `Estudiante123!` | Desarrollo de tareas en proyectos vinculados, subida de documentación versionada, evidencias multimedia y avances en bitácora. |

*(Nota: En la pantalla de inicio de sesión del frontend se han incluido botones de **Acceso Rápido** para ingresar con un solo clic con cualquiera de estos roles).*

---

## 🚀 Despliegue con Docker y Docker Compose

Toda la plataforma (Base de datos, Backend y Frontend) se encuentra 100% dockerizada.

### Requisitos previos:
- **Docker Desktop** (con Docker Compose v2+) instalado y en ejecución.

### Ejecución:
En la raíz del proyecto (`C:\Users\Alejandro Espana\Desktop\POO`), ejecute el siguiente comando:

```bash
docker compose up --build
```

Una vez levantados los contenedores:
- **Frontend (Angular):** [http://localhost:4200](http://localhost:4200)
- **Backend API (Spring Boot):** [http://localhost:8080/api](http://localhost:8080/api)
- **Documentación Swagger / OpenAPI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Base de Datos PostgreSQL:** Puerto `5432` (`laboratorio_db`, usuario `postgres`, contraseña `postgres`)

---

## 📁 Estructura del Proyecto

```text
├── docker-compose.yml          # Orquestación de contenedores (postgres, backend, frontend)
├── backend/                    # Proyecto Spring Boot 3 (API REST, JWT, JPA, DDD)
│   ├── src/main/java/com/laboratorio/proyectos/
│   │   ├── domain/             # Entidades ricas, Enums y Value Objects (Embeddable)
│   │   ├── dto/                # Request y Response DTOs (Java records)
│   │   ├── mapper/             # Mapeadores limpios Entity <-> DTO
│   │   ├── repository/         # Interfaces Spring Data JPA
│   │   ├── security/           # Filtro JWT, TokenProvider, UserDetails y SecurityConfig
│   │   ├── service/            # Lógica transaccional (@Transactional) y de auditoría
│   │   ├── controller/         # Controladores REST con OpenAPI
│   │   ├── exception/          # Manejador global de excepciones (GlobalExceptionHandler)
│   │   └── config/             # Configuración OpenAPI y Semilla de datos (DataInitializer)
│   ├── Dockerfile              # Construcción multi-stage de Spring Boot
│   └── pom.xml
├── bakcend/                    # Junction/Enlace simbólico que apunta a backend
└── Frontend/                   # Aplicación Angular 18 (Standalone Components)
    ├── src/app/
    │   ├── core/
    │   │   ├── models/         # Interfaces TypeScript espejo de los DTOs
    │   │   ├── services/       # Servicios HTTP con Signals y Observables
    │   │   ├── guards/         # AuthGuard y RoleGuard
    │   │   └── interceptors/   # JwtInterceptor y ErrorInterceptor
    │   ├── pages/
    │   │   ├── login/          # Inicio de sesión con acceso rápido demo
    │   │   ├── dashboard/      # Métricas globales y resumen de proyectos asignados
    │   │   ├── proyectos/      # Listado y Espacio de trabajo (Kanban, Versiones, Multimedia, Bitácora, Equipo)
    │   │   ├── integrantes/    # Directorio y Ficha individual con historial
    │   │   └── auditoria/      # Tabla inmutable de trazabilidad
    │   └── shared/             # Barra de navegación y componentes compartidos
    ├── Dockerfile              # Construcción multi-stage con Nginx
    └── nginx.conf              # Servidor estático y proxy inverso a /api/
```
