import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EvidenciaResponse, CategoriaEvidencia } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class EvidenciaService {
  constructor(private http: HttpClient) {}

  listarPorProyecto(proyectoId: string, categoria?: CategoriaEvidencia): Observable<EvidenciaResponse[]> {
    let url = `/api/proyectos/${proyectoId}/evidencias`;
    if (categoria) {
      url += `?categoria=${categoria}`;
    }
    return this.http.get<EvidenciaResponse[]>(url);
  }

  crear(proyectoId: string, formData: FormData): Observable<EvidenciaResponse> {
    return this.http.post<EvidenciaResponse>(`/api/proyectos/${proyectoId}/evidencias`, formData);
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`/api/evidencias/${id}`);
  }
}
