import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditoriaResponse } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class AuditoriaService {
  constructor(private http: HttpClient) {}

  listar(): Observable<AuditoriaResponse[]> {
    return this.http.get<AuditoriaResponse[]>('/api/auditoria');
  }

  listarPorUsuario(usuarioId: string): Observable<AuditoriaResponse[]> {
    return this.http.get<AuditoriaResponse[]>(`/api/auditoria/usuarios/${usuarioId}`);
  }

  listarPorEntidad(entidad: string): Observable<AuditoriaResponse[]> {
    return this.http.get<AuditoriaResponse[]>(`/api/auditoria/entidades/${entidad}`);
  }
}
