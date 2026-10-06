import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/components/navbar/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent],
  template: `
    <div class="min-vh-100 d-flex flex-column">
      <app-navbar></app-navbar>
      <main class="flex-grow-1">
        <router-outlet></router-outlet>
      </main>
      <footer class="bg-white border-top py-3 text-center text-muted small mt-auto">
        Plataforma Web para la Gestión y Trazabilidad de Proyectos de Software &bull; Laboratorio Universitario
      </footer>
    </div>
  `
})
export class AppComponent {
  title = 'Laboratorio de Proyectos';
}
