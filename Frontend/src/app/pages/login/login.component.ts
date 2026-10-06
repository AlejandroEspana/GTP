import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          
          <div class="text-center mb-4">
            <div class="d-inline-flex p-3 rounded-circle bg-primary bg-opacity-10 text-primary mb-3">
              <i class="bi bi-diagram-3-fill fs-1"></i>
            </div>
            <h2 class="fw-bold">Gesti&oacute;n de Proyectos</h2>
            <p class="text-muted">Laboratorio de Desarrollo e Investigaci&oacute;n</p>
          </div>

          <div class="card shadow-sm border-0">
            <div class="card-body p-4 p-sm-5">
              <h4 class="card-title fw-bold mb-4 text-center">Iniciar Sesi&oacute;n</h4>

              <div *ngIf="errorMessage" class="alert alert-danger py-2 px-3 small d-flex align-items-center gap-2 mb-3">
                <i class="bi bi-exclamation-triangle-fill"></i>
                <span>{{ errorMessage }}</span>
              </div>

              <form (ngSubmit)="onSubmit()">
                <div class="mb-3">
                  <label class="form-label small fw-semibold text-secondary">Correo Institucional</label>
                  <div class="input-group">
                    <span class="input-group-text bg-light border-end-0 text-muted"><i class="bi bi-envelope"></i></span>
                    <input type="email" class="form-control border-start-0 ps-0 bg-light" [(ngModel)]="correo" name="correo" required placeholder="nombre@laboratorio.edu">
                  </div>
                </div>

                <div class="mb-4">
                  <label class="form-label small fw-semibold text-secondary">Contrase&ntilde;a</label>
                  <div class="input-group">
                    <span class="input-group-text bg-light border-end-0 text-muted"><i class="bi bi-lock"></i></span>
                    <input type="password" class="form-control border-start-0 ps-0 bg-light" [(ngModel)]="password" name="password" required placeholder="Ingresa tu contrase&ntilde;a">
                  </div>
                </div>

                <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold d-flex justify-content-center align-items-center gap-2" [disabled]="loading">
                  <span *ngIf="loading" class="spinner-border spinner-border-sm"></span>
                  <i *ngIf="!loading" class="bi bi-box-arrow-in-right"></i>
                  <span>{{ loading ? 'Autenticando...' : 'Acceder al Sistema' }}</span>
                </button>
              </form>

              <hr class="my-4 text-muted opacity-25">

              <div class="text-center mb-4 mt-4">
                <p class="small text-muted mb-1">&iquest;No tienes una cuenta?</p>
                <a routerLink="/registro" class="btn btn-outline-primary btn-sm fw-semibold w-100 mt-1">Registrarse ahora</a>
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
      this.errorMessage = 'Por favor ingresa tu correo y contrase\u00f1a.';
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
        if (err.status === 401 || err.status === 403) {
           this.errorMessage = 'Correo o contrase\u00f1a incorrectos.';
        } else {
           this.errorMessage = err.error?.message || 'Error al intentar acceder.';
        }
      }
    });
  }
}
