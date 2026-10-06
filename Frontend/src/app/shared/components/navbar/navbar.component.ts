import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm" *ngIf="authService.isLoggedIn()">
      <div class="container-fluid px-4">
        <a class="navbar-brand fw-bold d-flex align-items-center gap-2" routerLink="/dashboard">
          <i class="bi bi-diagram-3-fill text-primary fs-4"></i>
          <span>LabProyectos</span>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navMenu">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0 gap-1">
            <li class="nav-item">
              <a class="nav-link" routerLink="/dashboard" routerLinkActive="active">
                <i class="bi bi-grid-1x2-fill me-1"></i> Dashboard
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" routerLink="/proyectos" routerLinkActive="active">
                <i class="bi bi-kanban me-1"></i> Proyectos
              </a>
            </li>
            <li class="nav-item" *ngIf="authService.hasAnyRole('COORDINADOR', 'ASESOR')">
              <a class="nav-link" routerLink="/integrantes" routerLinkActive="active">
                <i class="bi bi-people-fill me-1"></i> Integrantes
              </a>
            </li>
            <li class="nav-item" *ngIf="authService.hasAnyRole('COORDINADOR', 'ASESOR')">
              <a class="nav-link" routerLink="/auditoria" routerLinkActive="active">
                <i class="bi bi-shield-check me-1"></i> Auditoría
              </a>
            </li>
          </ul>

          <div class="d-flex align-items-center gap-3">
            <div class="text-end d-none d-sm-block">
              <div class="text-light fw-semibold small">{{ authService.currentUser()?.nombreCompleto }}</div>
              <span class="badge" [ngClass]="{
                'bg-primary': authService.currentUser()?.rol === 'COORDINADOR',
                'bg-info text-dark': authService.currentUser()?.rol === 'ASESOR',
                'bg-success': authService.currentUser()?.rol === 'ESTUDIANTE'
              }">
                {{ authService.currentUser()?.rol }}
              </span>
            </div>

            <button class="btn btn-outline-light btn-sm d-flex align-items-center gap-1" (click)="logout()">
              <i class="bi bi-box-arrow-right"></i>
              <span class="d-none d-md-inline">Salir</span>
            </button>
          </div>
        </div>
      </div>
    </nav>
  `
})
export class NavbarComponent {
  constructor(public authService: AuthService) {}

  logout(): void {
    this.authService.logout();
  }
}
