import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditoriaService } from '../../core/services/auditoria.service';
import { AuditoriaResponse } from '../../core/models/models';

@Component({
  selector: 'app-auditoria',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container-fluid py-4 px-4">
      <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
          <h3 class="fw-bold mb-1">Historial de Auditoría</h3>
          <p class="text-muted mb-0">Registro inmutable de acciones para trazabilidad y cumplimiento de privacidad</p>
        </div>
      </div>

      <!-- Filtros -->
      <div class="row g-2 mb-4">
        <div class="col-md-6 col-lg-4">
          <div class="input-group">
            <span class="input-group-text bg-white text-muted"><i class="bi bi-search"></i></span>
            <input type="text" class="form-control" placeholder="Buscar por acción o correo..." [(ngModel)]="filtroTexto" (input)="filtrar()">
          </div>
        </div>
      </div>

      <!-- Tabla de Auditoría -->
      <div class="card border-0 shadow-sm">
        <div class="table-responsive">
          <table class="table table-hover align-middle mb-0 font-monospace small">
            <thead class="table-light">
              <tr>
                <th>Fecha y Hora</th>
                <th>Acción</th>
                <th>Usuario Responsable</th>
                <th>Rol</th>
                <th>Entidad Afectada</th>
                <th>Detalles de la Operación</th>
                <th>IP</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let a of logsFiltrados">
                <td class="text-nowrap text-secondary">{{ a.fechaHora | date:'medium' }}</td>
                <td>
                  <span class="badge" [ngClass]="{
                    'bg-primary': a.accion.includes('CREACION') || a.accion.includes('REGISTRO'),
                    'bg-warning text-dark': a.accion.includes('ACTUALIZACION') || a.accion.includes('CAMBIO'),
                    'bg-danger': a.accion.includes('ELIMINACION') || a.accion.includes('DESACTIVACION'),
                    'bg-info text-dark': a.accion.includes('INICIO_SESION') || a.accion.includes('CONSULTA'),
                    'bg-secondary': !a.accion.includes('CREACION') && !a.accion.includes('ACTUALIZACION')
                  }">{{ a.accion }}</span>
                </td>
                <td class="fw-semibold text-dark">{{ a.correoUsuario }}</td>
                <td><span class="badge bg-light text-secondary border">{{ a.rolUsuario }}</span></td>
                <td><span class="fw-semibold text-primary">{{ a.entidadAfectada }}</span></td>
                <td class="text-secondary text-truncate" style="max-width: 320px;" [title]="a.detalles">{{ a.detalles }}</td>
                <td class="text-muted small">{{ a.ipOrigen || '127.0.0.1' }}</td>
              </tr>
              <tr *ngIf="logsFiltrados.length === 0">
                <td colspan="7" class="text-center py-4 text-muted">No se encontraron registros de auditoría.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class AuditoriaComponent implements OnInit {
  logs: AuditoriaResponse[] = [];
  logsFiltrados: AuditoriaResponse[] = [];
  filtroTexto = '';

  constructor(private auditoriaService: AuditoriaService) {}

  ngOnInit(): void {
    this.cargarLogs();
  }

  cargarLogs(): void {
    this.auditoriaService.listar().subscribe({
      next: (res) => {
        this.logs = res;
        this.logsFiltrados = res;
      },
      error: (err) => console.error(err)
    });
  }

  filtrar(): void {
    this.logsFiltrados = this.logs.filter(l => {
      return !this.filtroTexto ||
        (l.accion + ' ' + l.correoUsuario + ' ' + l.entidadAfectada + ' ' + l.detalles)
          .toLowerCase()
          .includes(this.filtroTexto.toLowerCase());
    });
  }
}
