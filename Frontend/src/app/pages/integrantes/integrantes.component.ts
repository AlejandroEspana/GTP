import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { UsuarioResponse, FichaIntegranteResponse, RolUsuario } from '../../core/models/models';

@Component({
  selector: 'app-integrantes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container-fluid py-4 px-4">
      <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
          <h3 class="fw-bold mb-1">Integrantes del Laboratorio</h3>
          <p class="text-muted mb-0">Directorio de estudiantes, asesores e investigadores</p>
        </div>
        <button *ngIf="authService.hasRole('COORDINADOR')" class="btn btn-primary btn-sm" (click)="abrirModalCrear()">
          <i class="bi bi-person-plus-fill me-1"></i> Nuevo Integrante
        </button>
      </div>

      <!-- Filtros -->
      <div class="row g-2 mb-4">
        <div class="col-md-6 col-lg-4">
          <div class="input-group">
            <span class="input-group-text bg-white text-muted"><i class="bi bi-search"></i></span>
            <input type="text" class="form-control" placeholder="Buscar por nombre o correo..." [(ngModel)]="filtroTexto" (input)="filtrar()">
          </div>
        </div>
        <div class="col-md-6 col-lg-3">
          <select class="form-select" [(ngModel)]="filtroRol" (change)="filtrar()">
            <option value="">Todos los Roles</option>
            <option value="ESTUDIANTE">Estudiantes</option>
            <option value="ASESOR">Asesores</option>
            <option value="COORDINADOR">Coordinadores</option>
          </select>
        </div>
      </div>

      <!-- Tabla de Integrantes -->
      <div class="card border-0 shadow-sm">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0">
            <thead class="table-light small">
              <tr>
                <th>Nombre Completo</th>
                <th>Correo Institucional</th>
                <th>Código</th>
                <th>Rol</th>
                <th>Programa</th>
                <th>Estado</th>
                <th class="text-end">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let u of integrantesFiltrados">
                <td class="fw-semibold">
                  <div class="d-flex align-items-center gap-2">
                    <div class="avatar-circle bg-primary bg-opacity-10 text-primary fw-bold px-2 py-1 rounded">
                      {{ u.nombres[0] }}{{ u.apellidos[0] }}
                    </div>
                    <span>{{ u.nombres }} {{ u.apellidos }}</span>
                  </div>
                </td>
                <td>{{ u.correo }}</td>
                <td><span class="badge bg-light text-secondary border">{{ u.codigo || 'N/A' }}</span></td>
                <td>
                  <span class="badge" [ngClass]="{
                    'bg-primary': u.rol === 'COORDINADOR',
                    'bg-info text-dark': u.rol === 'ASESOR',
                    'bg-success': u.rol === 'ESTUDIANTE'
                  }">{{ u.rol }}</span>
                </td>
                <td class="small text-muted">{{ u.programaAcademico || 'N/A' }}</td>
                <td>
                  <span class="badge" [class.bg-success]="u.activo" [class.bg-danger]="!u.activo">
                    {{ u.activo ? 'Activo' : 'Inactivo' }}
                  </span>
                </td>
                <td class="text-end">
                  <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary" (click)="verFicha(u.id)">
                      <i class="bi bi-person-lines-fill me-1"></i> Ficha
                    </button>
                    <button *ngIf="authService.hasRole('COORDINADOR') && u.activo"
                            class="btn btn-outline-danger" (click)="cambiarEstado(u.id, false)">
                      Desactivar
                    </button>
                    <button *ngIf="authService.hasRole('COORDINADOR') && !u.activo"
                            class="btn btn-outline-success" (click)="cambiarEstado(u.id, true)">
                      Activar
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- MODAL FICHA INDIVIDUAL DEL INTEGRANTE -->
      <div class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);" *ngIf="mostrarModalFicha && ficha">
        <div class="modal-dialog modal-lg modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header bg-light">
              <div>
                <h5 class="modal-title fw-bold mb-0">Ficha Individual del Integrante</h5>
                <span class="small text-muted">ID de auditoría: {{ ficha.id }}</span>
              </div>
              <button type="button" class="btn-close" (click)="mostrarModalFicha = false"></button>
            </div>
            <div class="modal-body p-4">
              <!-- Datos Personales y Académicos -->
              <div class="row g-3 mb-4">
                <div class="col-sm-6">
                  <div class="p-3 bg-light rounded">
                    <span class="text-muted small d-block">Nombres y Apellidos</span>
                    <span class="fs-5 fw-bold text-dark">{{ ficha.nombres }} {{ ficha.apellidos }}</span>
                  </div>
                </div>
                <div class="col-sm-6">
                  <div class="p-3 bg-light rounded">
                    <span class="text-muted small d-block">Correo Institucional</span>
                    <span class="fs-5 fw-semibold text-primary">{{ ficha.correo }}</span>
                  </div>
                </div>
                <div class="col-sm-4">
                  <span class="text-muted small d-block">Código Estudiantil / ID</span>
                  <span class="fw-semibold">{{ ficha.codigo || 'N/A' }}</span>
                </div>
                <div class="col-sm-4">
                  <span class="text-muted small d-block">Rol en la Plataforma</span>
                  <span class="badge bg-secondary">{{ ficha.rol }}</span>
                </div>
                <div class="col-sm-4">
                  <span class="text-muted small d-block">Fecha Ingreso al Laboratorio</span>
                  <span class="fw-semibold">{{ ficha.fechaIngresoLaboratorio }}</span>
                </div>
                <div class="col-sm-6">
                  <span class="text-muted small d-block">Programa Académico</span>
                  <span class="fw-semibold">{{ ficha.programaAcademico || 'No especificado' }}</span>
                </div>
                <div class="col-sm-6">
                  <span class="text-muted small d-block">Semestre</span>
                  <span class="fw-semibold">{{ ficha.semestre ? 'Semestre ' + ficha.semestre : 'N/A' }}</span>
                </div>
              </div>

              <!-- Historial de Participación en Proyectos -->
              <h6 class="fw-bold text-dark border-bottom pb-2 mb-3">
                <i class="bi bi-folder-check text-primary me-1"></i> Historial de Proyectos Vinculados
              </h6>
              <div class="table-responsive mb-4" *ngIf="ficha.participaciones.length > 0">
                <table class="table table-sm table-bordered align-middle">
                  <thead class="table-light small">
                    <tr>
                      <th>Proyecto</th>
                      <th>Rol en Proyecto</th>
                      <th>Inicio</th>
                      <th>Salida</th>
                      <th>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr *ngFor="let p of ficha.participaciones">
                      <td class="fw-semibold">{{ p.proyectoNombre }}</td>
                      <td><span class="badge bg-light text-dark border">{{ p.rolEnProyecto }}</span></td>
                      <td>{{ p.fechaInicio }}</td>
                      <td>{{ p.fechaFin || 'Vigente' }}</td>
                      <td>
                        <span class="badge" [class.bg-success]="p.activo" [class.bg-secondary]="!p.activo">
                          {{ p.activo ? 'Activo' : 'Finalizado' }}
                        </span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <p class="text-muted small" *ngIf="ficha.participaciones.length === 0">Sin historial de proyectos registrado.</p>

              <!-- Actividad Reciente -->
              <h6 class="fw-bold text-dark border-bottom pb-2 mb-3">
                <i class="bi bi-clock-history text-secondary me-1"></i> Actividad Reciente Registrada
              </h6>
              <div class="list-group list-group-flush small" *ngIf="ficha.actividadesRecientes.length > 0">
                <div class="list-group-item px-0" *ngFor="let a of ficha.actividadesRecientes">
                  <div class="d-flex justify-content-between align-items-center">
                    <span class="fw-bold text-dark">{{ a.accion }}</span>
                    <span class="text-muted">{{ a.fechaHora | date:'short' }}</span>
                  </div>
                  <span class="text-muted d-block">{{ a.detalles }}</span>
                </div>
              </div>
              <p class="text-muted small" *ngIf="ficha.actividadesRecientes.length === 0">Sin actividad reciente registrada.</p>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary btn-sm" (click)="mostrarModalFicha = false">Cerrar Ficha</button>
            </div>
          </div>
        </div>
      </div>

      <!-- MODAL CREAR USUARIO (COORDINADOR) -->
      <div class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);" *ngIf="mostrarModalCrear">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title fw-bold">Registrar Nuevo Integrante</h5>
              <button type="button" class="btn-close" (click)="mostrarModalCrear = false"></button>
            </div>
            <div class="modal-body">
              <div class="row g-2 mb-3">
                <div class="col-6">
                  <label class="form-label small fw-semibold">Nombres *</label>
                  <input type="text" class="form-control" [(ngModel)]="nuevoNombres">
                </div>
                <div class="col-6">
                  <label class="form-label small fw-semibold">Apellidos *</label>
                  <input type="text" class="form-control" [(ngModel)]="nuevoApellidos">
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Correo Institucional *</label>
                <input type="email" class="form-control" [(ngModel)]="nuevoCorreo" placeholder="usuario@laboratorio.edu">
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Contraseña Inicial *</label>
                <input type="password" class="form-control" [(ngModel)]="nuevoPassword" placeholder="••••••••">
              </div>
              <div class="row g-2 mb-3">
                <div class="col-6">
                  <label class="form-label small fw-semibold">Código Institucional</label>
                  <input type="text" class="form-control" [(ngModel)]="nuevoCodigo" placeholder="EST-001">
                </div>
                <div class="col-6">
                  <label class="form-label small fw-semibold">Rol *</label>
                  <select class="form-select" [(ngModel)]="nuevoRol">
                    <option value="ESTUDIANTE">Estudiante</option>
                    <option value="ASESOR">Asesor</option>
                    <option value="COORDINADOR">Coordinador</option>
                  </select>
                </div>
              </div>
              <div class="row g-2 mb-3">
                <div class="col-8">
                  <label class="form-label small fw-semibold">Programa Académico</label>
                  <input type="text" class="form-control" [(ngModel)]="nuevoPrograma" placeholder="Ingeniería de Sistemas">
                </div>
                <div class="col-4">
                  <label class="form-label small fw-semibold">Semestre</label>
                  <input type="number" class="form-control" [(ngModel)]="nuevoSemestre">
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary btn-sm" (click)="mostrarModalCrear = false">Cancelar</button>
              <button class="btn btn-primary btn-sm" (click)="guardarUsuario()" [disabled]="!nuevoNombres || !nuevoApellidos || !nuevoCorreo || !nuevoPassword">
                Registrar Integrante
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class IntegrantesComponent implements OnInit {
  integrantes: UsuarioResponse[] = [];
  integrantesFiltrados: UsuarioResponse[] = [];

  filtroTexto = '';
  filtroRol = '';

  mostrarModalFicha = false;
  ficha: FichaIntegranteResponse | null = null;

  mostrarModalCrear = false;
  nuevoNombres = '';
  nuevoApellidos = '';
  nuevoCorreo = '';
  nuevoPassword = '';
  nuevoCodigo = '';
  nuevoRol: RolUsuario = 'ESTUDIANTE';
  nuevoPrograma = '';
  nuevoSemestre: number | null = null;

  constructor(
    public authService: AuthService,
    private usuarioService: UsuarioService
  ) {}

  ngOnInit(): void {
    this.cargarIntegrantes();
  }

  cargarIntegrantes(): void {
    this.usuarioService.listar().subscribe({
      next: (res) => {
        this.integrantes = res;
        this.integrantesFiltrados = res;
      },
      error: (err) => console.error(err)
    });
  }

  filtrar(): void {
    this.integrantesFiltrados = this.integrantes.filter(u => {
      const matchTexto = !this.filtroTexto ||
        (u.nombres + ' ' + u.apellidos + ' ' + u.correo).toLowerCase().includes(this.filtroTexto.toLowerCase());
      const matchRol = !this.filtroRol || u.rol === this.filtroRol;
      return matchTexto && matchRol;
    });
  }

  verFicha(id: string): void {
    this.usuarioService.obtenerFicha(id).subscribe({
      next: (res) => {
        this.ficha = res;
        this.mostrarModalFicha = true;
      },
      error: (err) => alert(err.error?.message || err.message)
    });
  }

  cambiarEstado(id: string, activar: boolean): void {
    const accion = activar ? this.usuarioService.activar(id) : this.usuarioService.desactivar(id);
    accion.subscribe({
      next: () => this.cargarIntegrantes(),
      error: (err) => alert(err.error?.message || err.message)
    });
  }

  abrirModalCrear(): void {
    this.nuevoNombres = '';
    this.nuevoApellidos = '';
    this.nuevoCorreo = '';
    this.nuevoPassword = '';
    this.nuevoCodigo = '';
    this.nuevoRol = 'ESTUDIANTE';
    this.nuevoPrograma = '';
    this.nuevoSemestre = null;
    this.mostrarModalCrear = true;
  }

  guardarUsuario(): void {
    this.usuarioService.crear({
      nombres: this.nuevoNombres,
      apellidos: this.nuevoApellidos,
      correo: this.nuevoCorreo,
      password: this.nuevoPassword,
      codigo: this.nuevoCodigo || undefined,
      rol: this.nuevoRol,
      programaAcademico: this.nuevoPrograma || undefined,
      semestre: this.nuevoSemestre || undefined,
      fechaIngresoLaboratorio: new Date().toISOString().split('T')[0]
    }).subscribe({
      next: () => {
        this.mostrarModalCrear = false;
        this.cargarIntegrantes();
      },
      error: (err) => alert(err.error?.message || err.message)
    });
  }
}
