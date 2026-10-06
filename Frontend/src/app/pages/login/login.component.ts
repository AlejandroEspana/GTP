import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          <div class="text-center mb-4">
            <div class="d-inline-flex p-3 rounded-circle bg-primary bg-opacity-10 text-primary mb-3">
              <i class="bi bi-diagram-3-fill fs-1"></i>
            </div>
            <h2 class="fw-bold">Gestión de Proyectos</h2>
            <p class="text-muted">Laboratorio de Desarrollo e Investigación</p>
          </div>

          <div class="card shadow-sm border-0">
            <div class="card-body p-4 p-sm-5">
              <h4 class="card-title fw-bold mb-4">Iniciar Sesión</h4>

              <div *ngIf="errorMessage" class="alert alert-danger py-2 px-3 small d-flex align-items-center gap-2 mb-3">
                <i class="bi bi-exclamation-triangle-fill"></i>
                <span>{{ errorMessage }}</span>
              </div>

              <form (ngSubmit)="onSubmit()">
                <div class="mb-3">
                  <label class="form-label small fw-semibold text-secondary">Correo Institucional</label>
                  <div class="input-group">
                    <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-envelope"></i></span>
                    <input type="email" class="form-control border-start-0 ps-0" [(ngModel)]="correo" name="correo" required placeholder="nombre@laboratorio.edu">
                  </div>
                </div>

                <div class="mb-4">
                  <label class="form-label small fw-semibold text-secondary">Contraseña</label>
                  <div class="input-group">
                    <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-lock"></i></span>
                    <input type="password" class="form-control border-start-0 ps-0" [(ngModel)]="password" name="password" required placeholder="••••••••">
                  </div>
                </div>

                <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold d-flex justify-content-center align-items-center gap-2" [disabled]="loading">
                  <span *ngIf="loading" class="spinner-border spinner-border-sm"></span>
                  <i *ngIf="!loading" class="bi bi-box-arrow-in-right"></i>
                  <span>{{ loading ? 'Autenticando...' : 'Acceder al Sistema' }}</span>
                </button>
              </form>

              <hr class="my-4">

              <div>
                <p class="small text-muted mb-2 text-center fw-semibold">Acceso rápido para demostración:</p>
                <div class="d-flex flex-wrap gap-2 justify-content-center">
                  <button type="button" class="btn btn-outline-primary btn-sm" (click)="quickLogin('coordinador@laboratorio.edu', 'Admin123!')">
                    <i class="bi bi-person-badge me-1"></i> Coordinador
                  </button>
                  <button type="button" class="btn btn-outline-info text-dark btn-sm" (click)="quickLogin('asesor@laboratorio.edu', 'Asesor123!')">
                    <i class="bi bi-mortarboard me-1"></i> Asesor
                  </button>
                  <button type="button" class="btn btn-outline-success btn-sm" (click)="quickLogin('estudiante@laboratorio.edu', 'Estudiante123!')">
                    <i class="bi bi-person-workspace me-1"></i> Estudiante
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  correo = '';
  password = '';
  loading = false;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  quickLogin(correo: string, pass: string): void {
    this.correo = correo;
    this.password = pass;
    this.onSubmit();
  }

  onSubmit(): void {
    if (!this.correo || !this.password) {
      this.errorMessage = 'Por favor complete todos los campos';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.correo, this.password).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Error en las credenciales de acceso';
      }
    });
  }
}
