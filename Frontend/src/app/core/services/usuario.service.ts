import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UsuarioResponse, FichaIntegranteResponse, RolUsuario } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private apiUrl = 'http://localhost:8080/api/usuarios';

  constructor(private http: HttpClient) {}

  listar(rol?: RolUsuario): Observable<UsuarioResponse[]> {
    let url = this.apiUrl;
    if (rol) {
      url += `?rol=${rol}`;
    }
    return this.http.get<UsuarioResponse[]>(url);
  }

  obtenerPorId(id: string): Observable<UsuarioResponse> {
    return this.http.get<UsuarioResponse>(`${this.apiUrl}/${id}`);
  }

  obtenerFicha(id: string): Observable<FichaIntegranteResponse> {
    return this.http.get<FichaIntegranteResponse>(`${this.apiUrl}/${id}/ficha`);
  }

  crear(usuario: any): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(this.apiUrl, usuario);
  }

  actualizar(id: string, datos: any): Observable<UsuarioResponse> {
    return this.http.put<UsuarioResponse>(`${this.apiUrl}/${id}`, datos);
  }

  activar(id: string): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/activar`, {});
  }

  desactivar(id: string): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/desactivar`, {});
  }
}
