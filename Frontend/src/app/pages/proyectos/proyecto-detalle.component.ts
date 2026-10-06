import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { ProyectoService } from '../../core/services/proyecto.service';
import { TareaService } from '../../core/services/tarea.service';
import { DocumentoService } from '../../core/services/documento.service';
import { EvidenciaService } from '../../core/services/evidencia.service';
import { BitacoraService } from '../../core/services/bitacora.service';
import { UsuarioService } from '../../core/services/usuario.service';
import {
  ProyectoResponse,
  MiembroProyectoResponse,
  TareaResponse,
  DocumentoResponse,
  EvidenciaResponse,
  BitacoraResponse,
  UsuarioResponse,
  EstadoTarea,
  Prioridad,
  CategoriaDocumento,
  CategoriaEvidencia,
  TipoEntradaBitacora,
  RolEnProyecto
} from '../../core/models/models';

@Component({
  selector: 'app-proyecto-detalle',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="container-fluid py-4 px-4" *ngIf="proyecto">
      <!-- Encabezado del Proyecto -->
      <div class="card mb-4 border-0 shadow-sm bg-white">
        <div class="card-body p-4">
          <div class="d-flex flex-wrap justify-content-between align-items-start gap-3">
            <div>
              <div class="d-flex align-items-center gap-2 mb-2">
                <a routerLink="/proyectos" class="btn btn-outline-secondary btn-sm py-0 px-2">
                  <i class="bi bi-arrow-left"></i>
                </a>
                <span class="badge" [ngClass]="{
                  'bg-info text-dark': proyecto.estado === 'PLANEACION',
                  'bg-primary': proyecto.estado === 'EN_DESARROLLO',
                  'bg-success': proyecto.estado === 'FINALIZADO',
                  'bg-danger': proyecto.estado === 'CANCELADO'
                }">{{ proyecto.estado }}</span>
                <span class="text-muted small">Inicio: {{ proyecto.fechaInicio || 'N/A' }}</span>
              </div>
              <h2 class="fw-bold mb-2">{{ proyecto.nombre }}</h2>
              <p class="text-muted mb-0">{{ proyecto.descripcion || 'Sin descripción detallada' }}</p>
            </div>

            <div class="d-flex flex-column align-items-end gap-2">
              <div class="text-end">
                <span class="small text-muted d-block">Progreso del Proyecto</span>
                <span class="fs-4 fw-bold text-primary">{{ proyecto.porcentajeProgreso }}%</span>
              </div>
              <button *ngIf="authService.hasRole('COORDINADOR') && proyecto.estado !== 'FINALIZADO'"
                      class="btn btn-outline-danger btn-sm" (click)="cerrarProyecto()">
                <i class="bi bi-lock-fill me-1"></i> Cerrar Proyecto
              </button>
            </div>
          </div>

          <!-- Pestañas de Navegación -->
          <ul class="nav nav-tabs mt-4">
            <li class="nav-item">
              <button class="nav-link" [class.active]="tabActiva === 'tareas'" (click)="tabActiva = 'tareas'">
                <i class="bi bi-kanban me-1"></i> Tablero de Tareas ({{ tareas.length }})
              </button>
            </li>
            <li class="nav-item">
              <button class="nav-link" [class.active]="tabActiva === 'documentos'" (click)="tabActiva = 'documentos'">
                <i class="bi bi-file-earmark-diff me-1"></i> Documentación Versionada ({{ documentos.length }})
              </button>
            </li>
            <li class="nav-item">
              <button class="nav-link" [class.active]="tabActiva === 'evidencias'" (click)="tabActiva = 'evidencias'">
                <i class="bi bi-images me-1"></i> Galería de Evidencias ({{ evidencias.length }})
              </button>
            </li>
            <li class="nav-item">
              <button class="nav-link" [class.active]="tabActiva === 'bitacora'" (click)="tabActiva = 'bitacora'">
                <i class="bi bi-journal-text me-1"></i> Bitácora ({{ bitacoras.length }})
              </button>
            </li>
            <li class="nav-item">
              <button class="nav-link" [class.active]="tabActiva === 'equipo'" (click)="tabActiva = 'equipo'">
                <i class="bi bi-people me-1"></i> Equipo de Trabajo ({{ miembros.length }})
              </button>
            </li>
          </ul>
        </div>
      </div>

      <!-- CONTENIDO DE LAS PESTAÑAS -->

      <!-- TAB 1: TABLERO KANBAN DE TAREAS -->
      <div *ngIf="tabActiva === 'tareas'">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="fw-bold mb-0">Tablero Kanban</h5>
          <button class="btn btn-primary btn-sm" (click)="abrirModalTarea()">
            <i class="bi bi-plus-lg me-1"></i> Nueva Tarea
          </button>
        </div>

        <div class="row g-3">
          <!-- Columna: PENDIENTE -->
          <div class="col-md-4">
            <div class="kanban-col">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="fw-bold text-secondary">PENDIENTE</span>
                <span class="badge bg-secondary">{{ getTareasPorEstado('PENDIENTE').length }}</span>
              </div>

              <div *ngFor="let t of getTareasPorEstado('PENDIENTE')" class="kanban-card">
                <div class="d-flex justify-content-between align-items-start mb-2">
                  <span class="badge" [ngClass]="getBadgePrioridad(t.prioridad)">{{ t.prioridad }}</span>
                  <button class="btn btn-sm btn-link text-danger p-0" (click)="eliminarTarea(t.id)"><i class="bi bi-trash"></i></button>
                </div>
                <h6 class="fw-bold text-dark mb-1">{{ t.titulo }}</h6>
                <p class="small text-muted mb-2">{{ t.descripcion }}</p>
                <div class="small text-secondary mb-2" *ngIf="t.etiquetas">
                  <i class="bi bi-tags me-1"></i>{{ t.etiquetas }}
                </div>
                <div class="d-flex justify-content-between align-items-center border-top pt-2 small">
                  <span class="text-muted"><i class="bi bi-person me-1"></i>{{ t.responsableNombre || 'Sin asignar' }}</span>
                  <button class="btn btn-outline-primary btn-sm py-0 px-2" (click)="cambiarEstadoTarea(t.id, 'EN_DESARROLLO')">
                    Iniciar &rarr;
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Columna: EN DESARROLLO -->
          <div class="col-md-4">
            <div class="kanban-col">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="fw-bold text-primary">EN DESARROLLO</span>
                <span class="badge bg-primary">{{ getTareasPorEstado('EN_DESARROLLO').length }}</span>
              </div>

              <div *ngFor="let t of getTareasPorEstado('EN_DESARROLLO')" class="kanban-card border-start border-4 border-primary">
                <div class="d-flex justify-content-between align-items-start mb-2">
                  <span class="badge" [ngClass]="getBadgePrioridad(t.prioridad)">{{ t.prioridad }}</span>
                  <button class="btn btn-sm btn-link text-danger p-0" (click)="eliminarTarea(t.id)"><i class="bi bi-trash"></i></button>
                </div>
                <h6 class="fw-bold text-dark mb-1">{{ t.titulo }}</h6>
                <p class="small text-muted mb-2">{{ t.descripcion }}</p>
                <div class="small text-secondary mb-2" *ngIf="t.etiquetas">
                  <i class="bi bi-tags me-1"></i>{{ t.etiquetas }}
                </div>
                <div class="d-flex justify-content-between align-items-center border-top pt-2 small">
                  <button class="btn btn-outline-secondary btn-sm py-0 px-2" (click)="cambiarEstadoTarea(t.id, 'PENDIENTE')">
                    &larr; Pendiente
                  </button>
                  <button class="btn btn-outline-success btn-sm py-0 px-2" (click)="cambiarEstadoTarea(t.id, 'COMPLETADA')">
                    Completar &rarr;
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Columna: COMPLETADA -->
          <div class="col-md-4">
            <div class="kanban-col">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="fw-bold text-success">COMPLETADA</span>
                <span class="badge bg-success">{{ getTareasPorEstado('COMPLETADA').length }}</span>
              </div>

              <div *ngFor="let t of getTareasPorEstado('COMPLETADA')" class="kanban-card border-start border-4 border-success bg-light">
                <div class="d-flex justify-content-between align-items-start mb-2">
                  <span class="badge" [ngClass]="getBadgePrioridad(t.prioridad)">{{ t.prioridad }}</span>
                  <i class="bi bi-check-circle-fill text-success fs-5"></i>
                </div>
                <h6 class="fw-bold text-decoration-line-through text-muted mb-1">{{ t.titulo }}</h6>
                <p class="small text-muted mb-2">{{ t.descripcion }}</p>
                <div class="d-flex justify-content-between align-items-center border-top pt-2 small">
                  <span class="text-muted"><i class="bi bi-person me-1"></i>{{ t.responsableNombre }}</span>
                  <button class="btn btn-outline-secondary btn-sm py-0 px-2" (click)="cambiarEstadoTarea(t.id, 'EN_DESARROLLO')">
                    Reabrir
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- TAB 2: DOCUMENTACIÓN VERSIONADA -->
      <div *ngIf="tabActiva === 'documentos'">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <div class="d-flex gap-2">
            <select class="form-select form-select-sm w-auto" [(ngModel)]="filtroCategoriaDoc" (change)="cargarDocumentos()">
              <option value="">Todas las categorías</option>
              <option value="REQUERIMIENTOS">Requerimientos</option>
              <option value="DISENO">Diseño</option>
              <option value="METODOLOGIA">Metodología</option>
              <option value="INFORMES">Informes</option>
              <option value="PRESENTACIONES">Presentaciones</option>
            </select>
          </div>
          <button class="btn btn-primary btn-sm" (click)="mostrarModalSubirDoc = true">
            <i class="bi bi-upload me-1"></i> Subir Documento
          </button>
        </div>

        <div class="row g-3">
          <div class="col-md-6 col-lg-4" *ngFor="let doc of documentos">
            <div class="card h-100 shadow-sm border-0">
              <div class="card-body d-flex flex-column">
                <div class="d-flex justify-content-between align-items-start mb-2">
                  <span class="badge bg-secondary">{{ doc.categoria }}</span>
                  <span class="badge bg-primary">v{{ doc.totalVersiones }}</span>
                </div>
                <h5 class="fw-bold text-dark mb-1">{{ doc.titulo }}</h5>
                <p class="small text-muted flex-grow-1">{{ doc.descripcion || 'Sin descripción' }}</p>

                <div class="bg-light p-2 rounded mb-3 small" *ngIf="doc.ultimaVersion">
                  <div class="text-muted mb-1">Última versión (v{{ doc.ultimaVersion.numeroVersion }}):</div>
                  <div class="fw-semibold text-truncate">{{ doc.ultimaVersion.nombreArchivoOriginal || 'Archivo adjunto' }}</div>
                  <div class="text-secondary small">{{ doc.ultimaVersion.resumenCambios }}</div>
                  <div class="text-muted small mt-1">Por: {{ doc.ultimaVersion.autorNombre }} &bull; {{ doc.ultimaVersion.fechaSubida | date:'short' }}</div>
                </div>

                <div class="d-flex gap-2 mt-auto">
                  <a *ngIf="doc.ultimaVersion" [href]="doc.ultimaVersion.archivoUrl" target="_blank" class="btn btn-outline-primary btn-sm flex-grow-1">
                    <i class="bi bi-download me-1"></i> Descargar
                  </a>
                  <button class="btn btn-outline-secondary btn-sm" (click)="abrirHistorialVersiones(doc)">
                    <i class="bi bi-clock-history me-1"></i> Historial
                  </button>
                  <button class="btn btn-outline-success btn-sm" (click)="abrirModalNuevaVersion(doc)">
                    <i class="bi bi-plus-circle me-1"></i> Nueva v
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="col-12" *ngIf="documentos.length === 0">
            <div class="card text-center p-5 bg-white border-dashed">
              <i class="bi bi-file-earmark-x fs-1 text-muted mb-2"></i>
              <h5 class="text-secondary">No hay documentos registrados</h5>
              <p class="text-muted small">Sube el primer documento técnico o especificación de requerimientos.</p>
            </div>
          </div>
        </div>
      </div>

      <!-- TAB 3: GALERÍA DE EVIDENCIAS MULTIMEDIA -->
      <div *ngIf="tabActiva === 'evidencias'">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="fw-bold mb-0">Galería de Evidencias</h5>
          <button class="btn btn-primary btn-sm" (click)="mostrarModalEvidencia = true">
            <i class="bi bi-cloud-arrow-up me-1"></i> Subir Evidencia
          </button>
        </div>

        <div class="row g-3">
          <div class="col-sm-6 col-md-4 col-lg-3" *ngFor="let ev of evidencias">
            <div class="card h-100 shadow-sm border-0">
              <div class="position-relative bg-light text-center p-3 border-bottom" style="height: 160px; display: flex; align-items: center; justify-content: center;">
                <img *ngIf="esImagen(ev.tipoArchivo)" [src]="ev.archivoUrl" class="img-fluid rounded" style="max-height: 140px; object-fit: contain;">
                <i *ngIf="!esImagen(ev.tipoArchivo)" class="bi bi-file-earmark-play fs-1 text-primary"></i>
                <span class="badge bg-dark bg-opacity-75 position-absolute top-0 end-0 m-2">{{ ev.categoria }}</span>
              </div>
              <div class="card-body d-flex flex-column">
                <h6 class="fw-bold text-dark mb-1">{{ ev.titulo }}</h6>
                <p class="small text-muted flex-grow-1">{{ ev.descripcion }}</p>
                <div class="small text-muted mb-2">
                  <i class="bi bi-person me-1"></i>{{ ev.autorNombre }} &bull; {{ ev.fechaSubida | date:'shortDate' }}
                </div>
                <div class="d-flex justify-content-between align-items-center">
                  <a [href]="ev.archivoUrl" target="_blank" class="btn btn-sm btn-outline-primary">Ver Archivo</a>
                  <button class="btn btn-sm btn-link text-danger p-0" (click)="eliminarEvidencia(ev.id)"><i class="bi bi-trash"></i></button>
                </div>
              </div>
            </div>
          </div>

          <div class="col-12" *ngIf="evidencias.length === 0">
            <div class="card text-center p-5 bg-white border-dashed">
              <i class="bi bi-images fs-1 text-muted mb-2"></i>
              <h5 class="text-secondary">Sin evidencias multimedia</h5>
              <p class="text-muted small">Carga capturas de diagramas, prototipos de interfaz o videos del proyecto.</p>
            </div>
          </div>
        </div>
      </div>

      <!-- TAB 4: BITÁCORA CRONOLÓGICA -->
      <div *ngIf="tabActiva === 'bitacora'">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h5 class="fw-bold mb-1">Línea de Tiempo y Actas</h5>
            <p class="text-muted small mb-0">Trazabilidad secuencial de actividades, decisiones y actas de reunión</p>
          </div>
          <button class="btn btn-primary btn-sm" (click)="mostrarModalBitacora = true">
            <i class="bi bi-journal-plus me-1"></i> Registrar Entrada
          </button>
        </div>

        <div class="timeline" *ngIf="bitacoras.length > 0">
          <div class="timeline-item" *ngFor="let b of bitacoras">
            <div class="timeline-dot"></div>
            <div class="card shadow-sm border-0">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start mb-2">
                  <h6 class="fw-bold text-dark mb-0">{{ b.titulo }}</h6>
                  <span class="badge" [ngClass]="{
                    'bg-info text-dark': b.tipo === 'AVANCE',
                    'bg-warning text-dark': b.tipo === 'REUNION_ACTA',
                    'bg-danger': b.tipo === 'INCIDENCIA',
                    'bg-success': b.tipo === 'HITO'
                  }">{{ b.tipo }}</span>
                </div>
                <p class="text-secondary mb-2">{{ b.contenido }}</p>
                <div class="d-flex justify-content-between small text-muted border-top pt-2">
                  <span><i class="bi bi-person me-1"></i>{{ b.autorNombre }}</span>
                  <span><i class="bi bi-clock me-1"></i>{{ b.fechaHora | date:'medium' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="card text-center p-5 bg-white border-dashed" *ngIf="bitacoras.length === 0">
          <i class="bi bi-journal-x fs-1 text-muted mb-2"></i>
          <h5 class="text-secondary">Bitácora vacía</h5>
          <p class="text-muted small">Registra la primera reunión o avance para documentar la trazabilidad.</p>
        </div>
      </div>

      <!-- TAB 5: EQUIPO DE TRABAJO -->
      <div *ngIf="tabActiva === 'equipo'">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h5 class="fw-bold mb-0">Integrantes Vinculados</h5>
          <button *ngIf="authService.hasAnyRole('COORDINADOR', 'ASESOR')" class="btn btn-primary btn-sm" (click)="abrirModalVincular()">
            <i class="bi bi-person-plus-fill me-1"></i> Vincular Integrante
          </button>
        </div>

        <div class="card border-0 shadow-sm">
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light small">
                <tr>
                  <th>Nombre y Apellido</th>
                  <th>Correo</th>
                  <th>Código</th>
                  <th>Rol en el Proyecto</th>
                  <th>Fecha Vinculación</th>
                  <th>Estado</th>
                  <th *ngIf="authService.hasAnyRole('COORDINADOR', 'ASESOR')">Acción</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let m of miembros">
                  <td class="fw-semibold">{{ m.nombreUsuario }}</td>
                  <td>{{ m.correoUsuario }}</td>
                  <td><span class="badge bg-light text-secondary border">{{ m.codigoUsuario || 'N/A' }}</span></td>
                  <td>
                    <span class="badge" [ngClass]="{
                      'bg-primary': m.rolEnProyecto === 'ASESOR_LIDER',
                      'bg-info text-dark': m.rolEnProyecto === 'ASESOR_APOYO',
                      'bg-success': m.rolEnProyecto === 'ESTUDIANTE_DESARROLLADOR'
                    }">{{ m.rolEnProyecto }}</span>
                  </td>
                  <td>{{ m.fechaInicio }}</td>
                  <td>
                    <span class="badge" [class.bg-success]="m.activo" [class.bg-secondary]="!m.activo">
                      {{ m.activo ? 'Activo' : 'Finalizado' }}
                    </span>
                  </td>
                  <td *ngIf="authService.hasAnyRole('COORDINADOR', 'ASESOR')">
                    <button *ngIf="m.activo" class="btn btn-sm btn-outline-danger" (click)="desvincularMiembro(m.usuarioId)">
                      Desvincular
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- MODALES -->

      <!-- Modal Crear Tarea -->
      <div class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);" *ngIf="mostrarModalTarea">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title fw-bold">Nueva Tarea</h5>
              <button type="button" class="btn-close" (click)="mostrarModalTarea = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label small fw-semibold">Título de la Tarea *</label>
                <input type="text" class="form-control" [(ngModel)]="tareaTitulo">
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Descripción</label>
                <textarea class="form-control" rows="3" [(ngModel)]="tareaDescripcion"></textarea>
              </div>
              <div class="row g-2 mb-3">
                <div class="col-6">
                  <label class="form-label small fw-semibold">Prioridad</label>
                  <select class="form-select" [(ngModel)]="tareaPrioridad">
                    <option value="BAJA">Baja</option>
                    <option value="MEDIA">Media</option>
                    <option value="ALTA">Alta</option>
                    <option value="URGENTE">Urgente</option>
                  </select>
                </div>
                <div class="col-6">
                  <label class="form-label small fw-semibold">Fecha Límite</label>
                  <input type="date" class="form-control" [(ngModel)]="tareaFechaLimite">
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Responsable</label>
                <select class="form-select" [(ngModel)]="tareaResponsableId">
                  <option [ngValue]="null">Sin asignar</option>
                  <option *ngFor="let m of miembros" [value]="m.usuarioId">{{ m.nombreUsuario }}</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Etiquetas (separadas por coma)</label>
                <input type="text" class="form-control" placeholder="Backend, Base de datos..." [(ngModel)]="tareaEtiquetas">
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary btn-sm" (click)="mostrarModalTarea = false">Cancelar</button>
              <button class="btn btn-primary btn-sm" (click)="guardarTarea()" [disabled]="!tareaTitulo">Crear Tarea</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal Subir Documento -->
      <div class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);" *ngIf="mostrarModalSubirDoc">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title fw-bold">Subir Nuevo Documento (v1)</h5>
              <button type="button" class="btn-close" (click)="mostrarModalSubirDoc = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label small fw-semibold">Título del Documento *</label>
                <input type="text" class="form-control" [(ngModel)]="docTitulo">
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Categoría *</label>
                <select class="form-select" [(ngModel)]="docCategoria">
                  <option value="REQUERIMIENTOS">Requerimientos</option>
                  <option value="DISENO">Diseño</option>
                  <option value="METODOLOGIA">Metodología</option>
                  <option value="INFORMES">Informes</option>
                  <option value="PRESENTACIONES">Presentaciones</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Descripción</label>
                <textarea class="form-control" rows="2" [(ngModel)]="docDescripcion"></textarea>
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Archivo *</label>
                <input type="file" class="form-control" (change)="onFileChangeDoc($event)">
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Resumen de Cambios (v1)</label>
                <input type="text" class="form-control" [(ngModel)]="docResumen" placeholder="Versión inicial...">
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary btn-sm" (click)="mostrarModalSubirDoc = false">Cancelar</button>
              <button class="btn btn-primary btn-sm" (click)="guardarDocumento()" [disabled]="!docTitulo || !archivoDocSeleccionado">Subir Documento</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal Nueva Versión Documento -->
      <div class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);" *ngIf="mostrarModalNuevaVersion && docSeleccionado">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title fw-bold">Nueva Versión: {{ docSeleccionado.titulo }}</h5>
              <button type="button" class="btn-close" (click)="mostrarModalNuevaVersion = false"></button>
            </div>
            <div class="modal-body">
              <p class="small text-muted mb-3">
                La nueva versión (v{{ docSeleccionado.totalVersiones + 1 }}) conservará las versiones anteriores sin sobrescribirlas.
              </p>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Nuevo Archivo *</label>
                <input type="file" class="form-control" (change)="onFileChangeVersion($event)">
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Resumen de los Cambios Realizados *</label>
                <textarea class="form-control" rows="3" [(ngModel)]="versionResumen" placeholder="Ej. Se ajustaron los casos de uso 3 y 4..."></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary btn-sm" (click)="mostrarModalNuevaVersion = false">Cancelar</button>
              <button class="btn btn-success btn-sm" (click)="guardarNuevaVersion()" [disabled]="!archivoVersionSeleccionado || !versionResumen">Guardar Versión</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal Historial de Versiones -->
      <div class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);" *ngIf="mostrarModalHistorial && docSeleccionado">
        <div class="modal-dialog modal-lg modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title fw-bold">Historial de Versiones: {{ docSeleccionado.titulo }}</h5>
              <button type="button" class="btn-close" (click)="mostrarModalHistorial = false"></button>
            </div>
            <div class="modal-body">
              <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                  <thead class="table-light small">
                    <tr>
                      <th>Versión</th>
                      <th>Archivo</th>
                      <th>Autor</th>
                      <th>Fecha</th>
                      <th>Resumen de Cambios</th>
                      <th>Descarga</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr *ngFor="let v of docSeleccionado.versiones">
                      <td><span class="badge bg-primary">v{{ v.numeroVersion }}</span></td>
                      <td class="small fw-semibold">{{ v.nombreArchivoOriginal }}</td>
                      <td class="small">{{ v.autorNombre }}</td>
                      <td class="small text-muted">{{ v.fechaSubida | date:'short' }}</td>
                      <td class="small text-secondary">{{ v.resumenCambios }}</td>
                      <td>
                        <a [href]="v.archivoUrl" target="_blank" class="btn btn-outline-primary btn-sm py-0">
                          <i class="bi bi-download"></i>
                        </a>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary btn-sm" (click)="mostrarModalHistorial = false">Cerrar</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal Subir Evidencia -->
      <div class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);" *ngIf="mostrarModalEvidencia">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title fw-bold">Subir Evidencia Multimedia</h5>
              <button type="button" class="btn-close" (click)="mostrarModalEvidencia = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label small fw-semibold">Título *</label>
                <input type="text" class="form-control" [(ngModel)]="evTitulo">
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Categoría *</label>
                <select class="form-select" [(ngModel)]="evCategoria">
                  <option value="PROTOTIPO">Prototipo</option>
                  <option value="DESARROLLO">Desarrollo</option>
                  <option value="PRUEBAS">Pruebas</option>
                  <option value="PRESENTACION">Presentación</option>
                  <option value="INVESTIGACION">Investigación</option>
                  <option value="EVIDENCIA">Evidencia</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Descripción</label>
                <textarea class="form-control" rows="2" [(ngModel)]="evDescripcion"></textarea>
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Archivo (Imagen, Video, Documento) *</label>
                <input type="file" class="form-control" (change)="onFileChangeEvidencia($event)">
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary btn-sm" (click)="mostrarModalEvidencia = false">Cancelar</button>
              <button class="btn btn-primary btn-sm" (click)="guardarEvidencia()" [disabled]="!evTitulo || !archivoEvidenciaSeleccionado">Subir Evidencia</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal Nueva Bitácora -->
      <div class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);" *ngIf="mostrarModalBitacora">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title fw-bold">Nueva Entrada en Bitácora</h5>
              <button type="button" class="btn-close" (click)="mostrarModalBitacora = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label small fw-semibold">Tipo de Registro *</label>
                <select class="form-select" [(ngModel)]="bitacoraTipo">
                  <option value="AVANCE">Avance Técnico</option>
                  <option value="REUNION_ACTA">Acta de Reunión</option>
                  <option value="HITO">Hito Alcanzado</option>
                  <option value="INCIDENCIA">Incidencia o Bloqueo</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Título *</label>
                <input type="text" class="form-control" [(ngModel)]="bitacoraTitulo">
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Contenido y Acuerdos *</label>
                <textarea class="form-control" rows="4" [(ngModel)]="bitacoraContenido" placeholder="Detalles de lo conversado, tareas asignadas, decisiones técnicas..."></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary btn-sm" (click)="mostrarModalBitacora = false">Cancelar</button>
              <button class="btn btn-primary btn-sm" (click)="guardarBitacora()" [disabled]="!bitacoraTitulo || !bitacoraContenido">Registrar</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Modal Vincular Miembro -->
      <div class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);" *ngIf="mostrarModalVincular">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title fw-bold">Vincular Integrante al Proyecto</h5>
              <button type="button" class="btn-close" (click)="mostrarModalVincular = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label small fw-semibold">Seleccionar Usuario *</label>
                <select class="form-select" [(ngModel)]="usuarioAVincularId">
                  <option [ngValue]="null">Seleccionar usuario...</option>
                  <option *ngFor="let u of todosUsuarios" [value]="u.id">
                    {{ u.nombres }} {{ u.apellidos }} ({{ u.correo }} - {{ u.rol }})
                  </option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label small fw-semibold">Rol dentro del Proyecto *</label>
                <select class="form-select" [(ngModel)]="rolEnProyecto">
                  <option value="ESTUDIANTE_DESARROLLADOR">Estudiante Desarrollador</option>
                  <option value="ASESOR_LIDER" *ngIf="authService.hasRole('COORDINADOR')">Asesor Líder</option>
                  <option value="ASESOR_APOYO" *ngIf="authService.hasRole('COORDINADOR')">Asesor de Apoyo</option>
                </select>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary btn-sm" (click)="mostrarModalVincular = false">Cancelar</button>
              <button class="btn btn-primary btn-sm" (click)="guardarVinculacion()" [disabled]="!usuarioAVincularId">Vincular</button>
            </div>
          </div>
        </div>
      </div>

    </div>
  `
})
export class ProyectoDetalleComponent implements OnInit {
  proyectoId!: string;
  proyecto: ProyectoResponse | null = null;
  tabActiva: 'tareas' | 'documentos' | 'evidencias' | 'bitacora' | 'equipo' = 'tareas';

  tareas: TareaResponse[] = [];
  documentos: DocumentoResponse[] = [];
  evidencias: EvidenciaResponse[] = [];
  bitacoras: BitacoraResponse[] = [];
  miembros: MiembroProyectoResponse[] = [];
  todosUsuarios: UsuarioResponse[] = [];

  // Tarea form
  mostrarModalTarea = false;
  tareaTitulo = '';
  tareaDescripcion = '';
  tareaPrioridad: Prioridad = 'MEDIA';
  tareaFechaLimite = '';
  tareaResponsableId: string | null = null;
  tareaEtiquetas = '';

  // Documento form
  mostrarModalSubirDoc = false;
  filtroCategoriaDoc = '';
  docTitulo = '';
  docCategoria: CategoriaDocumento = 'REQUERIMIENTOS';
  docDescripcion = '';
  docResumen = '';
  archivoDocSeleccionado: File | null = null;

  // Nueva Versión form
  mostrarModalNuevaVersion = false;
  docSeleccionado: DocumentoResponse | null = null;
  versionResumen = '';
  archivoVersionSeleccionado: File | null = null;

  // Historial modal
  mostrarModalHistorial = false;

  // Evidencia form
  mostrarModalEvidencia = false;
  evTitulo = '';
  evCategoria: CategoriaEvidencia = 'PROTOTIPO';
  evDescripcion = '';
  archivoEvidenciaSeleccionado: File | null = null;

  // Bitácora form
  mostrarModalBitacora = false;
  bitacoraTipo: TipoEntradaBitacora = 'AVANCE';
  bitacoraTitulo = '';
  bitacoraContenido = '';

  // Vincular Miembro form
  mostrarModalVincular = false;
  usuarioAVincularId: string | null = null;
  rolEnProyecto: RolEnProyecto = 'ESTUDIANTE_DESARROLLADOR';

  constructor(
    private route: ActivatedRoute,
    public authService: AuthService,
    private proyectoService: ProyectoService,
    private tareaService: TareaService,
    private documentoService: DocumentoService,
    private evidenciaService: EvidenciaService,
    private bitacoraService: BitacoraService,
    private usuarioService: UsuarioService
  ) {}

  ngOnInit(): void {
    this.proyectoId = this.route.snapshot.paramMap.get('id')!;
    this.cargarDatosProyecto();
  }

  cargarDatosProyecto(): void {
    this.proyectoService.obtenerPorId(this.proyectoId).subscribe({
      next: (res) => this.proyecto = res,
      error: (err) => console.error(err)
    });

    this.cargarTareas();
    this.cargarDocumentos();
    this.cargarEvidencias();
    this.cargarBitacora();
    this.cargarMiembros();
  }

  cargarTareas(): void {
    this.tareaService.listarPorProyecto(this.proyectoId).subscribe({
      next: (res) => this.tareas = res,
      error: (err) => console.error(err)
    });
  }

  cargarDocumentos(): void {
    const cat = this.filtroCategoriaDoc ? (this.filtroCategoriaDoc as CategoriaDocumento) : undefined;
    this.documentoService.listarPorProyecto(this.proyectoId, cat).subscribe({
      next: (res) => this.documentos = res,
      error: (err) => console.error(err)
    });
  }

  cargarEvidencias(): void {
    this.evidenciaService.listarPorProyecto(this.proyectoId).subscribe({
      next: (res) => this.evidencias = res,
      error: (err) => console.error(err)
    });
  }

  cargarBitacora(): void {
    this.bitacoraService.listarPorProyecto(this.proyectoId).subscribe({
      next: (res) => this.bitacoras = res,
      error: (err) => console.error(err)
    });
  }

  cargarMiembros(): void {
    this.proyectoService.listarMiembros(this.proyectoId).subscribe({
      next: (res) => this.miembros = res,
      error: (err) => console.error(err)
    });
  }

  getTareasPorEstado(estado: EstadoTarea): TareaResponse[] {
    return this.tareas.filter(t => t.estado === estado);
  }

  getBadgePrioridad(p: Prioridad): string {
    switch (p) {
      case 'URGENTE': return 'bg-danger';
      case 'ALTA': return 'bg-warning text-dark';
      case 'MEDIA': return 'bg-primary';
      case 'BAJA': return 'bg-secondary';
    }
  }

  cambiarEstadoTarea(id: string, nuevoEstado: EstadoTarea): void {
    this.tareaService.cambiarEstado(id, nuevoEstado).subscribe({
      next: () => {
        this.cargarTareas();
        this.proyectoService.obtenerPorId(this.proyectoId).subscribe(p => this.proyecto = p);
      },
      error: (err) => alert(err.error?.message || err.message)
    });
  }

  eliminarTarea(id: string): void {
    if (confirm('¿Eliminar esta tarea?')) {
      this.tareaService.eliminar(id).subscribe({
        next: () => {
          this.cargarTareas();
          this.proyectoService.obtenerPorId(this.proyectoId).subscribe(p => this.proyecto = p);
        },
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  abrirModalTarea(): void {
    this.tareaTitulo = '';
    this.tareaDescripcion = '';
    this.tareaPrioridad = 'MEDIA';
    this.tareaFechaLimite = '';
    this.tareaResponsableId = null;
    this.tareaEtiquetas = '';
    this.mostrarModalTarea = true;
  }

  guardarTarea(): void {
    if (!this.tareaTitulo) return;
    this.tareaService.crear(this.proyectoId, {
      titulo: this.tareaTitulo,
      descripcion: this.tareaDescripcion,
      prioridad: this.tareaPrioridad,
      fechaLimite: this.tareaFechaLimite || undefined,
      responsableId: this.tareaResponsableId || undefined,
      etiquetas: this.tareaEtiquetas || undefined
    }).subscribe({
      next: () => {
        this.mostrarModalTarea = false;
        this.cargarTareas();
        this.proyectoService.obtenerPorId(this.proyectoId).subscribe(p => this.proyecto = p);
      },
      error: (err) => alert(err.error?.message || err.message)
    });
  }

  onFileChangeDoc(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.archivoDocSeleccionado = event.target.files[0];
    }
  }

  guardarDocumento(): void {
    if (!this.docTitulo || !this.archivoDocSeleccionado) return;

    const fd = new FormData();
    fd.append('titulo', this.docTitulo);
    fd.append('categoria', this.docCategoria);
    if (this.docDescripcion) fd.append('descripcion', this.docDescripcion);
    if (this.docResumen) fd.append('resumenCambios', this.docResumen);
    fd.append('archivo', this.archivoDocSeleccionado);

    this.documentoService.crear(this.proyectoId, fd).subscribe({
      next: () => {
        this.mostrarModalSubirDoc = false;
        this.archivoDocSeleccionado = null;
        this.docTitulo = '';
        this.docDescripcion = '';
        this.cargarDocumentos();
      },
      error: (err) => alert(err.error?.message || err.message)
    });
  }

  abrirModalNuevaVersion(doc: DocumentoResponse): void {
    this.docSeleccionado = doc;
    this.versionResumen = '';
    this.archivoVersionSeleccionado = null;
    this.mostrarModalNuevaVersion = true;
  }

  onFileChangeVersion(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.archivoVersionSeleccionado = event.target.files[0];
    }
  }

  guardarNuevaVersion(): void {
    if (!this.docSeleccionado || !this.archivoVersionSeleccionado) return;

    const fd = new FormData();
    fd.append('resumenCambios', this.versionResumen);
    fd.append('archivo', this.archivoVersionSeleccionado);

    this.documentoService.agregarVersion(this.docSeleccionado.id, fd).subscribe({
      next: () => {
        this.mostrarModalNuevaVersion = false;
        this.cargarDocumentos();
      },
      error: (err) => alert(err.error?.message || err.message)
    });
  }

  abrirHistorialVersiones(doc: DocumentoResponse): void {
    this.docSeleccionado = doc;
    this.mostrarModalHistorial = true;
  }

  onFileChangeEvidencia(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.archivoEvidenciaSeleccionado = event.target.files[0];
    }
  }

  guardarEvidencia(): void {
    if (!this.evTitulo || !this.archivoEvidenciaSeleccionado) return;

    const fd = new FormData();
    fd.append('titulo', this.evTitulo);
    fd.append('categoria', this.evCategoria);
    if (this.evDescripcion) fd.append('descripcion', this.evDescripcion);
    fd.append('archivo', this.archivoEvidenciaSeleccionado);

    this.evidenciaService.crear(this.proyectoId, fd).subscribe({
      next: () => {
        this.mostrarModalEvidencia = false;
        this.archivoEvidenciaSeleccionado = null;
        this.evTitulo = '';
        this.evDescripcion = '';
        this.cargarEvidencias();
      },
      error: (err) => alert(err.error?.message || err.message)
    });
  }

  eliminarEvidencia(id: string): void {
    if (confirm('¿Eliminar esta evidencia multimedia?')) {
      this.evidenciaService.eliminar(id).subscribe({
        next: () => this.cargarEvidencias(),
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  esImagen(tipo?: string): boolean {
    return !!tipo && (tipo.includes('image') || tipo.includes('png') || tipo.includes('jpeg') || tipo.includes('jpg'));
  }

  guardarBitacora(): void {
    if (!this.bitacoraTitulo || !this.bitacoraContenido) return;

    this.bitacoraService.crear(this.proyectoId, {
      titulo: this.bitacoraTitulo,
      contenido: this.bitacoraContenido,
      tipo: this.bitacoraTipo
    }).subscribe({
      next: () => {
        this.mostrarModalBitacora = false;
        this.bitacoraTitulo = '';
        this.bitacoraContenido = '';
        this.cargarBitacora();
      },
      error: (err) => alert(err.error?.message || err.message)
    });
  }

  abrirModalVincular(): void {
    this.usuarioService.listar().subscribe({
      next: (res) => {
        this.todosUsuarios = res;
        this.usuarioAVincularId = null;
        this.mostrarModalVincular = true;
      },
      error: (err) => console.error(err)
    });
  }

  guardarVinculacion(): void {
    if (!this.usuarioAVincularId) return;

    this.proyectoService.vincularMiembro(this.proyectoId, {
      usuarioId: this.usuarioAVincularId,
      rolEnProyecto: this.rolEnProyecto
    }).subscribe({
      next: () => {
        this.mostrarModalVincular = false;
        this.cargarMiembros();
      },
      error: (err) => alert(err.error?.message || err.message)
    });
  }

  desvincularMiembro(usuarioId: string): void {
    if (confirm('¿Desvincular a este integrante del proyecto?')) {
      this.proyectoService.desvincularMiembro(this.proyectoId, usuarioId).subscribe({
        next: () => this.cargarMiembros(),
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }

  cerrarProyecto(): void {
    if (confirm('¿Está seguro de cerrar y marcar como finalizado este proyecto?')) {
      this.proyectoService.cerrar(this.proyectoId).subscribe({
        next: (p) => this.proyecto = p,
        error: (err) => alert(err.error?.message || err.message)
      });
    }
  }
}
