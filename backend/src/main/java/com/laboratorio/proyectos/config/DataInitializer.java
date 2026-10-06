package com.laboratorio.proyectos.config;

import com.laboratorio.proyectos.domain.*;
import com.laboratorio.proyectos.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final MiembroProyectoRepository miembroProyectoRepository;
    private final TareaRepository tareaRepository;
    private final EntradaBitacoraRepository bitacoraRepository;
    private final RegistroAuditoriaRepository auditoriaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           ProyectoRepository proyectoRepository,
                           MiembroProyectoRepository miembroProyectoRepository,
                           TareaRepository tareaRepository,
                           EntradaBitacoraRepository bitacoraRepository,
                           RegistroAuditoriaRepository auditoriaRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.miembroProyectoRepository = miembroProyectoRepository;
        this.tareaRepository = tareaRepository;
        this.bitacoraRepository = bitacoraRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return; // Ya hay datos inicializados
        }

        System.out.println("===> Inicializando datos de demostración del Laboratorio...");

        // 1. Usuarios de los tres roles
        Usuario coordinador = new Usuario(
                "COORD-001",
                "Carlos",
                "Mendoza",
                "coordinador@laboratorio.edu",
                passwordEncoder.encode("Admin123!"),
                "Ingeniería de Sistemas",
                10,
                LocalDate.now().minusYears(3),
                RolUsuario.COORDINADOR
        );
        usuarioRepository.save(coordinador);

        Usuario asesor = new Usuario(
                "DOC-102",
                "Dra. Elena",
                "Gómez",
                "asesor@laboratorio.edu",
                passwordEncoder.encode("Asesor123!"),
                "Maestría en Computación",
                null,
                LocalDate.now().minusYears(2),
                RolUsuario.ASESOR
        );
        usuarioRepository.save(asesor);

        Usuario estudiante1 = new Usuario(
                "EST-2024-01",
                "Alejandro",
                "España",
                "estudiante@laboratorio.edu",
                passwordEncoder.encode("Estudiante123!"),
                "Ingeniería de Sistemas",
                8,
                LocalDate.now().minusMonths(6),
                RolUsuario.ESTUDIANTE
        );
        usuarioRepository.save(estudiante1);

        Usuario estudiante2 = new Usuario(
                "EST-2024-02",
                "Sofía",
                "Martínez",
                "sofia@laboratorio.edu",
                passwordEncoder.encode("Estudiante123!"),
                "Ingeniería de Software",
                7,
                LocalDate.now().minusMonths(4),
                RolUsuario.ESTUDIANTE
        );
        usuarioRepository.save(estudiante2);

        // 2. Proyectos de demostración
        Proyecto proyecto1 = new Proyecto(
                "Plataforma de Trazabilidad de Proyectos",
                "Sistema web centralizado para documentación versionada, bitácoras y gestión de tareas del laboratorio universitario.",
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(3)
        );
        proyecto1.cambiarEstado(EstadoProyecto.EN_DESARROLLO);
        proyectoRepository.save(proyecto1);

        Proyecto proyecto2 = new Proyecto(
                "Sistema de Telemetría IoT para Cultivos",
                "Monitoreo en tiempo real de humedad y temperatura mediante microcontroladores ESP32 y dashboard de visualización.",
                LocalDate.now().minusMonths(3),
                LocalDate.now().minusDays(5)
        );
        proyecto2.cerrarProyecto();
        proyectoRepository.save(proyecto2);

        // 3. Vincular miembros
        MiembroProyecto m1 = proyecto1.vincularMiembro(asesor, RolEnProyecto.ASESOR_LIDER, LocalDate.now().minusMonths(1));
        miembroProyectoRepository.save(m1);

        MiembroProyecto m2 = proyecto1.vincularMiembro(estudiante1, RolEnProyecto.ESTUDIANTE_DESARROLLADOR, LocalDate.now().minusMonths(1));
        miembroProyectoRepository.save(m2);

        MiembroProyecto m3 = proyecto1.vincularMiembro(estudiante2, RolEnProyecto.ESTUDIANTE_DESARROLLADOR, LocalDate.now().minusDays(20));
        miembroProyectoRepository.save(m3);

        // 4. Tareas iniciales
        Tarea t1 = new Tarea(
                proyecto1,
                "Diseño de arquitectura y base de datos relacional",
                "Definir esquema entidad-relación para usuarios, proyectos, miembros y bitácoras.",
                estudiante1,
                Prioridad.ALTA,
                LocalDate.now().minusDays(10),
                "Backend, Arquitectura, PostgreSQL"
        );
        t1.completar();
        tareaRepository.save(t1);

        Tarea t2 = new Tarea(
                proyecto1,
                "Implementar módulo de autenticación JWT y roles",
                "Configurar Spring Security con filtros para Estudiante, Asesor y Coordinador.",
                estudiante1,
                Prioridad.URGENTE,
                LocalDate.now().plusDays(2),
                "Seguridad, JWT, Backend"
        );
        t2.iniciarDesarrollo();
        tareaRepository.save(t2);

        Tarea t3 = new Tarea(
                proyecto1,
                "Diseño de interfaz de usuario en Angular",
                "Construir componentes de login, dashboard global y tablero kanban.",
                estudiante2,
                Prioridad.MEDIA,
                LocalDate.now().plusDays(7),
                "Frontend, Angular, UI"
        );
        tareaRepository.save(t3);

        // 5. Entradas de bitácora
        EntradaBitacora b1 = new EntradaBitacora(
                proyecto1,
                asesor,
                "Reunión de inicio y kick-off del proyecto",
                "Se definieron los roles y el alcance inicial del MVP. Los estudiantes Alejandro y Sofía se encargarán del backend y frontend respectivamente.",
                TipoEntradaBitacora.REUNION_ACTA
        );
        bitacoraRepository.save(b1);

        EntradaBitacora b2 = new EntradaBitacora(
                proyecto1,
                estudiante1,
                "Aprobación de la estructura DDD y modelo relacional",
                "Se validaron las invariantes del modelo rico de dominio y la estrategia de versionado de documentos.",
                TipoEntradaBitacora.HITO
        );
        bitacoraRepository.save(b2);

        // 6. Registro de Auditoría inicial
        RegistroAuditoria a1 = new RegistroAuditoria(
                "INICIALIZACION_SISTEMA",
                coordinador.getId(),
                coordinador.getCorreo(),
                coordinador.getRol().name(),
                "Sistema",
                "ALL",
                "Carga inicial de estructura y proyectos de laboratorio",
                "127.0.0.1"
        );
        auditoriaRepository.save(a1);

        System.out.println("===> Datos de demostración inicializados con éxito.");
    }
}
