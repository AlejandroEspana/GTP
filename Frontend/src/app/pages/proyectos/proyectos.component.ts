import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { ProyectoService } from '../../core/services/proyecto.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { ProyectoResponse, UsuarioResponse } from '../../core/models/models';

@Component({
  selector: 'app-proyectos',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="container-fluid py-4 px-4">
      <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
          <h3 class="fw-bold mb-1">Proyectos de Software</h3>
          <p class="text-muted mb-0">Gestión de iniciativas, desarrollo y espacios colaborativos</p>
        </div>
        <button *ngIf="authService.hasRole('COORDINADOR')" class="btn btn-primary btn-sm d-flex align-items-center gap-1" (click)="abrirModalCrear()">
          <i class="bi bi-plus-lg"></i>
          <span>Nuevo Proyecto</span>
        </button>
      </div>

      <!-- Filtros y Búsqueda -->
      <div class="row g-2 mb-4">
        <div class="col-md-6 col-lg-4">
          <div class="input-group">
            <span class="input-group-text bg-white text-muted"><i class="bi bi-search"></i></span>
            <input type="text" class="form-control" placeholder="Buscar por nombre..." [(ngModel)]="filtroTexto" (input)="filtrar()">
          </div>
        </div>
        <div class="col-md-6 col-lg-3">
          <select class="form-select" [(ngModel)]="filtroEstado" (change)="filtrar()">
            <option value="">Todos los Estados</option>
            <option value="PLANEACION">Planeación</option>
            <option value="EN_DESARROLLO">En Desarrollo</option>
            <option value="FINALIZADO">Finalizado</option>
            <option value="CANCELADO">Cancelado</option>
          </select>
        </div>
      </div>

      <!-- Spinner -->
      <div *ngIf="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="text-muted mt-2">Cargando proyectos...</p>
      </div>

      <!-- Lista de Proyectos -->
      <div class="row g-3" *ngIf="!loading">
        <div class="col-md-6 col-lg-4" *ngFor="let p of proyectosFiltrados">
          <div class="card h-100 shadow-sm border-0">
            <div class="card-body d-flex flex-column">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <span class="badge" [ngClass]="{
                  'bg-info text-dark': p.estado === 'PLANEACION',
                  'bg-primary': p.estado === 'EN_DESARROLLO',
                  'bg-success': p.estado === 'FINALIZADO',
                  'bg-danger': p.estado === 'CANCELADO'
                }">{{ p.estado }}</span>
                <span class="badge bg-light text-secondary border">
                  <i class="bi bi-people me-1"></i>{{ p.totalMiembros }}
                </span>
              </div>

              <h5 class="card-title fw-bold text-dark mb-2">{{ p.nombre }}</h5>
              <p class="card-text text-muted small flex-grow-1">{{ p.descripcion || 'Sin descripción' }}</p>

              <div class="mt-3">
                <div class="d-flex justify-content-between small text-muted mb-1">
                  <span>Tareas</span>
                  <span class="fw-semibold">{{ p.tareasCompletadas }}/{{ p.totalTareas }} ({{ p.porcentajeProgreso }}%)</span>
                </div>
                <div class="progress mb-3" style="height: 6px;">
                  <div class="progress-bar bg-primary" [style.width.%]="p.porcentajeProgreso"></div>
                </div>

                <div class="d-flex justify-content-between align-items-center small text-muted mb-3">
                  <span><i class="bi bi-calendar3 me-1"></i>Inicio: {{ p.fechaInicio || 'N/A' }}</span>
                  <span><i class="bi bi-calendar-check me-1"></i>Fin: {{ p.fechaFin || 'Activo' }}</span>
                </div>

                <a [routerLink]="['/proyectos', p.id]" class="btn btn-primary w-100 btn-sm">
                  <i class="bi bi-arrow-right-circle me-1"></i> Entorno de Trabajo
                </a>
              </div>
            </div>
          </div>
        </div>

        <div class="col-12" *ngIf="proyectosFiltrados.length === 0">
          <div class="card text-center p-5 bg-white border-dashed">
            <i class="bi bi-folder-x fs-1 text-muted mb-2"></i>
            <h5 class="text-secondary">No se encontraron proyectos</h5>
            <p class="text-muted small">Intenta ajustar los filtros de búsqueda.</p>
          </div>
        </div>
      </div>

      <!-- Modal Crear Proyecto (Coordinador) -->
      <div class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);" *ngIf="mostrarModalCrear">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title fw-bold">Crear Nuevo Proyecto</h5>
              <button type="button" class="btn-close" (click)="cerrarModalCrear()"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label small fw-semibold">Nombre del Proyecto *</label>
                <input type="text" class="form-control" [(ngModel)]="nuevoNombre" placeholder="Ej. Plataforma de Trazabilidad">
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Descripción</label>
                <textarea class="form-control" rows="3" [(ngModel)]="nuevaDescripcion" placeholder="Objetivos y alcance del proyecto..."></textarea>
              </div>
              <div class="row g-2 mb-3">
                <div class="col-6">
                  <label class="form-label small fw-semibold">Fecha Inicio</label>
                  <input type="date" class="form-control" [(ngModel)]="nuevaFechaInicio">
                </div>
                <div class="col-6">
                  <label class="form-label small fw-semibold">Fecha Fin Estimada</label>
                  <input type="date" class="form-control" [(ngModel)]="nuevaFechaFin">
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Asesor Líder Asignado</label>
                <select class="form-select" [(ngModel)]="nuevoAsesorId">
                  <option [ngValue]="null">Seleccionar asesor...</option>
                  <option *ngFor="let a of asesoresDisponibles" [value]="a.id">
                    {{ a.nombres }} {{ a.apellidos }} ({{ a.correo }})
                  </option>
                </select>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary btn-sm" (click)="cerrarModalCrear()">Cancelar</button>
              <button type="button" class="btn btn-primary btn-sm" (click)="guardarProyecto()" [disabled]="!nuevoNombre">Guardar Proyecto</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ProyectosComponent implements OnInit {
  proyectos: ProyectoResponse[] = [];
  proyectosFiltrados: ProyectoResponse[] = [];
  asesoresDisponibles: UsuarioResponse[] = [];
  loading = true;

  filtroTexto = '';
  filtroEstado = '';

  mostrarModalCrear = false;
  nuevoNombre = '';
  nuevaDescripcion = '';
  nuevaFechaInicio = new Date().toISOString().split('T')[0];
  nuevaFechaFin = '';
  nuevoAsesorId: string | null = null;

  constructor(
    public authService: AuthService,
    private proyectoService: ProyectoService,
    private usuarioService: UsuarioService
  ) {}

  ngOnInit(): void {
    this.cargarProyectos();
    if (this.authService.hasRole('COORDINADOR')) {
      this.usuarioService.listar('ASESOR').subscribe({
        next: (res) => this.asesoresDisponibles = res,
        error: (err) => console.error(err)
      });
    }
  }

  cargarProyectos(): void {
    this.loading = true;
    this.proyectoService.listar().subscribe({
      next: (res) => {
        this.proyectos = res;
        this.proyectosFiltrados = res;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
      }
    });
  }

  filtrar(): void {
    this.proyectosFiltrados = this.proyectos.filter(p => {
      const coincideTexto = !this.filtroTexto || p.nombre.toLowerCase().includes(this.filtroTexto.toLowerCase());
      const coincideEstado = !this.filtroEstado || p.estado === this.filtroEstado;
      return coincideTexto && coincideEstado;
    });
  }

  abrirModalCrear(): void {
    this.mostrarModalCrear = true;
  }

  cerrarModalCrear(): void {
    this.mostrarModalCrear = false;
    this.nuevoNombre = '';
    this.nuevaDescripcion = '';
    this.nuevoAsesorId = null;
  }

  guardarProyecto(): void {
    if (!this.nuevoNombre) return;

    this.proyectoService.crear({
      nombre: this.nuevoNombre,
      descripcion: this.nuevaDescripcion,
      fechaInicio: this.nuevaFechaInicio,
      fechaFin: this.nuevaFechaFin || undefined,
      asesorLiderId: this.nuevoAsesorId || undefined
    }).subscribe({
      next: () => {
        this.cerrarModalCrear();
        this.cargarProyectos();
      },
      error: (err) => alert('Error al crear proyecto: ' + (err.error?.message || err.message))
    });
  }
}
