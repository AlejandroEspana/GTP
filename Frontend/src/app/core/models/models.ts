export type RolUsuario = 'ESTUDIANTE' | 'ASESOR' | 'COORDINADOR';
export type RolEnProyecto = 'ESTUDIANTE_DESARROLLADOR' | 'ASESOR_LIDER' | 'ASESOR_APOYO';
export type EstadoProyecto = 'PLANEACION' | 'EN_DESARROLLO' | 'FINALIZADO' | 'CANCELADO';
export type EstadoTarea = 'PENDIENTE' | 'EN_DESARROLLO' | 'COMPLETADA';
export type Prioridad = 'BAJA' | 'MEDIA' | 'ALTA' | 'URGENTE';
export type CategoriaDocumento = 'REQUERIMIENTOS' | 'DISENO' | 'METODOLOGIA' | 'INFORMES' | 'PRESENTACIONES';
export type CategoriaEvidencia = 'PROTOTIPO' | 'DESARROLLO' | 'PRUEBAS' | 'PRESENTACION' | 'INVESTIGACION' | 'EVIDENCIA';
export type TipoEntradaBitacora = 'AVANCE' | 'REUNION_ACTA' | 'INCIDENCIA' | 'HITO';

export interface AuthResponse {
  token: string;
  id: string;
  correo: string;
  nombreCompleto: string;
  rol: RolUsuario;
  codigo?: string;
}

export interface UsuarioResponse {
  id: string;
  codigo?: string;
  nombres: string;
  apellidos: string;
  correo: string;
  programaAcademico?: string;
  semestre?: number;
  fechaIngresoLaboratorio: string;
  rol: RolUsuario;
  activo: boolean;
}

export interface ParticipacionProyecto {
  proyectoId: string;
  proyectoNombre: string;
  rolEnProyecto: RolEnProyecto;
  fechaInicio: string;
  fechaFin?: string;
  activo: boolean;
}

export interface ActividadReciente {
  accion: string;
  entidadAfectada: string;
  detalles: string;
  fechaHora: string;
}

export interface FichaIntegranteResponse {
  id: string;
  codigo?: string;
  nombres: string;
  apellidos: string;
  correo: string;
  programaAcademico?: string;
  semestre?: number;
  fechaIngresoLaboratorio: string;
  rol: RolUsuario;
  activo: boolean;
  participaciones: ParticipacionProyecto[];
  actividadesRecientes: ActividadReciente[];
}

export interface ProyectoResponse {
  id: string;
  nombre: string;
  descripcion?: string;
  estado: EstadoProyecto;
  fechaInicio?: string;
  fechaFin?: string;
  fechaCreacion: string;
  totalMiembros: number;
  totalTareas: number;
  tareasCompletadas: number;
  porcentajeProgreso: number;
}

export interface MiembroProyectoResponse {
  id: string;
  usuarioId: string;
  codigoUsuario?: string;
  nombreUsuario: string;
  correoUsuario: string;
  rolEnProyecto: RolEnProyecto;
  fechaInicio: string;
  fechaFin?: string;
  activo: boolean;
}

export interface TareaResponse {
  id: string;
  proyectoId: string;
  titulo: string;
  descripcion?: string;
  responsableId?: string;
  responsableNombre?: string;
  estado: EstadoTarea;
  prioridad: Prioridad;
  fechaLimite?: string;
  etiquetas?: string;
  fechaCreacion: string;
}

export interface VersionDocumentoResponse {
  id: string;
  numeroVersion: number;
  autorId: string;
  autorNombre: string;
  fechaSubida: string;
  archivoUrl: string;
  nombreArchivoOriginal?: string;
  resumenCambios?: string;
  tamanoBytes?: number;
  tipoContenido?: string;
}

export interface DocumentoResponse {
  id: string;
  proyectoId: string;
  titulo: string;
  descripcion?: string;
  categoria: CategoriaDocumento;
  fechaCreacion: string;
  versiones: VersionDocumentoResponse[];
  totalVersiones: number;
  ultimaVersion?: VersionDocumentoResponse;
}

export interface EvidenciaResponse {
  id: string;
  proyectoId: string;
  titulo: string;
  descripcion?: string;
  autorId: string;
  autorNombre: string;
  fechaSubida: string;
  categoria: CategoriaEvidencia;
  archivoUrl: string;
  tipoArchivo?: string;
}

export interface BitacoraResponse {
  id: string;
  proyectoId: string;
  autorId: string;
  autorNombre: string;
  fechaHora: string;
  titulo: string;
  contenido: string;
  tipo: TipoEntradaBitacora;
}

export interface AuditoriaResponse {
  id: string;
  accion: string;
  usuarioId?: string;
  correoUsuario: string;
  rolUsuario: string;
  entidadAfectada: string;
  entidadId?: string;
  detalles: string;
  fechaHora: string;
  ipOrigen?: string;
}

export interface DashboardGlobalResponse {
  totalProyectos: number;
  proyectosActivos: number;
  proyectosFinalizados: number;
  proyectosPlaneacion: number;
  totalEstudiantes: number;
  totalAsesores: number;
  totalDocumentos: number;
  totalTareas: number;
  tareasCompletadas: number;
  proyectosSinActividadReciente: ProyectoResponse[];
}

export interface DashboardProyectoResponse {
  proyectoId: string;
  proyectoNombre: string;
  estado: EstadoProyecto;
  totalTareas: number;
  tareasPendientes: number;
  tareasEnDesarrollo: number;
  tareasCompletadas: number;
  progreso: number;
  totalDocs: number;
  totalEvidencias: number;
  totalMiembros: number;
  ultimaActividad?: string;
}
