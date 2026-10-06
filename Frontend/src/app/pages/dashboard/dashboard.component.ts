import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { DashboardService } from '../../core/services/dashboard.service';
import { ProyectoService } from '../../core/services/proyecto.service';
import { DashboardGlobalResponse, ProyectoResponse } from '../../core/models/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container-fluid py-4 px-4">
      <!-- Encabezado de Bienvenida -->
      <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
          <h3 class="fw-bold mb-1">Panel de Control</h3>
          <p class="text-muted mb-0">
            Bienvenido, <span class="fw-semibold text-dark">{{ authService.currentUser()?.nombreCompleto }}</span>
            &bull; Rol: <span class="badge bg-secondary">{{ authService.currentUser()?.rol }}</span>
          </p>
        </div>
        <div class="d-flex gap-2 mt-2 mt-sm-0">
          <a routerLink="/proyectos" class="btn btn-outline-primary btn-sm">
            <i class="bi bi-kanban me-1"></i> Ir a Proyectos
          </a>
          <a *ngIf="authService.hasRole('COORDINADOR')" routerLink="/integrantes" class="btn btn-primary btn-sm">
            <i class="bi bi-person-plus-fill me-1"></i> Gestionar Integrantes
          </a>
        </div>
      </div>

      <!-- VISTA PARA COORDINADOR: DASHBOARD GLOBAL DEL LABORATORIO -->
      <div *ngIf="authService.hasRole('COORDINADOR')">
        <!-- Indicadores Clave (KPIs) -->
        <div class="row g-3 mb-4" *ngIf="globalData">
          <div class="col-sm-6 col-lg-3">
            <div class="card h-100 border-start border-primary border-4">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <span class="text-muted small fw-semibold text-uppercase">Proyectos Totales</span>
                  <div class="p-2 rounded bg-primary bg-opacity-10 text-primary">
                    <i class="bi bi-folder-fill fs-5"></i>
                  </div>
                </div>
                <div class="fs-2 fw-bold text-dark">{{ globalData.totalProyectos }}</div>
                <div class="text-muted small mt-1">
                  <span class="text-success fw-semibold">{{ globalData.proyectosActivos }}</span> activos &bull;
                  <span class="text-secondary">{{ globalData.proyectosFinalizados }}</span> finalizados
                </div>
              </div>
            </div>
          </div>

          <div class="col-sm-6 col-lg-3">
            <div class="card h-100 border-start border-info border-4">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <span class="text-muted small fw-semibold text-uppercase">Integrantes</span>
                  <div class="p-2 rounded bg-info bg-opacity-10 text-info">
                    <i class="bi bi-people-fill fs-5"></i>
                  </div>
                </div>
                <div class="fs-2 fw-bold text-dark">{{ globalData.totalEstudiantes + globalData.totalAsesores }}</div>
                <div class="text-muted small mt-1">
                  <span>{{ globalData.totalEstudiantes }} estudiantes</span> &bull;
                  <span>{{ globalData.totalAsesores }} asesores</span>
                </div>
              </div>
            </div>
          </div>

          <div class="col-sm-6 col-lg-3">
            <div class="card h-100 border-start border-success border-4">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <span class="text-muted small fw-semibold text-uppercase">Tareas Realizadas</span>
                  <div class="p-2 rounded bg-success bg-opacity-10 text-success">
                    <i class="bi bi-check2-circle fs-5"></i>
                  </div>
                </div>
                <div class="fs-2 fw-bold text-dark">{{ globalData.tareasCompletadas }} / {{ globalData.totalTareas }}</div>
                <div class="progress mt-2" style="height: 6px;">
                  <div class="progress-bar bg-success" [style.width.%]="calcularPorcentajeTareas(globalData)"></div>
                </div>
              </div>
            </div>
          </div>

          <div class="col-sm-6 col-lg-3">
            <div class="card h-100 border-start border-warning border-4">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <span class="text-muted small fw-semibold text-uppercase">Documentación</span>
                  <div class="p-2 rounded bg-warning bg-opacity-10 text-warning">
                    <i class="bi bi-file-earmark-text-fill fs-5"></i>
                  </div>
                </div>
                <div class="fs-2 fw-bold text-dark">{{ globalData.totalDocumentos }}</div>
                <div class="text-muted small mt-1">Versiones y actas archivadas</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Alerta de Proyectos sin Actividad Reciente -->
        <div class="card mb-4 border-warning" *ngIf="globalData?.proyectosSinActividadReciente?.length">
          <div class="card-header bg-warning bg-opacity-10 text-warning-emphasis d-flex align-items-center gap-2">
            <i class="bi bi-exclamation-triangle-fill text-warning"></i>
            <span class="fw-bold">Alerta de Seguimiento: Proyectos sin actividad reciente</span>
          </div>
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table table-hover align-middle mb-0">
                <thead class="table-light small">
                  <tr>
                    <th>Proyecto</th>
                    <th>Estado</th>
                    <th>Progreso</th>
                    <th>Acción</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let p of globalData?.proyectosSinActividadReciente">
                    <td class="fw-semibold">{{ p.nombre }}</td>
                    <td><span class="badge bg-primary">{{ p.estado }}</span></td>
                    <td style="width: 200px;">
                      <div class="progress" style="height: 6px;">
                        <div class="progress-bar bg-primary" [style.width.%]="p.porcentajeProgreso"></div>
                      </div>
                      <span class="small text-muted">{{ p.porcentajeProgreso }}%</span>
                    </td>
                    <td>
                      <a [routerLink]="['/proyectos', p.id]" class="btn btn-sm btn-outline-secondary">Ver Proyecto</a>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <!-- VISTA DE PROYECTOS PARA ASESORES Y ESTUDIANTES -->
      <div class="mb-4">
        <h5 class="fw-bold mb-3 d-flex align-items-center gap-2">
          <i class="bi bi-folder-check text-primary"></i>
          <span>{{ authService.hasRole('COORDINADOR') ? 'Todos los Proyectos del Laboratorio' : 'Mis Proyectos Asignados' }}</span>
        </h5>

        <div *ngIf="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
          <p class="text-muted mt-2">Cargando información...</p>
        </div>

        <div *ngIf="!loading && misProyectos.length === 0" class="card text-center p-5 bg-white">
          <div class="text-muted mb-3"><i class="bi bi-folder-x fs-1"></i></div>
          <h5>No se encontraron proyectos asignados</h5>
          <p class="text-muted small">No estás vinculado activamente a ningún proyecto en este momento.</p>
        </div>

        <div class="row g-3" *ngIf="!loading && misProyectos.length > 0">
          <div class="col-md-6 col-lg-4" *ngFor="let proy of misProyectos">
            <div class="card h-100 shadow-sm border-0">
              <div class="card-body d-flex flex-column">
                <div class="d-flex justify-content-between align-items-start mb-2">
                  <span class="badge" [ngClass]="{
                    'bg-info text-dark': proy.estado === 'PLANEACION',
                    'bg-primary': proy.estado === 'EN_DESARROLLO',
                    'bg-success': proy.estado === 'FINALIZADO',
                    'bg-danger': proy.estado === 'CANCELADO'
                  }">{{ proy.estado }}</span>
                  <small class="text-muted"><i class="bi bi-people me-1"></i>{{ proy.totalMiembros }} miembros</small>
                </div>

                <h5 class="card-title fw-bold text-dark mb-2">{{ proy.nombre }}</h5>
                <p class="card-text text-muted small flex-grow-1">{{ proy.descripcion || 'Sin descripción disponible' }}</p>

                <div class="mt-3">
                  <div class="d-flex justify-content-between small text-muted mb-1">
                    <span>Avance de tareas</span>
                    <span class="fw-semibold">{{ proy.tareasCompletadas }}/{{ proy.totalTareas }} ({{ proy.porcentajeProgreso }}%)</span>
                  </div>
                  <div class="progress mb-3" style="height: 6px;">
                    <div class="progress-bar bg-primary" [style.width.%]="proy.porcentajeProgreso"></div>
                  </div>

                  <a [routerLink]="['/proyectos', proy.id]" class="btn btn-outline-primary w-100 btn-sm fw-semibold">
                    <i class="bi bi-box-arrow-in-right me-1"></i> Abrir Entorno de Trabajo
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  globalData: DashboardGlobalResponse | null = null;
  misProyectos: ProyectoResponse[] = [];
  loading = true;

  constructor(
    public authService: AuthService,
    private dashboardService: DashboardService,
    private proyectoService: ProyectoService
  ) {}

  ngOnInit(): void {
    if (this.authService.hasRole('COORDINADOR')) {
      this.dashboardService.obtenerGlobal().subscribe({
        next: (res) => this.globalData = res,
        error: (err) => console.error(err)
      });
    }

    this.proyectoService.listar().subscribe({
      next: (res) => {
        this.misProyectos = res;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
      }
    });
  }

  calcularPorcentajeTareas(data: DashboardGlobalResponse): number {
    if (!data.totalTareas) return 0;
    return Math.round((data.tareasCompletadas / data.totalTareas) * 100);
  }
}
