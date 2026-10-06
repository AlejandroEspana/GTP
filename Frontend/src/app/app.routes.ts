import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { ProyectosComponent } from './pages/proyectos/proyectos.component';
import { ProyectoDetalleComponent } from './pages/proyectos/proyecto-detalle.component';
import { IntegrantesComponent } from './pages/integrantes/integrantes.component';
import { AuditoriaComponent } from './pages/auditoria/auditoria.component';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'proyectos', component: ProyectosComponent, canActivate: [authGuard] },
  { path: 'proyectos/:id', component: ProyectoDetalleComponent, canActivate: [authGuard] },
  {
    path: 'integrantes',
    component: IntegrantesComponent,
    canActivate: [authGuard, roleGuard],
    data: { expectedRoles: ['COORDINADOR', 'ASESOR'] }
  },
  {
    path: 'auditoria',
    component: AuditoriaComponent,
    canActivate: [authGuard, roleGuard],
    data: { expectedRoles: ['COORDINADOR', 'ASESOR'] }
  },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: '/dashboard' }
];
