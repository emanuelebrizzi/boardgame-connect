import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Boardgame } from '../model/boardgame';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BoardgameService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}`;

  getAllBoardgames(): Observable<Boardgame[]> {
    return this.http.get<Boardgame[]>(`${this.apiUrl}/boardgames`);
  }

  getMyBoardgames(): Observable<Boardgame[]> {
    return this.http.get<Boardgame[]>(`${this.apiUrl}/associations/boardgames`);
  }

  addBoardgames(gameIds: string[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/associations/boardgames`, gameIds);
  }

  removeBoardgames(gameIds: string[]): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/associations/boardgames`, {
      body: gameIds,
    });
  }
}
