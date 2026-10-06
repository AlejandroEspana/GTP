import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardGlobalResponse, DashboardProyectoResponse } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  constructor(private http: HttpClient) {}

  obtenerGlobal(): Observable<DashboardGlobalResponse> {
    return this.http.get<DashboardGlobalResponse>('/api/dashboard/global');
  }

  obtenerProyecto(proyectoId: string): Observable<DashboardProyectoResponse> {
    return this.http.get<DashboardProyectoResponse>(`/api/dashboard/proyectos/${proyectoId}`);
  }
}
