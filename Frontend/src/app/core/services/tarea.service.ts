import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TareaResponse, EstadoTarea, Prioridad } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class TareaService {
  constructor(private http: HttpClient) {}

  listarPorProyecto(proyectoId: string): Observable<TareaResponse[]> {
    return this.http.get<TareaResponse[]>(`/api/proyectos/${proyectoId}/tareas`);
  }

  crear(proyectoId: string, tarea: {
    titulo: string;
    descripcion?: string;
    responsableId?: string;
    prioridad: Prioridad;
    fechaLimite?: string;
    etiquetas?: string;
  }): Observable<TareaResponse> {
    return this.http.post<TareaResponse>(`/api/proyectos/${proyectoId}/tareas`, tarea);
  }

  actualizar(id: string, tarea: {
    titulo: string;
    descripcion?: string;
    responsableId?: string;
    prioridad: Prioridad;
    fechaLimite?: string;
    etiquetas?: string;
  }): Observable<TareaResponse> {
    return this.http.put<TareaResponse>(`/api/tareas/${id}`, tarea);
  }

  cambiarEstado(id: string, estado: EstadoTarea): Observable<TareaResponse> {
    return this.http.patch<TareaResponse>(`/api/tareas/${id}/estado`, { estado });
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`/api/tareas/${id}`);
  }
}
