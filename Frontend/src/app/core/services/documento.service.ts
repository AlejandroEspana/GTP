import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DocumentoResponse, VersionDocumentoResponse, CategoriaDocumento } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class DocumentoService {
  constructor(private http: HttpClient) {}

  listarPorProyecto(proyectoId: string, categoria?: CategoriaDocumento): Observable<DocumentoResponse[]> {
    let url = `/api/proyectos/${proyectoId}/documentos`;
    if (categoria) {
      url += `?categoria=${categoria}`;
    }
    return this.http.get<DocumentoResponse[]>(url);
  }

  obtenerPorId(id: string): Observable<DocumentoResponse> {
    return this.http.get<DocumentoResponse>(`/api/documentos/${id}`);
  }

  crear(proyectoId: string, formData: FormData): Observable<DocumentoResponse> {
    return this.http.post<DocumentoResponse>(`/api/proyectos/${proyectoId}/documentos`, formData);
  }

  agregarVersion(documentoId: string, formData: FormData): Observable<VersionDocumentoResponse> {
    return this.http.post<VersionDocumentoResponse>(`/api/documentos/${documentoId}/versiones`, formData);
  }
}
