import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { GameTable, GameTableRequest } from '../models/game-table';
import { AssociationSummary } from '../models/associaton';
import { Boardgame } from '../models/boardgame';

@Injectable({
  providedIn: 'root',
})
export class AssociationService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/associations`;

  getAllAssociations(): Observable<AssociationSummary[]> {
    return this.http.get<AssociationSummary[]>(`${this.apiUrl}`);
  }

  getBoardgames(associationId: string): Observable<Boardgame[]> {
    return this.http.get<Boardgame[]>(`${this.apiUrl}/${associationId}/boardgames`);
  }

  getTables(): Observable<GameTable[]> {
    return this.http.get<GameTable[]>(`${this.apiUrl}/tables`);
  }

  addTable(request: GameTableRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/tables`, request);
  }

  removeTable(tableId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/tables/${tableId}`);
  }
}
