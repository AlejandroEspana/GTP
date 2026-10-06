import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BitacoraResponse, TipoEntradaBitacora } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class BitacoraService {
  constructor(private http: HttpClient) {}

  listarPorProyecto(proyectoId: string): Observable<BitacoraResponse[]> {
    return this.http.get<BitacoraResponse[]>(`/api/proyectos/${proyectoId}/bitacora`);
  }

  crear(proyectoId: string, entrada: {
    titulo: string;
    contenido: string;
    tipo: TipoEntradaBitacora;
  }): Observable<BitacoraResponse> {
    return this.http.post<BitacoraResponse>(`/api/proyectos/${proyectoId}/bitacora`, entrada);
  }
}
