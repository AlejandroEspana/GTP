import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ProyectoResponse,
  MiembroProyectoResponse,
  RolEnProyecto,
  EstadoProyecto
} from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class ProyectoService {
  private apiUrl = 'http://localhost:8080/api/proyectos';

  constructor(private http: HttpClient) {}

  listar(): Observable<ProyectoResponse[]> {
    return this.http.get<ProyectoResponse[]>(this.apiUrl);
  }

  obtenerPorId(id: string): Observable<ProyectoResponse> {
    return this.http.get<ProyectoResponse>(`${this.apiUrl}/${id}`);
  }

  crear(proyecto: {
    nombre: string;
    descripcion?: string;
    fechaInicio?: string;
    fechaFin?: string;
    asesorLiderId?: string;
  }): Observable<ProyectoResponse> {
    return this.http.post<ProyectoResponse>(this.apiUrl, proyecto);
  }

  actualizar(id: string, proyecto: {
    nombre: string;
    descripcion?: string;
    estado?: EstadoProyecto;
    fechaInicio?: string;
    fechaFin?: string;
  }): Observable<ProyectoResponse> {
    return this.http.put<ProyectoResponse>(`${this.apiUrl}/${id}`, proyecto);
  }

  cerrar(id: string): Observable<ProyectoResponse> {
    return this.http.post<ProyectoResponse>(`${this.apiUrl}/${id}/cerrar`, {});
  }

  listarMiembros(proyectoId: string): Observable<MiembroProyectoResponse[]> {
    return this.http.get<MiembroProyectoResponse[]>(`${this.apiUrl}/${proyectoId}/miembros`);
  }

  vincularMiembro(proyectoId: string, miembro: {
    usuarioId: string;
    rolEnProyecto: RolEnProyecto;
    fechaInicio?: string;
  }): Observable<MiembroProyectoResponse> {
    return this.http.post<MiembroProyectoResponse>(`${this.apiUrl}/${proyectoId}/miembros`, miembro);
  }

  desvincularMiembro(proyectoId: string, usuarioId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${proyectoId}/miembros/${usuarioId}`);
  }
}
