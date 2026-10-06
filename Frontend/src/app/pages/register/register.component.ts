import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-7">
          
          <!-- Encabezado -->
          <div class="text-center mb-4">
            <div class="d-inline-flex p-3 rounded-circle bg-primary bg-opacity-10 text-primary mb-3">
              <i class="bi bi-person-plus-fill fs-1"></i>
            </div>
            <h2 class="fw-bold">Crear nueva cuenta</h2>
            <p class="text-muted">nete a la Plataforma de Gestin de Proyectos</p>
          </div>

          <div class="card shadow-sm border-0">
            <div class="card-body p-4 p-sm-5">
              
              <!-- Alerta de Errores -->
              <div *ngIf="errorMessage" class="alert alert-danger py-2 px-3 small d-flex align-items-center gap-2 mb-4">
                <i class="bi bi-exclamation-triangle-fill"></i>
                <span>{{ errorMessage }}</span>
              </div>

              <form (ngSubmit)="onSubmit()">
                
                <!-- Seccin 1: Datos Personales -->
                <h5 class="fw-bold text-primary mb-3"><i class="bi bi-person-badge me-2"></i>Datos Personales</h5>
                <div class="row g-3 mb-4">
                  <div class="col-md-6">
                    <label class="form-label small fw-semibold text-secondary">Nombres *</label>
                    <input type="text" class="form-control" [(ngModel)]="userData.nombres" name="nombres" required placeholder="Ej: Ana Maria">
                  </div>
                  <div class="col-md-6">
                    <label class="form-label small fw-semibold text-secondary">Apellidos *</label>
                    <input type="text" class="form-control" [(ngModel)]="userData.apellidos" name="apellidos" required placeholder="Ej: Lopez Perez">
                  </div>
                  <div class="col-md-12">
                    <label class="form-label small fw-semibold text-secondary">Rol en el sistema *</label>
                    <select class="form-select" [(ngModel)]="userData.rol" name="rol" required>
                      <option value="ESTUDIANTE">Soy Estudiante</option>
                      <option value="ASESOR">Soy Asesor / Docente</option>
                    </select>
                  </div>
                </div>

                <hr class="text-muted opacity-25">

                <!-- Seccin 2: Cuenta y Acceso -->
                <h5 class="fw-bold text-primary mb-3 mt-4"><i class="bi bi-shield-lock me-2"></i>Credenciales de Acceso</h5>
                <div class="row g-3 mb-4">
                  <div class="col-12">
                    <label class="form-label small fw-semibold text-secondary">Correo Institucional *</label>
                    <input type="email" class="form-control" [(ngModel)]="userData.correo" name="correo" required placeholder="usuario@laboratorio.edu">
                  </div>
                  <div class="col-md-6">
                    <label class="form-label small fw-semibold text-secondary">Contrase&ntilde;a *</label>
                    <input type="password" class="form-control" [(ngModel)]="userData.password" name="password" required placeholder="Mnimo 6 caracteres">
                  </div>
                  <div class="col-md-6">
                    <label class="form-label small fw-semibold text-secondary">Confirmar Contrase&ntilde;a *</label>
                    <input type="password" class="form-control" [(ngModel)]="confirmPassword" name="confirmPassword" required placeholder="Repite la contrase&ntilde;a">
                  </div>
                </div>

                <hr class="text-muted opacity-25">

                <!-- Seccin 3: Opcionales -->
                <h5 class="fw-bold text-secondary mb-3 mt-4"><i class="bi bi-journal-text me-2"></i>Informaci&oacute;n Acad&eacute;mica (Opcional)</h5>
                <div class="row g-3 mb-4">
                  <div class="col-md-4">
                    <label class="form-label small fw-semibold text-secondary">C&oacute;digo Estudiantil</label>
                    <input type="text" class="form-control" [(ngModel)]="userData.codigo" name="codigo" placeholder="Ej: 123456">
                  </div>
                  <div class="col-md-4">
                    <label class="form-label small fw-semibold text-secondary">Programa Acad&eacute;mico</label>
                    <input type="text" class="form-control" [(ngModel)]="userData.programaAcademico" name="programaAcademico" placeholder="Ej: Ingeniera">
                  </div>
                  <div class="col-md-4">
                    <label class="form-label small fw-semibold text-secondary">Semestre actual</label>
                    <input type="number" class="form-control" [(ngModel)]="userData.semestre" name="semestre" placeholder="Ej: 5">
                  </div>
                </div>

                <!-- Boton Submit -->
                <button type="submit" class="btn btn-primary w-100 py-3 fw-bold fs-6 d-flex justify-content-center align-items-center gap-2 mt-2 shadow-sm" [disabled]="loading">
                  <span *ngIf="loading" class="spinner-border spinner-border-sm"></span>
                  <i *ngIf="!loading" class="bi bi-person-check-fill fs-5"></i>
                  <span>{{ loading ? 'Creando cuenta, por favor espera...' : 'Registrar mi cuenta ahora' }}</span>
                </button>
              </form>

              <!-- Enlace Login -->
              <div class="text-center mt-4 pt-3 border-top">
                <p class="small text-muted mb-0">&iquest;Ya tienes una cuenta registrada?</p>
                <a routerLink="/login" class="text-decoration-none fw-bold text-primary">Haz clic aqu para Iniciar Sesi&oacute;n</a>
              </div>
              
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class RegisterComponent {
  userData = {
    nombres: '',
    apellidos: '',
    correo: '',
    password: '',
    rol: 'ESTUDIANTE',
    codigo: '',
    programaAcademico: '',
    semestre: null as number | null,
    fechaIngresoLaboratorio: new Date().toISOString().split('T')[0]
  };
  confirmPassword = '';
  loading = false;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onSubmit(): void {
    // Validaciones basicas frontend
    if (!this.userData.nombres || !this.userData.apellidos || !this.userData.correo || !this.userData.password || !this.userData.rol) {
      this.errorMessage = 'Por favor completa todos los campos con el asterisco (*)';
      return;
    }

    if (this.userData.password !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden. Verifícalas y vuelve a intentar.';
      return;
    }

    if (this.userData.password.length < 5) {
      this.errorMessage = 'La contraseña debe ser más segura y tener al menos 6 caracteres.';
      return;
    }

    const emailRegex = /^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/;
    if (!emailRegex.test(this.userData.correo)) {
      this.errorMessage = 'El correo electrónico no tiene un formato válido.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    // Convertimos semestres vacios a null
    const payload = { ...this.userData };
    if (!payload.semestre) payload.semestre = null;

    this.authService.register(payload).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        // Mejor manejo de errores provistos por el backend
        if (err.error && err.error.validaciones) {
          // Si el backend devolvio errores de validacion (ej. @NotBlank, @Email)
          const camposConError = Object.values(err.error.validaciones);
          this.errorMessage = camposConError[0] as string;
        } else if (err.error && err.error.message) {
          // Si el backend devolvio un error general (ej. Correo duplicado)
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Ha ocurrido un error de conexión con el servidor. Intenta de nuevo.';
        }
      }
    });
  }
}
